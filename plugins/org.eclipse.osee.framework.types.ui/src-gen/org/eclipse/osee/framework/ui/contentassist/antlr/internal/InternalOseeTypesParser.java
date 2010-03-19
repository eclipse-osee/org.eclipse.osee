package org.eclipse.osee.framework.ui.contentassist.antlr.internal; 

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.xtext.parsetree.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import org.eclipse.osee.framework.services.OseeTypesGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
            ruleMemo = new HashMap[245+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g"; }


     
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:61:1: entryRuleOseeTypeModel : ruleOseeTypeModel EOF ;
    public final void entryRuleOseeTypeModel() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:62:1: ( ruleOseeTypeModel EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:63:1: ruleOseeTypeModel EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel67);
            ruleOseeTypeModel();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeModelRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleOseeTypeModel74); if (failed) return ;

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:70:1: ruleOseeTypeModel : ( ( rule__OseeTypeModel__Group__0 ) ) ;
    public final void ruleOseeTypeModel() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:74:2: ( ( ( rule__OseeTypeModel__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:75:1: ( ( rule__OseeTypeModel__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:75:1: ( ( rule__OseeTypeModel__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:76:1: ( rule__OseeTypeModel__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:77:1: ( rule__OseeTypeModel__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:77:2: rule__OseeTypeModel__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__Group__0_in_ruleOseeTypeModel100);
            rule__OseeTypeModel__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeModelAccess().getGroup()); 
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
    // $ANTLR end ruleOseeTypeModel


    // $ANTLR start entryRuleImport
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:89:1: entryRuleImport : ruleImport EOF ;
    public final void entryRuleImport() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:90:1: ( ruleImport EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:91:1: ruleImport EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getImportRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleImport_in_entryRuleImport127);
            ruleImport();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getImportRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleImport134); if (failed) return ;

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:98:1: ruleImport : ( ( rule__Import__Group__0 ) ) ;
    public final void ruleImport() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:102:2: ( ( ( rule__Import__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:103:1: ( ( rule__Import__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:103:1: ( ( rule__Import__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:104:1: ( rule__Import__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getImportAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:105:1: ( rule__Import__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:105:2: rule__Import__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__Import__Group__0_in_ruleImport160);
            rule__Import__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getImportAccess().getGroup()); 
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
    // $ANTLR end ruleImport


    // $ANTLR start entryRuleNAME_REFERENCE
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:117:1: entryRuleNAME_REFERENCE : ruleNAME_REFERENCE EOF ;
    public final void entryRuleNAME_REFERENCE() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:118:1: ( ruleNAME_REFERENCE EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:119:1: ruleNAME_REFERENCE EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getNAME_REFERENCERule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE187);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getNAME_REFERENCERule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleNAME_REFERENCE194); if (failed) return ;

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:126:1: ruleNAME_REFERENCE : ( RULE_STRING ) ;
    public final void ruleNAME_REFERENCE() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:130:2: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:131:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:131:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:132:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getNAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE220); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getNAME_REFERENCEAccess().getSTRINGTerminalRuleCall()); 
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
    // $ANTLR end ruleNAME_REFERENCE


    // $ANTLR start entryRuleQUALIFIED_NAME
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:145:1: entryRuleQUALIFIED_NAME : ruleQUALIFIED_NAME EOF ;
    public final void entryRuleQUALIFIED_NAME() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:146:1: ( ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:147:1: ruleQUALIFIED_NAME EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMERule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME246);
            ruleQUALIFIED_NAME();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getQUALIFIED_NAMERule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleQUALIFIED_NAME253); if (failed) return ;

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:154:1: ruleQUALIFIED_NAME : ( ( rule__QUALIFIED_NAME__Group__0 ) ) ;
    public final void ruleQUALIFIED_NAME() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:158:2: ( ( ( rule__QUALIFIED_NAME__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:159:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:159:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:160:1: ( rule__QUALIFIED_NAME__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:161:1: ( rule__QUALIFIED_NAME__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:161:2: rule__QUALIFIED_NAME__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group__0_in_ruleQUALIFIED_NAME279);
            rule__QUALIFIED_NAME__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getQUALIFIED_NAMEAccess().getGroup()); 
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
    // $ANTLR end ruleQUALIFIED_NAME


    // $ANTLR start entryRuleOseeType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:175:1: entryRuleOseeType : ruleOseeType EOF ;
    public final void entryRuleOseeType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:176:1: ( ruleOseeType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:177:1: ruleOseeType EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleOseeType_in_entryRuleOseeType308);
            ruleOseeType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleOseeType315); if (failed) return ;

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:184:1: ruleOseeType : ( ( rule__OseeType__Alternatives ) ) ;
    public final void ruleOseeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:188:2: ( ( ( rule__OseeType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:189:1: ( ( rule__OseeType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:189:1: ( ( rule__OseeType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:190:1: ( rule__OseeType__Alternatives )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeAccess().getAlternatives()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:191:1: ( rule__OseeType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:191:2: rule__OseeType__Alternatives
            {
            pushFollow(FollowSets000.FOLLOW_rule__OseeType__Alternatives_in_ruleOseeType341);
            rule__OseeType__Alternatives();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeAccess().getAlternatives()); 
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
    // $ANTLR end ruleOseeType


    // $ANTLR start entryRuleXArtifactType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:203:1: entryRuleXArtifactType : ruleXArtifactType EOF ;
    public final void entryRuleXArtifactType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:204:1: ( ruleXArtifactType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:205:1: ruleXArtifactType EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXArtifactType_in_entryRuleXArtifactType368);
            ruleXArtifactType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleXArtifactType375); if (failed) return ;

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
    // $ANTLR end entryRuleXArtifactType


    // $ANTLR start ruleXArtifactType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:212:1: ruleXArtifactType : ( ( rule__XArtifactType__Group__0 ) ) ;
    public final void ruleXArtifactType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:216:2: ( ( ( rule__XArtifactType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:217:1: ( ( rule__XArtifactType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:217:1: ( ( rule__XArtifactType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:218:1: ( rule__XArtifactType__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:219:1: ( rule__XArtifactType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:219:2: rule__XArtifactType__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__0_in_ruleXArtifactType401);
            rule__XArtifactType__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getGroup()); 
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
    // $ANTLR end ruleXArtifactType


    // $ANTLR start entryRuleXAttributeTypeRef
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:231:1: entryRuleXAttributeTypeRef : ruleXAttributeTypeRef EOF ;
    public final void entryRuleXAttributeTypeRef() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:232:1: ( ruleXAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:233:1: ruleXAttributeTypeRef EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef428);
            ruleXAttributeTypeRef();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleXAttributeTypeRef435); if (failed) return ;

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
    // $ANTLR end entryRuleXAttributeTypeRef


    // $ANTLR start ruleXAttributeTypeRef
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:240:1: ruleXAttributeTypeRef : ( ( rule__XAttributeTypeRef__Group__0 ) ) ;
    public final void ruleXAttributeTypeRef() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:244:2: ( ( ( rule__XAttributeTypeRef__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:245:1: ( ( rule__XAttributeTypeRef__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:245:1: ( ( rule__XAttributeTypeRef__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:246:1: ( rule__XAttributeTypeRef__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:247:1: ( rule__XAttributeTypeRef__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:247:2: rule__XAttributeTypeRef__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group__0_in_ruleXAttributeTypeRef461);
            rule__XAttributeTypeRef__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getGroup()); 
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
    // $ANTLR end ruleXAttributeTypeRef


    // $ANTLR start entryRuleXAttributeType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:259:1: entryRuleXAttributeType : ruleXAttributeType EOF ;
    public final void entryRuleXAttributeType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:260:1: ( ruleXAttributeType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:261:1: ruleXAttributeType EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType488);
            ruleXAttributeType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleXAttributeType495); if (failed) return ;

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
    // $ANTLR end entryRuleXAttributeType


    // $ANTLR start ruleXAttributeType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:268:1: ruleXAttributeType : ( ( rule__XAttributeType__Group__0 ) ) ;
    public final void ruleXAttributeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:272:2: ( ( ( rule__XAttributeType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:273:1: ( ( rule__XAttributeType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:273:1: ( ( rule__XAttributeType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:274:1: ( rule__XAttributeType__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:275:1: ( rule__XAttributeType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:275:2: rule__XAttributeType__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__0_in_ruleXAttributeType521);
            rule__XAttributeType__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGroup()); 
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
    // $ANTLR end ruleXAttributeType


    // $ANTLR start entryRuleAttributeBaseType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:287:1: entryRuleAttributeBaseType : ruleAttributeBaseType EOF ;
    public final void entryRuleAttributeBaseType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:288:1: ( ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:289:1: ruleAttributeBaseType EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAttributeBaseTypeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType548);
            ruleAttributeBaseType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getAttributeBaseTypeRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleAttributeBaseType555); if (failed) return ;

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:296:1: ruleAttributeBaseType : ( ( rule__AttributeBaseType__Alternatives ) ) ;
    public final void ruleAttributeBaseType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:300:2: ( ( ( rule__AttributeBaseType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:301:1: ( ( rule__AttributeBaseType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:301:1: ( ( rule__AttributeBaseType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:302:1: ( rule__AttributeBaseType__Alternatives )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAttributeBaseTypeAccess().getAlternatives()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:303:1: ( rule__AttributeBaseType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:303:2: rule__AttributeBaseType__Alternatives
            {
            pushFollow(FollowSets000.FOLLOW_rule__AttributeBaseType__Alternatives_in_ruleAttributeBaseType581);
            rule__AttributeBaseType__Alternatives();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getAttributeBaseTypeAccess().getAlternatives()); 
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
    // $ANTLR end ruleAttributeBaseType


    // $ANTLR start entryRuleXOseeEnumType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:315:1: entryRuleXOseeEnumType : ruleXOseeEnumType EOF ;
    public final void entryRuleXOseeEnumType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:316:1: ( ruleXOseeEnumType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:317:1: ruleXOseeEnumType EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType608);
            ruleXOseeEnumType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleXOseeEnumType615); if (failed) return ;

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
    // $ANTLR end entryRuleXOseeEnumType


    // $ANTLR start ruleXOseeEnumType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:324:1: ruleXOseeEnumType : ( ( rule__XOseeEnumType__Group__0 ) ) ;
    public final void ruleXOseeEnumType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:328:2: ( ( ( rule__XOseeEnumType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:329:1: ( ( rule__XOseeEnumType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:329:1: ( ( rule__XOseeEnumType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:330:1: ( rule__XOseeEnumType__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:331:1: ( rule__XOseeEnumType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:331:2: rule__XOseeEnumType__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__0_in_ruleXOseeEnumType641);
            rule__XOseeEnumType__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getGroup()); 
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
    // $ANTLR end ruleXOseeEnumType


    // $ANTLR start entryRuleXOseeEnumEntry
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:343:1: entryRuleXOseeEnumEntry : ruleXOseeEnumEntry EOF ;
    public final void entryRuleXOseeEnumEntry() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:344:1: ( ruleXOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:345:1: ruleXOseeEnumEntry EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry668);
            ruleXOseeEnumEntry();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleXOseeEnumEntry675); if (failed) return ;

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
    // $ANTLR end entryRuleXOseeEnumEntry


    // $ANTLR start ruleXOseeEnumEntry
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:352:1: ruleXOseeEnumEntry : ( ( rule__XOseeEnumEntry__Group__0 ) ) ;
    public final void ruleXOseeEnumEntry() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:356:2: ( ( ( rule__XOseeEnumEntry__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:357:1: ( ( rule__XOseeEnumEntry__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:357:1: ( ( rule__XOseeEnumEntry__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:358:1: ( rule__XOseeEnumEntry__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:359:1: ( rule__XOseeEnumEntry__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:359:2: rule__XOseeEnumEntry__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__0_in_ruleXOseeEnumEntry701);
            rule__XOseeEnumEntry__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getGroup()); 
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
    // $ANTLR end ruleXOseeEnumEntry


    // $ANTLR start entryRuleXOseeEnumOverride
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:371:1: entryRuleXOseeEnumOverride : ruleXOseeEnumOverride EOF ;
    public final void entryRuleXOseeEnumOverride() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:372:1: ( ruleXOseeEnumOverride EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:373:1: ruleXOseeEnumOverride EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride728);
            ruleXOseeEnumOverride();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleXOseeEnumOverride735); if (failed) return ;

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
    // $ANTLR end entryRuleXOseeEnumOverride


    // $ANTLR start ruleXOseeEnumOverride
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:380:1: ruleXOseeEnumOverride : ( ( rule__XOseeEnumOverride__Group__0 ) ) ;
    public final void ruleXOseeEnumOverride() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:384:2: ( ( ( rule__XOseeEnumOverride__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:385:1: ( ( rule__XOseeEnumOverride__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:385:1: ( ( rule__XOseeEnumOverride__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:386:1: ( rule__XOseeEnumOverride__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:387:1: ( rule__XOseeEnumOverride__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:387:2: rule__XOseeEnumOverride__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__0_in_ruleXOseeEnumOverride761);
            rule__XOseeEnumOverride__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getGroup()); 
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
    // $ANTLR end ruleXOseeEnumOverride


    // $ANTLR start entryRuleOverrideOption
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:399:1: entryRuleOverrideOption : ruleOverrideOption EOF ;
    public final void entryRuleOverrideOption() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:400:1: ( ruleOverrideOption EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:401:1: ruleOverrideOption EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOverrideOptionRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption788);
            ruleOverrideOption();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getOverrideOptionRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleOverrideOption795); if (failed) return ;

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:408:1: ruleOverrideOption : ( ( rule__OverrideOption__Alternatives ) ) ;
    public final void ruleOverrideOption() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:412:2: ( ( ( rule__OverrideOption__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:413:1: ( ( rule__OverrideOption__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:413:1: ( ( rule__OverrideOption__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:414:1: ( rule__OverrideOption__Alternatives )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOverrideOptionAccess().getAlternatives()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:415:1: ( rule__OverrideOption__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:415:2: rule__OverrideOption__Alternatives
            {
            pushFollow(FollowSets000.FOLLOW_rule__OverrideOption__Alternatives_in_ruleOverrideOption821);
            rule__OverrideOption__Alternatives();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getOverrideOptionAccess().getAlternatives()); 
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
    // $ANTLR end ruleOverrideOption


    // $ANTLR start entryRuleAddEnum
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:427:1: entryRuleAddEnum : ruleAddEnum EOF ;
    public final void entryRuleAddEnum() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:428:1: ( ruleAddEnum EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:429:1: ruleAddEnum EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleAddEnum_in_entryRuleAddEnum848);
            ruleAddEnum();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleAddEnum855); if (failed) return ;

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:436:1: ruleAddEnum : ( ( rule__AddEnum__Group__0 ) ) ;
    public final void ruleAddEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:440:2: ( ( ( rule__AddEnum__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:441:1: ( ( rule__AddEnum__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:441:1: ( ( rule__AddEnum__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:442:1: ( rule__AddEnum__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:443:1: ( rule__AddEnum__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:443:2: rule__AddEnum__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__0_in_ruleAddEnum881);
            rule__AddEnum__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getGroup()); 
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
    // $ANTLR end ruleAddEnum


    // $ANTLR start entryRuleRemoveEnum
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:455:1: entryRuleRemoveEnum : ruleRemoveEnum EOF ;
    public final void entryRuleRemoveEnum() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:456:1: ( ruleRemoveEnum EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:457:1: ruleRemoveEnum EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum908);
            ruleRemoveEnum();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getRemoveEnumRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleRemoveEnum915); if (failed) return ;

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:464:1: ruleRemoveEnum : ( ( rule__RemoveEnum__Group__0 ) ) ;
    public final void ruleRemoveEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:468:2: ( ( ( rule__RemoveEnum__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:469:1: ( ( rule__RemoveEnum__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:469:1: ( ( rule__RemoveEnum__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:470:1: ( rule__RemoveEnum__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:471:1: ( rule__RemoveEnum__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:471:2: rule__RemoveEnum__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__RemoveEnum__Group__0_in_ruleRemoveEnum941);
            rule__RemoveEnum__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getRemoveEnumAccess().getGroup()); 
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
    // $ANTLR end ruleRemoveEnum


    // $ANTLR start entryRuleXRelationType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:483:1: entryRuleXRelationType : ruleXRelationType EOF ;
    public final void entryRuleXRelationType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:484:1: ( ruleXRelationType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:485:1: ruleXRelationType EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXRelationType_in_entryRuleXRelationType968);
            ruleXRelationType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleXRelationType975); if (failed) return ;

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
    // $ANTLR end entryRuleXRelationType


    // $ANTLR start ruleXRelationType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:492:1: ruleXRelationType : ( ( rule__XRelationType__Group__0 ) ) ;
    public final void ruleXRelationType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:496:2: ( ( ( rule__XRelationType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:497:1: ( ( rule__XRelationType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:497:1: ( ( rule__XRelationType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:498:1: ( rule__XRelationType__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:499:1: ( rule__XRelationType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:499:2: rule__XRelationType__Group__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__0_in_ruleXRelationType1001);
            rule__XRelationType__Group__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getGroup()); 
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
    // $ANTLR end ruleXRelationType


    // $ANTLR start entryRuleRelationOrderType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:511:1: entryRuleRelationOrderType : ruleRelationOrderType EOF ;
    public final void entryRuleRelationOrderType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:512:1: ( ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:513:1: ruleRelationOrderType EOF
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRelationOrderTypeRule()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType1028);
            ruleRelationOrderType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getRelationOrderTypeRule()); 
            }
            match(input,EOF,FollowSets000.FOLLOW_EOF_in_entryRuleRelationOrderType1035); if (failed) return ;

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:520:1: ruleRelationOrderType : ( ( rule__RelationOrderType__Alternatives ) ) ;
    public final void ruleRelationOrderType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:524:2: ( ( ( rule__RelationOrderType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:525:1: ( ( rule__RelationOrderType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:525:1: ( ( rule__RelationOrderType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:526:1: ( rule__RelationOrderType__Alternatives )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRelationOrderTypeAccess().getAlternatives()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:527:1: ( rule__RelationOrderType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:527:2: rule__RelationOrderType__Alternatives
            {
            pushFollow(FollowSets000.FOLLOW_rule__RelationOrderType__Alternatives_in_ruleRelationOrderType1061);
            rule__RelationOrderType__Alternatives();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getRelationOrderTypeAccess().getAlternatives()); 
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
    // $ANTLR end ruleRelationOrderType


    // $ANTLR start ruleRelationMultiplicityEnum
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:540:1: ruleRelationMultiplicityEnum : ( ( rule__RelationMultiplicityEnum__Alternatives ) ) ;
    public final void ruleRelationMultiplicityEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:544:1: ( ( ( rule__RelationMultiplicityEnum__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:545:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:545:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:546:1: ( rule__RelationMultiplicityEnum__Alternatives )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRelationMultiplicityEnumAccess().getAlternatives()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:547:1: ( rule__RelationMultiplicityEnum__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:547:2: rule__RelationMultiplicityEnum__Alternatives
            {
            pushFollow(FollowSets000.FOLLOW_rule__RelationMultiplicityEnum__Alternatives_in_ruleRelationMultiplicityEnum1098);
            rule__RelationMultiplicityEnum__Alternatives();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getRelationMultiplicityEnumAccess().getAlternatives()); 
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
    // $ANTLR end ruleRelationMultiplicityEnum


    // $ANTLR start rule__OseeTypeModel__Alternatives_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:558:1: rule__OseeTypeModel__Alternatives_1 : ( ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) ) );
    public final void rule__OseeTypeModel__Alternatives_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:562:1: ( ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) ) )
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
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("558:1: rule__OseeTypeModel__Alternatives_1 : ( ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) ) );", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:563:1: ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:563:1: ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:564:1: ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeModelAccess().getArtifactTypesAssignment_1_0()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:565:1: ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:565:2: rule__OseeTypeModel__ArtifactTypesAssignment_1_0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__ArtifactTypesAssignment_1_0_in_rule__OseeTypeModel__Alternatives_11133);
                    rule__OseeTypeModel__ArtifactTypesAssignment_1_0();
                    _fsp--;
                    if (failed) return ;

                    }

                    if ( backtracking==0 ) {
                       after(grammarAccess.getOseeTypeModelAccess().getArtifactTypesAssignment_1_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:569:6: ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:569:6: ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:570:1: ( rule__OseeTypeModel__RelationTypesAssignment_1_1 )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeModelAccess().getRelationTypesAssignment_1_1()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:571:1: ( rule__OseeTypeModel__RelationTypesAssignment_1_1 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:571:2: rule__OseeTypeModel__RelationTypesAssignment_1_1
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__RelationTypesAssignment_1_1_in_rule__OseeTypeModel__Alternatives_11151);
                    rule__OseeTypeModel__RelationTypesAssignment_1_1();
                    _fsp--;
                    if (failed) return ;

                    }

                    if ( backtracking==0 ) {
                       after(grammarAccess.getOseeTypeModelAccess().getRelationTypesAssignment_1_1()); 
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:575:6: ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:575:6: ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:576:1: ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeModelAccess().getAttributeTypesAssignment_1_2()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:577:1: ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:577:2: rule__OseeTypeModel__AttributeTypesAssignment_1_2
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__AttributeTypesAssignment_1_2_in_rule__OseeTypeModel__Alternatives_11169);
                    rule__OseeTypeModel__AttributeTypesAssignment_1_2();
                    _fsp--;
                    if (failed) return ;

                    }

                    if ( backtracking==0 ) {
                       after(grammarAccess.getOseeTypeModelAccess().getAttributeTypesAssignment_1_2()); 
                    }

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:581:6: ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:581:6: ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:582:1: ( rule__OseeTypeModel__EnumTypesAssignment_1_3 )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeModelAccess().getEnumTypesAssignment_1_3()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:583:1: ( rule__OseeTypeModel__EnumTypesAssignment_1_3 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:583:2: rule__OseeTypeModel__EnumTypesAssignment_1_3
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__EnumTypesAssignment_1_3_in_rule__OseeTypeModel__Alternatives_11187);
                    rule__OseeTypeModel__EnumTypesAssignment_1_3();
                    _fsp--;
                    if (failed) return ;

                    }

                    if ( backtracking==0 ) {
                       after(grammarAccess.getOseeTypeModelAccess().getEnumTypesAssignment_1_3()); 
                    }

                    }


                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:587:6: ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:587:6: ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:588:1: ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeModelAccess().getEnumOverridesAssignment_1_4()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:589:1: ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:589:2: rule__OseeTypeModel__EnumOverridesAssignment_1_4
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__EnumOverridesAssignment_1_4_in_rule__OseeTypeModel__Alternatives_11205);
                    rule__OseeTypeModel__EnumOverridesAssignment_1_4();
                    _fsp--;
                    if (failed) return ;

                    }

                    if ( backtracking==0 ) {
                       after(grammarAccess.getOseeTypeModelAccess().getEnumOverridesAssignment_1_4()); 
                    }

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:599:1: rule__OseeType__Alternatives : ( ( ruleXArtifactType ) | ( ruleXRelationType ) | ( ruleXAttributeType ) | ( ruleXOseeEnumType ) );
    public final void rule__OseeType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:603:1: ( ( ruleXArtifactType ) | ( ruleXRelationType ) | ( ruleXAttributeType ) | ( ruleXOseeEnumType ) )
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
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("599:1: rule__OseeType__Alternatives : ( ( ruleXArtifactType ) | ( ruleXRelationType ) | ( ruleXAttributeType ) | ( ruleXOseeEnumType ) );", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:604:1: ( ruleXArtifactType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:604:1: ( ruleXArtifactType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:605:1: ruleXArtifactType
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeAccess().getXArtifactTypeParserRuleCall_0()); 
                    }
                    pushFollow(FollowSets000.FOLLOW_ruleXArtifactType_in_rule__OseeType__Alternatives1239);
                    ruleXArtifactType();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getOseeTypeAccess().getXArtifactTypeParserRuleCall_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:610:6: ( ruleXRelationType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:610:6: ( ruleXRelationType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:611:1: ruleXRelationType
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeAccess().getXRelationTypeParserRuleCall_1()); 
                    }
                    pushFollow(FollowSets000.FOLLOW_ruleXRelationType_in_rule__OseeType__Alternatives1256);
                    ruleXRelationType();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getOseeTypeAccess().getXRelationTypeParserRuleCall_1()); 
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:616:6: ( ruleXAttributeType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:616:6: ( ruleXAttributeType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:617:1: ruleXAttributeType
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeAccess().getXAttributeTypeParserRuleCall_2()); 
                    }
                    pushFollow(FollowSets000.FOLLOW_ruleXAttributeType_in_rule__OseeType__Alternatives1273);
                    ruleXAttributeType();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getOseeTypeAccess().getXAttributeTypeParserRuleCall_2()); 
                    }

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:622:6: ( ruleXOseeEnumType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:622:6: ( ruleXOseeEnumType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:623:1: ruleXOseeEnumType
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeAccess().getXOseeEnumTypeParserRuleCall_3()); 
                    }
                    pushFollow(FollowSets000.FOLLOW_ruleXOseeEnumType_in_rule__OseeType__Alternatives1290);
                    ruleXOseeEnumType();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getOseeTypeAccess().getXOseeEnumTypeParserRuleCall_3()); 
                    }

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


    // $ANTLR start rule__XAttributeType__DataProviderAlternatives_8_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:633:1: rule__XAttributeType__DataProviderAlternatives_8_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__XAttributeType__DataProviderAlternatives_8_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:637:1: ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) )
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
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("633:1: rule__XAttributeType__DataProviderAlternatives_8_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:638:1: ( 'DefaultAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:638:1: ( 'DefaultAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:639:1: 'DefaultAttributeDataProvider'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getXAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_8_0_0()); 
                    }
                    match(input,12,FollowSets000.FOLLOW_12_in_rule__XAttributeType__DataProviderAlternatives_8_01323); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getXAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_8_0_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:646:6: ( 'UriAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:646:6: ( 'UriAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:647:1: 'UriAttributeDataProvider'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getXAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_8_0_1()); 
                    }
                    match(input,13,FollowSets000.FOLLOW_13_in_rule__XAttributeType__DataProviderAlternatives_8_01343); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getXAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_8_0_1()); 
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:654:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:654:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:655:1: ruleQUALIFIED_NAME
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getXAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_8_0_2()); 
                    }
                    pushFollow(FollowSets000.FOLLOW_ruleQUALIFIED_NAME_in_rule__XAttributeType__DataProviderAlternatives_8_01362);
                    ruleQUALIFIED_NAME();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getXAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_8_0_2()); 
                    }

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
    // $ANTLR end rule__XAttributeType__DataProviderAlternatives_8_0


    // $ANTLR start rule__XAttributeType__MaxAlternatives_12_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:665:1: rule__XAttributeType__MaxAlternatives_12_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );
    public final void rule__XAttributeType__MaxAlternatives_12_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:669:1: ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_WHOLE_NUM_STR) ) {
                alt4=1;
            }
            else if ( (LA4_0==14) ) {
                alt4=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("665:1: rule__XAttributeType__MaxAlternatives_12_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:670:1: ( RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:670:1: ( RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:671:1: RULE_WHOLE_NUM_STR
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getXAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_12_0_0()); 
                    }
                    match(input,RULE_WHOLE_NUM_STR,FollowSets000.FOLLOW_RULE_WHOLE_NUM_STR_in_rule__XAttributeType__MaxAlternatives_12_01394); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getXAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_12_0_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:676:6: ( 'unlimited' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:676:6: ( 'unlimited' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:677:1: 'unlimited'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getXAttributeTypeAccess().getMaxUnlimitedKeyword_12_0_1()); 
                    }
                    match(input,14,FollowSets000.FOLLOW_14_in_rule__XAttributeType__MaxAlternatives_12_01412); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getXAttributeTypeAccess().getMaxUnlimitedKeyword_12_0_1()); 
                    }

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
    // $ANTLR end rule__XAttributeType__MaxAlternatives_12_0


    // $ANTLR start rule__XAttributeType__TaggerIdAlternatives_13_1_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:689:1: rule__XAttributeType__TaggerIdAlternatives_13_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__XAttributeType__TaggerIdAlternatives_13_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:693:1: ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==15) ) {
                alt5=1;
            }
            else if ( (LA5_0==RULE_ID) ) {
                alt5=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("689:1: rule__XAttributeType__TaggerIdAlternatives_13_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:694:1: ( 'DefaultAttributeTaggerProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:694:1: ( 'DefaultAttributeTaggerProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:695:1: 'DefaultAttributeTaggerProvider'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getXAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_13_1_0_0()); 
                    }
                    match(input,15,FollowSets000.FOLLOW_15_in_rule__XAttributeType__TaggerIdAlternatives_13_1_01447); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getXAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_13_1_0_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:702:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:702:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:703:1: ruleQUALIFIED_NAME
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getXAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_13_1_0_1()); 
                    }
                    pushFollow(FollowSets000.FOLLOW_ruleQUALIFIED_NAME_in_rule__XAttributeType__TaggerIdAlternatives_13_1_01466);
                    ruleQUALIFIED_NAME();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getXAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_13_1_0_1()); 
                    }

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
    // $ANTLR end rule__XAttributeType__TaggerIdAlternatives_13_1_0


    // $ANTLR start rule__AttributeBaseType__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:713:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeBaseType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:717:1: ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) )
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
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("713:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:718:1: ( 'BooleanAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:718:1: ( 'BooleanAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:719:1: 'BooleanAttribute'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                    }
                    match(input,16,FollowSets000.FOLLOW_16_in_rule__AttributeBaseType__Alternatives1499); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:726:6: ( 'CompressedContentAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:726:6: ( 'CompressedContentAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:727:1: 'CompressedContentAttribute'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                    }
                    match(input,17,FollowSets000.FOLLOW_17_in_rule__AttributeBaseType__Alternatives1519); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:734:6: ( 'DateAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:734:6: ( 'DateAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:735:1: 'DateAttribute'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                    }
                    match(input,18,FollowSets000.FOLLOW_18_in_rule__AttributeBaseType__Alternatives1539); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                    }

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:742:6: ( 'EnumeratedAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:742:6: ( 'EnumeratedAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:743:1: 'EnumeratedAttribute'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                    }
                    match(input,19,FollowSets000.FOLLOW_19_in_rule__AttributeBaseType__Alternatives1559); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                    }

                    }


                    }
                    break;
                case 5 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:750:6: ( 'FloatingPointAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:750:6: ( 'FloatingPointAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:751:1: 'FloatingPointAttribute'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                    }
                    match(input,20,FollowSets000.FOLLOW_20_in_rule__AttributeBaseType__Alternatives1579); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                    }

                    }


                    }
                    break;
                case 6 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:758:6: ( 'IntegerAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:758:6: ( 'IntegerAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:759:1: 'IntegerAttribute'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                    }
                    match(input,21,FollowSets000.FOLLOW_21_in_rule__AttributeBaseType__Alternatives1599); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                    }

                    }


                    }
                    break;
                case 7 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:766:6: ( 'JavaObjectAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:766:6: ( 'JavaObjectAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:767:1: 'JavaObjectAttribute'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 
                    }
                    match(input,22,FollowSets000.FOLLOW_22_in_rule__AttributeBaseType__Alternatives1619); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_6()); 
                    }

                    }


                    }
                    break;
                case 8 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:774:6: ( 'StringAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:774:6: ( 'StringAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:775:1: 'StringAttribute'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 
                    }
                    match(input,23,FollowSets000.FOLLOW_23_in_rule__AttributeBaseType__Alternatives1639); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_7()); 
                    }

                    }


                    }
                    break;
                case 9 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:782:6: ( 'WordAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:782:6: ( 'WordAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:783:1: 'WordAttribute'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 
                    }
                    match(input,24,FollowSets000.FOLLOW_24_in_rule__AttributeBaseType__Alternatives1659); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_8()); 
                    }

                    }


                    }
                    break;
                case 10 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:790:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:790:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:791:1: ruleQUALIFIED_NAME
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9()); 
                    }
                    pushFollow(FollowSets000.FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeBaseType__Alternatives1678);
                    ruleQUALIFIED_NAME();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_9()); 
                    }

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:801:1: rule__OverrideOption__Alternatives : ( ( ruleAddEnum ) | ( ruleRemoveEnum ) );
    public final void rule__OverrideOption__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:805:1: ( ( ruleAddEnum ) | ( ruleRemoveEnum ) )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==56) ) {
                alt7=1;
            }
            else if ( (LA7_0==57) ) {
                alt7=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("801:1: rule__OverrideOption__Alternatives : ( ( ruleAddEnum ) | ( ruleRemoveEnum ) );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:806:1: ( ruleAddEnum )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:806:1: ( ruleAddEnum )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:807:1: ruleAddEnum
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0()); 
                    }
                    pushFollow(FollowSets000.FOLLOW_ruleAddEnum_in_rule__OverrideOption__Alternatives1710);
                    ruleAddEnum();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:812:6: ( ruleRemoveEnum )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:812:6: ( ruleRemoveEnum )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:813:1: ruleRemoveEnum
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1()); 
                    }
                    pushFollow(FollowSets000.FOLLOW_ruleRemoveEnum_in_rule__OverrideOption__Alternatives1727);
                    ruleRemoveEnum();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1()); 
                    }

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:823:1: rule__RelationOrderType__Alternatives : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );
    public final void rule__RelationOrderType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:827:1: ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) )
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
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("823:1: rule__RelationOrderType__Alternatives : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:828:1: ( 'Lexicographical_Ascending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:828:1: ( 'Lexicographical_Ascending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:829:1: 'Lexicographical_Ascending'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 
                    }
                    match(input,25,FollowSets000.FOLLOW_25_in_rule__RelationOrderType__Alternatives1760); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:836:6: ( 'Lexicographical_Descending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:836:6: ( 'Lexicographical_Descending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:837:1: 'Lexicographical_Descending'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 
                    }
                    match(input,26,FollowSets000.FOLLOW_26_in_rule__RelationOrderType__Alternatives1780); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:844:6: ( 'Unordered' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:844:6: ( 'Unordered' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:845:1: 'Unordered'
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 
                    }
                    match(input,27,FollowSets000.FOLLOW_27_in_rule__RelationOrderType__Alternatives1800); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 
                    }

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:852:6: ( RULE_ID )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:852:6: ( RULE_ID )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:853:1: RULE_ID
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationOrderTypeAccess().getIDTerminalRuleCall_3()); 
                    }
                    match(input,RULE_ID,FollowSets000.FOLLOW_RULE_ID_in_rule__RelationOrderType__Alternatives1819); if (failed) return ;
                    if ( backtracking==0 ) {
                       after(grammarAccess.getRelationOrderTypeAccess().getIDTerminalRuleCall_3()); 
                    }

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:863:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );
    public final void rule__RelationMultiplicityEnum__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:867:1: ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) )
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
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("863:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:868:1: ( ( 'ONE_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:868:1: ( ( 'ONE_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:869:1: ( 'ONE_TO_ONE' )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:870:1: ( 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:870:3: 'ONE_TO_ONE'
                    {
                    match(input,28,FollowSets000.FOLLOW_28_in_rule__RelationMultiplicityEnum__Alternatives1852); if (failed) return ;

                    }

                    if ( backtracking==0 ) {
                       after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:875:6: ( ( 'ONE_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:875:6: ( ( 'ONE_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:876:1: ( 'ONE_TO_MANY' )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:877:1: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:877:3: 'ONE_TO_MANY'
                    {
                    match(input,29,FollowSets000.FOLLOW_29_in_rule__RelationMultiplicityEnum__Alternatives1873); if (failed) return ;

                    }

                    if ( backtracking==0 ) {
                       after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:882:6: ( ( 'MANY_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:882:6: ( ( 'MANY_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:883:1: ( 'MANY_TO_ONE' )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:884:1: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:884:3: 'MANY_TO_ONE'
                    {
                    match(input,30,FollowSets000.FOLLOW_30_in_rule__RelationMultiplicityEnum__Alternatives1894); if (failed) return ;

                    }

                    if ( backtracking==0 ) {
                       after(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                    }

                    }


                    }
                    break;
                case 4 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:889:6: ( ( 'MANY_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:889:6: ( ( 'MANY_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:890:1: ( 'MANY_TO_MANY' )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:891:1: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:891:3: 'MANY_TO_MANY'
                    {
                    match(input,31,FollowSets000.FOLLOW_31_in_rule__RelationMultiplicityEnum__Alternatives1915); if (failed) return ;

                    }

                    if ( backtracking==0 ) {
                       after(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3()); 
                    }

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:903:1: rule__OseeTypeModel__Group__0 : ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1 ;
    public final void rule__OseeTypeModel__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:907:1: ( ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:908:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) rule__OseeTypeModel__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:908:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:909:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getImportsAssignment_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:910:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==32) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:910:2: rule__OseeTypeModel__ImportsAssignment_0
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__01952);
            	    rule__OseeTypeModel__ImportsAssignment_0();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeModelAccess().getImportsAssignment_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01962);
            rule__OseeTypeModel__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:921:1: rule__OseeTypeModel__Group__1 : ( ( rule__OseeTypeModel__Alternatives_1 )* ) ;
    public final void rule__OseeTypeModel__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:925:1: ( ( ( rule__OseeTypeModel__Alternatives_1 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:926:1: ( ( rule__OseeTypeModel__Alternatives_1 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:926:1: ( ( rule__OseeTypeModel__Alternatives_1 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:927:1: ( rule__OseeTypeModel__Alternatives_1 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getAlternatives_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:928:1: ( rule__OseeTypeModel__Alternatives_1 )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==34||LA11_0==42||LA11_0==52||LA11_0==55||LA11_0==58||LA11_0==65) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:928:2: rule__OseeTypeModel__Alternatives_1
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__Alternatives_1_in_rule__OseeTypeModel__Group__11990);
            	    rule__OseeTypeModel__Alternatives_1();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeModelAccess().getAlternatives_1()); 
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
    // $ANTLR end rule__OseeTypeModel__Group__1


    // $ANTLR start rule__Import__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:942:1: rule__Import__Group__0 : ( 'import' ) rule__Import__Group__1 ;
    public final void rule__Import__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:946:1: ( ( 'import' ) rule__Import__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:947:1: ( 'import' ) rule__Import__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:947:1: ( 'import' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:948:1: 'import'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getImportAccess().getImportKeyword_0()); 
            }
            match(input,32,FollowSets000.FOLLOW_32_in_rule__Import__Group__02030); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getImportAccess().getImportKeyword_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__Import__Group__1_in_rule__Import__Group__02040);
            rule__Import__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:962:1: rule__Import__Group__1 : ( ( rule__Import__ImportURIAssignment_1 ) ) ;
    public final void rule__Import__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:966:1: ( ( ( rule__Import__ImportURIAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:967:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:967:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:968:1: ( rule__Import__ImportURIAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getImportAccess().getImportURIAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:969:1: ( rule__Import__ImportURIAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:969:2: rule__Import__ImportURIAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__12068);
            rule__Import__ImportURIAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getImportAccess().getImportURIAssignment_1()); 
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
    // $ANTLR end rule__Import__Group__1


    // $ANTLR start rule__QUALIFIED_NAME__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:983:1: rule__QUALIFIED_NAME__Group__0 : ( RULE_ID ) rule__QUALIFIED_NAME__Group__1 ;
    public final void rule__QUALIFIED_NAME__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:987:1: ( ( RULE_ID ) rule__QUALIFIED_NAME__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:988:1: ( RULE_ID ) rule__QUALIFIED_NAME__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:988:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:989:1: RULE_ID
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
            }
            match(input,RULE_ID,FollowSets000.FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__02106); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__02114);
            rule__QUALIFIED_NAME__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1001:1: rule__QUALIFIED_NAME__Group__1 : ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) ;
    public final void rule__QUALIFIED_NAME__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1005:1: ( ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1006:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1006:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1007:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1008:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==33) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1008:2: rule__QUALIFIED_NAME__Group_1__0
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__12142);
            	    rule__QUALIFIED_NAME__Group_1__0();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            if ( backtracking==0 ) {
               after(grammarAccess.getQUALIFIED_NAMEAccess().getGroup_1()); 
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
    // $ANTLR end rule__QUALIFIED_NAME__Group__1


    // $ANTLR start rule__QUALIFIED_NAME__Group_1__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1022:1: rule__QUALIFIED_NAME__Group_1__0 : ( '.' ) rule__QUALIFIED_NAME__Group_1__1 ;
    public final void rule__QUALIFIED_NAME__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1026:1: ( ( '.' ) rule__QUALIFIED_NAME__Group_1__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1027:1: ( '.' ) rule__QUALIFIED_NAME__Group_1__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1027:1: ( '.' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1028:1: '.'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            }
            match(input,33,FollowSets000.FOLLOW_33_in_rule__QUALIFIED_NAME__Group_1__02182); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__02192);
            rule__QUALIFIED_NAME__Group_1__1();
            _fsp--;
            if (failed) return ;

            }

        }
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1042:1: rule__QUALIFIED_NAME__Group_1__1 : ( RULE_ID ) ;
    public final void rule__QUALIFIED_NAME__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1046:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1047:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1047:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1048:1: RULE_ID
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
            }
            match(input,RULE_ID,FollowSets000.FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__12220); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
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
    // $ANTLR end rule__QUALIFIED_NAME__Group_1__1


    // $ANTLR start rule__XArtifactType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1063:1: rule__XArtifactType__Group__0 : ( ( rule__XArtifactType__AbstractAssignment_0 )? ) rule__XArtifactType__Group__1 ;
    public final void rule__XArtifactType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1067:1: ( ( ( rule__XArtifactType__AbstractAssignment_0 )? ) rule__XArtifactType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1068:1: ( ( rule__XArtifactType__AbstractAssignment_0 )? ) rule__XArtifactType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1068:1: ( ( rule__XArtifactType__AbstractAssignment_0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1069:1: ( rule__XArtifactType__AbstractAssignment_0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getAbstractAssignment_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1070:1: ( rule__XArtifactType__AbstractAssignment_0 )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==65) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1070:2: rule__XArtifactType__AbstractAssignment_0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__AbstractAssignment_0_in_rule__XArtifactType__Group__02257);
                    rule__XArtifactType__AbstractAssignment_0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getAbstractAssignment_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__1_in_rule__XArtifactType__Group__02267);
            rule__XArtifactType__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__0


    // $ANTLR start rule__XArtifactType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1081:1: rule__XArtifactType__Group__1 : ( 'artifactType' ) rule__XArtifactType__Group__2 ;
    public final void rule__XArtifactType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1085:1: ( ( 'artifactType' ) rule__XArtifactType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1086:1: ( 'artifactType' ) rule__XArtifactType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1086:1: ( 'artifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1087:1: 'artifactType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1()); 
            }
            match(input,34,FollowSets000.FOLLOW_34_in_rule__XArtifactType__Group__12296); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__2_in_rule__XArtifactType__Group__12306);
            rule__XArtifactType__Group__2();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__1


    // $ANTLR start rule__XArtifactType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1101:1: rule__XArtifactType__Group__2 : ( ( rule__XArtifactType__NameAssignment_2 ) ) rule__XArtifactType__Group__3 ;
    public final void rule__XArtifactType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1105:1: ( ( ( rule__XArtifactType__NameAssignment_2 ) ) rule__XArtifactType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1106:1: ( ( rule__XArtifactType__NameAssignment_2 ) ) rule__XArtifactType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1106:1: ( ( rule__XArtifactType__NameAssignment_2 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1107:1: ( rule__XArtifactType__NameAssignment_2 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getNameAssignment_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1108:1: ( rule__XArtifactType__NameAssignment_2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1108:2: rule__XArtifactType__NameAssignment_2
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__NameAssignment_2_in_rule__XArtifactType__Group__22334);
            rule__XArtifactType__NameAssignment_2();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getNameAssignment_2()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__3_in_rule__XArtifactType__Group__22343);
            rule__XArtifactType__Group__3();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__2


    // $ANTLR start rule__XArtifactType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1119:1: rule__XArtifactType__Group__3 : ( ( rule__XArtifactType__Group_3__0 )? ) rule__XArtifactType__Group__4 ;
    public final void rule__XArtifactType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1123:1: ( ( ( rule__XArtifactType__Group_3__0 )? ) rule__XArtifactType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1124:1: ( ( rule__XArtifactType__Group_3__0 )? ) rule__XArtifactType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1124:1: ( ( rule__XArtifactType__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1125:1: ( rule__XArtifactType__Group_3__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getGroup_3()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1126:1: ( rule__XArtifactType__Group_3__0 )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==38) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1126:2: rule__XArtifactType__Group_3__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3__0_in_rule__XArtifactType__Group__32371);
                    rule__XArtifactType__Group_3__0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getGroup_3()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__4_in_rule__XArtifactType__Group__32381);
            rule__XArtifactType__Group__4();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__3


    // $ANTLR start rule__XArtifactType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1137:1: rule__XArtifactType__Group__4 : ( '{' ) rule__XArtifactType__Group__5 ;
    public final void rule__XArtifactType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1141:1: ( ( '{' ) rule__XArtifactType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1142:1: ( '{' ) rule__XArtifactType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1142:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1143:1: '{'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4()); 
            }
            match(input,35,FollowSets000.FOLLOW_35_in_rule__XArtifactType__Group__42410); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__5_in_rule__XArtifactType__Group__42420);
            rule__XArtifactType__Group__5();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__4


    // $ANTLR start rule__XArtifactType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1157:1: rule__XArtifactType__Group__5 : ( 'guid' ) rule__XArtifactType__Group__6 ;
    public final void rule__XArtifactType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1161:1: ( ( 'guid' ) rule__XArtifactType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1162:1: ( 'guid' ) rule__XArtifactType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1162:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1163:1: 'guid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5()); 
            }
            match(input,36,FollowSets000.FOLLOW_36_in_rule__XArtifactType__Group__52449); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__6_in_rule__XArtifactType__Group__52459);
            rule__XArtifactType__Group__6();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__5


    // $ANTLR start rule__XArtifactType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1177:1: rule__XArtifactType__Group__6 : ( ( rule__XArtifactType__TypeGuidAssignment_6 ) ) rule__XArtifactType__Group__7 ;
    public final void rule__XArtifactType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1181:1: ( ( ( rule__XArtifactType__TypeGuidAssignment_6 ) ) rule__XArtifactType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1182:1: ( ( rule__XArtifactType__TypeGuidAssignment_6 ) ) rule__XArtifactType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1182:1: ( ( rule__XArtifactType__TypeGuidAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1183:1: ( rule__XArtifactType__TypeGuidAssignment_6 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getTypeGuidAssignment_6()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1184:1: ( rule__XArtifactType__TypeGuidAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1184:2: rule__XArtifactType__TypeGuidAssignment_6
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__TypeGuidAssignment_6_in_rule__XArtifactType__Group__62487);
            rule__XArtifactType__TypeGuidAssignment_6();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getTypeGuidAssignment_6()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__7_in_rule__XArtifactType__Group__62496);
            rule__XArtifactType__Group__7();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__6


    // $ANTLR start rule__XArtifactType__Group__7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1195:1: rule__XArtifactType__Group__7 : ( ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__XArtifactType__Group__8 ;
    public final void rule__XArtifactType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1199:1: ( ( ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__XArtifactType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1200:1: ( ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )* ) rule__XArtifactType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1200:1: ( ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1201:1: ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesAssignment_7()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1202:1: ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==40) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1202:2: rule__XArtifactType__ValidAttributeTypesAssignment_7
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__ValidAttributeTypesAssignment_7_in_rule__XArtifactType__Group__72524);
            	    rule__XArtifactType__ValidAttributeTypesAssignment_7();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesAssignment_7()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__8_in_rule__XArtifactType__Group__72534);
            rule__XArtifactType__Group__8();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__7


    // $ANTLR start rule__XArtifactType__Group__8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1213:1: rule__XArtifactType__Group__8 : ( '}' ) ;
    public final void rule__XArtifactType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1217:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1218:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1218:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1219:1: '}'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getRightCurlyBracketKeyword_8()); 
            }
            match(input,37,FollowSets000.FOLLOW_37_in_rule__XArtifactType__Group__82563); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getRightCurlyBracketKeyword_8()); 
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
    // $ANTLR end rule__XArtifactType__Group__8


    // $ANTLR start rule__XArtifactType__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1250:1: rule__XArtifactType__Group_3__0 : ( 'extends' ) rule__XArtifactType__Group_3__1 ;
    public final void rule__XArtifactType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1254:1: ( ( 'extends' ) rule__XArtifactType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1255:1: ( 'extends' ) rule__XArtifactType__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1255:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1256:1: 'extends'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0()); 
            }
            match(input,38,FollowSets000.FOLLOW_38_in_rule__XArtifactType__Group_3__02617); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3__1_in_rule__XArtifactType__Group_3__02627);
            rule__XArtifactType__Group_3__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group_3__0


    // $ANTLR start rule__XArtifactType__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1270:1: rule__XArtifactType__Group_3__1 : ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__XArtifactType__Group_3__2 ;
    public final void rule__XArtifactType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1274:1: ( ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__XArtifactType__Group_3__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1275:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) ) rule__XArtifactType__Group_3__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1275:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1276:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1277:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1277:2: rule__XArtifactType__SuperArtifactTypesAssignment_3_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__SuperArtifactTypesAssignment_3_1_in_rule__XArtifactType__Group_3__12655);
            rule__XArtifactType__SuperArtifactTypesAssignment_3_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3__2_in_rule__XArtifactType__Group_3__12664);
            rule__XArtifactType__Group_3__2();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group_3__1


    // $ANTLR start rule__XArtifactType__Group_3__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1288:1: rule__XArtifactType__Group_3__2 : ( ( rule__XArtifactType__Group_3_2__0 )* ) ;
    public final void rule__XArtifactType__Group_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1292:1: ( ( ( rule__XArtifactType__Group_3_2__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1293:1: ( ( rule__XArtifactType__Group_3_2__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1293:1: ( ( rule__XArtifactType__Group_3_2__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1294:1: ( rule__XArtifactType__Group_3_2__0 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getGroup_3_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1295:1: ( rule__XArtifactType__Group_3_2__0 )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==39) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1295:2: rule__XArtifactType__Group_3_2__0
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3_2__0_in_rule__XArtifactType__Group_3__22692);
            	    rule__XArtifactType__Group_3_2__0();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getGroup_3_2()); 
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
    // $ANTLR end rule__XArtifactType__Group_3__2


    // $ANTLR start rule__XArtifactType__Group_3_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1311:1: rule__XArtifactType__Group_3_2__0 : ( ',' ) rule__XArtifactType__Group_3_2__1 ;
    public final void rule__XArtifactType__Group_3_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1315:1: ( ( ',' ) rule__XArtifactType__Group_3_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1316:1: ( ',' ) rule__XArtifactType__Group_3_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1316:1: ( ',' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1317:1: ','
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0()); 
            }
            match(input,39,FollowSets000.FOLLOW_39_in_rule__XArtifactType__Group_3_2__02734); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3_2__1_in_rule__XArtifactType__Group_3_2__02744);
            rule__XArtifactType__Group_3_2__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group_3_2__0


    // $ANTLR start rule__XArtifactType__Group_3_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1331:1: rule__XArtifactType__Group_3_2__1 : ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) ;
    public final void rule__XArtifactType__Group_3_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1335:1: ( ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1336:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1336:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1337:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_2_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1338:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1338:2: rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1_in_rule__XArtifactType__Group_3_2__12772);
            rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_2_1()); 
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
    // $ANTLR end rule__XArtifactType__Group_3_2__1


    // $ANTLR start rule__XAttributeTypeRef__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1352:1: rule__XAttributeTypeRef__Group__0 : ( 'attribute' ) rule__XAttributeTypeRef__Group__1 ;
    public final void rule__XAttributeTypeRef__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1356:1: ( ( 'attribute' ) rule__XAttributeTypeRef__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1357:1: ( 'attribute' ) rule__XAttributeTypeRef__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1357:1: ( 'attribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1358:1: 'attribute'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0()); 
            }
            match(input,40,FollowSets000.FOLLOW_40_in_rule__XAttributeTypeRef__Group__02811); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group__1_in_rule__XAttributeTypeRef__Group__02821);
            rule__XAttributeTypeRef__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeTypeRef__Group__0


    // $ANTLR start rule__XAttributeTypeRef__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1372:1: rule__XAttributeTypeRef__Group__1 : ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__XAttributeTypeRef__Group__2 ;
    public final void rule__XAttributeTypeRef__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1376:1: ( ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__XAttributeTypeRef__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1377:1: ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) rule__XAttributeTypeRef__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1377:1: ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1378:1: ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1379:1: ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1379:2: rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__XAttributeTypeRef__Group__12849);
            rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group__2_in_rule__XAttributeTypeRef__Group__12858);
            rule__XAttributeTypeRef__Group__2();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeTypeRef__Group__1


    // $ANTLR start rule__XAttributeTypeRef__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1390:1: rule__XAttributeTypeRef__Group__2 : ( ( rule__XAttributeTypeRef__Group_2__0 )? ) ;
    public final void rule__XAttributeTypeRef__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1394:1: ( ( ( rule__XAttributeTypeRef__Group_2__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1395:1: ( ( rule__XAttributeTypeRef__Group_2__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1395:1: ( ( rule__XAttributeTypeRef__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1396:1: ( rule__XAttributeTypeRef__Group_2__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getGroup_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1397:1: ( rule__XAttributeTypeRef__Group_2__0 )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==41) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1397:2: rule__XAttributeTypeRef__Group_2__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group_2__0_in_rule__XAttributeTypeRef__Group__22886);
                    rule__XAttributeTypeRef__Group_2__0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getGroup_2()); 
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
    // $ANTLR end rule__XAttributeTypeRef__Group__2


    // $ANTLR start rule__XAttributeTypeRef__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1413:1: rule__XAttributeTypeRef__Group_2__0 : ( 'branchGuid' ) rule__XAttributeTypeRef__Group_2__1 ;
    public final void rule__XAttributeTypeRef__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1417:1: ( ( 'branchGuid' ) rule__XAttributeTypeRef__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1418:1: ( 'branchGuid' ) rule__XAttributeTypeRef__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1418:1: ( 'branchGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1419:1: 'branchGuid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidKeyword_2_0()); 
            }
            match(input,41,FollowSets000.FOLLOW_41_in_rule__XAttributeTypeRef__Group_2__02928); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidKeyword_2_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group_2__1_in_rule__XAttributeTypeRef__Group_2__02938);
            rule__XAttributeTypeRef__Group_2__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeTypeRef__Group_2__0


    // $ANTLR start rule__XAttributeTypeRef__Group_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1433:1: rule__XAttributeTypeRef__Group_2__1 : ( ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 ) ) ;
    public final void rule__XAttributeTypeRef__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1437:1: ( ( ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1438:1: ( ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1438:1: ( ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1439:1: ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidAssignment_2_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1440:1: ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1440:2: rule__XAttributeTypeRef__BranchGuidAssignment_2_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__BranchGuidAssignment_2_1_in_rule__XAttributeTypeRef__Group_2__12966);
            rule__XAttributeTypeRef__BranchGuidAssignment_2_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidAssignment_2_1()); 
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
    // $ANTLR end rule__XAttributeTypeRef__Group_2__1


    // $ANTLR start rule__XAttributeType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1454:1: rule__XAttributeType__Group__0 : ( 'attributeType' ) rule__XAttributeType__Group__1 ;
    public final void rule__XAttributeType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1458:1: ( ( 'attributeType' ) rule__XAttributeType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1459:1: ( 'attributeType' ) rule__XAttributeType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1459:1: ( 'attributeType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1460:1: 'attributeType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0()); 
            }
            match(input,42,FollowSets000.FOLLOW_42_in_rule__XAttributeType__Group__03005); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__1_in_rule__XAttributeType__Group__03015);
            rule__XAttributeType__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__0


    // $ANTLR start rule__XAttributeType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1474:1: rule__XAttributeType__Group__1 : ( ( rule__XAttributeType__NameAssignment_1 ) ) rule__XAttributeType__Group__2 ;
    public final void rule__XAttributeType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1478:1: ( ( ( rule__XAttributeType__NameAssignment_1 ) ) rule__XAttributeType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1479:1: ( ( rule__XAttributeType__NameAssignment_1 ) ) rule__XAttributeType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1479:1: ( ( rule__XAttributeType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1480:1: ( rule__XAttributeType__NameAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getNameAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1481:1: ( rule__XAttributeType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1481:2: rule__XAttributeType__NameAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__NameAssignment_1_in_rule__XAttributeType__Group__13043);
            rule__XAttributeType__NameAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getNameAssignment_1()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__2_in_rule__XAttributeType__Group__13052);
            rule__XAttributeType__Group__2();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__1


    // $ANTLR start rule__XAttributeType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1492:1: rule__XAttributeType__Group__2 : ( ( rule__XAttributeType__Group_2__0 ) ) rule__XAttributeType__Group__3 ;
    public final void rule__XAttributeType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1496:1: ( ( ( rule__XAttributeType__Group_2__0 ) ) rule__XAttributeType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1497:1: ( ( rule__XAttributeType__Group_2__0 ) ) rule__XAttributeType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1497:1: ( ( rule__XAttributeType__Group_2__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1498:1: ( rule__XAttributeType__Group_2__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1499:1: ( rule__XAttributeType__Group_2__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1499:2: rule__XAttributeType__Group_2__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_2__0_in_rule__XAttributeType__Group__23080);
            rule__XAttributeType__Group_2__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGroup_2()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__3_in_rule__XAttributeType__Group__23089);
            rule__XAttributeType__Group__3();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__2


    // $ANTLR start rule__XAttributeType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1510:1: rule__XAttributeType__Group__3 : ( ( rule__XAttributeType__Group_3__0 )? ) rule__XAttributeType__Group__4 ;
    public final void rule__XAttributeType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1514:1: ( ( ( rule__XAttributeType__Group_3__0 )? ) rule__XAttributeType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1515:1: ( ( rule__XAttributeType__Group_3__0 )? ) rule__XAttributeType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1515:1: ( ( rule__XAttributeType__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1516:1: ( rule__XAttributeType__Group_3__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_3()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1517:1: ( rule__XAttributeType__Group_3__0 )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==46) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1517:2: rule__XAttributeType__Group_3__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_3__0_in_rule__XAttributeType__Group__33117);
                    rule__XAttributeType__Group_3__0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGroup_3()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__4_in_rule__XAttributeType__Group__33127);
            rule__XAttributeType__Group__4();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__3


    // $ANTLR start rule__XAttributeType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1528:1: rule__XAttributeType__Group__4 : ( '{' ) rule__XAttributeType__Group__5 ;
    public final void rule__XAttributeType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1532:1: ( ( '{' ) rule__XAttributeType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1533:1: ( '{' ) rule__XAttributeType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1533:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1534:1: '{'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4()); 
            }
            match(input,35,FollowSets000.FOLLOW_35_in_rule__XAttributeType__Group__43156); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__5_in_rule__XAttributeType__Group__43166);
            rule__XAttributeType__Group__5();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__4


    // $ANTLR start rule__XAttributeType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1548:1: rule__XAttributeType__Group__5 : ( 'guid' ) rule__XAttributeType__Group__6 ;
    public final void rule__XAttributeType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1552:1: ( ( 'guid' ) rule__XAttributeType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1553:1: ( 'guid' ) rule__XAttributeType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1553:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1554:1: 'guid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5()); 
            }
            match(input,36,FollowSets000.FOLLOW_36_in_rule__XAttributeType__Group__53195); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__6_in_rule__XAttributeType__Group__53205);
            rule__XAttributeType__Group__6();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__5


    // $ANTLR start rule__XAttributeType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1568:1: rule__XAttributeType__Group__6 : ( ( rule__XAttributeType__TypeGuidAssignment_6 ) ) rule__XAttributeType__Group__7 ;
    public final void rule__XAttributeType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1572:1: ( ( ( rule__XAttributeType__TypeGuidAssignment_6 ) ) rule__XAttributeType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1573:1: ( ( rule__XAttributeType__TypeGuidAssignment_6 ) ) rule__XAttributeType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1573:1: ( ( rule__XAttributeType__TypeGuidAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1574:1: ( rule__XAttributeType__TypeGuidAssignment_6 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getTypeGuidAssignment_6()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1575:1: ( rule__XAttributeType__TypeGuidAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1575:2: rule__XAttributeType__TypeGuidAssignment_6
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__TypeGuidAssignment_6_in_rule__XAttributeType__Group__63233);
            rule__XAttributeType__TypeGuidAssignment_6();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getTypeGuidAssignment_6()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__7_in_rule__XAttributeType__Group__63242);
            rule__XAttributeType__Group__7();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__6


    // $ANTLR start rule__XAttributeType__Group__7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1586:1: rule__XAttributeType__Group__7 : ( 'dataProvider' ) rule__XAttributeType__Group__8 ;
    public final void rule__XAttributeType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1590:1: ( ( 'dataProvider' ) rule__XAttributeType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1591:1: ( 'dataProvider' ) rule__XAttributeType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1591:1: ( 'dataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1592:1: 'dataProvider'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_7()); 
            }
            match(input,43,FollowSets000.FOLLOW_43_in_rule__XAttributeType__Group__73271); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_7()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__8_in_rule__XAttributeType__Group__73281);
            rule__XAttributeType__Group__8();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__7


    // $ANTLR start rule__XAttributeType__Group__8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1606:1: rule__XAttributeType__Group__8 : ( ( rule__XAttributeType__DataProviderAssignment_8 ) ) rule__XAttributeType__Group__9 ;
    public final void rule__XAttributeType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1610:1: ( ( ( rule__XAttributeType__DataProviderAssignment_8 ) ) rule__XAttributeType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1611:1: ( ( rule__XAttributeType__DataProviderAssignment_8 ) ) rule__XAttributeType__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1611:1: ( ( rule__XAttributeType__DataProviderAssignment_8 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1612:1: ( rule__XAttributeType__DataProviderAssignment_8 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDataProviderAssignment_8()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1613:1: ( rule__XAttributeType__DataProviderAssignment_8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1613:2: rule__XAttributeType__DataProviderAssignment_8
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__DataProviderAssignment_8_in_rule__XAttributeType__Group__83309);
            rule__XAttributeType__DataProviderAssignment_8();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDataProviderAssignment_8()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__9_in_rule__XAttributeType__Group__83318);
            rule__XAttributeType__Group__9();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__8


    // $ANTLR start rule__XAttributeType__Group__9
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1624:1: rule__XAttributeType__Group__9 : ( 'min' ) rule__XAttributeType__Group__10 ;
    public final void rule__XAttributeType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1628:1: ( ( 'min' ) rule__XAttributeType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1629:1: ( 'min' ) rule__XAttributeType__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1629:1: ( 'min' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1630:1: 'min'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMinKeyword_9()); 
            }
            match(input,44,FollowSets000.FOLLOW_44_in_rule__XAttributeType__Group__93347); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getMinKeyword_9()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__10_in_rule__XAttributeType__Group__93357);
            rule__XAttributeType__Group__10();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__9


    // $ANTLR start rule__XAttributeType__Group__10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1644:1: rule__XAttributeType__Group__10 : ( ( rule__XAttributeType__MinAssignment_10 ) ) rule__XAttributeType__Group__11 ;
    public final void rule__XAttributeType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1648:1: ( ( ( rule__XAttributeType__MinAssignment_10 ) ) rule__XAttributeType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1649:1: ( ( rule__XAttributeType__MinAssignment_10 ) ) rule__XAttributeType__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1649:1: ( ( rule__XAttributeType__MinAssignment_10 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1650:1: ( rule__XAttributeType__MinAssignment_10 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMinAssignment_10()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1651:1: ( rule__XAttributeType__MinAssignment_10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1651:2: rule__XAttributeType__MinAssignment_10
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__MinAssignment_10_in_rule__XAttributeType__Group__103385);
            rule__XAttributeType__MinAssignment_10();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getMinAssignment_10()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__11_in_rule__XAttributeType__Group__103394);
            rule__XAttributeType__Group__11();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__10


    // $ANTLR start rule__XAttributeType__Group__11
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1662:1: rule__XAttributeType__Group__11 : ( 'max' ) rule__XAttributeType__Group__12 ;
    public final void rule__XAttributeType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1666:1: ( ( 'max' ) rule__XAttributeType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1667:1: ( 'max' ) rule__XAttributeType__Group__12
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1667:1: ( 'max' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1668:1: 'max'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMaxKeyword_11()); 
            }
            match(input,45,FollowSets000.FOLLOW_45_in_rule__XAttributeType__Group__113423); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getMaxKeyword_11()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__12_in_rule__XAttributeType__Group__113433);
            rule__XAttributeType__Group__12();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__11


    // $ANTLR start rule__XAttributeType__Group__12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1682:1: rule__XAttributeType__Group__12 : ( ( rule__XAttributeType__MaxAssignment_12 ) ) rule__XAttributeType__Group__13 ;
    public final void rule__XAttributeType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1686:1: ( ( ( rule__XAttributeType__MaxAssignment_12 ) ) rule__XAttributeType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1687:1: ( ( rule__XAttributeType__MaxAssignment_12 ) ) rule__XAttributeType__Group__13
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1687:1: ( ( rule__XAttributeType__MaxAssignment_12 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1688:1: ( rule__XAttributeType__MaxAssignment_12 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMaxAssignment_12()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1689:1: ( rule__XAttributeType__MaxAssignment_12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1689:2: rule__XAttributeType__MaxAssignment_12
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__MaxAssignment_12_in_rule__XAttributeType__Group__123461);
            rule__XAttributeType__MaxAssignment_12();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getMaxAssignment_12()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__13_in_rule__XAttributeType__Group__123470);
            rule__XAttributeType__Group__13();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__12


    // $ANTLR start rule__XAttributeType__Group__13
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1700:1: rule__XAttributeType__Group__13 : ( ( rule__XAttributeType__Group_13__0 )? ) rule__XAttributeType__Group__14 ;
    public final void rule__XAttributeType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1704:1: ( ( ( rule__XAttributeType__Group_13__0 )? ) rule__XAttributeType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1705:1: ( ( rule__XAttributeType__Group_13__0 )? ) rule__XAttributeType__Group__14
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1705:1: ( ( rule__XAttributeType__Group_13__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1706:1: ( rule__XAttributeType__Group_13__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_13()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1707:1: ( rule__XAttributeType__Group_13__0 )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==47) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1707:2: rule__XAttributeType__Group_13__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_13__0_in_rule__XAttributeType__Group__133498);
                    rule__XAttributeType__Group_13__0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGroup_13()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__14_in_rule__XAttributeType__Group__133508);
            rule__XAttributeType__Group__14();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__13


    // $ANTLR start rule__XAttributeType__Group__14
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1718:1: rule__XAttributeType__Group__14 : ( ( rule__XAttributeType__Group_14__0 )? ) rule__XAttributeType__Group__15 ;
    public final void rule__XAttributeType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1722:1: ( ( ( rule__XAttributeType__Group_14__0 )? ) rule__XAttributeType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1723:1: ( ( rule__XAttributeType__Group_14__0 )? ) rule__XAttributeType__Group__15
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1723:1: ( ( rule__XAttributeType__Group_14__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1724:1: ( rule__XAttributeType__Group_14__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_14()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1725:1: ( rule__XAttributeType__Group_14__0 )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==48) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1725:2: rule__XAttributeType__Group_14__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_14__0_in_rule__XAttributeType__Group__143536);
                    rule__XAttributeType__Group_14__0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGroup_14()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__15_in_rule__XAttributeType__Group__143546);
            rule__XAttributeType__Group__15();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__14


    // $ANTLR start rule__XAttributeType__Group__15
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1736:1: rule__XAttributeType__Group__15 : ( ( rule__XAttributeType__Group_15__0 )? ) rule__XAttributeType__Group__16 ;
    public final void rule__XAttributeType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1740:1: ( ( ( rule__XAttributeType__Group_15__0 )? ) rule__XAttributeType__Group__16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1741:1: ( ( rule__XAttributeType__Group_15__0 )? ) rule__XAttributeType__Group__16
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1741:1: ( ( rule__XAttributeType__Group_15__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1742:1: ( rule__XAttributeType__Group_15__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_15()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1743:1: ( rule__XAttributeType__Group_15__0 )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==49) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1743:2: rule__XAttributeType__Group_15__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_15__0_in_rule__XAttributeType__Group__153574);
                    rule__XAttributeType__Group_15__0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGroup_15()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__16_in_rule__XAttributeType__Group__153584);
            rule__XAttributeType__Group__16();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__15


    // $ANTLR start rule__XAttributeType__Group__16
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1754:1: rule__XAttributeType__Group__16 : ( ( rule__XAttributeType__Group_16__0 )? ) rule__XAttributeType__Group__17 ;
    public final void rule__XAttributeType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1758:1: ( ( ( rule__XAttributeType__Group_16__0 )? ) rule__XAttributeType__Group__17 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1759:1: ( ( rule__XAttributeType__Group_16__0 )? ) rule__XAttributeType__Group__17
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1759:1: ( ( rule__XAttributeType__Group_16__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1760:1: ( rule__XAttributeType__Group_16__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_16()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1761:1: ( rule__XAttributeType__Group_16__0 )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==50) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1761:2: rule__XAttributeType__Group_16__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_16__0_in_rule__XAttributeType__Group__163612);
                    rule__XAttributeType__Group_16__0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGroup_16()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__17_in_rule__XAttributeType__Group__163622);
            rule__XAttributeType__Group__17();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__16


    // $ANTLR start rule__XAttributeType__Group__17
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1772:1: rule__XAttributeType__Group__17 : ( ( rule__XAttributeType__Group_17__0 )? ) rule__XAttributeType__Group__18 ;
    public final void rule__XAttributeType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1776:1: ( ( ( rule__XAttributeType__Group_17__0 )? ) rule__XAttributeType__Group__18 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1777:1: ( ( rule__XAttributeType__Group_17__0 )? ) rule__XAttributeType__Group__18
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1777:1: ( ( rule__XAttributeType__Group_17__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1778:1: ( rule__XAttributeType__Group_17__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_17()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1779:1: ( rule__XAttributeType__Group_17__0 )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==51) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1779:2: rule__XAttributeType__Group_17__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_17__0_in_rule__XAttributeType__Group__173650);
                    rule__XAttributeType__Group_17__0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGroup_17()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__18_in_rule__XAttributeType__Group__173660);
            rule__XAttributeType__Group__18();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__17


    // $ANTLR start rule__XAttributeType__Group__18
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1790:1: rule__XAttributeType__Group__18 : ( '}' ) ;
    public final void rule__XAttributeType__Group__18() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1794:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1795:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1795:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1796:1: '}'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getRightCurlyBracketKeyword_18()); 
            }
            match(input,37,FollowSets000.FOLLOW_37_in_rule__XAttributeType__Group__183689); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getRightCurlyBracketKeyword_18()); 
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
    // $ANTLR end rule__XAttributeType__Group__18


    // $ANTLR start rule__XAttributeType__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1847:1: rule__XAttributeType__Group_2__0 : ( 'extends' ) rule__XAttributeType__Group_2__1 ;
    public final void rule__XAttributeType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1851:1: ( ( 'extends' ) rule__XAttributeType__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1852:1: ( 'extends' ) rule__XAttributeType__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1852:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1853:1: 'extends'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0()); 
            }
            match(input,38,FollowSets000.FOLLOW_38_in_rule__XAttributeType__Group_2__03763); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_2__1_in_rule__XAttributeType__Group_2__03773);
            rule__XAttributeType__Group_2__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group_2__0


    // $ANTLR start rule__XAttributeType__Group_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1867:1: rule__XAttributeType__Group_2__1 : ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) ) ;
    public final void rule__XAttributeType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1871:1: ( ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1872:1: ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1872:1: ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1873:1: ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAssignment_2_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1874:1: ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1874:2: rule__XAttributeType__BaseAttributeTypeAssignment_2_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__BaseAttributeTypeAssignment_2_1_in_rule__XAttributeType__Group_2__13801);
            rule__XAttributeType__BaseAttributeTypeAssignment_2_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAssignment_2_1()); 
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
    // $ANTLR end rule__XAttributeType__Group_2__1


    // $ANTLR start rule__XAttributeType__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1888:1: rule__XAttributeType__Group_3__0 : ( 'overrides' ) rule__XAttributeType__Group_3__1 ;
    public final void rule__XAttributeType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1892:1: ( ( 'overrides' ) rule__XAttributeType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1893:1: ( 'overrides' ) rule__XAttributeType__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1893:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1894:1: 'overrides'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0()); 
            }
            match(input,46,FollowSets000.FOLLOW_46_in_rule__XAttributeType__Group_3__03840); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_3__1_in_rule__XAttributeType__Group_3__03850);
            rule__XAttributeType__Group_3__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group_3__0


    // $ANTLR start rule__XAttributeType__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1908:1: rule__XAttributeType__Group_3__1 : ( ( rule__XAttributeType__OverrideAssignment_3_1 ) ) ;
    public final void rule__XAttributeType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1912:1: ( ( ( rule__XAttributeType__OverrideAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1913:1: ( ( rule__XAttributeType__OverrideAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1913:1: ( ( rule__XAttributeType__OverrideAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1914:1: ( rule__XAttributeType__OverrideAssignment_3_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getOverrideAssignment_3_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1915:1: ( rule__XAttributeType__OverrideAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1915:2: rule__XAttributeType__OverrideAssignment_3_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__OverrideAssignment_3_1_in_rule__XAttributeType__Group_3__13878);
            rule__XAttributeType__OverrideAssignment_3_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getOverrideAssignment_3_1()); 
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
    // $ANTLR end rule__XAttributeType__Group_3__1


    // $ANTLR start rule__XAttributeType__Group_13__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1929:1: rule__XAttributeType__Group_13__0 : ( 'taggerId' ) rule__XAttributeType__Group_13__1 ;
    public final void rule__XAttributeType__Group_13__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1933:1: ( ( 'taggerId' ) rule__XAttributeType__Group_13__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1934:1: ( 'taggerId' ) rule__XAttributeType__Group_13__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1934:1: ( 'taggerId' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1935:1: 'taggerId'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_13_0()); 
            }
            match(input,47,FollowSets000.FOLLOW_47_in_rule__XAttributeType__Group_13__03917); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_13_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_13__1_in_rule__XAttributeType__Group_13__03927);
            rule__XAttributeType__Group_13__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group_13__0


    // $ANTLR start rule__XAttributeType__Group_13__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1949:1: rule__XAttributeType__Group_13__1 : ( ( rule__XAttributeType__TaggerIdAssignment_13_1 ) ) ;
    public final void rule__XAttributeType__Group_13__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1953:1: ( ( ( rule__XAttributeType__TaggerIdAssignment_13_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1954:1: ( ( rule__XAttributeType__TaggerIdAssignment_13_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1954:1: ( ( rule__XAttributeType__TaggerIdAssignment_13_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1955:1: ( rule__XAttributeType__TaggerIdAssignment_13_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getTaggerIdAssignment_13_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1956:1: ( rule__XAttributeType__TaggerIdAssignment_13_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1956:2: rule__XAttributeType__TaggerIdAssignment_13_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__TaggerIdAssignment_13_1_in_rule__XAttributeType__Group_13__13955);
            rule__XAttributeType__TaggerIdAssignment_13_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getTaggerIdAssignment_13_1()); 
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
    // $ANTLR end rule__XAttributeType__Group_13__1


    // $ANTLR start rule__XAttributeType__Group_14__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1970:1: rule__XAttributeType__Group_14__0 : ( 'enumType' ) rule__XAttributeType__Group_14__1 ;
    public final void rule__XAttributeType__Group_14__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1974:1: ( ( 'enumType' ) rule__XAttributeType__Group_14__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1975:1: ( 'enumType' ) rule__XAttributeType__Group_14__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1975:1: ( 'enumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1976:1: 'enumType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_14_0()); 
            }
            match(input,48,FollowSets000.FOLLOW_48_in_rule__XAttributeType__Group_14__03994); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_14_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_14__1_in_rule__XAttributeType__Group_14__04004);
            rule__XAttributeType__Group_14__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group_14__0


    // $ANTLR start rule__XAttributeType__Group_14__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1990:1: rule__XAttributeType__Group_14__1 : ( ( rule__XAttributeType__EnumTypeAssignment_14_1 ) ) ;
    public final void rule__XAttributeType__Group_14__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1994:1: ( ( ( rule__XAttributeType__EnumTypeAssignment_14_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1995:1: ( ( rule__XAttributeType__EnumTypeAssignment_14_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1995:1: ( ( rule__XAttributeType__EnumTypeAssignment_14_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1996:1: ( rule__XAttributeType__EnumTypeAssignment_14_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getEnumTypeAssignment_14_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1997:1: ( rule__XAttributeType__EnumTypeAssignment_14_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1997:2: rule__XAttributeType__EnumTypeAssignment_14_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__EnumTypeAssignment_14_1_in_rule__XAttributeType__Group_14__14032);
            rule__XAttributeType__EnumTypeAssignment_14_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getEnumTypeAssignment_14_1()); 
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
    // $ANTLR end rule__XAttributeType__Group_14__1


    // $ANTLR start rule__XAttributeType__Group_15__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2011:1: rule__XAttributeType__Group_15__0 : ( 'description' ) rule__XAttributeType__Group_15__1 ;
    public final void rule__XAttributeType__Group_15__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2015:1: ( ( 'description' ) rule__XAttributeType__Group_15__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2016:1: ( 'description' ) rule__XAttributeType__Group_15__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2016:1: ( 'description' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2017:1: 'description'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_15_0()); 
            }
            match(input,49,FollowSets000.FOLLOW_49_in_rule__XAttributeType__Group_15__04071); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_15_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_15__1_in_rule__XAttributeType__Group_15__04081);
            rule__XAttributeType__Group_15__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group_15__0


    // $ANTLR start rule__XAttributeType__Group_15__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2031:1: rule__XAttributeType__Group_15__1 : ( ( rule__XAttributeType__DescriptionAssignment_15_1 ) ) ;
    public final void rule__XAttributeType__Group_15__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2035:1: ( ( ( rule__XAttributeType__DescriptionAssignment_15_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2036:1: ( ( rule__XAttributeType__DescriptionAssignment_15_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2036:1: ( ( rule__XAttributeType__DescriptionAssignment_15_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2037:1: ( rule__XAttributeType__DescriptionAssignment_15_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDescriptionAssignment_15_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2038:1: ( rule__XAttributeType__DescriptionAssignment_15_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2038:2: rule__XAttributeType__DescriptionAssignment_15_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__DescriptionAssignment_15_1_in_rule__XAttributeType__Group_15__14109);
            rule__XAttributeType__DescriptionAssignment_15_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDescriptionAssignment_15_1()); 
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
    // $ANTLR end rule__XAttributeType__Group_15__1


    // $ANTLR start rule__XAttributeType__Group_16__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2052:1: rule__XAttributeType__Group_16__0 : ( 'defaultValue' ) rule__XAttributeType__Group_16__1 ;
    public final void rule__XAttributeType__Group_16__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2056:1: ( ( 'defaultValue' ) rule__XAttributeType__Group_16__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2057:1: ( 'defaultValue' ) rule__XAttributeType__Group_16__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2057:1: ( 'defaultValue' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2058:1: 'defaultValue'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_16_0()); 
            }
            match(input,50,FollowSets000.FOLLOW_50_in_rule__XAttributeType__Group_16__04148); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_16_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_16__1_in_rule__XAttributeType__Group_16__04158);
            rule__XAttributeType__Group_16__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group_16__0


    // $ANTLR start rule__XAttributeType__Group_16__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2072:1: rule__XAttributeType__Group_16__1 : ( ( rule__XAttributeType__DefaultValueAssignment_16_1 ) ) ;
    public final void rule__XAttributeType__Group_16__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2076:1: ( ( ( rule__XAttributeType__DefaultValueAssignment_16_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2077:1: ( ( rule__XAttributeType__DefaultValueAssignment_16_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2077:1: ( ( rule__XAttributeType__DefaultValueAssignment_16_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2078:1: ( rule__XAttributeType__DefaultValueAssignment_16_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDefaultValueAssignment_16_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2079:1: ( rule__XAttributeType__DefaultValueAssignment_16_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2079:2: rule__XAttributeType__DefaultValueAssignment_16_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__DefaultValueAssignment_16_1_in_rule__XAttributeType__Group_16__14186);
            rule__XAttributeType__DefaultValueAssignment_16_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDefaultValueAssignment_16_1()); 
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
    // $ANTLR end rule__XAttributeType__Group_16__1


    // $ANTLR start rule__XAttributeType__Group_17__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2093:1: rule__XAttributeType__Group_17__0 : ( 'fileExtension' ) rule__XAttributeType__Group_17__1 ;
    public final void rule__XAttributeType__Group_17__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2097:1: ( ( 'fileExtension' ) rule__XAttributeType__Group_17__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2098:1: ( 'fileExtension' ) rule__XAttributeType__Group_17__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2098:1: ( 'fileExtension' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2099:1: 'fileExtension'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_17_0()); 
            }
            match(input,51,FollowSets000.FOLLOW_51_in_rule__XAttributeType__Group_17__04225); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_17_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_17__1_in_rule__XAttributeType__Group_17__04235);
            rule__XAttributeType__Group_17__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group_17__0


    // $ANTLR start rule__XAttributeType__Group_17__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2113:1: rule__XAttributeType__Group_17__1 : ( ( rule__XAttributeType__FileExtensionAssignment_17_1 ) ) ;
    public final void rule__XAttributeType__Group_17__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2117:1: ( ( ( rule__XAttributeType__FileExtensionAssignment_17_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2118:1: ( ( rule__XAttributeType__FileExtensionAssignment_17_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2118:1: ( ( rule__XAttributeType__FileExtensionAssignment_17_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2119:1: ( rule__XAttributeType__FileExtensionAssignment_17_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getFileExtensionAssignment_17_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2120:1: ( rule__XAttributeType__FileExtensionAssignment_17_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2120:2: rule__XAttributeType__FileExtensionAssignment_17_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__FileExtensionAssignment_17_1_in_rule__XAttributeType__Group_17__14263);
            rule__XAttributeType__FileExtensionAssignment_17_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getFileExtensionAssignment_17_1()); 
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
    // $ANTLR end rule__XAttributeType__Group_17__1


    // $ANTLR start rule__XOseeEnumType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2134:1: rule__XOseeEnumType__Group__0 : ( 'oseeEnumType' ) rule__XOseeEnumType__Group__1 ;
    public final void rule__XOseeEnumType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2138:1: ( ( 'oseeEnumType' ) rule__XOseeEnumType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2139:1: ( 'oseeEnumType' ) rule__XOseeEnumType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2139:1: ( 'oseeEnumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2140:1: 'oseeEnumType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 
            }
            match(input,52,FollowSets000.FOLLOW_52_in_rule__XOseeEnumType__Group__04302); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__1_in_rule__XOseeEnumType__Group__04312);
            rule__XOseeEnumType__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumType__Group__0


    // $ANTLR start rule__XOseeEnumType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2154:1: rule__XOseeEnumType__Group__1 : ( ( rule__XOseeEnumType__NameAssignment_1 ) ) rule__XOseeEnumType__Group__2 ;
    public final void rule__XOseeEnumType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2158:1: ( ( ( rule__XOseeEnumType__NameAssignment_1 ) ) rule__XOseeEnumType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2159:1: ( ( rule__XOseeEnumType__NameAssignment_1 ) ) rule__XOseeEnumType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2159:1: ( ( rule__XOseeEnumType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2160:1: ( rule__XOseeEnumType__NameAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getNameAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2161:1: ( rule__XOseeEnumType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2161:2: rule__XOseeEnumType__NameAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__NameAssignment_1_in_rule__XOseeEnumType__Group__14340);
            rule__XOseeEnumType__NameAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getNameAssignment_1()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__2_in_rule__XOseeEnumType__Group__14349);
            rule__XOseeEnumType__Group__2();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumType__Group__1


    // $ANTLR start rule__XOseeEnumType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2172:1: rule__XOseeEnumType__Group__2 : ( '{' ) rule__XOseeEnumType__Group__3 ;
    public final void rule__XOseeEnumType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2176:1: ( ( '{' ) rule__XOseeEnumType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2177:1: ( '{' ) rule__XOseeEnumType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2177:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2178:1: '{'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2()); 
            }
            match(input,35,FollowSets000.FOLLOW_35_in_rule__XOseeEnumType__Group__24378); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__3_in_rule__XOseeEnumType__Group__24388);
            rule__XOseeEnumType__Group__3();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumType__Group__2


    // $ANTLR start rule__XOseeEnumType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2192:1: rule__XOseeEnumType__Group__3 : ( 'guid' ) rule__XOseeEnumType__Group__4 ;
    public final void rule__XOseeEnumType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2196:1: ( ( 'guid' ) rule__XOseeEnumType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2197:1: ( 'guid' ) rule__XOseeEnumType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2197:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2198:1: 'guid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3()); 
            }
            match(input,36,FollowSets000.FOLLOW_36_in_rule__XOseeEnumType__Group__34417); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__4_in_rule__XOseeEnumType__Group__34427);
            rule__XOseeEnumType__Group__4();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumType__Group__3


    // $ANTLR start rule__XOseeEnumType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2212:1: rule__XOseeEnumType__Group__4 : ( ( rule__XOseeEnumType__TypeGuidAssignment_4 ) ) rule__XOseeEnumType__Group__5 ;
    public final void rule__XOseeEnumType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2216:1: ( ( ( rule__XOseeEnumType__TypeGuidAssignment_4 ) ) rule__XOseeEnumType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2217:1: ( ( rule__XOseeEnumType__TypeGuidAssignment_4 ) ) rule__XOseeEnumType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2217:1: ( ( rule__XOseeEnumType__TypeGuidAssignment_4 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2218:1: ( rule__XOseeEnumType__TypeGuidAssignment_4 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidAssignment_4()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2219:1: ( rule__XOseeEnumType__TypeGuidAssignment_4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2219:2: rule__XOseeEnumType__TypeGuidAssignment_4
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__TypeGuidAssignment_4_in_rule__XOseeEnumType__Group__44455);
            rule__XOseeEnumType__TypeGuidAssignment_4();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidAssignment_4()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__5_in_rule__XOseeEnumType__Group__44464);
            rule__XOseeEnumType__Group__5();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumType__Group__4


    // $ANTLR start rule__XOseeEnumType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2230:1: rule__XOseeEnumType__Group__5 : ( ( rule__XOseeEnumType__EnumEntriesAssignment_5 )* ) rule__XOseeEnumType__Group__6 ;
    public final void rule__XOseeEnumType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2234:1: ( ( ( rule__XOseeEnumType__EnumEntriesAssignment_5 )* ) rule__XOseeEnumType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2235:1: ( ( rule__XOseeEnumType__EnumEntriesAssignment_5 )* ) rule__XOseeEnumType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2235:1: ( ( rule__XOseeEnumType__EnumEntriesAssignment_5 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2236:1: ( rule__XOseeEnumType__EnumEntriesAssignment_5 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesAssignment_5()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2237:1: ( rule__XOseeEnumType__EnumEntriesAssignment_5 )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==53) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2237:2: rule__XOseeEnumType__EnumEntriesAssignment_5
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__EnumEntriesAssignment_5_in_rule__XOseeEnumType__Group__54492);
            	    rule__XOseeEnumType__EnumEntriesAssignment_5();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesAssignment_5()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__6_in_rule__XOseeEnumType__Group__54502);
            rule__XOseeEnumType__Group__6();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumType__Group__5


    // $ANTLR start rule__XOseeEnumType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2248:1: rule__XOseeEnumType__Group__6 : ( '}' ) ;
    public final void rule__XOseeEnumType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2252:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2253:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2253:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2254:1: '}'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getRightCurlyBracketKeyword_6()); 
            }
            match(input,37,FollowSets000.FOLLOW_37_in_rule__XOseeEnumType__Group__64531); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getRightCurlyBracketKeyword_6()); 
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
    // $ANTLR end rule__XOseeEnumType__Group__6


    // $ANTLR start rule__XOseeEnumEntry__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2281:1: rule__XOseeEnumEntry__Group__0 : ( 'entry' ) rule__XOseeEnumEntry__Group__1 ;
    public final void rule__XOseeEnumEntry__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2285:1: ( ( 'entry' ) rule__XOseeEnumEntry__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2286:1: ( 'entry' ) rule__XOseeEnumEntry__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2286:1: ( 'entry' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2287:1: 'entry'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0()); 
            }
            match(input,53,FollowSets000.FOLLOW_53_in_rule__XOseeEnumEntry__Group__04581); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__1_in_rule__XOseeEnumEntry__Group__04591);
            rule__XOseeEnumEntry__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumEntry__Group__0


    // $ANTLR start rule__XOseeEnumEntry__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2301:1: rule__XOseeEnumEntry__Group__1 : ( ( rule__XOseeEnumEntry__NameAssignment_1 ) ) rule__XOseeEnumEntry__Group__2 ;
    public final void rule__XOseeEnumEntry__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2305:1: ( ( ( rule__XOseeEnumEntry__NameAssignment_1 ) ) rule__XOseeEnumEntry__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2306:1: ( ( rule__XOseeEnumEntry__NameAssignment_1 ) ) rule__XOseeEnumEntry__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2306:1: ( ( rule__XOseeEnumEntry__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2307:1: ( rule__XOseeEnumEntry__NameAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getNameAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2308:1: ( rule__XOseeEnumEntry__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2308:2: rule__XOseeEnumEntry__NameAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__NameAssignment_1_in_rule__XOseeEnumEntry__Group__14619);
            rule__XOseeEnumEntry__NameAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getNameAssignment_1()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__2_in_rule__XOseeEnumEntry__Group__14628);
            rule__XOseeEnumEntry__Group__2();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumEntry__Group__1


    // $ANTLR start rule__XOseeEnumEntry__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2319:1: rule__XOseeEnumEntry__Group__2 : ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? ) rule__XOseeEnumEntry__Group__3 ;
    public final void rule__XOseeEnumEntry__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2323:1: ( ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? ) rule__XOseeEnumEntry__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2324:1: ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? ) rule__XOseeEnumEntry__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2324:1: ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2325:1: ( rule__XOseeEnumEntry__OrdinalAssignment_2 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getOrdinalAssignment_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2326:1: ( rule__XOseeEnumEntry__OrdinalAssignment_2 )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==RULE_WHOLE_NUM_STR) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2326:2: rule__XOseeEnumEntry__OrdinalAssignment_2
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__OrdinalAssignment_2_in_rule__XOseeEnumEntry__Group__24656);
                    rule__XOseeEnumEntry__OrdinalAssignment_2();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getOrdinalAssignment_2()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__3_in_rule__XOseeEnumEntry__Group__24666);
            rule__XOseeEnumEntry__Group__3();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumEntry__Group__2


    // $ANTLR start rule__XOseeEnumEntry__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2337:1: rule__XOseeEnumEntry__Group__3 : ( ( rule__XOseeEnumEntry__Group_3__0 )? ) ;
    public final void rule__XOseeEnumEntry__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2341:1: ( ( ( rule__XOseeEnumEntry__Group_3__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2342:1: ( ( rule__XOseeEnumEntry__Group_3__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2342:1: ( ( rule__XOseeEnumEntry__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2343:1: ( rule__XOseeEnumEntry__Group_3__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getGroup_3()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2344:1: ( rule__XOseeEnumEntry__Group_3__0 )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==54) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2344:2: rule__XOseeEnumEntry__Group_3__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group_3__0_in_rule__XOseeEnumEntry__Group__34694);
                    rule__XOseeEnumEntry__Group_3__0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getGroup_3()); 
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
    // $ANTLR end rule__XOseeEnumEntry__Group__3


    // $ANTLR start rule__XOseeEnumEntry__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2362:1: rule__XOseeEnumEntry__Group_3__0 : ( 'entryGuid' ) rule__XOseeEnumEntry__Group_3__1 ;
    public final void rule__XOseeEnumEntry__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2366:1: ( ( 'entryGuid' ) rule__XOseeEnumEntry__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2367:1: ( 'entryGuid' ) rule__XOseeEnumEntry__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2367:1: ( 'entryGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2368:1: 'entryGuid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0()); 
            }
            match(input,54,FollowSets000.FOLLOW_54_in_rule__XOseeEnumEntry__Group_3__04738); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group_3__1_in_rule__XOseeEnumEntry__Group_3__04748);
            rule__XOseeEnumEntry__Group_3__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumEntry__Group_3__0


    // $ANTLR start rule__XOseeEnumEntry__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2382:1: rule__XOseeEnumEntry__Group_3__1 : ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) ) ;
    public final void rule__XOseeEnumEntry__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2386:1: ( ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2387:1: ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2387:1: ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2388:1: ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidAssignment_3_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2389:1: ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2389:2: rule__XOseeEnumEntry__EntryGuidAssignment_3_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__EntryGuidAssignment_3_1_in_rule__XOseeEnumEntry__Group_3__14776);
            rule__XOseeEnumEntry__EntryGuidAssignment_3_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidAssignment_3_1()); 
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
    // $ANTLR end rule__XOseeEnumEntry__Group_3__1


    // $ANTLR start rule__XOseeEnumOverride__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2403:1: rule__XOseeEnumOverride__Group__0 : ( 'overrides enum' ) rule__XOseeEnumOverride__Group__1 ;
    public final void rule__XOseeEnumOverride__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2407:1: ( ( 'overrides enum' ) rule__XOseeEnumOverride__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2408:1: ( 'overrides enum' ) rule__XOseeEnumOverride__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2408:1: ( 'overrides enum' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2409:1: 'overrides enum'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0()); 
            }
            match(input,55,FollowSets000.FOLLOW_55_in_rule__XOseeEnumOverride__Group__04815); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__1_in_rule__XOseeEnumOverride__Group__04825);
            rule__XOseeEnumOverride__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumOverride__Group__0


    // $ANTLR start rule__XOseeEnumOverride__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2423:1: rule__XOseeEnumOverride__Group__1 : ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) ) rule__XOseeEnumOverride__Group__2 ;
    public final void rule__XOseeEnumOverride__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2427:1: ( ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) ) rule__XOseeEnumOverride__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2428:1: ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) ) rule__XOseeEnumOverride__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2428:1: ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2429:1: ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2430:1: ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2430:2: rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1_in_rule__XOseeEnumOverride__Group__14853);
            rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeAssignment_1()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__2_in_rule__XOseeEnumOverride__Group__14862);
            rule__XOseeEnumOverride__Group__2();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumOverride__Group__1


    // $ANTLR start rule__XOseeEnumOverride__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2441:1: rule__XOseeEnumOverride__Group__2 : ( '{' ) rule__XOseeEnumOverride__Group__3 ;
    public final void rule__XOseeEnumOverride__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2445:1: ( ( '{' ) rule__XOseeEnumOverride__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2446:1: ( '{' ) rule__XOseeEnumOverride__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2446:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2447:1: '{'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2()); 
            }
            match(input,35,FollowSets000.FOLLOW_35_in_rule__XOseeEnumOverride__Group__24891); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__3_in_rule__XOseeEnumOverride__Group__24901);
            rule__XOseeEnumOverride__Group__3();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumOverride__Group__2


    // $ANTLR start rule__XOseeEnumOverride__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2461:1: rule__XOseeEnumOverride__Group__3 : ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? ) rule__XOseeEnumOverride__Group__4 ;
    public final void rule__XOseeEnumOverride__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2465:1: ( ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? ) rule__XOseeEnumOverride__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2466:1: ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? ) rule__XOseeEnumOverride__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2466:1: ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2467:1: ( rule__XOseeEnumOverride__InheritAllAssignment_3 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllAssignment_3()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2468:1: ( rule__XOseeEnumOverride__InheritAllAssignment_3 )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==66) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2468:2: rule__XOseeEnumOverride__InheritAllAssignment_3
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__InheritAllAssignment_3_in_rule__XOseeEnumOverride__Group__34929);
                    rule__XOseeEnumOverride__InheritAllAssignment_3();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllAssignment_3()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__4_in_rule__XOseeEnumOverride__Group__34939);
            rule__XOseeEnumOverride__Group__4();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumOverride__Group__3


    // $ANTLR start rule__XOseeEnumOverride__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2479:1: rule__XOseeEnumOverride__Group__4 : ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* ) rule__XOseeEnumOverride__Group__5 ;
    public final void rule__XOseeEnumOverride__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2483:1: ( ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* ) rule__XOseeEnumOverride__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2484:1: ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* ) rule__XOseeEnumOverride__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2484:1: ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2485:1: ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsAssignment_4()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2486:1: ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( ((LA28_0>=56 && LA28_0<=57)) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2486:2: rule__XOseeEnumOverride__OverrideOptionsAssignment_4
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__OverrideOptionsAssignment_4_in_rule__XOseeEnumOverride__Group__44967);
            	    rule__XOseeEnumOverride__OverrideOptionsAssignment_4();
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsAssignment_4()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__5_in_rule__XOseeEnumOverride__Group__44977);
            rule__XOseeEnumOverride__Group__5();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumOverride__Group__4


    // $ANTLR start rule__XOseeEnumOverride__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2497:1: rule__XOseeEnumOverride__Group__5 : ( '}' ) ;
    public final void rule__XOseeEnumOverride__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2501:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2502:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2502:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2503:1: '}'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5()); 
            }
            match(input,37,FollowSets000.FOLLOW_37_in_rule__XOseeEnumOverride__Group__55006); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5()); 
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
    // $ANTLR end rule__XOseeEnumOverride__Group__5


    // $ANTLR start rule__AddEnum__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2528:1: rule__AddEnum__Group__0 : ( 'add' ) rule__AddEnum__Group__1 ;
    public final void rule__AddEnum__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2532:1: ( ( 'add' ) rule__AddEnum__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2533:1: ( 'add' ) rule__AddEnum__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2533:1: ( 'add' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2534:1: 'add'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getAddKeyword_0()); 
            }
            match(input,56,FollowSets000.FOLLOW_56_in_rule__AddEnum__Group__05054); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getAddKeyword_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__1_in_rule__AddEnum__Group__05064);
            rule__AddEnum__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2548:1: rule__AddEnum__Group__1 : ( ( rule__AddEnum__EnumEntryAssignment_1 ) ) rule__AddEnum__Group__2 ;
    public final void rule__AddEnum__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2552:1: ( ( ( rule__AddEnum__EnumEntryAssignment_1 ) ) rule__AddEnum__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2553:1: ( ( rule__AddEnum__EnumEntryAssignment_1 ) ) rule__AddEnum__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2553:1: ( ( rule__AddEnum__EnumEntryAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2554:1: ( rule__AddEnum__EnumEntryAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getEnumEntryAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2555:1: ( rule__AddEnum__EnumEntryAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2555:2: rule__AddEnum__EnumEntryAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__EnumEntryAssignment_1_in_rule__AddEnum__Group__15092);
            rule__AddEnum__EnumEntryAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getEnumEntryAssignment_1()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__2_in_rule__AddEnum__Group__15101);
            rule__AddEnum__Group__2();
            _fsp--;
            if (failed) return ;

            }

        }
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2566:1: rule__AddEnum__Group__2 : ( ( rule__AddEnum__OrdinalAssignment_2 )? ) rule__AddEnum__Group__3 ;
    public final void rule__AddEnum__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2570:1: ( ( ( rule__AddEnum__OrdinalAssignment_2 )? ) rule__AddEnum__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2571:1: ( ( rule__AddEnum__OrdinalAssignment_2 )? ) rule__AddEnum__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2571:1: ( ( rule__AddEnum__OrdinalAssignment_2 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2572:1: ( rule__AddEnum__OrdinalAssignment_2 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getOrdinalAssignment_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2573:1: ( rule__AddEnum__OrdinalAssignment_2 )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==RULE_WHOLE_NUM_STR) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2573:2: rule__AddEnum__OrdinalAssignment_2
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__AddEnum__OrdinalAssignment_2_in_rule__AddEnum__Group__25129);
                    rule__AddEnum__OrdinalAssignment_2();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getOrdinalAssignment_2()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__3_in_rule__AddEnum__Group__25139);
            rule__AddEnum__Group__3();
            _fsp--;
            if (failed) return ;

            }

        }
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2584:1: rule__AddEnum__Group__3 : ( ( rule__AddEnum__Group_3__0 )? ) ;
    public final void rule__AddEnum__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2588:1: ( ( ( rule__AddEnum__Group_3__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2589:1: ( ( rule__AddEnum__Group_3__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2589:1: ( ( rule__AddEnum__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2590:1: ( rule__AddEnum__Group_3__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getGroup_3()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2591:1: ( rule__AddEnum__Group_3__0 )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==54) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2591:2: rule__AddEnum__Group_3__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group_3__0_in_rule__AddEnum__Group__35167);
                    rule__AddEnum__Group_3__0();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getGroup_3()); 
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
    // $ANTLR end rule__AddEnum__Group__3


    // $ANTLR start rule__AddEnum__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2609:1: rule__AddEnum__Group_3__0 : ( 'entryGuid' ) rule__AddEnum__Group_3__1 ;
    public final void rule__AddEnum__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2613:1: ( ( 'entryGuid' ) rule__AddEnum__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2614:1: ( 'entryGuid' ) rule__AddEnum__Group_3__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2614:1: ( 'entryGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2615:1: 'entryGuid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0()); 
            }
            match(input,54,FollowSets000.FOLLOW_54_in_rule__AddEnum__Group_3__05211); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group_3__1_in_rule__AddEnum__Group_3__05221);
            rule__AddEnum__Group_3__1();
            _fsp--;
            if (failed) return ;

            }

        }
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2629:1: rule__AddEnum__Group_3__1 : ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) ) ;
    public final void rule__AddEnum__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2633:1: ( ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2634:1: ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2634:1: ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2635:1: ( rule__AddEnum__EntryGuidAssignment_3_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getEntryGuidAssignment_3_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2636:1: ( rule__AddEnum__EntryGuidAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2636:2: rule__AddEnum__EntryGuidAssignment_3_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__EntryGuidAssignment_3_1_in_rule__AddEnum__Group_3__15249);
            rule__AddEnum__EntryGuidAssignment_3_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getEntryGuidAssignment_3_1()); 
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
    // $ANTLR end rule__AddEnum__Group_3__1


    // $ANTLR start rule__RemoveEnum__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2650:1: rule__RemoveEnum__Group__0 : ( 'remove' ) rule__RemoveEnum__Group__1 ;
    public final void rule__RemoveEnum__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2654:1: ( ( 'remove' ) rule__RemoveEnum__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2655:1: ( 'remove' ) rule__RemoveEnum__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2655:1: ( 'remove' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2656:1: 'remove'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0()); 
            }
            match(input,57,FollowSets000.FOLLOW_57_in_rule__RemoveEnum__Group__05288); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__RemoveEnum__Group__1_in_rule__RemoveEnum__Group__05298);
            rule__RemoveEnum__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2670:1: rule__RemoveEnum__Group__1 : ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) ) ;
    public final void rule__RemoveEnum__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2674:1: ( ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2675:1: ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2675:1: ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2676:1: ( rule__RemoveEnum__EnumEntryAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumAccess().getEnumEntryAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2677:1: ( rule__RemoveEnum__EnumEntryAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2677:2: rule__RemoveEnum__EnumEntryAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__RemoveEnum__EnumEntryAssignment_1_in_rule__RemoveEnum__Group__15326);
            rule__RemoveEnum__EnumEntryAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getRemoveEnumAccess().getEnumEntryAssignment_1()); 
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
    // $ANTLR end rule__RemoveEnum__Group__1


    // $ANTLR start rule__XRelationType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2691:1: rule__XRelationType__Group__0 : ( 'relationType' ) rule__XRelationType__Group__1 ;
    public final void rule__XRelationType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2695:1: ( ( 'relationType' ) rule__XRelationType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2696:1: ( 'relationType' ) rule__XRelationType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2696:1: ( 'relationType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2697:1: 'relationType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0()); 
            }
            match(input,58,FollowSets000.FOLLOW_58_in_rule__XRelationType__Group__05365); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__1_in_rule__XRelationType__Group__05375);
            rule__XRelationType__Group__1();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__0


    // $ANTLR start rule__XRelationType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2711:1: rule__XRelationType__Group__1 : ( ( rule__XRelationType__NameAssignment_1 ) ) rule__XRelationType__Group__2 ;
    public final void rule__XRelationType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2715:1: ( ( ( rule__XRelationType__NameAssignment_1 ) ) rule__XRelationType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2716:1: ( ( rule__XRelationType__NameAssignment_1 ) ) rule__XRelationType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2716:1: ( ( rule__XRelationType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2717:1: ( rule__XRelationType__NameAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getNameAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2718:1: ( rule__XRelationType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2718:2: rule__XRelationType__NameAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__NameAssignment_1_in_rule__XRelationType__Group__15403);
            rule__XRelationType__NameAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getNameAssignment_1()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__2_in_rule__XRelationType__Group__15412);
            rule__XRelationType__Group__2();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__1


    // $ANTLR start rule__XRelationType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2729:1: rule__XRelationType__Group__2 : ( '{' ) rule__XRelationType__Group__3 ;
    public final void rule__XRelationType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2733:1: ( ( '{' ) rule__XRelationType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2734:1: ( '{' ) rule__XRelationType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2734:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2735:1: '{'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 
            }
            match(input,35,FollowSets000.FOLLOW_35_in_rule__XRelationType__Group__25441); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__3_in_rule__XRelationType__Group__25451);
            rule__XRelationType__Group__3();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__2


    // $ANTLR start rule__XRelationType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2749:1: rule__XRelationType__Group__3 : ( 'guid' ) rule__XRelationType__Group__4 ;
    public final void rule__XRelationType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2753:1: ( ( 'guid' ) rule__XRelationType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2754:1: ( 'guid' ) rule__XRelationType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2754:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2755:1: 'guid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getGuidKeyword_3()); 
            }
            match(input,36,FollowSets000.FOLLOW_36_in_rule__XRelationType__Group__35480); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getGuidKeyword_3()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__4_in_rule__XRelationType__Group__35490);
            rule__XRelationType__Group__4();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__3


    // $ANTLR start rule__XRelationType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2769:1: rule__XRelationType__Group__4 : ( ( rule__XRelationType__TypeGuidAssignment_4 ) ) rule__XRelationType__Group__5 ;
    public final void rule__XRelationType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2773:1: ( ( ( rule__XRelationType__TypeGuidAssignment_4 ) ) rule__XRelationType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2774:1: ( ( rule__XRelationType__TypeGuidAssignment_4 ) ) rule__XRelationType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2774:1: ( ( rule__XRelationType__TypeGuidAssignment_4 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2775:1: ( rule__XRelationType__TypeGuidAssignment_4 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getTypeGuidAssignment_4()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2776:1: ( rule__XRelationType__TypeGuidAssignment_4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2776:2: rule__XRelationType__TypeGuidAssignment_4
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__TypeGuidAssignment_4_in_rule__XRelationType__Group__45518);
            rule__XRelationType__TypeGuidAssignment_4();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getTypeGuidAssignment_4()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__5_in_rule__XRelationType__Group__45527);
            rule__XRelationType__Group__5();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__4


    // $ANTLR start rule__XRelationType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2787:1: rule__XRelationType__Group__5 : ( 'sideAName' ) rule__XRelationType__Group__6 ;
    public final void rule__XRelationType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2791:1: ( ( 'sideAName' ) rule__XRelationType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2792:1: ( 'sideAName' ) rule__XRelationType__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2792:1: ( 'sideAName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2793:1: 'sideAName'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideANameKeyword_5()); 
            }
            match(input,59,FollowSets000.FOLLOW_59_in_rule__XRelationType__Group__55556); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideANameKeyword_5()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__6_in_rule__XRelationType__Group__55566);
            rule__XRelationType__Group__6();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__5


    // $ANTLR start rule__XRelationType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2807:1: rule__XRelationType__Group__6 : ( ( rule__XRelationType__SideANameAssignment_6 ) ) rule__XRelationType__Group__7 ;
    public final void rule__XRelationType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2811:1: ( ( ( rule__XRelationType__SideANameAssignment_6 ) ) rule__XRelationType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2812:1: ( ( rule__XRelationType__SideANameAssignment_6 ) ) rule__XRelationType__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2812:1: ( ( rule__XRelationType__SideANameAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2813:1: ( rule__XRelationType__SideANameAssignment_6 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideANameAssignment_6()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2814:1: ( rule__XRelationType__SideANameAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2814:2: rule__XRelationType__SideANameAssignment_6
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__SideANameAssignment_6_in_rule__XRelationType__Group__65594);
            rule__XRelationType__SideANameAssignment_6();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideANameAssignment_6()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__7_in_rule__XRelationType__Group__65603);
            rule__XRelationType__Group__7();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__6


    // $ANTLR start rule__XRelationType__Group__7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2825:1: rule__XRelationType__Group__7 : ( 'sideAArtifactType' ) rule__XRelationType__Group__8 ;
    public final void rule__XRelationType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2829:1: ( ( 'sideAArtifactType' ) rule__XRelationType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2830:1: ( 'sideAArtifactType' ) rule__XRelationType__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2830:1: ( 'sideAArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2831:1: 'sideAArtifactType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_7()); 
            }
            match(input,60,FollowSets000.FOLLOW_60_in_rule__XRelationType__Group__75632); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_7()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__8_in_rule__XRelationType__Group__75642);
            rule__XRelationType__Group__8();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__7


    // $ANTLR start rule__XRelationType__Group__8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2845:1: rule__XRelationType__Group__8 : ( ( rule__XRelationType__SideAArtifactTypeAssignment_8 ) ) rule__XRelationType__Group__9 ;
    public final void rule__XRelationType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2849:1: ( ( ( rule__XRelationType__SideAArtifactTypeAssignment_8 ) ) rule__XRelationType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2850:1: ( ( rule__XRelationType__SideAArtifactTypeAssignment_8 ) ) rule__XRelationType__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2850:1: ( ( rule__XRelationType__SideAArtifactTypeAssignment_8 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2851:1: ( rule__XRelationType__SideAArtifactTypeAssignment_8 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeAssignment_8()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2852:1: ( rule__XRelationType__SideAArtifactTypeAssignment_8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2852:2: rule__XRelationType__SideAArtifactTypeAssignment_8
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__SideAArtifactTypeAssignment_8_in_rule__XRelationType__Group__85670);
            rule__XRelationType__SideAArtifactTypeAssignment_8();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeAssignment_8()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__9_in_rule__XRelationType__Group__85679);
            rule__XRelationType__Group__9();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__8


    // $ANTLR start rule__XRelationType__Group__9
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2863:1: rule__XRelationType__Group__9 : ( 'sideBName' ) rule__XRelationType__Group__10 ;
    public final void rule__XRelationType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2867:1: ( ( 'sideBName' ) rule__XRelationType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2868:1: ( 'sideBName' ) rule__XRelationType__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2868:1: ( 'sideBName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2869:1: 'sideBName'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_9()); 
            }
            match(input,61,FollowSets000.FOLLOW_61_in_rule__XRelationType__Group__95708); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_9()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__10_in_rule__XRelationType__Group__95718);
            rule__XRelationType__Group__10();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__9


    // $ANTLR start rule__XRelationType__Group__10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2883:1: rule__XRelationType__Group__10 : ( ( rule__XRelationType__SideBNameAssignment_10 ) ) rule__XRelationType__Group__11 ;
    public final void rule__XRelationType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2887:1: ( ( ( rule__XRelationType__SideBNameAssignment_10 ) ) rule__XRelationType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2888:1: ( ( rule__XRelationType__SideBNameAssignment_10 ) ) rule__XRelationType__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2888:1: ( ( rule__XRelationType__SideBNameAssignment_10 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2889:1: ( rule__XRelationType__SideBNameAssignment_10 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBNameAssignment_10()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2890:1: ( rule__XRelationType__SideBNameAssignment_10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2890:2: rule__XRelationType__SideBNameAssignment_10
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__SideBNameAssignment_10_in_rule__XRelationType__Group__105746);
            rule__XRelationType__SideBNameAssignment_10();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBNameAssignment_10()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__11_in_rule__XRelationType__Group__105755);
            rule__XRelationType__Group__11();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__10


    // $ANTLR start rule__XRelationType__Group__11
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2901:1: rule__XRelationType__Group__11 : ( 'sideBArtifactType' ) rule__XRelationType__Group__12 ;
    public final void rule__XRelationType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2905:1: ( ( 'sideBArtifactType' ) rule__XRelationType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2906:1: ( 'sideBArtifactType' ) rule__XRelationType__Group__12
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2906:1: ( 'sideBArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2907:1: 'sideBArtifactType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_11()); 
            }
            match(input,62,FollowSets000.FOLLOW_62_in_rule__XRelationType__Group__115784); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_11()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__12_in_rule__XRelationType__Group__115794);
            rule__XRelationType__Group__12();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__11


    // $ANTLR start rule__XRelationType__Group__12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2921:1: rule__XRelationType__Group__12 : ( ( rule__XRelationType__SideBArtifactTypeAssignment_12 ) ) rule__XRelationType__Group__13 ;
    public final void rule__XRelationType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2925:1: ( ( ( rule__XRelationType__SideBArtifactTypeAssignment_12 ) ) rule__XRelationType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2926:1: ( ( rule__XRelationType__SideBArtifactTypeAssignment_12 ) ) rule__XRelationType__Group__13
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2926:1: ( ( rule__XRelationType__SideBArtifactTypeAssignment_12 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2927:1: ( rule__XRelationType__SideBArtifactTypeAssignment_12 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeAssignment_12()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2928:1: ( rule__XRelationType__SideBArtifactTypeAssignment_12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2928:2: rule__XRelationType__SideBArtifactTypeAssignment_12
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__SideBArtifactTypeAssignment_12_in_rule__XRelationType__Group__125822);
            rule__XRelationType__SideBArtifactTypeAssignment_12();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeAssignment_12()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__13_in_rule__XRelationType__Group__125831);
            rule__XRelationType__Group__13();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__12


    // $ANTLR start rule__XRelationType__Group__13
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2939:1: rule__XRelationType__Group__13 : ( 'defaultOrderType' ) rule__XRelationType__Group__14 ;
    public final void rule__XRelationType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2943:1: ( ( 'defaultOrderType' ) rule__XRelationType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2944:1: ( 'defaultOrderType' ) rule__XRelationType__Group__14
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2944:1: ( 'defaultOrderType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2945:1: 'defaultOrderType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_13()); 
            }
            match(input,63,FollowSets000.FOLLOW_63_in_rule__XRelationType__Group__135860); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_13()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__14_in_rule__XRelationType__Group__135870);
            rule__XRelationType__Group__14();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__13


    // $ANTLR start rule__XRelationType__Group__14
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2959:1: rule__XRelationType__Group__14 : ( ( rule__XRelationType__DefaultOrderTypeAssignment_14 ) ) rule__XRelationType__Group__15 ;
    public final void rule__XRelationType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2963:1: ( ( ( rule__XRelationType__DefaultOrderTypeAssignment_14 ) ) rule__XRelationType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2964:1: ( ( rule__XRelationType__DefaultOrderTypeAssignment_14 ) ) rule__XRelationType__Group__15
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2964:1: ( ( rule__XRelationType__DefaultOrderTypeAssignment_14 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2965:1: ( rule__XRelationType__DefaultOrderTypeAssignment_14 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeAssignment_14()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2966:1: ( rule__XRelationType__DefaultOrderTypeAssignment_14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2966:2: rule__XRelationType__DefaultOrderTypeAssignment_14
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__DefaultOrderTypeAssignment_14_in_rule__XRelationType__Group__145898);
            rule__XRelationType__DefaultOrderTypeAssignment_14();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeAssignment_14()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__15_in_rule__XRelationType__Group__145907);
            rule__XRelationType__Group__15();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__14


    // $ANTLR start rule__XRelationType__Group__15
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2977:1: rule__XRelationType__Group__15 : ( 'multiplicity' ) rule__XRelationType__Group__16 ;
    public final void rule__XRelationType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2981:1: ( ( 'multiplicity' ) rule__XRelationType__Group__16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2982:1: ( 'multiplicity' ) rule__XRelationType__Group__16
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2982:1: ( 'multiplicity' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2983:1: 'multiplicity'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_15()); 
            }
            match(input,64,FollowSets000.FOLLOW_64_in_rule__XRelationType__Group__155936); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_15()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__16_in_rule__XRelationType__Group__155946);
            rule__XRelationType__Group__16();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__15


    // $ANTLR start rule__XRelationType__Group__16
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2997:1: rule__XRelationType__Group__16 : ( ( rule__XRelationType__MultiplicityAssignment_16 ) ) rule__XRelationType__Group__17 ;
    public final void rule__XRelationType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3001:1: ( ( ( rule__XRelationType__MultiplicityAssignment_16 ) ) rule__XRelationType__Group__17 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3002:1: ( ( rule__XRelationType__MultiplicityAssignment_16 ) ) rule__XRelationType__Group__17
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3002:1: ( ( rule__XRelationType__MultiplicityAssignment_16 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3003:1: ( rule__XRelationType__MultiplicityAssignment_16 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getMultiplicityAssignment_16()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3004:1: ( rule__XRelationType__MultiplicityAssignment_16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3004:2: rule__XRelationType__MultiplicityAssignment_16
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__MultiplicityAssignment_16_in_rule__XRelationType__Group__165974);
            rule__XRelationType__MultiplicityAssignment_16();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getMultiplicityAssignment_16()); 
            }

            }

            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__17_in_rule__XRelationType__Group__165983);
            rule__XRelationType__Group__17();
            _fsp--;
            if (failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelationType__Group__16


    // $ANTLR start rule__XRelationType__Group__17
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3015:1: rule__XRelationType__Group__17 : ( '}' ) ;
    public final void rule__XRelationType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3019:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3020:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3020:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3021:1: '}'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getRightCurlyBracketKeyword_17()); 
            }
            match(input,37,FollowSets000.FOLLOW_37_in_rule__XRelationType__Group__176012); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getRightCurlyBracketKeyword_17()); 
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
    // $ANTLR end rule__XRelationType__Group__17


    // $ANTLR start rule__OseeTypeModel__ImportsAssignment_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3071:1: rule__OseeTypeModel__ImportsAssignment_0 : ( ruleImport ) ;
    public final void rule__OseeTypeModel__ImportsAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3075:1: ( ( ruleImport ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3076:1: ( ruleImport )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3076:1: ( ruleImport )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3077:1: ruleImport
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_06084);
            ruleImport();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0()); 
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
    // $ANTLR end rule__OseeTypeModel__ImportsAssignment_0


    // $ANTLR start rule__OseeTypeModel__ArtifactTypesAssignment_1_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3086:1: rule__OseeTypeModel__ArtifactTypesAssignment_1_0 : ( ruleXArtifactType ) ;
    public final void rule__OseeTypeModel__ArtifactTypesAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3090:1: ( ( ruleXArtifactType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3091:1: ( ruleXArtifactType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3091:1: ( ruleXArtifactType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3092:1: ruleXArtifactType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getArtifactTypesXArtifactTypeParserRuleCall_1_0_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXArtifactType_in_rule__OseeTypeModel__ArtifactTypesAssignment_1_06115);
            ruleXArtifactType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeModelAccess().getArtifactTypesXArtifactTypeParserRuleCall_1_0_0()); 
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
    // $ANTLR end rule__OseeTypeModel__ArtifactTypesAssignment_1_0


    // $ANTLR start rule__OseeTypeModel__RelationTypesAssignment_1_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3101:1: rule__OseeTypeModel__RelationTypesAssignment_1_1 : ( ruleXRelationType ) ;
    public final void rule__OseeTypeModel__RelationTypesAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3105:1: ( ( ruleXRelationType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3106:1: ( ruleXRelationType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3106:1: ( ruleXRelationType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3107:1: ruleXRelationType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getRelationTypesXRelationTypeParserRuleCall_1_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXRelationType_in_rule__OseeTypeModel__RelationTypesAssignment_1_16146);
            ruleXRelationType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeModelAccess().getRelationTypesXRelationTypeParserRuleCall_1_1_0()); 
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
    // $ANTLR end rule__OseeTypeModel__RelationTypesAssignment_1_1


    // $ANTLR start rule__OseeTypeModel__AttributeTypesAssignment_1_2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3116:1: rule__OseeTypeModel__AttributeTypesAssignment_1_2 : ( ruleXAttributeType ) ;
    public final void rule__OseeTypeModel__AttributeTypesAssignment_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3120:1: ( ( ruleXAttributeType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3121:1: ( ruleXAttributeType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3121:1: ( ruleXAttributeType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3122:1: ruleXAttributeType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getAttributeTypesXAttributeTypeParserRuleCall_1_2_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXAttributeType_in_rule__OseeTypeModel__AttributeTypesAssignment_1_26177);
            ruleXAttributeType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeModelAccess().getAttributeTypesXAttributeTypeParserRuleCall_1_2_0()); 
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
    // $ANTLR end rule__OseeTypeModel__AttributeTypesAssignment_1_2


    // $ANTLR start rule__OseeTypeModel__EnumTypesAssignment_1_3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3131:1: rule__OseeTypeModel__EnumTypesAssignment_1_3 : ( ruleXOseeEnumType ) ;
    public final void rule__OseeTypeModel__EnumTypesAssignment_1_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3135:1: ( ( ruleXOseeEnumType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3136:1: ( ruleXOseeEnumType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3136:1: ( ruleXOseeEnumType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3137:1: ruleXOseeEnumType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getEnumTypesXOseeEnumTypeParserRuleCall_1_3_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXOseeEnumType_in_rule__OseeTypeModel__EnumTypesAssignment_1_36208);
            ruleXOseeEnumType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeModelAccess().getEnumTypesXOseeEnumTypeParserRuleCall_1_3_0()); 
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
    // $ANTLR end rule__OseeTypeModel__EnumTypesAssignment_1_3


    // $ANTLR start rule__OseeTypeModel__EnumOverridesAssignment_1_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3146:1: rule__OseeTypeModel__EnumOverridesAssignment_1_4 : ( ruleXOseeEnumOverride ) ;
    public final void rule__OseeTypeModel__EnumOverridesAssignment_1_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3150:1: ( ( ruleXOseeEnumOverride ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3151:1: ( ruleXOseeEnumOverride )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3151:1: ( ruleXOseeEnumOverride )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3152:1: ruleXOseeEnumOverride
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getEnumOverridesXOseeEnumOverrideParserRuleCall_1_4_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXOseeEnumOverride_in_rule__OseeTypeModel__EnumOverridesAssignment_1_46239);
            ruleXOseeEnumOverride();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getOseeTypeModelAccess().getEnumOverridesXOseeEnumOverrideParserRuleCall_1_4_0()); 
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
    // $ANTLR end rule__OseeTypeModel__EnumOverridesAssignment_1_4


    // $ANTLR start rule__Import__ImportURIAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3161:1: rule__Import__ImportURIAssignment_1 : ( RULE_STRING ) ;
    public final void rule__Import__ImportURIAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3165:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3166:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3166:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3167:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_16270); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
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
    // $ANTLR end rule__Import__ImportURIAssignment_1


    // $ANTLR start rule__XArtifactType__AbstractAssignment_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3176:1: rule__XArtifactType__AbstractAssignment_0 : ( ( 'abstract' ) ) ;
    public final void rule__XArtifactType__AbstractAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3180:1: ( ( ( 'abstract' ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3181:1: ( ( 'abstract' ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3181:1: ( ( 'abstract' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3182:1: ( 'abstract' )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3183:1: ( 'abstract' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3184:1: 'abstract'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            }
            match(input,65,FollowSets000.FOLLOW_65_in_rule__XArtifactType__AbstractAssignment_06306); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
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
    // $ANTLR end rule__XArtifactType__AbstractAssignment_0


    // $ANTLR start rule__XArtifactType__NameAssignment_2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3199:1: rule__XArtifactType__NameAssignment_2 : ( ruleNAME_REFERENCE ) ;
    public final void rule__XArtifactType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3203:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3204:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3204:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3205:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__NameAssignment_26345);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0()); 
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
    // $ANTLR end rule__XArtifactType__NameAssignment_2


    // $ANTLR start rule__XArtifactType__SuperArtifactTypesAssignment_3_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3214:1: rule__XArtifactType__SuperArtifactTypesAssignment_3_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XArtifactType__SuperArtifactTypesAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3218:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3219:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3219:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3220:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3221:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3222:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__SuperArtifactTypesAssignment_3_16380);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_1_0()); 
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
    // $ANTLR end rule__XArtifactType__SuperArtifactTypesAssignment_3_1


    // $ANTLR start rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3233:1: rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3237:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3238:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3238:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3239:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_2_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3240:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3241:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeNAME_REFERENCEParserRuleCall_3_2_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__SuperArtifactTypesAssignment_3_2_16419);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeNAME_REFERENCEParserRuleCall_3_2_1_0_1()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_2_1_0()); 
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
    // $ANTLR end rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1


    // $ANTLR start rule__XArtifactType__TypeGuidAssignment_6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3252:1: rule__XArtifactType__TypeGuidAssignment_6 : ( RULE_STRING ) ;
    public final void rule__XArtifactType__TypeGuidAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3256:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3257:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3257:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3258:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XArtifactType__TypeGuidAssignment_66454); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
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
    // $ANTLR end rule__XArtifactType__TypeGuidAssignment_6


    // $ANTLR start rule__XArtifactType__ValidAttributeTypesAssignment_7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3267:1: rule__XArtifactType__ValidAttributeTypesAssignment_7 : ( ruleXAttributeTypeRef ) ;
    public final void rule__XArtifactType__ValidAttributeTypesAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3271:1: ( ( ruleXAttributeTypeRef ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3272:1: ( ruleXAttributeTypeRef )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3272:1: ( ruleXAttributeTypeRef )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3273:1: ruleXAttributeTypeRef
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_7_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXAttributeTypeRef_in_rule__XArtifactType__ValidAttributeTypesAssignment_76485);
            ruleXAttributeTypeRef();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_7_0()); 
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
    // $ANTLR end rule__XArtifactType__ValidAttributeTypesAssignment_7


    // $ANTLR start rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3282:1: rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3286:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3287:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3287:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3288:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3289:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3290:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeNAME_REFERENCEParserRuleCall_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeTypeRef__ValidAttributeTypeAssignment_16520);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeNAME_REFERENCEParserRuleCall_1_0_1()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0()); 
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
    // $ANTLR end rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1


    // $ANTLR start rule__XAttributeTypeRef__BranchGuidAssignment_2_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3301:1: rule__XAttributeTypeRef__BranchGuidAssignment_2_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeTypeRef__BranchGuidAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3305:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3306:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3306:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3307:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XAttributeTypeRef__BranchGuidAssignment_2_16555); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0()); 
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
    // $ANTLR end rule__XAttributeTypeRef__BranchGuidAssignment_2_1


    // $ANTLR start rule__XAttributeType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3316:1: rule__XAttributeType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__XAttributeType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3320:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3321:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3321:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3322:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__NameAssignment_16586);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
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
    // $ANTLR end rule__XAttributeType__NameAssignment_1


    // $ANTLR start rule__XAttributeType__BaseAttributeTypeAssignment_2_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3331:1: rule__XAttributeType__BaseAttributeTypeAssignment_2_1 : ( ruleAttributeBaseType ) ;
    public final void rule__XAttributeType__BaseAttributeTypeAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3335:1: ( ( ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3336:1: ( ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3336:1: ( ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3337:1: ruleAttributeBaseType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleAttributeBaseType_in_rule__XAttributeType__BaseAttributeTypeAssignment_2_16617);
            ruleAttributeBaseType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 
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
    // $ANTLR end rule__XAttributeType__BaseAttributeTypeAssignment_2_1


    // $ANTLR start rule__XAttributeType__OverrideAssignment_3_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3346:1: rule__XAttributeType__OverrideAssignment_3_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XAttributeType__OverrideAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3350:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3351:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3351:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3352:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3353:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3354:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__OverrideAssignment_3_16652);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0()); 
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
    // $ANTLR end rule__XAttributeType__OverrideAssignment_3_1


    // $ANTLR start rule__XAttributeType__TypeGuidAssignment_6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3365:1: rule__XAttributeType__TypeGuidAssignment_6 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__TypeGuidAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3369:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3370:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3370:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3371:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XAttributeType__TypeGuidAssignment_66687); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
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
    // $ANTLR end rule__XAttributeType__TypeGuidAssignment_6


    // $ANTLR start rule__XAttributeType__DataProviderAssignment_8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3380:1: rule__XAttributeType__DataProviderAssignment_8 : ( ( rule__XAttributeType__DataProviderAlternatives_8_0 ) ) ;
    public final void rule__XAttributeType__DataProviderAssignment_8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3384:1: ( ( ( rule__XAttributeType__DataProviderAlternatives_8_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3385:1: ( ( rule__XAttributeType__DataProviderAlternatives_8_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3385:1: ( ( rule__XAttributeType__DataProviderAlternatives_8_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3386:1: ( rule__XAttributeType__DataProviderAlternatives_8_0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDataProviderAlternatives_8_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3387:1: ( rule__XAttributeType__DataProviderAlternatives_8_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3387:2: rule__XAttributeType__DataProviderAlternatives_8_0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__DataProviderAlternatives_8_0_in_rule__XAttributeType__DataProviderAssignment_86718);
            rule__XAttributeType__DataProviderAlternatives_8_0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDataProviderAlternatives_8_0()); 
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
    // $ANTLR end rule__XAttributeType__DataProviderAssignment_8


    // $ANTLR start rule__XAttributeType__MinAssignment_10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3396:1: rule__XAttributeType__MinAssignment_10 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XAttributeType__MinAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3400:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3401:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3401:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3402:1: RULE_WHOLE_NUM_STR
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_10_0()); 
            }
            match(input,RULE_WHOLE_NUM_STR,FollowSets000.FOLLOW_RULE_WHOLE_NUM_STR_in_rule__XAttributeType__MinAssignment_106751); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_10_0()); 
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
    // $ANTLR end rule__XAttributeType__MinAssignment_10


    // $ANTLR start rule__XAttributeType__MaxAssignment_12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3411:1: rule__XAttributeType__MaxAssignment_12 : ( ( rule__XAttributeType__MaxAlternatives_12_0 ) ) ;
    public final void rule__XAttributeType__MaxAssignment_12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3415:1: ( ( ( rule__XAttributeType__MaxAlternatives_12_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3416:1: ( ( rule__XAttributeType__MaxAlternatives_12_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3416:1: ( ( rule__XAttributeType__MaxAlternatives_12_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3417:1: ( rule__XAttributeType__MaxAlternatives_12_0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMaxAlternatives_12_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3418:1: ( rule__XAttributeType__MaxAlternatives_12_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3418:2: rule__XAttributeType__MaxAlternatives_12_0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__MaxAlternatives_12_0_in_rule__XAttributeType__MaxAssignment_126782);
            rule__XAttributeType__MaxAlternatives_12_0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getMaxAlternatives_12_0()); 
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
    // $ANTLR end rule__XAttributeType__MaxAssignment_12


    // $ANTLR start rule__XAttributeType__TaggerIdAssignment_13_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3427:1: rule__XAttributeType__TaggerIdAssignment_13_1 : ( ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 ) ) ;
    public final void rule__XAttributeType__TaggerIdAssignment_13_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3431:1: ( ( ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3432:1: ( ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3432:1: ( ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3433:1: ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getTaggerIdAlternatives_13_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3434:1: ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3434:2: rule__XAttributeType__TaggerIdAlternatives_13_1_0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__TaggerIdAlternatives_13_1_0_in_rule__XAttributeType__TaggerIdAssignment_13_16815);
            rule__XAttributeType__TaggerIdAlternatives_13_1_0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getTaggerIdAlternatives_13_1_0()); 
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
    // $ANTLR end rule__XAttributeType__TaggerIdAssignment_13_1


    // $ANTLR start rule__XAttributeType__EnumTypeAssignment_14_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3443:1: rule__XAttributeType__EnumTypeAssignment_14_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XAttributeType__EnumTypeAssignment_14_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3447:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3448:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3448:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3449:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_14_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3450:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3451:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeNAME_REFERENCEParserRuleCall_14_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__EnumTypeAssignment_14_16852);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeNAME_REFERENCEParserRuleCall_14_1_0_1()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_14_1_0()); 
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
    // $ANTLR end rule__XAttributeType__EnumTypeAssignment_14_1


    // $ANTLR start rule__XAttributeType__DescriptionAssignment_15_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3462:1: rule__XAttributeType__DescriptionAssignment_15_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__DescriptionAssignment_15_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3466:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3467:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3467:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3468:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_15_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XAttributeType__DescriptionAssignment_15_16887); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_15_1_0()); 
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
    // $ANTLR end rule__XAttributeType__DescriptionAssignment_15_1


    // $ANTLR start rule__XAttributeType__DefaultValueAssignment_16_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3477:1: rule__XAttributeType__DefaultValueAssignment_16_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__DefaultValueAssignment_16_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3481:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3482:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3482:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3483:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_16_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XAttributeType__DefaultValueAssignment_16_16918); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_16_1_0()); 
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
    // $ANTLR end rule__XAttributeType__DefaultValueAssignment_16_1


    // $ANTLR start rule__XAttributeType__FileExtensionAssignment_17_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3492:1: rule__XAttributeType__FileExtensionAssignment_17_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__FileExtensionAssignment_17_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3496:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3497:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3497:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3498:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_17_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XAttributeType__FileExtensionAssignment_17_16949); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_17_1_0()); 
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
    // $ANTLR end rule__XAttributeType__FileExtensionAssignment_17_1


    // $ANTLR start rule__XOseeEnumType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3507:1: rule__XOseeEnumType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__XOseeEnumType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3511:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3512:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3512:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3513:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumType__NameAssignment_16980);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
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
    // $ANTLR end rule__XOseeEnumType__NameAssignment_1


    // $ANTLR start rule__XOseeEnumType__TypeGuidAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3522:1: rule__XOseeEnumType__TypeGuidAssignment_4 : ( RULE_STRING ) ;
    public final void rule__XOseeEnumType__TypeGuidAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3526:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3527:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3527:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3528:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XOseeEnumType__TypeGuidAssignment_47011); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
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
    // $ANTLR end rule__XOseeEnumType__TypeGuidAssignment_4


    // $ANTLR start rule__XOseeEnumType__EnumEntriesAssignment_5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3537:1: rule__XOseeEnumType__EnumEntriesAssignment_5 : ( ruleXOseeEnumEntry ) ;
    public final void rule__XOseeEnumType__EnumEntriesAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3541:1: ( ( ruleXOseeEnumEntry ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3542:1: ( ruleXOseeEnumEntry )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3542:1: ( ruleXOseeEnumEntry )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3543:1: ruleXOseeEnumEntry
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_5_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXOseeEnumEntry_in_rule__XOseeEnumType__EnumEntriesAssignment_57042);
            ruleXOseeEnumEntry();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_5_0()); 
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
    // $ANTLR end rule__XOseeEnumType__EnumEntriesAssignment_5


    // $ANTLR start rule__XOseeEnumEntry__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3552:1: rule__XOseeEnumEntry__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__XOseeEnumEntry__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3556:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3557:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3557:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3558:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumEntry__NameAssignment_17073);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
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
    // $ANTLR end rule__XOseeEnumEntry__NameAssignment_1


    // $ANTLR start rule__XOseeEnumEntry__OrdinalAssignment_2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3567:1: rule__XOseeEnumEntry__OrdinalAssignment_2 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XOseeEnumEntry__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3571:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3572:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3572:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3573:1: RULE_WHOLE_NUM_STR
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
            }
            match(input,RULE_WHOLE_NUM_STR,FollowSets000.FOLLOW_RULE_WHOLE_NUM_STR_in_rule__XOseeEnumEntry__OrdinalAssignment_27104); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
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
    // $ANTLR end rule__XOseeEnumEntry__OrdinalAssignment_2


    // $ANTLR start rule__XOseeEnumEntry__EntryGuidAssignment_3_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3582:1: rule__XOseeEnumEntry__EntryGuidAssignment_3_1 : ( RULE_STRING ) ;
    public final void rule__XOseeEnumEntry__EntryGuidAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3586:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3587:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3587:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3588:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XOseeEnumEntry__EntryGuidAssignment_3_17135); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
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
    // $ANTLR end rule__XOseeEnumEntry__EntryGuidAssignment_3_1


    // $ANTLR start rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3597:1: rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3601:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3602:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3602:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3603:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3604:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3605:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeNAME_REFERENCEParserRuleCall_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumOverride__OverridenEnumTypeAssignment_17170);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeNAME_REFERENCEParserRuleCall_1_0_1()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0()); 
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
    // $ANTLR end rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1


    // $ANTLR start rule__XOseeEnumOverride__InheritAllAssignment_3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3616:1: rule__XOseeEnumOverride__InheritAllAssignment_3 : ( ( 'inheritAll' ) ) ;
    public final void rule__XOseeEnumOverride__InheritAllAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3620:1: ( ( ( 'inheritAll' ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3621:1: ( ( 'inheritAll' ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3621:1: ( ( 'inheritAll' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3622:1: ( 'inheritAll' )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3623:1: ( 'inheritAll' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3624:1: 'inheritAll'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            }
            match(input,66,FollowSets000.FOLLOW_66_in_rule__XOseeEnumOverride__InheritAllAssignment_37210); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
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
    // $ANTLR end rule__XOseeEnumOverride__InheritAllAssignment_3


    // $ANTLR start rule__XOseeEnumOverride__OverrideOptionsAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3639:1: rule__XOseeEnumOverride__OverrideOptionsAssignment_4 : ( ruleOverrideOption ) ;
    public final void rule__XOseeEnumOverride__OverrideOptionsAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3643:1: ( ( ruleOverrideOption ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3644:1: ( ruleOverrideOption )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3644:1: ( ruleOverrideOption )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3645:1: ruleOverrideOption
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleOverrideOption_in_rule__XOseeEnumOverride__OverrideOptionsAssignment_47249);
            ruleOverrideOption();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 
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
    // $ANTLR end rule__XOseeEnumOverride__OverrideOptionsAssignment_4


    // $ANTLR start rule__AddEnum__EnumEntryAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3654:1: rule__AddEnum__EnumEntryAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__AddEnum__EnumEntryAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3658:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3659:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3659:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3660:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getEnumEntryNAME_REFERENCEParserRuleCall_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__AddEnum__EnumEntryAssignment_17280);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getEnumEntryNAME_REFERENCEParserRuleCall_1_0()); 
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
    // $ANTLR end rule__AddEnum__EnumEntryAssignment_1


    // $ANTLR start rule__AddEnum__OrdinalAssignment_2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3669:1: rule__AddEnum__OrdinalAssignment_2 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__AddEnum__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3673:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3674:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3674:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3675:1: RULE_WHOLE_NUM_STR
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
            }
            match(input,RULE_WHOLE_NUM_STR,FollowSets000.FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AddEnum__OrdinalAssignment_27311); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
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
    // $ANTLR end rule__AddEnum__OrdinalAssignment_2


    // $ANTLR start rule__AddEnum__EntryGuidAssignment_3_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3684:1: rule__AddEnum__EntryGuidAssignment_3_1 : ( RULE_STRING ) ;
    public final void rule__AddEnum__EntryGuidAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3688:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3689:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3689:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3690:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__AddEnum__EntryGuidAssignment_3_17342); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
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
    // $ANTLR end rule__AddEnum__EntryGuidAssignment_3_1


    // $ANTLR start rule__RemoveEnum__EnumEntryAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3699:1: rule__RemoveEnum__EnumEntryAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RemoveEnum__EnumEntryAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3703:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3704:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3704:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3705:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryCrossReference_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3706:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3707:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryNAME_REFERENCEParserRuleCall_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__RemoveEnum__EnumEntryAssignment_17377);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryNAME_REFERENCEParserRuleCall_1_0_1()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryCrossReference_1_0()); 
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
    // $ANTLR end rule__RemoveEnum__EnumEntryAssignment_1


    // $ANTLR start rule__XRelationType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3718:1: rule__XRelationType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__XRelationType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3722:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3723:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3723:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3724:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__NameAssignment_17412);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
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
    // $ANTLR end rule__XRelationType__NameAssignment_1


    // $ANTLR start rule__XRelationType__TypeGuidAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3733:1: rule__XRelationType__TypeGuidAssignment_4 : ( RULE_STRING ) ;
    public final void rule__XRelationType__TypeGuidAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3737:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3738:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3738:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3739:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XRelationType__TypeGuidAssignment_47443); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
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
    // $ANTLR end rule__XRelationType__TypeGuidAssignment_4


    // $ANTLR start rule__XRelationType__SideANameAssignment_6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3748:1: rule__XRelationType__SideANameAssignment_6 : ( RULE_STRING ) ;
    public final void rule__XRelationType__SideANameAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3752:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3753:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3753:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3754:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_6_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XRelationType__SideANameAssignment_67474); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_6_0()); 
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
    // $ANTLR end rule__XRelationType__SideANameAssignment_6


    // $ANTLR start rule__XRelationType__SideAArtifactTypeAssignment_8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3763:1: rule__XRelationType__SideAArtifactTypeAssignment_8 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XRelationType__SideAArtifactTypeAssignment_8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3767:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3768:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3768:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3769:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_8_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3770:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3771:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeNAME_REFERENCEParserRuleCall_8_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__SideAArtifactTypeAssignment_87509);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeNAME_REFERENCEParserRuleCall_8_0_1()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_8_0()); 
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
    // $ANTLR end rule__XRelationType__SideAArtifactTypeAssignment_8


    // $ANTLR start rule__XRelationType__SideBNameAssignment_10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3782:1: rule__XRelationType__SideBNameAssignment_10 : ( RULE_STRING ) ;
    public final void rule__XRelationType__SideBNameAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3786:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3787:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3787:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3788:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_10_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XRelationType__SideBNameAssignment_107544); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_10_0()); 
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
    // $ANTLR end rule__XRelationType__SideBNameAssignment_10


    // $ANTLR start rule__XRelationType__SideBArtifactTypeAssignment_12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3797:1: rule__XRelationType__SideBArtifactTypeAssignment_12 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XRelationType__SideBArtifactTypeAssignment_12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3801:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3802:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3802:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3803:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_12_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3804:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3805:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeNAME_REFERENCEParserRuleCall_12_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__SideBArtifactTypeAssignment_127579);
            ruleNAME_REFERENCE();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeNAME_REFERENCEParserRuleCall_12_0_1()); 
            }

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_12_0()); 
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
    // $ANTLR end rule__XRelationType__SideBArtifactTypeAssignment_12


    // $ANTLR start rule__XRelationType__DefaultOrderTypeAssignment_14
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3816:1: rule__XRelationType__DefaultOrderTypeAssignment_14 : ( ruleRelationOrderType ) ;
    public final void rule__XRelationType__DefaultOrderTypeAssignment_14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3820:1: ( ( ruleRelationOrderType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3821:1: ( ruleRelationOrderType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3821:1: ( ruleRelationOrderType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3822:1: ruleRelationOrderType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_14_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleRelationOrderType_in_rule__XRelationType__DefaultOrderTypeAssignment_147614);
            ruleRelationOrderType();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_14_0()); 
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
    // $ANTLR end rule__XRelationType__DefaultOrderTypeAssignment_14


    // $ANTLR start rule__XRelationType__MultiplicityAssignment_16
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3831:1: rule__XRelationType__MultiplicityAssignment_16 : ( ruleRelationMultiplicityEnum ) ;
    public final void rule__XRelationType__MultiplicityAssignment_16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3835:1: ( ( ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3836:1: ( ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3836:1: ( ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3837:1: ruleRelationMultiplicityEnum
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleRelationMultiplicityEnum_in_rule__XRelationType__MultiplicityAssignment_167645);
            ruleRelationMultiplicityEnum();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0()); 
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
    // $ANTLR end rule__XRelationType__MultiplicityAssignment_16


 

    
    private static class FollowSets000 {
        public static final BitSet FOLLOW_ruleOseeTypeModel_in_entryRuleOseeTypeModel67 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleOseeTypeModel74 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__Group__0_in_ruleOseeTypeModel100 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleImport_in_entryRuleImport127 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleImport134 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__Import__Group__0_in_ruleImport160 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_entryRuleNAME_REFERENCE187 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleNAME_REFERENCE194 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_ruleNAME_REFERENCE220 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_entryRuleQUALIFIED_NAME246 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleQUALIFIED_NAME253 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__0_in_ruleQUALIFIED_NAME279 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleOseeType_in_entryRuleOseeType308 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleOseeType315 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeType__Alternatives_in_ruleOseeType341 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXArtifactType_in_entryRuleXArtifactType368 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleXArtifactType375 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__0_in_ruleXArtifactType401 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_entryRuleXAttributeTypeRef428 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeTypeRef435 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group__0_in_ruleXAttributeTypeRef461 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXAttributeType_in_entryRuleXAttributeType488 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleXAttributeType495 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__0_in_ruleXAttributeType521 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleAttributeBaseType_in_entryRuleAttributeBaseType548 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleAttributeBaseType555 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AttributeBaseType__Alternatives_in_ruleAttributeBaseType581 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXOseeEnumType_in_entryRuleXOseeEnumType608 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumType615 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__0_in_ruleXOseeEnumType641 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_entryRuleXOseeEnumEntry668 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumEntry675 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__0_in_ruleXOseeEnumEntry701 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_entryRuleXOseeEnumOverride728 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleXOseeEnumOverride735 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__0_in_ruleXOseeEnumOverride761 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleOverrideOption_in_entryRuleOverrideOption788 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleOverrideOption795 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OverrideOption__Alternatives_in_ruleOverrideOption821 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleAddEnum_in_entryRuleAddEnum848 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleAddEnum855 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__0_in_ruleAddEnum881 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleRemoveEnum_in_entryRuleRemoveEnum908 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleRemoveEnum915 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__RemoveEnum__Group__0_in_ruleRemoveEnum941 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXRelationType_in_entryRuleXRelationType968 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleXRelationType975 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__0_in_ruleXRelationType1001 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleRelationOrderType_in_entryRuleRelationOrderType1028 = new BitSet(new long[]{0x0000000000000000L});
        public static final BitSet FOLLOW_EOF_in_entryRuleRelationOrderType1035 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__RelationOrderType__Alternatives_in_ruleRelationOrderType1061 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__RelationMultiplicityEnum__Alternatives_in_ruleRelationMultiplicityEnum1098 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__ArtifactTypesAssignment_1_0_in_rule__OseeTypeModel__Alternatives_11133 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__RelationTypesAssignment_1_1_in_rule__OseeTypeModel__Alternatives_11151 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__AttributeTypesAssignment_1_2_in_rule__OseeTypeModel__Alternatives_11169 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__EnumTypesAssignment_1_3_in_rule__OseeTypeModel__Alternatives_11187 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__EnumOverridesAssignment_1_4_in_rule__OseeTypeModel__Alternatives_11205 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXArtifactType_in_rule__OseeType__Alternatives1239 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXRelationType_in_rule__OseeType__Alternatives1256 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXAttributeType_in_rule__OseeType__Alternatives1273 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXOseeEnumType_in_rule__OseeType__Alternatives1290 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_12_in_rule__XAttributeType__DataProviderAlternatives_8_01323 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_13_in_rule__XAttributeType__DataProviderAlternatives_8_01343 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__XAttributeType__DataProviderAlternatives_8_01362 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__XAttributeType__MaxAlternatives_12_01394 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_14_in_rule__XAttributeType__MaxAlternatives_12_01412 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_15_in_rule__XAttributeType__TaggerIdAlternatives_13_1_01447 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__XAttributeType__TaggerIdAlternatives_13_1_01466 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_16_in_rule__AttributeBaseType__Alternatives1499 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_17_in_rule__AttributeBaseType__Alternatives1519 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_18_in_rule__AttributeBaseType__Alternatives1539 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_19_in_rule__AttributeBaseType__Alternatives1559 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_20_in_rule__AttributeBaseType__Alternatives1579 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_21_in_rule__AttributeBaseType__Alternatives1599 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_22_in_rule__AttributeBaseType__Alternatives1619 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_23_in_rule__AttributeBaseType__Alternatives1639 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_24_in_rule__AttributeBaseType__Alternatives1659 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleQUALIFIED_NAME_in_rule__AttributeBaseType__Alternatives1678 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleAddEnum_in_rule__OverrideOption__Alternatives1710 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleRemoveEnum_in_rule__OverrideOption__Alternatives1727 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_25_in_rule__RelationOrderType__Alternatives1760 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_26_in_rule__RelationOrderType__Alternatives1780 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_27_in_rule__RelationOrderType__Alternatives1800 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_ID_in_rule__RelationOrderType__Alternatives1819 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_28_in_rule__RelationMultiplicityEnum__Alternatives1852 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_29_in_rule__RelationMultiplicityEnum__Alternatives1873 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_30_in_rule__RelationMultiplicityEnum__Alternatives1894 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_31_in_rule__RelationMultiplicityEnum__Alternatives1915 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__01952 = new BitSet(new long[]{0x0490040500000002L,0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01962 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__Alternatives_1_in_rule__OseeTypeModel__Group__11990 = new BitSet(new long[]{0x0490040400000002L,0x0000000000000002L});
        public static final BitSet FOLLOW_32_in_rule__Import__Group__02030 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__Import__Group__1_in_rule__Import__Group__02040 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__12068 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__02106 = new BitSet(new long[]{0x0000000200000002L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__02114 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__12142 = new BitSet(new long[]{0x0000000200000002L});
        public static final BitSet FOLLOW_33_in_rule__QUALIFIED_NAME__Group_1__02182 = new BitSet(new long[]{0x0000000000000040L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__02192 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__12220 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__AbstractAssignment_0_in_rule__XArtifactType__Group__02257 = new BitSet(new long[]{0x0000000400000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__1_in_rule__XArtifactType__Group__02267 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_34_in_rule__XArtifactType__Group__12296 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__2_in_rule__XArtifactType__Group__12306 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__NameAssignment_2_in_rule__XArtifactType__Group__22334 = new BitSet(new long[]{0x0000004800000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__3_in_rule__XArtifactType__Group__22343 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3__0_in_rule__XArtifactType__Group__32371 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__4_in_rule__XArtifactType__Group__32381 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_35_in_rule__XArtifactType__Group__42410 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__5_in_rule__XArtifactType__Group__42420 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_36_in_rule__XArtifactType__Group__52449 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__6_in_rule__XArtifactType__Group__52459 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__TypeGuidAssignment_6_in_rule__XArtifactType__Group__62487 = new BitSet(new long[]{0x0000012000000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__7_in_rule__XArtifactType__Group__62496 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__ValidAttributeTypesAssignment_7_in_rule__XArtifactType__Group__72524 = new BitSet(new long[]{0x0000012000000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__8_in_rule__XArtifactType__Group__72534 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_37_in_rule__XArtifactType__Group__82563 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_38_in_rule__XArtifactType__Group_3__02617 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3__1_in_rule__XArtifactType__Group_3__02627 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__SuperArtifactTypesAssignment_3_1_in_rule__XArtifactType__Group_3__12655 = new BitSet(new long[]{0x0000008000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3__2_in_rule__XArtifactType__Group_3__12664 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3_2__0_in_rule__XArtifactType__Group_3__22692 = new BitSet(new long[]{0x0000008000000002L});
        public static final BitSet FOLLOW_39_in_rule__XArtifactType__Group_3_2__02734 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3_2__1_in_rule__XArtifactType__Group_3_2__02744 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1_in_rule__XArtifactType__Group_3_2__12772 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_40_in_rule__XAttributeTypeRef__Group__02811 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group__1_in_rule__XAttributeTypeRef__Group__02821 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__XAttributeTypeRef__Group__12849 = new BitSet(new long[]{0x0000020000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group__2_in_rule__XAttributeTypeRef__Group__12858 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group_2__0_in_rule__XAttributeTypeRef__Group__22886 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_41_in_rule__XAttributeTypeRef__Group_2__02928 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group_2__1_in_rule__XAttributeTypeRef__Group_2__02938 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__BranchGuidAssignment_2_1_in_rule__XAttributeTypeRef__Group_2__12966 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_42_in_rule__XAttributeType__Group__03005 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__1_in_rule__XAttributeType__Group__03015 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__NameAssignment_1_in_rule__XAttributeType__Group__13043 = new BitSet(new long[]{0x0000004000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__2_in_rule__XAttributeType__Group__13052 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_2__0_in_rule__XAttributeType__Group__23080 = new BitSet(new long[]{0x0000400800000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__3_in_rule__XAttributeType__Group__23089 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_3__0_in_rule__XAttributeType__Group__33117 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__4_in_rule__XAttributeType__Group__33127 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_35_in_rule__XAttributeType__Group__43156 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__5_in_rule__XAttributeType__Group__43166 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_36_in_rule__XAttributeType__Group__53195 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__6_in_rule__XAttributeType__Group__53205 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__TypeGuidAssignment_6_in_rule__XAttributeType__Group__63233 = new BitSet(new long[]{0x0000080000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__7_in_rule__XAttributeType__Group__63242 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_43_in_rule__XAttributeType__Group__73271 = new BitSet(new long[]{0x0000000000003040L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__8_in_rule__XAttributeType__Group__73281 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__DataProviderAssignment_8_in_rule__XAttributeType__Group__83309 = new BitSet(new long[]{0x0000100000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__9_in_rule__XAttributeType__Group__83318 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_44_in_rule__XAttributeType__Group__93347 = new BitSet(new long[]{0x0000000000000020L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__10_in_rule__XAttributeType__Group__93357 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__MinAssignment_10_in_rule__XAttributeType__Group__103385 = new BitSet(new long[]{0x0000200000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__11_in_rule__XAttributeType__Group__103394 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_45_in_rule__XAttributeType__Group__113423 = new BitSet(new long[]{0x0000000000004020L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__12_in_rule__XAttributeType__Group__113433 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__MaxAssignment_12_in_rule__XAttributeType__Group__123461 = new BitSet(new long[]{0x000F802000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__13_in_rule__XAttributeType__Group__123470 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_13__0_in_rule__XAttributeType__Group__133498 = new BitSet(new long[]{0x000F002000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__14_in_rule__XAttributeType__Group__133508 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_14__0_in_rule__XAttributeType__Group__143536 = new BitSet(new long[]{0x000E002000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__15_in_rule__XAttributeType__Group__143546 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_15__0_in_rule__XAttributeType__Group__153574 = new BitSet(new long[]{0x000C002000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__16_in_rule__XAttributeType__Group__153584 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_16__0_in_rule__XAttributeType__Group__163612 = new BitSet(new long[]{0x0008002000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__17_in_rule__XAttributeType__Group__163622 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_17__0_in_rule__XAttributeType__Group__173650 = new BitSet(new long[]{0x0000002000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__18_in_rule__XAttributeType__Group__173660 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_37_in_rule__XAttributeType__Group__183689 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_38_in_rule__XAttributeType__Group_2__03763 = new BitSet(new long[]{0x0000000001FF0040L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_2__1_in_rule__XAttributeType__Group_2__03773 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__BaseAttributeTypeAssignment_2_1_in_rule__XAttributeType__Group_2__13801 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_46_in_rule__XAttributeType__Group_3__03840 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_3__1_in_rule__XAttributeType__Group_3__03850 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__OverrideAssignment_3_1_in_rule__XAttributeType__Group_3__13878 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_47_in_rule__XAttributeType__Group_13__03917 = new BitSet(new long[]{0x0000000000008040L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_13__1_in_rule__XAttributeType__Group_13__03927 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__TaggerIdAssignment_13_1_in_rule__XAttributeType__Group_13__13955 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_48_in_rule__XAttributeType__Group_14__03994 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_14__1_in_rule__XAttributeType__Group_14__04004 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__EnumTypeAssignment_14_1_in_rule__XAttributeType__Group_14__14032 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_49_in_rule__XAttributeType__Group_15__04071 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_15__1_in_rule__XAttributeType__Group_15__04081 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__DescriptionAssignment_15_1_in_rule__XAttributeType__Group_15__14109 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_50_in_rule__XAttributeType__Group_16__04148 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_16__1_in_rule__XAttributeType__Group_16__04158 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__DefaultValueAssignment_16_1_in_rule__XAttributeType__Group_16__14186 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_51_in_rule__XAttributeType__Group_17__04225 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_17__1_in_rule__XAttributeType__Group_17__04235 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__FileExtensionAssignment_17_1_in_rule__XAttributeType__Group_17__14263 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_52_in_rule__XOseeEnumType__Group__04302 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__1_in_rule__XOseeEnumType__Group__04312 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__NameAssignment_1_in_rule__XOseeEnumType__Group__14340 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__2_in_rule__XOseeEnumType__Group__14349 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_35_in_rule__XOseeEnumType__Group__24378 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__3_in_rule__XOseeEnumType__Group__24388 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_36_in_rule__XOseeEnumType__Group__34417 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__4_in_rule__XOseeEnumType__Group__34427 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__TypeGuidAssignment_4_in_rule__XOseeEnumType__Group__44455 = new BitSet(new long[]{0x0020002000000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__5_in_rule__XOseeEnumType__Group__44464 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__EnumEntriesAssignment_5_in_rule__XOseeEnumType__Group__54492 = new BitSet(new long[]{0x0020002000000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__6_in_rule__XOseeEnumType__Group__54502 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_37_in_rule__XOseeEnumType__Group__64531 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_53_in_rule__XOseeEnumEntry__Group__04581 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__1_in_rule__XOseeEnumEntry__Group__04591 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__NameAssignment_1_in_rule__XOseeEnumEntry__Group__14619 = new BitSet(new long[]{0x0040000000000022L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__2_in_rule__XOseeEnumEntry__Group__14628 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__OrdinalAssignment_2_in_rule__XOseeEnumEntry__Group__24656 = new BitSet(new long[]{0x0040000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__3_in_rule__XOseeEnumEntry__Group__24666 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group_3__0_in_rule__XOseeEnumEntry__Group__34694 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_54_in_rule__XOseeEnumEntry__Group_3__04738 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group_3__1_in_rule__XOseeEnumEntry__Group_3__04748 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__EntryGuidAssignment_3_1_in_rule__XOseeEnumEntry__Group_3__14776 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_55_in_rule__XOseeEnumOverride__Group__04815 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__1_in_rule__XOseeEnumOverride__Group__04825 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1_in_rule__XOseeEnumOverride__Group__14853 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__2_in_rule__XOseeEnumOverride__Group__14862 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_35_in_rule__XOseeEnumOverride__Group__24891 = new BitSet(new long[]{0x0300002000000000L,0x0000000000000004L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__3_in_rule__XOseeEnumOverride__Group__24901 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__InheritAllAssignment_3_in_rule__XOseeEnumOverride__Group__34929 = new BitSet(new long[]{0x0300002000000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__4_in_rule__XOseeEnumOverride__Group__34939 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__OverrideOptionsAssignment_4_in_rule__XOseeEnumOverride__Group__44967 = new BitSet(new long[]{0x0300002000000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__5_in_rule__XOseeEnumOverride__Group__44977 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_37_in_rule__XOseeEnumOverride__Group__55006 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_56_in_rule__AddEnum__Group__05054 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__1_in_rule__AddEnum__Group__05064 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__EnumEntryAssignment_1_in_rule__AddEnum__Group__15092 = new BitSet(new long[]{0x0040000000000022L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__2_in_rule__AddEnum__Group__15101 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__OrdinalAssignment_2_in_rule__AddEnum__Group__25129 = new BitSet(new long[]{0x0040000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__3_in_rule__AddEnum__Group__25139 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group_3__0_in_rule__AddEnum__Group__35167 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_54_in_rule__AddEnum__Group_3__05211 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__AddEnum__Group_3__1_in_rule__AddEnum__Group_3__05221 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__EntryGuidAssignment_3_1_in_rule__AddEnum__Group_3__15249 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_57_in_rule__RemoveEnum__Group__05288 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__RemoveEnum__Group__1_in_rule__RemoveEnum__Group__05298 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__RemoveEnum__EnumEntryAssignment_1_in_rule__RemoveEnum__Group__15326 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_58_in_rule__XRelationType__Group__05365 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__1_in_rule__XRelationType__Group__05375 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__NameAssignment_1_in_rule__XRelationType__Group__15403 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__2_in_rule__XRelationType__Group__15412 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_35_in_rule__XRelationType__Group__25441 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__3_in_rule__XRelationType__Group__25451 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_36_in_rule__XRelationType__Group__35480 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__4_in_rule__XRelationType__Group__35490 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__TypeGuidAssignment_4_in_rule__XRelationType__Group__45518 = new BitSet(new long[]{0x0800000000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__5_in_rule__XRelationType__Group__45527 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_59_in_rule__XRelationType__Group__55556 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__6_in_rule__XRelationType__Group__55566 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__SideANameAssignment_6_in_rule__XRelationType__Group__65594 = new BitSet(new long[]{0x1000000000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__7_in_rule__XRelationType__Group__65603 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_60_in_rule__XRelationType__Group__75632 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__8_in_rule__XRelationType__Group__75642 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__SideAArtifactTypeAssignment_8_in_rule__XRelationType__Group__85670 = new BitSet(new long[]{0x2000000000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__9_in_rule__XRelationType__Group__85679 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_61_in_rule__XRelationType__Group__95708 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__10_in_rule__XRelationType__Group__95718 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__SideBNameAssignment_10_in_rule__XRelationType__Group__105746 = new BitSet(new long[]{0x4000000000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__11_in_rule__XRelationType__Group__105755 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_62_in_rule__XRelationType__Group__115784 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__12_in_rule__XRelationType__Group__115794 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__SideBArtifactTypeAssignment_12_in_rule__XRelationType__Group__125822 = new BitSet(new long[]{0x8000000000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__13_in_rule__XRelationType__Group__125831 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_63_in_rule__XRelationType__Group__135860 = new BitSet(new long[]{0x000000000E000040L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__14_in_rule__XRelationType__Group__135870 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__DefaultOrderTypeAssignment_14_in_rule__XRelationType__Group__145898 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__15_in_rule__XRelationType__Group__145907 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_64_in_rule__XRelationType__Group__155936 = new BitSet(new long[]{0x00000000F0000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__16_in_rule__XRelationType__Group__155946 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__MultiplicityAssignment_16_in_rule__XRelationType__Group__165974 = new BitSet(new long[]{0x0000002000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__17_in_rule__XRelationType__Group__165983 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_37_in_rule__XRelationType__Group__176012 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_06084 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXArtifactType_in_rule__OseeTypeModel__ArtifactTypesAssignment_1_06115 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXRelationType_in_rule__OseeTypeModel__RelationTypesAssignment_1_16146 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXAttributeType_in_rule__OseeTypeModel__AttributeTypesAssignment_1_26177 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXOseeEnumType_in_rule__OseeTypeModel__EnumTypesAssignment_1_36208 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_rule__OseeTypeModel__EnumOverridesAssignment_1_46239 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_16270 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_65_in_rule__XArtifactType__AbstractAssignment_06306 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__NameAssignment_26345 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__SuperArtifactTypesAssignment_3_16380 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__SuperArtifactTypesAssignment_3_2_16419 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XArtifactType__TypeGuidAssignment_66454 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_rule__XArtifactType__ValidAttributeTypesAssignment_76485 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeTypeRef__ValidAttributeTypeAssignment_16520 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttributeTypeRef__BranchGuidAssignment_2_16555 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__NameAssignment_16586 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleAttributeBaseType_in_rule__XAttributeType__BaseAttributeTypeAssignment_2_16617 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__OverrideAssignment_3_16652 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttributeType__TypeGuidAssignment_66687 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__DataProviderAlternatives_8_0_in_rule__XAttributeType__DataProviderAssignment_86718 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__XAttributeType__MinAssignment_106751 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__MaxAlternatives_12_0_in_rule__XAttributeType__MaxAssignment_126782 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__TaggerIdAlternatives_13_1_0_in_rule__XAttributeType__TaggerIdAssignment_13_16815 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__EnumTypeAssignment_14_16852 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttributeType__DescriptionAssignment_15_16887 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttributeType__DefaultValueAssignment_16_16918 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttributeType__FileExtensionAssignment_17_16949 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumType__NameAssignment_16980 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XOseeEnumType__TypeGuidAssignment_47011 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_rule__XOseeEnumType__EnumEntriesAssignment_57042 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumEntry__NameAssignment_17073 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__XOseeEnumEntry__OrdinalAssignment_27104 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XOseeEnumEntry__EntryGuidAssignment_3_17135 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumOverride__OverridenEnumTypeAssignment_17170 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_66_in_rule__XOseeEnumOverride__InheritAllAssignment_37210 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleOverrideOption_in_rule__XOseeEnumOverride__OverrideOptionsAssignment_47249 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AddEnum__EnumEntryAssignment_17280 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AddEnum__OrdinalAssignment_27311 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__AddEnum__EntryGuidAssignment_3_17342 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RemoveEnum__EnumEntryAssignment_17377 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__NameAssignment_17412 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XRelationType__TypeGuidAssignment_47443 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XRelationType__SideANameAssignment_67474 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__SideAArtifactTypeAssignment_87509 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XRelationType__SideBNameAssignment_107544 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__SideBArtifactTypeAssignment_127579 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleRelationOrderType_in_rule__XRelationType__DefaultOrderTypeAssignment_147614 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_rule__XRelationType__MultiplicityAssignment_167645 = new BitSet(new long[]{0x0000000000000002L});
    }


}