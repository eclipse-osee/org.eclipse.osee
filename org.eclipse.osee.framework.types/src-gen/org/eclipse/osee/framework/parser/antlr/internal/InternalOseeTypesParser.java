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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_WHOLE_NUM_STR", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'import'", "'.'", "'abstract'", "'artifactType'", "'extends'", "','", "'{'", "'guid'", "'}'", "'attribute'", "'branchGuid'", "'attributeType'", "'overrides'", "'dataProvider'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'MappedAttributeDataProvider'", "'min'", "'max'", "'unlimited'", "'taggerId'", "'DefaultAttributeTaggerProvider'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'WordAttribute'", "'oseeEnumType'", "'entry'", "'overrides enum'", "'inheritAll'", "'add'", "'remove'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'"
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:81:1: ruleOseeTypeModel returns [EObject current=null] : ( (lv_imports_0= ruleImport )* ( (lv_artifactTypes_1= ruleArtifactType ) | (lv_relationTypes_2= ruleRelationType ) | (lv_attributeTypes_3= ruleAttributeType ) | (lv_enumTypes_4= ruleOseeEnumType ) | (lv_enumOverrides_5= ruleOseeEnumOverride ) )* ) ;
    public final EObject ruleOseeTypeModel() throws RecognitionException {
        EObject current = null;

        EObject lv_imports_0 = null;

        EObject lv_artifactTypes_1 = null;

        EObject lv_relationTypes_2 = null;

        EObject lv_attributeTypes_3 = null;

        EObject lv_enumTypes_4 = null;

        EObject lv_enumOverrides_5 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:86:6: ( ( (lv_imports_0= ruleImport )* ( (lv_artifactTypes_1= ruleArtifactType ) | (lv_relationTypes_2= ruleRelationType ) | (lv_attributeTypes_3= ruleAttributeType ) | (lv_enumTypes_4= ruleOseeEnumType ) | (lv_enumOverrides_5= ruleOseeEnumOverride ) )* ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:1: ( (lv_imports_0= ruleImport )* ( (lv_artifactTypes_1= ruleArtifactType ) | (lv_relationTypes_2= ruleRelationType ) | (lv_attributeTypes_3= ruleAttributeType ) | (lv_enumTypes_4= ruleOseeEnumType ) | (lv_enumOverrides_5= ruleOseeEnumOverride ) )* )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:1: ( (lv_imports_0= ruleImport )* ( (lv_artifactTypes_1= ruleArtifactType ) | (lv_relationTypes_2= ruleRelationType ) | (lv_attributeTypes_3= ruleAttributeType ) | (lv_enumTypes_4= ruleOseeEnumType ) | (lv_enumOverrides_5= ruleOseeEnumOverride ) )* )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:2: (lv_imports_0= ruleImport )* ( (lv_artifactTypes_1= ruleArtifactType ) | (lv_relationTypes_2= ruleRelationType ) | (lv_attributeTypes_3= ruleAttributeType ) | (lv_enumTypes_4= ruleOseeEnumType ) | (lv_enumOverrides_5= ruleOseeEnumOverride ) )*
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:2: (lv_imports_0= ruleImport )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==12) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:90:6: lv_imports_0= ruleImport
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleImport_in_ruleOseeTypeModel142);
            	    lv_imports_0=ruleImport();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "imports", lv_imports_0, "Import", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:108:3: ( (lv_artifactTypes_1= ruleArtifactType ) | (lv_relationTypes_2= ruleRelationType ) | (lv_attributeTypes_3= ruleAttributeType ) | (lv_enumTypes_4= ruleOseeEnumType ) | (lv_enumOverrides_5= ruleOseeEnumOverride ) )*
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
                case 47:
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
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:108:4: (lv_artifactTypes_1= ruleArtifactType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:108:4: (lv_artifactTypes_1= ruleArtifactType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:111:6: lv_artifactTypes_1= ruleArtifactType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getArtifactTypesArtifactTypeParserRuleCall_1_0_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleArtifactType_in_ruleOseeTypeModel182);
            	    lv_artifactTypes_1=ruleArtifactType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "artifactTypes", lv_artifactTypes_1, "ArtifactType", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:130:6: (lv_relationTypes_2= ruleRelationType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:130:6: (lv_relationTypes_2= ruleRelationType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:133:6: lv_relationTypes_2= ruleRelationType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getRelationTypesRelationTypeParserRuleCall_1_1_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleRelationType_in_ruleOseeTypeModel226);
            	    lv_relationTypes_2=ruleRelationType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "relationTypes", lv_relationTypes_2, "RelationType", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;
            	case 3 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:152:6: (lv_attributeTypes_3= ruleAttributeType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:152:6: (lv_attributeTypes_3= ruleAttributeType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:155:6: lv_attributeTypes_3= ruleAttributeType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getAttributeTypesAttributeTypeParserRuleCall_1_2_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleAttributeType_in_ruleOseeTypeModel270);
            	    lv_attributeTypes_3=ruleAttributeType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "attributeTypes", lv_attributeTypes_3, "AttributeType", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;
            	case 4 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:174:6: (lv_enumTypes_4= ruleOseeEnumType )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:174:6: (lv_enumTypes_4= ruleOseeEnumType )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:177:6: lv_enumTypes_4= ruleOseeEnumType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getEnumTypesOseeEnumTypeParserRuleCall_1_3_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOseeEnumType_in_ruleOseeTypeModel314);
            	    lv_enumTypes_4=ruleOseeEnumType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "enumTypes", lv_enumTypes_4, "OseeEnumType", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;
            	case 5 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:196:6: (lv_enumOverrides_5= ruleOseeEnumOverride )
            	    {
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:196:6: (lv_enumOverrides_5= ruleOseeEnumOverride )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:199:6: lv_enumOverrides_5= ruleOseeEnumOverride
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getEnumOverridesOseeEnumOverrideParserRuleCall_1_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOseeEnumOverride_in_ruleOseeTypeModel358);
            	    lv_enumOverrides_5=ruleOseeEnumOverride();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "enumOverrides", lv_enumOverrides_5, "OseeEnumOverride", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:224:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:224:48: (iv_ruleImport= ruleImport EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:225:2: iv_ruleImport= ruleImport EOF
            {
             currentNode = createCompositeNode(grammarAccess.getImportRule(), currentNode); 
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport397);
            iv_ruleImport=ruleImport();
            _fsp--;

             current =iv_ruleImport; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport407); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:232:1: ruleImport returns [EObject current=null] : ( 'import' (lv_importURI_1= RULE_STRING ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token lv_importURI_1=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:237:6: ( ( 'import' (lv_importURI_1= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:238:1: ( 'import' (lv_importURI_1= RULE_STRING ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:238:1: ( 'import' (lv_importURI_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:238:2: 'import' (lv_importURI_1= RULE_STRING )
            {
            match(input,12,FOLLOW_12_in_ruleImport441); 

                    createLeafNode(grammarAccess.getImportAccess().getImportKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:242:1: (lv_importURI_1= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:244:6: lv_importURI_1= RULE_STRING
            {
            lv_importURI_1=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleImport463); 

            		createLeafNode(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0(), "importURI"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getImportRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "importURI", lv_importURI_1, "STRING", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:269:1: entryRuleNAME_REFERENCE returns [String current=null] : iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF ;
    public final String entryRuleNAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:269:55: (iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:270:2: iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getNAME_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE505);
            iv_ruleNAME_REFERENCE=ruleNAME_REFERENCE();
            _fsp--;

             current =iv_ruleNAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNAME_REFERENCE516); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:277:1: ruleNAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleNAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:283:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:284:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE555); 

            		current.merge(this_STRING_0);
                
             
                createLeafNode(grammarAccess.getNAME_REFERENCEAccess().getSTRINGTerminalRuleCall(), null); 
                

            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
            	    lastConsumedDatatypeToken = current;
                
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:299:1: entryRuleQUALIFIED_NAME returns [String current=null] : iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF ;
    public final String entryRuleQUALIFIED_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQUALIFIED_NAME = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:299:55: (iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:300:2: iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF
            {
             currentNode = createCompositeNode(grammarAccess.getQUALIFIED_NAMERule(), currentNode); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME598);
            iv_ruleQUALIFIED_NAME=ruleQUALIFIED_NAME();
            _fsp--;

             current =iv_ruleQUALIFIED_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME609); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:307:1: ruleQUALIFIED_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQUALIFIED_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:313:6: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:314:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:314:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:314:6: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME649); 

            		current.merge(this_ID_0);
                
             
                createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:321:1: (kw= '.' this_ID_2= RULE_ID )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==13) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:322:2: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,13,FOLLOW_13_in_ruleQUALIFIED_NAME668); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0(), null); 
            	        
            	    this_ID_2=(Token)input.LT(1);
            	    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME683); 

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
            	    lastConsumedDatatypeToken = current;
                
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:344:1: entryRuleOseeType returns [EObject current=null] : iv_ruleOseeType= ruleOseeType EOF ;
    public final EObject entryRuleOseeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:344:50: (iv_ruleOseeType= ruleOseeType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:345:2: iv_ruleOseeType= ruleOseeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType730);
            iv_ruleOseeType=ruleOseeType();
            _fsp--;

             current =iv_ruleOseeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType740); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:352:1: ruleOseeType returns [EObject current=null] : (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType ) ;
    public final EObject ruleOseeType() throws RecognitionException {
        EObject current = null;

        EObject this_ArtifactType_0 = null;

        EObject this_RelationType_1 = null;

        EObject this_AttributeType_2 = null;

        EObject this_OseeEnumType_3 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:357:6: ( (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:358:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:358:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )
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
            case 47:
                {
                alt4=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("358:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:359:5: this_ArtifactType_0= ruleArtifactType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleArtifactType_in_ruleOseeType787);
                    this_ArtifactType_0=ruleArtifactType();
                    _fsp--;

                     
                            current = this_ArtifactType_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:369:5: this_RelationType_1= ruleRelationType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleRelationType_in_ruleOseeType814);
                    this_RelationType_1=ruleRelationType();
                    _fsp--;

                     
                            current = this_RelationType_1; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:379:5: this_AttributeType_2= ruleAttributeType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleAttributeType_in_ruleOseeType841);
                    this_AttributeType_2=ruleAttributeType();
                    _fsp--;

                     
                            current = this_AttributeType_2; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:389:5: this_OseeEnumType_3= ruleOseeEnumType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getOseeEnumTypeParserRuleCall_3(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleOseeEnumType_in_ruleOseeType868);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:404:1: entryRuleArtifactType returns [EObject current=null] : iv_ruleArtifactType= ruleArtifactType EOF ;
    public final EObject entryRuleArtifactType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:404:54: (iv_ruleArtifactType= ruleArtifactType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:405:2: iv_ruleArtifactType= ruleArtifactType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getArtifactTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleArtifactType_in_entryRuleArtifactType900);
            iv_ruleArtifactType=ruleArtifactType();
            _fsp--;

             current =iv_ruleArtifactType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactType910); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:412:1: ruleArtifactType returns [EObject current=null] : ( (lv_abstract_0= 'abstract' )? 'artifactType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )* )? '{' ( 'guid' (lv_typeGuid_9= RULE_STRING ) )? (lv_validAttributeTypes_10= ruleAttributeTypeRef )* '}' ) ;
    public final EObject ruleArtifactType() throws RecognitionException {
        EObject current = null;

        Token lv_abstract_0=null;
        Token lv_typeGuid_9=null;
        AntlrDatatypeRuleToken lv_name_2 = null;

        EObject lv_validAttributeTypes_10 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:417:6: ( ( (lv_abstract_0= 'abstract' )? 'artifactType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )* )? '{' ( 'guid' (lv_typeGuid_9= RULE_STRING ) )? (lv_validAttributeTypes_10= ruleAttributeTypeRef )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:418:1: ( (lv_abstract_0= 'abstract' )? 'artifactType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )* )? '{' ( 'guid' (lv_typeGuid_9= RULE_STRING ) )? (lv_validAttributeTypes_10= ruleAttributeTypeRef )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:418:1: ( (lv_abstract_0= 'abstract' )? 'artifactType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )* )? '{' ( 'guid' (lv_typeGuid_9= RULE_STRING ) )? (lv_validAttributeTypes_10= ruleAttributeTypeRef )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:418:2: (lv_abstract_0= 'abstract' )? 'artifactType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )* )? '{' ( 'guid' (lv_typeGuid_9= RULE_STRING ) )? (lv_validAttributeTypes_10= ruleAttributeTypeRef )* '}'
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:418:2: (lv_abstract_0= 'abstract' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==14) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:420:6: lv_abstract_0= 'abstract'
                    {
                    lv_abstract_0=(Token)input.LT(1);
                    match(input,14,FOLLOW_14_in_ruleArtifactType956); 

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
                    break;

            }

            match(input,15,FOLLOW_15_in_ruleArtifactType979); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:443:1: (lv_name_2= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:446:6: lv_name_2= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType1013);
            lv_name_2=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_2, "NAME_REFERENCE", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:464:2: ( 'extends' ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )* )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==16) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:464:3: 'extends' ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )*
                    {
                    match(input,16,FOLLOW_16_in_ruleArtifactType1027); 

                            createLeafNode(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:468:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:471:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType1054);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }

                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:486:2: ( ',' ( ruleNAME_REFERENCE ) )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==17) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:486:3: ',' ( ruleNAME_REFERENCE )
                    	    {
                    	    match(input,17,FOLLOW_17_in_ruleArtifactType1067); 

                    	            createLeafNode(grammarAccess.getArtifactTypeAccess().getCommaKeyword_3_2_0(), null); 
                    	        
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:490:1: ( ruleNAME_REFERENCE )
                    	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:493:3: ruleNAME_REFERENCE
                    	    {

                    	    			if (current==null) {
                    	    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
                    	    	            associateNodeWithAstElement(currentNode, current);
                    	    	        }
                    	            
                    	     
                    	    	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_2_1_0(), currentNode); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType1094);
                    	    ruleNAME_REFERENCE();
                    	    _fsp--;

                    	     
                    	    	        currentNode = currentNode.getParent();
                    	    	    

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

            match(input,18,FOLLOW_18_in_ruleArtifactType1110); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_4(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:512:1: ( 'guid' (lv_typeGuid_9= RULE_STRING ) )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==19) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:512:2: 'guid' (lv_typeGuid_9= RULE_STRING )
                    {
                    match(input,19,FOLLOW_19_in_ruleArtifactType1120); 

                            createLeafNode(grammarAccess.getArtifactTypeAccess().getGuidKeyword_5_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:516:1: (lv_typeGuid_9= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:518:6: lv_typeGuid_9= RULE_STRING
                    {
                    lv_typeGuid_9=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleArtifactType1142); 

                    		createLeafNode(grammarAccess.getArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0(), "typeGuid"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "typeGuid", lv_typeGuid_9, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:536:4: (lv_validAttributeTypes_10= ruleAttributeTypeRef )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==21) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:539:6: lv_validAttributeTypes_10= ruleAttributeTypeRef
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAttributeTypeRefParserRuleCall_6_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleAttributeTypeRef_in_ruleArtifactType1186);
            	    lv_validAttributeTypes_10=ruleAttributeTypeRef();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "validAttributeTypes", lv_validAttributeTypes_10, "AttributeTypeRef", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match(input,20,FOLLOW_20_in_ruleArtifactType1200); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_7(), null); 
                

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:568:1: entryRuleAttributeTypeRef returns [EObject current=null] : iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF ;
    public final EObject entryRuleAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeTypeRef = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:568:58: (iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:569:2: iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeTypeRefRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef1233);
            iv_ruleAttributeTypeRef=ruleAttributeTypeRef();
            _fsp--;

             current =iv_ruleAttributeTypeRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRef1243); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:576:1: ruleAttributeTypeRef returns [EObject current=null] : ( 'attribute' ( ruleNAME_REFERENCE ) ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )? ) ;
    public final EObject ruleAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        Token lv_branchGuid_3=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:581:6: ( ( 'attribute' ( ruleNAME_REFERENCE ) ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:582:1: ( 'attribute' ( ruleNAME_REFERENCE ) ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:582:1: ( 'attribute' ( ruleNAME_REFERENCE ) ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:582:2: 'attribute' ( ruleNAME_REFERENCE ) ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )?
            {
            match(input,21,FOLLOW_21_in_ruleAttributeTypeRef1277); 

                    createLeafNode(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:586:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:589:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeTypeRef1304);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:604:2: ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==22) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:604:3: 'branchGuid' (lv_branchGuid_3= RULE_STRING )
                    {
                    match(input,22,FOLLOW_22_in_ruleAttributeTypeRef1317); 

                            createLeafNode(grammarAccess.getAttributeTypeRefAccess().getBranchGuidKeyword_2_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:608:1: (lv_branchGuid_3= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:610:6: lv_branchGuid_3= RULE_STRING
                    {
                    lv_branchGuid_3=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeTypeRef1339); 

                    		createLeafNode(grammarAccess.getAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0(), "branchGuid"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRefRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "branchGuid", lv_branchGuid_3, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:635:1: entryRuleAttributeType returns [EObject current=null] : iv_ruleAttributeType= ruleAttributeType EOF ;
    public final EObject entryRuleAttributeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:635:55: (iv_ruleAttributeType= ruleAttributeType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:636:2: iv_ruleAttributeType= ruleAttributeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeType_in_entryRuleAttributeType1382);
            iv_ruleAttributeType=ruleAttributeType();
            _fsp--;

             current =iv_ruleAttributeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeType1392); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:643:1: ruleAttributeType returns [EObject current=null] : ( 'attributeType' (lv_name_1= ruleNAME_REFERENCE ) ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' ( 'guid' (lv_typeGuid_8= RULE_STRING ) )? 'dataProvider' (lv_dataProvider_10= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_12= RULE_WHOLE_NUM_STR ) 'max' (lv_max_14= ( RULE_WHOLE_NUM_STR | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_16= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( ruleNAME_REFERENCE ) )? ( 'description' (lv_description_20= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_22= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_24= RULE_STRING ) )? '}' ) ;
    public final EObject ruleAttributeType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_8=null;
        Token lv_dataProvider_10=null;
        Token lv_min_12=null;
        Token lv_max_14=null;
        Token lv_taggerId_16=null;
        Token lv_description_20=null;
        Token lv_defaultValue_22=null;
        Token lv_fileExtension_24=null;
        AntlrDatatypeRuleToken lv_name_1 = null;

        AntlrDatatypeRuleToken lv_baseAttributeType_3 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:648:6: ( ( 'attributeType' (lv_name_1= ruleNAME_REFERENCE ) ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' ( 'guid' (lv_typeGuid_8= RULE_STRING ) )? 'dataProvider' (lv_dataProvider_10= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_12= RULE_WHOLE_NUM_STR ) 'max' (lv_max_14= ( RULE_WHOLE_NUM_STR | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_16= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( ruleNAME_REFERENCE ) )? ( 'description' (lv_description_20= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_22= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_24= RULE_STRING ) )? '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:649:1: ( 'attributeType' (lv_name_1= ruleNAME_REFERENCE ) ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' ( 'guid' (lv_typeGuid_8= RULE_STRING ) )? 'dataProvider' (lv_dataProvider_10= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_12= RULE_WHOLE_NUM_STR ) 'max' (lv_max_14= ( RULE_WHOLE_NUM_STR | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_16= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( ruleNAME_REFERENCE ) )? ( 'description' (lv_description_20= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_22= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_24= RULE_STRING ) )? '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:649:1: ( 'attributeType' (lv_name_1= ruleNAME_REFERENCE ) ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' ( 'guid' (lv_typeGuid_8= RULE_STRING ) )? 'dataProvider' (lv_dataProvider_10= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_12= RULE_WHOLE_NUM_STR ) 'max' (lv_max_14= ( RULE_WHOLE_NUM_STR | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_16= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( ruleNAME_REFERENCE ) )? ( 'description' (lv_description_20= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_22= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_24= RULE_STRING ) )? '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:649:2: 'attributeType' (lv_name_1= ruleNAME_REFERENCE ) ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' ( 'guid' (lv_typeGuid_8= RULE_STRING ) )? 'dataProvider' (lv_dataProvider_10= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_12= RULE_WHOLE_NUM_STR ) 'max' (lv_max_14= ( RULE_WHOLE_NUM_STR | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_16= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( ruleNAME_REFERENCE ) )? ( 'description' (lv_description_20= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_22= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_24= RULE_STRING ) )? '}'
            {
            match(input,23,FOLLOW_23_in_ruleAttributeType1426); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:653:1: (lv_name_1= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:656:6: lv_name_1= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1460);
            lv_name_1=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_1, "NAME_REFERENCE", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:674:2: ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:674:3: 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType )
            {
            match(input,16,FOLLOW_16_in_ruleAttributeType1474); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_2_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:678:1: (lv_baseAttributeType_3= ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:681:6: lv_baseAttributeType_3= ruleAttributeBaseType
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleAttributeBaseType_in_ruleAttributeType1508);
            lv_baseAttributeType_3=ruleAttributeBaseType();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "baseAttributeType", lv_baseAttributeType_3, "AttributeBaseType", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:699:3: ( 'overrides' ( ruleNAME_REFERENCE ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==24) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:699:4: 'overrides' ( ruleNAME_REFERENCE )
                    {
                    match(input,24,FOLLOW_24_in_ruleAttributeType1523); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getOverridesKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:703:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:706:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeCrossReference_3_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1550);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }
                    break;

            }

            match(input,18,FOLLOW_18_in_ruleAttributeType1564); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_4(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:725:1: ( 'guid' (lv_typeGuid_8= RULE_STRING ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==19) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:725:2: 'guid' (lv_typeGuid_8= RULE_STRING )
                    {
                    match(input,19,FOLLOW_19_in_ruleAttributeType1574); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getGuidKeyword_5_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:729:1: (lv_typeGuid_8= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:731:6: lv_typeGuid_8= RULE_STRING
                    {
                    lv_typeGuid_8=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1596); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0(), "typeGuid"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "typeGuid", lv_typeGuid_8, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            match(input,25,FOLLOW_25_in_ruleAttributeType1615); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_6(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:753:1: (lv_dataProvider_10= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:755:6: lv_dataProvider_10= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:755:25: ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME )
            int alt13=4;
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
            case 28:
                {
                alt13=3;
                }
                break;
            case RULE_ID:
                {
                alt13=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("755:25: ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME )", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:755:26: 'DefaultAttributeDataProvider'
                    {
                    match(input,26,FOLLOW_26_in_ruleAttributeType1637); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_7_0_0(), "dataProvider"); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:761:6: 'UriAttributeDataProvider'
                    {
                    match(input,27,FOLLOW_27_in_ruleAttributeType1653); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_7_0_1(), "dataProvider"); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:767:6: 'MappedAttributeDataProvider'
                    {
                    match(input,28,FOLLOW_28_in_ruleAttributeType1669); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderMappedAttributeDataProviderKeyword_7_0_2(), "dataProvider"); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:773:7: ruleQUALIFIED_NAME
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_7_0_3(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1689);
                    ruleQUALIFIED_NAME();
                    _fsp--;

                     
                            currentNode = currentNode.getParent();
                        

                    }
                    break;

            }


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "dataProvider", /* lv_dataProvider_10 */ input.LT(-1), null, lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,29,FOLLOW_29_in_ruleAttributeType1707); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getMinKeyword_8(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:798:1: (lv_min_12= RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:800:6: lv_min_12= RULE_WHOLE_NUM_STR
            {
            lv_min_12=(Token)input.LT(1);
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1729); 

            		createLeafNode(grammarAccess.getAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_9_0(), "min"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "min", lv_min_12, "WHOLE_NUM_STR", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,30,FOLLOW_30_in_ruleAttributeType1746); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxKeyword_10(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:822:1: (lv_max_14= ( RULE_WHOLE_NUM_STR | 'unlimited' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:824:6: lv_max_14= ( RULE_WHOLE_NUM_STR | 'unlimited' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:824:16: ( RULE_WHOLE_NUM_STR | 'unlimited' )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_WHOLE_NUM_STR) ) {
                alt14=1;
            }
            else if ( (LA14_0==31) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("824:16: ( RULE_WHOLE_NUM_STR | 'unlimited' )", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:824:18: RULE_WHOLE_NUM_STR
                    {
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1769); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_11_0_0(), "max"); 
                    	

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:829:6: 'unlimited'
                    {
                    match(input,31,FOLLOW_31_in_ruleAttributeType1780); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_11_0_1(), "max"); 
                        

                    }
                    break;

            }


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "max", /* lv_max_14 */ input.LT(-1), null, lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:849:2: ( 'taggerId' (lv_taggerId_16= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==32) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:849:3: 'taggerId' (lv_taggerId_16= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) )
                    {
                    match(input,32,FOLLOW_32_in_ruleAttributeType1805); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_12_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:853:1: (lv_taggerId_16= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:855:6: lv_taggerId_16= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:855:21: ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME )
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==33) ) {
                        alt15=1;
                    }
                    else if ( (LA15_0==RULE_ID) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("855:21: ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME )", 15, 0, input);

                        throw nvae;
                    }
                    switch (alt15) {
                        case 1 :
                            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:855:22: 'DefaultAttributeTaggerProvider'
                            {
                            match(input,33,FOLLOW_33_in_ruleAttributeType1827); 

                                    createLeafNode(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_12_1_0_0(), "taggerId"); 
                                

                            }
                            break;
                        case 2 :
                            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:861:7: ruleQUALIFIED_NAME
                            {
                             
                                    currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_12_1_0_1(), currentNode); 
                                
                            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1847);
                            ruleQUALIFIED_NAME();
                            _fsp--;

                             
                                    currentNode = currentNode.getParent();
                                

                            }
                            break;

                    }


                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "taggerId", /* lv_taggerId_16 */ input.LT(-1), null, lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:882:4: ( 'enumType' ( ruleNAME_REFERENCE ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==34) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:882:5: 'enumType' ( ruleNAME_REFERENCE )
                    {
                    match(input,34,FOLLOW_34_in_ruleAttributeType1868); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_13_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:886:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:889:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_13_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1895);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:904:4: ( 'description' (lv_description_20= RULE_STRING ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==35) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:904:5: 'description' (lv_description_20= RULE_STRING )
                    {
                    match(input,35,FOLLOW_35_in_ruleAttributeType1910); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_14_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:908:1: (lv_description_20= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:910:6: lv_description_20= RULE_STRING
                    {
                    lv_description_20=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1932); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_14_1_0(), "description"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "description", lv_description_20, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:928:4: ( 'defaultValue' (lv_defaultValue_22= RULE_STRING ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==36) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:928:5: 'defaultValue' (lv_defaultValue_22= RULE_STRING )
                    {
                    match(input,36,FOLLOW_36_in_ruleAttributeType1952); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_15_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:932:1: (lv_defaultValue_22= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:934:6: lv_defaultValue_22= RULE_STRING
                    {
                    lv_defaultValue_22=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1974); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_15_1_0(), "defaultValue"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "defaultValue", lv_defaultValue_22, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:952:4: ( 'fileExtension' (lv_fileExtension_24= RULE_STRING ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==37) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:952:5: 'fileExtension' (lv_fileExtension_24= RULE_STRING )
                    {
                    match(input,37,FOLLOW_37_in_ruleAttributeType1994); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_16_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:956:1: (lv_fileExtension_24= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:958:6: lv_fileExtension_24= RULE_STRING
                    {
                    lv_fileExtension_24=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType2016); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_16_1_0(), "fileExtension"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "fileExtension", lv_fileExtension_24, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            match(input,20,FOLLOW_20_in_ruleAttributeType2035); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_17(), null); 
                

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:987:1: entryRuleAttributeBaseType returns [String current=null] : iv_ruleAttributeBaseType= ruleAttributeBaseType EOF ;
    public final String entryRuleAttributeBaseType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAttributeBaseType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:987:58: (iv_ruleAttributeBaseType= ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:988:2: iv_ruleAttributeBaseType= ruleAttributeBaseType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeBaseTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType2069);
            iv_ruleAttributeBaseType=ruleAttributeBaseType();
            _fsp--;

             current =iv_ruleAttributeBaseType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType2080); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:995:1: ruleAttributeBaseType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) ;
    public final AntlrDatatypeRuleToken ruleAttributeBaseType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QUALIFIED_NAME_9 = null;


         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1001:6: ( (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1002:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1002:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            int alt21=10;
            switch ( input.LA(1) ) {
            case 38:
                {
                alt21=1;
                }
                break;
            case 39:
                {
                alt21=2;
                }
                break;
            case 40:
                {
                alt21=3;
                }
                break;
            case 41:
                {
                alt21=4;
                }
                break;
            case 42:
                {
                alt21=5;
                }
                break;
            case 43:
                {
                alt21=6;
                }
                break;
            case 44:
                {
                alt21=7;
                }
                break;
            case 45:
                {
                alt21=8;
                }
                break;
            case 46:
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
                    new NoViableAltException("1002:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1003:2: kw= 'BooleanAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_ruleAttributeBaseType2118); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0(), null); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1010:2: kw= 'CompressedContentAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,39,FOLLOW_39_in_ruleAttributeBaseType2137); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1(), null); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1017:2: kw= 'DateAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,40,FOLLOW_40_in_ruleAttributeBaseType2156); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2(), null); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1024:2: kw= 'EnumeratedAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,41,FOLLOW_41_in_ruleAttributeBaseType2175); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3(), null); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1031:2: kw= 'FloatingPointAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,42,FOLLOW_42_in_ruleAttributeBaseType2194); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4(), null); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1038:2: kw= 'IntegerAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_ruleAttributeBaseType2213); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5(), null); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1045:2: kw= 'JavaObjectAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_ruleAttributeBaseType2232); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6(), null); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1052:2: kw= 'StringAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,45,FOLLOW_45_in_ruleAttributeBaseType2251); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7(), null); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1059:2: kw= 'WordAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,46,FOLLOW_46_in_ruleAttributeBaseType2270); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8(), null); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1066:5: this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2298);
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
            	    lastConsumedDatatypeToken = current;
                
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1084:1: entryRuleOseeEnumType returns [EObject current=null] : iv_ruleOseeEnumType= ruleOseeEnumType EOF ;
    public final EObject entryRuleOseeEnumType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeEnumType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1084:54: (iv_ruleOseeEnumType= ruleOseeEnumType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1085:2: iv_ruleOseeEnumType= ruleOseeEnumType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeEnumTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType2341);
            iv_ruleOseeEnumType=ruleOseeEnumType();
            _fsp--;

             current =iv_ruleOseeEnumType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumType2351); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1092:1: ruleOseeEnumType returns [EObject current=null] : ( 'oseeEnumType' (lv_name_1= ruleNAME_REFERENCE ) '{' ( 'guid' (lv_typeGuid_4= RULE_STRING ) )? (lv_enumEntries_5= ruleOseeEnumEntry )* '}' ) ;
    public final EObject ruleOseeEnumType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_4=null;
        AntlrDatatypeRuleToken lv_name_1 = null;

        EObject lv_enumEntries_5 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1097:6: ( ( 'oseeEnumType' (lv_name_1= ruleNAME_REFERENCE ) '{' ( 'guid' (lv_typeGuid_4= RULE_STRING ) )? (lv_enumEntries_5= ruleOseeEnumEntry )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1098:1: ( 'oseeEnumType' (lv_name_1= ruleNAME_REFERENCE ) '{' ( 'guid' (lv_typeGuid_4= RULE_STRING ) )? (lv_enumEntries_5= ruleOseeEnumEntry )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1098:1: ( 'oseeEnumType' (lv_name_1= ruleNAME_REFERENCE ) '{' ( 'guid' (lv_typeGuid_4= RULE_STRING ) )? (lv_enumEntries_5= ruleOseeEnumEntry )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1098:2: 'oseeEnumType' (lv_name_1= ruleNAME_REFERENCE ) '{' ( 'guid' (lv_typeGuid_4= RULE_STRING ) )? (lv_enumEntries_5= ruleOseeEnumEntry )* '}'
            {
            match(input,47,FOLLOW_47_in_ruleOseeEnumType2385); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1102:1: (lv_name_1= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1105:6: lv_name_1= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumType2419);
            lv_name_1=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_1, "NAME_REFERENCE", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,18,FOLLOW_18_in_ruleOseeEnumType2432); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1127:1: ( 'guid' (lv_typeGuid_4= RULE_STRING ) )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==19) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1127:2: 'guid' (lv_typeGuid_4= RULE_STRING )
                    {
                    match(input,19,FOLLOW_19_in_ruleOseeEnumType2442); 

                            createLeafNode(grammarAccess.getOseeEnumTypeAccess().getGuidKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1131:1: (lv_typeGuid_4= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1133:6: lv_typeGuid_4= RULE_STRING
                    {
                    lv_typeGuid_4=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleOseeEnumType2464); 

                    		createLeafNode(grammarAccess.getOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0(), "typeGuid"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "typeGuid", lv_typeGuid_4, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1151:4: (lv_enumEntries_5= ruleOseeEnumEntry )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==48) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1154:6: lv_enumEntries_5= ruleOseeEnumEntry
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesOseeEnumEntryParserRuleCall_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOseeEnumEntry_in_ruleOseeEnumType2508);
            	    lv_enumEntries_5=ruleOseeEnumEntry();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "enumEntries", lv_enumEntries_5, "OseeEnumEntry", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

            match(input,20,FOLLOW_20_in_ruleOseeEnumType2522); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getRightCurlyBracketKeyword_5(), null); 
                

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1183:1: entryRuleOseeEnumEntry returns [EObject current=null] : iv_ruleOseeEnumEntry= ruleOseeEnumEntry EOF ;
    public final EObject entryRuleOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeEnumEntry = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1183:55: (iv_ruleOseeEnumEntry= ruleOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1184:2: iv_ruleOseeEnumEntry= ruleOseeEnumEntry EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeEnumEntryRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry2555);
            iv_ruleOseeEnumEntry=ruleOseeEnumEntry();
            _fsp--;

             current =iv_ruleOseeEnumEntry; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumEntry2565); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1191:1: ruleOseeEnumEntry returns [EObject current=null] : ( 'entry' (lv_name_1= ruleNAME_REFERENCE ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? ) ;
    public final EObject ruleOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        Token lv_ordinal_2=null;
        AntlrDatatypeRuleToken lv_name_1 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1196:6: ( ( 'entry' (lv_name_1= ruleNAME_REFERENCE ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1197:1: ( 'entry' (lv_name_1= ruleNAME_REFERENCE ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1197:1: ( 'entry' (lv_name_1= ruleNAME_REFERENCE ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1197:2: 'entry' (lv_name_1= ruleNAME_REFERENCE ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )?
            {
            match(input,48,FOLLOW_48_in_ruleOseeEnumEntry2599); 

                    createLeafNode(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1201:1: (lv_name_1= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1204:6: lv_name_1= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getOseeEnumEntryAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumEntry2633);
            lv_name_1=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getOseeEnumEntryRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_1, "NAME_REFERENCE", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1222:2: (lv_ordinal_2= RULE_WHOLE_NUM_STR )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==RULE_WHOLE_NUM_STR) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1224:6: lv_ordinal_2= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleOseeEnumEntry2659); 

                    		createLeafNode(grammarAccess.getOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0(), "ordinal"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getOseeEnumEntryRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "ordinal", lv_ordinal_2, "WHOLE_NUM_STR", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1249:1: entryRuleOseeEnumOverride returns [EObject current=null] : iv_ruleOseeEnumOverride= ruleOseeEnumOverride EOF ;
    public final EObject entryRuleOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeEnumOverride = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1249:58: (iv_ruleOseeEnumOverride= ruleOseeEnumOverride EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1250:2: iv_ruleOseeEnumOverride= ruleOseeEnumOverride EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeEnumOverrideRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeEnumOverride_in_entryRuleOseeEnumOverride2701);
            iv_ruleOseeEnumOverride=ruleOseeEnumOverride();
            _fsp--;

             current =iv_ruleOseeEnumOverride; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumOverride2711); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1257:1: ruleOseeEnumOverride returns [EObject current=null] : ( 'overrides enum' ( ruleNAME_REFERENCE ) '{' (lv_inheritAll_3= 'inheritAll' )? (lv_overrideOptions_4= ruleOverrideOption )* '}' ) ;
    public final EObject ruleOseeEnumOverride() throws RecognitionException {
        EObject current = null;

        Token lv_inheritAll_3=null;
        EObject lv_overrideOptions_4 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1262:6: ( ( 'overrides enum' ( ruleNAME_REFERENCE ) '{' (lv_inheritAll_3= 'inheritAll' )? (lv_overrideOptions_4= ruleOverrideOption )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1263:1: ( 'overrides enum' ( ruleNAME_REFERENCE ) '{' (lv_inheritAll_3= 'inheritAll' )? (lv_overrideOptions_4= ruleOverrideOption )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1263:1: ( 'overrides enum' ( ruleNAME_REFERENCE ) '{' (lv_inheritAll_3= 'inheritAll' )? (lv_overrideOptions_4= ruleOverrideOption )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1263:2: 'overrides enum' ( ruleNAME_REFERENCE ) '{' (lv_inheritAll_3= 'inheritAll' )? (lv_overrideOptions_4= ruleOverrideOption )* '}'
            {
            match(input,49,FOLLOW_49_in_ruleOseeEnumOverride2745); 

                    createLeafNode(grammarAccess.getOseeEnumOverrideAccess().getOverridesEnumKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1267:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1270:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getOseeEnumOverrideRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getOseeEnumOverrideAccess().getOverridenEnumTypeOseeEnumTypeCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumOverride2772);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,18,FOLLOW_18_in_ruleOseeEnumOverride2784); 

                    createLeafNode(grammarAccess.getOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1289:1: (lv_inheritAll_3= 'inheritAll' )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==50) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1291:6: lv_inheritAll_3= 'inheritAll'
                    {
                    lv_inheritAll_3=(Token)input.LT(1);
                    match(input,50,FOLLOW_50_in_ruleOseeEnumOverride2805); 

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
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1310:3: (lv_overrideOptions_4= ruleOverrideOption )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( ((LA26_0>=51 && LA26_0<=52)) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1313:6: lv_overrideOptions_4= ruleOverrideOption
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOverrideOption_in_ruleOseeEnumOverride2853);
            	    lv_overrideOptions_4=ruleOverrideOption();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeEnumOverrideRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "overrideOptions", lv_overrideOptions_4, "OverrideOption", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);

            match(input,20,FOLLOW_20_in_ruleOseeEnumOverride2867); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1342:1: entryRuleOverrideOption returns [EObject current=null] : iv_ruleOverrideOption= ruleOverrideOption EOF ;
    public final EObject entryRuleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOverrideOption = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1342:56: (iv_ruleOverrideOption= ruleOverrideOption EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1343:2: iv_ruleOverrideOption= ruleOverrideOption EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOverrideOptionRule(), currentNode); 
            pushFollow(FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2900);
            iv_ruleOverrideOption=ruleOverrideOption();
            _fsp--;

             current =iv_ruleOverrideOption; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOverrideOption2910); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1350:1: ruleOverrideOption returns [EObject current=null] : (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) ;
    public final EObject ruleOverrideOption() throws RecognitionException {
        EObject current = null;

        EObject this_AddEnum_0 = null;

        EObject this_RemoveEnum_1 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1355:6: ( (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1356:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1356:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )
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
                    new NoViableAltException("1356:1: (this_AddEnum_0= ruleAddEnum | this_RemoveEnum_1= ruleRemoveEnum )", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1357:5: this_AddEnum_0= ruleAddEnum
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleAddEnum_in_ruleOverrideOption2957);
                    this_AddEnum_0=ruleAddEnum();
                    _fsp--;

                     
                            current = this_AddEnum_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1367:5: this_RemoveEnum_1= ruleRemoveEnum
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2984);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1382:1: entryRuleAddEnum returns [EObject current=null] : iv_ruleAddEnum= ruleAddEnum EOF ;
    public final EObject entryRuleAddEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddEnum = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1382:49: (iv_ruleAddEnum= ruleAddEnum EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1383:2: iv_ruleAddEnum= ruleAddEnum EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAddEnumRule(), currentNode); 
            pushFollow(FOLLOW_ruleAddEnum_in_entryRuleAddEnum3016);
            iv_ruleAddEnum=ruleAddEnum();
            _fsp--;

             current =iv_ruleAddEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddEnum3026); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1390:1: ruleAddEnum returns [EObject current=null] : ( 'add' (lv_enumEntry_1= ruleNAME_REFERENCE ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? ) ;
    public final EObject ruleAddEnum() throws RecognitionException {
        EObject current = null;

        Token lv_ordinal_2=null;
        AntlrDatatypeRuleToken lv_enumEntry_1 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1395:6: ( ( 'add' (lv_enumEntry_1= ruleNAME_REFERENCE ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1396:1: ( 'add' (lv_enumEntry_1= ruleNAME_REFERENCE ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1396:1: ( 'add' (lv_enumEntry_1= ruleNAME_REFERENCE ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1396:2: 'add' (lv_enumEntry_1= ruleNAME_REFERENCE ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )?
            {
            match(input,51,FOLLOW_51_in_ruleAddEnum3060); 

                    createLeafNode(grammarAccess.getAddEnumAccess().getAddKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1400:1: (lv_enumEntry_1= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1403:6: lv_enumEntry_1= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAddEnumAccess().getEnumEntryNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAddEnum3094);
            lv_enumEntry_1=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAddEnumRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "enumEntry", lv_enumEntry_1, "NAME_REFERENCE", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1421:2: (lv_ordinal_2= RULE_WHOLE_NUM_STR )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==RULE_WHOLE_NUM_STR) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1423:6: lv_ordinal_2= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum3120); 

                    		createLeafNode(grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0(), "ordinal"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAddEnumRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "ordinal", lv_ordinal_2, "WHOLE_NUM_STR", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1448:1: entryRuleRemoveEnum returns [EObject current=null] : iv_ruleRemoveEnum= ruleRemoveEnum EOF ;
    public final EObject entryRuleRemoveEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRemoveEnum = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1448:52: (iv_ruleRemoveEnum= ruleRemoveEnum EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1449:2: iv_ruleRemoveEnum= ruleRemoveEnum EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRemoveEnumRule(), currentNode); 
            pushFollow(FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum3162);
            iv_ruleRemoveEnum=ruleRemoveEnum();
            _fsp--;

             current =iv_ruleRemoveEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRemoveEnum3172); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1456:1: ruleRemoveEnum returns [EObject current=null] : ( 'remove' ( ruleNAME_REFERENCE ) ) ;
    public final EObject ruleRemoveEnum() throws RecognitionException {
        EObject current = null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1461:6: ( ( 'remove' ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1462:1: ( 'remove' ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1462:1: ( 'remove' ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1462:2: 'remove' ( ruleNAME_REFERENCE )
            {
            match(input,52,FOLLOW_52_in_ruleRemoveEnum3206); 

                    createLeafNode(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1466:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1469:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRemoveEnumRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRemoveEnumAccess().getEnumEntryOseeEnumEntryCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRemoveEnum3233);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1491:1: entryRuleRelationType returns [EObject current=null] : iv_ruleRelationType= ruleRelationType EOF ;
    public final EObject entryRuleRelationType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1491:54: (iv_ruleRelationType= ruleRelationType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1492:2: iv_ruleRelationType= ruleRelationType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRelationTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleRelationType_in_entryRuleRelationType3269);
            iv_ruleRelationType=ruleRelationType();
            _fsp--;

             current =iv_ruleRelationType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationType3279); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1499:1: ruleRelationType returns [EObject current=null] : ( 'relationType' (lv_name_1= ruleNAME_REFERENCE ) '{' ( 'guid' (lv_typeGuid_4= RULE_STRING ) )? 'sideAName' (lv_sideAName_6= RULE_STRING ) 'sideAArtifactType' ( ruleNAME_REFERENCE ) 'sideBName' (lv_sideBName_10= RULE_STRING ) 'sideBArtifactType' ( ruleNAME_REFERENCE ) 'defaultOrderType' (lv_defaultOrderType_14= ruleRelationOrderType ) 'multiplicity' (lv_multiplicity_16= ruleRelationMultiplicityEnum ) '}' ) ;
    public final EObject ruleRelationType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_4=null;
        Token lv_sideAName_6=null;
        Token lv_sideBName_10=null;
        AntlrDatatypeRuleToken lv_name_1 = null;

        AntlrDatatypeRuleToken lv_defaultOrderType_14 = null;

        Enumerator lv_multiplicity_16 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1504:6: ( ( 'relationType' (lv_name_1= ruleNAME_REFERENCE ) '{' ( 'guid' (lv_typeGuid_4= RULE_STRING ) )? 'sideAName' (lv_sideAName_6= RULE_STRING ) 'sideAArtifactType' ( ruleNAME_REFERENCE ) 'sideBName' (lv_sideBName_10= RULE_STRING ) 'sideBArtifactType' ( ruleNAME_REFERENCE ) 'defaultOrderType' (lv_defaultOrderType_14= ruleRelationOrderType ) 'multiplicity' (lv_multiplicity_16= ruleRelationMultiplicityEnum ) '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1505:1: ( 'relationType' (lv_name_1= ruleNAME_REFERENCE ) '{' ( 'guid' (lv_typeGuid_4= RULE_STRING ) )? 'sideAName' (lv_sideAName_6= RULE_STRING ) 'sideAArtifactType' ( ruleNAME_REFERENCE ) 'sideBName' (lv_sideBName_10= RULE_STRING ) 'sideBArtifactType' ( ruleNAME_REFERENCE ) 'defaultOrderType' (lv_defaultOrderType_14= ruleRelationOrderType ) 'multiplicity' (lv_multiplicity_16= ruleRelationMultiplicityEnum ) '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1505:1: ( 'relationType' (lv_name_1= ruleNAME_REFERENCE ) '{' ( 'guid' (lv_typeGuid_4= RULE_STRING ) )? 'sideAName' (lv_sideAName_6= RULE_STRING ) 'sideAArtifactType' ( ruleNAME_REFERENCE ) 'sideBName' (lv_sideBName_10= RULE_STRING ) 'sideBArtifactType' ( ruleNAME_REFERENCE ) 'defaultOrderType' (lv_defaultOrderType_14= ruleRelationOrderType ) 'multiplicity' (lv_multiplicity_16= ruleRelationMultiplicityEnum ) '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1505:2: 'relationType' (lv_name_1= ruleNAME_REFERENCE ) '{' ( 'guid' (lv_typeGuid_4= RULE_STRING ) )? 'sideAName' (lv_sideAName_6= RULE_STRING ) 'sideAArtifactType' ( ruleNAME_REFERENCE ) 'sideBName' (lv_sideBName_10= RULE_STRING ) 'sideBArtifactType' ( ruleNAME_REFERENCE ) 'defaultOrderType' (lv_defaultOrderType_14= ruleRelationOrderType ) 'multiplicity' (lv_multiplicity_16= ruleRelationMultiplicityEnum ) '}'
            {
            match(input,53,FOLLOW_53_in_ruleRelationType3313); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1509:1: (lv_name_1= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1512:6: lv_name_1= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3347);
            lv_name_1=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_1, "NAME_REFERENCE", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,18,FOLLOW_18_in_ruleRelationType3360); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1534:1: ( 'guid' (lv_typeGuid_4= RULE_STRING ) )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==19) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1534:2: 'guid' (lv_typeGuid_4= RULE_STRING )
                    {
                    match(input,19,FOLLOW_19_in_ruleRelationType3370); 

                            createLeafNode(grammarAccess.getRelationTypeAccess().getGuidKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1538:1: (lv_typeGuid_4= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1540:6: lv_typeGuid_4= RULE_STRING
                    {
                    lv_typeGuid_4=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType3392); 

                    		createLeafNode(grammarAccess.getRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0(), "typeGuid"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "typeGuid", lv_typeGuid_4, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            match(input,54,FOLLOW_54_in_ruleRelationType3411); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideANameKeyword_4(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1562:1: (lv_sideAName_6= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1564:6: lv_sideAName_6= RULE_STRING
            {
            lv_sideAName_6=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType3433); 

            		createLeafNode(grammarAccess.getRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_5_0(), "sideAName"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "sideAName", lv_sideAName_6, "STRING", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,55,FOLLOW_55_in_ruleRelationType3450); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_6(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1586:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1589:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeCrossReference_7_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3477);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,56,FOLLOW_56_in_ruleRelationType3489); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_8(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1608:1: (lv_sideBName_10= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1610:6: lv_sideBName_10= RULE_STRING
            {
            lv_sideBName_10=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType3511); 

            		createLeafNode(grammarAccess.getRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_9_0(), "sideBName"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "sideBName", lv_sideBName_10, "STRING", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,57,FOLLOW_57_in_ruleRelationType3528); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_10(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1632:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1635:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeCrossReference_11_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3555);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,58,FOLLOW_58_in_ruleRelationType3567); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_12(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1654:1: (lv_defaultOrderType_14= ruleRelationOrderType )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1657:6: lv_defaultOrderType_14= ruleRelationOrderType
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_13_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRelationOrderType_in_ruleRelationType3601);
            lv_defaultOrderType_14=ruleRelationOrderType();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "defaultOrderType", lv_defaultOrderType_14, "RelationOrderType", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,59,FOLLOW_59_in_ruleRelationType3614); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_14(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1679:1: (lv_multiplicity_16= ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1682:6: lv_multiplicity_16= ruleRelationMultiplicityEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_15_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_ruleRelationType3648);
            lv_multiplicity_16=ruleRelationMultiplicityEnum();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "multiplicity", lv_multiplicity_16, "RelationMultiplicityEnum", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,20,FOLLOW_20_in_ruleRelationType3661); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_16(), null); 
                

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1711:1: entryRuleRelationOrderType returns [String current=null] : iv_ruleRelationOrderType= ruleRelationOrderType EOF ;
    public final String entryRuleRelationOrderType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleRelationOrderType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1711:58: (iv_ruleRelationOrderType= ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1712:2: iv_ruleRelationOrderType= ruleRelationOrderType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRelationOrderTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3695);
            iv_ruleRelationOrderType=ruleRelationOrderType();
            _fsp--;

             current =iv_ruleRelationOrderType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationOrderType3706); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1719:1: ruleRelationOrderType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) ;
    public final AntlrDatatypeRuleToken ruleRelationOrderType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_ID_3=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1725:6: ( (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1726:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1726:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )
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
                    new NoViableAltException("1726:1: (kw= 'Lexicographical_Ascending' | kw= 'Lexicographical_Descending' | kw= 'Unordered' | this_ID_3= RULE_ID )", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1727:2: kw= 'Lexicographical_Ascending'
                    {
                    kw=(Token)input.LT(1);
                    match(input,60,FOLLOW_60_in_ruleRelationOrderType3744); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0(), null); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1734:2: kw= 'Lexicographical_Descending'
                    {
                    kw=(Token)input.LT(1);
                    match(input,61,FOLLOW_61_in_ruleRelationOrderType3763); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1(), null); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1741:2: kw= 'Unordered'
                    {
                    kw=(Token)input.LT(1);
                    match(input,62,FOLLOW_62_in_ruleRelationOrderType3782); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2(), null); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1747:10: this_ID_3= RULE_ID
                    {
                    this_ID_3=(Token)input.LT(1);
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationOrderType3803); 

                    		current.merge(this_ID_3);
                        
                     
                        createLeafNode(grammarAccess.getRelationOrderTypeAccess().getIDTerminalRuleCall_3(), null); 
                        

                    }
                    break;

            }


            }

             resetLookahead(); 
            	    lastConsumedNode = currentNode;
            	    lastConsumedDatatypeToken = current;
                
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1762:1: ruleRelationMultiplicityEnum returns [Enumerator current=null] : ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) ) ;
    public final Enumerator ruleRelationMultiplicityEnum() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1766:6: ( ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1767:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1767:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )
            int alt31=4;
            switch ( input.LA(1) ) {
            case 63:
                {
                alt31=1;
                }
                break;
            case 64:
                {
                alt31=2;
                }
                break;
            case 65:
                {
                alt31=3;
                }
                break;
            case 66:
                {
                alt31=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1767:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1767:2: ( 'ONE_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1767:2: ( 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1767:4: 'ONE_TO_ONE'
                    {
                    match(input,63,FOLLOW_63_in_ruleRelationMultiplicityEnum3860); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1773:6: ( 'ONE_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1773:6: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1773:8: 'ONE_TO_MANY'
                    {
                    match(input,64,FOLLOW_64_in_ruleRelationMultiplicityEnum3875); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1779:6: ( 'MANY_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1779:6: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1779:8: 'MANY_TO_ONE'
                    {
                    match(input,65,FOLLOW_65_in_ruleRelationMultiplicityEnum3890); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2(), null); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1785:6: ( 'MANY_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1785:6: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1785:8: 'MANY_TO_MANY'
                    {
                    match(input,66,FOLLOW_66_in_ruleRelationMultiplicityEnum3905); 

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
    public static final BitSet FOLLOW_ruleImport_in_ruleOseeTypeModel142 = new BitSet(new long[]{0x002280000080D002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_ruleOseeTypeModel182 = new BitSet(new long[]{0x002280000080C002L});
    public static final BitSet FOLLOW_ruleRelationType_in_ruleOseeTypeModel226 = new BitSet(new long[]{0x002280000080C002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_ruleOseeTypeModel270 = new BitSet(new long[]{0x002280000080C002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_ruleOseeTypeModel314 = new BitSet(new long[]{0x002280000080C002L});
    public static final BitSet FOLLOW_ruleOseeEnumOverride_in_ruleOseeTypeModel358 = new BitSet(new long[]{0x002280000080C002L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport397 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_ruleImport441 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleImport463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE505 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNAME_REFERENCE516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME598 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME649 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_13_in_ruleQUALIFIED_NAME668 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME683 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType730 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_ruleOseeType787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_ruleOseeType814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_ruleOseeType841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_ruleOseeType868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_entryRuleArtifactType900 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactType910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_ruleArtifactType956 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleArtifactType979 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType1013 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_16_in_ruleArtifactType1027 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType1054 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_17_in_ruleArtifactType1067 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType1094 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_18_in_ruleArtifactType1110 = new BitSet(new long[]{0x0000000000380000L});
    public static final BitSet FOLLOW_19_in_ruleArtifactType1120 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleArtifactType1142 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_ruleArtifactType1186 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_20_in_ruleArtifactType1200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef1233 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRef1243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleAttributeTypeRef1277 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeTypeRef1304 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_22_in_ruleAttributeTypeRef1317 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeTypeRef1339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_entryRuleAttributeType1382 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeType1392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleAttributeType1426 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1460 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleAttributeType1474 = new BitSet(new long[]{0x00007FC000000020L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_ruleAttributeType1508 = new BitSet(new long[]{0x0000000001040000L});
    public static final BitSet FOLLOW_24_in_ruleAttributeType1523 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1550 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleAttributeType1564 = new BitSet(new long[]{0x0000000002080000L});
    public static final BitSet FOLLOW_19_in_ruleAttributeType1574 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1596 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_ruleAttributeType1615 = new BitSet(new long[]{0x000000001C000020L});
    public static final BitSet FOLLOW_26_in_ruleAttributeType1637 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_27_in_ruleAttributeType1653 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_28_in_ruleAttributeType1669 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1689 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_ruleAttributeType1707 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1729 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_ruleAttributeType1746 = new BitSet(new long[]{0x0000000080000040L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1769 = new BitSet(new long[]{0x0000003D00100000L});
    public static final BitSet FOLLOW_31_in_ruleAttributeType1780 = new BitSet(new long[]{0x0000003D00100000L});
    public static final BitSet FOLLOW_32_in_ruleAttributeType1805 = new BitSet(new long[]{0x0000000200000020L});
    public static final BitSet FOLLOW_33_in_ruleAttributeType1827 = new BitSet(new long[]{0x0000003C00100000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1847 = new BitSet(new long[]{0x0000003C00100000L});
    public static final BitSet FOLLOW_34_in_ruleAttributeType1868 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1895 = new BitSet(new long[]{0x0000003800100000L});
    public static final BitSet FOLLOW_35_in_ruleAttributeType1910 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1932 = new BitSet(new long[]{0x0000003000100000L});
    public static final BitSet FOLLOW_36_in_ruleAttributeType1952 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1974 = new BitSet(new long[]{0x0000002000100000L});
    public static final BitSet FOLLOW_37_in_ruleAttributeType1994 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType2016 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleAttributeType2035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType2069 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType2080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_ruleAttributeBaseType2118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleAttributeBaseType2137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_ruleAttributeBaseType2156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_ruleAttributeBaseType2175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_ruleAttributeBaseType2194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleAttributeBaseType2213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleAttributeBaseType2232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleAttributeBaseType2251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleAttributeBaseType2270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType2341 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumType2351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleOseeEnumType2385 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumType2419 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleOseeEnumType2432 = new BitSet(new long[]{0x0001000000180000L});
    public static final BitSet FOLLOW_19_in_ruleOseeEnumType2442 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleOseeEnumType2464 = new BitSet(new long[]{0x0001000000100000L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_ruleOseeEnumType2508 = new BitSet(new long[]{0x0001000000100000L});
    public static final BitSet FOLLOW_20_in_ruleOseeEnumType2522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry2555 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumEntry2565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_ruleOseeEnumEntry2599 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumEntry2633 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleOseeEnumEntry2659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumOverride_in_entryRuleOseeEnumOverride2701 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumOverride2711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleOseeEnumOverride2745 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumOverride2772 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleOseeEnumOverride2784 = new BitSet(new long[]{0x001C000000100000L});
    public static final BitSet FOLLOW_50_in_ruleOseeEnumOverride2805 = new BitSet(new long[]{0x0018000000100000L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_ruleOseeEnumOverride2853 = new BitSet(new long[]{0x0018000000100000L});
    public static final BitSet FOLLOW_20_in_ruleOseeEnumOverride2867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption2900 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOverrideOption2910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_ruleOverrideOption2957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_ruleOverrideOption2984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_entryRuleAddEnum3016 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddEnum3026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_ruleAddEnum3060 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAddEnum3094 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAddEnum3120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum3162 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRemoveEnum3172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_ruleRemoveEnum3206 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRemoveEnum3233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_entryRuleRelationType3269 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationType3279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_ruleRelationType3313 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3347 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleRelationType3360 = new BitSet(new long[]{0x0040000000080000L});
    public static final BitSet FOLLOW_19_in_ruleRelationType3370 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType3392 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_ruleRelationType3411 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType3433 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_ruleRelationType3450 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3477 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_ruleRelationType3489 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType3511 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_ruleRelationType3528 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType3555 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_ruleRelationType3567 = new BitSet(new long[]{0x7000000000000020L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_ruleRelationType3601 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_59_in_ruleRelationType3614 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000007L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_ruleRelationType3648 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_ruleRelationType3661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType3695 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationOrderType3706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_ruleRelationOrderType3744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_ruleRelationOrderType3763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_ruleRelationOrderType3782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationOrderType3803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_ruleRelationMultiplicityEnum3860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_ruleRelationMultiplicityEnum3875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleRelationMultiplicityEnum3890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_ruleRelationMultiplicityEnum3905 = new BitSet(new long[]{0x0000000000000002L});

}