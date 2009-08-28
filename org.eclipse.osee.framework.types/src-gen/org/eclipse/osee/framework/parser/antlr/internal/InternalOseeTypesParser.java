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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_WHOLE_NUM_STR", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'import'", "'.'", "'abstract'", "'artifactType'", "'extends'", "'overrides'", "'{'", "'}'", "','", "'attribute'", "'branchGuid'", "'attributeType'", "'dataProvider'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'MappedAttributeDataProvider'", "'min'", "'max'", "'unlimited'", "'taggerId'", "'DefaultAttributeTaggerProvider'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'WordAttribute'", "'oseeEnumType'", "'entry'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'multiplicity'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'"
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:81:1: ruleOseeTypeModel returns [EObject current=null] : ( (lv_imports_0= ruleImport )* (lv_types_1= ruleOseeType )* ) ;
    public final EObject ruleOseeTypeModel() throws RecognitionException {
        EObject current = null;

        EObject lv_imports_0 = null;

        EObject lv_types_1 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:86:6: ( ( (lv_imports_0= ruleImport )* (lv_types_1= ruleOseeType )* ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:1: ( (lv_imports_0= ruleImport )* (lv_types_1= ruleOseeType )* )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:1: ( (lv_imports_0= ruleImport )* (lv_types_1= ruleOseeType )* )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:87:2: (lv_imports_0= ruleImport )* (lv_types_1= ruleOseeType )*
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:108:3: (lv_types_1= ruleOseeType )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==RULE_STRING||(LA2_0>=14 && LA2_0<=15)||LA2_0==23||LA2_0==46||LA2_0==48) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:111:6: lv_types_1= ruleOseeType
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeTypeModelAccess().getTypesOseeTypeParserRuleCall_1_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOseeType_in_ruleOseeTypeModel181);
            	    lv_types_1=ruleOseeType();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeTypeModelRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "types", lv_types_1, "OseeType", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:136:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:136:48: (iv_ruleImport= ruleImport EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:137:2: iv_ruleImport= ruleImport EOF
            {
             currentNode = createCompositeNode(grammarAccess.getImportRule(), currentNode); 
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport219);
            iv_ruleImport=ruleImport();
            _fsp--;

             current =iv_ruleImport; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport229); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:144:1: ruleImport returns [EObject current=null] : ( 'import' (lv_importURI_1= RULE_STRING ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token lv_importURI_1=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:149:6: ( ( 'import' (lv_importURI_1= RULE_STRING ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:150:1: ( 'import' (lv_importURI_1= RULE_STRING ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:150:1: ( 'import' (lv_importURI_1= RULE_STRING ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:150:2: 'import' (lv_importURI_1= RULE_STRING )
            {
            match(input,12,FOLLOW_12_in_ruleImport263); 

                    createLeafNode(grammarAccess.getImportAccess().getImportKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:154:1: (lv_importURI_1= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:156:6: lv_importURI_1= RULE_STRING
            {
            lv_importURI_1=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleImport285); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:181:1: entryRuleNAME_REFERENCE returns [String current=null] : iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF ;
    public final String entryRuleNAME_REFERENCE() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNAME_REFERENCE = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:181:55: (iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:182:2: iv_ruleNAME_REFERENCE= ruleNAME_REFERENCE EOF
            {
             currentNode = createCompositeNode(grammarAccess.getNAME_REFERENCERule(), currentNode); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE327);
            iv_ruleNAME_REFERENCE=ruleNAME_REFERENCE();
            _fsp--;

             current =iv_ruleNAME_REFERENCE.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNAME_REFERENCE338); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:189:1: ruleNAME_REFERENCE returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleNAME_REFERENCE() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:195:6: (this_STRING_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:196:5: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE377); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:211:1: entryRuleQUALIFIED_NAME returns [String current=null] : iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF ;
    public final String entryRuleQUALIFIED_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQUALIFIED_NAME = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:211:55: (iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:212:2: iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF
            {
             currentNode = createCompositeNode(grammarAccess.getQUALIFIED_NAMERule(), currentNode); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME420);
            iv_ruleQUALIFIED_NAME=ruleQUALIFIED_NAME();
            _fsp--;

             current =iv_ruleQUALIFIED_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME431); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:219:1: ruleQUALIFIED_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQUALIFIED_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:225:6: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:226:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:226:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:226:6: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME471); 

            		current.merge(this_ID_0);
                
             
                createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:233:1: (kw= '.' this_ID_2= RULE_ID )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==13) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:234:2: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,13,FOLLOW_13_in_ruleQUALIFIED_NAME490); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0(), null); 
            	        
            	    this_ID_2=(Token)input.LT(1);
            	    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME505); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:254:1: entryRuleOseeType returns [EObject current=null] : iv_ruleOseeType= ruleOseeType EOF ;
    public final EObject entryRuleOseeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:254:50: (iv_ruleOseeType= ruleOseeType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:255:2: iv_ruleOseeType= ruleOseeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType550);
            iv_ruleOseeType=ruleOseeType();
            _fsp--;

             current =iv_ruleOseeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType560); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:262:1: ruleOseeType returns [EObject current=null] : (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType ) ;
    public final EObject ruleOseeType() throws RecognitionException {
        EObject current = null;

        EObject this_ArtifactType_0 = null;

        EObject this_RelationType_1 = null;

        EObject this_AttributeType_2 = null;

        EObject this_OseeEnumType_3 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:267:6: ( (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:268:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:268:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )
            int alt4=4;
            switch ( input.LA(1) ) {
            case 14:
            case 15:
                {
                alt4=1;
                }
                break;
            case 48:
                {
                alt4=2;
                }
                break;
            case RULE_STRING:
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
                    new NoViableAltException("268:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:269:5: this_ArtifactType_0= ruleArtifactType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleArtifactType_in_ruleOseeType607);
                    this_ArtifactType_0=ruleArtifactType();
                    _fsp--;

                     
                            current = this_ArtifactType_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:279:5: this_RelationType_1= ruleRelationType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleRelationType_in_ruleOseeType634);
                    this_RelationType_1=ruleRelationType();
                    _fsp--;

                     
                            current = this_RelationType_1; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:289:5: this_AttributeType_2= ruleAttributeType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleAttributeType_in_ruleOseeType661);
                    this_AttributeType_2=ruleAttributeType();
                    _fsp--;

                     
                            current = this_AttributeType_2; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:299:5: this_OseeEnumType_3= ruleOseeEnumType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getOseeEnumTypeParserRuleCall_3(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleOseeEnumType_in_ruleOseeType688);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:314:1: entryRuleArtifactType returns [EObject current=null] : iv_ruleArtifactType= ruleArtifactType EOF ;
    public final EObject entryRuleArtifactType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:314:54: (iv_ruleArtifactType= ruleArtifactType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:315:2: iv_ruleArtifactType= ruleArtifactType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getArtifactTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleArtifactType_in_entryRuleArtifactType720);
            iv_ruleArtifactType=ruleArtifactType();
            _fsp--;

             current =iv_ruleArtifactType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactType730); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:322:1: ruleArtifactType returns [EObject current=null] : ( (lv_abstract_0= 'abstract' )? 'artifactType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' (lv_superArtifactTypes_4= ruleArtifactSuperTypes ) )? ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_8= RULE_STRING )? (lv_validAttributeTypes_9= ruleAttributeTypeRef )* '}' ) ;
    public final EObject ruleArtifactType() throws RecognitionException {
        EObject current = null;

        Token lv_abstract_0=null;
        Token lv_typeGuid_8=null;
        AntlrDatatypeRuleToken lv_name_2 = null;

        EObject lv_superArtifactTypes_4 = null;

        EObject lv_validAttributeTypes_9 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:327:6: ( ( (lv_abstract_0= 'abstract' )? 'artifactType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' (lv_superArtifactTypes_4= ruleArtifactSuperTypes ) )? ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_8= RULE_STRING )? (lv_validAttributeTypes_9= ruleAttributeTypeRef )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:328:1: ( (lv_abstract_0= 'abstract' )? 'artifactType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' (lv_superArtifactTypes_4= ruleArtifactSuperTypes ) )? ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_8= RULE_STRING )? (lv_validAttributeTypes_9= ruleAttributeTypeRef )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:328:1: ( (lv_abstract_0= 'abstract' )? 'artifactType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' (lv_superArtifactTypes_4= ruleArtifactSuperTypes ) )? ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_8= RULE_STRING )? (lv_validAttributeTypes_9= ruleAttributeTypeRef )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:328:2: (lv_abstract_0= 'abstract' )? 'artifactType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' (lv_superArtifactTypes_4= ruleArtifactSuperTypes ) )? ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_8= RULE_STRING )? (lv_validAttributeTypes_9= ruleAttributeTypeRef )* '}'
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:328:2: (lv_abstract_0= 'abstract' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==14) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:330:6: lv_abstract_0= 'abstract'
                    {
                    lv_abstract_0=(Token)input.LT(1);
                    match(input,14,FOLLOW_14_in_ruleArtifactType776); 

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

            match(input,15,FOLLOW_15_in_ruleArtifactType799); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:353:1: (lv_name_2= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:356:6: lv_name_2= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType833);
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:374:2: ( 'extends' (lv_superArtifactTypes_4= ruleArtifactSuperTypes ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==16) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:374:3: 'extends' (lv_superArtifactTypes_4= ruleArtifactSuperTypes )
                    {
                    match(input,16,FOLLOW_16_in_ruleArtifactType847); 

                            createLeafNode(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:378:1: (lv_superArtifactTypes_4= ruleArtifactSuperTypes )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:381:6: lv_superArtifactTypes_4= ruleArtifactSuperTypes
                    {
                     
                    	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactSuperTypesParserRuleCall_3_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleArtifactSuperTypes_in_ruleArtifactType881);
                    lv_superArtifactTypes_4=ruleArtifactSuperTypes();
                    _fsp--;


                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode.getParent(), current);
                    	        }
                    	        
                    	        try {
                    	       		add(current, "superArtifactTypes", lv_superArtifactTypes_4, "ArtifactSuperTypes", currentNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:399:4: ( 'overrides' ( ruleNAME_REFERENCE ) )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==17) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:399:5: 'overrides' ( ruleNAME_REFERENCE )
                    {
                    match(input,17,FOLLOW_17_in_ruleArtifactType897); 

                            createLeafNode(grammarAccess.getArtifactTypeAccess().getOverridesKeyword_4_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:403:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:406:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getOverrideArtifactTypeCrossReference_4_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType924);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }
                    break;

            }

            match(input,18,FOLLOW_18_in_ruleArtifactType938); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:425:1: (lv_typeGuid_8= RULE_STRING )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_STRING) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:427:6: lv_typeGuid_8= RULE_STRING
                    {
                    lv_typeGuid_8=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleArtifactType960); 

                    		createLeafNode(grammarAccess.getArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0(), "typeGuid"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "typeGuid", lv_typeGuid_8, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:445:3: (lv_validAttributeTypes_9= ruleAttributeTypeRef )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==21) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:448:6: lv_validAttributeTypes_9= ruleAttributeTypeRef
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAttributeTypeRefParserRuleCall_7_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleAttributeTypeRef_in_ruleArtifactType1003);
            	    lv_validAttributeTypes_9=ruleAttributeTypeRef();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "validAttributeTypes", lv_validAttributeTypes_9, "AttributeTypeRef", currentNode);
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

            match(input,19,FOLLOW_19_in_ruleArtifactType1017); 

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


    // $ANTLR start entryRuleArtifactSuperTypes
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:477:1: entryRuleArtifactSuperTypes returns [EObject current=null] : iv_ruleArtifactSuperTypes= ruleArtifactSuperTypes EOF ;
    public final EObject entryRuleArtifactSuperTypes() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactSuperTypes = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:477:60: (iv_ruleArtifactSuperTypes= ruleArtifactSuperTypes EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:478:2: iv_ruleArtifactSuperTypes= ruleArtifactSuperTypes EOF
            {
             currentNode = createCompositeNode(grammarAccess.getArtifactSuperTypesRule(), currentNode); 
            pushFollow(FOLLOW_ruleArtifactSuperTypes_in_entryRuleArtifactSuperTypes1050);
            iv_ruleArtifactSuperTypes=ruleArtifactSuperTypes();
            _fsp--;

             current =iv_ruleArtifactSuperTypes; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactSuperTypes1060); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleArtifactSuperTypes


    // $ANTLR start ruleArtifactSuperTypes
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:485:1: ruleArtifactSuperTypes returns [EObject current=null] : ( ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )* ) ;
    public final EObject ruleArtifactSuperTypes() throws RecognitionException {
        EObject current = null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:490:6: ( ( ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )* ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:491:1: ( ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )* )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:491:1: ( ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )* )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:491:2: ( ruleNAME_REFERENCE ) ( ',' ( ruleNAME_REFERENCE ) )*
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:491:2: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:494:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getArtifactSuperTypesRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getArtifactSuperTypesAccess().getArtifactSuperTypeArtifactTypeCrossReference_0_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactSuperTypes1112);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:509:2: ( ',' ( ruleNAME_REFERENCE ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==20) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:509:3: ',' ( ruleNAME_REFERENCE )
            	    {
            	    match(input,20,FOLLOW_20_in_ruleArtifactSuperTypes1125); 

            	            createLeafNode(grammarAccess.getArtifactSuperTypesAccess().getCommaKeyword_1_0(), null); 
            	        
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:513:1: ( ruleNAME_REFERENCE )
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:516:3: ruleNAME_REFERENCE
            	    {

            	    			if (current==null) {
            	    	            current = factory.create(grammarAccess.getArtifactSuperTypesRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode, current);
            	    	        }
            	            
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getArtifactSuperTypesAccess().getArtifactSuperTypeArtifactTypeCrossReference_1_1_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactSuperTypes1152);
            	    ruleNAME_REFERENCE();
            	    _fsp--;

            	     
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
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
    // $ANTLR end ruleArtifactSuperTypes


    // $ANTLR start entryRuleAttributeTypeRef
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:538:1: entryRuleAttributeTypeRef returns [EObject current=null] : iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF ;
    public final EObject entryRuleAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeTypeRef = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:538:58: (iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:539:2: iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeTypeRefRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef1190);
            iv_ruleAttributeTypeRef=ruleAttributeTypeRef();
            _fsp--;

             current =iv_ruleAttributeTypeRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRef1200); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:546:1: ruleAttributeTypeRef returns [EObject current=null] : ( 'attribute' ( ruleNAME_REFERENCE ) ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )? ) ;
    public final EObject ruleAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        Token lv_branchGuid_3=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:551:6: ( ( 'attribute' ( ruleNAME_REFERENCE ) ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:552:1: ( 'attribute' ( ruleNAME_REFERENCE ) ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:552:1: ( 'attribute' ( ruleNAME_REFERENCE ) ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:552:2: 'attribute' ( ruleNAME_REFERENCE ) ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )?
            {
            match(input,21,FOLLOW_21_in_ruleAttributeTypeRef1234); 

                    createLeafNode(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:556:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:559:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeCrossReference_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeTypeRef1261);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:574:2: ( 'branchGuid' (lv_branchGuid_3= RULE_STRING ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==22) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:574:3: 'branchGuid' (lv_branchGuid_3= RULE_STRING )
                    {
                    match(input,22,FOLLOW_22_in_ruleAttributeTypeRef1274); 

                            createLeafNode(grammarAccess.getAttributeTypeRefAccess().getBranchGuidKeyword_2_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:578:1: (lv_branchGuid_3= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:580:6: lv_branchGuid_3= RULE_STRING
                    {
                    lv_branchGuid_3=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeTypeRef1296); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:605:1: entryRuleAttributeType returns [EObject current=null] : iv_ruleAttributeType= ruleAttributeType EOF ;
    public final EObject entryRuleAttributeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:605:55: (iv_ruleAttributeType= ruleAttributeType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:606:2: iv_ruleAttributeType= ruleAttributeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeType_in_entryRuleAttributeType1339);
            iv_ruleAttributeType=ruleAttributeType();
            _fsp--;

             current =iv_ruleAttributeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeType1349); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:613:1: ruleAttributeType returns [EObject current=null] : ( (lv_typeGuid_0= RULE_STRING )? 'attributeType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' (lv_baseAttributeType_4= ruleAttributeBaseType ) ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' 'dataProvider' (lv_dataProvider_9= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_11= RULE_WHOLE_NUM_STR ) 'max' (lv_max_13= ( RULE_WHOLE_NUM_STR | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_15= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( ruleNAME_REFERENCE ) )? ( 'description' (lv_description_19= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_21= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_23= RULE_STRING ) )? '}' ) ;
    public final EObject ruleAttributeType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_0=null;
        Token lv_dataProvider_9=null;
        Token lv_min_11=null;
        Token lv_max_13=null;
        Token lv_taggerId_15=null;
        Token lv_description_19=null;
        Token lv_defaultValue_21=null;
        Token lv_fileExtension_23=null;
        AntlrDatatypeRuleToken lv_name_2 = null;

        AntlrDatatypeRuleToken lv_baseAttributeType_4 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:618:6: ( ( (lv_typeGuid_0= RULE_STRING )? 'attributeType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' (lv_baseAttributeType_4= ruleAttributeBaseType ) ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' 'dataProvider' (lv_dataProvider_9= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_11= RULE_WHOLE_NUM_STR ) 'max' (lv_max_13= ( RULE_WHOLE_NUM_STR | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_15= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( ruleNAME_REFERENCE ) )? ( 'description' (lv_description_19= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_21= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_23= RULE_STRING ) )? '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:619:1: ( (lv_typeGuid_0= RULE_STRING )? 'attributeType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' (lv_baseAttributeType_4= ruleAttributeBaseType ) ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' 'dataProvider' (lv_dataProvider_9= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_11= RULE_WHOLE_NUM_STR ) 'max' (lv_max_13= ( RULE_WHOLE_NUM_STR | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_15= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( ruleNAME_REFERENCE ) )? ( 'description' (lv_description_19= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_21= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_23= RULE_STRING ) )? '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:619:1: ( (lv_typeGuid_0= RULE_STRING )? 'attributeType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' (lv_baseAttributeType_4= ruleAttributeBaseType ) ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' 'dataProvider' (lv_dataProvider_9= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_11= RULE_WHOLE_NUM_STR ) 'max' (lv_max_13= ( RULE_WHOLE_NUM_STR | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_15= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( ruleNAME_REFERENCE ) )? ( 'description' (lv_description_19= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_21= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_23= RULE_STRING ) )? '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:619:2: (lv_typeGuid_0= RULE_STRING )? 'attributeType' (lv_name_2= ruleNAME_REFERENCE ) ( 'extends' (lv_baseAttributeType_4= ruleAttributeBaseType ) ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' 'dataProvider' (lv_dataProvider_9= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_11= RULE_WHOLE_NUM_STR ) 'max' (lv_max_13= ( RULE_WHOLE_NUM_STR | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_15= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( ruleNAME_REFERENCE ) )? ( 'description' (lv_description_19= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_21= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_23= RULE_STRING ) )? '}'
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:619:2: (lv_typeGuid_0= RULE_STRING )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==RULE_STRING) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:621:6: lv_typeGuid_0= RULE_STRING
                    {
                    lv_typeGuid_0=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1396); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_0_0(), "typeGuid"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "typeGuid", lv_typeGuid_0, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }
                    break;

            }

            match(input,23,FOLLOW_23_in_ruleAttributeType1414); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_1(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:643:1: (lv_name_2= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:646:6: lv_name_2= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1448);
            lv_name_2=ruleNAME_REFERENCE();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_2, "NAME_REFERENCE", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:664:2: ( 'extends' (lv_baseAttributeType_4= ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:664:3: 'extends' (lv_baseAttributeType_4= ruleAttributeBaseType )
            {
            match(input,16,FOLLOW_16_in_ruleAttributeType1462); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_3_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:668:1: (lv_baseAttributeType_4= ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:671:6: lv_baseAttributeType_4= ruleAttributeBaseType
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_3_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleAttributeBaseType_in_ruleAttributeType1496);
            lv_baseAttributeType_4=ruleAttributeBaseType();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "baseAttributeType", lv_baseAttributeType_4, "AttributeBaseType", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }


            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:689:3: ( 'overrides' ( ruleNAME_REFERENCE ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==17) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:689:4: 'overrides' ( ruleNAME_REFERENCE )
                    {
                    match(input,17,FOLLOW_17_in_ruleAttributeType1511); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getOverridesKeyword_4_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:693:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:696:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeCrossReference_4_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1538);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }
                    break;

            }

            match(input,18,FOLLOW_18_in_ruleAttributeType1552); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_5(), null); 
                
            match(input,24,FOLLOW_24_in_ruleAttributeType1561); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_6(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:719:1: (lv_dataProvider_9= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:721:6: lv_dataProvider_9= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:721:24: ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME )
            int alt14=4;
            switch ( input.LA(1) ) {
            case 25:
                {
                alt14=1;
                }
                break;
            case 26:
                {
                alt14=2;
                }
                break;
            case 27:
                {
                alt14=3;
                }
                break;
            case RULE_ID:
                {
                alt14=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("721:24: ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME )", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:721:25: 'DefaultAttributeDataProvider'
                    {
                    match(input,25,FOLLOW_25_in_ruleAttributeType1583); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_7_0_0(), "dataProvider"); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:727:6: 'UriAttributeDataProvider'
                    {
                    match(input,26,FOLLOW_26_in_ruleAttributeType1599); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_7_0_1(), "dataProvider"); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:733:6: 'MappedAttributeDataProvider'
                    {
                    match(input,27,FOLLOW_27_in_ruleAttributeType1615); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderMappedAttributeDataProviderKeyword_7_0_2(), "dataProvider"); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:739:7: ruleQUALIFIED_NAME
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_7_0_3(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1635);
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
            	       		set(current, "dataProvider", /* lv_dataProvider_9 */ input.LT(-1), null, lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,28,FOLLOW_28_in_ruleAttributeType1653); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getMinKeyword_8(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:764:1: (lv_min_11= RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:766:6: lv_min_11= RULE_WHOLE_NUM_STR
            {
            lv_min_11=(Token)input.LT(1);
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1675); 

            		createLeafNode(grammarAccess.getAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_9_0(), "min"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "min", lv_min_11, "WHOLE_NUM_STR", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,29,FOLLOW_29_in_ruleAttributeType1692); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxKeyword_10(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:788:1: (lv_max_13= ( RULE_WHOLE_NUM_STR | 'unlimited' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:790:6: lv_max_13= ( RULE_WHOLE_NUM_STR | 'unlimited' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:790:16: ( RULE_WHOLE_NUM_STR | 'unlimited' )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==RULE_WHOLE_NUM_STR) ) {
                alt15=1;
            }
            else if ( (LA15_0==30) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("790:16: ( RULE_WHOLE_NUM_STR | 'unlimited' )", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:790:18: RULE_WHOLE_NUM_STR
                    {
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1715); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_11_0_0(), "max"); 
                    	

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:795:6: 'unlimited'
                    {
                    match(input,30,FOLLOW_30_in_ruleAttributeType1726); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_11_0_1(), "max"); 
                        

                    }
                    break;

            }


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "max", /* lv_max_13 */ input.LT(-1), null, lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:815:2: ( 'taggerId' (lv_taggerId_15= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==31) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:815:3: 'taggerId' (lv_taggerId_15= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) )
                    {
                    match(input,31,FOLLOW_31_in_ruleAttributeType1751); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_12_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:819:1: (lv_taggerId_15= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:821:6: lv_taggerId_15= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:821:21: ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME )
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==32) ) {
                        alt16=1;
                    }
                    else if ( (LA16_0==RULE_ID) ) {
                        alt16=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("821:21: ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME )", 16, 0, input);

                        throw nvae;
                    }
                    switch (alt16) {
                        case 1 :
                            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:821:22: 'DefaultAttributeTaggerProvider'
                            {
                            match(input,32,FOLLOW_32_in_ruleAttributeType1773); 

                                    createLeafNode(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_12_1_0_0(), "taggerId"); 
                                

                            }
                            break;
                        case 2 :
                            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:827:7: ruleQUALIFIED_NAME
                            {
                             
                                    currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_12_1_0_1(), currentNode); 
                                
                            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1793);
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
                    	       		set(current, "taggerId", /* lv_taggerId_15 */ input.LT(-1), null, lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:848:4: ( 'enumType' ( ruleNAME_REFERENCE ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==33) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:848:5: 'enumType' ( ruleNAME_REFERENCE )
                    {
                    match(input,33,FOLLOW_33_in_ruleAttributeType1814); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_13_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:852:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:855:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_13_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1841);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:870:4: ( 'description' (lv_description_19= RULE_STRING ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==34) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:870:5: 'description' (lv_description_19= RULE_STRING )
                    {
                    match(input,34,FOLLOW_34_in_ruleAttributeType1856); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_14_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:874:1: (lv_description_19= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:876:6: lv_description_19= RULE_STRING
                    {
                    lv_description_19=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1878); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_14_1_0(), "description"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "description", lv_description_19, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:894:4: ( 'defaultValue' (lv_defaultValue_21= RULE_STRING ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==35) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:894:5: 'defaultValue' (lv_defaultValue_21= RULE_STRING )
                    {
                    match(input,35,FOLLOW_35_in_ruleAttributeType1898); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_15_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:898:1: (lv_defaultValue_21= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:900:6: lv_defaultValue_21= RULE_STRING
                    {
                    lv_defaultValue_21=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1920); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_15_1_0(), "defaultValue"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "defaultValue", lv_defaultValue_21, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:918:4: ( 'fileExtension' (lv_fileExtension_23= RULE_STRING ) )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==36) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:918:5: 'fileExtension' (lv_fileExtension_23= RULE_STRING )
                    {
                    match(input,36,FOLLOW_36_in_ruleAttributeType1940); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_16_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:922:1: (lv_fileExtension_23= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:924:6: lv_fileExtension_23= RULE_STRING
                    {
                    lv_fileExtension_23=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1962); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_16_1_0(), "fileExtension"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "fileExtension", lv_fileExtension_23, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            match(input,19,FOLLOW_19_in_ruleAttributeType1981); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:953:1: entryRuleAttributeBaseType returns [String current=null] : iv_ruleAttributeBaseType= ruleAttributeBaseType EOF ;
    public final String entryRuleAttributeBaseType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAttributeBaseType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:953:58: (iv_ruleAttributeBaseType= ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:954:2: iv_ruleAttributeBaseType= ruleAttributeBaseType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeBaseTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType2015);
            iv_ruleAttributeBaseType=ruleAttributeBaseType();
            _fsp--;

             current =iv_ruleAttributeBaseType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType2026); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:961:1: ruleAttributeBaseType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) ;
    public final AntlrDatatypeRuleToken ruleAttributeBaseType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QUALIFIED_NAME_9 = null;


         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:967:6: ( (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:968:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:968:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            int alt22=10;
            switch ( input.LA(1) ) {
            case 37:
                {
                alt22=1;
                }
                break;
            case 38:
                {
                alt22=2;
                }
                break;
            case 39:
                {
                alt22=3;
                }
                break;
            case 40:
                {
                alt22=4;
                }
                break;
            case 41:
                {
                alt22=5;
                }
                break;
            case 42:
                {
                alt22=6;
                }
                break;
            case 43:
                {
                alt22=7;
                }
                break;
            case 44:
                {
                alt22=8;
                }
                break;
            case 45:
                {
                alt22=9;
                }
                break;
            case RULE_ID:
                {
                alt22=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("968:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:969:2: kw= 'BooleanAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_ruleAttributeBaseType2064); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0(), null); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:976:2: kw= 'CompressedContentAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_ruleAttributeBaseType2083); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1(), null); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:983:2: kw= 'DateAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,39,FOLLOW_39_in_ruleAttributeBaseType2102); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2(), null); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:990:2: kw= 'EnumeratedAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,40,FOLLOW_40_in_ruleAttributeBaseType2121); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3(), null); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:997:2: kw= 'FloatingPointAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,41,FOLLOW_41_in_ruleAttributeBaseType2140); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4(), null); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1004:2: kw= 'IntegerAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,42,FOLLOW_42_in_ruleAttributeBaseType2159); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5(), null); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1011:2: kw= 'JavaObjectAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_ruleAttributeBaseType2178); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6(), null); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1018:2: kw= 'StringAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_ruleAttributeBaseType2197); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7(), null); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1025:2: kw= 'WordAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,45,FOLLOW_45_in_ruleAttributeBaseType2216); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8(), null); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1032:5: this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2244);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1050:1: entryRuleOseeEnumType returns [EObject current=null] : iv_ruleOseeEnumType= ruleOseeEnumType EOF ;
    public final EObject entryRuleOseeEnumType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeEnumType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1050:54: (iv_ruleOseeEnumType= ruleOseeEnumType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1051:2: iv_ruleOseeEnumType= ruleOseeEnumType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeEnumTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType2287);
            iv_ruleOseeEnumType=ruleOseeEnumType();
            _fsp--;

             current =iv_ruleOseeEnumType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumType2297); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1058:1: ruleOseeEnumType returns [EObject current=null] : ( 'oseeEnumType' (lv_name_1= ruleNAME_REFERENCE ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_5= RULE_STRING )? (lv_enumEntries_6= ruleOseeEnumEntry )* '}' ) ;
    public final EObject ruleOseeEnumType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_5=null;
        AntlrDatatypeRuleToken lv_name_1 = null;

        EObject lv_enumEntries_6 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1063:6: ( ( 'oseeEnumType' (lv_name_1= ruleNAME_REFERENCE ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_5= RULE_STRING )? (lv_enumEntries_6= ruleOseeEnumEntry )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1064:1: ( 'oseeEnumType' (lv_name_1= ruleNAME_REFERENCE ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_5= RULE_STRING )? (lv_enumEntries_6= ruleOseeEnumEntry )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1064:1: ( 'oseeEnumType' (lv_name_1= ruleNAME_REFERENCE ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_5= RULE_STRING )? (lv_enumEntries_6= ruleOseeEnumEntry )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1064:2: 'oseeEnumType' (lv_name_1= ruleNAME_REFERENCE ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_5= RULE_STRING )? (lv_enumEntries_6= ruleOseeEnumEntry )* '}'
            {
            match(input,46,FOLLOW_46_in_ruleOseeEnumType2331); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1068:1: (lv_name_1= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1071:6: lv_name_1= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumType2365);
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1089:2: ( 'overrides' ( ruleNAME_REFERENCE ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==17) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1089:3: 'overrides' ( ruleNAME_REFERENCE )
                    {
                    match(input,17,FOLLOW_17_in_ruleOseeEnumType2379); 

                            createLeafNode(grammarAccess.getOseeEnumTypeAccess().getOverridesKeyword_2_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1093:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1096:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getOseeEnumTypeAccess().getOverrideOseeEnumTypeCrossReference_2_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumType2406);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }
                    break;

            }

            match(input,18,FOLLOW_18_in_ruleOseeEnumType2420); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_3(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1115:1: (lv_typeGuid_5= RULE_STRING )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==RULE_STRING) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1117:6: lv_typeGuid_5= RULE_STRING
                    {
                    lv_typeGuid_5=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleOseeEnumType2442); 

                    		createLeafNode(grammarAccess.getOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0(), "typeGuid"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "typeGuid", lv_typeGuid_5, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1135:3: (lv_enumEntries_6= ruleOseeEnumEntry )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==47) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1138:6: lv_enumEntries_6= ruleOseeEnumEntry
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesOseeEnumEntryParserRuleCall_5_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOseeEnumEntry_in_ruleOseeEnumType2485);
            	    lv_enumEntries_6=ruleOseeEnumEntry();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "enumEntries", lv_enumEntries_6, "OseeEnumEntry", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

            match(input,19,FOLLOW_19_in_ruleOseeEnumType2499); 

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1167:1: entryRuleOseeEnumEntry returns [EObject current=null] : iv_ruleOseeEnumEntry= ruleOseeEnumEntry EOF ;
    public final EObject entryRuleOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeEnumEntry = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1167:55: (iv_ruleOseeEnumEntry= ruleOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1168:2: iv_ruleOseeEnumEntry= ruleOseeEnumEntry EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeEnumEntryRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry2532);
            iv_ruleOseeEnumEntry=ruleOseeEnumEntry();
            _fsp--;

             current =iv_ruleOseeEnumEntry; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumEntry2542); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1175:1: ruleOseeEnumEntry returns [EObject current=null] : ( 'entry' (lv_name_1= RULE_STRING ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? ) ;
    public final EObject ruleOseeEnumEntry() throws RecognitionException {
        EObject current = null;

        Token lv_name_1=null;
        Token lv_ordinal_2=null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1180:6: ( ( 'entry' (lv_name_1= RULE_STRING ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1181:1: ( 'entry' (lv_name_1= RULE_STRING ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1181:1: ( 'entry' (lv_name_1= RULE_STRING ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1181:2: 'entry' (lv_name_1= RULE_STRING ) (lv_ordinal_2= RULE_WHOLE_NUM_STR )?
            {
            match(input,47,FOLLOW_47_in_ruleOseeEnumEntry2576); 

                    createLeafNode(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1185:1: (lv_name_1= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1187:6: lv_name_1= RULE_STRING
            {
            lv_name_1=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleOseeEnumEntry2598); 

            		createLeafNode(grammarAccess.getOseeEnumEntryAccess().getNameSTRINGTerminalRuleCall_1_0(), "name"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getOseeEnumEntryRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_1, "STRING", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1205:2: (lv_ordinal_2= RULE_WHOLE_NUM_STR )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==RULE_WHOLE_NUM_STR) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1207:6: lv_ordinal_2= RULE_WHOLE_NUM_STR
                    {
                    lv_ordinal_2=(Token)input.LT(1);
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_ruleOseeEnumEntry2628); 

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


    // $ANTLR start entryRuleRelationType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1232:1: entryRuleRelationType returns [EObject current=null] : iv_ruleRelationType= ruleRelationType EOF ;
    public final EObject entryRuleRelationType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1232:54: (iv_ruleRelationType= ruleRelationType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1233:2: iv_ruleRelationType= ruleRelationType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRelationTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleRelationType_in_entryRuleRelationType2670);
            iv_ruleRelationType=ruleRelationType();
            _fsp--;

             current =iv_ruleRelationType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationType2680); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1240:1: ruleRelationType returns [EObject current=null] : ( 'relationType' (lv_name_1= ruleNAME_REFERENCE ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_5= RULE_STRING )? 'sideAName' (lv_sideAName_7= RULE_STRING ) 'sideAArtifactType' ( ruleNAME_REFERENCE ) 'sideBName' (lv_sideBName_11= RULE_STRING ) 'sideBArtifactType' ( ruleNAME_REFERENCE ) 'defaultOrderType' (lv_defaultOrderType_15= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) ) 'multiplicity' (lv_multiplicity_17= ruleRelationMultiplicityEnum ) '}' ) ;
    public final EObject ruleRelationType() throws RecognitionException {
        EObject current = null;

        Token lv_typeGuid_5=null;
        Token lv_sideAName_7=null;
        Token lv_sideBName_11=null;
        Token lv_defaultOrderType_15=null;
        AntlrDatatypeRuleToken lv_name_1 = null;

        Enumerator lv_multiplicity_17 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1245:6: ( ( 'relationType' (lv_name_1= ruleNAME_REFERENCE ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_5= RULE_STRING )? 'sideAName' (lv_sideAName_7= RULE_STRING ) 'sideAArtifactType' ( ruleNAME_REFERENCE ) 'sideBName' (lv_sideBName_11= RULE_STRING ) 'sideBArtifactType' ( ruleNAME_REFERENCE ) 'defaultOrderType' (lv_defaultOrderType_15= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) ) 'multiplicity' (lv_multiplicity_17= ruleRelationMultiplicityEnum ) '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1246:1: ( 'relationType' (lv_name_1= ruleNAME_REFERENCE ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_5= RULE_STRING )? 'sideAName' (lv_sideAName_7= RULE_STRING ) 'sideAArtifactType' ( ruleNAME_REFERENCE ) 'sideBName' (lv_sideBName_11= RULE_STRING ) 'sideBArtifactType' ( ruleNAME_REFERENCE ) 'defaultOrderType' (lv_defaultOrderType_15= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) ) 'multiplicity' (lv_multiplicity_17= ruleRelationMultiplicityEnum ) '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1246:1: ( 'relationType' (lv_name_1= ruleNAME_REFERENCE ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_5= RULE_STRING )? 'sideAName' (lv_sideAName_7= RULE_STRING ) 'sideAArtifactType' ( ruleNAME_REFERENCE ) 'sideBName' (lv_sideBName_11= RULE_STRING ) 'sideBArtifactType' ( ruleNAME_REFERENCE ) 'defaultOrderType' (lv_defaultOrderType_15= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) ) 'multiplicity' (lv_multiplicity_17= ruleRelationMultiplicityEnum ) '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1246:2: 'relationType' (lv_name_1= ruleNAME_REFERENCE ) ( 'overrides' ( ruleNAME_REFERENCE ) )? '{' (lv_typeGuid_5= RULE_STRING )? 'sideAName' (lv_sideAName_7= RULE_STRING ) 'sideAArtifactType' ( ruleNAME_REFERENCE ) 'sideBName' (lv_sideBName_11= RULE_STRING ) 'sideBArtifactType' ( ruleNAME_REFERENCE ) 'defaultOrderType' (lv_defaultOrderType_15= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) ) 'multiplicity' (lv_multiplicity_17= ruleRelationMultiplicityEnum ) '}'
            {
            match(input,48,FOLLOW_48_in_ruleRelationType2714); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1250:1: (lv_name_1= ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1253:6: lv_name_1= ruleNAME_REFERENCE
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType2748);
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

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1271:2: ( 'overrides' ( ruleNAME_REFERENCE ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==17) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1271:3: 'overrides' ( ruleNAME_REFERENCE )
                    {
                    match(input,17,FOLLOW_17_in_ruleRelationType2762); 

                            createLeafNode(grammarAccess.getRelationTypeAccess().getOverridesKeyword_2_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1275:1: ( ruleNAME_REFERENCE )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1278:3: ruleNAME_REFERENCE
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                     
                    	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getOverrideRelationTypeCrossReference_2_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType2789);
                    ruleNAME_REFERENCE();
                    _fsp--;

                     
                    	        currentNode = currentNode.getParent();
                    	    

                    }


                    }
                    break;

            }

            match(input,18,FOLLOW_18_in_ruleRelationType2803); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_3(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1297:1: (lv_typeGuid_5= RULE_STRING )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==RULE_STRING) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1299:6: lv_typeGuid_5= RULE_STRING
                    {
                    lv_typeGuid_5=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType2825); 

                    		createLeafNode(grammarAccess.getRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0(), "typeGuid"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "typeGuid", lv_typeGuid_5, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }
                    break;

            }

            match(input,49,FOLLOW_49_in_ruleRelationType2843); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideANameKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1321:1: (lv_sideAName_7= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1323:6: lv_sideAName_7= RULE_STRING
            {
            lv_sideAName_7=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType2865); 

            		createLeafNode(grammarAccess.getRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_6_0(), "sideAName"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "sideAName", lv_sideAName_7, "STRING", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,50,FOLLOW_50_in_ruleRelationType2882); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_7(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1345:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1348:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeCrossReference_8_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType2909);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,51,FOLLOW_51_in_ruleRelationType2921); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_9(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1367:1: (lv_sideBName_11= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1369:6: lv_sideBName_11= RULE_STRING
            {
            lv_sideBName_11=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType2943); 

            		createLeafNode(grammarAccess.getRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_10_0(), "sideBName"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "sideBName", lv_sideBName_11, "STRING", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,52,FOLLOW_52_in_ruleRelationType2960); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_11(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1391:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1394:3: ruleNAME_REFERENCE
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeCrossReference_12_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType2987);
            ruleNAME_REFERENCE();
            _fsp--;

             
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,53,FOLLOW_53_in_ruleRelationType2999); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_13(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1413:1: (lv_defaultOrderType_15= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1415:6: lv_defaultOrderType_15= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1415:29: ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID )
            int alt29=4;
            switch ( input.LA(1) ) {
            case 54:
                {
                alt29=1;
                }
                break;
            case 55:
                {
                alt29=2;
                }
                break;
            case 56:
                {
                alt29=3;
                }
                break;
            case RULE_ID:
                {
                alt29=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1415:29: ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID )", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1415:30: 'Lexicographical_Ascending'
                    {
                    match(input,54,FOLLOW_54_in_ruleRelationType3021); 

                            createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_AscendingKeyword_14_0_0(), "defaultOrderType"); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1421:6: 'Lexicographical_Descending'
                    {
                    match(input,55,FOLLOW_55_in_ruleRelationType3037); 

                            createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_DescendingKeyword_14_0_1(), "defaultOrderType"); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1427:6: 'Unordered'
                    {
                    match(input,56,FOLLOW_56_in_ruleRelationType3053); 

                            createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeUnorderedKeyword_14_0_2(), "defaultOrderType"); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1433:7: RULE_ID
                    {
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationType3070); 

                    		createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeIDTerminalRuleCall_14_0_3(), "defaultOrderType"); 
                    	

                    }
                    break;

            }


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "defaultOrderType", /* lv_defaultOrderType_15 */ input.LT(-1), null, lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,57,FOLLOW_57_in_ruleRelationType3089); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_15(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1456:1: (lv_multiplicity_17= ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1459:6: lv_multiplicity_17= ruleRelationMultiplicityEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_ruleRelationType3123);
            lv_multiplicity_17=ruleRelationMultiplicityEnum();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "multiplicity", lv_multiplicity_17, "RelationMultiplicityEnum", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,19,FOLLOW_19_in_ruleRelationType3136); 

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


    // $ANTLR start ruleRelationMultiplicityEnum
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1488:1: ruleRelationMultiplicityEnum returns [Enumerator current=null] : ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) ) ;
    public final Enumerator ruleRelationMultiplicityEnum() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1492:6: ( ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1493:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1493:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )
            int alt30=4;
            switch ( input.LA(1) ) {
            case 58:
                {
                alt30=1;
                }
                break;
            case 59:
                {
                alt30=2;
                }
                break;
            case 60:
                {
                alt30=3;
                }
                break;
            case 61:
                {
                alt30=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1493:1: ( ( 'ONE_TO_ONE' ) | ( 'ONE_TO_MANY' ) | ( 'MANY_TO_ONE' ) | ( 'MANY_TO_MANY' ) )", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1493:2: ( 'ONE_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1493:2: ( 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1493:4: 'ONE_TO_ONE'
                    {
                    match(input,58,FOLLOW_58_in_ruleRelationMultiplicityEnum3183); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1499:6: ( 'ONE_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1499:6: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1499:8: 'ONE_TO_MANY'
                    {
                    match(input,59,FOLLOW_59_in_ruleRelationMultiplicityEnum3198); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1505:6: ( 'MANY_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1505:6: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1505:8: 'MANY_TO_ONE'
                    {
                    match(input,60,FOLLOW_60_in_ruleRelationMultiplicityEnum3213); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2(), null); 
                        

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1511:6: ( 'MANY_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1511:6: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1511:8: 'MANY_TO_MANY'
                    {
                    match(input,61,FOLLOW_61_in_ruleRelationMultiplicityEnum3228); 

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
    public static final BitSet FOLLOW_ruleImport_in_ruleOseeTypeModel142 = new BitSet(new long[]{0x000140000080D012L});
    public static final BitSet FOLLOW_ruleOseeType_in_ruleOseeTypeModel181 = new BitSet(new long[]{0x000140000080C012L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport219 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_ruleImport263 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleImport285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE327 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNAME_REFERENCE338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME420 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME471 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_13_in_ruleQUALIFIED_NAME490 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME505 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType550 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_ruleOseeType607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_ruleOseeType634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_ruleOseeType661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_ruleOseeType688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_entryRuleArtifactType720 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactType730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_ruleArtifactType776 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_ruleArtifactType799 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType833 = new BitSet(new long[]{0x0000000000070000L});
    public static final BitSet FOLLOW_16_in_ruleArtifactType847 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleArtifactSuperTypes_in_ruleArtifactType881 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_17_in_ruleArtifactType897 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactType924 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleArtifactType938 = new BitSet(new long[]{0x0000000000280010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleArtifactType960 = new BitSet(new long[]{0x0000000000280000L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_ruleArtifactType1003 = new BitSet(new long[]{0x0000000000280000L});
    public static final BitSet FOLLOW_19_in_ruleArtifactType1017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactSuperTypes_in_entryRuleArtifactSuperTypes1050 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactSuperTypes1060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactSuperTypes1112 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_20_in_ruleArtifactSuperTypes1125 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleArtifactSuperTypes1152 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef1190 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRef1200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleAttributeTypeRef1234 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeTypeRef1261 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_22_in_ruleAttributeTypeRef1274 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeTypeRef1296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_entryRuleAttributeType1339 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeType1349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1396 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_ruleAttributeType1414 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1448 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleAttributeType1462 = new BitSet(new long[]{0x00003FE000000020L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_ruleAttributeType1496 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_17_in_ruleAttributeType1511 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1538 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleAttributeType1552 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_ruleAttributeType1561 = new BitSet(new long[]{0x000000000E000020L});
    public static final BitSet FOLLOW_25_in_ruleAttributeType1583 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_26_in_ruleAttributeType1599 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_27_in_ruleAttributeType1615 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1635 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_ruleAttributeType1653 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1675 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_ruleAttributeType1692 = new BitSet(new long[]{0x0000000040000040L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleAttributeType1715 = new BitSet(new long[]{0x0000001E80080000L});
    public static final BitSet FOLLOW_30_in_ruleAttributeType1726 = new BitSet(new long[]{0x0000001E80080000L});
    public static final BitSet FOLLOW_31_in_ruleAttributeType1751 = new BitSet(new long[]{0x0000000100000020L});
    public static final BitSet FOLLOW_32_in_ruleAttributeType1773 = new BitSet(new long[]{0x0000001E00080000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1793 = new BitSet(new long[]{0x0000001E00080000L});
    public static final BitSet FOLLOW_33_in_ruleAttributeType1814 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleAttributeType1841 = new BitSet(new long[]{0x0000001C00080000L});
    public static final BitSet FOLLOW_34_in_ruleAttributeType1856 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1878 = new BitSet(new long[]{0x0000001800080000L});
    public static final BitSet FOLLOW_35_in_ruleAttributeType1898 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1920 = new BitSet(new long[]{0x0000001000080000L});
    public static final BitSet FOLLOW_36_in_ruleAttributeType1940 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1962 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleAttributeType1981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType2015 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType2026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_ruleAttributeBaseType2064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_ruleAttributeBaseType2083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleAttributeBaseType2102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_ruleAttributeBaseType2121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_ruleAttributeBaseType2140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_ruleAttributeBaseType2159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleAttributeBaseType2178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleAttributeBaseType2197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleAttributeBaseType2216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType2287 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumType2297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleOseeEnumType2331 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumType2365 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_17_in_ruleOseeEnumType2379 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleOseeEnumType2406 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleOseeEnumType2420 = new BitSet(new long[]{0x0000800000080010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleOseeEnumType2442 = new BitSet(new long[]{0x0000800000080000L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_ruleOseeEnumType2485 = new BitSet(new long[]{0x0000800000080000L});
    public static final BitSet FOLLOW_19_in_ruleOseeEnumType2499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry2532 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumEntry2542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleOseeEnumEntry2576 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleOseeEnumEntry2598 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_ruleOseeEnumEntry2628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_entryRuleRelationType2670 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationType2680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_ruleRelationType2714 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType2748 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_17_in_ruleRelationType2762 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType2789 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleRelationType2803 = new BitSet(new long[]{0x0002000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType2825 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_ruleRelationType2843 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType2865 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_ruleRelationType2882 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType2909 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_ruleRelationType2921 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType2943 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_ruleRelationType2960 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_ruleRelationType2987 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_53_in_ruleRelationType2999 = new BitSet(new long[]{0x01C0000000000020L});
    public static final BitSet FOLLOW_54_in_ruleRelationType3021 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_55_in_ruleRelationType3037 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_56_in_ruleRelationType3053 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationType3070 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_ruleRelationType3089 = new BitSet(new long[]{0x3C00000000000000L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_ruleRelationType3123 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleRelationType3136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_ruleRelationMultiplicityEnum3183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_ruleRelationMultiplicityEnum3198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_ruleRelationMultiplicityEnum3213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_ruleRelationMultiplicityEnum3228 = new BitSet(new long[]{0x0000000000000002L});

}