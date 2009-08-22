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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_ID", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'one-to-many'", "'many-to-many'", "'many-to-one'", "'import'", "'artifactType'", "'{'", "'}'", "'extends'", "'relation'", "'attribute'", "'attributeType'", "'dataProvider'", "'taggerId'", "'defaultValue'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'"
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




    // $ANTLR start entryRuleModel
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:60:1: entryRuleModel : ruleModel EOF ;
    public final void entryRuleModel() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:60:16: ( ruleModel EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:61:1: ruleModel EOF
            {
             before(grammarAccess.getModelRule()); 
            pushFollow(FOLLOW_ruleModel_in_entryRuleModel60);
            ruleModel();
            _fsp--;

             after(grammarAccess.getModelRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleModel67); 

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
    // $ANTLR end entryRuleModel


    // $ANTLR start ruleModel
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:68:1: ruleModel : ( ( rule__Model__Group__0 ) ) ;
    public final void ruleModel() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:72:2: ( ( ( rule__Model__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:73:1: ( ( rule__Model__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:73:1: ( ( rule__Model__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:74:1: ( rule__Model__Group__0 )
            {
             before(grammarAccess.getModelAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:75:1: ( rule__Model__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:75:2: rule__Model__Group__0
            {
            pushFollow(FOLLOW_rule__Model__Group__0_in_ruleModel94);
            rule__Model__Group__0();
            _fsp--;


            }

             after(grammarAccess.getModelAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleModel


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


    // $ANTLR start entryRuleType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:114:1: entryRuleType : ruleType EOF ;
    public final void entryRuleType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:114:15: ( ruleType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:115:1: ruleType EOF
            {
             before(grammarAccess.getTypeRule()); 
            pushFollow(FOLLOW_ruleType_in_entryRuleType180);
            ruleType();
            _fsp--;

             after(grammarAccess.getTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleType187); 

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
    // $ANTLR end entryRuleType


    // $ANTLR start ruleType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:122:1: ruleType : ( ( rule__Type__Alternatives ) ) ;
    public final void ruleType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:126:2: ( ( ( rule__Type__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:127:1: ( ( rule__Type__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:127:1: ( ( rule__Type__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:128:1: ( rule__Type__Alternatives )
            {
             before(grammarAccess.getTypeAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:129:1: ( rule__Type__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:129:2: rule__Type__Alternatives
            {
            pushFollow(FOLLOW_rule__Type__Alternatives_in_ruleType214);
            rule__Type__Alternatives();
            _fsp--;


            }

             after(grammarAccess.getTypeAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleType


    // $ANTLR start entryRuleArtifactType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:141:1: entryRuleArtifactType : ruleArtifactType EOF ;
    public final void entryRuleArtifactType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:141:23: ( ruleArtifactType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:142:1: ruleArtifactType EOF
            {
             before(grammarAccess.getArtifactTypeRule()); 
            pushFollow(FOLLOW_ruleArtifactType_in_entryRuleArtifactType240);
            ruleArtifactType();
            _fsp--;

             after(grammarAccess.getArtifactTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleArtifactType247); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:149:1: ruleArtifactType : ( ( rule__ArtifactType__Group__0 ) ) ;
    public final void ruleArtifactType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:153:2: ( ( ( rule__ArtifactType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:154:1: ( ( rule__ArtifactType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:154:1: ( ( rule__ArtifactType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:155:1: ( rule__ArtifactType__Group__0 )
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:156:1: ( rule__ArtifactType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:156:2: rule__ArtifactType__Group__0
            {
            pushFollow(FOLLOW_rule__ArtifactType__Group__0_in_ruleArtifactType274);
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


    // $ANTLR start entryRuleXRef
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:168:1: entryRuleXRef : ruleXRef EOF ;
    public final void entryRuleXRef() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:168:15: ( ruleXRef EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:169:1: ruleXRef EOF
            {
             before(grammarAccess.getXRefRule()); 
            pushFollow(FOLLOW_ruleXRef_in_entryRuleXRef300);
            ruleXRef();
            _fsp--;

             after(grammarAccess.getXRefRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXRef307); 

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
    // $ANTLR end entryRuleXRef


    // $ANTLR start ruleXRef
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:176:1: ruleXRef : ( ( rule__XRef__Alternatives ) ) ;
    public final void ruleXRef() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:180:2: ( ( ( rule__XRef__Alternatives ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:181:1: ( ( rule__XRef__Alternatives ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:181:1: ( ( rule__XRef__Alternatives ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:182:1: ( rule__XRef__Alternatives )
            {
             before(grammarAccess.getXRefAccess().getAlternatives()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:183:1: ( rule__XRef__Alternatives )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:183:2: rule__XRef__Alternatives
            {
            pushFollow(FOLLOW_rule__XRef__Alternatives_in_ruleXRef334);
            rule__XRef__Alternatives();
            _fsp--;


            }

             after(grammarAccess.getXRefAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleXRef


    // $ANTLR start entryRuleRelationTypeRef
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:195:1: entryRuleRelationTypeRef : ruleRelationTypeRef EOF ;
    public final void entryRuleRelationTypeRef() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:195:26: ( ruleRelationTypeRef EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:196:1: ruleRelationTypeRef EOF
            {
             before(grammarAccess.getRelationTypeRefRule()); 
            pushFollow(FOLLOW_ruleRelationTypeRef_in_entryRuleRelationTypeRef360);
            ruleRelationTypeRef();
            _fsp--;

             after(grammarAccess.getRelationTypeRefRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationTypeRef367); 

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
    // $ANTLR end entryRuleRelationTypeRef


    // $ANTLR start ruleRelationTypeRef
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:203:1: ruleRelationTypeRef : ( ( rule__RelationTypeRef__Group__0 ) ) ;
    public final void ruleRelationTypeRef() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:207:2: ( ( ( rule__RelationTypeRef__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:208:1: ( ( rule__RelationTypeRef__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:208:1: ( ( rule__RelationTypeRef__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:209:1: ( rule__RelationTypeRef__Group__0 )
            {
             before(grammarAccess.getRelationTypeRefAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:210:1: ( rule__RelationTypeRef__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:210:2: rule__RelationTypeRef__Group__0
            {
            pushFollow(FOLLOW_rule__RelationTypeRef__Group__0_in_ruleRelationTypeRef394);
            rule__RelationTypeRef__Group__0();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeRefAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleRelationTypeRef


    // $ANTLR start entryRuleAttributeTypeRef
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:222:1: entryRuleAttributeTypeRef : ruleAttributeTypeRef EOF ;
    public final void entryRuleAttributeTypeRef() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:222:27: ( ruleAttributeTypeRef EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:223:1: ruleAttributeTypeRef EOF
            {
             before(grammarAccess.getAttributeTypeRefRule()); 
            pushFollow(FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef420);
            ruleAttributeTypeRef();
            _fsp--;

             after(grammarAccess.getAttributeTypeRefRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeTypeRef427); 

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
            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__0_in_ruleAttributeTypeRef454);
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
            pushFollow(FOLLOW_ruleAttributeType_in_entryRuleAttributeType480);
            ruleAttributeType();
            _fsp--;

             after(grammarAccess.getAttributeTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeType487); 

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
            pushFollow(FOLLOW_rule__AttributeType__Group__0_in_ruleAttributeType514);
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


    // $ANTLR start entryRuleXAttribute
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:276:1: entryRuleXAttribute : ruleXAttribute EOF ;
    public final void entryRuleXAttribute() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:276:21: ( ruleXAttribute EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:277:1: ruleXAttribute EOF
            {
             before(grammarAccess.getXAttributeRule()); 
            pushFollow(FOLLOW_ruleXAttribute_in_entryRuleXAttribute540);
            ruleXAttribute();
            _fsp--;

             after(grammarAccess.getXAttributeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXAttribute547); 

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
    // $ANTLR end entryRuleXAttribute


    // $ANTLR start ruleXAttribute
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:284:1: ruleXAttribute : ( ( rule__XAttribute__Group__0 ) ) ;
    public final void ruleXAttribute() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:288:2: ( ( ( rule__XAttribute__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:289:1: ( ( rule__XAttribute__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:289:1: ( ( rule__XAttribute__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:290:1: ( rule__XAttribute__Group__0 )
            {
             before(grammarAccess.getXAttributeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:291:1: ( rule__XAttribute__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:291:2: rule__XAttribute__Group__0
            {
            pushFollow(FOLLOW_rule__XAttribute__Group__0_in_ruleXAttribute574);
            rule__XAttribute__Group__0();
            _fsp--;


            }

             after(grammarAccess.getXAttributeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleXAttribute


    // $ANTLR start entryRuleRelationType
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:303:1: entryRuleRelationType : ruleRelationType EOF ;
    public final void entryRuleRelationType() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:303:23: ( ruleRelationType EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:304:1: ruleRelationType EOF
            {
             before(grammarAccess.getRelationTypeRule()); 
            pushFollow(FOLLOW_ruleRelationType_in_entryRuleRelationType600);
            ruleRelationType();
            _fsp--;

             after(grammarAccess.getRelationTypeRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelationType607); 

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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:311:1: ruleRelationType : ( ( rule__RelationType__Group__0 ) ) ;
    public final void ruleRelationType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:315:2: ( ( ( rule__RelationType__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:316:1: ( ( rule__RelationType__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:316:1: ( ( rule__RelationType__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:317:1: ( rule__RelationType__Group__0 )
            {
             before(grammarAccess.getRelationTypeAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:318:1: ( rule__RelationType__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:318:2: rule__RelationType__Group__0
            {
            pushFollow(FOLLOW_rule__RelationType__Group__0_in_ruleRelationType634);
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


    // $ANTLR start entryRuleXRelation
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:330:1: entryRuleXRelation : ruleXRelation EOF ;
    public final void entryRuleXRelation() throws RecognitionException {
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:330:20: ( ruleXRelation EOF )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:331:1: ruleXRelation EOF
            {
             before(grammarAccess.getXRelationRule()); 
            pushFollow(FOLLOW_ruleXRelation_in_entryRuleXRelation660);
            ruleXRelation();
            _fsp--;

             after(grammarAccess.getXRelationRule()); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleXRelation667); 

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
    // $ANTLR end entryRuleXRelation


    // $ANTLR start ruleXRelation
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:338:1: ruleXRelation : ( ( rule__XRelation__Group__0 ) ) ;
    public final void ruleXRelation() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:342:2: ( ( ( rule__XRelation__Group__0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:343:1: ( ( rule__XRelation__Group__0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:343:1: ( ( rule__XRelation__Group__0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:344:1: ( rule__XRelation__Group__0 )
            {
             before(grammarAccess.getXRelationAccess().getGroup()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:345:1: ( rule__XRelation__Group__0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:345:2: rule__XRelation__Group__0
            {
            pushFollow(FOLLOW_rule__XRelation__Group__0_in_ruleXRelation694);
            rule__XRelation__Group__0();
            _fsp--;


            }

             after(grammarAccess.getXRelationAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end ruleXRelation


    // $ANTLR start rule__Type__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:357:1: rule__Type__Alternatives : ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) );
    public final void rule__Type__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:361:1: ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) )
            int alt1=3;
            switch ( input.LA(1) ) {
            case 15:
                {
                alt1=1;
                }
                break;
            case 25:
                {
                alt1=2;
                }
                break;
            case 21:
                {
                alt1=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("357:1: rule__Type__Alternatives : ( ( ruleArtifactType ) | ( ruleRelationType ) | ( ruleAttributeType ) );", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:362:1: ( ruleArtifactType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:362:1: ( ruleArtifactType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:363:1: ruleArtifactType
                    {
                     before(grammarAccess.getTypeAccess().getArtifactTypeParserRuleCall_0()); 
                    pushFollow(FOLLOW_ruleArtifactType_in_rule__Type__Alternatives730);
                    ruleArtifactType();
                    _fsp--;

                     after(grammarAccess.getTypeAccess().getArtifactTypeParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:368:6: ( ruleRelationType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:368:6: ( ruleRelationType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:369:1: ruleRelationType
                    {
                     before(grammarAccess.getTypeAccess().getRelationTypeParserRuleCall_1()); 
                    pushFollow(FOLLOW_ruleRelationType_in_rule__Type__Alternatives747);
                    ruleRelationType();
                    _fsp--;

                     after(grammarAccess.getTypeAccess().getRelationTypeParserRuleCall_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:374:6: ( ruleAttributeType )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:374:6: ( ruleAttributeType )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:375:1: ruleAttributeType
                    {
                     before(grammarAccess.getTypeAccess().getAttributeTypeParserRuleCall_2()); 
                    pushFollow(FOLLOW_ruleAttributeType_in_rule__Type__Alternatives764);
                    ruleAttributeType();
                    _fsp--;

                     after(grammarAccess.getTypeAccess().getAttributeTypeParserRuleCall_2()); 

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
    // $ANTLR end rule__Type__Alternatives


    // $ANTLR start rule__XRef__Alternatives
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:385:1: rule__XRef__Alternatives : ( ( ruleRelationTypeRef ) | ( ruleAttributeTypeRef ) );
    public final void rule__XRef__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:389:1: ( ( ruleRelationTypeRef ) | ( ruleAttributeTypeRef ) )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==19) ) {
                alt2=1;
            }
            else if ( (LA2_0==20) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("385:1: rule__XRef__Alternatives : ( ( ruleRelationTypeRef ) | ( ruleAttributeTypeRef ) );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:390:1: ( ruleRelationTypeRef )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:390:1: ( ruleRelationTypeRef )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:391:1: ruleRelationTypeRef
                    {
                     before(grammarAccess.getXRefAccess().getRelationTypeRefParserRuleCall_0()); 
                    pushFollow(FOLLOW_ruleRelationTypeRef_in_rule__XRef__Alternatives796);
                    ruleRelationTypeRef();
                    _fsp--;

                     after(grammarAccess.getXRefAccess().getRelationTypeRefParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:396:6: ( ruleAttributeTypeRef )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:396:6: ( ruleAttributeTypeRef )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:397:1: ruleAttributeTypeRef
                    {
                     before(grammarAccess.getXRefAccess().getAttributeTypeRefParserRuleCall_1()); 
                    pushFollow(FOLLOW_ruleAttributeTypeRef_in_rule__XRef__Alternatives813);
                    ruleAttributeTypeRef();
                    _fsp--;

                     after(grammarAccess.getXRefAccess().getAttributeTypeRefParserRuleCall_1()); 

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
    // $ANTLR end rule__XRef__Alternatives


    // $ANTLR start rule__XRelation__NameAlternatives_11_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:407:1: rule__XRelation__NameAlternatives_11_0 : ( ( 'one-to-many' ) | ( 'many-to-many' ) | ( 'many-to-one' ) );
    public final void rule__XRelation__NameAlternatives_11_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:411:1: ( ( 'one-to-many' ) | ( 'many-to-many' ) | ( 'many-to-one' ) )
            int alt3=3;
            switch ( input.LA(1) ) {
            case 11:
                {
                alt3=1;
                }
                break;
            case 12:
                {
                alt3=2;
                }
                break;
            case 13:
                {
                alt3=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("407:1: rule__XRelation__NameAlternatives_11_0 : ( ( 'one-to-many' ) | ( 'many-to-many' ) | ( 'many-to-one' ) );", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:412:1: ( 'one-to-many' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:412:1: ( 'one-to-many' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:413:1: 'one-to-many'
                    {
                     before(grammarAccess.getXRelationAccess().getNameOneToManyKeyword_11_0_0()); 
                    match(input,11,FOLLOW_11_in_rule__XRelation__NameAlternatives_11_0846); 
                     after(grammarAccess.getXRelationAccess().getNameOneToManyKeyword_11_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:420:6: ( 'many-to-many' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:420:6: ( 'many-to-many' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:421:1: 'many-to-many'
                    {
                     before(grammarAccess.getXRelationAccess().getNameManyToManyKeyword_11_0_1()); 
                    match(input,12,FOLLOW_12_in_rule__XRelation__NameAlternatives_11_0866); 
                     after(grammarAccess.getXRelationAccess().getNameManyToManyKeyword_11_0_1()); 

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:428:6: ( 'many-to-one' )
                    {
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:428:6: ( 'many-to-one' )
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:429:1: 'many-to-one'
                    {
                     before(grammarAccess.getXRelationAccess().getNameManyToOneKeyword_11_0_2()); 
                    match(input,13,FOLLOW_13_in_rule__XRelation__NameAlternatives_11_0886); 
                     after(grammarAccess.getXRelationAccess().getNameManyToOneKeyword_11_0_2()); 

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
    // $ANTLR end rule__XRelation__NameAlternatives_11_0


    // $ANTLR start rule__Model__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:443:1: rule__Model__Group__0 : ( ( rule__Model__ImportsAssignment_0 )* ) rule__Model__Group__1 ;
    public final void rule__Model__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:447:1: ( ( ( rule__Model__ImportsAssignment_0 )* ) rule__Model__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:448:1: ( ( rule__Model__ImportsAssignment_0 )* ) rule__Model__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:448:1: ( ( rule__Model__ImportsAssignment_0 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:449:1: ( rule__Model__ImportsAssignment_0 )*
            {
             before(grammarAccess.getModelAccess().getImportsAssignment_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:450:1: ( rule__Model__ImportsAssignment_0 )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==14) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:450:2: rule__Model__ImportsAssignment_0
            	    {
            	    pushFollow(FOLLOW_rule__Model__ImportsAssignment_0_in_rule__Model__Group__0922);
            	    rule__Model__ImportsAssignment_0();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

             after(grammarAccess.getModelAccess().getImportsAssignment_0()); 

            }

            pushFollow(FOLLOW_rule__Model__Group__1_in_rule__Model__Group__0932);
            rule__Model__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Model__Group__0


    // $ANTLR start rule__Model__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:461:1: rule__Model__Group__1 : ( ( rule__Model__ElementsAssignment_1 )* ) ;
    public final void rule__Model__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:465:1: ( ( ( rule__Model__ElementsAssignment_1 )* ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:466:1: ( ( rule__Model__ElementsAssignment_1 )* )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:466:1: ( ( rule__Model__ElementsAssignment_1 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:467:1: ( rule__Model__ElementsAssignment_1 )*
            {
             before(grammarAccess.getModelAccess().getElementsAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:468:1: ( rule__Model__ElementsAssignment_1 )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==15||LA5_0==21||LA5_0==25) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:468:2: rule__Model__ElementsAssignment_1
            	    {
            	    pushFollow(FOLLOW_rule__Model__ElementsAssignment_1_in_rule__Model__Group__1960);
            	    rule__Model__ElementsAssignment_1();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

             after(grammarAccess.getModelAccess().getElementsAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Model__Group__1


    // $ANTLR start rule__Import__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:482:1: rule__Import__Group__0 : ( 'import' ) rule__Import__Group__1 ;
    public final void rule__Import__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:486:1: ( ( 'import' ) rule__Import__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:487:1: ( 'import' ) rule__Import__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:487:1: ( 'import' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:488:1: 'import'
            {
             before(grammarAccess.getImportAccess().getImportKeyword_0()); 
            match(input,14,FOLLOW_14_in_rule__Import__Group__01000); 
             after(grammarAccess.getImportAccess().getImportKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__Import__Group__1_in_rule__Import__Group__01010);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:502:1: rule__Import__Group__1 : ( ( rule__Import__ImportURIAssignment_1 ) ) ;
    public final void rule__Import__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:506:1: ( ( ( rule__Import__ImportURIAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:507:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:507:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:508:1: ( rule__Import__ImportURIAssignment_1 )
            {
             before(grammarAccess.getImportAccess().getImportURIAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:509:1: ( rule__Import__ImportURIAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:509:2: rule__Import__ImportURIAssignment_1
            {
            pushFollow(FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__11038);
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


    // $ANTLR start rule__ArtifactType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:523:1: rule__ArtifactType__Group__0 : ( 'artifactType' ) rule__ArtifactType__Group__1 ;
    public final void rule__ArtifactType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:527:1: ( ( 'artifactType' ) rule__ArtifactType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:528:1: ( 'artifactType' ) rule__ArtifactType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:528:1: ( 'artifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:529:1: 'artifactType'
            {
             before(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_0()); 
            match(input,15,FOLLOW_15_in_rule__ArtifactType__Group__01077); 
             after(grammarAccess.getArtifactTypeAccess().getArtifactTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__1_in_rule__ArtifactType__Group__01087);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:543:1: rule__ArtifactType__Group__1 : ( ( rule__ArtifactType__NameAssignment_1 ) ) rule__ArtifactType__Group__2 ;
    public final void rule__ArtifactType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:547:1: ( ( ( rule__ArtifactType__NameAssignment_1 ) ) rule__ArtifactType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:548:1: ( ( rule__ArtifactType__NameAssignment_1 ) ) rule__ArtifactType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:548:1: ( ( rule__ArtifactType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:549:1: ( rule__ArtifactType__NameAssignment_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:550:1: ( rule__ArtifactType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:550:2: rule__ArtifactType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__NameAssignment_1_in_rule__ArtifactType__Group__11115);
            rule__ArtifactType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__2_in_rule__ArtifactType__Group__11124);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:561:1: rule__ArtifactType__Group__2 : ( ( rule__ArtifactType__Group_2__0 )? ) rule__ArtifactType__Group__3 ;
    public final void rule__ArtifactType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:565:1: ( ( ( rule__ArtifactType__Group_2__0 )? ) rule__ArtifactType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:566:1: ( ( rule__ArtifactType__Group_2__0 )? ) rule__ArtifactType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:566:1: ( ( rule__ArtifactType__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:567:1: ( rule__ArtifactType__Group_2__0 )?
            {
             before(grammarAccess.getArtifactTypeAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:568:1: ( rule__ArtifactType__Group_2__0 )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==18) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:568:2: rule__ArtifactType__Group_2__0
                    {
                    pushFollow(FOLLOW_rule__ArtifactType__Group_2__0_in_rule__ArtifactType__Group__21152);
                    rule__ArtifactType__Group_2__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getArtifactTypeAccess().getGroup_2()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__3_in_rule__ArtifactType__Group__21162);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:579:1: rule__ArtifactType__Group__3 : ( '{' ) rule__ArtifactType__Group__4 ;
    public final void rule__ArtifactType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:583:1: ( ( '{' ) rule__ArtifactType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:584:1: ( '{' ) rule__ArtifactType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:584:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:585:1: '{'
            {
             before(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_3()); 
            match(input,16,FOLLOW_16_in_rule__ArtifactType__Group__31191); 
             after(grammarAccess.getArtifactTypeAccess().getLeftCurlyBracketKeyword_3()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__4_in_rule__ArtifactType__Group__31201);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:599:1: rule__ArtifactType__Group__4 : ( ( rule__ArtifactType__AttributesAssignment_4 )* ) rule__ArtifactType__Group__5 ;
    public final void rule__ArtifactType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:603:1: ( ( ( rule__ArtifactType__AttributesAssignment_4 )* ) rule__ArtifactType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:604:1: ( ( rule__ArtifactType__AttributesAssignment_4 )* ) rule__ArtifactType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:604:1: ( ( rule__ArtifactType__AttributesAssignment_4 )* )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:605:1: ( rule__ArtifactType__AttributesAssignment_4 )*
            {
             before(grammarAccess.getArtifactTypeAccess().getAttributesAssignment_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:606:1: ( rule__ArtifactType__AttributesAssignment_4 )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>=19 && LA7_0<=20)) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:606:2: rule__ArtifactType__AttributesAssignment_4
            	    {
            	    pushFollow(FOLLOW_rule__ArtifactType__AttributesAssignment_4_in_rule__ArtifactType__Group__41229);
            	    rule__ArtifactType__AttributesAssignment_4();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

             after(grammarAccess.getArtifactTypeAccess().getAttributesAssignment_4()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group__5_in_rule__ArtifactType__Group__41239);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:617:1: rule__ArtifactType__Group__5 : ( '}' ) ;
    public final void rule__ArtifactType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:621:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:622:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:622:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:623:1: '}'
            {
             before(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_5()); 
            match(input,17,FOLLOW_17_in_rule__ArtifactType__Group__51268); 
             after(grammarAccess.getArtifactTypeAccess().getRightCurlyBracketKeyword_5()); 

            }


            }

        }
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


    // $ANTLR start rule__ArtifactType__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:648:1: rule__ArtifactType__Group_2__0 : ( 'extends' ) rule__ArtifactType__Group_2__1 ;
    public final void rule__ArtifactType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:652:1: ( ( 'extends' ) rule__ArtifactType__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:653:1: ( 'extends' ) rule__ArtifactType__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:653:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:654:1: 'extends'
            {
             before(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_2_0()); 
            match(input,18,FOLLOW_18_in_rule__ArtifactType__Group_2__01316); 
             after(grammarAccess.getArtifactTypeAccess().getExtendsKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__ArtifactType__Group_2__1_in_rule__ArtifactType__Group_2__01326);
            rule__ArtifactType__Group_2__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group_2__0


    // $ANTLR start rule__ArtifactType__Group_2__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:668:1: rule__ArtifactType__Group_2__1 : ( ( rule__ArtifactType__SuperEntityAssignment_2_1 ) ) ;
    public final void rule__ArtifactType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:672:1: ( ( ( rule__ArtifactType__SuperEntityAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:673:1: ( ( rule__ArtifactType__SuperEntityAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:673:1: ( ( rule__ArtifactType__SuperEntityAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:674:1: ( rule__ArtifactType__SuperEntityAssignment_2_1 )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperEntityAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:675:1: ( rule__ArtifactType__SuperEntityAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:675:2: rule__ArtifactType__SuperEntityAssignment_2_1
            {
            pushFollow(FOLLOW_rule__ArtifactType__SuperEntityAssignment_2_1_in_rule__ArtifactType__Group_2__11354);
            rule__ArtifactType__SuperEntityAssignment_2_1();
            _fsp--;


            }

             after(grammarAccess.getArtifactTypeAccess().getSuperEntityAssignment_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__Group_2__1


    // $ANTLR start rule__RelationTypeRef__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:689:1: rule__RelationTypeRef__Group__0 : ( 'relation' ) rule__RelationTypeRef__Group__1 ;
    public final void rule__RelationTypeRef__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:693:1: ( ( 'relation' ) rule__RelationTypeRef__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:694:1: ( 'relation' ) rule__RelationTypeRef__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:694:1: ( 'relation' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:695:1: 'relation'
            {
             before(grammarAccess.getRelationTypeRefAccess().getRelationKeyword_0()); 
            match(input,19,FOLLOW_19_in_rule__RelationTypeRef__Group__01393); 
             after(grammarAccess.getRelationTypeRefAccess().getRelationKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__RelationTypeRef__Group__1_in_rule__RelationTypeRef__Group__01403);
            rule__RelationTypeRef__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationTypeRef__Group__0


    // $ANTLR start rule__RelationTypeRef__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:709:1: rule__RelationTypeRef__Group__1 : ( ( rule__RelationTypeRef__TypeAssignment_1 ) ) ;
    public final void rule__RelationTypeRef__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:713:1: ( ( ( rule__RelationTypeRef__TypeAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:714:1: ( ( rule__RelationTypeRef__TypeAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:714:1: ( ( rule__RelationTypeRef__TypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:715:1: ( rule__RelationTypeRef__TypeAssignment_1 )
            {
             before(grammarAccess.getRelationTypeRefAccess().getTypeAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:716:1: ( rule__RelationTypeRef__TypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:716:2: rule__RelationTypeRef__TypeAssignment_1
            {
            pushFollow(FOLLOW_rule__RelationTypeRef__TypeAssignment_1_in_rule__RelationTypeRef__Group__11431);
            rule__RelationTypeRef__TypeAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeRefAccess().getTypeAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationTypeRef__Group__1


    // $ANTLR start rule__AttributeTypeRef__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:730:1: rule__AttributeTypeRef__Group__0 : ( 'attribute' ) rule__AttributeTypeRef__Group__1 ;
    public final void rule__AttributeTypeRef__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:734:1: ( ( 'attribute' ) rule__AttributeTypeRef__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:735:1: ( 'attribute' ) rule__AttributeTypeRef__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:735:1: ( 'attribute' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:736:1: 'attribute'
            {
             before(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0()); 
            match(input,20,FOLLOW_20_in_rule__AttributeTypeRef__Group__01470); 
             after(grammarAccess.getAttributeTypeRefAccess().getAttributeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeTypeRef__Group__1_in_rule__AttributeTypeRef__Group__01480);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:750:1: rule__AttributeTypeRef__Group__1 : ( ( rule__AttributeTypeRef__TypeAssignment_1 ) ) ;
    public final void rule__AttributeTypeRef__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:754:1: ( ( ( rule__AttributeTypeRef__TypeAssignment_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:755:1: ( ( rule__AttributeTypeRef__TypeAssignment_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:755:1: ( ( rule__AttributeTypeRef__TypeAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:756:1: ( rule__AttributeTypeRef__TypeAssignment_1 )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getTypeAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:757:1: ( rule__AttributeTypeRef__TypeAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:757:2: rule__AttributeTypeRef__TypeAssignment_1
            {
            pushFollow(FOLLOW_rule__AttributeTypeRef__TypeAssignment_1_in_rule__AttributeTypeRef__Group__11508);
            rule__AttributeTypeRef__TypeAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeRefAccess().getTypeAssignment_1()); 

            }


            }

        }
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:771:1: rule__AttributeType__Group__0 : ( 'attributeType' ) rule__AttributeType__Group__1 ;
    public final void rule__AttributeType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:775:1: ( ( 'attributeType' ) rule__AttributeType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:776:1: ( 'attributeType' ) rule__AttributeType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:776:1: ( 'attributeType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:777:1: 'attributeType'
            {
             before(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_0()); 
            match(input,21,FOLLOW_21_in_rule__AttributeType__Group__01547); 
             after(grammarAccess.getAttributeTypeAccess().getAttributeTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__1_in_rule__AttributeType__Group__01557);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:791:1: rule__AttributeType__Group__1 : ( ( rule__AttributeType__NameAssignment_1 ) ) rule__AttributeType__Group__2 ;
    public final void rule__AttributeType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:795:1: ( ( ( rule__AttributeType__NameAssignment_1 ) ) rule__AttributeType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:796:1: ( ( rule__AttributeType__NameAssignment_1 ) ) rule__AttributeType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:796:1: ( ( rule__AttributeType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:797:1: ( rule__AttributeType__NameAssignment_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:798:1: ( rule__AttributeType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:798:2: rule__AttributeType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__AttributeType__NameAssignment_1_in_rule__AttributeType__Group__11585);
            rule__AttributeType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__2_in_rule__AttributeType__Group__11594);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:809:1: rule__AttributeType__Group__2 : ( ( rule__AttributeType__Group_2__0 )? ) rule__AttributeType__Group__3 ;
    public final void rule__AttributeType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:813:1: ( ( ( rule__AttributeType__Group_2__0 )? ) rule__AttributeType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:814:1: ( ( rule__AttributeType__Group_2__0 )? ) rule__AttributeType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:814:1: ( ( rule__AttributeType__Group_2__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:815:1: ( rule__AttributeType__Group_2__0 )?
            {
             before(grammarAccess.getAttributeTypeAccess().getGroup_2()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:816:1: ( rule__AttributeType__Group_2__0 )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==18) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:816:2: rule__AttributeType__Group_2__0
                    {
                    pushFollow(FOLLOW_rule__AttributeType__Group_2__0_in_rule__AttributeType__Group__21622);
                    rule__AttributeType__Group_2__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeAccess().getGroup_2()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__3_in_rule__AttributeType__Group__21632);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:827:1: rule__AttributeType__Group__3 : ( '{' ) rule__AttributeType__Group__4 ;
    public final void rule__AttributeType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:831:1: ( ( '{' ) rule__AttributeType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:832:1: ( '{' ) rule__AttributeType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:832:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:833:1: '{'
            {
             before(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_3()); 
            match(input,16,FOLLOW_16_in_rule__AttributeType__Group__31661); 
             after(grammarAccess.getAttributeTypeAccess().getLeftCurlyBracketKeyword_3()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__4_in_rule__AttributeType__Group__31671);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:847:1: rule__AttributeType__Group__4 : ( ( rule__AttributeType__AttributesAssignment_4 ) ) rule__AttributeType__Group__5 ;
    public final void rule__AttributeType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:851:1: ( ( ( rule__AttributeType__AttributesAssignment_4 ) ) rule__AttributeType__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:852:1: ( ( rule__AttributeType__AttributesAssignment_4 ) ) rule__AttributeType__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:852:1: ( ( rule__AttributeType__AttributesAssignment_4 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:853:1: ( rule__AttributeType__AttributesAssignment_4 )
            {
             before(grammarAccess.getAttributeTypeAccess().getAttributesAssignment_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:854:1: ( rule__AttributeType__AttributesAssignment_4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:854:2: rule__AttributeType__AttributesAssignment_4
            {
            pushFollow(FOLLOW_rule__AttributeType__AttributesAssignment_4_in_rule__AttributeType__Group__41699);
            rule__AttributeType__AttributesAssignment_4();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getAttributesAssignment_4()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group__5_in_rule__AttributeType__Group__41708);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:865:1: rule__AttributeType__Group__5 : ( '}' ) ;
    public final void rule__AttributeType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:869:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:870:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:870:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:871:1: '}'
            {
             before(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_5()); 
            match(input,17,FOLLOW_17_in_rule__AttributeType__Group__51737); 
             after(grammarAccess.getAttributeTypeAccess().getRightCurlyBracketKeyword_5()); 

            }


            }

        }
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


    // $ANTLR start rule__AttributeType__Group_2__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:896:1: rule__AttributeType__Group_2__0 : ( 'extends' ) rule__AttributeType__Group_2__1 ;
    public final void rule__AttributeType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:900:1: ( ( 'extends' ) rule__AttributeType__Group_2__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:901:1: ( 'extends' ) rule__AttributeType__Group_2__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:901:1: ( 'extends' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:902:1: 'extends'
            {
             before(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_2_0()); 
            match(input,18,FOLLOW_18_in_rule__AttributeType__Group_2__01785); 
             after(grammarAccess.getAttributeTypeAccess().getExtendsKeyword_2_0()); 

            }

            pushFollow(FOLLOW_rule__AttributeType__Group_2__1_in_rule__AttributeType__Group_2__01795);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:916:1: rule__AttributeType__Group_2__1 : ( ( rule__AttributeType__SuperEntityAssignment_2_1 ) ) ;
    public final void rule__AttributeType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:920:1: ( ( ( rule__AttributeType__SuperEntityAssignment_2_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:921:1: ( ( rule__AttributeType__SuperEntityAssignment_2_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:921:1: ( ( rule__AttributeType__SuperEntityAssignment_2_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:922:1: ( rule__AttributeType__SuperEntityAssignment_2_1 )
            {
             before(grammarAccess.getAttributeTypeAccess().getSuperEntityAssignment_2_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:923:1: ( rule__AttributeType__SuperEntityAssignment_2_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:923:2: rule__AttributeType__SuperEntityAssignment_2_1
            {
            pushFollow(FOLLOW_rule__AttributeType__SuperEntityAssignment_2_1_in_rule__AttributeType__Group_2__11823);
            rule__AttributeType__SuperEntityAssignment_2_1();
            _fsp--;


            }

             after(grammarAccess.getAttributeTypeAccess().getSuperEntityAssignment_2_1()); 

            }


            }

        }
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


    // $ANTLR start rule__XAttribute__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:937:1: rule__XAttribute__Group__0 : ( 'dataProvider' ) rule__XAttribute__Group__1 ;
    public final void rule__XAttribute__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:941:1: ( ( 'dataProvider' ) rule__XAttribute__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:942:1: ( 'dataProvider' ) rule__XAttribute__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:942:1: ( 'dataProvider' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:943:1: 'dataProvider'
            {
             before(grammarAccess.getXAttributeAccess().getDataProviderKeyword_0()); 
            match(input,22,FOLLOW_22_in_rule__XAttribute__Group__01862); 
             after(grammarAccess.getXAttributeAccess().getDataProviderKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__XAttribute__Group__1_in_rule__XAttribute__Group__01872);
            rule__XAttribute__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttribute__Group__0


    // $ANTLR start rule__XAttribute__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:957:1: rule__XAttribute__Group__1 : ( ( rule__XAttribute__NameAssignment_1 ) ) rule__XAttribute__Group__2 ;
    public final void rule__XAttribute__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:961:1: ( ( ( rule__XAttribute__NameAssignment_1 ) ) rule__XAttribute__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:962:1: ( ( rule__XAttribute__NameAssignment_1 ) ) rule__XAttribute__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:962:1: ( ( rule__XAttribute__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:963:1: ( rule__XAttribute__NameAssignment_1 )
            {
             before(grammarAccess.getXAttributeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:964:1: ( rule__XAttribute__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:964:2: rule__XAttribute__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__XAttribute__NameAssignment_1_in_rule__XAttribute__Group__11900);
            rule__XAttribute__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getXAttributeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__XAttribute__Group__2_in_rule__XAttribute__Group__11909);
            rule__XAttribute__Group__2();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttribute__Group__1


    // $ANTLR start rule__XAttribute__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:975:1: rule__XAttribute__Group__2 : ( 'taggerId' ) rule__XAttribute__Group__3 ;
    public final void rule__XAttribute__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:979:1: ( ( 'taggerId' ) rule__XAttribute__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:980:1: ( 'taggerId' ) rule__XAttribute__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:980:1: ( 'taggerId' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:981:1: 'taggerId'
            {
             before(grammarAccess.getXAttributeAccess().getTaggerIdKeyword_2()); 
            match(input,23,FOLLOW_23_in_rule__XAttribute__Group__21938); 
             after(grammarAccess.getXAttributeAccess().getTaggerIdKeyword_2()); 

            }

            pushFollow(FOLLOW_rule__XAttribute__Group__3_in_rule__XAttribute__Group__21948);
            rule__XAttribute__Group__3();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttribute__Group__2


    // $ANTLR start rule__XAttribute__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:995:1: rule__XAttribute__Group__3 : ( ( rule__XAttribute__NameAssignment_3 ) ) rule__XAttribute__Group__4 ;
    public final void rule__XAttribute__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:999:1: ( ( ( rule__XAttribute__NameAssignment_3 ) ) rule__XAttribute__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1000:1: ( ( rule__XAttribute__NameAssignment_3 ) ) rule__XAttribute__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1000:1: ( ( rule__XAttribute__NameAssignment_3 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1001:1: ( rule__XAttribute__NameAssignment_3 )
            {
             before(grammarAccess.getXAttributeAccess().getNameAssignment_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1002:1: ( rule__XAttribute__NameAssignment_3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1002:2: rule__XAttribute__NameAssignment_3
            {
            pushFollow(FOLLOW_rule__XAttribute__NameAssignment_3_in_rule__XAttribute__Group__31976);
            rule__XAttribute__NameAssignment_3();
            _fsp--;


            }

             after(grammarAccess.getXAttributeAccess().getNameAssignment_3()); 

            }

            pushFollow(FOLLOW_rule__XAttribute__Group__4_in_rule__XAttribute__Group__31985);
            rule__XAttribute__Group__4();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttribute__Group__3


    // $ANTLR start rule__XAttribute__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1013:1: rule__XAttribute__Group__4 : ( ( rule__XAttribute__Group_4__0 )? ) ;
    public final void rule__XAttribute__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1017:1: ( ( ( rule__XAttribute__Group_4__0 )? ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1018:1: ( ( rule__XAttribute__Group_4__0 )? )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1018:1: ( ( rule__XAttribute__Group_4__0 )? )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1019:1: ( rule__XAttribute__Group_4__0 )?
            {
             before(grammarAccess.getXAttributeAccess().getGroup_4()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1020:1: ( rule__XAttribute__Group_4__0 )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==24) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1020:2: rule__XAttribute__Group_4__0
                    {
                    pushFollow(FOLLOW_rule__XAttribute__Group_4__0_in_rule__XAttribute__Group__42013);
                    rule__XAttribute__Group_4__0();
                    _fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXAttributeAccess().getGroup_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttribute__Group__4


    // $ANTLR start rule__XAttribute__Group_4__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1040:1: rule__XAttribute__Group_4__0 : ( 'defaultValue' ) rule__XAttribute__Group_4__1 ;
    public final void rule__XAttribute__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1044:1: ( ( 'defaultValue' ) rule__XAttribute__Group_4__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1045:1: ( 'defaultValue' ) rule__XAttribute__Group_4__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1045:1: ( 'defaultValue' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1046:1: 'defaultValue'
            {
             before(grammarAccess.getXAttributeAccess().getDefaultValueKeyword_4_0()); 
            match(input,24,FOLLOW_24_in_rule__XAttribute__Group_4__02059); 
             after(grammarAccess.getXAttributeAccess().getDefaultValueKeyword_4_0()); 

            }

            pushFollow(FOLLOW_rule__XAttribute__Group_4__1_in_rule__XAttribute__Group_4__02069);
            rule__XAttribute__Group_4__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttribute__Group_4__0


    // $ANTLR start rule__XAttribute__Group_4__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1060:1: rule__XAttribute__Group_4__1 : ( ( rule__XAttribute__NameAssignment_4_1 ) ) ;
    public final void rule__XAttribute__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1064:1: ( ( ( rule__XAttribute__NameAssignment_4_1 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1065:1: ( ( rule__XAttribute__NameAssignment_4_1 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1065:1: ( ( rule__XAttribute__NameAssignment_4_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1066:1: ( rule__XAttribute__NameAssignment_4_1 )
            {
             before(grammarAccess.getXAttributeAccess().getNameAssignment_4_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1067:1: ( rule__XAttribute__NameAssignment_4_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1067:2: rule__XAttribute__NameAssignment_4_1
            {
            pushFollow(FOLLOW_rule__XAttribute__NameAssignment_4_1_in_rule__XAttribute__Group_4__12097);
            rule__XAttribute__NameAssignment_4_1();
            _fsp--;


            }

             after(grammarAccess.getXAttributeAccess().getNameAssignment_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttribute__Group_4__1


    // $ANTLR start rule__RelationType__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1081:1: rule__RelationType__Group__0 : ( 'relationType' ) rule__RelationType__Group__1 ;
    public final void rule__RelationType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1085:1: ( ( 'relationType' ) rule__RelationType__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1086:1: ( 'relationType' ) rule__RelationType__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1086:1: ( 'relationType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1087:1: 'relationType'
            {
             before(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0()); 
            match(input,25,FOLLOW_25_in_rule__RelationType__Group__02136); 
             after(grammarAccess.getRelationTypeAccess().getRelationTypeKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__1_in_rule__RelationType__Group__02146);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1101:1: rule__RelationType__Group__1 : ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2 ;
    public final void rule__RelationType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1105:1: ( ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1106:1: ( ( rule__RelationType__NameAssignment_1 ) ) rule__RelationType__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1106:1: ( ( rule__RelationType__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1107:1: ( rule__RelationType__NameAssignment_1 )
            {
             before(grammarAccess.getRelationTypeAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1108:1: ( rule__RelationType__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1108:2: rule__RelationType__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__RelationType__NameAssignment_1_in_rule__RelationType__Group__12174);
            rule__RelationType__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__2_in_rule__RelationType__Group__12183);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1119:1: rule__RelationType__Group__2 : ( '{' ) rule__RelationType__Group__3 ;
    public final void rule__RelationType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1123:1: ( ( '{' ) rule__RelationType__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1124:1: ( '{' ) rule__RelationType__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1124:1: ( '{' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1125:1: '{'
            {
             before(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,16,FOLLOW_16_in_rule__RelationType__Group__22212); 
             after(grammarAccess.getRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__3_in_rule__RelationType__Group__22222);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1139:1: rule__RelationType__Group__3 : ( ( rule__RelationType__AttributesAssignment_3 ) ) rule__RelationType__Group__4 ;
    public final void rule__RelationType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1143:1: ( ( ( rule__RelationType__AttributesAssignment_3 ) ) rule__RelationType__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1144:1: ( ( rule__RelationType__AttributesAssignment_3 ) ) rule__RelationType__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1144:1: ( ( rule__RelationType__AttributesAssignment_3 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1145:1: ( rule__RelationType__AttributesAssignment_3 )
            {
             before(grammarAccess.getRelationTypeAccess().getAttributesAssignment_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1146:1: ( rule__RelationType__AttributesAssignment_3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1146:2: rule__RelationType__AttributesAssignment_3
            {
            pushFollow(FOLLOW_rule__RelationType__AttributesAssignment_3_in_rule__RelationType__Group__32250);
            rule__RelationType__AttributesAssignment_3();
            _fsp--;


            }

             after(grammarAccess.getRelationTypeAccess().getAttributesAssignment_3()); 

            }

            pushFollow(FOLLOW_rule__RelationType__Group__4_in_rule__RelationType__Group__32259);
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
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1157:1: rule__RelationType__Group__4 : ( '}' ) ;
    public final void rule__RelationType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1161:1: ( ( '}' ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1162:1: ( '}' )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1162:1: ( '}' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1163:1: '}'
            {
             before(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_4()); 
            match(input,17,FOLLOW_17_in_rule__RelationType__Group__42288); 
             after(grammarAccess.getRelationTypeAccess().getRightCurlyBracketKeyword_4()); 

            }


            }

        }
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


    // $ANTLR start rule__XRelation__Group__0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1186:1: rule__XRelation__Group__0 : ( 'sideAName' ) rule__XRelation__Group__1 ;
    public final void rule__XRelation__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1190:1: ( ( 'sideAName' ) rule__XRelation__Group__1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1191:1: ( 'sideAName' ) rule__XRelation__Group__1
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1191:1: ( 'sideAName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1192:1: 'sideAName'
            {
             before(grammarAccess.getXRelationAccess().getSideANameKeyword_0()); 
            match(input,26,FOLLOW_26_in_rule__XRelation__Group__02334); 
             after(grammarAccess.getXRelationAccess().getSideANameKeyword_0()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__1_in_rule__XRelation__Group__02344);
            rule__XRelation__Group__1();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__0


    // $ANTLR start rule__XRelation__Group__1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1206:1: rule__XRelation__Group__1 : ( ( rule__XRelation__NameAssignment_1 ) ) rule__XRelation__Group__2 ;
    public final void rule__XRelation__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1210:1: ( ( ( rule__XRelation__NameAssignment_1 ) ) rule__XRelation__Group__2 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1211:1: ( ( rule__XRelation__NameAssignment_1 ) ) rule__XRelation__Group__2
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1211:1: ( ( rule__XRelation__NameAssignment_1 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1212:1: ( rule__XRelation__NameAssignment_1 )
            {
             before(grammarAccess.getXRelationAccess().getNameAssignment_1()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1213:1: ( rule__XRelation__NameAssignment_1 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1213:2: rule__XRelation__NameAssignment_1
            {
            pushFollow(FOLLOW_rule__XRelation__NameAssignment_1_in_rule__XRelation__Group__12372);
            rule__XRelation__NameAssignment_1();
            _fsp--;


            }

             after(grammarAccess.getXRelationAccess().getNameAssignment_1()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__2_in_rule__XRelation__Group__12381);
            rule__XRelation__Group__2();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__1


    // $ANTLR start rule__XRelation__Group__2
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1224:1: rule__XRelation__Group__2 : ( 'sideAArtifactType' ) rule__XRelation__Group__3 ;
    public final void rule__XRelation__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1228:1: ( ( 'sideAArtifactType' ) rule__XRelation__Group__3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1229:1: ( 'sideAArtifactType' ) rule__XRelation__Group__3
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1229:1: ( 'sideAArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1230:1: 'sideAArtifactType'
            {
             before(grammarAccess.getXRelationAccess().getSideAArtifactTypeKeyword_2()); 
            match(input,27,FOLLOW_27_in_rule__XRelation__Group__22410); 
             after(grammarAccess.getXRelationAccess().getSideAArtifactTypeKeyword_2()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__3_in_rule__XRelation__Group__22420);
            rule__XRelation__Group__3();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__2


    // $ANTLR start rule__XRelation__Group__3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1244:1: rule__XRelation__Group__3 : ( ( rule__XRelation__TypeAssignment_3 ) ) rule__XRelation__Group__4 ;
    public final void rule__XRelation__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1248:1: ( ( ( rule__XRelation__TypeAssignment_3 ) ) rule__XRelation__Group__4 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1249:1: ( ( rule__XRelation__TypeAssignment_3 ) ) rule__XRelation__Group__4
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1249:1: ( ( rule__XRelation__TypeAssignment_3 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1250:1: ( rule__XRelation__TypeAssignment_3 )
            {
             before(grammarAccess.getXRelationAccess().getTypeAssignment_3()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1251:1: ( rule__XRelation__TypeAssignment_3 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1251:2: rule__XRelation__TypeAssignment_3
            {
            pushFollow(FOLLOW_rule__XRelation__TypeAssignment_3_in_rule__XRelation__Group__32448);
            rule__XRelation__TypeAssignment_3();
            _fsp--;


            }

             after(grammarAccess.getXRelationAccess().getTypeAssignment_3()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__4_in_rule__XRelation__Group__32457);
            rule__XRelation__Group__4();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__3


    // $ANTLR start rule__XRelation__Group__4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1262:1: rule__XRelation__Group__4 : ( 'sideBName' ) rule__XRelation__Group__5 ;
    public final void rule__XRelation__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1266:1: ( ( 'sideBName' ) rule__XRelation__Group__5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1267:1: ( 'sideBName' ) rule__XRelation__Group__5
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1267:1: ( 'sideBName' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1268:1: 'sideBName'
            {
             before(grammarAccess.getXRelationAccess().getSideBNameKeyword_4()); 
            match(input,28,FOLLOW_28_in_rule__XRelation__Group__42486); 
             after(grammarAccess.getXRelationAccess().getSideBNameKeyword_4()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__5_in_rule__XRelation__Group__42496);
            rule__XRelation__Group__5();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__4


    // $ANTLR start rule__XRelation__Group__5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1282:1: rule__XRelation__Group__5 : ( ( rule__XRelation__NameAssignment_5 ) ) rule__XRelation__Group__6 ;
    public final void rule__XRelation__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1286:1: ( ( ( rule__XRelation__NameAssignment_5 ) ) rule__XRelation__Group__6 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1287:1: ( ( rule__XRelation__NameAssignment_5 ) ) rule__XRelation__Group__6
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1287:1: ( ( rule__XRelation__NameAssignment_5 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1288:1: ( rule__XRelation__NameAssignment_5 )
            {
             before(grammarAccess.getXRelationAccess().getNameAssignment_5()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1289:1: ( rule__XRelation__NameAssignment_5 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1289:2: rule__XRelation__NameAssignment_5
            {
            pushFollow(FOLLOW_rule__XRelation__NameAssignment_5_in_rule__XRelation__Group__52524);
            rule__XRelation__NameAssignment_5();
            _fsp--;


            }

             after(grammarAccess.getXRelationAccess().getNameAssignment_5()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__6_in_rule__XRelation__Group__52533);
            rule__XRelation__Group__6();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__5


    // $ANTLR start rule__XRelation__Group__6
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1300:1: rule__XRelation__Group__6 : ( 'sideBArtifactType' ) rule__XRelation__Group__7 ;
    public final void rule__XRelation__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1304:1: ( ( 'sideBArtifactType' ) rule__XRelation__Group__7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1305:1: ( 'sideBArtifactType' ) rule__XRelation__Group__7
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1305:1: ( 'sideBArtifactType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1306:1: 'sideBArtifactType'
            {
             before(grammarAccess.getXRelationAccess().getSideBArtifactTypeKeyword_6()); 
            match(input,29,FOLLOW_29_in_rule__XRelation__Group__62562); 
             after(grammarAccess.getXRelationAccess().getSideBArtifactTypeKeyword_6()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__7_in_rule__XRelation__Group__62572);
            rule__XRelation__Group__7();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__6


    // $ANTLR start rule__XRelation__Group__7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1320:1: rule__XRelation__Group__7 : ( ( rule__XRelation__TypeAssignment_7 ) ) rule__XRelation__Group__8 ;
    public final void rule__XRelation__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1324:1: ( ( ( rule__XRelation__TypeAssignment_7 ) ) rule__XRelation__Group__8 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1325:1: ( ( rule__XRelation__TypeAssignment_7 ) ) rule__XRelation__Group__8
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1325:1: ( ( rule__XRelation__TypeAssignment_7 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1326:1: ( rule__XRelation__TypeAssignment_7 )
            {
             before(grammarAccess.getXRelationAccess().getTypeAssignment_7()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1327:1: ( rule__XRelation__TypeAssignment_7 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1327:2: rule__XRelation__TypeAssignment_7
            {
            pushFollow(FOLLOW_rule__XRelation__TypeAssignment_7_in_rule__XRelation__Group__72600);
            rule__XRelation__TypeAssignment_7();
            _fsp--;


            }

             after(grammarAccess.getXRelationAccess().getTypeAssignment_7()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__8_in_rule__XRelation__Group__72609);
            rule__XRelation__Group__8();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__7


    // $ANTLR start rule__XRelation__Group__8
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1338:1: rule__XRelation__Group__8 : ( 'defaultOrderType' ) rule__XRelation__Group__9 ;
    public final void rule__XRelation__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1342:1: ( ( 'defaultOrderType' ) rule__XRelation__Group__9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1343:1: ( 'defaultOrderType' ) rule__XRelation__Group__9
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1343:1: ( 'defaultOrderType' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1344:1: 'defaultOrderType'
            {
             before(grammarAccess.getXRelationAccess().getDefaultOrderTypeKeyword_8()); 
            match(input,30,FOLLOW_30_in_rule__XRelation__Group__82638); 
             after(grammarAccess.getXRelationAccess().getDefaultOrderTypeKeyword_8()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__9_in_rule__XRelation__Group__82648);
            rule__XRelation__Group__9();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__8


    // $ANTLR start rule__XRelation__Group__9
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1358:1: rule__XRelation__Group__9 : ( ( rule__XRelation__NameAssignment_9 ) ) rule__XRelation__Group__10 ;
    public final void rule__XRelation__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1362:1: ( ( ( rule__XRelation__NameAssignment_9 ) ) rule__XRelation__Group__10 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1363:1: ( ( rule__XRelation__NameAssignment_9 ) ) rule__XRelation__Group__10
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1363:1: ( ( rule__XRelation__NameAssignment_9 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1364:1: ( rule__XRelation__NameAssignment_9 )
            {
             before(grammarAccess.getXRelationAccess().getNameAssignment_9()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1365:1: ( rule__XRelation__NameAssignment_9 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1365:2: rule__XRelation__NameAssignment_9
            {
            pushFollow(FOLLOW_rule__XRelation__NameAssignment_9_in_rule__XRelation__Group__92676);
            rule__XRelation__NameAssignment_9();
            _fsp--;


            }

             after(grammarAccess.getXRelationAccess().getNameAssignment_9()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__10_in_rule__XRelation__Group__92685);
            rule__XRelation__Group__10();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__9


    // $ANTLR start rule__XRelation__Group__10
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1376:1: rule__XRelation__Group__10 : ( 'multiplicity' ) rule__XRelation__Group__11 ;
    public final void rule__XRelation__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1380:1: ( ( 'multiplicity' ) rule__XRelation__Group__11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1381:1: ( 'multiplicity' ) rule__XRelation__Group__11
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1381:1: ( 'multiplicity' )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1382:1: 'multiplicity'
            {
             before(grammarAccess.getXRelationAccess().getMultiplicityKeyword_10()); 
            match(input,31,FOLLOW_31_in_rule__XRelation__Group__102714); 
             after(grammarAccess.getXRelationAccess().getMultiplicityKeyword_10()); 

            }

            pushFollow(FOLLOW_rule__XRelation__Group__11_in_rule__XRelation__Group__102724);
            rule__XRelation__Group__11();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__10


    // $ANTLR start rule__XRelation__Group__11
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1396:1: rule__XRelation__Group__11 : ( ( rule__XRelation__NameAssignment_11 ) ) ;
    public final void rule__XRelation__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1400:1: ( ( ( rule__XRelation__NameAssignment_11 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1401:1: ( ( rule__XRelation__NameAssignment_11 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1401:1: ( ( rule__XRelation__NameAssignment_11 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1402:1: ( rule__XRelation__NameAssignment_11 )
            {
             before(grammarAccess.getXRelationAccess().getNameAssignment_11()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1403:1: ( rule__XRelation__NameAssignment_11 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1403:2: rule__XRelation__NameAssignment_11
            {
            pushFollow(FOLLOW_rule__XRelation__NameAssignment_11_in_rule__XRelation__Group__112752);
            rule__XRelation__NameAssignment_11();
            _fsp--;


            }

             after(grammarAccess.getXRelationAccess().getNameAssignment_11()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__Group__11


    // $ANTLR start rule__Model__ImportsAssignment_0
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1437:1: rule__Model__ImportsAssignment_0 : ( ruleImport ) ;
    public final void rule__Model__ImportsAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1441:1: ( ( ruleImport ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1442:1: ( ruleImport )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1442:1: ( ruleImport )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1443:1: ruleImport
            {
             before(grammarAccess.getModelAccess().getImportsImportParserRuleCall_0_0()); 
            pushFollow(FOLLOW_ruleImport_in_rule__Model__ImportsAssignment_02810);
            ruleImport();
            _fsp--;

             after(grammarAccess.getModelAccess().getImportsImportParserRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Model__ImportsAssignment_0


    // $ANTLR start rule__Model__ElementsAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1452:1: rule__Model__ElementsAssignment_1 : ( ruleType ) ;
    public final void rule__Model__ElementsAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1456:1: ( ( ruleType ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1457:1: ( ruleType )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1457:1: ( ruleType )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1458:1: ruleType
            {
             before(grammarAccess.getModelAccess().getElementsTypeParserRuleCall_1_0()); 
            pushFollow(FOLLOW_ruleType_in_rule__Model__ElementsAssignment_12841);
            ruleType();
            _fsp--;

             after(grammarAccess.getModelAccess().getElementsTypeParserRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__Model__ElementsAssignment_1


    // $ANTLR start rule__Import__ImportURIAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1467:1: rule__Import__ImportURIAssignment_1 : ( RULE_STRING ) ;
    public final void rule__Import__ImportURIAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1471:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1472:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1472:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1473:1: RULE_STRING
            {
             before(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_12872); 
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


    // $ANTLR start rule__ArtifactType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1482:1: rule__ArtifactType__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__ArtifactType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1486:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1487:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1487:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1488:1: RULE_ID
            {
             before(grammarAccess.getArtifactTypeAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__ArtifactType__NameAssignment_12903); 
             after(grammarAccess.getArtifactTypeAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__NameAssignment_1


    // $ANTLR start rule__ArtifactType__SuperEntityAssignment_2_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1497:1: rule__ArtifactType__SuperEntityAssignment_2_1 : ( ( RULE_ID ) ) ;
    public final void rule__ArtifactType__SuperEntityAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1501:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1502:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1502:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1503:1: ( RULE_ID )
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperEntityArtifactTypeCrossReference_2_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1504:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1505:1: RULE_ID
            {
             before(grammarAccess.getArtifactTypeAccess().getSuperEntityArtifactTypeIDTerminalRuleCall_2_1_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__ArtifactType__SuperEntityAssignment_2_12938); 
             after(grammarAccess.getArtifactTypeAccess().getSuperEntityArtifactTypeIDTerminalRuleCall_2_1_0_1()); 

            }

             after(grammarAccess.getArtifactTypeAccess().getSuperEntityArtifactTypeCrossReference_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__SuperEntityAssignment_2_1


    // $ANTLR start rule__ArtifactType__AttributesAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1516:1: rule__ArtifactType__AttributesAssignment_4 : ( ruleXRef ) ;
    public final void rule__ArtifactType__AttributesAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1520:1: ( ( ruleXRef ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1521:1: ( ruleXRef )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1521:1: ( ruleXRef )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1522:1: ruleXRef
            {
             before(grammarAccess.getArtifactTypeAccess().getAttributesXRefParserRuleCall_4_0()); 
            pushFollow(FOLLOW_ruleXRef_in_rule__ArtifactType__AttributesAssignment_42973);
            ruleXRef();
            _fsp--;

             after(grammarAccess.getArtifactTypeAccess().getAttributesXRefParserRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__ArtifactType__AttributesAssignment_4


    // $ANTLR start rule__RelationTypeRef__TypeAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1531:1: rule__RelationTypeRef__TypeAssignment_1 : ( ( RULE_ID ) ) ;
    public final void rule__RelationTypeRef__TypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1535:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1536:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1536:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1537:1: ( RULE_ID )
            {
             before(grammarAccess.getRelationTypeRefAccess().getTypeRelationTypeCrossReference_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1538:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1539:1: RULE_ID
            {
             before(grammarAccess.getRelationTypeRefAccess().getTypeRelationTypeIDTerminalRuleCall_1_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__RelationTypeRef__TypeAssignment_13008); 
             after(grammarAccess.getRelationTypeRefAccess().getTypeRelationTypeIDTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getRelationTypeRefAccess().getTypeRelationTypeCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationTypeRef__TypeAssignment_1


    // $ANTLR start rule__AttributeTypeRef__TypeAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1550:1: rule__AttributeTypeRef__TypeAssignment_1 : ( ( RULE_ID ) ) ;
    public final void rule__AttributeTypeRef__TypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1554:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1555:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1555:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1556:1: ( RULE_ID )
            {
             before(grammarAccess.getAttributeTypeRefAccess().getTypeAttributeTypeCrossReference_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1557:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1558:1: RULE_ID
            {
             before(grammarAccess.getAttributeTypeRefAccess().getTypeAttributeTypeIDTerminalRuleCall_1_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__AttributeTypeRef__TypeAssignment_13047); 
             after(grammarAccess.getAttributeTypeRefAccess().getTypeAttributeTypeIDTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getAttributeTypeRefAccess().getTypeAttributeTypeCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeTypeRef__TypeAssignment_1


    // $ANTLR start rule__AttributeType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1569:1: rule__AttributeType__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__AttributeType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1573:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1574:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1574:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1575:1: RULE_ID
            {
             before(grammarAccess.getAttributeTypeAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__AttributeType__NameAssignment_13082); 
             after(grammarAccess.getAttributeTypeAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
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


    // $ANTLR start rule__AttributeType__SuperEntityAssignment_2_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1584:1: rule__AttributeType__SuperEntityAssignment_2_1 : ( ( RULE_ID ) ) ;
    public final void rule__AttributeType__SuperEntityAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1588:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1589:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1589:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1590:1: ( RULE_ID )
            {
             before(grammarAccess.getAttributeTypeAccess().getSuperEntityAttributeTypeCrossReference_2_1_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1591:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1592:1: RULE_ID
            {
             before(grammarAccess.getAttributeTypeAccess().getSuperEntityAttributeTypeIDTerminalRuleCall_2_1_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__AttributeType__SuperEntityAssignment_2_13117); 
             after(grammarAccess.getAttributeTypeAccess().getSuperEntityAttributeTypeIDTerminalRuleCall_2_1_0_1()); 

            }

             after(grammarAccess.getAttributeTypeAccess().getSuperEntityAttributeTypeCrossReference_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__SuperEntityAssignment_2_1


    // $ANTLR start rule__AttributeType__AttributesAssignment_4
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1603:1: rule__AttributeType__AttributesAssignment_4 : ( ruleXAttribute ) ;
    public final void rule__AttributeType__AttributesAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1607:1: ( ( ruleXAttribute ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1608:1: ( ruleXAttribute )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1608:1: ( ruleXAttribute )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1609:1: ruleXAttribute
            {
             before(grammarAccess.getAttributeTypeAccess().getAttributesXAttributeParserRuleCall_4_0()); 
            pushFollow(FOLLOW_ruleXAttribute_in_rule__AttributeType__AttributesAssignment_43152);
            ruleXAttribute();
            _fsp--;

             after(grammarAccess.getAttributeTypeAccess().getAttributesXAttributeParserRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__AttributeType__AttributesAssignment_4


    // $ANTLR start rule__XAttribute__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1618:1: rule__XAttribute__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__XAttribute__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1622:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1623:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1623:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1624:1: RULE_ID
            {
             before(grammarAccess.getXAttributeAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__XAttribute__NameAssignment_13183); 
             after(grammarAccess.getXAttributeAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttribute__NameAssignment_1


    // $ANTLR start rule__XAttribute__NameAssignment_3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1633:1: rule__XAttribute__NameAssignment_3 : ( RULE_ID ) ;
    public final void rule__XAttribute__NameAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1637:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1638:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1638:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1639:1: RULE_ID
            {
             before(grammarAccess.getXAttributeAccess().getNameIDTerminalRuleCall_3_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__XAttribute__NameAssignment_33214); 
             after(grammarAccess.getXAttributeAccess().getNameIDTerminalRuleCall_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttribute__NameAssignment_3


    // $ANTLR start rule__XAttribute__NameAssignment_4_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1648:1: rule__XAttribute__NameAssignment_4_1 : ( RULE_STRING ) ;
    public final void rule__XAttribute__NameAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1652:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1653:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1653:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1654:1: RULE_STRING
            {
             before(grammarAccess.getXAttributeAccess().getNameSTRINGTerminalRuleCall_4_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__XAttribute__NameAssignment_4_13245); 
             after(grammarAccess.getXAttributeAccess().getNameSTRINGTerminalRuleCall_4_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XAttribute__NameAssignment_4_1


    // $ANTLR start rule__RelationType__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1663:1: rule__RelationType__NameAssignment_1 : ( RULE_ID ) ;
    public final void rule__RelationType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1667:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1668:1: ( RULE_ID )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1668:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1669:1: RULE_ID
            {
             before(grammarAccess.getRelationTypeAccess().getNameIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__RelationType__NameAssignment_13276); 
             after(grammarAccess.getRelationTypeAccess().getNameIDTerminalRuleCall_1_0()); 

            }


            }

        }
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


    // $ANTLR start rule__RelationType__AttributesAssignment_3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1678:1: rule__RelationType__AttributesAssignment_3 : ( ruleXRelation ) ;
    public final void rule__RelationType__AttributesAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1682:1: ( ( ruleXRelation ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1683:1: ( ruleXRelation )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1683:1: ( ruleXRelation )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1684:1: ruleXRelation
            {
             before(grammarAccess.getRelationTypeAccess().getAttributesXRelationParserRuleCall_3_0()); 
            pushFollow(FOLLOW_ruleXRelation_in_rule__RelationType__AttributesAssignment_33307);
            ruleXRelation();
            _fsp--;

             after(grammarAccess.getRelationTypeAccess().getAttributesXRelationParserRuleCall_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__RelationType__AttributesAssignment_3


    // $ANTLR start rule__XRelation__NameAssignment_1
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1693:1: rule__XRelation__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__XRelation__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1697:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1698:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1698:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1699:1: RULE_STRING
            {
             before(grammarAccess.getXRelationAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__XRelation__NameAssignment_13338); 
             after(grammarAccess.getXRelationAccess().getNameSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__NameAssignment_1


    // $ANTLR start rule__XRelation__TypeAssignment_3
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1708:1: rule__XRelation__TypeAssignment_3 : ( ( RULE_ID ) ) ;
    public final void rule__XRelation__TypeAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1712:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1713:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1713:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1714:1: ( RULE_ID )
            {
             before(grammarAccess.getXRelationAccess().getTypeArtifactTypeCrossReference_3_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1715:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1716:1: RULE_ID
            {
             before(grammarAccess.getXRelationAccess().getTypeArtifactTypeIDTerminalRuleCall_3_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__XRelation__TypeAssignment_33373); 
             after(grammarAccess.getXRelationAccess().getTypeArtifactTypeIDTerminalRuleCall_3_0_1()); 

            }

             after(grammarAccess.getXRelationAccess().getTypeArtifactTypeCrossReference_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__TypeAssignment_3


    // $ANTLR start rule__XRelation__NameAssignment_5
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1727:1: rule__XRelation__NameAssignment_5 : ( RULE_STRING ) ;
    public final void rule__XRelation__NameAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1731:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1732:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1732:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1733:1: RULE_STRING
            {
             before(grammarAccess.getXRelationAccess().getNameSTRINGTerminalRuleCall_5_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__XRelation__NameAssignment_53408); 
             after(grammarAccess.getXRelationAccess().getNameSTRINGTerminalRuleCall_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__NameAssignment_5


    // $ANTLR start rule__XRelation__TypeAssignment_7
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1742:1: rule__XRelation__TypeAssignment_7 : ( ( RULE_ID ) ) ;
    public final void rule__XRelation__TypeAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1746:1: ( ( ( RULE_ID ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1747:1: ( ( RULE_ID ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1747:1: ( ( RULE_ID ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1748:1: ( RULE_ID )
            {
             before(grammarAccess.getXRelationAccess().getTypeArtifactTypeCrossReference_7_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1749:1: ( RULE_ID )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1750:1: RULE_ID
            {
             before(grammarAccess.getXRelationAccess().getTypeArtifactTypeIDTerminalRuleCall_7_0_1()); 
            match(input,RULE_ID,FOLLOW_RULE_ID_in_rule__XRelation__TypeAssignment_73443); 
             after(grammarAccess.getXRelationAccess().getTypeArtifactTypeIDTerminalRuleCall_7_0_1()); 

            }

             after(grammarAccess.getXRelationAccess().getTypeArtifactTypeCrossReference_7_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__TypeAssignment_7


    // $ANTLR start rule__XRelation__NameAssignment_9
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1761:1: rule__XRelation__NameAssignment_9 : ( RULE_STRING ) ;
    public final void rule__XRelation__NameAssignment_9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1765:1: ( ( RULE_STRING ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1766:1: ( RULE_STRING )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1766:1: ( RULE_STRING )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1767:1: RULE_STRING
            {
             before(grammarAccess.getXRelationAccess().getNameSTRINGTerminalRuleCall_9_0()); 
            match(input,RULE_STRING,FOLLOW_RULE_STRING_in_rule__XRelation__NameAssignment_93478); 
             after(grammarAccess.getXRelationAccess().getNameSTRINGTerminalRuleCall_9_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__NameAssignment_9


    // $ANTLR start rule__XRelation__NameAssignment_11
    // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1776:1: rule__XRelation__NameAssignment_11 : ( ( rule__XRelation__NameAlternatives_11_0 ) ) ;
    public final void rule__XRelation__NameAssignment_11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1780:1: ( ( ( rule__XRelation__NameAlternatives_11_0 ) ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1781:1: ( ( rule__XRelation__NameAlternatives_11_0 ) )
            {
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1781:1: ( ( rule__XRelation__NameAlternatives_11_0 ) )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1782:1: ( rule__XRelation__NameAlternatives_11_0 )
            {
             before(grammarAccess.getXRelationAccess().getNameAlternatives_11_0()); 
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1783:1: ( rule__XRelation__NameAlternatives_11_0 )
            // ../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g:1783:2: rule__XRelation__NameAlternatives_11_0
            {
            pushFollow(FOLLOW_rule__XRelation__NameAlternatives_11_0_in_rule__XRelation__NameAssignment_113509);
            rule__XRelation__NameAlternatives_11_0();
            _fsp--;


            }

             after(grammarAccess.getXRelationAccess().getNameAlternatives_11_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end rule__XRelation__NameAssignment_11


 

    public static final BitSet FOLLOW_ruleModel_in_entryRuleModel60 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleModel67 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Model__Group__0_in_ruleModel94 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport120 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Import__Group__0_in_ruleImport154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleType_in_entryRuleType180 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleType187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Type__Alternatives_in_ruleType214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_entryRuleArtifactType240 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArtifactType247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__0_in_ruleArtifactType274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRef_in_entryRuleXRef300 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXRef307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XRef__Alternatives_in_ruleXRef334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeRef_in_entryRuleRelationTypeRef360 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationTypeRef367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationTypeRef__Group__0_in_ruleRelationTypeRef394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_entryRuleAttributeTypeRef420 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeTypeRef427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__0_in_ruleAttributeTypeRef454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_entryRuleAttributeType480 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeType487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__0_in_ruleAttributeType514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttribute_in_entryRuleXAttribute540 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXAttribute547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XAttribute__Group__0_in_ruleXAttribute574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_entryRuleRelationType600 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelationType607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__Group__0_in_ruleRelationType634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelation_in_entryRuleXRelation660 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleXRelation667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XRelation__Group__0_in_ruleXRelation694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArtifactType_in_rule__Type__Alternatives730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationType_in_rule__Type__Alternatives747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_rule__Type__Alternatives764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelationTypeRef_in_rule__XRef__Alternatives796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeTypeRef_in_rule__XRef__Alternatives813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_11_in_rule__XRelation__NameAlternatives_11_0846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_rule__XRelation__NameAlternatives_11_0866 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_rule__XRelation__NameAlternatives_11_0886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Model__ImportsAssignment_0_in_rule__Model__Group__0922 = new BitSet(new long[]{0x000000000220C002L});
    public static final BitSet FOLLOW_rule__Model__Group__1_in_rule__Model__Group__0932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Model__ElementsAssignment_1_in_rule__Model__Group__1960 = new BitSet(new long[]{0x0000000002208002L});
    public static final BitSet FOLLOW_14_in_rule__Import__Group__01000 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__Import__Group__1_in_rule__Import__Group__01010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__Import__ImportURIAssignment_1_in_rule__Import__Group__11038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_rule__ArtifactType__Group__01077 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__1_in_rule__ArtifactType__Group__01087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__NameAssignment_1_in_rule__ArtifactType__Group__11115 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__2_in_rule__ArtifactType__Group__11124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_2__0_in_rule__ArtifactType__Group__21152 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__3_in_rule__ArtifactType__Group__21162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_rule__ArtifactType__Group__31191 = new BitSet(new long[]{0x00000000001A0000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__4_in_rule__ArtifactType__Group__31201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__AttributesAssignment_4_in_rule__ArtifactType__Group__41229 = new BitSet(new long[]{0x00000000001A0000L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group__5_in_rule__ArtifactType__Group__41239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_rule__ArtifactType__Group__51268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_rule__ArtifactType__Group_2__01316 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__ArtifactType__Group_2__1_in_rule__ArtifactType__Group_2__01326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__ArtifactType__SuperEntityAssignment_2_1_in_rule__ArtifactType__Group_2__11354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_rule__RelationTypeRef__Group__01393 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__RelationTypeRef__Group__1_in_rule__RelationTypeRef__Group__01403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationTypeRef__TypeAssignment_1_in_rule__RelationTypeRef__Group__11431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_rule__AttributeTypeRef__Group__01470 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__Group__1_in_rule__AttributeTypeRef__Group__01480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeTypeRef__TypeAssignment_1_in_rule__AttributeTypeRef__Group__11508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_rule__AttributeType__Group__01547 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__1_in_rule__AttributeType__Group__01557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__NameAssignment_1_in_rule__AttributeType__Group__11585 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__2_in_rule__AttributeType__Group__11594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_2__0_in_rule__AttributeType__Group__21622 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__3_in_rule__AttributeType__Group__21632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_rule__AttributeType__Group__31661 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__4_in_rule__AttributeType__Group__31671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__AttributesAssignment_4_in_rule__AttributeType__Group__41699 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_rule__AttributeType__Group__5_in_rule__AttributeType__Group__41708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_rule__AttributeType__Group__51737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_rule__AttributeType__Group_2__01785 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__AttributeType__Group_2__1_in_rule__AttributeType__Group_2__01795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__AttributeType__SuperEntityAssignment_2_1_in_rule__AttributeType__Group_2__11823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_rule__XAttribute__Group__01862 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__XAttribute__Group__1_in_rule__XAttribute__Group__01872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XAttribute__NameAssignment_1_in_rule__XAttribute__Group__11900 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_rule__XAttribute__Group__2_in_rule__XAttribute__Group__11909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_rule__XAttribute__Group__21938 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__XAttribute__Group__3_in_rule__XAttribute__Group__21948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XAttribute__NameAssignment_3_in_rule__XAttribute__Group__31976 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_rule__XAttribute__Group__4_in_rule__XAttribute__Group__31985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XAttribute__Group_4__0_in_rule__XAttribute__Group__42013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_rule__XAttribute__Group_4__02059 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__XAttribute__Group_4__1_in_rule__XAttribute__Group_4__02069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XAttribute__NameAssignment_4_1_in_rule__XAttribute__Group_4__12097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_rule__RelationType__Group__02136 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__RelationType__Group__1_in_rule__RelationType__Group__02146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__NameAssignment_1_in_rule__RelationType__Group__12174 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__2_in_rule__RelationType__Group__12183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_rule__RelationType__Group__22212 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__3_in_rule__RelationType__Group__22222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__RelationType__AttributesAssignment_3_in_rule__RelationType__Group__32250 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_rule__RelationType__Group__4_in_rule__RelationType__Group__32259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_rule__RelationType__Group__42288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_rule__XRelation__Group__02334 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__XRelation__Group__1_in_rule__XRelation__Group__02344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XRelation__NameAssignment_1_in_rule__XRelation__Group__12372 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_rule__XRelation__Group__2_in_rule__XRelation__Group__12381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_rule__XRelation__Group__22410 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__XRelation__Group__3_in_rule__XRelation__Group__22420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XRelation__TypeAssignment_3_in_rule__XRelation__Group__32448 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_rule__XRelation__Group__4_in_rule__XRelation__Group__32457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_rule__XRelation__Group__42486 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__XRelation__Group__5_in_rule__XRelation__Group__42496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XRelation__NameAssignment_5_in_rule__XRelation__Group__52524 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_rule__XRelation__Group__6_in_rule__XRelation__Group__52533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_rule__XRelation__Group__62562 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_rule__XRelation__Group__7_in_rule__XRelation__Group__62572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XRelation__TypeAssignment_7_in_rule__XRelation__Group__72600 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_rule__XRelation__Group__8_in_rule__XRelation__Group__72609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_rule__XRelation__Group__82638 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_rule__XRelation__Group__9_in_rule__XRelation__Group__82648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XRelation__NameAssignment_9_in_rule__XRelation__Group__92676 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_rule__XRelation__Group__10_in_rule__XRelation__Group__92685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_rule__XRelation__Group__102714 = new BitSet(new long[]{0x0000000000003800L});
    public static final BitSet FOLLOW_rule__XRelation__Group__11_in_rule__XRelation__Group__102724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XRelation__NameAssignment_11_in_rule__XRelation__Group__112752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleImport_in_rule__Model__ImportsAssignment_02810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleType_in_rule__Model__ElementsAssignment_12841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__Import__ImportURIAssignment_12872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__ArtifactType__NameAssignment_12903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__ArtifactType__SuperEntityAssignment_2_12938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRef_in_rule__ArtifactType__AttributesAssignment_42973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__RelationTypeRef__TypeAssignment_13008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__AttributeTypeRef__TypeAssignment_13047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__AttributeType__NameAssignment_13082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__AttributeType__SuperEntityAssignment_2_13117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXAttribute_in_rule__AttributeType__AttributesAssignment_43152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__XAttribute__NameAssignment_13183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__XAttribute__NameAssignment_33214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__XAttribute__NameAssignment_4_13245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__RelationType__NameAssignment_13276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleXRelation_in_rule__RelationType__AttributesAssignment_33307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__XRelation__NameAssignment_13338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__XRelation__TypeAssignment_33373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__XRelation__NameAssignment_53408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_rule__XRelation__TypeAssignment_73443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_rule__XRelation__NameAssignment_93478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule__XRelation__NameAlternatives_11_0_in_rule__XRelation__NameAssignment_113509 = new BitSet(new long[]{0x0000000000000002L});

}