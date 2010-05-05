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
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.DFA;
import org.eclipse.osee.framework.services.OseeTypesGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
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
            ruleMemo = new HashMap[350+1];
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:62:1: entryRuleOseeTypeModel : ruleOseeTypeModel EOF ;
    public final void entryRuleOseeTypeModel() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:63:1: ( ruleOseeTypeModel EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:64:1: ruleOseeTypeModel EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:71:1: ruleOseeTypeModel : ( ( rule__OseeTypeModel__Group__0 ) ) ;
    public final void ruleOseeTypeModel() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:75:2: ( ( ( rule__OseeTypeModel__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:76:1: ( ( rule__OseeTypeModel__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:76:1: ( ( rule__OseeTypeModel__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:77:1: ( rule__OseeTypeModel__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:78:1: ( rule__OseeTypeModel__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:78:2: rule__OseeTypeModel__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:90:1: entryRuleImport : ruleImport EOF ;
    public final void entryRuleImport() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:91:1: ( ruleImport EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:92:1: ruleImport EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:99:1: ruleImport : ( ( rule__Import__Group__0 ) ) ;
    public final void ruleImport() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:103:2: ( ( ( rule__Import__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:104:1: ( ( rule__Import__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:104:1: ( ( rule__Import__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:105:1: ( rule__Import__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getImportAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:106:1: ( rule__Import__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:106:2: rule__Import__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:118:1: entryRuleNAME_REFERENCE : ruleNAME_REFERENCE EOF ;
    public final void entryRuleNAME_REFERENCE() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:119:1: ( ruleNAME_REFERENCE EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:120:1: ruleNAME_REFERENCE EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:127:1: ruleNAME_REFERENCE : ( RULE_STRING ) ;
    public final void ruleNAME_REFERENCE() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:131:2: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:132:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:132:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:133:1: RULE_STRING
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:146:1: entryRuleQUALIFIED_NAME : ruleQUALIFIED_NAME EOF ;
    public final void entryRuleQUALIFIED_NAME() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:147:1: ( ruleQUALIFIED_NAME EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:148:1: ruleQUALIFIED_NAME EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:155:1: ruleQUALIFIED_NAME : ( ( rule__QUALIFIED_NAME__Group__0 ) ) ;
    public final void ruleQUALIFIED_NAME() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:159:2: ( ( ( rule__QUALIFIED_NAME__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:160:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:160:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:161:1: ( rule__QUALIFIED_NAME__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:162:1: ( rule__QUALIFIED_NAME__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:162:2: rule__QUALIFIED_NAME__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:176:1: entryRuleOseeType : ruleOseeType EOF ;
    public final void entryRuleOseeType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:177:1: ( ruleOseeType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:178:1: ruleOseeType EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:185:1: ruleOseeType : ( ( rule__OseeType__Alternatives ) ) ;
    public final void ruleOseeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:189:2: ( ( ( rule__OseeType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:190:1: ( ( rule__OseeType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:190:1: ( ( rule__OseeType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:191:1: ( rule__OseeType__Alternatives )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeAccess().getAlternatives()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:192:1: ( rule__OseeType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:192:2: rule__OseeType__Alternatives
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:204:1: entryRuleXArtifactType : ruleXArtifactType EOF ;
    public final void entryRuleXArtifactType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:205:1: ( ruleXArtifactType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:206:1: ruleXArtifactType EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:213:1: ruleXArtifactType : ( ( rule__XArtifactType__Group__0 ) ) ;
    public final void ruleXArtifactType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:217:2: ( ( ( rule__XArtifactType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:218:1: ( ( rule__XArtifactType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:218:1: ( ( rule__XArtifactType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:219:1: ( rule__XArtifactType__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:220:1: ( rule__XArtifactType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:220:2: rule__XArtifactType__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:232:1: entryRuleXAttributeTypeRef : ruleXAttributeTypeRef EOF ;
    public final void entryRuleXAttributeTypeRef() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:233:1: ( ruleXAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:234:1: ruleXAttributeTypeRef EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:241:1: ruleXAttributeTypeRef : ( ( rule__XAttributeTypeRef__Group__0 ) ) ;
    public final void ruleXAttributeTypeRef() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:245:2: ( ( ( rule__XAttributeTypeRef__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:246:1: ( ( rule__XAttributeTypeRef__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:246:1: ( ( rule__XAttributeTypeRef__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:247:1: ( rule__XAttributeTypeRef__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:248:1: ( rule__XAttributeTypeRef__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:248:2: rule__XAttributeTypeRef__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:260:1: entryRuleXAttributeType : ruleXAttributeType EOF ;
    public final void entryRuleXAttributeType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:261:1: ( ruleXAttributeType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:262:1: ruleXAttributeType EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:269:1: ruleXAttributeType : ( ( rule__XAttributeType__Group__0 ) ) ;
    public final void ruleXAttributeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:273:2: ( ( ( rule__XAttributeType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:274:1: ( ( rule__XAttributeType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:274:1: ( ( rule__XAttributeType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:275:1: ( rule__XAttributeType__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:276:1: ( rule__XAttributeType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:276:2: rule__XAttributeType__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:288:1: entryRuleAttributeBaseType : ruleAttributeBaseType EOF ;
    public final void entryRuleAttributeBaseType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:289:1: ( ruleAttributeBaseType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:290:1: ruleAttributeBaseType EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:297:1: ruleAttributeBaseType : ( ( rule__AttributeBaseType__Alternatives ) ) ;
    public final void ruleAttributeBaseType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:301:2: ( ( ( rule__AttributeBaseType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:302:1: ( ( rule__AttributeBaseType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:302:1: ( ( rule__AttributeBaseType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:303:1: ( rule__AttributeBaseType__Alternatives )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAttributeBaseTypeAccess().getAlternatives()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:304:1: ( rule__AttributeBaseType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:304:2: rule__AttributeBaseType__Alternatives
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:316:1: entryRuleXOseeEnumType : ruleXOseeEnumType EOF ;
    public final void entryRuleXOseeEnumType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:317:1: ( ruleXOseeEnumType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:318:1: ruleXOseeEnumType EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:325:1: ruleXOseeEnumType : ( ( rule__XOseeEnumType__Group__0 ) ) ;
    public final void ruleXOseeEnumType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:329:2: ( ( ( rule__XOseeEnumType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:330:1: ( ( rule__XOseeEnumType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:330:1: ( ( rule__XOseeEnumType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:331:1: ( rule__XOseeEnumType__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:332:1: ( rule__XOseeEnumType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:332:2: rule__XOseeEnumType__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:344:1: entryRuleXOseeEnumEntry : ruleXOseeEnumEntry EOF ;
    public final void entryRuleXOseeEnumEntry() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:345:1: ( ruleXOseeEnumEntry EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:346:1: ruleXOseeEnumEntry EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:353:1: ruleXOseeEnumEntry : ( ( rule__XOseeEnumEntry__Group__0 ) ) ;
    public final void ruleXOseeEnumEntry() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:357:2: ( ( ( rule__XOseeEnumEntry__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:358:1: ( ( rule__XOseeEnumEntry__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:358:1: ( ( rule__XOseeEnumEntry__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:359:1: ( rule__XOseeEnumEntry__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:360:1: ( rule__XOseeEnumEntry__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:360:2: rule__XOseeEnumEntry__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:372:1: entryRuleXOseeEnumOverride : ruleXOseeEnumOverride EOF ;
    public final void entryRuleXOseeEnumOverride() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:373:1: ( ruleXOseeEnumOverride EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:374:1: ruleXOseeEnumOverride EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:381:1: ruleXOseeEnumOverride : ( ( rule__XOseeEnumOverride__Group__0 ) ) ;
    public final void ruleXOseeEnumOverride() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:385:2: ( ( ( rule__XOseeEnumOverride__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:386:1: ( ( rule__XOseeEnumOverride__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:386:1: ( ( rule__XOseeEnumOverride__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:387:1: ( rule__XOseeEnumOverride__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:388:1: ( rule__XOseeEnumOverride__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:388:2: rule__XOseeEnumOverride__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:400:1: entryRuleOverrideOption : ruleOverrideOption EOF ;
    public final void entryRuleOverrideOption() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:401:1: ( ruleOverrideOption EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:402:1: ruleOverrideOption EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:409:1: ruleOverrideOption : ( ( rule__OverrideOption__Alternatives ) ) ;
    public final void ruleOverrideOption() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:413:2: ( ( ( rule__OverrideOption__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:414:1: ( ( rule__OverrideOption__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:414:1: ( ( rule__OverrideOption__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:415:1: ( rule__OverrideOption__Alternatives )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOverrideOptionAccess().getAlternatives()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:416:1: ( rule__OverrideOption__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:416:2: rule__OverrideOption__Alternatives
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:428:1: entryRuleAddEnum : ruleAddEnum EOF ;
    public final void entryRuleAddEnum() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:429:1: ( ruleAddEnum EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:430:1: ruleAddEnum EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:437:1: ruleAddEnum : ( ( rule__AddEnum__Group__0 ) ) ;
    public final void ruleAddEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:441:2: ( ( ( rule__AddEnum__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:442:1: ( ( rule__AddEnum__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:442:1: ( ( rule__AddEnum__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:443:1: ( rule__AddEnum__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:444:1: ( rule__AddEnum__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:444:2: rule__AddEnum__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:456:1: entryRuleRemoveEnum : ruleRemoveEnum EOF ;
    public final void entryRuleRemoveEnum() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:457:1: ( ruleRemoveEnum EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:458:1: ruleRemoveEnum EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:465:1: ruleRemoveEnum : ( ( rule__RemoveEnum__Group__0 ) ) ;
    public final void ruleRemoveEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:469:2: ( ( ( rule__RemoveEnum__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:470:1: ( ( rule__RemoveEnum__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:470:1: ( ( rule__RemoveEnum__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:471:1: ( rule__RemoveEnum__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:472:1: ( rule__RemoveEnum__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:472:2: rule__RemoveEnum__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:484:1: entryRuleXRelationType : ruleXRelationType EOF ;
    public final void entryRuleXRelationType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:485:1: ( ruleXRelationType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:486:1: ruleXRelationType EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:493:1: ruleXRelationType : ( ( rule__XRelationType__Group__0 ) ) ;
    public final void ruleXRelationType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:497:2: ( ( ( rule__XRelationType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:498:1: ( ( rule__XRelationType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:498:1: ( ( rule__XRelationType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:499:1: ( rule__XRelationType__Group__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getGroup()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:500:1: ( rule__XRelationType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:500:2: rule__XRelationType__Group__0
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:512:1: entryRuleRelationOrderType : ruleRelationOrderType EOF ;
    public final void entryRuleRelationOrderType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:513:1: ( ruleRelationOrderType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:514:1: ruleRelationOrderType EOF
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:521:1: ruleRelationOrderType : ( ( rule__RelationOrderType__Alternatives ) ) ;
    public final void ruleRelationOrderType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:525:2: ( ( ( rule__RelationOrderType__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:526:1: ( ( rule__RelationOrderType__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:526:1: ( ( rule__RelationOrderType__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:527:1: ( rule__RelationOrderType__Alternatives )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRelationOrderTypeAccess().getAlternatives()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:528:1: ( rule__RelationOrderType__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:528:2: rule__RelationOrderType__Alternatives
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:541:1: ruleRelationMultiplicityEnum : ( ( rule__RelationMultiplicityEnum__Alternatives ) ) ;
    public final void ruleRelationMultiplicityEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:545:1: ( ( ( rule__RelationMultiplicityEnum__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:546:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:546:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:547:1: ( rule__RelationMultiplicityEnum__Alternatives )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRelationMultiplicityEnumAccess().getAlternatives()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:548:1: ( rule__RelationMultiplicityEnum__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:548:2: rule__RelationMultiplicityEnum__Alternatives
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:559:1: rule__OseeTypeModel__Alternatives_1 : ( ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) ) );
    public final void rule__OseeTypeModel__Alternatives_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:563:1: ( ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) ) )
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
                    new NoViableAltException("559:1: rule__OseeTypeModel__Alternatives_1 : ( ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) ) );", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:564:1: ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:564:1: ( ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:565:1: ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeModelAccess().getArtifactTypesAssignment_1_0()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:566:1: ( rule__OseeTypeModel__ArtifactTypesAssignment_1_0 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:566:2: rule__OseeTypeModel__ArtifactTypesAssignment_1_0
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:570:6: ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:570:6: ( ( rule__OseeTypeModel__RelationTypesAssignment_1_1 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:571:1: ( rule__OseeTypeModel__RelationTypesAssignment_1_1 )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeModelAccess().getRelationTypesAssignment_1_1()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:572:1: ( rule__OseeTypeModel__RelationTypesAssignment_1_1 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:572:2: rule__OseeTypeModel__RelationTypesAssignment_1_1
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:576:6: ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:576:6: ( ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:577:1: ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeModelAccess().getAttributeTypesAssignment_1_2()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:578:1: ( rule__OseeTypeModel__AttributeTypesAssignment_1_2 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:578:2: rule__OseeTypeModel__AttributeTypesAssignment_1_2
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:582:6: ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:582:6: ( ( rule__OseeTypeModel__EnumTypesAssignment_1_3 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:583:1: ( rule__OseeTypeModel__EnumTypesAssignment_1_3 )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeModelAccess().getEnumTypesAssignment_1_3()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:584:1: ( rule__OseeTypeModel__EnumTypesAssignment_1_3 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:584:2: rule__OseeTypeModel__EnumTypesAssignment_1_3
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:588:6: ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:588:6: ( ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:589:1: ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getOseeTypeModelAccess().getEnumOverridesAssignment_1_4()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:590:1: ( rule__OseeTypeModel__EnumOverridesAssignment_1_4 )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:590:2: rule__OseeTypeModel__EnumOverridesAssignment_1_4
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:600:1: rule__OseeType__Alternatives : ( ( ruleXArtifactType ) | ( ruleXRelationType ) | ( ruleXAttributeType ) | ( ruleXOseeEnumType ) );
    public final void rule__OseeType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:604:1: ( ( ruleXArtifactType ) | ( ruleXRelationType ) | ( ruleXAttributeType ) | ( ruleXOseeEnumType ) )
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
                    new NoViableAltException("600:1: rule__OseeType__Alternatives : ( ( ruleXArtifactType ) | ( ruleXRelationType ) | ( ruleXAttributeType ) | ( ruleXOseeEnumType ) );", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:605:1: ( ruleXArtifactType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:605:1: ( ruleXArtifactType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:606:1: ruleXArtifactType
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:611:6: ( ruleXRelationType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:611:6: ( ruleXRelationType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:612:1: ruleXRelationType
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:617:6: ( ruleXAttributeType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:617:6: ( ruleXAttributeType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:618:1: ruleXAttributeType
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:623:6: ( ruleXOseeEnumType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:623:6: ( ruleXOseeEnumType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:624:1: ruleXOseeEnumType
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:634:1: rule__XAttributeType__DataProviderAlternatives_8_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__XAttributeType__DataProviderAlternatives_8_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:638:1: ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) )
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
                    new NoViableAltException("634:1: rule__XAttributeType__DataProviderAlternatives_8_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:639:1: ( 'DefaultAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:639:1: ( 'DefaultAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:640:1: 'DefaultAttributeDataProvider'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:647:6: ( 'UriAttributeDataProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:647:6: ( 'UriAttributeDataProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:648:1: 'UriAttributeDataProvider'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:655:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:655:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:656:1: ruleQUALIFIED_NAME
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:666:1: rule__XAttributeType__MaxAlternatives_12_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );
    public final void rule__XAttributeType__MaxAlternatives_12_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:670:1: ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) )
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
                    new NoViableAltException("666:1: rule__XAttributeType__MaxAlternatives_12_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:671:1: ( RULE_WHOLE_NUM_STR )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:671:1: ( RULE_WHOLE_NUM_STR )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:672:1: RULE_WHOLE_NUM_STR
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:677:6: ( 'unlimited' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:677:6: ( 'unlimited' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:678:1: 'unlimited'
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:690:1: rule__XAttributeType__TaggerIdAlternatives_13_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__XAttributeType__TaggerIdAlternatives_13_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:694:1: ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) )
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
                    new NoViableAltException("690:1: rule__XAttributeType__TaggerIdAlternatives_13_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:695:1: ( 'DefaultAttributeTaggerProvider' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:695:1: ( 'DefaultAttributeTaggerProvider' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:696:1: 'DefaultAttributeTaggerProvider'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:703:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:703:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:704:1: ruleQUALIFIED_NAME
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:714:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeBaseType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:718:1: ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) )
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
                    new NoViableAltException("714:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'WordAttribute' ) | ( ruleQUALIFIED_NAME ) );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:719:1: ( 'BooleanAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:719:1: ( 'BooleanAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:720:1: 'BooleanAttribute'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:727:6: ( 'CompressedContentAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:727:6: ( 'CompressedContentAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:728:1: 'CompressedContentAttribute'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:735:6: ( 'DateAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:735:6: ( 'DateAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:736:1: 'DateAttribute'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:743:6: ( 'EnumeratedAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:743:6: ( 'EnumeratedAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:744:1: 'EnumeratedAttribute'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:751:6: ( 'FloatingPointAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:751:6: ( 'FloatingPointAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:752:1: 'FloatingPointAttribute'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:759:6: ( 'IntegerAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:759:6: ( 'IntegerAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:760:1: 'IntegerAttribute'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:767:6: ( 'JavaObjectAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:767:6: ( 'JavaObjectAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:768:1: 'JavaObjectAttribute'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:775:6: ( 'StringAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:775:6: ( 'StringAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:776:1: 'StringAttribute'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:783:6: ( 'WordAttribute' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:783:6: ( 'WordAttribute' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:784:1: 'WordAttribute'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:791:6: ( ruleQUALIFIED_NAME )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:791:6: ( ruleQUALIFIED_NAME )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:792:1: ruleQUALIFIED_NAME
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:802:1: rule__OverrideOption__Alternatives : ( ( ruleAddEnum ) | ( ruleRemoveEnum ) );
    public final void rule__OverrideOption__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:806:1: ( ( ruleAddEnum ) | ( ruleRemoveEnum ) )
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
                    new NoViableAltException("802:1: rule__OverrideOption__Alternatives : ( ( ruleAddEnum ) | ( ruleRemoveEnum ) );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:807:1: ( ruleAddEnum )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:807:1: ( ruleAddEnum )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:808:1: ruleAddEnum
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:813:6: ( ruleRemoveEnum )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:813:6: ( ruleRemoveEnum )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:814:1: ruleRemoveEnum
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:824:1: rule__RelationOrderType__Alternatives : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );
    public final void rule__RelationOrderType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:828:1: ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) )
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
                    new NoViableAltException("824:1: rule__RelationOrderType__Alternatives : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:829:1: ( 'Lexicographical_Ascending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:829:1: ( 'Lexicographical_Ascending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:830:1: 'Lexicographical_Ascending'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:837:6: ( 'Lexicographical_Descending' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:837:6: ( 'Lexicographical_Descending' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:838:1: 'Lexicographical_Descending'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:845:6: ( 'Unordered' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:845:6: ( 'Unordered' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:846:1: 'Unordered'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:853:6: ( RULE_ID )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:853:6: ( RULE_ID )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:854:1: RULE_ID
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:864:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );
    public final void rule__RelationMultiplicityEnum__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:868:1: ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) )
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
                    new NoViableAltException("864:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:869:1: ( ( 'ONE_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:869:1: ( ( 'ONE_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:870:1: ( 'ONE_TO_ONE' )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:871:1: ( 'ONE_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:871:3: 'ONE_TO_ONE'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:876:6: ( ( 'ONE_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:876:6: ( ( 'ONE_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:877:1: ( 'ONE_TO_MANY' )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:878:1: ( 'ONE_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:878:3: 'ONE_TO_MANY'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:883:6: ( ( 'MANY_TO_ONE' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:883:6: ( ( 'MANY_TO_ONE' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:884:1: ( 'MANY_TO_ONE' )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:885:1: ( 'MANY_TO_ONE' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:885:3: 'MANY_TO_ONE'
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
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:890:6: ( ( 'MANY_TO_MANY' ) )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:890:6: ( ( 'MANY_TO_MANY' ) )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:891:1: ( 'MANY_TO_MANY' )
                    {
                    if ( backtracking==0 ) {
                       before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3()); 
                    }
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:892:1: ( 'MANY_TO_MANY' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:892:3: 'MANY_TO_MANY'
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:904:1: rule__OseeTypeModel__Group__0 : rule__OseeTypeModel__Group__0__Impl rule__OseeTypeModel__Group__1 ;
    public final void rule__OseeTypeModel__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:908:1: ( rule__OseeTypeModel__Group__0__Impl rule__OseeTypeModel__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:909:2: rule__OseeTypeModel__Group__0__Impl rule__OseeTypeModel__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__Group__0__Impl_in_rule__OseeTypeModel__Group__01948);
            rule__OseeTypeModel__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01951);
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


    // $ANTLR start rule__OseeTypeModel__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:916:1: rule__OseeTypeModel__Group__0__Impl : ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) ;
    public final void rule__OseeTypeModel__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:920:1: ( ( ( rule__OseeTypeModel__ImportsAssignment_0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:921:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:921:1: ( ( rule__OseeTypeModel__ImportsAssignment_0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:922:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getImportsAssignment_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:923:1: ( rule__OseeTypeModel__ImportsAssignment_0 )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==32) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:923:2: rule__OseeTypeModel__ImportsAssignment_0
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__0__Impl1978);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__OseeTypeModel__Group__0__Impl


    // $ANTLR start rule__OseeTypeModel__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:933:1: rule__OseeTypeModel__Group__1 : rule__OseeTypeModel__Group__1__Impl ;
    public final void rule__OseeTypeModel__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:937:1: ( rule__OseeTypeModel__Group__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:938:2: rule__OseeTypeModel__Group__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__Group__1__Impl_in_rule__OseeTypeModel__Group__12009);
            rule__OseeTypeModel__Group__1__Impl();
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
    // $ANTLR end rule__OseeTypeModel__Group__1


    // $ANTLR start rule__OseeTypeModel__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:944:1: rule__OseeTypeModel__Group__1__Impl : ( ( rule__OseeTypeModel__Alternatives_1 )* ) ;
    public final void rule__OseeTypeModel__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:948:1: ( ( ( rule__OseeTypeModel__Alternatives_1 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:949:1: ( ( rule__OseeTypeModel__Alternatives_1 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:949:1: ( ( rule__OseeTypeModel__Alternatives_1 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:950:1: ( rule__OseeTypeModel__Alternatives_1 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getAlternatives_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:951:1: ( rule__OseeTypeModel__Alternatives_1 )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==34||LA11_0==42||LA11_0==52||LA11_0==55||LA11_0==58||LA11_0==65) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:951:2: rule__OseeTypeModel__Alternatives_1
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__OseeTypeModel__Alternatives_1_in_rule__OseeTypeModel__Group__1__Impl2036);
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
    // $ANTLR end rule__OseeTypeModel__Group__1__Impl


    // $ANTLR start rule__Import__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:965:1: rule__Import__Group__0 : rule__Import__Group__0__Impl rule__Import__Group__1 ;
    public final void rule__Import__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:969:1: ( rule__Import__Group__0__Impl rule__Import__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:970:2: rule__Import__Group__0__Impl rule__Import__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__Import__Group__0__Impl_in_rule__Import__Group__02071);
            rule__Import__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__Import__Group__1_in_rule__Import__Group__02074);
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


    // $ANTLR start rule__Import__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:977:1: rule__Import__Group__0__Impl : ( 'import' ) ;
    public final void rule__Import__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:981:1: ( ( 'import' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:982:1: ( 'import' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:982:1: ( 'import' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:983:1: 'import'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getImportAccess().getImportKeyword_0()); 
            }
            match(input,32,FollowSets000.FOLLOW_32_in_rule__Import__Group__0__Impl2102); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getImportAccess().getImportKeyword_0()); 
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
    // $ANTLR end rule__Import__Group__0__Impl


    // $ANTLR start rule__Import__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:996:1: rule__Import__Group__1 : rule__Import__Group__1__Impl ;
    public final void rule__Import__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1000:1: ( rule__Import__Group__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1001:2: rule__Import__Group__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__Import__Group__1__Impl_in_rule__Import__Group__12133);
            rule__Import__Group__1__Impl();
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
    // $ANTLR end rule__Import__Group__1


    // $ANTLR start rule__Import__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1007:1: rule__Import__Group__1__Impl : ( ( rule__Import__ImportURIAssignment_1 ) ) ;
    public final void rule__Import__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1011:1: ( ( ( rule__Import__ImportURIAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1012:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1012:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1013:1: ( rule__Import__ImportURIAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getImportAccess().getImportURIAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1014:1: ( rule__Import__ImportURIAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1014:2: rule__Import__ImportURIAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__1__Impl2160);
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
    // $ANTLR end rule__Import__Group__1__Impl


    // $ANTLR start rule__QUALIFIED_NAME__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1028:1: rule__QUALIFIED_NAME__Group__0 : rule__QUALIFIED_NAME__Group__0__Impl rule__QUALIFIED_NAME__Group__1 ;
    public final void rule__QUALIFIED_NAME__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1032:1: ( rule__QUALIFIED_NAME__Group__0__Impl rule__QUALIFIED_NAME__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1033:2: rule__QUALIFIED_NAME__Group__0__Impl rule__QUALIFIED_NAME__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group__0__Impl_in_rule__QUALIFIED_NAME__Group__02194);
            rule__QUALIFIED_NAME__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__02197);
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


    // $ANTLR start rule__QUALIFIED_NAME__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1040:1: rule__QUALIFIED_NAME__Group__0__Impl : ( RULE_ID ) ;
    public final void rule__QUALIFIED_NAME__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1044:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1045:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1045:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1046:1: RULE_ID
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
            }
            match(input,RULE_ID,FollowSets000.FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__0__Impl2224); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
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
    // $ANTLR end rule__QUALIFIED_NAME__Group__0__Impl


    // $ANTLR start rule__QUALIFIED_NAME__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1057:1: rule__QUALIFIED_NAME__Group__1 : rule__QUALIFIED_NAME__Group__1__Impl ;
    public final void rule__QUALIFIED_NAME__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1061:1: ( rule__QUALIFIED_NAME__Group__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1062:2: rule__QUALIFIED_NAME__Group__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group__1__Impl_in_rule__QUALIFIED_NAME__Group__12253);
            rule__QUALIFIED_NAME__Group__1__Impl();
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
    // $ANTLR end rule__QUALIFIED_NAME__Group__1


    // $ANTLR start rule__QUALIFIED_NAME__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1068:1: rule__QUALIFIED_NAME__Group__1__Impl : ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) ;
    public final void rule__QUALIFIED_NAME__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1072:1: ( ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1073:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1073:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1074:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1075:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==33) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1075:2: rule__QUALIFIED_NAME__Group_1__0
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__1__Impl2280);
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
    // $ANTLR end rule__QUALIFIED_NAME__Group__1__Impl


    // $ANTLR start rule__QUALIFIED_NAME__Group_1__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1089:1: rule__QUALIFIED_NAME__Group_1__0 : rule__QUALIFIED_NAME__Group_1__0__Impl rule__QUALIFIED_NAME__Group_1__1 ;
    public final void rule__QUALIFIED_NAME__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1093:1: ( rule__QUALIFIED_NAME__Group_1__0__Impl rule__QUALIFIED_NAME__Group_1__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1094:2: rule__QUALIFIED_NAME__Group_1__0__Impl rule__QUALIFIED_NAME__Group_1__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group_1__0__Impl_in_rule__QUALIFIED_NAME__Group_1__02315);
            rule__QUALIFIED_NAME__Group_1__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__02318);
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


    // $ANTLR start rule__QUALIFIED_NAME__Group_1__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1101:1: rule__QUALIFIED_NAME__Group_1__0__Impl : ( '.' ) ;
    public final void rule__QUALIFIED_NAME__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1105:1: ( ( '.' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1106:1: ( '.' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1106:1: ( '.' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1107:1: '.'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            }
            match(input,33,FollowSets000.FOLLOW_33_in_rule__QUALIFIED_NAME__Group_1__0__Impl2346); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
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
    // $ANTLR end rule__QUALIFIED_NAME__Group_1__0__Impl


    // $ANTLR start rule__QUALIFIED_NAME__Group_1__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1120:1: rule__QUALIFIED_NAME__Group_1__1 : rule__QUALIFIED_NAME__Group_1__1__Impl ;
    public final void rule__QUALIFIED_NAME__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1124:1: ( rule__QUALIFIED_NAME__Group_1__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1125:2: rule__QUALIFIED_NAME__Group_1__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__QUALIFIED_NAME__Group_1__1__Impl_in_rule__QUALIFIED_NAME__Group_1__12377);
            rule__QUALIFIED_NAME__Group_1__1__Impl();
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
    // $ANTLR end rule__QUALIFIED_NAME__Group_1__1


    // $ANTLR start rule__QUALIFIED_NAME__Group_1__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1131:1: rule__QUALIFIED_NAME__Group_1__1__Impl : ( RULE_ID ) ;
    public final void rule__QUALIFIED_NAME__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1135:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1136:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1136:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1137:1: RULE_ID
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
            }
            match(input,RULE_ID,FollowSets000.FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__1__Impl2404); if (failed) return ;
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
    // $ANTLR end rule__QUALIFIED_NAME__Group_1__1__Impl


    // $ANTLR start rule__XArtifactType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1152:1: rule__XArtifactType__Group__0 : rule__XArtifactType__Group__0__Impl rule__XArtifactType__Group__1 ;
    public final void rule__XArtifactType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1156:1: ( rule__XArtifactType__Group__0__Impl rule__XArtifactType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1157:2: rule__XArtifactType__Group__0__Impl rule__XArtifactType__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__0__Impl_in_rule__XArtifactType__Group__02437);
            rule__XArtifactType__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__1_in_rule__XArtifactType__Group__02440);
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


    // $ANTLR start rule__XArtifactType__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1164:1: rule__XArtifactType__Group__0__Impl : ( ( rule__XArtifactType__AbstractAssignment_0 )? ) ;
    public final void rule__XArtifactType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1168:1: ( ( ( rule__XArtifactType__AbstractAssignment_0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1169:1: ( ( rule__XArtifactType__AbstractAssignment_0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1169:1: ( ( rule__XArtifactType__AbstractAssignment_0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1170:1: ( rule__XArtifactType__AbstractAssignment_0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getAbstractAssignment_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1171:1: ( rule__XArtifactType__AbstractAssignment_0 )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==65) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1171:2: rule__XArtifactType__AbstractAssignment_0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__AbstractAssignment_0_in_rule__XArtifactType__Group__0__Impl2467);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__0__Impl


    // $ANTLR start rule__XArtifactType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1181:1: rule__XArtifactType__Group__1 : rule__XArtifactType__Group__1__Impl rule__XArtifactType__Group__2 ;
    public final void rule__XArtifactType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1185:1: ( rule__XArtifactType__Group__1__Impl rule__XArtifactType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1186:2: rule__XArtifactType__Group__1__Impl rule__XArtifactType__Group__2
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__1__Impl_in_rule__XArtifactType__Group__12498);
            rule__XArtifactType__Group__1__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__2_in_rule__XArtifactType__Group__12501);
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


    // $ANTLR start rule__XArtifactType__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1193:1: rule__XArtifactType__Group__1__Impl : ( 'artifactType' ) ;
    public final void rule__XArtifactType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1197:1: ( ( 'artifactType' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1198:1: ( 'artifactType' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1198:1: ( 'artifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1199:1: 'artifactType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1()); 
            }
            match(input,34,FollowSets000.FOLLOW_34_in_rule__XArtifactType__Group__1__Impl2529); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1()); 
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
    // $ANTLR end rule__XArtifactType__Group__1__Impl


    // $ANTLR start rule__XArtifactType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1212:1: rule__XArtifactType__Group__2 : rule__XArtifactType__Group__2__Impl rule__XArtifactType__Group__3 ;
    public final void rule__XArtifactType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1216:1: ( rule__XArtifactType__Group__2__Impl rule__XArtifactType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1217:2: rule__XArtifactType__Group__2__Impl rule__XArtifactType__Group__3
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__2__Impl_in_rule__XArtifactType__Group__22560);
            rule__XArtifactType__Group__2__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__3_in_rule__XArtifactType__Group__22563);
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


    // $ANTLR start rule__XArtifactType__Group__2__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1224:1: rule__XArtifactType__Group__2__Impl : ( ( rule__XArtifactType__NameAssignment_2 ) ) ;
    public final void rule__XArtifactType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1228:1: ( ( ( rule__XArtifactType__NameAssignment_2 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1229:1: ( ( rule__XArtifactType__NameAssignment_2 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1229:1: ( ( rule__XArtifactType__NameAssignment_2 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1230:1: ( rule__XArtifactType__NameAssignment_2 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getNameAssignment_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1231:1: ( rule__XArtifactType__NameAssignment_2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1231:2: rule__XArtifactType__NameAssignment_2
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__NameAssignment_2_in_rule__XArtifactType__Group__2__Impl2590);
            rule__XArtifactType__NameAssignment_2();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getNameAssignment_2()); 
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
    // $ANTLR end rule__XArtifactType__Group__2__Impl


    // $ANTLR start rule__XArtifactType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1241:1: rule__XArtifactType__Group__3 : rule__XArtifactType__Group__3__Impl rule__XArtifactType__Group__4 ;
    public final void rule__XArtifactType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1245:1: ( rule__XArtifactType__Group__3__Impl rule__XArtifactType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1246:2: rule__XArtifactType__Group__3__Impl rule__XArtifactType__Group__4
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__3__Impl_in_rule__XArtifactType__Group__32620);
            rule__XArtifactType__Group__3__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__4_in_rule__XArtifactType__Group__32623);
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


    // $ANTLR start rule__XArtifactType__Group__3__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1253:1: rule__XArtifactType__Group__3__Impl : ( ( rule__XArtifactType__Group_3__0 )? ) ;
    public final void rule__XArtifactType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1257:1: ( ( ( rule__XArtifactType__Group_3__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1258:1: ( ( rule__XArtifactType__Group_3__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1258:1: ( ( rule__XArtifactType__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1259:1: ( rule__XArtifactType__Group_3__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getGroup_3()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1260:1: ( rule__XArtifactType__Group_3__0 )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==38) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1260:2: rule__XArtifactType__Group_3__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3__0_in_rule__XArtifactType__Group__3__Impl2650);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__3__Impl


    // $ANTLR start rule__XArtifactType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1270:1: rule__XArtifactType__Group__4 : rule__XArtifactType__Group__4__Impl rule__XArtifactType__Group__5 ;
    public final void rule__XArtifactType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1274:1: ( rule__XArtifactType__Group__4__Impl rule__XArtifactType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1275:2: rule__XArtifactType__Group__4__Impl rule__XArtifactType__Group__5
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__4__Impl_in_rule__XArtifactType__Group__42681);
            rule__XArtifactType__Group__4__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__5_in_rule__XArtifactType__Group__42684);
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


    // $ANTLR start rule__XArtifactType__Group__4__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1282:1: rule__XArtifactType__Group__4__Impl : ( '{' ) ;
    public final void rule__XArtifactType__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1286:1: ( ( '{' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1287:1: ( '{' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1287:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1288:1: '{'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4()); 
            }
            match(input,35,FollowSets000.FOLLOW_35_in_rule__XArtifactType__Group__4__Impl2712); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4()); 
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
    // $ANTLR end rule__XArtifactType__Group__4__Impl


    // $ANTLR start rule__XArtifactType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1301:1: rule__XArtifactType__Group__5 : rule__XArtifactType__Group__5__Impl rule__XArtifactType__Group__6 ;
    public final void rule__XArtifactType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1305:1: ( rule__XArtifactType__Group__5__Impl rule__XArtifactType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1306:2: rule__XArtifactType__Group__5__Impl rule__XArtifactType__Group__6
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__5__Impl_in_rule__XArtifactType__Group__52743);
            rule__XArtifactType__Group__5__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__6_in_rule__XArtifactType__Group__52746);
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


    // $ANTLR start rule__XArtifactType__Group__5__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1313:1: rule__XArtifactType__Group__5__Impl : ( 'guid' ) ;
    public final void rule__XArtifactType__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1317:1: ( ( 'guid' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1318:1: ( 'guid' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1318:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1319:1: 'guid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5()); 
            }
            match(input,36,FollowSets000.FOLLOW_36_in_rule__XArtifactType__Group__5__Impl2774); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5()); 
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
    // $ANTLR end rule__XArtifactType__Group__5__Impl


    // $ANTLR start rule__XArtifactType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1332:1: rule__XArtifactType__Group__6 : rule__XArtifactType__Group__6__Impl rule__XArtifactType__Group__7 ;
    public final void rule__XArtifactType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1336:1: ( rule__XArtifactType__Group__6__Impl rule__XArtifactType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1337:2: rule__XArtifactType__Group__6__Impl rule__XArtifactType__Group__7
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__6__Impl_in_rule__XArtifactType__Group__62805);
            rule__XArtifactType__Group__6__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__7_in_rule__XArtifactType__Group__62808);
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


    // $ANTLR start rule__XArtifactType__Group__6__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1344:1: rule__XArtifactType__Group__6__Impl : ( ( rule__XArtifactType__TypeGuidAssignment_6 ) ) ;
    public final void rule__XArtifactType__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1348:1: ( ( ( rule__XArtifactType__TypeGuidAssignment_6 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1349:1: ( ( rule__XArtifactType__TypeGuidAssignment_6 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1349:1: ( ( rule__XArtifactType__TypeGuidAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1350:1: ( rule__XArtifactType__TypeGuidAssignment_6 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getTypeGuidAssignment_6()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1351:1: ( rule__XArtifactType__TypeGuidAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1351:2: rule__XArtifactType__TypeGuidAssignment_6
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__TypeGuidAssignment_6_in_rule__XArtifactType__Group__6__Impl2835);
            rule__XArtifactType__TypeGuidAssignment_6();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getTypeGuidAssignment_6()); 
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
    // $ANTLR end rule__XArtifactType__Group__6__Impl


    // $ANTLR start rule__XArtifactType__Group__7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1361:1: rule__XArtifactType__Group__7 : rule__XArtifactType__Group__7__Impl rule__XArtifactType__Group__8 ;
    public final void rule__XArtifactType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1365:1: ( rule__XArtifactType__Group__7__Impl rule__XArtifactType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1366:2: rule__XArtifactType__Group__7__Impl rule__XArtifactType__Group__8
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__7__Impl_in_rule__XArtifactType__Group__72865);
            rule__XArtifactType__Group__7__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__8_in_rule__XArtifactType__Group__72868);
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


    // $ANTLR start rule__XArtifactType__Group__7__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1373:1: rule__XArtifactType__Group__7__Impl : ( ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )* ) ;
    public final void rule__XArtifactType__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1377:1: ( ( ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1378:1: ( ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1378:1: ( ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1379:1: ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesAssignment_7()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1380:1: ( rule__XArtifactType__ValidAttributeTypesAssignment_7 )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==40) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1380:2: rule__XArtifactType__ValidAttributeTypesAssignment_7
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__ValidAttributeTypesAssignment_7_in_rule__XArtifactType__Group__7__Impl2895);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XArtifactType__Group__7__Impl


    // $ANTLR start rule__XArtifactType__Group__8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1390:1: rule__XArtifactType__Group__8 : rule__XArtifactType__Group__8__Impl ;
    public final void rule__XArtifactType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1394:1: ( rule__XArtifactType__Group__8__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1395:2: rule__XArtifactType__Group__8__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group__8__Impl_in_rule__XArtifactType__Group__82926);
            rule__XArtifactType__Group__8__Impl();
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
    // $ANTLR end rule__XArtifactType__Group__8


    // $ANTLR start rule__XArtifactType__Group__8__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1401:1: rule__XArtifactType__Group__8__Impl : ( '}' ) ;
    public final void rule__XArtifactType__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1405:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1406:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1406:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1407:1: '}'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getRightCurlyBracketKeyword_8()); 
            }
            match(input,37,FollowSets000.FOLLOW_37_in_rule__XArtifactType__Group__8__Impl2954); if (failed) return ;
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
    // $ANTLR end rule__XArtifactType__Group__8__Impl


    // $ANTLR start rule__XArtifactType__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1438:1: rule__XArtifactType__Group_3__0 : rule__XArtifactType__Group_3__0__Impl rule__XArtifactType__Group_3__1 ;
    public final void rule__XArtifactType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1442:1: ( rule__XArtifactType__Group_3__0__Impl rule__XArtifactType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1443:2: rule__XArtifactType__Group_3__0__Impl rule__XArtifactType__Group_3__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3__0__Impl_in_rule__XArtifactType__Group_3__03003);
            rule__XArtifactType__Group_3__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3__1_in_rule__XArtifactType__Group_3__03006);
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


    // $ANTLR start rule__XArtifactType__Group_3__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1450:1: rule__XArtifactType__Group_3__0__Impl : ( 'extends' ) ;
    public final void rule__XArtifactType__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1454:1: ( ( 'extends' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1455:1: ( 'extends' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1455:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1456:1: 'extends'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0()); 
            }
            match(input,38,FollowSets000.FOLLOW_38_in_rule__XArtifactType__Group_3__0__Impl3034); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0()); 
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
    // $ANTLR end rule__XArtifactType__Group_3__0__Impl


    // $ANTLR start rule__XArtifactType__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1469:1: rule__XArtifactType__Group_3__1 : rule__XArtifactType__Group_3__1__Impl rule__XArtifactType__Group_3__2 ;
    public final void rule__XArtifactType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1473:1: ( rule__XArtifactType__Group_3__1__Impl rule__XArtifactType__Group_3__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1474:2: rule__XArtifactType__Group_3__1__Impl rule__XArtifactType__Group_3__2
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3__1__Impl_in_rule__XArtifactType__Group_3__13065);
            rule__XArtifactType__Group_3__1__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3__2_in_rule__XArtifactType__Group_3__13068);
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


    // $ANTLR start rule__XArtifactType__Group_3__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1481:1: rule__XArtifactType__Group_3__1__Impl : ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) ) ;
    public final void rule__XArtifactType__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1485:1: ( ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1486:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1486:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1487:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1488:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1488:2: rule__XArtifactType__SuperArtifactTypesAssignment_3_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__SuperArtifactTypesAssignment_3_1_in_rule__XArtifactType__Group_3__1__Impl3095);
            rule__XArtifactType__SuperArtifactTypesAssignment_3_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 
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
    // $ANTLR end rule__XArtifactType__Group_3__1__Impl


    // $ANTLR start rule__XArtifactType__Group_3__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1498:1: rule__XArtifactType__Group_3__2 : rule__XArtifactType__Group_3__2__Impl ;
    public final void rule__XArtifactType__Group_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1502:1: ( rule__XArtifactType__Group_3__2__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1503:2: rule__XArtifactType__Group_3__2__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3__2__Impl_in_rule__XArtifactType__Group_3__23125);
            rule__XArtifactType__Group_3__2__Impl();
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
    // $ANTLR end rule__XArtifactType__Group_3__2


    // $ANTLR start rule__XArtifactType__Group_3__2__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1509:1: rule__XArtifactType__Group_3__2__Impl : ( ( rule__XArtifactType__Group_3_2__0 )* ) ;
    public final void rule__XArtifactType__Group_3__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1513:1: ( ( ( rule__XArtifactType__Group_3_2__0 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1514:1: ( ( rule__XArtifactType__Group_3_2__0 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1514:1: ( ( rule__XArtifactType__Group_3_2__0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1515:1: ( rule__XArtifactType__Group_3_2__0 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getGroup_3_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1516:1: ( rule__XArtifactType__Group_3_2__0 )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==39) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1516:2: rule__XArtifactType__Group_3_2__0
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3_2__0_in_rule__XArtifactType__Group_3__2__Impl3152);
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
    // $ANTLR end rule__XArtifactType__Group_3__2__Impl


    // $ANTLR start rule__XArtifactType__Group_3_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1532:1: rule__XArtifactType__Group_3_2__0 : rule__XArtifactType__Group_3_2__0__Impl rule__XArtifactType__Group_3_2__1 ;
    public final void rule__XArtifactType__Group_3_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1536:1: ( rule__XArtifactType__Group_3_2__0__Impl rule__XArtifactType__Group_3_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1537:2: rule__XArtifactType__Group_3_2__0__Impl rule__XArtifactType__Group_3_2__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3_2__0__Impl_in_rule__XArtifactType__Group_3_2__03189);
            rule__XArtifactType__Group_3_2__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3_2__1_in_rule__XArtifactType__Group_3_2__03192);
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


    // $ANTLR start rule__XArtifactType__Group_3_2__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1544:1: rule__XArtifactType__Group_3_2__0__Impl : ( ',' ) ;
    public final void rule__XArtifactType__Group_3_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1548:1: ( ( ',' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1549:1: ( ',' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1549:1: ( ',' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1550:1: ','
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0()); 
            }
            match(input,39,FollowSets000.FOLLOW_39_in_rule__XArtifactType__Group_3_2__0__Impl3220); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0()); 
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
    // $ANTLR end rule__XArtifactType__Group_3_2__0__Impl


    // $ANTLR start rule__XArtifactType__Group_3_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1563:1: rule__XArtifactType__Group_3_2__1 : rule__XArtifactType__Group_3_2__1__Impl ;
    public final void rule__XArtifactType__Group_3_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1567:1: ( rule__XArtifactType__Group_3_2__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1568:2: rule__XArtifactType__Group_3_2__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__Group_3_2__1__Impl_in_rule__XArtifactType__Group_3_2__13251);
            rule__XArtifactType__Group_3_2__1__Impl();
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
    // $ANTLR end rule__XArtifactType__Group_3_2__1


    // $ANTLR start rule__XArtifactType__Group_3_2__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1574:1: rule__XArtifactType__Group_3_2__1__Impl : ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) ;
    public final void rule__XArtifactType__Group_3_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1578:1: ( ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1579:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1579:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1580:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_2_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1581:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1581:2: rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1_in_rule__XArtifactType__Group_3_2__1__Impl3278);
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
    // $ANTLR end rule__XArtifactType__Group_3_2__1__Impl


    // $ANTLR start rule__XAttributeTypeRef__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1595:1: rule__XAttributeTypeRef__Group__0 : rule__XAttributeTypeRef__Group__0__Impl rule__XAttributeTypeRef__Group__1 ;
    public final void rule__XAttributeTypeRef__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1599:1: ( rule__XAttributeTypeRef__Group__0__Impl rule__XAttributeTypeRef__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1600:2: rule__XAttributeTypeRef__Group__0__Impl rule__XAttributeTypeRef__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group__0__Impl_in_rule__XAttributeTypeRef__Group__03312);
            rule__XAttributeTypeRef__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group__1_in_rule__XAttributeTypeRef__Group__03315);
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


    // $ANTLR start rule__XAttributeTypeRef__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1607:1: rule__XAttributeTypeRef__Group__0__Impl : ( 'attribute' ) ;
    public final void rule__XAttributeTypeRef__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1611:1: ( ( 'attribute' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1612:1: ( 'attribute' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1612:1: ( 'attribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1613:1: 'attribute'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0()); 
            }
            match(input,40,FollowSets000.FOLLOW_40_in_rule__XAttributeTypeRef__Group__0__Impl3343); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0()); 
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
    // $ANTLR end rule__XAttributeTypeRef__Group__0__Impl


    // $ANTLR start rule__XAttributeTypeRef__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1626:1: rule__XAttributeTypeRef__Group__1 : rule__XAttributeTypeRef__Group__1__Impl rule__XAttributeTypeRef__Group__2 ;
    public final void rule__XAttributeTypeRef__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1630:1: ( rule__XAttributeTypeRef__Group__1__Impl rule__XAttributeTypeRef__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1631:2: rule__XAttributeTypeRef__Group__1__Impl rule__XAttributeTypeRef__Group__2
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group__1__Impl_in_rule__XAttributeTypeRef__Group__13374);
            rule__XAttributeTypeRef__Group__1__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group__2_in_rule__XAttributeTypeRef__Group__13377);
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


    // $ANTLR start rule__XAttributeTypeRef__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1638:1: rule__XAttributeTypeRef__Group__1__Impl : ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) ;
    public final void rule__XAttributeTypeRef__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1642:1: ( ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1643:1: ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1643:1: ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1644:1: ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1645:1: ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1645:2: rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__XAttributeTypeRef__Group__1__Impl3404);
            rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 
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
    // $ANTLR end rule__XAttributeTypeRef__Group__1__Impl


    // $ANTLR start rule__XAttributeTypeRef__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1655:1: rule__XAttributeTypeRef__Group__2 : rule__XAttributeTypeRef__Group__2__Impl ;
    public final void rule__XAttributeTypeRef__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1659:1: ( rule__XAttributeTypeRef__Group__2__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1660:2: rule__XAttributeTypeRef__Group__2__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group__2__Impl_in_rule__XAttributeTypeRef__Group__23434);
            rule__XAttributeTypeRef__Group__2__Impl();
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
    // $ANTLR end rule__XAttributeTypeRef__Group__2


    // $ANTLR start rule__XAttributeTypeRef__Group__2__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1666:1: rule__XAttributeTypeRef__Group__2__Impl : ( ( rule__XAttributeTypeRef__Group_2__0 )? ) ;
    public final void rule__XAttributeTypeRef__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1670:1: ( ( ( rule__XAttributeTypeRef__Group_2__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1671:1: ( ( rule__XAttributeTypeRef__Group_2__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1671:1: ( ( rule__XAttributeTypeRef__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1672:1: ( rule__XAttributeTypeRef__Group_2__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getGroup_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1673:1: ( rule__XAttributeTypeRef__Group_2__0 )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==41) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1673:2: rule__XAttributeTypeRef__Group_2__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group_2__0_in_rule__XAttributeTypeRef__Group__2__Impl3461);
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
    // $ANTLR end rule__XAttributeTypeRef__Group__2__Impl


    // $ANTLR start rule__XAttributeTypeRef__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1689:1: rule__XAttributeTypeRef__Group_2__0 : rule__XAttributeTypeRef__Group_2__0__Impl rule__XAttributeTypeRef__Group_2__1 ;
    public final void rule__XAttributeTypeRef__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1693:1: ( rule__XAttributeTypeRef__Group_2__0__Impl rule__XAttributeTypeRef__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1694:2: rule__XAttributeTypeRef__Group_2__0__Impl rule__XAttributeTypeRef__Group_2__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group_2__0__Impl_in_rule__XAttributeTypeRef__Group_2__03498);
            rule__XAttributeTypeRef__Group_2__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group_2__1_in_rule__XAttributeTypeRef__Group_2__03501);
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


    // $ANTLR start rule__XAttributeTypeRef__Group_2__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1701:1: rule__XAttributeTypeRef__Group_2__0__Impl : ( 'branchGuid' ) ;
    public final void rule__XAttributeTypeRef__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1705:1: ( ( 'branchGuid' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1706:1: ( 'branchGuid' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1706:1: ( 'branchGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1707:1: 'branchGuid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidKeyword_2_0()); 
            }
            match(input,41,FollowSets000.FOLLOW_41_in_rule__XAttributeTypeRef__Group_2__0__Impl3529); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidKeyword_2_0()); 
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
    // $ANTLR end rule__XAttributeTypeRef__Group_2__0__Impl


    // $ANTLR start rule__XAttributeTypeRef__Group_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1720:1: rule__XAttributeTypeRef__Group_2__1 : rule__XAttributeTypeRef__Group_2__1__Impl ;
    public final void rule__XAttributeTypeRef__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1724:1: ( rule__XAttributeTypeRef__Group_2__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1725:2: rule__XAttributeTypeRef__Group_2__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__Group_2__1__Impl_in_rule__XAttributeTypeRef__Group_2__13560);
            rule__XAttributeTypeRef__Group_2__1__Impl();
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
    // $ANTLR end rule__XAttributeTypeRef__Group_2__1


    // $ANTLR start rule__XAttributeTypeRef__Group_2__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1731:1: rule__XAttributeTypeRef__Group_2__1__Impl : ( ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 ) ) ;
    public final void rule__XAttributeTypeRef__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1735:1: ( ( ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1736:1: ( ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1736:1: ( ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1737:1: ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidAssignment_2_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1738:1: ( rule__XAttributeTypeRef__BranchGuidAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1738:2: rule__XAttributeTypeRef__BranchGuidAssignment_2_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeTypeRef__BranchGuidAssignment_2_1_in_rule__XAttributeTypeRef__Group_2__1__Impl3587);
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
    // $ANTLR end rule__XAttributeTypeRef__Group_2__1__Impl


    // $ANTLR start rule__XAttributeType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1752:1: rule__XAttributeType__Group__0 : rule__XAttributeType__Group__0__Impl rule__XAttributeType__Group__1 ;
    public final void rule__XAttributeType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1756:1: ( rule__XAttributeType__Group__0__Impl rule__XAttributeType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1757:2: rule__XAttributeType__Group__0__Impl rule__XAttributeType__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__0__Impl_in_rule__XAttributeType__Group__03621);
            rule__XAttributeType__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__1_in_rule__XAttributeType__Group__03624);
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


    // $ANTLR start rule__XAttributeType__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1764:1: rule__XAttributeType__Group__0__Impl : ( 'attributeType' ) ;
    public final void rule__XAttributeType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1768:1: ( ( 'attributeType' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1769:1: ( 'attributeType' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1769:1: ( 'attributeType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1770:1: 'attributeType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0()); 
            }
            match(input,42,FollowSets000.FOLLOW_42_in_rule__XAttributeType__Group__0__Impl3652); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0()); 
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
    // $ANTLR end rule__XAttributeType__Group__0__Impl


    // $ANTLR start rule__XAttributeType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1783:1: rule__XAttributeType__Group__1 : rule__XAttributeType__Group__1__Impl rule__XAttributeType__Group__2 ;
    public final void rule__XAttributeType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1787:1: ( rule__XAttributeType__Group__1__Impl rule__XAttributeType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1788:2: rule__XAttributeType__Group__1__Impl rule__XAttributeType__Group__2
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__1__Impl_in_rule__XAttributeType__Group__13683);
            rule__XAttributeType__Group__1__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__2_in_rule__XAttributeType__Group__13686);
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


    // $ANTLR start rule__XAttributeType__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1795:1: rule__XAttributeType__Group__1__Impl : ( ( rule__XAttributeType__NameAssignment_1 ) ) ;
    public final void rule__XAttributeType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1799:1: ( ( ( rule__XAttributeType__NameAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1800:1: ( ( rule__XAttributeType__NameAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1800:1: ( ( rule__XAttributeType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1801:1: ( rule__XAttributeType__NameAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getNameAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1802:1: ( rule__XAttributeType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1802:2: rule__XAttributeType__NameAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__NameAssignment_1_in_rule__XAttributeType__Group__1__Impl3713);
            rule__XAttributeType__NameAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getNameAssignment_1()); 
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
    // $ANTLR end rule__XAttributeType__Group__1__Impl


    // $ANTLR start rule__XAttributeType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1812:1: rule__XAttributeType__Group__2 : rule__XAttributeType__Group__2__Impl rule__XAttributeType__Group__3 ;
    public final void rule__XAttributeType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1816:1: ( rule__XAttributeType__Group__2__Impl rule__XAttributeType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1817:2: rule__XAttributeType__Group__2__Impl rule__XAttributeType__Group__3
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__2__Impl_in_rule__XAttributeType__Group__23743);
            rule__XAttributeType__Group__2__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__3_in_rule__XAttributeType__Group__23746);
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


    // $ANTLR start rule__XAttributeType__Group__2__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1824:1: rule__XAttributeType__Group__2__Impl : ( ( rule__XAttributeType__Group_2__0 ) ) ;
    public final void rule__XAttributeType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1828:1: ( ( ( rule__XAttributeType__Group_2__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1829:1: ( ( rule__XAttributeType__Group_2__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1829:1: ( ( rule__XAttributeType__Group_2__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1830:1: ( rule__XAttributeType__Group_2__0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1831:1: ( rule__XAttributeType__Group_2__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1831:2: rule__XAttributeType__Group_2__0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_2__0_in_rule__XAttributeType__Group__2__Impl3773);
            rule__XAttributeType__Group_2__0();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGroup_2()); 
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
    // $ANTLR end rule__XAttributeType__Group__2__Impl


    // $ANTLR start rule__XAttributeType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1841:1: rule__XAttributeType__Group__3 : rule__XAttributeType__Group__3__Impl rule__XAttributeType__Group__4 ;
    public final void rule__XAttributeType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1845:1: ( rule__XAttributeType__Group__3__Impl rule__XAttributeType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1846:2: rule__XAttributeType__Group__3__Impl rule__XAttributeType__Group__4
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__3__Impl_in_rule__XAttributeType__Group__33803);
            rule__XAttributeType__Group__3__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__4_in_rule__XAttributeType__Group__33806);
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


    // $ANTLR start rule__XAttributeType__Group__3__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1853:1: rule__XAttributeType__Group__3__Impl : ( ( rule__XAttributeType__Group_3__0 )? ) ;
    public final void rule__XAttributeType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1857:1: ( ( ( rule__XAttributeType__Group_3__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1858:1: ( ( rule__XAttributeType__Group_3__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1858:1: ( ( rule__XAttributeType__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1859:1: ( rule__XAttributeType__Group_3__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_3()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1860:1: ( rule__XAttributeType__Group_3__0 )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==46) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1860:2: rule__XAttributeType__Group_3__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_3__0_in_rule__XAttributeType__Group__3__Impl3833);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__3__Impl


    // $ANTLR start rule__XAttributeType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1870:1: rule__XAttributeType__Group__4 : rule__XAttributeType__Group__4__Impl rule__XAttributeType__Group__5 ;
    public final void rule__XAttributeType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1874:1: ( rule__XAttributeType__Group__4__Impl rule__XAttributeType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1875:2: rule__XAttributeType__Group__4__Impl rule__XAttributeType__Group__5
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__4__Impl_in_rule__XAttributeType__Group__43864);
            rule__XAttributeType__Group__4__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__5_in_rule__XAttributeType__Group__43867);
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


    // $ANTLR start rule__XAttributeType__Group__4__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1882:1: rule__XAttributeType__Group__4__Impl : ( '{' ) ;
    public final void rule__XAttributeType__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1886:1: ( ( '{' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1887:1: ( '{' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1887:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1888:1: '{'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4()); 
            }
            match(input,35,FollowSets000.FOLLOW_35_in_rule__XAttributeType__Group__4__Impl3895); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4()); 
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
    // $ANTLR end rule__XAttributeType__Group__4__Impl


    // $ANTLR start rule__XAttributeType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1901:1: rule__XAttributeType__Group__5 : rule__XAttributeType__Group__5__Impl rule__XAttributeType__Group__6 ;
    public final void rule__XAttributeType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1905:1: ( rule__XAttributeType__Group__5__Impl rule__XAttributeType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1906:2: rule__XAttributeType__Group__5__Impl rule__XAttributeType__Group__6
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__5__Impl_in_rule__XAttributeType__Group__53926);
            rule__XAttributeType__Group__5__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__6_in_rule__XAttributeType__Group__53929);
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


    // $ANTLR start rule__XAttributeType__Group__5__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1913:1: rule__XAttributeType__Group__5__Impl : ( 'guid' ) ;
    public final void rule__XAttributeType__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1917:1: ( ( 'guid' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1918:1: ( 'guid' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1918:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1919:1: 'guid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5()); 
            }
            match(input,36,FollowSets000.FOLLOW_36_in_rule__XAttributeType__Group__5__Impl3957); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5()); 
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
    // $ANTLR end rule__XAttributeType__Group__5__Impl


    // $ANTLR start rule__XAttributeType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1932:1: rule__XAttributeType__Group__6 : rule__XAttributeType__Group__6__Impl rule__XAttributeType__Group__7 ;
    public final void rule__XAttributeType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1936:1: ( rule__XAttributeType__Group__6__Impl rule__XAttributeType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1937:2: rule__XAttributeType__Group__6__Impl rule__XAttributeType__Group__7
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__6__Impl_in_rule__XAttributeType__Group__63988);
            rule__XAttributeType__Group__6__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__7_in_rule__XAttributeType__Group__63991);
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


    // $ANTLR start rule__XAttributeType__Group__6__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1944:1: rule__XAttributeType__Group__6__Impl : ( ( rule__XAttributeType__TypeGuidAssignment_6 ) ) ;
    public final void rule__XAttributeType__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1948:1: ( ( ( rule__XAttributeType__TypeGuidAssignment_6 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1949:1: ( ( rule__XAttributeType__TypeGuidAssignment_6 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1949:1: ( ( rule__XAttributeType__TypeGuidAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1950:1: ( rule__XAttributeType__TypeGuidAssignment_6 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getTypeGuidAssignment_6()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1951:1: ( rule__XAttributeType__TypeGuidAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1951:2: rule__XAttributeType__TypeGuidAssignment_6
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__TypeGuidAssignment_6_in_rule__XAttributeType__Group__6__Impl4018);
            rule__XAttributeType__TypeGuidAssignment_6();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getTypeGuidAssignment_6()); 
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
    // $ANTLR end rule__XAttributeType__Group__6__Impl


    // $ANTLR start rule__XAttributeType__Group__7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1961:1: rule__XAttributeType__Group__7 : rule__XAttributeType__Group__7__Impl rule__XAttributeType__Group__8 ;
    public final void rule__XAttributeType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1965:1: ( rule__XAttributeType__Group__7__Impl rule__XAttributeType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1966:2: rule__XAttributeType__Group__7__Impl rule__XAttributeType__Group__8
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__7__Impl_in_rule__XAttributeType__Group__74048);
            rule__XAttributeType__Group__7__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__8_in_rule__XAttributeType__Group__74051);
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


    // $ANTLR start rule__XAttributeType__Group__7__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1973:1: rule__XAttributeType__Group__7__Impl : ( 'dataProvider' ) ;
    public final void rule__XAttributeType__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1977:1: ( ( 'dataProvider' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1978:1: ( 'dataProvider' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1978:1: ( 'dataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1979:1: 'dataProvider'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_7()); 
            }
            match(input,43,FollowSets000.FOLLOW_43_in_rule__XAttributeType__Group__7__Impl4079); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_7()); 
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
    // $ANTLR end rule__XAttributeType__Group__7__Impl


    // $ANTLR start rule__XAttributeType__Group__8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1992:1: rule__XAttributeType__Group__8 : rule__XAttributeType__Group__8__Impl rule__XAttributeType__Group__9 ;
    public final void rule__XAttributeType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1996:1: ( rule__XAttributeType__Group__8__Impl rule__XAttributeType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:1997:2: rule__XAttributeType__Group__8__Impl rule__XAttributeType__Group__9
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__8__Impl_in_rule__XAttributeType__Group__84110);
            rule__XAttributeType__Group__8__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__9_in_rule__XAttributeType__Group__84113);
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


    // $ANTLR start rule__XAttributeType__Group__8__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2004:1: rule__XAttributeType__Group__8__Impl : ( ( rule__XAttributeType__DataProviderAssignment_8 ) ) ;
    public final void rule__XAttributeType__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2008:1: ( ( ( rule__XAttributeType__DataProviderAssignment_8 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2009:1: ( ( rule__XAttributeType__DataProviderAssignment_8 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2009:1: ( ( rule__XAttributeType__DataProviderAssignment_8 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2010:1: ( rule__XAttributeType__DataProviderAssignment_8 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDataProviderAssignment_8()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2011:1: ( rule__XAttributeType__DataProviderAssignment_8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2011:2: rule__XAttributeType__DataProviderAssignment_8
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__DataProviderAssignment_8_in_rule__XAttributeType__Group__8__Impl4140);
            rule__XAttributeType__DataProviderAssignment_8();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDataProviderAssignment_8()); 
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
    // $ANTLR end rule__XAttributeType__Group__8__Impl


    // $ANTLR start rule__XAttributeType__Group__9
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2021:1: rule__XAttributeType__Group__9 : rule__XAttributeType__Group__9__Impl rule__XAttributeType__Group__10 ;
    public final void rule__XAttributeType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2025:1: ( rule__XAttributeType__Group__9__Impl rule__XAttributeType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2026:2: rule__XAttributeType__Group__9__Impl rule__XAttributeType__Group__10
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__9__Impl_in_rule__XAttributeType__Group__94170);
            rule__XAttributeType__Group__9__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__10_in_rule__XAttributeType__Group__94173);
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


    // $ANTLR start rule__XAttributeType__Group__9__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2033:1: rule__XAttributeType__Group__9__Impl : ( 'min' ) ;
    public final void rule__XAttributeType__Group__9__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2037:1: ( ( 'min' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2038:1: ( 'min' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2038:1: ( 'min' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2039:1: 'min'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMinKeyword_9()); 
            }
            match(input,44,FollowSets000.FOLLOW_44_in_rule__XAttributeType__Group__9__Impl4201); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getMinKeyword_9()); 
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
    // $ANTLR end rule__XAttributeType__Group__9__Impl


    // $ANTLR start rule__XAttributeType__Group__10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2052:1: rule__XAttributeType__Group__10 : rule__XAttributeType__Group__10__Impl rule__XAttributeType__Group__11 ;
    public final void rule__XAttributeType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2056:1: ( rule__XAttributeType__Group__10__Impl rule__XAttributeType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2057:2: rule__XAttributeType__Group__10__Impl rule__XAttributeType__Group__11
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__10__Impl_in_rule__XAttributeType__Group__104232);
            rule__XAttributeType__Group__10__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__11_in_rule__XAttributeType__Group__104235);
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


    // $ANTLR start rule__XAttributeType__Group__10__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2064:1: rule__XAttributeType__Group__10__Impl : ( ( rule__XAttributeType__MinAssignment_10 ) ) ;
    public final void rule__XAttributeType__Group__10__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2068:1: ( ( ( rule__XAttributeType__MinAssignment_10 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2069:1: ( ( rule__XAttributeType__MinAssignment_10 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2069:1: ( ( rule__XAttributeType__MinAssignment_10 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2070:1: ( rule__XAttributeType__MinAssignment_10 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMinAssignment_10()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2071:1: ( rule__XAttributeType__MinAssignment_10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2071:2: rule__XAttributeType__MinAssignment_10
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__MinAssignment_10_in_rule__XAttributeType__Group__10__Impl4262);
            rule__XAttributeType__MinAssignment_10();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getMinAssignment_10()); 
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
    // $ANTLR end rule__XAttributeType__Group__10__Impl


    // $ANTLR start rule__XAttributeType__Group__11
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2081:1: rule__XAttributeType__Group__11 : rule__XAttributeType__Group__11__Impl rule__XAttributeType__Group__12 ;
    public final void rule__XAttributeType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2085:1: ( rule__XAttributeType__Group__11__Impl rule__XAttributeType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2086:2: rule__XAttributeType__Group__11__Impl rule__XAttributeType__Group__12
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__11__Impl_in_rule__XAttributeType__Group__114292);
            rule__XAttributeType__Group__11__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__12_in_rule__XAttributeType__Group__114295);
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


    // $ANTLR start rule__XAttributeType__Group__11__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2093:1: rule__XAttributeType__Group__11__Impl : ( 'max' ) ;
    public final void rule__XAttributeType__Group__11__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2097:1: ( ( 'max' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2098:1: ( 'max' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2098:1: ( 'max' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2099:1: 'max'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMaxKeyword_11()); 
            }
            match(input,45,FollowSets000.FOLLOW_45_in_rule__XAttributeType__Group__11__Impl4323); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getMaxKeyword_11()); 
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
    // $ANTLR end rule__XAttributeType__Group__11__Impl


    // $ANTLR start rule__XAttributeType__Group__12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2112:1: rule__XAttributeType__Group__12 : rule__XAttributeType__Group__12__Impl rule__XAttributeType__Group__13 ;
    public final void rule__XAttributeType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2116:1: ( rule__XAttributeType__Group__12__Impl rule__XAttributeType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2117:2: rule__XAttributeType__Group__12__Impl rule__XAttributeType__Group__13
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__12__Impl_in_rule__XAttributeType__Group__124354);
            rule__XAttributeType__Group__12__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__13_in_rule__XAttributeType__Group__124357);
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


    // $ANTLR start rule__XAttributeType__Group__12__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2124:1: rule__XAttributeType__Group__12__Impl : ( ( rule__XAttributeType__MaxAssignment_12 ) ) ;
    public final void rule__XAttributeType__Group__12__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2128:1: ( ( ( rule__XAttributeType__MaxAssignment_12 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2129:1: ( ( rule__XAttributeType__MaxAssignment_12 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2129:1: ( ( rule__XAttributeType__MaxAssignment_12 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2130:1: ( rule__XAttributeType__MaxAssignment_12 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMaxAssignment_12()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2131:1: ( rule__XAttributeType__MaxAssignment_12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2131:2: rule__XAttributeType__MaxAssignment_12
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__MaxAssignment_12_in_rule__XAttributeType__Group__12__Impl4384);
            rule__XAttributeType__MaxAssignment_12();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getMaxAssignment_12()); 
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
    // $ANTLR end rule__XAttributeType__Group__12__Impl


    // $ANTLR start rule__XAttributeType__Group__13
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2141:1: rule__XAttributeType__Group__13 : rule__XAttributeType__Group__13__Impl rule__XAttributeType__Group__14 ;
    public final void rule__XAttributeType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2145:1: ( rule__XAttributeType__Group__13__Impl rule__XAttributeType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2146:2: rule__XAttributeType__Group__13__Impl rule__XAttributeType__Group__14
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__13__Impl_in_rule__XAttributeType__Group__134414);
            rule__XAttributeType__Group__13__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__14_in_rule__XAttributeType__Group__134417);
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


    // $ANTLR start rule__XAttributeType__Group__13__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2153:1: rule__XAttributeType__Group__13__Impl : ( ( rule__XAttributeType__Group_13__0 )? ) ;
    public final void rule__XAttributeType__Group__13__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2157:1: ( ( ( rule__XAttributeType__Group_13__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2158:1: ( ( rule__XAttributeType__Group_13__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2158:1: ( ( rule__XAttributeType__Group_13__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2159:1: ( rule__XAttributeType__Group_13__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_13()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2160:1: ( rule__XAttributeType__Group_13__0 )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==47) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2160:2: rule__XAttributeType__Group_13__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_13__0_in_rule__XAttributeType__Group__13__Impl4444);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__13__Impl


    // $ANTLR start rule__XAttributeType__Group__14
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2170:1: rule__XAttributeType__Group__14 : rule__XAttributeType__Group__14__Impl rule__XAttributeType__Group__15 ;
    public final void rule__XAttributeType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2174:1: ( rule__XAttributeType__Group__14__Impl rule__XAttributeType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2175:2: rule__XAttributeType__Group__14__Impl rule__XAttributeType__Group__15
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__14__Impl_in_rule__XAttributeType__Group__144475);
            rule__XAttributeType__Group__14__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__15_in_rule__XAttributeType__Group__144478);
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


    // $ANTLR start rule__XAttributeType__Group__14__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2182:1: rule__XAttributeType__Group__14__Impl : ( ( rule__XAttributeType__Group_14__0 )? ) ;
    public final void rule__XAttributeType__Group__14__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2186:1: ( ( ( rule__XAttributeType__Group_14__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2187:1: ( ( rule__XAttributeType__Group_14__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2187:1: ( ( rule__XAttributeType__Group_14__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2188:1: ( rule__XAttributeType__Group_14__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_14()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2189:1: ( rule__XAttributeType__Group_14__0 )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==48) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2189:2: rule__XAttributeType__Group_14__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_14__0_in_rule__XAttributeType__Group__14__Impl4505);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__14__Impl


    // $ANTLR start rule__XAttributeType__Group__15
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2199:1: rule__XAttributeType__Group__15 : rule__XAttributeType__Group__15__Impl rule__XAttributeType__Group__16 ;
    public final void rule__XAttributeType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2203:1: ( rule__XAttributeType__Group__15__Impl rule__XAttributeType__Group__16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2204:2: rule__XAttributeType__Group__15__Impl rule__XAttributeType__Group__16
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__15__Impl_in_rule__XAttributeType__Group__154536);
            rule__XAttributeType__Group__15__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__16_in_rule__XAttributeType__Group__154539);
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


    // $ANTLR start rule__XAttributeType__Group__15__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2211:1: rule__XAttributeType__Group__15__Impl : ( ( rule__XAttributeType__Group_15__0 )? ) ;
    public final void rule__XAttributeType__Group__15__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2215:1: ( ( ( rule__XAttributeType__Group_15__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2216:1: ( ( rule__XAttributeType__Group_15__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2216:1: ( ( rule__XAttributeType__Group_15__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2217:1: ( rule__XAttributeType__Group_15__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_15()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2218:1: ( rule__XAttributeType__Group_15__0 )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==49) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2218:2: rule__XAttributeType__Group_15__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_15__0_in_rule__XAttributeType__Group__15__Impl4566);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__15__Impl


    // $ANTLR start rule__XAttributeType__Group__16
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2228:1: rule__XAttributeType__Group__16 : rule__XAttributeType__Group__16__Impl rule__XAttributeType__Group__17 ;
    public final void rule__XAttributeType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2232:1: ( rule__XAttributeType__Group__16__Impl rule__XAttributeType__Group__17 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2233:2: rule__XAttributeType__Group__16__Impl rule__XAttributeType__Group__17
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__16__Impl_in_rule__XAttributeType__Group__164597);
            rule__XAttributeType__Group__16__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__17_in_rule__XAttributeType__Group__164600);
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


    // $ANTLR start rule__XAttributeType__Group__16__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2240:1: rule__XAttributeType__Group__16__Impl : ( ( rule__XAttributeType__Group_16__0 )? ) ;
    public final void rule__XAttributeType__Group__16__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2244:1: ( ( ( rule__XAttributeType__Group_16__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2245:1: ( ( rule__XAttributeType__Group_16__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2245:1: ( ( rule__XAttributeType__Group_16__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2246:1: ( rule__XAttributeType__Group_16__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_16()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2247:1: ( rule__XAttributeType__Group_16__0 )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==50) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2247:2: rule__XAttributeType__Group_16__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_16__0_in_rule__XAttributeType__Group__16__Impl4627);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__16__Impl


    // $ANTLR start rule__XAttributeType__Group__17
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2257:1: rule__XAttributeType__Group__17 : rule__XAttributeType__Group__17__Impl rule__XAttributeType__Group__18 ;
    public final void rule__XAttributeType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2261:1: ( rule__XAttributeType__Group__17__Impl rule__XAttributeType__Group__18 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2262:2: rule__XAttributeType__Group__17__Impl rule__XAttributeType__Group__18
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__17__Impl_in_rule__XAttributeType__Group__174658);
            rule__XAttributeType__Group__17__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__18_in_rule__XAttributeType__Group__174661);
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


    // $ANTLR start rule__XAttributeType__Group__17__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2269:1: rule__XAttributeType__Group__17__Impl : ( ( rule__XAttributeType__Group_17__0 )? ) ;
    public final void rule__XAttributeType__Group__17__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2273:1: ( ( ( rule__XAttributeType__Group_17__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2274:1: ( ( rule__XAttributeType__Group_17__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2274:1: ( ( rule__XAttributeType__Group_17__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2275:1: ( rule__XAttributeType__Group_17__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getGroup_17()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2276:1: ( rule__XAttributeType__Group_17__0 )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==51) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2276:2: rule__XAttributeType__Group_17__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_17__0_in_rule__XAttributeType__Group__17__Impl4688);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttributeType__Group__17__Impl


    // $ANTLR start rule__XAttributeType__Group__18
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2286:1: rule__XAttributeType__Group__18 : rule__XAttributeType__Group__18__Impl ;
    public final void rule__XAttributeType__Group__18() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2290:1: ( rule__XAttributeType__Group__18__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2291:2: rule__XAttributeType__Group__18__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group__18__Impl_in_rule__XAttributeType__Group__184719);
            rule__XAttributeType__Group__18__Impl();
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
    // $ANTLR end rule__XAttributeType__Group__18


    // $ANTLR start rule__XAttributeType__Group__18__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2297:1: rule__XAttributeType__Group__18__Impl : ( '}' ) ;
    public final void rule__XAttributeType__Group__18__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2301:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2302:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2302:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2303:1: '}'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getRightCurlyBracketKeyword_18()); 
            }
            match(input,37,FollowSets000.FOLLOW_37_in_rule__XAttributeType__Group__18__Impl4747); if (failed) return ;
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
    // $ANTLR end rule__XAttributeType__Group__18__Impl


    // $ANTLR start rule__XAttributeType__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2354:1: rule__XAttributeType__Group_2__0 : rule__XAttributeType__Group_2__0__Impl rule__XAttributeType__Group_2__1 ;
    public final void rule__XAttributeType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2358:1: ( rule__XAttributeType__Group_2__0__Impl rule__XAttributeType__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2359:2: rule__XAttributeType__Group_2__0__Impl rule__XAttributeType__Group_2__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_2__0__Impl_in_rule__XAttributeType__Group_2__04816);
            rule__XAttributeType__Group_2__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_2__1_in_rule__XAttributeType__Group_2__04819);
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


    // $ANTLR start rule__XAttributeType__Group_2__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2366:1: rule__XAttributeType__Group_2__0__Impl : ( 'extends' ) ;
    public final void rule__XAttributeType__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2370:1: ( ( 'extends' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2371:1: ( 'extends' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2371:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2372:1: 'extends'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0()); 
            }
            match(input,38,FollowSets000.FOLLOW_38_in_rule__XAttributeType__Group_2__0__Impl4847); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0()); 
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
    // $ANTLR end rule__XAttributeType__Group_2__0__Impl


    // $ANTLR start rule__XAttributeType__Group_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2385:1: rule__XAttributeType__Group_2__1 : rule__XAttributeType__Group_2__1__Impl ;
    public final void rule__XAttributeType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2389:1: ( rule__XAttributeType__Group_2__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2390:2: rule__XAttributeType__Group_2__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_2__1__Impl_in_rule__XAttributeType__Group_2__14878);
            rule__XAttributeType__Group_2__1__Impl();
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
    // $ANTLR end rule__XAttributeType__Group_2__1


    // $ANTLR start rule__XAttributeType__Group_2__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2396:1: rule__XAttributeType__Group_2__1__Impl : ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) ) ;
    public final void rule__XAttributeType__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2400:1: ( ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2401:1: ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2401:1: ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2402:1: ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAssignment_2_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2403:1: ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2403:2: rule__XAttributeType__BaseAttributeTypeAssignment_2_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__BaseAttributeTypeAssignment_2_1_in_rule__XAttributeType__Group_2__1__Impl4905);
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
    // $ANTLR end rule__XAttributeType__Group_2__1__Impl


    // $ANTLR start rule__XAttributeType__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2417:1: rule__XAttributeType__Group_3__0 : rule__XAttributeType__Group_3__0__Impl rule__XAttributeType__Group_3__1 ;
    public final void rule__XAttributeType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2421:1: ( rule__XAttributeType__Group_3__0__Impl rule__XAttributeType__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2422:2: rule__XAttributeType__Group_3__0__Impl rule__XAttributeType__Group_3__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_3__0__Impl_in_rule__XAttributeType__Group_3__04939);
            rule__XAttributeType__Group_3__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_3__1_in_rule__XAttributeType__Group_3__04942);
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


    // $ANTLR start rule__XAttributeType__Group_3__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2429:1: rule__XAttributeType__Group_3__0__Impl : ( 'overrides' ) ;
    public final void rule__XAttributeType__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2433:1: ( ( 'overrides' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2434:1: ( 'overrides' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2434:1: ( 'overrides' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2435:1: 'overrides'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0()); 
            }
            match(input,46,FollowSets000.FOLLOW_46_in_rule__XAttributeType__Group_3__0__Impl4970); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0()); 
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
    // $ANTLR end rule__XAttributeType__Group_3__0__Impl


    // $ANTLR start rule__XAttributeType__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2448:1: rule__XAttributeType__Group_3__1 : rule__XAttributeType__Group_3__1__Impl ;
    public final void rule__XAttributeType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2452:1: ( rule__XAttributeType__Group_3__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2453:2: rule__XAttributeType__Group_3__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_3__1__Impl_in_rule__XAttributeType__Group_3__15001);
            rule__XAttributeType__Group_3__1__Impl();
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
    // $ANTLR end rule__XAttributeType__Group_3__1


    // $ANTLR start rule__XAttributeType__Group_3__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2459:1: rule__XAttributeType__Group_3__1__Impl : ( ( rule__XAttributeType__OverrideAssignment_3_1 ) ) ;
    public final void rule__XAttributeType__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2463:1: ( ( ( rule__XAttributeType__OverrideAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2464:1: ( ( rule__XAttributeType__OverrideAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2464:1: ( ( rule__XAttributeType__OverrideAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2465:1: ( rule__XAttributeType__OverrideAssignment_3_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getOverrideAssignment_3_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2466:1: ( rule__XAttributeType__OverrideAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2466:2: rule__XAttributeType__OverrideAssignment_3_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__OverrideAssignment_3_1_in_rule__XAttributeType__Group_3__1__Impl5028);
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
    // $ANTLR end rule__XAttributeType__Group_3__1__Impl


    // $ANTLR start rule__XAttributeType__Group_13__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2480:1: rule__XAttributeType__Group_13__0 : rule__XAttributeType__Group_13__0__Impl rule__XAttributeType__Group_13__1 ;
    public final void rule__XAttributeType__Group_13__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2484:1: ( rule__XAttributeType__Group_13__0__Impl rule__XAttributeType__Group_13__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2485:2: rule__XAttributeType__Group_13__0__Impl rule__XAttributeType__Group_13__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_13__0__Impl_in_rule__XAttributeType__Group_13__05062);
            rule__XAttributeType__Group_13__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_13__1_in_rule__XAttributeType__Group_13__05065);
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


    // $ANTLR start rule__XAttributeType__Group_13__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2492:1: rule__XAttributeType__Group_13__0__Impl : ( 'taggerId' ) ;
    public final void rule__XAttributeType__Group_13__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2496:1: ( ( 'taggerId' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2497:1: ( 'taggerId' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2497:1: ( 'taggerId' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2498:1: 'taggerId'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_13_0()); 
            }
            match(input,47,FollowSets000.FOLLOW_47_in_rule__XAttributeType__Group_13__0__Impl5093); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_13_0()); 
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
    // $ANTLR end rule__XAttributeType__Group_13__0__Impl


    // $ANTLR start rule__XAttributeType__Group_13__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2511:1: rule__XAttributeType__Group_13__1 : rule__XAttributeType__Group_13__1__Impl ;
    public final void rule__XAttributeType__Group_13__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2515:1: ( rule__XAttributeType__Group_13__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2516:2: rule__XAttributeType__Group_13__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_13__1__Impl_in_rule__XAttributeType__Group_13__15124);
            rule__XAttributeType__Group_13__1__Impl();
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
    // $ANTLR end rule__XAttributeType__Group_13__1


    // $ANTLR start rule__XAttributeType__Group_13__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2522:1: rule__XAttributeType__Group_13__1__Impl : ( ( rule__XAttributeType__TaggerIdAssignment_13_1 ) ) ;
    public final void rule__XAttributeType__Group_13__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2526:1: ( ( ( rule__XAttributeType__TaggerIdAssignment_13_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2527:1: ( ( rule__XAttributeType__TaggerIdAssignment_13_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2527:1: ( ( rule__XAttributeType__TaggerIdAssignment_13_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2528:1: ( rule__XAttributeType__TaggerIdAssignment_13_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getTaggerIdAssignment_13_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2529:1: ( rule__XAttributeType__TaggerIdAssignment_13_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2529:2: rule__XAttributeType__TaggerIdAssignment_13_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__TaggerIdAssignment_13_1_in_rule__XAttributeType__Group_13__1__Impl5151);
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
    // $ANTLR end rule__XAttributeType__Group_13__1__Impl


    // $ANTLR start rule__XAttributeType__Group_14__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2543:1: rule__XAttributeType__Group_14__0 : rule__XAttributeType__Group_14__0__Impl rule__XAttributeType__Group_14__1 ;
    public final void rule__XAttributeType__Group_14__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2547:1: ( rule__XAttributeType__Group_14__0__Impl rule__XAttributeType__Group_14__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2548:2: rule__XAttributeType__Group_14__0__Impl rule__XAttributeType__Group_14__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_14__0__Impl_in_rule__XAttributeType__Group_14__05185);
            rule__XAttributeType__Group_14__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_14__1_in_rule__XAttributeType__Group_14__05188);
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


    // $ANTLR start rule__XAttributeType__Group_14__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2555:1: rule__XAttributeType__Group_14__0__Impl : ( 'enumType' ) ;
    public final void rule__XAttributeType__Group_14__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2559:1: ( ( 'enumType' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2560:1: ( 'enumType' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2560:1: ( 'enumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2561:1: 'enumType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_14_0()); 
            }
            match(input,48,FollowSets000.FOLLOW_48_in_rule__XAttributeType__Group_14__0__Impl5216); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_14_0()); 
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
    // $ANTLR end rule__XAttributeType__Group_14__0__Impl


    // $ANTLR start rule__XAttributeType__Group_14__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2574:1: rule__XAttributeType__Group_14__1 : rule__XAttributeType__Group_14__1__Impl ;
    public final void rule__XAttributeType__Group_14__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2578:1: ( rule__XAttributeType__Group_14__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2579:2: rule__XAttributeType__Group_14__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_14__1__Impl_in_rule__XAttributeType__Group_14__15247);
            rule__XAttributeType__Group_14__1__Impl();
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
    // $ANTLR end rule__XAttributeType__Group_14__1


    // $ANTLR start rule__XAttributeType__Group_14__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2585:1: rule__XAttributeType__Group_14__1__Impl : ( ( rule__XAttributeType__EnumTypeAssignment_14_1 ) ) ;
    public final void rule__XAttributeType__Group_14__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2589:1: ( ( ( rule__XAttributeType__EnumTypeAssignment_14_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2590:1: ( ( rule__XAttributeType__EnumTypeAssignment_14_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2590:1: ( ( rule__XAttributeType__EnumTypeAssignment_14_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2591:1: ( rule__XAttributeType__EnumTypeAssignment_14_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getEnumTypeAssignment_14_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2592:1: ( rule__XAttributeType__EnumTypeAssignment_14_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2592:2: rule__XAttributeType__EnumTypeAssignment_14_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__EnumTypeAssignment_14_1_in_rule__XAttributeType__Group_14__1__Impl5274);
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
    // $ANTLR end rule__XAttributeType__Group_14__1__Impl


    // $ANTLR start rule__XAttributeType__Group_15__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2606:1: rule__XAttributeType__Group_15__0 : rule__XAttributeType__Group_15__0__Impl rule__XAttributeType__Group_15__1 ;
    public final void rule__XAttributeType__Group_15__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2610:1: ( rule__XAttributeType__Group_15__0__Impl rule__XAttributeType__Group_15__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2611:2: rule__XAttributeType__Group_15__0__Impl rule__XAttributeType__Group_15__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_15__0__Impl_in_rule__XAttributeType__Group_15__05308);
            rule__XAttributeType__Group_15__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_15__1_in_rule__XAttributeType__Group_15__05311);
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


    // $ANTLR start rule__XAttributeType__Group_15__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2618:1: rule__XAttributeType__Group_15__0__Impl : ( 'description' ) ;
    public final void rule__XAttributeType__Group_15__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2622:1: ( ( 'description' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2623:1: ( 'description' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2623:1: ( 'description' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2624:1: 'description'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_15_0()); 
            }
            match(input,49,FollowSets000.FOLLOW_49_in_rule__XAttributeType__Group_15__0__Impl5339); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_15_0()); 
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
    // $ANTLR end rule__XAttributeType__Group_15__0__Impl


    // $ANTLR start rule__XAttributeType__Group_15__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2637:1: rule__XAttributeType__Group_15__1 : rule__XAttributeType__Group_15__1__Impl ;
    public final void rule__XAttributeType__Group_15__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2641:1: ( rule__XAttributeType__Group_15__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2642:2: rule__XAttributeType__Group_15__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_15__1__Impl_in_rule__XAttributeType__Group_15__15370);
            rule__XAttributeType__Group_15__1__Impl();
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
    // $ANTLR end rule__XAttributeType__Group_15__1


    // $ANTLR start rule__XAttributeType__Group_15__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2648:1: rule__XAttributeType__Group_15__1__Impl : ( ( rule__XAttributeType__DescriptionAssignment_15_1 ) ) ;
    public final void rule__XAttributeType__Group_15__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2652:1: ( ( ( rule__XAttributeType__DescriptionAssignment_15_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2653:1: ( ( rule__XAttributeType__DescriptionAssignment_15_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2653:1: ( ( rule__XAttributeType__DescriptionAssignment_15_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2654:1: ( rule__XAttributeType__DescriptionAssignment_15_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDescriptionAssignment_15_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2655:1: ( rule__XAttributeType__DescriptionAssignment_15_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2655:2: rule__XAttributeType__DescriptionAssignment_15_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__DescriptionAssignment_15_1_in_rule__XAttributeType__Group_15__1__Impl5397);
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
    // $ANTLR end rule__XAttributeType__Group_15__1__Impl


    // $ANTLR start rule__XAttributeType__Group_16__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2669:1: rule__XAttributeType__Group_16__0 : rule__XAttributeType__Group_16__0__Impl rule__XAttributeType__Group_16__1 ;
    public final void rule__XAttributeType__Group_16__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2673:1: ( rule__XAttributeType__Group_16__0__Impl rule__XAttributeType__Group_16__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2674:2: rule__XAttributeType__Group_16__0__Impl rule__XAttributeType__Group_16__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_16__0__Impl_in_rule__XAttributeType__Group_16__05431);
            rule__XAttributeType__Group_16__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_16__1_in_rule__XAttributeType__Group_16__05434);
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


    // $ANTLR start rule__XAttributeType__Group_16__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2681:1: rule__XAttributeType__Group_16__0__Impl : ( 'defaultValue' ) ;
    public final void rule__XAttributeType__Group_16__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2685:1: ( ( 'defaultValue' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2686:1: ( 'defaultValue' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2686:1: ( 'defaultValue' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2687:1: 'defaultValue'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_16_0()); 
            }
            match(input,50,FollowSets000.FOLLOW_50_in_rule__XAttributeType__Group_16__0__Impl5462); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_16_0()); 
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
    // $ANTLR end rule__XAttributeType__Group_16__0__Impl


    // $ANTLR start rule__XAttributeType__Group_16__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2700:1: rule__XAttributeType__Group_16__1 : rule__XAttributeType__Group_16__1__Impl ;
    public final void rule__XAttributeType__Group_16__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2704:1: ( rule__XAttributeType__Group_16__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2705:2: rule__XAttributeType__Group_16__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_16__1__Impl_in_rule__XAttributeType__Group_16__15493);
            rule__XAttributeType__Group_16__1__Impl();
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
    // $ANTLR end rule__XAttributeType__Group_16__1


    // $ANTLR start rule__XAttributeType__Group_16__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2711:1: rule__XAttributeType__Group_16__1__Impl : ( ( rule__XAttributeType__DefaultValueAssignment_16_1 ) ) ;
    public final void rule__XAttributeType__Group_16__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2715:1: ( ( ( rule__XAttributeType__DefaultValueAssignment_16_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2716:1: ( ( rule__XAttributeType__DefaultValueAssignment_16_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2716:1: ( ( rule__XAttributeType__DefaultValueAssignment_16_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2717:1: ( rule__XAttributeType__DefaultValueAssignment_16_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDefaultValueAssignment_16_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2718:1: ( rule__XAttributeType__DefaultValueAssignment_16_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2718:2: rule__XAttributeType__DefaultValueAssignment_16_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__DefaultValueAssignment_16_1_in_rule__XAttributeType__Group_16__1__Impl5520);
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
    // $ANTLR end rule__XAttributeType__Group_16__1__Impl


    // $ANTLR start rule__XAttributeType__Group_17__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2732:1: rule__XAttributeType__Group_17__0 : rule__XAttributeType__Group_17__0__Impl rule__XAttributeType__Group_17__1 ;
    public final void rule__XAttributeType__Group_17__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2736:1: ( rule__XAttributeType__Group_17__0__Impl rule__XAttributeType__Group_17__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2737:2: rule__XAttributeType__Group_17__0__Impl rule__XAttributeType__Group_17__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_17__0__Impl_in_rule__XAttributeType__Group_17__05554);
            rule__XAttributeType__Group_17__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_17__1_in_rule__XAttributeType__Group_17__05557);
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


    // $ANTLR start rule__XAttributeType__Group_17__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2744:1: rule__XAttributeType__Group_17__0__Impl : ( 'fileExtension' ) ;
    public final void rule__XAttributeType__Group_17__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2748:1: ( ( 'fileExtension' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2749:1: ( 'fileExtension' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2749:1: ( 'fileExtension' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2750:1: 'fileExtension'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_17_0()); 
            }
            match(input,51,FollowSets000.FOLLOW_51_in_rule__XAttributeType__Group_17__0__Impl5585); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_17_0()); 
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
    // $ANTLR end rule__XAttributeType__Group_17__0__Impl


    // $ANTLR start rule__XAttributeType__Group_17__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2763:1: rule__XAttributeType__Group_17__1 : rule__XAttributeType__Group_17__1__Impl ;
    public final void rule__XAttributeType__Group_17__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2767:1: ( rule__XAttributeType__Group_17__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2768:2: rule__XAttributeType__Group_17__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__Group_17__1__Impl_in_rule__XAttributeType__Group_17__15616);
            rule__XAttributeType__Group_17__1__Impl();
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
    // $ANTLR end rule__XAttributeType__Group_17__1


    // $ANTLR start rule__XAttributeType__Group_17__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2774:1: rule__XAttributeType__Group_17__1__Impl : ( ( rule__XAttributeType__FileExtensionAssignment_17_1 ) ) ;
    public final void rule__XAttributeType__Group_17__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2778:1: ( ( ( rule__XAttributeType__FileExtensionAssignment_17_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2779:1: ( ( rule__XAttributeType__FileExtensionAssignment_17_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2779:1: ( ( rule__XAttributeType__FileExtensionAssignment_17_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2780:1: ( rule__XAttributeType__FileExtensionAssignment_17_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getFileExtensionAssignment_17_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2781:1: ( rule__XAttributeType__FileExtensionAssignment_17_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2781:2: rule__XAttributeType__FileExtensionAssignment_17_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__FileExtensionAssignment_17_1_in_rule__XAttributeType__Group_17__1__Impl5643);
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
    // $ANTLR end rule__XAttributeType__Group_17__1__Impl


    // $ANTLR start rule__XOseeEnumType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2795:1: rule__XOseeEnumType__Group__0 : rule__XOseeEnumType__Group__0__Impl rule__XOseeEnumType__Group__1 ;
    public final void rule__XOseeEnumType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2799:1: ( rule__XOseeEnumType__Group__0__Impl rule__XOseeEnumType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2800:2: rule__XOseeEnumType__Group__0__Impl rule__XOseeEnumType__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__0__Impl_in_rule__XOseeEnumType__Group__05677);
            rule__XOseeEnumType__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__1_in_rule__XOseeEnumType__Group__05680);
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


    // $ANTLR start rule__XOseeEnumType__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2807:1: rule__XOseeEnumType__Group__0__Impl : ( 'oseeEnumType' ) ;
    public final void rule__XOseeEnumType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2811:1: ( ( 'oseeEnumType' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2812:1: ( 'oseeEnumType' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2812:1: ( 'oseeEnumType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2813:1: 'oseeEnumType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 
            }
            match(input,52,FollowSets000.FOLLOW_52_in_rule__XOseeEnumType__Group__0__Impl5708); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 
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
    // $ANTLR end rule__XOseeEnumType__Group__0__Impl


    // $ANTLR start rule__XOseeEnumType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2826:1: rule__XOseeEnumType__Group__1 : rule__XOseeEnumType__Group__1__Impl rule__XOseeEnumType__Group__2 ;
    public final void rule__XOseeEnumType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2830:1: ( rule__XOseeEnumType__Group__1__Impl rule__XOseeEnumType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2831:2: rule__XOseeEnumType__Group__1__Impl rule__XOseeEnumType__Group__2
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__1__Impl_in_rule__XOseeEnumType__Group__15739);
            rule__XOseeEnumType__Group__1__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__2_in_rule__XOseeEnumType__Group__15742);
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


    // $ANTLR start rule__XOseeEnumType__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2838:1: rule__XOseeEnumType__Group__1__Impl : ( ( rule__XOseeEnumType__NameAssignment_1 ) ) ;
    public final void rule__XOseeEnumType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2842:1: ( ( ( rule__XOseeEnumType__NameAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2843:1: ( ( rule__XOseeEnumType__NameAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2843:1: ( ( rule__XOseeEnumType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2844:1: ( rule__XOseeEnumType__NameAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getNameAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2845:1: ( rule__XOseeEnumType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2845:2: rule__XOseeEnumType__NameAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__NameAssignment_1_in_rule__XOseeEnumType__Group__1__Impl5769);
            rule__XOseeEnumType__NameAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getNameAssignment_1()); 
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
    // $ANTLR end rule__XOseeEnumType__Group__1__Impl


    // $ANTLR start rule__XOseeEnumType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2855:1: rule__XOseeEnumType__Group__2 : rule__XOseeEnumType__Group__2__Impl rule__XOseeEnumType__Group__3 ;
    public final void rule__XOseeEnumType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2859:1: ( rule__XOseeEnumType__Group__2__Impl rule__XOseeEnumType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2860:2: rule__XOseeEnumType__Group__2__Impl rule__XOseeEnumType__Group__3
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__2__Impl_in_rule__XOseeEnumType__Group__25799);
            rule__XOseeEnumType__Group__2__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__3_in_rule__XOseeEnumType__Group__25802);
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


    // $ANTLR start rule__XOseeEnumType__Group__2__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2867:1: rule__XOseeEnumType__Group__2__Impl : ( '{' ) ;
    public final void rule__XOseeEnumType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2871:1: ( ( '{' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2872:1: ( '{' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2872:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2873:1: '{'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2()); 
            }
            match(input,35,FollowSets000.FOLLOW_35_in_rule__XOseeEnumType__Group__2__Impl5830); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2()); 
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
    // $ANTLR end rule__XOseeEnumType__Group__2__Impl


    // $ANTLR start rule__XOseeEnumType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2886:1: rule__XOseeEnumType__Group__3 : rule__XOseeEnumType__Group__3__Impl rule__XOseeEnumType__Group__4 ;
    public final void rule__XOseeEnumType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2890:1: ( rule__XOseeEnumType__Group__3__Impl rule__XOseeEnumType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2891:2: rule__XOseeEnumType__Group__3__Impl rule__XOseeEnumType__Group__4
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__3__Impl_in_rule__XOseeEnumType__Group__35861);
            rule__XOseeEnumType__Group__3__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__4_in_rule__XOseeEnumType__Group__35864);
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


    // $ANTLR start rule__XOseeEnumType__Group__3__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2898:1: rule__XOseeEnumType__Group__3__Impl : ( 'guid' ) ;
    public final void rule__XOseeEnumType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2902:1: ( ( 'guid' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2903:1: ( 'guid' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2903:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2904:1: 'guid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3()); 
            }
            match(input,36,FollowSets000.FOLLOW_36_in_rule__XOseeEnumType__Group__3__Impl5892); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3()); 
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
    // $ANTLR end rule__XOseeEnumType__Group__3__Impl


    // $ANTLR start rule__XOseeEnumType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2917:1: rule__XOseeEnumType__Group__4 : rule__XOseeEnumType__Group__4__Impl rule__XOseeEnumType__Group__5 ;
    public final void rule__XOseeEnumType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2921:1: ( rule__XOseeEnumType__Group__4__Impl rule__XOseeEnumType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2922:2: rule__XOseeEnumType__Group__4__Impl rule__XOseeEnumType__Group__5
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__4__Impl_in_rule__XOseeEnumType__Group__45923);
            rule__XOseeEnumType__Group__4__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__5_in_rule__XOseeEnumType__Group__45926);
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


    // $ANTLR start rule__XOseeEnumType__Group__4__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2929:1: rule__XOseeEnumType__Group__4__Impl : ( ( rule__XOseeEnumType__TypeGuidAssignment_4 ) ) ;
    public final void rule__XOseeEnumType__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2933:1: ( ( ( rule__XOseeEnumType__TypeGuidAssignment_4 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2934:1: ( ( rule__XOseeEnumType__TypeGuidAssignment_4 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2934:1: ( ( rule__XOseeEnumType__TypeGuidAssignment_4 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2935:1: ( rule__XOseeEnumType__TypeGuidAssignment_4 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidAssignment_4()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2936:1: ( rule__XOseeEnumType__TypeGuidAssignment_4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2936:2: rule__XOseeEnumType__TypeGuidAssignment_4
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__TypeGuidAssignment_4_in_rule__XOseeEnumType__Group__4__Impl5953);
            rule__XOseeEnumType__TypeGuidAssignment_4();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidAssignment_4()); 
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
    // $ANTLR end rule__XOseeEnumType__Group__4__Impl


    // $ANTLR start rule__XOseeEnumType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2946:1: rule__XOseeEnumType__Group__5 : rule__XOseeEnumType__Group__5__Impl rule__XOseeEnumType__Group__6 ;
    public final void rule__XOseeEnumType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2950:1: ( rule__XOseeEnumType__Group__5__Impl rule__XOseeEnumType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2951:2: rule__XOseeEnumType__Group__5__Impl rule__XOseeEnumType__Group__6
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__5__Impl_in_rule__XOseeEnumType__Group__55983);
            rule__XOseeEnumType__Group__5__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__6_in_rule__XOseeEnumType__Group__55986);
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


    // $ANTLR start rule__XOseeEnumType__Group__5__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2958:1: rule__XOseeEnumType__Group__5__Impl : ( ( rule__XOseeEnumType__EnumEntriesAssignment_5 )* ) ;
    public final void rule__XOseeEnumType__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2962:1: ( ( ( rule__XOseeEnumType__EnumEntriesAssignment_5 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2963:1: ( ( rule__XOseeEnumType__EnumEntriesAssignment_5 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2963:1: ( ( rule__XOseeEnumType__EnumEntriesAssignment_5 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2964:1: ( rule__XOseeEnumType__EnumEntriesAssignment_5 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesAssignment_5()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2965:1: ( rule__XOseeEnumType__EnumEntriesAssignment_5 )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==53) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2965:2: rule__XOseeEnumType__EnumEntriesAssignment_5
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__EnumEntriesAssignment_5_in_rule__XOseeEnumType__Group__5__Impl6013);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumType__Group__5__Impl


    // $ANTLR start rule__XOseeEnumType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2975:1: rule__XOseeEnumType__Group__6 : rule__XOseeEnumType__Group__6__Impl ;
    public final void rule__XOseeEnumType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2979:1: ( rule__XOseeEnumType__Group__6__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2980:2: rule__XOseeEnumType__Group__6__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumType__Group__6__Impl_in_rule__XOseeEnumType__Group__66044);
            rule__XOseeEnumType__Group__6__Impl();
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
    // $ANTLR end rule__XOseeEnumType__Group__6


    // $ANTLR start rule__XOseeEnumType__Group__6__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2986:1: rule__XOseeEnumType__Group__6__Impl : ( '}' ) ;
    public final void rule__XOseeEnumType__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2990:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2991:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2991:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:2992:1: '}'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getRightCurlyBracketKeyword_6()); 
            }
            match(input,37,FollowSets000.FOLLOW_37_in_rule__XOseeEnumType__Group__6__Impl6072); if (failed) return ;
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
    // $ANTLR end rule__XOseeEnumType__Group__6__Impl


    // $ANTLR start rule__XOseeEnumEntry__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3019:1: rule__XOseeEnumEntry__Group__0 : rule__XOseeEnumEntry__Group__0__Impl rule__XOseeEnumEntry__Group__1 ;
    public final void rule__XOseeEnumEntry__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3023:1: ( rule__XOseeEnumEntry__Group__0__Impl rule__XOseeEnumEntry__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3024:2: rule__XOseeEnumEntry__Group__0__Impl rule__XOseeEnumEntry__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__0__Impl_in_rule__XOseeEnumEntry__Group__06117);
            rule__XOseeEnumEntry__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__1_in_rule__XOseeEnumEntry__Group__06120);
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


    // $ANTLR start rule__XOseeEnumEntry__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3031:1: rule__XOseeEnumEntry__Group__0__Impl : ( 'entry' ) ;
    public final void rule__XOseeEnumEntry__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3035:1: ( ( 'entry' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3036:1: ( 'entry' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3036:1: ( 'entry' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3037:1: 'entry'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0()); 
            }
            match(input,53,FollowSets000.FOLLOW_53_in_rule__XOseeEnumEntry__Group__0__Impl6148); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0()); 
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
    // $ANTLR end rule__XOseeEnumEntry__Group__0__Impl


    // $ANTLR start rule__XOseeEnumEntry__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3050:1: rule__XOseeEnumEntry__Group__1 : rule__XOseeEnumEntry__Group__1__Impl rule__XOseeEnumEntry__Group__2 ;
    public final void rule__XOseeEnumEntry__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3054:1: ( rule__XOseeEnumEntry__Group__1__Impl rule__XOseeEnumEntry__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3055:2: rule__XOseeEnumEntry__Group__1__Impl rule__XOseeEnumEntry__Group__2
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__1__Impl_in_rule__XOseeEnumEntry__Group__16179);
            rule__XOseeEnumEntry__Group__1__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__2_in_rule__XOseeEnumEntry__Group__16182);
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


    // $ANTLR start rule__XOseeEnumEntry__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3062:1: rule__XOseeEnumEntry__Group__1__Impl : ( ( rule__XOseeEnumEntry__NameAssignment_1 ) ) ;
    public final void rule__XOseeEnumEntry__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3066:1: ( ( ( rule__XOseeEnumEntry__NameAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3067:1: ( ( rule__XOseeEnumEntry__NameAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3067:1: ( ( rule__XOseeEnumEntry__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3068:1: ( rule__XOseeEnumEntry__NameAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getNameAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3069:1: ( rule__XOseeEnumEntry__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3069:2: rule__XOseeEnumEntry__NameAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__NameAssignment_1_in_rule__XOseeEnumEntry__Group__1__Impl6209);
            rule__XOseeEnumEntry__NameAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getNameAssignment_1()); 
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
    // $ANTLR end rule__XOseeEnumEntry__Group__1__Impl


    // $ANTLR start rule__XOseeEnumEntry__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3079:1: rule__XOseeEnumEntry__Group__2 : rule__XOseeEnumEntry__Group__2__Impl rule__XOseeEnumEntry__Group__3 ;
    public final void rule__XOseeEnumEntry__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3083:1: ( rule__XOseeEnumEntry__Group__2__Impl rule__XOseeEnumEntry__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3084:2: rule__XOseeEnumEntry__Group__2__Impl rule__XOseeEnumEntry__Group__3
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__2__Impl_in_rule__XOseeEnumEntry__Group__26239);
            rule__XOseeEnumEntry__Group__2__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__3_in_rule__XOseeEnumEntry__Group__26242);
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


    // $ANTLR start rule__XOseeEnumEntry__Group__2__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3091:1: rule__XOseeEnumEntry__Group__2__Impl : ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? ) ;
    public final void rule__XOseeEnumEntry__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3095:1: ( ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3096:1: ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3096:1: ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3097:1: ( rule__XOseeEnumEntry__OrdinalAssignment_2 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getOrdinalAssignment_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3098:1: ( rule__XOseeEnumEntry__OrdinalAssignment_2 )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==RULE_WHOLE_NUM_STR) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3098:2: rule__XOseeEnumEntry__OrdinalAssignment_2
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__OrdinalAssignment_2_in_rule__XOseeEnumEntry__Group__2__Impl6269);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumEntry__Group__2__Impl


    // $ANTLR start rule__XOseeEnumEntry__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3108:1: rule__XOseeEnumEntry__Group__3 : rule__XOseeEnumEntry__Group__3__Impl ;
    public final void rule__XOseeEnumEntry__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3112:1: ( rule__XOseeEnumEntry__Group__3__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3113:2: rule__XOseeEnumEntry__Group__3__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group__3__Impl_in_rule__XOseeEnumEntry__Group__36300);
            rule__XOseeEnumEntry__Group__3__Impl();
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
    // $ANTLR end rule__XOseeEnumEntry__Group__3


    // $ANTLR start rule__XOseeEnumEntry__Group__3__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3119:1: rule__XOseeEnumEntry__Group__3__Impl : ( ( rule__XOseeEnumEntry__Group_3__0 )? ) ;
    public final void rule__XOseeEnumEntry__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3123:1: ( ( ( rule__XOseeEnumEntry__Group_3__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3124:1: ( ( rule__XOseeEnumEntry__Group_3__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3124:1: ( ( rule__XOseeEnumEntry__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3125:1: ( rule__XOseeEnumEntry__Group_3__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getGroup_3()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3126:1: ( rule__XOseeEnumEntry__Group_3__0 )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==54) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3126:2: rule__XOseeEnumEntry__Group_3__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group_3__0_in_rule__XOseeEnumEntry__Group__3__Impl6327);
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
    // $ANTLR end rule__XOseeEnumEntry__Group__3__Impl


    // $ANTLR start rule__XOseeEnumEntry__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3144:1: rule__XOseeEnumEntry__Group_3__0 : rule__XOseeEnumEntry__Group_3__0__Impl rule__XOseeEnumEntry__Group_3__1 ;
    public final void rule__XOseeEnumEntry__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3148:1: ( rule__XOseeEnumEntry__Group_3__0__Impl rule__XOseeEnumEntry__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3149:2: rule__XOseeEnumEntry__Group_3__0__Impl rule__XOseeEnumEntry__Group_3__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group_3__0__Impl_in_rule__XOseeEnumEntry__Group_3__06366);
            rule__XOseeEnumEntry__Group_3__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group_3__1_in_rule__XOseeEnumEntry__Group_3__06369);
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


    // $ANTLR start rule__XOseeEnumEntry__Group_3__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3156:1: rule__XOseeEnumEntry__Group_3__0__Impl : ( 'entryGuid' ) ;
    public final void rule__XOseeEnumEntry__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3160:1: ( ( 'entryGuid' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3161:1: ( 'entryGuid' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3161:1: ( 'entryGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3162:1: 'entryGuid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0()); 
            }
            match(input,54,FollowSets000.FOLLOW_54_in_rule__XOseeEnumEntry__Group_3__0__Impl6397); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0()); 
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
    // $ANTLR end rule__XOseeEnumEntry__Group_3__0__Impl


    // $ANTLR start rule__XOseeEnumEntry__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3175:1: rule__XOseeEnumEntry__Group_3__1 : rule__XOseeEnumEntry__Group_3__1__Impl ;
    public final void rule__XOseeEnumEntry__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3179:1: ( rule__XOseeEnumEntry__Group_3__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3180:2: rule__XOseeEnumEntry__Group_3__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__Group_3__1__Impl_in_rule__XOseeEnumEntry__Group_3__16428);
            rule__XOseeEnumEntry__Group_3__1__Impl();
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
    // $ANTLR end rule__XOseeEnumEntry__Group_3__1


    // $ANTLR start rule__XOseeEnumEntry__Group_3__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3186:1: rule__XOseeEnumEntry__Group_3__1__Impl : ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) ) ;
    public final void rule__XOseeEnumEntry__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3190:1: ( ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3191:1: ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3191:1: ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3192:1: ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidAssignment_3_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3193:1: ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3193:2: rule__XOseeEnumEntry__EntryGuidAssignment_3_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumEntry__EntryGuidAssignment_3_1_in_rule__XOseeEnumEntry__Group_3__1__Impl6455);
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
    // $ANTLR end rule__XOseeEnumEntry__Group_3__1__Impl


    // $ANTLR start rule__XOseeEnumOverride__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3207:1: rule__XOseeEnumOverride__Group__0 : rule__XOseeEnumOverride__Group__0__Impl rule__XOseeEnumOverride__Group__1 ;
    public final void rule__XOseeEnumOverride__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3211:1: ( rule__XOseeEnumOverride__Group__0__Impl rule__XOseeEnumOverride__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3212:2: rule__XOseeEnumOverride__Group__0__Impl rule__XOseeEnumOverride__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__0__Impl_in_rule__XOseeEnumOverride__Group__06489);
            rule__XOseeEnumOverride__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__1_in_rule__XOseeEnumOverride__Group__06492);
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


    // $ANTLR start rule__XOseeEnumOverride__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3219:1: rule__XOseeEnumOverride__Group__0__Impl : ( 'overrides enum' ) ;
    public final void rule__XOseeEnumOverride__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3223:1: ( ( 'overrides enum' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3224:1: ( 'overrides enum' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3224:1: ( 'overrides enum' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3225:1: 'overrides enum'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0()); 
            }
            match(input,55,FollowSets000.FOLLOW_55_in_rule__XOseeEnumOverride__Group__0__Impl6520); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0()); 
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
    // $ANTLR end rule__XOseeEnumOverride__Group__0__Impl


    // $ANTLR start rule__XOseeEnumOverride__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3238:1: rule__XOseeEnumOverride__Group__1 : rule__XOseeEnumOverride__Group__1__Impl rule__XOseeEnumOverride__Group__2 ;
    public final void rule__XOseeEnumOverride__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3242:1: ( rule__XOseeEnumOverride__Group__1__Impl rule__XOseeEnumOverride__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3243:2: rule__XOseeEnumOverride__Group__1__Impl rule__XOseeEnumOverride__Group__2
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__1__Impl_in_rule__XOseeEnumOverride__Group__16551);
            rule__XOseeEnumOverride__Group__1__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__2_in_rule__XOseeEnumOverride__Group__16554);
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


    // $ANTLR start rule__XOseeEnumOverride__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3250:1: rule__XOseeEnumOverride__Group__1__Impl : ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) ) ;
    public final void rule__XOseeEnumOverride__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3254:1: ( ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3255:1: ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3255:1: ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3256:1: ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3257:1: ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3257:2: rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1_in_rule__XOseeEnumOverride__Group__1__Impl6581);
            rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeAssignment_1()); 
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
    // $ANTLR end rule__XOseeEnumOverride__Group__1__Impl


    // $ANTLR start rule__XOseeEnumOverride__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3267:1: rule__XOseeEnumOverride__Group__2 : rule__XOseeEnumOverride__Group__2__Impl rule__XOseeEnumOverride__Group__3 ;
    public final void rule__XOseeEnumOverride__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3271:1: ( rule__XOseeEnumOverride__Group__2__Impl rule__XOseeEnumOverride__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3272:2: rule__XOseeEnumOverride__Group__2__Impl rule__XOseeEnumOverride__Group__3
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__2__Impl_in_rule__XOseeEnumOverride__Group__26611);
            rule__XOseeEnumOverride__Group__2__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__3_in_rule__XOseeEnumOverride__Group__26614);
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


    // $ANTLR start rule__XOseeEnumOverride__Group__2__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3279:1: rule__XOseeEnumOverride__Group__2__Impl : ( '{' ) ;
    public final void rule__XOseeEnumOverride__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3283:1: ( ( '{' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3284:1: ( '{' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3284:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3285:1: '{'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2()); 
            }
            match(input,35,FollowSets000.FOLLOW_35_in_rule__XOseeEnumOverride__Group__2__Impl6642); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2()); 
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
    // $ANTLR end rule__XOseeEnumOverride__Group__2__Impl


    // $ANTLR start rule__XOseeEnumOverride__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3298:1: rule__XOseeEnumOverride__Group__3 : rule__XOseeEnumOverride__Group__3__Impl rule__XOseeEnumOverride__Group__4 ;
    public final void rule__XOseeEnumOverride__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3302:1: ( rule__XOseeEnumOverride__Group__3__Impl rule__XOseeEnumOverride__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3303:2: rule__XOseeEnumOverride__Group__3__Impl rule__XOseeEnumOverride__Group__4
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__3__Impl_in_rule__XOseeEnumOverride__Group__36673);
            rule__XOseeEnumOverride__Group__3__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__4_in_rule__XOseeEnumOverride__Group__36676);
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


    // $ANTLR start rule__XOseeEnumOverride__Group__3__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3310:1: rule__XOseeEnumOverride__Group__3__Impl : ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? ) ;
    public final void rule__XOseeEnumOverride__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3314:1: ( ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3315:1: ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3315:1: ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3316:1: ( rule__XOseeEnumOverride__InheritAllAssignment_3 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllAssignment_3()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3317:1: ( rule__XOseeEnumOverride__InheritAllAssignment_3 )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==66) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3317:2: rule__XOseeEnumOverride__InheritAllAssignment_3
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__InheritAllAssignment_3_in_rule__XOseeEnumOverride__Group__3__Impl6703);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumOverride__Group__3__Impl


    // $ANTLR start rule__XOseeEnumOverride__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3327:1: rule__XOseeEnumOverride__Group__4 : rule__XOseeEnumOverride__Group__4__Impl rule__XOseeEnumOverride__Group__5 ;
    public final void rule__XOseeEnumOverride__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3331:1: ( rule__XOseeEnumOverride__Group__4__Impl rule__XOseeEnumOverride__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3332:2: rule__XOseeEnumOverride__Group__4__Impl rule__XOseeEnumOverride__Group__5
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__4__Impl_in_rule__XOseeEnumOverride__Group__46734);
            rule__XOseeEnumOverride__Group__4__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__5_in_rule__XOseeEnumOverride__Group__46737);
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


    // $ANTLR start rule__XOseeEnumOverride__Group__4__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3339:1: rule__XOseeEnumOverride__Group__4__Impl : ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* ) ;
    public final void rule__XOseeEnumOverride__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3343:1: ( ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3344:1: ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3344:1: ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3345:1: ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )*
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsAssignment_4()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3346:1: ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( ((LA28_0>=56 && LA28_0<=57)) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3346:2: rule__XOseeEnumOverride__OverrideOptionsAssignment_4
            	    {
            	    pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__OverrideOptionsAssignment_4_in_rule__XOseeEnumOverride__Group__4__Impl6764);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XOseeEnumOverride__Group__4__Impl


    // $ANTLR start rule__XOseeEnumOverride__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3356:1: rule__XOseeEnumOverride__Group__5 : rule__XOseeEnumOverride__Group__5__Impl ;
    public final void rule__XOseeEnumOverride__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3360:1: ( rule__XOseeEnumOverride__Group__5__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3361:2: rule__XOseeEnumOverride__Group__5__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XOseeEnumOverride__Group__5__Impl_in_rule__XOseeEnumOverride__Group__56795);
            rule__XOseeEnumOverride__Group__5__Impl();
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
    // $ANTLR end rule__XOseeEnumOverride__Group__5


    // $ANTLR start rule__XOseeEnumOverride__Group__5__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3367:1: rule__XOseeEnumOverride__Group__5__Impl : ( '}' ) ;
    public final void rule__XOseeEnumOverride__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3371:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3372:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3372:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3373:1: '}'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5()); 
            }
            match(input,37,FollowSets000.FOLLOW_37_in_rule__XOseeEnumOverride__Group__5__Impl6823); if (failed) return ;
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
    // $ANTLR end rule__XOseeEnumOverride__Group__5__Impl


    // $ANTLR start rule__AddEnum__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3398:1: rule__AddEnum__Group__0 : rule__AddEnum__Group__0__Impl rule__AddEnum__Group__1 ;
    public final void rule__AddEnum__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3402:1: ( rule__AddEnum__Group__0__Impl rule__AddEnum__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3403:2: rule__AddEnum__Group__0__Impl rule__AddEnum__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__0__Impl_in_rule__AddEnum__Group__06866);
            rule__AddEnum__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__1_in_rule__AddEnum__Group__06869);
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


    // $ANTLR start rule__AddEnum__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3410:1: rule__AddEnum__Group__0__Impl : ( 'add' ) ;
    public final void rule__AddEnum__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3414:1: ( ( 'add' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3415:1: ( 'add' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3415:1: ( 'add' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3416:1: 'add'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getAddKeyword_0()); 
            }
            match(input,56,FollowSets000.FOLLOW_56_in_rule__AddEnum__Group__0__Impl6897); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getAddKeyword_0()); 
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
    // $ANTLR end rule__AddEnum__Group__0__Impl


    // $ANTLR start rule__AddEnum__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3429:1: rule__AddEnum__Group__1 : rule__AddEnum__Group__1__Impl rule__AddEnum__Group__2 ;
    public final void rule__AddEnum__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3433:1: ( rule__AddEnum__Group__1__Impl rule__AddEnum__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3434:2: rule__AddEnum__Group__1__Impl rule__AddEnum__Group__2
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__1__Impl_in_rule__AddEnum__Group__16928);
            rule__AddEnum__Group__1__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__2_in_rule__AddEnum__Group__16931);
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


    // $ANTLR start rule__AddEnum__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3441:1: rule__AddEnum__Group__1__Impl : ( ( rule__AddEnum__EnumEntryAssignment_1 ) ) ;
    public final void rule__AddEnum__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3445:1: ( ( ( rule__AddEnum__EnumEntryAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3446:1: ( ( rule__AddEnum__EnumEntryAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3446:1: ( ( rule__AddEnum__EnumEntryAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3447:1: ( rule__AddEnum__EnumEntryAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getEnumEntryAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3448:1: ( rule__AddEnum__EnumEntryAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3448:2: rule__AddEnum__EnumEntryAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__EnumEntryAssignment_1_in_rule__AddEnum__Group__1__Impl6958);
            rule__AddEnum__EnumEntryAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getEnumEntryAssignment_1()); 
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
    // $ANTLR end rule__AddEnum__Group__1__Impl


    // $ANTLR start rule__AddEnum__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3458:1: rule__AddEnum__Group__2 : rule__AddEnum__Group__2__Impl rule__AddEnum__Group__3 ;
    public final void rule__AddEnum__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3462:1: ( rule__AddEnum__Group__2__Impl rule__AddEnum__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3463:2: rule__AddEnum__Group__2__Impl rule__AddEnum__Group__3
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__2__Impl_in_rule__AddEnum__Group__26988);
            rule__AddEnum__Group__2__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__3_in_rule__AddEnum__Group__26991);
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


    // $ANTLR start rule__AddEnum__Group__2__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3470:1: rule__AddEnum__Group__2__Impl : ( ( rule__AddEnum__OrdinalAssignment_2 )? ) ;
    public final void rule__AddEnum__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3474:1: ( ( ( rule__AddEnum__OrdinalAssignment_2 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3475:1: ( ( rule__AddEnum__OrdinalAssignment_2 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3475:1: ( ( rule__AddEnum__OrdinalAssignment_2 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3476:1: ( rule__AddEnum__OrdinalAssignment_2 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getOrdinalAssignment_2()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3477:1: ( rule__AddEnum__OrdinalAssignment_2 )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==RULE_WHOLE_NUM_STR) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3477:2: rule__AddEnum__OrdinalAssignment_2
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__AddEnum__OrdinalAssignment_2_in_rule__AddEnum__Group__2__Impl7018);
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


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AddEnum__Group__2__Impl


    // $ANTLR start rule__AddEnum__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3487:1: rule__AddEnum__Group__3 : rule__AddEnum__Group__3__Impl ;
    public final void rule__AddEnum__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3491:1: ( rule__AddEnum__Group__3__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3492:2: rule__AddEnum__Group__3__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group__3__Impl_in_rule__AddEnum__Group__37049);
            rule__AddEnum__Group__3__Impl();
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
    // $ANTLR end rule__AddEnum__Group__3


    // $ANTLR start rule__AddEnum__Group__3__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3498:1: rule__AddEnum__Group__3__Impl : ( ( rule__AddEnum__Group_3__0 )? ) ;
    public final void rule__AddEnum__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3502:1: ( ( ( rule__AddEnum__Group_3__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3503:1: ( ( rule__AddEnum__Group_3__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3503:1: ( ( rule__AddEnum__Group_3__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3504:1: ( rule__AddEnum__Group_3__0 )?
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getGroup_3()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3505:1: ( rule__AddEnum__Group_3__0 )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==54) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3505:2: rule__AddEnum__Group_3__0
                    {
                    pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group_3__0_in_rule__AddEnum__Group__3__Impl7076);
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
    // $ANTLR end rule__AddEnum__Group__3__Impl


    // $ANTLR start rule__AddEnum__Group_3__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3523:1: rule__AddEnum__Group_3__0 : rule__AddEnum__Group_3__0__Impl rule__AddEnum__Group_3__1 ;
    public final void rule__AddEnum__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3527:1: ( rule__AddEnum__Group_3__0__Impl rule__AddEnum__Group_3__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3528:2: rule__AddEnum__Group_3__0__Impl rule__AddEnum__Group_3__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group_3__0__Impl_in_rule__AddEnum__Group_3__07115);
            rule__AddEnum__Group_3__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group_3__1_in_rule__AddEnum__Group_3__07118);
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


    // $ANTLR start rule__AddEnum__Group_3__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3535:1: rule__AddEnum__Group_3__0__Impl : ( 'entryGuid' ) ;
    public final void rule__AddEnum__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3539:1: ( ( 'entryGuid' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3540:1: ( 'entryGuid' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3540:1: ( 'entryGuid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3541:1: 'entryGuid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0()); 
            }
            match(input,54,FollowSets000.FOLLOW_54_in_rule__AddEnum__Group_3__0__Impl7146); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0()); 
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
    // $ANTLR end rule__AddEnum__Group_3__0__Impl


    // $ANTLR start rule__AddEnum__Group_3__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3554:1: rule__AddEnum__Group_3__1 : rule__AddEnum__Group_3__1__Impl ;
    public final void rule__AddEnum__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3558:1: ( rule__AddEnum__Group_3__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3559:2: rule__AddEnum__Group_3__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__Group_3__1__Impl_in_rule__AddEnum__Group_3__17177);
            rule__AddEnum__Group_3__1__Impl();
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
    // $ANTLR end rule__AddEnum__Group_3__1


    // $ANTLR start rule__AddEnum__Group_3__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3565:1: rule__AddEnum__Group_3__1__Impl : ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) ) ;
    public final void rule__AddEnum__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3569:1: ( ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3570:1: ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3570:1: ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3571:1: ( rule__AddEnum__EntryGuidAssignment_3_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getEntryGuidAssignment_3_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3572:1: ( rule__AddEnum__EntryGuidAssignment_3_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3572:2: rule__AddEnum__EntryGuidAssignment_3_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__AddEnum__EntryGuidAssignment_3_1_in_rule__AddEnum__Group_3__1__Impl7204);
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
    // $ANTLR end rule__AddEnum__Group_3__1__Impl


    // $ANTLR start rule__RemoveEnum__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3586:1: rule__RemoveEnum__Group__0 : rule__RemoveEnum__Group__0__Impl rule__RemoveEnum__Group__1 ;
    public final void rule__RemoveEnum__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3590:1: ( rule__RemoveEnum__Group__0__Impl rule__RemoveEnum__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3591:2: rule__RemoveEnum__Group__0__Impl rule__RemoveEnum__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__RemoveEnum__Group__0__Impl_in_rule__RemoveEnum__Group__07238);
            rule__RemoveEnum__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__RemoveEnum__Group__1_in_rule__RemoveEnum__Group__07241);
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


    // $ANTLR start rule__RemoveEnum__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3598:1: rule__RemoveEnum__Group__0__Impl : ( 'remove' ) ;
    public final void rule__RemoveEnum__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3602:1: ( ( 'remove' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3603:1: ( 'remove' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3603:1: ( 'remove' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3604:1: 'remove'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0()); 
            }
            match(input,57,FollowSets000.FOLLOW_57_in_rule__RemoveEnum__Group__0__Impl7269); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0()); 
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
    // $ANTLR end rule__RemoveEnum__Group__0__Impl


    // $ANTLR start rule__RemoveEnum__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3617:1: rule__RemoveEnum__Group__1 : rule__RemoveEnum__Group__1__Impl ;
    public final void rule__RemoveEnum__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3621:1: ( rule__RemoveEnum__Group__1__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3622:2: rule__RemoveEnum__Group__1__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__RemoveEnum__Group__1__Impl_in_rule__RemoveEnum__Group__17300);
            rule__RemoveEnum__Group__1__Impl();
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
    // $ANTLR end rule__RemoveEnum__Group__1


    // $ANTLR start rule__RemoveEnum__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3628:1: rule__RemoveEnum__Group__1__Impl : ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) ) ;
    public final void rule__RemoveEnum__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3632:1: ( ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3633:1: ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3633:1: ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3634:1: ( rule__RemoveEnum__EnumEntryAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumAccess().getEnumEntryAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3635:1: ( rule__RemoveEnum__EnumEntryAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3635:2: rule__RemoveEnum__EnumEntryAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__RemoveEnum__EnumEntryAssignment_1_in_rule__RemoveEnum__Group__1__Impl7327);
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
    // $ANTLR end rule__RemoveEnum__Group__1__Impl


    // $ANTLR start rule__XRelationType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3649:1: rule__XRelationType__Group__0 : rule__XRelationType__Group__0__Impl rule__XRelationType__Group__1 ;
    public final void rule__XRelationType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3653:1: ( rule__XRelationType__Group__0__Impl rule__XRelationType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3654:2: rule__XRelationType__Group__0__Impl rule__XRelationType__Group__1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__0__Impl_in_rule__XRelationType__Group__07361);
            rule__XRelationType__Group__0__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__1_in_rule__XRelationType__Group__07364);
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


    // $ANTLR start rule__XRelationType__Group__0__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3661:1: rule__XRelationType__Group__0__Impl : ( 'relationType' ) ;
    public final void rule__XRelationType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3665:1: ( ( 'relationType' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3666:1: ( 'relationType' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3666:1: ( 'relationType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3667:1: 'relationType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0()); 
            }
            match(input,58,FollowSets000.FOLLOW_58_in_rule__XRelationType__Group__0__Impl7392); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0()); 
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
    // $ANTLR end rule__XRelationType__Group__0__Impl


    // $ANTLR start rule__XRelationType__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3680:1: rule__XRelationType__Group__1 : rule__XRelationType__Group__1__Impl rule__XRelationType__Group__2 ;
    public final void rule__XRelationType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3684:1: ( rule__XRelationType__Group__1__Impl rule__XRelationType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3685:2: rule__XRelationType__Group__1__Impl rule__XRelationType__Group__2
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__1__Impl_in_rule__XRelationType__Group__17423);
            rule__XRelationType__Group__1__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__2_in_rule__XRelationType__Group__17426);
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


    // $ANTLR start rule__XRelationType__Group__1__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3692:1: rule__XRelationType__Group__1__Impl : ( ( rule__XRelationType__NameAssignment_1 ) ) ;
    public final void rule__XRelationType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3696:1: ( ( ( rule__XRelationType__NameAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3697:1: ( ( rule__XRelationType__NameAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3697:1: ( ( rule__XRelationType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3698:1: ( rule__XRelationType__NameAssignment_1 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getNameAssignment_1()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3699:1: ( rule__XRelationType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3699:2: rule__XRelationType__NameAssignment_1
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__NameAssignment_1_in_rule__XRelationType__Group__1__Impl7453);
            rule__XRelationType__NameAssignment_1();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getNameAssignment_1()); 
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
    // $ANTLR end rule__XRelationType__Group__1__Impl


    // $ANTLR start rule__XRelationType__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3709:1: rule__XRelationType__Group__2 : rule__XRelationType__Group__2__Impl rule__XRelationType__Group__3 ;
    public final void rule__XRelationType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3713:1: ( rule__XRelationType__Group__2__Impl rule__XRelationType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3714:2: rule__XRelationType__Group__2__Impl rule__XRelationType__Group__3
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__2__Impl_in_rule__XRelationType__Group__27483);
            rule__XRelationType__Group__2__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__3_in_rule__XRelationType__Group__27486);
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


    // $ANTLR start rule__XRelationType__Group__2__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3721:1: rule__XRelationType__Group__2__Impl : ( '{' ) ;
    public final void rule__XRelationType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3725:1: ( ( '{' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3726:1: ( '{' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3726:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3727:1: '{'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 
            }
            match(input,35,FollowSets000.FOLLOW_35_in_rule__XRelationType__Group__2__Impl7514); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 
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
    // $ANTLR end rule__XRelationType__Group__2__Impl


    // $ANTLR start rule__XRelationType__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3740:1: rule__XRelationType__Group__3 : rule__XRelationType__Group__3__Impl rule__XRelationType__Group__4 ;
    public final void rule__XRelationType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3744:1: ( rule__XRelationType__Group__3__Impl rule__XRelationType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3745:2: rule__XRelationType__Group__3__Impl rule__XRelationType__Group__4
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__3__Impl_in_rule__XRelationType__Group__37545);
            rule__XRelationType__Group__3__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__4_in_rule__XRelationType__Group__37548);
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


    // $ANTLR start rule__XRelationType__Group__3__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3752:1: rule__XRelationType__Group__3__Impl : ( 'guid' ) ;
    public final void rule__XRelationType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3756:1: ( ( 'guid' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3757:1: ( 'guid' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3757:1: ( 'guid' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3758:1: 'guid'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getGuidKeyword_3()); 
            }
            match(input,36,FollowSets000.FOLLOW_36_in_rule__XRelationType__Group__3__Impl7576); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getGuidKeyword_3()); 
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
    // $ANTLR end rule__XRelationType__Group__3__Impl


    // $ANTLR start rule__XRelationType__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3771:1: rule__XRelationType__Group__4 : rule__XRelationType__Group__4__Impl rule__XRelationType__Group__5 ;
    public final void rule__XRelationType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3775:1: ( rule__XRelationType__Group__4__Impl rule__XRelationType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3776:2: rule__XRelationType__Group__4__Impl rule__XRelationType__Group__5
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__4__Impl_in_rule__XRelationType__Group__47607);
            rule__XRelationType__Group__4__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__5_in_rule__XRelationType__Group__47610);
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


    // $ANTLR start rule__XRelationType__Group__4__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3783:1: rule__XRelationType__Group__4__Impl : ( ( rule__XRelationType__TypeGuidAssignment_4 ) ) ;
    public final void rule__XRelationType__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3787:1: ( ( ( rule__XRelationType__TypeGuidAssignment_4 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3788:1: ( ( rule__XRelationType__TypeGuidAssignment_4 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3788:1: ( ( rule__XRelationType__TypeGuidAssignment_4 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3789:1: ( rule__XRelationType__TypeGuidAssignment_4 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getTypeGuidAssignment_4()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3790:1: ( rule__XRelationType__TypeGuidAssignment_4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3790:2: rule__XRelationType__TypeGuidAssignment_4
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__TypeGuidAssignment_4_in_rule__XRelationType__Group__4__Impl7637);
            rule__XRelationType__TypeGuidAssignment_4();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getTypeGuidAssignment_4()); 
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
    // $ANTLR end rule__XRelationType__Group__4__Impl


    // $ANTLR start rule__XRelationType__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3800:1: rule__XRelationType__Group__5 : rule__XRelationType__Group__5__Impl rule__XRelationType__Group__6 ;
    public final void rule__XRelationType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3804:1: ( rule__XRelationType__Group__5__Impl rule__XRelationType__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3805:2: rule__XRelationType__Group__5__Impl rule__XRelationType__Group__6
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__5__Impl_in_rule__XRelationType__Group__57667);
            rule__XRelationType__Group__5__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__6_in_rule__XRelationType__Group__57670);
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


    // $ANTLR start rule__XRelationType__Group__5__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3812:1: rule__XRelationType__Group__5__Impl : ( 'sideAName' ) ;
    public final void rule__XRelationType__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3816:1: ( ( 'sideAName' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3817:1: ( 'sideAName' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3817:1: ( 'sideAName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3818:1: 'sideAName'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideANameKeyword_5()); 
            }
            match(input,59,FollowSets000.FOLLOW_59_in_rule__XRelationType__Group__5__Impl7698); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideANameKeyword_5()); 
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
    // $ANTLR end rule__XRelationType__Group__5__Impl


    // $ANTLR start rule__XRelationType__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3831:1: rule__XRelationType__Group__6 : rule__XRelationType__Group__6__Impl rule__XRelationType__Group__7 ;
    public final void rule__XRelationType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3835:1: ( rule__XRelationType__Group__6__Impl rule__XRelationType__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3836:2: rule__XRelationType__Group__6__Impl rule__XRelationType__Group__7
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__6__Impl_in_rule__XRelationType__Group__67729);
            rule__XRelationType__Group__6__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__7_in_rule__XRelationType__Group__67732);
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


    // $ANTLR start rule__XRelationType__Group__6__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3843:1: rule__XRelationType__Group__6__Impl : ( ( rule__XRelationType__SideANameAssignment_6 ) ) ;
    public final void rule__XRelationType__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3847:1: ( ( ( rule__XRelationType__SideANameAssignment_6 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3848:1: ( ( rule__XRelationType__SideANameAssignment_6 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3848:1: ( ( rule__XRelationType__SideANameAssignment_6 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3849:1: ( rule__XRelationType__SideANameAssignment_6 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideANameAssignment_6()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3850:1: ( rule__XRelationType__SideANameAssignment_6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3850:2: rule__XRelationType__SideANameAssignment_6
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__SideANameAssignment_6_in_rule__XRelationType__Group__6__Impl7759);
            rule__XRelationType__SideANameAssignment_6();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideANameAssignment_6()); 
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
    // $ANTLR end rule__XRelationType__Group__6__Impl


    // $ANTLR start rule__XRelationType__Group__7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3860:1: rule__XRelationType__Group__7 : rule__XRelationType__Group__7__Impl rule__XRelationType__Group__8 ;
    public final void rule__XRelationType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3864:1: ( rule__XRelationType__Group__7__Impl rule__XRelationType__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3865:2: rule__XRelationType__Group__7__Impl rule__XRelationType__Group__8
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__7__Impl_in_rule__XRelationType__Group__77789);
            rule__XRelationType__Group__7__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__8_in_rule__XRelationType__Group__77792);
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


    // $ANTLR start rule__XRelationType__Group__7__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3872:1: rule__XRelationType__Group__7__Impl : ( 'sideAArtifactType' ) ;
    public final void rule__XRelationType__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3876:1: ( ( 'sideAArtifactType' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3877:1: ( 'sideAArtifactType' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3877:1: ( 'sideAArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3878:1: 'sideAArtifactType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_7()); 
            }
            match(input,60,FollowSets000.FOLLOW_60_in_rule__XRelationType__Group__7__Impl7820); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_7()); 
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
    // $ANTLR end rule__XRelationType__Group__7__Impl


    // $ANTLR start rule__XRelationType__Group__8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3891:1: rule__XRelationType__Group__8 : rule__XRelationType__Group__8__Impl rule__XRelationType__Group__9 ;
    public final void rule__XRelationType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3895:1: ( rule__XRelationType__Group__8__Impl rule__XRelationType__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3896:2: rule__XRelationType__Group__8__Impl rule__XRelationType__Group__9
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__8__Impl_in_rule__XRelationType__Group__87851);
            rule__XRelationType__Group__8__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__9_in_rule__XRelationType__Group__87854);
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


    // $ANTLR start rule__XRelationType__Group__8__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3903:1: rule__XRelationType__Group__8__Impl : ( ( rule__XRelationType__SideAArtifactTypeAssignment_8 ) ) ;
    public final void rule__XRelationType__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3907:1: ( ( ( rule__XRelationType__SideAArtifactTypeAssignment_8 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3908:1: ( ( rule__XRelationType__SideAArtifactTypeAssignment_8 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3908:1: ( ( rule__XRelationType__SideAArtifactTypeAssignment_8 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3909:1: ( rule__XRelationType__SideAArtifactTypeAssignment_8 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeAssignment_8()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3910:1: ( rule__XRelationType__SideAArtifactTypeAssignment_8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3910:2: rule__XRelationType__SideAArtifactTypeAssignment_8
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__SideAArtifactTypeAssignment_8_in_rule__XRelationType__Group__8__Impl7881);
            rule__XRelationType__SideAArtifactTypeAssignment_8();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeAssignment_8()); 
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
    // $ANTLR end rule__XRelationType__Group__8__Impl


    // $ANTLR start rule__XRelationType__Group__9
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3920:1: rule__XRelationType__Group__9 : rule__XRelationType__Group__9__Impl rule__XRelationType__Group__10 ;
    public final void rule__XRelationType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3924:1: ( rule__XRelationType__Group__9__Impl rule__XRelationType__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3925:2: rule__XRelationType__Group__9__Impl rule__XRelationType__Group__10
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__9__Impl_in_rule__XRelationType__Group__97911);
            rule__XRelationType__Group__9__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__10_in_rule__XRelationType__Group__97914);
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


    // $ANTLR start rule__XRelationType__Group__9__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3932:1: rule__XRelationType__Group__9__Impl : ( 'sideBName' ) ;
    public final void rule__XRelationType__Group__9__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3936:1: ( ( 'sideBName' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3937:1: ( 'sideBName' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3937:1: ( 'sideBName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3938:1: 'sideBName'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_9()); 
            }
            match(input,61,FollowSets000.FOLLOW_61_in_rule__XRelationType__Group__9__Impl7942); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_9()); 
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
    // $ANTLR end rule__XRelationType__Group__9__Impl


    // $ANTLR start rule__XRelationType__Group__10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3951:1: rule__XRelationType__Group__10 : rule__XRelationType__Group__10__Impl rule__XRelationType__Group__11 ;
    public final void rule__XRelationType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3955:1: ( rule__XRelationType__Group__10__Impl rule__XRelationType__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3956:2: rule__XRelationType__Group__10__Impl rule__XRelationType__Group__11
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__10__Impl_in_rule__XRelationType__Group__107973);
            rule__XRelationType__Group__10__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__11_in_rule__XRelationType__Group__107976);
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


    // $ANTLR start rule__XRelationType__Group__10__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3963:1: rule__XRelationType__Group__10__Impl : ( ( rule__XRelationType__SideBNameAssignment_10 ) ) ;
    public final void rule__XRelationType__Group__10__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3967:1: ( ( ( rule__XRelationType__SideBNameAssignment_10 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3968:1: ( ( rule__XRelationType__SideBNameAssignment_10 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3968:1: ( ( rule__XRelationType__SideBNameAssignment_10 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3969:1: ( rule__XRelationType__SideBNameAssignment_10 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBNameAssignment_10()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3970:1: ( rule__XRelationType__SideBNameAssignment_10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3970:2: rule__XRelationType__SideBNameAssignment_10
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__SideBNameAssignment_10_in_rule__XRelationType__Group__10__Impl8003);
            rule__XRelationType__SideBNameAssignment_10();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBNameAssignment_10()); 
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
    // $ANTLR end rule__XRelationType__Group__10__Impl


    // $ANTLR start rule__XRelationType__Group__11
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3980:1: rule__XRelationType__Group__11 : rule__XRelationType__Group__11__Impl rule__XRelationType__Group__12 ;
    public final void rule__XRelationType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3984:1: ( rule__XRelationType__Group__11__Impl rule__XRelationType__Group__12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3985:2: rule__XRelationType__Group__11__Impl rule__XRelationType__Group__12
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__11__Impl_in_rule__XRelationType__Group__118033);
            rule__XRelationType__Group__11__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__12_in_rule__XRelationType__Group__118036);
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


    // $ANTLR start rule__XRelationType__Group__11__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3992:1: rule__XRelationType__Group__11__Impl : ( 'sideBArtifactType' ) ;
    public final void rule__XRelationType__Group__11__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3996:1: ( ( 'sideBArtifactType' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3997:1: ( 'sideBArtifactType' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3997:1: ( 'sideBArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:3998:1: 'sideBArtifactType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_11()); 
            }
            match(input,62,FollowSets000.FOLLOW_62_in_rule__XRelationType__Group__11__Impl8064); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_11()); 
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
    // $ANTLR end rule__XRelationType__Group__11__Impl


    // $ANTLR start rule__XRelationType__Group__12
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4011:1: rule__XRelationType__Group__12 : rule__XRelationType__Group__12__Impl rule__XRelationType__Group__13 ;
    public final void rule__XRelationType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4015:1: ( rule__XRelationType__Group__12__Impl rule__XRelationType__Group__13 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4016:2: rule__XRelationType__Group__12__Impl rule__XRelationType__Group__13
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__12__Impl_in_rule__XRelationType__Group__128095);
            rule__XRelationType__Group__12__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__13_in_rule__XRelationType__Group__128098);
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


    // $ANTLR start rule__XRelationType__Group__12__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4023:1: rule__XRelationType__Group__12__Impl : ( ( rule__XRelationType__SideBArtifactTypeAssignment_12 ) ) ;
    public final void rule__XRelationType__Group__12__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4027:1: ( ( ( rule__XRelationType__SideBArtifactTypeAssignment_12 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4028:1: ( ( rule__XRelationType__SideBArtifactTypeAssignment_12 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4028:1: ( ( rule__XRelationType__SideBArtifactTypeAssignment_12 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4029:1: ( rule__XRelationType__SideBArtifactTypeAssignment_12 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeAssignment_12()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4030:1: ( rule__XRelationType__SideBArtifactTypeAssignment_12 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4030:2: rule__XRelationType__SideBArtifactTypeAssignment_12
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__SideBArtifactTypeAssignment_12_in_rule__XRelationType__Group__12__Impl8125);
            rule__XRelationType__SideBArtifactTypeAssignment_12();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeAssignment_12()); 
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
    // $ANTLR end rule__XRelationType__Group__12__Impl


    // $ANTLR start rule__XRelationType__Group__13
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4040:1: rule__XRelationType__Group__13 : rule__XRelationType__Group__13__Impl rule__XRelationType__Group__14 ;
    public final void rule__XRelationType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4044:1: ( rule__XRelationType__Group__13__Impl rule__XRelationType__Group__14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4045:2: rule__XRelationType__Group__13__Impl rule__XRelationType__Group__14
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__13__Impl_in_rule__XRelationType__Group__138155);
            rule__XRelationType__Group__13__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__14_in_rule__XRelationType__Group__138158);
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


    // $ANTLR start rule__XRelationType__Group__13__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4052:1: rule__XRelationType__Group__13__Impl : ( 'defaultOrderType' ) ;
    public final void rule__XRelationType__Group__13__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4056:1: ( ( 'defaultOrderType' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4057:1: ( 'defaultOrderType' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4057:1: ( 'defaultOrderType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4058:1: 'defaultOrderType'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_13()); 
            }
            match(input,63,FollowSets000.FOLLOW_63_in_rule__XRelationType__Group__13__Impl8186); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_13()); 
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
    // $ANTLR end rule__XRelationType__Group__13__Impl


    // $ANTLR start rule__XRelationType__Group__14
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4071:1: rule__XRelationType__Group__14 : rule__XRelationType__Group__14__Impl rule__XRelationType__Group__15 ;
    public final void rule__XRelationType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4075:1: ( rule__XRelationType__Group__14__Impl rule__XRelationType__Group__15 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4076:2: rule__XRelationType__Group__14__Impl rule__XRelationType__Group__15
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__14__Impl_in_rule__XRelationType__Group__148217);
            rule__XRelationType__Group__14__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__15_in_rule__XRelationType__Group__148220);
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


    // $ANTLR start rule__XRelationType__Group__14__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4083:1: rule__XRelationType__Group__14__Impl : ( ( rule__XRelationType__DefaultOrderTypeAssignment_14 ) ) ;
    public final void rule__XRelationType__Group__14__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4087:1: ( ( ( rule__XRelationType__DefaultOrderTypeAssignment_14 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4088:1: ( ( rule__XRelationType__DefaultOrderTypeAssignment_14 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4088:1: ( ( rule__XRelationType__DefaultOrderTypeAssignment_14 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4089:1: ( rule__XRelationType__DefaultOrderTypeAssignment_14 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeAssignment_14()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4090:1: ( rule__XRelationType__DefaultOrderTypeAssignment_14 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4090:2: rule__XRelationType__DefaultOrderTypeAssignment_14
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__DefaultOrderTypeAssignment_14_in_rule__XRelationType__Group__14__Impl8247);
            rule__XRelationType__DefaultOrderTypeAssignment_14();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeAssignment_14()); 
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
    // $ANTLR end rule__XRelationType__Group__14__Impl


    // $ANTLR start rule__XRelationType__Group__15
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4100:1: rule__XRelationType__Group__15 : rule__XRelationType__Group__15__Impl rule__XRelationType__Group__16 ;
    public final void rule__XRelationType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4104:1: ( rule__XRelationType__Group__15__Impl rule__XRelationType__Group__16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4105:2: rule__XRelationType__Group__15__Impl rule__XRelationType__Group__16
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__15__Impl_in_rule__XRelationType__Group__158277);
            rule__XRelationType__Group__15__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__16_in_rule__XRelationType__Group__158280);
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


    // $ANTLR start rule__XRelationType__Group__15__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4112:1: rule__XRelationType__Group__15__Impl : ( 'multiplicity' ) ;
    public final void rule__XRelationType__Group__15__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4116:1: ( ( 'multiplicity' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4117:1: ( 'multiplicity' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4117:1: ( 'multiplicity' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4118:1: 'multiplicity'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_15()); 
            }
            match(input,64,FollowSets000.FOLLOW_64_in_rule__XRelationType__Group__15__Impl8308); if (failed) return ;
            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_15()); 
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
    // $ANTLR end rule__XRelationType__Group__15__Impl


    // $ANTLR start rule__XRelationType__Group__16
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4131:1: rule__XRelationType__Group__16 : rule__XRelationType__Group__16__Impl rule__XRelationType__Group__17 ;
    public final void rule__XRelationType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4135:1: ( rule__XRelationType__Group__16__Impl rule__XRelationType__Group__17 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4136:2: rule__XRelationType__Group__16__Impl rule__XRelationType__Group__17
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__16__Impl_in_rule__XRelationType__Group__168339);
            rule__XRelationType__Group__16__Impl();
            _fsp--;
            if (failed) return ;
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__17_in_rule__XRelationType__Group__168342);
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


    // $ANTLR start rule__XRelationType__Group__16__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4143:1: rule__XRelationType__Group__16__Impl : ( ( rule__XRelationType__MultiplicityAssignment_16 ) ) ;
    public final void rule__XRelationType__Group__16__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4147:1: ( ( ( rule__XRelationType__MultiplicityAssignment_16 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4148:1: ( ( rule__XRelationType__MultiplicityAssignment_16 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4148:1: ( ( rule__XRelationType__MultiplicityAssignment_16 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4149:1: ( rule__XRelationType__MultiplicityAssignment_16 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getMultiplicityAssignment_16()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4150:1: ( rule__XRelationType__MultiplicityAssignment_16 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4150:2: rule__XRelationType__MultiplicityAssignment_16
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__MultiplicityAssignment_16_in_rule__XRelationType__Group__16__Impl8369);
            rule__XRelationType__MultiplicityAssignment_16();
            _fsp--;
            if (failed) return ;

            }

            if ( backtracking==0 ) {
               after(grammarAccess.getXRelationTypeAccess().getMultiplicityAssignment_16()); 
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
    // $ANTLR end rule__XRelationType__Group__16__Impl


    // $ANTLR start rule__XRelationType__Group__17
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4160:1: rule__XRelationType__Group__17 : rule__XRelationType__Group__17__Impl ;
    public final void rule__XRelationType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4164:1: ( rule__XRelationType__Group__17__Impl )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4165:2: rule__XRelationType__Group__17__Impl
            {
            pushFollow(FollowSets000.FOLLOW_rule__XRelationType__Group__17__Impl_in_rule__XRelationType__Group__178399);
            rule__XRelationType__Group__17__Impl();
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
    // $ANTLR end rule__XRelationType__Group__17


    // $ANTLR start rule__XRelationType__Group__17__Impl
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4171:1: rule__XRelationType__Group__17__Impl : ( '}' ) ;
    public final void rule__XRelationType__Group__17__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4175:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4176:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4176:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4177:1: '}'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getRightCurlyBracketKeyword_17()); 
            }
            match(input,37,FollowSets000.FOLLOW_37_in_rule__XRelationType__Group__17__Impl8427); if (failed) return ;
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
    // $ANTLR end rule__XRelationType__Group__17__Impl


    // $ANTLR start rule__OseeTypeModel__ImportsAssignment_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4227:1: rule__OseeTypeModel__ImportsAssignment_0 : ( ruleImport ) ;
    public final void rule__OseeTypeModel__ImportsAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4231:1: ( ( ruleImport ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4232:1: ( ruleImport )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4232:1: ( ruleImport )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4233:1: ruleImport
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getImportsImportParserRuleCall_0_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_08499);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4242:1: rule__OseeTypeModel__ArtifactTypesAssignment_1_0 : ( ruleXArtifactType ) ;
    public final void rule__OseeTypeModel__ArtifactTypesAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4246:1: ( ( ruleXArtifactType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4247:1: ( ruleXArtifactType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4247:1: ( ruleXArtifactType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4248:1: ruleXArtifactType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getArtifactTypesXArtifactTypeParserRuleCall_1_0_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXArtifactType_in_rule__OseeTypeModel__ArtifactTypesAssignment_1_08530);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4257:1: rule__OseeTypeModel__RelationTypesAssignment_1_1 : ( ruleXRelationType ) ;
    public final void rule__OseeTypeModel__RelationTypesAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4261:1: ( ( ruleXRelationType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4262:1: ( ruleXRelationType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4262:1: ( ruleXRelationType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4263:1: ruleXRelationType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getRelationTypesXRelationTypeParserRuleCall_1_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXRelationType_in_rule__OseeTypeModel__RelationTypesAssignment_1_18561);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4272:1: rule__OseeTypeModel__AttributeTypesAssignment_1_2 : ( ruleXAttributeType ) ;
    public final void rule__OseeTypeModel__AttributeTypesAssignment_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4276:1: ( ( ruleXAttributeType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4277:1: ( ruleXAttributeType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4277:1: ( ruleXAttributeType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4278:1: ruleXAttributeType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getAttributeTypesXAttributeTypeParserRuleCall_1_2_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXAttributeType_in_rule__OseeTypeModel__AttributeTypesAssignment_1_28592);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4287:1: rule__OseeTypeModel__EnumTypesAssignment_1_3 : ( ruleXOseeEnumType ) ;
    public final void rule__OseeTypeModel__EnumTypesAssignment_1_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4291:1: ( ( ruleXOseeEnumType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4292:1: ( ruleXOseeEnumType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4292:1: ( ruleXOseeEnumType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4293:1: ruleXOseeEnumType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getEnumTypesXOseeEnumTypeParserRuleCall_1_3_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXOseeEnumType_in_rule__OseeTypeModel__EnumTypesAssignment_1_38623);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4302:1: rule__OseeTypeModel__EnumOverridesAssignment_1_4 : ( ruleXOseeEnumOverride ) ;
    public final void rule__OseeTypeModel__EnumOverridesAssignment_1_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4306:1: ( ( ruleXOseeEnumOverride ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4307:1: ( ruleXOseeEnumOverride )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4307:1: ( ruleXOseeEnumOverride )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4308:1: ruleXOseeEnumOverride
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getOseeTypeModelAccess().getEnumOverridesXOseeEnumOverrideParserRuleCall_1_4_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXOseeEnumOverride_in_rule__OseeTypeModel__EnumOverridesAssignment_1_48654);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4317:1: rule__Import__ImportURIAssignment_1 : ( RULE_STRING ) ;
    public final void rule__Import__ImportURIAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4321:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4322:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4322:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4323:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_18685); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4332:1: rule__XArtifactType__AbstractAssignment_0 : ( ( 'abstract' ) ) ;
    public final void rule__XArtifactType__AbstractAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4336:1: ( ( ( 'abstract' ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4337:1: ( ( 'abstract' ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4337:1: ( ( 'abstract' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4338:1: ( 'abstract' )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4339:1: ( 'abstract' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4340:1: 'abstract'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            }
            match(input,65,FollowSets000.FOLLOW_65_in_rule__XArtifactType__AbstractAssignment_08721); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4355:1: rule__XArtifactType__NameAssignment_2 : ( ruleNAME_REFERENCE ) ;
    public final void rule__XArtifactType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4359:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4360:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4360:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4361:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getNameNAME_REFERENCEParserRuleCall_2_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__NameAssignment_28760);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4370:1: rule__XArtifactType__SuperArtifactTypesAssignment_3_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XArtifactType__SuperArtifactTypesAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4374:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4375:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4375:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4376:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4377:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4378:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__SuperArtifactTypesAssignment_3_18795);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4389:1: rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4393:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4394:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4394:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4395:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_2_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4396:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4397:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeNAME_REFERENCEParserRuleCall_3_2_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__SuperArtifactTypesAssignment_3_2_18834);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4408:1: rule__XArtifactType__TypeGuidAssignment_6 : ( RULE_STRING ) ;
    public final void rule__XArtifactType__TypeGuidAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4412:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4413:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4413:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4414:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XArtifactType__TypeGuidAssignment_68869); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4423:1: rule__XArtifactType__ValidAttributeTypesAssignment_7 : ( ruleXAttributeTypeRef ) ;
    public final void rule__XArtifactType__ValidAttributeTypesAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4427:1: ( ( ruleXAttributeTypeRef ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4428:1: ( ruleXAttributeTypeRef )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4428:1: ( ruleXAttributeTypeRef )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4429:1: ruleXAttributeTypeRef
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_7_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXAttributeTypeRef_in_rule__XArtifactType__ValidAttributeTypesAssignment_78900);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4438:1: rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4442:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4443:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4443:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4444:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4445:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4446:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeNAME_REFERENCEParserRuleCall_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeTypeRef__ValidAttributeTypeAssignment_18935);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4457:1: rule__XAttributeTypeRef__BranchGuidAssignment_2_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeTypeRef__BranchGuidAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4461:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4462:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4462:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4463:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeRefAccess().getBranchGuidSTRINGTerminalRuleCall_2_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XAttributeTypeRef__BranchGuidAssignment_2_18970); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4472:1: rule__XAttributeType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__XAttributeType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4476:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4477:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4477:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4478:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__NameAssignment_19001);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4487:1: rule__XAttributeType__BaseAttributeTypeAssignment_2_1 : ( ruleAttributeBaseType ) ;
    public final void rule__XAttributeType__BaseAttributeTypeAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4491:1: ( ( ruleAttributeBaseType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4492:1: ( ruleAttributeBaseType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4492:1: ( ruleAttributeBaseType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4493:1: ruleAttributeBaseType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleAttributeBaseType_in_rule__XAttributeType__BaseAttributeTypeAssignment_2_19032);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4502:1: rule__XAttributeType__OverrideAssignment_3_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XAttributeType__OverrideAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4506:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4507:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4507:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4508:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4509:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4510:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeNAME_REFERENCEParserRuleCall_3_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__OverrideAssignment_3_19067);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4521:1: rule__XAttributeType__TypeGuidAssignment_6 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__TypeGuidAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4525:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4526:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4526:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4527:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_6_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XAttributeType__TypeGuidAssignment_69102); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4536:1: rule__XAttributeType__DataProviderAssignment_8 : ( ( rule__XAttributeType__DataProviderAlternatives_8_0 ) ) ;
    public final void rule__XAttributeType__DataProviderAssignment_8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4540:1: ( ( ( rule__XAttributeType__DataProviderAlternatives_8_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4541:1: ( ( rule__XAttributeType__DataProviderAlternatives_8_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4541:1: ( ( rule__XAttributeType__DataProviderAlternatives_8_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4542:1: ( rule__XAttributeType__DataProviderAlternatives_8_0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDataProviderAlternatives_8_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4543:1: ( rule__XAttributeType__DataProviderAlternatives_8_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4543:2: rule__XAttributeType__DataProviderAlternatives_8_0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__DataProviderAlternatives_8_0_in_rule__XAttributeType__DataProviderAssignment_89133);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4552:1: rule__XAttributeType__MinAssignment_10 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XAttributeType__MinAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4556:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4557:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4557:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4558:1: RULE_WHOLE_NUM_STR
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_10_0()); 
            }
            match(input,RULE_WHOLE_NUM_STR,FollowSets000.FOLLOW_RULE_WHOLE_NUM_STR_in_rule__XAttributeType__MinAssignment_109166); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4567:1: rule__XAttributeType__MaxAssignment_12 : ( ( rule__XAttributeType__MaxAlternatives_12_0 ) ) ;
    public final void rule__XAttributeType__MaxAssignment_12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4571:1: ( ( ( rule__XAttributeType__MaxAlternatives_12_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4572:1: ( ( rule__XAttributeType__MaxAlternatives_12_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4572:1: ( ( rule__XAttributeType__MaxAlternatives_12_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4573:1: ( rule__XAttributeType__MaxAlternatives_12_0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getMaxAlternatives_12_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4574:1: ( rule__XAttributeType__MaxAlternatives_12_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4574:2: rule__XAttributeType__MaxAlternatives_12_0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__MaxAlternatives_12_0_in_rule__XAttributeType__MaxAssignment_129197);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4583:1: rule__XAttributeType__TaggerIdAssignment_13_1 : ( ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 ) ) ;
    public final void rule__XAttributeType__TaggerIdAssignment_13_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4587:1: ( ( ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4588:1: ( ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4588:1: ( ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4589:1: ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getTaggerIdAlternatives_13_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4590:1: ( rule__XAttributeType__TaggerIdAlternatives_13_1_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4590:2: rule__XAttributeType__TaggerIdAlternatives_13_1_0
            {
            pushFollow(FollowSets000.FOLLOW_rule__XAttributeType__TaggerIdAlternatives_13_1_0_in_rule__XAttributeType__TaggerIdAssignment_13_19230);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4599:1: rule__XAttributeType__EnumTypeAssignment_14_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XAttributeType__EnumTypeAssignment_14_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4603:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4604:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4604:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4605:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_14_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4606:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4607:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeNAME_REFERENCEParserRuleCall_14_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__EnumTypeAssignment_14_19267);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4618:1: rule__XAttributeType__DescriptionAssignment_15_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__DescriptionAssignment_15_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4622:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4623:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4623:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4624:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_15_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XAttributeType__DescriptionAssignment_15_19302); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4633:1: rule__XAttributeType__DefaultValueAssignment_16_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__DefaultValueAssignment_16_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4637:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4638:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4638:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4639:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_16_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XAttributeType__DefaultValueAssignment_16_19333); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4648:1: rule__XAttributeType__FileExtensionAssignment_17_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__FileExtensionAssignment_17_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4652:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4653:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4653:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4654:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_17_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XAttributeType__FileExtensionAssignment_17_19364); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4663:1: rule__XOseeEnumType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__XOseeEnumType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4667:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4668:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4668:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4669:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumType__NameAssignment_19395);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4678:1: rule__XOseeEnumType__TypeGuidAssignment_4 : ( RULE_STRING ) ;
    public final void rule__XOseeEnumType__TypeGuidAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4682:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4683:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4683:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4684:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XOseeEnumType__TypeGuidAssignment_49426); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4693:1: rule__XOseeEnumType__EnumEntriesAssignment_5 : ( ruleXOseeEnumEntry ) ;
    public final void rule__XOseeEnumType__EnumEntriesAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4697:1: ( ( ruleXOseeEnumEntry ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4698:1: ( ruleXOseeEnumEntry )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4698:1: ( ruleXOseeEnumEntry )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4699:1: ruleXOseeEnumEntry
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_5_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleXOseeEnumEntry_in_rule__XOseeEnumType__EnumEntriesAssignment_59457);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4708:1: rule__XOseeEnumEntry__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__XOseeEnumEntry__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4712:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4713:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4713:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4714:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumEntry__NameAssignment_19488);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4723:1: rule__XOseeEnumEntry__OrdinalAssignment_2 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XOseeEnumEntry__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4727:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4728:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4728:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4729:1: RULE_WHOLE_NUM_STR
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
            }
            match(input,RULE_WHOLE_NUM_STR,FollowSets000.FOLLOW_RULE_WHOLE_NUM_STR_in_rule__XOseeEnumEntry__OrdinalAssignment_29519); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4738:1: rule__XOseeEnumEntry__EntryGuidAssignment_3_1 : ( RULE_STRING ) ;
    public final void rule__XOseeEnumEntry__EntryGuidAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4742:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4743:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4743:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4744:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XOseeEnumEntry__EntryGuidAssignment_3_19550); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4753:1: rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4757:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4758:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4758:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4759:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4760:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4761:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeNAME_REFERENCEParserRuleCall_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumOverride__OverridenEnumTypeAssignment_19585);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4772:1: rule__XOseeEnumOverride__InheritAllAssignment_3 : ( ( 'inheritAll' ) ) ;
    public final void rule__XOseeEnumOverride__InheritAllAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4776:1: ( ( ( 'inheritAll' ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4777:1: ( ( 'inheritAll' ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4777:1: ( ( 'inheritAll' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4778:1: ( 'inheritAll' )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4779:1: ( 'inheritAll' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4780:1: 'inheritAll'
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            }
            match(input,66,FollowSets000.FOLLOW_66_in_rule__XOseeEnumOverride__InheritAllAssignment_39625); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4795:1: rule__XOseeEnumOverride__OverrideOptionsAssignment_4 : ( ruleOverrideOption ) ;
    public final void rule__XOseeEnumOverride__OverrideOptionsAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4799:1: ( ( ruleOverrideOption ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4800:1: ( ruleOverrideOption )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4800:1: ( ruleOverrideOption )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4801:1: ruleOverrideOption
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleOverrideOption_in_rule__XOseeEnumOverride__OverrideOptionsAssignment_49664);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4810:1: rule__AddEnum__EnumEntryAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__AddEnum__EnumEntryAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4814:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4815:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4815:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4816:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getEnumEntryNAME_REFERENCEParserRuleCall_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__AddEnum__EnumEntryAssignment_19695);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4825:1: rule__AddEnum__OrdinalAssignment_2 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__AddEnum__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4829:1: ( ( RULE_WHOLE_NUM_STR ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4830:1: ( RULE_WHOLE_NUM_STR )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4830:1: ( RULE_WHOLE_NUM_STR )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4831:1: RULE_WHOLE_NUM_STR
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
            }
            match(input,RULE_WHOLE_NUM_STR,FollowSets000.FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AddEnum__OrdinalAssignment_29726); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4840:1: rule__AddEnum__EntryGuidAssignment_3_1 : ( RULE_STRING ) ;
    public final void rule__AddEnum__EntryGuidAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4844:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4845:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4845:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4846:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getAddEnumAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__AddEnum__EntryGuidAssignment_3_19757); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4855:1: rule__RemoveEnum__EnumEntryAssignment_1 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__RemoveEnum__EnumEntryAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4859:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4860:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4860:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4861:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryCrossReference_1_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4862:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4863:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryNAME_REFERENCEParserRuleCall_1_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__RemoveEnum__EnumEntryAssignment_19792);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4874:1: rule__XRelationType__NameAssignment_1 : ( ruleNAME_REFERENCE ) ;
    public final void rule__XRelationType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4878:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4879:1: ( ruleNAME_REFERENCE )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4879:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4880:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getNameNAME_REFERENCEParserRuleCall_1_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__NameAssignment_19827);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4889:1: rule__XRelationType__TypeGuidAssignment_4 : ( RULE_STRING ) ;
    public final void rule__XRelationType__TypeGuidAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4893:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4894:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4894:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4895:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_4_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XRelationType__TypeGuidAssignment_49858); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4904:1: rule__XRelationType__SideANameAssignment_6 : ( RULE_STRING ) ;
    public final void rule__XRelationType__SideANameAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4908:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4909:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4909:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4910:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_6_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XRelationType__SideANameAssignment_69889); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4919:1: rule__XRelationType__SideAArtifactTypeAssignment_8 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XRelationType__SideAArtifactTypeAssignment_8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4923:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4924:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4924:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4925:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_8_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4926:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4927:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeNAME_REFERENCEParserRuleCall_8_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__SideAArtifactTypeAssignment_89924);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4938:1: rule__XRelationType__SideBNameAssignment_10 : ( RULE_STRING ) ;
    public final void rule__XRelationType__SideBNameAssignment_10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4942:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4943:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4943:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4944:1: RULE_STRING
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_10_0()); 
            }
            match(input,RULE_STRING,FollowSets000.FOLLOW_RULE_STRING_in_rule__XRelationType__SideBNameAssignment_109959); if (failed) return ;
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4953:1: rule__XRelationType__SideBArtifactTypeAssignment_12 : ( ( ruleNAME_REFERENCE ) ) ;
    public final void rule__XRelationType__SideBArtifactTypeAssignment_12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4957:1: ( ( ( ruleNAME_REFERENCE ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4958:1: ( ( ruleNAME_REFERENCE ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4958:1: ( ( ruleNAME_REFERENCE ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4959:1: ( ruleNAME_REFERENCE )
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_12_0()); 
            }
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4960:1: ( ruleNAME_REFERENCE )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4961:1: ruleNAME_REFERENCE
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeNAME_REFERENCEParserRuleCall_12_0_1()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__SideBArtifactTypeAssignment_129994);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4972:1: rule__XRelationType__DefaultOrderTypeAssignment_14 : ( ruleRelationOrderType ) ;
    public final void rule__XRelationType__DefaultOrderTypeAssignment_14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4976:1: ( ( ruleRelationOrderType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4977:1: ( ruleRelationOrderType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4977:1: ( ruleRelationOrderType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4978:1: ruleRelationOrderType
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_14_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleRelationOrderType_in_rule__XRelationType__DefaultOrderTypeAssignment_1410029);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4987:1: rule__XRelationType__MultiplicityAssignment_16 : ( ruleRelationMultiplicityEnum ) ;
    public final void rule__XRelationType__MultiplicityAssignment_16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4991:1: ( ( ruleRelationMultiplicityEnum ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4992:1: ( ruleRelationMultiplicityEnum )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4992:1: ( ruleRelationMultiplicityEnum )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/ui/contentassist/antlr/internal/InternalOseeTypes.g:4993:1: ruleRelationMultiplicityEnum
            {
            if ( backtracking==0 ) {
               before(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_16_0()); 
            }
            pushFollow(FollowSets000.FOLLOW_ruleRelationMultiplicityEnum_in_rule__XRelationType__MultiplicityAssignment_1610060);
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
        public static final BitSet FOLLOW_rule__OseeTypeModel__Group__0__Impl_in_rule__OseeTypeModel__Group__01948 = new BitSet(new long[]{0x0490040400000002L,0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__Group__1_in_rule__OseeTypeModel__Group__01951 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__ImportsAssignment_0_in_rule__OseeTypeModel__Group__0__Impl1978 = new BitSet(new long[]{0x0000000100000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__Group__1__Impl_in_rule__OseeTypeModel__Group__12009 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__OseeTypeModel__Alternatives_1_in_rule__OseeTypeModel__Group__1__Impl2036 = new BitSet(new long[]{0x0490040400000002L,0x0000000000000002L});
        public static final BitSet FOLLOW_rule__Import__Group__0__Impl_in_rule__Import__Group__02071 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__Import__Group__1_in_rule__Import__Group__02074 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_32_in_rule__Import__Group__0__Impl2102 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__Import__Group__1__Impl_in_rule__Import__Group__12133 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__1__Impl2160 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__0__Impl_in_rule__QUALIFIED_NAME__Group__02194 = new BitSet(new long[]{0x0000000200000002L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__1_in_rule__QUALIFIED_NAME__Group__02197 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group__0__Impl2224 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group__1__Impl_in_rule__QUALIFIED_NAME__Group__12253 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__0_in_rule__QUALIFIED_NAME__Group__1__Impl2280 = new BitSet(new long[]{0x0000000200000002L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__0__Impl_in_rule__QUALIFIED_NAME__Group_1__02315 = new BitSet(new long[]{0x0000000000000040L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__1_in_rule__QUALIFIED_NAME__Group_1__02318 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_33_in_rule__QUALIFIED_NAME__Group_1__0__Impl2346 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__QUALIFIED_NAME__Group_1__1__Impl_in_rule__QUALIFIED_NAME__Group_1__12377 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_ID_in_rule__QUALIFIED_NAME__Group_1__1__Impl2404 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__0__Impl_in_rule__XArtifactType__Group__02437 = new BitSet(new long[]{0x0000000400000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__1_in_rule__XArtifactType__Group__02440 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__AbstractAssignment_0_in_rule__XArtifactType__Group__0__Impl2467 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__1__Impl_in_rule__XArtifactType__Group__12498 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__2_in_rule__XArtifactType__Group__12501 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_34_in_rule__XArtifactType__Group__1__Impl2529 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__2__Impl_in_rule__XArtifactType__Group__22560 = new BitSet(new long[]{0x0000004800000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__3_in_rule__XArtifactType__Group__22563 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__NameAssignment_2_in_rule__XArtifactType__Group__2__Impl2590 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__3__Impl_in_rule__XArtifactType__Group__32620 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__4_in_rule__XArtifactType__Group__32623 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3__0_in_rule__XArtifactType__Group__3__Impl2650 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__4__Impl_in_rule__XArtifactType__Group__42681 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__5_in_rule__XArtifactType__Group__42684 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_35_in_rule__XArtifactType__Group__4__Impl2712 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__5__Impl_in_rule__XArtifactType__Group__52743 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__6_in_rule__XArtifactType__Group__52746 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_36_in_rule__XArtifactType__Group__5__Impl2774 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__6__Impl_in_rule__XArtifactType__Group__62805 = new BitSet(new long[]{0x0000012000000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__7_in_rule__XArtifactType__Group__62808 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__TypeGuidAssignment_6_in_rule__XArtifactType__Group__6__Impl2835 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__7__Impl_in_rule__XArtifactType__Group__72865 = new BitSet(new long[]{0x0000002000000000L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__8_in_rule__XArtifactType__Group__72868 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__ValidAttributeTypesAssignment_7_in_rule__XArtifactType__Group__7__Impl2895 = new BitSet(new long[]{0x0000010000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group__8__Impl_in_rule__XArtifactType__Group__82926 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_37_in_rule__XArtifactType__Group__8__Impl2954 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3__0__Impl_in_rule__XArtifactType__Group_3__03003 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3__1_in_rule__XArtifactType__Group_3__03006 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_38_in_rule__XArtifactType__Group_3__0__Impl3034 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3__1__Impl_in_rule__XArtifactType__Group_3__13065 = new BitSet(new long[]{0x0000008000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3__2_in_rule__XArtifactType__Group_3__13068 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__SuperArtifactTypesAssignment_3_1_in_rule__XArtifactType__Group_3__1__Impl3095 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3__2__Impl_in_rule__XArtifactType__Group_3__23125 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3_2__0_in_rule__XArtifactType__Group_3__2__Impl3152 = new BitSet(new long[]{0x0000008000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3_2__0__Impl_in_rule__XArtifactType__Group_3_2__03189 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3_2__1_in_rule__XArtifactType__Group_3_2__03192 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_39_in_rule__XArtifactType__Group_3_2__0__Impl3220 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__Group_3_2__1__Impl_in_rule__XArtifactType__Group_3_2__13251 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1_in_rule__XArtifactType__Group_3_2__1__Impl3278 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group__0__Impl_in_rule__XAttributeTypeRef__Group__03312 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group__1_in_rule__XAttributeTypeRef__Group__03315 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_40_in_rule__XAttributeTypeRef__Group__0__Impl3343 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group__1__Impl_in_rule__XAttributeTypeRef__Group__13374 = new BitSet(new long[]{0x0000020000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group__2_in_rule__XAttributeTypeRef__Group__13377 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1_in_rule__XAttributeTypeRef__Group__1__Impl3404 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group__2__Impl_in_rule__XAttributeTypeRef__Group__23434 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group_2__0_in_rule__XAttributeTypeRef__Group__2__Impl3461 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group_2__0__Impl_in_rule__XAttributeTypeRef__Group_2__03498 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group_2__1_in_rule__XAttributeTypeRef__Group_2__03501 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_41_in_rule__XAttributeTypeRef__Group_2__0__Impl3529 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__Group_2__1__Impl_in_rule__XAttributeTypeRef__Group_2__13560 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeTypeRef__BranchGuidAssignment_2_1_in_rule__XAttributeTypeRef__Group_2__1__Impl3587 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__0__Impl_in_rule__XAttributeType__Group__03621 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__1_in_rule__XAttributeType__Group__03624 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_42_in_rule__XAttributeType__Group__0__Impl3652 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__1__Impl_in_rule__XAttributeType__Group__13683 = new BitSet(new long[]{0x0000004000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__2_in_rule__XAttributeType__Group__13686 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__NameAssignment_1_in_rule__XAttributeType__Group__1__Impl3713 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__2__Impl_in_rule__XAttributeType__Group__23743 = new BitSet(new long[]{0x0000400800000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__3_in_rule__XAttributeType__Group__23746 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_2__0_in_rule__XAttributeType__Group__2__Impl3773 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__3__Impl_in_rule__XAttributeType__Group__33803 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__4_in_rule__XAttributeType__Group__33806 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_3__0_in_rule__XAttributeType__Group__3__Impl3833 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__4__Impl_in_rule__XAttributeType__Group__43864 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__5_in_rule__XAttributeType__Group__43867 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_35_in_rule__XAttributeType__Group__4__Impl3895 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__5__Impl_in_rule__XAttributeType__Group__53926 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__6_in_rule__XAttributeType__Group__53929 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_36_in_rule__XAttributeType__Group__5__Impl3957 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__6__Impl_in_rule__XAttributeType__Group__63988 = new BitSet(new long[]{0x0000080000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__7_in_rule__XAttributeType__Group__63991 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__TypeGuidAssignment_6_in_rule__XAttributeType__Group__6__Impl4018 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__7__Impl_in_rule__XAttributeType__Group__74048 = new BitSet(new long[]{0x0000000000003040L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__8_in_rule__XAttributeType__Group__74051 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_43_in_rule__XAttributeType__Group__7__Impl4079 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__8__Impl_in_rule__XAttributeType__Group__84110 = new BitSet(new long[]{0x0000100000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__9_in_rule__XAttributeType__Group__84113 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__DataProviderAssignment_8_in_rule__XAttributeType__Group__8__Impl4140 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__9__Impl_in_rule__XAttributeType__Group__94170 = new BitSet(new long[]{0x0000000000000020L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__10_in_rule__XAttributeType__Group__94173 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_44_in_rule__XAttributeType__Group__9__Impl4201 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__10__Impl_in_rule__XAttributeType__Group__104232 = new BitSet(new long[]{0x0000200000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__11_in_rule__XAttributeType__Group__104235 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__MinAssignment_10_in_rule__XAttributeType__Group__10__Impl4262 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__11__Impl_in_rule__XAttributeType__Group__114292 = new BitSet(new long[]{0x0000000000004020L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__12_in_rule__XAttributeType__Group__114295 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_45_in_rule__XAttributeType__Group__11__Impl4323 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__12__Impl_in_rule__XAttributeType__Group__124354 = new BitSet(new long[]{0x000F802000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__13_in_rule__XAttributeType__Group__124357 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__MaxAssignment_12_in_rule__XAttributeType__Group__12__Impl4384 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__13__Impl_in_rule__XAttributeType__Group__134414 = new BitSet(new long[]{0x000F002000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__14_in_rule__XAttributeType__Group__134417 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_13__0_in_rule__XAttributeType__Group__13__Impl4444 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__14__Impl_in_rule__XAttributeType__Group__144475 = new BitSet(new long[]{0x000E002000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__15_in_rule__XAttributeType__Group__144478 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_14__0_in_rule__XAttributeType__Group__14__Impl4505 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__15__Impl_in_rule__XAttributeType__Group__154536 = new BitSet(new long[]{0x000C002000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__16_in_rule__XAttributeType__Group__154539 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_15__0_in_rule__XAttributeType__Group__15__Impl4566 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__16__Impl_in_rule__XAttributeType__Group__164597 = new BitSet(new long[]{0x0008002000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__17_in_rule__XAttributeType__Group__164600 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_16__0_in_rule__XAttributeType__Group__16__Impl4627 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__17__Impl_in_rule__XAttributeType__Group__174658 = new BitSet(new long[]{0x0000002000000000L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__18_in_rule__XAttributeType__Group__174661 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_17__0_in_rule__XAttributeType__Group__17__Impl4688 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group__18__Impl_in_rule__XAttributeType__Group__184719 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_37_in_rule__XAttributeType__Group__18__Impl4747 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_2__0__Impl_in_rule__XAttributeType__Group_2__04816 = new BitSet(new long[]{0x0000000001FF0040L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_2__1_in_rule__XAttributeType__Group_2__04819 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_38_in_rule__XAttributeType__Group_2__0__Impl4847 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_2__1__Impl_in_rule__XAttributeType__Group_2__14878 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__BaseAttributeTypeAssignment_2_1_in_rule__XAttributeType__Group_2__1__Impl4905 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_3__0__Impl_in_rule__XAttributeType__Group_3__04939 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_3__1_in_rule__XAttributeType__Group_3__04942 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_46_in_rule__XAttributeType__Group_3__0__Impl4970 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_3__1__Impl_in_rule__XAttributeType__Group_3__15001 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__OverrideAssignment_3_1_in_rule__XAttributeType__Group_3__1__Impl5028 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_13__0__Impl_in_rule__XAttributeType__Group_13__05062 = new BitSet(new long[]{0x0000000000008040L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_13__1_in_rule__XAttributeType__Group_13__05065 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_47_in_rule__XAttributeType__Group_13__0__Impl5093 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_13__1__Impl_in_rule__XAttributeType__Group_13__15124 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__TaggerIdAssignment_13_1_in_rule__XAttributeType__Group_13__1__Impl5151 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_14__0__Impl_in_rule__XAttributeType__Group_14__05185 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_14__1_in_rule__XAttributeType__Group_14__05188 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_48_in_rule__XAttributeType__Group_14__0__Impl5216 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_14__1__Impl_in_rule__XAttributeType__Group_14__15247 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__EnumTypeAssignment_14_1_in_rule__XAttributeType__Group_14__1__Impl5274 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_15__0__Impl_in_rule__XAttributeType__Group_15__05308 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_15__1_in_rule__XAttributeType__Group_15__05311 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_49_in_rule__XAttributeType__Group_15__0__Impl5339 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_15__1__Impl_in_rule__XAttributeType__Group_15__15370 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__DescriptionAssignment_15_1_in_rule__XAttributeType__Group_15__1__Impl5397 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_16__0__Impl_in_rule__XAttributeType__Group_16__05431 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_16__1_in_rule__XAttributeType__Group_16__05434 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_50_in_rule__XAttributeType__Group_16__0__Impl5462 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_16__1__Impl_in_rule__XAttributeType__Group_16__15493 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__DefaultValueAssignment_16_1_in_rule__XAttributeType__Group_16__1__Impl5520 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_17__0__Impl_in_rule__XAttributeType__Group_17__05554 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_17__1_in_rule__XAttributeType__Group_17__05557 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_51_in_rule__XAttributeType__Group_17__0__Impl5585 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__Group_17__1__Impl_in_rule__XAttributeType__Group_17__15616 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__FileExtensionAssignment_17_1_in_rule__XAttributeType__Group_17__1__Impl5643 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__0__Impl_in_rule__XOseeEnumType__Group__05677 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__1_in_rule__XOseeEnumType__Group__05680 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_52_in_rule__XOseeEnumType__Group__0__Impl5708 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__1__Impl_in_rule__XOseeEnumType__Group__15739 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__2_in_rule__XOseeEnumType__Group__15742 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__NameAssignment_1_in_rule__XOseeEnumType__Group__1__Impl5769 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__2__Impl_in_rule__XOseeEnumType__Group__25799 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__3_in_rule__XOseeEnumType__Group__25802 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_35_in_rule__XOseeEnumType__Group__2__Impl5830 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__3__Impl_in_rule__XOseeEnumType__Group__35861 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__4_in_rule__XOseeEnumType__Group__35864 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_36_in_rule__XOseeEnumType__Group__3__Impl5892 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__4__Impl_in_rule__XOseeEnumType__Group__45923 = new BitSet(new long[]{0x0020002000000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__5_in_rule__XOseeEnumType__Group__45926 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__TypeGuidAssignment_4_in_rule__XOseeEnumType__Group__4__Impl5953 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__5__Impl_in_rule__XOseeEnumType__Group__55983 = new BitSet(new long[]{0x0000002000000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__6_in_rule__XOseeEnumType__Group__55986 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__EnumEntriesAssignment_5_in_rule__XOseeEnumType__Group__5__Impl6013 = new BitSet(new long[]{0x0020000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumType__Group__6__Impl_in_rule__XOseeEnumType__Group__66044 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_37_in_rule__XOseeEnumType__Group__6__Impl6072 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__0__Impl_in_rule__XOseeEnumEntry__Group__06117 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__1_in_rule__XOseeEnumEntry__Group__06120 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_53_in_rule__XOseeEnumEntry__Group__0__Impl6148 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__1__Impl_in_rule__XOseeEnumEntry__Group__16179 = new BitSet(new long[]{0x0040000000000022L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__2_in_rule__XOseeEnumEntry__Group__16182 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__NameAssignment_1_in_rule__XOseeEnumEntry__Group__1__Impl6209 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__2__Impl_in_rule__XOseeEnumEntry__Group__26239 = new BitSet(new long[]{0x0040000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__3_in_rule__XOseeEnumEntry__Group__26242 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__OrdinalAssignment_2_in_rule__XOseeEnumEntry__Group__2__Impl6269 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group__3__Impl_in_rule__XOseeEnumEntry__Group__36300 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group_3__0_in_rule__XOseeEnumEntry__Group__3__Impl6327 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group_3__0__Impl_in_rule__XOseeEnumEntry__Group_3__06366 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group_3__1_in_rule__XOseeEnumEntry__Group_3__06369 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_54_in_rule__XOseeEnumEntry__Group_3__0__Impl6397 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__Group_3__1__Impl_in_rule__XOseeEnumEntry__Group_3__16428 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumEntry__EntryGuidAssignment_3_1_in_rule__XOseeEnumEntry__Group_3__1__Impl6455 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__0__Impl_in_rule__XOseeEnumOverride__Group__06489 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__1_in_rule__XOseeEnumOverride__Group__06492 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_55_in_rule__XOseeEnumOverride__Group__0__Impl6520 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__1__Impl_in_rule__XOseeEnumOverride__Group__16551 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__2_in_rule__XOseeEnumOverride__Group__16554 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1_in_rule__XOseeEnumOverride__Group__1__Impl6581 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__2__Impl_in_rule__XOseeEnumOverride__Group__26611 = new BitSet(new long[]{0x0300002000000000L,0x0000000000000004L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__3_in_rule__XOseeEnumOverride__Group__26614 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_35_in_rule__XOseeEnumOverride__Group__2__Impl6642 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__3__Impl_in_rule__XOseeEnumOverride__Group__36673 = new BitSet(new long[]{0x0300002000000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__4_in_rule__XOseeEnumOverride__Group__36676 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__InheritAllAssignment_3_in_rule__XOseeEnumOverride__Group__3__Impl6703 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__4__Impl_in_rule__XOseeEnumOverride__Group__46734 = new BitSet(new long[]{0x0000002000000000L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__5_in_rule__XOseeEnumOverride__Group__46737 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__OverrideOptionsAssignment_4_in_rule__XOseeEnumOverride__Group__4__Impl6764 = new BitSet(new long[]{0x0300000000000002L});
        public static final BitSet FOLLOW_rule__XOseeEnumOverride__Group__5__Impl_in_rule__XOseeEnumOverride__Group__56795 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_37_in_rule__XOseeEnumOverride__Group__5__Impl6823 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__0__Impl_in_rule__AddEnum__Group__06866 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__1_in_rule__AddEnum__Group__06869 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_56_in_rule__AddEnum__Group__0__Impl6897 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__1__Impl_in_rule__AddEnum__Group__16928 = new BitSet(new long[]{0x0040000000000022L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__2_in_rule__AddEnum__Group__16931 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__EnumEntryAssignment_1_in_rule__AddEnum__Group__1__Impl6958 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__2__Impl_in_rule__AddEnum__Group__26988 = new BitSet(new long[]{0x0040000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__3_in_rule__AddEnum__Group__26991 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__OrdinalAssignment_2_in_rule__AddEnum__Group__2__Impl7018 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group__3__Impl_in_rule__AddEnum__Group__37049 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group_3__0_in_rule__AddEnum__Group__3__Impl7076 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group_3__0__Impl_in_rule__AddEnum__Group_3__07115 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__AddEnum__Group_3__1_in_rule__AddEnum__Group_3__07118 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_54_in_rule__AddEnum__Group_3__0__Impl7146 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__Group_3__1__Impl_in_rule__AddEnum__Group_3__17177 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__AddEnum__EntryGuidAssignment_3_1_in_rule__AddEnum__Group_3__1__Impl7204 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__RemoveEnum__Group__0__Impl_in_rule__RemoveEnum__Group__07238 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__RemoveEnum__Group__1_in_rule__RemoveEnum__Group__07241 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_57_in_rule__RemoveEnum__Group__0__Impl7269 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__RemoveEnum__Group__1__Impl_in_rule__RemoveEnum__Group__17300 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__RemoveEnum__EnumEntryAssignment_1_in_rule__RemoveEnum__Group__1__Impl7327 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__0__Impl_in_rule__XRelationType__Group__07361 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__1_in_rule__XRelationType__Group__07364 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_58_in_rule__XRelationType__Group__0__Impl7392 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__1__Impl_in_rule__XRelationType__Group__17423 = new BitSet(new long[]{0x0000000800000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__2_in_rule__XRelationType__Group__17426 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__NameAssignment_1_in_rule__XRelationType__Group__1__Impl7453 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__2__Impl_in_rule__XRelationType__Group__27483 = new BitSet(new long[]{0x0000001000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__3_in_rule__XRelationType__Group__27486 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_35_in_rule__XRelationType__Group__2__Impl7514 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__3__Impl_in_rule__XRelationType__Group__37545 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__4_in_rule__XRelationType__Group__37548 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_36_in_rule__XRelationType__Group__3__Impl7576 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__4__Impl_in_rule__XRelationType__Group__47607 = new BitSet(new long[]{0x0800000000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__5_in_rule__XRelationType__Group__47610 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__TypeGuidAssignment_4_in_rule__XRelationType__Group__4__Impl7637 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__5__Impl_in_rule__XRelationType__Group__57667 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__6_in_rule__XRelationType__Group__57670 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_59_in_rule__XRelationType__Group__5__Impl7698 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__6__Impl_in_rule__XRelationType__Group__67729 = new BitSet(new long[]{0x1000000000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__7_in_rule__XRelationType__Group__67732 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__SideANameAssignment_6_in_rule__XRelationType__Group__6__Impl7759 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__7__Impl_in_rule__XRelationType__Group__77789 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__8_in_rule__XRelationType__Group__77792 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_60_in_rule__XRelationType__Group__7__Impl7820 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__8__Impl_in_rule__XRelationType__Group__87851 = new BitSet(new long[]{0x2000000000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__9_in_rule__XRelationType__Group__87854 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__SideAArtifactTypeAssignment_8_in_rule__XRelationType__Group__8__Impl7881 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__9__Impl_in_rule__XRelationType__Group__97911 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__10_in_rule__XRelationType__Group__97914 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_61_in_rule__XRelationType__Group__9__Impl7942 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__10__Impl_in_rule__XRelationType__Group__107973 = new BitSet(new long[]{0x4000000000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__11_in_rule__XRelationType__Group__107976 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__SideBNameAssignment_10_in_rule__XRelationType__Group__10__Impl8003 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__11__Impl_in_rule__XRelationType__Group__118033 = new BitSet(new long[]{0x0000000000000010L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__12_in_rule__XRelationType__Group__118036 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_62_in_rule__XRelationType__Group__11__Impl8064 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__12__Impl_in_rule__XRelationType__Group__128095 = new BitSet(new long[]{0x8000000000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__13_in_rule__XRelationType__Group__128098 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__SideBArtifactTypeAssignment_12_in_rule__XRelationType__Group__12__Impl8125 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__13__Impl_in_rule__XRelationType__Group__138155 = new BitSet(new long[]{0x000000000E000040L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__14_in_rule__XRelationType__Group__138158 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_63_in_rule__XRelationType__Group__13__Impl8186 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__14__Impl_in_rule__XRelationType__Group__148217 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__15_in_rule__XRelationType__Group__148220 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__DefaultOrderTypeAssignment_14_in_rule__XRelationType__Group__14__Impl8247 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__15__Impl_in_rule__XRelationType__Group__158277 = new BitSet(new long[]{0x00000000F0000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__16_in_rule__XRelationType__Group__158280 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_64_in_rule__XRelationType__Group__15__Impl8308 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__16__Impl_in_rule__XRelationType__Group__168339 = new BitSet(new long[]{0x0000002000000000L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__17_in_rule__XRelationType__Group__168342 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__MultiplicityAssignment_16_in_rule__XRelationType__Group__16__Impl8369 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XRelationType__Group__17__Impl_in_rule__XRelationType__Group__178399 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_37_in_rule__XRelationType__Group__17__Impl8427 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleImport_in_rule__OseeTypeModel__ImportsAssignment_08499 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXArtifactType_in_rule__OseeTypeModel__ArtifactTypesAssignment_1_08530 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXRelationType_in_rule__OseeTypeModel__RelationTypesAssignment_1_18561 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXAttributeType_in_rule__OseeTypeModel__AttributeTypesAssignment_1_28592 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXOseeEnumType_in_rule__OseeTypeModel__EnumTypesAssignment_1_38623 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXOseeEnumOverride_in_rule__OseeTypeModel__EnumOverridesAssignment_1_48654 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_18685 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_65_in_rule__XArtifactType__AbstractAssignment_08721 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__NameAssignment_28760 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__SuperArtifactTypesAssignment_3_18795 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XArtifactType__SuperArtifactTypesAssignment_3_2_18834 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XArtifactType__TypeGuidAssignment_68869 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXAttributeTypeRef_in_rule__XArtifactType__ValidAttributeTypesAssignment_78900 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeTypeRef__ValidAttributeTypeAssignment_18935 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttributeTypeRef__BranchGuidAssignment_2_18970 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__NameAssignment_19001 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleAttributeBaseType_in_rule__XAttributeType__BaseAttributeTypeAssignment_2_19032 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__OverrideAssignment_3_19067 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttributeType__TypeGuidAssignment_69102 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__DataProviderAlternatives_8_0_in_rule__XAttributeType__DataProviderAssignment_89133 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__XAttributeType__MinAssignment_109166 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__MaxAlternatives_12_0_in_rule__XAttributeType__MaxAssignment_129197 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_rule__XAttributeType__TaggerIdAlternatives_13_1_0_in_rule__XAttributeType__TaggerIdAssignment_13_19230 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XAttributeType__EnumTypeAssignment_14_19267 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttributeType__DescriptionAssignment_15_19302 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttributeType__DefaultValueAssignment_16_19333 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttributeType__FileExtensionAssignment_17_19364 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumType__NameAssignment_19395 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XOseeEnumType__TypeGuidAssignment_49426 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleXOseeEnumEntry_in_rule__XOseeEnumType__EnumEntriesAssignment_59457 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumEntry__NameAssignment_19488 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__XOseeEnumEntry__OrdinalAssignment_29519 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XOseeEnumEntry__EntryGuidAssignment_3_19550 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XOseeEnumOverride__OverridenEnumTypeAssignment_19585 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_66_in_rule__XOseeEnumOverride__InheritAllAssignment_39625 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleOverrideOption_in_rule__XOseeEnumOverride__OverrideOptionsAssignment_49664 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__AddEnum__EnumEntryAssignment_19695 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_WHOLE_NUM_STR_in_rule__AddEnum__OrdinalAssignment_29726 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__AddEnum__EntryGuidAssignment_3_19757 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__RemoveEnum__EnumEntryAssignment_19792 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__NameAssignment_19827 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XRelationType__TypeGuidAssignment_49858 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XRelationType__SideANameAssignment_69889 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__SideAArtifactTypeAssignment_89924 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_RULE_STRING_in_rule__XRelationType__SideBNameAssignment_109959 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleNAME_REFERENCE_in_rule__XRelationType__SideBArtifactTypeAssignment_129994 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleRelationOrderType_in_rule__XRelationType__DefaultOrderTypeAssignment_1410029 = new BitSet(new long[]{0x0000000000000002L});
        public static final BitSet FOLLOW_ruleRelationMultiplicityEnum_in_rule__XRelationType__MultiplicityAssignment_1610060 = new BitSet(new long[]{0x0000000000000002L});
    }


}