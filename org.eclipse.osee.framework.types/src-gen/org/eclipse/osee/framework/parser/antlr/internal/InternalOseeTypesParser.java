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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'import'", "'.'", "'0'", "'1'", "'2'", "'3'", "'4'", "'5'", "'6'", "'7'", "'8'", "'9'", "'abstract'", "'artifactType'", "'extends'", "'{'", "'}'", "'attribute'", "'attributeType'", "'dataProvider'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'MappedAttributeDataProvider'", "'min'", "'max'", "'unlimited'", "'taggerId'", "'DefaultAttributeTaggerProvider'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'WordAttribute'", "'oseeEnumType'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'multiplicity'", "'ONE_TO_MANY'", "'MANY_TO_MANY'", "'MANY_TO_ONE'"
    };
    public static final int RULE_ID=5;
    public static final int RULE_STRING=4;
    public static final int RULE_ANY_OTHER=10;
    public static final int RULE_INT=6;
    public static final int RULE_WS=9;
    public static final int RULE_SL_COMMENT=8;
    public static final int EOF=-1;
    public static final int RULE_ML_COMMENT=7;

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

                if ( (LA1_0==11) ) {
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

                if ( ((LA2_0>=23 && LA2_0<=24)||LA2_0==29||(LA2_0>=52 && LA2_0<=53)) ) {
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
            match(input,11,FOLLOW_11_in_ruleImport263); 

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


    // $ANTLR start entryRuleQUALIFIED_NAME
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:181:1: entryRuleQUALIFIED_NAME returns [String current=null] : iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF ;
    public final String entryRuleQUALIFIED_NAME() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQUALIFIED_NAME = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:181:55: (iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:182:2: iv_ruleQUALIFIED_NAME= ruleQUALIFIED_NAME EOF
            {
             currentNode = createCompositeNode(grammarAccess.getQUALIFIED_NAMERule(), currentNode); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME327);
            iv_ruleQUALIFIED_NAME=ruleQUALIFIED_NAME();
            _fsp--;

             current =iv_ruleQUALIFIED_NAME.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME338); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:189:1: ruleQUALIFIED_NAME returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQUALIFIED_NAME() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:195:6: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:196:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:196:1: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:196:6: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)input.LT(1);
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME378); 

            		current.merge(this_ID_0);
                
             
                createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:203:1: (kw= '.' this_ID_2= RULE_ID )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==12) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:204:2: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,12,FOLLOW_12_in_ruleQUALIFIED_NAME397); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0(), null); 
            	        
            	    this_ID_2=(Token)input.LT(1);
            	    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME412); 

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


    // $ANTLR start entryRuleDIGITS
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:224:1: entryRuleDIGITS returns [String current=null] : iv_ruleDIGITS= ruleDIGITS EOF ;
    public final String entryRuleDIGITS() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDIGITS = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:224:47: (iv_ruleDIGITS= ruleDIGITS EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:225:2: iv_ruleDIGITS= ruleDIGITS EOF
            {
             currentNode = createCompositeNode(grammarAccess.getDIGITSRule(), currentNode); 
            pushFollow(FOLLOW_ruleDIGITS_in_entryRuleDIGITS458);
            iv_ruleDIGITS=ruleDIGITS();
            _fsp--;

             current =iv_ruleDIGITS.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDIGITS469); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleDIGITS


    // $ANTLR start ruleDIGITS
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:232:1: ruleDIGITS returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= '0' | kw= '1' | kw= '2' | kw= '3' | kw= '4' | kw= '5' | kw= '6' | kw= '7' | kw= '8' | kw= '9' )+ ;
    public final AntlrDatatypeRuleToken ruleDIGITS() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:238:6: ( (kw= '0' | kw= '1' | kw= '2' | kw= '3' | kw= '4' | kw= '5' | kw= '6' | kw= '7' | kw= '8' | kw= '9' )+ )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:239:1: (kw= '0' | kw= '1' | kw= '2' | kw= '3' | kw= '4' | kw= '5' | kw= '6' | kw= '7' | kw= '8' | kw= '9' )+
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:239:1: (kw= '0' | kw= '1' | kw= '2' | kw= '3' | kw= '4' | kw= '5' | kw= '6' | kw= '7' | kw= '8' | kw= '9' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=11;
                switch ( input.LA(1) ) {
                case 13:
                    {
                    alt4=1;
                    }
                    break;
                case 14:
                    {
                    alt4=2;
                    }
                    break;
                case 15:
                    {
                    alt4=3;
                    }
                    break;
                case 16:
                    {
                    alt4=4;
                    }
                    break;
                case 17:
                    {
                    alt4=5;
                    }
                    break;
                case 18:
                    {
                    alt4=6;
                    }
                    break;
                case 19:
                    {
                    alt4=7;
                    }
                    break;
                case 20:
                    {
                    alt4=8;
                    }
                    break;
                case 21:
                    {
                    alt4=9;
                    }
                    break;
                case 22:
                    {
                    alt4=10;
                    }
                    break;

                }

                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:240:2: kw= '0'
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,13,FOLLOW_13_in_ruleDIGITS507); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getDIGITSAccess().getDigitZeroKeyword_0(), null); 
            	        

            	    }
            	    break;
            	case 2 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:247:2: kw= '1'
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,14,FOLLOW_14_in_ruleDIGITS526); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getDIGITSAccess().getDigitOneKeyword_1(), null); 
            	        

            	    }
            	    break;
            	case 3 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:254:2: kw= '2'
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,15,FOLLOW_15_in_ruleDIGITS545); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getDIGITSAccess().getDigitTwoKeyword_2(), null); 
            	        

            	    }
            	    break;
            	case 4 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:261:2: kw= '3'
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,16,FOLLOW_16_in_ruleDIGITS564); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getDIGITSAccess().getDigitThreeKeyword_3(), null); 
            	        

            	    }
            	    break;
            	case 5 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:268:2: kw= '4'
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,17,FOLLOW_17_in_ruleDIGITS583); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getDIGITSAccess().getDigitFourKeyword_4(), null); 
            	        

            	    }
            	    break;
            	case 6 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:275:2: kw= '5'
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,18,FOLLOW_18_in_ruleDIGITS602); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getDIGITSAccess().getDigitFiveKeyword_5(), null); 
            	        

            	    }
            	    break;
            	case 7 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:282:2: kw= '6'
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,19,FOLLOW_19_in_ruleDIGITS621); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getDIGITSAccess().getDigitSixKeyword_6(), null); 
            	        

            	    }
            	    break;
            	case 8 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:289:2: kw= '7'
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,20,FOLLOW_20_in_ruleDIGITS640); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getDIGITSAccess().getDigitSevenKeyword_7(), null); 
            	        

            	    }
            	    break;
            	case 9 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:296:2: kw= '8'
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,21,FOLLOW_21_in_ruleDIGITS659); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getDIGITSAccess().getDigitEightKeyword_8(), null); 
            	        

            	    }
            	    break;
            	case 10 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:303:2: kw= '9'
            	    {
            	    kw=(Token)input.LT(1);
            	    match(input,22,FOLLOW_22_in_ruleDIGITS678); 

            	            current.merge(kw);
            	            createLeafNode(grammarAccess.getDIGITSAccess().getDigitNineKeyword_9(), null); 
            	        

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


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
    // $ANTLR end ruleDIGITS


    // $ANTLR start entryRuleOseeType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:316:1: entryRuleOseeType returns [EObject current=null] : iv_ruleOseeType= ruleOseeType EOF ;
    public final EObject entryRuleOseeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:316:50: (iv_ruleOseeType= ruleOseeType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:317:2: iv_ruleOseeType= ruleOseeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType717);
            iv_ruleOseeType=ruleOseeType();
            _fsp--;

             current =iv_ruleOseeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType727); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:324:1: ruleOseeType returns [EObject current=null] : (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType ) ;
    public final EObject ruleOseeType() throws RecognitionException {
        EObject current = null;

        EObject this_ArtifactType_0 = null;

        EObject this_RelationType_1 = null;

        EObject this_AttributeType_2 = null;

        EObject this_OseeEnumType_3 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:329:6: ( (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:330:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:330:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )
            int alt5=4;
            switch ( input.LA(1) ) {
            case 23:
            case 24:
                {
                alt5=1;
                }
                break;
            case 53:
                {
                alt5=2;
                }
                break;
            case 29:
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
                    new NoViableAltException("330:1: (this_ArtifactType_0= ruleArtifactType | this_RelationType_1= ruleRelationType | this_AttributeType_2= ruleAttributeType | this_OseeEnumType_3= ruleOseeEnumType )", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:331:5: this_ArtifactType_0= ruleArtifactType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleArtifactType_in_ruleOseeType774);
                    this_ArtifactType_0=ruleArtifactType();
                    _fsp--;

                     
                            current = this_ArtifactType_0; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:341:5: this_RelationType_1= ruleRelationType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleRelationType_in_ruleOseeType801);
                    this_RelationType_1=ruleRelationType();
                    _fsp--;

                     
                            current = this_RelationType_1; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:351:5: this_AttributeType_2= ruleAttributeType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleAttributeType_in_ruleOseeType828);
                    this_AttributeType_2=ruleAttributeType();
                    _fsp--;

                     
                            current = this_AttributeType_2; 
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:361:5: this_OseeEnumType_3= ruleOseeEnumType
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getOseeTypeAccess().getOseeEnumTypeParserRuleCall_3(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleOseeEnumType_in_ruleOseeType855);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:376:1: entryRuleArtifactType returns [EObject current=null] : iv_ruleArtifactType= ruleArtifactType EOF ;
    public final EObject entryRuleArtifactType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArtifactType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:376:54: (iv_ruleArtifactType= ruleArtifactType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:377:2: iv_ruleArtifactType= ruleArtifactType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getArtifactTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleArtifactType_in_entryRuleArtifactType887);
            iv_ruleArtifactType=ruleArtifactType();
            _fsp--;

             current =iv_ruleArtifactType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactType897); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:384:1: ruleArtifactType returns [EObject current=null] : ( ( 'abstract' )? 'artifactType' (lv_name_2= ruleQUALIFIED_NAME ) ( 'extends' ( RULE_ID ) )? '{' (lv_validAttributeTypes_6= ruleAttributeTypeRef )* '}' ) ;
    public final EObject ruleArtifactType() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_name_2 = null;

        EObject lv_validAttributeTypes_6 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:389:6: ( ( ( 'abstract' )? 'artifactType' (lv_name_2= ruleQUALIFIED_NAME ) ( 'extends' ( RULE_ID ) )? '{' (lv_validAttributeTypes_6= ruleAttributeTypeRef )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:390:1: ( ( 'abstract' )? 'artifactType' (lv_name_2= ruleQUALIFIED_NAME ) ( 'extends' ( RULE_ID ) )? '{' (lv_validAttributeTypes_6= ruleAttributeTypeRef )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:390:1: ( ( 'abstract' )? 'artifactType' (lv_name_2= ruleQUALIFIED_NAME ) ( 'extends' ( RULE_ID ) )? '{' (lv_validAttributeTypes_6= ruleAttributeTypeRef )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:390:2: ( 'abstract' )? 'artifactType' (lv_name_2= ruleQUALIFIED_NAME ) ( 'extends' ( RULE_ID ) )? '{' (lv_validAttributeTypes_6= ruleAttributeTypeRef )* '}'
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:390:2: ( 'abstract' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==23) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:390:3: 'abstract'
                    {
                    match(input,23,FOLLOW_23_in_ruleArtifactType932); 

                            createLeafNode(grammarAccess.getArtifactTypeAccess().getAbstractKeyword_0(), null); 
                        

                    }
                    break;

            }

            match(input,24,FOLLOW_24_in_ruleArtifactType943); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:398:1: (lv_name_2= ruleQUALIFIED_NAME )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:401:6: lv_name_2= ruleQUALIFIED_NAME
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_2_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleArtifactType977);
            lv_name_2=ruleQUALIFIED_NAME();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_2, "QUALIFIED_NAME", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:419:2: ( 'extends' ( RULE_ID ) )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==25) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:419:3: 'extends' ( RULE_ID )
                    {
                    match(input,25,FOLLOW_25_in_ruleArtifactType991); 

                            createLeafNode(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:423:1: ( RULE_ID )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:426:3: RULE_ID
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleArtifactType1013); 

                    		createLeafNode(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypeArtifactTypeCrossReference_3_1_0(), "superArtifactType"); 
                    	

                    }


                    }
                    break;

            }

            match(input,26,FOLLOW_26_in_ruleArtifactType1027); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_4(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:443:1: (lv_validAttributeTypes_6= ruleAttributeTypeRef )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==28) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:446:6: lv_validAttributeTypes_6= ruleAttributeTypeRef
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAttributeTypeRefParserRuleCall_5_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleAttributeTypeRef_in_ruleArtifactType1061);
            	    lv_validAttributeTypes_6=ruleAttributeTypeRef();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getArtifactTypeRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "validAttributeTypes", lv_validAttributeTypes_6, "AttributeTypeRef", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            match(input,27,FOLLOW_27_in_ruleArtifactType1075); 

                    createLeafNode(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_6(), null); 
                

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:475:1: entryRuleAttributeTypeRef returns [EObject current=null] : iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF ;
    public final EObject entryRuleAttributeTypeRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeTypeRef = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:475:58: (iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:476:2: iv_ruleAttributeTypeRef= ruleAttributeTypeRef EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeTypeRefRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef1108);
            iv_ruleAttributeTypeRef=ruleAttributeTypeRef();
            _fsp--;

             current =iv_ruleAttributeTypeRef; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRef1118); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:483:1: ruleAttributeTypeRef returns [EObject current=null] : ( 'attribute' ( RULE_ID ) ) ;
    public final EObject ruleAttributeTypeRef() throws RecognitionException {
        EObject current = null;

         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:488:6: ( ( 'attribute' ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:489:1: ( 'attribute' ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:489:1: ( 'attribute' ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:489:2: 'attribute' ( RULE_ID )
            {
            match(input,28,FOLLOW_28_in_ruleAttributeTypeRef1152); 

                    createLeafNode(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:493:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:496:3: RULE_ID
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRefRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleAttributeTypeRef1174); 

            		createLeafNode(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeCrossReference_1_0(), "validAttributeType"); 
            	

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:516:1: entryRuleAttributeType returns [EObject current=null] : iv_ruleAttributeType= ruleAttributeType EOF ;
    public final EObject entryRuleAttributeType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:516:55: (iv_ruleAttributeType= ruleAttributeType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:517:2: iv_ruleAttributeType= ruleAttributeType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeType_in_entryRuleAttributeType1210);
            iv_ruleAttributeType=ruleAttributeType();
            _fsp--;

             current =iv_ruleAttributeType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeType1220); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:524:1: ruleAttributeType returns [EObject current=null] : ( 'attributeType' (lv_name_1= ruleQUALIFIED_NAME ) ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) ) '{' 'dataProvider' (lv_dataProvider_6= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_8= ruleDIGITS ) 'max' (lv_max_10= ( ruleDIGITS | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_12= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( RULE_ID ) )? ( 'description' (lv_description_16= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_18= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_20= RULE_STRING ) )? '}' ) ;
    public final EObject ruleAttributeType() throws RecognitionException {
        EObject current = null;

        Token lv_dataProvider_6=null;
        Token lv_max_10=null;
        Token lv_taggerId_12=null;
        Token lv_description_16=null;
        Token lv_defaultValue_18=null;
        Token lv_fileExtension_20=null;
        AntlrDatatypeRuleToken lv_name_1 = null;

        AntlrDatatypeRuleToken lv_baseAttributeType_3 = null;

        AntlrDatatypeRuleToken lv_min_8 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:529:6: ( ( 'attributeType' (lv_name_1= ruleQUALIFIED_NAME ) ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) ) '{' 'dataProvider' (lv_dataProvider_6= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_8= ruleDIGITS ) 'max' (lv_max_10= ( ruleDIGITS | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_12= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( RULE_ID ) )? ( 'description' (lv_description_16= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_18= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_20= RULE_STRING ) )? '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:530:1: ( 'attributeType' (lv_name_1= ruleQUALIFIED_NAME ) ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) ) '{' 'dataProvider' (lv_dataProvider_6= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_8= ruleDIGITS ) 'max' (lv_max_10= ( ruleDIGITS | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_12= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( RULE_ID ) )? ( 'description' (lv_description_16= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_18= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_20= RULE_STRING ) )? '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:530:1: ( 'attributeType' (lv_name_1= ruleQUALIFIED_NAME ) ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) ) '{' 'dataProvider' (lv_dataProvider_6= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_8= ruleDIGITS ) 'max' (lv_max_10= ( ruleDIGITS | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_12= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( RULE_ID ) )? ( 'description' (lv_description_16= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_18= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_20= RULE_STRING ) )? '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:530:2: 'attributeType' (lv_name_1= ruleQUALIFIED_NAME ) ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) ) '{' 'dataProvider' (lv_dataProvider_6= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) ) 'min' (lv_min_8= ruleDIGITS ) 'max' (lv_max_10= ( ruleDIGITS | 'unlimited' ) ) ( 'taggerId' (lv_taggerId_12= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )? ( 'enumType' ( RULE_ID ) )? ( 'description' (lv_description_16= RULE_STRING ) )? ( 'defaultValue' (lv_defaultValue_18= RULE_STRING ) )? ( 'fileExtension' (lv_fileExtension_20= RULE_STRING ) )? '}'
            {
            match(input,29,FOLLOW_29_in_ruleAttributeType1254); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:534:1: (lv_name_1= ruleQUALIFIED_NAME )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:537:6: lv_name_1= ruleQUALIFIED_NAME
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1288);
            lv_name_1=ruleQUALIFIED_NAME();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_1, "QUALIFIED_NAME", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:555:2: ( 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:555:3: 'extends' (lv_baseAttributeType_3= ruleAttributeBaseType )
            {
            match(input,25,FOLLOW_25_in_ruleAttributeType1302); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_2_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:559:1: (lv_baseAttributeType_3= ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:562:6: lv_baseAttributeType_3= ruleAttributeBaseType
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleAttributeBaseType_in_ruleAttributeType1336);
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

            match(input,26,FOLLOW_26_in_ruleAttributeType1350); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_3(), null); 
                
            match(input,30,FOLLOW_30_in_ruleAttributeType1359); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_4(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:588:1: (lv_dataProvider_6= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:590:6: lv_dataProvider_6= ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:590:24: ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME )
            int alt9=4;
            switch ( input.LA(1) ) {
            case 31:
                {
                alt9=1;
                }
                break;
            case 32:
                {
                alt9=2;
                }
                break;
            case 33:
                {
                alt9=3;
                }
                break;
            case RULE_ID:
                {
                alt9=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("590:24: ( 'DefaultAttributeDataProvider' | 'UriAttributeDataProvider' | 'MappedAttributeDataProvider' | ruleQUALIFIED_NAME )", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:590:25: 'DefaultAttributeDataProvider'
                    {
                    match(input,31,FOLLOW_31_in_ruleAttributeType1381); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_5_0_0(), "dataProvider"); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:596:6: 'UriAttributeDataProvider'
                    {
                    match(input,32,FOLLOW_32_in_ruleAttributeType1397); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_5_0_1(), "dataProvider"); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:602:6: 'MappedAttributeDataProvider'
                    {
                    match(input,33,FOLLOW_33_in_ruleAttributeType1413); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDataProviderMappedAttributeDataProviderKeyword_5_0_2(), "dataProvider"); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:608:7: ruleQUALIFIED_NAME
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_5_0_3(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1433);
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
            	       		set(current, "dataProvider", /* lv_dataProvider_6 */ input.LT(-1), null, lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,34,FOLLOW_34_in_ruleAttributeType1451); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getMinKeyword_6(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:633:1: (lv_min_8= ruleDIGITS )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:636:6: lv_min_8= ruleDIGITS
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getMinDIGITSParserRuleCall_7_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleDIGITS_in_ruleAttributeType1485);
            lv_min_8=ruleDIGITS();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "min", lv_min_8, "DIGITS", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,35,FOLLOW_35_in_ruleAttributeType1498); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxKeyword_8(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:658:1: (lv_max_10= ( ruleDIGITS | 'unlimited' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:660:6: lv_max_10= ( ruleDIGITS | 'unlimited' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:660:16: ( ruleDIGITS | 'unlimited' )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( ((LA10_0>=13 && LA10_0<=22)) ) {
                alt10=1;
            }
            else if ( (LA10_0==36) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("660:16: ( ruleDIGITS | 'unlimited' )", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:660:18: ruleDIGITS
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getMaxDIGITSParserRuleCall_9_0_0(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleDIGITS_in_ruleAttributeType1524);
                    ruleDIGITS();
                    _fsp--;

                     
                            currentNode = currentNode.getParent();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:667:6: 'unlimited'
                    {
                    match(input,36,FOLLOW_36_in_ruleAttributeType1534); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_9_0_1(), "max"); 
                        

                    }
                    break;

            }


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "max", /* lv_max_10 */ input.LT(-1), null, lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:687:2: ( 'taggerId' (lv_taggerId_12= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==37) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:687:3: 'taggerId' (lv_taggerId_12= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) )
                    {
                    match(input,37,FOLLOW_37_in_ruleAttributeType1559); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_10_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:691:1: (lv_taggerId_12= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME ) )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:693:6: lv_taggerId_12= ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:693:21: ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME )
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==38) ) {
                        alt11=1;
                    }
                    else if ( (LA11_0==RULE_ID) ) {
                        alt11=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("693:21: ( 'DefaultAttributeTaggerProvider' | ruleQUALIFIED_NAME )", 11, 0, input);

                        throw nvae;
                    }
                    switch (alt11) {
                        case 1 :
                            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:693:22: 'DefaultAttributeTaggerProvider'
                            {
                            match(input,38,FOLLOW_38_in_ruleAttributeType1581); 

                                    createLeafNode(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_10_1_0_0(), "taggerId"); 
                                

                            }
                            break;
                        case 2 :
                            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:699:7: ruleQUALIFIED_NAME
                            {
                             
                                    currentNode=createCompositeNode(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_10_1_0_1(), currentNode); 
                                
                            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1601);
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
                    	       		set(current, "taggerId", /* lv_taggerId_12 */ input.LT(-1), null, lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:720:4: ( 'enumType' ( RULE_ID ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==39) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:720:5: 'enumType' ( RULE_ID )
                    {
                    match(input,39,FOLLOW_39_in_ruleAttributeType1622); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_11_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:724:1: ( RULE_ID )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:727:3: RULE_ID
                    {

                    			if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                            
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleAttributeType1644); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_11_1_0(), "enumType"); 
                    	

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:740:4: ( 'description' (lv_description_16= RULE_STRING ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==40) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:740:5: 'description' (lv_description_16= RULE_STRING )
                    {
                    match(input,40,FOLLOW_40_in_ruleAttributeType1659); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_12_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:744:1: (lv_description_16= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:746:6: lv_description_16= RULE_STRING
                    {
                    lv_description_16=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1681); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_12_1_0(), "description"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "description", lv_description_16, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:764:4: ( 'defaultValue' (lv_defaultValue_18= RULE_STRING ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==41) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:764:5: 'defaultValue' (lv_defaultValue_18= RULE_STRING )
                    {
                    match(input,41,FOLLOW_41_in_ruleAttributeType1701); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_13_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:768:1: (lv_defaultValue_18= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:770:6: lv_defaultValue_18= RULE_STRING
                    {
                    lv_defaultValue_18=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1723); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_13_1_0(), "defaultValue"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "defaultValue", lv_defaultValue_18, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:788:4: ( 'fileExtension' (lv_fileExtension_20= RULE_STRING ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==42) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:788:5: 'fileExtension' (lv_fileExtension_20= RULE_STRING )
                    {
                    match(input,42,FOLLOW_42_in_ruleAttributeType1743); 

                            createLeafNode(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_14_0(), null); 
                        
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:792:1: (lv_fileExtension_20= RULE_STRING )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:794:6: lv_fileExtension_20= RULE_STRING
                    {
                    lv_fileExtension_20=(Token)input.LT(1);
                    match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleAttributeType1765); 

                    		createLeafNode(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_14_1_0(), "fileExtension"); 
                    	

                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getAttributeTypeRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode, current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "fileExtension", lv_fileExtension_20, "STRING", lastConsumedNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	    

                    }


                    }
                    break;

            }

            match(input,27,FOLLOW_27_in_ruleAttributeType1784); 

                    createLeafNode(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_15(), null); 
                

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:823:1: entryRuleAttributeBaseType returns [String current=null] : iv_ruleAttributeBaseType= ruleAttributeBaseType EOF ;
    public final String entryRuleAttributeBaseType() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAttributeBaseType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:823:58: (iv_ruleAttributeBaseType= ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:824:2: iv_ruleAttributeBaseType= ruleAttributeBaseType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getAttributeBaseTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1818);
            iv_ruleAttributeBaseType=ruleAttributeBaseType();
            _fsp--;

             current =iv_ruleAttributeBaseType.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType1829); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:831:1: ruleAttributeBaseType returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) ;
    public final AntlrDatatypeRuleToken ruleAttributeBaseType() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        AntlrDatatypeRuleToken this_QUALIFIED_NAME_9 = null;


         setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:837:6: ( (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:838:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:838:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )
            int alt17=10;
            switch ( input.LA(1) ) {
            case 43:
                {
                alt17=1;
                }
                break;
            case 44:
                {
                alt17=2;
                }
                break;
            case 45:
                {
                alt17=3;
                }
                break;
            case 46:
                {
                alt17=4;
                }
                break;
            case 47:
                {
                alt17=5;
                }
                break;
            case 48:
                {
                alt17=6;
                }
                break;
            case 49:
                {
                alt17=7;
                }
                break;
            case 50:
                {
                alt17=8;
                }
                break;
            case 51:
                {
                alt17=9;
                }
                break;
            case RULE_ID:
                {
                alt17=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("838:1: (kw= 'BooleanAttribute' | kw= 'CompressedContentAttribute' | kw= 'DateAttribute' | kw= 'EnumeratedAttribute' | kw= 'FloatingPointAttribute' | kw= 'IntegerAttribute' | kw= 'JavaObjectAttribute' | kw= 'StringAttribute' | kw= 'WordAttribute' | this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME )", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:839:2: kw= 'BooleanAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_ruleAttributeBaseType1867); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0(), null); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:846:2: kw= 'CompressedContentAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_ruleAttributeBaseType1886); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1(), null); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:853:2: kw= 'DateAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,45,FOLLOW_45_in_ruleAttributeBaseType1905); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2(), null); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:860:2: kw= 'EnumeratedAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,46,FOLLOW_46_in_ruleAttributeBaseType1924); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3(), null); 
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:867:2: kw= 'FloatingPointAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,47,FOLLOW_47_in_ruleAttributeBaseType1943); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4(), null); 
                        

                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:874:2: kw= 'IntegerAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,48,FOLLOW_48_in_ruleAttributeBaseType1962); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5(), null); 
                        

                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:881:2: kw= 'JavaObjectAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,49,FOLLOW_49_in_ruleAttributeBaseType1981); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6(), null); 
                        

                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:888:2: kw= 'StringAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,50,FOLLOW_50_in_ruleAttributeBaseType2000); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7(), null); 
                        

                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:895:2: kw= 'WordAttribute'
                    {
                    kw=(Token)input.LT(1);
                    match(input,51,FOLLOW_51_in_ruleAttributeBaseType2019); 

                            current.merge(kw);
                            createLeafNode(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8(), null); 
                        

                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:902:5: this_QUALIFIED_NAME_9= ruleQUALIFIED_NAME
                    {
                     
                            currentNode=createCompositeNode(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9(), currentNode); 
                        
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2047);
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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:920:1: entryRuleOseeEnumType returns [EObject current=null] : iv_ruleOseeEnumType= ruleOseeEnumType EOF ;
    public final EObject entryRuleOseeEnumType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeEnumType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:920:54: (iv_ruleOseeEnumType= ruleOseeEnumType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:921:2: iv_ruleOseeEnumType= ruleOseeEnumType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeEnumTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType2090);
            iv_ruleOseeEnumType=ruleOseeEnumType();
            _fsp--;

             current =iv_ruleOseeEnumType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumType2100); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:928:1: ruleOseeEnumType returns [EObject current=null] : ( 'oseeEnumType' (lv_name_1= ruleQUALIFIED_NAME ) '{' (lv_enums_3= ruleOseeEnum )* '}' ) ;
    public final EObject ruleOseeEnumType() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_name_1 = null;

        EObject lv_enums_3 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:933:6: ( ( 'oseeEnumType' (lv_name_1= ruleQUALIFIED_NAME ) '{' (lv_enums_3= ruleOseeEnum )* '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:934:1: ( 'oseeEnumType' (lv_name_1= ruleQUALIFIED_NAME ) '{' (lv_enums_3= ruleOseeEnum )* '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:934:1: ( 'oseeEnumType' (lv_name_1= ruleQUALIFIED_NAME ) '{' (lv_enums_3= ruleOseeEnum )* '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:934:2: 'oseeEnumType' (lv_name_1= ruleQUALIFIED_NAME ) '{' (lv_enums_3= ruleOseeEnum )* '}'
            {
            match(input,52,FOLLOW_52_in_ruleOseeEnumType2134); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:938:1: (lv_name_1= ruleQUALIFIED_NAME )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:941:6: lv_name_1= ruleQUALIFIED_NAME
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getOseeEnumTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleOseeEnumType2168);
            lv_name_1=ruleQUALIFIED_NAME();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_1, "QUALIFIED_NAME", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,26,FOLLOW_26_in_ruleOseeEnumType2181); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:963:1: (lv_enums_3= ruleOseeEnum )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==RULE_STRING) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:966:6: lv_enums_3= ruleOseeEnum
            	    {
            	     
            	    	        currentNode=createCompositeNode(grammarAccess.getOseeEnumTypeAccess().getEnumsOseeEnumParserRuleCall_3_0(), currentNode); 
            	    	    
            	    pushFollow(FOLLOW_ruleOseeEnum_in_ruleOseeEnumType2215);
            	    lv_enums_3=ruleOseeEnum();
            	    _fsp--;


            	    	        if (current==null) {
            	    	            current = factory.create(grammarAccess.getOseeEnumTypeRule().getType().getClassifier());
            	    	            associateNodeWithAstElement(currentNode.getParent(), current);
            	    	        }
            	    	        
            	    	        try {
            	    	       		add(current, "enums", lv_enums_3, "OseeEnum", currentNode);
            	    	        } catch (ValueConverterException vce) {
            	    				handleValueConverterException(vce);
            	    	        }
            	    	        currentNode = currentNode.getParent();
            	    	    

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            match(input,27,FOLLOW_27_in_ruleOseeEnumType2229); 

                    createLeafNode(grammarAccess.getOseeEnumTypeAccess().getRightCurlyBracketKeyword_4(), null); 
                

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


    // $ANTLR start entryRuleOseeEnum
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:995:1: entryRuleOseeEnum returns [EObject current=null] : iv_ruleOseeEnum= ruleOseeEnum EOF ;
    public final EObject entryRuleOseeEnum() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOseeEnum = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:995:50: (iv_ruleOseeEnum= ruleOseeEnum EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:996:2: iv_ruleOseeEnum= ruleOseeEnum EOF
            {
             currentNode = createCompositeNode(grammarAccess.getOseeEnumRule(), currentNode); 
            pushFollow(FOLLOW_ruleOseeEnum_in_entryRuleOseeEnum2262);
            iv_ruleOseeEnum=ruleOseeEnum();
            _fsp--;

             current =iv_ruleOseeEnum; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnum2272); 

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end entryRuleOseeEnum


    // $ANTLR start ruleOseeEnum
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1003:1: ruleOseeEnum returns [EObject current=null] : ( (lv_name_0= RULE_STRING ) (lv_ordinal_1= ruleDIGITS )? ) ;
    public final EObject ruleOseeEnum() throws RecognitionException {
        EObject current = null;

        Token lv_name_0=null;
        AntlrDatatypeRuleToken lv_ordinal_1 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1008:6: ( ( (lv_name_0= RULE_STRING ) (lv_ordinal_1= ruleDIGITS )? ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1009:1: ( (lv_name_0= RULE_STRING ) (lv_ordinal_1= ruleDIGITS )? )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1009:1: ( (lv_name_0= RULE_STRING ) (lv_ordinal_1= ruleDIGITS )? )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1009:2: (lv_name_0= RULE_STRING ) (lv_ordinal_1= ruleDIGITS )?
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1009:2: (lv_name_0= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1011:6: lv_name_0= RULE_STRING
            {
            lv_name_0=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleOseeEnum2319); 

            		createLeafNode(grammarAccess.getOseeEnumAccess().getNameSTRINGTerminalRuleCall_0_0(), "name"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getOseeEnumRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_0, "STRING", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1029:2: (lv_ordinal_1= ruleDIGITS )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>=13 && LA19_0<=22)) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1032:6: lv_ordinal_1= ruleDIGITS
                    {
                     
                    	        currentNode=createCompositeNode(grammarAccess.getOseeEnumAccess().getOrdinalDIGITSParserRuleCall_1_0(), currentNode); 
                    	    
                    pushFollow(FOLLOW_ruleDIGITS_in_ruleOseeEnum2361);
                    lv_ordinal_1=ruleDIGITS();
                    _fsp--;


                    	        if (current==null) {
                    	            current = factory.create(grammarAccess.getOseeEnumRule().getType().getClassifier());
                    	            associateNodeWithAstElement(currentNode.getParent(), current);
                    	        }
                    	        
                    	        try {
                    	       		set(current, "ordinal", lv_ordinal_1, "DIGITS", currentNode);
                    	        } catch (ValueConverterException vce) {
                    				handleValueConverterException(vce);
                    	        }
                    	        currentNode = currentNode.getParent();
                    	    

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
    // $ANTLR end ruleOseeEnum


    // $ANTLR start entryRuleRelationType
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1057:1: entryRuleRelationType returns [EObject current=null] : iv_ruleRelationType= ruleRelationType EOF ;
    public final EObject entryRuleRelationType() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationType = null;


        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1057:54: (iv_ruleRelationType= ruleRelationType EOF )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1058:2: iv_ruleRelationType= ruleRelationType EOF
            {
             currentNode = createCompositeNode(grammarAccess.getRelationTypeRule(), currentNode); 
            pushFollow(FOLLOW_ruleRelationType_in_entryRuleRelationType2399);
            iv_ruleRelationType=ruleRelationType();
            _fsp--;

             current =iv_ruleRelationType; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationType2409); 

            }

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1065:1: ruleRelationType returns [EObject current=null] : ( 'relationType' (lv_name_1= ruleQUALIFIED_NAME ) '{' 'sideAName' (lv_sideAName_4= RULE_STRING ) 'sideAArtifactType' ( RULE_ID ) 'sideBName' (lv_sideBName_8= RULE_STRING ) 'sideBArtifactType' ( RULE_ID ) 'defaultOrderType' (lv_defaultOrderType_12= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) ) 'multiplicity' (lv_multiplicity_14= ruleRelationMultiplicityEnum ) '}' ) ;
    public final EObject ruleRelationType() throws RecognitionException {
        EObject current = null;

        Token lv_sideAName_4=null;
        Token lv_sideBName_8=null;
        Token lv_defaultOrderType_12=null;
        AntlrDatatypeRuleToken lv_name_1 = null;

        Enumerator lv_multiplicity_14 = null;


         EObject temp=null; setCurrentLookahead(); resetLookahead(); 
            
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1070:6: ( ( 'relationType' (lv_name_1= ruleQUALIFIED_NAME ) '{' 'sideAName' (lv_sideAName_4= RULE_STRING ) 'sideAArtifactType' ( RULE_ID ) 'sideBName' (lv_sideBName_8= RULE_STRING ) 'sideBArtifactType' ( RULE_ID ) 'defaultOrderType' (lv_defaultOrderType_12= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) ) 'multiplicity' (lv_multiplicity_14= ruleRelationMultiplicityEnum ) '}' ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1071:1: ( 'relationType' (lv_name_1= ruleQUALIFIED_NAME ) '{' 'sideAName' (lv_sideAName_4= RULE_STRING ) 'sideAArtifactType' ( RULE_ID ) 'sideBName' (lv_sideBName_8= RULE_STRING ) 'sideBArtifactType' ( RULE_ID ) 'defaultOrderType' (lv_defaultOrderType_12= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) ) 'multiplicity' (lv_multiplicity_14= ruleRelationMultiplicityEnum ) '}' )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1071:1: ( 'relationType' (lv_name_1= ruleQUALIFIED_NAME ) '{' 'sideAName' (lv_sideAName_4= RULE_STRING ) 'sideAArtifactType' ( RULE_ID ) 'sideBName' (lv_sideBName_8= RULE_STRING ) 'sideBArtifactType' ( RULE_ID ) 'defaultOrderType' (lv_defaultOrderType_12= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) ) 'multiplicity' (lv_multiplicity_14= ruleRelationMultiplicityEnum ) '}' )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1071:2: 'relationType' (lv_name_1= ruleQUALIFIED_NAME ) '{' 'sideAName' (lv_sideAName_4= RULE_STRING ) 'sideAArtifactType' ( RULE_ID ) 'sideBName' (lv_sideBName_8= RULE_STRING ) 'sideBArtifactType' ( RULE_ID ) 'defaultOrderType' (lv_defaultOrderType_12= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) ) 'multiplicity' (lv_multiplicity_14= ruleRelationMultiplicityEnum ) '}'
            {
            match(input,53,FOLLOW_53_in_ruleRelationType2443); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1075:1: (lv_name_1= ruleQUALIFIED_NAME )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1078:6: lv_name_1= ruleQUALIFIED_NAME
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_1_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_ruleRelationType2477);
            lv_name_1=ruleQUALIFIED_NAME();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "name", lv_name_1, "QUALIFIED_NAME", currentNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,26,FOLLOW_26_in_ruleRelationType2490); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_2(), null); 
                
            match(input,54,FOLLOW_54_in_ruleRelationType2499); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideANameKeyword_3(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1104:1: (lv_sideAName_4= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1106:6: lv_sideAName_4= RULE_STRING
            {
            lv_sideAName_4=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType2521); 

            		createLeafNode(grammarAccess.getRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_4_0(), "sideAName"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "sideAName", lv_sideAName_4, "STRING", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,55,FOLLOW_55_in_ruleRelationType2538); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_5(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1128:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1131:3: RULE_ID
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationType2560); 

            		createLeafNode(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeCrossReference_6_0(), "sideAArtifactType"); 
            	

            }

            match(input,56,FOLLOW_56_in_ruleRelationType2572); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_7(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1148:1: (lv_sideBName_8= RULE_STRING )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1150:6: lv_sideBName_8= RULE_STRING
            {
            lv_sideBName_8=(Token)input.LT(1);
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleRelationType2594); 

            		createLeafNode(grammarAccess.getRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_8_0(), "sideBName"); 
            	

            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "sideBName", lv_sideBName_8, "STRING", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,57,FOLLOW_57_in_ruleRelationType2611); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_9(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1172:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1175:3: RULE_ID
            {

            			if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
                    
            match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationType2633); 

            		createLeafNode(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeCrossReference_10_0(), "sideBArtifactType"); 
            	

            }

            match(input,58,FOLLOW_58_in_ruleRelationType2645); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_11(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1192:1: (lv_defaultOrderType_12= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1194:6: lv_defaultOrderType_12= ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1194:29: ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID )
            int alt20=4;
            switch ( input.LA(1) ) {
            case 59:
                {
                alt20=1;
                }
                break;
            case 60:
                {
                alt20=2;
                }
                break;
            case 61:
                {
                alt20=3;
                }
                break;
            case RULE_ID:
                {
                alt20=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1194:29: ( 'Lexicographical_Ascending' | 'Lexicographical_Descending' | 'Unordered' | RULE_ID )", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1194:30: 'Lexicographical_Ascending'
                    {
                    match(input,59,FOLLOW_59_in_ruleRelationType2667); 

                            createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_AscendingKeyword_12_0_0(), "defaultOrderType"); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1200:6: 'Lexicographical_Descending'
                    {
                    match(input,60,FOLLOW_60_in_ruleRelationType2683); 

                            createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_DescendingKeyword_12_0_1(), "defaultOrderType"); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1206:6: 'Unordered'
                    {
                    match(input,61,FOLLOW_61_in_ruleRelationType2699); 

                            createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeUnorderedKeyword_12_0_2(), "defaultOrderType"); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1212:7: RULE_ID
                    {
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleRelationType2716); 

                    		createLeafNode(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeIDTerminalRuleCall_12_0_3(), "defaultOrderType"); 
                    	

                    }
                    break;

            }


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode, current);
            	        }
            	        
            	        try {
            	       		set(current, "defaultOrderType", /* lv_defaultOrderType_12 */ input.LT(-1), null, lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	    

            }

            match(input,62,FOLLOW_62_in_ruleRelationType2735); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_13(), null); 
                
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1235:1: (lv_multiplicity_14= ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1238:6: lv_multiplicity_14= ruleRelationMultiplicityEnum
            {
             
            	        currentNode=createCompositeNode(grammarAccess.getRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_14_0(), currentNode); 
            	    
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_ruleRelationType2769);
            lv_multiplicity_14=ruleRelationMultiplicityEnum();
            _fsp--;


            	        if (current==null) {
            	            current = factory.create(grammarAccess.getRelationTypeRule().getType().getClassifier());
            	            associateNodeWithAstElement(currentNode.getParent(), current);
            	        }
            	        
            	        try {
            	       		set(current, "multiplicity", lv_multiplicity_14, "RelationMultiplicityEnum", lastConsumedNode);
            	        } catch (ValueConverterException vce) {
            				handleValueConverterException(vce);
            	        }
            	        currentNode = currentNode.getParent();
            	    

            }

            match(input,27,FOLLOW_27_in_ruleRelationType2782); 

                    createLeafNode(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_15(), null); 
                

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
    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1267:1: ruleRelationMultiplicityEnum returns [Enumerator current=null] : ( ( 'ONE_TO_MANY' ) | ( 'MANY_TO_MANY' ) | ( 'MANY_TO_ONE' ) ) ;
    public final Enumerator ruleRelationMultiplicityEnum() throws RecognitionException {
        Enumerator current = null;

         setCurrentLookahead(); resetLookahead(); 
        try {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1271:6: ( ( ( 'ONE_TO_MANY' ) | ( 'MANY_TO_MANY' ) | ( 'MANY_TO_ONE' ) ) )
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1272:1: ( ( 'ONE_TO_MANY' ) | ( 'MANY_TO_MANY' ) | ( 'MANY_TO_ONE' ) )
            {
            // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1272:1: ( ( 'ONE_TO_MANY' ) | ( 'MANY_TO_MANY' ) | ( 'MANY_TO_ONE' ) )
            int alt21=3;
            switch ( input.LA(1) ) {
            case 63:
                {
                alt21=1;
                }
                break;
            case 64:
                {
                alt21=2;
                }
                break;
            case 65:
                {
                alt21=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1272:1: ( ( 'ONE_TO_MANY' ) | ( 'MANY_TO_MANY' ) | ( 'MANY_TO_ONE' ) )", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1272:2: ( 'ONE_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1272:2: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1272:4: 'ONE_TO_MANY'
                    {
                    match(input,63,FOLLOW_63_in_ruleRelationMultiplicityEnum2829); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_0(), null); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1278:6: ( 'MANY_TO_MANY' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1278:6: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1278:8: 'MANY_TO_MANY'
                    {
                    match(input,64,FOLLOW_64_in_ruleRelationMultiplicityEnum2844); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_1(), null); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1284:6: ( 'MANY_TO_ONE' )
                    {
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1284:6: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g:1284:8: 'MANY_TO_ONE'
                    {
                    match(input,65,FOLLOW_65_in_ruleRelationMultiplicityEnum2859); 

                            current = grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            createLeafNode(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2(), null); 
                        

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
    public static final BitSet FOLLOW_ruleImport_in_ruleOseeTypeModel142 = new BitSet(new long[]{0x0030000021800802L});
    public static final BitSet FOLLOW_ruleOseeType_in_ruleOseeTypeModel181 = new BitSet(new long[]{0x0030000021800002L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport219 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_ruleImport263 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleImport285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME327 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME378 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_12_in_ruleQUALIFIED_NAME397 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleQUALIFIED_NAME412 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_ruleDIGITS_in_entryRuleDIGITS458 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDIGITS469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_ruleDIGITS507 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_14_in_ruleDIGITS526 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_15_in_ruleDIGITS545 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_16_in_ruleDIGITS564 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_17_in_ruleDIGITS583 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_18_in_ruleDIGITS602 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_19_in_ruleDIGITS621 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_20_in_ruleDIGITS640 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_21_in_ruleDIGITS659 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_22_in_ruleDIGITS678 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType717 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_ruleOseeType774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_ruleOseeType801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_ruleOseeType828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_ruleOseeType855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_entryRuleArtifactType887 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactType897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleArtifactType932 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_ruleArtifactType943 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleArtifactType977 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_25_in_ruleArtifactType991 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleArtifactType1013 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_ruleArtifactType1027 = new BitSet(new long[]{0x0000000018000000L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_ruleArtifactType1061 = new BitSet(new long[]{0x0000000018000000L});
    public static final BitSet FOLLOW_27_in_ruleArtifactType1075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef1108 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRef1118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_ruleAttributeTypeRef1152 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleAttributeTypeRef1174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_entryRuleAttributeType1210 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeType1220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_ruleAttributeType1254 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1288 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_ruleAttributeType1302 = new BitSet(new long[]{0x000FF80000000020L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_ruleAttributeType1336 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_ruleAttributeType1350 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_ruleAttributeType1359 = new BitSet(new long[]{0x0000000380000020L});
    public static final BitSet FOLLOW_31_in_ruleAttributeType1381 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_32_in_ruleAttributeType1397 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_33_in_ruleAttributeType1413 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1433 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_ruleAttributeType1451 = new BitSet(new long[]{0x00000000007FE000L});
    public static final BitSet FOLLOW_ruleDIGITS_in_ruleAttributeType1485 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_ruleAttributeType1498 = new BitSet(new long[]{0x00000010007FE000L});
    public static final BitSet FOLLOW_ruleDIGITS_in_ruleAttributeType1524 = new BitSet(new long[]{0x000007A008000000L});
    public static final BitSet FOLLOW_36_in_ruleAttributeType1534 = new BitSet(new long[]{0x000007A008000000L});
    public static final BitSet FOLLOW_37_in_ruleAttributeType1559 = new BitSet(new long[]{0x0000004000000020L});
    public static final BitSet FOLLOW_38_in_ruleAttributeType1581 = new BitSet(new long[]{0x0000078008000000L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeType1601 = new BitSet(new long[]{0x0000078008000000L});
    public static final BitSet FOLLOW_39_in_ruleAttributeType1622 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleAttributeType1644 = new BitSet(new long[]{0x0000070008000000L});
    public static final BitSet FOLLOW_40_in_ruleAttributeType1659 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1681 = new BitSet(new long[]{0x0000060008000000L});
    public static final BitSet FOLLOW_41_in_ruleAttributeType1701 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1723 = new BitSet(new long[]{0x0000040008000000L});
    public static final BitSet FOLLOW_42_in_ruleAttributeType1743 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleAttributeType1765 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_ruleAttributeType1784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType1818 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType1829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleAttributeBaseType1867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleAttributeBaseType1886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleAttributeBaseType1905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleAttributeBaseType1924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleAttributeBaseType1943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_ruleAttributeBaseType1962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleAttributeBaseType1981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_ruleAttributeBaseType2000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_ruleAttributeBaseType2019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleAttributeBaseType2047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType2090 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumType2100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_ruleOseeEnumType2134 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleOseeEnumType2168 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_ruleOseeEnumType2181 = new BitSet(new long[]{0x0000000008000010L});
    public static final BitSet FOLLOW_ruleOseeEnum_in_ruleOseeEnumType2215 = new BitSet(new long[]{0x0000000008000010L});
    public static final BitSet FOLLOW_27_in_ruleOseeEnumType2229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnum_in_entryRuleOseeEnum2262 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnum2272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleOseeEnum2319 = new BitSet(new long[]{0x00000000007FE002L});
    public static final BitSet FOLLOW_ruleDIGITS_in_ruleOseeEnum2361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_entryRuleRelationType2399 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationType2409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_ruleRelationType2443 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_ruleRelationType2477 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_ruleRelationType2490 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_ruleRelationType2499 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType2521 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_ruleRelationType2538 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationType2560 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_ruleRelationType2572 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleRelationType2594 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_ruleRelationType2611 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationType2633 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_ruleRelationType2645 = new BitSet(new long[]{0x3800000000000020L});
    public static final BitSet FOLLOW_59_in_ruleRelationType2667 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_60_in_ruleRelationType2683 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_61_in_ruleRelationType2699 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleRelationType2716 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_62_in_ruleRelationType2735 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000003L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_ruleRelationType2769 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_ruleRelationType2782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_ruleRelationMultiplicityEnum2829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_ruleRelationMultiplicityEnum2844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleRelationMultiplicityEnum2859 = new BitSet(new long[]{0x0000000000000002L});

}