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


    // $ANTLR start ruleRelationMultiplicityEnum
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:385:1: ruleRelationMultiplicityEnum : ( ( rule__RelationMultiplicityEnum__Alternatives ) ) ;
    public final void ruleRelationMultiplicityEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:389:1: ( ( ( rule__RelationMultiplicityEnum__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:390:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:390:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:391:1: ( rule__RelationMultiplicityEnum__Alternatives )
            {
             before(grammarAccess.getRelationMultiplicityEnumAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:392:1: ( rule__RelationMultiplicityEnum__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:392:2: rule__RelationMultiplicityEnum__Alternatives
            {
            pushFollow(FOLLOW_rule__RelationMultiplicityEnum__Alternatives_in_ruleRelationMultiplicityEnum790);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:403:1: rule__OseeType__Alternatives : ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) );
    public final void rule__OseeType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:407:1: ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) )
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
                    new NoViableAltException("403:1: rule__OseeType__Alternatives : ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) );", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:408:1: ( ruleArtifactType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:408:1: ( ruleArtifactType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:409:1: ruleArtifactType
                    {
                     before(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0()); 
                    pushFollow(FOLLOW_ruleArtifactType_in_rule__OseeType__Alternatives825);
                    ruleArtifactType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:414:6: ( ruleRelationType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:414:6: ( ruleRelationType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:415:1: ruleRelationType
                    {
                     before(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1()); 
                    pushFollow(FOLLOW_ruleRelationType_in_rule__OseeType__Alternatives842);
                    ruleRelationType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:420:6: ( ruleAttributeType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:420:6: ( ruleAttributeType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:421:1: ruleAttributeType
                    {
                     before(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2()); 
                    pushFollow(FOLLOW_ruleAttributeType_in_rule__OseeType__Alternatives859);
                    ruleAttributeType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:426:6: ( ruleOseeEnumType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:426:6: ( ruleOseeEnumType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:427:1: ruleOseeEnumType
                    {
                     before(grammarAccess.getOseeTypeAccess().getOseeEnumTypeParserRuleCall_3()); 
                    pushFollow(FOLLOW_ruleOseeEnumType_in_rule__OseeType__Alternatives876);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:437:1: rule__AttributeType__DataProviderAlternatives_7_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( 'MappedAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeType__DataProviderAlternatives_7_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:441:1: ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( 'MappedAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) )
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
                    new NoViableAltException("437:1: rule__AttributeType__DataProviderAlternatives_7_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( 'MappedAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:442:1: ( 'DefaultAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:442:1: ( 'DefaultAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:443:1: 'DefaultAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_7_0_0()); 
                    match(input,12,FOLLOW_12_in_rule__AttributeType__DataProviderAlternatives_7_0909); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_7_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:450:6: ( 'UriAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:450:6: ( 'UriAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:451:1: 'UriAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_7_0_1()); 
                    match(input,13,FOLLOW_13_in_rule__AttributeType__DataProviderAlternatives_7_0929); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_7_0_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:458:6: ( 'MappedAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:458:6: ( 'MappedAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:459:1: 'MappedAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderMappedAttributeDataProviderKeyword_7_0_2()); 
                    match(input,14,FOLLOW_14_in_rule__AttributeType__DataProviderAlternatives_7_0949); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderMappedAttributeDataProviderKeyword_7_0_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:466:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:466:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:467:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_7_0_3()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__DataProviderAlternatives_7_0968);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:477:1: rule__AttributeType__MaxAlternatives_11_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );
    public final void rule__AttributeType__MaxAlternatives_11_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:481:1: ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) )
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
                    new NoViableAltException("477:1: rule__AttributeType__MaxAlternatives_11_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:482:1: ( RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:482:1: ( RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:483:1: RULE_WHOLE_NUM_STR
                    {
                     before(grammarAccess.getAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_11_0_0()); 
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MaxAlternatives_11_01000); 
                     after(grammarAccess.getAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_11_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:488:6: ( 'unlimited' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:488:6: ( 'unlimited' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:489:1: 'unlimited'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_11_0_1()); 
                    match(input,15,FOLLOW_15_in_rule__AttributeType__MaxAlternatives_11_01018); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:501:1: rule__AttributeType__TaggerIdAlternatives_12_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeType__TaggerIdAlternatives_12_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:505:1: ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) )
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
                    new NoViableAltException("501:1: rule__AttributeType__TaggerIdAlternatives_12_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:506:1: ( 'DefaultAttributeTaggerProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:506:1: ( 'DefaultAttributeTaggerProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:507:1: 'DefaultAttributeTaggerProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_12_1_0_0()); 
                    match(input,16,FOLLOW_16_in_rule__AttributeType__TaggerIdAlternatives_12_1_01053); 
                     after(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_12_1_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:514:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:514:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:515:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_12_1_0_1()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__TaggerIdAlternatives_12_1_01072);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:525:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeBaseType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:529:1: ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) )
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
                    new NoViableAltException("525:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:530:1: ( 'BooleanAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:530:1: ( 'BooleanAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:531:1: 'BooleanAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                    match(input,17,FOLLOW_17_in_rule__AttributeBaseType__Alternatives1105); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:538:6: ( 'CompressedContentAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:538:6: ( 'CompressedContentAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:539:1: 'CompressedContentAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                    match(input,18,FOLLOW_18_in_rule__AttributeBaseType__Alternatives1125); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:546:6: ( 'DateAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:546:6: ( 'DateAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:547:1: 'DateAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                    match(input,19,FOLLOW_19_in_rule__AttributeBaseType__Alternatives1145); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:554:6: ( 'EnumeratedAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:554:6: ( 'EnumeratedAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:555:1: 'EnumeratedAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                    match(input,20,FOLLOW_20_in_rule__AttributeBaseType__Alternatives1165); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 

                    }


                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:562:6: ( 'FloatingPointAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:562:6: ( 'FloatingPointAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:563:1: 'FloatingPointAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                    match(input,21,FOLLOW_21_in_rule__AttributeBaseType__Alternatives1185); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 

                    }


                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:570:6: ( 'IntegerAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:570:6: ( 'IntegerAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:571:1: 'IntegerAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                    match(input,22,FOLLOW_22_in_rule__AttributeBaseType__Alternatives1205); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 

                    }


                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:578:6: ( 'JavaObjectAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:578:6: ( 'JavaObjectAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:579:1: 'JavaObjectAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 
                    match(input,23,FOLLOW_23_in_rule__AttributeBaseType__Alternatives1225); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 

                    }


                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:586:6: ( 'StringAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:586:6: ( 'StringAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:587:1: 'StringAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 
                    match(input,24,FOLLOW_24_in_rule__AttributeBaseType__Alternatives1245); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 

                    }


                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:594:6: ( 'WordAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:594:6: ( 'WordAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:595:1: 'WordAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 
                    match(input,25,FOLLOW_25_in_rule__AttributeBaseType__Alternatives1265); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 

                    }


                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:602:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:602:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:603:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeBaseType__Alternatives1284);
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


    // $ANTLR start rule__RelationType__DefaultOrderTypeAlternatives_14_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:613:1: rule__RelationType__DefaultOrderTypeAlternatives_14_0 : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );
    public final void rule__RelationType__DefaultOrderTypeAlternatives_14_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:617:1: ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) )
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
                    new NoViableAltException("613:1: rule__RelationType__DefaultOrderTypeAlternatives_14_0 : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:618:1: ( 'Lexicographical_Ascending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:618:1: ( 'Lexicographical_Ascending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:619:1: 'Lexicographical_Ascending'
                    {
                     before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_AscendingKeyword_14_0_0()); 
                    match(input,26,FOLLOW_26_in_rule__RelationType__DefaultOrderTypeAlternatives_14_01317); 
                     after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_AscendingKeyword_14_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:626:6: ( 'Lexicographical_Descending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:626:6: ( 'Lexicographical_Descending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:627:1: 'Lexicographical_Descending'
                    {
                     before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_DescendingKeyword_14_0_1()); 
                    match(input,27,FOLLOW_27_in_rule__RelationType__DefaultOrderTypeAlternatives_14_01337); 
                     after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeLexicographical_DescendingKeyword_14_0_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:634:6: ( 'Unordered' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:634:6: ( 'Unordered' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:635:1: 'Unordered'
                    {
                     before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeUnorderedKeyword_14_0_2()); 
                    match(input,28,FOLLOW_28_in_rule__RelationType__DefaultOrderTypeAlternatives_14_01357); 
                     after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeUnorderedKeyword_14_0_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:642:6: ( RULE_ID )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:642:6: ( RULE_ID )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:643:1: RULE_ID
                    {
                     before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeIDTerminalRuleCall_14_0_3()); 
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__RelationType__DefaultOrderTypeAlternatives_14_01376); 
                     after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeIDTerminalRuleCall_14_0_3()); 

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
    // $ANTLR end rule__RelationType__DefaultOrderTypeAlternatives_14_0


    // $ANTLR start rule__RelationMultiplicityEnum__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:653:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );
    public final void rule__RelationMultiplicityEnum__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:657:1: ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) )
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
                    new NoViableAltException("653:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:658:1: ( ( 'ONE_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:658:1: ( ( 'ONE_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:659:1: ( 'ONE_TO_ONE' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:660:1: ( 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:660:3: 'ONE_TO_ONE'
                    {
                    match(input,29,FOLLOW_29_in_rule__RelationMultiplicityEnum__Alternatives1409); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:665:6: ( ( 'ONE_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:665:6: ( ( 'ONE_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:666:1: ( 'ONE_TO_MANY' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:667:1: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:667:3: 'ONE_TO_MANY'
                    {
                    match(input,30,FOLLOW_30_in_rule__RelationMultiplicityEnum__Alternatives1430); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:672:6: ( ( 'MANY_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:672:6: ( ( 'MANY_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:673:1: ( 'MANY_TO_ONE' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:674:1: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:674:3: 'MANY_TO_ONE'
                    {
                    match(input,31,FOLLOW_31_in_rule__RelationMultiplicityEnum__Alternatives1451); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:679:6: ( ( 'MANY_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:679:6: ( ( 'MANY_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:680:1: ( 'MANY_TO_MANY' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:681:1: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:681:3: 'MANY_TO_MANY'
                    {
                    match(input,32,FOLLOW_32_in_rule__RelationMultiplicityEnum__Alternatives1472); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:693:1: rule__OseeTypeModel__Group__0 : ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1 ;
    public final void rule__OseeTypeModel__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:697:1: ( ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:698:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:698:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:699:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            {
             before(grammarAccess.getOseeTypeModelAccess().getImportsAssignment_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:700:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==33) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:700:2: rule__OseeTypeModel__ImportsAssignment_0
            	    {
            	    pushFollow(FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__01509);
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

            pushFollow(FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01519);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:711:1: rule__OseeTypeModel__Group__1 : ( ( rule__OseeTypeModel__TypesAssignment_1 )* ) ;
    public final void rule__OseeTypeModel__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:715:1: ( ( ( rule__OseeTypeModel__TypesAssignment_1 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:716:1: ( ( rule__OseeTypeModel__TypesAssignment_1 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:716:1: ( ( rule__OseeTypeModel__TypesAssignment_1 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:717:1: ( rule__OseeTypeModel__TypesAssignment_1 )*
            {
             before(grammarAccess.getOseeTypeModelAccess().getTypesAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:718:1: ( rule__OseeTypeModel__TypesAssignment_1 )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==RULE_STRING||LA9_0==35||LA9_0==43||LA9_0==52||LA9_0==54||LA9_0==61) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:718:2: rule__OseeTypeModel__TypesAssignment_1
            	    {
            	    pushFollow(FOLLOW_rule__OseeTypeModel__TypesAssignment_1_in_rule__OseeTypeModel__Group__11547);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:732:1: rule__Import__Group__0 : ( 'import' ) rule__Import__Group__1 ;
    public final void rule__Import__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:736:1: ( ( 'import' ) rule__Import__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:737:1: ( 'import' ) rule__Import__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:737:1: ( 'import' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:738:1: 'import'
            {
             before(grammarAccess.getImportAccess().getImportKeyword_0()); 
            match(input,33,FOLLOW_33_in_rule__Import__Group__01587); 
             after(grammarAccess.getImportAccess().getImportKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__Import__Group__1_in_rule__Import__Group__01597);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:752:1: rule__Import__Group__1 : ( ( rule__Import__ImportURIAssignment_1 ) ) ;
    public final void rule__Import__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:756:1: ( ( ( rule__Import__ImportURIAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:757:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:757:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:758:1: ( rule__Import__ImportURIAssignment_1 )
            {
             before(grammarAccess.getImportAccess().getImportURIAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:759:1: ( rule__Import__ImportURIAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:759:2: rule__Import__ImportURIAssignment_1
            {
            pushFollow(FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__11625);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:773:1: rule__QUALIFIED_NAME__Group__0 : ( RULE_ID ) rule__QUALIFIED_NAME__Group__1 ;
    public final void rule__QUALIFIED_NAME__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:777:1: ( ( RULE_ID ) rule__QUALIFIED_NAME__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:778:1: ( RULE_ID ) rule__QUALIFIED_NAME__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:778:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:779:1: RULE_ID
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__01663); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 

            }

            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__01671);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:791:1: rule__QUALIFIED_NAME__Group__1 : ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) ;
    public final void rule__QUALIFIED_NAME__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:795:1: ( ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:796:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:796:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:797:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:798:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==34) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:798:2: rule__QUALIFIED_NAME__Group_1__0
            	    {
            	    pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__11699);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:812:1: rule__QUALIFIED_NAME__Group_1__0 : ( '.' ) rule__QUALIFIED_NAME__Group_1__1 ;
    public final void rule__QUALIFIED_NAME__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:816:1: ( ( '.' ) rule__QUALIFIED_NAME__Group_1__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:817:1: ( '.' ) rule__QUALIFIED_NAME__Group_1__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:817:1: ( '.' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:818:1: '.'
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            match(input,34,FOLLOW_34_in_rule__QUALIFIED_NAME__Group_1__01739); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 

            }

            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__01749);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:832:1: rule__QUALIFIED_NAME__Group_1__1 : ( RULE_ID ) ;
    public final void rule__QUALIFIED_NAME__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:836:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:837:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:837:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:838:1: RULE_ID
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__11777); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:853:1: rule__ArtifactType__Group__0 : ( ( rule__ArtifactType__AbstractAssignment_0 )? ) rule__ArtifactType__Group__1 ;
    public final void rule__ArtifactType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:857:1: ( ( ( rule__ArtifactType__AbstractAssignment_0 )? ) rule__ArtifactType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:858:1: ( ( rule__ArtifactType__AbstractAssignment_0 )? ) rule__ArtifactType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:858:1: ( ( rule__ArtifactType__AbstractAssignment_0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:859:1: ( rule__ArtifactType__AbstractAssignment_0 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getAbstractAssignment_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:860:1: ( rule__ArtifactType__AbstractAssignment_0 )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==61) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:860:2: rule__ArtifactType__AbstractAssignment_0
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__AbstractAssignment_0_in_rule__ArtifactType__Group__01814);
                    rule__ArtifactType__AbstractAssignment_0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getAbstractAssignment_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__1_in_rule__ArtifactType__Group__01824);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:871:1: rule__ArtifactType__Group__1 : ( 'artifactType' ) rule__ArtifactType__Group__2 ;
    public final void rule__ArtifactType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:875:1: ( ( 'artifactType' ) rule__ArtifactType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:876:1: ( 'artifactType' ) rule__ArtifactType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:876:1: ( 'artifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:877:1: 'artifactType'
            {
             before(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1()); 
            match(input,35,FOLLOW_35_in_rule__ArtifactType__Group__11853); 
             after(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__2_in_rule__ArtifactType__Group__11863);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:891:1: rule__ArtifactType__Group__2 : ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3 ;
    public final void rule__ArtifactType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:895:1: ( ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:896:1: ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:896:1: ( ( rule__ArtifactType__NameAssignment_2 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:897:1: ( rule__ArtifactType__NameAssignment_2 )
            {
             before(grammarAccess.getArtifactTypeAccess().getNameAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:898:1: ( rule__ArtifactType__NameAssignment_2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:898:2: rule__ArtifactType__NameAssignment_2
            {
            pushFollow(FOLLOW_rule__ArtifactType__NameAssignment_2_in_rule__ArtifactType__Group__21891);
            rule__ArtifactType__NameAssignment_2();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getNameAssignment_2()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__3_in_rule__ArtifactType__Group__21900);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:909:1: rule__ArtifactType__Group__3 : ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4 ;
    public final void rule__ArtifactType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:913:1: ( ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:914:1: ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:914:1: ( ( rule__ArtifactType__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:915:1: ( rule__ArtifactType__Group_3__0 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:916:1: ( rule__ArtifactType__Group_3__0 )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==38) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:916:2: rule__ArtifactType__Group_3__0
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__Group_3__0_in_rule__ArtifactType__Group__31928);
                    rule__ArtifactType__Group_3__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getGroup_3()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__4_in_rule__ArtifactType__Group__31938);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:927:1: rule__ArtifactType__Group__4 : ( ( rule__ArtifactType__Group_4__0 )? ) rule__ArtifactType__Group__5 ;
    public final void rule__ArtifactType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:931:1: ( ( ( rule__ArtifactType__Group_4__0 )? ) rule__ArtifactType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:932:1: ( ( rule__ArtifactType__Group_4__0 )? ) rule__ArtifactType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:932:1: ( ( rule__ArtifactType__Group_4__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:933:1: ( rule__ArtifactType__Group_4__0 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:934:1: ( rule__ArtifactType__Group_4__0 )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==40) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:934:2: rule__ArtifactType__Group_4__0
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__Group_4__0_in_rule__ArtifactType__Group__41966);
                    rule__ArtifactType__Group_4__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getGroup_4()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__5_in_rule__ArtifactType__Group__41976);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:945:1: rule__ArtifactType__Group__5 : ( '{' ) rule__ArtifactType__Group__6 ;
    public final void rule__ArtifactType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:949:1: ( ( '{' ) rule__ArtifactType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:950:1: ( '{' ) rule__ArtifactType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:950:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:951:1: '{'
            {
             before(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_5()); 
            match(input,36,FOLLOW_36_in_rule__ArtifactType__Group__52005); 
             after(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_5()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__6_in_rule__ArtifactType__Group__52015);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:965:1: rule__ArtifactType__Group__6 : ( ( rule__ArtifactType__TypeGuidAssignment_6 )? ) rule__ArtifactType__Group__7 ;
    public final void rule__ArtifactType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:969:1: ( ( ( rule__ArtifactType__TypeGuidAssignment_6 )? ) rule__ArtifactType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:970:1: ( ( rule__ArtifactType__TypeGuidAssignment_6 )? ) rule__ArtifactType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:970:1: ( ( rule__ArtifactType__TypeGuidAssignment_6 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:971:1: ( rule__ArtifactType__TypeGuidAssignment_6 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getTypeGuidAssignment_6()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:972:1: ( rule__ArtifactType__TypeGuidAssignment_6 )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_STRING) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:972:2: rule__ArtifactType__TypeGuidAssignment_6
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__TypeGuidAssignment_6_in_rule__ArtifactType__Group__62043);
                    rule__ArtifactType__TypeGuidAssignment_6();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getTypeGuidAssignment_6()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__7_in_rule__ArtifactType__Group__62053);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:983:1: rule__ArtifactType__Group__7 : ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__ArtifactType__Group__8 ;
    public final void rule__ArtifactType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:987:1: ( ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__ArtifactType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:988:1: ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__ArtifactType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:988:1: ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:989:1: ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )*
            {
             before(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAssignment_7()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:990:1: ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==41) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:990:2: rule__ArtifactType__ValidAttributeTypesAssignment_7
            	    {
            	    pushFollow(FOLLOW_rule__ArtifactType__ValidAttributeTypesAssignment_7_in_rule__ArtifactType__Group__72081);
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

            pushFollow(FOLLOW_rule__ArtifactType__Group__8_in_rule__ArtifactType__Group__72091);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1001:1: rule__ArtifactType__Group__8 : ( '}' ) ;
    public final void rule__ArtifactType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1005:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1006:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1006:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1007:1: '}'
            {
             before(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_8()); 
            match(input,37,FOLLOW_37_in_rule__ArtifactType__Group__82120); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1038:1: rule__ArtifactType__Group_3__0 : ( 'extends' ) rule__ArtifactType__Group_3__1 ;
    public final void rule__ArtifactType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1042:1: ( ( 'extends' ) rule__ArtifactType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1043:1: ( 'extends' ) rule__ArtifactType__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1043:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1044:1: 'extends'
            {
             before(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0()); 
            match(input,38,FOLLOW_38_in_rule__ArtifactType__Group_3__02174); 
             after(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_3__1_in_rule__ArtifactType__Group_3__02184);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1058:1: rule__ArtifactType__Group_3__1 : ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__ArtifactType__Group_3__2 ;
    public final void rule__ArtifactType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1062:1: ( ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__ArtifactType__Group_3__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1063:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__ArtifactType__Group_3__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1063:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1064:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1065:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1065:2: rule__ArtifactType__SuperArtifactTypesAssignment_3_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_1_in_rule__ArtifactType__Group_3__12212);
            rule__ArtifactType__SuperArtifactTypesAssignment_3_1();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_3__2_in_rule__ArtifactType__Group_3__12221);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1076:1: rule__ArtifactType__Group_3__2 : ( ( rule__ArtifactType__Group_3_2__0 )* ) ;
    public final void rule__ArtifactType__Group_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1080:1: ( ( ( rule__ArtifactType__Group_3_2__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1081:1: ( ( rule__ArtifactType__Group_3_2__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1081:1: ( ( rule__ArtifactType__Group_3_2__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1082:1: ( rule__ArtifactType__Group_3_2__0 )*
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup_3_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1083:1: ( rule__ArtifactType__Group_3_2__0 )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==39) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1083:2: rule__ArtifactType__Group_3_2__0
            	    {
            	    pushFollow(FOLLOW_rule__ArtifactType__Group_3_2__0_in_rule__ArtifactType__Group_3__22249);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1099:1: rule__ArtifactType__Group_3_2__0 : ( ',' ) rule__ArtifactType__Group_3_2__1 ;
    public final void rule__ArtifactType__Group_3_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1103:1: ( ( ',' ) rule__ArtifactType__Group_3_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1104:1: ( ',' ) rule__ArtifactType__Group_3_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1104:1: ( ',' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1105:1: ','
            {
             before(grammarAccess.getArtifactTypeAccess().getCommaKeyword_3_2_0()); 
            match(input,39,FOLLOW_39_in_rule__ArtifactType__Group_3_2__02291); 
             after(grammarAccess.getArtifactTypeAccess().getCommaKeyword_3_2_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_3_2__1_in_rule__ArtifactType__Group_3_2__02301);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1119:1: rule__ArtifactType__Group_3_2__1 : ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) ;
    public final void rule__ArtifactType__Group_3_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1123:1: ( ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1124:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1124:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1125:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesAssignment_3_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1126:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1126:2: rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1_in_rule__ArtifactType__Group_3_2__12329);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1140:1: rule__ArtifactType__Group_4__0 : ( 'overrides' ) rule__ArtifactType__Group_4__1 ;
    public final void rule__ArtifactType__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1144:1: ( ( 'overrides' ) rule__ArtifactType__Group_4__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1145:1: ( 'overrides' ) rule__ArtifactType__Group_4__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1145:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1146:1: 'overrides'
            {
             before(grammarAccess.getArtifactTypeAccess().getOverridesKeyword_4_0()); 
            match(input,40,FOLLOW_40_in_rule__ArtifactType__Group_4__02368); 
             after(grammarAccess.getArtifactTypeAccess().getOverridesKeyword_4_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_4__1_in_rule__ArtifactType__Group_4__02378);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1160:1: rule__ArtifactType__Group_4__1 : ( ( rule__ArtifactType__OverrideAssignment_4_1 ) ) ;
    public final void rule__ArtifactType__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1164:1: ( ( ( rule__ArtifactType__OverrideAssignment_4_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1165:1: ( ( rule__ArtifactType__OverrideAssignment_4_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1165:1: ( ( rule__ArtifactType__OverrideAssignment_4_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1166:1: ( rule__ArtifactType__OverrideAssignment_4_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getOverrideAssignment_4_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1167:1: ( rule__ArtifactType__OverrideAssignment_4_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1167:2: rule__ArtifactType__OverrideAssignment_4_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__OverrideAssignment_4_1_in_rule__ArtifactType__Group_4__12406);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1181:1: rule__AttributeTypeRef__Group__0 : ( 'attribute' ) rule__AttributeTypeRef__Group__1 ;
    public final void rule__AttributeTypeRef__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1185:1: ( ( 'attribute' ) rule__AttributeTypeRef__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1186:1: ( 'attribute' ) rule__AttributeTypeRef__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1186:1: ( 'attribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1187:1: 'attribute'
            {
             before(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0()); 
            match(input,41,FOLLOW_41_in_rule__AttributeTypeRef__Group__02445); 
             after(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__1_in_rule__AttributeTypeRef__Group__02455);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1201:1: rule__AttributeTypeRef__Group__1 : ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__AttributeTypeRef__Group__2 ;
    public final void rule__AttributeTypeRef__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1205:1: ( ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__AttributeTypeRef__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1206:1: ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__AttributeTypeRef__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1206:1: ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1207:1: ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1208:1: ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1208:2: rule__AttributeTypeRef__ValidAttributeTypeAssignment_1
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__AttributeTypeRef__Group__12483);
            rule__AttributeTypeRef__ValidAttributeTypeAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__2_in_rule__AttributeTypeRef__Group__12492);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1219:1: rule__AttributeTypeRef__Group__2 : ( ( rule__AttributeTypeRef__Group_2__0 )? ) ;
    public final void rule__AttributeTypeRef__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1223:1: ( ( ( rule__AttributeTypeRef__Group_2__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1224:1: ( ( rule__AttributeTypeRef__Group_2__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1224:1: ( ( rule__AttributeTypeRef__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1225:1: ( rule__AttributeTypeRef__Group_2__0 )?
            {
             before(grammarAccess.getAttributeTypeRefAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1226:1: ( rule__AttributeTypeRef__Group_2__0 )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==42) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1226:2: rule__AttributeTypeRef__Group_2__0
                    {
                    pushFollow(FOLLOW_rule__AttributeTypeRef__Group_2__0_in_rule__AttributeTypeRef__Group__22520);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1242:1: rule__AttributeTypeRef__Group_2__0 : ( 'branchGuid' ) rule__AttributeTypeRef__Group_2__1 ;
    public final void rule__AttributeTypeRef__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1246:1: ( ( 'branchGuid' ) rule__AttributeTypeRef__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1247:1: ( 'branchGuid' ) rule__AttributeTypeRef__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1247:1: ( 'branchGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1248:1: 'branchGuid'
            {
             before(grammarAccess.getAttributeTypeRefAccess().getBranchGuidKeyword_2_0()); 
            match(input,42,FOLLOW_42_in_rule__AttributeTypeRef__Group_2__02562); 
             after(grammarAccess.getAttributeTypeRefAccess().getBranchGuidKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group_2__1_in_rule__AttributeTypeRef__Group_2__02572);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1262:1: rule__AttributeTypeRef__Group_2__1 : ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) ) ;
    public final void rule__AttributeTypeRef__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1266:1: ( ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1267:1: ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1267:1: ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1268:1: ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getBranchGuidAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1269:1: ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1269:2: rule__AttributeTypeRef__BranchGuidAssignment_2_1
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__BranchGuidAssignment_2_1_in_rule__AttributeTypeRef__Group_2__12600);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1283:1: rule__AttributeType__Group__0 : ( ( rule__AttributeType__TypeGuidAssignment_0 )? ) rule__AttributeType__Group__1 ;
    public final void rule__AttributeType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1287:1: ( ( ( rule__AttributeType__TypeGuidAssignment_0 )? ) rule__AttributeType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1288:1: ( ( rule__AttributeType__TypeGuidAssignment_0 )? ) rule__AttributeType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1288:1: ( ( rule__AttributeType__TypeGuidAssignment_0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1289:1: ( rule__AttributeType__TypeGuidAssignment_0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getTypeGuidAssignment_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1290:1: ( rule__AttributeType__TypeGuidAssignment_0 )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RULE_STRING) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1290:2: rule__AttributeType__TypeGuidAssignment_0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__TypeGuidAssignment_0_in_rule__AttributeType__Group__02638);
                    rule__AttributeType__TypeGuidAssignment_0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getTypeGuidAssignment_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__1_in_rule__AttributeType__Group__02648);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1301:1: rule__AttributeType__Group__1 : ( 'attributeType' ) rule__AttributeType__Group__2 ;
    public final void rule__AttributeType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1305:1: ( ( 'attributeType' ) rule__AttributeType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1306:1: ( 'attributeType' ) rule__AttributeType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1306:1: ( 'attributeType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1307:1: 'attributeType'
            {
             before(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_1()); 
            match(input,43,FOLLOW_43_in_rule__AttributeType__Group__12677); 
             after(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_1()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__2_in_rule__AttributeType__Group__12687);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1321:1: rule__AttributeType__Group__2 : ( ( rule__AttributeType__NameAssignment_2 ) ) rule__AttributeType__Group__3 ;
    public final void rule__AttributeType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1325:1: ( ( ( rule__AttributeType__NameAssignment_2 ) ) rule__AttributeType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1326:1: ( ( rule__AttributeType__NameAssignment_2 ) ) rule__AttributeType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1326:1: ( ( rule__AttributeType__NameAssignment_2 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1327:1: ( rule__AttributeType__NameAssignment_2 )
            {
             before(grammarAccess.getAttributeTypeAccess().getNameAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1328:1: ( rule__AttributeType__NameAssignment_2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1328:2: rule__AttributeType__NameAssignment_2
            {
            pushFollow(FOLLOW_rule__AttributeType__NameAssignment_2_in_rule__AttributeType__Group__22715);
            rule__AttributeType__NameAssignment_2();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getNameAssignment_2()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__3_in_rule__AttributeType__Group__22724);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1339:1: rule__AttributeType__Group__3 : ( ( rule__AttributeType__Group_3__0 ) ) rule__AttributeType__Group__4 ;
    public final void rule__AttributeType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1343:1: ( ( ( rule__AttributeType__Group_3__0 ) ) rule__AttributeType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1344:1: ( ( rule__AttributeType__Group_3__0 ) ) rule__AttributeType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1344:1: ( ( rule__AttributeType__Group_3__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1345:1: ( rule__AttributeType__Group_3__0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1346:1: ( rule__AttributeType__Group_3__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1346:2: rule__AttributeType__Group_3__0
            {
            pushFollow(FOLLOW_rule__AttributeType__Group_3__0_in_rule__AttributeType__Group__32752);
            rule__AttributeType__Group_3__0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_3()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__4_in_rule__AttributeType__Group__32761);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1357:1: rule__AttributeType__Group__4 : ( ( rule__AttributeType__Group_4__0 )? ) rule__AttributeType__Group__5 ;
    public final void rule__AttributeType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1361:1: ( ( ( rule__AttributeType__Group_4__0 )? ) rule__AttributeType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1362:1: ( ( rule__AttributeType__Group_4__0 )? ) rule__AttributeType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1362:1: ( ( rule__AttributeType__Group_4__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1363:1: ( rule__AttributeType__Group_4__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1364:1: ( rule__AttributeType__Group_4__0 )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==40) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1364:2: rule__AttributeType__Group_4__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_4__0_in_rule__AttributeType__Group__42789);
                    rule__AttributeType__Group_4__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_4()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__5_in_rule__AttributeType__Group__42799);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1375:1: rule__AttributeType__Group__5 : ( '{' ) rule__AttributeType__Group__6 ;
    public final void rule__AttributeType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1379:1: ( ( '{' ) rule__AttributeType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1380:1: ( '{' ) rule__AttributeType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1380:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1381:1: '{'
            {
             before(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_5()); 
            match(input,36,FOLLOW_36_in_rule__AttributeType__Group__52828); 
             after(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_5()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__6_in_rule__AttributeType__Group__52838);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1395:1: rule__AttributeType__Group__6 : ( 'dataProvider' ) rule__AttributeType__Group__7 ;
    public final void rule__AttributeType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1399:1: ( ( 'dataProvider' ) rule__AttributeType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1400:1: ( 'dataProvider' ) rule__AttributeType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1400:1: ( 'dataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1401:1: 'dataProvider'
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_6()); 
            match(input,44,FOLLOW_44_in_rule__AttributeType__Group__62867); 
             after(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_6()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__7_in_rule__AttributeType__Group__62877);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1415:1: rule__AttributeType__Group__7 : ( ( rule__AttributeType__DataProviderAssignment_7 ) ) rule__AttributeType__Group__8 ;
    public final void rule__AttributeType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1419:1: ( ( ( rule__AttributeType__DataProviderAssignment_7 ) ) rule__AttributeType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1420:1: ( ( rule__AttributeType__DataProviderAssignment_7 ) ) rule__AttributeType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1420:1: ( ( rule__AttributeType__DataProviderAssignment_7 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1421:1: ( rule__AttributeType__DataProviderAssignment_7 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderAssignment_7()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1422:1: ( rule__AttributeType__DataProviderAssignment_7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1422:2: rule__AttributeType__DataProviderAssignment_7
            {
            pushFollow(FOLLOW_rule__AttributeType__DataProviderAssignment_7_in_rule__AttributeType__Group__72905);
            rule__AttributeType__DataProviderAssignment_7();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDataProviderAssignment_7()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__8_in_rule__AttributeType__Group__72914);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1433:1: rule__AttributeType__Group__8 : ( 'min' ) rule__AttributeType__Group__9 ;
    public final void rule__AttributeType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1437:1: ( ( 'min' ) rule__AttributeType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1438:1: ( 'min' ) rule__AttributeType__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1438:1: ( 'min' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1439:1: 'min'
            {
             before(grammarAccess.getAttributeTypeAccess().getMinKeyword_8()); 
            match(input,45,FOLLOW_45_in_rule__AttributeType__Group__82943); 
             after(grammarAccess.getAttributeTypeAccess().getMinKeyword_8()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__9_in_rule__AttributeType__Group__82953);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1453:1: rule__AttributeType__Group__9 : ( ( rule__AttributeType__MinAssignment_9 ) ) rule__AttributeType__Group__10 ;
    public final void rule__AttributeType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1457:1: ( ( ( rule__AttributeType__MinAssignment_9 ) ) rule__AttributeType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1458:1: ( ( rule__AttributeType__MinAssignment_9 ) ) rule__AttributeType__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1458:1: ( ( rule__AttributeType__MinAssignment_9 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1459:1: ( rule__AttributeType__MinAssignment_9 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMinAssignment_9()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1460:1: ( rule__AttributeType__MinAssignment_9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1460:2: rule__AttributeType__MinAssignment_9
            {
            pushFollow(FOLLOW_rule__AttributeType__MinAssignment_9_in_rule__AttributeType__Group__92981);
            rule__AttributeType__MinAssignment_9();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMinAssignment_9()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__10_in_rule__AttributeType__Group__92990);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1471:1: rule__AttributeType__Group__10 : ( 'max' ) rule__AttributeType__Group__11 ;
    public final void rule__AttributeType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1475:1: ( ( 'max' ) rule__AttributeType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1476:1: ( 'max' ) rule__AttributeType__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1476:1: ( 'max' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1477:1: 'max'
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxKeyword_10()); 
            match(input,46,FOLLOW_46_in_rule__AttributeType__Group__103019); 
             after(grammarAccess.getAttributeTypeAccess().getMaxKeyword_10()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__11_in_rule__AttributeType__Group__103029);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1491:1: rule__AttributeType__Group__11 : ( ( rule__AttributeType__MaxAssignment_11 ) ) rule__AttributeType__Group__12 ;
    public final void rule__AttributeType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1495:1: ( ( ( rule__AttributeType__MaxAssignment_11 ) ) rule__AttributeType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1496:1: ( ( rule__AttributeType__MaxAssignment_11 ) ) rule__AttributeType__Group__12
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1496:1: ( ( rule__AttributeType__MaxAssignment_11 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1497:1: ( rule__AttributeType__MaxAssignment_11 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxAssignment_11()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1498:1: ( rule__AttributeType__MaxAssignment_11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1498:2: rule__AttributeType__MaxAssignment_11
            {
            pushFollow(FOLLOW_rule__AttributeType__MaxAssignment_11_in_rule__AttributeType__Group__113057);
            rule__AttributeType__MaxAssignment_11();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMaxAssignment_11()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__12_in_rule__AttributeType__Group__113066);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1509:1: rule__AttributeType__Group__12 : ( ( rule__AttributeType__Group_12__0 )? ) rule__AttributeType__Group__13 ;
    public final void rule__AttributeType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1513:1: ( ( ( rule__AttributeType__Group_12__0 )? ) rule__AttributeType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1514:1: ( ( rule__AttributeType__Group_12__0 )? ) rule__AttributeType__Group__13
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1514:1: ( ( rule__AttributeType__Group_12__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1515:1: ( rule__AttributeType__Group_12__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_12()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1516:1: ( rule__AttributeType__Group_12__0 )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==47) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1516:2: rule__AttributeType__Group_12__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_12__0_in_rule__AttributeType__Group__123094);
                    rule__AttributeType__Group_12__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_12()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__13_in_rule__AttributeType__Group__123104);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1527:1: rule__AttributeType__Group__13 : ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14 ;
    public final void rule__AttributeType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1531:1: ( ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1532:1: ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1532:1: ( ( rule__AttributeType__Group_13__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1533:1: ( rule__AttributeType__Group_13__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_13()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1534:1: ( rule__AttributeType__Group_13__0 )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==48) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1534:2: rule__AttributeType__Group_13__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_13__0_in_rule__AttributeType__Group__133132);
                    rule__AttributeType__Group_13__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_13()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__14_in_rule__AttributeType__Group__133142);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1545:1: rule__AttributeType__Group__14 : ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15 ;
    public final void rule__AttributeType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1549:1: ( ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1550:1: ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1550:1: ( ( rule__AttributeType__Group_14__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1551:1: ( rule__AttributeType__Group_14__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_14()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1552:1: ( rule__AttributeType__Group_14__0 )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==49) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1552:2: rule__AttributeType__Group_14__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_14__0_in_rule__AttributeType__Group__143170);
                    rule__AttributeType__Group_14__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_14()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__15_in_rule__AttributeType__Group__143180);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1563:1: rule__AttributeType__Group__15 : ( ( rule__AttributeType__Group_15__0 )? ) rule__AttributeType__Group__16 ;
    public final void rule__AttributeType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1567:1: ( ( ( rule__AttributeType__Group_15__0 )? ) rule__AttributeType__Group__16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1568:1: ( ( rule__AttributeType__Group_15__0 )? ) rule__AttributeType__Group__16
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1568:1: ( ( rule__AttributeType__Group_15__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1569:1: ( rule__AttributeType__Group_15__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_15()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1570:1: ( rule__AttributeType__Group_15__0 )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==50) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1570:2: rule__AttributeType__Group_15__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_15__0_in_rule__AttributeType__Group__153208);
                    rule__AttributeType__Group_15__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_15()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__16_in_rule__AttributeType__Group__153218);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1581:1: rule__AttributeType__Group__16 : ( ( rule__AttributeType__Group_16__0 )? ) rule__AttributeType__Group__17 ;
    public final void rule__AttributeType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1585:1: ( ( ( rule__AttributeType__Group_16__0 )? ) rule__AttributeType__Group__17 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1586:1: ( ( rule__AttributeType__Group_16__0 )? ) rule__AttributeType__Group__17
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1586:1: ( ( rule__AttributeType__Group_16__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1587:1: ( rule__AttributeType__Group_16__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_16()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1588:1: ( rule__AttributeType__Group_16__0 )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==51) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1588:2: rule__AttributeType__Group_16__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_16__0_in_rule__AttributeType__Group__163246);
                    rule__AttributeType__Group_16__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_16()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__17_in_rule__AttributeType__Group__163256);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1599:1: rule__AttributeType__Group__17 : ( '}' ) ;
    public final void rule__AttributeType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1603:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1604:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1604:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1605:1: '}'
            {
             before(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_17()); 
            match(input,37,FOLLOW_37_in_rule__AttributeType__Group__173285); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1654:1: rule__AttributeType__Group_3__0 : ( 'extends' ) rule__AttributeType__Group_3__1 ;
    public final void rule__AttributeType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1658:1: ( ( 'extends' ) rule__AttributeType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1659:1: ( 'extends' ) rule__AttributeType__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1659:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1660:1: 'extends'
            {
             before(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_3_0()); 
            match(input,38,FOLLOW_38_in_rule__AttributeType__Group_3__03357); 
             after(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_3_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_3__1_in_rule__AttributeType__Group_3__03367);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1674:1: rule__AttributeType__Group_3__1 : ( ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 ) ) ;
    public final void rule__AttributeType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1678:1: ( ( ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1679:1: ( ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1679:1: ( ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1680:1: ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAssignment_3_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1681:1: ( rule__AttributeType__BaseAttributeTypeAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1681:2: rule__AttributeType__BaseAttributeTypeAssignment_3_1
            {
            pushFollow(FOLLOW_rule__AttributeType__BaseAttributeTypeAssignment_3_1_in_rule__AttributeType__Group_3__13395);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1695:1: rule__AttributeType__Group_4__0 : ( 'overrides' ) rule__AttributeType__Group_4__1 ;
    public final void rule__AttributeType__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1699:1: ( ( 'overrides' ) rule__AttributeType__Group_4__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1700:1: ( 'overrides' ) rule__AttributeType__Group_4__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1700:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1701:1: 'overrides'
            {
             before(grammarAccess.getAttributeTypeAccess().getOverridesKeyword_4_0()); 
            match(input,40,FOLLOW_40_in_rule__AttributeType__Group_4__03434); 
             after(grammarAccess.getAttributeTypeAccess().getOverridesKeyword_4_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_4__1_in_rule__AttributeType__Group_4__03444);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1715:1: rule__AttributeType__Group_4__1 : ( ( rule__AttributeType__OverrideAssignment_4_1 ) ) ;
    public final void rule__AttributeType__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1719:1: ( ( ( rule__AttributeType__OverrideAssignment_4_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1720:1: ( ( rule__AttributeType__OverrideAssignment_4_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1720:1: ( ( rule__AttributeType__OverrideAssignment_4_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1721:1: ( rule__AttributeType__OverrideAssignment_4_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getOverrideAssignment_4_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1722:1: ( rule__AttributeType__OverrideAssignment_4_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1722:2: rule__AttributeType__OverrideAssignment_4_1
            {
            pushFollow(FOLLOW_rule__AttributeType__OverrideAssignment_4_1_in_rule__AttributeType__Group_4__13472);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1736:1: rule__AttributeType__Group_12__0 : ( 'taggerId' ) rule__AttributeType__Group_12__1 ;
    public final void rule__AttributeType__Group_12__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1740:1: ( ( 'taggerId' ) rule__AttributeType__Group_12__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1741:1: ( 'taggerId' ) rule__AttributeType__Group_12__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1741:1: ( 'taggerId' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1742:1: 'taggerId'
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_12_0()); 
            match(input,47,FOLLOW_47_in_rule__AttributeType__Group_12__03511); 
             after(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_12_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_12__1_in_rule__AttributeType__Group_12__03521);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1756:1: rule__AttributeType__Group_12__1 : ( ( rule__AttributeType__TaggerIdAssignment_12_1 ) ) ;
    public final void rule__AttributeType__Group_12__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1760:1: ( ( ( rule__AttributeType__TaggerIdAssignment_12_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1761:1: ( ( rule__AttributeType__TaggerIdAssignment_12_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1761:1: ( ( rule__AttributeType__TaggerIdAssignment_12_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1762:1: ( rule__AttributeType__TaggerIdAssignment_12_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdAssignment_12_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1763:1: ( rule__AttributeType__TaggerIdAssignment_12_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1763:2: rule__AttributeType__TaggerIdAssignment_12_1
            {
            pushFollow(FOLLOW_rule__AttributeType__TaggerIdAssignment_12_1_in_rule__AttributeType__Group_12__13549);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1777:1: rule__AttributeType__Group_13__0 : ( 'enumType' ) rule__AttributeType__Group_13__1 ;
    public final void rule__AttributeType__Group_13__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1781:1: ( ( 'enumType' ) rule__AttributeType__Group_13__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1782:1: ( 'enumType' ) rule__AttributeType__Group_13__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1782:1: ( 'enumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1783:1: 'enumType'
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_13_0()); 
            match(input,48,FOLLOW_48_in_rule__AttributeType__Group_13__03588); 
             after(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_13_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_13__1_in_rule__AttributeType__Group_13__03598);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1797:1: rule__AttributeType__Group_13__1 : ( ( rule__AttributeType__EnumTypeAssignment_13_1 ) ) ;
    public final void rule__AttributeType__Group_13__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1801:1: ( ( ( rule__AttributeType__EnumTypeAssignment_13_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1802:1: ( ( rule__AttributeType__EnumTypeAssignment_13_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1802:1: ( ( rule__AttributeType__EnumTypeAssignment_13_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1803:1: ( rule__AttributeType__EnumTypeAssignment_13_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeAssignment_13_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1804:1: ( rule__AttributeType__EnumTypeAssignment_13_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1804:2: rule__AttributeType__EnumTypeAssignment_13_1
            {
            pushFollow(FOLLOW_rule__AttributeType__EnumTypeAssignment_13_1_in_rule__AttributeType__Group_13__13626);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1818:1: rule__AttributeType__Group_14__0 : ( 'description' ) rule__AttributeType__Group_14__1 ;
    public final void rule__AttributeType__Group_14__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1822:1: ( ( 'description' ) rule__AttributeType__Group_14__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1823:1: ( 'description' ) rule__AttributeType__Group_14__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1823:1: ( 'description' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1824:1: 'description'
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_14_0()); 
            match(input,49,FOLLOW_49_in_rule__AttributeType__Group_14__03665); 
             after(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_14_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_14__1_in_rule__AttributeType__Group_14__03675);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1838:1: rule__AttributeType__Group_14__1 : ( ( rule__AttributeType__DescriptionAssignment_14_1 ) ) ;
    public final void rule__AttributeType__Group_14__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1842:1: ( ( ( rule__AttributeType__DescriptionAssignment_14_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1843:1: ( ( rule__AttributeType__DescriptionAssignment_14_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1843:1: ( ( rule__AttributeType__DescriptionAssignment_14_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1844:1: ( rule__AttributeType__DescriptionAssignment_14_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionAssignment_14_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1845:1: ( rule__AttributeType__DescriptionAssignment_14_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1845:2: rule__AttributeType__DescriptionAssignment_14_1
            {
            pushFollow(FOLLOW_rule__AttributeType__DescriptionAssignment_14_1_in_rule__AttributeType__Group_14__13703);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1859:1: rule__AttributeType__Group_15__0 : ( 'defaultValue' ) rule__AttributeType__Group_15__1 ;
    public final void rule__AttributeType__Group_15__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1863:1: ( ( 'defaultValue' ) rule__AttributeType__Group_15__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1864:1: ( 'defaultValue' ) rule__AttributeType__Group_15__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1864:1: ( 'defaultValue' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1865:1: 'defaultValue'
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_15_0()); 
            match(input,50,FOLLOW_50_in_rule__AttributeType__Group_15__03742); 
             after(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_15_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_15__1_in_rule__AttributeType__Group_15__03752);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1879:1: rule__AttributeType__Group_15__1 : ( ( rule__AttributeType__DefaultValueAssignment_15_1 ) ) ;
    public final void rule__AttributeType__Group_15__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1883:1: ( ( ( rule__AttributeType__DefaultValueAssignment_15_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1884:1: ( ( rule__AttributeType__DefaultValueAssignment_15_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1884:1: ( ( rule__AttributeType__DefaultValueAssignment_15_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1885:1: ( rule__AttributeType__DefaultValueAssignment_15_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueAssignment_15_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1886:1: ( rule__AttributeType__DefaultValueAssignment_15_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1886:2: rule__AttributeType__DefaultValueAssignment_15_1
            {
            pushFollow(FOLLOW_rule__AttributeType__DefaultValueAssignment_15_1_in_rule__AttributeType__Group_15__13780);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1900:1: rule__AttributeType__Group_16__0 : ( 'fileExtension' ) rule__AttributeType__Group_16__1 ;
    public final void rule__AttributeType__Group_16__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1904:1: ( ( 'fileExtension' ) rule__AttributeType__Group_16__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1905:1: ( 'fileExtension' ) rule__AttributeType__Group_16__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1905:1: ( 'fileExtension' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1906:1: 'fileExtension'
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_16_0()); 
            match(input,51,FOLLOW_51_in_rule__AttributeType__Group_16__03819); 
             after(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_16_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_16__1_in_rule__AttributeType__Group_16__03829);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1920:1: rule__AttributeType__Group_16__1 : ( ( rule__AttributeType__FileExtensionAssignment_16_1 ) ) ;
    public final void rule__AttributeType__Group_16__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1924:1: ( ( ( rule__AttributeType__FileExtensionAssignment_16_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1925:1: ( ( rule__AttributeType__FileExtensionAssignment_16_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1925:1: ( ( rule__AttributeType__FileExtensionAssignment_16_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1926:1: ( rule__AttributeType__FileExtensionAssignment_16_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionAssignment_16_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1927:1: ( rule__AttributeType__FileExtensionAssignment_16_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1927:2: rule__AttributeType__FileExtensionAssignment_16_1
            {
            pushFollow(FOLLOW_rule__AttributeType__FileExtensionAssignment_16_1_in_rule__AttributeType__Group_16__13857);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1941:1: rule__OseeEnumType__Group__0 : ( 'oseeEnumType' ) rule__OseeEnumType__Group__1 ;
    public final void rule__OseeEnumType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1945:1: ( ( 'oseeEnumType' ) rule__OseeEnumType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1946:1: ( 'oseeEnumType' ) rule__OseeEnumType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1946:1: ( 'oseeEnumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1947:1: 'oseeEnumType'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 
            match(input,52,FOLLOW_52_in_rule__OseeEnumType__Group__03896); 
             after(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__1_in_rule__OseeEnumType__Group__03906);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1961:1: rule__OseeEnumType__Group__1 : ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2 ;
    public final void rule__OseeEnumType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1965:1: ( ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1966:1: ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1966:1: ( ( rule__OseeEnumType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1967:1: ( rule__OseeEnumType__NameAssignment_1 )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1968:1: ( rule__OseeEnumType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1968:2: rule__OseeEnumType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__OseeEnumType__NameAssignment_1_in_rule__OseeEnumType__Group__13934);
            rule__OseeEnumType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__2_in_rule__OseeEnumType__Group__13943);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1979:1: rule__OseeEnumType__Group__2 : ( ( rule__OseeEnumType__Group_2__0 )? ) rule__OseeEnumType__Group__3 ;
    public final void rule__OseeEnumType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1983:1: ( ( ( rule__OseeEnumType__Group_2__0 )? ) rule__OseeEnumType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1984:1: ( ( rule__OseeEnumType__Group_2__0 )? ) rule__OseeEnumType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1984:1: ( ( rule__OseeEnumType__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1985:1: ( rule__OseeEnumType__Group_2__0 )?
            {
             before(grammarAccess.getOseeEnumTypeAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1986:1: ( rule__OseeEnumType__Group_2__0 )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==40) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1986:2: rule__OseeEnumType__Group_2__0
                    {
                    pushFollow(FOLLOW_rule__OseeEnumType__Group_2__0_in_rule__OseeEnumType__Group__23971);
                    rule__OseeEnumType__Group_2__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOseeEnumTypeAccess().getGroup_2()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__3_in_rule__OseeEnumType__Group__23981);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1997:1: rule__OseeEnumType__Group__3 : ( '{' ) rule__OseeEnumType__Group__4 ;
    public final void rule__OseeEnumType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2001:1: ( ( '{' ) rule__OseeEnumType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2002:1: ( '{' ) rule__OseeEnumType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2002:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2003:1: '{'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_3()); 
            match(input,36,FOLLOW_36_in_rule__OseeEnumType__Group__34010); 
             after(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_3()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__4_in_rule__OseeEnumType__Group__34020);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2017:1: rule__OseeEnumType__Group__4 : ( ( rule__OseeEnumType__TypeGuidAssignment_4 )? ) rule__OseeEnumType__Group__5 ;
    public final void rule__OseeEnumType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2021:1: ( ( ( rule__OseeEnumType__TypeGuidAssignment_4 )? ) rule__OseeEnumType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2022:1: ( ( rule__OseeEnumType__TypeGuidAssignment_4 )? ) rule__OseeEnumType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2022:1: ( ( rule__OseeEnumType__TypeGuidAssignment_4 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2023:1: ( rule__OseeEnumType__TypeGuidAssignment_4 )?
            {
             before(grammarAccess.getOseeEnumTypeAccess().getTypeGuidAssignment_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2024:1: ( rule__OseeEnumType__TypeGuidAssignment_4 )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==RULE_STRING) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2024:2: rule__OseeEnumType__TypeGuidAssignment_4
                    {
                    pushFollow(FOLLOW_rule__OseeEnumType__TypeGuidAssignment_4_in_rule__OseeEnumType__Group__44048);
                    rule__OseeEnumType__TypeGuidAssignment_4();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOseeEnumTypeAccess().getTypeGuidAssignment_4()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__5_in_rule__OseeEnumType__Group__44058);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2035:1: rule__OseeEnumType__Group__5 : ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* ) rule__OseeEnumType__Group__6 ;
    public final void rule__OseeEnumType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2039:1: ( ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* ) rule__OseeEnumType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2040:1: ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* ) rule__OseeEnumType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2040:1: ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2041:1: ( rule__OseeEnumType__EnumEntriesAssignment_5 )*
            {
             before(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesAssignment_5()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2042:1: ( rule__OseeEnumType__EnumEntriesAssignment_5 )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==53) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2042:2: rule__OseeEnumType__EnumEntriesAssignment_5
            	    {
            	    pushFollow(FOLLOW_rule__OseeEnumType__EnumEntriesAssignment_5_in_rule__OseeEnumType__Group__54086);
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

            pushFollow(FOLLOW_rule__OseeEnumType__Group__6_in_rule__OseeEnumType__Group__54096);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2053:1: rule__OseeEnumType__Group__6 : ( '}' ) ;
    public final void rule__OseeEnumType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2057:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2058:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2058:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2059:1: '}'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getRightCurlyBracketKeyword_6()); 
            match(input,37,FOLLOW_37_in_rule__OseeEnumType__Group__64125); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2086:1: rule__OseeEnumType__Group_2__0 : ( 'overrides' ) rule__OseeEnumType__Group_2__1 ;
    public final void rule__OseeEnumType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2090:1: ( ( 'overrides' ) rule__OseeEnumType__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2091:1: ( 'overrides' ) rule__OseeEnumType__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2091:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2092:1: 'overrides'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOverridesKeyword_2_0()); 
            match(input,40,FOLLOW_40_in_rule__OseeEnumType__Group_2__04175); 
             after(grammarAccess.getOseeEnumTypeAccess().getOverridesKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group_2__1_in_rule__OseeEnumType__Group_2__04185);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2106:1: rule__OseeEnumType__Group_2__1 : ( ( rule__OseeEnumType__OverrideAssignment_2_1 ) ) ;
    public final void rule__OseeEnumType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2110:1: ( ( ( rule__OseeEnumType__OverrideAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2111:1: ( ( rule__OseeEnumType__OverrideAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2111:1: ( ( rule__OseeEnumType__OverrideAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2112:1: ( rule__OseeEnumType__OverrideAssignment_2_1 )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOverrideAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2113:1: ( rule__OseeEnumType__OverrideAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2113:2: rule__OseeEnumType__OverrideAssignment_2_1
            {
            pushFollow(FOLLOW_rule__OseeEnumType__OverrideAssignment_2_1_in_rule__OseeEnumType__Group_2__14213);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2127:1: rule__OseeEnumEntry__Group__0 : ( 'entry' ) rule__OseeEnumEntry__Group__1 ;
    public final void rule__OseeEnumEntry__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2131:1: ( ( 'entry' ) rule__OseeEnumEntry__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2132:1: ( 'entry' ) rule__OseeEnumEntry__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2132:1: ( 'entry' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2133:1: 'entry'
            {
             before(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0()); 
            match(input,53,FOLLOW_53_in_rule__OseeEnumEntry__Group__04252); 
             after(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__1_in_rule__OseeEnumEntry__Group__04262);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2147:1: rule__OseeEnumEntry__Group__1 : ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2 ;
    public final void rule__OseeEnumEntry__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2151:1: ( ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2152:1: ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2152:1: ( ( rule__OseeEnumEntry__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2153:1: ( rule__OseeEnumEntry__NameAssignment_1 )
            {
             before(grammarAccess.getOseeEnumEntryAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2154:1: ( rule__OseeEnumEntry__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2154:2: rule__OseeEnumEntry__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__OseeEnumEntry__NameAssignment_1_in_rule__OseeEnumEntry__Group__14290);
            rule__OseeEnumEntry__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumEntryAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__2_in_rule__OseeEnumEntry__Group__14299);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2165:1: rule__OseeEnumEntry__Group__2 : ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? ) ;
    public final void rule__OseeEnumEntry__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2169:1: ( ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2170:1: ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2170:1: ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2171:1: ( rule__OseeEnumEntry__OrdinalAssignment_2 )?
            {
             before(grammarAccess.getOseeEnumEntryAccess().getOrdinalAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2172:1: ( rule__OseeEnumEntry__OrdinalAssignment_2 )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==RULE_WHOLE_NUM_STR) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2172:2: rule__OseeEnumEntry__OrdinalAssignment_2
                    {
                    pushFollow(FOLLOW_rule__OseeEnumEntry__OrdinalAssignment_2_in_rule__OseeEnumEntry__Group__24327);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2188:1: rule__RelationType__Group__0 : ( 'relationType' ) rule__RelationType__Group__1 ;
    public final void rule__RelationType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2192:1: ( ( 'relationType' ) rule__RelationType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2193:1: ( 'relationType' ) rule__RelationType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2193:1: ( 'relationType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2194:1: 'relationType'
            {
             before(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0()); 
            match(input,54,FOLLOW_54_in_rule__RelationType__Group__04369); 
             after(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__1_in_rule__RelationType__Group__04379);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2208:1: rule__RelationType__Group__1 : ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2 ;
    public final void rule__RelationType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2212:1: ( ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2213:1: ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2213:1: ( ( rule__RelationType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2214:1: ( rule__RelationType__NameAssignment_1 )
            {
             before(grammarAccess.getRelationTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2215:1: ( rule__RelationType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2215:2: rule__RelationType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__RelationType__NameAssignment_1_in_rule__RelationType__Group__14407);
            rule__RelationType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__2_in_rule__RelationType__Group__14416);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2226:1: rule__RelationType__Group__2 : ( ( rule__RelationType__Group_2__0 )? ) rule__RelationType__Group__3 ;
    public final void rule__RelationType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2230:1: ( ( ( rule__RelationType__Group_2__0 )? ) rule__RelationType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2231:1: ( ( rule__RelationType__Group_2__0 )? ) rule__RelationType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2231:1: ( ( rule__RelationType__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2232:1: ( rule__RelationType__Group_2__0 )?
            {
             before(grammarAccess.getRelationTypeAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2233:1: ( rule__RelationType__Group_2__0 )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==40) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2233:2: rule__RelationType__Group_2__0
                    {
                    pushFollow(FOLLOW_rule__RelationType__Group_2__0_in_rule__RelationType__Group__24444);
                    rule__RelationType__Group_2__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getRelationTypeAccess().getGroup_2()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__3_in_rule__RelationType__Group__24454);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2244:1: rule__RelationType__Group__3 : ( '{' ) rule__RelationType__Group__4 ;
    public final void rule__RelationType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2248:1: ( ( '{' ) rule__RelationType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2249:1: ( '{' ) rule__RelationType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2249:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2250:1: '{'
            {
             before(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_3()); 
            match(input,36,FOLLOW_36_in_rule__RelationType__Group__34483); 
             after(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_3()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__4_in_rule__RelationType__Group__34493);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2264:1: rule__RelationType__Group__4 : ( ( rule__RelationType__TypeGuidAssignment_4 )? ) rule__RelationType__Group__5 ;
    public final void rule__RelationType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2268:1: ( ( ( rule__RelationType__TypeGuidAssignment_4 )? ) rule__RelationType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2269:1: ( ( rule__RelationType__TypeGuidAssignment_4 )? ) rule__RelationType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2269:1: ( ( rule__RelationType__TypeGuidAssignment_4 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2270:1: ( rule__RelationType__TypeGuidAssignment_4 )?
            {
             before(grammarAccess.getRelationTypeAccess().getTypeGuidAssignment_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2271:1: ( rule__RelationType__TypeGuidAssignment_4 )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==RULE_STRING) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2271:2: rule__RelationType__TypeGuidAssignment_4
                    {
                    pushFollow(FOLLOW_rule__RelationType__TypeGuidAssignment_4_in_rule__RelationType__Group__44521);
                    rule__RelationType__TypeGuidAssignment_4();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getRelationTypeAccess().getTypeGuidAssignment_4()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__5_in_rule__RelationType__Group__44531);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2282:1: rule__RelationType__Group__5 : ( 'sideAName' ) rule__RelationType__Group__6 ;
    public final void rule__RelationType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2286:1: ( ( 'sideAName' ) rule__RelationType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2287:1: ( 'sideAName' ) rule__RelationType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2287:1: ( 'sideAName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2288:1: 'sideAName'
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameKeyword_5()); 
            match(input,55,FOLLOW_55_in_rule__RelationType__Group__54560); 
             after(grammarAccess.getRelationTypeAccess().getSideANameKeyword_5()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__6_in_rule__RelationType__Group__54570);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2302:1: rule__RelationType__Group__6 : ( ( rule__RelationType__SideANameAssignment_6 ) ) rule__RelationType__Group__7 ;
    public final void rule__RelationType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2306:1: ( ( ( rule__RelationType__SideANameAssignment_6 ) ) rule__RelationType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2307:1: ( ( rule__RelationType__SideANameAssignment_6 ) ) rule__RelationType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2307:1: ( ( rule__RelationType__SideANameAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2308:1: ( rule__RelationType__SideANameAssignment_6 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameAssignment_6()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2309:1: ( rule__RelationType__SideANameAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2309:2: rule__RelationType__SideANameAssignment_6
            {
            pushFollow(FOLLOW_rule__RelationType__SideANameAssignment_6_in_rule__RelationType__Group__64598);
            rule__RelationType__SideANameAssignment_6();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideANameAssignment_6()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__7_in_rule__RelationType__Group__64607);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2320:1: rule__RelationType__Group__7 : ( 'sideAArtifactType' ) rule__RelationType__Group__8 ;
    public final void rule__RelationType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2324:1: ( ( 'sideAArtifactType' ) rule__RelationType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2325:1: ( 'sideAArtifactType' ) rule__RelationType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2325:1: ( 'sideAArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2326:1: 'sideAArtifactType'
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_7()); 
            match(input,56,FOLLOW_56_in_rule__RelationType__Group__74636); 
             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_7()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__8_in_rule__RelationType__Group__74646);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2340:1: rule__RelationType__Group__8 : ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) ) rule__RelationType__Group__9 ;
    public final void rule__RelationType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2344:1: ( ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) ) rule__RelationType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2345:1: ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) ) rule__RelationType__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2345:1: ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2346:1: ( rule__RelationType__SideAArtifactTypeAssignment_8 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeAssignment_8()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2347:1: ( rule__RelationType__SideAArtifactTypeAssignment_8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2347:2: rule__RelationType__SideAArtifactTypeAssignment_8
            {
            pushFollow(FOLLOW_rule__RelationType__SideAArtifactTypeAssignment_8_in_rule__RelationType__Group__84674);
            rule__RelationType__SideAArtifactTypeAssignment_8();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeAssignment_8()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__9_in_rule__RelationType__Group__84683);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2358:1: rule__RelationType__Group__9 : ( 'sideBName' ) rule__RelationType__Group__10 ;
    public final void rule__RelationType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2362:1: ( ( 'sideBName' ) rule__RelationType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2363:1: ( 'sideBName' ) rule__RelationType__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2363:1: ( 'sideBName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2364:1: 'sideBName'
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_9()); 
            match(input,57,FOLLOW_57_in_rule__RelationType__Group__94712); 
             after(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_9()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__10_in_rule__RelationType__Group__94722);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2378:1: rule__RelationType__Group__10 : ( ( rule__RelationType__SideBNameAssignment_10 ) ) rule__RelationType__Group__11 ;
    public final void rule__RelationType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2382:1: ( ( ( rule__RelationType__SideBNameAssignment_10 ) ) rule__RelationType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2383:1: ( ( rule__RelationType__SideBNameAssignment_10 ) ) rule__RelationType__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2383:1: ( ( rule__RelationType__SideBNameAssignment_10 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2384:1: ( rule__RelationType__SideBNameAssignment_10 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameAssignment_10()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2385:1: ( rule__RelationType__SideBNameAssignment_10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2385:2: rule__RelationType__SideBNameAssignment_10
            {
            pushFollow(FOLLOW_rule__RelationType__SideBNameAssignment_10_in_rule__RelationType__Group__104750);
            rule__RelationType__SideBNameAssignment_10();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideBNameAssignment_10()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__11_in_rule__RelationType__Group__104759);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2396:1: rule__RelationType__Group__11 : ( 'sideBArtifactType' ) rule__RelationType__Group__12 ;
    public final void rule__RelationType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2400:1: ( ( 'sideBArtifactType' ) rule__RelationType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2401:1: ( 'sideBArtifactType' ) rule__RelationType__Group__12
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2401:1: ( 'sideBArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2402:1: 'sideBArtifactType'
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_11()); 
            match(input,58,FOLLOW_58_in_rule__RelationType__Group__114788); 
             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_11()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__12_in_rule__RelationType__Group__114798);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2416:1: rule__RelationType__Group__12 : ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) ) rule__RelationType__Group__13 ;
    public final void rule__RelationType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2420:1: ( ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) ) rule__RelationType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2421:1: ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) ) rule__RelationType__Group__13
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2421:1: ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2422:1: ( rule__RelationType__SideBArtifactTypeAssignment_12 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeAssignment_12()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2423:1: ( rule__RelationType__SideBArtifactTypeAssignment_12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2423:2: rule__RelationType__SideBArtifactTypeAssignment_12
            {
            pushFollow(FOLLOW_rule__RelationType__SideBArtifactTypeAssignment_12_in_rule__RelationType__Group__124826);
            rule__RelationType__SideBArtifactTypeAssignment_12();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeAssignment_12()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__13_in_rule__RelationType__Group__124835);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2434:1: rule__RelationType__Group__13 : ( 'defaultOrderType' ) rule__RelationType__Group__14 ;
    public final void rule__RelationType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2438:1: ( ( 'defaultOrderType' ) rule__RelationType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2439:1: ( 'defaultOrderType' ) rule__RelationType__Group__14
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2439:1: ( 'defaultOrderType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2440:1: 'defaultOrderType'
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_13()); 
            match(input,59,FOLLOW_59_in_rule__RelationType__Group__134864); 
             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_13()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__14_in_rule__RelationType__Group__134874);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2454:1: rule__RelationType__Group__14 : ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) ) rule__RelationType__Group__15 ;
    public final void rule__RelationType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2458:1: ( ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) ) rule__RelationType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2459:1: ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) ) rule__RelationType__Group__15
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2459:1: ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2460:1: ( rule__RelationType__DefaultOrderTypeAssignment_14 )
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAssignment_14()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2461:1: ( rule__RelationType__DefaultOrderTypeAssignment_14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2461:2: rule__RelationType__DefaultOrderTypeAssignment_14
            {
            pushFollow(FOLLOW_rule__RelationType__DefaultOrderTypeAssignment_14_in_rule__RelationType__Group__144902);
            rule__RelationType__DefaultOrderTypeAssignment_14();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAssignment_14()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__15_in_rule__RelationType__Group__144911);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2472:1: rule__RelationType__Group__15 : ( 'multiplicity' ) rule__RelationType__Group__16 ;
    public final void rule__RelationType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2476:1: ( ( 'multiplicity' ) rule__RelationType__Group__16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2477:1: ( 'multiplicity' ) rule__RelationType__Group__16
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2477:1: ( 'multiplicity' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2478:1: 'multiplicity'
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_15()); 
            match(input,60,FOLLOW_60_in_rule__RelationType__Group__154940); 
             after(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_15()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__16_in_rule__RelationType__Group__154950);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2492:1: rule__RelationType__Group__16 : ( ( rule__RelationType__MultiplicityAssignment_16 ) ) rule__RelationType__Group__17 ;
    public final void rule__RelationType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2496:1: ( ( ( rule__RelationType__MultiplicityAssignment_16 ) ) rule__RelationType__Group__17 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2497:1: ( ( rule__RelationType__MultiplicityAssignment_16 ) ) rule__RelationType__Group__17
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2497:1: ( ( rule__RelationType__MultiplicityAssignment_16 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2498:1: ( rule__RelationType__MultiplicityAssignment_16 )
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityAssignment_16()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2499:1: ( rule__RelationType__MultiplicityAssignment_16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2499:2: rule__RelationType__MultiplicityAssignment_16
            {
            pushFollow(FOLLOW_rule__RelationType__MultiplicityAssignment_16_in_rule__RelationType__Group__164978);
            rule__RelationType__MultiplicityAssignment_16();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getMultiplicityAssignment_16()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__17_in_rule__RelationType__Group__164987);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2510:1: rule__RelationType__Group__17 : ( '}' ) ;
    public final void rule__RelationType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2514:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2515:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2515:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2516:1: '}'
            {
             before(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_17()); 
            match(input,37,FOLLOW_37_in_rule__RelationType__Group__175016); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2565:1: rule__RelationType__Group_2__0 : ( 'overrides' ) rule__RelationType__Group_2__1 ;
    public final void rule__RelationType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2569:1: ( ( 'overrides' ) rule__RelationType__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2570:1: ( 'overrides' ) rule__RelationType__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2570:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2571:1: 'overrides'
            {
             before(grammarAccess.getRelationTypeAccess().getOverridesKeyword_2_0()); 
            match(input,40,FOLLOW_40_in_rule__RelationType__Group_2__05088); 
             after(grammarAccess.getRelationTypeAccess().getOverridesKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group_2__1_in_rule__RelationType__Group_2__05098);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2585:1: rule__RelationType__Group_2__1 : ( ( rule__RelationType__OverrideAssignment_2_1 ) ) ;
    public final void rule__RelationType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2589:1: ( ( ( rule__RelationType__OverrideAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2590:1: ( ( rule__RelationType__OverrideAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2590:1: ( ( rule__RelationType__OverrideAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2591:1: ( rule__RelationType__OverrideAssignment_2_1 )
            {
             before(grammarAccess.getRelationTypeAccess().getOverrideAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2592:1: ( rule__RelationType__OverrideAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2592:2: rule__RelationType__OverrideAssignment_2_1
            {
            pushFollow(FOLLOW_rule__RelationType__OverrideAssignment_2_1_in_rule__RelationType__Group_2__15126);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2606:1: rule__OseeTypeModel__ImportsAssignment_0 : ( ruleImport ) ;
    public final void rule__OseeTypeModel__ImportsAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2610:1: ( ( ruleImport ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2611:1: ( ruleImport )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2611:1: ( ruleImport )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2612:1: ruleImport
            {
             before(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0()); 
            pushFollow(FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_05164);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2621:1: rule__OseeTypeModel__TypesAssignment_1 : ( ruleOseeType ) ;
    public final void rule__OseeTypeModel__TypesAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2625:1: ( ( ruleOseeType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2626:1: ( ruleOseeType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2626:1: ( ruleOseeType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2627:1: ruleOseeType
            {
             before(grammarAccess.getOseeTypeModelAccess().getTypesOseeTypeParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleOseeType_in_rule__OseeTypeModel__TypesAssignment_15195);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2636:1: rule__Import__ImportURIAssignment_1 : ( RULE_STRING ) ;
    public final void rule__Import__ImportURIAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2640:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2641:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2641:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2642:1: RULE_STRING
            {
             before(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_15226); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2651:1: rule__ArtifactType__AbstractAssignment_0 : ( ( 'abstract' ) ) ;
    public final void rule__ArtifactType__AbstractAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2655:1: ( ( ( 'abstract' ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2656:1: ( ( 'abstract' ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2656:1: ( ( 'abstract' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2657:1: ( 'abstract' )
            {
             before(grammarAccess.getArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2658:1: ( 'abstract' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2659:1: 'abstract'
            {
             before(grammarAccess.getArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            match(input,61,FOLLOW_61_in_rule__ArtifactType__AbstractAssignment_05262); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2674:1: rule__ArtifactType__NameAssignment_2 : ( ruleNAME_REFERENCE ) ;
    public final void rule__ArtifactType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2678:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2679:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2679:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2680:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__NameAssignment_25301);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2689:1: rule__ArtifactType__SuperArtifactTypesAssignment_3_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__ArtifactType__SuperArtifactTypesAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2693:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2694:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2694:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2695:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2696:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2697:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_15336);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2708:1: rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2712:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2713:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2713:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2714:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_2_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2715:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2716:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeNAME_REFERENCEParserRuleCall_3_2_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_15375);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2727:1: rule__ArtifactType__OverrideAssignment_4_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__ArtifactType__OverrideAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2731:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2732:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2732:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2733:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getArtifactTypeAccess().getOverrideArtifactTypeCrossReference_4_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2734:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2735:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getOverrideArtifactTypeNAME_REFERENCEParserRuleCall_4_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__OverrideAssignment_4_15414);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2746:1: rule__ArtifactType__TypeGuidAssignment_6 : ( RULE_STRING ) ;
    public final void rule__ArtifactType__TypeGuidAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2750:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2751:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2751:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2752:1: RULE_STRING
            {
             before(grammarAccess.getArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__ArtifactType__TypeGuidAssignment_65449); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2761:1: rule__ArtifactType__ValidAttributeTypesAssignment_7 : ( ruleAttributeTypeRef ) ;
    public final void rule__ArtifactType__ValidAttributeTypesAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2765:1: ( ( ruleAttributeTypeRef ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2766:1: ( ruleAttributeTypeRef )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2766:1: ( ruleAttributeTypeRef )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2767:1: ruleAttributeTypeRef
            {
             before(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAttributeTypeRefParserRuleCall_7_0()); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_rule__ArtifactType__ValidAttributeTypesAssignment_75480);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2776:1: rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__AttributeTypeRef__ValidAttributeTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2780:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2781:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2781:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2782:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeCrossReference_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2783:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2784:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeNAME_REFERENCEParserRuleCall_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeTypeRef__ValidAttributeTypeAssignment_15515);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2795:1: rule__AttributeTypeRef__BranchGuidAssignment_2_1 : ( RULE_STRING ) ;
    public final void rule__AttributeTypeRef__BranchGuidAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2799:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2800:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2800:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2801:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeTypeRef__BranchGuidAssignment_2_15550); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2810:1: rule__AttributeType__TypeGuidAssignment_0 : ( RULE_STRING ) ;
    public final void rule__AttributeType__TypeGuidAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2814:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2815:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2815:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2816:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_0_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__TypeGuidAssignment_05581); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2825:1: rule__AttributeType__NameAssignment_2 : ( ruleNAME_REFERENCE ) ;
    public final void rule__AttributeType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2829:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2830:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2830:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2831:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__NameAssignment_25612);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2840:1: rule__AttributeType__BaseAttributeTypeAssignment_3_1 : ( ruleAttributeBaseType ) ;
    public final void rule__AttributeType__BaseAttributeTypeAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2844:1: ( ( ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2845:1: ( ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2845:1: ( ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2846:1: ruleAttributeBaseType
            {
             before(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_3_1_0()); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_rule__AttributeType__BaseAttributeTypeAssignment_3_15643);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2855:1: rule__AttributeType__OverrideAssignment_4_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__AttributeType__OverrideAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2859:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2860:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2860:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2861:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeCrossReference_4_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2862:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2863:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeNAME_REFERENCEParserRuleCall_4_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__OverrideAssignment_4_15678);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2874:1: rule__AttributeType__DataProviderAssignment_7 : ( ( rule__AttributeType__DataProviderAlternatives_7_0 ) ) ;
    public final void rule__AttributeType__DataProviderAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2878:1: ( ( ( rule__AttributeType__DataProviderAlternatives_7_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2879:1: ( ( rule__AttributeType__DataProviderAlternatives_7_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2879:1: ( ( rule__AttributeType__DataProviderAlternatives_7_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2880:1: ( rule__AttributeType__DataProviderAlternatives_7_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderAlternatives_7_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2881:1: ( rule__AttributeType__DataProviderAlternatives_7_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2881:2: rule__AttributeType__DataProviderAlternatives_7_0
            {
            pushFollow(FOLLOW_rule__AttributeType__DataProviderAlternatives_7_0_in_rule__AttributeType__DataProviderAssignment_75713);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2890:1: rule__AttributeType__MinAssignment_9 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__AttributeType__MinAssignment_9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2894:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2895:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2895:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2896:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_9_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MinAssignment_95746); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2905:1: rule__AttributeType__MaxAssignment_11 : ( ( rule__AttributeType__MaxAlternatives_11_0 ) ) ;
    public final void rule__AttributeType__MaxAssignment_11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2909:1: ( ( ( rule__AttributeType__MaxAlternatives_11_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2910:1: ( ( rule__AttributeType__MaxAlternatives_11_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2910:1: ( ( rule__AttributeType__MaxAlternatives_11_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2911:1: ( rule__AttributeType__MaxAlternatives_11_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxAlternatives_11_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2912:1: ( rule__AttributeType__MaxAlternatives_11_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2912:2: rule__AttributeType__MaxAlternatives_11_0
            {
            pushFollow(FOLLOW_rule__AttributeType__MaxAlternatives_11_0_in_rule__AttributeType__MaxAssignment_115777);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2921:1: rule__AttributeType__TaggerIdAssignment_12_1 : ( ( rule__AttributeType__TaggerIdAlternatives_12_1_0 ) ) ;
    public final void rule__AttributeType__TaggerIdAssignment_12_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2925:1: ( ( ( rule__AttributeType__TaggerIdAlternatives_12_1_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2926:1: ( ( rule__AttributeType__TaggerIdAlternatives_12_1_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2926:1: ( ( rule__AttributeType__TaggerIdAlternatives_12_1_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2927:1: ( rule__AttributeType__TaggerIdAlternatives_12_1_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdAlternatives_12_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2928:1: ( rule__AttributeType__TaggerIdAlternatives_12_1_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2928:2: rule__AttributeType__TaggerIdAlternatives_12_1_0
            {
            pushFollow(FOLLOW_rule__AttributeType__TaggerIdAlternatives_12_1_0_in_rule__AttributeType__TaggerIdAssignment_12_15810);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2937:1: rule__AttributeType__EnumTypeAssignment_13_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__AttributeType__EnumTypeAssignment_13_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2941:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2942:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2942:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2943:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_13_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2944:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2945:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeNAME_REFERENCEParserRuleCall_13_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__EnumTypeAssignment_13_15847);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2956:1: rule__AttributeType__DescriptionAssignment_14_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__DescriptionAssignment_14_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2960:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2961:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2961:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2962:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_14_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__DescriptionAssignment_14_15882); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2971:1: rule__AttributeType__DefaultValueAssignment_15_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__DefaultValueAssignment_15_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2975:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2976:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2976:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2977:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_15_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__DefaultValueAssignment_15_15913); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2986:1: rule__AttributeType__FileExtensionAssignment_16_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__FileExtensionAssignment_16_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2990:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2991:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2991:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2992:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_16_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__FileExtensionAssignment_16_15944); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3001:1: rule__OseeEnumType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__OseeEnumType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3005:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3006:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3006:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3007:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumType__NameAssignment_15975);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3016:1: rule__OseeEnumType__OverrideAssignment_2_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__OseeEnumType__OverrideAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3020:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3021:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3021:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3022:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOverrideOseeEnumTypeCrossReference_2_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3023:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3024:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOverrideOseeEnumTypeNAME_REFERENCEParserRuleCall_2_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumType__OverrideAssignment_2_16010);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3035:1: rule__OseeEnumType__TypeGuidAssignment_4 : ( RULE_STRING ) ;
    public final void rule__OseeEnumType__TypeGuidAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3039:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3040:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3040:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3041:1: RULE_STRING
            {
             before(grammarAccess.getOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__OseeEnumType__TypeGuidAssignment_46045); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3050:1: rule__OseeEnumType__EnumEntriesAssignment_5 : ( ruleOseeEnumEntry ) ;
    public final void rule__OseeEnumType__EnumEntriesAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3054:1: ( ( ruleOseeEnumEntry ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3055:1: ( ruleOseeEnumEntry )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3055:1: ( ruleOseeEnumEntry )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3056:1: ruleOseeEnumEntry
            {
             before(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesOseeEnumEntryParserRuleCall_5_0()); 
            pushFollow(FOLLOW_ruleOseeEnumEntry_in_rule__OseeEnumType__EnumEntriesAssignment_56076);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3065:1: rule__OseeEnumEntry__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__OseeEnumEntry__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3069:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3070:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3070:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3071:1: RULE_STRING
            {
             before(grammarAccess.getOseeEnumEntryAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__OseeEnumEntry__NameAssignment_16107); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3080:1: rule__OseeEnumEntry__OrdinalAssignment_2 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__OseeEnumEntry__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3084:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3085:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3085:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3086:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_rule__OseeEnumEntry__OrdinalAssignment_26138); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3095:1: rule__RelationType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__RelationType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3099:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3100:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3100:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3101:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__NameAssignment_16169);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3110:1: rule__RelationType__OverrideAssignment_2_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RelationType__OverrideAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3114:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3115:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3115:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3116:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getRelationTypeAccess().getOverrideRelationTypeCrossReference_2_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3117:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3118:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getOverrideRelationTypeNAME_REFERENCEParserRuleCall_2_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__OverrideAssignment_2_16204);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3129:1: rule__RelationType__TypeGuidAssignment_4 : ( RULE_STRING ) ;
    public final void rule__RelationType__TypeGuidAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3133:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3134:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3134:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3135:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__TypeGuidAssignment_46239); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3144:1: rule__RelationType__SideANameAssignment_6 : ( RULE_STRING ) ;
    public final void rule__RelationType__SideANameAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3148:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3149:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3149:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3150:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_6_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__SideANameAssignment_66270); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3159:1: rule__RelationType__SideAArtifactTypeAssignment_8 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RelationType__SideAArtifactTypeAssignment_8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3163:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3164:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3164:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3165:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeCrossReference_8_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3166:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3167:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeNAME_REFERENCEParserRuleCall_8_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideAArtifactTypeAssignment_86305);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3178:1: rule__RelationType__SideBNameAssignment_10 : ( RULE_STRING ) ;
    public final void rule__RelationType__SideBNameAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3182:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3183:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3183:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3184:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_10_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__SideBNameAssignment_106340); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3193:1: rule__RelationType__SideBArtifactTypeAssignment_12 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RelationType__SideBArtifactTypeAssignment_12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3197:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3198:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3198:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3199:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeCrossReference_12_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3200:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3201:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeNAME_REFERENCEParserRuleCall_12_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideBArtifactTypeAssignment_126375);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3212:1: rule__RelationType__DefaultOrderTypeAssignment_14 : ( ( rule__RelationType__DefaultOrderTypeAlternatives_14_0 ) ) ;
    public final void rule__RelationType__DefaultOrderTypeAssignment_14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3216:1: ( ( ( rule__RelationType__DefaultOrderTypeAlternatives_14_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3217:1: ( ( rule__RelationType__DefaultOrderTypeAlternatives_14_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3217:1: ( ( rule__RelationType__DefaultOrderTypeAlternatives_14_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3218:1: ( rule__RelationType__DefaultOrderTypeAlternatives_14_0 )
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAlternatives_14_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3219:1: ( rule__RelationType__DefaultOrderTypeAlternatives_14_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3219:2: rule__RelationType__DefaultOrderTypeAlternatives_14_0
            {
            pushFollow(FOLLOW_rule__RelationType__DefaultOrderTypeAlternatives_14_0_in_rule__RelationType__DefaultOrderTypeAssignment_146410);
            rule__RelationType__DefaultOrderTypeAlternatives_14_0();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAlternatives_14_0()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3228:1: rule__RelationType__MultiplicityAssignment_16 : ( ruleRelationMultiplicityEnum ) ;
    public final void rule__RelationType__MultiplicityAssignment_16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3232:1: ( ( ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3233:1: ( ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3233:1: ( ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3234:1: ruleRelationMultiplicityEnum
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0()); 
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_rule__RelationType__MultiplicityAssignment_166443);
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
    public static final BitSet FOLLOW_rule__RelationMultiplicityEnum__Alternatives_in_ruleRelationMultiplicityEnum790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_rule__OseeType__Alternatives825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_rule__OseeType__Alternatives842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_rule__OseeType__Alternatives859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_rule__OseeType__Alternatives876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_rule__AttributeType__DataProviderAlternatives_7_0909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_rule__AttributeType__DataProviderAlternatives_7_0929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_rule__AttributeType__DataProviderAlternatives_7_0949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__DataProviderAlternatives_7_0968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MaxAlternatives_11_01000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_rule__AttributeType__MaxAlternatives_11_01018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_rule__AttributeType__TaggerIdAlternatives_12_1_01053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__TaggerIdAlternatives_12_1_01072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_rule__AttributeBaseType__Alternatives1105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_rule__AttributeBaseType__Alternatives1125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_rule__AttributeBaseType__Alternatives1145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_rule__AttributeBaseType__Alternatives1165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_rule__AttributeBaseType__Alternatives1185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_rule__AttributeBaseType__Alternatives1205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_rule__AttributeBaseType__Alternatives1225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_rule__AttributeBaseType__Alternatives1245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_rule__AttributeBaseType__Alternatives1265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeBaseType__Alternatives1284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_rule__RelationType__DefaultOrderTypeAlternatives_14_01317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_rule__RelationType__DefaultOrderTypeAlternatives_14_01337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_rule__RelationType__DefaultOrderTypeAlternatives_14_01357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__RelationType__DefaultOrderTypeAlternatives_14_01376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_rule__RelationMultiplicityEnum__Alternatives1409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_rule__RelationMultiplicityEnum__Alternatives1430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_rule__RelationMultiplicityEnum__Alternatives1451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_rule__RelationMultiplicityEnum__Alternatives1472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__01509 = new BitSet(new long[]{0x2050080A00000012L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__TypesAssignment_1_in_rule__OseeTypeModel__Group__11547 = new BitSet(new long[]{0x2050080800000012L});
    public static final BitSet FOLLOW_33_in_rule__Import__Group__01587 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Import__Group__1_in_rule__Import__Group__01597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__11625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__01663 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__01671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__11699 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_rule__QUALIFIED_NAME__Group_1__01739 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__01749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__11777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__AbstractAssignment_0_in_rule__ArtifactType__Group__01814 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__1_in_rule__ArtifactType__Group__01824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_rule__ArtifactType__Group__11853 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__2_in_rule__ArtifactType__Group__11863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__NameAssignment_2_in_rule__ArtifactType__Group__21891 = new BitSet(new long[]{0x0000015000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__3_in_rule__ArtifactType__Group__21900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__0_in_rule__ArtifactType__Group__31928 = new BitSet(new long[]{0x0000011000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__4_in_rule__ArtifactType__Group__31938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_4__0_in_rule__ArtifactType__Group__41966 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__5_in_rule__ArtifactType__Group__41976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__ArtifactType__Group__52005 = new BitSet(new long[]{0x0000022000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__6_in_rule__ArtifactType__Group__52015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__TypeGuidAssignment_6_in_rule__ArtifactType__Group__62043 = new BitSet(new long[]{0x0000022000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__7_in_rule__ArtifactType__Group__62053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__ValidAttributeTypesAssignment_7_in_rule__ArtifactType__Group__72081 = new BitSet(new long[]{0x0000022000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__8_in_rule__ArtifactType__Group__72091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__ArtifactType__Group__82120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_rule__ArtifactType__Group_3__02174 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__1_in_rule__ArtifactType__Group_3__02184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_1_in_rule__ArtifactType__Group_3__12212 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__2_in_rule__ArtifactType__Group_3__12221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3_2__0_in_rule__ArtifactType__Group_3__22249 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_39_in_rule__ArtifactType__Group_3_2__02291 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3_2__1_in_rule__ArtifactType__Group_3_2__02301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1_in_rule__ArtifactType__Group_3_2__12329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule__ArtifactType__Group_4__02368 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_4__1_in_rule__ArtifactType__Group_4__02378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__OverrideAssignment_4_1_in_rule__ArtifactType__Group_4__12406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_rule__AttributeTypeRef__Group__02445 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__1_in_rule__AttributeTypeRef__Group__02455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__AttributeTypeRef__Group__12483 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__2_in_rule__AttributeTypeRef__Group__12492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group_2__0_in_rule__AttributeTypeRef__Group__22520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_rule__AttributeTypeRef__Group_2__02562 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group_2__1_in_rule__AttributeTypeRef__Group_2__02572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__BranchGuidAssignment_2_1_in_rule__AttributeTypeRef__Group_2__12600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TypeGuidAssignment_0_in_rule__AttributeType__Group__02638 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__1_in_rule__AttributeType__Group__02648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_rule__AttributeType__Group__12677 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__2_in_rule__AttributeType__Group__12687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__NameAssignment_2_in_rule__AttributeType__Group__22715 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__3_in_rule__AttributeType__Group__22724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_3__0_in_rule__AttributeType__Group__32752 = new BitSet(new long[]{0x0000011000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__4_in_rule__AttributeType__Group__32761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_4__0_in_rule__AttributeType__Group__42789 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__5_in_rule__AttributeType__Group__42799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__AttributeType__Group__52828 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__6_in_rule__AttributeType__Group__52838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_rule__AttributeType__Group__62867 = new BitSet(new long[]{0x0000000000007040L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__7_in_rule__AttributeType__Group__62877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DataProviderAssignment_7_in_rule__AttributeType__Group__72905 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__8_in_rule__AttributeType__Group__72914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_rule__AttributeType__Group__82943 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__9_in_rule__AttributeType__Group__82953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MinAssignment_9_in_rule__AttributeType__Group__92981 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__10_in_rule__AttributeType__Group__92990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_rule__AttributeType__Group__103019 = new BitSet(new long[]{0x0000000000008020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__11_in_rule__AttributeType__Group__103029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MaxAssignment_11_in_rule__AttributeType__Group__113057 = new BitSet(new long[]{0x000F802000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__12_in_rule__AttributeType__Group__113066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_12__0_in_rule__AttributeType__Group__123094 = new BitSet(new long[]{0x000F002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__13_in_rule__AttributeType__Group__123104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_13__0_in_rule__AttributeType__Group__133132 = new BitSet(new long[]{0x000E002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__14_in_rule__AttributeType__Group__133142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_14__0_in_rule__AttributeType__Group__143170 = new BitSet(new long[]{0x000C002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__15_in_rule__AttributeType__Group__143180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_15__0_in_rule__AttributeType__Group__153208 = new BitSet(new long[]{0x0008002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__16_in_rule__AttributeType__Group__153218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_16__0_in_rule__AttributeType__Group__163246 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__17_in_rule__AttributeType__Group__163256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__AttributeType__Group__173285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_rule__AttributeType__Group_3__03357 = new BitSet(new long[]{0x0000000003FE0040L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_3__1_in_rule__AttributeType__Group_3__03367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__BaseAttributeTypeAssignment_3_1_in_rule__AttributeType__Group_3__13395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule__AttributeType__Group_4__03434 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_4__1_in_rule__AttributeType__Group_4__03444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__OverrideAssignment_4_1_in_rule__AttributeType__Group_4__13472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_rule__AttributeType__Group_12__03511 = new BitSet(new long[]{0x0000000000010040L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_12__1_in_rule__AttributeType__Group_12__03521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TaggerIdAssignment_12_1_in_rule__AttributeType__Group_12__13549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_rule__AttributeType__Group_13__03588 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_13__1_in_rule__AttributeType__Group_13__03598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__EnumTypeAssignment_13_1_in_rule__AttributeType__Group_13__13626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_rule__AttributeType__Group_14__03665 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_14__1_in_rule__AttributeType__Group_14__03675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DescriptionAssignment_14_1_in_rule__AttributeType__Group_14__13703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_rule__AttributeType__Group_15__03742 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_15__1_in_rule__AttributeType__Group_15__03752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DefaultValueAssignment_15_1_in_rule__AttributeType__Group_15__13780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_rule__AttributeType__Group_16__03819 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_16__1_in_rule__AttributeType__Group_16__03829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__FileExtensionAssignment_16_1_in_rule__AttributeType__Group_16__13857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_rule__OseeEnumType__Group__03896 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__1_in_rule__OseeEnumType__Group__03906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__NameAssignment_1_in_rule__OseeEnumType__Group__13934 = new BitSet(new long[]{0x0000011000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__2_in_rule__OseeEnumType__Group__13943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group_2__0_in_rule__OseeEnumType__Group__23971 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__3_in_rule__OseeEnumType__Group__23981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__OseeEnumType__Group__34010 = new BitSet(new long[]{0x0020002000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__4_in_rule__OseeEnumType__Group__34020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__TypeGuidAssignment_4_in_rule__OseeEnumType__Group__44048 = new BitSet(new long[]{0x0020002000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__5_in_rule__OseeEnumType__Group__44058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__EnumEntriesAssignment_5_in_rule__OseeEnumType__Group__54086 = new BitSet(new long[]{0x0020002000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__6_in_rule__OseeEnumType__Group__54096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__OseeEnumType__Group__64125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule__OseeEnumType__Group_2__04175 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group_2__1_in_rule__OseeEnumType__Group_2__04185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__OverrideAssignment_2_1_in_rule__OseeEnumType__Group_2__14213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_rule__OseeEnumEntry__Group__04252 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__1_in_rule__OseeEnumEntry__Group__04262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__NameAssignment_1_in_rule__OseeEnumEntry__Group__14290 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__2_in_rule__OseeEnumEntry__Group__14299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__OrdinalAssignment_2_in_rule__OseeEnumEntry__Group__24327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_rule__RelationType__Group__04369 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__1_in_rule__RelationType__Group__04379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__NameAssignment_1_in_rule__RelationType__Group__14407 = new BitSet(new long[]{0x0000011000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__2_in_rule__RelationType__Group__14416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__Group_2__0_in_rule__RelationType__Group__24444 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__3_in_rule__RelationType__Group__24454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__RelationType__Group__34483 = new BitSet(new long[]{0x0080000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__4_in_rule__RelationType__Group__34493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__TypeGuidAssignment_4_in_rule__RelationType__Group__44521 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__5_in_rule__RelationType__Group__44531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_rule__RelationType__Group__54560 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__6_in_rule__RelationType__Group__54570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideANameAssignment_6_in_rule__RelationType__Group__64598 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__7_in_rule__RelationType__Group__64607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_rule__RelationType__Group__74636 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__8_in_rule__RelationType__Group__74646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideAArtifactTypeAssignment_8_in_rule__RelationType__Group__84674 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__9_in_rule__RelationType__Group__84683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_rule__RelationType__Group__94712 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__10_in_rule__RelationType__Group__94722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideBNameAssignment_10_in_rule__RelationType__Group__104750 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__11_in_rule__RelationType__Group__104759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_rule__RelationType__Group__114788 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__12_in_rule__RelationType__Group__114798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideBArtifactTypeAssignment_12_in_rule__RelationType__Group__124826 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__13_in_rule__RelationType__Group__124835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_rule__RelationType__Group__134864 = new BitSet(new long[]{0x000000001C000040L});
    public static final BitSet FOLLOW_rule__RelationType__Group__14_in_rule__RelationType__Group__134874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__DefaultOrderTypeAssignment_14_in_rule__RelationType__Group__144902 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__15_in_rule__RelationType__Group__144911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_rule__RelationType__Group__154940 = new BitSet(new long[]{0x00000001E0000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__16_in_rule__RelationType__Group__154950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__MultiplicityAssignment_16_in_rule__RelationType__Group__164978 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__17_in_rule__RelationType__Group__164987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__RelationType__Group__175016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule__RelationType__Group_2__05088 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group_2__1_in_rule__RelationType__Group_2__05098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__OverrideAssignment_2_1_in_rule__RelationType__Group_2__15126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_05164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeType_in_rule__OseeTypeModel__TypesAssignment_15195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_15226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_rule__ArtifactType__AbstractAssignment_05262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__NameAssignment_25301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_15336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_15375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__OverrideAssignment_4_15414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__ArtifactType__TypeGuidAssignment_65449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_rule__ArtifactType__ValidAttributeTypesAssignment_75480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeTypeRef__ValidAttributeTypeAssignment_15515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeTypeRef__BranchGuidAssignment_2_15550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__TypeGuidAssignment_05581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__NameAssignment_25612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_rule__AttributeType__BaseAttributeTypeAssignment_3_15643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__OverrideAssignment_4_15678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DataProviderAlternatives_7_0_in_rule__AttributeType__DataProviderAssignment_75713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MinAssignment_95746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MaxAlternatives_11_0_in_rule__AttributeType__MaxAssignment_115777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TaggerIdAlternatives_12_1_0_in_rule__AttributeType__TaggerIdAssignment_12_15810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__EnumTypeAssignment_13_15847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__DescriptionAssignment_14_15882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__DefaultValueAssignment_15_15913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__FileExtensionAssignment_16_15944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumType__NameAssignment_15975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumType__OverrideAssignment_2_16010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__OseeEnumType__TypeGuidAssignment_46045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_rule__OseeEnumType__EnumEntriesAssignment_56076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__OseeEnumEntry__NameAssignment_16107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__OseeEnumEntry__OrdinalAssignment_26138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__NameAssignment_16169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__OverrideAssignment_2_16204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__TypeGuidAssignment_46239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__SideANameAssignment_66270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideAArtifactTypeAssignment_86305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__SideBNameAssignment_106340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideBArtifactTypeAssignment_126375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__DefaultOrderTypeAlternatives_14_0_in_rule__RelationType__DefaultOrderTypeAssignment_146410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_rule__RelationType__MultiplicityAssignment_166443 = new BitSet(new long[]{0x0000000000000002L});

}