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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_WHOLE_NUM_STR", "RULE_ID", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'MappedAttributeDataProvider'", "'unlimited'", "'DefaultAttributeTaggerProvider'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'WordAttribute'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'", "'import'", "'.'", "'artifactType'", "'{'", "'}'", "'extends'", "','", "'overrides'", "'attribute'", "'branchGuid'", "'attributeType'", "'dataProvider'", "'min'", "'max'", "'taggerId'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'oseeEnumType'", "'entry'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'", "'abstract'"
    };
    public static final int RULE_ID=6;
    public static final int RULE_STRING=4;
    public static final int RULE_WHOLE_NUM_STR=5;
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


    // $ANTLR start entryRuleNAME_REFERENCE
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:114:1: entryRuleNAME_REFERENCE : ruleNAME_REFERENCE EOF ;
    public final void entryRuleNAME_REFERENCE() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:114:25: ( ruleNAME_REFERENCE EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:115:1: ruleNAME_REFERENCE EOF
            {
             before(grammarAccess.getNAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE180);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getNAME_REFERENCERule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNAME_REFERENCE187); 

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
    // $ANTLR end entryRuleNAME_REFERENCE


    // $ANTLR start ruleNAME_REFERENCE
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:122:1: ruleNAME_REFERENCE : ( RULE_STRING ) ;
    public final void ruleNAME_REFERENCE() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:126:2: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:127:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:127:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:128:1: RULE_STRING
            {
             before(grammarAccess.getNAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE214); 
             after(grammarAccess.getNAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 

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
    // $ANTLR end ruleNAME_REFERENCE


    // $ANTLR start entryRuleQUALIFIED_NAME
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:141:1: entryRuleQUALIFIED_NAME : ruleQUALIFIED_NAME EOF ;
    public final void entryRuleQUALIFIED_NAME() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:141:25: ( ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:142:1: ruleQUALIFIED_NAME EOF
            {
             before(grammarAccess.getQUALIFIED_NAMERule()); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME239);
            ruleQUALIFIED_NAME();
            _fsp--;

             after(grammarAccess.getQUALIFIED_NAMERule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME246); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:149:1: ruleQUALIFIED_NAME : ( ( rule__QUALIFIED_NAME__Group__0 ) ) ;
    public final void ruleQUALIFIED_NAME() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:153:2: ( ( ( rule__QUALIFIED_NAME__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:154:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:154:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:155:1: ( rule__QUALIFIED_NAME__Group__0 )
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:156:1: ( rule__QUALIFIED_NAME__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:156:2: rule__QUALIFIED_NAME__Group__0
            {
            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group__0_in_ruleQUALIFIED_NAME273);
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


    // $ANTLR start entryRuleOseeType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:168:1: entryRuleOseeType : ruleOseeType EOF ;
    public final void entryRuleOseeType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:168:19: ( ruleOseeType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:169:1: ruleOseeType EOF
            {
             before(grammarAccess.getOseeTypeRule()); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType299);
            ruleOseeType();
            _fsp--;

             after(grammarAccess.getOseeTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType306); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:176:1: ruleOseeType : ( ( rule__OseeType__Alternatives ) ) ;
    public final void ruleOseeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:180:2: ( ( ( rule__OseeType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:181:1: ( ( rule__OseeType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:181:1: ( ( rule__OseeType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:182:1: ( rule__OseeType__Alternatives )
            {
             before(grammarAccess.getOseeTypeAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:183:1: ( rule__OseeType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:183:2: rule__OseeType__Alternatives
            {
            pushFollow(FOLLOW_rule__OseeType__Alternatives_in_ruleOseeType333);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:195:1: entryRuleArtifactType : ruleArtifactType EOF ;
    public final void entryRuleArtifactType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:195:23: ( ruleArtifactType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:196:1: ruleArtifactType EOF
            {
             before(grammarAccess.getArtifactTypeRule()); 
            pushFollow(FOLLOW_ruleArtifactType_in_entryRuleArtifactType359);
            ruleArtifactType();
            _fsp--;

             after(grammarAccess.getArtifactTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactType366); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:203:1: ruleArtifactType : ( ( rule__ArtifactType__Group__0 ) ) ;
    public final void ruleArtifactType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:207:2: ( ( ( rule__ArtifactType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:208:1: ( ( rule__ArtifactType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:208:1: ( ( rule__ArtifactType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:209:1: ( rule__ArtifactType__Group__0 )
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:210:1: ( rule__ArtifactType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:210:2: rule__ArtifactType__Group__0
            {
            pushFollow(FOLLOW_rule__ArtifactType__Group__0_in_ruleArtifactType393);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:222:1: entryRuleAttributeTypeRef : ruleAttributeTypeRef EOF ;
    public final void entryRuleAttributeTypeRef() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:222:27: ( ruleAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:223:1: ruleAttributeTypeRef EOF
            {
             before(grammarAccess.getAttributeTypeRefRule()); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef419);
            ruleAttributeTypeRef();
            _fsp--;

             after(grammarAccess.getAttributeTypeRefRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRef426); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:230:1: ruleAttributeTypeRef : ( ( rule__AttributeTypeRef__Group__0 ) ) ;
    public final void ruleAttributeTypeRef() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:234:2: ( ( ( rule__AttributeTypeRef__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:235:1: ( ( rule__AttributeTypeRef__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:235:1: ( ( rule__AttributeTypeRef__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:236:1: ( rule__AttributeTypeRef__Group__0 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:237:1: ( rule__AttributeTypeRef__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:237:2: rule__AttributeTypeRef__Group__0
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__0_in_ruleAttributeTypeRef453);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:249:1: entryRuleAttributeType : ruleAttributeType EOF ;
    public final void entryRuleAttributeType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:249:24: ( ruleAttributeType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:250:1: ruleAttributeType EOF
            {
             before(grammarAccess.getAttributeTypeRule()); 
            pushFollow(FOLLOW_ruleAttributeType_in_entryRuleAttributeType479);
            ruleAttributeType();
            _fsp--;

             after(grammarAccess.getAttributeTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeType486); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:257:1: ruleAttributeType : ( ( rule__AttributeType__Group__0 ) ) ;
    public final void ruleAttributeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:261:2: ( ( ( rule__AttributeType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:262:1: ( ( rule__AttributeType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:262:1: ( ( rule__AttributeType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:263:1: ( rule__AttributeType__Group__0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:264:1: ( rule__AttributeType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:264:2: rule__AttributeType__Group__0
            {
            pushFollow(FOLLOW_rule__AttributeType__Group__0_in_ruleAttributeType513);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:276:1: entryRuleAttributeBaseType : ruleAttributeBaseType EOF ;
    public final void entryRuleAttributeBaseType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:276:28: ( ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:277:1: ruleAttributeBaseType EOF
            {
             before(grammarAccess.getAttributeBaseTypeRule()); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType539);
            ruleAttributeBaseType();
            _fsp--;

             after(grammarAccess.getAttributeBaseTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType546); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:284:1: ruleAttributeBaseType : ( ( rule__AttributeBaseType__Alternatives ) ) ;
    public final void ruleAttributeBaseType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:288:2: ( ( ( rule__AttributeBaseType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:289:1: ( ( rule__AttributeBaseType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:289:1: ( ( rule__AttributeBaseType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:290:1: ( rule__AttributeBaseType__Alternatives )
            {
             before(grammarAccess.getAttributeBaseTypeAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:291:1: ( rule__AttributeBaseType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:291:2: rule__AttributeBaseType__Alternatives
            {
            pushFollow(FOLLOW_rule__AttributeBaseType__Alternatives_in_ruleAttributeBaseType573);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:303:1: entryRuleOseeEnumType : ruleOseeEnumType EOF ;
    public final void entryRuleOseeEnumType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:303:23: ( ruleOseeEnumType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:304:1: ruleOseeEnumType EOF
            {
             before(grammarAccess.getOseeEnumTypeRule()); 
            pushFollow(FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType599);
            ruleOseeEnumType();
            _fsp--;

             after(grammarAccess.getOseeEnumTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumType606); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:311:1: ruleOseeEnumType : ( ( rule__OseeEnumType__Group__0 ) ) ;
    public final void ruleOseeEnumType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:315:2: ( ( ( rule__OseeEnumType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:316:1: ( ( rule__OseeEnumType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:316:1: ( ( rule__OseeEnumType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:317:1: ( rule__OseeEnumType__Group__0 )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:318:1: ( rule__OseeEnumType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:318:2: rule__OseeEnumType__Group__0
            {
            pushFollow(FOLLOW_rule__OseeEnumType__Group__0_in_ruleOseeEnumType633);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:330:1: entryRuleOseeEnumEntry : ruleOseeEnumEntry EOF ;
    public final void entryRuleOseeEnumEntry() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:330:24: ( ruleOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:331:1: ruleOseeEnumEntry EOF
            {
             before(grammarAccess.getOseeEnumEntryRule()); 
            pushFollow(FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry659);
            ruleOseeEnumEntry();
            _fsp--;

             after(grammarAccess.getOseeEnumEntryRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumEntry666); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:338:1: ruleOseeEnumEntry : ( ( rule__OseeEnumEntry__Group__0 ) ) ;
    public final void ruleOseeEnumEntry() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:342:2: ( ( ( rule__OseeEnumEntry__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:343:1: ( ( rule__OseeEnumEntry__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:343:1: ( ( rule__OseeEnumEntry__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:344:1: ( rule__OseeEnumEntry__Group__0 )
            {
             before(grammarAccess.getOseeEnumEntryAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:345:1: ( rule__OseeEnumEntry__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:345:2: rule__OseeEnumEntry__Group__0
            {
            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__0_in_ruleOseeEnumEntry693);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:357:1: entryRuleRelationType : ruleRelationType EOF ;
    public final void entryRuleRelationType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:357:23: ( ruleRelationType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:358:1: ruleRelationType EOF
            {
             before(grammarAccess.getRelationTypeRule()); 
            pushFollow(FOLLOW_ruleRelationType_in_entryRuleRelationType719);
            ruleRelationType();
            _fsp--;

             after(grammarAccess.getRelationTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationType726); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:365:1: ruleRelationType : ( ( rule__RelationType__Group__0 ) ) ;
    public final void ruleRelationType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:369:2: ( ( ( rule__RelationType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:370:1: ( ( rule__RelationType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:370:1: ( ( rule__RelationType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:371:1: ( rule__RelationType__Group__0 )
            {
             before(grammarAccess.getRelationTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:372:1: ( rule__RelationType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:372:2: rule__RelationType__Group__0
            {
            pushFollow(FOLLOW_rule__RelationType__Group__0_in_ruleRelationType753);
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


    // $ANTLR start entryRuleRelationOrderType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:384:1: entryRuleRelationOrderType : ruleRelationOrderType EOF ;
    public final void entryRuleRelationOrderType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:384:28: ( ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:385:1: ruleRelationOrderType EOF
            {
             before(grammarAccess.getRelationOrderTypeRule()); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType779);
            ruleRelationOrderType();
            _fsp--;

             after(grammarAccess.getRelationOrderTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationOrderType786); 

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
    // $ANTLR end entryRuleRelationOrderType


    // $ANTLR start ruleRelationOrderType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:392:1: ruleRelationOrderType : ( ( rule__RelationOrderType__Alternatives ) ) ;
    public final void ruleRelationOrderType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:396:2: ( ( ( rule__RelationOrderType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:397:1: ( ( rule__RelationOrderType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:397:1: ( ( rule__RelationOrderType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:398:1: ( rule__RelationOrderType__Alternatives )
            {
             before(grammarAccess.getRelationOrderTypeAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:399:1: ( rule__RelationOrderType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:399:2: rule__RelationOrderType__Alternatives
            {
            pushFollow(FOLLOW_rule__RelationOrderType__Alternatives_in_ruleRelationOrderType813);
            rule__RelationOrderType__Alternatives();
            _fsp--;


            }

             after(grammarAccess.getRelationOrderTypeAccess().getAlternatives()); 

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
    // $ANTLR end ruleRelationOrderType


    // $ANTLR start ruleRelationMultiplicityEnum
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:412:1: ruleRelationMultiplicityEnum : ( ( rule__RelationMultiplicityEnum__Alternatives ) ) ;
    public final void ruleRelationMultiplicityEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:416:1: ( ( ( rule__RelationMultiplicityEnum__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:417:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:417:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:418:1: ( rule__RelationMultiplicityEnum__Alternatives )
            {
             before(grammarAccess.getRelationMultiplicityEnumAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:419:1: ( rule__RelationMultiplicityEnum__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:419:2: rule__RelationMultiplicityEnum__Alternatives
            {
            pushFollow(FOLLOW_rule__RelationMultiplicityEnum__Alternatives_in_ruleRelationMultiplicityEnum850);
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


    // $ANTLR start rule__OseeType__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:430:1: rule__OseeType__Alternatives : ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) );
    public final void rule__OseeType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:434:1: ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) )
            int alt1=4;
            switch ( input.LA(1) ) {
            case 35:
            case 61:
                {
                alt1=1;
                }
                break;
            case 54:
                {
                alt1=2;
                }
                break;
            case RULE_STRING:
            case 43:
                {
                alt1=3;
                }
                break;
            case 52:
                {
                alt1=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("430:1: rule__OseeType__Alternatives : ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) );", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:435:1: ( ruleArtifactType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:435:1: ( ruleArtifactType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:436:1: ruleArtifactType
                    {
                     before(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0()); 
                    pushFollow(FOLLOW_ruleArtifactType_in_rule__OseeType__Alternatives885);
                    ruleArtifactType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:441:6: ( ruleRelationType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:441:6: ( ruleRelationType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:442:1: ruleRelationType
                    {
                     before(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1()); 
                    pushFollow(FOLLOW_ruleRelationType_in_rule__OseeType__Alternatives902);
                    ruleRelationType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:447:6: ( ruleAttributeType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:447:6: ( ruleAttributeType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:448:1: ruleAttributeType
                    {
                     before(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2()); 
                    pushFollow(FOLLOW_ruleAttributeType_in_rule__OseeType__Alternatives919);
                    ruleAttributeType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:453:6: ( ruleOseeEnumType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:453:6: ( ruleOseeEnumType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:454:1: ruleOseeEnumType
                    {
                     before(grammarAccess.getOseeTypeAccess().getOseeEnumTypeParserRuleCall_3()); 
                    pushFollow(FOLLOW_ruleOseeEnumType_in_rule__OseeType__Alternatives936);
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


    // $ANTLR start rule__AttributeType__DataProviderAlternatives_7_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:464:1: rule__AttributeType__DataProviderAlternatives_7_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( 'MappedAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeType__DataProviderAlternatives_7_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:468:1: ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( 'MappedAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) )
            int alt2=4;
            switch ( input.LA(1) ) {
            case 12:
                {
                alt2=1;
                }
                break;
            case 13:
                {
                alt2=2;
                }
                break;
            case 14:
                {
                alt2=3;
                }
                break;
            case RULE_ID:
                {
                alt2=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("464:1: rule__AttributeType__DataProviderAlternatives_7_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( 'MappedAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:469:1: ( 'DefaultAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:469:1: ( 'DefaultAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:470:1: 'DefaultAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_7_0_0()); 
                    match(input,12,FOLLOW_12_in_rule__AttributeType__DataProviderAlternatives_7_0969); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_7_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:477:6: ( 'UriAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:477:6: ( 'UriAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:478:1: 'UriAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_7_0_1()); 
                    match(input,13,FOLLOW_13_in_rule__AttributeType__DataProviderAlternatives_7_0989); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_7_0_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:485:6: ( 'MappedAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:485:6: ( 'MappedAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:486:1: 'MappedAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderMappedAttributeDataProviderKeyword_7_0_2()); 
                    match(input,14,FOLLOW_14_in_rule__AttributeType__DataProviderAlternatives_7_01009); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderMappedAttributeDataProviderKeyword_7_0_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:493:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:493:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:494:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_7_0_3()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__DataProviderAlternatives_7_01028);
                    ruleQUALIFIED_NAME();
                    _fsp--;

                     after(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_7_0_3()); 

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
    // $ANTLR end rule__AttributeType__DataProviderAlternatives_7_0


    // $ANTLR start rule__AttributeType__MaxAlternatives_11_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:504:1: rule__AttributeType__MaxAlternatives_11_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );
    public final void rule__AttributeType__MaxAlternatives_11_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:508:1: ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==RULE_WHOLE_NUM_STR) ) {
                alt3=1;
            }
            else if ( (LA3_0==15) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("504:1: rule__AttributeType__MaxAlternatives_11_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:509:1: ( RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:509:1: ( RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:510:1: RULE_WHOLE_NUM_STR
                    {
                     before(grammarAccess.getAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_11_0_0()); 
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MaxAlternatives_11_01060); 
                     after(grammarAccess.getAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_11_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:515:6: ( 'unlimited' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:515:6: ( 'unlimited' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:516:1: 'unlimited'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_11_0_1()); 
                    match(input,15,FOLLOW_15_in_rule__AttributeType__MaxAlternatives_11_01078); 
                     after(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_11_0_1()); 

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
    // $ANTLR end rule__AttributeType__MaxAlternatives_11_0


    // $ANTLR start rule__AttributeType__TaggerIdAlternatives_12_1_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:528:1: rule__AttributeType__TaggerIdAlternatives_12_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeType__TaggerIdAlternatives_12_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:532:1: ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==16) ) {
                alt4=1;
            }
            else if ( (LA4_0==RULE_ID) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("528:1: rule__AttributeType__TaggerIdAlternatives_12_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:533:1: ( 'DefaultAttributeTaggerProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:533:1: ( 'DefaultAttributeTaggerProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:534:1: 'DefaultAttributeTaggerProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_12_1_0_0()); 
                    match(input,16,FOLLOW_16_in_rule__AttributeType__TaggerIdAlternatives_12_1_01113); 
                     after(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_12_1_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:541:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:541:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:542:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_12_1_0_1()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__TaggerIdAlternatives_12_1_01132);
                    ruleQUALIFIED_NAME();
                    _fsp--;

                     after(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_12_1_0_1()); 

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
    // $ANTLR end rule__AttributeType__TaggerIdAlternatives_12_1_0


    // $ANTLR start rule__AttributeBaseType__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:552:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeBaseType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:556:1: ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) )
            int alt5=10;
            switch ( input.LA(1) ) {
            case 17:
                {
                alt5=1;
                }
                break;
            case 18:
                {
                alt5=2;
                }
                break;
            case 19:
                {
                alt5=3;
                }
                break;
            case 20:
                {
                alt5=4;
                }
                break;
            case 21:
                {
                alt5=5;
                }
                break;
            case 22:
                {
                alt5=6;
                }
                break;
            case 23:
                {
                alt5=7;
                }
                break;
            case 24:
                {
                alt5=8;
                }
                break;
            case 25:
                {
                alt5=9;
                }
                break;
            case RULE_ID:
                {
                alt5=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("552:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:557:1: ( 'BooleanAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:557:1: ( 'BooleanAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:558:1: 'BooleanAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                    match(input,17,FOLLOW_17_in_rule__AttributeBaseType__Alternatives1165); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:565:6: ( 'CompressedContentAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:565:6: ( 'CompressedContentAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:566:1: 'CompressedContentAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                    match(input,18,FOLLOW_18_in_rule__AttributeBaseType__Alternatives1185); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:573:6: ( 'DateAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:573:6: ( 'DateAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:574:1: 'DateAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                    match(input,19,FOLLOW_19_in_rule__AttributeBaseType__Alternatives1205); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:581:6: ( 'EnumeratedAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:581:6: ( 'EnumeratedAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:582:1: 'EnumeratedAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                    match(input,20,FOLLOW_20_in_rule__AttributeBaseType__Alternatives1225); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 

                    }


                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:589:6: ( 'FloatingPointAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:589:6: ( 'FloatingPointAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:590:1: 'FloatingPointAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                    match(input,21,FOLLOW_21_in_rule__AttributeBaseType__Alternatives1245); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 

                    }


                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:597:6: ( 'IntegerAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:597:6: ( 'IntegerAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:598:1: 'IntegerAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                    match(input,22,FOLLOW_22_in_rule__AttributeBaseType__Alternatives1265); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 

                    }


                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:605:6: ( 'JavaObjectAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:605:6: ( 'JavaObjectAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:606:1: 'JavaObjectAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 
                    match(input,23,FOLLOW_23_in_rule__AttributeBaseType__Alternatives1285); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 

                    }


                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:613:6: ( 'StringAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:613:6: ( 'StringAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:614:1: 'StringAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 
                    match(input,24,FOLLOW_24_in_rule__AttributeBaseType__Alternatives1305); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 

                    }


                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:621:6: ( 'WordAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:621:6: ( 'WordAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:622:1: 'WordAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 
                    match(input,25,FOLLOW_25_in_rule__AttributeBaseType__Alternatives1325); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 

                    }


                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:629:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:629:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:630:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeBaseType__Alternatives1344);
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


    // $ANTLR start rule__RelationOrderType__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:640:1: rule__RelationOrderType__Alternatives : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );
    public final void rule__RelationOrderType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:644:1: ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) )
            int alt6=4;
            switch ( input.LA(1) ) {
            case 26:
                {
                alt6=1;
                }
                break;
            case 27:
                {
                alt6=2;
                }
                break;
            case 28:
                {
                alt6=3;
                }
                break;
            case RULE_ID:
                {
                alt6=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("640:1: rule__RelationOrderType__Alternatives : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:645:1: ( 'Lexicographical_Ascending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:645:1: ( 'Lexicographical_Ascending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:646:1: 'Lexicographical_Ascending'
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 
                    match(input,26,FOLLOW_26_in_rule__RelationOrderType__Alternatives1377); 
                     after(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:653:6: ( 'Lexicographical_Descending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:653:6: ( 'Lexicographical_Descending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:654:1: 'Lexicographical_Descending'
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 
                    match(input,27,FOLLOW_27_in_rule__RelationOrderType__Alternatives1397); 
                     after(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:661:6: ( 'Unordered' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:661:6: ( 'Unordered' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:662:1: 'Unordered'
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 
                    match(input,28,FOLLOW_28_in_rule__RelationOrderType__Alternatives1417); 
                     after(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:669:6: ( RULE_ID )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:669:6: ( RULE_ID )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:670:1: RULE_ID
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getIDTerminalRuleCall_3()); 
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__RelationOrderType__Alternatives1436); 
                     after(grammarAccess.getRelationOrderTypeAccess().getIDTerminalRuleCall_3()); 

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
    // $ANTLR end rule__RelationOrderType__Alternatives


    // $ANTLR start rule__RelationMultiplicityEnum__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:680:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );
    public final void rule__RelationMultiplicityEnum__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:684:1: ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) )
            int alt7=4;
            switch ( input.LA(1) ) {
            case 29:
                {
                alt7=1;
                }
                break;
            case 30:
                {
                alt7=2;
                }
                break;
            case 31:
                {
                alt7=3;
                }
                break;
            case 32:
                {
                alt7=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("680:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:685:1: ( ( 'ONE_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:685:1: ( ( 'ONE_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:686:1: ( 'ONE_TO_ONE' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:687:1: ( 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:687:3: 'ONE_TO_ONE'
                    {
                    match(input,29,FOLLOW_29_in_rule__RelationMultiplicityEnum__Alternatives1469); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:692:6: ( ( 'ONE_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:692:6: ( ( 'ONE_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:693:1: ( 'ONE_TO_MANY' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:694:1: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:694:3: 'ONE_TO_MANY'
                    {
                    match(input,30,FOLLOW_30_in_rule__RelationMultiplicityEnum__Alternatives1490); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:699:6: ( ( 'MANY_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:699:6: ( ( 'MANY_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:700:1: ( 'MANY_TO_ONE' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:701:1: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:701:3: 'MANY_TO_ONE'
                    {
                    match(input,31,FOLLOW_31_in_rule__RelationMultiplicityEnum__Alternatives1511); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:706:6: ( ( 'MANY_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:706:6: ( ( 'MANY_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:707:1: ( 'MANY_TO_MANY' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:708:1: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:708:3: 'MANY_TO_MANY'
                    {
                    match(input,32,FOLLOW_32_in_rule__RelationMultiplicityEnum__Alternatives1532); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:720:1: rule__OseeTypeModel__Group__0 : ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1 ;
    public final void rule__OseeTypeModel__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:724:1: ( ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:725:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:725:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:726:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            {
             before(grammarAccess.getOseeTypeModelAccess().getImportsAssignment_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:727:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==33) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:727:2: rule__OseeTypeModel__ImportsAssignment_0
            	    {
            	    pushFollow(FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__01569);
            	    rule__OseeTypeModel__ImportsAssignment_0();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

             after(grammarAccess.getOseeTypeModelAccess().getImportsAssignment_0()); 

            }

            pushFollow(FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01579);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:738:1: rule__OseeTypeModel__Group__1 : ( ( rule__OseeTypeModel__TypesAssignment_1 )* ) ;
    public final void rule__OseeTypeModel__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:742:1: ( ( ( rule__OseeTypeModel__TypesAssignment_1 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:743:1: ( ( rule__OseeTypeModel__TypesAssignment_1 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:743:1: ( ( rule__OseeTypeModel__TypesAssignment_1 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:744:1: ( rule__OseeTypeModel__TypesAssignment_1 )*
            {
             before(grammarAccess.getOseeTypeModelAccess().getTypesAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:745:1: ( rule__OseeTypeModel__TypesAssignment_1 )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==RULE_STRING||LA9_0==35||LA9_0==43||LA9_0==52||LA9_0==54||LA9_0==61) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:745:2: rule__OseeTypeModel__TypesAssignment_1
            	    {
            	    pushFollow(FOLLOW_rule__OseeTypeModel__TypesAssignment_1_in_rule__OseeTypeModel__Group__11607);
            	    rule__OseeTypeModel__TypesAssignment_1();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop9;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:759:1: rule__Import__Group__0 : ( 'import' ) rule__Import__Group__1 ;
    public final void rule__Import__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:763:1: ( ( 'import' ) rule__Import__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:764:1: ( 'import' ) rule__Import__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:764:1: ( 'import' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:765:1: 'import'
            {
             before(grammarAccess.getImportAccess().getImportKeyword_0()); 
            match(input,33,FOLLOW_33_in_rule__Import__Group__01647); 
             after(grammarAccess.getImportAccess().getImportKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__Import__Group__1_in_rule__Import__Group__01657);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:779:1: rule__Import__Group__1 : ( ( rule__Import__ImportURIAssignment_1 ) ) ;
    public final void rule__Import__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:783:1: ( ( ( rule__Import__ImportURIAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:784:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:784:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:785:1: ( rule__Import__ImportURIAssignment_1 )
            {
             before(grammarAccess.getImportAccess().getImportURIAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:786:1: ( rule__Import__ImportURIAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:786:2: rule__Import__ImportURIAssignment_1
            {
            pushFollow(FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__11685);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:800:1: rule__QUALIFIED_NAME__Group__0 : ( RULE_ID ) rule__QUALIFIED_NAME__Group__1 ;
    public final void rule__QUALIFIED_NAME__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:804:1: ( ( RULE_ID ) rule__QUALIFIED_NAME__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:805:1: ( RULE_ID ) rule__QUALIFIED_NAME__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:805:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:806:1: RULE_ID
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__01723); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 

            }

            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__01731);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:818:1: rule__QUALIFIED_NAME__Group__1 : ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) ;
    public final void rule__QUALIFIED_NAME__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:822:1: ( ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:823:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:823:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:824:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:825:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==34) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:825:2: rule__QUALIFIED_NAME__Group_1__0
            	    {
            	    pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__11759);
            	    rule__QUALIFIED_NAME__Group_1__0();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop10;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:839:1: rule__QUALIFIED_NAME__Group_1__0 : ( '.' ) rule__QUALIFIED_NAME__Group_1__1 ;
    public final void rule__QUALIFIED_NAME__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:843:1: ( ( '.' ) rule__QUALIFIED_NAME__Group_1__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:844:1: ( '.' ) rule__QUALIFIED_NAME__Group_1__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:844:1: ( '.' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:845:1: '.'
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            match(input,34,FOLLOW_34_in_rule__QUALIFIED_NAME__Group_1__01799); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 

            }

            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__01809);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:859:1: rule__QUALIFIED_NAME__Group_1__1 : ( RULE_ID ) ;
    public final void rule__QUALIFIED_NAME__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:863:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:864:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:864:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:865:1: RULE_ID
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__11837); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:880:1: rule__ArtifactType__Group__0 : ( ( rule__ArtifactType__AbstractAssignment_0 )? ) rule__ArtifactType__Group__1 ;
    public final void rule__ArtifactType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:884:1: ( ( ( rule__ArtifactType__AbstractAssignment_0 )? ) rule__ArtifactType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:885:1: ( ( rule__ArtifactType__AbstractAssignment_0 )? ) rule__ArtifactType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:885:1: ( ( rule__ArtifactType__AbstractAssignment_0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:886:1: ( rule__ArtifactType__AbstractAssignment_0 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getAbstractAssignment_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:887:1: ( rule__ArtifactType__AbstractAssignment_0 )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==61) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:887:2: rule__ArtifactType__AbstractAssignment_0
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__AbstractAssignment_0_in_rule__ArtifactType__Group__01874);
                    rule__ArtifactType__AbstractAssignment_0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getAbstractAssignment_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__1_in_rule__ArtifactType__Group__01884);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:898:1: rule__ArtifactType__Group__1 : ( 'artifactType' ) rule__ArtifactType__Group__2 ;
    public final void rule__ArtifactType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:902:1: ( ( 'artifactType' ) rule__ArtifactType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:903:1: ( 'artifactType' ) rule__ArtifactType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:903:1: ( 'artifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:904:1: 'artifactType'
            {
             before(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1()); 
            match(input,35,FOLLOW_35_in_rule__ArtifactType__Group__11913); 
             after(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__2_in_rule__ArtifactType__Group__11923);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:918:1: rule__ArtifactType__Group__2 : ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3 ;
    public final void rule__ArtifactType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:922:1: ( ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:923:1: ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:923:1: ( ( rule__ArtifactType__NameAssignment_2 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:924:1: ( rule__ArtifactType__NameAssignment_2 )
            {
             before(grammarAccess.getArtifactTypeAccess().getNameAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:925:1: ( rule__ArtifactType__NameAssignment_2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:925:2: rule__ArtifactType__NameAssignment_2
            {
            pushFollow(FOLLOW_rule__ArtifactType__NameAssignment_2_in_rule__ArtifactType__Group__21951);
            rule__ArtifactType__NameAssignment_2();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getNameAssignment_2()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__3_in_rule__ArtifactType__Group__21960);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:936:1: rule__ArtifactType__Group__3 : ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4 ;
    public final void rule__ArtifactType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:940:1: ( ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:941:1: ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:941:1: ( ( rule__ArtifactType__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:942:1: ( rule__ArtifactType__Group_3__0 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:943:1: ( rule__ArtifactType__Group_3__0 )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==38) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:943:2: rule__ArtifactType__Group_3__0
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__Group_3__0_in_rule__ArtifactType__Group__31988);
                    rule__ArtifactType__Group_3__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getGroup_3()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__4_in_rule__ArtifactType__Group__31998);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:954:1: rule__ArtifactType__Group__4 : ( ( rule__ArtifactType__Group_4__0 )? ) rule__ArtifactType__Group__5 ;
    public final void rule__ArtifactType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:958:1: ( ( ( rule__ArtifactType__Group_4__0 )? ) rule__ArtifactType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:959:1: ( ( rule__ArtifactType__Group_4__0 )? ) rule__ArtifactType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:959:1: ( ( rule__ArtifactType__Group_4__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:960:1: ( rule__ArtifactType__Group_4__0 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:961:1: ( rule__ArtifactType__Group_4__0 )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==40) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:961:2: rule__ArtifactType__Group_4__0
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__Group_4__0_in_rule__ArtifactType__Group__42026);
                    rule__ArtifactType__Group_4__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getGroup_4()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__5_in_rule__ArtifactType__Group__42036);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:972:1: rule__ArtifactType__Group__5 : ( '{' ) rule__ArtifactType__Group__6 ;
    public final void rule__ArtifactType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:976:1: ( ( '{' ) rule__ArtifactType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:977:1: ( '{' ) rule__ArtifactType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:977:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:978:1: '{'
            {
             before(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_5()); 
            match(input,36,FOLLOW_36_in_rule__ArtifactType__Group__52065); 
             after(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_5()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__6_in_rule__ArtifactType__Group__52075);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:992:1: rule__ArtifactType__Group__6 : ( ( rule__ArtifactType__TypeGuidAssignment_6 )? ) rule__ArtifactType__Group__7 ;
    public final void rule__ArtifactType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:996:1: ( ( ( rule__ArtifactType__TypeGuidAssignment_6 )? ) rule__ArtifactType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:997:1: ( ( rule__ArtifactType__TypeGuidAssignment_6 )? ) rule__ArtifactType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:997:1: ( ( rule__ArtifactType__TypeGuidAssignment_6 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:998:1: ( rule__ArtifactType__TypeGuidAssignment_6 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getTypeGuidAssignment_6()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:999:1: ( rule__ArtifactType__TypeGuidAssignment_6 )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_STRING) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:999:2: rule__ArtifactType__TypeGuidAssignment_6
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__TypeGuidAssignment_6_in_rule__ArtifactType__Group__62103);
                    rule__ArtifactType__TypeGuidAssignment_6();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getTypeGuidAssignment_6()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__7_in_rule__ArtifactType__Group__62113);
            rule__ArtifactType__Group__7();
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
    // $ANTLR end rule__ArtifactType__Group__6


    // $ANTLR start rule__ArtifactType__Group__7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1010:1: rule__ArtifactType__Group__7 : ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__ArtifactType__Group__8 ;
    public final void rule__ArtifactType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1014:1: ( ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__ArtifactType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1015:1: ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__ArtifactType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1015:1: ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1016:1: ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )*
            {
             before(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAssignment_7()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1017:1: ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==41) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1017:2: rule__ArtifactType__ValidAttributeTypesAssignment_7
            	    {
            	    pushFollow(FOLLOW_rule__ArtifactType__ValidAttributeTypesAssignment_7_in_rule__ArtifactType__Group__72141);
            	    rule__ArtifactType__ValidAttributeTypesAssignment_7();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

             after(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAssignment_7()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__8_in_rule__ArtifactType__Group__72151);
            rule__ArtifactType__Group__8();
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
    // $ANTLR end rule__ArtifactType__Group__7


    // $ANTLR start rule__ArtifactType__Group__8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1028:1: rule__ArtifactType__Group__8 : ( '}' ) ;
    public final void rule__ArtifactType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1032:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1033:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1033:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1034:1: '}'
            {
             before(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_8()); 
            match(input,37,FOLLOW_37_in_rule__ArtifactType__Group__82180); 
             after(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_8()); 

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
    // $ANTLR end rule__ArtifactType__Group__8


    // $ANTLR start rule__ArtifactType__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1065:1: rule__ArtifactType__Group_3__0 : ( 'extends' ) rule__ArtifactType__Group_3__1 ;
    public final void rule__ArtifactType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1069:1: ( ( 'extends' ) rule__ArtifactType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1070:1: ( 'extends' ) rule__ArtifactType__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1070:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1071:1: 'extends'
            {
             before(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0()); 
            match(input,38,FOLLOW_38_in_rule__ArtifactType__Group_3__02234); 
             after(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_3__1_in_rule__ArtifactType__Group_3__02244);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1085:1: rule__ArtifactType__Group_3__1 : ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__ArtifactType__Group_3__2 ;
    public final void rule__ArtifactType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1089:1: ( ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__ArtifactType__Group_3__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1090:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__ArtifactType__Group_3__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1090:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1091:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1092:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1092:2: rule__ArtifactType__SuperArtifactTypesAssignment_3_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_1_in_rule__ArtifactType__Group_3__12272);
            rule__ArtifactType__SuperArtifactTypesAssignment_3_1();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_3__2_in_rule__ArtifactType__Group_3__12281);
            rule__ArtifactType__Group_3__2();
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
    // $ANTLR end rule__ArtifactType__Group_3__1


    // $ANTLR start rule__ArtifactType__Group_3__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1103:1: rule__ArtifactType__Group_3__2 : ( ( rule__ArtifactType__Group_3_2__0 )* ) ;
    public final void rule__ArtifactType__Group_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1107:1: ( ( ( rule__ArtifactType__Group_3_2__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1108:1: ( ( rule__ArtifactType__Group_3_2__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1108:1: ( ( rule__ArtifactType__Group_3_2__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1109:1: ( rule__ArtifactType__Group_3_2__0 )*
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup_3_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1110:1: ( rule__ArtifactType__Group_3_2__0 )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==39) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1110:2: rule__ArtifactType__Group_3_2__0
            	    {
            	    pushFollow(FOLLOW_rule__ArtifactType__Group_3_2__0_in_rule__ArtifactType__Group_3__22309);
            	    rule__ArtifactType__Group_3_2__0();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

             after(grammarAccess.getArtifactTypeAccess().getGroup_3_2()); 

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
    // $ANTLR end rule__ArtifactType__Group_3__2


    // $ANTLR start rule__ArtifactType__Group_3_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1126:1: rule__ArtifactType__Group_3_2__0 : ( ',' ) rule__ArtifactType__Group_3_2__1 ;
    public final void rule__ArtifactType__Group_3_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1130:1: ( ( ',' ) rule__ArtifactType__Group_3_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1131:1: ( ',' ) rule__ArtifactType__Group_3_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1131:1: ( ',' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1132:1: ','
            {
             before(grammarAccess.getArtifactTypeAccess().getCommaKeyword_3_2_0()); 
            match(input,39,FOLLOW_39_in_rule__ArtifactType__Group_3_2__02351); 
             after(grammarAccess.getArtifactTypeAccess().getCommaKeyword_3_2_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_3_2__1_in_rule__ArtifactType__Group_3_2__02361);
            rule__ArtifactType__Group_3_2__1();
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
    // $ANTLR end rule__ArtifactType__Group_3_2__0


    // $ANTLR start rule__ArtifactType__Group_3_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1146:1: rule__ArtifactType__Group_3_2__1 : ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) ;
    public final void rule__ArtifactType__Group_3_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1150:1: ( ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1151:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1151:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1152:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesAssignment_3_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1153:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1153:2: rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1_in_rule__ArtifactType__Group_3_2__12389);
            rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesAssignment_3_2_1()); 

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
    // $ANTLR end rule__ArtifactType__Group_3_2__1


    // $ANTLR start rule__ArtifactType__Group_4__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1167:1: rule__ArtifactType__Group_4__0 : ( 'overrides' ) rule__ArtifactType__Group_4__1 ;
    public final void rule__ArtifactType__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1171:1: ( ( 'overrides' ) rule__ArtifactType__Group_4__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1172:1: ( 'overrides' ) rule__ArtifactType__Group_4__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1172:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1173:1: 'overrides'
            {
             before(grammarAccess.getArtifactTypeAccess().getOverridesKeyword_4_0()); 
            match(input,40,FOLLOW_40_in_rule__ArtifactType__Group_4__02428); 
             after(grammarAccess.getArtifactTypeAccess().getOverridesKeyword_4_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_4__1_in_rule__ArtifactType__Group_4__02438);
            rule__ArtifactType__Group_4__1();
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
    // $ANTLR end rule__ArtifactType__Group_4__0


    // $ANTLR start rule__ArtifactType__Group_4__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1187:1: rule__ArtifactType__Group_4__1 : ( ( rule__ArtifactType__OverrideAssignment_4_1 ) ) ;
    public final void rule__ArtifactType__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1191:1: ( ( ( rule__ArtifactType__OverrideAssignment_4_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1192:1: ( ( rule__ArtifactType__OverrideAssignment_4_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1192:1: ( ( rule__ArtifactType__OverrideAssignment_4_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1193:1: ( rule__ArtifactType__OverrideAssignment_4_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getOverrideAssignment_4_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1194:1: ( rule__ArtifactType__OverrideAssignment_4_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1194:2: rule__ArtifactType__OverrideAssignment_4_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__OverrideAssignment_4_1_in_rule__ArtifactType__Group_4__12466);
            rule__ArtifactType__OverrideAssignment_4_1();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getOverrideAssignment_4_1()); 

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
    // $ANTLR end rule__ArtifactType__Group_4__1


    // $ANTLR start rule__AttributeTypeRef__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1208:1: rule__AttributeTypeRef__Group__0 : ( 'attribute' ) rule__AttributeTypeRef__Group__1 ;
    public final void rule__AttributeTypeRef__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1212:1: ( ( 'attribute' ) rule__AttributeTypeRef__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1213:1: ( 'attribute' ) rule__AttributeTypeRef__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1213:1: ( 'attribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1214:1: 'attribute'
            {
             before(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0()); 
            match(input,41,FOLLOW_41_in_rule__AttributeTypeRef__Group__02505); 
             after(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__1_in_rule__AttributeTypeRef__Group__02515);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1228:1: rule__AttributeTypeRef__Group__1 : ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__AttributeTypeRef__Group__2 ;
    public final void rule__AttributeTypeRef__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1232:1: ( ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__AttributeTypeRef__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1233:1: ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__AttributeTypeRef__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1233:1: ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1234:1: ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1235:1: ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1235:2: rule__AttributeTypeRef__ValidAttributeTypeAssignment_1
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__AttributeTypeRef__Group__12543);
            rule__AttributeTypeRef__ValidAttributeTypeAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__2_in_rule__AttributeTypeRef__Group__12552);
            rule__AttributeTypeRef__Group__2();
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
    // $ANTLR end rule__AttributeTypeRef__Group__1


    // $ANTLR start rule__AttributeTypeRef__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1246:1: rule__AttributeTypeRef__Group__2 : ( ( rule__AttributeTypeRef__Group_2__0 )? ) ;
    public final void rule__AttributeTypeRef__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1250:1: ( ( ( rule__AttributeTypeRef__Group_2__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1251:1: ( ( rule__AttributeTypeRef__Group_2__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1251:1: ( ( rule__AttributeTypeRef__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1252:1: ( rule__AttributeTypeRef__Group_2__0 )?
            {
             before(grammarAccess.getAttributeTypeRefAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1253:1: ( rule__AttributeTypeRef__Group_2__0 )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==42) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1253:2: rule__AttributeTypeRef__Group_2__0
                    {
                    pushFollow(FOLLOW_rule__AttributeTypeRef__Group_2__0_in_rule__AttributeTypeRef__Group__22580);
                    rule__AttributeTypeRef__Group_2__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeRefAccess().getGroup_2()); 

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
    // $ANTLR end rule__AttributeTypeRef__Group__2


    // $ANTLR start rule__AttributeTypeRef__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1269:1: rule__AttributeTypeRef__Group_2__0 : ( 'branchGuid' ) rule__AttributeTypeRef__Group_2__1 ;
    public final void rule__AttributeTypeRef__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1273:1: ( ( 'branchGuid' ) rule__AttributeTypeRef__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1274:1: ( 'branchGuid' ) rule__AttributeTypeRef__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1274:1: ( 'branchGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1275:1: 'branchGuid'
            {
             before(grammarAccess.getAttributeTypeRefAccess().getBranchGuidKeyword_2_0()); 
            match(input,42,FOLLOW_42_in_rule__AttributeTypeRef__Group_2__02622); 
             after(grammarAccess.getAttributeTypeRefAccess().getBranchGuidKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group_2__1_in_rule__AttributeTypeRef__Group_2__02632);
            rule__AttributeTypeRef__Group_2__1();
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
    // $ANTLR end rule__AttributeTypeRef__Group_2__0


    // $ANTLR start rule__AttributeTypeRef__Group_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1289:1: rule__AttributeTypeRef__Group_2__1 : ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) ) ;
    public final void rule__AttributeTypeRef__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1293:1: ( ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1294:1: ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1294:1: ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1295:1: ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getBranchGuidAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1296:1: ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1296:2: rule__AttributeTypeRef__BranchGuidAssignment_2_1
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__BranchGuidAssignment_2_1_in_rule__AttributeTypeRef__Group_2__12660);
            rule__AttributeTypeRef__BranchGuidAssignment_2_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeRefAccess().getBranchGuidAssignment_2_1()); 

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
    // $ANTLR end rule__AttributeTypeRef__Group_2__1


    // $ANTLR start rule__AttributeType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1310:1: rule__AttributeType__Group__0 : ( ( rule__AttributeType__TypeGuidAssignment_0 )? ) rule__AttributeType__Group__1 ;
    public final void rule__AttributeType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1314:1: ( ( ( rule__AttributeType__TypeGuidAssignment_0 )? ) rule__AttributeType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1315:1: ( ( rule__AttributeType__TypeGuidAssignment_0 )? ) rule__AttributeType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1315:1: ( ( rule__AttributeType__TypeGuidAssignment_0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1316:1: ( rule__AttributeType__TypeGuidAssignment_0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getTypeGuidAssignment_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1317:1: ( rule__AttributeType__TypeGuidAssignment_0 )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RULE_STRING) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1317:2: rule__AttributeType__TypeGuidAssignment_0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__TypeGuidAssignment_0_in_rule__AttributeType__Group__02698);
                    rule__AttributeType__TypeGuidAssignment_0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getTypeGuidAssignment_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__1_in_rule__AttributeType__Group__02708);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1328:1: rule__AttributeType__Group__1 : ( 'attributeType' ) rule__AttributeType__Group__2 ;
    public final void rule__AttributeType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1332:1: ( ( 'attributeType' ) rule__AttributeType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1333:1: ( 'attributeType' ) rule__AttributeType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1333:1: ( 'attributeType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1334:1: 'attributeType'
            {
             before(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_1()); 
            match(input,43,FOLLOW_43_in_rule__AttributeType__Group__12737); 
             after(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_1()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__2_in_rule__AttributeType__Group__12747);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1348:1: rule__AttributeType__Group__2 : ( ( rule__AttributeType__NameAssignment_2 ) ) rule__AttributeType__Group__3 ;
    public final void rule__AttributeType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1352:1: ( ( ( rule__AttributeType__NameAssignment_2 ) ) rule__AttributeType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1353:1: ( ( rule__AttributeType__NameAssignment_2 ) ) rule__AttributeType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1353:1: ( ( rule__AttributeType__NameAssignment_2 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1354:1: ( rule__AttributeType__NameAssignment_2 )
            {
             before(grammarAccess.getAttributeTypeAccess().getNameAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1355:1: ( rule__AttributeType__NameAssignment_2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1355:2: rule__AttributeType__NameAssignment_2
            {
            pushFollow(FOLLOW_rule__AttributeType__NameAssignment_2_in_rule__AttributeType__Group__22775);
            rule__AttributeType__NameAssignment_2();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getNameAssignment_2()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__3_in_rule__AttributeType__Group__22784);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1366:1: rule__AttributeType__Group__3 : ( ( rule__AttributeType__Group_3__0 ) ) rule__AttributeType__Group__4 ;
    public final void rule__AttributeType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1370:1: ( ( ( rule__AttributeType__Group_3__0 ) ) rule__AttributeType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1371:1: ( ( rule__AttributeType__Group_3__0 ) ) rule__AttributeType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1371:1: ( ( rule__AttributeType__Group_3__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1372:1: ( rule__AttributeType__Group_3__0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1373:1: ( rule__AttributeType__Group_3__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1373:2: rule__AttributeType__Group_3__0
            {
            pushFollow(FOLLOW_rule__AttributeType__Group_3__0_in_rule__AttributeType__Group__32812);
            rule__AttributeType__Group_3__0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_3()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__4_in_rule__AttributeType__Group__32821);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1384:1: rule__AttributeType__Group__4 : ( ( rule__AttributeType__Group_4__0 )? ) rule__AttributeType__Group__5 ;
    public final void rule__AttributeType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1388:1: ( ( ( rule__AttributeType__Group_4__0 )? ) rule__AttributeType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1389:1: ( ( rule__AttributeType__Group_4__0 )? ) rule__AttributeType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1389:1: ( ( rule__AttributeType__Group_4__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1390:1: ( rule__AttributeType__Group_4__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1391:1: ( rule__AttributeType__Group_4__0 )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==40) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1391:2: rule__AttributeType__Group_4__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_4__0_in_rule__AttributeType__Group__42849);
                    rule__AttributeType__Group_4__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_4()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__5_in_rule__AttributeType__Group__42859);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1402:1: rule__AttributeType__Group__5 : ( '{' ) rule__AttributeType__Group__6 ;
    public final void rule__AttributeType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1406:1: ( ( '{' ) rule__AttributeType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1407:1: ( '{' ) rule__AttributeType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1407:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1408:1: '{'
            {
             before(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_5()); 
            match(input,36,FOLLOW_36_in_rule__AttributeType__Group__52888); 
             after(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_5()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__6_in_rule__AttributeType__Group__52898);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1422:1: rule__AttributeType__Group__6 : ( 'dataProvider' ) rule__AttributeType__Group__7 ;
    public final void rule__AttributeType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1426:1: ( ( 'dataProvider' ) rule__AttributeType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1427:1: ( 'dataProvider' ) rule__AttributeType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1427:1: ( 'dataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1428:1: 'dataProvider'
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_6()); 
            match(input,44,FOLLOW_44_in_rule__AttributeType__Group__62927); 
             after(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_6()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__7_in_rule__AttributeType__Group__62937);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1442:1: rule__AttributeType__Group__7 : ( ( rule__AttributeType__DataProviderAssignment_7 ) ) rule__AttributeType__Group__8 ;
    public final void rule__AttributeType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1446:1: ( ( ( rule__AttributeType__DataProviderAssignment_7 ) ) rule__AttributeType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1447:1: ( ( rule__AttributeType__DataProviderAssignment_7 ) ) rule__AttributeType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1447:1: ( ( rule__AttributeType__DataProviderAssignment_7 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1448:1: ( rule__AttributeType__DataProviderAssignment_7 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderAssignment_7()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1449:1: ( rule__AttributeType__DataProviderAssignment_7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1449:2: rule__AttributeType__DataProviderAssignment_7
            {
            pushFollow(FOLLOW_rule__AttributeType__DataProviderAssignment_7_in_rule__AttributeType__Group__72965);
            rule__AttributeType__DataProviderAssignment_7();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDataProviderAssignment_7()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__8_in_rule__AttributeType__Group__72974);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1460:1: rule__AttributeType__Group__8 : ( 'min' ) rule__AttributeType__Group__9 ;
    public final void rule__AttributeType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1464:1: ( ( 'min' ) rule__AttributeType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1465:1: ( 'min' ) rule__AttributeType__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1465:1: ( 'min' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1466:1: 'min'
            {
             before(grammarAccess.getAttributeTypeAccess().getMinKeyword_8()); 
            match(input,45,FOLLOW_45_in_rule__AttributeType__Group__83003); 
             after(grammarAccess.getAttributeTypeAccess().getMinKeyword_8()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__9_in_rule__AttributeType__Group__83013);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1480:1: rule__AttributeType__Group__9 : ( ( rule__AttributeType__MinAssignment_9 ) ) rule__AttributeType__Group__10 ;
    public final void rule__AttributeType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1484:1: ( ( ( rule__AttributeType__MinAssignment_9 ) ) rule__AttributeType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1485:1: ( ( rule__AttributeType__MinAssignment_9 ) ) rule__AttributeType__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1485:1: ( ( rule__AttributeType__MinAssignment_9 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1486:1: ( rule__AttributeType__MinAssignment_9 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMinAssignment_9()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1487:1: ( rule__AttributeType__MinAssignment_9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1487:2: rule__AttributeType__MinAssignment_9
            {
            pushFollow(FOLLOW_rule__AttributeType__MinAssignment_9_in_rule__AttributeType__Group__93041);
            rule__AttributeType__MinAssignment_9();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMinAssignment_9()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__10_in_rule__AttributeType__Group__93050);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1498:1: rule__AttributeType__Group__10 : ( 'max' ) rule__AttributeType__Group__11 ;
    public final void rule__AttributeType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1502:1: ( ( 'max' ) rule__AttributeType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1503:1: ( 'max' ) rule__AttributeType__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1503:1: ( 'max' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1504:1: 'max'
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxKeyword_10()); 
            match(input,46,FOLLOW_46_in_rule__AttributeType__Group__103079); 
             after(grammarAccess.getAttributeTypeAccess().getMaxKeyword_10()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__11_in_rule__AttributeType__Group__103089);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1518:1: rule__AttributeType__Group__11 : ( ( rule__AttributeType__MaxAssignment_11 ) ) rule__AttributeType__Group__12 ;
    public final void rule__AttributeType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1522:1: ( ( ( rule__AttributeType__MaxAssignment_11 ) ) rule__AttributeType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1523:1: ( ( rule__AttributeType__MaxAssignment_11 ) ) rule__AttributeType__Group__12
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1523:1: ( ( rule__AttributeType__MaxAssignment_11 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1524:1: ( rule__AttributeType__MaxAssignment_11 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxAssignment_11()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1525:1: ( rule__AttributeType__MaxAssignment_11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1525:2: rule__AttributeType__MaxAssignment_11
            {
            pushFollow(FOLLOW_rule__AttributeType__MaxAssignment_11_in_rule__AttributeType__Group__113117);
            rule__AttributeType__MaxAssignment_11();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMaxAssignment_11()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__12_in_rule__AttributeType__Group__113126);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1536:1: rule__AttributeType__Group__12 : ( ( rule__AttributeType__Group_12__0 )? ) rule__AttributeType__Group__13 ;
    public final void rule__AttributeType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1540:1: ( ( ( rule__AttributeType__Group_12__0 )? ) rule__AttributeType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1541:1: ( ( rule__AttributeType__Group_12__0 )? ) rule__AttributeType__Group__13
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1541:1: ( ( rule__AttributeType__Group_12__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1542:1: ( rule__AttributeType__Group_12__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_12()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1543:1: ( rule__AttributeType__Group_12__0 )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==47) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1543:2: rule__AttributeType__Group_12__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_12__0_in_rule__AttributeType__Group__123154);
                    rule__AttributeType__Group_12__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_12()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__13_in_rule__AttributeType__Group__123164);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1554:1: rule__AttributeType__Group__13 : ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14 ;
    public final void rule__AttributeType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1558:1: ( ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1559:1: ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1559:1: ( ( rule__AttributeType__Group_13__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1560:1: ( rule__AttributeType__Group_13__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_13()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1561:1: ( rule__AttributeType__Group_13__0 )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==48) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1561:2: rule__AttributeType__Group_13__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_13__0_in_rule__AttributeType__Group__133192);
                    rule__AttributeType__Group_13__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_13()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__14_in_rule__AttributeType__Group__133202);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1572:1: rule__AttributeType__Group__14 : ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15 ;
    public final void rule__AttributeType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1576:1: ( ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1577:1: ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1577:1: ( ( rule__AttributeType__Group_14__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1578:1: ( rule__AttributeType__Group_14__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_14()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1579:1: ( rule__AttributeType__Group_14__0 )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==49) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1579:2: rule__AttributeType__Group_14__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_14__0_in_rule__AttributeType__Group__143230);
                    rule__AttributeType__Group_14__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_14()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__15_in_rule__AttributeType__Group__143240);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1590:1: rule__AttributeType__Group__15 : ( ( rule__AttributeType__Group_15__0 )? ) rule__AttributeType__Group__16 ;
    public final void rule__AttributeType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1594:1: ( ( ( rule__AttributeType__Group_15__0 )? ) rule__AttributeType__Group__16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1595:1: ( ( rule__AttributeType__Group_15__0 )? ) rule__AttributeType__Group__16
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1595:1: ( ( rule__AttributeType__Group_15__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1596:1: ( rule__AttributeType__Group_15__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_15()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1597:1: ( rule__AttributeType__Group_15__0 )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==50) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1597:2: rule__AttributeType__Group_15__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_15__0_in_rule__AttributeType__Group__153268);
                    rule__AttributeType__Group_15__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_15()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__16_in_rule__AttributeType__Group__153278);
            rule__AttributeType__Group__16();
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
    // $ANTLR end rule__AttributeType__Group__15


    // $ANTLR start rule__AttributeType__Group__16
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1608:1: rule__AttributeType__Group__16 : ( ( rule__AttributeType__Group_16__0 )? ) rule__AttributeType__Group__17 ;
    public final void rule__AttributeType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1612:1: ( ( ( rule__AttributeType__Group_16__0 )? ) rule__AttributeType__Group__17 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1613:1: ( ( rule__AttributeType__Group_16__0 )? ) rule__AttributeType__Group__17
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1613:1: ( ( rule__AttributeType__Group_16__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1614:1: ( rule__AttributeType__Group_16__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_16()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1615:1: ( rule__AttributeType__Group_16__0 )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==51) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1615:2: rule__AttributeType__Group_16__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_16__0_in_rule__AttributeType__Group__163306);
                    rule__AttributeType__Group_16__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_16()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__17_in_rule__AttributeType__Group__163316);
            rule__AttributeType__Group__17();
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
    // $ANTLR end rule__AttributeType__Group__16


    // $ANTLR start rule__AttributeType__Group__17
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1626:1: rule__AttributeType__Group__17 : ( '}' ) ;
    public final void rule__AttributeType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1630:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1631:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1631:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1632:1: '}'
            {
             before(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_17()); 
            match(input,37,FOLLOW_37_in_rule__AttributeType__Group__173345); 
             after(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_17()); 

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
    // $ANTLR end rule__AttributeType__Group__17


    // $ANTLR start rule__AttributeType__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1681:1: rule__AttributeType__Group_3__0 : ( 'extends' ) rule__AttributeType__Group_3__1 ;
    public final void rule__AttributeType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1685:1: ( ( 'extends' ) rule__AttributeType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1686:1: ( 'extends' ) rule__AttributeType__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1686:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1687:1: 'extends'
            {
             before(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_3_0()); 
            match(input,38,FOLLOW_38_in_rule__AttributeType__Group_3__03417); 
             after(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_3_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_3__1_in_rule__AttributeType__Group_3__03427);
            rule__AttributeType__Group_3__1();
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
    // $ANTLR end rule__AttributeType__Group_3__0


    // $ANTLR start rule__AttributeType__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1701:1: rule__AttributeType__Group_3__1 : ( ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 ) ) ;
    public final void rule__AttributeType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1705:1: ( ( ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1706:1: ( ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1706:1: ( ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1707:1: ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAssignment_3_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1708:1: ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1708:2: rule__AttributeType__BaseAttributeTypeAssignment_3_1
            {
            pushFollow(FOLLOW_rule__AttributeType__BaseAttributeTypeAssignment_3_1_in_rule__AttributeType__Group_3__13455);
            rule__AttributeType__BaseAttributeTypeAssignment_3_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAssignment_3_1()); 

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
    // $ANTLR end rule__AttributeType__Group_3__1


    // $ANTLR start rule__AttributeType__Group_4__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1722:1: rule__AttributeType__Group_4__0 : ( 'overrides' ) rule__AttributeType__Group_4__1 ;
    public final void rule__AttributeType__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1726:1: ( ( 'overrides' ) rule__AttributeType__Group_4__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1727:1: ( 'overrides' ) rule__AttributeType__Group_4__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1727:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1728:1: 'overrides'
            {
             before(grammarAccess.getAttributeTypeAccess().getOverridesKeyword_4_0()); 
            match(input,40,FOLLOW_40_in_rule__AttributeType__Group_4__03494); 
             after(grammarAccess.getAttributeTypeAccess().getOverridesKeyword_4_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_4__1_in_rule__AttributeType__Group_4__03504);
            rule__AttributeType__Group_4__1();
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
    // $ANTLR end rule__AttributeType__Group_4__0


    // $ANTLR start rule__AttributeType__Group_4__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1742:1: rule__AttributeType__Group_4__1 : ( ( rule__AttributeType__OverrideAssignment_4_1 ) ) ;
    public final void rule__AttributeType__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1746:1: ( ( ( rule__AttributeType__OverrideAssignment_4_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1747:1: ( ( rule__AttributeType__OverrideAssignment_4_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1747:1: ( ( rule__AttributeType__OverrideAssignment_4_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1748:1: ( rule__AttributeType__OverrideAssignment_4_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getOverrideAssignment_4_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1749:1: ( rule__AttributeType__OverrideAssignment_4_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1749:2: rule__AttributeType__OverrideAssignment_4_1
            {
            pushFollow(FOLLOW_rule__AttributeType__OverrideAssignment_4_1_in_rule__AttributeType__Group_4__13532);
            rule__AttributeType__OverrideAssignment_4_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getOverrideAssignment_4_1()); 

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
    // $ANTLR end rule__AttributeType__Group_4__1


    // $ANTLR start rule__AttributeType__Group_12__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1763:1: rule__AttributeType__Group_12__0 : ( 'taggerId' ) rule__AttributeType__Group_12__1 ;
    public final void rule__AttributeType__Group_12__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1767:1: ( ( 'taggerId' ) rule__AttributeType__Group_12__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1768:1: ( 'taggerId' ) rule__AttributeType__Group_12__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1768:1: ( 'taggerId' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1769:1: 'taggerId'
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_12_0()); 
            match(input,47,FOLLOW_47_in_rule__AttributeType__Group_12__03571); 
             after(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_12_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_12__1_in_rule__AttributeType__Group_12__03581);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1783:1: rule__AttributeType__Group_12__1 : ( ( rule__AttributeType__TaggerIdAssignment_12_1 ) ) ;
    public final void rule__AttributeType__Group_12__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1787:1: ( ( ( rule__AttributeType__TaggerIdAssignment_12_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1788:1: ( ( rule__AttributeType__TaggerIdAssignment_12_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1788:1: ( ( rule__AttributeType__TaggerIdAssignment_12_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1789:1: ( rule__AttributeType__TaggerIdAssignment_12_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdAssignment_12_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1790:1: ( rule__AttributeType__TaggerIdAssignment_12_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1790:2: rule__AttributeType__TaggerIdAssignment_12_1
            {
            pushFollow(FOLLOW_rule__AttributeType__TaggerIdAssignment_12_1_in_rule__AttributeType__Group_12__13609);
            rule__AttributeType__TaggerIdAssignment_12_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getTaggerIdAssignment_12_1()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1804:1: rule__AttributeType__Group_13__0 : ( 'enumType' ) rule__AttributeType__Group_13__1 ;
    public final void rule__AttributeType__Group_13__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1808:1: ( ( 'enumType' ) rule__AttributeType__Group_13__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1809:1: ( 'enumType' ) rule__AttributeType__Group_13__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1809:1: ( 'enumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1810:1: 'enumType'
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_13_0()); 
            match(input,48,FOLLOW_48_in_rule__AttributeType__Group_13__03648); 
             after(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_13_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_13__1_in_rule__AttributeType__Group_13__03658);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1824:1: rule__AttributeType__Group_13__1 : ( ( rule__AttributeType__EnumTypeAssignment_13_1 ) ) ;
    public final void rule__AttributeType__Group_13__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1828:1: ( ( ( rule__AttributeType__EnumTypeAssignment_13_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1829:1: ( ( rule__AttributeType__EnumTypeAssignment_13_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1829:1: ( ( rule__AttributeType__EnumTypeAssignment_13_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1830:1: ( rule__AttributeType__EnumTypeAssignment_13_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeAssignment_13_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1831:1: ( rule__AttributeType__EnumTypeAssignment_13_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1831:2: rule__AttributeType__EnumTypeAssignment_13_1
            {
            pushFollow(FOLLOW_rule__AttributeType__EnumTypeAssignment_13_1_in_rule__AttributeType__Group_13__13686);
            rule__AttributeType__EnumTypeAssignment_13_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getEnumTypeAssignment_13_1()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1845:1: rule__AttributeType__Group_14__0 : ( 'description' ) rule__AttributeType__Group_14__1 ;
    public final void rule__AttributeType__Group_14__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1849:1: ( ( 'description' ) rule__AttributeType__Group_14__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1850:1: ( 'description' ) rule__AttributeType__Group_14__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1850:1: ( 'description' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1851:1: 'description'
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_14_0()); 
            match(input,49,FOLLOW_49_in_rule__AttributeType__Group_14__03725); 
             after(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_14_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_14__1_in_rule__AttributeType__Group_14__03735);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1865:1: rule__AttributeType__Group_14__1 : ( ( rule__AttributeType__DescriptionAssignment_14_1 ) ) ;
    public final void rule__AttributeType__Group_14__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1869:1: ( ( ( rule__AttributeType__DescriptionAssignment_14_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1870:1: ( ( rule__AttributeType__DescriptionAssignment_14_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1870:1: ( ( rule__AttributeType__DescriptionAssignment_14_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1871:1: ( rule__AttributeType__DescriptionAssignment_14_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionAssignment_14_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1872:1: ( rule__AttributeType__DescriptionAssignment_14_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1872:2: rule__AttributeType__DescriptionAssignment_14_1
            {
            pushFollow(FOLLOW_rule__AttributeType__DescriptionAssignment_14_1_in_rule__AttributeType__Group_14__13763);
            rule__AttributeType__DescriptionAssignment_14_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDescriptionAssignment_14_1()); 

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


    // $ANTLR start rule__AttributeType__Group_15__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1886:1: rule__AttributeType__Group_15__0 : ( 'defaultValue' ) rule__AttributeType__Group_15__1 ;
    public final void rule__AttributeType__Group_15__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1890:1: ( ( 'defaultValue' ) rule__AttributeType__Group_15__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1891:1: ( 'defaultValue' ) rule__AttributeType__Group_15__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1891:1: ( 'defaultValue' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1892:1: 'defaultValue'
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_15_0()); 
            match(input,50,FOLLOW_50_in_rule__AttributeType__Group_15__03802); 
             after(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_15_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_15__1_in_rule__AttributeType__Group_15__03812);
            rule__AttributeType__Group_15__1();
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
    // $ANTLR end rule__AttributeType__Group_15__0


    // $ANTLR start rule__AttributeType__Group_15__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1906:1: rule__AttributeType__Group_15__1 : ( ( rule__AttributeType__DefaultValueAssignment_15_1 ) ) ;
    public final void rule__AttributeType__Group_15__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1910:1: ( ( ( rule__AttributeType__DefaultValueAssignment_15_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1911:1: ( ( rule__AttributeType__DefaultValueAssignment_15_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1911:1: ( ( rule__AttributeType__DefaultValueAssignment_15_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1912:1: ( rule__AttributeType__DefaultValueAssignment_15_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueAssignment_15_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1913:1: ( rule__AttributeType__DefaultValueAssignment_15_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1913:2: rule__AttributeType__DefaultValueAssignment_15_1
            {
            pushFollow(FOLLOW_rule__AttributeType__DefaultValueAssignment_15_1_in_rule__AttributeType__Group_15__13840);
            rule__AttributeType__DefaultValueAssignment_15_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDefaultValueAssignment_15_1()); 

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
    // $ANTLR end rule__AttributeType__Group_15__1


    // $ANTLR start rule__AttributeType__Group_16__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1927:1: rule__AttributeType__Group_16__0 : ( 'fileExtension' ) rule__AttributeType__Group_16__1 ;
    public final void rule__AttributeType__Group_16__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1931:1: ( ( 'fileExtension' ) rule__AttributeType__Group_16__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1932:1: ( 'fileExtension' ) rule__AttributeType__Group_16__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1932:1: ( 'fileExtension' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1933:1: 'fileExtension'
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_16_0()); 
            match(input,51,FOLLOW_51_in_rule__AttributeType__Group_16__03879); 
             after(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_16_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_16__1_in_rule__AttributeType__Group_16__03889);
            rule__AttributeType__Group_16__1();
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
    // $ANTLR end rule__AttributeType__Group_16__0


    // $ANTLR start rule__AttributeType__Group_16__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1947:1: rule__AttributeType__Group_16__1 : ( ( rule__AttributeType__FileExtensionAssignment_16_1 ) ) ;
    public final void rule__AttributeType__Group_16__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1951:1: ( ( ( rule__AttributeType__FileExtensionAssignment_16_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1952:1: ( ( rule__AttributeType__FileExtensionAssignment_16_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1952:1: ( ( rule__AttributeType__FileExtensionAssignment_16_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1953:1: ( rule__AttributeType__FileExtensionAssignment_16_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionAssignment_16_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1954:1: ( rule__AttributeType__FileExtensionAssignment_16_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1954:2: rule__AttributeType__FileExtensionAssignment_16_1
            {
            pushFollow(FOLLOW_rule__AttributeType__FileExtensionAssignment_16_1_in_rule__AttributeType__Group_16__13917);
            rule__AttributeType__FileExtensionAssignment_16_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getFileExtensionAssignment_16_1()); 

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
    // $ANTLR end rule__AttributeType__Group_16__1


    // $ANTLR start rule__OseeEnumType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1968:1: rule__OseeEnumType__Group__0 : ( 'oseeEnumType' ) rule__OseeEnumType__Group__1 ;
    public final void rule__OseeEnumType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1972:1: ( ( 'oseeEnumType' ) rule__OseeEnumType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1973:1: ( 'oseeEnumType' ) rule__OseeEnumType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1973:1: ( 'oseeEnumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1974:1: 'oseeEnumType'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 
            match(input,52,FOLLOW_52_in_rule__OseeEnumType__Group__03956); 
             after(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__1_in_rule__OseeEnumType__Group__03966);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1988:1: rule__OseeEnumType__Group__1 : ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2 ;
    public final void rule__OseeEnumType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1992:1: ( ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1993:1: ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1993:1: ( ( rule__OseeEnumType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1994:1: ( rule__OseeEnumType__NameAssignment_1 )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1995:1: ( rule__OseeEnumType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1995:2: rule__OseeEnumType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__OseeEnumType__NameAssignment_1_in_rule__OseeEnumType__Group__13994);
            rule__OseeEnumType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__2_in_rule__OseeEnumType__Group__14003);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2006:1: rule__OseeEnumType__Group__2 : ( ( rule__OseeEnumType__Group_2__0 )? ) rule__OseeEnumType__Group__3 ;
    public final void rule__OseeEnumType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2010:1: ( ( ( rule__OseeEnumType__Group_2__0 )? ) rule__OseeEnumType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2011:1: ( ( rule__OseeEnumType__Group_2__0 )? ) rule__OseeEnumType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2011:1: ( ( rule__OseeEnumType__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2012:1: ( rule__OseeEnumType__Group_2__0 )?
            {
             before(grammarAccess.getOseeEnumTypeAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2013:1: ( rule__OseeEnumType__Group_2__0 )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==40) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2013:2: rule__OseeEnumType__Group_2__0
                    {
                    pushFollow(FOLLOW_rule__OseeEnumType__Group_2__0_in_rule__OseeEnumType__Group__24031);
                    rule__OseeEnumType__Group_2__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOseeEnumTypeAccess().getGroup_2()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__3_in_rule__OseeEnumType__Group__24041);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2024:1: rule__OseeEnumType__Group__3 : ( '{' ) rule__OseeEnumType__Group__4 ;
    public final void rule__OseeEnumType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2028:1: ( ( '{' ) rule__OseeEnumType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2029:1: ( '{' ) rule__OseeEnumType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2029:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2030:1: '{'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_3()); 
            match(input,36,FOLLOW_36_in_rule__OseeEnumType__Group__34070); 
             after(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_3()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__4_in_rule__OseeEnumType__Group__34080);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2044:1: rule__OseeEnumType__Group__4 : ( ( rule__OseeEnumType__TypeGuidAssignment_4 )? ) rule__OseeEnumType__Group__5 ;
    public final void rule__OseeEnumType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2048:1: ( ( ( rule__OseeEnumType__TypeGuidAssignment_4 )? ) rule__OseeEnumType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2049:1: ( ( rule__OseeEnumType__TypeGuidAssignment_4 )? ) rule__OseeEnumType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2049:1: ( ( rule__OseeEnumType__TypeGuidAssignment_4 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2050:1: ( rule__OseeEnumType__TypeGuidAssignment_4 )?
            {
             before(grammarAccess.getOseeEnumTypeAccess().getTypeGuidAssignment_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2051:1: ( rule__OseeEnumType__TypeGuidAssignment_4 )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==RULE_STRING) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2051:2: rule__OseeEnumType__TypeGuidAssignment_4
                    {
                    pushFollow(FOLLOW_rule__OseeEnumType__TypeGuidAssignment_4_in_rule__OseeEnumType__Group__44108);
                    rule__OseeEnumType__TypeGuidAssignment_4();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOseeEnumTypeAccess().getTypeGuidAssignment_4()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__5_in_rule__OseeEnumType__Group__44118);
            rule__OseeEnumType__Group__5();
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
    // $ANTLR end rule__OseeEnumType__Group__4


    // $ANTLR start rule__OseeEnumType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2062:1: rule__OseeEnumType__Group__5 : ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* ) rule__OseeEnumType__Group__6 ;
    public final void rule__OseeEnumType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2066:1: ( ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* ) rule__OseeEnumType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2067:1: ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* ) rule__OseeEnumType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2067:1: ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2068:1: ( rule__OseeEnumType__EnumEntriesAssignment_5 )*
            {
             before(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesAssignment_5()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2069:1: ( rule__OseeEnumType__EnumEntriesAssignment_5 )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==53) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2069:2: rule__OseeEnumType__EnumEntriesAssignment_5
            	    {
            	    pushFollow(FOLLOW_rule__OseeEnumType__EnumEntriesAssignment_5_in_rule__OseeEnumType__Group__54146);
            	    rule__OseeEnumType__EnumEntriesAssignment_5();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);

             after(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesAssignment_5()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__6_in_rule__OseeEnumType__Group__54156);
            rule__OseeEnumType__Group__6();
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
    // $ANTLR end rule__OseeEnumType__Group__5


    // $ANTLR start rule__OseeEnumType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2080:1: rule__OseeEnumType__Group__6 : ( '}' ) ;
    public final void rule__OseeEnumType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2084:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2085:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2085:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2086:1: '}'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getRightCurlyBracketKeyword_6()); 
            match(input,37,FOLLOW_37_in_rule__OseeEnumType__Group__64185); 
             after(grammarAccess.getOseeEnumTypeAccess().getRightCurlyBracketKeyword_6()); 

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
    // $ANTLR end rule__OseeEnumType__Group__6


    // $ANTLR start rule__OseeEnumType__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2113:1: rule__OseeEnumType__Group_2__0 : ( 'overrides' ) rule__OseeEnumType__Group_2__1 ;
    public final void rule__OseeEnumType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2117:1: ( ( 'overrides' ) rule__OseeEnumType__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2118:1: ( 'overrides' ) rule__OseeEnumType__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2118:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2119:1: 'overrides'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOverridesKeyword_2_0()); 
            match(input,40,FOLLOW_40_in_rule__OseeEnumType__Group_2__04235); 
             after(grammarAccess.getOseeEnumTypeAccess().getOverridesKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group_2__1_in_rule__OseeEnumType__Group_2__04245);
            rule__OseeEnumType__Group_2__1();
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
    // $ANTLR end rule__OseeEnumType__Group_2__0


    // $ANTLR start rule__OseeEnumType__Group_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2133:1: rule__OseeEnumType__Group_2__1 : ( ( rule__OseeEnumType__OverrideAssignment_2_1 ) ) ;
    public final void rule__OseeEnumType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2137:1: ( ( ( rule__OseeEnumType__OverrideAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2138:1: ( ( rule__OseeEnumType__OverrideAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2138:1: ( ( rule__OseeEnumType__OverrideAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2139:1: ( rule__OseeEnumType__OverrideAssignment_2_1 )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOverrideAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2140:1: ( rule__OseeEnumType__OverrideAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2140:2: rule__OseeEnumType__OverrideAssignment_2_1
            {
            pushFollow(FOLLOW_rule__OseeEnumType__OverrideAssignment_2_1_in_rule__OseeEnumType__Group_2__14273);
            rule__OseeEnumType__OverrideAssignment_2_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumTypeAccess().getOverrideAssignment_2_1()); 

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
    // $ANTLR end rule__OseeEnumType__Group_2__1


    // $ANTLR start rule__OseeEnumEntry__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2154:1: rule__OseeEnumEntry__Group__0 : ( 'entry' ) rule__OseeEnumEntry__Group__1 ;
    public final void rule__OseeEnumEntry__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2158:1: ( ( 'entry' ) rule__OseeEnumEntry__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2159:1: ( 'entry' ) rule__OseeEnumEntry__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2159:1: ( 'entry' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2160:1: 'entry'
            {
             before(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0()); 
            match(input,53,FOLLOW_53_in_rule__OseeEnumEntry__Group__04312); 
             after(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__1_in_rule__OseeEnumEntry__Group__04322);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2174:1: rule__OseeEnumEntry__Group__1 : ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2 ;
    public final void rule__OseeEnumEntry__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2178:1: ( ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2179:1: ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2179:1: ( ( rule__OseeEnumEntry__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2180:1: ( rule__OseeEnumEntry__NameAssignment_1 )
            {
             before(grammarAccess.getOseeEnumEntryAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2181:1: ( rule__OseeEnumEntry__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2181:2: rule__OseeEnumEntry__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__OseeEnumEntry__NameAssignment_1_in_rule__OseeEnumEntry__Group__14350);
            rule__OseeEnumEntry__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumEntryAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__2_in_rule__OseeEnumEntry__Group__14359);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2192:1: rule__OseeEnumEntry__Group__2 : ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? ) ;
    public final void rule__OseeEnumEntry__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2196:1: ( ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2197:1: ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2197:1: ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2198:1: ( rule__OseeEnumEntry__OrdinalAssignment_2 )?
            {
             before(grammarAccess.getOseeEnumEntryAccess().getOrdinalAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2199:1: ( rule__OseeEnumEntry__OrdinalAssignment_2 )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==RULE_WHOLE_NUM_STR) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2199:2: rule__OseeEnumEntry__OrdinalAssignment_2
                    {
                    pushFollow(FOLLOW_rule__OseeEnumEntry__OrdinalAssignment_2_in_rule__OseeEnumEntry__Group__24387);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2215:1: rule__RelationType__Group__0 : ( 'relationType' ) rule__RelationType__Group__1 ;
    public final void rule__RelationType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2219:1: ( ( 'relationType' ) rule__RelationType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2220:1: ( 'relationType' ) rule__RelationType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2220:1: ( 'relationType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2221:1: 'relationType'
            {
             before(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0()); 
            match(input,54,FOLLOW_54_in_rule__RelationType__Group__04429); 
             after(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__1_in_rule__RelationType__Group__04439);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2235:1: rule__RelationType__Group__1 : ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2 ;
    public final void rule__RelationType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2239:1: ( ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2240:1: ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2240:1: ( ( rule__RelationType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2241:1: ( rule__RelationType__NameAssignment_1 )
            {
             before(grammarAccess.getRelationTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2242:1: ( rule__RelationType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2242:2: rule__RelationType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__RelationType__NameAssignment_1_in_rule__RelationType__Group__14467);
            rule__RelationType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__2_in_rule__RelationType__Group__14476);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2253:1: rule__RelationType__Group__2 : ( ( rule__RelationType__Group_2__0 )? ) rule__RelationType__Group__3 ;
    public final void rule__RelationType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2257:1: ( ( ( rule__RelationType__Group_2__0 )? ) rule__RelationType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2258:1: ( ( rule__RelationType__Group_2__0 )? ) rule__RelationType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2258:1: ( ( rule__RelationType__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2259:1: ( rule__RelationType__Group_2__0 )?
            {
             before(grammarAccess.getRelationTypeAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2260:1: ( rule__RelationType__Group_2__0 )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==40) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2260:2: rule__RelationType__Group_2__0
                    {
                    pushFollow(FOLLOW_rule__RelationType__Group_2__0_in_rule__RelationType__Group__24504);
                    rule__RelationType__Group_2__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getRelationTypeAccess().getGroup_2()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__3_in_rule__RelationType__Group__24514);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2271:1: rule__RelationType__Group__3 : ( '{' ) rule__RelationType__Group__4 ;
    public final void rule__RelationType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2275:1: ( ( '{' ) rule__RelationType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2276:1: ( '{' ) rule__RelationType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2276:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2277:1: '{'
            {
             before(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_3()); 
            match(input,36,FOLLOW_36_in_rule__RelationType__Group__34543); 
             after(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_3()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__4_in_rule__RelationType__Group__34553);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2291:1: rule__RelationType__Group__4 : ( ( rule__RelationType__TypeGuidAssignment_4 )? ) rule__RelationType__Group__5 ;
    public final void rule__RelationType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2295:1: ( ( ( rule__RelationType__TypeGuidAssignment_4 )? ) rule__RelationType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2296:1: ( ( rule__RelationType__TypeGuidAssignment_4 )? ) rule__RelationType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2296:1: ( ( rule__RelationType__TypeGuidAssignment_4 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2297:1: ( rule__RelationType__TypeGuidAssignment_4 )?
            {
             before(grammarAccess.getRelationTypeAccess().getTypeGuidAssignment_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2298:1: ( rule__RelationType__TypeGuidAssignment_4 )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==RULE_STRING) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2298:2: rule__RelationType__TypeGuidAssignment_4
                    {
                    pushFollow(FOLLOW_rule__RelationType__TypeGuidAssignment_4_in_rule__RelationType__Group__44581);
                    rule__RelationType__TypeGuidAssignment_4();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getRelationTypeAccess().getTypeGuidAssignment_4()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__5_in_rule__RelationType__Group__44591);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2309:1: rule__RelationType__Group__5 : ( 'sideAName' ) rule__RelationType__Group__6 ;
    public final void rule__RelationType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2313:1: ( ( 'sideAName' ) rule__RelationType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2314:1: ( 'sideAName' ) rule__RelationType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2314:1: ( 'sideAName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2315:1: 'sideAName'
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameKeyword_5()); 
            match(input,55,FOLLOW_55_in_rule__RelationType__Group__54620); 
             after(grammarAccess.getRelationTypeAccess().getSideANameKeyword_5()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__6_in_rule__RelationType__Group__54630);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2329:1: rule__RelationType__Group__6 : ( ( rule__RelationType__SideANameAssignment_6 ) ) rule__RelationType__Group__7 ;
    public final void rule__RelationType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2333:1: ( ( ( rule__RelationType__SideANameAssignment_6 ) ) rule__RelationType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2334:1: ( ( rule__RelationType__SideANameAssignment_6 ) ) rule__RelationType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2334:1: ( ( rule__RelationType__SideANameAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2335:1: ( rule__RelationType__SideANameAssignment_6 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameAssignment_6()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2336:1: ( rule__RelationType__SideANameAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2336:2: rule__RelationType__SideANameAssignment_6
            {
            pushFollow(FOLLOW_rule__RelationType__SideANameAssignment_6_in_rule__RelationType__Group__64658);
            rule__RelationType__SideANameAssignment_6();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideANameAssignment_6()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__7_in_rule__RelationType__Group__64667);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2347:1: rule__RelationType__Group__7 : ( 'sideAArtifactType' ) rule__RelationType__Group__8 ;
    public final void rule__RelationType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2351:1: ( ( 'sideAArtifactType' ) rule__RelationType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2352:1: ( 'sideAArtifactType' ) rule__RelationType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2352:1: ( 'sideAArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2353:1: 'sideAArtifactType'
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_7()); 
            match(input,56,FOLLOW_56_in_rule__RelationType__Group__74696); 
             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_7()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__8_in_rule__RelationType__Group__74706);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2367:1: rule__RelationType__Group__8 : ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) ) rule__RelationType__Group__9 ;
    public final void rule__RelationType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2371:1: ( ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) ) rule__RelationType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2372:1: ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) ) rule__RelationType__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2372:1: ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2373:1: ( rule__RelationType__SideAArtifactTypeAssignment_8 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeAssignment_8()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2374:1: ( rule__RelationType__SideAArtifactTypeAssignment_8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2374:2: rule__RelationType__SideAArtifactTypeAssignment_8
            {
            pushFollow(FOLLOW_rule__RelationType__SideAArtifactTypeAssignment_8_in_rule__RelationType__Group__84734);
            rule__RelationType__SideAArtifactTypeAssignment_8();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeAssignment_8()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__9_in_rule__RelationType__Group__84743);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2385:1: rule__RelationType__Group__9 : ( 'sideBName' ) rule__RelationType__Group__10 ;
    public final void rule__RelationType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2389:1: ( ( 'sideBName' ) rule__RelationType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2390:1: ( 'sideBName' ) rule__RelationType__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2390:1: ( 'sideBName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2391:1: 'sideBName'
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_9()); 
            match(input,57,FOLLOW_57_in_rule__RelationType__Group__94772); 
             after(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_9()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__10_in_rule__RelationType__Group__94782);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2405:1: rule__RelationType__Group__10 : ( ( rule__RelationType__SideBNameAssignment_10 ) ) rule__RelationType__Group__11 ;
    public final void rule__RelationType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2409:1: ( ( ( rule__RelationType__SideBNameAssignment_10 ) ) rule__RelationType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2410:1: ( ( rule__RelationType__SideBNameAssignment_10 ) ) rule__RelationType__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2410:1: ( ( rule__RelationType__SideBNameAssignment_10 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2411:1: ( rule__RelationType__SideBNameAssignment_10 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameAssignment_10()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2412:1: ( rule__RelationType__SideBNameAssignment_10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2412:2: rule__RelationType__SideBNameAssignment_10
            {
            pushFollow(FOLLOW_rule__RelationType__SideBNameAssignment_10_in_rule__RelationType__Group__104810);
            rule__RelationType__SideBNameAssignment_10();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideBNameAssignment_10()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__11_in_rule__RelationType__Group__104819);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2423:1: rule__RelationType__Group__11 : ( 'sideBArtifactType' ) rule__RelationType__Group__12 ;
    public final void rule__RelationType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2427:1: ( ( 'sideBArtifactType' ) rule__RelationType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2428:1: ( 'sideBArtifactType' ) rule__RelationType__Group__12
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2428:1: ( 'sideBArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2429:1: 'sideBArtifactType'
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_11()); 
            match(input,58,FOLLOW_58_in_rule__RelationType__Group__114848); 
             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_11()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__12_in_rule__RelationType__Group__114858);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2443:1: rule__RelationType__Group__12 : ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) ) rule__RelationType__Group__13 ;
    public final void rule__RelationType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2447:1: ( ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) ) rule__RelationType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2448:1: ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) ) rule__RelationType__Group__13
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2448:1: ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2449:1: ( rule__RelationType__SideBArtifactTypeAssignment_12 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeAssignment_12()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2450:1: ( rule__RelationType__SideBArtifactTypeAssignment_12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2450:2: rule__RelationType__SideBArtifactTypeAssignment_12
            {
            pushFollow(FOLLOW_rule__RelationType__SideBArtifactTypeAssignment_12_in_rule__RelationType__Group__124886);
            rule__RelationType__SideBArtifactTypeAssignment_12();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeAssignment_12()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__13_in_rule__RelationType__Group__124895);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2461:1: rule__RelationType__Group__13 : ( 'defaultOrderType' ) rule__RelationType__Group__14 ;
    public final void rule__RelationType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2465:1: ( ( 'defaultOrderType' ) rule__RelationType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2466:1: ( 'defaultOrderType' ) rule__RelationType__Group__14
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2466:1: ( 'defaultOrderType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2467:1: 'defaultOrderType'
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_13()); 
            match(input,59,FOLLOW_59_in_rule__RelationType__Group__134924); 
             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_13()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__14_in_rule__RelationType__Group__134934);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2481:1: rule__RelationType__Group__14 : ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) ) rule__RelationType__Group__15 ;
    public final void rule__RelationType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2485:1: ( ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) ) rule__RelationType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2486:1: ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) ) rule__RelationType__Group__15
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2486:1: ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2487:1: ( rule__RelationType__DefaultOrderTypeAssignment_14 )
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAssignment_14()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2488:1: ( rule__RelationType__DefaultOrderTypeAssignment_14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2488:2: rule__RelationType__DefaultOrderTypeAssignment_14
            {
            pushFollow(FOLLOW_rule__RelationType__DefaultOrderTypeAssignment_14_in_rule__RelationType__Group__144962);
            rule__RelationType__DefaultOrderTypeAssignment_14();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAssignment_14()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__15_in_rule__RelationType__Group__144971);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2499:1: rule__RelationType__Group__15 : ( 'multiplicity' ) rule__RelationType__Group__16 ;
    public final void rule__RelationType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2503:1: ( ( 'multiplicity' ) rule__RelationType__Group__16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2504:1: ( 'multiplicity' ) rule__RelationType__Group__16
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2504:1: ( 'multiplicity' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2505:1: 'multiplicity'
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_15()); 
            match(input,60,FOLLOW_60_in_rule__RelationType__Group__155000); 
             after(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_15()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__16_in_rule__RelationType__Group__155010);
            rule__RelationType__Group__16();
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
    // $ANTLR end rule__RelationType__Group__15


    // $ANTLR start rule__RelationType__Group__16
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2519:1: rule__RelationType__Group__16 : ( ( rule__RelationType__MultiplicityAssignment_16 ) ) rule__RelationType__Group__17 ;
    public final void rule__RelationType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2523:1: ( ( ( rule__RelationType__MultiplicityAssignment_16 ) ) rule__RelationType__Group__17 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2524:1: ( ( rule__RelationType__MultiplicityAssignment_16 ) ) rule__RelationType__Group__17
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2524:1: ( ( rule__RelationType__MultiplicityAssignment_16 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2525:1: ( rule__RelationType__MultiplicityAssignment_16 )
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityAssignment_16()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2526:1: ( rule__RelationType__MultiplicityAssignment_16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2526:2: rule__RelationType__MultiplicityAssignment_16
            {
            pushFollow(FOLLOW_rule__RelationType__MultiplicityAssignment_16_in_rule__RelationType__Group__165038);
            rule__RelationType__MultiplicityAssignment_16();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getMultiplicityAssignment_16()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__17_in_rule__RelationType__Group__165047);
            rule__RelationType__Group__17();
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
    // $ANTLR end rule__RelationType__Group__16


    // $ANTLR start rule__RelationType__Group__17
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2537:1: rule__RelationType__Group__17 : ( '}' ) ;
    public final void rule__RelationType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2541:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2542:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2542:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2543:1: '}'
            {
             before(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_17()); 
            match(input,37,FOLLOW_37_in_rule__RelationType__Group__175076); 
             after(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_17()); 

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
    // $ANTLR end rule__RelationType__Group__17


    // $ANTLR start rule__RelationType__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2592:1: rule__RelationType__Group_2__0 : ( 'overrides' ) rule__RelationType__Group_2__1 ;
    public final void rule__RelationType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2596:1: ( ( 'overrides' ) rule__RelationType__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2597:1: ( 'overrides' ) rule__RelationType__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2597:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2598:1: 'overrides'
            {
             before(grammarAccess.getRelationTypeAccess().getOverridesKeyword_2_0()); 
            match(input,40,FOLLOW_40_in_rule__RelationType__Group_2__05148); 
             after(grammarAccess.getRelationTypeAccess().getOverridesKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group_2__1_in_rule__RelationType__Group_2__05158);
            rule__RelationType__Group_2__1();
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
    // $ANTLR end rule__RelationType__Group_2__0


    // $ANTLR start rule__RelationType__Group_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2612:1: rule__RelationType__Group_2__1 : ( ( rule__RelationType__OverrideAssignment_2_1 ) ) ;
    public final void rule__RelationType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2616:1: ( ( ( rule__RelationType__OverrideAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2617:1: ( ( rule__RelationType__OverrideAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2617:1: ( ( rule__RelationType__OverrideAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2618:1: ( rule__RelationType__OverrideAssignment_2_1 )
            {
             before(grammarAccess.getRelationTypeAccess().getOverrideAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2619:1: ( rule__RelationType__OverrideAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2619:2: rule__RelationType__OverrideAssignment_2_1
            {
            pushFollow(FOLLOW_rule__RelationType__OverrideAssignment_2_1_in_rule__RelationType__Group_2__15186);
            rule__RelationType__OverrideAssignment_2_1();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getOverrideAssignment_2_1()); 

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
    // $ANTLR end rule__RelationType__Group_2__1


    // $ANTLR start rule__OseeTypeModel__ImportsAssignment_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2633:1: rule__OseeTypeModel__ImportsAssignment_0 : ( ruleImport ) ;
    public final void rule__OseeTypeModel__ImportsAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2637:1: ( ( ruleImport ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2638:1: ( ruleImport )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2638:1: ( ruleImport )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2639:1: ruleImport
            {
             before(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0()); 
            pushFollow(FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_05224);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2648:1: rule__OseeTypeModel__TypesAssignment_1 : ( ruleOseeType ) ;
    public final void rule__OseeTypeModel__TypesAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2652:1: ( ( ruleOseeType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2653:1: ( ruleOseeType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2653:1: ( ruleOseeType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2654:1: ruleOseeType
            {
             before(grammarAccess.getOseeTypeModelAccess().getTypesOseeTypeParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleOseeType_in_rule__OseeTypeModel__TypesAssignment_15255);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2663:1: rule__Import__ImportURIAssignment_1 : ( RULE_STRING ) ;
    public final void rule__Import__ImportURIAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2667:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2668:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2668:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2669:1: RULE_STRING
            {
             before(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_15286); 
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


    // $ANTLR start rule__ArtifactType__AbstractAssignment_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2678:1: rule__ArtifactType__AbstractAssignment_0 : ( ( 'abstract' ) ) ;
    public final void rule__ArtifactType__AbstractAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2682:1: ( ( ( 'abstract' ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2683:1: ( ( 'abstract' ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2683:1: ( ( 'abstract' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2684:1: ( 'abstract' )
            {
             before(grammarAccess.getArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2685:1: ( 'abstract' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2686:1: 'abstract'
            {
             before(grammarAccess.getArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            match(input,61,FOLLOW_61_in_rule__ArtifactType__AbstractAssignment_05322); 
             after(grammarAccess.getArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 

            }

             after(grammarAccess.getArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 

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
    // $ANTLR end rule__ArtifactType__AbstractAssignment_0


    // $ANTLR start rule__ArtifactType__NameAssignment_2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2701:1: rule__ArtifactType__NameAssignment_2 : ( ruleNAME_REFERENCE ) ;
    public final void rule__ArtifactType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2705:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2706:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2706:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2707:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__NameAssignment_25361);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0()); 

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


    // $ANTLR start rule__ArtifactType__SuperArtifactTypesAssignment_3_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2716:1: rule__ArtifactType__SuperArtifactTypesAssignment_3_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__ArtifactType__SuperArtifactTypesAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2720:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2721:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2721:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2722:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2723:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2724:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_15396);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 

            }

             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_1_0()); 

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
    // $ANTLR end rule__ArtifactType__SuperArtifactTypesAssignment_3_1


    // $ANTLR start rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2735:1: rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2739:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2740:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2740:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2741:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_2_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2742:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2743:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeNAME_REFERENCEParserRuleCall_3_2_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_15435);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeNAME_REFERENCEParserRuleCall_3_2_1_0_1()); 

            }

             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_2_1_0()); 

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
    // $ANTLR end rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1


    // $ANTLR start rule__ArtifactType__OverrideAssignment_4_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2754:1: rule__ArtifactType__OverrideAssignment_4_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__ArtifactType__OverrideAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2758:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2759:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2759:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2760:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getArtifactTypeAccess().getOverrideArtifactTypeCrossReference_4_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2761:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2762:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getOverrideArtifactTypeNAME_REFERENCEParserRuleCall_4_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__OverrideAssignment_4_15474);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getArtifactTypeAccess().getOverrideArtifactTypeNAME_REFERENCEParserRuleCall_4_1_0_1()); 

            }

             after(grammarAccess.getArtifactTypeAccess().getOverrideArtifactTypeCrossReference_4_1_0()); 

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
    // $ANTLR end rule__ArtifactType__OverrideAssignment_4_1


    // $ANTLR start rule__ArtifactType__TypeGuidAssignment_6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2773:1: rule__ArtifactType__TypeGuidAssignment_6 : ( RULE_STRING ) ;
    public final void rule__ArtifactType__TypeGuidAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2777:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2778:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2778:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2779:1: RULE_STRING
            {
             before(grammarAccess.getArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__ArtifactType__TypeGuidAssignment_65509); 
             after(grammarAccess.getArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 

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
    // $ANTLR end rule__ArtifactType__TypeGuidAssignment_6


    // $ANTLR start rule__ArtifactType__ValidAttributeTypesAssignment_7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2788:1: rule__ArtifactType__ValidAttributeTypesAssignment_7 : ( ruleAttributeTypeRef ) ;
    public final void rule__ArtifactType__ValidAttributeTypesAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2792:1: ( ( ruleAttributeTypeRef ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2793:1: ( ruleAttributeTypeRef )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2793:1: ( ruleAttributeTypeRef )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2794:1: ruleAttributeTypeRef
            {
             before(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAttributeTypeRefParserRuleCall_7_0()); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_rule__ArtifactType__ValidAttributeTypesAssignment_75540);
            ruleAttributeTypeRef();
            _fsp--;

             after(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAttributeTypeRefParserRuleCall_7_0()); 

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
    // $ANTLR end rule__ArtifactType__ValidAttributeTypesAssignment_7


    // $ANTLR start rule__AttributeTypeRef__ValidAttributeTypeAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2803:1: rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__AttributeTypeRef__ValidAttributeTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2807:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2808:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2808:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2809:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeCrossReference_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2810:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2811:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeNAME_REFERENCEParserRuleCall_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeTypeRef__ValidAttributeTypeAssignment_15575);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeNAME_REFERENCEParserRuleCall_1_0_1()); 

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


    // $ANTLR start rule__AttributeTypeRef__BranchGuidAssignment_2_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2822:1: rule__AttributeTypeRef__BranchGuidAssignment_2_1 : ( RULE_STRING ) ;
    public final void rule__AttributeTypeRef__BranchGuidAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2826:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2827:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2827:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2828:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeTypeRef__BranchGuidAssignment_2_15610); 
             after(grammarAccess.getAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0()); 

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
    // $ANTLR end rule__AttributeTypeRef__BranchGuidAssignment_2_1


    // $ANTLR start rule__AttributeType__TypeGuidAssignment_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2837:1: rule__AttributeType__TypeGuidAssignment_0 : ( RULE_STRING ) ;
    public final void rule__AttributeType__TypeGuidAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2841:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2842:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2842:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2843:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_0_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__TypeGuidAssignment_05641); 
             after(grammarAccess.getAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_0_0()); 

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
    // $ANTLR end rule__AttributeType__TypeGuidAssignment_0


    // $ANTLR start rule__AttributeType__NameAssignment_2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2852:1: rule__AttributeType__NameAssignment_2 : ( ruleNAME_REFERENCE ) ;
    public final void rule__AttributeType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2856:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2857:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2857:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2858:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__NameAssignment_25672);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0()); 

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
    // $ANTLR end rule__AttributeType__NameAssignment_2


    // $ANTLR start rule__AttributeType__BaseAttributeTypeAssignment_3_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2867:1: rule__AttributeType__BaseAttributeTypeAssignment_3_1 : ( ruleAttributeBaseType ) ;
    public final void rule__AttributeType__BaseAttributeTypeAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2871:1: ( ( ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2872:1: ( ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2872:1: ( ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2873:1: ruleAttributeBaseType
            {
             before(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_3_1_0()); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_rule__AttributeType__BaseAttributeTypeAssignment_3_15703);
            ruleAttributeBaseType();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_3_1_0()); 

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
    // $ANTLR end rule__AttributeType__BaseAttributeTypeAssignment_3_1


    // $ANTLR start rule__AttributeType__OverrideAssignment_4_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2882:1: rule__AttributeType__OverrideAssignment_4_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__AttributeType__OverrideAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2886:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2887:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2887:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2888:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeCrossReference_4_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2889:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2890:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeNAME_REFERENCEParserRuleCall_4_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__OverrideAssignment_4_15738);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeNAME_REFERENCEParserRuleCall_4_1_0_1()); 

            }

             after(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeCrossReference_4_1_0()); 

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
    // $ANTLR end rule__AttributeType__OverrideAssignment_4_1


    // $ANTLR start rule__AttributeType__DataProviderAssignment_7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2901:1: rule__AttributeType__DataProviderAssignment_7 : ( ( rule__AttributeType__DataProviderAlternatives_7_0 ) ) ;
    public final void rule__AttributeType__DataProviderAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2905:1: ( ( ( rule__AttributeType__DataProviderAlternatives_7_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2906:1: ( ( rule__AttributeType__DataProviderAlternatives_7_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2906:1: ( ( rule__AttributeType__DataProviderAlternatives_7_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2907:1: ( rule__AttributeType__DataProviderAlternatives_7_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderAlternatives_7_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2908:1: ( rule__AttributeType__DataProviderAlternatives_7_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2908:2: rule__AttributeType__DataProviderAlternatives_7_0
            {
            pushFollow(FOLLOW_rule__AttributeType__DataProviderAlternatives_7_0_in_rule__AttributeType__DataProviderAssignment_75773);
            rule__AttributeType__DataProviderAlternatives_7_0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDataProviderAlternatives_7_0()); 

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
    // $ANTLR end rule__AttributeType__DataProviderAssignment_7


    // $ANTLR start rule__AttributeType__MinAssignment_9
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2917:1: rule__AttributeType__MinAssignment_9 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__AttributeType__MinAssignment_9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2921:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2922:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2922:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2923:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_9_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MinAssignment_95806); 
             after(grammarAccess.getAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_9_0()); 

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
    // $ANTLR end rule__AttributeType__MinAssignment_9


    // $ANTLR start rule__AttributeType__MaxAssignment_11
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2932:1: rule__AttributeType__MaxAssignment_11 : ( ( rule__AttributeType__MaxAlternatives_11_0 ) ) ;
    public final void rule__AttributeType__MaxAssignment_11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2936:1: ( ( ( rule__AttributeType__MaxAlternatives_11_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2937:1: ( ( rule__AttributeType__MaxAlternatives_11_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2937:1: ( ( rule__AttributeType__MaxAlternatives_11_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2938:1: ( rule__AttributeType__MaxAlternatives_11_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxAlternatives_11_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2939:1: ( rule__AttributeType__MaxAlternatives_11_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2939:2: rule__AttributeType__MaxAlternatives_11_0
            {
            pushFollow(FOLLOW_rule__AttributeType__MaxAlternatives_11_0_in_rule__AttributeType__MaxAssignment_115837);
            rule__AttributeType__MaxAlternatives_11_0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMaxAlternatives_11_0()); 

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
    // $ANTLR end rule__AttributeType__MaxAssignment_11


    // $ANTLR start rule__AttributeType__TaggerIdAssignment_12_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2948:1: rule__AttributeType__TaggerIdAssignment_12_1 : ( ( rule__AttributeType__TaggerIdAlternatives_12_1_0 ) ) ;
    public final void rule__AttributeType__TaggerIdAssignment_12_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2952:1: ( ( ( rule__AttributeType__TaggerIdAlternatives_12_1_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2953:1: ( ( rule__AttributeType__TaggerIdAlternatives_12_1_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2953:1: ( ( rule__AttributeType__TaggerIdAlternatives_12_1_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2954:1: ( rule__AttributeType__TaggerIdAlternatives_12_1_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdAlternatives_12_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2955:1: ( rule__AttributeType__TaggerIdAlternatives_12_1_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2955:2: rule__AttributeType__TaggerIdAlternatives_12_1_0
            {
            pushFollow(FOLLOW_rule__AttributeType__TaggerIdAlternatives_12_1_0_in_rule__AttributeType__TaggerIdAssignment_12_15870);
            rule__AttributeType__TaggerIdAlternatives_12_1_0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getTaggerIdAlternatives_12_1_0()); 

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
    // $ANTLR end rule__AttributeType__TaggerIdAssignment_12_1


    // $ANTLR start rule__AttributeType__EnumTypeAssignment_13_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2964:1: rule__AttributeType__EnumTypeAssignment_13_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__AttributeType__EnumTypeAssignment_13_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2968:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2969:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2969:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2970:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_13_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2971:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2972:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeNAME_REFERENCEParserRuleCall_13_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__EnumTypeAssignment_13_15907);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeNAME_REFERENCEParserRuleCall_13_1_0_1()); 

            }

             after(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_13_1_0()); 

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
    // $ANTLR end rule__AttributeType__EnumTypeAssignment_13_1


    // $ANTLR start rule__AttributeType__DescriptionAssignment_14_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2983:1: rule__AttributeType__DescriptionAssignment_14_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__DescriptionAssignment_14_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2987:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2988:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2988:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2989:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_14_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__DescriptionAssignment_14_15942); 
             after(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_14_1_0()); 

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
    // $ANTLR end rule__AttributeType__DescriptionAssignment_14_1


    // $ANTLR start rule__AttributeType__DefaultValueAssignment_15_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2998:1: rule__AttributeType__DefaultValueAssignment_15_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__DefaultValueAssignment_15_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3002:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3003:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3003:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3004:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_15_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__DefaultValueAssignment_15_15973); 
             after(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_15_1_0()); 

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
    // $ANTLR end rule__AttributeType__DefaultValueAssignment_15_1


    // $ANTLR start rule__AttributeType__FileExtensionAssignment_16_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3013:1: rule__AttributeType__FileExtensionAssignment_16_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__FileExtensionAssignment_16_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3017:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3018:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3018:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3019:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_16_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__FileExtensionAssignment_16_16004); 
             after(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_16_1_0()); 

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
    // $ANTLR end rule__AttributeType__FileExtensionAssignment_16_1


    // $ANTLR start rule__OseeEnumType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3028:1: rule__OseeEnumType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__OseeEnumType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3032:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3033:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3033:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3034:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumType__NameAssignment_16035);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 

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


    // $ANTLR start rule__OseeEnumType__OverrideAssignment_2_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3043:1: rule__OseeEnumType__OverrideAssignment_2_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__OseeEnumType__OverrideAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3047:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3048:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3048:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3049:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOverrideOseeEnumTypeCrossReference_2_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3050:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3051:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOverrideOseeEnumTypeNAME_REFERENCEParserRuleCall_2_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumType__OverrideAssignment_2_16070);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getOseeEnumTypeAccess().getOverrideOseeEnumTypeNAME_REFERENCEParserRuleCall_2_1_0_1()); 

            }

             after(grammarAccess.getOseeEnumTypeAccess().getOverrideOseeEnumTypeCrossReference_2_1_0()); 

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
    // $ANTLR end rule__OseeEnumType__OverrideAssignment_2_1


    // $ANTLR start rule__OseeEnumType__TypeGuidAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3062:1: rule__OseeEnumType__TypeGuidAssignment_4 : ( RULE_STRING ) ;
    public final void rule__OseeEnumType__TypeGuidAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3066:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3067:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3067:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3068:1: RULE_STRING
            {
             before(grammarAccess.getOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__OseeEnumType__TypeGuidAssignment_46105); 
             after(grammarAccess.getOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 

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
    // $ANTLR end rule__OseeEnumType__TypeGuidAssignment_4


    // $ANTLR start rule__OseeEnumType__EnumEntriesAssignment_5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3077:1: rule__OseeEnumType__EnumEntriesAssignment_5 : ( ruleOseeEnumEntry ) ;
    public final void rule__OseeEnumType__EnumEntriesAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3081:1: ( ( ruleOseeEnumEntry ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3082:1: ( ruleOseeEnumEntry )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3082:1: ( ruleOseeEnumEntry )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3083:1: ruleOseeEnumEntry
            {
             before(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesOseeEnumEntryParserRuleCall_5_0()); 
            pushFollow(FOLLOW_ruleOseeEnumEntry_in_rule__OseeEnumType__EnumEntriesAssignment_56136);
            ruleOseeEnumEntry();
            _fsp--;

             after(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesOseeEnumEntryParserRuleCall_5_0()); 

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
    // $ANTLR end rule__OseeEnumType__EnumEntriesAssignment_5


    // $ANTLR start rule__OseeEnumEntry__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3092:1: rule__OseeEnumEntry__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__OseeEnumEntry__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3096:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3097:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3097:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3098:1: RULE_STRING
            {
             before(grammarAccess.getOseeEnumEntryAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__OseeEnumEntry__NameAssignment_16167); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3107:1: rule__OseeEnumEntry__OrdinalAssignment_2 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__OseeEnumEntry__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3111:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3112:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3112:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3113:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_rule__OseeEnumEntry__OrdinalAssignment_26198); 
             after(grammarAccess.getOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3122:1: rule__RelationType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__RelationType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3126:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3127:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3127:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3128:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__NameAssignment_16229);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 

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


    // $ANTLR start rule__RelationType__OverrideAssignment_2_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3137:1: rule__RelationType__OverrideAssignment_2_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RelationType__OverrideAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3141:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3142:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3142:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3143:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getRelationTypeAccess().getOverrideRelationTypeCrossReference_2_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3144:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3145:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getOverrideRelationTypeNAME_REFERENCEParserRuleCall_2_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__OverrideAssignment_2_16264);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getRelationTypeAccess().getOverrideRelationTypeNAME_REFERENCEParserRuleCall_2_1_0_1()); 

            }

             after(grammarAccess.getRelationTypeAccess().getOverrideRelationTypeCrossReference_2_1_0()); 

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
    // $ANTLR end rule__RelationType__OverrideAssignment_2_1


    // $ANTLR start rule__RelationType__TypeGuidAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3156:1: rule__RelationType__TypeGuidAssignment_4 : ( RULE_STRING ) ;
    public final void rule__RelationType__TypeGuidAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3160:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3161:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3161:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3162:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__TypeGuidAssignment_46299); 
             after(grammarAccess.getRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 

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
    // $ANTLR end rule__RelationType__TypeGuidAssignment_4


    // $ANTLR start rule__RelationType__SideANameAssignment_6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3171:1: rule__RelationType__SideANameAssignment_6 : ( RULE_STRING ) ;
    public final void rule__RelationType__SideANameAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3175:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3176:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3176:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3177:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_6_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__SideANameAssignment_66330); 
             after(grammarAccess.getRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_6_0()); 

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
    // $ANTLR end rule__RelationType__SideANameAssignment_6


    // $ANTLR start rule__RelationType__SideAArtifactTypeAssignment_8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3186:1: rule__RelationType__SideAArtifactTypeAssignment_8 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RelationType__SideAArtifactTypeAssignment_8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3190:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3191:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3191:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3192:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeCrossReference_8_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3193:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3194:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeNAME_REFERENCEParserRuleCall_8_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideAArtifactTypeAssignment_86365);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeNAME_REFERENCEParserRuleCall_8_0_1()); 

            }

             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeCrossReference_8_0()); 

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
    // $ANTLR end rule__RelationType__SideAArtifactTypeAssignment_8


    // $ANTLR start rule__RelationType__SideBNameAssignment_10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3205:1: rule__RelationType__SideBNameAssignment_10 : ( RULE_STRING ) ;
    public final void rule__RelationType__SideBNameAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3209:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3210:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3210:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3211:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_10_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__SideBNameAssignment_106400); 
             after(grammarAccess.getRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_10_0()); 

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
    // $ANTLR end rule__RelationType__SideBNameAssignment_10


    // $ANTLR start rule__RelationType__SideBArtifactTypeAssignment_12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3220:1: rule__RelationType__SideBArtifactTypeAssignment_12 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RelationType__SideBArtifactTypeAssignment_12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3224:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3225:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3225:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3226:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeCrossReference_12_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3227:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3228:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeNAME_REFERENCEParserRuleCall_12_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideBArtifactTypeAssignment_126435);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeNAME_REFERENCEParserRuleCall_12_0_1()); 

            }

             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeCrossReference_12_0()); 

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
    // $ANTLR end rule__RelationType__SideBArtifactTypeAssignment_12


    // $ANTLR start rule__RelationType__DefaultOrderTypeAssignment_14
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3239:1: rule__RelationType__DefaultOrderTypeAssignment_14 : ( ruleRelationOrderType ) ;
    public final void rule__RelationType__DefaultOrderTypeAssignment_14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3243:1: ( ( ruleRelationOrderType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3244:1: ( ruleRelationOrderType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3244:1: ( ruleRelationOrderType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3245:1: ruleRelationOrderType
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_14_0()); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_rule__RelationType__DefaultOrderTypeAssignment_146470);
            ruleRelationOrderType();
            _fsp--;

             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_14_0()); 

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
    // $ANTLR end rule__RelationType__DefaultOrderTypeAssignment_14


    // $ANTLR start rule__RelationType__MultiplicityAssignment_16
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3254:1: rule__RelationType__MultiplicityAssignment_16 : ( ruleRelationMultiplicityEnum ) ;
    public final void rule__RelationType__MultiplicityAssignment_16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3258:1: ( ( ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3259:1: ( ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3259:1: ( ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3260:1: ruleRelationMultiplicityEnum
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0()); 
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_rule__RelationType__MultiplicityAssignment_166501);
            ruleRelationMultiplicityEnum();
            _fsp--;

             after(grammarAccess.getRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0()); 

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
    // $ANTLR end rule__RelationType__MultiplicityAssignment_16


 

    public static final BitSet FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel60 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeTypeModel67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__Group__0_in_ruleOseeTypeModel94 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport120 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Import__Group__0_in_ruleImport154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE180 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNAME_REFERENCE187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME239 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__0_in_ruleQUALIFIED_NAME273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType299 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeType__Alternatives_in_ruleOseeType333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_entryRuleArtifactType359 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactType366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__0_in_ruleArtifactType393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef419 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRef426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__0_in_ruleAttributeTypeRef453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_entryRuleAttributeType479 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeType486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__0_in_ruleAttributeType513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType539 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeBaseType__Alternatives_in_ruleAttributeBaseType573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType599 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumType606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__0_in_ruleOseeEnumType633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry659 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumEntry666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__0_in_ruleOseeEnumEntry693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_entryRuleRelationType719 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationType726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__Group__0_in_ruleRelationType753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType779 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationOrderType786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationOrderType__Alternatives_in_ruleRelationOrderType813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationMultiplicityEnum__Alternatives_in_ruleRelationMultiplicityEnum850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_rule__OseeType__Alternatives885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_rule__OseeType__Alternatives902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_rule__OseeType__Alternatives919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_rule__OseeType__Alternatives936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_rule__AttributeType__DataProviderAlternatives_7_0969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_rule__AttributeType__DataProviderAlternatives_7_0989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_rule__AttributeType__DataProviderAlternatives_7_01009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__DataProviderAlternatives_7_01028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MaxAlternatives_11_01060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_rule__AttributeType__MaxAlternatives_11_01078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_rule__AttributeType__TaggerIdAlternatives_12_1_01113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__TaggerIdAlternatives_12_1_01132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_rule__AttributeBaseType__Alternatives1165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_rule__AttributeBaseType__Alternatives1185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_rule__AttributeBaseType__Alternatives1205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_rule__AttributeBaseType__Alternatives1225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_rule__AttributeBaseType__Alternatives1245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_rule__AttributeBaseType__Alternatives1265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_rule__AttributeBaseType__Alternatives1285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_rule__AttributeBaseType__Alternatives1305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_rule__AttributeBaseType__Alternatives1325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeBaseType__Alternatives1344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_rule__RelationOrderType__Alternatives1377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_rule__RelationOrderType__Alternatives1397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_rule__RelationOrderType__Alternatives1417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__RelationOrderType__Alternatives1436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_rule__RelationMultiplicityEnum__Alternatives1469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_rule__RelationMultiplicityEnum__Alternatives1490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_rule__RelationMultiplicityEnum__Alternatives1511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_rule__RelationMultiplicityEnum__Alternatives1532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__01569 = new BitSet(new long[]{0x2050080A00000012L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__TypesAssignment_1_in_rule__OseeTypeModel__Group__11607 = new BitSet(new long[]{0x2050080800000012L});
    public static final BitSet FOLLOW_33_in_rule__Import__Group__01647 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Import__Group__1_in_rule__Import__Group__01657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__11685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__01723 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__01731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__11759 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_rule__QUALIFIED_NAME__Group_1__01799 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__01809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__11837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__AbstractAssignment_0_in_rule__ArtifactType__Group__01874 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__1_in_rule__ArtifactType__Group__01884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_rule__ArtifactType__Group__11913 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__2_in_rule__ArtifactType__Group__11923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__NameAssignment_2_in_rule__ArtifactType__Group__21951 = new BitSet(new long[]{0x0000015000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__3_in_rule__ArtifactType__Group__21960 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__0_in_rule__ArtifactType__Group__31988 = new BitSet(new long[]{0x0000011000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__4_in_rule__ArtifactType__Group__31998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_4__0_in_rule__ArtifactType__Group__42026 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__5_in_rule__ArtifactType__Group__42036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__ArtifactType__Group__52065 = new BitSet(new long[]{0x0000022000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__6_in_rule__ArtifactType__Group__52075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__TypeGuidAssignment_6_in_rule__ArtifactType__Group__62103 = new BitSet(new long[]{0x0000022000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__7_in_rule__ArtifactType__Group__62113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__ValidAttributeTypesAssignment_7_in_rule__ArtifactType__Group__72141 = new BitSet(new long[]{0x0000022000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__8_in_rule__ArtifactType__Group__72151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__ArtifactType__Group__82180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_rule__ArtifactType__Group_3__02234 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__1_in_rule__ArtifactType__Group_3__02244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_1_in_rule__ArtifactType__Group_3__12272 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__2_in_rule__ArtifactType__Group_3__12281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3_2__0_in_rule__ArtifactType__Group_3__22309 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_39_in_rule__ArtifactType__Group_3_2__02351 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3_2__1_in_rule__ArtifactType__Group_3_2__02361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1_in_rule__ArtifactType__Group_3_2__12389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule__ArtifactType__Group_4__02428 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_4__1_in_rule__ArtifactType__Group_4__02438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__OverrideAssignment_4_1_in_rule__ArtifactType__Group_4__12466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_rule__AttributeTypeRef__Group__02505 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__1_in_rule__AttributeTypeRef__Group__02515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__AttributeTypeRef__Group__12543 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__2_in_rule__AttributeTypeRef__Group__12552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group_2__0_in_rule__AttributeTypeRef__Group__22580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_rule__AttributeTypeRef__Group_2__02622 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group_2__1_in_rule__AttributeTypeRef__Group_2__02632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__BranchGuidAssignment_2_1_in_rule__AttributeTypeRef__Group_2__12660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TypeGuidAssignment_0_in_rule__AttributeType__Group__02698 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__1_in_rule__AttributeType__Group__02708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_rule__AttributeType__Group__12737 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__2_in_rule__AttributeType__Group__12747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__NameAssignment_2_in_rule__AttributeType__Group__22775 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__3_in_rule__AttributeType__Group__22784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_3__0_in_rule__AttributeType__Group__32812 = new BitSet(new long[]{0x0000011000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__4_in_rule__AttributeType__Group__32821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_4__0_in_rule__AttributeType__Group__42849 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__5_in_rule__AttributeType__Group__42859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__AttributeType__Group__52888 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__6_in_rule__AttributeType__Group__52898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_rule__AttributeType__Group__62927 = new BitSet(new long[]{0x0000000000007040L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__7_in_rule__AttributeType__Group__62937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DataProviderAssignment_7_in_rule__AttributeType__Group__72965 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__8_in_rule__AttributeType__Group__72974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_rule__AttributeType__Group__83003 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__9_in_rule__AttributeType__Group__83013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MinAssignment_9_in_rule__AttributeType__Group__93041 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__10_in_rule__AttributeType__Group__93050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_rule__AttributeType__Group__103079 = new BitSet(new long[]{0x0000000000008020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__11_in_rule__AttributeType__Group__103089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MaxAssignment_11_in_rule__AttributeType__Group__113117 = new BitSet(new long[]{0x000F802000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__12_in_rule__AttributeType__Group__113126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_12__0_in_rule__AttributeType__Group__123154 = new BitSet(new long[]{0x000F002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__13_in_rule__AttributeType__Group__123164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_13__0_in_rule__AttributeType__Group__133192 = new BitSet(new long[]{0x000E002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__14_in_rule__AttributeType__Group__133202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_14__0_in_rule__AttributeType__Group__143230 = new BitSet(new long[]{0x000C002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__15_in_rule__AttributeType__Group__143240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_15__0_in_rule__AttributeType__Group__153268 = new BitSet(new long[]{0x0008002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__16_in_rule__AttributeType__Group__153278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_16__0_in_rule__AttributeType__Group__163306 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__17_in_rule__AttributeType__Group__163316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__AttributeType__Group__173345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_rule__AttributeType__Group_3__03417 = new BitSet(new long[]{0x0000000003FE0040L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_3__1_in_rule__AttributeType__Group_3__03427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__BaseAttributeTypeAssignment_3_1_in_rule__AttributeType__Group_3__13455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule__AttributeType__Group_4__03494 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_4__1_in_rule__AttributeType__Group_4__03504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__OverrideAssignment_4_1_in_rule__AttributeType__Group_4__13532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_rule__AttributeType__Group_12__03571 = new BitSet(new long[]{0x0000000000010040L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_12__1_in_rule__AttributeType__Group_12__03581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TaggerIdAssignment_12_1_in_rule__AttributeType__Group_12__13609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_rule__AttributeType__Group_13__03648 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_13__1_in_rule__AttributeType__Group_13__03658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__EnumTypeAssignment_13_1_in_rule__AttributeType__Group_13__13686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_rule__AttributeType__Group_14__03725 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_14__1_in_rule__AttributeType__Group_14__03735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DescriptionAssignment_14_1_in_rule__AttributeType__Group_14__13763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_rule__AttributeType__Group_15__03802 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_15__1_in_rule__AttributeType__Group_15__03812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DefaultValueAssignment_15_1_in_rule__AttributeType__Group_15__13840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_rule__AttributeType__Group_16__03879 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_16__1_in_rule__AttributeType__Group_16__03889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__FileExtensionAssignment_16_1_in_rule__AttributeType__Group_16__13917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_rule__OseeEnumType__Group__03956 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__1_in_rule__OseeEnumType__Group__03966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__NameAssignment_1_in_rule__OseeEnumType__Group__13994 = new BitSet(new long[]{0x0000011000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__2_in_rule__OseeEnumType__Group__14003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group_2__0_in_rule__OseeEnumType__Group__24031 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__3_in_rule__OseeEnumType__Group__24041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__OseeEnumType__Group__34070 = new BitSet(new long[]{0x0020002000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__4_in_rule__OseeEnumType__Group__34080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__TypeGuidAssignment_4_in_rule__OseeEnumType__Group__44108 = new BitSet(new long[]{0x0020002000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__5_in_rule__OseeEnumType__Group__44118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__EnumEntriesAssignment_5_in_rule__OseeEnumType__Group__54146 = new BitSet(new long[]{0x0020002000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__6_in_rule__OseeEnumType__Group__54156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__OseeEnumType__Group__64185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule__OseeEnumType__Group_2__04235 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group_2__1_in_rule__OseeEnumType__Group_2__04245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__OverrideAssignment_2_1_in_rule__OseeEnumType__Group_2__14273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_rule__OseeEnumEntry__Group__04312 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__1_in_rule__OseeEnumEntry__Group__04322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__NameAssignment_1_in_rule__OseeEnumEntry__Group__14350 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__2_in_rule__OseeEnumEntry__Group__14359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__OrdinalAssignment_2_in_rule__OseeEnumEntry__Group__24387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_rule__RelationType__Group__04429 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__1_in_rule__RelationType__Group__04439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__NameAssignment_1_in_rule__RelationType__Group__14467 = new BitSet(new long[]{0x0000011000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__2_in_rule__RelationType__Group__14476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__Group_2__0_in_rule__RelationType__Group__24504 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__3_in_rule__RelationType__Group__24514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__RelationType__Group__34543 = new BitSet(new long[]{0x0080000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__4_in_rule__RelationType__Group__34553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__TypeGuidAssignment_4_in_rule__RelationType__Group__44581 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__5_in_rule__RelationType__Group__44591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_rule__RelationType__Group__54620 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__6_in_rule__RelationType__Group__54630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideANameAssignment_6_in_rule__RelationType__Group__64658 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__7_in_rule__RelationType__Group__64667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_rule__RelationType__Group__74696 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__8_in_rule__RelationType__Group__74706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideAArtifactTypeAssignment_8_in_rule__RelationType__Group__84734 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__9_in_rule__RelationType__Group__84743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_rule__RelationType__Group__94772 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__10_in_rule__RelationType__Group__94782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideBNameAssignment_10_in_rule__RelationType__Group__104810 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__11_in_rule__RelationType__Group__104819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_rule__RelationType__Group__114848 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__12_in_rule__RelationType__Group__114858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideBArtifactTypeAssignment_12_in_rule__RelationType__Group__124886 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__13_in_rule__RelationType__Group__124895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_rule__RelationType__Group__134924 = new BitSet(new long[]{0x000000001C000040L});
    public static final BitSet FOLLOW_rule__RelationType__Group__14_in_rule__RelationType__Group__134934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__DefaultOrderTypeAssignment_14_in_rule__RelationType__Group__144962 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__15_in_rule__RelationType__Group__144971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_rule__RelationType__Group__155000 = new BitSet(new long[]{0x00000001E0000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__16_in_rule__RelationType__Group__155010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__MultiplicityAssignment_16_in_rule__RelationType__Group__165038 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__17_in_rule__RelationType__Group__165047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__RelationType__Group__175076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule__RelationType__Group_2__05148 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group_2__1_in_rule__RelationType__Group_2__05158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__OverrideAssignment_2_1_in_rule__RelationType__Group_2__15186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_05224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeType_in_rule__OseeTypeModel__TypesAssignment_15255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_15286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_rule__ArtifactType__AbstractAssignment_05322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__NameAssignment_25361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_15396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_15435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__OverrideAssignment_4_15474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__ArtifactType__TypeGuidAssignment_65509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_rule__ArtifactType__ValidAttributeTypesAssignment_75540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeTypeRef__ValidAttributeTypeAssignment_15575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeTypeRef__BranchGuidAssignment_2_15610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__TypeGuidAssignment_05641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__NameAssignment_25672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_rule__AttributeType__BaseAttributeTypeAssignment_3_15703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__OverrideAssignment_4_15738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DataProviderAlternatives_7_0_in_rule__AttributeType__DataProviderAssignment_75773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MinAssignment_95806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MaxAlternatives_11_0_in_rule__AttributeType__MaxAssignment_115837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TaggerIdAlternatives_12_1_0_in_rule__AttributeType__TaggerIdAssignment_12_15870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__EnumTypeAssignment_13_15907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__DescriptionAssignment_14_15942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__DefaultValueAssignment_15_15973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__FileExtensionAssignment_16_16004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumType__NameAssignment_16035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumType__OverrideAssignment_2_16070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__OseeEnumType__TypeGuidAssignment_46105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_rule__OseeEnumType__EnumEntriesAssignment_56136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__OseeEnumEntry__NameAssignment_16167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__OseeEnumEntry__OrdinalAssignment_26198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__NameAssignment_16229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__OverrideAssignment_2_16264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__TypeGuidAssignment_46299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__SideANameAssignment_66330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideAArtifactTypeAssignment_86365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__SideBNameAssignment_106400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideBArtifactTypeAssignment_126435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_rule__RelationType__DefaultOrderTypeAssignment_146470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_rule__RelationType__MultiplicityAssignment_166501 = new BitSet(new long[]{0x0000000000000002L});

}