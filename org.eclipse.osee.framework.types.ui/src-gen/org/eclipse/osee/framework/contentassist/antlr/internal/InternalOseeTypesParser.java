package org.eclipse.osee.framework.contentassist.antlr.internal; 

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.xtext.parsetree.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import org.eclipse.osee.framework.services.OseeTypesGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class InternalOseeTypesParser extends AbstractInternalContentAssistParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_STRING", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'0'", "'1'", "'2'", "'3'", "'4'", "'5'", "'6'", "'7'", "'8'", "'9'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'MappedAttributeDataProvider'", "'unlimited'", "'DefaultAttributeTaggerProvider'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'WordAttribute'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'ONE_TO_MANY'", "'MANY_TO_MANY'", "'MANY_TO_ONE'", "'import'", "'.'", "'abstract'", "'artifactType'", "'{'", "'}'", "'extends'", "'attribute'", "'attributeType'", "'dataProvider'", "'min'", "'max'", "'taggerId'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'oseeEnumType'", "'entry'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'"
    };
    public static final int RULE_ID=4;
    public static final int RULE_STRING=5;
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
    public String getGrammarFileName() { return "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g"; }


     
     	private OseeTypesGrammarAccess grammarAccess;
     	
        public void setGrammarAccess(OseeTypesGrammarAccess grammarAccess) {
        	this.grammarAccess = grammarAccess;
        }
        
        @Override
        protected Grammar getGrammar() {
        	return grammarAccess.getGrammar();
        }
        
        @Override
        protected String getValueForTokenName(String tokenName) {
        	return tokenName;
        }




    // $ANTLR start entryRuleOseeTypeModel
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:60:1: entryRuleOseeTypeModel : ruleOseeTypeModel EOF ;
    public final void entryRuleOseeTypeModel() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:60:24: ( ruleOseeTypeModel EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:61:1: ruleOseeTypeModel EOF
            {
             before(grammarAccess.getOseeTypeModelRule()); 
            pushFollow(FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel60);
            ruleOseeTypeModel();
            _fsp--;

             after(grammarAccess.getOseeTypeModelRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeTypeModel67); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleOseeTypeModel


    // $ANTLR start ruleOseeTypeModel
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:68:1: ruleOseeTypeModel : ( ( rule__OseeTypeModel__Group__0 ) ) ;
    public final void ruleOseeTypeModel() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:72:2: ( ( ( rule__OseeTypeModel__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:73:1: ( ( rule__OseeTypeModel__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:73:1: ( ( rule__OseeTypeModel__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:74:1: ( rule__OseeTypeModel__Group__0 )
            {
             before(grammarAccess.getOseeTypeModelAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:75:1: ( rule__OseeTypeModel__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:75:2: rule__OseeTypeModel__Group__0
            {
            pushFollow(FOLLOW_rule__OseeTypeModel__Group__0_in_ruleOseeTypeModel94);
            rule__OseeTypeModel__Group__0();
            _fsp--;


            }

             after(grammarAccess.getOseeTypeModelAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleOseeTypeModel


    // $ANTLR start entryRuleImport
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:87:1: entryRuleImport : ruleImport EOF ;
    public final void entryRuleImport() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:87:17: ( ruleImport EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:88:1: ruleImport EOF
            {
             before(grammarAccess.getImportRule()); 
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport120);
            ruleImport();
            _fsp--;

             after(grammarAccess.getImportRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport127); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleImport


    // $ANTLR start ruleImport
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:95:1: ruleImport : ( ( rule__Import__Group__0 ) ) ;
    public final void ruleImport() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:99:2: ( ( ( rule__Import__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:100:1: ( ( rule__Import__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:100:1: ( ( rule__Import__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:101:1: ( rule__Import__Group__0 )
            {
             before(grammarAccess.getImportAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:102:1: ( rule__Import__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:102:2: rule__Import__Group__0
            {
            pushFollow(FOLLOW_rule__Import__Group__0_in_ruleImport154);
            rule__Import__Group__0();
            _fsp--;


            }

             after(grammarAccess.getImportAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleImport


    // $ANTLR start entryRuleQUALIFIED_NAME
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:114:1: entryRuleQUALIFIED_NAME : ruleQUALIFIED_NAME EOF ;
    public final void entryRuleQUALIFIED_NAME() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:114:25: ( ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:115:1: ruleQUALIFIED_NAME EOF
            {
             before(grammarAccess.getQUALIFIED_NAMERule()); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME180);
            ruleQUALIFIED_NAME();
            _fsp--;

             after(grammarAccess.getQUALIFIED_NAMERule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME187); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleQUALIFIED_NAME


    // $ANTLR start ruleQUALIFIED_NAME
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:122:1: ruleQUALIFIED_NAME : ( ( rule__QUALIFIED_NAME__Group__0 ) ) ;
    public final void ruleQUALIFIED_NAME() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:126:2: ( ( ( rule__QUALIFIED_NAME__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:127:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:127:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:128:1: ( rule__QUALIFIED_NAME__Group__0 )
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:129:1: ( rule__QUALIFIED_NAME__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:129:2: rule__QUALIFIED_NAME__Group__0
            {
            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group__0_in_ruleQUALIFIED_NAME214);
            rule__QUALIFIED_NAME__Group__0();
            _fsp--;


            }

             after(grammarAccess.getQUALIFIED_NAMEAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleQUALIFIED_NAME


    // $ANTLR start entryRuleDIGITS
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:141:1: entryRuleDIGITS : ruleDIGITS EOF ;
    public final void entryRuleDIGITS() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:141:17: ( ruleDIGITS EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:142:1: ruleDIGITS EOF
            {
             before(grammarAccess.getDIGITSRule()); 
            pushFollow(FOLLOW_ruleDIGITS_in_entryRuleDIGITS240);
            ruleDIGITS();
            _fsp--;

             after(grammarAccess.getDIGITSRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDIGITS247); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleDIGITS


    // $ANTLR start ruleDIGITS
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:149:1: ruleDIGITS : ( ( ( rule__DIGITS__Alternatives ) ) ( ( rule__DIGITS__Alternatives )* ) ) ;
    public final void ruleDIGITS() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:153:2: ( ( ( ( rule__DIGITS__Alternatives ) ) ( ( rule__DIGITS__Alternatives )* ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:154:1: ( ( ( rule__DIGITS__Alternatives ) ) ( ( rule__DIGITS__Alternatives )* ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:154:1: ( ( ( rule__DIGITS__Alternatives ) ) ( ( rule__DIGITS__Alternatives )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:155:1: ( ( rule__DIGITS__Alternatives ) ) ( ( rule__DIGITS__Alternatives )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:155:1: ( ( rule__DIGITS__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:156:1: ( rule__DIGITS__Alternatives )
            {
             before(grammarAccess.getDIGITSAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:157:1: ( rule__DIGITS__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:157:2: rule__DIGITS__Alternatives
            {
            pushFollow(FOLLOW_rule__DIGITS__Alternatives_in_ruleDIGITS276);
            rule__DIGITS__Alternatives();
            _fsp--;


            }

             after(grammarAccess.getDIGITSAccess().getAlternatives()); 

            }

            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:160:1: ( ( rule__DIGITS__Alternatives )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:161:1: ( rule__DIGITS__Alternatives )*
            {
             before(grammarAccess.getDIGITSAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:162:1: ( rule__DIGITS__Alternatives )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=11 && LA1_0<=20)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:162:2: rule__DIGITS__Alternatives
            	    {
            	    pushFollow(FOLLOW_rule__DIGITS__Alternatives_in_ruleDIGITS288);
            	    rule__DIGITS__Alternatives();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

             after(grammarAccess.getDIGITSAccess().getAlternatives()); 

            }


            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleDIGITS


    // $ANTLR start entryRuleOseeType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:175:1: entryRuleOseeType : ruleOseeType EOF ;
    public final void entryRuleOseeType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:175:19: ( ruleOseeType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:176:1: ruleOseeType EOF
            {
             before(grammarAccess.getOseeTypeRule()); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType317);
            ruleOseeType();
            _fsp--;

             after(grammarAccess.getOseeTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType324); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleOseeType


    // $ANTLR start ruleOseeType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:183:1: ruleOseeType : ( ( rule__OseeType__Alternatives ) ) ;
    public final void ruleOseeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:187:2: ( ( ( rule__OseeType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:188:1: ( ( rule__OseeType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:188:1: ( ( rule__OseeType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:189:1: ( rule__OseeType__Alternatives )
            {
             before(grammarAccess.getOseeTypeAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:190:1: ( rule__OseeType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:190:2: rule__OseeType__Alternatives
            {
            pushFollow(FOLLOW_rule__OseeType__Alternatives_in_ruleOseeType351);
            rule__OseeType__Alternatives();
            _fsp--;


            }

             after(grammarAccess.getOseeTypeAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleOseeType


    // $ANTLR start entryRuleArtifactType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:202:1: entryRuleArtifactType : ruleArtifactType EOF ;
    public final void entryRuleArtifactType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:202:23: ( ruleArtifactType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:203:1: ruleArtifactType EOF
            {
             before(grammarAccess.getArtifactTypeRule()); 
            pushFollow(FOLLOW_ruleArtifactType_in_entryRuleArtifactType377);
            ruleArtifactType();
            _fsp--;

             after(grammarAccess.getArtifactTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactType384); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleArtifactType


    // $ANTLR start ruleArtifactType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:210:1: ruleArtifactType : ( ( rule__ArtifactType__Group__0 ) ) ;
    public final void ruleArtifactType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:214:2: ( ( ( rule__ArtifactType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:215:1: ( ( rule__ArtifactType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:215:1: ( ( rule__ArtifactType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:216:1: ( rule__ArtifactType__Group__0 )
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:217:1: ( rule__ArtifactType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:217:2: rule__ArtifactType__Group__0
            {
            pushFollow(FOLLOW_rule__ArtifactType__Group__0_in_ruleArtifactType411);
            rule__ArtifactType__Group__0();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleArtifactType


    // $ANTLR start entryRuleAttributeTypeRef
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:229:1: entryRuleAttributeTypeRef : ruleAttributeTypeRef EOF ;
    public final void entryRuleAttributeTypeRef() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:229:27: ( ruleAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:230:1: ruleAttributeTypeRef EOF
            {
             before(grammarAccess.getAttributeTypeRefRule()); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef437);
            ruleAttributeTypeRef();
            _fsp--;

             after(grammarAccess.getAttributeTypeRefRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRef444); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleAttributeTypeRef


    // $ANTLR start ruleAttributeTypeRef
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:237:1: ruleAttributeTypeRef : ( ( rule__AttributeTypeRef__Group__0 ) ) ;
    public final void ruleAttributeTypeRef() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:241:2: ( ( ( rule__AttributeTypeRef__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:242:1: ( ( rule__AttributeTypeRef__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:242:1: ( ( rule__AttributeTypeRef__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:243:1: ( rule__AttributeTypeRef__Group__0 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:244:1: ( rule__AttributeTypeRef__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:244:2: rule__AttributeTypeRef__Group__0
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__0_in_ruleAttributeTypeRef471);
            rule__AttributeTypeRef__Group__0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeRefAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleAttributeTypeRef


    // $ANTLR start entryRuleAttributeType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:256:1: entryRuleAttributeType : ruleAttributeType EOF ;
    public final void entryRuleAttributeType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:256:24: ( ruleAttributeType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:257:1: ruleAttributeType EOF
            {
             before(grammarAccess.getAttributeTypeRule()); 
            pushFollow(FOLLOW_ruleAttributeType_in_entryRuleAttributeType497);
            ruleAttributeType();
            _fsp--;

             after(grammarAccess.getAttributeTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeType504); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleAttributeType


    // $ANTLR start ruleAttributeType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:264:1: ruleAttributeType : ( ( rule__AttributeType__Group__0 ) ) ;
    public final void ruleAttributeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:268:2: ( ( ( rule__AttributeType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:269:1: ( ( rule__AttributeType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:269:1: ( ( rule__AttributeType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:270:1: ( rule__AttributeType__Group__0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:271:1: ( rule__AttributeType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:271:2: rule__AttributeType__Group__0
            {
            pushFollow(FOLLOW_rule__AttributeType__Group__0_in_ruleAttributeType531);
            rule__AttributeType__Group__0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleAttributeType


    // $ANTLR start entryRuleAttributeBaseType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:283:1: entryRuleAttributeBaseType : ruleAttributeBaseType EOF ;
    public final void entryRuleAttributeBaseType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:283:28: ( ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:284:1: ruleAttributeBaseType EOF
            {
             before(grammarAccess.getAttributeBaseTypeRule()); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType557);
            ruleAttributeBaseType();
            _fsp--;

             after(grammarAccess.getAttributeBaseTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType564); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleAttributeBaseType


    // $ANTLR start ruleAttributeBaseType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:291:1: ruleAttributeBaseType : ( ( rule__AttributeBaseType__Alternatives ) ) ;
    public final void ruleAttributeBaseType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:295:2: ( ( ( rule__AttributeBaseType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:296:1: ( ( rule__AttributeBaseType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:296:1: ( ( rule__AttributeBaseType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:297:1: ( rule__AttributeBaseType__Alternatives )
            {
             before(grammarAccess.getAttributeBaseTypeAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:298:1: ( rule__AttributeBaseType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:298:2: rule__AttributeBaseType__Alternatives
            {
            pushFollow(FOLLOW_rule__AttributeBaseType__Alternatives_in_ruleAttributeBaseType591);
            rule__AttributeBaseType__Alternatives();
            _fsp--;


            }

             after(grammarAccess.getAttributeBaseTypeAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleAttributeBaseType


    // $ANTLR start entryRuleOseeEnumType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:310:1: entryRuleOseeEnumType : ruleOseeEnumType EOF ;
    public final void entryRuleOseeEnumType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:310:23: ( ruleOseeEnumType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:311:1: ruleOseeEnumType EOF
            {
             before(grammarAccess.getOseeEnumTypeRule()); 
            pushFollow(FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType617);
            ruleOseeEnumType();
            _fsp--;

             after(grammarAccess.getOseeEnumTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumType624); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleOseeEnumType


    // $ANTLR start ruleOseeEnumType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:318:1: ruleOseeEnumType : ( ( rule__OseeEnumType__Group__0 ) ) ;
    public final void ruleOseeEnumType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:322:2: ( ( ( rule__OseeEnumType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:323:1: ( ( rule__OseeEnumType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:323:1: ( ( rule__OseeEnumType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:324:1: ( rule__OseeEnumType__Group__0 )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:325:1: ( rule__OseeEnumType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:325:2: rule__OseeEnumType__Group__0
            {
            pushFollow(FOLLOW_rule__OseeEnumType__Group__0_in_ruleOseeEnumType651);
            rule__OseeEnumType__Group__0();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleOseeEnumType


    // $ANTLR start entryRuleOseeEnumEntry
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:337:1: entryRuleOseeEnumEntry : ruleOseeEnumEntry EOF ;
    public final void entryRuleOseeEnumEntry() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:337:24: ( ruleOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:338:1: ruleOseeEnumEntry EOF
            {
             before(grammarAccess.getOseeEnumEntryRule()); 
            pushFollow(FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry677);
            ruleOseeEnumEntry();
            _fsp--;

             after(grammarAccess.getOseeEnumEntryRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumEntry684); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleOseeEnumEntry


    // $ANTLR start ruleOseeEnumEntry
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:345:1: ruleOseeEnumEntry : ( ( rule__OseeEnumEntry__Group__0 ) ) ;
    public final void ruleOseeEnumEntry() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:349:2: ( ( ( rule__OseeEnumEntry__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:350:1: ( ( rule__OseeEnumEntry__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:350:1: ( ( rule__OseeEnumEntry__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:351:1: ( rule__OseeEnumEntry__Group__0 )
            {
             before(grammarAccess.getOseeEnumEntryAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:352:1: ( rule__OseeEnumEntry__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:352:2: rule__OseeEnumEntry__Group__0
            {
            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__0_in_ruleOseeEnumEntry711);
            rule__OseeEnumEntry__Group__0();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumEntryAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleOseeEnumEntry


    // $ANTLR start entryRuleRelationType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:364:1: entryRuleRelationType : ruleRelationType EOF ;
    public final void entryRuleRelationType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:364:23: ( ruleRelationType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:365:1: ruleRelationType EOF
            {
             before(grammarAccess.getRelationTypeRule()); 
            pushFollow(FOLLOW_ruleRelationType_in_entryRuleRelationType737);
            ruleRelationType();
            _fsp--;

             after(grammarAccess.getRelationTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationType744); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end entryRuleRelationType


    // $ANTLR start ruleRelationType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:372:1: ruleRelationType : ( ( rule__RelationType__Group__0 ) ) ;
    public final void ruleRelationType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:376:2: ( ( ( rule__RelationType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:377:1: ( ( rule__RelationType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:377:1: ( ( rule__RelationType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:378:1: ( rule__RelationType__Group__0 )
            {
             before(grammarAccess.getRelationTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:379:1: ( rule__RelationType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:379:2: rule__RelationType__Group__0
            {
            pushFollow(FOLLOW_rule__RelationType__Group__0_in_ruleRelationType771);
            rule__RelationType__Group__0();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleRelationType


    // $ANTLR start ruleRelationMultiplicityEnum
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:392:1: ruleRelationMultiplicityEnum : ( ( rule__RelationMultiplicityEnum__Alternatives ) ) ;
    public final void ruleRelationMultiplicityEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:396:1: ( ( ( rule__RelationMultiplicityEnum__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:397:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:397:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:398:1: ( rule__RelationMultiplicityEnum__Alternatives )
            {
             before(grammarAccess.getRelationMultiplicityEnumAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:399:1: ( rule__RelationMultiplicityEnum__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:399:2: rule__RelationMultiplicityEnum__Alternatives
            {
            pushFollow(FOLLOW_rule__RelationMultiplicityEnum__Alternatives_in_ruleRelationMultiplicityEnum808);
            rule__RelationMultiplicityEnum__Alternatives();
            _fsp--;


            }

             after(grammarAccess.getRelationMultiplicityEnumAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleRelationMultiplicityEnum


    // $ANTLR start rule__DIGITS__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:410:1: rule__DIGITS__Alternatives : ( ( '0' ) | ( '1' ) | ( '2' ) | ( '3' ) | ( '4' ) | ( '5' ) | ( '6' ) | ( '7' ) | ( '8' ) | ( '9' ) );
    public final void rule__DIGITS__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:414:1: ( ( '0' ) | ( '1' ) | ( '2' ) | ( '3' ) | ( '4' ) | ( '5' ) | ( '6' ) | ( '7' ) | ( '8' ) | ( '9' ) )
            int alt2=10;
            switch ( input.LA(1) ) {
            case 11:
                {
                alt2=1;
                }
                break;
            case 12:
                {
                alt2=2;
                }
                break;
            case 13:
                {
                alt2=3;
                }
                break;
            case 14:
                {
                alt2=4;
                }
                break;
            case 15:
                {
                alt2=5;
                }
                break;
            case 16:
                {
                alt2=6;
                }
                break;
            case 17:
                {
                alt2=7;
                }
                break;
            case 18:
                {
                alt2=8;
                }
                break;
            case 19:
                {
                alt2=9;
                }
                break;
            case 20:
                {
                alt2=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("410:1: rule__DIGITS__Alternatives : ( ( '0' ) | ( '1' ) | ( '2' ) | ( '3' ) | ( '4' ) | ( '5' ) | ( '6' ) | ( '7' ) | ( '8' ) | ( '9' ) );", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:415:1: ( '0' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:415:1: ( '0' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:416:1: '0'
                    {
                     before(grammarAccess.getDIGITSAccess().getDigitZeroKeyword_0()); 
                    match(input,11,FOLLOW_11_in_rule__DIGITS__Alternatives844); 
                     after(grammarAccess.getDIGITSAccess().getDigitZeroKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:423:6: ( '1' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:423:6: ( '1' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:424:1: '1'
                    {
                     before(grammarAccess.getDIGITSAccess().getDigitOneKeyword_1()); 
                    match(input,12,FOLLOW_12_in_rule__DIGITS__Alternatives864); 
                     after(grammarAccess.getDIGITSAccess().getDigitOneKeyword_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:431:6: ( '2' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:431:6: ( '2' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:432:1: '2'
                    {
                     before(grammarAccess.getDIGITSAccess().getDigitTwoKeyword_2()); 
                    match(input,13,FOLLOW_13_in_rule__DIGITS__Alternatives884); 
                     after(grammarAccess.getDIGITSAccess().getDigitTwoKeyword_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:439:6: ( '3' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:439:6: ( '3' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:440:1: '3'
                    {
                     before(grammarAccess.getDIGITSAccess().getDigitThreeKeyword_3()); 
                    match(input,14,FOLLOW_14_in_rule__DIGITS__Alternatives904); 
                     after(grammarAccess.getDIGITSAccess().getDigitThreeKeyword_3()); 

                    }


                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:447:6: ( '4' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:447:6: ( '4' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:448:1: '4'
                    {
                     before(grammarAccess.getDIGITSAccess().getDigitFourKeyword_4()); 
                    match(input,15,FOLLOW_15_in_rule__DIGITS__Alternatives924); 
                     after(grammarAccess.getDIGITSAccess().getDigitFourKeyword_4()); 

                    }


                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:455:6: ( '5' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:455:6: ( '5' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:456:1: '5'
                    {
                     before(grammarAccess.getDIGITSAccess().getDigitFiveKeyword_5()); 
                    match(input,16,FOLLOW_16_in_rule__DIGITS__Alternatives944); 
                     after(grammarAccess.getDIGITSAccess().getDigitFiveKeyword_5()); 

                    }


                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:463:6: ( '6' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:463:6: ( '6' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:464:1: '6'
                    {
                     before(grammarAccess.getDIGITSAccess().getDigitSixKeyword_6()); 
                    match(input,17,FOLLOW_17_in_rule__DIGITS__Alternatives964); 
                     after(grammarAccess.getDIGITSAccess().getDigitSixKeyword_6()); 

                    }


                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:471:6: ( '7' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:471:6: ( '7' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:472:1: '7'
                    {
                     before(grammarAccess.getDIGITSAccess().getDigitSevenKeyword_7()); 
                    match(input,18,FOLLOW_18_in_rule__DIGITS__Alternatives984); 
                     after(grammarAccess.getDIGITSAccess().getDigitSevenKeyword_7()); 

                    }


                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:479:6: ( '8' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:479:6: ( '8' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:480:1: '8'
                    {
                     before(grammarAccess.getDIGITSAccess().getDigitEightKeyword_8()); 
                    match(input,19,FOLLOW_19_in_rule__DIGITS__Alternatives1004); 
                     after(grammarAccess.getDIGITSAccess().getDigitEightKeyword_8()); 

                    }


                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:487:6: ( '9' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:487:6: ( '9' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:488:1: '9'
                    {
                     before(grammarAccess.getDIGITSAccess().getDigitNineKeyword_9()); 
                    match(input,20,FOLLOW_20_in_rule__DIGITS__Alternatives1024); 
                     after(grammarAccess.getDIGITSAccess().getDigitNineKeyword_9()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__DIGITS__Alternatives


    // $ANTLR start rule__OseeType__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:500:1: rule__OseeType__Alternatives : ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) );
    public final void rule__OseeType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:504:1: ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) )
            int alt3=4;
            switch ( input.LA(1) ) {
            case 43:
            case 44:
                {
                alt3=1;
                }
                break;
            case 60:
                {
                alt3=2;
                }
                break;
            case 49:
                {
                alt3=3;
                }
                break;
            case 58:
                {
                alt3=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("500:1: rule__OseeType__Alternatives : ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) );", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:505:1: ( ruleArtifactType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:505:1: ( ruleArtifactType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:506:1: ruleArtifactType
                    {
                     before(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0()); 
                    pushFollow(FOLLOW_ruleArtifactType_in_rule__OseeType__Alternatives1058);
                    ruleArtifactType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:511:6: ( ruleRelationType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:511:6: ( ruleRelationType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:512:1: ruleRelationType
                    {
                     before(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1()); 
                    pushFollow(FOLLOW_ruleRelationType_in_rule__OseeType__Alternatives1075);
                    ruleRelationType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:517:6: ( ruleAttributeType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:517:6: ( ruleAttributeType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:518:1: ruleAttributeType
                    {
                     before(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2()); 
                    pushFollow(FOLLOW_ruleAttributeType_in_rule__OseeType__Alternatives1092);
                    ruleAttributeType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:523:6: ( ruleOseeEnumType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:523:6: ( ruleOseeEnumType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:524:1: ruleOseeEnumType
                    {
                     before(grammarAccess.getOseeTypeAccess().getOseeEnumTypeParserRuleCall_3()); 
                    pushFollow(FOLLOW_ruleOseeEnumType_in_rule__OseeType__Alternatives1109);
                    ruleOseeEnumType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getOseeEnumTypeParserRuleCall_3()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeType__Alternatives


    // $ANTLR start rule__AttributeType__DataProviderAlternatives_5_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:534:1: rule__AttributeType__DataProviderAlternatives_5_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( 'MappedAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeType__DataProviderAlternatives_5_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:538:1: ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( 'MappedAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) )
            int alt4=4;
            switch ( input.LA(1) ) {
            case 21:
                {
                alt4=1;
                }
                break;
            case 22:
                {
                alt4=2;
                }
                break;
            case 23:
                {
                alt4=3;
                }
                break;
            case RULE_ID:
                {
                alt4=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("534:1: rule__AttributeType__DataProviderAlternatives_5_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( 'MappedAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:539:1: ( 'DefaultAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:539:1: ( 'DefaultAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:540:1: 'DefaultAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_5_0_0()); 
                    match(input,21,FOLLOW_21_in_rule__AttributeType__DataProviderAlternatives_5_01142); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_5_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:547:6: ( 'UriAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:547:6: ( 'UriAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:548:1: 'UriAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_5_0_1()); 
                    match(input,22,FOLLOW_22_in_rule__AttributeType__DataProviderAlternatives_5_01162); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_5_0_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:555:6: ( 'MappedAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:555:6: ( 'MappedAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:556:1: 'MappedAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderMappedAttributeDataProviderKeyword_5_0_2()); 
                    match(input,23,FOLLOW_23_in_rule__AttributeType__DataProviderAlternatives_5_01182); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderMappedAttributeDataProviderKeyword_5_0_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:563:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:563:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:564:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_5_0_3()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__DataProviderAlternatives_5_01201);
                    ruleQUALIFIED_NAME();
                    _fsp--;

                     after(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_5_0_3()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__DataProviderAlternatives_5_0


    // $ANTLR start rule__AttributeType__MaxAlternatives_9_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:574:1: rule__AttributeType__MaxAlternatives_9_0 : ( ( ruleDIGITS ) | ( 'unlimited' ) );
    public final void rule__AttributeType__MaxAlternatives_9_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:578:1: ( ( ruleDIGITS ) | ( 'unlimited' ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0>=11 && LA5_0<=20)) ) {
                alt5=1;
            }
            else if ( (LA5_0==24) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("574:1: rule__AttributeType__MaxAlternatives_9_0 : ( ( ruleDIGITS ) | ( 'unlimited' ) );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:579:1: ( ruleDIGITS )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:579:1: ( ruleDIGITS )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:580:1: ruleDIGITS
                    {
                     before(grammarAccess.getAttributeTypeAccess().getMaxDIGITSParserRuleCall_9_0_0()); 
                    pushFollow(FOLLOW_ruleDIGITS_in_rule__AttributeType__MaxAlternatives_9_01233);
                    ruleDIGITS();
                    _fsp--;

                     after(grammarAccess.getAttributeTypeAccess().getMaxDIGITSParserRuleCall_9_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:585:6: ( 'unlimited' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:585:6: ( 'unlimited' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:586:1: 'unlimited'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_9_0_1()); 
                    match(input,24,FOLLOW_24_in_rule__AttributeType__MaxAlternatives_9_01251); 
                     after(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_9_0_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__MaxAlternatives_9_0


    // $ANTLR start rule__AttributeType__TaggerIdAlternatives_10_1_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:598:1: rule__AttributeType__TaggerIdAlternatives_10_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeType__TaggerIdAlternatives_10_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:602:1: ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==25) ) {
                alt6=1;
            }
            else if ( (LA6_0==RULE_ID) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("598:1: rule__AttributeType__TaggerIdAlternatives_10_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:603:1: ( 'DefaultAttributeTaggerProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:603:1: ( 'DefaultAttributeTaggerProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:604:1: 'DefaultAttributeTaggerProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_10_1_0_0()); 
                    match(input,25,FOLLOW_25_in_rule__AttributeType__TaggerIdAlternatives_10_1_01286); 
                     after(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_10_1_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:611:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:611:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:612:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_10_1_0_1()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__TaggerIdAlternatives_10_1_01305);
                    ruleQUALIFIED_NAME();
                    _fsp--;

                     after(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_10_1_0_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__TaggerIdAlternatives_10_1_0


    // $ANTLR start rule__AttributeBaseType__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:622:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeBaseType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:626:1: ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) )
            int alt7=10;
            switch ( input.LA(1) ) {
            case 26:
                {
                alt7=1;
                }
                break;
            case 27:
                {
                alt7=2;
                }
                break;
            case 28:
                {
                alt7=3;
                }
                break;
            case 29:
                {
                alt7=4;
                }
                break;
            case 30:
                {
                alt7=5;
                }
                break;
            case 31:
                {
                alt7=6;
                }
                break;
            case 32:
                {
                alt7=7;
                }
                break;
            case 33:
                {
                alt7=8;
                }
                break;
            case 34:
                {
                alt7=9;
                }
                break;
            case RULE_ID:
                {
                alt7=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("622:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:627:1: ( 'BooleanAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:627:1: ( 'BooleanAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:628:1: 'BooleanAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                    match(input,26,FOLLOW_26_in_rule__AttributeBaseType__Alternatives1338); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:635:6: ( 'CompressedContentAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:635:6: ( 'CompressedContentAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:636:1: 'CompressedContentAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                    match(input,27,FOLLOW_27_in_rule__AttributeBaseType__Alternatives1358); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:643:6: ( 'DateAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:643:6: ( 'DateAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:644:1: 'DateAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                    match(input,28,FOLLOW_28_in_rule__AttributeBaseType__Alternatives1378); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:651:6: ( 'EnumeratedAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:651:6: ( 'EnumeratedAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:652:1: 'EnumeratedAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                    match(input,29,FOLLOW_29_in_rule__AttributeBaseType__Alternatives1398); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 

                    }


                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:659:6: ( 'FloatingPointAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:659:6: ( 'FloatingPointAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:660:1: 'FloatingPointAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                    match(input,30,FOLLOW_30_in_rule__AttributeBaseType__Alternatives1418); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 

                    }


                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:667:6: ( 'IntegerAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:667:6: ( 'IntegerAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:668:1: 'IntegerAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                    match(input,31,FOLLOW_31_in_rule__AttributeBaseType__Alternatives1438); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 

                    }


                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:675:6: ( 'JavaObjectAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:675:6: ( 'JavaObjectAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:676:1: 'JavaObjectAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 
                    match(input,32,FOLLOW_32_in_rule__AttributeBaseType__Alternatives1458); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 

                    }


                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:683:6: ( 'StringAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:683:6: ( 'StringAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:684:1: 'StringAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 
                    match(input,33,FOLLOW_33_in_rule__AttributeBaseType__Alternatives1478); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 

                    }


                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:691:6: ( 'WordAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:691:6: ( 'WordAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:692:1: 'WordAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 
                    match(input,34,FOLLOW_34_in_rule__AttributeBaseType__Alternatives1498); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 

                    }


                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:699:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:699:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:700:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeBaseType__Alternatives1517);
                    ruleQUALIFIED_NAME();
                    _fsp--;

                     after(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeBaseType__Alternatives


    // $ANTLR start rule__RelationType__DefaultOrderTypeAlternatives_12_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:710:1: rule__RelationType__DefaultOrderTypeAlternatives_12_0 : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );
    public final void rule__RelationType__DefaultOrderTypeAlternatives_12_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:714:1: ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) )
            int alt8=4;
            switch ( input.LA(1) ) {
            case 35:
                {
                alt8=1;
                }
                break;
            case 36:
                {
                alt8=2;
                }
                break;
            case 37:
                {
                alt8=3;
                }
                break;
            case RULE_ID:
                {
                alt8=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("710:1: rule__RelationType__DefaultOrderTypeAlternatives_12_0 : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:715:1: ( 'Lexicographical_Ascending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:715:1: ( 'Lexicographical_Ascending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:716:1: 'Lexicographical_Ascending'
                    {
                     before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_AscendingKeyword_12_0_0()); 
                    match(input,35,FOLLOW_35_in_rule__RelationType__DefaultOrderTypeAlternatives_12_01550); 
                     after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_AscendingKeyword_12_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:723:6: ( 'Lexicographical_Descending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:723:6: ( 'Lexicographical_Descending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:724:1: 'Lexicographical_Descending'
                    {
                     before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_DescendingKeyword_12_0_1()); 
                    match(input,36,FOLLOW_36_in_rule__RelationType__DefaultOrderTypeAlternatives_12_01570); 
                     after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_DescendingKeyword_12_0_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:731:6: ( 'Unordered' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:731:6: ( 'Unordered' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:732:1: 'Unordered'
                    {
                     before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeUnorderedKeyword_12_0_2()); 
                    match(input,37,FOLLOW_37_in_rule__RelationType__DefaultOrderTypeAlternatives_12_01590); 
                     after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeUnorderedKeyword_12_0_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:739:6: ( RULE_ID )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:739:6: ( RULE_ID )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:740:1: RULE_ID
                    {
                     before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeIDTerminalRuleCall_12_0_3()); 
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__RelationType__DefaultOrderTypeAlternatives_12_01609); 
                     after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeIDTerminalRuleCall_12_0_3()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__DefaultOrderTypeAlternatives_12_0


    // $ANTLR start rule__RelationMultiplicityEnum__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:750:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) );
    public final void rule__RelationMultiplicityEnum__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:754:1: ( ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) )
            int alt9=3;
            switch ( input.LA(1) ) {
            case 38:
                {
                alt9=1;
                }
                break;
            case 39:
                {
                alt9=2;
                }
                break;
            case 40:
                {
                alt9=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("750:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) );", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:755:1: ( ( 'ONE_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:755:1: ( ( 'ONE_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:756:1: ( 'ONE_TO_MANY' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_0()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:757:1: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:757:3: 'ONE_TO_MANY'
                    {
                    match(input,38,FOLLOW_38_in_rule__RelationMultiplicityEnum__Alternatives1642); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:762:6: ( ( 'MANY_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:762:6: ( ( 'MANY_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:763:1: ( 'MANY_TO_MANY' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_1()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:764:1: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:764:3: 'MANY_TO_MANY'
                    {
                    match(input,39,FOLLOW_39_in_rule__RelationMultiplicityEnum__Alternatives1663); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:769:6: ( ( 'MANY_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:769:6: ( ( 'MANY_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:770:1: ( 'MANY_TO_ONE' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:771:1: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:771:3: 'MANY_TO_ONE'
                    {
                    match(input,40,FOLLOW_40_in_rule__RelationMultiplicityEnum__Alternatives1684); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationMultiplicityEnum__Alternatives


    // $ANTLR start rule__OseeTypeModel__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:783:1: rule__OseeTypeModel__Group__0 : ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1 ;
    public final void rule__OseeTypeModel__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:787:1: ( ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:788:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:788:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:789:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            {
             before(grammarAccess.getOseeTypeModelAccess().getImportsAssignment_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:790:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==41) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:790:2: rule__OseeTypeModel__ImportsAssignment_0
            	    {
            	    pushFollow(FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__01721);
            	    rule__OseeTypeModel__ImportsAssignment_0();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

             after(grammarAccess.getOseeTypeModelAccess().getImportsAssignment_0()); 

            }

            pushFollow(FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01731);
            rule__OseeTypeModel__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeTypeModel__Group__0


    // $ANTLR start rule__OseeTypeModel__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:801:1: rule__OseeTypeModel__Group__1 : ( ( rule__OseeTypeModel__TypesAssignment_1 )* ) ;
    public final void rule__OseeTypeModel__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:805:1: ( ( ( rule__OseeTypeModel__TypesAssignment_1 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:806:1: ( ( rule__OseeTypeModel__TypesAssignment_1 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:806:1: ( ( rule__OseeTypeModel__TypesAssignment_1 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:807:1: ( rule__OseeTypeModel__TypesAssignment_1 )*
            {
             before(grammarAccess.getOseeTypeModelAccess().getTypesAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:808:1: ( rule__OseeTypeModel__TypesAssignment_1 )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>=43 && LA11_0<=44)||LA11_0==49||LA11_0==58||LA11_0==60) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:808:2: rule__OseeTypeModel__TypesAssignment_1
            	    {
            	    pushFollow(FOLLOW_rule__OseeTypeModel__TypesAssignment_1_in_rule__OseeTypeModel__Group__11759);
            	    rule__OseeTypeModel__TypesAssignment_1();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

             after(grammarAccess.getOseeTypeModelAccess().getTypesAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeTypeModel__Group__1


    // $ANTLR start rule__Import__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:822:1: rule__Import__Group__0 : ( 'import' ) rule__Import__Group__1 ;
    public final void rule__Import__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:826:1: ( ( 'import' ) rule__Import__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:827:1: ( 'import' ) rule__Import__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:827:1: ( 'import' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:828:1: 'import'
            {
             before(grammarAccess.getImportAccess().getImportKeyword_0()); 
            match(input,41,FOLLOW_41_in_rule__Import__Group__01799); 
             after(grammarAccess.getImportAccess().getImportKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__Import__Group__1_in_rule__Import__Group__01809);
            rule__Import__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Import__Group__0


    // $ANTLR start rule__Import__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:842:1: rule__Import__Group__1 : ( ( rule__Import__ImportURIAssignment_1 ) ) ;
    public final void rule__Import__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:846:1: ( ( ( rule__Import__ImportURIAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:847:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:847:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:848:1: ( rule__Import__ImportURIAssignment_1 )
            {
             before(grammarAccess.getImportAccess().getImportURIAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:849:1: ( rule__Import__ImportURIAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:849:2: rule__Import__ImportURIAssignment_1
            {
            pushFollow(FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__11837);
            rule__Import__ImportURIAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getImportAccess().getImportURIAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Import__Group__1


    // $ANTLR start rule__QUALIFIED_NAME__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:863:1: rule__QUALIFIED_NAME__Group__0 : ( RULE_ID ) rule__QUALIFIED_NAME__Group__1 ;
    public final void rule__QUALIFIED_NAME__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:867:1: ( ( RULE_ID ) rule__QUALIFIED_NAME__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:868:1: ( RULE_ID ) rule__QUALIFIED_NAME__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:868:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:869:1: RULE_ID
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__01875); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 

            }

            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__01883);
            rule__QUALIFIED_NAME__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__QUALIFIED_NAME__Group__0


    // $ANTLR start rule__QUALIFIED_NAME__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:881:1: rule__QUALIFIED_NAME__Group__1 : ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) ;
    public final void rule__QUALIFIED_NAME__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:885:1: ( ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:886:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:886:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:887:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:888:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==42) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:888:2: rule__QUALIFIED_NAME__Group_1__0
            	    {
            	    pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__11911);
            	    rule__QUALIFIED_NAME__Group_1__0();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

             after(grammarAccess.getQUALIFIED_NAMEAccess().getGroup_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__QUALIFIED_NAME__Group__1


    // $ANTLR start rule__QUALIFIED_NAME__Group_1__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:902:1: rule__QUALIFIED_NAME__Group_1__0 : ( '.' ) rule__QUALIFIED_NAME__Group_1__1 ;
    public final void rule__QUALIFIED_NAME__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:906:1: ( ( '.' ) rule__QUALIFIED_NAME__Group_1__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:907:1: ( '.' ) rule__QUALIFIED_NAME__Group_1__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:907:1: ( '.' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:908:1: '.'
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            match(input,42,FOLLOW_42_in_rule__QUALIFIED_NAME__Group_1__01951); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 

            }

            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__01961);
            rule__QUALIFIED_NAME__Group_1__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__QUALIFIED_NAME__Group_1__0


    // $ANTLR start rule__QUALIFIED_NAME__Group_1__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:922:1: rule__QUALIFIED_NAME__Group_1__1 : ( RULE_ID ) ;
    public final void rule__QUALIFIED_NAME__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:926:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:927:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:927:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:928:1: RULE_ID
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__11989); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__QUALIFIED_NAME__Group_1__1


    // $ANTLR start rule__ArtifactType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:943:1: rule__ArtifactType__Group__0 : ( ( 'abstract' )? ) rule__ArtifactType__Group__1 ;
    public final void rule__ArtifactType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:947:1: ( ( ( 'abstract' )? ) rule__ArtifactType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:948:1: ( ( 'abstract' )? ) rule__ArtifactType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:948:1: ( ( 'abstract' )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:949:1: ( 'abstract' )?
            {
             before(grammarAccess.getArtifactTypeAccess().getAbstractKeyword_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:950:1: ( 'abstract' )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==43) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:951:2: 'abstract'
                    {
                    match(input,43,FOLLOW_43_in_rule__ArtifactType__Group__02028); 

                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getAbstractKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__1_in_rule__ArtifactType__Group__02040);
            rule__ArtifactType__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group__0


    // $ANTLR start rule__ArtifactType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:963:1: rule__ArtifactType__Group__1 : ( 'artifactType' ) rule__ArtifactType__Group__2 ;
    public final void rule__ArtifactType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:967:1: ( ( 'artifactType' ) rule__ArtifactType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:968:1: ( 'artifactType' ) rule__ArtifactType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:968:1: ( 'artifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:969:1: 'artifactType'
            {
             before(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1()); 
            match(input,44,FOLLOW_44_in_rule__ArtifactType__Group__12069); 
             after(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__2_in_rule__ArtifactType__Group__12079);
            rule__ArtifactType__Group__2();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group__1


    // $ANTLR start rule__ArtifactType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:983:1: rule__ArtifactType__Group__2 : ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3 ;
    public final void rule__ArtifactType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:987:1: ( ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:988:1: ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:988:1: ( ( rule__ArtifactType__NameAssignment_2 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:989:1: ( rule__ArtifactType__NameAssignment_2 )
            {
             before(grammarAccess.getArtifactTypeAccess().getNameAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:990:1: ( rule__ArtifactType__NameAssignment_2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:990:2: rule__ArtifactType__NameAssignment_2
            {
            pushFollow(FOLLOW_rule__ArtifactType__NameAssignment_2_in_rule__ArtifactType__Group__22107);
            rule__ArtifactType__NameAssignment_2();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getNameAssignment_2()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__3_in_rule__ArtifactType__Group__22116);
            rule__ArtifactType__Group__3();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group__2


    // $ANTLR start rule__ArtifactType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1001:1: rule__ArtifactType__Group__3 : ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4 ;
    public final void rule__ArtifactType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1005:1: ( ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1006:1: ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1006:1: ( ( rule__ArtifactType__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1007:1: ( rule__ArtifactType__Group_3__0 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1008:1: ( rule__ArtifactType__Group_3__0 )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==47) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1008:2: rule__ArtifactType__Group_3__0
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__Group_3__0_in_rule__ArtifactType__Group__32144);
                    rule__ArtifactType__Group_3__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getGroup_3()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__4_in_rule__ArtifactType__Group__32154);
            rule__ArtifactType__Group__4();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group__3


    // $ANTLR start rule__ArtifactType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1019:1: rule__ArtifactType__Group__4 : ( '{' ) rule__ArtifactType__Group__5 ;
    public final void rule__ArtifactType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1023:1: ( ( '{' ) rule__ArtifactType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1024:1: ( '{' ) rule__ArtifactType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1024:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1025:1: '{'
            {
             before(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_4()); 
            match(input,45,FOLLOW_45_in_rule__ArtifactType__Group__42183); 
             after(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_4()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__5_in_rule__ArtifactType__Group__42193);
            rule__ArtifactType__Group__5();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group__4


    // $ANTLR start rule__ArtifactType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1039:1: rule__ArtifactType__Group__5 : ( ( rule__ArtifactType__ValidAttributeTypesAssignment_5 )* ) rule__ArtifactType__Group__6 ;
    public final void rule__ArtifactType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1043:1: ( ( ( rule__ArtifactType__ValidAttributeTypesAssignment_5 )* ) rule__ArtifactType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1044:1: ( ( rule__ArtifactType__ValidAttributeTypesAssignment_5 )* ) rule__ArtifactType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1044:1: ( ( rule__ArtifactType__ValidAttributeTypesAssignment_5 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1045:1: ( rule__ArtifactType__ValidAttributeTypesAssignment_5 )*
            {
             before(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAssignment_5()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1046:1: ( rule__ArtifactType__ValidAttributeTypesAssignment_5 )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==48) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1046:2: rule__ArtifactType__ValidAttributeTypesAssignment_5
            	    {
            	    pushFollow(FOLLOW_rule__ArtifactType__ValidAttributeTypesAssignment_5_in_rule__ArtifactType__Group__52221);
            	    rule__ArtifactType__ValidAttributeTypesAssignment_5();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

             after(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAssignment_5()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__6_in_rule__ArtifactType__Group__52231);
            rule__ArtifactType__Group__6();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group__5


    // $ANTLR start rule__ArtifactType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1057:1: rule__ArtifactType__Group__6 : ( '}' ) ;
    public final void rule__ArtifactType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1061:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1062:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1062:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1063:1: '}'
            {
             before(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_6()); 
            match(input,46,FOLLOW_46_in_rule__ArtifactType__Group__62260); 
             after(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group__6


    // $ANTLR start rule__ArtifactType__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1090:1: rule__ArtifactType__Group_3__0 : ( 'extends' ) rule__ArtifactType__Group_3__1 ;
    public final void rule__ArtifactType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1094:1: ( ( 'extends' ) rule__ArtifactType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1095:1: ( 'extends' ) rule__ArtifactType__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1095:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1096:1: 'extends'
            {
             before(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0()); 
            match(input,47,FOLLOW_47_in_rule__ArtifactType__Group_3__02310); 
             after(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_3__1_in_rule__ArtifactType__Group_3__02320);
            rule__ArtifactType__Group_3__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group_3__0


    // $ANTLR start rule__ArtifactType__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1110:1: rule__ArtifactType__Group_3__1 : ( ( rule__ArtifactType__SuperArtifactTypeAssignment_3_1 ) ) ;
    public final void rule__ArtifactType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1114:1: ( ( ( rule__ArtifactType__SuperArtifactTypeAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1115:1: ( ( rule__ArtifactType__SuperArtifactTypeAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1115:1: ( ( rule__ArtifactType__SuperArtifactTypeAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1116:1: ( rule__ArtifactType__SuperArtifactTypeAssignment_3_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypeAssignment_3_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1117:1: ( rule__ArtifactType__SuperArtifactTypeAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1117:2: rule__ArtifactType__SuperArtifactTypeAssignment_3_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__SuperArtifactTypeAssignment_3_1_in_rule__ArtifactType__Group_3__12348);
            rule__ArtifactType__SuperArtifactTypeAssignment_3_1();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypeAssignment_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group_3__1


    // $ANTLR start rule__AttributeTypeRef__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1131:1: rule__AttributeTypeRef__Group__0 : ( 'attribute' ) rule__AttributeTypeRef__Group__1 ;
    public final void rule__AttributeTypeRef__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1135:1: ( ( 'attribute' ) rule__AttributeTypeRef__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1136:1: ( 'attribute' ) rule__AttributeTypeRef__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1136:1: ( 'attribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1137:1: 'attribute'
            {
             before(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0()); 
            match(input,48,FOLLOW_48_in_rule__AttributeTypeRef__Group__02387); 
             after(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__1_in_rule__AttributeTypeRef__Group__02397);
            rule__AttributeTypeRef__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeTypeRef__Group__0


    // $ANTLR start rule__AttributeTypeRef__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1151:1: rule__AttributeTypeRef__Group__1 : ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) ;
    public final void rule__AttributeTypeRef__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1155:1: ( ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1156:1: ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1156:1: ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1157:1: ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1158:1: ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1158:2: rule__AttributeTypeRef__ValidAttributeTypeAssignment_1
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__AttributeTypeRef__Group__12425);
            rule__AttributeTypeRef__ValidAttributeTypeAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeTypeRef__Group__1


    // $ANTLR start rule__AttributeType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1172:1: rule__AttributeType__Group__0 : ( 'attributeType' ) rule__AttributeType__Group__1 ;
    public final void rule__AttributeType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1176:1: ( ( 'attributeType' ) rule__AttributeType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1177:1: ( 'attributeType' ) rule__AttributeType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1177:1: ( 'attributeType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1178:1: 'attributeType'
            {
             before(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_0()); 
            match(input,49,FOLLOW_49_in_rule__AttributeType__Group__02464); 
             after(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__1_in_rule__AttributeType__Group__02474);
            rule__AttributeType__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__0


    // $ANTLR start rule__AttributeType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1192:1: rule__AttributeType__Group__1 : ( ( rule__AttributeType__NameAssignment_1 ) ) rule__AttributeType__Group__2 ;
    public final void rule__AttributeType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1196:1: ( ( ( rule__AttributeType__NameAssignment_1 ) ) rule__AttributeType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1197:1: ( ( rule__AttributeType__NameAssignment_1 ) ) rule__AttributeType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1197:1: ( ( rule__AttributeType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1198:1: ( rule__AttributeType__NameAssignment_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1199:1: ( rule__AttributeType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1199:2: rule__AttributeType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__AttributeType__NameAssignment_1_in_rule__AttributeType__Group__12502);
            rule__AttributeType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__2_in_rule__AttributeType__Group__12511);
            rule__AttributeType__Group__2();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__1


    // $ANTLR start rule__AttributeType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1210:1: rule__AttributeType__Group__2 : ( ( rule__AttributeType__Group_2__0 ) ) rule__AttributeType__Group__3 ;
    public final void rule__AttributeType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1214:1: ( ( ( rule__AttributeType__Group_2__0 ) ) rule__AttributeType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1215:1: ( ( rule__AttributeType__Group_2__0 ) ) rule__AttributeType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1215:1: ( ( rule__AttributeType__Group_2__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1216:1: ( rule__AttributeType__Group_2__0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1217:1: ( rule__AttributeType__Group_2__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1217:2: rule__AttributeType__Group_2__0
            {
            pushFollow(FOLLOW_rule__AttributeType__Group_2__0_in_rule__AttributeType__Group__22539);
            rule__AttributeType__Group_2__0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_2()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__3_in_rule__AttributeType__Group__22548);
            rule__AttributeType__Group__3();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__2


    // $ANTLR start rule__AttributeType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1228:1: rule__AttributeType__Group__3 : ( '{' ) rule__AttributeType__Group__4 ;
    public final void rule__AttributeType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1232:1: ( ( '{' ) rule__AttributeType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1233:1: ( '{' ) rule__AttributeType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1233:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1234:1: '{'
            {
             before(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_3()); 
            match(input,45,FOLLOW_45_in_rule__AttributeType__Group__32577); 
             after(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_3()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__4_in_rule__AttributeType__Group__32587);
            rule__AttributeType__Group__4();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__3


    // $ANTLR start rule__AttributeType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1248:1: rule__AttributeType__Group__4 : ( 'dataProvider' ) rule__AttributeType__Group__5 ;
    public final void rule__AttributeType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1252:1: ( ( 'dataProvider' ) rule__AttributeType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1253:1: ( 'dataProvider' ) rule__AttributeType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1253:1: ( 'dataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1254:1: 'dataProvider'
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_4()); 
            match(input,50,FOLLOW_50_in_rule__AttributeType__Group__42616); 
             after(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_4()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__5_in_rule__AttributeType__Group__42626);
            rule__AttributeType__Group__5();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__4


    // $ANTLR start rule__AttributeType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1268:1: rule__AttributeType__Group__5 : ( ( rule__AttributeType__DataProviderAssignment_5 ) ) rule__AttributeType__Group__6 ;
    public final void rule__AttributeType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1272:1: ( ( ( rule__AttributeType__DataProviderAssignment_5 ) ) rule__AttributeType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1273:1: ( ( rule__AttributeType__DataProviderAssignment_5 ) ) rule__AttributeType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1273:1: ( ( rule__AttributeType__DataProviderAssignment_5 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1274:1: ( rule__AttributeType__DataProviderAssignment_5 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderAssignment_5()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1275:1: ( rule__AttributeType__DataProviderAssignment_5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1275:2: rule__AttributeType__DataProviderAssignment_5
            {
            pushFollow(FOLLOW_rule__AttributeType__DataProviderAssignment_5_in_rule__AttributeType__Group__52654);
            rule__AttributeType__DataProviderAssignment_5();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDataProviderAssignment_5()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__6_in_rule__AttributeType__Group__52663);
            rule__AttributeType__Group__6();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__5


    // $ANTLR start rule__AttributeType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1286:1: rule__AttributeType__Group__6 : ( 'min' ) rule__AttributeType__Group__7 ;
    public final void rule__AttributeType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1290:1: ( ( 'min' ) rule__AttributeType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1291:1: ( 'min' ) rule__AttributeType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1291:1: ( 'min' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1292:1: 'min'
            {
             before(grammarAccess.getAttributeTypeAccess().getMinKeyword_6()); 
            match(input,51,FOLLOW_51_in_rule__AttributeType__Group__62692); 
             after(grammarAccess.getAttributeTypeAccess().getMinKeyword_6()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__7_in_rule__AttributeType__Group__62702);
            rule__AttributeType__Group__7();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__6


    // $ANTLR start rule__AttributeType__Group__7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1306:1: rule__AttributeType__Group__7 : ( ( rule__AttributeType__MinAssignment_7 ) ) rule__AttributeType__Group__8 ;
    public final void rule__AttributeType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1310:1: ( ( ( rule__AttributeType__MinAssignment_7 ) ) rule__AttributeType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1311:1: ( ( rule__AttributeType__MinAssignment_7 ) ) rule__AttributeType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1311:1: ( ( rule__AttributeType__MinAssignment_7 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1312:1: ( rule__AttributeType__MinAssignment_7 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMinAssignment_7()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1313:1: ( rule__AttributeType__MinAssignment_7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1313:2: rule__AttributeType__MinAssignment_7
            {
            pushFollow(FOLLOW_rule__AttributeType__MinAssignment_7_in_rule__AttributeType__Group__72730);
            rule__AttributeType__MinAssignment_7();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMinAssignment_7()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__8_in_rule__AttributeType__Group__72739);
            rule__AttributeType__Group__8();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__7


    // $ANTLR start rule__AttributeType__Group__8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1324:1: rule__AttributeType__Group__8 : ( 'max' ) rule__AttributeType__Group__9 ;
    public final void rule__AttributeType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1328:1: ( ( 'max' ) rule__AttributeType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1329:1: ( 'max' ) rule__AttributeType__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1329:1: ( 'max' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1330:1: 'max'
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxKeyword_8()); 
            match(input,52,FOLLOW_52_in_rule__AttributeType__Group__82768); 
             after(grammarAccess.getAttributeTypeAccess().getMaxKeyword_8()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__9_in_rule__AttributeType__Group__82778);
            rule__AttributeType__Group__9();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__8


    // $ANTLR start rule__AttributeType__Group__9
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1344:1: rule__AttributeType__Group__9 : ( ( rule__AttributeType__MaxAssignment_9 ) ) rule__AttributeType__Group__10 ;
    public final void rule__AttributeType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1348:1: ( ( ( rule__AttributeType__MaxAssignment_9 ) ) rule__AttributeType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1349:1: ( ( rule__AttributeType__MaxAssignment_9 ) ) rule__AttributeType__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1349:1: ( ( rule__AttributeType__MaxAssignment_9 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1350:1: ( rule__AttributeType__MaxAssignment_9 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxAssignment_9()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1351:1: ( rule__AttributeType__MaxAssignment_9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1351:2: rule__AttributeType__MaxAssignment_9
            {
            pushFollow(FOLLOW_rule__AttributeType__MaxAssignment_9_in_rule__AttributeType__Group__92806);
            rule__AttributeType__MaxAssignment_9();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMaxAssignment_9()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__10_in_rule__AttributeType__Group__92815);
            rule__AttributeType__Group__10();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__9


    // $ANTLR start rule__AttributeType__Group__10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1362:1: rule__AttributeType__Group__10 : ( ( rule__AttributeType__Group_10__0 )? ) rule__AttributeType__Group__11 ;
    public final void rule__AttributeType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1366:1: ( ( ( rule__AttributeType__Group_10__0 )? ) rule__AttributeType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1367:1: ( ( rule__AttributeType__Group_10__0 )? ) rule__AttributeType__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1367:1: ( ( rule__AttributeType__Group_10__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1368:1: ( rule__AttributeType__Group_10__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_10()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1369:1: ( rule__AttributeType__Group_10__0 )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==53) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1369:2: rule__AttributeType__Group_10__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_10__0_in_rule__AttributeType__Group__102843);
                    rule__AttributeType__Group_10__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_10()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__11_in_rule__AttributeType__Group__102853);
            rule__AttributeType__Group__11();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__10


    // $ANTLR start rule__AttributeType__Group__11
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1380:1: rule__AttributeType__Group__11 : ( ( rule__AttributeType__Group_11__0 )? ) rule__AttributeType__Group__12 ;
    public final void rule__AttributeType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1384:1: ( ( ( rule__AttributeType__Group_11__0 )? ) rule__AttributeType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1385:1: ( ( rule__AttributeType__Group_11__0 )? ) rule__AttributeType__Group__12
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1385:1: ( ( rule__AttributeType__Group_11__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1386:1: ( rule__AttributeType__Group_11__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_11()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1387:1: ( rule__AttributeType__Group_11__0 )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==54) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1387:2: rule__AttributeType__Group_11__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_11__0_in_rule__AttributeType__Group__112881);
                    rule__AttributeType__Group_11__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_11()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__12_in_rule__AttributeType__Group__112891);
            rule__AttributeType__Group__12();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__11


    // $ANTLR start rule__AttributeType__Group__12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1398:1: rule__AttributeType__Group__12 : ( ( rule__AttributeType__Group_12__0 )? ) rule__AttributeType__Group__13 ;
    public final void rule__AttributeType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1402:1: ( ( ( rule__AttributeType__Group_12__0 )? ) rule__AttributeType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1403:1: ( ( rule__AttributeType__Group_12__0 )? ) rule__AttributeType__Group__13
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1403:1: ( ( rule__AttributeType__Group_12__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1404:1: ( rule__AttributeType__Group_12__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_12()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1405:1: ( rule__AttributeType__Group_12__0 )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==55) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1405:2: rule__AttributeType__Group_12__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_12__0_in_rule__AttributeType__Group__122919);
                    rule__AttributeType__Group_12__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_12()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__13_in_rule__AttributeType__Group__122929);
            rule__AttributeType__Group__13();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__12


    // $ANTLR start rule__AttributeType__Group__13
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1416:1: rule__AttributeType__Group__13 : ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14 ;
    public final void rule__AttributeType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1420:1: ( ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1421:1: ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1421:1: ( ( rule__AttributeType__Group_13__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1422:1: ( rule__AttributeType__Group_13__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_13()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1423:1: ( rule__AttributeType__Group_13__0 )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==56) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1423:2: rule__AttributeType__Group_13__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_13__0_in_rule__AttributeType__Group__132957);
                    rule__AttributeType__Group_13__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_13()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__14_in_rule__AttributeType__Group__132967);
            rule__AttributeType__Group__14();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__13


    // $ANTLR start rule__AttributeType__Group__14
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1434:1: rule__AttributeType__Group__14 : ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15 ;
    public final void rule__AttributeType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1438:1: ( ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1439:1: ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1439:1: ( ( rule__AttributeType__Group_14__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1440:1: ( rule__AttributeType__Group_14__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_14()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1441:1: ( rule__AttributeType__Group_14__0 )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==57) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1441:2: rule__AttributeType__Group_14__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_14__0_in_rule__AttributeType__Group__142995);
                    rule__AttributeType__Group_14__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_14()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__15_in_rule__AttributeType__Group__143005);
            rule__AttributeType__Group__15();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__14


    // $ANTLR start rule__AttributeType__Group__15
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1452:1: rule__AttributeType__Group__15 : ( '}' ) ;
    public final void rule__AttributeType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1456:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1457:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1457:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1458:1: '}'
            {
             before(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_15()); 
            match(input,46,FOLLOW_46_in_rule__AttributeType__Group__153034); 
             after(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_15()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group__15


    // $ANTLR start rule__AttributeType__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1503:1: rule__AttributeType__Group_2__0 : ( 'extends' ) rule__AttributeType__Group_2__1 ;
    public final void rule__AttributeType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1507:1: ( ( 'extends' ) rule__AttributeType__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1508:1: ( 'extends' ) rule__AttributeType__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1508:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1509:1: 'extends'
            {
             before(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_2_0()); 
            match(input,47,FOLLOW_47_in_rule__AttributeType__Group_2__03102); 
             after(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_2__1_in_rule__AttributeType__Group_2__03112);
            rule__AttributeType__Group_2__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_2__0


    // $ANTLR start rule__AttributeType__Group_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1523:1: rule__AttributeType__Group_2__1 : ( ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 ) ) ;
    public final void rule__AttributeType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1527:1: ( ( ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1528:1: ( ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1528:1: ( ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1529:1: ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1530:1: ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1530:2: rule__AttributeType__BaseAttributeTypeAssignment_2_1
            {
            pushFollow(FOLLOW_rule__AttributeType__BaseAttributeTypeAssignment_2_1_in_rule__AttributeType__Group_2__13140);
            rule__AttributeType__BaseAttributeTypeAssignment_2_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAssignment_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_2__1


    // $ANTLR start rule__AttributeType__Group_10__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1544:1: rule__AttributeType__Group_10__0 : ( 'taggerId' ) rule__AttributeType__Group_10__1 ;
    public final void rule__AttributeType__Group_10__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1548:1: ( ( 'taggerId' ) rule__AttributeType__Group_10__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1549:1: ( 'taggerId' ) rule__AttributeType__Group_10__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1549:1: ( 'taggerId' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1550:1: 'taggerId'
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_10_0()); 
            match(input,53,FOLLOW_53_in_rule__AttributeType__Group_10__03179); 
             after(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_10_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_10__1_in_rule__AttributeType__Group_10__03189);
            rule__AttributeType__Group_10__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_10__0


    // $ANTLR start rule__AttributeType__Group_10__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1564:1: rule__AttributeType__Group_10__1 : ( ( rule__AttributeType__TaggerIdAssignment_10_1 ) ) ;
    public final void rule__AttributeType__Group_10__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1568:1: ( ( ( rule__AttributeType__TaggerIdAssignment_10_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1569:1: ( ( rule__AttributeType__TaggerIdAssignment_10_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1569:1: ( ( rule__AttributeType__TaggerIdAssignment_10_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1570:1: ( rule__AttributeType__TaggerIdAssignment_10_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdAssignment_10_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1571:1: ( rule__AttributeType__TaggerIdAssignment_10_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1571:2: rule__AttributeType__TaggerIdAssignment_10_1
            {
            pushFollow(FOLLOW_rule__AttributeType__TaggerIdAssignment_10_1_in_rule__AttributeType__Group_10__13217);
            rule__AttributeType__TaggerIdAssignment_10_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getTaggerIdAssignment_10_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_10__1


    // $ANTLR start rule__AttributeType__Group_11__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1585:1: rule__AttributeType__Group_11__0 : ( 'enumType' ) rule__AttributeType__Group_11__1 ;
    public final void rule__AttributeType__Group_11__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1589:1: ( ( 'enumType' ) rule__AttributeType__Group_11__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1590:1: ( 'enumType' ) rule__AttributeType__Group_11__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1590:1: ( 'enumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1591:1: 'enumType'
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_11_0()); 
            match(input,54,FOLLOW_54_in_rule__AttributeType__Group_11__03256); 
             after(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_11_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_11__1_in_rule__AttributeType__Group_11__03266);
            rule__AttributeType__Group_11__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_11__0


    // $ANTLR start rule__AttributeType__Group_11__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1605:1: rule__AttributeType__Group_11__1 : ( ( rule__AttributeType__EnumTypeAssignment_11_1 ) ) ;
    public final void rule__AttributeType__Group_11__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1609:1: ( ( ( rule__AttributeType__EnumTypeAssignment_11_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1610:1: ( ( rule__AttributeType__EnumTypeAssignment_11_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1610:1: ( ( rule__AttributeType__EnumTypeAssignment_11_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1611:1: ( rule__AttributeType__EnumTypeAssignment_11_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeAssignment_11_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1612:1: ( rule__AttributeType__EnumTypeAssignment_11_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1612:2: rule__AttributeType__EnumTypeAssignment_11_1
            {
            pushFollow(FOLLOW_rule__AttributeType__EnumTypeAssignment_11_1_in_rule__AttributeType__Group_11__13294);
            rule__AttributeType__EnumTypeAssignment_11_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getEnumTypeAssignment_11_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_11__1


    // $ANTLR start rule__AttributeType__Group_12__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1626:1: rule__AttributeType__Group_12__0 : ( 'description' ) rule__AttributeType__Group_12__1 ;
    public final void rule__AttributeType__Group_12__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1630:1: ( ( 'description' ) rule__AttributeType__Group_12__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1631:1: ( 'description' ) rule__AttributeType__Group_12__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1631:1: ( 'description' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1632:1: 'description'
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_12_0()); 
            match(input,55,FOLLOW_55_in_rule__AttributeType__Group_12__03333); 
             after(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_12_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_12__1_in_rule__AttributeType__Group_12__03343);
            rule__AttributeType__Group_12__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_12__0


    // $ANTLR start rule__AttributeType__Group_12__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1646:1: rule__AttributeType__Group_12__1 : ( ( rule__AttributeType__DescriptionAssignment_12_1 ) ) ;
    public final void rule__AttributeType__Group_12__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1650:1: ( ( ( rule__AttributeType__DescriptionAssignment_12_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1651:1: ( ( rule__AttributeType__DescriptionAssignment_12_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1651:1: ( ( rule__AttributeType__DescriptionAssignment_12_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1652:1: ( rule__AttributeType__DescriptionAssignment_12_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionAssignment_12_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1653:1: ( rule__AttributeType__DescriptionAssignment_12_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1653:2: rule__AttributeType__DescriptionAssignment_12_1
            {
            pushFollow(FOLLOW_rule__AttributeType__DescriptionAssignment_12_1_in_rule__AttributeType__Group_12__13371);
            rule__AttributeType__DescriptionAssignment_12_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDescriptionAssignment_12_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_12__1


    // $ANTLR start rule__AttributeType__Group_13__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1667:1: rule__AttributeType__Group_13__0 : ( 'defaultValue' ) rule__AttributeType__Group_13__1 ;
    public final void rule__AttributeType__Group_13__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1671:1: ( ( 'defaultValue' ) rule__AttributeType__Group_13__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1672:1: ( 'defaultValue' ) rule__AttributeType__Group_13__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1672:1: ( 'defaultValue' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1673:1: 'defaultValue'
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_13_0()); 
            match(input,56,FOLLOW_56_in_rule__AttributeType__Group_13__03410); 
             after(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_13_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_13__1_in_rule__AttributeType__Group_13__03420);
            rule__AttributeType__Group_13__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_13__0


    // $ANTLR start rule__AttributeType__Group_13__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1687:1: rule__AttributeType__Group_13__1 : ( ( rule__AttributeType__DefaultValueAssignment_13_1 ) ) ;
    public final void rule__AttributeType__Group_13__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1691:1: ( ( ( rule__AttributeType__DefaultValueAssignment_13_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1692:1: ( ( rule__AttributeType__DefaultValueAssignment_13_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1692:1: ( ( rule__AttributeType__DefaultValueAssignment_13_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1693:1: ( rule__AttributeType__DefaultValueAssignment_13_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueAssignment_13_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1694:1: ( rule__AttributeType__DefaultValueAssignment_13_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1694:2: rule__AttributeType__DefaultValueAssignment_13_1
            {
            pushFollow(FOLLOW_rule__AttributeType__DefaultValueAssignment_13_1_in_rule__AttributeType__Group_13__13448);
            rule__AttributeType__DefaultValueAssignment_13_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDefaultValueAssignment_13_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_13__1


    // $ANTLR start rule__AttributeType__Group_14__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1708:1: rule__AttributeType__Group_14__0 : ( 'fileExtension' ) rule__AttributeType__Group_14__1 ;
    public final void rule__AttributeType__Group_14__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1712:1: ( ( 'fileExtension' ) rule__AttributeType__Group_14__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1713:1: ( 'fileExtension' ) rule__AttributeType__Group_14__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1713:1: ( 'fileExtension' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1714:1: 'fileExtension'
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_14_0()); 
            match(input,57,FOLLOW_57_in_rule__AttributeType__Group_14__03487); 
             after(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_14_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_14__1_in_rule__AttributeType__Group_14__03497);
            rule__AttributeType__Group_14__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_14__0


    // $ANTLR start rule__AttributeType__Group_14__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1728:1: rule__AttributeType__Group_14__1 : ( ( rule__AttributeType__FileExtensionAssignment_14_1 ) ) ;
    public final void rule__AttributeType__Group_14__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1732:1: ( ( ( rule__AttributeType__FileExtensionAssignment_14_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1733:1: ( ( rule__AttributeType__FileExtensionAssignment_14_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1733:1: ( ( rule__AttributeType__FileExtensionAssignment_14_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1734:1: ( rule__AttributeType__FileExtensionAssignment_14_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionAssignment_14_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1735:1: ( rule__AttributeType__FileExtensionAssignment_14_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1735:2: rule__AttributeType__FileExtensionAssignment_14_1
            {
            pushFollow(FOLLOW_rule__AttributeType__FileExtensionAssignment_14_1_in_rule__AttributeType__Group_14__13525);
            rule__AttributeType__FileExtensionAssignment_14_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getFileExtensionAssignment_14_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__Group_14__1


    // $ANTLR start rule__OseeEnumType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1749:1: rule__OseeEnumType__Group__0 : ( 'oseeEnumType' ) rule__OseeEnumType__Group__1 ;
    public final void rule__OseeEnumType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1753:1: ( ( 'oseeEnumType' ) rule__OseeEnumType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1754:1: ( 'oseeEnumType' ) rule__OseeEnumType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1754:1: ( 'oseeEnumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1755:1: 'oseeEnumType'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 
            match(input,58,FOLLOW_58_in_rule__OseeEnumType__Group__03564); 
             after(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__1_in_rule__OseeEnumType__Group__03574);
            rule__OseeEnumType__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumType__Group__0


    // $ANTLR start rule__OseeEnumType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1769:1: rule__OseeEnumType__Group__1 : ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2 ;
    public final void rule__OseeEnumType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1773:1: ( ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1774:1: ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1774:1: ( ( rule__OseeEnumType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1775:1: ( rule__OseeEnumType__NameAssignment_1 )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1776:1: ( rule__OseeEnumType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1776:2: rule__OseeEnumType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__OseeEnumType__NameAssignment_1_in_rule__OseeEnumType__Group__13602);
            rule__OseeEnumType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__2_in_rule__OseeEnumType__Group__13611);
            rule__OseeEnumType__Group__2();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumType__Group__1


    // $ANTLR start rule__OseeEnumType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1787:1: rule__OseeEnumType__Group__2 : ( '{' ) rule__OseeEnumType__Group__3 ;
    public final void rule__OseeEnumType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1791:1: ( ( '{' ) rule__OseeEnumType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1792:1: ( '{' ) rule__OseeEnumType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1792:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1793:1: '{'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,45,FOLLOW_45_in_rule__OseeEnumType__Group__23640); 
             after(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__3_in_rule__OseeEnumType__Group__23650);
            rule__OseeEnumType__Group__3();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumType__Group__2


    // $ANTLR start rule__OseeEnumType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1807:1: rule__OseeEnumType__Group__3 : ( ( rule__OseeEnumType__EnumEntriesAssignment_3 )* ) rule__OseeEnumType__Group__4 ;
    public final void rule__OseeEnumType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1811:1: ( ( ( rule__OseeEnumType__EnumEntriesAssignment_3 )* ) rule__OseeEnumType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1812:1: ( ( rule__OseeEnumType__EnumEntriesAssignment_3 )* ) rule__OseeEnumType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1812:1: ( ( rule__OseeEnumType__EnumEntriesAssignment_3 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1813:1: ( rule__OseeEnumType__EnumEntriesAssignment_3 )*
            {
             before(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesAssignment_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1814:1: ( rule__OseeEnumType__EnumEntriesAssignment_3 )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==59) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1814:2: rule__OseeEnumType__EnumEntriesAssignment_3
            	    {
            	    pushFollow(FOLLOW_rule__OseeEnumType__EnumEntriesAssignment_3_in_rule__OseeEnumType__Group__33678);
            	    rule__OseeEnumType__EnumEntriesAssignment_3();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

             after(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesAssignment_3()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__4_in_rule__OseeEnumType__Group__33688);
            rule__OseeEnumType__Group__4();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumType__Group__3


    // $ANTLR start rule__OseeEnumType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1825:1: rule__OseeEnumType__Group__4 : ( '}' ) ;
    public final void rule__OseeEnumType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1829:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1830:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1830:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1831:1: '}'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getRightCurlyBracketKeyword_4()); 
            match(input,46,FOLLOW_46_in_rule__OseeEnumType__Group__43717); 
             after(grammarAccess.getOseeEnumTypeAccess().getRightCurlyBracketKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumType__Group__4


    // $ANTLR start rule__OseeEnumEntry__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1854:1: rule__OseeEnumEntry__Group__0 : ( 'entry' ) rule__OseeEnumEntry__Group__1 ;
    public final void rule__OseeEnumEntry__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1858:1: ( ( 'entry' ) rule__OseeEnumEntry__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1859:1: ( 'entry' ) rule__OseeEnumEntry__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1859:1: ( 'entry' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1860:1: 'entry'
            {
             before(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0()); 
            match(input,59,FOLLOW_59_in_rule__OseeEnumEntry__Group__03763); 
             after(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__1_in_rule__OseeEnumEntry__Group__03773);
            rule__OseeEnumEntry__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumEntry__Group__0


    // $ANTLR start rule__OseeEnumEntry__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1874:1: rule__OseeEnumEntry__Group__1 : ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2 ;
    public final void rule__OseeEnumEntry__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1878:1: ( ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1879:1: ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1879:1: ( ( rule__OseeEnumEntry__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1880:1: ( rule__OseeEnumEntry__NameAssignment_1 )
            {
             before(grammarAccess.getOseeEnumEntryAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1881:1: ( rule__OseeEnumEntry__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1881:2: rule__OseeEnumEntry__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__OseeEnumEntry__NameAssignment_1_in_rule__OseeEnumEntry__Group__13801);
            rule__OseeEnumEntry__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumEntryAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__2_in_rule__OseeEnumEntry__Group__13810);
            rule__OseeEnumEntry__Group__2();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumEntry__Group__1


    // $ANTLR start rule__OseeEnumEntry__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1892:1: rule__OseeEnumEntry__Group__2 : ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? ) ;
    public final void rule__OseeEnumEntry__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1896:1: ( ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1897:1: ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1897:1: ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1898:1: ( rule__OseeEnumEntry__OrdinalAssignment_2 )?
            {
             before(grammarAccess.getOseeEnumEntryAccess().getOrdinalAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1899:1: ( rule__OseeEnumEntry__OrdinalAssignment_2 )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( ((LA22_0>=11 && LA22_0<=20)) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1899:2: rule__OseeEnumEntry__OrdinalAssignment_2
                    {
                    pushFollow(FOLLOW_rule__OseeEnumEntry__OrdinalAssignment_2_in_rule__OseeEnumEntry__Group__23838);
                    rule__OseeEnumEntry__OrdinalAssignment_2();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOseeEnumEntryAccess().getOrdinalAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumEntry__Group__2


    // $ANTLR start rule__RelationType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1915:1: rule__RelationType__Group__0 : ( 'relationType' ) rule__RelationType__Group__1 ;
    public final void rule__RelationType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1919:1: ( ( 'relationType' ) rule__RelationType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1920:1: ( 'relationType' ) rule__RelationType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1920:1: ( 'relationType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1921:1: 'relationType'
            {
             before(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0()); 
            match(input,60,FOLLOW_60_in_rule__RelationType__Group__03880); 
             after(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__1_in_rule__RelationType__Group__03890);
            rule__RelationType__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__0


    // $ANTLR start rule__RelationType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1935:1: rule__RelationType__Group__1 : ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2 ;
    public final void rule__RelationType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1939:1: ( ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1940:1: ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1940:1: ( ( rule__RelationType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1941:1: ( rule__RelationType__NameAssignment_1 )
            {
             before(grammarAccess.getRelationTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1942:1: ( rule__RelationType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1942:2: rule__RelationType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__RelationType__NameAssignment_1_in_rule__RelationType__Group__13918);
            rule__RelationType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__2_in_rule__RelationType__Group__13927);
            rule__RelationType__Group__2();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__1


    // $ANTLR start rule__RelationType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1953:1: rule__RelationType__Group__2 : ( '{' ) rule__RelationType__Group__3 ;
    public final void rule__RelationType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1957:1: ( ( '{' ) rule__RelationType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1958:1: ( '{' ) rule__RelationType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1958:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1959:1: '{'
            {
             before(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,45,FOLLOW_45_in_rule__RelationType__Group__23956); 
             after(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__3_in_rule__RelationType__Group__23966);
            rule__RelationType__Group__3();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__2


    // $ANTLR start rule__RelationType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1973:1: rule__RelationType__Group__3 : ( 'sideAName' ) rule__RelationType__Group__4 ;
    public final void rule__RelationType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1977:1: ( ( 'sideAName' ) rule__RelationType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1978:1: ( 'sideAName' ) rule__RelationType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1978:1: ( 'sideAName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1979:1: 'sideAName'
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameKeyword_3()); 
            match(input,61,FOLLOW_61_in_rule__RelationType__Group__33995); 
             after(grammarAccess.getRelationTypeAccess().getSideANameKeyword_3()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__4_in_rule__RelationType__Group__34005);
            rule__RelationType__Group__4();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__3


    // $ANTLR start rule__RelationType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1993:1: rule__RelationType__Group__4 : ( ( rule__RelationType__SideANameAssignment_4 ) ) rule__RelationType__Group__5 ;
    public final void rule__RelationType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1997:1: ( ( ( rule__RelationType__SideANameAssignment_4 ) ) rule__RelationType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1998:1: ( ( rule__RelationType__SideANameAssignment_4 ) ) rule__RelationType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1998:1: ( ( rule__RelationType__SideANameAssignment_4 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1999:1: ( rule__RelationType__SideANameAssignment_4 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameAssignment_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2000:1: ( rule__RelationType__SideANameAssignment_4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2000:2: rule__RelationType__SideANameAssignment_4
            {
            pushFollow(FOLLOW_rule__RelationType__SideANameAssignment_4_in_rule__RelationType__Group__44033);
            rule__RelationType__SideANameAssignment_4();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideANameAssignment_4()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__5_in_rule__RelationType__Group__44042);
            rule__RelationType__Group__5();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__4


    // $ANTLR start rule__RelationType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2011:1: rule__RelationType__Group__5 : ( 'sideAArtifactType' ) rule__RelationType__Group__6 ;
    public final void rule__RelationType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2015:1: ( ( 'sideAArtifactType' ) rule__RelationType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2016:1: ( 'sideAArtifactType' ) rule__RelationType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2016:1: ( 'sideAArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2017:1: 'sideAArtifactType'
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_5()); 
            match(input,62,FOLLOW_62_in_rule__RelationType__Group__54071); 
             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_5()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__6_in_rule__RelationType__Group__54081);
            rule__RelationType__Group__6();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__5


    // $ANTLR start rule__RelationType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2031:1: rule__RelationType__Group__6 : ( ( rule__RelationType__SideAArtifactTypeAssignment_6 ) ) rule__RelationType__Group__7 ;
    public final void rule__RelationType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2035:1: ( ( ( rule__RelationType__SideAArtifactTypeAssignment_6 ) ) rule__RelationType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2036:1: ( ( rule__RelationType__SideAArtifactTypeAssignment_6 ) ) rule__RelationType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2036:1: ( ( rule__RelationType__SideAArtifactTypeAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2037:1: ( rule__RelationType__SideAArtifactTypeAssignment_6 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeAssignment_6()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2038:1: ( rule__RelationType__SideAArtifactTypeAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2038:2: rule__RelationType__SideAArtifactTypeAssignment_6
            {
            pushFollow(FOLLOW_rule__RelationType__SideAArtifactTypeAssignment_6_in_rule__RelationType__Group__64109);
            rule__RelationType__SideAArtifactTypeAssignment_6();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeAssignment_6()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__7_in_rule__RelationType__Group__64118);
            rule__RelationType__Group__7();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__6


    // $ANTLR start rule__RelationType__Group__7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2049:1: rule__RelationType__Group__7 : ( 'sideBName' ) rule__RelationType__Group__8 ;
    public final void rule__RelationType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2053:1: ( ( 'sideBName' ) rule__RelationType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2054:1: ( 'sideBName' ) rule__RelationType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2054:1: ( 'sideBName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2055:1: 'sideBName'
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_7()); 
            match(input,63,FOLLOW_63_in_rule__RelationType__Group__74147); 
             after(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_7()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__8_in_rule__RelationType__Group__74157);
            rule__RelationType__Group__8();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__7


    // $ANTLR start rule__RelationType__Group__8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2069:1: rule__RelationType__Group__8 : ( ( rule__RelationType__SideBNameAssignment_8 ) ) rule__RelationType__Group__9 ;
    public final void rule__RelationType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2073:1: ( ( ( rule__RelationType__SideBNameAssignment_8 ) ) rule__RelationType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2074:1: ( ( rule__RelationType__SideBNameAssignment_8 ) ) rule__RelationType__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2074:1: ( ( rule__RelationType__SideBNameAssignment_8 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2075:1: ( rule__RelationType__SideBNameAssignment_8 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameAssignment_8()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2076:1: ( rule__RelationType__SideBNameAssignment_8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2076:2: rule__RelationType__SideBNameAssignment_8
            {
            pushFollow(FOLLOW_rule__RelationType__SideBNameAssignment_8_in_rule__RelationType__Group__84185);
            rule__RelationType__SideBNameAssignment_8();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideBNameAssignment_8()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__9_in_rule__RelationType__Group__84194);
            rule__RelationType__Group__9();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__8


    // $ANTLR start rule__RelationType__Group__9
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2087:1: rule__RelationType__Group__9 : ( 'sideBArtifactType' ) rule__RelationType__Group__10 ;
    public final void rule__RelationType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2091:1: ( ( 'sideBArtifactType' ) rule__RelationType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2092:1: ( 'sideBArtifactType' ) rule__RelationType__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2092:1: ( 'sideBArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2093:1: 'sideBArtifactType'
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_9()); 
            match(input,64,FOLLOW_64_in_rule__RelationType__Group__94223); 
             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_9()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__10_in_rule__RelationType__Group__94233);
            rule__RelationType__Group__10();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__9


    // $ANTLR start rule__RelationType__Group__10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2107:1: rule__RelationType__Group__10 : ( ( rule__RelationType__SideBArtifactTypeAssignment_10 ) ) rule__RelationType__Group__11 ;
    public final void rule__RelationType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2111:1: ( ( ( rule__RelationType__SideBArtifactTypeAssignment_10 ) ) rule__RelationType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2112:1: ( ( rule__RelationType__SideBArtifactTypeAssignment_10 ) ) rule__RelationType__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2112:1: ( ( rule__RelationType__SideBArtifactTypeAssignment_10 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2113:1: ( rule__RelationType__SideBArtifactTypeAssignment_10 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeAssignment_10()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2114:1: ( rule__RelationType__SideBArtifactTypeAssignment_10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2114:2: rule__RelationType__SideBArtifactTypeAssignment_10
            {
            pushFollow(FOLLOW_rule__RelationType__SideBArtifactTypeAssignment_10_in_rule__RelationType__Group__104261);
            rule__RelationType__SideBArtifactTypeAssignment_10();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeAssignment_10()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__11_in_rule__RelationType__Group__104270);
            rule__RelationType__Group__11();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__10


    // $ANTLR start rule__RelationType__Group__11
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2125:1: rule__RelationType__Group__11 : ( 'defaultOrderType' ) rule__RelationType__Group__12 ;
    public final void rule__RelationType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2129:1: ( ( 'defaultOrderType' ) rule__RelationType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2130:1: ( 'defaultOrderType' ) rule__RelationType__Group__12
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2130:1: ( 'defaultOrderType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2131:1: 'defaultOrderType'
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_11()); 
            match(input,65,FOLLOW_65_in_rule__RelationType__Group__114299); 
             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_11()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__12_in_rule__RelationType__Group__114309);
            rule__RelationType__Group__12();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__11


    // $ANTLR start rule__RelationType__Group__12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2145:1: rule__RelationType__Group__12 : ( ( rule__RelationType__DefaultOrderTypeAssignment_12 ) ) rule__RelationType__Group__13 ;
    public final void rule__RelationType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2149:1: ( ( ( rule__RelationType__DefaultOrderTypeAssignment_12 ) ) rule__RelationType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2150:1: ( ( rule__RelationType__DefaultOrderTypeAssignment_12 ) ) rule__RelationType__Group__13
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2150:1: ( ( rule__RelationType__DefaultOrderTypeAssignment_12 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2151:1: ( rule__RelationType__DefaultOrderTypeAssignment_12 )
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAssignment_12()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2152:1: ( rule__RelationType__DefaultOrderTypeAssignment_12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2152:2: rule__RelationType__DefaultOrderTypeAssignment_12
            {
            pushFollow(FOLLOW_rule__RelationType__DefaultOrderTypeAssignment_12_in_rule__RelationType__Group__124337);
            rule__RelationType__DefaultOrderTypeAssignment_12();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAssignment_12()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__13_in_rule__RelationType__Group__124346);
            rule__RelationType__Group__13();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__12


    // $ANTLR start rule__RelationType__Group__13
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2163:1: rule__RelationType__Group__13 : ( 'multiplicity' ) rule__RelationType__Group__14 ;
    public final void rule__RelationType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2167:1: ( ( 'multiplicity' ) rule__RelationType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2168:1: ( 'multiplicity' ) rule__RelationType__Group__14
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2168:1: ( 'multiplicity' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2169:1: 'multiplicity'
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_13()); 
            match(input,66,FOLLOW_66_in_rule__RelationType__Group__134375); 
             after(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_13()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__14_in_rule__RelationType__Group__134385);
            rule__RelationType__Group__14();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__13


    // $ANTLR start rule__RelationType__Group__14
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2183:1: rule__RelationType__Group__14 : ( ( rule__RelationType__MultiplicityAssignment_14 ) ) rule__RelationType__Group__15 ;
    public final void rule__RelationType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2187:1: ( ( ( rule__RelationType__MultiplicityAssignment_14 ) ) rule__RelationType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2188:1: ( ( rule__RelationType__MultiplicityAssignment_14 ) ) rule__RelationType__Group__15
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2188:1: ( ( rule__RelationType__MultiplicityAssignment_14 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2189:1: ( rule__RelationType__MultiplicityAssignment_14 )
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityAssignment_14()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2190:1: ( rule__RelationType__MultiplicityAssignment_14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2190:2: rule__RelationType__MultiplicityAssignment_14
            {
            pushFollow(FOLLOW_rule__RelationType__MultiplicityAssignment_14_in_rule__RelationType__Group__144413);
            rule__RelationType__MultiplicityAssignment_14();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getMultiplicityAssignment_14()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__15_in_rule__RelationType__Group__144422);
            rule__RelationType__Group__15();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__14


    // $ANTLR start rule__RelationType__Group__15
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2201:1: rule__RelationType__Group__15 : ( '}' ) ;
    public final void rule__RelationType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2205:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2206:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2206:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2207:1: '}'
            {
             before(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_15()); 
            match(input,46,FOLLOW_46_in_rule__RelationType__Group__154451); 
             after(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_15()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__Group__15


    // $ANTLR start rule__OseeTypeModel__ImportsAssignment_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2252:1: rule__OseeTypeModel__ImportsAssignment_0 : ( ruleImport ) ;
    public final void rule__OseeTypeModel__ImportsAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2256:1: ( ( ruleImport ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2257:1: ( ruleImport )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2257:1: ( ruleImport )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2258:1: ruleImport
            {
             before(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0()); 
            pushFollow(FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_04518);
            ruleImport();
            _fsp--;

             after(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeTypeModel__ImportsAssignment_0


    // $ANTLR start rule__OseeTypeModel__TypesAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2267:1: rule__OseeTypeModel__TypesAssignment_1 : ( ruleOseeType ) ;
    public final void rule__OseeTypeModel__TypesAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2271:1: ( ( ruleOseeType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2272:1: ( ruleOseeType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2272:1: ( ruleOseeType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2273:1: ruleOseeType
            {
             before(grammarAccess.getOseeTypeModelAccess().getTypesOseeTypeParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleOseeType_in_rule__OseeTypeModel__TypesAssignment_14549);
            ruleOseeType();
            _fsp--;

             after(grammarAccess.getOseeTypeModelAccess().getTypesOseeTypeParserRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeTypeModel__TypesAssignment_1


    // $ANTLR start rule__Import__ImportURIAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2282:1: rule__Import__ImportURIAssignment_1 : ( RULE_STRING ) ;
    public final void rule__Import__ImportURIAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2286:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2287:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2287:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2288:1: RULE_STRING
            {
             before(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_14580); 
             after(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Import__ImportURIAssignment_1


    // $ANTLR start rule__ArtifactType__NameAssignment_2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2297:1: rule__ArtifactType__NameAssignment_2 : ( ruleQUALIFIED_NAME ) ;
    public final void rule__ArtifactType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2301:1: ( ( ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2302:1: ( ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2302:1: ( ruleQUALIFIED_NAME )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2303:1: ruleQUALIFIED_NAME
            {
             before(grammarAccess.getArtifactTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_2_0()); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__ArtifactType__NameAssignment_24611);
            ruleQUALIFIED_NAME();
            _fsp--;

             after(grammarAccess.getArtifactTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__NameAssignment_2


    // $ANTLR start rule__ArtifactType__SuperArtifactTypeAssignment_3_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2312:1: rule__ArtifactType__SuperArtifactTypeAssignment_3_1 : ( ( RULE_ID ) ) ;
    public final void rule__ArtifactType__SuperArtifactTypeAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2316:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2317:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2317:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2318:1: ( RULE_ID )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypeArtifactTypeCrossReference_3_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2319:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2320:1: RULE_ID
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypeArtifactTypeIDTerminalRuleCall_3_1_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__ArtifactType__SuperArtifactTypeAssignment_3_14646); 
             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypeArtifactTypeIDTerminalRuleCall_3_1_0_1()); 

            }

             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypeArtifactTypeCrossReference_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__SuperArtifactTypeAssignment_3_1


    // $ANTLR start rule__ArtifactType__ValidAttributeTypesAssignment_5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2331:1: rule__ArtifactType__ValidAttributeTypesAssignment_5 : ( ruleAttributeTypeRef ) ;
    public final void rule__ArtifactType__ValidAttributeTypesAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2335:1: ( ( ruleAttributeTypeRef ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2336:1: ( ruleAttributeTypeRef )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2336:1: ( ruleAttributeTypeRef )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2337:1: ruleAttributeTypeRef
            {
             before(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAttributeTypeRefParserRuleCall_5_0()); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_rule__ArtifactType__ValidAttributeTypesAssignment_54681);
            ruleAttributeTypeRef();
            _fsp--;

             after(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAttributeTypeRefParserRuleCall_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__ValidAttributeTypesAssignment_5


    // $ANTLR start rule__AttributeTypeRef__ValidAttributeTypeAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2346:1: rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 : ( ( RULE_ID ) ) ;
    public final void rule__AttributeTypeRef__ValidAttributeTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2350:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2351:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2351:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2352:1: ( RULE_ID )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeCrossReference_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2353:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2354:1: RULE_ID
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeIDTerminalRuleCall_1_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__AttributeTypeRef__ValidAttributeTypeAssignment_14716); 
             after(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeIDTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeTypeRef__ValidAttributeTypeAssignment_1


    // $ANTLR start rule__AttributeType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2365:1: rule__AttributeType__NameAssignment_1 : ( ruleQUALIFIED_NAME ) ;
    public final void rule__AttributeType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2369:1: ( ( ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2370:1: ( ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2370:1: ( ruleQUALIFIED_NAME )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2371:1: ruleQUALIFIED_NAME
            {
             before(grammarAccess.getAttributeTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__NameAssignment_14751);
            ruleQUALIFIED_NAME();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__NameAssignment_1


    // $ANTLR start rule__AttributeType__BaseAttributeTypeAssignment_2_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2380:1: rule__AttributeType__BaseAttributeTypeAssignment_2_1 : ( ruleAttributeBaseType ) ;
    public final void rule__AttributeType__BaseAttributeTypeAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2384:1: ( ( ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2385:1: ( ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2385:1: ( ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2386:1: ruleAttributeBaseType
            {
             before(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_rule__AttributeType__BaseAttributeTypeAssignment_2_14782);
            ruleAttributeBaseType();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__BaseAttributeTypeAssignment_2_1


    // $ANTLR start rule__AttributeType__DataProviderAssignment_5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2395:1: rule__AttributeType__DataProviderAssignment_5 : ( ( rule__AttributeType__DataProviderAlternatives_5_0 ) ) ;
    public final void rule__AttributeType__DataProviderAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2399:1: ( ( ( rule__AttributeType__DataProviderAlternatives_5_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2400:1: ( ( rule__AttributeType__DataProviderAlternatives_5_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2400:1: ( ( rule__AttributeType__DataProviderAlternatives_5_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2401:1: ( rule__AttributeType__DataProviderAlternatives_5_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderAlternatives_5_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2402:1: ( rule__AttributeType__DataProviderAlternatives_5_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2402:2: rule__AttributeType__DataProviderAlternatives_5_0
            {
            pushFollow(FOLLOW_rule__AttributeType__DataProviderAlternatives_5_0_in_rule__AttributeType__DataProviderAssignment_54813);
            rule__AttributeType__DataProviderAlternatives_5_0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDataProviderAlternatives_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__DataProviderAssignment_5


    // $ANTLR start rule__AttributeType__MinAssignment_7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2411:1: rule__AttributeType__MinAssignment_7 : ( ruleDIGITS ) ;
    public final void rule__AttributeType__MinAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2415:1: ( ( ruleDIGITS ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2416:1: ( ruleDIGITS )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2416:1: ( ruleDIGITS )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2417:1: ruleDIGITS
            {
             before(grammarAccess.getAttributeTypeAccess().getMinDIGITSParserRuleCall_7_0()); 
            pushFollow(FOLLOW_ruleDIGITS_in_rule__AttributeType__MinAssignment_74846);
            ruleDIGITS();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getMinDIGITSParserRuleCall_7_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__MinAssignment_7


    // $ANTLR start rule__AttributeType__MaxAssignment_9
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2426:1: rule__AttributeType__MaxAssignment_9 : ( ( rule__AttributeType__MaxAlternatives_9_0 ) ) ;
    public final void rule__AttributeType__MaxAssignment_9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2430:1: ( ( ( rule__AttributeType__MaxAlternatives_9_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2431:1: ( ( rule__AttributeType__MaxAlternatives_9_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2431:1: ( ( rule__AttributeType__MaxAlternatives_9_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2432:1: ( rule__AttributeType__MaxAlternatives_9_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxAlternatives_9_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2433:1: ( rule__AttributeType__MaxAlternatives_9_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2433:2: rule__AttributeType__MaxAlternatives_9_0
            {
            pushFollow(FOLLOW_rule__AttributeType__MaxAlternatives_9_0_in_rule__AttributeType__MaxAssignment_94877);
            rule__AttributeType__MaxAlternatives_9_0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMaxAlternatives_9_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__MaxAssignment_9


    // $ANTLR start rule__AttributeType__TaggerIdAssignment_10_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2442:1: rule__AttributeType__TaggerIdAssignment_10_1 : ( ( rule__AttributeType__TaggerIdAlternatives_10_1_0 ) ) ;
    public final void rule__AttributeType__TaggerIdAssignment_10_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2446:1: ( ( ( rule__AttributeType__TaggerIdAlternatives_10_1_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2447:1: ( ( rule__AttributeType__TaggerIdAlternatives_10_1_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2447:1: ( ( rule__AttributeType__TaggerIdAlternatives_10_1_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2448:1: ( rule__AttributeType__TaggerIdAlternatives_10_1_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdAlternatives_10_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2449:1: ( rule__AttributeType__TaggerIdAlternatives_10_1_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2449:2: rule__AttributeType__TaggerIdAlternatives_10_1_0
            {
            pushFollow(FOLLOW_rule__AttributeType__TaggerIdAlternatives_10_1_0_in_rule__AttributeType__TaggerIdAssignment_10_14910);
            rule__AttributeType__TaggerIdAlternatives_10_1_0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getTaggerIdAlternatives_10_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__TaggerIdAssignment_10_1


    // $ANTLR start rule__AttributeType__EnumTypeAssignment_11_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2458:1: rule__AttributeType__EnumTypeAssignment_11_1 : ( ( RULE_ID ) ) ;
    public final void rule__AttributeType__EnumTypeAssignment_11_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2462:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2463:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2463:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2464:1: ( RULE_ID )
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_11_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2465:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2466:1: RULE_ID
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeIDTerminalRuleCall_11_1_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__AttributeType__EnumTypeAssignment_11_14947); 
             after(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeIDTerminalRuleCall_11_1_0_1()); 

            }

             after(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_11_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__EnumTypeAssignment_11_1


    // $ANTLR start rule__AttributeType__DescriptionAssignment_12_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2477:1: rule__AttributeType__DescriptionAssignment_12_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__DescriptionAssignment_12_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2481:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2482:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2482:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2483:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_12_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__DescriptionAssignment_12_14982); 
             after(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_12_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__DescriptionAssignment_12_1


    // $ANTLR start rule__AttributeType__DefaultValueAssignment_13_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2492:1: rule__AttributeType__DefaultValueAssignment_13_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__DefaultValueAssignment_13_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2496:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2497:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2497:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2498:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_13_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__DefaultValueAssignment_13_15013); 
             after(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_13_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__DefaultValueAssignment_13_1


    // $ANTLR start rule__AttributeType__FileExtensionAssignment_14_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2507:1: rule__AttributeType__FileExtensionAssignment_14_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__FileExtensionAssignment_14_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2511:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2512:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2512:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2513:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_14_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__FileExtensionAssignment_14_15044); 
             after(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_14_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__FileExtensionAssignment_14_1


    // $ANTLR start rule__OseeEnumType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2522:1: rule__OseeEnumType__NameAssignment_1 : ( ruleQUALIFIED_NAME ) ;
    public final void rule__OseeEnumType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2526:1: ( ( ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2527:1: ( ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2527:1: ( ruleQUALIFIED_NAME )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2528:1: ruleQUALIFIED_NAME
            {
             before(grammarAccess.getOseeEnumTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__OseeEnumType__NameAssignment_15075);
            ruleQUALIFIED_NAME();
            _fsp--;

             after(grammarAccess.getOseeEnumTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumType__NameAssignment_1


    // $ANTLR start rule__OseeEnumType__EnumEntriesAssignment_3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2537:1: rule__OseeEnumType__EnumEntriesAssignment_3 : ( ruleOseeEnumEntry ) ;
    public final void rule__OseeEnumType__EnumEntriesAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2541:1: ( ( ruleOseeEnumEntry ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2542:1: ( ruleOseeEnumEntry )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2542:1: ( ruleOseeEnumEntry )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2543:1: ruleOseeEnumEntry
            {
             before(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesOseeEnumEntryParserRuleCall_3_0()); 
            pushFollow(FOLLOW_ruleOseeEnumEntry_in_rule__OseeEnumType__EnumEntriesAssignment_35106);
            ruleOseeEnumEntry();
            _fsp--;

             after(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesOseeEnumEntryParserRuleCall_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumType__EnumEntriesAssignment_3


    // $ANTLR start rule__OseeEnumEntry__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2552:1: rule__OseeEnumEntry__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__OseeEnumEntry__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2556:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2557:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2557:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2558:1: RULE_STRING
            {
             before(grammarAccess.getOseeEnumEntryAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__OseeEnumEntry__NameAssignment_15137); 
             after(grammarAccess.getOseeEnumEntryAccess().getNameSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumEntry__NameAssignment_1


    // $ANTLR start rule__OseeEnumEntry__OrdinalAssignment_2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2567:1: rule__OseeEnumEntry__OrdinalAssignment_2 : ( ruleDIGITS ) ;
    public final void rule__OseeEnumEntry__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2571:1: ( ( ruleDIGITS ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2572:1: ( ruleDIGITS )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2572:1: ( ruleDIGITS )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2573:1: ruleDIGITS
            {
             before(grammarAccess.getOseeEnumEntryAccess().getOrdinalDIGITSParserRuleCall_2_0()); 
            pushFollow(FOLLOW_ruleDIGITS_in_rule__OseeEnumEntry__OrdinalAssignment_25168);
            ruleDIGITS();
            _fsp--;

             after(grammarAccess.getOseeEnumEntryAccess().getOrdinalDIGITSParserRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeEnumEntry__OrdinalAssignment_2


    // $ANTLR start rule__RelationType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2582:1: rule__RelationType__NameAssignment_1 : ( ruleQUALIFIED_NAME ) ;
    public final void rule__RelationType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2586:1: ( ( ruleQUALIFIED_NAME ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2587:1: ( ruleQUALIFIED_NAME )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2587:1: ( ruleQUALIFIED_NAME )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2588:1: ruleQUALIFIED_NAME
            {
             before(grammarAccess.getRelationTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__RelationType__NameAssignment_15199);
            ruleQUALIFIED_NAME();
            _fsp--;

             after(grammarAccess.getRelationTypeAccess().getNameQUALIFIED_NAMEParserRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__NameAssignment_1


    // $ANTLR start rule__RelationType__SideANameAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2597:1: rule__RelationType__SideANameAssignment_4 : ( RULE_STRING ) ;
    public final void rule__RelationType__SideANameAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2601:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2602:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2602:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2603:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_4_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__SideANameAssignment_45230); 
             after(grammarAccess.getRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__SideANameAssignment_4


    // $ANTLR start rule__RelationType__SideAArtifactTypeAssignment_6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2612:1: rule__RelationType__SideAArtifactTypeAssignment_6 : ( ( RULE_ID ) ) ;
    public final void rule__RelationType__SideAArtifactTypeAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2616:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2617:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2617:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2618:1: ( RULE_ID )
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeCrossReference_6_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2619:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2620:1: RULE_ID
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeIDTerminalRuleCall_6_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__RelationType__SideAArtifactTypeAssignment_65265); 
             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeIDTerminalRuleCall_6_0_1()); 

            }

             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeCrossReference_6_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__SideAArtifactTypeAssignment_6


    // $ANTLR start rule__RelationType__SideBNameAssignment_8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2631:1: rule__RelationType__SideBNameAssignment_8 : ( RULE_STRING ) ;
    public final void rule__RelationType__SideBNameAssignment_8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2635:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2636:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2636:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2637:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_8_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__SideBNameAssignment_85300); 
             after(grammarAccess.getRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_8_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__SideBNameAssignment_8


    // $ANTLR start rule__RelationType__SideBArtifactTypeAssignment_10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2646:1: rule__RelationType__SideBArtifactTypeAssignment_10 : ( ( RULE_ID ) ) ;
    public final void rule__RelationType__SideBArtifactTypeAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2650:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2651:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2651:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2652:1: ( RULE_ID )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeCrossReference_10_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2653:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2654:1: RULE_ID
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeIDTerminalRuleCall_10_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__RelationType__SideBArtifactTypeAssignment_105335); 
             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeIDTerminalRuleCall_10_0_1()); 

            }

             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeCrossReference_10_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__SideBArtifactTypeAssignment_10


    // $ANTLR start rule__RelationType__DefaultOrderTypeAssignment_12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2665:1: rule__RelationType__DefaultOrderTypeAssignment_12 : ( ( rule__RelationType__DefaultOrderTypeAlternatives_12_0 ) ) ;
    public final void rule__RelationType__DefaultOrderTypeAssignment_12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2669:1: ( ( ( rule__RelationType__DefaultOrderTypeAlternatives_12_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2670:1: ( ( rule__RelationType__DefaultOrderTypeAlternatives_12_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2670:1: ( ( rule__RelationType__DefaultOrderTypeAlternatives_12_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2671:1: ( rule__RelationType__DefaultOrderTypeAlternatives_12_0 )
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAlternatives_12_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2672:1: ( rule__RelationType__DefaultOrderTypeAlternatives_12_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2672:2: rule__RelationType__DefaultOrderTypeAlternatives_12_0
            {
            pushFollow(FOLLOW_rule__RelationType__DefaultOrderTypeAlternatives_12_0_in_rule__RelationType__DefaultOrderTypeAssignment_125370);
            rule__RelationType__DefaultOrderTypeAlternatives_12_0();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAlternatives_12_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__DefaultOrderTypeAssignment_12


    // $ANTLR start rule__RelationType__MultiplicityAssignment_14
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2681:1: rule__RelationType__MultiplicityAssignment_14 : ( ruleRelationMultiplicityEnum ) ;
    public final void rule__RelationType__MultiplicityAssignment_14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2685:1: ( ( ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2686:1: ( ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2686:1: ( ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2687:1: ruleRelationMultiplicityEnum
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_14_0()); 
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_rule__RelationType__MultiplicityAssignment_145403);
            ruleRelationMultiplicityEnum();
            _fsp--;

             after(grammarAccess.getRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_14_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__MultiplicityAssignment_14


 

    public static final BitSet FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel60 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeTypeModel67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__Group__0_in_ruleOseeTypeModel94 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport120 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Import__Group__0_in_ruleImport154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME180 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__0_in_ruleQUALIFIED_NAME214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDIGITS_in_entryRuleDIGITS240 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDIGITS247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__DIGITS__Alternatives_in_ruleDIGITS276 = new BitSet(new long[]{0x00000000001FF802L});
    public static final BitSet FOLLOW_rule__DIGITS__Alternatives_in_ruleDIGITS288 = new BitSet(new long[]{0x00000000001FF802L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType317 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeType__Alternatives_in_ruleOseeType351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_entryRuleArtifactType377 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactType384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__0_in_ruleArtifactType411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef437 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRef444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__0_in_ruleAttributeTypeRef471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_entryRuleAttributeType497 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeType504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__0_in_ruleAttributeType531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType557 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeBaseType__Alternatives_in_ruleAttributeBaseType591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType617 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumType624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__0_in_ruleOseeEnumType651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry677 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumEntry684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__0_in_ruleOseeEnumEntry711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_entryRuleRelationType737 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationType744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__Group__0_in_ruleRelationType771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationMultiplicityEnum__Alternatives_in_ruleRelationMultiplicityEnum808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_rule__DIGITS__Alternatives844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_rule__DIGITS__Alternatives864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_rule__DIGITS__Alternatives884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_rule__DIGITS__Alternatives904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_rule__DIGITS__Alternatives924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_rule__DIGITS__Alternatives944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_rule__DIGITS__Alternatives964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_rule__DIGITS__Alternatives984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_rule__DIGITS__Alternatives1004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_rule__DIGITS__Alternatives1024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_rule__OseeType__Alternatives1058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_rule__OseeType__Alternatives1075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_rule__OseeType__Alternatives1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_rule__OseeType__Alternatives1109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_rule__AttributeType__DataProviderAlternatives_5_01142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_rule__AttributeType__DataProviderAlternatives_5_01162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_rule__AttributeType__DataProviderAlternatives_5_01182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__DataProviderAlternatives_5_01201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDIGITS_in_rule__AttributeType__MaxAlternatives_9_01233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_rule__AttributeType__MaxAlternatives_9_01251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_rule__AttributeType__TaggerIdAlternatives_10_1_01286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__TaggerIdAlternatives_10_1_01305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_rule__AttributeBaseType__Alternatives1338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_rule__AttributeBaseType__Alternatives1358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_rule__AttributeBaseType__Alternatives1378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_rule__AttributeBaseType__Alternatives1398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_rule__AttributeBaseType__Alternatives1418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_rule__AttributeBaseType__Alternatives1438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_rule__AttributeBaseType__Alternatives1458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_rule__AttributeBaseType__Alternatives1478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_rule__AttributeBaseType__Alternatives1498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeBaseType__Alternatives1517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_rule__RelationType__DefaultOrderTypeAlternatives_12_01550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__RelationType__DefaultOrderTypeAlternatives_12_01570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__RelationType__DefaultOrderTypeAlternatives_12_01590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__RelationType__DefaultOrderTypeAlternatives_12_01609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_rule__RelationMultiplicityEnum__Alternatives1642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_rule__RelationMultiplicityEnum__Alternatives1663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule__RelationMultiplicityEnum__Alternatives1684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__01721 = new BitSet(new long[]{0x14021A0000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__TypesAssignment_1_in_rule__OseeTypeModel__Group__11759 = new BitSet(new long[]{0x1402180000000002L});
    public static final BitSet FOLLOW_41_in_rule__Import__Group__01799 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__Import__Group__1_in_rule__Import__Group__01809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__11837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__01875 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__01883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__11911 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_42_in_rule__QUALIFIED_NAME__Group_1__01951 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__01961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__11989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_rule__ArtifactType__Group__02028 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__1_in_rule__ArtifactType__Group__02040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_rule__ArtifactType__Group__12069 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__2_in_rule__ArtifactType__Group__12079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__NameAssignment_2_in_rule__ArtifactType__Group__22107 = new BitSet(new long[]{0x0000A00000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__3_in_rule__ArtifactType__Group__22116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__0_in_rule__ArtifactType__Group__32144 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__4_in_rule__ArtifactType__Group__32154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_rule__ArtifactType__Group__42183 = new BitSet(new long[]{0x0001400000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__5_in_rule__ArtifactType__Group__42193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__ValidAttributeTypesAssignment_5_in_rule__ArtifactType__Group__52221 = new BitSet(new long[]{0x0001400000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__6_in_rule__ArtifactType__Group__52231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_rule__ArtifactType__Group__62260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_rule__ArtifactType__Group_3__02310 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__1_in_rule__ArtifactType__Group_3__02320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__SuperArtifactTypeAssignment_3_1_in_rule__ArtifactType__Group_3__12348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_rule__AttributeTypeRef__Group__02387 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__1_in_rule__AttributeTypeRef__Group__02397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__AttributeTypeRef__Group__12425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_rule__AttributeType__Group__02464 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__1_in_rule__AttributeType__Group__02474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__NameAssignment_1_in_rule__AttributeType__Group__12502 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__2_in_rule__AttributeType__Group__12511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_2__0_in_rule__AttributeType__Group__22539 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__3_in_rule__AttributeType__Group__22548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_rule__AttributeType__Group__32577 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__4_in_rule__AttributeType__Group__32587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_rule__AttributeType__Group__42616 = new BitSet(new long[]{0x0000000000E00010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__5_in_rule__AttributeType__Group__42626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DataProviderAssignment_5_in_rule__AttributeType__Group__52654 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__6_in_rule__AttributeType__Group__52663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_rule__AttributeType__Group__62692 = new BitSet(new long[]{0x00000000001FF800L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__7_in_rule__AttributeType__Group__62702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MinAssignment_7_in_rule__AttributeType__Group__72730 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__8_in_rule__AttributeType__Group__72739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_rule__AttributeType__Group__82768 = new BitSet(new long[]{0x00000000011FF800L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__9_in_rule__AttributeType__Group__82778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MaxAssignment_9_in_rule__AttributeType__Group__92806 = new BitSet(new long[]{0x03E0400000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__10_in_rule__AttributeType__Group__92815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_10__0_in_rule__AttributeType__Group__102843 = new BitSet(new long[]{0x03C0400000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__11_in_rule__AttributeType__Group__102853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_11__0_in_rule__AttributeType__Group__112881 = new BitSet(new long[]{0x0380400000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__12_in_rule__AttributeType__Group__112891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_12__0_in_rule__AttributeType__Group__122919 = new BitSet(new long[]{0x0300400000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__13_in_rule__AttributeType__Group__122929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_13__0_in_rule__AttributeType__Group__132957 = new BitSet(new long[]{0x0200400000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__14_in_rule__AttributeType__Group__132967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_14__0_in_rule__AttributeType__Group__142995 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__15_in_rule__AttributeType__Group__143005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_rule__AttributeType__Group__153034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_rule__AttributeType__Group_2__03102 = new BitSet(new long[]{0x00000007FC000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_2__1_in_rule__AttributeType__Group_2__03112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__BaseAttributeTypeAssignment_2_1_in_rule__AttributeType__Group_2__13140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_rule__AttributeType__Group_10__03179 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_10__1_in_rule__AttributeType__Group_10__03189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TaggerIdAssignment_10_1_in_rule__AttributeType__Group_10__13217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_rule__AttributeType__Group_11__03256 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_11__1_in_rule__AttributeType__Group_11__03266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__EnumTypeAssignment_11_1_in_rule__AttributeType__Group_11__13294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_rule__AttributeType__Group_12__03333 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_12__1_in_rule__AttributeType__Group_12__03343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DescriptionAssignment_12_1_in_rule__AttributeType__Group_12__13371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_rule__AttributeType__Group_13__03410 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_13__1_in_rule__AttributeType__Group_13__03420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DefaultValueAssignment_13_1_in_rule__AttributeType__Group_13__13448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_rule__AttributeType__Group_14__03487 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_14__1_in_rule__AttributeType__Group_14__03497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__FileExtensionAssignment_14_1_in_rule__AttributeType__Group_14__13525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_rule__OseeEnumType__Group__03564 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__1_in_rule__OseeEnumType__Group__03574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__NameAssignment_1_in_rule__OseeEnumType__Group__13602 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__2_in_rule__OseeEnumType__Group__13611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_rule__OseeEnumType__Group__23640 = new BitSet(new long[]{0x0800400000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__3_in_rule__OseeEnumType__Group__23650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__EnumEntriesAssignment_3_in_rule__OseeEnumType__Group__33678 = new BitSet(new long[]{0x0800400000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__4_in_rule__OseeEnumType__Group__33688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_rule__OseeEnumType__Group__43717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_rule__OseeEnumEntry__Group__03763 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__1_in_rule__OseeEnumEntry__Group__03773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__NameAssignment_1_in_rule__OseeEnumEntry__Group__13801 = new BitSet(new long[]{0x00000000001FF802L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__2_in_rule__OseeEnumEntry__Group__13810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__OrdinalAssignment_2_in_rule__OseeEnumEntry__Group__23838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_rule__RelationType__Group__03880 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__1_in_rule__RelationType__Group__03890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__NameAssignment_1_in_rule__RelationType__Group__13918 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__2_in_rule__RelationType__Group__13927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_rule__RelationType__Group__23956 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__3_in_rule__RelationType__Group__23966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_rule__RelationType__Group__33995 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__RelationType__Group__4_in_rule__RelationType__Group__34005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideANameAssignment_4_in_rule__RelationType__Group__44033 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__5_in_rule__RelationType__Group__44042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_rule__RelationType__Group__54071 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__6_in_rule__RelationType__Group__54081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideAArtifactTypeAssignment_6_in_rule__RelationType__Group__64109 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__7_in_rule__RelationType__Group__64118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_rule__RelationType__Group__74147 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__RelationType__Group__8_in_rule__RelationType__Group__74157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideBNameAssignment_8_in_rule__RelationType__Group__84185 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rule__RelationType__Group__9_in_rule__RelationType__Group__84194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_rule__RelationType__Group__94223 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__10_in_rule__RelationType__Group__94233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideBArtifactTypeAssignment_10_in_rule__RelationType__Group__104261 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__Group__11_in_rule__RelationType__Group__104270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_rule__RelationType__Group__114299 = new BitSet(new long[]{0x0000003800000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__12_in_rule__RelationType__Group__114309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__DefaultOrderTypeAssignment_12_in_rule__RelationType__Group__124337 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_rule__RelationType__Group__13_in_rule__RelationType__Group__124346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_rule__RelationType__Group__134375 = new BitSet(new long[]{0x000001C000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__14_in_rule__RelationType__Group__134385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__MultiplicityAssignment_14_in_rule__RelationType__Group__144413 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__15_in_rule__RelationType__Group__144422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_rule__RelationType__Group__154451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_04518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeType_in_rule__OseeTypeModel__TypesAssignment_14549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_14580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__ArtifactType__NameAssignment_24611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__ArtifactType__SuperArtifactTypeAssignment_3_14646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_rule__ArtifactType__ValidAttributeTypesAssignment_54681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__AttributeTypeRef__ValidAttributeTypeAssignment_14716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__NameAssignment_14751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_rule__AttributeType__BaseAttributeTypeAssignment_2_14782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DataProviderAlternatives_5_0_in_rule__AttributeType__DataProviderAssignment_54813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDIGITS_in_rule__AttributeType__MinAssignment_74846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MaxAlternatives_9_0_in_rule__AttributeType__MaxAssignment_94877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TaggerIdAlternatives_10_1_0_in_rule__AttributeType__TaggerIdAssignment_10_14910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__AttributeType__EnumTypeAssignment_11_14947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__DescriptionAssignment_12_14982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__DefaultValueAssignment_13_15013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__FileExtensionAssignment_14_15044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__OseeEnumType__NameAssignment_15075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_rule__OseeEnumType__EnumEntriesAssignment_35106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__OseeEnumEntry__NameAssignment_15137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDIGITS_in_rule__OseeEnumEntry__OrdinalAssignment_25168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__RelationType__NameAssignment_15199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__SideANameAssignment_45230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__RelationType__SideAArtifactTypeAssignment_65265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__SideBNameAssignment_85300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__RelationType__SideBArtifactTypeAssignment_105335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__DefaultOrderTypeAlternatives_12_0_in_rule__RelationType__DefaultOrderTypeAssignment_125370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_rule__RelationType__MultiplicityAssignment_145403 = new BitSet(new long[]{0x0000000000000002L});

}