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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_WHOLE_NUM_STR", "RULE_ID", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'unlimited'", "'DefaultAttributeTaggerProvider'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'WordAttribute'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'", "'import'", "'.'", "'artifactType'", "'{'", "'guid'", "'}'", "'extends'", "','", "'attribute'", "'branchGuid'", "'attributeType'", "'dataProvider'", "'min'", "'max'", "'overrides'", "'taggerId'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'oseeEnumType'", "'entry'", "'entryGuid'", "'overrides enum'", "'add'", "'remove'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'", "'abstract'", "'inheritAll'"
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:61:1: ( ruleOseeTypeModel EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:62:1: ruleOseeTypeModel EOF
            {
             before(grammarAccess.getOseeTypeModelRule()); 
            pushFollow(FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel61);
            ruleOseeTypeModel();
            _fsp--;

             after(grammarAccess.getOseeTypeModelRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeTypeModel68); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:69:1: ruleOseeTypeModel : ( ( rule__OseeTypeModel__Group__0 ) ) ;
    public final void ruleOseeTypeModel() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:73:2: ( ( ( rule__OseeTypeModel__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:74:1: ( ( rule__OseeTypeModel__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:74:1: ( ( rule__OseeTypeModel__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:75:1: ( rule__OseeTypeModel__Group__0 )
            {
             before(grammarAccess.getOseeTypeModelAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:76:1: ( rule__OseeTypeModel__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:76:2: rule__OseeTypeModel__Group__0
            {
            pushFollow(FOLLOW_rule__OseeTypeModel__Group__0_in_ruleOseeTypeModel95);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:88:1: entryRuleImport : ruleImport EOF ;
    public final void entryRuleImport() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:89:1: ( ruleImport EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:90:1: ruleImport EOF
            {
             before(grammarAccess.getImportRule()); 
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport122);
            ruleImport();
            _fsp--;

             after(grammarAccess.getImportRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport129); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:97:1: ruleImport : ( ( rule__Import__Group__0 ) ) ;
    public final void ruleImport() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:101:2: ( ( ( rule__Import__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:102:1: ( ( rule__Import__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:102:1: ( ( rule__Import__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:103:1: ( rule__Import__Group__0 )
            {
             before(grammarAccess.getImportAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:104:1: ( rule__Import__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:104:2: rule__Import__Group__0
            {
            pushFollow(FOLLOW_rule__Import__Group__0_in_ruleImport156);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:116:1: entryRuleNAME_REFERENCE : ruleNAME_REFERENCE EOF ;
    public final void entryRuleNAME_REFERENCE() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:117:1: ( ruleNAME_REFERENCE EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:118:1: ruleNAME_REFERENCE EOF
            {
             before(grammarAccess.getNAME_REFERENCERule()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE183);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getNAME_REFERENCERule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNAME_REFERENCE190); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:125:1: ruleNAME_REFERENCE : ( RULE_STRING ) ;
    public final void ruleNAME_REFERENCE() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:129:2: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:130:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:130:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:131:1: RULE_STRING
            {
             before(grammarAccess.getNAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE217); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:144:1: entryRuleQUALIFIED_NAME : ruleQUALIFIED_NAME EOF ;
    public final void entryRuleQUALIFIED_NAME() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:145:1: ( ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:146:1: ruleQUALIFIED_NAME EOF
            {
             before(grammarAccess.getQUALIFIED_NAMERule()); 
            pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME243);
            ruleQUALIFIED_NAME();
            _fsp--;

             after(grammarAccess.getQUALIFIED_NAMERule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleQUALIFIED_NAME250); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:153:1: ruleQUALIFIED_NAME : ( ( rule__QUALIFIED_NAME__Group__0 ) ) ;
    public final void ruleQUALIFIED_NAME() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:157:2: ( ( ( rule__QUALIFIED_NAME__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:158:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:158:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:159:1: ( rule__QUALIFIED_NAME__Group__0 )
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:160:1: ( rule__QUALIFIED_NAME__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:160:2: rule__QUALIFIED_NAME__Group__0
            {
            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group__0_in_ruleQUALIFIED_NAME277);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:174:1: entryRuleOseeType : ruleOseeType EOF ;
    public final void entryRuleOseeType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:175:1: ( ruleOseeType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:176:1: ruleOseeType EOF
            {
             before(grammarAccess.getOseeTypeRule()); 
            pushFollow(FOLLOW_ruleOseeType_in_entryRuleOseeType306);
            ruleOseeType();
            _fsp--;

             after(grammarAccess.getOseeTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeType313); 

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
            pushFollow(FOLLOW_rule__OseeType__Alternatives_in_ruleOseeType340);
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
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:203:1: ( ruleArtifactType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:204:1: ruleArtifactType EOF
            {
             before(grammarAccess.getArtifactTypeRule()); 
            pushFollow(FOLLOW_ruleArtifactType_in_entryRuleArtifactType367);
            ruleArtifactType();
            _fsp--;

             after(grammarAccess.getArtifactTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactType374); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:211:1: ruleArtifactType : ( ( rule__ArtifactType__Group__0 ) ) ;
    public final void ruleArtifactType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:215:2: ( ( ( rule__ArtifactType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:216:1: ( ( rule__ArtifactType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:216:1: ( ( rule__ArtifactType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:217:1: ( rule__ArtifactType__Group__0 )
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:218:1: ( rule__ArtifactType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:218:2: rule__ArtifactType__Group__0
            {
            pushFollow(FOLLOW_rule__ArtifactType__Group__0_in_ruleArtifactType401);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:230:1: entryRuleAttributeTypeRef : ruleAttributeTypeRef EOF ;
    public final void entryRuleAttributeTypeRef() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:231:1: ( ruleAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:232:1: ruleAttributeTypeRef EOF
            {
             before(grammarAccess.getAttributeTypeRefRule()); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef428);
            ruleAttributeTypeRef();
            _fsp--;

             after(grammarAccess.getAttributeTypeRefRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRef435); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:239:1: ruleAttributeTypeRef : ( ( rule__AttributeTypeRef__Group__0 ) ) ;
    public final void ruleAttributeTypeRef() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:243:2: ( ( ( rule__AttributeTypeRef__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:244:1: ( ( rule__AttributeTypeRef__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:244:1: ( ( rule__AttributeTypeRef__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:245:1: ( rule__AttributeTypeRef__Group__0 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:246:1: ( rule__AttributeTypeRef__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:246:2: rule__AttributeTypeRef__Group__0
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__0_in_ruleAttributeTypeRef462);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:258:1: entryRuleAttributeType : ruleAttributeType EOF ;
    public final void entryRuleAttributeType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:259:1: ( ruleAttributeType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:260:1: ruleAttributeType EOF
            {
             before(grammarAccess.getAttributeTypeRule()); 
            pushFollow(FOLLOW_ruleAttributeType_in_entryRuleAttributeType489);
            ruleAttributeType();
            _fsp--;

             after(grammarAccess.getAttributeTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeType496); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:267:1: ruleAttributeType : ( ( rule__AttributeType__Group__0 ) ) ;
    public final void ruleAttributeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:271:2: ( ( ( rule__AttributeType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:272:1: ( ( rule__AttributeType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:272:1: ( ( rule__AttributeType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:273:1: ( rule__AttributeType__Group__0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:274:1: ( rule__AttributeType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:274:2: rule__AttributeType__Group__0
            {
            pushFollow(FOLLOW_rule__AttributeType__Group__0_in_ruleAttributeType523);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:286:1: entryRuleAttributeBaseType : ruleAttributeBaseType EOF ;
    public final void entryRuleAttributeBaseType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:287:1: ( ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:288:1: ruleAttributeBaseType EOF
            {
             before(grammarAccess.getAttributeBaseTypeRule()); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType550);
            ruleAttributeBaseType();
            _fsp--;

             after(grammarAccess.getAttributeBaseTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeBaseType557); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:295:1: ruleAttributeBaseType : ( ( rule__AttributeBaseType__Alternatives ) ) ;
    public final void ruleAttributeBaseType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:299:2: ( ( ( rule__AttributeBaseType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:300:1: ( ( rule__AttributeBaseType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:300:1: ( ( rule__AttributeBaseType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:301:1: ( rule__AttributeBaseType__Alternatives )
            {
             before(grammarAccess.getAttributeBaseTypeAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:302:1: ( rule__AttributeBaseType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:302:2: rule__AttributeBaseType__Alternatives
            {
            pushFollow(FOLLOW_rule__AttributeBaseType__Alternatives_in_ruleAttributeBaseType584);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:314:1: entryRuleOseeEnumType : ruleOseeEnumType EOF ;
    public final void entryRuleOseeEnumType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:315:1: ( ruleOseeEnumType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:316:1: ruleOseeEnumType EOF
            {
             before(grammarAccess.getOseeEnumTypeRule()); 
            pushFollow(FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType611);
            ruleOseeEnumType();
            _fsp--;

             after(grammarAccess.getOseeEnumTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumType618); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:323:1: ruleOseeEnumType : ( ( rule__OseeEnumType__Group__0 ) ) ;
    public final void ruleOseeEnumType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:327:2: ( ( ( rule__OseeEnumType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:328:1: ( ( rule__OseeEnumType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:328:1: ( ( rule__OseeEnumType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:329:1: ( rule__OseeEnumType__Group__0 )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:330:1: ( rule__OseeEnumType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:330:2: rule__OseeEnumType__Group__0
            {
            pushFollow(FOLLOW_rule__OseeEnumType__Group__0_in_ruleOseeEnumType645);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:342:1: entryRuleOseeEnumEntry : ruleOseeEnumEntry EOF ;
    public final void entryRuleOseeEnumEntry() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:343:1: ( ruleOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:344:1: ruleOseeEnumEntry EOF
            {
             before(grammarAccess.getOseeEnumEntryRule()); 
            pushFollow(FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry672);
            ruleOseeEnumEntry();
            _fsp--;

             after(grammarAccess.getOseeEnumEntryRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumEntry679); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:351:1: ruleOseeEnumEntry : ( ( rule__OseeEnumEntry__Group__0 ) ) ;
    public final void ruleOseeEnumEntry() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:355:2: ( ( ( rule__OseeEnumEntry__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:356:1: ( ( rule__OseeEnumEntry__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:356:1: ( ( rule__OseeEnumEntry__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:357:1: ( rule__OseeEnumEntry__Group__0 )
            {
             before(grammarAccess.getOseeEnumEntryAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:358:1: ( rule__OseeEnumEntry__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:358:2: rule__OseeEnumEntry__Group__0
            {
            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__0_in_ruleOseeEnumEntry706);
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


    // $ANTLR start entryRuleOseeEnumOverride
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:370:1: entryRuleOseeEnumOverride : ruleOseeEnumOverride EOF ;
    public final void entryRuleOseeEnumOverride() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:371:1: ( ruleOseeEnumOverride EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:372:1: ruleOseeEnumOverride EOF
            {
             before(grammarAccess.getOseeEnumOverrideRule()); 
            pushFollow(FOLLOW_ruleOseeEnumOverride_in_entryRuleOseeEnumOverride733);
            ruleOseeEnumOverride();
            _fsp--;

             after(grammarAccess.getOseeEnumOverrideRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOseeEnumOverride740); 

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
    // $ANTLR end entryRuleOseeEnumOverride


    // $ANTLR start ruleOseeEnumOverride
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:379:1: ruleOseeEnumOverride : ( ( rule__OseeEnumOverride__Group__0 ) ) ;
    public final void ruleOseeEnumOverride() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:383:2: ( ( ( rule__OseeEnumOverride__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:384:1: ( ( rule__OseeEnumOverride__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:384:1: ( ( rule__OseeEnumOverride__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:385:1: ( rule__OseeEnumOverride__Group__0 )
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:386:1: ( rule__OseeEnumOverride__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:386:2: rule__OseeEnumOverride__Group__0
            {
            pushFollow(FOLLOW_rule__OseeEnumOverride__Group__0_in_ruleOseeEnumOverride767);
            rule__OseeEnumOverride__Group__0();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumOverrideAccess().getGroup()); 

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
    // $ANTLR end ruleOseeEnumOverride


    // $ANTLR start entryRuleOverrideOption
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:398:1: entryRuleOverrideOption : ruleOverrideOption EOF ;
    public final void entryRuleOverrideOption() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:399:1: ( ruleOverrideOption EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:400:1: ruleOverrideOption EOF
            {
             before(grammarAccess.getOverrideOptionRule()); 
            pushFollow(FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption794);
            ruleOverrideOption();
            _fsp--;

             after(grammarAccess.getOverrideOptionRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleOverrideOption801); 

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
    // $ANTLR end entryRuleOverrideOption


    // $ANTLR start ruleOverrideOption
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:407:1: ruleOverrideOption : ( ( rule__OverrideOption__Alternatives ) ) ;
    public final void ruleOverrideOption() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:411:2: ( ( ( rule__OverrideOption__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:412:1: ( ( rule__OverrideOption__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:412:1: ( ( rule__OverrideOption__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:413:1: ( rule__OverrideOption__Alternatives )
            {
             before(grammarAccess.getOverrideOptionAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:414:1: ( rule__OverrideOption__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:414:2: rule__OverrideOption__Alternatives
            {
            pushFollow(FOLLOW_rule__OverrideOption__Alternatives_in_ruleOverrideOption828);
            rule__OverrideOption__Alternatives();
            _fsp--;


            }

             after(grammarAccess.getOverrideOptionAccess().getAlternatives()); 

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
    // $ANTLR end ruleOverrideOption


    // $ANTLR start entryRuleAddEnum
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:426:1: entryRuleAddEnum : ruleAddEnum EOF ;
    public final void entryRuleAddEnum() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:427:1: ( ruleAddEnum EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:428:1: ruleAddEnum EOF
            {
             before(grammarAccess.getAddEnumRule()); 
            pushFollow(FOLLOW_ruleAddEnum_in_entryRuleAddEnum855);
            ruleAddEnum();
            _fsp--;

             after(grammarAccess.getAddEnumRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddEnum862); 

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
    // $ANTLR end entryRuleAddEnum


    // $ANTLR start ruleAddEnum
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:435:1: ruleAddEnum : ( ( rule__AddEnum__Group__0 ) ) ;
    public final void ruleAddEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:439:2: ( ( ( rule__AddEnum__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:440:1: ( ( rule__AddEnum__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:440:1: ( ( rule__AddEnum__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:441:1: ( rule__AddEnum__Group__0 )
            {
             before(grammarAccess.getAddEnumAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:442:1: ( rule__AddEnum__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:442:2: rule__AddEnum__Group__0
            {
            pushFollow(FOLLOW_rule__AddEnum__Group__0_in_ruleAddEnum889);
            rule__AddEnum__Group__0();
            _fsp--;


            }

             after(grammarAccess.getAddEnumAccess().getGroup()); 

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
    // $ANTLR end ruleAddEnum


    // $ANTLR start entryRuleRemoveEnum
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:454:1: entryRuleRemoveEnum : ruleRemoveEnum EOF ;
    public final void entryRuleRemoveEnum() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:455:1: ( ruleRemoveEnum EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:456:1: ruleRemoveEnum EOF
            {
             before(grammarAccess.getRemoveEnumRule()); 
            pushFollow(FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum916);
            ruleRemoveEnum();
            _fsp--;

             after(grammarAccess.getRemoveEnumRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRemoveEnum923); 

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
    // $ANTLR end entryRuleRemoveEnum


    // $ANTLR start ruleRemoveEnum
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:463:1: ruleRemoveEnum : ( ( rule__RemoveEnum__Group__0 ) ) ;
    public final void ruleRemoveEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:467:2: ( ( ( rule__RemoveEnum__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:468:1: ( ( rule__RemoveEnum__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:468:1: ( ( rule__RemoveEnum__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:469:1: ( rule__RemoveEnum__Group__0 )
            {
             before(grammarAccess.getRemoveEnumAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:470:1: ( rule__RemoveEnum__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:470:2: rule__RemoveEnum__Group__0
            {
            pushFollow(FOLLOW_rule__RemoveEnum__Group__0_in_ruleRemoveEnum950);
            rule__RemoveEnum__Group__0();
            _fsp--;


            }

             after(grammarAccess.getRemoveEnumAccess().getGroup()); 

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
    // $ANTLR end ruleRemoveEnum


    // $ANTLR start entryRuleRelationType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:482:1: entryRuleRelationType : ruleRelationType EOF ;
    public final void entryRuleRelationType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:483:1: ( ruleRelationType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:484:1: ruleRelationType EOF
            {
             before(grammarAccess.getRelationTypeRule()); 
            pushFollow(FOLLOW_ruleRelationType_in_entryRuleRelationType977);
            ruleRelationType();
            _fsp--;

             after(grammarAccess.getRelationTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationType984); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:491:1: ruleRelationType : ( ( rule__RelationType__Group__0 ) ) ;
    public final void ruleRelationType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:495:2: ( ( ( rule__RelationType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:496:1: ( ( rule__RelationType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:496:1: ( ( rule__RelationType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:497:1: ( rule__RelationType__Group__0 )
            {
             before(grammarAccess.getRelationTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:498:1: ( rule__RelationType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:498:2: rule__RelationType__Group__0
            {
            pushFollow(FOLLOW_rule__RelationType__Group__0_in_ruleRelationType1011);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:510:1: entryRuleRelationOrderType : ruleRelationOrderType EOF ;
    public final void entryRuleRelationOrderType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:511:1: ( ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:512:1: ruleRelationOrderType EOF
            {
             before(grammarAccess.getRelationOrderTypeRule()); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType1038);
            ruleRelationOrderType();
            _fsp--;

             after(grammarAccess.getRelationOrderTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationOrderType1045); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:519:1: ruleRelationOrderType : ( ( rule__RelationOrderType__Alternatives ) ) ;
    public final void ruleRelationOrderType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:523:2: ( ( ( rule__RelationOrderType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:524:1: ( ( rule__RelationOrderType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:524:1: ( ( rule__RelationOrderType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:525:1: ( rule__RelationOrderType__Alternatives )
            {
             before(grammarAccess.getRelationOrderTypeAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:526:1: ( rule__RelationOrderType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:526:2: rule__RelationOrderType__Alternatives
            {
            pushFollow(FOLLOW_rule__RelationOrderType__Alternatives_in_ruleRelationOrderType1072);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:539:1: ruleRelationMultiplicityEnum : ( ( rule__RelationMultiplicityEnum__Alternatives ) ) ;
    public final void ruleRelationMultiplicityEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:543:1: ( ( ( rule__RelationMultiplicityEnum__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:544:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:544:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:545:1: ( rule__RelationMultiplicityEnum__Alternatives )
            {
             before(grammarAccess.getRelationMultiplicityEnumAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:546:1: ( rule__RelationMultiplicityEnum__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:546:2: rule__RelationMultiplicityEnum__Alternatives
            {
            pushFollow(FOLLOW_rule__RelationMultiplicityEnum__Alternatives_in_ruleRelationMultiplicityEnum1109);
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


    // $ANTLR start rule__OseeTypeModel__Alternatives_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:557:1: rule__OseeTypeModel__Alternatives_1 : ( ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) ) );
    public final void rule__OseeTypeModel__Alternatives_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:561:1: ( ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) ) )
            int alt1=5;
            switch ( input.LA(1) ) {
            case 34:
            case 65:
                {
                alt1=1;
                }
                break;
            case 58:
                {
                alt1=2;
                }
                break;
            case 42:
                {
                alt1=3;
                }
                break;
            case 52:
                {
                alt1=4;
                }
                break;
            case 55:
                {
                alt1=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("557:1: rule__OseeTypeModel__Alternatives_1 : ( ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) ) );", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:562:1: ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:562:1: ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:563:1: ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 )
                    {
                     before(grammarAccess.getOseeTypeModelAccess().getArtifactTypesAssignment_1_0()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:564:1: ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:564:2: rule__OseeTypeModel__ArtifactTypesAssignment_1_0
                    {
                    pushFollow(FOLLOW_rule__OseeTypeModel__ArtifactTypesAssignment_1_0_in_rule__OseeTypeModel__Alternatives_11144);
                    rule__OseeTypeModel__ArtifactTypesAssignment_1_0();
                    _fsp--;


                    }

                     after(grammarAccess.getOseeTypeModelAccess().getArtifactTypesAssignment_1_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:568:6: ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:568:6: ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:569:1: ( rule__OseeTypeModel__RelationTypesAssignment_1_1 )
                    {
                     before(grammarAccess.getOseeTypeModelAccess().getRelationTypesAssignment_1_1()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:570:1: ( rule__OseeTypeModel__RelationTypesAssignment_1_1 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:570:2: rule__OseeTypeModel__RelationTypesAssignment_1_1
                    {
                    pushFollow(FOLLOW_rule__OseeTypeModel__RelationTypesAssignment_1_1_in_rule__OseeTypeModel__Alternatives_11162);
                    rule__OseeTypeModel__RelationTypesAssignment_1_1();
                    _fsp--;


                    }

                     after(grammarAccess.getOseeTypeModelAccess().getRelationTypesAssignment_1_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:574:6: ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:574:6: ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:575:1: ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 )
                    {
                     before(grammarAccess.getOseeTypeModelAccess().getAttributeTypesAssignment_1_2()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:576:1: ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:576:2: rule__OseeTypeModel__AttributeTypesAssignment_1_2
                    {
                    pushFollow(FOLLOW_rule__OseeTypeModel__AttributeTypesAssignment_1_2_in_rule__OseeTypeModel__Alternatives_11180);
                    rule__OseeTypeModel__AttributeTypesAssignment_1_2();
                    _fsp--;


                    }

                     after(grammarAccess.getOseeTypeModelAccess().getAttributeTypesAssignment_1_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:580:6: ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:580:6: ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:581:1: ( rule__OseeTypeModel__EnumTypesAssignment_1_3 )
                    {
                     before(grammarAccess.getOseeTypeModelAccess().getEnumTypesAssignment_1_3()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:582:1: ( rule__OseeTypeModel__EnumTypesAssignment_1_3 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:582:2: rule__OseeTypeModel__EnumTypesAssignment_1_3
                    {
                    pushFollow(FOLLOW_rule__OseeTypeModel__EnumTypesAssignment_1_3_in_rule__OseeTypeModel__Alternatives_11198);
                    rule__OseeTypeModel__EnumTypesAssignment_1_3();
                    _fsp--;


                    }

                     after(grammarAccess.getOseeTypeModelAccess().getEnumTypesAssignment_1_3()); 

                    }


                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:586:6: ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:586:6: ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:587:1: ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 )
                    {
                     before(grammarAccess.getOseeTypeModelAccess().getEnumOverridesAssignment_1_4()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:588:1: ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:588:2: rule__OseeTypeModel__EnumOverridesAssignment_1_4
                    {
                    pushFollow(FOLLOW_rule__OseeTypeModel__EnumOverridesAssignment_1_4_in_rule__OseeTypeModel__Alternatives_11216);
                    rule__OseeTypeModel__EnumOverridesAssignment_1_4();
                    _fsp--;


                    }

                     after(grammarAccess.getOseeTypeModelAccess().getEnumOverridesAssignment_1_4()); 

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
    // $ANTLR end rule__OseeTypeModel__Alternatives_1


    // $ANTLR start rule__OseeType__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:598:1: rule__OseeType__Alternatives : ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) );
    public final void rule__OseeType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:602:1: ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) )
            int alt2=4;
            switch ( input.LA(1) ) {
            case 34:
            case 65:
                {
                alt2=1;
                }
                break;
            case 58:
                {
                alt2=2;
                }
                break;
            case 42:
                {
                alt2=3;
                }
                break;
            case 52:
                {
                alt2=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("598:1: rule__OseeType__Alternatives : ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) | ( ruleOseeEnumType ) );", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:603:1: ( ruleArtifactType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:603:1: ( ruleArtifactType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:604:1: ruleArtifactType
                    {
                     before(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0()); 
                    pushFollow(FOLLOW_ruleArtifactType_in_rule__OseeType__Alternatives1250);
                    ruleArtifactType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getArtifactTypeParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:609:6: ( ruleRelationType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:609:6: ( ruleRelationType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:610:1: ruleRelationType
                    {
                     before(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1()); 
                    pushFollow(FOLLOW_ruleRelationType_in_rule__OseeType__Alternatives1267);
                    ruleRelationType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getRelationTypeParserRuleCall_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:615:6: ( ruleAttributeType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:615:6: ( ruleAttributeType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:616:1: ruleAttributeType
                    {
                     before(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2()); 
                    pushFollow(FOLLOW_ruleAttributeType_in_rule__OseeType__Alternatives1284);
                    ruleAttributeType();
                    _fsp--;

                     after(grammarAccess.getOseeTypeAccess().getAttributeTypeParserRuleCall_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:621:6: ( ruleOseeEnumType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:621:6: ( ruleOseeEnumType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:622:1: ruleOseeEnumType
                    {
                     before(grammarAccess.getOseeTypeAccess().getOseeEnumTypeParserRuleCall_3()); 
                    pushFollow(FOLLOW_ruleOseeEnumType_in_rule__OseeType__Alternatives1301);
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


    // $ANTLR start rule__AttributeType__DataProviderAlternatives_8_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:632:1: rule__AttributeType__DataProviderAlternatives_8_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeType__DataProviderAlternatives_8_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:636:1: ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) )
            int alt3=3;
            switch ( input.LA(1) ) {
            case 12:
                {
                alt3=1;
                }
                break;
            case 13:
                {
                alt3=2;
                }
                break;
            case RULE_ID:
                {
                alt3=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("632:1: rule__AttributeType__DataProviderAlternatives_8_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:637:1: ( 'DefaultAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:637:1: ( 'DefaultAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:638:1: 'DefaultAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_8_0_0()); 
                    match(input,12,FOLLOW_12_in_rule__AttributeType__DataProviderAlternatives_8_01334); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_8_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:645:6: ( 'UriAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:645:6: ( 'UriAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:646:1: 'UriAttributeDataProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_8_0_1()); 
                    match(input,13,FOLLOW_13_in_rule__AttributeType__DataProviderAlternatives_8_01354); 
                     after(grammarAccess.getAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_8_0_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:653:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:653:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:654:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_8_0_2()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__DataProviderAlternatives_8_01373);
                    ruleQUALIFIED_NAME();
                    _fsp--;

                     after(grammarAccess.getAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_8_0_2()); 

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
    // $ANTLR end rule__AttributeType__DataProviderAlternatives_8_0


    // $ANTLR start rule__AttributeType__MaxAlternatives_12_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:664:1: rule__AttributeType__MaxAlternatives_12_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );
    public final void rule__AttributeType__MaxAlternatives_12_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:668:1: ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_WHOLE_NUM_STR) ) {
                alt4=1;
            }
            else if ( (LA4_0==14) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("664:1: rule__AttributeType__MaxAlternatives_12_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:669:1: ( RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:669:1: ( RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:670:1: RULE_WHOLE_NUM_STR
                    {
                     before(grammarAccess.getAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_12_0_0()); 
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MaxAlternatives_12_01405); 
                     after(grammarAccess.getAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_12_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:675:6: ( 'unlimited' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:675:6: ( 'unlimited' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:676:1: 'unlimited'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_12_0_1()); 
                    match(input,14,FOLLOW_14_in_rule__AttributeType__MaxAlternatives_12_01423); 
                     after(grammarAccess.getAttributeTypeAccess().getMaxUnlimitedKeyword_12_0_1()); 

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
    // $ANTLR end rule__AttributeType__MaxAlternatives_12_0


    // $ANTLR start rule__AttributeType__TaggerIdAlternatives_13_1_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:688:1: rule__AttributeType__TaggerIdAlternatives_13_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeType__TaggerIdAlternatives_13_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:692:1: ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==15) ) {
                alt5=1;
            }
            else if ( (LA5_0==RULE_ID) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("688:1: rule__AttributeType__TaggerIdAlternatives_13_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:693:1: ( 'DefaultAttributeTaggerProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:693:1: ( 'DefaultAttributeTaggerProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:694:1: 'DefaultAttributeTaggerProvider'
                    {
                     before(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_13_1_0_0()); 
                    match(input,15,FOLLOW_15_in_rule__AttributeType__TaggerIdAlternatives_13_1_01458); 
                     after(grammarAccess.getAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_13_1_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:701:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:701:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:702:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_13_1_0_1()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__TaggerIdAlternatives_13_1_01477);
                    ruleQUALIFIED_NAME();
                    _fsp--;

                     after(grammarAccess.getAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_13_1_0_1()); 

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
    // $ANTLR end rule__AttributeType__TaggerIdAlternatives_13_1_0


    // $ANTLR start rule__AttributeBaseType__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:712:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeBaseType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:716:1: ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) )
            int alt6=10;
            switch ( input.LA(1) ) {
            case 16:
                {
                alt6=1;
                }
                break;
            case 17:
                {
                alt6=2;
                }
                break;
            case 18:
                {
                alt6=3;
                }
                break;
            case 19:
                {
                alt6=4;
                }
                break;
            case 20:
                {
                alt6=5;
                }
                break;
            case 21:
                {
                alt6=6;
                }
                break;
            case 22:
                {
                alt6=7;
                }
                break;
            case 23:
                {
                alt6=8;
                }
                break;
            case 24:
                {
                alt6=9;
                }
                break;
            case RULE_ID:
                {
                alt6=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("712:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:717:1: ( 'BooleanAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:717:1: ( 'BooleanAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:718:1: 'BooleanAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                    match(input,16,FOLLOW_16_in_rule__AttributeBaseType__Alternatives1510); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:725:6: ( 'CompressedContentAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:725:6: ( 'CompressedContentAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:726:1: 'CompressedContentAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                    match(input,17,FOLLOW_17_in_rule__AttributeBaseType__Alternatives1530); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:733:6: ( 'DateAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:733:6: ( 'DateAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:734:1: 'DateAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                    match(input,18,FOLLOW_18_in_rule__AttributeBaseType__Alternatives1550); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:741:6: ( 'EnumeratedAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:741:6: ( 'EnumeratedAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:742:1: 'EnumeratedAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                    match(input,19,FOLLOW_19_in_rule__AttributeBaseType__Alternatives1570); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 

                    }


                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:749:6: ( 'FloatingPointAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:749:6: ( 'FloatingPointAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:750:1: 'FloatingPointAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                    match(input,20,FOLLOW_20_in_rule__AttributeBaseType__Alternatives1590); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 

                    }


                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:757:6: ( 'IntegerAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:757:6: ( 'IntegerAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:758:1: 'IntegerAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                    match(input,21,FOLLOW_21_in_rule__AttributeBaseType__Alternatives1610); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 

                    }


                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:765:6: ( 'JavaObjectAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:765:6: ( 'JavaObjectAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:766:1: 'JavaObjectAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 
                    match(input,22,FOLLOW_22_in_rule__AttributeBaseType__Alternatives1630); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 

                    }


                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:773:6: ( 'StringAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:773:6: ( 'StringAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:774:1: 'StringAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 
                    match(input,23,FOLLOW_23_in_rule__AttributeBaseType__Alternatives1650); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 

                    }


                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:781:6: ( 'WordAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:781:6: ( 'WordAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:782:1: 'WordAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 
                    match(input,24,FOLLOW_24_in_rule__AttributeBaseType__Alternatives1670); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 

                    }


                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:789:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:789:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:790:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9()); 
                    pushFollow(FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeBaseType__Alternatives1689);
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


    // $ANTLR start rule__OverrideOption__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:800:1: rule__OverrideOption__Alternatives : ( ( ruleAddEnum ) | ( ruleRemoveEnum ) );
    public final void rule__OverrideOption__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:804:1: ( ( ruleAddEnum ) | ( ruleRemoveEnum ) )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==56) ) {
                alt7=1;
            }
            else if ( (LA7_0==57) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("800:1: rule__OverrideOption__Alternatives : ( ( ruleAddEnum ) | ( ruleRemoveEnum ) );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:805:1: ( ruleAddEnum )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:805:1: ( ruleAddEnum )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:806:1: ruleAddEnum
                    {
                     before(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0()); 
                    pushFollow(FOLLOW_ruleAddEnum_in_rule__OverrideOption__Alternatives1721);
                    ruleAddEnum();
                    _fsp--;

                     after(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:811:6: ( ruleRemoveEnum )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:811:6: ( ruleRemoveEnum )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:812:1: ruleRemoveEnum
                    {
                     before(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1()); 
                    pushFollow(FOLLOW_ruleRemoveEnum_in_rule__OverrideOption__Alternatives1738);
                    ruleRemoveEnum();
                    _fsp--;

                     after(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1()); 

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
    // $ANTLR end rule__OverrideOption__Alternatives


    // $ANTLR start rule__RelationOrderType__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:822:1: rule__RelationOrderType__Alternatives : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );
    public final void rule__RelationOrderType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:826:1: ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) )
            int alt8=4;
            switch ( input.LA(1) ) {
            case 25:
                {
                alt8=1;
                }
                break;
            case 26:
                {
                alt8=2;
                }
                break;
            case 27:
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
                    new NoViableAltException("822:1: rule__RelationOrderType__Alternatives : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:827:1: ( 'Lexicographical_Ascending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:827:1: ( 'Lexicographical_Ascending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:828:1: 'Lexicographical_Ascending'
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 
                    match(input,25,FOLLOW_25_in_rule__RelationOrderType__Alternatives1771); 
                     after(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:835:6: ( 'Lexicographical_Descending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:835:6: ( 'Lexicographical_Descending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:836:1: 'Lexicographical_Descending'
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 
                    match(input,26,FOLLOW_26_in_rule__RelationOrderType__Alternatives1791); 
                     after(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:843:6: ( 'Unordered' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:843:6: ( 'Unordered' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:844:1: 'Unordered'
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 
                    match(input,27,FOLLOW_27_in_rule__RelationOrderType__Alternatives1811); 
                     after(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:851:6: ( RULE_ID )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:851:6: ( RULE_ID )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:852:1: RULE_ID
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getIDTerminalRuleCall_3()); 
                    match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__RelationOrderType__Alternatives1830); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:862:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );
    public final void rule__RelationMultiplicityEnum__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:866:1: ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) )
            int alt9=4;
            switch ( input.LA(1) ) {
            case 28:
                {
                alt9=1;
                }
                break;
            case 29:
                {
                alt9=2;
                }
                break;
            case 30:
                {
                alt9=3;
                }
                break;
            case 31:
                {
                alt9=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("862:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:867:1: ( ( 'ONE_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:867:1: ( ( 'ONE_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:868:1: ( 'ONE_TO_ONE' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:869:1: ( 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:869:3: 'ONE_TO_ONE'
                    {
                    match(input,28,FOLLOW_28_in_rule__RelationMultiplicityEnum__Alternatives1863); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:874:6: ( ( 'ONE_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:874:6: ( ( 'ONE_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:875:1: ( 'ONE_TO_MANY' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:876:1: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:876:3: 'ONE_TO_MANY'
                    {
                    match(input,29,FOLLOW_29_in_rule__RelationMultiplicityEnum__Alternatives1884); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:881:6: ( ( 'MANY_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:881:6: ( ( 'MANY_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:882:1: ( 'MANY_TO_ONE' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:883:1: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:883:3: 'MANY_TO_ONE'
                    {
                    match(input,30,FOLLOW_30_in_rule__RelationMultiplicityEnum__Alternatives1905); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:888:6: ( ( 'MANY_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:888:6: ( ( 'MANY_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:889:1: ( 'MANY_TO_MANY' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3()); 
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:890:1: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:890:3: 'MANY_TO_MANY'
                    {
                    match(input,31,FOLLOW_31_in_rule__RelationMultiplicityEnum__Alternatives1926); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:902:1: rule__OseeTypeModel__Group__0 : ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1 ;
    public final void rule__OseeTypeModel__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:906:1: ( ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:907:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:907:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:908:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            {
             before(grammarAccess.getOseeTypeModelAccess().getImportsAssignment_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:909:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==32) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:909:2: rule__OseeTypeModel__ImportsAssignment_0
            	    {
            	    pushFollow(FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__01963);
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

            pushFollow(FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01973);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:920:1: rule__OseeTypeModel__Group__1 : ( ( rule__OseeTypeModel__Alternatives_1 )* ) ;
    public final void rule__OseeTypeModel__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:924:1: ( ( ( rule__OseeTypeModel__Alternatives_1 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:925:1: ( ( rule__OseeTypeModel__Alternatives_1 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:925:1: ( ( rule__OseeTypeModel__Alternatives_1 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:926:1: ( rule__OseeTypeModel__Alternatives_1 )*
            {
             before(grammarAccess.getOseeTypeModelAccess().getAlternatives_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:927:1: ( rule__OseeTypeModel__Alternatives_1 )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==34||LA11_0==42||LA11_0==52||LA11_0==55||LA11_0==58||LA11_0==65) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:927:2: rule__OseeTypeModel__Alternatives_1
            	    {
            	    pushFollow(FOLLOW_rule__OseeTypeModel__Alternatives_1_in_rule__OseeTypeModel__Group__12001);
            	    rule__OseeTypeModel__Alternatives_1();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

             after(grammarAccess.getOseeTypeModelAccess().getAlternatives_1()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:941:1: rule__Import__Group__0 : ( 'import' ) rule__Import__Group__1 ;
    public final void rule__Import__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:945:1: ( ( 'import' ) rule__Import__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:946:1: ( 'import' ) rule__Import__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:946:1: ( 'import' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:947:1: 'import'
            {
             before(grammarAccess.getImportAccess().getImportKeyword_0()); 
            match(input,32,FOLLOW_32_in_rule__Import__Group__02041); 
             after(grammarAccess.getImportAccess().getImportKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__Import__Group__1_in_rule__Import__Group__02051);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:961:1: rule__Import__Group__1 : ( ( rule__Import__ImportURIAssignment_1 ) ) ;
    public final void rule__Import__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:965:1: ( ( ( rule__Import__ImportURIAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:966:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:966:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:967:1: ( rule__Import__ImportURIAssignment_1 )
            {
             before(grammarAccess.getImportAccess().getImportURIAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:968:1: ( rule__Import__ImportURIAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:968:2: rule__Import__ImportURIAssignment_1
            {
            pushFollow(FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__12079);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:982:1: rule__QUALIFIED_NAME__Group__0 : ( RULE_ID ) rule__QUALIFIED_NAME__Group__1 ;
    public final void rule__QUALIFIED_NAME__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:986:1: ( ( RULE_ID ) rule__QUALIFIED_NAME__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:987:1: ( RULE_ID ) rule__QUALIFIED_NAME__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:987:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:988:1: RULE_ID
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__02117); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 

            }

            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__02125);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1000:1: rule__QUALIFIED_NAME__Group__1 : ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) ;
    public final void rule__QUALIFIED_NAME__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1004:1: ( ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1005:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1005:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1006:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1007:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==33) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1007:2: rule__QUALIFIED_NAME__Group_1__0
            	    {
            	    pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__12153);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1021:1: rule__QUALIFIED_NAME__Group_1__0 : ( '.' ) rule__QUALIFIED_NAME__Group_1__1 ;
    public final void rule__QUALIFIED_NAME__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1025:1: ( ( '.' ) rule__QUALIFIED_NAME__Group_1__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1026:1: ( '.' ) rule__QUALIFIED_NAME__Group_1__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1026:1: ( '.' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1027:1: '.'
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            match(input,33,FOLLOW_33_in_rule__QUALIFIED_NAME__Group_1__02193); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 

            }

            pushFollow(FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__02203);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1041:1: rule__QUALIFIED_NAME__Group_1__1 : ( RULE_ID ) ;
    public final void rule__QUALIFIED_NAME__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1045:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1046:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1046:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1047:1: RULE_ID
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__12231); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1062:1: rule__ArtifactType__Group__0 : ( ( rule__ArtifactType__AbstractAssignment_0 )? ) rule__ArtifactType__Group__1 ;
    public final void rule__ArtifactType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1066:1: ( ( ( rule__ArtifactType__AbstractAssignment_0 )? ) rule__ArtifactType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1067:1: ( ( rule__ArtifactType__AbstractAssignment_0 )? ) rule__ArtifactType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1067:1: ( ( rule__ArtifactType__AbstractAssignment_0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1068:1: ( rule__ArtifactType__AbstractAssignment_0 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getAbstractAssignment_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1069:1: ( rule__ArtifactType__AbstractAssignment_0 )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==65) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1069:2: rule__ArtifactType__AbstractAssignment_0
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__AbstractAssignment_0_in_rule__ArtifactType__Group__02268);
                    rule__ArtifactType__AbstractAssignment_0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getAbstractAssignment_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__1_in_rule__ArtifactType__Group__02278);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1080:1: rule__ArtifactType__Group__1 : ( 'artifactType' ) rule__ArtifactType__Group__2 ;
    public final void rule__ArtifactType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1084:1: ( ( 'artifactType' ) rule__ArtifactType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1085:1: ( 'artifactType' ) rule__ArtifactType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1085:1: ( 'artifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1086:1: 'artifactType'
            {
             before(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1()); 
            match(input,34,FOLLOW_34_in_rule__ArtifactType__Group__12307); 
             after(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_1()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__2_in_rule__ArtifactType__Group__12317);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1100:1: rule__ArtifactType__Group__2 : ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3 ;
    public final void rule__ArtifactType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1104:1: ( ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1105:1: ( ( rule__ArtifactType__NameAssignment_2 ) ) rule__ArtifactType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1105:1: ( ( rule__ArtifactType__NameAssignment_2 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1106:1: ( rule__ArtifactType__NameAssignment_2 )
            {
             before(grammarAccess.getArtifactTypeAccess().getNameAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1107:1: ( rule__ArtifactType__NameAssignment_2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1107:2: rule__ArtifactType__NameAssignment_2
            {
            pushFollow(FOLLOW_rule__ArtifactType__NameAssignment_2_in_rule__ArtifactType__Group__22345);
            rule__ArtifactType__NameAssignment_2();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getNameAssignment_2()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__3_in_rule__ArtifactType__Group__22354);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1118:1: rule__ArtifactType__Group__3 : ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4 ;
    public final void rule__ArtifactType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1122:1: ( ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1123:1: ( ( rule__ArtifactType__Group_3__0 )? ) rule__ArtifactType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1123:1: ( ( rule__ArtifactType__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1124:1: ( rule__ArtifactType__Group_3__0 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1125:1: ( rule__ArtifactType__Group_3__0 )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==38) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1125:2: rule__ArtifactType__Group_3__0
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__Group_3__0_in_rule__ArtifactType__Group__32382);
                    rule__ArtifactType__Group_3__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getGroup_3()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__4_in_rule__ArtifactType__Group__32392);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1136:1: rule__ArtifactType__Group__4 : ( '{' ) rule__ArtifactType__Group__5 ;
    public final void rule__ArtifactType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1140:1: ( ( '{' ) rule__ArtifactType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1141:1: ( '{' ) rule__ArtifactType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1141:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1142:1: '{'
            {
             before(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_4()); 
            match(input,35,FOLLOW_35_in_rule__ArtifactType__Group__42421); 
             after(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_4()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__5_in_rule__ArtifactType__Group__42431);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1156:1: rule__ArtifactType__Group__5 : ( 'guid' ) rule__ArtifactType__Group__6 ;
    public final void rule__ArtifactType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1160:1: ( ( 'guid' ) rule__ArtifactType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1161:1: ( 'guid' ) rule__ArtifactType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1161:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1162:1: 'guid'
            {
             before(grammarAccess.getArtifactTypeAccess().getGuidKeyword_5()); 
            match(input,36,FOLLOW_36_in_rule__ArtifactType__Group__52460); 
             after(grammarAccess.getArtifactTypeAccess().getGuidKeyword_5()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__6_in_rule__ArtifactType__Group__52470);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1176:1: rule__ArtifactType__Group__6 : ( ( rule__ArtifactType__TypeGuidAssignment_6 ) ) rule__ArtifactType__Group__7 ;
    public final void rule__ArtifactType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1180:1: ( ( ( rule__ArtifactType__TypeGuidAssignment_6 ) ) rule__ArtifactType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1181:1: ( ( rule__ArtifactType__TypeGuidAssignment_6 ) ) rule__ArtifactType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1181:1: ( ( rule__ArtifactType__TypeGuidAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1182:1: ( rule__ArtifactType__TypeGuidAssignment_6 )
            {
             before(grammarAccess.getArtifactTypeAccess().getTypeGuidAssignment_6()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1183:1: ( rule__ArtifactType__TypeGuidAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1183:2: rule__ArtifactType__TypeGuidAssignment_6
            {
            pushFollow(FOLLOW_rule__ArtifactType__TypeGuidAssignment_6_in_rule__ArtifactType__Group__62498);
            rule__ArtifactType__TypeGuidAssignment_6();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getTypeGuidAssignment_6()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__7_in_rule__ArtifactType__Group__62507);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1194:1: rule__ArtifactType__Group__7 : ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__ArtifactType__Group__8 ;
    public final void rule__ArtifactType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1198:1: ( ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__ArtifactType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1199:1: ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__ArtifactType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1199:1: ( ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1200:1: ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )*
            {
             before(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAssignment_7()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1201:1: ( rule__ArtifactType__ValidAttributeTypesAssignment_7 )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==40) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1201:2: rule__ArtifactType__ValidAttributeTypesAssignment_7
            	    {
            	    pushFollow(FOLLOW_rule__ArtifactType__ValidAttributeTypesAssignment_7_in_rule__ArtifactType__Group__72535);
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

            pushFollow(FOLLOW_rule__ArtifactType__Group__8_in_rule__ArtifactType__Group__72545);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1212:1: rule__ArtifactType__Group__8 : ( '}' ) ;
    public final void rule__ArtifactType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1216:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1217:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1217:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1218:1: '}'
            {
             before(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_8()); 
            match(input,37,FOLLOW_37_in_rule__ArtifactType__Group__82574); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1249:1: rule__ArtifactType__Group_3__0 : ( 'extends' ) rule__ArtifactType__Group_3__1 ;
    public final void rule__ArtifactType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1253:1: ( ( 'extends' ) rule__ArtifactType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1254:1: ( 'extends' ) rule__ArtifactType__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1254:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1255:1: 'extends'
            {
             before(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0()); 
            match(input,38,FOLLOW_38_in_rule__ArtifactType__Group_3__02628); 
             after(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_3_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_3__1_in_rule__ArtifactType__Group_3__02638);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1269:1: rule__ArtifactType__Group_3__1 : ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__ArtifactType__Group_3__2 ;
    public final void rule__ArtifactType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1273:1: ( ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__ArtifactType__Group_3__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1274:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__ArtifactType__Group_3__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1274:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1275:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1276:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1276:2: rule__ArtifactType__SuperArtifactTypesAssignment_3_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_1_in_rule__ArtifactType__Group_3__12666);
            rule__ArtifactType__SuperArtifactTypesAssignment_3_1();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_3__2_in_rule__ArtifactType__Group_3__12675);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1287:1: rule__ArtifactType__Group_3__2 : ( ( rule__ArtifactType__Group_3_2__0 )* ) ;
    public final void rule__ArtifactType__Group_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1291:1: ( ( ( rule__ArtifactType__Group_3_2__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1292:1: ( ( rule__ArtifactType__Group_3_2__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1292:1: ( ( rule__ArtifactType__Group_3_2__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1293:1: ( rule__ArtifactType__Group_3_2__0 )*
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup_3_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1294:1: ( rule__ArtifactType__Group_3_2__0 )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==39) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1294:2: rule__ArtifactType__Group_3_2__0
            	    {
            	    pushFollow(FOLLOW_rule__ArtifactType__Group_3_2__0_in_rule__ArtifactType__Group_3__22703);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1310:1: rule__ArtifactType__Group_3_2__0 : ( ',' ) rule__ArtifactType__Group_3_2__1 ;
    public final void rule__ArtifactType__Group_3_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1314:1: ( ( ',' ) rule__ArtifactType__Group_3_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1315:1: ( ',' ) rule__ArtifactType__Group_3_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1315:1: ( ',' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1316:1: ','
            {
             before(grammarAccess.getArtifactTypeAccess().getCommaKeyword_3_2_0()); 
            match(input,39,FOLLOW_39_in_rule__ArtifactType__Group_3_2__02745); 
             after(grammarAccess.getArtifactTypeAccess().getCommaKeyword_3_2_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_3_2__1_in_rule__ArtifactType__Group_3_2__02755);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1330:1: rule__ArtifactType__Group_3_2__1 : ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) ;
    public final void rule__ArtifactType__Group_3_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1334:1: ( ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1335:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1335:1: ( ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1336:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesAssignment_3_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1337:1: ( rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1337:2: rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1_in_rule__ArtifactType__Group_3_2__12783);
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


    // $ANTLR start rule__AttributeTypeRef__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1351:1: rule__AttributeTypeRef__Group__0 : ( 'attribute' ) rule__AttributeTypeRef__Group__1 ;
    public final void rule__AttributeTypeRef__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1355:1: ( ( 'attribute' ) rule__AttributeTypeRef__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1356:1: ( 'attribute' ) rule__AttributeTypeRef__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1356:1: ( 'attribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1357:1: 'attribute'
            {
             before(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0()); 
            match(input,40,FOLLOW_40_in_rule__AttributeTypeRef__Group__02822); 
             after(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__1_in_rule__AttributeTypeRef__Group__02832);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1371:1: rule__AttributeTypeRef__Group__1 : ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__AttributeTypeRef__Group__2 ;
    public final void rule__AttributeTypeRef__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1375:1: ( ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__AttributeTypeRef__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1376:1: ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__AttributeTypeRef__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1376:1: ( ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1377:1: ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1378:1: ( rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1378:2: rule__AttributeTypeRef__ValidAttributeTypeAssignment_1
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__AttributeTypeRef__Group__12860);
            rule__AttributeTypeRef__ValidAttributeTypeAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__2_in_rule__AttributeTypeRef__Group__12869);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1389:1: rule__AttributeTypeRef__Group__2 : ( ( rule__AttributeTypeRef__Group_2__0 )? ) ;
    public final void rule__AttributeTypeRef__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1393:1: ( ( ( rule__AttributeTypeRef__Group_2__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1394:1: ( ( rule__AttributeTypeRef__Group_2__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1394:1: ( ( rule__AttributeTypeRef__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1395:1: ( rule__AttributeTypeRef__Group_2__0 )?
            {
             before(grammarAccess.getAttributeTypeRefAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1396:1: ( rule__AttributeTypeRef__Group_2__0 )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==41) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1396:2: rule__AttributeTypeRef__Group_2__0
                    {
                    pushFollow(FOLLOW_rule__AttributeTypeRef__Group_2__0_in_rule__AttributeTypeRef__Group__22897);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1412:1: rule__AttributeTypeRef__Group_2__0 : ( 'branchGuid' ) rule__AttributeTypeRef__Group_2__1 ;
    public final void rule__AttributeTypeRef__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1416:1: ( ( 'branchGuid' ) rule__AttributeTypeRef__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1417:1: ( 'branchGuid' ) rule__AttributeTypeRef__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1417:1: ( 'branchGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1418:1: 'branchGuid'
            {
             before(grammarAccess.getAttributeTypeRefAccess().getBranchGuidKeyword_2_0()); 
            match(input,41,FOLLOW_41_in_rule__AttributeTypeRef__Group_2__02939); 
             after(grammarAccess.getAttributeTypeRefAccess().getBranchGuidKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group_2__1_in_rule__AttributeTypeRef__Group_2__02949);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1432:1: rule__AttributeTypeRef__Group_2__1 : ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) ) ;
    public final void rule__AttributeTypeRef__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1436:1: ( ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1437:1: ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1437:1: ( ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1438:1: ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getBranchGuidAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1439:1: ( rule__AttributeTypeRef__BranchGuidAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1439:2: rule__AttributeTypeRef__BranchGuidAssignment_2_1
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__BranchGuidAssignment_2_1_in_rule__AttributeTypeRef__Group_2__12977);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1453:1: rule__AttributeType__Group__0 : ( 'attributeType' ) rule__AttributeType__Group__1 ;
    public final void rule__AttributeType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1457:1: ( ( 'attributeType' ) rule__AttributeType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1458:1: ( 'attributeType' ) rule__AttributeType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1458:1: ( 'attributeType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1459:1: 'attributeType'
            {
             before(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_0()); 
            match(input,42,FOLLOW_42_in_rule__AttributeType__Group__03016); 
             after(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__1_in_rule__AttributeType__Group__03026);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1473:1: rule__AttributeType__Group__1 : ( ( rule__AttributeType__NameAssignment_1 ) ) rule__AttributeType__Group__2 ;
    public final void rule__AttributeType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1477:1: ( ( ( rule__AttributeType__NameAssignment_1 ) ) rule__AttributeType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1478:1: ( ( rule__AttributeType__NameAssignment_1 ) ) rule__AttributeType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1478:1: ( ( rule__AttributeType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1479:1: ( rule__AttributeType__NameAssignment_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1480:1: ( rule__AttributeType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1480:2: rule__AttributeType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__AttributeType__NameAssignment_1_in_rule__AttributeType__Group__13054);
            rule__AttributeType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__2_in_rule__AttributeType__Group__13063);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1491:1: rule__AttributeType__Group__2 : ( ( rule__AttributeType__Group_2__0 ) ) rule__AttributeType__Group__3 ;
    public final void rule__AttributeType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1495:1: ( ( ( rule__AttributeType__Group_2__0 ) ) rule__AttributeType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1496:1: ( ( rule__AttributeType__Group_2__0 ) ) rule__AttributeType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1496:1: ( ( rule__AttributeType__Group_2__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1497:1: ( rule__AttributeType__Group_2__0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1498:1: ( rule__AttributeType__Group_2__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1498:2: rule__AttributeType__Group_2__0
            {
            pushFollow(FOLLOW_rule__AttributeType__Group_2__0_in_rule__AttributeType__Group__23091);
            rule__AttributeType__Group_2__0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_2()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__3_in_rule__AttributeType__Group__23100);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1509:1: rule__AttributeType__Group__3 : ( ( rule__AttributeType__Group_3__0 )? ) rule__AttributeType__Group__4 ;
    public final void rule__AttributeType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1513:1: ( ( ( rule__AttributeType__Group_3__0 )? ) rule__AttributeType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1514:1: ( ( rule__AttributeType__Group_3__0 )? ) rule__AttributeType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1514:1: ( ( rule__AttributeType__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1515:1: ( rule__AttributeType__Group_3__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1516:1: ( rule__AttributeType__Group_3__0 )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==46) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1516:2: rule__AttributeType__Group_3__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_3__0_in_rule__AttributeType__Group__33128);
                    rule__AttributeType__Group_3__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_3()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__4_in_rule__AttributeType__Group__33138);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1527:1: rule__AttributeType__Group__4 : ( '{' ) rule__AttributeType__Group__5 ;
    public final void rule__AttributeType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1531:1: ( ( '{' ) rule__AttributeType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1532:1: ( '{' ) rule__AttributeType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1532:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1533:1: '{'
            {
             before(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_4()); 
            match(input,35,FOLLOW_35_in_rule__AttributeType__Group__43167); 
             after(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_4()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__5_in_rule__AttributeType__Group__43177);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1547:1: rule__AttributeType__Group__5 : ( 'guid' ) rule__AttributeType__Group__6 ;
    public final void rule__AttributeType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1551:1: ( ( 'guid' ) rule__AttributeType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1552:1: ( 'guid' ) rule__AttributeType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1552:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1553:1: 'guid'
            {
             before(grammarAccess.getAttributeTypeAccess().getGuidKeyword_5()); 
            match(input,36,FOLLOW_36_in_rule__AttributeType__Group__53206); 
             after(grammarAccess.getAttributeTypeAccess().getGuidKeyword_5()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__6_in_rule__AttributeType__Group__53216);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1567:1: rule__AttributeType__Group__6 : ( ( rule__AttributeType__TypeGuidAssignment_6 ) ) rule__AttributeType__Group__7 ;
    public final void rule__AttributeType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1571:1: ( ( ( rule__AttributeType__TypeGuidAssignment_6 ) ) rule__AttributeType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1572:1: ( ( rule__AttributeType__TypeGuidAssignment_6 ) ) rule__AttributeType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1572:1: ( ( rule__AttributeType__TypeGuidAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1573:1: ( rule__AttributeType__TypeGuidAssignment_6 )
            {
             before(grammarAccess.getAttributeTypeAccess().getTypeGuidAssignment_6()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1574:1: ( rule__AttributeType__TypeGuidAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1574:2: rule__AttributeType__TypeGuidAssignment_6
            {
            pushFollow(FOLLOW_rule__AttributeType__TypeGuidAssignment_6_in_rule__AttributeType__Group__63244);
            rule__AttributeType__TypeGuidAssignment_6();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getTypeGuidAssignment_6()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__7_in_rule__AttributeType__Group__63253);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1585:1: rule__AttributeType__Group__7 : ( 'dataProvider' ) rule__AttributeType__Group__8 ;
    public final void rule__AttributeType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1589:1: ( ( 'dataProvider' ) rule__AttributeType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1590:1: ( 'dataProvider' ) rule__AttributeType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1590:1: ( 'dataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1591:1: 'dataProvider'
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_7()); 
            match(input,43,FOLLOW_43_in_rule__AttributeType__Group__73282); 
             after(grammarAccess.getAttributeTypeAccess().getDataProviderKeyword_7()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__8_in_rule__AttributeType__Group__73292);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1605:1: rule__AttributeType__Group__8 : ( ( rule__AttributeType__DataProviderAssignment_8 ) ) rule__AttributeType__Group__9 ;
    public final void rule__AttributeType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1609:1: ( ( ( rule__AttributeType__DataProviderAssignment_8 ) ) rule__AttributeType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1610:1: ( ( rule__AttributeType__DataProviderAssignment_8 ) ) rule__AttributeType__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1610:1: ( ( rule__AttributeType__DataProviderAssignment_8 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1611:1: ( rule__AttributeType__DataProviderAssignment_8 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderAssignment_8()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1612:1: ( rule__AttributeType__DataProviderAssignment_8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1612:2: rule__AttributeType__DataProviderAssignment_8
            {
            pushFollow(FOLLOW_rule__AttributeType__DataProviderAssignment_8_in_rule__AttributeType__Group__83320);
            rule__AttributeType__DataProviderAssignment_8();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDataProviderAssignment_8()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__9_in_rule__AttributeType__Group__83329);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1623:1: rule__AttributeType__Group__9 : ( 'min' ) rule__AttributeType__Group__10 ;
    public final void rule__AttributeType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1627:1: ( ( 'min' ) rule__AttributeType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1628:1: ( 'min' ) rule__AttributeType__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1628:1: ( 'min' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1629:1: 'min'
            {
             before(grammarAccess.getAttributeTypeAccess().getMinKeyword_9()); 
            match(input,44,FOLLOW_44_in_rule__AttributeType__Group__93358); 
             after(grammarAccess.getAttributeTypeAccess().getMinKeyword_9()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__10_in_rule__AttributeType__Group__93368);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1643:1: rule__AttributeType__Group__10 : ( ( rule__AttributeType__MinAssignment_10 ) ) rule__AttributeType__Group__11 ;
    public final void rule__AttributeType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1647:1: ( ( ( rule__AttributeType__MinAssignment_10 ) ) rule__AttributeType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1648:1: ( ( rule__AttributeType__MinAssignment_10 ) ) rule__AttributeType__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1648:1: ( ( rule__AttributeType__MinAssignment_10 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1649:1: ( rule__AttributeType__MinAssignment_10 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMinAssignment_10()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1650:1: ( rule__AttributeType__MinAssignment_10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1650:2: rule__AttributeType__MinAssignment_10
            {
            pushFollow(FOLLOW_rule__AttributeType__MinAssignment_10_in_rule__AttributeType__Group__103396);
            rule__AttributeType__MinAssignment_10();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMinAssignment_10()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__11_in_rule__AttributeType__Group__103405);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1661:1: rule__AttributeType__Group__11 : ( 'max' ) rule__AttributeType__Group__12 ;
    public final void rule__AttributeType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1665:1: ( ( 'max' ) rule__AttributeType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1666:1: ( 'max' ) rule__AttributeType__Group__12
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1666:1: ( 'max' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1667:1: 'max'
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxKeyword_11()); 
            match(input,45,FOLLOW_45_in_rule__AttributeType__Group__113434); 
             after(grammarAccess.getAttributeTypeAccess().getMaxKeyword_11()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__12_in_rule__AttributeType__Group__113444);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1681:1: rule__AttributeType__Group__12 : ( ( rule__AttributeType__MaxAssignment_12 ) ) rule__AttributeType__Group__13 ;
    public final void rule__AttributeType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1685:1: ( ( ( rule__AttributeType__MaxAssignment_12 ) ) rule__AttributeType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1686:1: ( ( rule__AttributeType__MaxAssignment_12 ) ) rule__AttributeType__Group__13
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1686:1: ( ( rule__AttributeType__MaxAssignment_12 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1687:1: ( rule__AttributeType__MaxAssignment_12 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxAssignment_12()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1688:1: ( rule__AttributeType__MaxAssignment_12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1688:2: rule__AttributeType__MaxAssignment_12
            {
            pushFollow(FOLLOW_rule__AttributeType__MaxAssignment_12_in_rule__AttributeType__Group__123472);
            rule__AttributeType__MaxAssignment_12();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMaxAssignment_12()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__13_in_rule__AttributeType__Group__123481);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1699:1: rule__AttributeType__Group__13 : ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14 ;
    public final void rule__AttributeType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1703:1: ( ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1704:1: ( ( rule__AttributeType__Group_13__0 )? ) rule__AttributeType__Group__14
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1704:1: ( ( rule__AttributeType__Group_13__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1705:1: ( rule__AttributeType__Group_13__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_13()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1706:1: ( rule__AttributeType__Group_13__0 )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==47) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1706:2: rule__AttributeType__Group_13__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_13__0_in_rule__AttributeType__Group__133509);
                    rule__AttributeType__Group_13__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_13()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__14_in_rule__AttributeType__Group__133519);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1717:1: rule__AttributeType__Group__14 : ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15 ;
    public final void rule__AttributeType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1721:1: ( ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1722:1: ( ( rule__AttributeType__Group_14__0 )? ) rule__AttributeType__Group__15
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1722:1: ( ( rule__AttributeType__Group_14__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1723:1: ( rule__AttributeType__Group_14__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_14()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1724:1: ( rule__AttributeType__Group_14__0 )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==48) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1724:2: rule__AttributeType__Group_14__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_14__0_in_rule__AttributeType__Group__143547);
                    rule__AttributeType__Group_14__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_14()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__15_in_rule__AttributeType__Group__143557);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1735:1: rule__AttributeType__Group__15 : ( ( rule__AttributeType__Group_15__0 )? ) rule__AttributeType__Group__16 ;
    public final void rule__AttributeType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1739:1: ( ( ( rule__AttributeType__Group_15__0 )? ) rule__AttributeType__Group__16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1740:1: ( ( rule__AttributeType__Group_15__0 )? ) rule__AttributeType__Group__16
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1740:1: ( ( rule__AttributeType__Group_15__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1741:1: ( rule__AttributeType__Group_15__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_15()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1742:1: ( rule__AttributeType__Group_15__0 )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==49) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1742:2: rule__AttributeType__Group_15__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_15__0_in_rule__AttributeType__Group__153585);
                    rule__AttributeType__Group_15__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_15()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__16_in_rule__AttributeType__Group__153595);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1753:1: rule__AttributeType__Group__16 : ( ( rule__AttributeType__Group_16__0 )? ) rule__AttributeType__Group__17 ;
    public final void rule__AttributeType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1757:1: ( ( ( rule__AttributeType__Group_16__0 )? ) rule__AttributeType__Group__17 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1758:1: ( ( rule__AttributeType__Group_16__0 )? ) rule__AttributeType__Group__17
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1758:1: ( ( rule__AttributeType__Group_16__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1759:1: ( rule__AttributeType__Group_16__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_16()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1760:1: ( rule__AttributeType__Group_16__0 )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==50) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1760:2: rule__AttributeType__Group_16__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_16__0_in_rule__AttributeType__Group__163623);
                    rule__AttributeType__Group_16__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_16()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__17_in_rule__AttributeType__Group__163633);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1771:1: rule__AttributeType__Group__17 : ( ( rule__AttributeType__Group_17__0 )? ) rule__AttributeType__Group__18 ;
    public final void rule__AttributeType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1775:1: ( ( ( rule__AttributeType__Group_17__0 )? ) rule__AttributeType__Group__18 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1776:1: ( ( rule__AttributeType__Group_17__0 )? ) rule__AttributeType__Group__18
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1776:1: ( ( rule__AttributeType__Group_17__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1777:1: ( rule__AttributeType__Group_17__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_17()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1778:1: ( rule__AttributeType__Group_17__0 )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==51) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1778:2: rule__AttributeType__Group_17__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_17__0_in_rule__AttributeType__Group__173661);
                    rule__AttributeType__Group_17__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_17()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__18_in_rule__AttributeType__Group__173671);
            rule__AttributeType__Group__18();
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
    // $ANTLR end rule__AttributeType__Group__17


    // $ANTLR start rule__AttributeType__Group__18
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1789:1: rule__AttributeType__Group__18 : ( '}' ) ;
    public final void rule__AttributeType__Group__18() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1793:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1794:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1794:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1795:1: '}'
            {
             before(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_18()); 
            match(input,37,FOLLOW_37_in_rule__AttributeType__Group__183700); 
             after(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_18()); 

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
    // $ANTLR end rule__AttributeType__Group__18


    // $ANTLR start rule__AttributeType__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1846:1: rule__AttributeType__Group_2__0 : ( 'extends' ) rule__AttributeType__Group_2__1 ;
    public final void rule__AttributeType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1850:1: ( ( 'extends' ) rule__AttributeType__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1851:1: ( 'extends' ) rule__AttributeType__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1851:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1852:1: 'extends'
            {
             before(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_2_0()); 
            match(input,38,FOLLOW_38_in_rule__AttributeType__Group_2__03774); 
             after(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_2__1_in_rule__AttributeType__Group_2__03784);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1866:1: rule__AttributeType__Group_2__1 : ( ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 ) ) ;
    public final void rule__AttributeType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1870:1: ( ( ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1871:1: ( ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1871:1: ( ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1872:1: ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1873:1: ( rule__AttributeType__BaseAttributeTypeAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1873:2: rule__AttributeType__BaseAttributeTypeAssignment_2_1
            {
            pushFollow(FOLLOW_rule__AttributeType__BaseAttributeTypeAssignment_2_1_in_rule__AttributeType__Group_2__13812);
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


    // $ANTLR start rule__AttributeType__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1887:1: rule__AttributeType__Group_3__0 : ( 'overrides' ) rule__AttributeType__Group_3__1 ;
    public final void rule__AttributeType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1891:1: ( ( 'overrides' ) rule__AttributeType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1892:1: ( 'overrides' ) rule__AttributeType__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1892:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1893:1: 'overrides'
            {
             before(grammarAccess.getAttributeTypeAccess().getOverridesKeyword_3_0()); 
            match(input,46,FOLLOW_46_in_rule__AttributeType__Group_3__03851); 
             after(grammarAccess.getAttributeTypeAccess().getOverridesKeyword_3_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_3__1_in_rule__AttributeType__Group_3__03861);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1907:1: rule__AttributeType__Group_3__1 : ( ( rule__AttributeType__OverrideAssignment_3_1 ) ) ;
    public final void rule__AttributeType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1911:1: ( ( ( rule__AttributeType__OverrideAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1912:1: ( ( rule__AttributeType__OverrideAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1912:1: ( ( rule__AttributeType__OverrideAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1913:1: ( rule__AttributeType__OverrideAssignment_3_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getOverrideAssignment_3_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1914:1: ( rule__AttributeType__OverrideAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1914:2: rule__AttributeType__OverrideAssignment_3_1
            {
            pushFollow(FOLLOW_rule__AttributeType__OverrideAssignment_3_1_in_rule__AttributeType__Group_3__13889);
            rule__AttributeType__OverrideAssignment_3_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getOverrideAssignment_3_1()); 

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


    // $ANTLR start rule__AttributeType__Group_13__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1928:1: rule__AttributeType__Group_13__0 : ( 'taggerId' ) rule__AttributeType__Group_13__1 ;
    public final void rule__AttributeType__Group_13__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1932:1: ( ( 'taggerId' ) rule__AttributeType__Group_13__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1933:1: ( 'taggerId' ) rule__AttributeType__Group_13__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1933:1: ( 'taggerId' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1934:1: 'taggerId'
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_13_0()); 
            match(input,47,FOLLOW_47_in_rule__AttributeType__Group_13__03928); 
             after(grammarAccess.getAttributeTypeAccess().getTaggerIdKeyword_13_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_13__1_in_rule__AttributeType__Group_13__03938);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1948:1: rule__AttributeType__Group_13__1 : ( ( rule__AttributeType__TaggerIdAssignment_13_1 ) ) ;
    public final void rule__AttributeType__Group_13__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1952:1: ( ( ( rule__AttributeType__TaggerIdAssignment_13_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1953:1: ( ( rule__AttributeType__TaggerIdAssignment_13_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1953:1: ( ( rule__AttributeType__TaggerIdAssignment_13_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1954:1: ( rule__AttributeType__TaggerIdAssignment_13_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdAssignment_13_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1955:1: ( rule__AttributeType__TaggerIdAssignment_13_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1955:2: rule__AttributeType__TaggerIdAssignment_13_1
            {
            pushFollow(FOLLOW_rule__AttributeType__TaggerIdAssignment_13_1_in_rule__AttributeType__Group_13__13966);
            rule__AttributeType__TaggerIdAssignment_13_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getTaggerIdAssignment_13_1()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1969:1: rule__AttributeType__Group_14__0 : ( 'enumType' ) rule__AttributeType__Group_14__1 ;
    public final void rule__AttributeType__Group_14__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1973:1: ( ( 'enumType' ) rule__AttributeType__Group_14__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1974:1: ( 'enumType' ) rule__AttributeType__Group_14__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1974:1: ( 'enumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1975:1: 'enumType'
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_14_0()); 
            match(input,48,FOLLOW_48_in_rule__AttributeType__Group_14__04005); 
             after(grammarAccess.getAttributeTypeAccess().getEnumTypeKeyword_14_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_14__1_in_rule__AttributeType__Group_14__04015);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1989:1: rule__AttributeType__Group_14__1 : ( ( rule__AttributeType__EnumTypeAssignment_14_1 ) ) ;
    public final void rule__AttributeType__Group_14__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1993:1: ( ( ( rule__AttributeType__EnumTypeAssignment_14_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1994:1: ( ( rule__AttributeType__EnumTypeAssignment_14_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1994:1: ( ( rule__AttributeType__EnumTypeAssignment_14_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1995:1: ( rule__AttributeType__EnumTypeAssignment_14_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeAssignment_14_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1996:1: ( rule__AttributeType__EnumTypeAssignment_14_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1996:2: rule__AttributeType__EnumTypeAssignment_14_1
            {
            pushFollow(FOLLOW_rule__AttributeType__EnumTypeAssignment_14_1_in_rule__AttributeType__Group_14__14043);
            rule__AttributeType__EnumTypeAssignment_14_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getEnumTypeAssignment_14_1()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2010:1: rule__AttributeType__Group_15__0 : ( 'description' ) rule__AttributeType__Group_15__1 ;
    public final void rule__AttributeType__Group_15__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2014:1: ( ( 'description' ) rule__AttributeType__Group_15__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2015:1: ( 'description' ) rule__AttributeType__Group_15__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2015:1: ( 'description' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2016:1: 'description'
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_15_0()); 
            match(input,49,FOLLOW_49_in_rule__AttributeType__Group_15__04082); 
             after(grammarAccess.getAttributeTypeAccess().getDescriptionKeyword_15_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_15__1_in_rule__AttributeType__Group_15__04092);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2030:1: rule__AttributeType__Group_15__1 : ( ( rule__AttributeType__DescriptionAssignment_15_1 ) ) ;
    public final void rule__AttributeType__Group_15__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2034:1: ( ( ( rule__AttributeType__DescriptionAssignment_15_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2035:1: ( ( rule__AttributeType__DescriptionAssignment_15_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2035:1: ( ( rule__AttributeType__DescriptionAssignment_15_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2036:1: ( rule__AttributeType__DescriptionAssignment_15_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionAssignment_15_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2037:1: ( rule__AttributeType__DescriptionAssignment_15_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2037:2: rule__AttributeType__DescriptionAssignment_15_1
            {
            pushFollow(FOLLOW_rule__AttributeType__DescriptionAssignment_15_1_in_rule__AttributeType__Group_15__14120);
            rule__AttributeType__DescriptionAssignment_15_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDescriptionAssignment_15_1()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2051:1: rule__AttributeType__Group_16__0 : ( 'defaultValue' ) rule__AttributeType__Group_16__1 ;
    public final void rule__AttributeType__Group_16__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2055:1: ( ( 'defaultValue' ) rule__AttributeType__Group_16__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2056:1: ( 'defaultValue' ) rule__AttributeType__Group_16__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2056:1: ( 'defaultValue' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2057:1: 'defaultValue'
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_16_0()); 
            match(input,50,FOLLOW_50_in_rule__AttributeType__Group_16__04159); 
             after(grammarAccess.getAttributeTypeAccess().getDefaultValueKeyword_16_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_16__1_in_rule__AttributeType__Group_16__04169);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2071:1: rule__AttributeType__Group_16__1 : ( ( rule__AttributeType__DefaultValueAssignment_16_1 ) ) ;
    public final void rule__AttributeType__Group_16__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2075:1: ( ( ( rule__AttributeType__DefaultValueAssignment_16_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2076:1: ( ( rule__AttributeType__DefaultValueAssignment_16_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2076:1: ( ( rule__AttributeType__DefaultValueAssignment_16_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2077:1: ( rule__AttributeType__DefaultValueAssignment_16_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueAssignment_16_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2078:1: ( rule__AttributeType__DefaultValueAssignment_16_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2078:2: rule__AttributeType__DefaultValueAssignment_16_1
            {
            pushFollow(FOLLOW_rule__AttributeType__DefaultValueAssignment_16_1_in_rule__AttributeType__Group_16__14197);
            rule__AttributeType__DefaultValueAssignment_16_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDefaultValueAssignment_16_1()); 

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


    // $ANTLR start rule__AttributeType__Group_17__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2092:1: rule__AttributeType__Group_17__0 : ( 'fileExtension' ) rule__AttributeType__Group_17__1 ;
    public final void rule__AttributeType__Group_17__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2096:1: ( ( 'fileExtension' ) rule__AttributeType__Group_17__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2097:1: ( 'fileExtension' ) rule__AttributeType__Group_17__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2097:1: ( 'fileExtension' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2098:1: 'fileExtension'
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_17_0()); 
            match(input,51,FOLLOW_51_in_rule__AttributeType__Group_17__04236); 
             after(grammarAccess.getAttributeTypeAccess().getFileExtensionKeyword_17_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_17__1_in_rule__AttributeType__Group_17__04246);
            rule__AttributeType__Group_17__1();
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
    // $ANTLR end rule__AttributeType__Group_17__0


    // $ANTLR start rule__AttributeType__Group_17__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2112:1: rule__AttributeType__Group_17__1 : ( ( rule__AttributeType__FileExtensionAssignment_17_1 ) ) ;
    public final void rule__AttributeType__Group_17__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2116:1: ( ( ( rule__AttributeType__FileExtensionAssignment_17_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2117:1: ( ( rule__AttributeType__FileExtensionAssignment_17_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2117:1: ( ( rule__AttributeType__FileExtensionAssignment_17_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2118:1: ( rule__AttributeType__FileExtensionAssignment_17_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionAssignment_17_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2119:1: ( rule__AttributeType__FileExtensionAssignment_17_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2119:2: rule__AttributeType__FileExtensionAssignment_17_1
            {
            pushFollow(FOLLOW_rule__AttributeType__FileExtensionAssignment_17_1_in_rule__AttributeType__Group_17__14274);
            rule__AttributeType__FileExtensionAssignment_17_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getFileExtensionAssignment_17_1()); 

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
    // $ANTLR end rule__AttributeType__Group_17__1


    // $ANTLR start rule__OseeEnumType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2133:1: rule__OseeEnumType__Group__0 : ( 'oseeEnumType' ) rule__OseeEnumType__Group__1 ;
    public final void rule__OseeEnumType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2137:1: ( ( 'oseeEnumType' ) rule__OseeEnumType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2138:1: ( 'oseeEnumType' ) rule__OseeEnumType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2138:1: ( 'oseeEnumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2139:1: 'oseeEnumType'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 
            match(input,52,FOLLOW_52_in_rule__OseeEnumType__Group__04313); 
             after(grammarAccess.getOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__1_in_rule__OseeEnumType__Group__04323);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2153:1: rule__OseeEnumType__Group__1 : ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2 ;
    public final void rule__OseeEnumType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2157:1: ( ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2158:1: ( ( rule__OseeEnumType__NameAssignment_1 ) ) rule__OseeEnumType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2158:1: ( ( rule__OseeEnumType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2159:1: ( rule__OseeEnumType__NameAssignment_1 )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2160:1: ( rule__OseeEnumType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2160:2: rule__OseeEnumType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__OseeEnumType__NameAssignment_1_in_rule__OseeEnumType__Group__14351);
            rule__OseeEnumType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__2_in_rule__OseeEnumType__Group__14360);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2171:1: rule__OseeEnumType__Group__2 : ( '{' ) rule__OseeEnumType__Group__3 ;
    public final void rule__OseeEnumType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2175:1: ( ( '{' ) rule__OseeEnumType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2176:1: ( '{' ) rule__OseeEnumType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2176:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2177:1: '{'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,35,FOLLOW_35_in_rule__OseeEnumType__Group__24389); 
             after(grammarAccess.getOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__3_in_rule__OseeEnumType__Group__24399);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2191:1: rule__OseeEnumType__Group__3 : ( 'guid' ) rule__OseeEnumType__Group__4 ;
    public final void rule__OseeEnumType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2195:1: ( ( 'guid' ) rule__OseeEnumType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2196:1: ( 'guid' ) rule__OseeEnumType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2196:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2197:1: 'guid'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getGuidKeyword_3()); 
            match(input,36,FOLLOW_36_in_rule__OseeEnumType__Group__34428); 
             after(grammarAccess.getOseeEnumTypeAccess().getGuidKeyword_3()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__4_in_rule__OseeEnumType__Group__34438);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2211:1: rule__OseeEnumType__Group__4 : ( ( rule__OseeEnumType__TypeGuidAssignment_4 ) ) rule__OseeEnumType__Group__5 ;
    public final void rule__OseeEnumType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2215:1: ( ( ( rule__OseeEnumType__TypeGuidAssignment_4 ) ) rule__OseeEnumType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2216:1: ( ( rule__OseeEnumType__TypeGuidAssignment_4 ) ) rule__OseeEnumType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2216:1: ( ( rule__OseeEnumType__TypeGuidAssignment_4 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2217:1: ( rule__OseeEnumType__TypeGuidAssignment_4 )
            {
             before(grammarAccess.getOseeEnumTypeAccess().getTypeGuidAssignment_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2218:1: ( rule__OseeEnumType__TypeGuidAssignment_4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2218:2: rule__OseeEnumType__TypeGuidAssignment_4
            {
            pushFollow(FOLLOW_rule__OseeEnumType__TypeGuidAssignment_4_in_rule__OseeEnumType__Group__44466);
            rule__OseeEnumType__TypeGuidAssignment_4();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumTypeAccess().getTypeGuidAssignment_4()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__5_in_rule__OseeEnumType__Group__44475);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2229:1: rule__OseeEnumType__Group__5 : ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* ) rule__OseeEnumType__Group__6 ;
    public final void rule__OseeEnumType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2233:1: ( ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* ) rule__OseeEnumType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2234:1: ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* ) rule__OseeEnumType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2234:1: ( ( rule__OseeEnumType__EnumEntriesAssignment_5 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2235:1: ( rule__OseeEnumType__EnumEntriesAssignment_5 )*
            {
             before(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesAssignment_5()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2236:1: ( rule__OseeEnumType__EnumEntriesAssignment_5 )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==53) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2236:2: rule__OseeEnumType__EnumEntriesAssignment_5
            	    {
            	    pushFollow(FOLLOW_rule__OseeEnumType__EnumEntriesAssignment_5_in_rule__OseeEnumType__Group__54503);
            	    rule__OseeEnumType__EnumEntriesAssignment_5();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);

             after(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesAssignment_5()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumType__Group__6_in_rule__OseeEnumType__Group__54513);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2247:1: rule__OseeEnumType__Group__6 : ( '}' ) ;
    public final void rule__OseeEnumType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2251:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2252:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2252:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2253:1: '}'
            {
             before(grammarAccess.getOseeEnumTypeAccess().getRightCurlyBracketKeyword_6()); 
            match(input,37,FOLLOW_37_in_rule__OseeEnumType__Group__64542); 
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


    // $ANTLR start rule__OseeEnumEntry__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2280:1: rule__OseeEnumEntry__Group__0 : ( 'entry' ) rule__OseeEnumEntry__Group__1 ;
    public final void rule__OseeEnumEntry__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2284:1: ( ( 'entry' ) rule__OseeEnumEntry__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2285:1: ( 'entry' ) rule__OseeEnumEntry__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2285:1: ( 'entry' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2286:1: 'entry'
            {
             before(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0()); 
            match(input,53,FOLLOW_53_in_rule__OseeEnumEntry__Group__04592); 
             after(grammarAccess.getOseeEnumEntryAccess().getEntryKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__1_in_rule__OseeEnumEntry__Group__04602);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2300:1: rule__OseeEnumEntry__Group__1 : ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2 ;
    public final void rule__OseeEnumEntry__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2304:1: ( ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2305:1: ( ( rule__OseeEnumEntry__NameAssignment_1 ) ) rule__OseeEnumEntry__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2305:1: ( ( rule__OseeEnumEntry__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2306:1: ( rule__OseeEnumEntry__NameAssignment_1 )
            {
             before(grammarAccess.getOseeEnumEntryAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2307:1: ( rule__OseeEnumEntry__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2307:2: rule__OseeEnumEntry__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__OseeEnumEntry__NameAssignment_1_in_rule__OseeEnumEntry__Group__14630);
            rule__OseeEnumEntry__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumEntryAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__2_in_rule__OseeEnumEntry__Group__14639);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2318:1: rule__OseeEnumEntry__Group__2 : ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? ) rule__OseeEnumEntry__Group__3 ;
    public final void rule__OseeEnumEntry__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2322:1: ( ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? ) rule__OseeEnumEntry__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2323:1: ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? ) rule__OseeEnumEntry__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2323:1: ( ( rule__OseeEnumEntry__OrdinalAssignment_2 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2324:1: ( rule__OseeEnumEntry__OrdinalAssignment_2 )?
            {
             before(grammarAccess.getOseeEnumEntryAccess().getOrdinalAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2325:1: ( rule__OseeEnumEntry__OrdinalAssignment_2 )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==RULE_WHOLE_NUM_STR) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2325:2: rule__OseeEnumEntry__OrdinalAssignment_2
                    {
                    pushFollow(FOLLOW_rule__OseeEnumEntry__OrdinalAssignment_2_in_rule__OseeEnumEntry__Group__24667);
                    rule__OseeEnumEntry__OrdinalAssignment_2();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOseeEnumEntryAccess().getOrdinalAssignment_2()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumEntry__Group__3_in_rule__OseeEnumEntry__Group__24677);
            rule__OseeEnumEntry__Group__3();
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
    // $ANTLR end rule__OseeEnumEntry__Group__2


    // $ANTLR start rule__OseeEnumEntry__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2336:1: rule__OseeEnumEntry__Group__3 : ( ( rule__OseeEnumEntry__Group_3__0 )? ) ;
    public final void rule__OseeEnumEntry__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2340:1: ( ( ( rule__OseeEnumEntry__Group_3__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2341:1: ( ( rule__OseeEnumEntry__Group_3__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2341:1: ( ( rule__OseeEnumEntry__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2342:1: ( rule__OseeEnumEntry__Group_3__0 )?
            {
             before(grammarAccess.getOseeEnumEntryAccess().getGroup_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2343:1: ( rule__OseeEnumEntry__Group_3__0 )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==54) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2343:2: rule__OseeEnumEntry__Group_3__0
                    {
                    pushFollow(FOLLOW_rule__OseeEnumEntry__Group_3__0_in_rule__OseeEnumEntry__Group__34705);
                    rule__OseeEnumEntry__Group_3__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOseeEnumEntryAccess().getGroup_3()); 

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
    // $ANTLR end rule__OseeEnumEntry__Group__3


    // $ANTLR start rule__OseeEnumEntry__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2361:1: rule__OseeEnumEntry__Group_3__0 : ( 'entryGuid' ) rule__OseeEnumEntry__Group_3__1 ;
    public final void rule__OseeEnumEntry__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2365:1: ( ( 'entryGuid' ) rule__OseeEnumEntry__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2366:1: ( 'entryGuid' ) rule__OseeEnumEntry__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2366:1: ( 'entryGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2367:1: 'entryGuid'
            {
             before(grammarAccess.getOseeEnumEntryAccess().getEntryGuidKeyword_3_0()); 
            match(input,54,FOLLOW_54_in_rule__OseeEnumEntry__Group_3__04749); 
             after(grammarAccess.getOseeEnumEntryAccess().getEntryGuidKeyword_3_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumEntry__Group_3__1_in_rule__OseeEnumEntry__Group_3__04759);
            rule__OseeEnumEntry__Group_3__1();
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
    // $ANTLR end rule__OseeEnumEntry__Group_3__0


    // $ANTLR start rule__OseeEnumEntry__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2381:1: rule__OseeEnumEntry__Group_3__1 : ( ( rule__OseeEnumEntry__EntryGuidAssignment_3_1 ) ) ;
    public final void rule__OseeEnumEntry__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2385:1: ( ( ( rule__OseeEnumEntry__EntryGuidAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2386:1: ( ( rule__OseeEnumEntry__EntryGuidAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2386:1: ( ( rule__OseeEnumEntry__EntryGuidAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2387:1: ( rule__OseeEnumEntry__EntryGuidAssignment_3_1 )
            {
             before(grammarAccess.getOseeEnumEntryAccess().getEntryGuidAssignment_3_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2388:1: ( rule__OseeEnumEntry__EntryGuidAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2388:2: rule__OseeEnumEntry__EntryGuidAssignment_3_1
            {
            pushFollow(FOLLOW_rule__OseeEnumEntry__EntryGuidAssignment_3_1_in_rule__OseeEnumEntry__Group_3__14787);
            rule__OseeEnumEntry__EntryGuidAssignment_3_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumEntryAccess().getEntryGuidAssignment_3_1()); 

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
    // $ANTLR end rule__OseeEnumEntry__Group_3__1


    // $ANTLR start rule__OseeEnumOverride__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2402:1: rule__OseeEnumOverride__Group__0 : ( 'overrides enum' ) rule__OseeEnumOverride__Group__1 ;
    public final void rule__OseeEnumOverride__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2406:1: ( ( 'overrides enum' ) rule__OseeEnumOverride__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2407:1: ( 'overrides enum' ) rule__OseeEnumOverride__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2407:1: ( 'overrides enum' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2408:1: 'overrides enum'
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getOverridesEnumKeyword_0()); 
            match(input,55,FOLLOW_55_in_rule__OseeEnumOverride__Group__04826); 
             after(grammarAccess.getOseeEnumOverrideAccess().getOverridesEnumKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumOverride__Group__1_in_rule__OseeEnumOverride__Group__04836);
            rule__OseeEnumOverride__Group__1();
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
    // $ANTLR end rule__OseeEnumOverride__Group__0


    // $ANTLR start rule__OseeEnumOverride__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2422:1: rule__OseeEnumOverride__Group__1 : ( ( rule__OseeEnumOverride__OverridenEnumTypeAssignment_1 ) ) rule__OseeEnumOverride__Group__2 ;
    public final void rule__OseeEnumOverride__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2426:1: ( ( ( rule__OseeEnumOverride__OverridenEnumTypeAssignment_1 ) ) rule__OseeEnumOverride__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2427:1: ( ( rule__OseeEnumOverride__OverridenEnumTypeAssignment_1 ) ) rule__OseeEnumOverride__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2427:1: ( ( rule__OseeEnumOverride__OverridenEnumTypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2428:1: ( rule__OseeEnumOverride__OverridenEnumTypeAssignment_1 )
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getOverridenEnumTypeAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2429:1: ( rule__OseeEnumOverride__OverridenEnumTypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2429:2: rule__OseeEnumOverride__OverridenEnumTypeAssignment_1
            {
            pushFollow(FOLLOW_rule__OseeEnumOverride__OverridenEnumTypeAssignment_1_in_rule__OseeEnumOverride__Group__14864);
            rule__OseeEnumOverride__OverridenEnumTypeAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getOseeEnumOverrideAccess().getOverridenEnumTypeAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumOverride__Group__2_in_rule__OseeEnumOverride__Group__14873);
            rule__OseeEnumOverride__Group__2();
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
    // $ANTLR end rule__OseeEnumOverride__Group__1


    // $ANTLR start rule__OseeEnumOverride__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2440:1: rule__OseeEnumOverride__Group__2 : ( '{' ) rule__OseeEnumOverride__Group__3 ;
    public final void rule__OseeEnumOverride__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2444:1: ( ( '{' ) rule__OseeEnumOverride__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2445:1: ( '{' ) rule__OseeEnumOverride__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2445:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2446:1: '{'
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,35,FOLLOW_35_in_rule__OseeEnumOverride__Group__24902); 
             after(grammarAccess.getOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumOverride__Group__3_in_rule__OseeEnumOverride__Group__24912);
            rule__OseeEnumOverride__Group__3();
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
    // $ANTLR end rule__OseeEnumOverride__Group__2


    // $ANTLR start rule__OseeEnumOverride__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2460:1: rule__OseeEnumOverride__Group__3 : ( ( rule__OseeEnumOverride__InheritAllAssignment_3 )? ) rule__OseeEnumOverride__Group__4 ;
    public final void rule__OseeEnumOverride__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2464:1: ( ( ( rule__OseeEnumOverride__InheritAllAssignment_3 )? ) rule__OseeEnumOverride__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2465:1: ( ( rule__OseeEnumOverride__InheritAllAssignment_3 )? ) rule__OseeEnumOverride__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2465:1: ( ( rule__OseeEnumOverride__InheritAllAssignment_3 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2466:1: ( rule__OseeEnumOverride__InheritAllAssignment_3 )?
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getInheritAllAssignment_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2467:1: ( rule__OseeEnumOverride__InheritAllAssignment_3 )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==66) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2467:2: rule__OseeEnumOverride__InheritAllAssignment_3
                    {
                    pushFollow(FOLLOW_rule__OseeEnumOverride__InheritAllAssignment_3_in_rule__OseeEnumOverride__Group__34940);
                    rule__OseeEnumOverride__InheritAllAssignment_3();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getOseeEnumOverrideAccess().getInheritAllAssignment_3()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumOverride__Group__4_in_rule__OseeEnumOverride__Group__34950);
            rule__OseeEnumOverride__Group__4();
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
    // $ANTLR end rule__OseeEnumOverride__Group__3


    // $ANTLR start rule__OseeEnumOverride__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2478:1: rule__OseeEnumOverride__Group__4 : ( ( rule__OseeEnumOverride__OverrideOptionsAssignment_4 )* ) rule__OseeEnumOverride__Group__5 ;
    public final void rule__OseeEnumOverride__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2482:1: ( ( ( rule__OseeEnumOverride__OverrideOptionsAssignment_4 )* ) rule__OseeEnumOverride__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2483:1: ( ( rule__OseeEnumOverride__OverrideOptionsAssignment_4 )* ) rule__OseeEnumOverride__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2483:1: ( ( rule__OseeEnumOverride__OverrideOptionsAssignment_4 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2484:1: ( rule__OseeEnumOverride__OverrideOptionsAssignment_4 )*
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getOverrideOptionsAssignment_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2485:1: ( rule__OseeEnumOverride__OverrideOptionsAssignment_4 )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( ((LA28_0>=56 && LA28_0<=57)) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2485:2: rule__OseeEnumOverride__OverrideOptionsAssignment_4
            	    {
            	    pushFollow(FOLLOW_rule__OseeEnumOverride__OverrideOptionsAssignment_4_in_rule__OseeEnumOverride__Group__44978);
            	    rule__OseeEnumOverride__OverrideOptionsAssignment_4();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

             after(grammarAccess.getOseeEnumOverrideAccess().getOverrideOptionsAssignment_4()); 

            }

            pushFollow(FOLLOW_rule__OseeEnumOverride__Group__5_in_rule__OseeEnumOverride__Group__44988);
            rule__OseeEnumOverride__Group__5();
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
    // $ANTLR end rule__OseeEnumOverride__Group__4


    // $ANTLR start rule__OseeEnumOverride__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2496:1: rule__OseeEnumOverride__Group__5 : ( '}' ) ;
    public final void rule__OseeEnumOverride__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2500:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2501:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2501:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2502:1: '}'
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5()); 
            match(input,37,FOLLOW_37_in_rule__OseeEnumOverride__Group__55017); 
             after(grammarAccess.getOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5()); 

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
    // $ANTLR end rule__OseeEnumOverride__Group__5


    // $ANTLR start rule__AddEnum__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2527:1: rule__AddEnum__Group__0 : ( 'add' ) rule__AddEnum__Group__1 ;
    public final void rule__AddEnum__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2531:1: ( ( 'add' ) rule__AddEnum__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2532:1: ( 'add' ) rule__AddEnum__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2532:1: ( 'add' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2533:1: 'add'
            {
             before(grammarAccess.getAddEnumAccess().getAddKeyword_0()); 
            match(input,56,FOLLOW_56_in_rule__AddEnum__Group__05065); 
             after(grammarAccess.getAddEnumAccess().getAddKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__AddEnum__Group__1_in_rule__AddEnum__Group__05075);
            rule__AddEnum__Group__1();
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
    // $ANTLR end rule__AddEnum__Group__0


    // $ANTLR start rule__AddEnum__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2547:1: rule__AddEnum__Group__1 : ( ( rule__AddEnum__EnumEntryAssignment_1 ) ) rule__AddEnum__Group__2 ;
    public final void rule__AddEnum__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2551:1: ( ( ( rule__AddEnum__EnumEntryAssignment_1 ) ) rule__AddEnum__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2552:1: ( ( rule__AddEnum__EnumEntryAssignment_1 ) ) rule__AddEnum__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2552:1: ( ( rule__AddEnum__EnumEntryAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2553:1: ( rule__AddEnum__EnumEntryAssignment_1 )
            {
             before(grammarAccess.getAddEnumAccess().getEnumEntryAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2554:1: ( rule__AddEnum__EnumEntryAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2554:2: rule__AddEnum__EnumEntryAssignment_1
            {
            pushFollow(FOLLOW_rule__AddEnum__EnumEntryAssignment_1_in_rule__AddEnum__Group__15103);
            rule__AddEnum__EnumEntryAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getAddEnumAccess().getEnumEntryAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__AddEnum__Group__2_in_rule__AddEnum__Group__15112);
            rule__AddEnum__Group__2();
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
    // $ANTLR end rule__AddEnum__Group__1


    // $ANTLR start rule__AddEnum__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2565:1: rule__AddEnum__Group__2 : ( ( rule__AddEnum__OrdinalAssignment_2 )? ) rule__AddEnum__Group__3 ;
    public final void rule__AddEnum__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2569:1: ( ( ( rule__AddEnum__OrdinalAssignment_2 )? ) rule__AddEnum__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2570:1: ( ( rule__AddEnum__OrdinalAssignment_2 )? ) rule__AddEnum__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2570:1: ( ( rule__AddEnum__OrdinalAssignment_2 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2571:1: ( rule__AddEnum__OrdinalAssignment_2 )?
            {
             before(grammarAccess.getAddEnumAccess().getOrdinalAssignment_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2572:1: ( rule__AddEnum__OrdinalAssignment_2 )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==RULE_WHOLE_NUM_STR) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2572:2: rule__AddEnum__OrdinalAssignment_2
                    {
                    pushFollow(FOLLOW_rule__AddEnum__OrdinalAssignment_2_in_rule__AddEnum__Group__25140);
                    rule__AddEnum__OrdinalAssignment_2();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAddEnumAccess().getOrdinalAssignment_2()); 

            }

            pushFollow(FOLLOW_rule__AddEnum__Group__3_in_rule__AddEnum__Group__25150);
            rule__AddEnum__Group__3();
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
    // $ANTLR end rule__AddEnum__Group__2


    // $ANTLR start rule__AddEnum__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2583:1: rule__AddEnum__Group__3 : ( ( rule__AddEnum__Group_3__0 )? ) ;
    public final void rule__AddEnum__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2587:1: ( ( ( rule__AddEnum__Group_3__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2588:1: ( ( rule__AddEnum__Group_3__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2588:1: ( ( rule__AddEnum__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2589:1: ( rule__AddEnum__Group_3__0 )?
            {
             before(grammarAccess.getAddEnumAccess().getGroup_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2590:1: ( rule__AddEnum__Group_3__0 )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==54) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2590:2: rule__AddEnum__Group_3__0
                    {
                    pushFollow(FOLLOW_rule__AddEnum__Group_3__0_in_rule__AddEnum__Group__35178);
                    rule__AddEnum__Group_3__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAddEnumAccess().getGroup_3()); 

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
    // $ANTLR end rule__AddEnum__Group__3


    // $ANTLR start rule__AddEnum__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2608:1: rule__AddEnum__Group_3__0 : ( 'entryGuid' ) rule__AddEnum__Group_3__1 ;
    public final void rule__AddEnum__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2612:1: ( ( 'entryGuid' ) rule__AddEnum__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2613:1: ( 'entryGuid' ) rule__AddEnum__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2613:1: ( 'entryGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2614:1: 'entryGuid'
            {
             before(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0()); 
            match(input,54,FOLLOW_54_in_rule__AddEnum__Group_3__05222); 
             after(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0()); 

            }

            pushFollow(FOLLOW_rule__AddEnum__Group_3__1_in_rule__AddEnum__Group_3__05232);
            rule__AddEnum__Group_3__1();
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
    // $ANTLR end rule__AddEnum__Group_3__0


    // $ANTLR start rule__AddEnum__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2628:1: rule__AddEnum__Group_3__1 : ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) ) ;
    public final void rule__AddEnum__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2632:1: ( ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2633:1: ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2633:1: ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2634:1: ( rule__AddEnum__EntryGuidAssignment_3_1 )
            {
             before(grammarAccess.getAddEnumAccess().getEntryGuidAssignment_3_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2635:1: ( rule__AddEnum__EntryGuidAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2635:2: rule__AddEnum__EntryGuidAssignment_3_1
            {
            pushFollow(FOLLOW_rule__AddEnum__EntryGuidAssignment_3_1_in_rule__AddEnum__Group_3__15260);
            rule__AddEnum__EntryGuidAssignment_3_1();
            _fsp--;


            }

             after(grammarAccess.getAddEnumAccess().getEntryGuidAssignment_3_1()); 

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
    // $ANTLR end rule__AddEnum__Group_3__1


    // $ANTLR start rule__RemoveEnum__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2649:1: rule__RemoveEnum__Group__0 : ( 'remove' ) rule__RemoveEnum__Group__1 ;
    public final void rule__RemoveEnum__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2653:1: ( ( 'remove' ) rule__RemoveEnum__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2654:1: ( 'remove' ) rule__RemoveEnum__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2654:1: ( 'remove' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2655:1: 'remove'
            {
             before(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0()); 
            match(input,57,FOLLOW_57_in_rule__RemoveEnum__Group__05299); 
             after(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__RemoveEnum__Group__1_in_rule__RemoveEnum__Group__05309);
            rule__RemoveEnum__Group__1();
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
    // $ANTLR end rule__RemoveEnum__Group__0


    // $ANTLR start rule__RemoveEnum__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2669:1: rule__RemoveEnum__Group__1 : ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) ) ;
    public final void rule__RemoveEnum__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2673:1: ( ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2674:1: ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2674:1: ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2675:1: ( rule__RemoveEnum__EnumEntryAssignment_1 )
            {
             before(grammarAccess.getRemoveEnumAccess().getEnumEntryAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2676:1: ( rule__RemoveEnum__EnumEntryAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2676:2: rule__RemoveEnum__EnumEntryAssignment_1
            {
            pushFollow(FOLLOW_rule__RemoveEnum__EnumEntryAssignment_1_in_rule__RemoveEnum__Group__15337);
            rule__RemoveEnum__EnumEntryAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getRemoveEnumAccess().getEnumEntryAssignment_1()); 

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
    // $ANTLR end rule__RemoveEnum__Group__1


    // $ANTLR start rule__RelationType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2690:1: rule__RelationType__Group__0 : ( 'relationType' ) rule__RelationType__Group__1 ;
    public final void rule__RelationType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2694:1: ( ( 'relationType' ) rule__RelationType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2695:1: ( 'relationType' ) rule__RelationType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2695:1: ( 'relationType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2696:1: 'relationType'
            {
             before(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0()); 
            match(input,58,FOLLOW_58_in_rule__RelationType__Group__05376); 
             after(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__1_in_rule__RelationType__Group__05386);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2710:1: rule__RelationType__Group__1 : ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2 ;
    public final void rule__RelationType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2714:1: ( ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2715:1: ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2715:1: ( ( rule__RelationType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2716:1: ( rule__RelationType__NameAssignment_1 )
            {
             before(grammarAccess.getRelationTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2717:1: ( rule__RelationType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2717:2: rule__RelationType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__RelationType__NameAssignment_1_in_rule__RelationType__Group__15414);
            rule__RelationType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__2_in_rule__RelationType__Group__15423);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2728:1: rule__RelationType__Group__2 : ( '{' ) rule__RelationType__Group__3 ;
    public final void rule__RelationType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2732:1: ( ( '{' ) rule__RelationType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2733:1: ( '{' ) rule__RelationType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2733:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2734:1: '{'
            {
             before(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,35,FOLLOW_35_in_rule__RelationType__Group__25452); 
             after(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__3_in_rule__RelationType__Group__25462);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2748:1: rule__RelationType__Group__3 : ( 'guid' ) rule__RelationType__Group__4 ;
    public final void rule__RelationType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2752:1: ( ( 'guid' ) rule__RelationType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2753:1: ( 'guid' ) rule__RelationType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2753:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2754:1: 'guid'
            {
             before(grammarAccess.getRelationTypeAccess().getGuidKeyword_3()); 
            match(input,36,FOLLOW_36_in_rule__RelationType__Group__35491); 
             after(grammarAccess.getRelationTypeAccess().getGuidKeyword_3()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__4_in_rule__RelationType__Group__35501);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2768:1: rule__RelationType__Group__4 : ( ( rule__RelationType__TypeGuidAssignment_4 ) ) rule__RelationType__Group__5 ;
    public final void rule__RelationType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2772:1: ( ( ( rule__RelationType__TypeGuidAssignment_4 ) ) rule__RelationType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2773:1: ( ( rule__RelationType__TypeGuidAssignment_4 ) ) rule__RelationType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2773:1: ( ( rule__RelationType__TypeGuidAssignment_4 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2774:1: ( rule__RelationType__TypeGuidAssignment_4 )
            {
             before(grammarAccess.getRelationTypeAccess().getTypeGuidAssignment_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2775:1: ( rule__RelationType__TypeGuidAssignment_4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2775:2: rule__RelationType__TypeGuidAssignment_4
            {
            pushFollow(FOLLOW_rule__RelationType__TypeGuidAssignment_4_in_rule__RelationType__Group__45529);
            rule__RelationType__TypeGuidAssignment_4();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getTypeGuidAssignment_4()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__5_in_rule__RelationType__Group__45538);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2786:1: rule__RelationType__Group__5 : ( 'sideAName' ) rule__RelationType__Group__6 ;
    public final void rule__RelationType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2790:1: ( ( 'sideAName' ) rule__RelationType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2791:1: ( 'sideAName' ) rule__RelationType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2791:1: ( 'sideAName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2792:1: 'sideAName'
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameKeyword_5()); 
            match(input,59,FOLLOW_59_in_rule__RelationType__Group__55567); 
             after(grammarAccess.getRelationTypeAccess().getSideANameKeyword_5()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__6_in_rule__RelationType__Group__55577);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2806:1: rule__RelationType__Group__6 : ( ( rule__RelationType__SideANameAssignment_6 ) ) rule__RelationType__Group__7 ;
    public final void rule__RelationType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2810:1: ( ( ( rule__RelationType__SideANameAssignment_6 ) ) rule__RelationType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2811:1: ( ( rule__RelationType__SideANameAssignment_6 ) ) rule__RelationType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2811:1: ( ( rule__RelationType__SideANameAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2812:1: ( rule__RelationType__SideANameAssignment_6 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameAssignment_6()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2813:1: ( rule__RelationType__SideANameAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2813:2: rule__RelationType__SideANameAssignment_6
            {
            pushFollow(FOLLOW_rule__RelationType__SideANameAssignment_6_in_rule__RelationType__Group__65605);
            rule__RelationType__SideANameAssignment_6();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideANameAssignment_6()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__7_in_rule__RelationType__Group__65614);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2824:1: rule__RelationType__Group__7 : ( 'sideAArtifactType' ) rule__RelationType__Group__8 ;
    public final void rule__RelationType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2828:1: ( ( 'sideAArtifactType' ) rule__RelationType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2829:1: ( 'sideAArtifactType' ) rule__RelationType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2829:1: ( 'sideAArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2830:1: 'sideAArtifactType'
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_7()); 
            match(input,60,FOLLOW_60_in_rule__RelationType__Group__75643); 
             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeKeyword_7()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__8_in_rule__RelationType__Group__75653);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2844:1: rule__RelationType__Group__8 : ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) ) rule__RelationType__Group__9 ;
    public final void rule__RelationType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2848:1: ( ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) ) rule__RelationType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2849:1: ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) ) rule__RelationType__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2849:1: ( ( rule__RelationType__SideAArtifactTypeAssignment_8 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2850:1: ( rule__RelationType__SideAArtifactTypeAssignment_8 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeAssignment_8()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2851:1: ( rule__RelationType__SideAArtifactTypeAssignment_8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2851:2: rule__RelationType__SideAArtifactTypeAssignment_8
            {
            pushFollow(FOLLOW_rule__RelationType__SideAArtifactTypeAssignment_8_in_rule__RelationType__Group__85681);
            rule__RelationType__SideAArtifactTypeAssignment_8();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeAssignment_8()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__9_in_rule__RelationType__Group__85690);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2862:1: rule__RelationType__Group__9 : ( 'sideBName' ) rule__RelationType__Group__10 ;
    public final void rule__RelationType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2866:1: ( ( 'sideBName' ) rule__RelationType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2867:1: ( 'sideBName' ) rule__RelationType__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2867:1: ( 'sideBName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2868:1: 'sideBName'
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_9()); 
            match(input,61,FOLLOW_61_in_rule__RelationType__Group__95719); 
             after(grammarAccess.getRelationTypeAccess().getSideBNameKeyword_9()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__10_in_rule__RelationType__Group__95729);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2882:1: rule__RelationType__Group__10 : ( ( rule__RelationType__SideBNameAssignment_10 ) ) rule__RelationType__Group__11 ;
    public final void rule__RelationType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2886:1: ( ( ( rule__RelationType__SideBNameAssignment_10 ) ) rule__RelationType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2887:1: ( ( rule__RelationType__SideBNameAssignment_10 ) ) rule__RelationType__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2887:1: ( ( rule__RelationType__SideBNameAssignment_10 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2888:1: ( rule__RelationType__SideBNameAssignment_10 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameAssignment_10()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2889:1: ( rule__RelationType__SideBNameAssignment_10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2889:2: rule__RelationType__SideBNameAssignment_10
            {
            pushFollow(FOLLOW_rule__RelationType__SideBNameAssignment_10_in_rule__RelationType__Group__105757);
            rule__RelationType__SideBNameAssignment_10();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideBNameAssignment_10()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__11_in_rule__RelationType__Group__105766);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2900:1: rule__RelationType__Group__11 : ( 'sideBArtifactType' ) rule__RelationType__Group__12 ;
    public final void rule__RelationType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2904:1: ( ( 'sideBArtifactType' ) rule__RelationType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2905:1: ( 'sideBArtifactType' ) rule__RelationType__Group__12
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2905:1: ( 'sideBArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2906:1: 'sideBArtifactType'
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_11()); 
            match(input,62,FOLLOW_62_in_rule__RelationType__Group__115795); 
             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeKeyword_11()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__12_in_rule__RelationType__Group__115805);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2920:1: rule__RelationType__Group__12 : ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) ) rule__RelationType__Group__13 ;
    public final void rule__RelationType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2924:1: ( ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) ) rule__RelationType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2925:1: ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) ) rule__RelationType__Group__13
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2925:1: ( ( rule__RelationType__SideBArtifactTypeAssignment_12 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2926:1: ( rule__RelationType__SideBArtifactTypeAssignment_12 )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeAssignment_12()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2927:1: ( rule__RelationType__SideBArtifactTypeAssignment_12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2927:2: rule__RelationType__SideBArtifactTypeAssignment_12
            {
            pushFollow(FOLLOW_rule__RelationType__SideBArtifactTypeAssignment_12_in_rule__RelationType__Group__125833);
            rule__RelationType__SideBArtifactTypeAssignment_12();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeAssignment_12()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__13_in_rule__RelationType__Group__125842);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2938:1: rule__RelationType__Group__13 : ( 'defaultOrderType' ) rule__RelationType__Group__14 ;
    public final void rule__RelationType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2942:1: ( ( 'defaultOrderType' ) rule__RelationType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2943:1: ( 'defaultOrderType' ) rule__RelationType__Group__14
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2943:1: ( 'defaultOrderType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2944:1: 'defaultOrderType'
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_13()); 
            match(input,63,FOLLOW_63_in_rule__RelationType__Group__135871); 
             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeKeyword_13()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__14_in_rule__RelationType__Group__135881);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2958:1: rule__RelationType__Group__14 : ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) ) rule__RelationType__Group__15 ;
    public final void rule__RelationType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2962:1: ( ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) ) rule__RelationType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2963:1: ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) ) rule__RelationType__Group__15
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2963:1: ( ( rule__RelationType__DefaultOrderTypeAssignment_14 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2964:1: ( rule__RelationType__DefaultOrderTypeAssignment_14 )
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAssignment_14()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2965:1: ( rule__RelationType__DefaultOrderTypeAssignment_14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2965:2: rule__RelationType__DefaultOrderTypeAssignment_14
            {
            pushFollow(FOLLOW_rule__RelationType__DefaultOrderTypeAssignment_14_in_rule__RelationType__Group__145909);
            rule__RelationType__DefaultOrderTypeAssignment_14();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeAssignment_14()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__15_in_rule__RelationType__Group__145918);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2976:1: rule__RelationType__Group__15 : ( 'multiplicity' ) rule__RelationType__Group__16 ;
    public final void rule__RelationType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2980:1: ( ( 'multiplicity' ) rule__RelationType__Group__16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2981:1: ( 'multiplicity' ) rule__RelationType__Group__16
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2981:1: ( 'multiplicity' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2982:1: 'multiplicity'
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_15()); 
            match(input,64,FOLLOW_64_in_rule__RelationType__Group__155947); 
             after(grammarAccess.getRelationTypeAccess().getMultiplicityKeyword_15()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__16_in_rule__RelationType__Group__155957);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:2996:1: rule__RelationType__Group__16 : ( ( rule__RelationType__MultiplicityAssignment_16 ) ) rule__RelationType__Group__17 ;
    public final void rule__RelationType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3000:1: ( ( ( rule__RelationType__MultiplicityAssignment_16 ) ) rule__RelationType__Group__17 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3001:1: ( ( rule__RelationType__MultiplicityAssignment_16 ) ) rule__RelationType__Group__17
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3001:1: ( ( rule__RelationType__MultiplicityAssignment_16 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3002:1: ( rule__RelationType__MultiplicityAssignment_16 )
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityAssignment_16()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3003:1: ( rule__RelationType__MultiplicityAssignment_16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3003:2: rule__RelationType__MultiplicityAssignment_16
            {
            pushFollow(FOLLOW_rule__RelationType__MultiplicityAssignment_16_in_rule__RelationType__Group__165985);
            rule__RelationType__MultiplicityAssignment_16();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getMultiplicityAssignment_16()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__17_in_rule__RelationType__Group__165994);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3014:1: rule__RelationType__Group__17 : ( '}' ) ;
    public final void rule__RelationType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3018:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3019:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3019:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3020:1: '}'
            {
             before(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_17()); 
            match(input,37,FOLLOW_37_in_rule__RelationType__Group__176023); 
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


    // $ANTLR start rule__OseeTypeModel__ImportsAssignment_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3069:1: rule__OseeTypeModel__ImportsAssignment_0 : ( ruleImport ) ;
    public final void rule__OseeTypeModel__ImportsAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3073:1: ( ( ruleImport ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3074:1: ( ruleImport )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3074:1: ( ruleImport )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3075:1: ruleImport
            {
             before(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0()); 
            pushFollow(FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_06094);
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


    // $ANTLR start rule__OseeTypeModel__ArtifactTypesAssignment_1_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3084:1: rule__OseeTypeModel__ArtifactTypesAssignment_1_0 : ( ruleArtifactType ) ;
    public final void rule__OseeTypeModel__ArtifactTypesAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3088:1: ( ( ruleArtifactType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3089:1: ( ruleArtifactType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3089:1: ( ruleArtifactType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3090:1: ruleArtifactType
            {
             before(grammarAccess.getOseeTypeModelAccess().getArtifactTypesArtifactTypeParserRuleCall_1_0_0()); 
            pushFollow(FOLLOW_ruleArtifactType_in_rule__OseeTypeModel__ArtifactTypesAssignment_1_06125);
            ruleArtifactType();
            _fsp--;

             after(grammarAccess.getOseeTypeModelAccess().getArtifactTypesArtifactTypeParserRuleCall_1_0_0()); 

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
    // $ANTLR end rule__OseeTypeModel__ArtifactTypesAssignment_1_0


    // $ANTLR start rule__OseeTypeModel__RelationTypesAssignment_1_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3099:1: rule__OseeTypeModel__RelationTypesAssignment_1_1 : ( ruleRelationType ) ;
    public final void rule__OseeTypeModel__RelationTypesAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3103:1: ( ( ruleRelationType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3104:1: ( ruleRelationType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3104:1: ( ruleRelationType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3105:1: ruleRelationType
            {
             before(grammarAccess.getOseeTypeModelAccess().getRelationTypesRelationTypeParserRuleCall_1_1_0()); 
            pushFollow(FOLLOW_ruleRelationType_in_rule__OseeTypeModel__RelationTypesAssignment_1_16156);
            ruleRelationType();
            _fsp--;

             after(grammarAccess.getOseeTypeModelAccess().getRelationTypesRelationTypeParserRuleCall_1_1_0()); 

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
    // $ANTLR end rule__OseeTypeModel__RelationTypesAssignment_1_1


    // $ANTLR start rule__OseeTypeModel__AttributeTypesAssignment_1_2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3114:1: rule__OseeTypeModel__AttributeTypesAssignment_1_2 : ( ruleAttributeType ) ;
    public final void rule__OseeTypeModel__AttributeTypesAssignment_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3118:1: ( ( ruleAttributeType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3119:1: ( ruleAttributeType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3119:1: ( ruleAttributeType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3120:1: ruleAttributeType
            {
             before(grammarAccess.getOseeTypeModelAccess().getAttributeTypesAttributeTypeParserRuleCall_1_2_0()); 
            pushFollow(FOLLOW_ruleAttributeType_in_rule__OseeTypeModel__AttributeTypesAssignment_1_26187);
            ruleAttributeType();
            _fsp--;

             after(grammarAccess.getOseeTypeModelAccess().getAttributeTypesAttributeTypeParserRuleCall_1_2_0()); 

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
    // $ANTLR end rule__OseeTypeModel__AttributeTypesAssignment_1_2


    // $ANTLR start rule__OseeTypeModel__EnumTypesAssignment_1_3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3129:1: rule__OseeTypeModel__EnumTypesAssignment_1_3 : ( ruleOseeEnumType ) ;
    public final void rule__OseeTypeModel__EnumTypesAssignment_1_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3133:1: ( ( ruleOseeEnumType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3134:1: ( ruleOseeEnumType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3134:1: ( ruleOseeEnumType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3135:1: ruleOseeEnumType
            {
             before(grammarAccess.getOseeTypeModelAccess().getEnumTypesOseeEnumTypeParserRuleCall_1_3_0()); 
            pushFollow(FOLLOW_ruleOseeEnumType_in_rule__OseeTypeModel__EnumTypesAssignment_1_36218);
            ruleOseeEnumType();
            _fsp--;

             after(grammarAccess.getOseeTypeModelAccess().getEnumTypesOseeEnumTypeParserRuleCall_1_3_0()); 

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
    // $ANTLR end rule__OseeTypeModel__EnumTypesAssignment_1_3


    // $ANTLR start rule__OseeTypeModel__EnumOverridesAssignment_1_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3144:1: rule__OseeTypeModel__EnumOverridesAssignment_1_4 : ( ruleOseeEnumOverride ) ;
    public final void rule__OseeTypeModel__EnumOverridesAssignment_1_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3148:1: ( ( ruleOseeEnumOverride ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3149:1: ( ruleOseeEnumOverride )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3149:1: ( ruleOseeEnumOverride )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3150:1: ruleOseeEnumOverride
            {
             before(grammarAccess.getOseeTypeModelAccess().getEnumOverridesOseeEnumOverrideParserRuleCall_1_4_0()); 
            pushFollow(FOLLOW_ruleOseeEnumOverride_in_rule__OseeTypeModel__EnumOverridesAssignment_1_46249);
            ruleOseeEnumOverride();
            _fsp--;

             after(grammarAccess.getOseeTypeModelAccess().getEnumOverridesOseeEnumOverrideParserRuleCall_1_4_0()); 

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
    // $ANTLR end rule__OseeTypeModel__EnumOverridesAssignment_1_4


    // $ANTLR start rule__Import__ImportURIAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3159:1: rule__Import__ImportURIAssignment_1 : ( RULE_STRING ) ;
    public final void rule__Import__ImportURIAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3163:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3164:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3164:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3165:1: RULE_STRING
            {
             before(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_16280); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3174:1: rule__ArtifactType__AbstractAssignment_0 : ( ( 'abstract' ) ) ;
    public final void rule__ArtifactType__AbstractAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3178:1: ( ( ( 'abstract' ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3179:1: ( ( 'abstract' ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3179:1: ( ( 'abstract' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3180:1: ( 'abstract' )
            {
             before(grammarAccess.getArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3181:1: ( 'abstract' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3182:1: 'abstract'
            {
             before(grammarAccess.getArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            match(input,65,FOLLOW_65_in_rule__ArtifactType__AbstractAssignment_06316); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3197:1: rule__ArtifactType__NameAssignment_2 : ( ruleNAME_REFERENCE ) ;
    public final void rule__ArtifactType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3201:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3202:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3202:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3203:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__NameAssignment_26355);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3212:1: rule__ArtifactType__SuperArtifactTypesAssignment_3_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__ArtifactType__SuperArtifactTypesAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3216:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3217:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3217:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3218:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3219:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3220:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_16390);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3231:1: rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3235:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3236:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3236:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3237:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeCrossReference_3_2_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3238:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3239:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperArtifactTypesArtifactTypeNAME_REFERENCEParserRuleCall_3_2_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_16429);
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


    // $ANTLR start rule__ArtifactType__TypeGuidAssignment_6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3250:1: rule__ArtifactType__TypeGuidAssignment_6 : ( RULE_STRING ) ;
    public final void rule__ArtifactType__TypeGuidAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3254:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3255:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3255:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3256:1: RULE_STRING
            {
             before(grammarAccess.getArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__ArtifactType__TypeGuidAssignment_66464); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3265:1: rule__ArtifactType__ValidAttributeTypesAssignment_7 : ( ruleAttributeTypeRef ) ;
    public final void rule__ArtifactType__ValidAttributeTypesAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3269:1: ( ( ruleAttributeTypeRef ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3270:1: ( ruleAttributeTypeRef )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3270:1: ( ruleAttributeTypeRef )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3271:1: ruleAttributeTypeRef
            {
             before(grammarAccess.getArtifactTypeAccess().getValidAttributeTypesAttributeTypeRefParserRuleCall_7_0()); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_rule__ArtifactType__ValidAttributeTypesAssignment_76495);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3280:1: rule__AttributeTypeRef__ValidAttributeTypeAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__AttributeTypeRef__ValidAttributeTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3284:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3285:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3285:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3286:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeCrossReference_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3287:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3288:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeRefAccess().getValidAttributeTypeAttributeTypeNAME_REFERENCEParserRuleCall_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeTypeRef__ValidAttributeTypeAssignment_16530);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3299:1: rule__AttributeTypeRef__BranchGuidAssignment_2_1 : ( RULE_STRING ) ;
    public final void rule__AttributeTypeRef__BranchGuidAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3303:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3304:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3304:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3305:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeTypeRef__BranchGuidAssignment_2_16565); 
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


    // $ANTLR start rule__AttributeType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3314:1: rule__AttributeType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__AttributeType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3318:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3319:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3319:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3320:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__NameAssignment_16596);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3329:1: rule__AttributeType__BaseAttributeTypeAssignment_2_1 : ( ruleAttributeBaseType ) ;
    public final void rule__AttributeType__BaseAttributeTypeAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3333:1: ( ( ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3334:1: ( ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3334:1: ( ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3335:1: ruleAttributeBaseType
            {
             before(grammarAccess.getAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 
            pushFollow(FOLLOW_ruleAttributeBaseType_in_rule__AttributeType__BaseAttributeTypeAssignment_2_16627);
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


    // $ANTLR start rule__AttributeType__OverrideAssignment_3_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3344:1: rule__AttributeType__OverrideAssignment_3_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__AttributeType__OverrideAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3348:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3349:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3349:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3350:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeCrossReference_3_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3351:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3352:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__OverrideAssignment_3_16662);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 

            }

             after(grammarAccess.getAttributeTypeAccess().getOverrideAttributeTypeCrossReference_3_1_0()); 

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
    // $ANTLR end rule__AttributeType__OverrideAssignment_3_1


    // $ANTLR start rule__AttributeType__TypeGuidAssignment_6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3363:1: rule__AttributeType__TypeGuidAssignment_6 : ( RULE_STRING ) ;
    public final void rule__AttributeType__TypeGuidAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3367:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3368:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3368:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3369:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__TypeGuidAssignment_66697); 
             after(grammarAccess.getAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 

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
    // $ANTLR end rule__AttributeType__TypeGuidAssignment_6


    // $ANTLR start rule__AttributeType__DataProviderAssignment_8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3378:1: rule__AttributeType__DataProviderAssignment_8 : ( ( rule__AttributeType__DataProviderAlternatives_8_0 ) ) ;
    public final void rule__AttributeType__DataProviderAssignment_8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3382:1: ( ( ( rule__AttributeType__DataProviderAlternatives_8_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3383:1: ( ( rule__AttributeType__DataProviderAlternatives_8_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3383:1: ( ( rule__AttributeType__DataProviderAlternatives_8_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3384:1: ( rule__AttributeType__DataProviderAlternatives_8_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getDataProviderAlternatives_8_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3385:1: ( rule__AttributeType__DataProviderAlternatives_8_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3385:2: rule__AttributeType__DataProviderAlternatives_8_0
            {
            pushFollow(FOLLOW_rule__AttributeType__DataProviderAlternatives_8_0_in_rule__AttributeType__DataProviderAssignment_86728);
            rule__AttributeType__DataProviderAlternatives_8_0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getDataProviderAlternatives_8_0()); 

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
    // $ANTLR end rule__AttributeType__DataProviderAssignment_8


    // $ANTLR start rule__AttributeType__MinAssignment_10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3394:1: rule__AttributeType__MinAssignment_10 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__AttributeType__MinAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3398:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3399:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3399:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3400:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_10_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MinAssignment_106761); 
             after(grammarAccess.getAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_10_0()); 

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
    // $ANTLR end rule__AttributeType__MinAssignment_10


    // $ANTLR start rule__AttributeType__MaxAssignment_12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3409:1: rule__AttributeType__MaxAssignment_12 : ( ( rule__AttributeType__MaxAlternatives_12_0 ) ) ;
    public final void rule__AttributeType__MaxAssignment_12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3413:1: ( ( ( rule__AttributeType__MaxAlternatives_12_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3414:1: ( ( rule__AttributeType__MaxAlternatives_12_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3414:1: ( ( rule__AttributeType__MaxAlternatives_12_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3415:1: ( rule__AttributeType__MaxAlternatives_12_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getMaxAlternatives_12_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3416:1: ( rule__AttributeType__MaxAlternatives_12_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3416:2: rule__AttributeType__MaxAlternatives_12_0
            {
            pushFollow(FOLLOW_rule__AttributeType__MaxAlternatives_12_0_in_rule__AttributeType__MaxAssignment_126792);
            rule__AttributeType__MaxAlternatives_12_0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getMaxAlternatives_12_0()); 

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
    // $ANTLR end rule__AttributeType__MaxAssignment_12


    // $ANTLR start rule__AttributeType__TaggerIdAssignment_13_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3425:1: rule__AttributeType__TaggerIdAssignment_13_1 : ( ( rule__AttributeType__TaggerIdAlternatives_13_1_0 ) ) ;
    public final void rule__AttributeType__TaggerIdAssignment_13_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3429:1: ( ( ( rule__AttributeType__TaggerIdAlternatives_13_1_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3430:1: ( ( rule__AttributeType__TaggerIdAlternatives_13_1_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3430:1: ( ( rule__AttributeType__TaggerIdAlternatives_13_1_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3431:1: ( rule__AttributeType__TaggerIdAlternatives_13_1_0 )
            {
             before(grammarAccess.getAttributeTypeAccess().getTaggerIdAlternatives_13_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3432:1: ( rule__AttributeType__TaggerIdAlternatives_13_1_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3432:2: rule__AttributeType__TaggerIdAlternatives_13_1_0
            {
            pushFollow(FOLLOW_rule__AttributeType__TaggerIdAlternatives_13_1_0_in_rule__AttributeType__TaggerIdAssignment_13_16825);
            rule__AttributeType__TaggerIdAlternatives_13_1_0();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getTaggerIdAlternatives_13_1_0()); 

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
    // $ANTLR end rule__AttributeType__TaggerIdAssignment_13_1


    // $ANTLR start rule__AttributeType__EnumTypeAssignment_14_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3441:1: rule__AttributeType__EnumTypeAssignment_14_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__AttributeType__EnumTypeAssignment_14_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3445:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3446:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3446:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3447:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_14_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3448:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3449:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeNAME_REFERENCEParserRuleCall_14_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__EnumTypeAssignment_14_16862);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeNAME_REFERENCEParserRuleCall_14_1_0_1()); 

            }

             after(grammarAccess.getAttributeTypeAccess().getEnumTypeOseeEnumTypeCrossReference_14_1_0()); 

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
    // $ANTLR end rule__AttributeType__EnumTypeAssignment_14_1


    // $ANTLR start rule__AttributeType__DescriptionAssignment_15_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3460:1: rule__AttributeType__DescriptionAssignment_15_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__DescriptionAssignment_15_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3464:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3465:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3465:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3466:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_15_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__DescriptionAssignment_15_16897); 
             after(grammarAccess.getAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_15_1_0()); 

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
    // $ANTLR end rule__AttributeType__DescriptionAssignment_15_1


    // $ANTLR start rule__AttributeType__DefaultValueAssignment_16_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3475:1: rule__AttributeType__DefaultValueAssignment_16_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__DefaultValueAssignment_16_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3479:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3480:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3480:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3481:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_16_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__DefaultValueAssignment_16_16928); 
             after(grammarAccess.getAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_16_1_0()); 

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
    // $ANTLR end rule__AttributeType__DefaultValueAssignment_16_1


    // $ANTLR start rule__AttributeType__FileExtensionAssignment_17_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3490:1: rule__AttributeType__FileExtensionAssignment_17_1 : ( RULE_STRING ) ;
    public final void rule__AttributeType__FileExtensionAssignment_17_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3494:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3495:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3495:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3496:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_17_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AttributeType__FileExtensionAssignment_17_16959); 
             after(grammarAccess.getAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_17_1_0()); 

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
    // $ANTLR end rule__AttributeType__FileExtensionAssignment_17_1


    // $ANTLR start rule__OseeEnumType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3505:1: rule__OseeEnumType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__OseeEnumType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3509:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3510:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3510:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3511:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumType__NameAssignment_16990);
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


    // $ANTLR start rule__OseeEnumType__TypeGuidAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3520:1: rule__OseeEnumType__TypeGuidAssignment_4 : ( RULE_STRING ) ;
    public final void rule__OseeEnumType__TypeGuidAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3524:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3525:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3525:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3526:1: RULE_STRING
            {
             before(grammarAccess.getOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__OseeEnumType__TypeGuidAssignment_47021); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3535:1: rule__OseeEnumType__EnumEntriesAssignment_5 : ( ruleOseeEnumEntry ) ;
    public final void rule__OseeEnumType__EnumEntriesAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3539:1: ( ( ruleOseeEnumEntry ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3540:1: ( ruleOseeEnumEntry )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3540:1: ( ruleOseeEnumEntry )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3541:1: ruleOseeEnumEntry
            {
             before(grammarAccess.getOseeEnumTypeAccess().getEnumEntriesOseeEnumEntryParserRuleCall_5_0()); 
            pushFollow(FOLLOW_ruleOseeEnumEntry_in_rule__OseeEnumType__EnumEntriesAssignment_57052);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3550:1: rule__OseeEnumEntry__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__OseeEnumEntry__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3554:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3555:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3555:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3556:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getOseeEnumEntryAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumEntry__NameAssignment_17083);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getOseeEnumEntryAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3565:1: rule__OseeEnumEntry__OrdinalAssignment_2 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__OseeEnumEntry__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3569:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3570:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3570:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3571:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_rule__OseeEnumEntry__OrdinalAssignment_27114); 
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


    // $ANTLR start rule__OseeEnumEntry__EntryGuidAssignment_3_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3580:1: rule__OseeEnumEntry__EntryGuidAssignment_3_1 : ( RULE_STRING ) ;
    public final void rule__OseeEnumEntry__EntryGuidAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3584:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3585:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3585:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3586:1: RULE_STRING
            {
             before(grammarAccess.getOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__OseeEnumEntry__EntryGuidAssignment_3_17145); 
             after(grammarAccess.getOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 

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
    // $ANTLR end rule__OseeEnumEntry__EntryGuidAssignment_3_1


    // $ANTLR start rule__OseeEnumOverride__OverridenEnumTypeAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3595:1: rule__OseeEnumOverride__OverridenEnumTypeAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__OseeEnumOverride__OverridenEnumTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3599:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3600:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3600:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3601:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getOverridenEnumTypeOseeEnumTypeCrossReference_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3602:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3603:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getOverridenEnumTypeOseeEnumTypeNAME_REFERENCEParserRuleCall_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumOverride__OverridenEnumTypeAssignment_17180);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getOseeEnumOverrideAccess().getOverridenEnumTypeOseeEnumTypeNAME_REFERENCEParserRuleCall_1_0_1()); 

            }

             after(grammarAccess.getOseeEnumOverrideAccess().getOverridenEnumTypeOseeEnumTypeCrossReference_1_0()); 

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
    // $ANTLR end rule__OseeEnumOverride__OverridenEnumTypeAssignment_1


    // $ANTLR start rule__OseeEnumOverride__InheritAllAssignment_3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3614:1: rule__OseeEnumOverride__InheritAllAssignment_3 : ( ( 'inheritAll' ) ) ;
    public final void rule__OseeEnumOverride__InheritAllAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3618:1: ( ( ( 'inheritAll' ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3619:1: ( ( 'inheritAll' ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3619:1: ( ( 'inheritAll' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3620:1: ( 'inheritAll' )
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3621:1: ( 'inheritAll' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3622:1: 'inheritAll'
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            match(input,66,FOLLOW_66_in_rule__OseeEnumOverride__InheritAllAssignment_37220); 
             after(grammarAccess.getOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 

            }

             after(grammarAccess.getOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 

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
    // $ANTLR end rule__OseeEnumOverride__InheritAllAssignment_3


    // $ANTLR start rule__OseeEnumOverride__OverrideOptionsAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3637:1: rule__OseeEnumOverride__OverrideOptionsAssignment_4 : ( ruleOverrideOption ) ;
    public final void rule__OseeEnumOverride__OverrideOptionsAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3641:1: ( ( ruleOverrideOption ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3642:1: ( ruleOverrideOption )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3642:1: ( ruleOverrideOption )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3643:1: ruleOverrideOption
            {
             before(grammarAccess.getOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 
            pushFollow(FOLLOW_ruleOverrideOption_in_rule__OseeEnumOverride__OverrideOptionsAssignment_47259);
            ruleOverrideOption();
            _fsp--;

             after(grammarAccess.getOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 

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
    // $ANTLR end rule__OseeEnumOverride__OverrideOptionsAssignment_4


    // $ANTLR start rule__AddEnum__EnumEntryAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3652:1: rule__AddEnum__EnumEntryAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__AddEnum__EnumEntryAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3656:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3657:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3657:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3658:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getAddEnumAccess().getEnumEntryNAME_REFERENCEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__AddEnum__EnumEntryAssignment_17290);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getAddEnumAccess().getEnumEntryNAME_REFERENCEParserRuleCall_1_0()); 

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
    // $ANTLR end rule__AddEnum__EnumEntryAssignment_1


    // $ANTLR start rule__AddEnum__OrdinalAssignment_2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3667:1: rule__AddEnum__OrdinalAssignment_2 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__AddEnum__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3671:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3672:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3672:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3673:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AddEnum__OrdinalAssignment_27321); 
             after(grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 

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
    // $ANTLR end rule__AddEnum__OrdinalAssignment_2


    // $ANTLR start rule__AddEnum__EntryGuidAssignment_3_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3682:1: rule__AddEnum__EntryGuidAssignment_3_1 : ( RULE_STRING ) ;
    public final void rule__AddEnum__EntryGuidAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3686:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3687:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3687:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3688:1: RULE_STRING
            {
             before(grammarAccess.getAddEnumAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__AddEnum__EntryGuidAssignment_3_17352); 
             after(grammarAccess.getAddEnumAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 

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
    // $ANTLR end rule__AddEnum__EntryGuidAssignment_3_1


    // $ANTLR start rule__RemoveEnum__EnumEntryAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3697:1: rule__RemoveEnum__EnumEntryAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RemoveEnum__EnumEntryAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3701:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3702:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3702:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3703:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getRemoveEnumAccess().getEnumEntryOseeEnumEntryCrossReference_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3704:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3705:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRemoveEnumAccess().getEnumEntryOseeEnumEntryNAME_REFERENCEParserRuleCall_1_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RemoveEnum__EnumEntryAssignment_17387);
            ruleNAME_REFERENCE();
            _fsp--;

             after(grammarAccess.getRemoveEnumAccess().getEnumEntryOseeEnumEntryNAME_REFERENCEParserRuleCall_1_0_1()); 

            }

             after(grammarAccess.getRemoveEnumAccess().getEnumEntryOseeEnumEntryCrossReference_1_0()); 

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
    // $ANTLR end rule__RemoveEnum__EnumEntryAssignment_1


    // $ANTLR start rule__RelationType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3716:1: rule__RelationType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__RelationType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3720:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3721:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3721:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3722:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__NameAssignment_17422);
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


    // $ANTLR start rule__RelationType__TypeGuidAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3731:1: rule__RelationType__TypeGuidAssignment_4 : ( RULE_STRING ) ;
    public final void rule__RelationType__TypeGuidAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3735:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3736:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3736:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3737:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__TypeGuidAssignment_47453); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3746:1: rule__RelationType__SideANameAssignment_6 : ( RULE_STRING ) ;
    public final void rule__RelationType__SideANameAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3750:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3751:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3751:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3752:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_6_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__SideANameAssignment_67484); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3761:1: rule__RelationType__SideAArtifactTypeAssignment_8 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RelationType__SideAArtifactTypeAssignment_8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3765:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3766:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3766:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3767:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeCrossReference_8_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3768:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3769:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getSideAArtifactTypeArtifactTypeNAME_REFERENCEParserRuleCall_8_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideAArtifactTypeAssignment_87519);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3780:1: rule__RelationType__SideBNameAssignment_10 : ( RULE_STRING ) ;
    public final void rule__RelationType__SideBNameAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3784:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3785:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3785:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3786:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_10_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__RelationType__SideBNameAssignment_107554); 
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3795:1: rule__RelationType__SideBArtifactTypeAssignment_12 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RelationType__SideBArtifactTypeAssignment_12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3799:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3800:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3800:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3801:1: ( ruleNAME_REFERENCE )
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeCrossReference_12_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3802:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3803:1: ruleNAME_REFERENCE
            {
             before(grammarAccess.getRelationTypeAccess().getSideBArtifactTypeArtifactTypeNAME_REFERENCEParserRuleCall_12_0_1()); 
            pushFollow(FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideBArtifactTypeAssignment_127589);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3814:1: rule__RelationType__DefaultOrderTypeAssignment_14 : ( ruleRelationOrderType ) ;
    public final void rule__RelationType__DefaultOrderTypeAssignment_14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3818:1: ( ( ruleRelationOrderType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3819:1: ( ruleRelationOrderType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3819:1: ( ruleRelationOrderType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3820:1: ruleRelationOrderType
            {
             before(grammarAccess.getRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_14_0()); 
            pushFollow(FOLLOW_ruleRelationOrderType_in_rule__RelationType__DefaultOrderTypeAssignment_147624);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3829:1: rule__RelationType__MultiplicityAssignment_16 : ( ruleRelationMultiplicityEnum ) ;
    public final void rule__RelationType__MultiplicityAssignment_16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3833:1: ( ( ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:1: ( ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3834:1: ( ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:3835:1: ruleRelationMultiplicityEnum
            {
             before(grammarAccess.getRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0()); 
            pushFollow(FOLLOW_ruleRelationMultiplicityEnum_in_rule__RelationType__MultiplicityAssignment_167655);
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


 

    public static final BitSet FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel61 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeTypeModel68 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__Group__0_in_ruleOseeTypeModel95 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport122 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Import__Group__0_in_ruleImport156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE183 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNAME_REFERENCE190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME243 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__0_in_ruleQUALIFIED_NAME277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType306 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeType313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeType__Alternatives_in_ruleOseeType340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_entryRuleArtifactType367 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactType374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__0_in_ruleArtifactType401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef428 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRef435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__0_in_ruleAttributeTypeRef462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_entryRuleAttributeType489 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeType496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__0_in_ruleAttributeType523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType550 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeBaseType__Alternatives_in_ruleAttributeBaseType584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_entryRuleOseeEnumType611 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumType618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__0_in_ruleOseeEnumType645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_entryRuleOseeEnumEntry672 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumEntry679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__0_in_ruleOseeEnumEntry706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumOverride_in_entryRuleOseeEnumOverride733 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOseeEnumOverride740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumOverride__Group__0_in_ruleOseeEnumOverride767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption794 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOverrideOption801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OverrideOption__Alternatives_in_ruleOverrideOption828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_entryRuleAddEnum855 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddEnum862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AddEnum__Group__0_in_ruleAddEnum889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum916 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRemoveEnum923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RemoveEnum__Group__0_in_ruleRemoveEnum950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_entryRuleRelationType977 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationType984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__Group__0_in_ruleRelationType1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType1038 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationOrderType1045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationOrderType__Alternatives_in_ruleRelationOrderType1072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationMultiplicityEnum__Alternatives_in_ruleRelationMultiplicityEnum1109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__ArtifactTypesAssignment_1_0_in_rule__OseeTypeModel__Alternatives_11144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__RelationTypesAssignment_1_1_in_rule__OseeTypeModel__Alternatives_11162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__AttributeTypesAssignment_1_2_in_rule__OseeTypeModel__Alternatives_11180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__EnumTypesAssignment_1_3_in_rule__OseeTypeModel__Alternatives_11198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__EnumOverridesAssignment_1_4_in_rule__OseeTypeModel__Alternatives_11216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_rule__OseeType__Alternatives1250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_rule__OseeType__Alternatives1267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_rule__OseeType__Alternatives1284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_rule__OseeType__Alternatives1301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_rule__AttributeType__DataProviderAlternatives_8_01334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_rule__AttributeType__DataProviderAlternatives_8_01354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__DataProviderAlternatives_8_01373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MaxAlternatives_12_01405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_rule__AttributeType__MaxAlternatives_12_01423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_rule__AttributeType__TaggerIdAlternatives_13_1_01458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeType__TaggerIdAlternatives_13_1_01477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_rule__AttributeBaseType__Alternatives1510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_rule__AttributeBaseType__Alternatives1530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_rule__AttributeBaseType__Alternatives1550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_rule__AttributeBaseType__Alternatives1570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_rule__AttributeBaseType__Alternatives1590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_rule__AttributeBaseType__Alternatives1610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_rule__AttributeBaseType__Alternatives1630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_rule__AttributeBaseType__Alternatives1650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_rule__AttributeBaseType__Alternatives1670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeBaseType__Alternatives1689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddEnum_in_rule__OverrideOption__Alternatives1721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRemoveEnum_in_rule__OverrideOption__Alternatives1738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_rule__RelationOrderType__Alternatives1771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_rule__RelationOrderType__Alternatives1791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_rule__RelationOrderType__Alternatives1811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__RelationOrderType__Alternatives1830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_rule__RelationMultiplicityEnum__Alternatives1863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_rule__RelationMultiplicityEnum__Alternatives1884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_rule__RelationMultiplicityEnum__Alternatives1905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_rule__RelationMultiplicityEnum__Alternatives1926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__01963 = new BitSet(new long[]{0x0490040500000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeTypeModel__Alternatives_1_in_rule__OseeTypeModel__Group__12001 = new BitSet(new long[]{0x0490040400000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_rule__Import__Group__02041 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Import__Group__1_in_rule__Import__Group__02051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__12079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__02117 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__02125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__12153 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_33_in_rule__QUALIFIED_NAME__Group_1__02193 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__02203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__12231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__AbstractAssignment_0_in_rule__ArtifactType__Group__02268 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__1_in_rule__ArtifactType__Group__02278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_rule__ArtifactType__Group__12307 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__2_in_rule__ArtifactType__Group__12317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__NameAssignment_2_in_rule__ArtifactType__Group__22345 = new BitSet(new long[]{0x0000004800000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__3_in_rule__ArtifactType__Group__22354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__0_in_rule__ArtifactType__Group__32382 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__4_in_rule__ArtifactType__Group__32392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_rule__ArtifactType__Group__42421 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__5_in_rule__ArtifactType__Group__42431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__ArtifactType__Group__52460 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__6_in_rule__ArtifactType__Group__52470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__TypeGuidAssignment_6_in_rule__ArtifactType__Group__62498 = new BitSet(new long[]{0x0000012000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__7_in_rule__ArtifactType__Group__62507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__ValidAttributeTypesAssignment_7_in_rule__ArtifactType__Group__72535 = new BitSet(new long[]{0x0000012000000000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__8_in_rule__ArtifactType__Group__72545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__ArtifactType__Group__82574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_rule__ArtifactType__Group_3__02628 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__1_in_rule__ArtifactType__Group_3__02638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_1_in_rule__ArtifactType__Group_3__12666 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3__2_in_rule__ArtifactType__Group_3__12675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3_2__0_in_rule__ArtifactType__Group_3__22703 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_39_in_rule__ArtifactType__Group_3_2__02745 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_3_2__1_in_rule__ArtifactType__Group_3_2__02755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_1_in_rule__ArtifactType__Group_3_2__12783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule__AttributeTypeRef__Group__02822 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__1_in_rule__AttributeTypeRef__Group__02832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__AttributeTypeRef__Group__12860 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__2_in_rule__AttributeTypeRef__Group__12869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group_2__0_in_rule__AttributeTypeRef__Group__22897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_rule__AttributeTypeRef__Group_2__02939 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group_2__1_in_rule__AttributeTypeRef__Group_2__02949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__BranchGuidAssignment_2_1_in_rule__AttributeTypeRef__Group_2__12977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_rule__AttributeType__Group__03016 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__1_in_rule__AttributeType__Group__03026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__NameAssignment_1_in_rule__AttributeType__Group__13054 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__2_in_rule__AttributeType__Group__13063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_2__0_in_rule__AttributeType__Group__23091 = new BitSet(new long[]{0x0000400800000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__3_in_rule__AttributeType__Group__23100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_3__0_in_rule__AttributeType__Group__33128 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__4_in_rule__AttributeType__Group__33138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_rule__AttributeType__Group__43167 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__5_in_rule__AttributeType__Group__43177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__AttributeType__Group__53206 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__6_in_rule__AttributeType__Group__53216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TypeGuidAssignment_6_in_rule__AttributeType__Group__63244 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__7_in_rule__AttributeType__Group__63253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_rule__AttributeType__Group__73282 = new BitSet(new long[]{0x0000000000003040L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__8_in_rule__AttributeType__Group__73292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DataProviderAssignment_8_in_rule__AttributeType__Group__83320 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__9_in_rule__AttributeType__Group__83329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_rule__AttributeType__Group__93358 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__10_in_rule__AttributeType__Group__93368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MinAssignment_10_in_rule__AttributeType__Group__103396 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__11_in_rule__AttributeType__Group__103405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_rule__AttributeType__Group__113434 = new BitSet(new long[]{0x0000000000004020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__12_in_rule__AttributeType__Group__113444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MaxAssignment_12_in_rule__AttributeType__Group__123472 = new BitSet(new long[]{0x000F802000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__13_in_rule__AttributeType__Group__123481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_13__0_in_rule__AttributeType__Group__133509 = new BitSet(new long[]{0x000F002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__14_in_rule__AttributeType__Group__133519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_14__0_in_rule__AttributeType__Group__143547 = new BitSet(new long[]{0x000E002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__15_in_rule__AttributeType__Group__143557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_15__0_in_rule__AttributeType__Group__153585 = new BitSet(new long[]{0x000C002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__16_in_rule__AttributeType__Group__153595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_16__0_in_rule__AttributeType__Group__163623 = new BitSet(new long[]{0x0008002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__17_in_rule__AttributeType__Group__163633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_17__0_in_rule__AttributeType__Group__173661 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__18_in_rule__AttributeType__Group__173671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__AttributeType__Group__183700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_rule__AttributeType__Group_2__03774 = new BitSet(new long[]{0x0000000001FF0040L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_2__1_in_rule__AttributeType__Group_2__03784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__BaseAttributeTypeAssignment_2_1_in_rule__AttributeType__Group_2__13812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_rule__AttributeType__Group_3__03851 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_3__1_in_rule__AttributeType__Group_3__03861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__OverrideAssignment_3_1_in_rule__AttributeType__Group_3__13889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_rule__AttributeType__Group_13__03928 = new BitSet(new long[]{0x0000000000008040L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_13__1_in_rule__AttributeType__Group_13__03938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TaggerIdAssignment_13_1_in_rule__AttributeType__Group_13__13966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_rule__AttributeType__Group_14__04005 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_14__1_in_rule__AttributeType__Group_14__04015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__EnumTypeAssignment_14_1_in_rule__AttributeType__Group_14__14043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_rule__AttributeType__Group_15__04082 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_15__1_in_rule__AttributeType__Group_15__04092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DescriptionAssignment_15_1_in_rule__AttributeType__Group_15__14120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_rule__AttributeType__Group_16__04159 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_16__1_in_rule__AttributeType__Group_16__04169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DefaultValueAssignment_16_1_in_rule__AttributeType__Group_16__14197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_rule__AttributeType__Group_17__04236 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_17__1_in_rule__AttributeType__Group_17__04246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__FileExtensionAssignment_17_1_in_rule__AttributeType__Group_17__14274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_rule__OseeEnumType__Group__04313 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__1_in_rule__OseeEnumType__Group__04323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__NameAssignment_1_in_rule__OseeEnumType__Group__14351 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__2_in_rule__OseeEnumType__Group__14360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_rule__OseeEnumType__Group__24389 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__3_in_rule__OseeEnumType__Group__24399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__OseeEnumType__Group__34428 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__4_in_rule__OseeEnumType__Group__34438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__TypeGuidAssignment_4_in_rule__OseeEnumType__Group__44466 = new BitSet(new long[]{0x0020002000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__5_in_rule__OseeEnumType__Group__44475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumType__EnumEntriesAssignment_5_in_rule__OseeEnumType__Group__54503 = new BitSet(new long[]{0x0020002000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumType__Group__6_in_rule__OseeEnumType__Group__54513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__OseeEnumType__Group__64542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_rule__OseeEnumEntry__Group__04592 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__1_in_rule__OseeEnumEntry__Group__04602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__NameAssignment_1_in_rule__OseeEnumEntry__Group__14630 = new BitSet(new long[]{0x0040000000000022L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__2_in_rule__OseeEnumEntry__Group__14639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__OrdinalAssignment_2_in_rule__OseeEnumEntry__Group__24667 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group__3_in_rule__OseeEnumEntry__Group__24677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group_3__0_in_rule__OseeEnumEntry__Group__34705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_rule__OseeEnumEntry__Group_3__04749 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__Group_3__1_in_rule__OseeEnumEntry__Group_3__04759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumEntry__EntryGuidAssignment_3_1_in_rule__OseeEnumEntry__Group_3__14787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_rule__OseeEnumOverride__Group__04826 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__OseeEnumOverride__Group__1_in_rule__OseeEnumOverride__Group__04836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumOverride__OverridenEnumTypeAssignment_1_in_rule__OseeEnumOverride__Group__14864 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_rule__OseeEnumOverride__Group__2_in_rule__OseeEnumOverride__Group__14873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_rule__OseeEnumOverride__Group__24902 = new BitSet(new long[]{0x0300002000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_rule__OseeEnumOverride__Group__3_in_rule__OseeEnumOverride__Group__24912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumOverride__InheritAllAssignment_3_in_rule__OseeEnumOverride__Group__34940 = new BitSet(new long[]{0x0300002000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumOverride__Group__4_in_rule__OseeEnumOverride__Group__34950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__OseeEnumOverride__OverrideOptionsAssignment_4_in_rule__OseeEnumOverride__Group__44978 = new BitSet(new long[]{0x0300002000000000L});
    public static final BitSet FOLLOW_rule__OseeEnumOverride__Group__5_in_rule__OseeEnumOverride__Group__44988 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__OseeEnumOverride__Group__55017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_rule__AddEnum__Group__05065 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AddEnum__Group__1_in_rule__AddEnum__Group__05075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AddEnum__EnumEntryAssignment_1_in_rule__AddEnum__Group__15103 = new BitSet(new long[]{0x0040000000000022L});
    public static final BitSet FOLLOW_rule__AddEnum__Group__2_in_rule__AddEnum__Group__15112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AddEnum__OrdinalAssignment_2_in_rule__AddEnum__Group__25140 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_rule__AddEnum__Group__3_in_rule__AddEnum__Group__25150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AddEnum__Group_3__0_in_rule__AddEnum__Group__35178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_rule__AddEnum__Group_3__05222 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__AddEnum__Group_3__1_in_rule__AddEnum__Group_3__05232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AddEnum__EntryGuidAssignment_3_1_in_rule__AddEnum__Group_3__15260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_rule__RemoveEnum__Group__05299 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RemoveEnum__Group__1_in_rule__RemoveEnum__Group__05309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RemoveEnum__EnumEntryAssignment_1_in_rule__RemoveEnum__Group__15337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_rule__RelationType__Group__05376 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__1_in_rule__RelationType__Group__05386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__NameAssignment_1_in_rule__RelationType__Group__15414 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__2_in_rule__RelationType__Group__15423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_rule__RelationType__Group__25452 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__3_in_rule__RelationType__Group__25462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule__RelationType__Group__35491 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__4_in_rule__RelationType__Group__35501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__TypeGuidAssignment_4_in_rule__RelationType__Group__45529 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__5_in_rule__RelationType__Group__45538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_rule__RelationType__Group__55567 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__6_in_rule__RelationType__Group__55577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideANameAssignment_6_in_rule__RelationType__Group__65605 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__7_in_rule__RelationType__Group__65614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_rule__RelationType__Group__75643 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__8_in_rule__RelationType__Group__75653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideAArtifactTypeAssignment_8_in_rule__RelationType__Group__85681 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__9_in_rule__RelationType__Group__85690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_rule__RelationType__Group__95719 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__10_in_rule__RelationType__Group__95729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideBNameAssignment_10_in_rule__RelationType__Group__105757 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__11_in_rule__RelationType__Group__105766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_rule__RelationType__Group__115795 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__RelationType__Group__12_in_rule__RelationType__Group__115805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__SideBArtifactTypeAssignment_12_in_rule__RelationType__Group__125833 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__13_in_rule__RelationType__Group__125842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_rule__RelationType__Group__135871 = new BitSet(new long[]{0x000000000E000040L});
    public static final BitSet FOLLOW_rule__RelationType__Group__14_in_rule__RelationType__Group__135881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__DefaultOrderTypeAssignment_14_in_rule__RelationType__Group__145909 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rule__RelationType__Group__15_in_rule__RelationType__Group__145918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_rule__RelationType__Group__155947 = new BitSet(new long[]{0x00000000F0000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__16_in_rule__RelationType__Group__155957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__MultiplicityAssignment_16_in_rule__RelationType__Group__165985 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__17_in_rule__RelationType__Group__165994 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule__RelationType__Group__176023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_06094 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_rule__OseeTypeModel__ArtifactTypesAssignment_1_06125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_rule__OseeTypeModel__RelationTypesAssignment_1_16156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_rule__OseeTypeModel__AttributeTypesAssignment_1_26187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumType_in_rule__OseeTypeModel__EnumTypesAssignment_1_36218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumOverride_in_rule__OseeTypeModel__EnumOverridesAssignment_1_46249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_16280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_rule__ArtifactType__AbstractAssignment_06316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__NameAssignment_26355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_16390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__ArtifactType__SuperArtifactTypesAssignment_3_2_16429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__ArtifactType__TypeGuidAssignment_66464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_rule__ArtifactType__ValidAttributeTypesAssignment_76495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeTypeRef__ValidAttributeTypeAssignment_16530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeTypeRef__BranchGuidAssignment_2_16565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__NameAssignment_16596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeBaseType_in_rule__AttributeType__BaseAttributeTypeAssignment_2_16627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__OverrideAssignment_3_16662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__TypeGuidAssignment_66697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__DataProviderAlternatives_8_0_in_rule__AttributeType__DataProviderAssignment_86728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AttributeType__MinAssignment_106761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__MaxAlternatives_12_0_in_rule__AttributeType__MaxAssignment_126792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__TaggerIdAlternatives_13_1_0_in_rule__AttributeType__TaggerIdAssignment_13_16825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AttributeType__EnumTypeAssignment_14_16862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__DescriptionAssignment_15_16897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__DefaultValueAssignment_16_16928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AttributeType__FileExtensionAssignment_17_16959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumType__NameAssignment_16990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__OseeEnumType__TypeGuidAssignment_47021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOseeEnumEntry_in_rule__OseeEnumType__EnumEntriesAssignment_57052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumEntry__NameAssignment_17083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__OseeEnumEntry__OrdinalAssignment_27114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__OseeEnumEntry__EntryGuidAssignment_3_17145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__OseeEnumOverride__OverridenEnumTypeAssignment_17180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_rule__OseeEnumOverride__InheritAllAssignment_37220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOverrideOption_in_rule__OseeEnumOverride__OverrideOptionsAssignment_47259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AddEnum__EnumEntryAssignment_17290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AddEnum__OrdinalAssignment_27321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__AddEnum__EntryGuidAssignment_3_17352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RemoveEnum__EnumEntryAssignment_17387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__NameAssignment_17422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__TypeGuidAssignment_47453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__SideANameAssignment_67484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideAArtifactTypeAssignment_87519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__RelationType__SideBNameAssignment_107554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RelationType__SideBArtifactTypeAssignment_127589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationOrderType_in_rule__RelationType__DefaultOrderTypeAssignment_147624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_rule__RelationType__MultiplicityAssignment_167655 = new BitSet(new long[]{0x0000000000000002L});

}