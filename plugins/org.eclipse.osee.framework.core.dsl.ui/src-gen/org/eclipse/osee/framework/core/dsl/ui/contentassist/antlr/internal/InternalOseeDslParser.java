package org.eclipse.osee.framework.core.dsl.ui.contentassist.antlr.internal; 

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.DFA;
import org.eclipse.osee.framework.core.dsl.services.OseeDslGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalOseeDslParser extends AbstractInternalContentAssistParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_WHOLE_NUM_STR", "RULE_ID", "RULE_STRING", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'ALL'", "'DefaultAttributeDataProvider'", "'UriAttributeDataProvider'", "'unlimited'", "'DefaultAttributeTaggerProvider'", "'BooleanAttribute'", "'CompressedContentAttribute'", "'DateAttribute'", "'EnumeratedAttribute'", "'FloatingPointAttribute'", "'IntegerAttribute'", "'LongAttribute'", "'JavaObjectAttribute'", "'StringAttribute'", "'ArtifactReferenceAttribute'", "'BranchReferenceAttribute'", "'WordAttribute'", "'OutlineNumberAttribute'", "'Lexicographical_Ascending'", "'Lexicographical_Descending'", "'Unordered'", "'ONE_TO_ONE'", "'ONE_TO_MANY'", "'MANY_TO_ONE'", "'MANY_TO_MANY'", "'EQ'", "'LIKE'", "'AND'", "'OR'", "'artifactName'", "'artifactGuid'", "'branchName'", "'branchUuid'", "'ALLOW'", "'DENY'", "'SIDE_A'", "'SIDE_B'", "'BOTH'", "'import'", "'.'", "'artifactType'", "'{'", "'id'", "'}'", "'extends'", "','", "'guid'", "'attribute'", "'attributeType'", "'dataProvider'", "'min'", "'max'", "'overrides'", "'taggerId'", "'enumType'", "'description'", "'defaultValue'", "'fileExtension'", "'mediaType'", "'oseeEnumType'", "'entry'", "'entryGuid'", "'overrides enum'", "'add'", "'remove'", "'overrides artifactType'", "'update'", "'relationType'", "'sideAName'", "'sideAArtifactType'", "'sideBName'", "'sideBArtifactType'", "'defaultOrderType'", "'multiplicity'", "'('", "')'", "'artifactMatcher'", "'where'", "';'", "'role'", "'accessContext'", "'childrenOf'", "'artifact'", "'edit'", "'of'", "'abstract'", "'inheritAll'"
    };
    public static final int T__50=50;
    public static final int T__59=59;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__60=60;
    public static final int T__61=61;
    public static final int RULE_ID=5;
    public static final int RULE_INT=7;
    public static final int T__66=66;
    public static final int RULE_ML_COMMENT=8;
    public static final int T__67=67;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int T__64=64;
    public static final int T__65=65;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int RULE_WHOLE_NUM_STR=4;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__93=93;
    public static final int T__94=94;
    public static final int T__90=90;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int T__95=95;
    public static final int T__96=96;
    public static final int T__97=97;
    public static final int T__98=98;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__70=70;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int RULE_STRING=6;
    public static final int RULE_SL_COMMENT=9;
    public static final int T__77=77;
    public static final int T__78=78;
    public static final int T__79=79;
    public static final int T__73=73;
    public static final int EOF=-1;
    public static final int T__74=74;
    public static final int T__75=75;
    public static final int T__76=76;
    public static final int T__80=80;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int T__83=83;
    public static final int RULE_WS=10;
    public static final int RULE_ANY_OTHER=11;
    public static final int T__88=88;
    public static final int T__89=89;
    public static final int T__84=84;
    public static final int T__85=85;
    public static final int T__86=86;
    public static final int T__87=87;

    // delegates
    // delegators


        public InternalOseeDslParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalOseeDslParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalOseeDslParser.tokenNames; }
    public String getGrammarFileName() { return "InternalOseeDsl.g"; }


     
     	private OseeDslGrammarAccess grammarAccess;
     	
        public void setGrammarAccess(OseeDslGrammarAccess grammarAccess) {
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




    // $ANTLR start "entryRuleOseeDsl"
    // InternalOseeDsl.g:60:1: entryRuleOseeDsl : ruleOseeDsl EOF ;
    public final void entryRuleOseeDsl() throws RecognitionException {
        try {
            // InternalOseeDsl.g:61:1: ( ruleOseeDsl EOF )
            // InternalOseeDsl.g:62:1: ruleOseeDsl EOF
            {
             before(grammarAccess.getOseeDslRule()); 
            pushFollow(FOLLOW_1);
            ruleOseeDsl();

            state._fsp--;

             after(grammarAccess.getOseeDslRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleOseeDsl"


    // $ANTLR start "ruleOseeDsl"
    // InternalOseeDsl.g:69:1: ruleOseeDsl : ( ( rule__OseeDsl__Group__0 ) ) ;
    public final void ruleOseeDsl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:73:2: ( ( ( rule__OseeDsl__Group__0 ) ) )
            // InternalOseeDsl.g:74:1: ( ( rule__OseeDsl__Group__0 ) )
            {
            // InternalOseeDsl.g:74:1: ( ( rule__OseeDsl__Group__0 ) )
            // InternalOseeDsl.g:75:1: ( rule__OseeDsl__Group__0 )
            {
             before(grammarAccess.getOseeDslAccess().getGroup()); 
            // InternalOseeDsl.g:76:1: ( rule__OseeDsl__Group__0 )
            // InternalOseeDsl.g:76:2: rule__OseeDsl__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__OseeDsl__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getOseeDslAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleOseeDsl"


    // $ANTLR start "entryRuleImport"
    // InternalOseeDsl.g:88:1: entryRuleImport : ruleImport EOF ;
    public final void entryRuleImport() throws RecognitionException {
        try {
            // InternalOseeDsl.g:89:1: ( ruleImport EOF )
            // InternalOseeDsl.g:90:1: ruleImport EOF
            {
             before(grammarAccess.getImportRule()); 
            pushFollow(FOLLOW_1);
            ruleImport();

            state._fsp--;

             after(grammarAccess.getImportRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleImport"


    // $ANTLR start "ruleImport"
    // InternalOseeDsl.g:97:1: ruleImport : ( ( rule__Import__Group__0 ) ) ;
    public final void ruleImport() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:101:2: ( ( ( rule__Import__Group__0 ) ) )
            // InternalOseeDsl.g:102:1: ( ( rule__Import__Group__0 ) )
            {
            // InternalOseeDsl.g:102:1: ( ( rule__Import__Group__0 ) )
            // InternalOseeDsl.g:103:1: ( rule__Import__Group__0 )
            {
             before(grammarAccess.getImportAccess().getGroup()); 
            // InternalOseeDsl.g:104:1: ( rule__Import__Group__0 )
            // InternalOseeDsl.g:104:2: rule__Import__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__Import__Group__0();

            state._fsp--;


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
    // $ANTLR end "ruleImport"


    // $ANTLR start "entryRuleQUALIFIED_NAME"
    // InternalOseeDsl.g:116:1: entryRuleQUALIFIED_NAME : ruleQUALIFIED_NAME EOF ;
    public final void entryRuleQUALIFIED_NAME() throws RecognitionException {
        try {
            // InternalOseeDsl.g:117:1: ( ruleQUALIFIED_NAME EOF )
            // InternalOseeDsl.g:118:1: ruleQUALIFIED_NAME EOF
            {
             before(grammarAccess.getQUALIFIED_NAMERule()); 
            pushFollow(FOLLOW_1);
            ruleQUALIFIED_NAME();

            state._fsp--;

             after(grammarAccess.getQUALIFIED_NAMERule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleQUALIFIED_NAME"


    // $ANTLR start "ruleQUALIFIED_NAME"
    // InternalOseeDsl.g:125:1: ruleQUALIFIED_NAME : ( ( rule__QUALIFIED_NAME__Group__0 ) ) ;
    public final void ruleQUALIFIED_NAME() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:129:2: ( ( ( rule__QUALIFIED_NAME__Group__0 ) ) )
            // InternalOseeDsl.g:130:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            {
            // InternalOseeDsl.g:130:1: ( ( rule__QUALIFIED_NAME__Group__0 ) )
            // InternalOseeDsl.g:131:1: ( rule__QUALIFIED_NAME__Group__0 )
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup()); 
            // InternalOseeDsl.g:132:1: ( rule__QUALIFIED_NAME__Group__0 )
            // InternalOseeDsl.g:132:2: rule__QUALIFIED_NAME__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__QUALIFIED_NAME__Group__0();

            state._fsp--;


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
    // $ANTLR end "ruleQUALIFIED_NAME"


    // $ANTLR start "entryRuleOseeType"
    // InternalOseeDsl.g:146:1: entryRuleOseeType : ruleOseeType EOF ;
    public final void entryRuleOseeType() throws RecognitionException {
        try {
            // InternalOseeDsl.g:147:1: ( ruleOseeType EOF )
            // InternalOseeDsl.g:148:1: ruleOseeType EOF
            {
             before(grammarAccess.getOseeTypeRule()); 
            pushFollow(FOLLOW_1);
            ruleOseeType();

            state._fsp--;

             after(grammarAccess.getOseeTypeRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleOseeType"


    // $ANTLR start "ruleOseeType"
    // InternalOseeDsl.g:155:1: ruleOseeType : ( ( rule__OseeType__Alternatives ) ) ;
    public final void ruleOseeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:159:2: ( ( ( rule__OseeType__Alternatives ) ) )
            // InternalOseeDsl.g:160:1: ( ( rule__OseeType__Alternatives ) )
            {
            // InternalOseeDsl.g:160:1: ( ( rule__OseeType__Alternatives ) )
            // InternalOseeDsl.g:161:1: ( rule__OseeType__Alternatives )
            {
             before(grammarAccess.getOseeTypeAccess().getAlternatives()); 
            // InternalOseeDsl.g:162:1: ( rule__OseeType__Alternatives )
            // InternalOseeDsl.g:162:2: rule__OseeType__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__OseeType__Alternatives();

            state._fsp--;


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
    // $ANTLR end "ruleOseeType"


    // $ANTLR start "entryRuleXArtifactType"
    // InternalOseeDsl.g:174:1: entryRuleXArtifactType : ruleXArtifactType EOF ;
    public final void entryRuleXArtifactType() throws RecognitionException {
        try {
            // InternalOseeDsl.g:175:1: ( ruleXArtifactType EOF )
            // InternalOseeDsl.g:176:1: ruleXArtifactType EOF
            {
             before(grammarAccess.getXArtifactTypeRule()); 
            pushFollow(FOLLOW_1);
            ruleXArtifactType();

            state._fsp--;

             after(grammarAccess.getXArtifactTypeRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleXArtifactType"


    // $ANTLR start "ruleXArtifactType"
    // InternalOseeDsl.g:183:1: ruleXArtifactType : ( ( rule__XArtifactType__Group__0 ) ) ;
    public final void ruleXArtifactType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:187:2: ( ( ( rule__XArtifactType__Group__0 ) ) )
            // InternalOseeDsl.g:188:1: ( ( rule__XArtifactType__Group__0 ) )
            {
            // InternalOseeDsl.g:188:1: ( ( rule__XArtifactType__Group__0 ) )
            // InternalOseeDsl.g:189:1: ( rule__XArtifactType__Group__0 )
            {
             before(grammarAccess.getXArtifactTypeAccess().getGroup()); 
            // InternalOseeDsl.g:190:1: ( rule__XArtifactType__Group__0 )
            // InternalOseeDsl.g:190:2: rule__XArtifactType__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXArtifactType"


    // $ANTLR start "entryRuleXAttributeTypeRef"
    // InternalOseeDsl.g:202:1: entryRuleXAttributeTypeRef : ruleXAttributeTypeRef EOF ;
    public final void entryRuleXAttributeTypeRef() throws RecognitionException {
        try {
            // InternalOseeDsl.g:203:1: ( ruleXAttributeTypeRef EOF )
            // InternalOseeDsl.g:204:1: ruleXAttributeTypeRef EOF
            {
             before(grammarAccess.getXAttributeTypeRefRule()); 
            pushFollow(FOLLOW_1);
            ruleXAttributeTypeRef();

            state._fsp--;

             after(grammarAccess.getXAttributeTypeRefRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleXAttributeTypeRef"


    // $ANTLR start "ruleXAttributeTypeRef"
    // InternalOseeDsl.g:211:1: ruleXAttributeTypeRef : ( ( rule__XAttributeTypeRef__Group__0 ) ) ;
    public final void ruleXAttributeTypeRef() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:215:2: ( ( ( rule__XAttributeTypeRef__Group__0 ) ) )
            // InternalOseeDsl.g:216:1: ( ( rule__XAttributeTypeRef__Group__0 ) )
            {
            // InternalOseeDsl.g:216:1: ( ( rule__XAttributeTypeRef__Group__0 ) )
            // InternalOseeDsl.g:217:1: ( rule__XAttributeTypeRef__Group__0 )
            {
             before(grammarAccess.getXAttributeTypeRefAccess().getGroup()); 
            // InternalOseeDsl.g:218:1: ( rule__XAttributeTypeRef__Group__0 )
            // InternalOseeDsl.g:218:2: rule__XAttributeTypeRef__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeTypeRef__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeRefAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXAttributeTypeRef"


    // $ANTLR start "entryRuleXAttributeType"
    // InternalOseeDsl.g:230:1: entryRuleXAttributeType : ruleXAttributeType EOF ;
    public final void entryRuleXAttributeType() throws RecognitionException {
        try {
            // InternalOseeDsl.g:231:1: ( ruleXAttributeType EOF )
            // InternalOseeDsl.g:232:1: ruleXAttributeType EOF
            {
             before(grammarAccess.getXAttributeTypeRule()); 
            pushFollow(FOLLOW_1);
            ruleXAttributeType();

            state._fsp--;

             after(grammarAccess.getXAttributeTypeRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleXAttributeType"


    // $ANTLR start "ruleXAttributeType"
    // InternalOseeDsl.g:239:1: ruleXAttributeType : ( ( rule__XAttributeType__Group__0 ) ) ;
    public final void ruleXAttributeType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:243:2: ( ( ( rule__XAttributeType__Group__0 ) ) )
            // InternalOseeDsl.g:244:1: ( ( rule__XAttributeType__Group__0 ) )
            {
            // InternalOseeDsl.g:244:1: ( ( rule__XAttributeType__Group__0 ) )
            // InternalOseeDsl.g:245:1: ( rule__XAttributeType__Group__0 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getGroup()); 
            // InternalOseeDsl.g:246:1: ( rule__XAttributeType__Group__0 )
            // InternalOseeDsl.g:246:2: rule__XAttributeType__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXAttributeType"


    // $ANTLR start "entryRuleAttributeBaseType"
    // InternalOseeDsl.g:258:1: entryRuleAttributeBaseType : ruleAttributeBaseType EOF ;
    public final void entryRuleAttributeBaseType() throws RecognitionException {
        try {
            // InternalOseeDsl.g:259:1: ( ruleAttributeBaseType EOF )
            // InternalOseeDsl.g:260:1: ruleAttributeBaseType EOF
            {
             before(grammarAccess.getAttributeBaseTypeRule()); 
            pushFollow(FOLLOW_1);
            ruleAttributeBaseType();

            state._fsp--;

             after(grammarAccess.getAttributeBaseTypeRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleAttributeBaseType"


    // $ANTLR start "ruleAttributeBaseType"
    // InternalOseeDsl.g:267:1: ruleAttributeBaseType : ( ( rule__AttributeBaseType__Alternatives ) ) ;
    public final void ruleAttributeBaseType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:271:2: ( ( ( rule__AttributeBaseType__Alternatives ) ) )
            // InternalOseeDsl.g:272:1: ( ( rule__AttributeBaseType__Alternatives ) )
            {
            // InternalOseeDsl.g:272:1: ( ( rule__AttributeBaseType__Alternatives ) )
            // InternalOseeDsl.g:273:1: ( rule__AttributeBaseType__Alternatives )
            {
             before(grammarAccess.getAttributeBaseTypeAccess().getAlternatives()); 
            // InternalOseeDsl.g:274:1: ( rule__AttributeBaseType__Alternatives )
            // InternalOseeDsl.g:274:2: rule__AttributeBaseType__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__AttributeBaseType__Alternatives();

            state._fsp--;


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
    // $ANTLR end "ruleAttributeBaseType"


    // $ANTLR start "entryRuleXOseeEnumType"
    // InternalOseeDsl.g:286:1: entryRuleXOseeEnumType : ruleXOseeEnumType EOF ;
    public final void entryRuleXOseeEnumType() throws RecognitionException {
        try {
            // InternalOseeDsl.g:287:1: ( ruleXOseeEnumType EOF )
            // InternalOseeDsl.g:288:1: ruleXOseeEnumType EOF
            {
             before(grammarAccess.getXOseeEnumTypeRule()); 
            pushFollow(FOLLOW_1);
            ruleXOseeEnumType();

            state._fsp--;

             after(grammarAccess.getXOseeEnumTypeRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleXOseeEnumType"


    // $ANTLR start "ruleXOseeEnumType"
    // InternalOseeDsl.g:295:1: ruleXOseeEnumType : ( ( rule__XOseeEnumType__Group__0 ) ) ;
    public final void ruleXOseeEnumType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:299:2: ( ( ( rule__XOseeEnumType__Group__0 ) ) )
            // InternalOseeDsl.g:300:1: ( ( rule__XOseeEnumType__Group__0 ) )
            {
            // InternalOseeDsl.g:300:1: ( ( rule__XOseeEnumType__Group__0 ) )
            // InternalOseeDsl.g:301:1: ( rule__XOseeEnumType__Group__0 )
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getGroup()); 
            // InternalOseeDsl.g:302:1: ( rule__XOseeEnumType__Group__0 )
            // InternalOseeDsl.g:302:2: rule__XOseeEnumType__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getXOseeEnumTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXOseeEnumType"


    // $ANTLR start "entryRuleXOseeEnumEntry"
    // InternalOseeDsl.g:314:1: entryRuleXOseeEnumEntry : ruleXOseeEnumEntry EOF ;
    public final void entryRuleXOseeEnumEntry() throws RecognitionException {
        try {
            // InternalOseeDsl.g:315:1: ( ruleXOseeEnumEntry EOF )
            // InternalOseeDsl.g:316:1: ruleXOseeEnumEntry EOF
            {
             before(grammarAccess.getXOseeEnumEntryRule()); 
            pushFollow(FOLLOW_1);
            ruleXOseeEnumEntry();

            state._fsp--;

             after(grammarAccess.getXOseeEnumEntryRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleXOseeEnumEntry"


    // $ANTLR start "ruleXOseeEnumEntry"
    // InternalOseeDsl.g:323:1: ruleXOseeEnumEntry : ( ( rule__XOseeEnumEntry__Group__0 ) ) ;
    public final void ruleXOseeEnumEntry() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:327:2: ( ( ( rule__XOseeEnumEntry__Group__0 ) ) )
            // InternalOseeDsl.g:328:1: ( ( rule__XOseeEnumEntry__Group__0 ) )
            {
            // InternalOseeDsl.g:328:1: ( ( rule__XOseeEnumEntry__Group__0 ) )
            // InternalOseeDsl.g:329:1: ( rule__XOseeEnumEntry__Group__0 )
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getGroup()); 
            // InternalOseeDsl.g:330:1: ( rule__XOseeEnumEntry__Group__0 )
            // InternalOseeDsl.g:330:2: rule__XOseeEnumEntry__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getXOseeEnumEntryAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXOseeEnumEntry"


    // $ANTLR start "entryRuleXOseeEnumOverride"
    // InternalOseeDsl.g:342:1: entryRuleXOseeEnumOverride : ruleXOseeEnumOverride EOF ;
    public final void entryRuleXOseeEnumOverride() throws RecognitionException {
        try {
            // InternalOseeDsl.g:343:1: ( ruleXOseeEnumOverride EOF )
            // InternalOseeDsl.g:344:1: ruleXOseeEnumOverride EOF
            {
             before(grammarAccess.getXOseeEnumOverrideRule()); 
            pushFollow(FOLLOW_1);
            ruleXOseeEnumOverride();

            state._fsp--;

             after(grammarAccess.getXOseeEnumOverrideRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleXOseeEnumOverride"


    // $ANTLR start "ruleXOseeEnumOverride"
    // InternalOseeDsl.g:351:1: ruleXOseeEnumOverride : ( ( rule__XOseeEnumOverride__Group__0 ) ) ;
    public final void ruleXOseeEnumOverride() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:355:2: ( ( ( rule__XOseeEnumOverride__Group__0 ) ) )
            // InternalOseeDsl.g:356:1: ( ( rule__XOseeEnumOverride__Group__0 ) )
            {
            // InternalOseeDsl.g:356:1: ( ( rule__XOseeEnumOverride__Group__0 ) )
            // InternalOseeDsl.g:357:1: ( rule__XOseeEnumOverride__Group__0 )
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getGroup()); 
            // InternalOseeDsl.g:358:1: ( rule__XOseeEnumOverride__Group__0 )
            // InternalOseeDsl.g:358:2: rule__XOseeEnumOverride__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumOverride__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getXOseeEnumOverrideAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXOseeEnumOverride"


    // $ANTLR start "entryRuleOverrideOption"
    // InternalOseeDsl.g:370:1: entryRuleOverrideOption : ruleOverrideOption EOF ;
    public final void entryRuleOverrideOption() throws RecognitionException {
        try {
            // InternalOseeDsl.g:371:1: ( ruleOverrideOption EOF )
            // InternalOseeDsl.g:372:1: ruleOverrideOption EOF
            {
             before(grammarAccess.getOverrideOptionRule()); 
            pushFollow(FOLLOW_1);
            ruleOverrideOption();

            state._fsp--;

             after(grammarAccess.getOverrideOptionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleOverrideOption"


    // $ANTLR start "ruleOverrideOption"
    // InternalOseeDsl.g:379:1: ruleOverrideOption : ( ( rule__OverrideOption__Alternatives ) ) ;
    public final void ruleOverrideOption() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:383:2: ( ( ( rule__OverrideOption__Alternatives ) ) )
            // InternalOseeDsl.g:384:1: ( ( rule__OverrideOption__Alternatives ) )
            {
            // InternalOseeDsl.g:384:1: ( ( rule__OverrideOption__Alternatives ) )
            // InternalOseeDsl.g:385:1: ( rule__OverrideOption__Alternatives )
            {
             before(grammarAccess.getOverrideOptionAccess().getAlternatives()); 
            // InternalOseeDsl.g:386:1: ( rule__OverrideOption__Alternatives )
            // InternalOseeDsl.g:386:2: rule__OverrideOption__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__OverrideOption__Alternatives();

            state._fsp--;


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
    // $ANTLR end "ruleOverrideOption"


    // $ANTLR start "entryRuleAddEnum"
    // InternalOseeDsl.g:398:1: entryRuleAddEnum : ruleAddEnum EOF ;
    public final void entryRuleAddEnum() throws RecognitionException {
        try {
            // InternalOseeDsl.g:399:1: ( ruleAddEnum EOF )
            // InternalOseeDsl.g:400:1: ruleAddEnum EOF
            {
             before(grammarAccess.getAddEnumRule()); 
            pushFollow(FOLLOW_1);
            ruleAddEnum();

            state._fsp--;

             after(grammarAccess.getAddEnumRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleAddEnum"


    // $ANTLR start "ruleAddEnum"
    // InternalOseeDsl.g:407:1: ruleAddEnum : ( ( rule__AddEnum__Group__0 ) ) ;
    public final void ruleAddEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:411:2: ( ( ( rule__AddEnum__Group__0 ) ) )
            // InternalOseeDsl.g:412:1: ( ( rule__AddEnum__Group__0 ) )
            {
            // InternalOseeDsl.g:412:1: ( ( rule__AddEnum__Group__0 ) )
            // InternalOseeDsl.g:413:1: ( rule__AddEnum__Group__0 )
            {
             before(grammarAccess.getAddEnumAccess().getGroup()); 
            // InternalOseeDsl.g:414:1: ( rule__AddEnum__Group__0 )
            // InternalOseeDsl.g:414:2: rule__AddEnum__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__AddEnum__Group__0();

            state._fsp--;


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
    // $ANTLR end "ruleAddEnum"


    // $ANTLR start "entryRuleRemoveEnum"
    // InternalOseeDsl.g:426:1: entryRuleRemoveEnum : ruleRemoveEnum EOF ;
    public final void entryRuleRemoveEnum() throws RecognitionException {
        try {
            // InternalOseeDsl.g:427:1: ( ruleRemoveEnum EOF )
            // InternalOseeDsl.g:428:1: ruleRemoveEnum EOF
            {
             before(grammarAccess.getRemoveEnumRule()); 
            pushFollow(FOLLOW_1);
            ruleRemoveEnum();

            state._fsp--;

             after(grammarAccess.getRemoveEnumRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRemoveEnum"


    // $ANTLR start "ruleRemoveEnum"
    // InternalOseeDsl.g:435:1: ruleRemoveEnum : ( ( rule__RemoveEnum__Group__0 ) ) ;
    public final void ruleRemoveEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:439:2: ( ( ( rule__RemoveEnum__Group__0 ) ) )
            // InternalOseeDsl.g:440:1: ( ( rule__RemoveEnum__Group__0 ) )
            {
            // InternalOseeDsl.g:440:1: ( ( rule__RemoveEnum__Group__0 ) )
            // InternalOseeDsl.g:441:1: ( rule__RemoveEnum__Group__0 )
            {
             before(grammarAccess.getRemoveEnumAccess().getGroup()); 
            // InternalOseeDsl.g:442:1: ( rule__RemoveEnum__Group__0 )
            // InternalOseeDsl.g:442:2: rule__RemoveEnum__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__RemoveEnum__Group__0();

            state._fsp--;


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
    // $ANTLR end "ruleRemoveEnum"


    // $ANTLR start "entryRuleXOseeArtifactTypeOverride"
    // InternalOseeDsl.g:454:1: entryRuleXOseeArtifactTypeOverride : ruleXOseeArtifactTypeOverride EOF ;
    public final void entryRuleXOseeArtifactTypeOverride() throws RecognitionException {
        try {
            // InternalOseeDsl.g:455:1: ( ruleXOseeArtifactTypeOverride EOF )
            // InternalOseeDsl.g:456:1: ruleXOseeArtifactTypeOverride EOF
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideRule()); 
            pushFollow(FOLLOW_1);
            ruleXOseeArtifactTypeOverride();

            state._fsp--;

             after(grammarAccess.getXOseeArtifactTypeOverrideRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleXOseeArtifactTypeOverride"


    // $ANTLR start "ruleXOseeArtifactTypeOverride"
    // InternalOseeDsl.g:463:1: ruleXOseeArtifactTypeOverride : ( ( rule__XOseeArtifactTypeOverride__Group__0 ) ) ;
    public final void ruleXOseeArtifactTypeOverride() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:467:2: ( ( ( rule__XOseeArtifactTypeOverride__Group__0 ) ) )
            // InternalOseeDsl.g:468:1: ( ( rule__XOseeArtifactTypeOverride__Group__0 ) )
            {
            // InternalOseeDsl.g:468:1: ( ( rule__XOseeArtifactTypeOverride__Group__0 ) )
            // InternalOseeDsl.g:469:1: ( rule__XOseeArtifactTypeOverride__Group__0 )
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getGroup()); 
            // InternalOseeDsl.g:470:1: ( rule__XOseeArtifactTypeOverride__Group__0 )
            // InternalOseeDsl.g:470:2: rule__XOseeArtifactTypeOverride__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__XOseeArtifactTypeOverride__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXOseeArtifactTypeOverride"


    // $ANTLR start "entryRuleAttributeOverrideOption"
    // InternalOseeDsl.g:482:1: entryRuleAttributeOverrideOption : ruleAttributeOverrideOption EOF ;
    public final void entryRuleAttributeOverrideOption() throws RecognitionException {
        try {
            // InternalOseeDsl.g:483:1: ( ruleAttributeOverrideOption EOF )
            // InternalOseeDsl.g:484:1: ruleAttributeOverrideOption EOF
            {
             before(grammarAccess.getAttributeOverrideOptionRule()); 
            pushFollow(FOLLOW_1);
            ruleAttributeOverrideOption();

            state._fsp--;

             after(grammarAccess.getAttributeOverrideOptionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleAttributeOverrideOption"


    // $ANTLR start "ruleAttributeOverrideOption"
    // InternalOseeDsl.g:491:1: ruleAttributeOverrideOption : ( ( rule__AttributeOverrideOption__Alternatives ) ) ;
    public final void ruleAttributeOverrideOption() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:495:2: ( ( ( rule__AttributeOverrideOption__Alternatives ) ) )
            // InternalOseeDsl.g:496:1: ( ( rule__AttributeOverrideOption__Alternatives ) )
            {
            // InternalOseeDsl.g:496:1: ( ( rule__AttributeOverrideOption__Alternatives ) )
            // InternalOseeDsl.g:497:1: ( rule__AttributeOverrideOption__Alternatives )
            {
             before(grammarAccess.getAttributeOverrideOptionAccess().getAlternatives()); 
            // InternalOseeDsl.g:498:1: ( rule__AttributeOverrideOption__Alternatives )
            // InternalOseeDsl.g:498:2: rule__AttributeOverrideOption__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__AttributeOverrideOption__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getAttributeOverrideOptionAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAttributeOverrideOption"


    // $ANTLR start "entryRuleAddAttribute"
    // InternalOseeDsl.g:510:1: entryRuleAddAttribute : ruleAddAttribute EOF ;
    public final void entryRuleAddAttribute() throws RecognitionException {
        try {
            // InternalOseeDsl.g:511:1: ( ruleAddAttribute EOF )
            // InternalOseeDsl.g:512:1: ruleAddAttribute EOF
            {
             before(grammarAccess.getAddAttributeRule()); 
            pushFollow(FOLLOW_1);
            ruleAddAttribute();

            state._fsp--;

             after(grammarAccess.getAddAttributeRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleAddAttribute"


    // $ANTLR start "ruleAddAttribute"
    // InternalOseeDsl.g:519:1: ruleAddAttribute : ( ( rule__AddAttribute__Group__0 ) ) ;
    public final void ruleAddAttribute() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:523:2: ( ( ( rule__AddAttribute__Group__0 ) ) )
            // InternalOseeDsl.g:524:1: ( ( rule__AddAttribute__Group__0 ) )
            {
            // InternalOseeDsl.g:524:1: ( ( rule__AddAttribute__Group__0 ) )
            // InternalOseeDsl.g:525:1: ( rule__AddAttribute__Group__0 )
            {
             before(grammarAccess.getAddAttributeAccess().getGroup()); 
            // InternalOseeDsl.g:526:1: ( rule__AddAttribute__Group__0 )
            // InternalOseeDsl.g:526:2: rule__AddAttribute__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__AddAttribute__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getAddAttributeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAddAttribute"


    // $ANTLR start "entryRuleRemoveAttribute"
    // InternalOseeDsl.g:538:1: entryRuleRemoveAttribute : ruleRemoveAttribute EOF ;
    public final void entryRuleRemoveAttribute() throws RecognitionException {
        try {
            // InternalOseeDsl.g:539:1: ( ruleRemoveAttribute EOF )
            // InternalOseeDsl.g:540:1: ruleRemoveAttribute EOF
            {
             before(grammarAccess.getRemoveAttributeRule()); 
            pushFollow(FOLLOW_1);
            ruleRemoveAttribute();

            state._fsp--;

             after(grammarAccess.getRemoveAttributeRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRemoveAttribute"


    // $ANTLR start "ruleRemoveAttribute"
    // InternalOseeDsl.g:547:1: ruleRemoveAttribute : ( ( rule__RemoveAttribute__Group__0 ) ) ;
    public final void ruleRemoveAttribute() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:551:2: ( ( ( rule__RemoveAttribute__Group__0 ) ) )
            // InternalOseeDsl.g:552:1: ( ( rule__RemoveAttribute__Group__0 ) )
            {
            // InternalOseeDsl.g:552:1: ( ( rule__RemoveAttribute__Group__0 ) )
            // InternalOseeDsl.g:553:1: ( rule__RemoveAttribute__Group__0 )
            {
             before(grammarAccess.getRemoveAttributeAccess().getGroup()); 
            // InternalOseeDsl.g:554:1: ( rule__RemoveAttribute__Group__0 )
            // InternalOseeDsl.g:554:2: rule__RemoveAttribute__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__RemoveAttribute__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getRemoveAttributeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleRemoveAttribute"


    // $ANTLR start "entryRuleUpdateAttribute"
    // InternalOseeDsl.g:566:1: entryRuleUpdateAttribute : ruleUpdateAttribute EOF ;
    public final void entryRuleUpdateAttribute() throws RecognitionException {
        try {
            // InternalOseeDsl.g:567:1: ( ruleUpdateAttribute EOF )
            // InternalOseeDsl.g:568:1: ruleUpdateAttribute EOF
            {
             before(grammarAccess.getUpdateAttributeRule()); 
            pushFollow(FOLLOW_1);
            ruleUpdateAttribute();

            state._fsp--;

             after(grammarAccess.getUpdateAttributeRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleUpdateAttribute"


    // $ANTLR start "ruleUpdateAttribute"
    // InternalOseeDsl.g:575:1: ruleUpdateAttribute : ( ( rule__UpdateAttribute__Group__0 ) ) ;
    public final void ruleUpdateAttribute() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:579:2: ( ( ( rule__UpdateAttribute__Group__0 ) ) )
            // InternalOseeDsl.g:580:1: ( ( rule__UpdateAttribute__Group__0 ) )
            {
            // InternalOseeDsl.g:580:1: ( ( rule__UpdateAttribute__Group__0 ) )
            // InternalOseeDsl.g:581:1: ( rule__UpdateAttribute__Group__0 )
            {
             before(grammarAccess.getUpdateAttributeAccess().getGroup()); 
            // InternalOseeDsl.g:582:1: ( rule__UpdateAttribute__Group__0 )
            // InternalOseeDsl.g:582:2: rule__UpdateAttribute__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__UpdateAttribute__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getUpdateAttributeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleUpdateAttribute"


    // $ANTLR start "entryRuleXRelationType"
    // InternalOseeDsl.g:594:1: entryRuleXRelationType : ruleXRelationType EOF ;
    public final void entryRuleXRelationType() throws RecognitionException {
        try {
            // InternalOseeDsl.g:595:1: ( ruleXRelationType EOF )
            // InternalOseeDsl.g:596:1: ruleXRelationType EOF
            {
             before(grammarAccess.getXRelationTypeRule()); 
            pushFollow(FOLLOW_1);
            ruleXRelationType();

            state._fsp--;

             after(grammarAccess.getXRelationTypeRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleXRelationType"


    // $ANTLR start "ruleXRelationType"
    // InternalOseeDsl.g:603:1: ruleXRelationType : ( ( rule__XRelationType__Group__0 ) ) ;
    public final void ruleXRelationType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:607:2: ( ( ( rule__XRelationType__Group__0 ) ) )
            // InternalOseeDsl.g:608:1: ( ( rule__XRelationType__Group__0 ) )
            {
            // InternalOseeDsl.g:608:1: ( ( rule__XRelationType__Group__0 ) )
            // InternalOseeDsl.g:609:1: ( rule__XRelationType__Group__0 )
            {
             before(grammarAccess.getXRelationTypeAccess().getGroup()); 
            // InternalOseeDsl.g:610:1: ( rule__XRelationType__Group__0 )
            // InternalOseeDsl.g:610:2: rule__XRelationType__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getXRelationTypeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXRelationType"


    // $ANTLR start "entryRuleRelationOrderType"
    // InternalOseeDsl.g:622:1: entryRuleRelationOrderType : ruleRelationOrderType EOF ;
    public final void entryRuleRelationOrderType() throws RecognitionException {
        try {
            // InternalOseeDsl.g:623:1: ( ruleRelationOrderType EOF )
            // InternalOseeDsl.g:624:1: ruleRelationOrderType EOF
            {
             before(grammarAccess.getRelationOrderTypeRule()); 
            pushFollow(FOLLOW_1);
            ruleRelationOrderType();

            state._fsp--;

             after(grammarAccess.getRelationOrderTypeRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRelationOrderType"


    // $ANTLR start "ruleRelationOrderType"
    // InternalOseeDsl.g:631:1: ruleRelationOrderType : ( ( rule__RelationOrderType__Alternatives ) ) ;
    public final void ruleRelationOrderType() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:635:2: ( ( ( rule__RelationOrderType__Alternatives ) ) )
            // InternalOseeDsl.g:636:1: ( ( rule__RelationOrderType__Alternatives ) )
            {
            // InternalOseeDsl.g:636:1: ( ( rule__RelationOrderType__Alternatives ) )
            // InternalOseeDsl.g:637:1: ( rule__RelationOrderType__Alternatives )
            {
             before(grammarAccess.getRelationOrderTypeAccess().getAlternatives()); 
            // InternalOseeDsl.g:638:1: ( rule__RelationOrderType__Alternatives )
            // InternalOseeDsl.g:638:2: rule__RelationOrderType__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__RelationOrderType__Alternatives();

            state._fsp--;


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
    // $ANTLR end "ruleRelationOrderType"


    // $ANTLR start "entryRuleCondition"
    // InternalOseeDsl.g:652:1: entryRuleCondition : ruleCondition EOF ;
    public final void entryRuleCondition() throws RecognitionException {
        try {
            // InternalOseeDsl.g:653:1: ( ruleCondition EOF )
            // InternalOseeDsl.g:654:1: ruleCondition EOF
            {
             before(grammarAccess.getConditionRule()); 
            pushFollow(FOLLOW_1);
            ruleCondition();

            state._fsp--;

             after(grammarAccess.getConditionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleCondition"


    // $ANTLR start "ruleCondition"
    // InternalOseeDsl.g:661:1: ruleCondition : ( ( rule__Condition__Alternatives ) ) ;
    public final void ruleCondition() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:665:2: ( ( ( rule__Condition__Alternatives ) ) )
            // InternalOseeDsl.g:666:1: ( ( rule__Condition__Alternatives ) )
            {
            // InternalOseeDsl.g:666:1: ( ( rule__Condition__Alternatives ) )
            // InternalOseeDsl.g:667:1: ( rule__Condition__Alternatives )
            {
             before(grammarAccess.getConditionAccess().getAlternatives()); 
            // InternalOseeDsl.g:668:1: ( rule__Condition__Alternatives )
            // InternalOseeDsl.g:668:2: rule__Condition__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__Condition__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getConditionAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleCondition"


    // $ANTLR start "entryRuleSimpleCondition"
    // InternalOseeDsl.g:680:1: entryRuleSimpleCondition : ruleSimpleCondition EOF ;
    public final void entryRuleSimpleCondition() throws RecognitionException {
        try {
            // InternalOseeDsl.g:681:1: ( ruleSimpleCondition EOF )
            // InternalOseeDsl.g:682:1: ruleSimpleCondition EOF
            {
             before(grammarAccess.getSimpleConditionRule()); 
            pushFollow(FOLLOW_1);
            ruleSimpleCondition();

            state._fsp--;

             after(grammarAccess.getSimpleConditionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleSimpleCondition"


    // $ANTLR start "ruleSimpleCondition"
    // InternalOseeDsl.g:689:1: ruleSimpleCondition : ( ( rule__SimpleCondition__Group__0 ) ) ;
    public final void ruleSimpleCondition() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:693:2: ( ( ( rule__SimpleCondition__Group__0 ) ) )
            // InternalOseeDsl.g:694:1: ( ( rule__SimpleCondition__Group__0 ) )
            {
            // InternalOseeDsl.g:694:1: ( ( rule__SimpleCondition__Group__0 ) )
            // InternalOseeDsl.g:695:1: ( rule__SimpleCondition__Group__0 )
            {
             before(grammarAccess.getSimpleConditionAccess().getGroup()); 
            // InternalOseeDsl.g:696:1: ( rule__SimpleCondition__Group__0 )
            // InternalOseeDsl.g:696:2: rule__SimpleCondition__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__SimpleCondition__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getSimpleConditionAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleSimpleCondition"


    // $ANTLR start "entryRuleCompoundCondition"
    // InternalOseeDsl.g:708:1: entryRuleCompoundCondition : ruleCompoundCondition EOF ;
    public final void entryRuleCompoundCondition() throws RecognitionException {
        try {
            // InternalOseeDsl.g:709:1: ( ruleCompoundCondition EOF )
            // InternalOseeDsl.g:710:1: ruleCompoundCondition EOF
            {
             before(grammarAccess.getCompoundConditionRule()); 
            pushFollow(FOLLOW_1);
            ruleCompoundCondition();

            state._fsp--;

             after(grammarAccess.getCompoundConditionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleCompoundCondition"


    // $ANTLR start "ruleCompoundCondition"
    // InternalOseeDsl.g:717:1: ruleCompoundCondition : ( ( rule__CompoundCondition__Group__0 ) ) ;
    public final void ruleCompoundCondition() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:721:2: ( ( ( rule__CompoundCondition__Group__0 ) ) )
            // InternalOseeDsl.g:722:1: ( ( rule__CompoundCondition__Group__0 ) )
            {
            // InternalOseeDsl.g:722:1: ( ( rule__CompoundCondition__Group__0 ) )
            // InternalOseeDsl.g:723:1: ( rule__CompoundCondition__Group__0 )
            {
             before(grammarAccess.getCompoundConditionAccess().getGroup()); 
            // InternalOseeDsl.g:724:1: ( rule__CompoundCondition__Group__0 )
            // InternalOseeDsl.g:724:2: rule__CompoundCondition__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__CompoundCondition__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getCompoundConditionAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleCompoundCondition"


    // $ANTLR start "entryRuleXArtifactMatcher"
    // InternalOseeDsl.g:736:1: entryRuleXArtifactMatcher : ruleXArtifactMatcher EOF ;
    public final void entryRuleXArtifactMatcher() throws RecognitionException {
        try {
            // InternalOseeDsl.g:737:1: ( ruleXArtifactMatcher EOF )
            // InternalOseeDsl.g:738:1: ruleXArtifactMatcher EOF
            {
             before(grammarAccess.getXArtifactMatcherRule()); 
            pushFollow(FOLLOW_1);
            ruleXArtifactMatcher();

            state._fsp--;

             after(grammarAccess.getXArtifactMatcherRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleXArtifactMatcher"


    // $ANTLR start "ruleXArtifactMatcher"
    // InternalOseeDsl.g:745:1: ruleXArtifactMatcher : ( ( rule__XArtifactMatcher__Group__0 ) ) ;
    public final void ruleXArtifactMatcher() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:749:2: ( ( ( rule__XArtifactMatcher__Group__0 ) ) )
            // InternalOseeDsl.g:750:1: ( ( rule__XArtifactMatcher__Group__0 ) )
            {
            // InternalOseeDsl.g:750:1: ( ( rule__XArtifactMatcher__Group__0 ) )
            // InternalOseeDsl.g:751:1: ( rule__XArtifactMatcher__Group__0 )
            {
             before(grammarAccess.getXArtifactMatcherAccess().getGroup()); 
            // InternalOseeDsl.g:752:1: ( rule__XArtifactMatcher__Group__0 )
            // InternalOseeDsl.g:752:2: rule__XArtifactMatcher__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactMatcherAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXArtifactMatcher"


    // $ANTLR start "entryRuleRole"
    // InternalOseeDsl.g:764:1: entryRuleRole : ruleRole EOF ;
    public final void entryRuleRole() throws RecognitionException {
        try {
            // InternalOseeDsl.g:765:1: ( ruleRole EOF )
            // InternalOseeDsl.g:766:1: ruleRole EOF
            {
             before(grammarAccess.getRoleRule()); 
            pushFollow(FOLLOW_1);
            ruleRole();

            state._fsp--;

             after(grammarAccess.getRoleRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRole"


    // $ANTLR start "ruleRole"
    // InternalOseeDsl.g:773:1: ruleRole : ( ( rule__Role__Group__0 ) ) ;
    public final void ruleRole() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:777:2: ( ( ( rule__Role__Group__0 ) ) )
            // InternalOseeDsl.g:778:1: ( ( rule__Role__Group__0 ) )
            {
            // InternalOseeDsl.g:778:1: ( ( rule__Role__Group__0 ) )
            // InternalOseeDsl.g:779:1: ( rule__Role__Group__0 )
            {
             before(grammarAccess.getRoleAccess().getGroup()); 
            // InternalOseeDsl.g:780:1: ( rule__Role__Group__0 )
            // InternalOseeDsl.g:780:2: rule__Role__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__Role__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getRoleAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleRole"


    // $ANTLR start "entryRuleReferencedContext"
    // InternalOseeDsl.g:792:1: entryRuleReferencedContext : ruleReferencedContext EOF ;
    public final void entryRuleReferencedContext() throws RecognitionException {
        try {
            // InternalOseeDsl.g:793:1: ( ruleReferencedContext EOF )
            // InternalOseeDsl.g:794:1: ruleReferencedContext EOF
            {
             before(grammarAccess.getReferencedContextRule()); 
            pushFollow(FOLLOW_1);
            ruleReferencedContext();

            state._fsp--;

             after(grammarAccess.getReferencedContextRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleReferencedContext"


    // $ANTLR start "ruleReferencedContext"
    // InternalOseeDsl.g:801:1: ruleReferencedContext : ( ( rule__ReferencedContext__Group__0 ) ) ;
    public final void ruleReferencedContext() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:805:2: ( ( ( rule__ReferencedContext__Group__0 ) ) )
            // InternalOseeDsl.g:806:1: ( ( rule__ReferencedContext__Group__0 ) )
            {
            // InternalOseeDsl.g:806:1: ( ( rule__ReferencedContext__Group__0 ) )
            // InternalOseeDsl.g:807:1: ( rule__ReferencedContext__Group__0 )
            {
             before(grammarAccess.getReferencedContextAccess().getGroup()); 
            // InternalOseeDsl.g:808:1: ( rule__ReferencedContext__Group__0 )
            // InternalOseeDsl.g:808:2: rule__ReferencedContext__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ReferencedContext__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getReferencedContextAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleReferencedContext"


    // $ANTLR start "entryRuleUsersAndGroups"
    // InternalOseeDsl.g:820:1: entryRuleUsersAndGroups : ruleUsersAndGroups EOF ;
    public final void entryRuleUsersAndGroups() throws RecognitionException {
        try {
            // InternalOseeDsl.g:821:1: ( ruleUsersAndGroups EOF )
            // InternalOseeDsl.g:822:1: ruleUsersAndGroups EOF
            {
             before(grammarAccess.getUsersAndGroupsRule()); 
            pushFollow(FOLLOW_1);
            ruleUsersAndGroups();

            state._fsp--;

             after(grammarAccess.getUsersAndGroupsRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleUsersAndGroups"


    // $ANTLR start "ruleUsersAndGroups"
    // InternalOseeDsl.g:829:1: ruleUsersAndGroups : ( ( rule__UsersAndGroups__Group__0 ) ) ;
    public final void ruleUsersAndGroups() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:833:2: ( ( ( rule__UsersAndGroups__Group__0 ) ) )
            // InternalOseeDsl.g:834:1: ( ( rule__UsersAndGroups__Group__0 ) )
            {
            // InternalOseeDsl.g:834:1: ( ( rule__UsersAndGroups__Group__0 ) )
            // InternalOseeDsl.g:835:1: ( rule__UsersAndGroups__Group__0 )
            {
             before(grammarAccess.getUsersAndGroupsAccess().getGroup()); 
            // InternalOseeDsl.g:836:1: ( rule__UsersAndGroups__Group__0 )
            // InternalOseeDsl.g:836:2: rule__UsersAndGroups__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__UsersAndGroups__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getUsersAndGroupsAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleUsersAndGroups"


    // $ANTLR start "entryRuleAccessContext"
    // InternalOseeDsl.g:848:1: entryRuleAccessContext : ruleAccessContext EOF ;
    public final void entryRuleAccessContext() throws RecognitionException {
        try {
            // InternalOseeDsl.g:849:1: ( ruleAccessContext EOF )
            // InternalOseeDsl.g:850:1: ruleAccessContext EOF
            {
             before(grammarAccess.getAccessContextRule()); 
            pushFollow(FOLLOW_1);
            ruleAccessContext();

            state._fsp--;

             after(grammarAccess.getAccessContextRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleAccessContext"


    // $ANTLR start "ruleAccessContext"
    // InternalOseeDsl.g:857:1: ruleAccessContext : ( ( rule__AccessContext__Group__0 ) ) ;
    public final void ruleAccessContext() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:861:2: ( ( ( rule__AccessContext__Group__0 ) ) )
            // InternalOseeDsl.g:862:1: ( ( rule__AccessContext__Group__0 ) )
            {
            // InternalOseeDsl.g:862:1: ( ( rule__AccessContext__Group__0 ) )
            // InternalOseeDsl.g:863:1: ( rule__AccessContext__Group__0 )
            {
             before(grammarAccess.getAccessContextAccess().getGroup()); 
            // InternalOseeDsl.g:864:1: ( rule__AccessContext__Group__0 )
            // InternalOseeDsl.g:864:2: rule__AccessContext__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__AccessContext__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getAccessContextAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAccessContext"


    // $ANTLR start "entryRuleHierarchyRestriction"
    // InternalOseeDsl.g:876:1: entryRuleHierarchyRestriction : ruleHierarchyRestriction EOF ;
    public final void entryRuleHierarchyRestriction() throws RecognitionException {
        try {
            // InternalOseeDsl.g:877:1: ( ruleHierarchyRestriction EOF )
            // InternalOseeDsl.g:878:1: ruleHierarchyRestriction EOF
            {
             before(grammarAccess.getHierarchyRestrictionRule()); 
            pushFollow(FOLLOW_1);
            ruleHierarchyRestriction();

            state._fsp--;

             after(grammarAccess.getHierarchyRestrictionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleHierarchyRestriction"


    // $ANTLR start "ruleHierarchyRestriction"
    // InternalOseeDsl.g:885:1: ruleHierarchyRestriction : ( ( rule__HierarchyRestriction__Group__0 ) ) ;
    public final void ruleHierarchyRestriction() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:889:2: ( ( ( rule__HierarchyRestriction__Group__0 ) ) )
            // InternalOseeDsl.g:890:1: ( ( rule__HierarchyRestriction__Group__0 ) )
            {
            // InternalOseeDsl.g:890:1: ( ( rule__HierarchyRestriction__Group__0 ) )
            // InternalOseeDsl.g:891:1: ( rule__HierarchyRestriction__Group__0 )
            {
             before(grammarAccess.getHierarchyRestrictionAccess().getGroup()); 
            // InternalOseeDsl.g:892:1: ( rule__HierarchyRestriction__Group__0 )
            // InternalOseeDsl.g:892:2: rule__HierarchyRestriction__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__HierarchyRestriction__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getHierarchyRestrictionAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleHierarchyRestriction"


    // $ANTLR start "entryRuleRelationTypeArtifactTypePredicate"
    // InternalOseeDsl.g:904:1: entryRuleRelationTypeArtifactTypePredicate : ruleRelationTypeArtifactTypePredicate EOF ;
    public final void entryRuleRelationTypeArtifactTypePredicate() throws RecognitionException {
        try {
            // InternalOseeDsl.g:905:1: ( ruleRelationTypeArtifactTypePredicate EOF )
            // InternalOseeDsl.g:906:1: ruleRelationTypeArtifactTypePredicate EOF
            {
             before(grammarAccess.getRelationTypeArtifactTypePredicateRule()); 
            pushFollow(FOLLOW_1);
            ruleRelationTypeArtifactTypePredicate();

            state._fsp--;

             after(grammarAccess.getRelationTypeArtifactTypePredicateRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRelationTypeArtifactTypePredicate"


    // $ANTLR start "ruleRelationTypeArtifactTypePredicate"
    // InternalOseeDsl.g:913:1: ruleRelationTypeArtifactTypePredicate : ( ( rule__RelationTypeArtifactTypePredicate__Group__0 ) ) ;
    public final void ruleRelationTypeArtifactTypePredicate() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:917:2: ( ( ( rule__RelationTypeArtifactTypePredicate__Group__0 ) ) )
            // InternalOseeDsl.g:918:1: ( ( rule__RelationTypeArtifactTypePredicate__Group__0 ) )
            {
            // InternalOseeDsl.g:918:1: ( ( rule__RelationTypeArtifactTypePredicate__Group__0 ) )
            // InternalOseeDsl.g:919:1: ( rule__RelationTypeArtifactTypePredicate__Group__0 )
            {
             before(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getGroup()); 
            // InternalOseeDsl.g:920:1: ( rule__RelationTypeArtifactTypePredicate__Group__0 )
            // InternalOseeDsl.g:920:2: rule__RelationTypeArtifactTypePredicate__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeArtifactTypePredicate__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleRelationTypeArtifactTypePredicate"


    // $ANTLR start "entryRuleRelationTypeArtifactPredicate"
    // InternalOseeDsl.g:932:1: entryRuleRelationTypeArtifactPredicate : ruleRelationTypeArtifactPredicate EOF ;
    public final void entryRuleRelationTypeArtifactPredicate() throws RecognitionException {
        try {
            // InternalOseeDsl.g:933:1: ( ruleRelationTypeArtifactPredicate EOF )
            // InternalOseeDsl.g:934:1: ruleRelationTypeArtifactPredicate EOF
            {
             before(grammarAccess.getRelationTypeArtifactPredicateRule()); 
            pushFollow(FOLLOW_1);
            ruleRelationTypeArtifactPredicate();

            state._fsp--;

             after(grammarAccess.getRelationTypeArtifactPredicateRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRelationTypeArtifactPredicate"


    // $ANTLR start "ruleRelationTypeArtifactPredicate"
    // InternalOseeDsl.g:941:1: ruleRelationTypeArtifactPredicate : ( ( rule__RelationTypeArtifactPredicate__Group__0 ) ) ;
    public final void ruleRelationTypeArtifactPredicate() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:945:2: ( ( ( rule__RelationTypeArtifactPredicate__Group__0 ) ) )
            // InternalOseeDsl.g:946:1: ( ( rule__RelationTypeArtifactPredicate__Group__0 ) )
            {
            // InternalOseeDsl.g:946:1: ( ( rule__RelationTypeArtifactPredicate__Group__0 ) )
            // InternalOseeDsl.g:947:1: ( rule__RelationTypeArtifactPredicate__Group__0 )
            {
             before(grammarAccess.getRelationTypeArtifactPredicateAccess().getGroup()); 
            // InternalOseeDsl.g:948:1: ( rule__RelationTypeArtifactPredicate__Group__0 )
            // InternalOseeDsl.g:948:2: rule__RelationTypeArtifactPredicate__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeArtifactPredicate__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getRelationTypeArtifactPredicateAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleRelationTypeArtifactPredicate"


    // $ANTLR start "entryRuleRelationTypePredicate"
    // InternalOseeDsl.g:960:1: entryRuleRelationTypePredicate : ruleRelationTypePredicate EOF ;
    public final void entryRuleRelationTypePredicate() throws RecognitionException {
        try {
            // InternalOseeDsl.g:961:1: ( ruleRelationTypePredicate EOF )
            // InternalOseeDsl.g:962:1: ruleRelationTypePredicate EOF
            {
             before(grammarAccess.getRelationTypePredicateRule()); 
            pushFollow(FOLLOW_1);
            ruleRelationTypePredicate();

            state._fsp--;

             after(grammarAccess.getRelationTypePredicateRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRelationTypePredicate"


    // $ANTLR start "ruleRelationTypePredicate"
    // InternalOseeDsl.g:969:1: ruleRelationTypePredicate : ( ( rule__RelationTypePredicate__Alternatives ) ) ;
    public final void ruleRelationTypePredicate() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:973:2: ( ( ( rule__RelationTypePredicate__Alternatives ) ) )
            // InternalOseeDsl.g:974:1: ( ( rule__RelationTypePredicate__Alternatives ) )
            {
            // InternalOseeDsl.g:974:1: ( ( rule__RelationTypePredicate__Alternatives ) )
            // InternalOseeDsl.g:975:1: ( rule__RelationTypePredicate__Alternatives )
            {
             before(grammarAccess.getRelationTypePredicateAccess().getAlternatives()); 
            // InternalOseeDsl.g:976:1: ( rule__RelationTypePredicate__Alternatives )
            // InternalOseeDsl.g:976:2: rule__RelationTypePredicate__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypePredicate__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getRelationTypePredicateAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleRelationTypePredicate"


    // $ANTLR start "entryRuleObjectRestriction"
    // InternalOseeDsl.g:988:1: entryRuleObjectRestriction : ruleObjectRestriction EOF ;
    public final void entryRuleObjectRestriction() throws RecognitionException {
        try {
            // InternalOseeDsl.g:989:1: ( ruleObjectRestriction EOF )
            // InternalOseeDsl.g:990:1: ruleObjectRestriction EOF
            {
             before(grammarAccess.getObjectRestrictionRule()); 
            pushFollow(FOLLOW_1);
            ruleObjectRestriction();

            state._fsp--;

             after(grammarAccess.getObjectRestrictionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleObjectRestriction"


    // $ANTLR start "ruleObjectRestriction"
    // InternalOseeDsl.g:997:1: ruleObjectRestriction : ( ( rule__ObjectRestriction__Alternatives ) ) ;
    public final void ruleObjectRestriction() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1001:2: ( ( ( rule__ObjectRestriction__Alternatives ) ) )
            // InternalOseeDsl.g:1002:1: ( ( rule__ObjectRestriction__Alternatives ) )
            {
            // InternalOseeDsl.g:1002:1: ( ( rule__ObjectRestriction__Alternatives ) )
            // InternalOseeDsl.g:1003:1: ( rule__ObjectRestriction__Alternatives )
            {
             before(grammarAccess.getObjectRestrictionAccess().getAlternatives()); 
            // InternalOseeDsl.g:1004:1: ( rule__ObjectRestriction__Alternatives )
            // InternalOseeDsl.g:1004:2: rule__ObjectRestriction__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__ObjectRestriction__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getObjectRestrictionAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleObjectRestriction"


    // $ANTLR start "entryRuleArtifactMatchRestriction"
    // InternalOseeDsl.g:1016:1: entryRuleArtifactMatchRestriction : ruleArtifactMatchRestriction EOF ;
    public final void entryRuleArtifactMatchRestriction() throws RecognitionException {
        try {
            // InternalOseeDsl.g:1017:1: ( ruleArtifactMatchRestriction EOF )
            // InternalOseeDsl.g:1018:1: ruleArtifactMatchRestriction EOF
            {
             before(grammarAccess.getArtifactMatchRestrictionRule()); 
            pushFollow(FOLLOW_1);
            ruleArtifactMatchRestriction();

            state._fsp--;

             after(grammarAccess.getArtifactMatchRestrictionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleArtifactMatchRestriction"


    // $ANTLR start "ruleArtifactMatchRestriction"
    // InternalOseeDsl.g:1025:1: ruleArtifactMatchRestriction : ( ( rule__ArtifactMatchRestriction__Group__0 ) ) ;
    public final void ruleArtifactMatchRestriction() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1029:2: ( ( ( rule__ArtifactMatchRestriction__Group__0 ) ) )
            // InternalOseeDsl.g:1030:1: ( ( rule__ArtifactMatchRestriction__Group__0 ) )
            {
            // InternalOseeDsl.g:1030:1: ( ( rule__ArtifactMatchRestriction__Group__0 ) )
            // InternalOseeDsl.g:1031:1: ( rule__ArtifactMatchRestriction__Group__0 )
            {
             before(grammarAccess.getArtifactMatchRestrictionAccess().getGroup()); 
            // InternalOseeDsl.g:1032:1: ( rule__ArtifactMatchRestriction__Group__0 )
            // InternalOseeDsl.g:1032:2: rule__ArtifactMatchRestriction__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ArtifactMatchRestriction__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getArtifactMatchRestrictionAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleArtifactMatchRestriction"


    // $ANTLR start "entryRuleArtifactTypeRestriction"
    // InternalOseeDsl.g:1044:1: entryRuleArtifactTypeRestriction : ruleArtifactTypeRestriction EOF ;
    public final void entryRuleArtifactTypeRestriction() throws RecognitionException {
        try {
            // InternalOseeDsl.g:1045:1: ( ruleArtifactTypeRestriction EOF )
            // InternalOseeDsl.g:1046:1: ruleArtifactTypeRestriction EOF
            {
             before(grammarAccess.getArtifactTypeRestrictionRule()); 
            pushFollow(FOLLOW_1);
            ruleArtifactTypeRestriction();

            state._fsp--;

             after(grammarAccess.getArtifactTypeRestrictionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleArtifactTypeRestriction"


    // $ANTLR start "ruleArtifactTypeRestriction"
    // InternalOseeDsl.g:1053:1: ruleArtifactTypeRestriction : ( ( rule__ArtifactTypeRestriction__Group__0 ) ) ;
    public final void ruleArtifactTypeRestriction() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1057:2: ( ( ( rule__ArtifactTypeRestriction__Group__0 ) ) )
            // InternalOseeDsl.g:1058:1: ( ( rule__ArtifactTypeRestriction__Group__0 ) )
            {
            // InternalOseeDsl.g:1058:1: ( ( rule__ArtifactTypeRestriction__Group__0 ) )
            // InternalOseeDsl.g:1059:1: ( rule__ArtifactTypeRestriction__Group__0 )
            {
             before(grammarAccess.getArtifactTypeRestrictionAccess().getGroup()); 
            // InternalOseeDsl.g:1060:1: ( rule__ArtifactTypeRestriction__Group__0 )
            // InternalOseeDsl.g:1060:2: rule__ArtifactTypeRestriction__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ArtifactTypeRestriction__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getArtifactTypeRestrictionAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleArtifactTypeRestriction"


    // $ANTLR start "entryRuleAttributeTypeRestriction"
    // InternalOseeDsl.g:1072:1: entryRuleAttributeTypeRestriction : ruleAttributeTypeRestriction EOF ;
    public final void entryRuleAttributeTypeRestriction() throws RecognitionException {
        try {
            // InternalOseeDsl.g:1073:1: ( ruleAttributeTypeRestriction EOF )
            // InternalOseeDsl.g:1074:1: ruleAttributeTypeRestriction EOF
            {
             before(grammarAccess.getAttributeTypeRestrictionRule()); 
            pushFollow(FOLLOW_1);
            ruleAttributeTypeRestriction();

            state._fsp--;

             after(grammarAccess.getAttributeTypeRestrictionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleAttributeTypeRestriction"


    // $ANTLR start "ruleAttributeTypeRestriction"
    // InternalOseeDsl.g:1081:1: ruleAttributeTypeRestriction : ( ( rule__AttributeTypeRestriction__Group__0 ) ) ;
    public final void ruleAttributeTypeRestriction() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1085:2: ( ( ( rule__AttributeTypeRestriction__Group__0 ) ) )
            // InternalOseeDsl.g:1086:1: ( ( rule__AttributeTypeRestriction__Group__0 ) )
            {
            // InternalOseeDsl.g:1086:1: ( ( rule__AttributeTypeRestriction__Group__0 ) )
            // InternalOseeDsl.g:1087:1: ( rule__AttributeTypeRestriction__Group__0 )
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getGroup()); 
            // InternalOseeDsl.g:1088:1: ( rule__AttributeTypeRestriction__Group__0 )
            // InternalOseeDsl.g:1088:2: rule__AttributeTypeRestriction__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getAttributeTypeRestrictionAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAttributeTypeRestriction"


    // $ANTLR start "entryRuleRelationTypeRestriction"
    // InternalOseeDsl.g:1102:1: entryRuleRelationTypeRestriction : ruleRelationTypeRestriction EOF ;
    public final void entryRuleRelationTypeRestriction() throws RecognitionException {
        try {
            // InternalOseeDsl.g:1103:1: ( ruleRelationTypeRestriction EOF )
            // InternalOseeDsl.g:1104:1: ruleRelationTypeRestriction EOF
            {
             before(grammarAccess.getRelationTypeRestrictionRule()); 
            pushFollow(FOLLOW_1);
            ruleRelationTypeRestriction();

            state._fsp--;

             after(grammarAccess.getRelationTypeRestrictionRule()); 
            match(input,EOF,FOLLOW_2); 

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
    // $ANTLR end "entryRuleRelationTypeRestriction"


    // $ANTLR start "ruleRelationTypeRestriction"
    // InternalOseeDsl.g:1111:1: ruleRelationTypeRestriction : ( ( rule__RelationTypeRestriction__Group__0 ) ) ;
    public final void ruleRelationTypeRestriction() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1115:2: ( ( ( rule__RelationTypeRestriction__Group__0 ) ) )
            // InternalOseeDsl.g:1116:1: ( ( rule__RelationTypeRestriction__Group__0 ) )
            {
            // InternalOseeDsl.g:1116:1: ( ( rule__RelationTypeRestriction__Group__0 ) )
            // InternalOseeDsl.g:1117:1: ( rule__RelationTypeRestriction__Group__0 )
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getGroup()); 
            // InternalOseeDsl.g:1118:1: ( rule__RelationTypeRestriction__Group__0 )
            // InternalOseeDsl.g:1118:2: rule__RelationTypeRestriction__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getRelationTypeRestrictionAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleRelationTypeRestriction"


    // $ANTLR start "ruleRelationMultiplicityEnum"
    // InternalOseeDsl.g:1131:1: ruleRelationMultiplicityEnum : ( ( rule__RelationMultiplicityEnum__Alternatives ) ) ;
    public final void ruleRelationMultiplicityEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1135:1: ( ( ( rule__RelationMultiplicityEnum__Alternatives ) ) )
            // InternalOseeDsl.g:1136:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            {
            // InternalOseeDsl.g:1136:1: ( ( rule__RelationMultiplicityEnum__Alternatives ) )
            // InternalOseeDsl.g:1137:1: ( rule__RelationMultiplicityEnum__Alternatives )
            {
             before(grammarAccess.getRelationMultiplicityEnumAccess().getAlternatives()); 
            // InternalOseeDsl.g:1138:1: ( rule__RelationMultiplicityEnum__Alternatives )
            // InternalOseeDsl.g:1138:2: rule__RelationMultiplicityEnum__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__RelationMultiplicityEnum__Alternatives();

            state._fsp--;


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
    // $ANTLR end "ruleRelationMultiplicityEnum"


    // $ANTLR start "ruleCompareOp"
    // InternalOseeDsl.g:1150:1: ruleCompareOp : ( ( rule__CompareOp__Alternatives ) ) ;
    public final void ruleCompareOp() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1154:1: ( ( ( rule__CompareOp__Alternatives ) ) )
            // InternalOseeDsl.g:1155:1: ( ( rule__CompareOp__Alternatives ) )
            {
            // InternalOseeDsl.g:1155:1: ( ( rule__CompareOp__Alternatives ) )
            // InternalOseeDsl.g:1156:1: ( rule__CompareOp__Alternatives )
            {
             before(grammarAccess.getCompareOpAccess().getAlternatives()); 
            // InternalOseeDsl.g:1157:1: ( rule__CompareOp__Alternatives )
            // InternalOseeDsl.g:1157:2: rule__CompareOp__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__CompareOp__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getCompareOpAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleCompareOp"


    // $ANTLR start "ruleXLogicOperator"
    // InternalOseeDsl.g:1169:1: ruleXLogicOperator : ( ( rule__XLogicOperator__Alternatives ) ) ;
    public final void ruleXLogicOperator() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1173:1: ( ( ( rule__XLogicOperator__Alternatives ) ) )
            // InternalOseeDsl.g:1174:1: ( ( rule__XLogicOperator__Alternatives ) )
            {
            // InternalOseeDsl.g:1174:1: ( ( rule__XLogicOperator__Alternatives ) )
            // InternalOseeDsl.g:1175:1: ( rule__XLogicOperator__Alternatives )
            {
             before(grammarAccess.getXLogicOperatorAccess().getAlternatives()); 
            // InternalOseeDsl.g:1176:1: ( rule__XLogicOperator__Alternatives )
            // InternalOseeDsl.g:1176:2: rule__XLogicOperator__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__XLogicOperator__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getXLogicOperatorAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXLogicOperator"


    // $ANTLR start "ruleMatchField"
    // InternalOseeDsl.g:1188:1: ruleMatchField : ( ( rule__MatchField__Alternatives ) ) ;
    public final void ruleMatchField() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1192:1: ( ( ( rule__MatchField__Alternatives ) ) )
            // InternalOseeDsl.g:1193:1: ( ( rule__MatchField__Alternatives ) )
            {
            // InternalOseeDsl.g:1193:1: ( ( rule__MatchField__Alternatives ) )
            // InternalOseeDsl.g:1194:1: ( rule__MatchField__Alternatives )
            {
             before(grammarAccess.getMatchFieldAccess().getAlternatives()); 
            // InternalOseeDsl.g:1195:1: ( rule__MatchField__Alternatives )
            // InternalOseeDsl.g:1195:2: rule__MatchField__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__MatchField__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getMatchFieldAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleMatchField"


    // $ANTLR start "ruleAccessPermissionEnum"
    // InternalOseeDsl.g:1207:1: ruleAccessPermissionEnum : ( ( rule__AccessPermissionEnum__Alternatives ) ) ;
    public final void ruleAccessPermissionEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1211:1: ( ( ( rule__AccessPermissionEnum__Alternatives ) ) )
            // InternalOseeDsl.g:1212:1: ( ( rule__AccessPermissionEnum__Alternatives ) )
            {
            // InternalOseeDsl.g:1212:1: ( ( rule__AccessPermissionEnum__Alternatives ) )
            // InternalOseeDsl.g:1213:1: ( rule__AccessPermissionEnum__Alternatives )
            {
             before(grammarAccess.getAccessPermissionEnumAccess().getAlternatives()); 
            // InternalOseeDsl.g:1214:1: ( rule__AccessPermissionEnum__Alternatives )
            // InternalOseeDsl.g:1214:2: rule__AccessPermissionEnum__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__AccessPermissionEnum__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getAccessPermissionEnumAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleAccessPermissionEnum"


    // $ANTLR start "ruleRelationTypeMatch"
    // InternalOseeDsl.g:1226:1: ruleRelationTypeMatch : ( ( 'ALL' ) ) ;
    public final void ruleRelationTypeMatch() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1230:1: ( ( ( 'ALL' ) ) )
            // InternalOseeDsl.g:1231:1: ( ( 'ALL' ) )
            {
            // InternalOseeDsl.g:1231:1: ( ( 'ALL' ) )
            // InternalOseeDsl.g:1232:1: ( 'ALL' )
            {
             before(grammarAccess.getRelationTypeMatchAccess().getALLEnumLiteralDeclaration()); 
            // InternalOseeDsl.g:1233:1: ( 'ALL' )
            // InternalOseeDsl.g:1233:3: 'ALL'
            {
            match(input,12,FOLLOW_2); 

            }

             after(grammarAccess.getRelationTypeMatchAccess().getALLEnumLiteralDeclaration()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleRelationTypeMatch"


    // $ANTLR start "ruleXRelationSideEnum"
    // InternalOseeDsl.g:1246:1: ruleXRelationSideEnum : ( ( rule__XRelationSideEnum__Alternatives ) ) ;
    public final void ruleXRelationSideEnum() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1250:1: ( ( ( rule__XRelationSideEnum__Alternatives ) ) )
            // InternalOseeDsl.g:1251:1: ( ( rule__XRelationSideEnum__Alternatives ) )
            {
            // InternalOseeDsl.g:1251:1: ( ( rule__XRelationSideEnum__Alternatives ) )
            // InternalOseeDsl.g:1252:1: ( rule__XRelationSideEnum__Alternatives )
            {
             before(grammarAccess.getXRelationSideEnumAccess().getAlternatives()); 
            // InternalOseeDsl.g:1253:1: ( rule__XRelationSideEnum__Alternatives )
            // InternalOseeDsl.g:1253:2: rule__XRelationSideEnum__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__XRelationSideEnum__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getXRelationSideEnumAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleXRelationSideEnum"


    // $ANTLR start "rule__OseeDsl__Alternatives_1"
    // InternalOseeDsl.g:1264:1: rule__OseeDsl__Alternatives_1 : ( ( ( rule__OseeDsl__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeDsl__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeDsl__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeDsl__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeDsl__EnumOverridesAssignment_1_4 ) ) | ( ( rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5 ) ) );
    public final void rule__OseeDsl__Alternatives_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1268:1: ( ( ( rule__OseeDsl__ArtifactTypesAssignment_1_0 ) ) | ( ( rule__OseeDsl__RelationTypesAssignment_1_1 ) ) | ( ( rule__OseeDsl__AttributeTypesAssignment_1_2 ) ) | ( ( rule__OseeDsl__EnumTypesAssignment_1_3 ) ) | ( ( rule__OseeDsl__EnumOverridesAssignment_1_4 ) ) | ( ( rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5 ) ) )
            int alt1=6;
            switch ( input.LA(1) ) {
            case 52:
            case 97:
                {
                alt1=1;
                }
                break;
            case 79:
                {
                alt1=2;
                }
                break;
            case 60:
                {
                alt1=3;
                }
                break;
            case 71:
                {
                alt1=4;
                }
                break;
            case 74:
                {
                alt1=5;
                }
                break;
            case 77:
                {
                alt1=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // InternalOseeDsl.g:1269:1: ( ( rule__OseeDsl__ArtifactTypesAssignment_1_0 ) )
                    {
                    // InternalOseeDsl.g:1269:1: ( ( rule__OseeDsl__ArtifactTypesAssignment_1_0 ) )
                    // InternalOseeDsl.g:1270:1: ( rule__OseeDsl__ArtifactTypesAssignment_1_0 )
                    {
                     before(grammarAccess.getOseeDslAccess().getArtifactTypesAssignment_1_0()); 
                    // InternalOseeDsl.g:1271:1: ( rule__OseeDsl__ArtifactTypesAssignment_1_0 )
                    // InternalOseeDsl.g:1271:2: rule__OseeDsl__ArtifactTypesAssignment_1_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__OseeDsl__ArtifactTypesAssignment_1_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getOseeDslAccess().getArtifactTypesAssignment_1_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1275:6: ( ( rule__OseeDsl__RelationTypesAssignment_1_1 ) )
                    {
                    // InternalOseeDsl.g:1275:6: ( ( rule__OseeDsl__RelationTypesAssignment_1_1 ) )
                    // InternalOseeDsl.g:1276:1: ( rule__OseeDsl__RelationTypesAssignment_1_1 )
                    {
                     before(grammarAccess.getOseeDslAccess().getRelationTypesAssignment_1_1()); 
                    // InternalOseeDsl.g:1277:1: ( rule__OseeDsl__RelationTypesAssignment_1_1 )
                    // InternalOseeDsl.g:1277:2: rule__OseeDsl__RelationTypesAssignment_1_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__OseeDsl__RelationTypesAssignment_1_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getOseeDslAccess().getRelationTypesAssignment_1_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1281:6: ( ( rule__OseeDsl__AttributeTypesAssignment_1_2 ) )
                    {
                    // InternalOseeDsl.g:1281:6: ( ( rule__OseeDsl__AttributeTypesAssignment_1_2 ) )
                    // InternalOseeDsl.g:1282:1: ( rule__OseeDsl__AttributeTypesAssignment_1_2 )
                    {
                     before(grammarAccess.getOseeDslAccess().getAttributeTypesAssignment_1_2()); 
                    // InternalOseeDsl.g:1283:1: ( rule__OseeDsl__AttributeTypesAssignment_1_2 )
                    // InternalOseeDsl.g:1283:2: rule__OseeDsl__AttributeTypesAssignment_1_2
                    {
                    pushFollow(FOLLOW_2);
                    rule__OseeDsl__AttributeTypesAssignment_1_2();

                    state._fsp--;


                    }

                     after(grammarAccess.getOseeDslAccess().getAttributeTypesAssignment_1_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:1287:6: ( ( rule__OseeDsl__EnumTypesAssignment_1_3 ) )
                    {
                    // InternalOseeDsl.g:1287:6: ( ( rule__OseeDsl__EnumTypesAssignment_1_3 ) )
                    // InternalOseeDsl.g:1288:1: ( rule__OseeDsl__EnumTypesAssignment_1_3 )
                    {
                     before(grammarAccess.getOseeDslAccess().getEnumTypesAssignment_1_3()); 
                    // InternalOseeDsl.g:1289:1: ( rule__OseeDsl__EnumTypesAssignment_1_3 )
                    // InternalOseeDsl.g:1289:2: rule__OseeDsl__EnumTypesAssignment_1_3
                    {
                    pushFollow(FOLLOW_2);
                    rule__OseeDsl__EnumTypesAssignment_1_3();

                    state._fsp--;


                    }

                     after(grammarAccess.getOseeDslAccess().getEnumTypesAssignment_1_3()); 

                    }


                    }
                    break;
                case 5 :
                    // InternalOseeDsl.g:1293:6: ( ( rule__OseeDsl__EnumOverridesAssignment_1_4 ) )
                    {
                    // InternalOseeDsl.g:1293:6: ( ( rule__OseeDsl__EnumOverridesAssignment_1_4 ) )
                    // InternalOseeDsl.g:1294:1: ( rule__OseeDsl__EnumOverridesAssignment_1_4 )
                    {
                     before(grammarAccess.getOseeDslAccess().getEnumOverridesAssignment_1_4()); 
                    // InternalOseeDsl.g:1295:1: ( rule__OseeDsl__EnumOverridesAssignment_1_4 )
                    // InternalOseeDsl.g:1295:2: rule__OseeDsl__EnumOverridesAssignment_1_4
                    {
                    pushFollow(FOLLOW_2);
                    rule__OseeDsl__EnumOverridesAssignment_1_4();

                    state._fsp--;


                    }

                     after(grammarAccess.getOseeDslAccess().getEnumOverridesAssignment_1_4()); 

                    }


                    }
                    break;
                case 6 :
                    // InternalOseeDsl.g:1299:6: ( ( rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5 ) )
                    {
                    // InternalOseeDsl.g:1299:6: ( ( rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5 ) )
                    // InternalOseeDsl.g:1300:1: ( rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5 )
                    {
                     before(grammarAccess.getOseeDslAccess().getArtifactTypeOverridesAssignment_1_5()); 
                    // InternalOseeDsl.g:1301:1: ( rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5 )
                    // InternalOseeDsl.g:1301:2: rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5
                    {
                    pushFollow(FOLLOW_2);
                    rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5();

                    state._fsp--;


                    }

                     after(grammarAccess.getOseeDslAccess().getArtifactTypeOverridesAssignment_1_5()); 

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
    // $ANTLR end "rule__OseeDsl__Alternatives_1"


    // $ANTLR start "rule__OseeDsl__Alternatives_2"
    // InternalOseeDsl.g:1310:1: rule__OseeDsl__Alternatives_2 : ( ( ( rule__OseeDsl__ArtifactMatchRefsAssignment_2_0 ) ) | ( ( rule__OseeDsl__AccessDeclarationsAssignment_2_1 ) ) | ( ( rule__OseeDsl__RoleDeclarationsAssignment_2_2 ) ) );
    public final void rule__OseeDsl__Alternatives_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1314:1: ( ( ( rule__OseeDsl__ArtifactMatchRefsAssignment_2_0 ) ) | ( ( rule__OseeDsl__AccessDeclarationsAssignment_2_1 ) ) | ( ( rule__OseeDsl__RoleDeclarationsAssignment_2_2 ) ) )
            int alt2=3;
            switch ( input.LA(1) ) {
            case 88:
                {
                alt2=1;
                }
                break;
            case 92:
                {
                alt2=2;
                }
                break;
            case 91:
                {
                alt2=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // InternalOseeDsl.g:1315:1: ( ( rule__OseeDsl__ArtifactMatchRefsAssignment_2_0 ) )
                    {
                    // InternalOseeDsl.g:1315:1: ( ( rule__OseeDsl__ArtifactMatchRefsAssignment_2_0 ) )
                    // InternalOseeDsl.g:1316:1: ( rule__OseeDsl__ArtifactMatchRefsAssignment_2_0 )
                    {
                     before(grammarAccess.getOseeDslAccess().getArtifactMatchRefsAssignment_2_0()); 
                    // InternalOseeDsl.g:1317:1: ( rule__OseeDsl__ArtifactMatchRefsAssignment_2_0 )
                    // InternalOseeDsl.g:1317:2: rule__OseeDsl__ArtifactMatchRefsAssignment_2_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__OseeDsl__ArtifactMatchRefsAssignment_2_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getOseeDslAccess().getArtifactMatchRefsAssignment_2_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1321:6: ( ( rule__OseeDsl__AccessDeclarationsAssignment_2_1 ) )
                    {
                    // InternalOseeDsl.g:1321:6: ( ( rule__OseeDsl__AccessDeclarationsAssignment_2_1 ) )
                    // InternalOseeDsl.g:1322:1: ( rule__OseeDsl__AccessDeclarationsAssignment_2_1 )
                    {
                     before(grammarAccess.getOseeDslAccess().getAccessDeclarationsAssignment_2_1()); 
                    // InternalOseeDsl.g:1323:1: ( rule__OseeDsl__AccessDeclarationsAssignment_2_1 )
                    // InternalOseeDsl.g:1323:2: rule__OseeDsl__AccessDeclarationsAssignment_2_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__OseeDsl__AccessDeclarationsAssignment_2_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getOseeDslAccess().getAccessDeclarationsAssignment_2_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1327:6: ( ( rule__OseeDsl__RoleDeclarationsAssignment_2_2 ) )
                    {
                    // InternalOseeDsl.g:1327:6: ( ( rule__OseeDsl__RoleDeclarationsAssignment_2_2 ) )
                    // InternalOseeDsl.g:1328:1: ( rule__OseeDsl__RoleDeclarationsAssignment_2_2 )
                    {
                     before(grammarAccess.getOseeDslAccess().getRoleDeclarationsAssignment_2_2()); 
                    // InternalOseeDsl.g:1329:1: ( rule__OseeDsl__RoleDeclarationsAssignment_2_2 )
                    // InternalOseeDsl.g:1329:2: rule__OseeDsl__RoleDeclarationsAssignment_2_2
                    {
                    pushFollow(FOLLOW_2);
                    rule__OseeDsl__RoleDeclarationsAssignment_2_2();

                    state._fsp--;


                    }

                     after(grammarAccess.getOseeDslAccess().getRoleDeclarationsAssignment_2_2()); 

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
    // $ANTLR end "rule__OseeDsl__Alternatives_2"


    // $ANTLR start "rule__OseeType__Alternatives"
    // InternalOseeDsl.g:1339:1: rule__OseeType__Alternatives : ( ( ruleXArtifactType ) | ( ruleXRelationType ) | ( ruleXAttributeType ) | ( ruleXOseeEnumType ) );
    public final void rule__OseeType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1343:1: ( ( ruleXArtifactType ) | ( ruleXRelationType ) | ( ruleXAttributeType ) | ( ruleXOseeEnumType ) )
            int alt3=4;
            switch ( input.LA(1) ) {
            case 52:
            case 97:
                {
                alt3=1;
                }
                break;
            case 79:
                {
                alt3=2;
                }
                break;
            case 60:
                {
                alt3=3;
                }
                break;
            case 71:
                {
                alt3=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // InternalOseeDsl.g:1344:1: ( ruleXArtifactType )
                    {
                    // InternalOseeDsl.g:1344:1: ( ruleXArtifactType )
                    // InternalOseeDsl.g:1345:1: ruleXArtifactType
                    {
                     before(grammarAccess.getOseeTypeAccess().getXArtifactTypeParserRuleCall_0()); 
                    pushFollow(FOLLOW_2);
                    ruleXArtifactType();

                    state._fsp--;

                     after(grammarAccess.getOseeTypeAccess().getXArtifactTypeParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1350:6: ( ruleXRelationType )
                    {
                    // InternalOseeDsl.g:1350:6: ( ruleXRelationType )
                    // InternalOseeDsl.g:1351:1: ruleXRelationType
                    {
                     before(grammarAccess.getOseeTypeAccess().getXRelationTypeParserRuleCall_1()); 
                    pushFollow(FOLLOW_2);
                    ruleXRelationType();

                    state._fsp--;

                     after(grammarAccess.getOseeTypeAccess().getXRelationTypeParserRuleCall_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1356:6: ( ruleXAttributeType )
                    {
                    // InternalOseeDsl.g:1356:6: ( ruleXAttributeType )
                    // InternalOseeDsl.g:1357:1: ruleXAttributeType
                    {
                     before(grammarAccess.getOseeTypeAccess().getXAttributeTypeParserRuleCall_2()); 
                    pushFollow(FOLLOW_2);
                    ruleXAttributeType();

                    state._fsp--;

                     after(grammarAccess.getOseeTypeAccess().getXAttributeTypeParserRuleCall_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:1362:6: ( ruleXOseeEnumType )
                    {
                    // InternalOseeDsl.g:1362:6: ( ruleXOseeEnumType )
                    // InternalOseeDsl.g:1363:1: ruleXOseeEnumType
                    {
                     before(grammarAccess.getOseeTypeAccess().getXOseeEnumTypeParserRuleCall_3()); 
                    pushFollow(FOLLOW_2);
                    ruleXOseeEnumType();

                    state._fsp--;

                     after(grammarAccess.getOseeTypeAccess().getXOseeEnumTypeParserRuleCall_3()); 

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
    // $ANTLR end "rule__OseeType__Alternatives"


    // $ANTLR start "rule__XAttributeType__DataProviderAlternatives_9_0"
    // InternalOseeDsl.g:1373:1: rule__XAttributeType__DataProviderAlternatives_9_0 : ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__XAttributeType__DataProviderAlternatives_9_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1377:1: ( ( 'DefaultAttributeDataProvider' ) | ( 'UriAttributeDataProvider' ) | ( ruleQUALIFIED_NAME ) )
            int alt4=3;
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
            case RULE_ID:
                {
                alt4=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // InternalOseeDsl.g:1378:1: ( 'DefaultAttributeDataProvider' )
                    {
                    // InternalOseeDsl.g:1378:1: ( 'DefaultAttributeDataProvider' )
                    // InternalOseeDsl.g:1379:1: 'DefaultAttributeDataProvider'
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_9_0_0()); 
                    match(input,13,FOLLOW_2); 
                     after(grammarAccess.getXAttributeTypeAccess().getDataProviderDefaultAttributeDataProviderKeyword_9_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1386:6: ( 'UriAttributeDataProvider' )
                    {
                    // InternalOseeDsl.g:1386:6: ( 'UriAttributeDataProvider' )
                    // InternalOseeDsl.g:1387:1: 'UriAttributeDataProvider'
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_9_0_1()); 
                    match(input,14,FOLLOW_2); 
                     after(grammarAccess.getXAttributeTypeAccess().getDataProviderUriAttributeDataProviderKeyword_9_0_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1394:6: ( ruleQUALIFIED_NAME )
                    {
                    // InternalOseeDsl.g:1394:6: ( ruleQUALIFIED_NAME )
                    // InternalOseeDsl.g:1395:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_9_0_2()); 
                    pushFollow(FOLLOW_2);
                    ruleQUALIFIED_NAME();

                    state._fsp--;

                     after(grammarAccess.getXAttributeTypeAccess().getDataProviderQUALIFIED_NAMEParserRuleCall_9_0_2()); 

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
    // $ANTLR end "rule__XAttributeType__DataProviderAlternatives_9_0"


    // $ANTLR start "rule__XAttributeType__MaxAlternatives_13_0"
    // InternalOseeDsl.g:1405:1: rule__XAttributeType__MaxAlternatives_13_0 : ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) );
    public final void rule__XAttributeType__MaxAlternatives_13_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1409:1: ( ( RULE_WHOLE_NUM_STR ) | ( 'unlimited' ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==RULE_WHOLE_NUM_STR) ) {
                alt5=1;
            }
            else if ( (LA5_0==15) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // InternalOseeDsl.g:1410:1: ( RULE_WHOLE_NUM_STR )
                    {
                    // InternalOseeDsl.g:1410:1: ( RULE_WHOLE_NUM_STR )
                    // InternalOseeDsl.g:1411:1: RULE_WHOLE_NUM_STR
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_13_0_0()); 
                    match(input,RULE_WHOLE_NUM_STR,FOLLOW_2); 
                     after(grammarAccess.getXAttributeTypeAccess().getMaxWHOLE_NUM_STRTerminalRuleCall_13_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1416:6: ( 'unlimited' )
                    {
                    // InternalOseeDsl.g:1416:6: ( 'unlimited' )
                    // InternalOseeDsl.g:1417:1: 'unlimited'
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getMaxUnlimitedKeyword_13_0_1()); 
                    match(input,15,FOLLOW_2); 
                     after(grammarAccess.getXAttributeTypeAccess().getMaxUnlimitedKeyword_13_0_1()); 

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
    // $ANTLR end "rule__XAttributeType__MaxAlternatives_13_0"


    // $ANTLR start "rule__XAttributeType__TaggerIdAlternatives_14_0_1_0"
    // InternalOseeDsl.g:1429:1: rule__XAttributeType__TaggerIdAlternatives_14_0_1_0 : ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__XAttributeType__TaggerIdAlternatives_14_0_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1433:1: ( ( 'DefaultAttributeTaggerProvider' ) | ( ruleQUALIFIED_NAME ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==16) ) {
                alt6=1;
            }
            else if ( (LA6_0==RULE_ID) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // InternalOseeDsl.g:1434:1: ( 'DefaultAttributeTaggerProvider' )
                    {
                    // InternalOseeDsl.g:1434:1: ( 'DefaultAttributeTaggerProvider' )
                    // InternalOseeDsl.g:1435:1: 'DefaultAttributeTaggerProvider'
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_14_0_1_0_0()); 
                    match(input,16,FOLLOW_2); 
                     after(grammarAccess.getXAttributeTypeAccess().getTaggerIdDefaultAttributeTaggerProviderKeyword_14_0_1_0_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1442:6: ( ruleQUALIFIED_NAME )
                    {
                    // InternalOseeDsl.g:1442:6: ( ruleQUALIFIED_NAME )
                    // InternalOseeDsl.g:1443:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_14_0_1_0_1()); 
                    pushFollow(FOLLOW_2);
                    ruleQUALIFIED_NAME();

                    state._fsp--;

                     after(grammarAccess.getXAttributeTypeAccess().getTaggerIdQUALIFIED_NAMEParserRuleCall_14_0_1_0_1()); 

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
    // $ANTLR end "rule__XAttributeType__TaggerIdAlternatives_14_0_1_0"


    // $ANTLR start "rule__AttributeBaseType__Alternatives"
    // InternalOseeDsl.g:1453:1: rule__AttributeBaseType__Alternatives : ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'LongAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'ArtifactReferenceAttribute' ) | ( 'BranchReferenceAttribute' ) | ( 'WordAttribute' ) | ( 'OutlineNumberAttribute' ) | ( ruleQUALIFIED_NAME ) );
    public final void rule__AttributeBaseType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1457:1: ( ( 'BooleanAttribute' ) | ( 'CompressedContentAttribute' ) | ( 'DateAttribute' ) | ( 'EnumeratedAttribute' ) | ( 'FloatingPointAttribute' ) | ( 'IntegerAttribute' ) | ( 'LongAttribute' ) | ( 'JavaObjectAttribute' ) | ( 'StringAttribute' ) | ( 'ArtifactReferenceAttribute' ) | ( 'BranchReferenceAttribute' ) | ( 'WordAttribute' ) | ( 'OutlineNumberAttribute' ) | ( ruleQUALIFIED_NAME ) )
            int alt7=14;
            switch ( input.LA(1) ) {
            case 17:
                {
                alt7=1;
                }
                break;
            case 18:
                {
                alt7=2;
                }
                break;
            case 19:
                {
                alt7=3;
                }
                break;
            case 20:
                {
                alt7=4;
                }
                break;
            case 21:
                {
                alt7=5;
                }
                break;
            case 22:
                {
                alt7=6;
                }
                break;
            case 23:
                {
                alt7=7;
                }
                break;
            case 24:
                {
                alt7=8;
                }
                break;
            case 25:
                {
                alt7=9;
                }
                break;
            case 26:
                {
                alt7=10;
                }
                break;
            case 27:
                {
                alt7=11;
                }
                break;
            case 28:
                {
                alt7=12;
                }
                break;
            case 29:
                {
                alt7=13;
                }
                break;
            case RULE_ID:
                {
                alt7=14;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // InternalOseeDsl.g:1458:1: ( 'BooleanAttribute' )
                    {
                    // InternalOseeDsl.g:1458:1: ( 'BooleanAttribute' )
                    // InternalOseeDsl.g:1459:1: 'BooleanAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 
                    match(input,17,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getBooleanAttributeKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1466:6: ( 'CompressedContentAttribute' )
                    {
                    // InternalOseeDsl.g:1466:6: ( 'CompressedContentAttribute' )
                    // InternalOseeDsl.g:1467:1: 'CompressedContentAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 
                    match(input,18,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getCompressedContentAttributeKeyword_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1474:6: ( 'DateAttribute' )
                    {
                    // InternalOseeDsl.g:1474:6: ( 'DateAttribute' )
                    // InternalOseeDsl.g:1475:1: 'DateAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 
                    match(input,19,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getDateAttributeKeyword_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:1482:6: ( 'EnumeratedAttribute' )
                    {
                    // InternalOseeDsl.g:1482:6: ( 'EnumeratedAttribute' )
                    // InternalOseeDsl.g:1483:1: 'EnumeratedAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 
                    match(input,20,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getEnumeratedAttributeKeyword_3()); 

                    }


                    }
                    break;
                case 5 :
                    // InternalOseeDsl.g:1490:6: ( 'FloatingPointAttribute' )
                    {
                    // InternalOseeDsl.g:1490:6: ( 'FloatingPointAttribute' )
                    // InternalOseeDsl.g:1491:1: 'FloatingPointAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 
                    match(input,21,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getFloatingPointAttributeKeyword_4()); 

                    }


                    }
                    break;
                case 6 :
                    // InternalOseeDsl.g:1498:6: ( 'IntegerAttribute' )
                    {
                    // InternalOseeDsl.g:1498:6: ( 'IntegerAttribute' )
                    // InternalOseeDsl.g:1499:1: 'IntegerAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 
                    match(input,22,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getIntegerAttributeKeyword_5()); 

                    }


                    }
                    break;
                case 7 :
                    // InternalOseeDsl.g:1506:6: ( 'LongAttribute' )
                    {
                    // InternalOseeDsl.g:1506:6: ( 'LongAttribute' )
                    // InternalOseeDsl.g:1507:1: 'LongAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getLongAttributeKeyword_6()); 
                    match(input,23,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getLongAttributeKeyword_6()); 

                    }


                    }
                    break;
                case 8 :
                    // InternalOseeDsl.g:1514:6: ( 'JavaObjectAttribute' )
                    {
                    // InternalOseeDsl.g:1514:6: ( 'JavaObjectAttribute' )
                    // InternalOseeDsl.g:1515:1: 'JavaObjectAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_7()); 
                    match(input,24,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getJavaObjectAttributeKeyword_7()); 

                    }


                    }
                    break;
                case 9 :
                    // InternalOseeDsl.g:1522:6: ( 'StringAttribute' )
                    {
                    // InternalOseeDsl.g:1522:6: ( 'StringAttribute' )
                    // InternalOseeDsl.g:1523:1: 'StringAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_8()); 
                    match(input,25,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getStringAttributeKeyword_8()); 

                    }


                    }
                    break;
                case 10 :
                    // InternalOseeDsl.g:1530:6: ( 'ArtifactReferenceAttribute' )
                    {
                    // InternalOseeDsl.g:1530:6: ( 'ArtifactReferenceAttribute' )
                    // InternalOseeDsl.g:1531:1: 'ArtifactReferenceAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getArtifactReferenceAttributeKeyword_9()); 
                    match(input,26,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getArtifactReferenceAttributeKeyword_9()); 

                    }


                    }
                    break;
                case 11 :
                    // InternalOseeDsl.g:1538:6: ( 'BranchReferenceAttribute' )
                    {
                    // InternalOseeDsl.g:1538:6: ( 'BranchReferenceAttribute' )
                    // InternalOseeDsl.g:1539:1: 'BranchReferenceAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getBranchReferenceAttributeKeyword_10()); 
                    match(input,27,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getBranchReferenceAttributeKeyword_10()); 

                    }


                    }
                    break;
                case 12 :
                    // InternalOseeDsl.g:1546:6: ( 'WordAttribute' )
                    {
                    // InternalOseeDsl.g:1546:6: ( 'WordAttribute' )
                    // InternalOseeDsl.g:1547:1: 'WordAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_11()); 
                    match(input,28,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getWordAttributeKeyword_11()); 

                    }


                    }
                    break;
                case 13 :
                    // InternalOseeDsl.g:1554:6: ( 'OutlineNumberAttribute' )
                    {
                    // InternalOseeDsl.g:1554:6: ( 'OutlineNumberAttribute' )
                    // InternalOseeDsl.g:1555:1: 'OutlineNumberAttribute'
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getOutlineNumberAttributeKeyword_12()); 
                    match(input,29,FOLLOW_2); 
                     after(grammarAccess.getAttributeBaseTypeAccess().getOutlineNumberAttributeKeyword_12()); 

                    }


                    }
                    break;
                case 14 :
                    // InternalOseeDsl.g:1562:6: ( ruleQUALIFIED_NAME )
                    {
                    // InternalOseeDsl.g:1562:6: ( ruleQUALIFIED_NAME )
                    // InternalOseeDsl.g:1563:1: ruleQUALIFIED_NAME
                    {
                     before(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_13()); 
                    pushFollow(FOLLOW_2);
                    ruleQUALIFIED_NAME();

                    state._fsp--;

                     after(grammarAccess.getAttributeBaseTypeAccess().getQUALIFIED_NAMEParserRuleCall_13()); 

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
    // $ANTLR end "rule__AttributeBaseType__Alternatives"


    // $ANTLR start "rule__OverrideOption__Alternatives"
    // InternalOseeDsl.g:1573:1: rule__OverrideOption__Alternatives : ( ( ruleAddEnum ) | ( ruleRemoveEnum ) );
    public final void rule__OverrideOption__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1577:1: ( ( ruleAddEnum ) | ( ruleRemoveEnum ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==75) ) {
                alt8=1;
            }
            else if ( (LA8_0==76) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // InternalOseeDsl.g:1578:1: ( ruleAddEnum )
                    {
                    // InternalOseeDsl.g:1578:1: ( ruleAddEnum )
                    // InternalOseeDsl.g:1579:1: ruleAddEnum
                    {
                     before(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0()); 
                    pushFollow(FOLLOW_2);
                    ruleAddEnum();

                    state._fsp--;

                     after(grammarAccess.getOverrideOptionAccess().getAddEnumParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1584:6: ( ruleRemoveEnum )
                    {
                    // InternalOseeDsl.g:1584:6: ( ruleRemoveEnum )
                    // InternalOseeDsl.g:1585:1: ruleRemoveEnum
                    {
                     before(grammarAccess.getOverrideOptionAccess().getRemoveEnumParserRuleCall_1()); 
                    pushFollow(FOLLOW_2);
                    ruleRemoveEnum();

                    state._fsp--;

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
    // $ANTLR end "rule__OverrideOption__Alternatives"


    // $ANTLR start "rule__AttributeOverrideOption__Alternatives"
    // InternalOseeDsl.g:1595:1: rule__AttributeOverrideOption__Alternatives : ( ( ruleAddAttribute ) | ( ruleRemoveAttribute ) | ( ruleUpdateAttribute ) );
    public final void rule__AttributeOverrideOption__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1599:1: ( ( ruleAddAttribute ) | ( ruleRemoveAttribute ) | ( ruleUpdateAttribute ) )
            int alt9=3;
            switch ( input.LA(1) ) {
            case 75:
                {
                alt9=1;
                }
                break;
            case 76:
                {
                alt9=2;
                }
                break;
            case 78:
                {
                alt9=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // InternalOseeDsl.g:1600:1: ( ruleAddAttribute )
                    {
                    // InternalOseeDsl.g:1600:1: ( ruleAddAttribute )
                    // InternalOseeDsl.g:1601:1: ruleAddAttribute
                    {
                     before(grammarAccess.getAttributeOverrideOptionAccess().getAddAttributeParserRuleCall_0()); 
                    pushFollow(FOLLOW_2);
                    ruleAddAttribute();

                    state._fsp--;

                     after(grammarAccess.getAttributeOverrideOptionAccess().getAddAttributeParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1606:6: ( ruleRemoveAttribute )
                    {
                    // InternalOseeDsl.g:1606:6: ( ruleRemoveAttribute )
                    // InternalOseeDsl.g:1607:1: ruleRemoveAttribute
                    {
                     before(grammarAccess.getAttributeOverrideOptionAccess().getRemoveAttributeParserRuleCall_1()); 
                    pushFollow(FOLLOW_2);
                    ruleRemoveAttribute();

                    state._fsp--;

                     after(grammarAccess.getAttributeOverrideOptionAccess().getRemoveAttributeParserRuleCall_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1612:6: ( ruleUpdateAttribute )
                    {
                    // InternalOseeDsl.g:1612:6: ( ruleUpdateAttribute )
                    // InternalOseeDsl.g:1613:1: ruleUpdateAttribute
                    {
                     before(grammarAccess.getAttributeOverrideOptionAccess().getUpdateAttributeParserRuleCall_2()); 
                    pushFollow(FOLLOW_2);
                    ruleUpdateAttribute();

                    state._fsp--;

                     after(grammarAccess.getAttributeOverrideOptionAccess().getUpdateAttributeParserRuleCall_2()); 

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
    // $ANTLR end "rule__AttributeOverrideOption__Alternatives"


    // $ANTLR start "rule__RelationOrderType__Alternatives"
    // InternalOseeDsl.g:1623:1: rule__RelationOrderType__Alternatives : ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) );
    public final void rule__RelationOrderType__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1627:1: ( ( 'Lexicographical_Ascending' ) | ( 'Lexicographical_Descending' ) | ( 'Unordered' ) | ( RULE_ID ) )
            int alt10=4;
            switch ( input.LA(1) ) {
            case 30:
                {
                alt10=1;
                }
                break;
            case 31:
                {
                alt10=2;
                }
                break;
            case 32:
                {
                alt10=3;
                }
                break;
            case RULE_ID:
                {
                alt10=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // InternalOseeDsl.g:1628:1: ( 'Lexicographical_Ascending' )
                    {
                    // InternalOseeDsl.g:1628:1: ( 'Lexicographical_Ascending' )
                    // InternalOseeDsl.g:1629:1: 'Lexicographical_Ascending'
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 
                    match(input,30,FOLLOW_2); 
                     after(grammarAccess.getRelationOrderTypeAccess().getLexicographical_AscendingKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1636:6: ( 'Lexicographical_Descending' )
                    {
                    // InternalOseeDsl.g:1636:6: ( 'Lexicographical_Descending' )
                    // InternalOseeDsl.g:1637:1: 'Lexicographical_Descending'
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 
                    match(input,31,FOLLOW_2); 
                     after(grammarAccess.getRelationOrderTypeAccess().getLexicographical_DescendingKeyword_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1644:6: ( 'Unordered' )
                    {
                    // InternalOseeDsl.g:1644:6: ( 'Unordered' )
                    // InternalOseeDsl.g:1645:1: 'Unordered'
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 
                    match(input,32,FOLLOW_2); 
                     after(grammarAccess.getRelationOrderTypeAccess().getUnorderedKeyword_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:1652:6: ( RULE_ID )
                    {
                    // InternalOseeDsl.g:1652:6: ( RULE_ID )
                    // InternalOseeDsl.g:1653:1: RULE_ID
                    {
                     before(grammarAccess.getRelationOrderTypeAccess().getIDTerminalRuleCall_3()); 
                    match(input,RULE_ID,FOLLOW_2); 
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
    // $ANTLR end "rule__RelationOrderType__Alternatives"


    // $ANTLR start "rule__Condition__Alternatives"
    // InternalOseeDsl.g:1664:1: rule__Condition__Alternatives : ( ( ruleSimpleCondition ) | ( ruleCompoundCondition ) );
    public final void rule__Condition__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1668:1: ( ( ruleSimpleCondition ) | ( ruleCompoundCondition ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>=41 && LA11_0<=44)) ) {
                alt11=1;
            }
            else if ( (LA11_0==86) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // InternalOseeDsl.g:1669:1: ( ruleSimpleCondition )
                    {
                    // InternalOseeDsl.g:1669:1: ( ruleSimpleCondition )
                    // InternalOseeDsl.g:1670:1: ruleSimpleCondition
                    {
                     before(grammarAccess.getConditionAccess().getSimpleConditionParserRuleCall_0()); 
                    pushFollow(FOLLOW_2);
                    ruleSimpleCondition();

                    state._fsp--;

                     after(grammarAccess.getConditionAccess().getSimpleConditionParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1675:6: ( ruleCompoundCondition )
                    {
                    // InternalOseeDsl.g:1675:6: ( ruleCompoundCondition )
                    // InternalOseeDsl.g:1676:1: ruleCompoundCondition
                    {
                     before(grammarAccess.getConditionAccess().getCompoundConditionParserRuleCall_1()); 
                    pushFollow(FOLLOW_2);
                    ruleCompoundCondition();

                    state._fsp--;

                     after(grammarAccess.getConditionAccess().getCompoundConditionParserRuleCall_1()); 

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
    // $ANTLR end "rule__Condition__Alternatives"


    // $ANTLR start "rule__Role__Alternatives_4"
    // InternalOseeDsl.g:1686:1: rule__Role__Alternatives_4 : ( ( ( rule__Role__UsersAndGroupsAssignment_4_0 ) ) | ( ( rule__Role__ReferencedContextsAssignment_4_1 ) ) );
    public final void rule__Role__Alternatives_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1690:1: ( ( ( rule__Role__UsersAndGroupsAssignment_4_0 ) ) | ( ( rule__Role__ReferencedContextsAssignment_4_1 ) ) )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==58) ) {
                alt12=1;
            }
            else if ( (LA12_0==92) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // InternalOseeDsl.g:1691:1: ( ( rule__Role__UsersAndGroupsAssignment_4_0 ) )
                    {
                    // InternalOseeDsl.g:1691:1: ( ( rule__Role__UsersAndGroupsAssignment_4_0 ) )
                    // InternalOseeDsl.g:1692:1: ( rule__Role__UsersAndGroupsAssignment_4_0 )
                    {
                     before(grammarAccess.getRoleAccess().getUsersAndGroupsAssignment_4_0()); 
                    // InternalOseeDsl.g:1693:1: ( rule__Role__UsersAndGroupsAssignment_4_0 )
                    // InternalOseeDsl.g:1693:2: rule__Role__UsersAndGroupsAssignment_4_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__Role__UsersAndGroupsAssignment_4_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getRoleAccess().getUsersAndGroupsAssignment_4_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1697:6: ( ( rule__Role__ReferencedContextsAssignment_4_1 ) )
                    {
                    // InternalOseeDsl.g:1697:6: ( ( rule__Role__ReferencedContextsAssignment_4_1 ) )
                    // InternalOseeDsl.g:1698:1: ( rule__Role__ReferencedContextsAssignment_4_1 )
                    {
                     before(grammarAccess.getRoleAccess().getReferencedContextsAssignment_4_1()); 
                    // InternalOseeDsl.g:1699:1: ( rule__Role__ReferencedContextsAssignment_4_1 )
                    // InternalOseeDsl.g:1699:2: rule__Role__ReferencedContextsAssignment_4_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__Role__ReferencedContextsAssignment_4_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getRoleAccess().getReferencedContextsAssignment_4_1()); 

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
    // $ANTLR end "rule__Role__Alternatives_4"


    // $ANTLR start "rule__AccessContext__Alternatives_7"
    // InternalOseeDsl.g:1708:1: rule__AccessContext__Alternatives_7 : ( ( ( rule__AccessContext__AccessRulesAssignment_7_0 ) ) | ( ( rule__AccessContext__HierarchyRestrictionsAssignment_7_1 ) ) );
    public final void rule__AccessContext__Alternatives_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1712:1: ( ( ( rule__AccessContext__AccessRulesAssignment_7_0 ) ) | ( ( rule__AccessContext__HierarchyRestrictionsAssignment_7_1 ) ) )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( ((LA13_0>=45 && LA13_0<=46)) ) {
                alt13=1;
            }
            else if ( (LA13_0==93) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // InternalOseeDsl.g:1713:1: ( ( rule__AccessContext__AccessRulesAssignment_7_0 ) )
                    {
                    // InternalOseeDsl.g:1713:1: ( ( rule__AccessContext__AccessRulesAssignment_7_0 ) )
                    // InternalOseeDsl.g:1714:1: ( rule__AccessContext__AccessRulesAssignment_7_0 )
                    {
                     before(grammarAccess.getAccessContextAccess().getAccessRulesAssignment_7_0()); 
                    // InternalOseeDsl.g:1715:1: ( rule__AccessContext__AccessRulesAssignment_7_0 )
                    // InternalOseeDsl.g:1715:2: rule__AccessContext__AccessRulesAssignment_7_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__AccessContext__AccessRulesAssignment_7_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getAccessContextAccess().getAccessRulesAssignment_7_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1719:6: ( ( rule__AccessContext__HierarchyRestrictionsAssignment_7_1 ) )
                    {
                    // InternalOseeDsl.g:1719:6: ( ( rule__AccessContext__HierarchyRestrictionsAssignment_7_1 ) )
                    // InternalOseeDsl.g:1720:1: ( rule__AccessContext__HierarchyRestrictionsAssignment_7_1 )
                    {
                     before(grammarAccess.getAccessContextAccess().getHierarchyRestrictionsAssignment_7_1()); 
                    // InternalOseeDsl.g:1721:1: ( rule__AccessContext__HierarchyRestrictionsAssignment_7_1 )
                    // InternalOseeDsl.g:1721:2: rule__AccessContext__HierarchyRestrictionsAssignment_7_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__AccessContext__HierarchyRestrictionsAssignment_7_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getAccessContextAccess().getHierarchyRestrictionsAssignment_7_1()); 

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
    // $ANTLR end "rule__AccessContext__Alternatives_7"


    // $ANTLR start "rule__RelationTypePredicate__Alternatives"
    // InternalOseeDsl.g:1730:1: rule__RelationTypePredicate__Alternatives : ( ( ruleRelationTypeArtifactPredicate ) | ( ruleRelationTypeArtifactTypePredicate ) );
    public final void rule__RelationTypePredicate__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1734:1: ( ( ruleRelationTypeArtifactPredicate ) | ( ruleRelationTypeArtifactTypePredicate ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==94) ) {
                alt14=1;
            }
            else if ( (LA14_0==52) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // InternalOseeDsl.g:1735:1: ( ruleRelationTypeArtifactPredicate )
                    {
                    // InternalOseeDsl.g:1735:1: ( ruleRelationTypeArtifactPredicate )
                    // InternalOseeDsl.g:1736:1: ruleRelationTypeArtifactPredicate
                    {
                     before(grammarAccess.getRelationTypePredicateAccess().getRelationTypeArtifactPredicateParserRuleCall_0()); 
                    pushFollow(FOLLOW_2);
                    ruleRelationTypeArtifactPredicate();

                    state._fsp--;

                     after(grammarAccess.getRelationTypePredicateAccess().getRelationTypeArtifactPredicateParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1741:6: ( ruleRelationTypeArtifactTypePredicate )
                    {
                    // InternalOseeDsl.g:1741:6: ( ruleRelationTypeArtifactTypePredicate )
                    // InternalOseeDsl.g:1742:1: ruleRelationTypeArtifactTypePredicate
                    {
                     before(grammarAccess.getRelationTypePredicateAccess().getRelationTypeArtifactTypePredicateParserRuleCall_1()); 
                    pushFollow(FOLLOW_2);
                    ruleRelationTypeArtifactTypePredicate();

                    state._fsp--;

                     after(grammarAccess.getRelationTypePredicateAccess().getRelationTypeArtifactTypePredicateParserRuleCall_1()); 

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
    // $ANTLR end "rule__RelationTypePredicate__Alternatives"


    // $ANTLR start "rule__ObjectRestriction__Alternatives"
    // InternalOseeDsl.g:1752:1: rule__ObjectRestriction__Alternatives : ( ( ruleArtifactMatchRestriction ) | ( ruleArtifactTypeRestriction ) | ( ruleRelationTypeRestriction ) | ( ruleAttributeTypeRestriction ) );
    public final void rule__ObjectRestriction__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1756:1: ( ( ruleArtifactMatchRestriction ) | ( ruleArtifactTypeRestriction ) | ( ruleRelationTypeRestriction ) | ( ruleAttributeTypeRestriction ) )
            int alt15=4;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==45) ) {
                int LA15_1 = input.LA(2);

                if ( (LA15_1==95) ) {
                    switch ( input.LA(3) ) {
                    case 79:
                        {
                        alt15=3;
                        }
                        break;
                    case 60:
                        {
                        alt15=4;
                        }
                        break;
                    case 52:
                        {
                        alt15=2;
                        }
                        break;
                    case 94:
                        {
                        alt15=1;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 15, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA15_0==46) ) {
                int LA15_2 = input.LA(2);

                if ( (LA15_2==95) ) {
                    switch ( input.LA(3) ) {
                    case 79:
                        {
                        alt15=3;
                        }
                        break;
                    case 60:
                        {
                        alt15=4;
                        }
                        break;
                    case 52:
                        {
                        alt15=2;
                        }
                        break;
                    case 94:
                        {
                        alt15=1;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 15, 3, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // InternalOseeDsl.g:1757:1: ( ruleArtifactMatchRestriction )
                    {
                    // InternalOseeDsl.g:1757:1: ( ruleArtifactMatchRestriction )
                    // InternalOseeDsl.g:1758:1: ruleArtifactMatchRestriction
                    {
                     before(grammarAccess.getObjectRestrictionAccess().getArtifactMatchRestrictionParserRuleCall_0()); 
                    pushFollow(FOLLOW_2);
                    ruleArtifactMatchRestriction();

                    state._fsp--;

                     after(grammarAccess.getObjectRestrictionAccess().getArtifactMatchRestrictionParserRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1763:6: ( ruleArtifactTypeRestriction )
                    {
                    // InternalOseeDsl.g:1763:6: ( ruleArtifactTypeRestriction )
                    // InternalOseeDsl.g:1764:1: ruleArtifactTypeRestriction
                    {
                     before(grammarAccess.getObjectRestrictionAccess().getArtifactTypeRestrictionParserRuleCall_1()); 
                    pushFollow(FOLLOW_2);
                    ruleArtifactTypeRestriction();

                    state._fsp--;

                     after(grammarAccess.getObjectRestrictionAccess().getArtifactTypeRestrictionParserRuleCall_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1769:6: ( ruleRelationTypeRestriction )
                    {
                    // InternalOseeDsl.g:1769:6: ( ruleRelationTypeRestriction )
                    // InternalOseeDsl.g:1770:1: ruleRelationTypeRestriction
                    {
                     before(grammarAccess.getObjectRestrictionAccess().getRelationTypeRestrictionParserRuleCall_2()); 
                    pushFollow(FOLLOW_2);
                    ruleRelationTypeRestriction();

                    state._fsp--;

                     after(grammarAccess.getObjectRestrictionAccess().getRelationTypeRestrictionParserRuleCall_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:1775:6: ( ruleAttributeTypeRestriction )
                    {
                    // InternalOseeDsl.g:1775:6: ( ruleAttributeTypeRestriction )
                    // InternalOseeDsl.g:1776:1: ruleAttributeTypeRestriction
                    {
                     before(grammarAccess.getObjectRestrictionAccess().getAttributeTypeRestrictionParserRuleCall_3()); 
                    pushFollow(FOLLOW_2);
                    ruleAttributeTypeRestriction();

                    state._fsp--;

                     after(grammarAccess.getObjectRestrictionAccess().getAttributeTypeRestrictionParserRuleCall_3()); 

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
    // $ANTLR end "rule__ObjectRestriction__Alternatives"


    // $ANTLR start "rule__RelationTypeRestriction__Alternatives_3"
    // InternalOseeDsl.g:1786:1: rule__RelationTypeRestriction__Alternatives_3 : ( ( ( rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0 ) ) | ( ( rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1 ) ) );
    public final void rule__RelationTypeRestriction__Alternatives_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1790:1: ( ( ( rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0 ) ) | ( ( rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1 ) ) )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==12) ) {
                alt16=1;
            }
            else if ( (LA16_0==RULE_STRING) ) {
                alt16=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // InternalOseeDsl.g:1791:1: ( ( rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0 ) )
                    {
                    // InternalOseeDsl.g:1791:1: ( ( rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0 ) )
                    // InternalOseeDsl.g:1792:1: ( rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0 )
                    {
                     before(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeMatchAssignment_3_0()); 
                    // InternalOseeDsl.g:1793:1: ( rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0 )
                    // InternalOseeDsl.g:1793:2: rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeMatchAssignment_3_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1797:6: ( ( rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1 ) )
                    {
                    // InternalOseeDsl.g:1797:6: ( ( rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1 ) )
                    // InternalOseeDsl.g:1798:1: ( rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1 )
                    {
                     before(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefAssignment_3_1()); 
                    // InternalOseeDsl.g:1799:1: ( rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1 )
                    // InternalOseeDsl.g:1799:2: rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefAssignment_3_1()); 

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
    // $ANTLR end "rule__RelationTypeRestriction__Alternatives_3"


    // $ANTLR start "rule__RelationMultiplicityEnum__Alternatives"
    // InternalOseeDsl.g:1808:1: rule__RelationMultiplicityEnum__Alternatives : ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) );
    public final void rule__RelationMultiplicityEnum__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1812:1: ( ( ( 'ONE_TO_ONE' ) ) | ( ( 'ONE_TO_MANY' ) ) | ( ( 'MANY_TO_ONE' ) ) | ( ( 'MANY_TO_MANY' ) ) )
            int alt17=4;
            switch ( input.LA(1) ) {
            case 33:
                {
                alt17=1;
                }
                break;
            case 34:
                {
                alt17=2;
                }
                break;
            case 35:
                {
                alt17=3;
                }
                break;
            case 36:
                {
                alt17=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // InternalOseeDsl.g:1813:1: ( ( 'ONE_TO_ONE' ) )
                    {
                    // InternalOseeDsl.g:1813:1: ( ( 'ONE_TO_ONE' ) )
                    // InternalOseeDsl.g:1814:1: ( 'ONE_TO_ONE' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 
                    // InternalOseeDsl.g:1815:1: ( 'ONE_TO_ONE' )
                    // InternalOseeDsl.g:1815:3: 'ONE_TO_ONE'
                    {
                    match(input,33,FOLLOW_2); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_ONEEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1820:6: ( ( 'ONE_TO_MANY' ) )
                    {
                    // InternalOseeDsl.g:1820:6: ( ( 'ONE_TO_MANY' ) )
                    // InternalOseeDsl.g:1821:1: ( 'ONE_TO_MANY' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 
                    // InternalOseeDsl.g:1822:1: ( 'ONE_TO_MANY' )
                    // InternalOseeDsl.g:1822:3: 'ONE_TO_MANY'
                    {
                    match(input,34,FOLLOW_2); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getONE_TO_MANYEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1827:6: ( ( 'MANY_TO_ONE' ) )
                    {
                    // InternalOseeDsl.g:1827:6: ( ( 'MANY_TO_ONE' ) )
                    // InternalOseeDsl.g:1828:1: ( 'MANY_TO_ONE' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 
                    // InternalOseeDsl.g:1829:1: ( 'MANY_TO_ONE' )
                    // InternalOseeDsl.g:1829:3: 'MANY_TO_ONE'
                    {
                    match(input,35,FOLLOW_2); 

                    }

                     after(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_ONEEnumLiteralDeclaration_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:1834:6: ( ( 'MANY_TO_MANY' ) )
                    {
                    // InternalOseeDsl.g:1834:6: ( ( 'MANY_TO_MANY' ) )
                    // InternalOseeDsl.g:1835:1: ( 'MANY_TO_MANY' )
                    {
                     before(grammarAccess.getRelationMultiplicityEnumAccess().getMANY_TO_MANYEnumLiteralDeclaration_3()); 
                    // InternalOseeDsl.g:1836:1: ( 'MANY_TO_MANY' )
                    // InternalOseeDsl.g:1836:3: 'MANY_TO_MANY'
                    {
                    match(input,36,FOLLOW_2); 

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
    // $ANTLR end "rule__RelationMultiplicityEnum__Alternatives"


    // $ANTLR start "rule__CompareOp__Alternatives"
    // InternalOseeDsl.g:1846:1: rule__CompareOp__Alternatives : ( ( ( 'EQ' ) ) | ( ( 'LIKE' ) ) );
    public final void rule__CompareOp__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1850:1: ( ( ( 'EQ' ) ) | ( ( 'LIKE' ) ) )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==37) ) {
                alt18=1;
            }
            else if ( (LA18_0==38) ) {
                alt18=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // InternalOseeDsl.g:1851:1: ( ( 'EQ' ) )
                    {
                    // InternalOseeDsl.g:1851:1: ( ( 'EQ' ) )
                    // InternalOseeDsl.g:1852:1: ( 'EQ' )
                    {
                     before(grammarAccess.getCompareOpAccess().getEQEnumLiteralDeclaration_0()); 
                    // InternalOseeDsl.g:1853:1: ( 'EQ' )
                    // InternalOseeDsl.g:1853:3: 'EQ'
                    {
                    match(input,37,FOLLOW_2); 

                    }

                     after(grammarAccess.getCompareOpAccess().getEQEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1858:6: ( ( 'LIKE' ) )
                    {
                    // InternalOseeDsl.g:1858:6: ( ( 'LIKE' ) )
                    // InternalOseeDsl.g:1859:1: ( 'LIKE' )
                    {
                     before(grammarAccess.getCompareOpAccess().getLIKEEnumLiteralDeclaration_1()); 
                    // InternalOseeDsl.g:1860:1: ( 'LIKE' )
                    // InternalOseeDsl.g:1860:3: 'LIKE'
                    {
                    match(input,38,FOLLOW_2); 

                    }

                     after(grammarAccess.getCompareOpAccess().getLIKEEnumLiteralDeclaration_1()); 

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
    // $ANTLR end "rule__CompareOp__Alternatives"


    // $ANTLR start "rule__XLogicOperator__Alternatives"
    // InternalOseeDsl.g:1870:1: rule__XLogicOperator__Alternatives : ( ( ( 'AND' ) ) | ( ( 'OR' ) ) );
    public final void rule__XLogicOperator__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1874:1: ( ( ( 'AND' ) ) | ( ( 'OR' ) ) )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==39) ) {
                alt19=1;
            }
            else if ( (LA19_0==40) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // InternalOseeDsl.g:1875:1: ( ( 'AND' ) )
                    {
                    // InternalOseeDsl.g:1875:1: ( ( 'AND' ) )
                    // InternalOseeDsl.g:1876:1: ( 'AND' )
                    {
                     before(grammarAccess.getXLogicOperatorAccess().getANDEnumLiteralDeclaration_0()); 
                    // InternalOseeDsl.g:1877:1: ( 'AND' )
                    // InternalOseeDsl.g:1877:3: 'AND'
                    {
                    match(input,39,FOLLOW_2); 

                    }

                     after(grammarAccess.getXLogicOperatorAccess().getANDEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1882:6: ( ( 'OR' ) )
                    {
                    // InternalOseeDsl.g:1882:6: ( ( 'OR' ) )
                    // InternalOseeDsl.g:1883:1: ( 'OR' )
                    {
                     before(grammarAccess.getXLogicOperatorAccess().getOREnumLiteralDeclaration_1()); 
                    // InternalOseeDsl.g:1884:1: ( 'OR' )
                    // InternalOseeDsl.g:1884:3: 'OR'
                    {
                    match(input,40,FOLLOW_2); 

                    }

                     after(grammarAccess.getXLogicOperatorAccess().getOREnumLiteralDeclaration_1()); 

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
    // $ANTLR end "rule__XLogicOperator__Alternatives"


    // $ANTLR start "rule__MatchField__Alternatives"
    // InternalOseeDsl.g:1894:1: rule__MatchField__Alternatives : ( ( ( 'artifactName' ) ) | ( ( 'artifactGuid' ) ) | ( ( 'branchName' ) ) | ( ( 'branchUuid' ) ) );
    public final void rule__MatchField__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1898:1: ( ( ( 'artifactName' ) ) | ( ( 'artifactGuid' ) ) | ( ( 'branchName' ) ) | ( ( 'branchUuid' ) ) )
            int alt20=4;
            switch ( input.LA(1) ) {
            case 41:
                {
                alt20=1;
                }
                break;
            case 42:
                {
                alt20=2;
                }
                break;
            case 43:
                {
                alt20=3;
                }
                break;
            case 44:
                {
                alt20=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // InternalOseeDsl.g:1899:1: ( ( 'artifactName' ) )
                    {
                    // InternalOseeDsl.g:1899:1: ( ( 'artifactName' ) )
                    // InternalOseeDsl.g:1900:1: ( 'artifactName' )
                    {
                     before(grammarAccess.getMatchFieldAccess().getArtifactNameEnumLiteralDeclaration_0()); 
                    // InternalOseeDsl.g:1901:1: ( 'artifactName' )
                    // InternalOseeDsl.g:1901:3: 'artifactName'
                    {
                    match(input,41,FOLLOW_2); 

                    }

                     after(grammarAccess.getMatchFieldAccess().getArtifactNameEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1906:6: ( ( 'artifactGuid' ) )
                    {
                    // InternalOseeDsl.g:1906:6: ( ( 'artifactGuid' ) )
                    // InternalOseeDsl.g:1907:1: ( 'artifactGuid' )
                    {
                     before(grammarAccess.getMatchFieldAccess().getArtifactGuidEnumLiteralDeclaration_1()); 
                    // InternalOseeDsl.g:1908:1: ( 'artifactGuid' )
                    // InternalOseeDsl.g:1908:3: 'artifactGuid'
                    {
                    match(input,42,FOLLOW_2); 

                    }

                     after(grammarAccess.getMatchFieldAccess().getArtifactGuidEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1913:6: ( ( 'branchName' ) )
                    {
                    // InternalOseeDsl.g:1913:6: ( ( 'branchName' ) )
                    // InternalOseeDsl.g:1914:1: ( 'branchName' )
                    {
                     before(grammarAccess.getMatchFieldAccess().getBranchNameEnumLiteralDeclaration_2()); 
                    // InternalOseeDsl.g:1915:1: ( 'branchName' )
                    // InternalOseeDsl.g:1915:3: 'branchName'
                    {
                    match(input,43,FOLLOW_2); 

                    }

                     after(grammarAccess.getMatchFieldAccess().getBranchNameEnumLiteralDeclaration_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:1920:6: ( ( 'branchUuid' ) )
                    {
                    // InternalOseeDsl.g:1920:6: ( ( 'branchUuid' ) )
                    // InternalOseeDsl.g:1921:1: ( 'branchUuid' )
                    {
                     before(grammarAccess.getMatchFieldAccess().getBranchUuidEnumLiteralDeclaration_3()); 
                    // InternalOseeDsl.g:1922:1: ( 'branchUuid' )
                    // InternalOseeDsl.g:1922:3: 'branchUuid'
                    {
                    match(input,44,FOLLOW_2); 

                    }

                     after(grammarAccess.getMatchFieldAccess().getBranchUuidEnumLiteralDeclaration_3()); 

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
    // $ANTLR end "rule__MatchField__Alternatives"


    // $ANTLR start "rule__AccessPermissionEnum__Alternatives"
    // InternalOseeDsl.g:1932:1: rule__AccessPermissionEnum__Alternatives : ( ( ( 'ALLOW' ) ) | ( ( 'DENY' ) ) );
    public final void rule__AccessPermissionEnum__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1936:1: ( ( ( 'ALLOW' ) ) | ( ( 'DENY' ) ) )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==45) ) {
                alt21=1;
            }
            else if ( (LA21_0==46) ) {
                alt21=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // InternalOseeDsl.g:1937:1: ( ( 'ALLOW' ) )
                    {
                    // InternalOseeDsl.g:1937:1: ( ( 'ALLOW' ) )
                    // InternalOseeDsl.g:1938:1: ( 'ALLOW' )
                    {
                     before(grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0()); 
                    // InternalOseeDsl.g:1939:1: ( 'ALLOW' )
                    // InternalOseeDsl.g:1939:3: 'ALLOW'
                    {
                    match(input,45,FOLLOW_2); 

                    }

                     after(grammarAccess.getAccessPermissionEnumAccess().getALLOWEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1944:6: ( ( 'DENY' ) )
                    {
                    // InternalOseeDsl.g:1944:6: ( ( 'DENY' ) )
                    // InternalOseeDsl.g:1945:1: ( 'DENY' )
                    {
                     before(grammarAccess.getAccessPermissionEnumAccess().getDENYEnumLiteralDeclaration_1()); 
                    // InternalOseeDsl.g:1946:1: ( 'DENY' )
                    // InternalOseeDsl.g:1946:3: 'DENY'
                    {
                    match(input,46,FOLLOW_2); 

                    }

                     after(grammarAccess.getAccessPermissionEnumAccess().getDENYEnumLiteralDeclaration_1()); 

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
    // $ANTLR end "rule__AccessPermissionEnum__Alternatives"


    // $ANTLR start "rule__XRelationSideEnum__Alternatives"
    // InternalOseeDsl.g:1956:1: rule__XRelationSideEnum__Alternatives : ( ( ( 'SIDE_A' ) ) | ( ( 'SIDE_B' ) ) | ( ( 'BOTH' ) ) );
    public final void rule__XRelationSideEnum__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1960:1: ( ( ( 'SIDE_A' ) ) | ( ( 'SIDE_B' ) ) | ( ( 'BOTH' ) ) )
            int alt22=3;
            switch ( input.LA(1) ) {
            case 47:
                {
                alt22=1;
                }
                break;
            case 48:
                {
                alt22=2;
                }
                break;
            case 49:
                {
                alt22=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // InternalOseeDsl.g:1961:1: ( ( 'SIDE_A' ) )
                    {
                    // InternalOseeDsl.g:1961:1: ( ( 'SIDE_A' ) )
                    // InternalOseeDsl.g:1962:1: ( 'SIDE_A' )
                    {
                     before(grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0()); 
                    // InternalOseeDsl.g:1963:1: ( 'SIDE_A' )
                    // InternalOseeDsl.g:1963:3: 'SIDE_A'
                    {
                    match(input,47,FOLLOW_2); 

                    }

                     after(grammarAccess.getXRelationSideEnumAccess().getSIDE_AEnumLiteralDeclaration_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:1968:6: ( ( 'SIDE_B' ) )
                    {
                    // InternalOseeDsl.g:1968:6: ( ( 'SIDE_B' ) )
                    // InternalOseeDsl.g:1969:1: ( 'SIDE_B' )
                    {
                     before(grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1()); 
                    // InternalOseeDsl.g:1970:1: ( 'SIDE_B' )
                    // InternalOseeDsl.g:1970:3: 'SIDE_B'
                    {
                    match(input,48,FOLLOW_2); 

                    }

                     after(grammarAccess.getXRelationSideEnumAccess().getSIDE_BEnumLiteralDeclaration_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:1975:6: ( ( 'BOTH' ) )
                    {
                    // InternalOseeDsl.g:1975:6: ( ( 'BOTH' ) )
                    // InternalOseeDsl.g:1976:1: ( 'BOTH' )
                    {
                     before(grammarAccess.getXRelationSideEnumAccess().getBOTHEnumLiteralDeclaration_2()); 
                    // InternalOseeDsl.g:1977:1: ( 'BOTH' )
                    // InternalOseeDsl.g:1977:3: 'BOTH'
                    {
                    match(input,49,FOLLOW_2); 

                    }

                     after(grammarAccess.getXRelationSideEnumAccess().getBOTHEnumLiteralDeclaration_2()); 

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
    // $ANTLR end "rule__XRelationSideEnum__Alternatives"


    // $ANTLR start "rule__OseeDsl__Group__0"
    // InternalOseeDsl.g:1989:1: rule__OseeDsl__Group__0 : rule__OseeDsl__Group__0__Impl rule__OseeDsl__Group__1 ;
    public final void rule__OseeDsl__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:1993:1: ( rule__OseeDsl__Group__0__Impl rule__OseeDsl__Group__1 )
            // InternalOseeDsl.g:1994:2: rule__OseeDsl__Group__0__Impl rule__OseeDsl__Group__1
            {
            pushFollow(FOLLOW_3);
            rule__OseeDsl__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__OseeDsl__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__Group__0"


    // $ANTLR start "rule__OseeDsl__Group__0__Impl"
    // InternalOseeDsl.g:2001:1: rule__OseeDsl__Group__0__Impl : ( ( rule__OseeDsl__ImportsAssignment_0 )* ) ;
    public final void rule__OseeDsl__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2005:1: ( ( ( rule__OseeDsl__ImportsAssignment_0 )* ) )
            // InternalOseeDsl.g:2006:1: ( ( rule__OseeDsl__ImportsAssignment_0 )* )
            {
            // InternalOseeDsl.g:2006:1: ( ( rule__OseeDsl__ImportsAssignment_0 )* )
            // InternalOseeDsl.g:2007:1: ( rule__OseeDsl__ImportsAssignment_0 )*
            {
             before(grammarAccess.getOseeDslAccess().getImportsAssignment_0()); 
            // InternalOseeDsl.g:2008:1: ( rule__OseeDsl__ImportsAssignment_0 )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==50) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // InternalOseeDsl.g:2008:2: rule__OseeDsl__ImportsAssignment_0
            	    {
            	    pushFollow(FOLLOW_4);
            	    rule__OseeDsl__ImportsAssignment_0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

             after(grammarAccess.getOseeDslAccess().getImportsAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__Group__0__Impl"


    // $ANTLR start "rule__OseeDsl__Group__1"
    // InternalOseeDsl.g:2018:1: rule__OseeDsl__Group__1 : rule__OseeDsl__Group__1__Impl rule__OseeDsl__Group__2 ;
    public final void rule__OseeDsl__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2022:1: ( rule__OseeDsl__Group__1__Impl rule__OseeDsl__Group__2 )
            // InternalOseeDsl.g:2023:2: rule__OseeDsl__Group__1__Impl rule__OseeDsl__Group__2
            {
            pushFollow(FOLLOW_3);
            rule__OseeDsl__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__OseeDsl__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__Group__1"


    // $ANTLR start "rule__OseeDsl__Group__1__Impl"
    // InternalOseeDsl.g:2030:1: rule__OseeDsl__Group__1__Impl : ( ( rule__OseeDsl__Alternatives_1 )* ) ;
    public final void rule__OseeDsl__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2034:1: ( ( ( rule__OseeDsl__Alternatives_1 )* ) )
            // InternalOseeDsl.g:2035:1: ( ( rule__OseeDsl__Alternatives_1 )* )
            {
            // InternalOseeDsl.g:2035:1: ( ( rule__OseeDsl__Alternatives_1 )* )
            // InternalOseeDsl.g:2036:1: ( rule__OseeDsl__Alternatives_1 )*
            {
             before(grammarAccess.getOseeDslAccess().getAlternatives_1()); 
            // InternalOseeDsl.g:2037:1: ( rule__OseeDsl__Alternatives_1 )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==52||LA24_0==60||LA24_0==71||LA24_0==74||LA24_0==77||LA24_0==79||LA24_0==97) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // InternalOseeDsl.g:2037:2: rule__OseeDsl__Alternatives_1
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__OseeDsl__Alternatives_1();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);

             after(grammarAccess.getOseeDslAccess().getAlternatives_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__Group__1__Impl"


    // $ANTLR start "rule__OseeDsl__Group__2"
    // InternalOseeDsl.g:2047:1: rule__OseeDsl__Group__2 : rule__OseeDsl__Group__2__Impl ;
    public final void rule__OseeDsl__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2051:1: ( rule__OseeDsl__Group__2__Impl )
            // InternalOseeDsl.g:2052:2: rule__OseeDsl__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__OseeDsl__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__Group__2"


    // $ANTLR start "rule__OseeDsl__Group__2__Impl"
    // InternalOseeDsl.g:2058:1: rule__OseeDsl__Group__2__Impl : ( ( rule__OseeDsl__Alternatives_2 )* ) ;
    public final void rule__OseeDsl__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2062:1: ( ( ( rule__OseeDsl__Alternatives_2 )* ) )
            // InternalOseeDsl.g:2063:1: ( ( rule__OseeDsl__Alternatives_2 )* )
            {
            // InternalOseeDsl.g:2063:1: ( ( rule__OseeDsl__Alternatives_2 )* )
            // InternalOseeDsl.g:2064:1: ( rule__OseeDsl__Alternatives_2 )*
            {
             before(grammarAccess.getOseeDslAccess().getAlternatives_2()); 
            // InternalOseeDsl.g:2065:1: ( rule__OseeDsl__Alternatives_2 )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==88||(LA25_0>=91 && LA25_0<=92)) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // InternalOseeDsl.g:2065:2: rule__OseeDsl__Alternatives_2
            	    {
            	    pushFollow(FOLLOW_6);
            	    rule__OseeDsl__Alternatives_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

             after(grammarAccess.getOseeDslAccess().getAlternatives_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__Group__2__Impl"


    // $ANTLR start "rule__Import__Group__0"
    // InternalOseeDsl.g:2081:1: rule__Import__Group__0 : rule__Import__Group__0__Impl rule__Import__Group__1 ;
    public final void rule__Import__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2085:1: ( rule__Import__Group__0__Impl rule__Import__Group__1 )
            // InternalOseeDsl.g:2086:2: rule__Import__Group__0__Impl rule__Import__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__Import__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Import__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Import__Group__0"


    // $ANTLR start "rule__Import__Group__0__Impl"
    // InternalOseeDsl.g:2093:1: rule__Import__Group__0__Impl : ( 'import' ) ;
    public final void rule__Import__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2097:1: ( ( 'import' ) )
            // InternalOseeDsl.g:2098:1: ( 'import' )
            {
            // InternalOseeDsl.g:2098:1: ( 'import' )
            // InternalOseeDsl.g:2099:1: 'import'
            {
             before(grammarAccess.getImportAccess().getImportKeyword_0()); 
            match(input,50,FOLLOW_2); 
             after(grammarAccess.getImportAccess().getImportKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Import__Group__0__Impl"


    // $ANTLR start "rule__Import__Group__1"
    // InternalOseeDsl.g:2112:1: rule__Import__Group__1 : rule__Import__Group__1__Impl ;
    public final void rule__Import__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2116:1: ( rule__Import__Group__1__Impl )
            // InternalOseeDsl.g:2117:2: rule__Import__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Import__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Import__Group__1"


    // $ANTLR start "rule__Import__Group__1__Impl"
    // InternalOseeDsl.g:2123:1: rule__Import__Group__1__Impl : ( ( rule__Import__ImportURIAssignment_1 ) ) ;
    public final void rule__Import__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2127:1: ( ( ( rule__Import__ImportURIAssignment_1 ) ) )
            // InternalOseeDsl.g:2128:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            {
            // InternalOseeDsl.g:2128:1: ( ( rule__Import__ImportURIAssignment_1 ) )
            // InternalOseeDsl.g:2129:1: ( rule__Import__ImportURIAssignment_1 )
            {
             before(grammarAccess.getImportAccess().getImportURIAssignment_1()); 
            // InternalOseeDsl.g:2130:1: ( rule__Import__ImportURIAssignment_1 )
            // InternalOseeDsl.g:2130:2: rule__Import__ImportURIAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__Import__ImportURIAssignment_1();

            state._fsp--;


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
    // $ANTLR end "rule__Import__Group__1__Impl"


    // $ANTLR start "rule__QUALIFIED_NAME__Group__0"
    // InternalOseeDsl.g:2144:1: rule__QUALIFIED_NAME__Group__0 : rule__QUALIFIED_NAME__Group__0__Impl rule__QUALIFIED_NAME__Group__1 ;
    public final void rule__QUALIFIED_NAME__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2148:1: ( rule__QUALIFIED_NAME__Group__0__Impl rule__QUALIFIED_NAME__Group__1 )
            // InternalOseeDsl.g:2149:2: rule__QUALIFIED_NAME__Group__0__Impl rule__QUALIFIED_NAME__Group__1
            {
            pushFollow(FOLLOW_8);
            rule__QUALIFIED_NAME__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__QUALIFIED_NAME__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QUALIFIED_NAME__Group__0"


    // $ANTLR start "rule__QUALIFIED_NAME__Group__0__Impl"
    // InternalOseeDsl.g:2156:1: rule__QUALIFIED_NAME__Group__0__Impl : ( RULE_ID ) ;
    public final void rule__QUALIFIED_NAME__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2160:1: ( ( RULE_ID ) )
            // InternalOseeDsl.g:2161:1: ( RULE_ID )
            {
            // InternalOseeDsl.g:2161:1: ( RULE_ID )
            // InternalOseeDsl.g:2162:1: RULE_ID
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QUALIFIED_NAME__Group__0__Impl"


    // $ANTLR start "rule__QUALIFIED_NAME__Group__1"
    // InternalOseeDsl.g:2173:1: rule__QUALIFIED_NAME__Group__1 : rule__QUALIFIED_NAME__Group__1__Impl ;
    public final void rule__QUALIFIED_NAME__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2177:1: ( rule__QUALIFIED_NAME__Group__1__Impl )
            // InternalOseeDsl.g:2178:2: rule__QUALIFIED_NAME__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__QUALIFIED_NAME__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QUALIFIED_NAME__Group__1"


    // $ANTLR start "rule__QUALIFIED_NAME__Group__1__Impl"
    // InternalOseeDsl.g:2184:1: rule__QUALIFIED_NAME__Group__1__Impl : ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) ;
    public final void rule__QUALIFIED_NAME__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2188:1: ( ( ( rule__QUALIFIED_NAME__Group_1__0 )* ) )
            // InternalOseeDsl.g:2189:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            {
            // InternalOseeDsl.g:2189:1: ( ( rule__QUALIFIED_NAME__Group_1__0 )* )
            // InternalOseeDsl.g:2190:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getGroup_1()); 
            // InternalOseeDsl.g:2191:1: ( rule__QUALIFIED_NAME__Group_1__0 )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==51) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // InternalOseeDsl.g:2191:2: rule__QUALIFIED_NAME__Group_1__0
            	    {
            	    pushFollow(FOLLOW_9);
            	    rule__QUALIFIED_NAME__Group_1__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop26;
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
    // $ANTLR end "rule__QUALIFIED_NAME__Group__1__Impl"


    // $ANTLR start "rule__QUALIFIED_NAME__Group_1__0"
    // InternalOseeDsl.g:2205:1: rule__QUALIFIED_NAME__Group_1__0 : rule__QUALIFIED_NAME__Group_1__0__Impl rule__QUALIFIED_NAME__Group_1__1 ;
    public final void rule__QUALIFIED_NAME__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2209:1: ( rule__QUALIFIED_NAME__Group_1__0__Impl rule__QUALIFIED_NAME__Group_1__1 )
            // InternalOseeDsl.g:2210:2: rule__QUALIFIED_NAME__Group_1__0__Impl rule__QUALIFIED_NAME__Group_1__1
            {
            pushFollow(FOLLOW_10);
            rule__QUALIFIED_NAME__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__QUALIFIED_NAME__Group_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QUALIFIED_NAME__Group_1__0"


    // $ANTLR start "rule__QUALIFIED_NAME__Group_1__0__Impl"
    // InternalOseeDsl.g:2217:1: rule__QUALIFIED_NAME__Group_1__0__Impl : ( '.' ) ;
    public final void rule__QUALIFIED_NAME__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2221:1: ( ( '.' ) )
            // InternalOseeDsl.g:2222:1: ( '.' )
            {
            // InternalOseeDsl.g:2222:1: ( '.' )
            // InternalOseeDsl.g:2223:1: '.'
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 
            match(input,51,FOLLOW_2); 
             after(grammarAccess.getQUALIFIED_NAMEAccess().getFullStopKeyword_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QUALIFIED_NAME__Group_1__0__Impl"


    // $ANTLR start "rule__QUALIFIED_NAME__Group_1__1"
    // InternalOseeDsl.g:2236:1: rule__QUALIFIED_NAME__Group_1__1 : rule__QUALIFIED_NAME__Group_1__1__Impl ;
    public final void rule__QUALIFIED_NAME__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2240:1: ( rule__QUALIFIED_NAME__Group_1__1__Impl )
            // InternalOseeDsl.g:2241:2: rule__QUALIFIED_NAME__Group_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__QUALIFIED_NAME__Group_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QUALIFIED_NAME__Group_1__1"


    // $ANTLR start "rule__QUALIFIED_NAME__Group_1__1__Impl"
    // InternalOseeDsl.g:2247:1: rule__QUALIFIED_NAME__Group_1__1__Impl : ( RULE_ID ) ;
    public final void rule__QUALIFIED_NAME__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2251:1: ( ( RULE_ID ) )
            // InternalOseeDsl.g:2252:1: ( RULE_ID )
            {
            // InternalOseeDsl.g:2252:1: ( RULE_ID )
            // InternalOseeDsl.g:2253:1: RULE_ID
            {
             before(grammarAccess.getQUALIFIED_NAMEAccess().getIDTerminalRuleCall_1_1()); 
            match(input,RULE_ID,FOLLOW_2); 
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
    // $ANTLR end "rule__QUALIFIED_NAME__Group_1__1__Impl"


    // $ANTLR start "rule__XArtifactType__Group__0"
    // InternalOseeDsl.g:2268:1: rule__XArtifactType__Group__0 : rule__XArtifactType__Group__0__Impl rule__XArtifactType__Group__1 ;
    public final void rule__XArtifactType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2272:1: ( rule__XArtifactType__Group__0__Impl rule__XArtifactType__Group__1 )
            // InternalOseeDsl.g:2273:2: rule__XArtifactType__Group__0__Impl rule__XArtifactType__Group__1
            {
            pushFollow(FOLLOW_11);
            rule__XArtifactType__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__0"


    // $ANTLR start "rule__XArtifactType__Group__0__Impl"
    // InternalOseeDsl.g:2280:1: rule__XArtifactType__Group__0__Impl : ( ( rule__XArtifactType__AbstractAssignment_0 )? ) ;
    public final void rule__XArtifactType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2284:1: ( ( ( rule__XArtifactType__AbstractAssignment_0 )? ) )
            // InternalOseeDsl.g:2285:1: ( ( rule__XArtifactType__AbstractAssignment_0 )? )
            {
            // InternalOseeDsl.g:2285:1: ( ( rule__XArtifactType__AbstractAssignment_0 )? )
            // InternalOseeDsl.g:2286:1: ( rule__XArtifactType__AbstractAssignment_0 )?
            {
             before(grammarAccess.getXArtifactTypeAccess().getAbstractAssignment_0()); 
            // InternalOseeDsl.g:2287:1: ( rule__XArtifactType__AbstractAssignment_0 )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==97) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // InternalOseeDsl.g:2287:2: rule__XArtifactType__AbstractAssignment_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XArtifactType__AbstractAssignment_0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXArtifactTypeAccess().getAbstractAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__0__Impl"


    // $ANTLR start "rule__XArtifactType__Group__1"
    // InternalOseeDsl.g:2297:1: rule__XArtifactType__Group__1 : rule__XArtifactType__Group__1__Impl rule__XArtifactType__Group__2 ;
    public final void rule__XArtifactType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2301:1: ( rule__XArtifactType__Group__1__Impl rule__XArtifactType__Group__2 )
            // InternalOseeDsl.g:2302:2: rule__XArtifactType__Group__1__Impl rule__XArtifactType__Group__2
            {
            pushFollow(FOLLOW_7);
            rule__XArtifactType__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__1"


    // $ANTLR start "rule__XArtifactType__Group__1__Impl"
    // InternalOseeDsl.g:2309:1: rule__XArtifactType__Group__1__Impl : ( 'artifactType' ) ;
    public final void rule__XArtifactType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2313:1: ( ( 'artifactType' ) )
            // InternalOseeDsl.g:2314:1: ( 'artifactType' )
            {
            // InternalOseeDsl.g:2314:1: ( 'artifactType' )
            // InternalOseeDsl.g:2315:1: 'artifactType'
            {
             before(grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1()); 
            match(input,52,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getArtifactTypeKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__1__Impl"


    // $ANTLR start "rule__XArtifactType__Group__2"
    // InternalOseeDsl.g:2328:1: rule__XArtifactType__Group__2 : rule__XArtifactType__Group__2__Impl rule__XArtifactType__Group__3 ;
    public final void rule__XArtifactType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2332:1: ( rule__XArtifactType__Group__2__Impl rule__XArtifactType__Group__3 )
            // InternalOseeDsl.g:2333:2: rule__XArtifactType__Group__2__Impl rule__XArtifactType__Group__3
            {
            pushFollow(FOLLOW_12);
            rule__XArtifactType__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__2"


    // $ANTLR start "rule__XArtifactType__Group__2__Impl"
    // InternalOseeDsl.g:2340:1: rule__XArtifactType__Group__2__Impl : ( ( rule__XArtifactType__NameAssignment_2 ) ) ;
    public final void rule__XArtifactType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2344:1: ( ( ( rule__XArtifactType__NameAssignment_2 ) ) )
            // InternalOseeDsl.g:2345:1: ( ( rule__XArtifactType__NameAssignment_2 ) )
            {
            // InternalOseeDsl.g:2345:1: ( ( rule__XArtifactType__NameAssignment_2 ) )
            // InternalOseeDsl.g:2346:1: ( rule__XArtifactType__NameAssignment_2 )
            {
             before(grammarAccess.getXArtifactTypeAccess().getNameAssignment_2()); 
            // InternalOseeDsl.g:2347:1: ( rule__XArtifactType__NameAssignment_2 )
            // InternalOseeDsl.g:2347:2: rule__XArtifactType__NameAssignment_2
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactType__NameAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactTypeAccess().getNameAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__2__Impl"


    // $ANTLR start "rule__XArtifactType__Group__3"
    // InternalOseeDsl.g:2357:1: rule__XArtifactType__Group__3 : rule__XArtifactType__Group__3__Impl rule__XArtifactType__Group__4 ;
    public final void rule__XArtifactType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2361:1: ( rule__XArtifactType__Group__3__Impl rule__XArtifactType__Group__4 )
            // InternalOseeDsl.g:2362:2: rule__XArtifactType__Group__3__Impl rule__XArtifactType__Group__4
            {
            pushFollow(FOLLOW_12);
            rule__XArtifactType__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__3"


    // $ANTLR start "rule__XArtifactType__Group__3__Impl"
    // InternalOseeDsl.g:2369:1: rule__XArtifactType__Group__3__Impl : ( ( rule__XArtifactType__Group_3__0 )? ) ;
    public final void rule__XArtifactType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2373:1: ( ( ( rule__XArtifactType__Group_3__0 )? ) )
            // InternalOseeDsl.g:2374:1: ( ( rule__XArtifactType__Group_3__0 )? )
            {
            // InternalOseeDsl.g:2374:1: ( ( rule__XArtifactType__Group_3__0 )? )
            // InternalOseeDsl.g:2375:1: ( rule__XArtifactType__Group_3__0 )?
            {
             before(grammarAccess.getXArtifactTypeAccess().getGroup_3()); 
            // InternalOseeDsl.g:2376:1: ( rule__XArtifactType__Group_3__0 )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==56) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // InternalOseeDsl.g:2376:2: rule__XArtifactType__Group_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XArtifactType__Group_3__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXArtifactTypeAccess().getGroup_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__3__Impl"


    // $ANTLR start "rule__XArtifactType__Group__4"
    // InternalOseeDsl.g:2386:1: rule__XArtifactType__Group__4 : rule__XArtifactType__Group__4__Impl rule__XArtifactType__Group__5 ;
    public final void rule__XArtifactType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2390:1: ( rule__XArtifactType__Group__4__Impl rule__XArtifactType__Group__5 )
            // InternalOseeDsl.g:2391:2: rule__XArtifactType__Group__4__Impl rule__XArtifactType__Group__5
            {
            pushFollow(FOLLOW_13);
            rule__XArtifactType__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__4"


    // $ANTLR start "rule__XArtifactType__Group__4__Impl"
    // InternalOseeDsl.g:2398:1: rule__XArtifactType__Group__4__Impl : ( '{' ) ;
    public final void rule__XArtifactType__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2402:1: ( ( '{' ) )
            // InternalOseeDsl.g:2403:1: ( '{' )
            {
            // InternalOseeDsl.g:2403:1: ( '{' )
            // InternalOseeDsl.g:2404:1: '{'
            {
             before(grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4()); 
            match(input,53,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getLeftCurlyBracketKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__4__Impl"


    // $ANTLR start "rule__XArtifactType__Group__5"
    // InternalOseeDsl.g:2417:1: rule__XArtifactType__Group__5 : rule__XArtifactType__Group__5__Impl rule__XArtifactType__Group__6 ;
    public final void rule__XArtifactType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2421:1: ( rule__XArtifactType__Group__5__Impl rule__XArtifactType__Group__6 )
            // InternalOseeDsl.g:2422:2: rule__XArtifactType__Group__5__Impl rule__XArtifactType__Group__6
            {
            pushFollow(FOLLOW_13);
            rule__XArtifactType__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__5"


    // $ANTLR start "rule__XArtifactType__Group__5__Impl"
    // InternalOseeDsl.g:2429:1: rule__XArtifactType__Group__5__Impl : ( ( rule__XArtifactType__Group_5__0 )? ) ;
    public final void rule__XArtifactType__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2433:1: ( ( ( rule__XArtifactType__Group_5__0 )? ) )
            // InternalOseeDsl.g:2434:1: ( ( rule__XArtifactType__Group_5__0 )? )
            {
            // InternalOseeDsl.g:2434:1: ( ( rule__XArtifactType__Group_5__0 )? )
            // InternalOseeDsl.g:2435:1: ( rule__XArtifactType__Group_5__0 )?
            {
             before(grammarAccess.getXArtifactTypeAccess().getGroup_5()); 
            // InternalOseeDsl.g:2436:1: ( rule__XArtifactType__Group_5__0 )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==58) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // InternalOseeDsl.g:2436:2: rule__XArtifactType__Group_5__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XArtifactType__Group_5__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXArtifactTypeAccess().getGroup_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__5__Impl"


    // $ANTLR start "rule__XArtifactType__Group__6"
    // InternalOseeDsl.g:2446:1: rule__XArtifactType__Group__6 : rule__XArtifactType__Group__6__Impl rule__XArtifactType__Group__7 ;
    public final void rule__XArtifactType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2450:1: ( rule__XArtifactType__Group__6__Impl rule__XArtifactType__Group__7 )
            // InternalOseeDsl.g:2451:2: rule__XArtifactType__Group__6__Impl rule__XArtifactType__Group__7
            {
            pushFollow(FOLLOW_14);
            rule__XArtifactType__Group__6__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__7();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__6"


    // $ANTLR start "rule__XArtifactType__Group__6__Impl"
    // InternalOseeDsl.g:2458:1: rule__XArtifactType__Group__6__Impl : ( 'id' ) ;
    public final void rule__XArtifactType__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2462:1: ( ( 'id' ) )
            // InternalOseeDsl.g:2463:1: ( 'id' )
            {
            // InternalOseeDsl.g:2463:1: ( 'id' )
            // InternalOseeDsl.g:2464:1: 'id'
            {
             before(grammarAccess.getXArtifactTypeAccess().getIdKeyword_6()); 
            match(input,54,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getIdKeyword_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__6__Impl"


    // $ANTLR start "rule__XArtifactType__Group__7"
    // InternalOseeDsl.g:2477:1: rule__XArtifactType__Group__7 : rule__XArtifactType__Group__7__Impl rule__XArtifactType__Group__8 ;
    public final void rule__XArtifactType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2481:1: ( rule__XArtifactType__Group__7__Impl rule__XArtifactType__Group__8 )
            // InternalOseeDsl.g:2482:2: rule__XArtifactType__Group__7__Impl rule__XArtifactType__Group__8
            {
            pushFollow(FOLLOW_15);
            rule__XArtifactType__Group__7__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__8();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__7"


    // $ANTLR start "rule__XArtifactType__Group__7__Impl"
    // InternalOseeDsl.g:2489:1: rule__XArtifactType__Group__7__Impl : ( ( rule__XArtifactType__IdAssignment_7 ) ) ;
    public final void rule__XArtifactType__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2493:1: ( ( ( rule__XArtifactType__IdAssignment_7 ) ) )
            // InternalOseeDsl.g:2494:1: ( ( rule__XArtifactType__IdAssignment_7 ) )
            {
            // InternalOseeDsl.g:2494:1: ( ( rule__XArtifactType__IdAssignment_7 ) )
            // InternalOseeDsl.g:2495:1: ( rule__XArtifactType__IdAssignment_7 )
            {
             before(grammarAccess.getXArtifactTypeAccess().getIdAssignment_7()); 
            // InternalOseeDsl.g:2496:1: ( rule__XArtifactType__IdAssignment_7 )
            // InternalOseeDsl.g:2496:2: rule__XArtifactType__IdAssignment_7
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactType__IdAssignment_7();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactTypeAccess().getIdAssignment_7()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__7__Impl"


    // $ANTLR start "rule__XArtifactType__Group__8"
    // InternalOseeDsl.g:2506:1: rule__XArtifactType__Group__8 : rule__XArtifactType__Group__8__Impl rule__XArtifactType__Group__9 ;
    public final void rule__XArtifactType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2510:1: ( rule__XArtifactType__Group__8__Impl rule__XArtifactType__Group__9 )
            // InternalOseeDsl.g:2511:2: rule__XArtifactType__Group__8__Impl rule__XArtifactType__Group__9
            {
            pushFollow(FOLLOW_15);
            rule__XArtifactType__Group__8__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__9();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__8"


    // $ANTLR start "rule__XArtifactType__Group__8__Impl"
    // InternalOseeDsl.g:2518:1: rule__XArtifactType__Group__8__Impl : ( ( rule__XArtifactType__ValidAttributeTypesAssignment_8 )* ) ;
    public final void rule__XArtifactType__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2522:1: ( ( ( rule__XArtifactType__ValidAttributeTypesAssignment_8 )* ) )
            // InternalOseeDsl.g:2523:1: ( ( rule__XArtifactType__ValidAttributeTypesAssignment_8 )* )
            {
            // InternalOseeDsl.g:2523:1: ( ( rule__XArtifactType__ValidAttributeTypesAssignment_8 )* )
            // InternalOseeDsl.g:2524:1: ( rule__XArtifactType__ValidAttributeTypesAssignment_8 )*
            {
             before(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesAssignment_8()); 
            // InternalOseeDsl.g:2525:1: ( rule__XArtifactType__ValidAttributeTypesAssignment_8 )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==59) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // InternalOseeDsl.g:2525:2: rule__XArtifactType__ValidAttributeTypesAssignment_8
            	    {
            	    pushFollow(FOLLOW_16);
            	    rule__XArtifactType__ValidAttributeTypesAssignment_8();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

             after(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesAssignment_8()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__8__Impl"


    // $ANTLR start "rule__XArtifactType__Group__9"
    // InternalOseeDsl.g:2535:1: rule__XArtifactType__Group__9 : rule__XArtifactType__Group__9__Impl ;
    public final void rule__XArtifactType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2539:1: ( rule__XArtifactType__Group__9__Impl )
            // InternalOseeDsl.g:2540:2: rule__XArtifactType__Group__9__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group__9__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__9"


    // $ANTLR start "rule__XArtifactType__Group__9__Impl"
    // InternalOseeDsl.g:2546:1: rule__XArtifactType__Group__9__Impl : ( '}' ) ;
    public final void rule__XArtifactType__Group__9__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2550:1: ( ( '}' ) )
            // InternalOseeDsl.g:2551:1: ( '}' )
            {
            // InternalOseeDsl.g:2551:1: ( '}' )
            // InternalOseeDsl.g:2552:1: '}'
            {
             before(grammarAccess.getXArtifactTypeAccess().getRightCurlyBracketKeyword_9()); 
            match(input,55,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getRightCurlyBracketKeyword_9()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group__9__Impl"


    // $ANTLR start "rule__XArtifactType__Group_3__0"
    // InternalOseeDsl.g:2585:1: rule__XArtifactType__Group_3__0 : rule__XArtifactType__Group_3__0__Impl rule__XArtifactType__Group_3__1 ;
    public final void rule__XArtifactType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2589:1: ( rule__XArtifactType__Group_3__0__Impl rule__XArtifactType__Group_3__1 )
            // InternalOseeDsl.g:2590:2: rule__XArtifactType__Group_3__0__Impl rule__XArtifactType__Group_3__1
            {
            pushFollow(FOLLOW_7);
            rule__XArtifactType__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_3__0"


    // $ANTLR start "rule__XArtifactType__Group_3__0__Impl"
    // InternalOseeDsl.g:2597:1: rule__XArtifactType__Group_3__0__Impl : ( 'extends' ) ;
    public final void rule__XArtifactType__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2601:1: ( ( 'extends' ) )
            // InternalOseeDsl.g:2602:1: ( 'extends' )
            {
            // InternalOseeDsl.g:2602:1: ( 'extends' )
            // InternalOseeDsl.g:2603:1: 'extends'
            {
             before(grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0()); 
            match(input,56,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getExtendsKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_3__0__Impl"


    // $ANTLR start "rule__XArtifactType__Group_3__1"
    // InternalOseeDsl.g:2616:1: rule__XArtifactType__Group_3__1 : rule__XArtifactType__Group_3__1__Impl rule__XArtifactType__Group_3__2 ;
    public final void rule__XArtifactType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2620:1: ( rule__XArtifactType__Group_3__1__Impl rule__XArtifactType__Group_3__2 )
            // InternalOseeDsl.g:2621:2: rule__XArtifactType__Group_3__1__Impl rule__XArtifactType__Group_3__2
            {
            pushFollow(FOLLOW_17);
            rule__XArtifactType__Group_3__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group_3__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_3__1"


    // $ANTLR start "rule__XArtifactType__Group_3__1__Impl"
    // InternalOseeDsl.g:2628:1: rule__XArtifactType__Group_3__1__Impl : ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) ) ;
    public final void rule__XArtifactType__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2632:1: ( ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) ) )
            // InternalOseeDsl.g:2633:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) )
            {
            // InternalOseeDsl.g:2633:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 ) )
            // InternalOseeDsl.g:2634:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 )
            {
             before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 
            // InternalOseeDsl.g:2635:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_1 )
            // InternalOseeDsl.g:2635:2: rule__XArtifactType__SuperArtifactTypesAssignment_3_1
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactType__SuperArtifactTypesAssignment_3_1();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_3__1__Impl"


    // $ANTLR start "rule__XArtifactType__Group_3__2"
    // InternalOseeDsl.g:2645:1: rule__XArtifactType__Group_3__2 : rule__XArtifactType__Group_3__2__Impl ;
    public final void rule__XArtifactType__Group_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2649:1: ( rule__XArtifactType__Group_3__2__Impl )
            // InternalOseeDsl.g:2650:2: rule__XArtifactType__Group_3__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group_3__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_3__2"


    // $ANTLR start "rule__XArtifactType__Group_3__2__Impl"
    // InternalOseeDsl.g:2656:1: rule__XArtifactType__Group_3__2__Impl : ( ( rule__XArtifactType__Group_3_2__0 )* ) ;
    public final void rule__XArtifactType__Group_3__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2660:1: ( ( ( rule__XArtifactType__Group_3_2__0 )* ) )
            // InternalOseeDsl.g:2661:1: ( ( rule__XArtifactType__Group_3_2__0 )* )
            {
            // InternalOseeDsl.g:2661:1: ( ( rule__XArtifactType__Group_3_2__0 )* )
            // InternalOseeDsl.g:2662:1: ( rule__XArtifactType__Group_3_2__0 )*
            {
             before(grammarAccess.getXArtifactTypeAccess().getGroup_3_2()); 
            // InternalOseeDsl.g:2663:1: ( rule__XArtifactType__Group_3_2__0 )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0==57) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // InternalOseeDsl.g:2663:2: rule__XArtifactType__Group_3_2__0
            	    {
            	    pushFollow(FOLLOW_18);
            	    rule__XArtifactType__Group_3_2__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);

             after(grammarAccess.getXArtifactTypeAccess().getGroup_3_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_3__2__Impl"


    // $ANTLR start "rule__XArtifactType__Group_3_2__0"
    // InternalOseeDsl.g:2679:1: rule__XArtifactType__Group_3_2__0 : rule__XArtifactType__Group_3_2__0__Impl rule__XArtifactType__Group_3_2__1 ;
    public final void rule__XArtifactType__Group_3_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2683:1: ( rule__XArtifactType__Group_3_2__0__Impl rule__XArtifactType__Group_3_2__1 )
            // InternalOseeDsl.g:2684:2: rule__XArtifactType__Group_3_2__0__Impl rule__XArtifactType__Group_3_2__1
            {
            pushFollow(FOLLOW_7);
            rule__XArtifactType__Group_3_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group_3_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_3_2__0"


    // $ANTLR start "rule__XArtifactType__Group_3_2__0__Impl"
    // InternalOseeDsl.g:2691:1: rule__XArtifactType__Group_3_2__0__Impl : ( ',' ) ;
    public final void rule__XArtifactType__Group_3_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2695:1: ( ( ',' ) )
            // InternalOseeDsl.g:2696:1: ( ',' )
            {
            // InternalOseeDsl.g:2696:1: ( ',' )
            // InternalOseeDsl.g:2697:1: ','
            {
             before(grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0()); 
            match(input,57,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getCommaKeyword_3_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_3_2__0__Impl"


    // $ANTLR start "rule__XArtifactType__Group_3_2__1"
    // InternalOseeDsl.g:2710:1: rule__XArtifactType__Group_3_2__1 : rule__XArtifactType__Group_3_2__1__Impl ;
    public final void rule__XArtifactType__Group_3_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2714:1: ( rule__XArtifactType__Group_3_2__1__Impl )
            // InternalOseeDsl.g:2715:2: rule__XArtifactType__Group_3_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group_3_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_3_2__1"


    // $ANTLR start "rule__XArtifactType__Group_3_2__1__Impl"
    // InternalOseeDsl.g:2721:1: rule__XArtifactType__Group_3_2__1__Impl : ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) ;
    public final void rule__XArtifactType__Group_3_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2725:1: ( ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) ) )
            // InternalOseeDsl.g:2726:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            {
            // InternalOseeDsl.g:2726:1: ( ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 ) )
            // InternalOseeDsl.g:2727:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            {
             before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_2_1()); 
            // InternalOseeDsl.g:2728:1: ( rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 )
            // InternalOseeDsl.g:2728:2: rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_3_2__1__Impl"


    // $ANTLR start "rule__XArtifactType__Group_5__0"
    // InternalOseeDsl.g:2742:1: rule__XArtifactType__Group_5__0 : rule__XArtifactType__Group_5__0__Impl rule__XArtifactType__Group_5__1 ;
    public final void rule__XArtifactType__Group_5__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2746:1: ( rule__XArtifactType__Group_5__0__Impl rule__XArtifactType__Group_5__1 )
            // InternalOseeDsl.g:2747:2: rule__XArtifactType__Group_5__0__Impl rule__XArtifactType__Group_5__1
            {
            pushFollow(FOLLOW_7);
            rule__XArtifactType__Group_5__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group_5__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_5__0"


    // $ANTLR start "rule__XArtifactType__Group_5__0__Impl"
    // InternalOseeDsl.g:2754:1: rule__XArtifactType__Group_5__0__Impl : ( 'guid' ) ;
    public final void rule__XArtifactType__Group_5__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2758:1: ( ( 'guid' ) )
            // InternalOseeDsl.g:2759:1: ( 'guid' )
            {
            // InternalOseeDsl.g:2759:1: ( 'guid' )
            // InternalOseeDsl.g:2760:1: 'guid'
            {
             before(grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5_0()); 
            match(input,58,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getGuidKeyword_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_5__0__Impl"


    // $ANTLR start "rule__XArtifactType__Group_5__1"
    // InternalOseeDsl.g:2773:1: rule__XArtifactType__Group_5__1 : rule__XArtifactType__Group_5__1__Impl ;
    public final void rule__XArtifactType__Group_5__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2777:1: ( rule__XArtifactType__Group_5__1__Impl )
            // InternalOseeDsl.g:2778:2: rule__XArtifactType__Group_5__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactType__Group_5__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_5__1"


    // $ANTLR start "rule__XArtifactType__Group_5__1__Impl"
    // InternalOseeDsl.g:2784:1: rule__XArtifactType__Group_5__1__Impl : ( ( rule__XArtifactType__TypeGuidAssignment_5_1 ) ) ;
    public final void rule__XArtifactType__Group_5__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2788:1: ( ( ( rule__XArtifactType__TypeGuidAssignment_5_1 ) ) )
            // InternalOseeDsl.g:2789:1: ( ( rule__XArtifactType__TypeGuidAssignment_5_1 ) )
            {
            // InternalOseeDsl.g:2789:1: ( ( rule__XArtifactType__TypeGuidAssignment_5_1 ) )
            // InternalOseeDsl.g:2790:1: ( rule__XArtifactType__TypeGuidAssignment_5_1 )
            {
             before(grammarAccess.getXArtifactTypeAccess().getTypeGuidAssignment_5_1()); 
            // InternalOseeDsl.g:2791:1: ( rule__XArtifactType__TypeGuidAssignment_5_1 )
            // InternalOseeDsl.g:2791:2: rule__XArtifactType__TypeGuidAssignment_5_1
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactType__TypeGuidAssignment_5_1();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactTypeAccess().getTypeGuidAssignment_5_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__Group_5__1__Impl"


    // $ANTLR start "rule__XAttributeTypeRef__Group__0"
    // InternalOseeDsl.g:2805:1: rule__XAttributeTypeRef__Group__0 : rule__XAttributeTypeRef__Group__0__Impl rule__XAttributeTypeRef__Group__1 ;
    public final void rule__XAttributeTypeRef__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2809:1: ( rule__XAttributeTypeRef__Group__0__Impl rule__XAttributeTypeRef__Group__1 )
            // InternalOseeDsl.g:2810:2: rule__XAttributeTypeRef__Group__0__Impl rule__XAttributeTypeRef__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__XAttributeTypeRef__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeTypeRef__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__Group__0"


    // $ANTLR start "rule__XAttributeTypeRef__Group__0__Impl"
    // InternalOseeDsl.g:2817:1: rule__XAttributeTypeRef__Group__0__Impl : ( 'attribute' ) ;
    public final void rule__XAttributeTypeRef__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2821:1: ( ( 'attribute' ) )
            // InternalOseeDsl.g:2822:1: ( 'attribute' )
            {
            // InternalOseeDsl.g:2822:1: ( 'attribute' )
            // InternalOseeDsl.g:2823:1: 'attribute'
            {
             before(grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0()); 
            match(input,59,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeRefAccess().getAttributeKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__Group__0__Impl"


    // $ANTLR start "rule__XAttributeTypeRef__Group__1"
    // InternalOseeDsl.g:2836:1: rule__XAttributeTypeRef__Group__1 : rule__XAttributeTypeRef__Group__1__Impl rule__XAttributeTypeRef__Group__2 ;
    public final void rule__XAttributeTypeRef__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2840:1: ( rule__XAttributeTypeRef__Group__1__Impl rule__XAttributeTypeRef__Group__2 )
            // InternalOseeDsl.g:2841:2: rule__XAttributeTypeRef__Group__1__Impl rule__XAttributeTypeRef__Group__2
            {
            pushFollow(FOLLOW_19);
            rule__XAttributeTypeRef__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeTypeRef__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__Group__1"


    // $ANTLR start "rule__XAttributeTypeRef__Group__1__Impl"
    // InternalOseeDsl.g:2848:1: rule__XAttributeTypeRef__Group__1__Impl : ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) ;
    public final void rule__XAttributeTypeRef__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2852:1: ( ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) ) )
            // InternalOseeDsl.g:2853:1: ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) )
            {
            // InternalOseeDsl.g:2853:1: ( ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 ) )
            // InternalOseeDsl.g:2854:1: ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 )
            {
             before(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 
            // InternalOseeDsl.g:2855:1: ( rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 )
            // InternalOseeDsl.g:2855:2: rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__Group__1__Impl"


    // $ANTLR start "rule__XAttributeTypeRef__Group__2"
    // InternalOseeDsl.g:2865:1: rule__XAttributeTypeRef__Group__2 : rule__XAttributeTypeRef__Group__2__Impl ;
    public final void rule__XAttributeTypeRef__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2869:1: ( rule__XAttributeTypeRef__Group__2__Impl )
            // InternalOseeDsl.g:2870:2: rule__XAttributeTypeRef__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeTypeRef__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__Group__2"


    // $ANTLR start "rule__XAttributeTypeRef__Group__2__Impl"
    // InternalOseeDsl.g:2876:1: rule__XAttributeTypeRef__Group__2__Impl : ( ( rule__XAttributeTypeRef__Group_2__0 )? ) ;
    public final void rule__XAttributeTypeRef__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2880:1: ( ( ( rule__XAttributeTypeRef__Group_2__0 )? ) )
            // InternalOseeDsl.g:2881:1: ( ( rule__XAttributeTypeRef__Group_2__0 )? )
            {
            // InternalOseeDsl.g:2881:1: ( ( rule__XAttributeTypeRef__Group_2__0 )? )
            // InternalOseeDsl.g:2882:1: ( rule__XAttributeTypeRef__Group_2__0 )?
            {
             before(grammarAccess.getXAttributeTypeRefAccess().getGroup_2()); 
            // InternalOseeDsl.g:2883:1: ( rule__XAttributeTypeRef__Group_2__0 )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==44) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // InternalOseeDsl.g:2883:2: rule__XAttributeTypeRef__Group_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeTypeRef__Group_2__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXAttributeTypeRefAccess().getGroup_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__Group__2__Impl"


    // $ANTLR start "rule__XAttributeTypeRef__Group_2__0"
    // InternalOseeDsl.g:2899:1: rule__XAttributeTypeRef__Group_2__0 : rule__XAttributeTypeRef__Group_2__0__Impl rule__XAttributeTypeRef__Group_2__1 ;
    public final void rule__XAttributeTypeRef__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2903:1: ( rule__XAttributeTypeRef__Group_2__0__Impl rule__XAttributeTypeRef__Group_2__1 )
            // InternalOseeDsl.g:2904:2: rule__XAttributeTypeRef__Group_2__0__Impl rule__XAttributeTypeRef__Group_2__1
            {
            pushFollow(FOLLOW_14);
            rule__XAttributeTypeRef__Group_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeTypeRef__Group_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__Group_2__0"


    // $ANTLR start "rule__XAttributeTypeRef__Group_2__0__Impl"
    // InternalOseeDsl.g:2911:1: rule__XAttributeTypeRef__Group_2__0__Impl : ( 'branchUuid' ) ;
    public final void rule__XAttributeTypeRef__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2915:1: ( ( 'branchUuid' ) )
            // InternalOseeDsl.g:2916:1: ( 'branchUuid' )
            {
            // InternalOseeDsl.g:2916:1: ( 'branchUuid' )
            // InternalOseeDsl.g:2917:1: 'branchUuid'
            {
             before(grammarAccess.getXAttributeTypeRefAccess().getBranchUuidKeyword_2_0()); 
            match(input,44,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeRefAccess().getBranchUuidKeyword_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__Group_2__0__Impl"


    // $ANTLR start "rule__XAttributeTypeRef__Group_2__1"
    // InternalOseeDsl.g:2930:1: rule__XAttributeTypeRef__Group_2__1 : rule__XAttributeTypeRef__Group_2__1__Impl ;
    public final void rule__XAttributeTypeRef__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2934:1: ( rule__XAttributeTypeRef__Group_2__1__Impl )
            // InternalOseeDsl.g:2935:2: rule__XAttributeTypeRef__Group_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeTypeRef__Group_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__Group_2__1"


    // $ANTLR start "rule__XAttributeTypeRef__Group_2__1__Impl"
    // InternalOseeDsl.g:2941:1: rule__XAttributeTypeRef__Group_2__1__Impl : ( ( rule__XAttributeTypeRef__BranchUuidAssignment_2_1 ) ) ;
    public final void rule__XAttributeTypeRef__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2945:1: ( ( ( rule__XAttributeTypeRef__BranchUuidAssignment_2_1 ) ) )
            // InternalOseeDsl.g:2946:1: ( ( rule__XAttributeTypeRef__BranchUuidAssignment_2_1 ) )
            {
            // InternalOseeDsl.g:2946:1: ( ( rule__XAttributeTypeRef__BranchUuidAssignment_2_1 ) )
            // InternalOseeDsl.g:2947:1: ( rule__XAttributeTypeRef__BranchUuidAssignment_2_1 )
            {
             before(grammarAccess.getXAttributeTypeRefAccess().getBranchUuidAssignment_2_1()); 
            // InternalOseeDsl.g:2948:1: ( rule__XAttributeTypeRef__BranchUuidAssignment_2_1 )
            // InternalOseeDsl.g:2948:2: rule__XAttributeTypeRef__BranchUuidAssignment_2_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeTypeRef__BranchUuidAssignment_2_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeRefAccess().getBranchUuidAssignment_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__Group_2__1__Impl"


    // $ANTLR start "rule__XAttributeType__Group__0"
    // InternalOseeDsl.g:2962:1: rule__XAttributeType__Group__0 : rule__XAttributeType__Group__0__Impl rule__XAttributeType__Group__1 ;
    public final void rule__XAttributeType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2966:1: ( rule__XAttributeType__Group__0__Impl rule__XAttributeType__Group__1 )
            // InternalOseeDsl.g:2967:2: rule__XAttributeType__Group__0__Impl rule__XAttributeType__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__XAttributeType__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__0"


    // $ANTLR start "rule__XAttributeType__Group__0__Impl"
    // InternalOseeDsl.g:2974:1: rule__XAttributeType__Group__0__Impl : ( 'attributeType' ) ;
    public final void rule__XAttributeType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2978:1: ( ( 'attributeType' ) )
            // InternalOseeDsl.g:2979:1: ( 'attributeType' )
            {
            // InternalOseeDsl.g:2979:1: ( 'attributeType' )
            // InternalOseeDsl.g:2980:1: 'attributeType'
            {
             before(grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0()); 
            match(input,60,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getAttributeTypeKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__0__Impl"


    // $ANTLR start "rule__XAttributeType__Group__1"
    // InternalOseeDsl.g:2993:1: rule__XAttributeType__Group__1 : rule__XAttributeType__Group__1__Impl rule__XAttributeType__Group__2 ;
    public final void rule__XAttributeType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:2997:1: ( rule__XAttributeType__Group__1__Impl rule__XAttributeType__Group__2 )
            // InternalOseeDsl.g:2998:2: rule__XAttributeType__Group__1__Impl rule__XAttributeType__Group__2
            {
            pushFollow(FOLLOW_20);
            rule__XAttributeType__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__1"


    // $ANTLR start "rule__XAttributeType__Group__1__Impl"
    // InternalOseeDsl.g:3005:1: rule__XAttributeType__Group__1__Impl : ( ( rule__XAttributeType__NameAssignment_1 ) ) ;
    public final void rule__XAttributeType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3009:1: ( ( ( rule__XAttributeType__NameAssignment_1 ) ) )
            // InternalOseeDsl.g:3010:1: ( ( rule__XAttributeType__NameAssignment_1 ) )
            {
            // InternalOseeDsl.g:3010:1: ( ( rule__XAttributeType__NameAssignment_1 ) )
            // InternalOseeDsl.g:3011:1: ( rule__XAttributeType__NameAssignment_1 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getNameAssignment_1()); 
            // InternalOseeDsl.g:3012:1: ( rule__XAttributeType__NameAssignment_1 )
            // InternalOseeDsl.g:3012:2: rule__XAttributeType__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__1__Impl"


    // $ANTLR start "rule__XAttributeType__Group__2"
    // InternalOseeDsl.g:3022:1: rule__XAttributeType__Group__2 : rule__XAttributeType__Group__2__Impl rule__XAttributeType__Group__3 ;
    public final void rule__XAttributeType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3026:1: ( rule__XAttributeType__Group__2__Impl rule__XAttributeType__Group__3 )
            // InternalOseeDsl.g:3027:2: rule__XAttributeType__Group__2__Impl rule__XAttributeType__Group__3
            {
            pushFollow(FOLLOW_21);
            rule__XAttributeType__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__2"


    // $ANTLR start "rule__XAttributeType__Group__2__Impl"
    // InternalOseeDsl.g:3034:1: rule__XAttributeType__Group__2__Impl : ( ( rule__XAttributeType__Group_2__0 ) ) ;
    public final void rule__XAttributeType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3038:1: ( ( ( rule__XAttributeType__Group_2__0 ) ) )
            // InternalOseeDsl.g:3039:1: ( ( rule__XAttributeType__Group_2__0 ) )
            {
            // InternalOseeDsl.g:3039:1: ( ( rule__XAttributeType__Group_2__0 ) )
            // InternalOseeDsl.g:3040:1: ( rule__XAttributeType__Group_2__0 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getGroup_2()); 
            // InternalOseeDsl.g:3041:1: ( rule__XAttributeType__Group_2__0 )
            // InternalOseeDsl.g:3041:2: rule__XAttributeType__Group_2__0
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_2__0();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getGroup_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__2__Impl"


    // $ANTLR start "rule__XAttributeType__Group__3"
    // InternalOseeDsl.g:3051:1: rule__XAttributeType__Group__3 : rule__XAttributeType__Group__3__Impl rule__XAttributeType__Group__4 ;
    public final void rule__XAttributeType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3055:1: ( rule__XAttributeType__Group__3__Impl rule__XAttributeType__Group__4 )
            // InternalOseeDsl.g:3056:2: rule__XAttributeType__Group__3__Impl rule__XAttributeType__Group__4
            {
            pushFollow(FOLLOW_21);
            rule__XAttributeType__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__3"


    // $ANTLR start "rule__XAttributeType__Group__3__Impl"
    // InternalOseeDsl.g:3063:1: rule__XAttributeType__Group__3__Impl : ( ( rule__XAttributeType__Group_3__0 )? ) ;
    public final void rule__XAttributeType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3067:1: ( ( ( rule__XAttributeType__Group_3__0 )? ) )
            // InternalOseeDsl.g:3068:1: ( ( rule__XAttributeType__Group_3__0 )? )
            {
            // InternalOseeDsl.g:3068:1: ( ( rule__XAttributeType__Group_3__0 )? )
            // InternalOseeDsl.g:3069:1: ( rule__XAttributeType__Group_3__0 )?
            {
             before(grammarAccess.getXAttributeTypeAccess().getGroup_3()); 
            // InternalOseeDsl.g:3070:1: ( rule__XAttributeType__Group_3__0 )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==64) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // InternalOseeDsl.g:3070:2: rule__XAttributeType__Group_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__Group_3__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXAttributeTypeAccess().getGroup_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__3__Impl"


    // $ANTLR start "rule__XAttributeType__Group__4"
    // InternalOseeDsl.g:3080:1: rule__XAttributeType__Group__4 : rule__XAttributeType__Group__4__Impl rule__XAttributeType__Group__5 ;
    public final void rule__XAttributeType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3084:1: ( rule__XAttributeType__Group__4__Impl rule__XAttributeType__Group__5 )
            // InternalOseeDsl.g:3085:2: rule__XAttributeType__Group__4__Impl rule__XAttributeType__Group__5
            {
            pushFollow(FOLLOW_13);
            rule__XAttributeType__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__4"


    // $ANTLR start "rule__XAttributeType__Group__4__Impl"
    // InternalOseeDsl.g:3092:1: rule__XAttributeType__Group__4__Impl : ( '{' ) ;
    public final void rule__XAttributeType__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3096:1: ( ( '{' ) )
            // InternalOseeDsl.g:3097:1: ( '{' )
            {
            // InternalOseeDsl.g:3097:1: ( '{' )
            // InternalOseeDsl.g:3098:1: '{'
            {
             before(grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4()); 
            match(input,53,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getLeftCurlyBracketKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__4__Impl"


    // $ANTLR start "rule__XAttributeType__Group__5"
    // InternalOseeDsl.g:3111:1: rule__XAttributeType__Group__5 : rule__XAttributeType__Group__5__Impl rule__XAttributeType__Group__6 ;
    public final void rule__XAttributeType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3115:1: ( rule__XAttributeType__Group__5__Impl rule__XAttributeType__Group__6 )
            // InternalOseeDsl.g:3116:2: rule__XAttributeType__Group__5__Impl rule__XAttributeType__Group__6
            {
            pushFollow(FOLLOW_13);
            rule__XAttributeType__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__5"


    // $ANTLR start "rule__XAttributeType__Group__5__Impl"
    // InternalOseeDsl.g:3123:1: rule__XAttributeType__Group__5__Impl : ( ( rule__XAttributeType__Group_5__0 )? ) ;
    public final void rule__XAttributeType__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3127:1: ( ( ( rule__XAttributeType__Group_5__0 )? ) )
            // InternalOseeDsl.g:3128:1: ( ( rule__XAttributeType__Group_5__0 )? )
            {
            // InternalOseeDsl.g:3128:1: ( ( rule__XAttributeType__Group_5__0 )? )
            // InternalOseeDsl.g:3129:1: ( rule__XAttributeType__Group_5__0 )?
            {
             before(grammarAccess.getXAttributeTypeAccess().getGroup_5()); 
            // InternalOseeDsl.g:3130:1: ( rule__XAttributeType__Group_5__0 )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==58) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // InternalOseeDsl.g:3130:2: rule__XAttributeType__Group_5__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__Group_5__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXAttributeTypeAccess().getGroup_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__5__Impl"


    // $ANTLR start "rule__XAttributeType__Group__6"
    // InternalOseeDsl.g:3140:1: rule__XAttributeType__Group__6 : rule__XAttributeType__Group__6__Impl rule__XAttributeType__Group__7 ;
    public final void rule__XAttributeType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3144:1: ( rule__XAttributeType__Group__6__Impl rule__XAttributeType__Group__7 )
            // InternalOseeDsl.g:3145:2: rule__XAttributeType__Group__6__Impl rule__XAttributeType__Group__7
            {
            pushFollow(FOLLOW_14);
            rule__XAttributeType__Group__6__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__7();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__6"


    // $ANTLR start "rule__XAttributeType__Group__6__Impl"
    // InternalOseeDsl.g:3152:1: rule__XAttributeType__Group__6__Impl : ( 'id' ) ;
    public final void rule__XAttributeType__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3156:1: ( ( 'id' ) )
            // InternalOseeDsl.g:3157:1: ( 'id' )
            {
            // InternalOseeDsl.g:3157:1: ( 'id' )
            // InternalOseeDsl.g:3158:1: 'id'
            {
             before(grammarAccess.getXAttributeTypeAccess().getIdKeyword_6()); 
            match(input,54,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getIdKeyword_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__6__Impl"


    // $ANTLR start "rule__XAttributeType__Group__7"
    // InternalOseeDsl.g:3171:1: rule__XAttributeType__Group__7 : rule__XAttributeType__Group__7__Impl rule__XAttributeType__Group__8 ;
    public final void rule__XAttributeType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3175:1: ( rule__XAttributeType__Group__7__Impl rule__XAttributeType__Group__8 )
            // InternalOseeDsl.g:3176:2: rule__XAttributeType__Group__7__Impl rule__XAttributeType__Group__8
            {
            pushFollow(FOLLOW_22);
            rule__XAttributeType__Group__7__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__8();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__7"


    // $ANTLR start "rule__XAttributeType__Group__7__Impl"
    // InternalOseeDsl.g:3183:1: rule__XAttributeType__Group__7__Impl : ( ( rule__XAttributeType__IdAssignment_7 ) ) ;
    public final void rule__XAttributeType__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3187:1: ( ( ( rule__XAttributeType__IdAssignment_7 ) ) )
            // InternalOseeDsl.g:3188:1: ( ( rule__XAttributeType__IdAssignment_7 ) )
            {
            // InternalOseeDsl.g:3188:1: ( ( rule__XAttributeType__IdAssignment_7 ) )
            // InternalOseeDsl.g:3189:1: ( rule__XAttributeType__IdAssignment_7 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getIdAssignment_7()); 
            // InternalOseeDsl.g:3190:1: ( rule__XAttributeType__IdAssignment_7 )
            // InternalOseeDsl.g:3190:2: rule__XAttributeType__IdAssignment_7
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__IdAssignment_7();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getIdAssignment_7()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__7__Impl"


    // $ANTLR start "rule__XAttributeType__Group__8"
    // InternalOseeDsl.g:3200:1: rule__XAttributeType__Group__8 : rule__XAttributeType__Group__8__Impl rule__XAttributeType__Group__9 ;
    public final void rule__XAttributeType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3204:1: ( rule__XAttributeType__Group__8__Impl rule__XAttributeType__Group__9 )
            // InternalOseeDsl.g:3205:2: rule__XAttributeType__Group__8__Impl rule__XAttributeType__Group__9
            {
            pushFollow(FOLLOW_23);
            rule__XAttributeType__Group__8__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__9();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__8"


    // $ANTLR start "rule__XAttributeType__Group__8__Impl"
    // InternalOseeDsl.g:3212:1: rule__XAttributeType__Group__8__Impl : ( 'dataProvider' ) ;
    public final void rule__XAttributeType__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3216:1: ( ( 'dataProvider' ) )
            // InternalOseeDsl.g:3217:1: ( 'dataProvider' )
            {
            // InternalOseeDsl.g:3217:1: ( 'dataProvider' )
            // InternalOseeDsl.g:3218:1: 'dataProvider'
            {
             before(grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_8()); 
            match(input,61,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getDataProviderKeyword_8()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__8__Impl"


    // $ANTLR start "rule__XAttributeType__Group__9"
    // InternalOseeDsl.g:3231:1: rule__XAttributeType__Group__9 : rule__XAttributeType__Group__9__Impl rule__XAttributeType__Group__10 ;
    public final void rule__XAttributeType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3235:1: ( rule__XAttributeType__Group__9__Impl rule__XAttributeType__Group__10 )
            // InternalOseeDsl.g:3236:2: rule__XAttributeType__Group__9__Impl rule__XAttributeType__Group__10
            {
            pushFollow(FOLLOW_24);
            rule__XAttributeType__Group__9__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__10();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__9"


    // $ANTLR start "rule__XAttributeType__Group__9__Impl"
    // InternalOseeDsl.g:3243:1: rule__XAttributeType__Group__9__Impl : ( ( rule__XAttributeType__DataProviderAssignment_9 ) ) ;
    public final void rule__XAttributeType__Group__9__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3247:1: ( ( ( rule__XAttributeType__DataProviderAssignment_9 ) ) )
            // InternalOseeDsl.g:3248:1: ( ( rule__XAttributeType__DataProviderAssignment_9 ) )
            {
            // InternalOseeDsl.g:3248:1: ( ( rule__XAttributeType__DataProviderAssignment_9 ) )
            // InternalOseeDsl.g:3249:1: ( rule__XAttributeType__DataProviderAssignment_9 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getDataProviderAssignment_9()); 
            // InternalOseeDsl.g:3250:1: ( rule__XAttributeType__DataProviderAssignment_9 )
            // InternalOseeDsl.g:3250:2: rule__XAttributeType__DataProviderAssignment_9
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__DataProviderAssignment_9();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getDataProviderAssignment_9()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__9__Impl"


    // $ANTLR start "rule__XAttributeType__Group__10"
    // InternalOseeDsl.g:3260:1: rule__XAttributeType__Group__10 : rule__XAttributeType__Group__10__Impl rule__XAttributeType__Group__11 ;
    public final void rule__XAttributeType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3264:1: ( rule__XAttributeType__Group__10__Impl rule__XAttributeType__Group__11 )
            // InternalOseeDsl.g:3265:2: rule__XAttributeType__Group__10__Impl rule__XAttributeType__Group__11
            {
            pushFollow(FOLLOW_14);
            rule__XAttributeType__Group__10__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__11();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__10"


    // $ANTLR start "rule__XAttributeType__Group__10__Impl"
    // InternalOseeDsl.g:3272:1: rule__XAttributeType__Group__10__Impl : ( 'min' ) ;
    public final void rule__XAttributeType__Group__10__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3276:1: ( ( 'min' ) )
            // InternalOseeDsl.g:3277:1: ( 'min' )
            {
            // InternalOseeDsl.g:3277:1: ( 'min' )
            // InternalOseeDsl.g:3278:1: 'min'
            {
             before(grammarAccess.getXAttributeTypeAccess().getMinKeyword_10()); 
            match(input,62,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getMinKeyword_10()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__10__Impl"


    // $ANTLR start "rule__XAttributeType__Group__11"
    // InternalOseeDsl.g:3291:1: rule__XAttributeType__Group__11 : rule__XAttributeType__Group__11__Impl rule__XAttributeType__Group__12 ;
    public final void rule__XAttributeType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3295:1: ( rule__XAttributeType__Group__11__Impl rule__XAttributeType__Group__12 )
            // InternalOseeDsl.g:3296:2: rule__XAttributeType__Group__11__Impl rule__XAttributeType__Group__12
            {
            pushFollow(FOLLOW_25);
            rule__XAttributeType__Group__11__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__12();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__11"


    // $ANTLR start "rule__XAttributeType__Group__11__Impl"
    // InternalOseeDsl.g:3303:1: rule__XAttributeType__Group__11__Impl : ( ( rule__XAttributeType__MinAssignment_11 ) ) ;
    public final void rule__XAttributeType__Group__11__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3307:1: ( ( ( rule__XAttributeType__MinAssignment_11 ) ) )
            // InternalOseeDsl.g:3308:1: ( ( rule__XAttributeType__MinAssignment_11 ) )
            {
            // InternalOseeDsl.g:3308:1: ( ( rule__XAttributeType__MinAssignment_11 ) )
            // InternalOseeDsl.g:3309:1: ( rule__XAttributeType__MinAssignment_11 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getMinAssignment_11()); 
            // InternalOseeDsl.g:3310:1: ( rule__XAttributeType__MinAssignment_11 )
            // InternalOseeDsl.g:3310:2: rule__XAttributeType__MinAssignment_11
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__MinAssignment_11();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getMinAssignment_11()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__11__Impl"


    // $ANTLR start "rule__XAttributeType__Group__12"
    // InternalOseeDsl.g:3320:1: rule__XAttributeType__Group__12 : rule__XAttributeType__Group__12__Impl rule__XAttributeType__Group__13 ;
    public final void rule__XAttributeType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3324:1: ( rule__XAttributeType__Group__12__Impl rule__XAttributeType__Group__13 )
            // InternalOseeDsl.g:3325:2: rule__XAttributeType__Group__12__Impl rule__XAttributeType__Group__13
            {
            pushFollow(FOLLOW_26);
            rule__XAttributeType__Group__12__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__13();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__12"


    // $ANTLR start "rule__XAttributeType__Group__12__Impl"
    // InternalOseeDsl.g:3332:1: rule__XAttributeType__Group__12__Impl : ( 'max' ) ;
    public final void rule__XAttributeType__Group__12__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3336:1: ( ( 'max' ) )
            // InternalOseeDsl.g:3337:1: ( 'max' )
            {
            // InternalOseeDsl.g:3337:1: ( 'max' )
            // InternalOseeDsl.g:3338:1: 'max'
            {
             before(grammarAccess.getXAttributeTypeAccess().getMaxKeyword_12()); 
            match(input,63,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getMaxKeyword_12()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__12__Impl"


    // $ANTLR start "rule__XAttributeType__Group__13"
    // InternalOseeDsl.g:3351:1: rule__XAttributeType__Group__13 : rule__XAttributeType__Group__13__Impl rule__XAttributeType__Group__14 ;
    public final void rule__XAttributeType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3355:1: ( rule__XAttributeType__Group__13__Impl rule__XAttributeType__Group__14 )
            // InternalOseeDsl.g:3356:2: rule__XAttributeType__Group__13__Impl rule__XAttributeType__Group__14
            {
            pushFollow(FOLLOW_27);
            rule__XAttributeType__Group__13__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__14();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__13"


    // $ANTLR start "rule__XAttributeType__Group__13__Impl"
    // InternalOseeDsl.g:3363:1: rule__XAttributeType__Group__13__Impl : ( ( rule__XAttributeType__MaxAssignment_13 ) ) ;
    public final void rule__XAttributeType__Group__13__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3367:1: ( ( ( rule__XAttributeType__MaxAssignment_13 ) ) )
            // InternalOseeDsl.g:3368:1: ( ( rule__XAttributeType__MaxAssignment_13 ) )
            {
            // InternalOseeDsl.g:3368:1: ( ( rule__XAttributeType__MaxAssignment_13 ) )
            // InternalOseeDsl.g:3369:1: ( rule__XAttributeType__MaxAssignment_13 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getMaxAssignment_13()); 
            // InternalOseeDsl.g:3370:1: ( rule__XAttributeType__MaxAssignment_13 )
            // InternalOseeDsl.g:3370:2: rule__XAttributeType__MaxAssignment_13
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__MaxAssignment_13();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getMaxAssignment_13()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__13__Impl"


    // $ANTLR start "rule__XAttributeType__Group__14"
    // InternalOseeDsl.g:3380:1: rule__XAttributeType__Group__14 : rule__XAttributeType__Group__14__Impl rule__XAttributeType__Group__15 ;
    public final void rule__XAttributeType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3384:1: ( rule__XAttributeType__Group__14__Impl rule__XAttributeType__Group__15 )
            // InternalOseeDsl.g:3385:2: rule__XAttributeType__Group__14__Impl rule__XAttributeType__Group__15
            {
            pushFollow(FOLLOW_28);
            rule__XAttributeType__Group__14__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__15();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__14"


    // $ANTLR start "rule__XAttributeType__Group__14__Impl"
    // InternalOseeDsl.g:3392:1: rule__XAttributeType__Group__14__Impl : ( ( rule__XAttributeType__UnorderedGroup_14 ) ) ;
    public final void rule__XAttributeType__Group__14__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3396:1: ( ( ( rule__XAttributeType__UnorderedGroup_14 ) ) )
            // InternalOseeDsl.g:3397:1: ( ( rule__XAttributeType__UnorderedGroup_14 ) )
            {
            // InternalOseeDsl.g:3397:1: ( ( rule__XAttributeType__UnorderedGroup_14 ) )
            // InternalOseeDsl.g:3398:1: ( rule__XAttributeType__UnorderedGroup_14 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14()); 
            // InternalOseeDsl.g:3399:1: ( rule__XAttributeType__UnorderedGroup_14 )
            // InternalOseeDsl.g:3399:2: rule__XAttributeType__UnorderedGroup_14
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__UnorderedGroup_14();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__14__Impl"


    // $ANTLR start "rule__XAttributeType__Group__15"
    // InternalOseeDsl.g:3409:1: rule__XAttributeType__Group__15 : rule__XAttributeType__Group__15__Impl ;
    public final void rule__XAttributeType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3413:1: ( rule__XAttributeType__Group__15__Impl )
            // InternalOseeDsl.g:3414:2: rule__XAttributeType__Group__15__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group__15__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__15"


    // $ANTLR start "rule__XAttributeType__Group__15__Impl"
    // InternalOseeDsl.g:3420:1: rule__XAttributeType__Group__15__Impl : ( '}' ) ;
    public final void rule__XAttributeType__Group__15__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3424:1: ( ( '}' ) )
            // InternalOseeDsl.g:3425:1: ( '}' )
            {
            // InternalOseeDsl.g:3425:1: ( '}' )
            // InternalOseeDsl.g:3426:1: '}'
            {
             before(grammarAccess.getXAttributeTypeAccess().getRightCurlyBracketKeyword_15()); 
            match(input,55,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getRightCurlyBracketKeyword_15()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group__15__Impl"


    // $ANTLR start "rule__XAttributeType__Group_2__0"
    // InternalOseeDsl.g:3471:1: rule__XAttributeType__Group_2__0 : rule__XAttributeType__Group_2__0__Impl rule__XAttributeType__Group_2__1 ;
    public final void rule__XAttributeType__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3475:1: ( rule__XAttributeType__Group_2__0__Impl rule__XAttributeType__Group_2__1 )
            // InternalOseeDsl.g:3476:2: rule__XAttributeType__Group_2__0__Impl rule__XAttributeType__Group_2__1
            {
            pushFollow(FOLLOW_29);
            rule__XAttributeType__Group_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_2__0"


    // $ANTLR start "rule__XAttributeType__Group_2__0__Impl"
    // InternalOseeDsl.g:3483:1: rule__XAttributeType__Group_2__0__Impl : ( 'extends' ) ;
    public final void rule__XAttributeType__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3487:1: ( ( 'extends' ) )
            // InternalOseeDsl.g:3488:1: ( 'extends' )
            {
            // InternalOseeDsl.g:3488:1: ( 'extends' )
            // InternalOseeDsl.g:3489:1: 'extends'
            {
             before(grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0()); 
            match(input,56,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getExtendsKeyword_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_2__0__Impl"


    // $ANTLR start "rule__XAttributeType__Group_2__1"
    // InternalOseeDsl.g:3502:1: rule__XAttributeType__Group_2__1 : rule__XAttributeType__Group_2__1__Impl ;
    public final void rule__XAttributeType__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3506:1: ( rule__XAttributeType__Group_2__1__Impl )
            // InternalOseeDsl.g:3507:2: rule__XAttributeType__Group_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_2__1"


    // $ANTLR start "rule__XAttributeType__Group_2__1__Impl"
    // InternalOseeDsl.g:3513:1: rule__XAttributeType__Group_2__1__Impl : ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) ) ;
    public final void rule__XAttributeType__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3517:1: ( ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) ) )
            // InternalOseeDsl.g:3518:1: ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) )
            {
            // InternalOseeDsl.g:3518:1: ( ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 ) )
            // InternalOseeDsl.g:3519:1: ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAssignment_2_1()); 
            // InternalOseeDsl.g:3520:1: ( rule__XAttributeType__BaseAttributeTypeAssignment_2_1 )
            // InternalOseeDsl.g:3520:2: rule__XAttributeType__BaseAttributeTypeAssignment_2_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__BaseAttributeTypeAssignment_2_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAssignment_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_2__1__Impl"


    // $ANTLR start "rule__XAttributeType__Group_3__0"
    // InternalOseeDsl.g:3534:1: rule__XAttributeType__Group_3__0 : rule__XAttributeType__Group_3__0__Impl rule__XAttributeType__Group_3__1 ;
    public final void rule__XAttributeType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3538:1: ( rule__XAttributeType__Group_3__0__Impl rule__XAttributeType__Group_3__1 )
            // InternalOseeDsl.g:3539:2: rule__XAttributeType__Group_3__0__Impl rule__XAttributeType__Group_3__1
            {
            pushFollow(FOLLOW_7);
            rule__XAttributeType__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_3__0"


    // $ANTLR start "rule__XAttributeType__Group_3__0__Impl"
    // InternalOseeDsl.g:3546:1: rule__XAttributeType__Group_3__0__Impl : ( 'overrides' ) ;
    public final void rule__XAttributeType__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3550:1: ( ( 'overrides' ) )
            // InternalOseeDsl.g:3551:1: ( 'overrides' )
            {
            // InternalOseeDsl.g:3551:1: ( 'overrides' )
            // InternalOseeDsl.g:3552:1: 'overrides'
            {
             before(grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0()); 
            match(input,64,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getOverridesKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_3__0__Impl"


    // $ANTLR start "rule__XAttributeType__Group_3__1"
    // InternalOseeDsl.g:3565:1: rule__XAttributeType__Group_3__1 : rule__XAttributeType__Group_3__1__Impl ;
    public final void rule__XAttributeType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3569:1: ( rule__XAttributeType__Group_3__1__Impl )
            // InternalOseeDsl.g:3570:2: rule__XAttributeType__Group_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_3__1"


    // $ANTLR start "rule__XAttributeType__Group_3__1__Impl"
    // InternalOseeDsl.g:3576:1: rule__XAttributeType__Group_3__1__Impl : ( ( rule__XAttributeType__OverrideAssignment_3_1 ) ) ;
    public final void rule__XAttributeType__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3580:1: ( ( ( rule__XAttributeType__OverrideAssignment_3_1 ) ) )
            // InternalOseeDsl.g:3581:1: ( ( rule__XAttributeType__OverrideAssignment_3_1 ) )
            {
            // InternalOseeDsl.g:3581:1: ( ( rule__XAttributeType__OverrideAssignment_3_1 ) )
            // InternalOseeDsl.g:3582:1: ( rule__XAttributeType__OverrideAssignment_3_1 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getOverrideAssignment_3_1()); 
            // InternalOseeDsl.g:3583:1: ( rule__XAttributeType__OverrideAssignment_3_1 )
            // InternalOseeDsl.g:3583:2: rule__XAttributeType__OverrideAssignment_3_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__OverrideAssignment_3_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getOverrideAssignment_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_3__1__Impl"


    // $ANTLR start "rule__XAttributeType__Group_5__0"
    // InternalOseeDsl.g:3597:1: rule__XAttributeType__Group_5__0 : rule__XAttributeType__Group_5__0__Impl rule__XAttributeType__Group_5__1 ;
    public final void rule__XAttributeType__Group_5__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3601:1: ( rule__XAttributeType__Group_5__0__Impl rule__XAttributeType__Group_5__1 )
            // InternalOseeDsl.g:3602:2: rule__XAttributeType__Group_5__0__Impl rule__XAttributeType__Group_5__1
            {
            pushFollow(FOLLOW_7);
            rule__XAttributeType__Group_5__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_5__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_5__0"


    // $ANTLR start "rule__XAttributeType__Group_5__0__Impl"
    // InternalOseeDsl.g:3609:1: rule__XAttributeType__Group_5__0__Impl : ( 'guid' ) ;
    public final void rule__XAttributeType__Group_5__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3613:1: ( ( 'guid' ) )
            // InternalOseeDsl.g:3614:1: ( 'guid' )
            {
            // InternalOseeDsl.g:3614:1: ( 'guid' )
            // InternalOseeDsl.g:3615:1: 'guid'
            {
             before(grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5_0()); 
            match(input,58,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getGuidKeyword_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_5__0__Impl"


    // $ANTLR start "rule__XAttributeType__Group_5__1"
    // InternalOseeDsl.g:3628:1: rule__XAttributeType__Group_5__1 : rule__XAttributeType__Group_5__1__Impl ;
    public final void rule__XAttributeType__Group_5__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3632:1: ( rule__XAttributeType__Group_5__1__Impl )
            // InternalOseeDsl.g:3633:2: rule__XAttributeType__Group_5__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_5__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_5__1"


    // $ANTLR start "rule__XAttributeType__Group_5__1__Impl"
    // InternalOseeDsl.g:3639:1: rule__XAttributeType__Group_5__1__Impl : ( ( rule__XAttributeType__TypeGuidAssignment_5_1 ) ) ;
    public final void rule__XAttributeType__Group_5__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3643:1: ( ( ( rule__XAttributeType__TypeGuidAssignment_5_1 ) ) )
            // InternalOseeDsl.g:3644:1: ( ( rule__XAttributeType__TypeGuidAssignment_5_1 ) )
            {
            // InternalOseeDsl.g:3644:1: ( ( rule__XAttributeType__TypeGuidAssignment_5_1 ) )
            // InternalOseeDsl.g:3645:1: ( rule__XAttributeType__TypeGuidAssignment_5_1 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getTypeGuidAssignment_5_1()); 
            // InternalOseeDsl.g:3646:1: ( rule__XAttributeType__TypeGuidAssignment_5_1 )
            // InternalOseeDsl.g:3646:2: rule__XAttributeType__TypeGuidAssignment_5_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__TypeGuidAssignment_5_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getTypeGuidAssignment_5_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_5__1__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_0__0"
    // InternalOseeDsl.g:3660:1: rule__XAttributeType__Group_14_0__0 : rule__XAttributeType__Group_14_0__0__Impl rule__XAttributeType__Group_14_0__1 ;
    public final void rule__XAttributeType__Group_14_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3664:1: ( rule__XAttributeType__Group_14_0__0__Impl rule__XAttributeType__Group_14_0__1 )
            // InternalOseeDsl.g:3665:2: rule__XAttributeType__Group_14_0__0__Impl rule__XAttributeType__Group_14_0__1
            {
            pushFollow(FOLLOW_30);
            rule__XAttributeType__Group_14_0__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_0__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_0__0"


    // $ANTLR start "rule__XAttributeType__Group_14_0__0__Impl"
    // InternalOseeDsl.g:3672:1: rule__XAttributeType__Group_14_0__0__Impl : ( 'taggerId' ) ;
    public final void rule__XAttributeType__Group_14_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3676:1: ( ( 'taggerId' ) )
            // InternalOseeDsl.g:3677:1: ( 'taggerId' )
            {
            // InternalOseeDsl.g:3677:1: ( 'taggerId' )
            // InternalOseeDsl.g:3678:1: 'taggerId'
            {
             before(grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_14_0_0()); 
            match(input,65,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getTaggerIdKeyword_14_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_0__0__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_0__1"
    // InternalOseeDsl.g:3691:1: rule__XAttributeType__Group_14_0__1 : rule__XAttributeType__Group_14_0__1__Impl ;
    public final void rule__XAttributeType__Group_14_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3695:1: ( rule__XAttributeType__Group_14_0__1__Impl )
            // InternalOseeDsl.g:3696:2: rule__XAttributeType__Group_14_0__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_0__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_0__1"


    // $ANTLR start "rule__XAttributeType__Group_14_0__1__Impl"
    // InternalOseeDsl.g:3702:1: rule__XAttributeType__Group_14_0__1__Impl : ( ( rule__XAttributeType__TaggerIdAssignment_14_0_1 ) ) ;
    public final void rule__XAttributeType__Group_14_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3706:1: ( ( ( rule__XAttributeType__TaggerIdAssignment_14_0_1 ) ) )
            // InternalOseeDsl.g:3707:1: ( ( rule__XAttributeType__TaggerIdAssignment_14_0_1 ) )
            {
            // InternalOseeDsl.g:3707:1: ( ( rule__XAttributeType__TaggerIdAssignment_14_0_1 ) )
            // InternalOseeDsl.g:3708:1: ( rule__XAttributeType__TaggerIdAssignment_14_0_1 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getTaggerIdAssignment_14_0_1()); 
            // InternalOseeDsl.g:3709:1: ( rule__XAttributeType__TaggerIdAssignment_14_0_1 )
            // InternalOseeDsl.g:3709:2: rule__XAttributeType__TaggerIdAssignment_14_0_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__TaggerIdAssignment_14_0_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getTaggerIdAssignment_14_0_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_0__1__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_1__0"
    // InternalOseeDsl.g:3723:1: rule__XAttributeType__Group_14_1__0 : rule__XAttributeType__Group_14_1__0__Impl rule__XAttributeType__Group_14_1__1 ;
    public final void rule__XAttributeType__Group_14_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3727:1: ( rule__XAttributeType__Group_14_1__0__Impl rule__XAttributeType__Group_14_1__1 )
            // InternalOseeDsl.g:3728:2: rule__XAttributeType__Group_14_1__0__Impl rule__XAttributeType__Group_14_1__1
            {
            pushFollow(FOLLOW_7);
            rule__XAttributeType__Group_14_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_1__0"


    // $ANTLR start "rule__XAttributeType__Group_14_1__0__Impl"
    // InternalOseeDsl.g:3735:1: rule__XAttributeType__Group_14_1__0__Impl : ( 'enumType' ) ;
    public final void rule__XAttributeType__Group_14_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3739:1: ( ( 'enumType' ) )
            // InternalOseeDsl.g:3740:1: ( 'enumType' )
            {
            // InternalOseeDsl.g:3740:1: ( 'enumType' )
            // InternalOseeDsl.g:3741:1: 'enumType'
            {
             before(grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_14_1_0()); 
            match(input,66,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getEnumTypeKeyword_14_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_1__0__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_1__1"
    // InternalOseeDsl.g:3754:1: rule__XAttributeType__Group_14_1__1 : rule__XAttributeType__Group_14_1__1__Impl ;
    public final void rule__XAttributeType__Group_14_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3758:1: ( rule__XAttributeType__Group_14_1__1__Impl )
            // InternalOseeDsl.g:3759:2: rule__XAttributeType__Group_14_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_1__1"


    // $ANTLR start "rule__XAttributeType__Group_14_1__1__Impl"
    // InternalOseeDsl.g:3765:1: rule__XAttributeType__Group_14_1__1__Impl : ( ( rule__XAttributeType__EnumTypeAssignment_14_1_1 ) ) ;
    public final void rule__XAttributeType__Group_14_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3769:1: ( ( ( rule__XAttributeType__EnumTypeAssignment_14_1_1 ) ) )
            // InternalOseeDsl.g:3770:1: ( ( rule__XAttributeType__EnumTypeAssignment_14_1_1 ) )
            {
            // InternalOseeDsl.g:3770:1: ( ( rule__XAttributeType__EnumTypeAssignment_14_1_1 ) )
            // InternalOseeDsl.g:3771:1: ( rule__XAttributeType__EnumTypeAssignment_14_1_1 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getEnumTypeAssignment_14_1_1()); 
            // InternalOseeDsl.g:3772:1: ( rule__XAttributeType__EnumTypeAssignment_14_1_1 )
            // InternalOseeDsl.g:3772:2: rule__XAttributeType__EnumTypeAssignment_14_1_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__EnumTypeAssignment_14_1_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getEnumTypeAssignment_14_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_1__1__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_2__0"
    // InternalOseeDsl.g:3786:1: rule__XAttributeType__Group_14_2__0 : rule__XAttributeType__Group_14_2__0__Impl rule__XAttributeType__Group_14_2__1 ;
    public final void rule__XAttributeType__Group_14_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3790:1: ( rule__XAttributeType__Group_14_2__0__Impl rule__XAttributeType__Group_14_2__1 )
            // InternalOseeDsl.g:3791:2: rule__XAttributeType__Group_14_2__0__Impl rule__XAttributeType__Group_14_2__1
            {
            pushFollow(FOLLOW_7);
            rule__XAttributeType__Group_14_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_2__0"


    // $ANTLR start "rule__XAttributeType__Group_14_2__0__Impl"
    // InternalOseeDsl.g:3798:1: rule__XAttributeType__Group_14_2__0__Impl : ( 'description' ) ;
    public final void rule__XAttributeType__Group_14_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3802:1: ( ( 'description' ) )
            // InternalOseeDsl.g:3803:1: ( 'description' )
            {
            // InternalOseeDsl.g:3803:1: ( 'description' )
            // InternalOseeDsl.g:3804:1: 'description'
            {
             before(grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_14_2_0()); 
            match(input,67,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getDescriptionKeyword_14_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_2__0__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_2__1"
    // InternalOseeDsl.g:3817:1: rule__XAttributeType__Group_14_2__1 : rule__XAttributeType__Group_14_2__1__Impl ;
    public final void rule__XAttributeType__Group_14_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3821:1: ( rule__XAttributeType__Group_14_2__1__Impl )
            // InternalOseeDsl.g:3822:2: rule__XAttributeType__Group_14_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_2__1"


    // $ANTLR start "rule__XAttributeType__Group_14_2__1__Impl"
    // InternalOseeDsl.g:3828:1: rule__XAttributeType__Group_14_2__1__Impl : ( ( rule__XAttributeType__DescriptionAssignment_14_2_1 ) ) ;
    public final void rule__XAttributeType__Group_14_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3832:1: ( ( ( rule__XAttributeType__DescriptionAssignment_14_2_1 ) ) )
            // InternalOseeDsl.g:3833:1: ( ( rule__XAttributeType__DescriptionAssignment_14_2_1 ) )
            {
            // InternalOseeDsl.g:3833:1: ( ( rule__XAttributeType__DescriptionAssignment_14_2_1 ) )
            // InternalOseeDsl.g:3834:1: ( rule__XAttributeType__DescriptionAssignment_14_2_1 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getDescriptionAssignment_14_2_1()); 
            // InternalOseeDsl.g:3835:1: ( rule__XAttributeType__DescriptionAssignment_14_2_1 )
            // InternalOseeDsl.g:3835:2: rule__XAttributeType__DescriptionAssignment_14_2_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__DescriptionAssignment_14_2_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getDescriptionAssignment_14_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_2__1__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_3__0"
    // InternalOseeDsl.g:3849:1: rule__XAttributeType__Group_14_3__0 : rule__XAttributeType__Group_14_3__0__Impl rule__XAttributeType__Group_14_3__1 ;
    public final void rule__XAttributeType__Group_14_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3853:1: ( rule__XAttributeType__Group_14_3__0__Impl rule__XAttributeType__Group_14_3__1 )
            // InternalOseeDsl.g:3854:2: rule__XAttributeType__Group_14_3__0__Impl rule__XAttributeType__Group_14_3__1
            {
            pushFollow(FOLLOW_7);
            rule__XAttributeType__Group_14_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_3__0"


    // $ANTLR start "rule__XAttributeType__Group_14_3__0__Impl"
    // InternalOseeDsl.g:3861:1: rule__XAttributeType__Group_14_3__0__Impl : ( 'defaultValue' ) ;
    public final void rule__XAttributeType__Group_14_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3865:1: ( ( 'defaultValue' ) )
            // InternalOseeDsl.g:3866:1: ( 'defaultValue' )
            {
            // InternalOseeDsl.g:3866:1: ( 'defaultValue' )
            // InternalOseeDsl.g:3867:1: 'defaultValue'
            {
             before(grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_14_3_0()); 
            match(input,68,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getDefaultValueKeyword_14_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_3__0__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_3__1"
    // InternalOseeDsl.g:3880:1: rule__XAttributeType__Group_14_3__1 : rule__XAttributeType__Group_14_3__1__Impl ;
    public final void rule__XAttributeType__Group_14_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3884:1: ( rule__XAttributeType__Group_14_3__1__Impl )
            // InternalOseeDsl.g:3885:2: rule__XAttributeType__Group_14_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_3__1"


    // $ANTLR start "rule__XAttributeType__Group_14_3__1__Impl"
    // InternalOseeDsl.g:3891:1: rule__XAttributeType__Group_14_3__1__Impl : ( ( rule__XAttributeType__DefaultValueAssignment_14_3_1 ) ) ;
    public final void rule__XAttributeType__Group_14_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3895:1: ( ( ( rule__XAttributeType__DefaultValueAssignment_14_3_1 ) ) )
            // InternalOseeDsl.g:3896:1: ( ( rule__XAttributeType__DefaultValueAssignment_14_3_1 ) )
            {
            // InternalOseeDsl.g:3896:1: ( ( rule__XAttributeType__DefaultValueAssignment_14_3_1 ) )
            // InternalOseeDsl.g:3897:1: ( rule__XAttributeType__DefaultValueAssignment_14_3_1 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getDefaultValueAssignment_14_3_1()); 
            // InternalOseeDsl.g:3898:1: ( rule__XAttributeType__DefaultValueAssignment_14_3_1 )
            // InternalOseeDsl.g:3898:2: rule__XAttributeType__DefaultValueAssignment_14_3_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__DefaultValueAssignment_14_3_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getDefaultValueAssignment_14_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_3__1__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_4__0"
    // InternalOseeDsl.g:3912:1: rule__XAttributeType__Group_14_4__0 : rule__XAttributeType__Group_14_4__0__Impl rule__XAttributeType__Group_14_4__1 ;
    public final void rule__XAttributeType__Group_14_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3916:1: ( rule__XAttributeType__Group_14_4__0__Impl rule__XAttributeType__Group_14_4__1 )
            // InternalOseeDsl.g:3917:2: rule__XAttributeType__Group_14_4__0__Impl rule__XAttributeType__Group_14_4__1
            {
            pushFollow(FOLLOW_7);
            rule__XAttributeType__Group_14_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_4__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_4__0"


    // $ANTLR start "rule__XAttributeType__Group_14_4__0__Impl"
    // InternalOseeDsl.g:3924:1: rule__XAttributeType__Group_14_4__0__Impl : ( 'fileExtension' ) ;
    public final void rule__XAttributeType__Group_14_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3928:1: ( ( 'fileExtension' ) )
            // InternalOseeDsl.g:3929:1: ( 'fileExtension' )
            {
            // InternalOseeDsl.g:3929:1: ( 'fileExtension' )
            // InternalOseeDsl.g:3930:1: 'fileExtension'
            {
             before(grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_14_4_0()); 
            match(input,69,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getFileExtensionKeyword_14_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_4__0__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_4__1"
    // InternalOseeDsl.g:3943:1: rule__XAttributeType__Group_14_4__1 : rule__XAttributeType__Group_14_4__1__Impl ;
    public final void rule__XAttributeType__Group_14_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3947:1: ( rule__XAttributeType__Group_14_4__1__Impl )
            // InternalOseeDsl.g:3948:2: rule__XAttributeType__Group_14_4__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_4__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_4__1"


    // $ANTLR start "rule__XAttributeType__Group_14_4__1__Impl"
    // InternalOseeDsl.g:3954:1: rule__XAttributeType__Group_14_4__1__Impl : ( ( rule__XAttributeType__FileExtensionAssignment_14_4_1 ) ) ;
    public final void rule__XAttributeType__Group_14_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3958:1: ( ( ( rule__XAttributeType__FileExtensionAssignment_14_4_1 ) ) )
            // InternalOseeDsl.g:3959:1: ( ( rule__XAttributeType__FileExtensionAssignment_14_4_1 ) )
            {
            // InternalOseeDsl.g:3959:1: ( ( rule__XAttributeType__FileExtensionAssignment_14_4_1 ) )
            // InternalOseeDsl.g:3960:1: ( rule__XAttributeType__FileExtensionAssignment_14_4_1 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getFileExtensionAssignment_14_4_1()); 
            // InternalOseeDsl.g:3961:1: ( rule__XAttributeType__FileExtensionAssignment_14_4_1 )
            // InternalOseeDsl.g:3961:2: rule__XAttributeType__FileExtensionAssignment_14_4_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__FileExtensionAssignment_14_4_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getFileExtensionAssignment_14_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_4__1__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_5__0"
    // InternalOseeDsl.g:3975:1: rule__XAttributeType__Group_14_5__0 : rule__XAttributeType__Group_14_5__0__Impl rule__XAttributeType__Group_14_5__1 ;
    public final void rule__XAttributeType__Group_14_5__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3979:1: ( rule__XAttributeType__Group_14_5__0__Impl rule__XAttributeType__Group_14_5__1 )
            // InternalOseeDsl.g:3980:2: rule__XAttributeType__Group_14_5__0__Impl rule__XAttributeType__Group_14_5__1
            {
            pushFollow(FOLLOW_7);
            rule__XAttributeType__Group_14_5__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_5__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_5__0"


    // $ANTLR start "rule__XAttributeType__Group_14_5__0__Impl"
    // InternalOseeDsl.g:3987:1: rule__XAttributeType__Group_14_5__0__Impl : ( 'mediaType' ) ;
    public final void rule__XAttributeType__Group_14_5__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:3991:1: ( ( 'mediaType' ) )
            // InternalOseeDsl.g:3992:1: ( 'mediaType' )
            {
            // InternalOseeDsl.g:3992:1: ( 'mediaType' )
            // InternalOseeDsl.g:3993:1: 'mediaType'
            {
             before(grammarAccess.getXAttributeTypeAccess().getMediaTypeKeyword_14_5_0()); 
            match(input,70,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getMediaTypeKeyword_14_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_5__0__Impl"


    // $ANTLR start "rule__XAttributeType__Group_14_5__1"
    // InternalOseeDsl.g:4006:1: rule__XAttributeType__Group_14_5__1 : rule__XAttributeType__Group_14_5__1__Impl ;
    public final void rule__XAttributeType__Group_14_5__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4010:1: ( rule__XAttributeType__Group_14_5__1__Impl )
            // InternalOseeDsl.g:4011:2: rule__XAttributeType__Group_14_5__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__Group_14_5__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_5__1"


    // $ANTLR start "rule__XAttributeType__Group_14_5__1__Impl"
    // InternalOseeDsl.g:4017:1: rule__XAttributeType__Group_14_5__1__Impl : ( ( rule__XAttributeType__MediaTypeAssignment_14_5_1 ) ) ;
    public final void rule__XAttributeType__Group_14_5__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4021:1: ( ( ( rule__XAttributeType__MediaTypeAssignment_14_5_1 ) ) )
            // InternalOseeDsl.g:4022:1: ( ( rule__XAttributeType__MediaTypeAssignment_14_5_1 ) )
            {
            // InternalOseeDsl.g:4022:1: ( ( rule__XAttributeType__MediaTypeAssignment_14_5_1 ) )
            // InternalOseeDsl.g:4023:1: ( rule__XAttributeType__MediaTypeAssignment_14_5_1 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getMediaTypeAssignment_14_5_1()); 
            // InternalOseeDsl.g:4024:1: ( rule__XAttributeType__MediaTypeAssignment_14_5_1 )
            // InternalOseeDsl.g:4024:2: rule__XAttributeType__MediaTypeAssignment_14_5_1
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__MediaTypeAssignment_14_5_1();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getMediaTypeAssignment_14_5_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__Group_14_5__1__Impl"


    // $ANTLR start "rule__XOseeEnumType__Group__0"
    // InternalOseeDsl.g:4038:1: rule__XOseeEnumType__Group__0 : rule__XOseeEnumType__Group__0__Impl rule__XOseeEnumType__Group__1 ;
    public final void rule__XOseeEnumType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4042:1: ( rule__XOseeEnumType__Group__0__Impl rule__XOseeEnumType__Group__1 )
            // InternalOseeDsl.g:4043:2: rule__XOseeEnumType__Group__0__Impl rule__XOseeEnumType__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__XOseeEnumType__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__0"


    // $ANTLR start "rule__XOseeEnumType__Group__0__Impl"
    // InternalOseeDsl.g:4050:1: rule__XOseeEnumType__Group__0__Impl : ( 'oseeEnumType' ) ;
    public final void rule__XOseeEnumType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4054:1: ( ( 'oseeEnumType' ) )
            // InternalOseeDsl.g:4055:1: ( 'oseeEnumType' )
            {
            // InternalOseeDsl.g:4055:1: ( 'oseeEnumType' )
            // InternalOseeDsl.g:4056:1: 'oseeEnumType'
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 
            match(input,71,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumTypeAccess().getOseeEnumTypeKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__0__Impl"


    // $ANTLR start "rule__XOseeEnumType__Group__1"
    // InternalOseeDsl.g:4069:1: rule__XOseeEnumType__Group__1 : rule__XOseeEnumType__Group__1__Impl rule__XOseeEnumType__Group__2 ;
    public final void rule__XOseeEnumType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4073:1: ( rule__XOseeEnumType__Group__1__Impl rule__XOseeEnumType__Group__2 )
            // InternalOseeDsl.g:4074:2: rule__XOseeEnumType__Group__1__Impl rule__XOseeEnumType__Group__2
            {
            pushFollow(FOLLOW_31);
            rule__XOseeEnumType__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__1"


    // $ANTLR start "rule__XOseeEnumType__Group__1__Impl"
    // InternalOseeDsl.g:4081:1: rule__XOseeEnumType__Group__1__Impl : ( ( rule__XOseeEnumType__NameAssignment_1 ) ) ;
    public final void rule__XOseeEnumType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4085:1: ( ( ( rule__XOseeEnumType__NameAssignment_1 ) ) )
            // InternalOseeDsl.g:4086:1: ( ( rule__XOseeEnumType__NameAssignment_1 ) )
            {
            // InternalOseeDsl.g:4086:1: ( ( rule__XOseeEnumType__NameAssignment_1 ) )
            // InternalOseeDsl.g:4087:1: ( rule__XOseeEnumType__NameAssignment_1 )
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getNameAssignment_1()); 
            // InternalOseeDsl.g:4088:1: ( rule__XOseeEnumType__NameAssignment_1 )
            // InternalOseeDsl.g:4088:2: rule__XOseeEnumType__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getXOseeEnumTypeAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__1__Impl"


    // $ANTLR start "rule__XOseeEnumType__Group__2"
    // InternalOseeDsl.g:4098:1: rule__XOseeEnumType__Group__2 : rule__XOseeEnumType__Group__2__Impl rule__XOseeEnumType__Group__3 ;
    public final void rule__XOseeEnumType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4102:1: ( rule__XOseeEnumType__Group__2__Impl rule__XOseeEnumType__Group__3 )
            // InternalOseeDsl.g:4103:2: rule__XOseeEnumType__Group__2__Impl rule__XOseeEnumType__Group__3
            {
            pushFollow(FOLLOW_13);
            rule__XOseeEnumType__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__2"


    // $ANTLR start "rule__XOseeEnumType__Group__2__Impl"
    // InternalOseeDsl.g:4110:1: rule__XOseeEnumType__Group__2__Impl : ( '{' ) ;
    public final void rule__XOseeEnumType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4114:1: ( ( '{' ) )
            // InternalOseeDsl.g:4115:1: ( '{' )
            {
            // InternalOseeDsl.g:4115:1: ( '{' )
            // InternalOseeDsl.g:4116:1: '{'
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,53,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumTypeAccess().getLeftCurlyBracketKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__2__Impl"


    // $ANTLR start "rule__XOseeEnumType__Group__3"
    // InternalOseeDsl.g:4129:1: rule__XOseeEnumType__Group__3 : rule__XOseeEnumType__Group__3__Impl rule__XOseeEnumType__Group__4 ;
    public final void rule__XOseeEnumType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4133:1: ( rule__XOseeEnumType__Group__3__Impl rule__XOseeEnumType__Group__4 )
            // InternalOseeDsl.g:4134:2: rule__XOseeEnumType__Group__3__Impl rule__XOseeEnumType__Group__4
            {
            pushFollow(FOLLOW_13);
            rule__XOseeEnumType__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__3"


    // $ANTLR start "rule__XOseeEnumType__Group__3__Impl"
    // InternalOseeDsl.g:4141:1: rule__XOseeEnumType__Group__3__Impl : ( ( rule__XOseeEnumType__Group_3__0 )? ) ;
    public final void rule__XOseeEnumType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4145:1: ( ( ( rule__XOseeEnumType__Group_3__0 )? ) )
            // InternalOseeDsl.g:4146:1: ( ( rule__XOseeEnumType__Group_3__0 )? )
            {
            // InternalOseeDsl.g:4146:1: ( ( rule__XOseeEnumType__Group_3__0 )? )
            // InternalOseeDsl.g:4147:1: ( rule__XOseeEnumType__Group_3__0 )?
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getGroup_3()); 
            // InternalOseeDsl.g:4148:1: ( rule__XOseeEnumType__Group_3__0 )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==58) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // InternalOseeDsl.g:4148:2: rule__XOseeEnumType__Group_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XOseeEnumType__Group_3__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXOseeEnumTypeAccess().getGroup_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__3__Impl"


    // $ANTLR start "rule__XOseeEnumType__Group__4"
    // InternalOseeDsl.g:4158:1: rule__XOseeEnumType__Group__4 : rule__XOseeEnumType__Group__4__Impl rule__XOseeEnumType__Group__5 ;
    public final void rule__XOseeEnumType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4162:1: ( rule__XOseeEnumType__Group__4__Impl rule__XOseeEnumType__Group__5 )
            // InternalOseeDsl.g:4163:2: rule__XOseeEnumType__Group__4__Impl rule__XOseeEnumType__Group__5
            {
            pushFollow(FOLLOW_14);
            rule__XOseeEnumType__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__4"


    // $ANTLR start "rule__XOseeEnumType__Group__4__Impl"
    // InternalOseeDsl.g:4170:1: rule__XOseeEnumType__Group__4__Impl : ( 'id' ) ;
    public final void rule__XOseeEnumType__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4174:1: ( ( 'id' ) )
            // InternalOseeDsl.g:4175:1: ( 'id' )
            {
            // InternalOseeDsl.g:4175:1: ( 'id' )
            // InternalOseeDsl.g:4176:1: 'id'
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getIdKeyword_4()); 
            match(input,54,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumTypeAccess().getIdKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__4__Impl"


    // $ANTLR start "rule__XOseeEnumType__Group__5"
    // InternalOseeDsl.g:4189:1: rule__XOseeEnumType__Group__5 : rule__XOseeEnumType__Group__5__Impl rule__XOseeEnumType__Group__6 ;
    public final void rule__XOseeEnumType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4193:1: ( rule__XOseeEnumType__Group__5__Impl rule__XOseeEnumType__Group__6 )
            // InternalOseeDsl.g:4194:2: rule__XOseeEnumType__Group__5__Impl rule__XOseeEnumType__Group__6
            {
            pushFollow(FOLLOW_32);
            rule__XOseeEnumType__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__5"


    // $ANTLR start "rule__XOseeEnumType__Group__5__Impl"
    // InternalOseeDsl.g:4201:1: rule__XOseeEnumType__Group__5__Impl : ( ( rule__XOseeEnumType__IdAssignment_5 ) ) ;
    public final void rule__XOseeEnumType__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4205:1: ( ( ( rule__XOseeEnumType__IdAssignment_5 ) ) )
            // InternalOseeDsl.g:4206:1: ( ( rule__XOseeEnumType__IdAssignment_5 ) )
            {
            // InternalOseeDsl.g:4206:1: ( ( rule__XOseeEnumType__IdAssignment_5 ) )
            // InternalOseeDsl.g:4207:1: ( rule__XOseeEnumType__IdAssignment_5 )
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getIdAssignment_5()); 
            // InternalOseeDsl.g:4208:1: ( rule__XOseeEnumType__IdAssignment_5 )
            // InternalOseeDsl.g:4208:2: rule__XOseeEnumType__IdAssignment_5
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__IdAssignment_5();

            state._fsp--;


            }

             after(grammarAccess.getXOseeEnumTypeAccess().getIdAssignment_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__5__Impl"


    // $ANTLR start "rule__XOseeEnumType__Group__6"
    // InternalOseeDsl.g:4218:1: rule__XOseeEnumType__Group__6 : rule__XOseeEnumType__Group__6__Impl rule__XOseeEnumType__Group__7 ;
    public final void rule__XOseeEnumType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4222:1: ( rule__XOseeEnumType__Group__6__Impl rule__XOseeEnumType__Group__7 )
            // InternalOseeDsl.g:4223:2: rule__XOseeEnumType__Group__6__Impl rule__XOseeEnumType__Group__7
            {
            pushFollow(FOLLOW_32);
            rule__XOseeEnumType__Group__6__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group__7();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__6"


    // $ANTLR start "rule__XOseeEnumType__Group__6__Impl"
    // InternalOseeDsl.g:4230:1: rule__XOseeEnumType__Group__6__Impl : ( ( rule__XOseeEnumType__EnumEntriesAssignment_6 )* ) ;
    public final void rule__XOseeEnumType__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4234:1: ( ( ( rule__XOseeEnumType__EnumEntriesAssignment_6 )* ) )
            // InternalOseeDsl.g:4235:1: ( ( rule__XOseeEnumType__EnumEntriesAssignment_6 )* )
            {
            // InternalOseeDsl.g:4235:1: ( ( rule__XOseeEnumType__EnumEntriesAssignment_6 )* )
            // InternalOseeDsl.g:4236:1: ( rule__XOseeEnumType__EnumEntriesAssignment_6 )*
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesAssignment_6()); 
            // InternalOseeDsl.g:4237:1: ( rule__XOseeEnumType__EnumEntriesAssignment_6 )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==72) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // InternalOseeDsl.g:4237:2: rule__XOseeEnumType__EnumEntriesAssignment_6
            	    {
            	    pushFollow(FOLLOW_33);
            	    rule__XOseeEnumType__EnumEntriesAssignment_6();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

             after(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesAssignment_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__6__Impl"


    // $ANTLR start "rule__XOseeEnumType__Group__7"
    // InternalOseeDsl.g:4247:1: rule__XOseeEnumType__Group__7 : rule__XOseeEnumType__Group__7__Impl ;
    public final void rule__XOseeEnumType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4251:1: ( rule__XOseeEnumType__Group__7__Impl )
            // InternalOseeDsl.g:4252:2: rule__XOseeEnumType__Group__7__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group__7__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__7"


    // $ANTLR start "rule__XOseeEnumType__Group__7__Impl"
    // InternalOseeDsl.g:4258:1: rule__XOseeEnumType__Group__7__Impl : ( '}' ) ;
    public final void rule__XOseeEnumType__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4262:1: ( ( '}' ) )
            // InternalOseeDsl.g:4263:1: ( '}' )
            {
            // InternalOseeDsl.g:4263:1: ( '}' )
            // InternalOseeDsl.g:4264:1: '}'
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getRightCurlyBracketKeyword_7()); 
            match(input,55,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumTypeAccess().getRightCurlyBracketKeyword_7()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group__7__Impl"


    // $ANTLR start "rule__XOseeEnumType__Group_3__0"
    // InternalOseeDsl.g:4293:1: rule__XOseeEnumType__Group_3__0 : rule__XOseeEnumType__Group_3__0__Impl rule__XOseeEnumType__Group_3__1 ;
    public final void rule__XOseeEnumType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4297:1: ( rule__XOseeEnumType__Group_3__0__Impl rule__XOseeEnumType__Group_3__1 )
            // InternalOseeDsl.g:4298:2: rule__XOseeEnumType__Group_3__0__Impl rule__XOseeEnumType__Group_3__1
            {
            pushFollow(FOLLOW_7);
            rule__XOseeEnumType__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group_3__0"


    // $ANTLR start "rule__XOseeEnumType__Group_3__0__Impl"
    // InternalOseeDsl.g:4305:1: rule__XOseeEnumType__Group_3__0__Impl : ( 'guid' ) ;
    public final void rule__XOseeEnumType__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4309:1: ( ( 'guid' ) )
            // InternalOseeDsl.g:4310:1: ( 'guid' )
            {
            // InternalOseeDsl.g:4310:1: ( 'guid' )
            // InternalOseeDsl.g:4311:1: 'guid'
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3_0()); 
            match(input,58,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumTypeAccess().getGuidKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group_3__0__Impl"


    // $ANTLR start "rule__XOseeEnumType__Group_3__1"
    // InternalOseeDsl.g:4324:1: rule__XOseeEnumType__Group_3__1 : rule__XOseeEnumType__Group_3__1__Impl ;
    public final void rule__XOseeEnumType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4328:1: ( rule__XOseeEnumType__Group_3__1__Impl )
            // InternalOseeDsl.g:4329:2: rule__XOseeEnumType__Group_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__Group_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group_3__1"


    // $ANTLR start "rule__XOseeEnumType__Group_3__1__Impl"
    // InternalOseeDsl.g:4335:1: rule__XOseeEnumType__Group_3__1__Impl : ( ( rule__XOseeEnumType__TypeGuidAssignment_3_1 ) ) ;
    public final void rule__XOseeEnumType__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4339:1: ( ( ( rule__XOseeEnumType__TypeGuidAssignment_3_1 ) ) )
            // InternalOseeDsl.g:4340:1: ( ( rule__XOseeEnumType__TypeGuidAssignment_3_1 ) )
            {
            // InternalOseeDsl.g:4340:1: ( ( rule__XOseeEnumType__TypeGuidAssignment_3_1 ) )
            // InternalOseeDsl.g:4341:1: ( rule__XOseeEnumType__TypeGuidAssignment_3_1 )
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidAssignment_3_1()); 
            // InternalOseeDsl.g:4342:1: ( rule__XOseeEnumType__TypeGuidAssignment_3_1 )
            // InternalOseeDsl.g:4342:2: rule__XOseeEnumType__TypeGuidAssignment_3_1
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumType__TypeGuidAssignment_3_1();

            state._fsp--;


            }

             after(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidAssignment_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__Group_3__1__Impl"


    // $ANTLR start "rule__XOseeEnumEntry__Group__0"
    // InternalOseeDsl.g:4356:1: rule__XOseeEnumEntry__Group__0 : rule__XOseeEnumEntry__Group__0__Impl rule__XOseeEnumEntry__Group__1 ;
    public final void rule__XOseeEnumEntry__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4360:1: ( rule__XOseeEnumEntry__Group__0__Impl rule__XOseeEnumEntry__Group__1 )
            // InternalOseeDsl.g:4361:2: rule__XOseeEnumEntry__Group__0__Impl rule__XOseeEnumEntry__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__XOseeEnumEntry__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group__0"


    // $ANTLR start "rule__XOseeEnumEntry__Group__0__Impl"
    // InternalOseeDsl.g:4368:1: rule__XOseeEnumEntry__Group__0__Impl : ( 'entry' ) ;
    public final void rule__XOseeEnumEntry__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4372:1: ( ( 'entry' ) )
            // InternalOseeDsl.g:4373:1: ( 'entry' )
            {
            // InternalOseeDsl.g:4373:1: ( 'entry' )
            // InternalOseeDsl.g:4374:1: 'entry'
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0()); 
            match(input,72,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumEntryAccess().getEntryKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group__0__Impl"


    // $ANTLR start "rule__XOseeEnumEntry__Group__1"
    // InternalOseeDsl.g:4387:1: rule__XOseeEnumEntry__Group__1 : rule__XOseeEnumEntry__Group__1__Impl rule__XOseeEnumEntry__Group__2 ;
    public final void rule__XOseeEnumEntry__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4391:1: ( rule__XOseeEnumEntry__Group__1__Impl rule__XOseeEnumEntry__Group__2 )
            // InternalOseeDsl.g:4392:2: rule__XOseeEnumEntry__Group__1__Impl rule__XOseeEnumEntry__Group__2
            {
            pushFollow(FOLLOW_34);
            rule__XOseeEnumEntry__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group__1"


    // $ANTLR start "rule__XOseeEnumEntry__Group__1__Impl"
    // InternalOseeDsl.g:4399:1: rule__XOseeEnumEntry__Group__1__Impl : ( ( rule__XOseeEnumEntry__NameAssignment_1 ) ) ;
    public final void rule__XOseeEnumEntry__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4403:1: ( ( ( rule__XOseeEnumEntry__NameAssignment_1 ) ) )
            // InternalOseeDsl.g:4404:1: ( ( rule__XOseeEnumEntry__NameAssignment_1 ) )
            {
            // InternalOseeDsl.g:4404:1: ( ( rule__XOseeEnumEntry__NameAssignment_1 ) )
            // InternalOseeDsl.g:4405:1: ( rule__XOseeEnumEntry__NameAssignment_1 )
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getNameAssignment_1()); 
            // InternalOseeDsl.g:4406:1: ( rule__XOseeEnumEntry__NameAssignment_1 )
            // InternalOseeDsl.g:4406:2: rule__XOseeEnumEntry__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getXOseeEnumEntryAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group__1__Impl"


    // $ANTLR start "rule__XOseeEnumEntry__Group__2"
    // InternalOseeDsl.g:4416:1: rule__XOseeEnumEntry__Group__2 : rule__XOseeEnumEntry__Group__2__Impl rule__XOseeEnumEntry__Group__3 ;
    public final void rule__XOseeEnumEntry__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4420:1: ( rule__XOseeEnumEntry__Group__2__Impl rule__XOseeEnumEntry__Group__3 )
            // InternalOseeDsl.g:4421:2: rule__XOseeEnumEntry__Group__2__Impl rule__XOseeEnumEntry__Group__3
            {
            pushFollow(FOLLOW_34);
            rule__XOseeEnumEntry__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group__2"


    // $ANTLR start "rule__XOseeEnumEntry__Group__2__Impl"
    // InternalOseeDsl.g:4428:1: rule__XOseeEnumEntry__Group__2__Impl : ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? ) ;
    public final void rule__XOseeEnumEntry__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4432:1: ( ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? ) )
            // InternalOseeDsl.g:4433:1: ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? )
            {
            // InternalOseeDsl.g:4433:1: ( ( rule__XOseeEnumEntry__OrdinalAssignment_2 )? )
            // InternalOseeDsl.g:4434:1: ( rule__XOseeEnumEntry__OrdinalAssignment_2 )?
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getOrdinalAssignment_2()); 
            // InternalOseeDsl.g:4435:1: ( rule__XOseeEnumEntry__OrdinalAssignment_2 )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==RULE_WHOLE_NUM_STR) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // InternalOseeDsl.g:4435:2: rule__XOseeEnumEntry__OrdinalAssignment_2
                    {
                    pushFollow(FOLLOW_2);
                    rule__XOseeEnumEntry__OrdinalAssignment_2();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXOseeEnumEntryAccess().getOrdinalAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group__2__Impl"


    // $ANTLR start "rule__XOseeEnumEntry__Group__3"
    // InternalOseeDsl.g:4445:1: rule__XOseeEnumEntry__Group__3 : rule__XOseeEnumEntry__Group__3__Impl rule__XOseeEnumEntry__Group__4 ;
    public final void rule__XOseeEnumEntry__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4449:1: ( rule__XOseeEnumEntry__Group__3__Impl rule__XOseeEnumEntry__Group__4 )
            // InternalOseeDsl.g:4450:2: rule__XOseeEnumEntry__Group__3__Impl rule__XOseeEnumEntry__Group__4
            {
            pushFollow(FOLLOW_34);
            rule__XOseeEnumEntry__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group__3"


    // $ANTLR start "rule__XOseeEnumEntry__Group__3__Impl"
    // InternalOseeDsl.g:4457:1: rule__XOseeEnumEntry__Group__3__Impl : ( ( rule__XOseeEnumEntry__Group_3__0 )? ) ;
    public final void rule__XOseeEnumEntry__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4461:1: ( ( ( rule__XOseeEnumEntry__Group_3__0 )? ) )
            // InternalOseeDsl.g:4462:1: ( ( rule__XOseeEnumEntry__Group_3__0 )? )
            {
            // InternalOseeDsl.g:4462:1: ( ( rule__XOseeEnumEntry__Group_3__0 )? )
            // InternalOseeDsl.g:4463:1: ( rule__XOseeEnumEntry__Group_3__0 )?
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getGroup_3()); 
            // InternalOseeDsl.g:4464:1: ( rule__XOseeEnumEntry__Group_3__0 )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==73) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // InternalOseeDsl.g:4464:2: rule__XOseeEnumEntry__Group_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XOseeEnumEntry__Group_3__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXOseeEnumEntryAccess().getGroup_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group__3__Impl"


    // $ANTLR start "rule__XOseeEnumEntry__Group__4"
    // InternalOseeDsl.g:4474:1: rule__XOseeEnumEntry__Group__4 : rule__XOseeEnumEntry__Group__4__Impl ;
    public final void rule__XOseeEnumEntry__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4478:1: ( rule__XOseeEnumEntry__Group__4__Impl )
            // InternalOseeDsl.g:4479:2: rule__XOseeEnumEntry__Group__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__Group__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group__4"


    // $ANTLR start "rule__XOseeEnumEntry__Group__4__Impl"
    // InternalOseeDsl.g:4485:1: rule__XOseeEnumEntry__Group__4__Impl : ( ( rule__XOseeEnumEntry__Group_4__0 )? ) ;
    public final void rule__XOseeEnumEntry__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4489:1: ( ( ( rule__XOseeEnumEntry__Group_4__0 )? ) )
            // InternalOseeDsl.g:4490:1: ( ( rule__XOseeEnumEntry__Group_4__0 )? )
            {
            // InternalOseeDsl.g:4490:1: ( ( rule__XOseeEnumEntry__Group_4__0 )? )
            // InternalOseeDsl.g:4491:1: ( rule__XOseeEnumEntry__Group_4__0 )?
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getGroup_4()); 
            // InternalOseeDsl.g:4492:1: ( rule__XOseeEnumEntry__Group_4__0 )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==67) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // InternalOseeDsl.g:4492:2: rule__XOseeEnumEntry__Group_4__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XOseeEnumEntry__Group_4__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXOseeEnumEntryAccess().getGroup_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group__4__Impl"


    // $ANTLR start "rule__XOseeEnumEntry__Group_3__0"
    // InternalOseeDsl.g:4512:1: rule__XOseeEnumEntry__Group_3__0 : rule__XOseeEnumEntry__Group_3__0__Impl rule__XOseeEnumEntry__Group_3__1 ;
    public final void rule__XOseeEnumEntry__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4516:1: ( rule__XOseeEnumEntry__Group_3__0__Impl rule__XOseeEnumEntry__Group_3__1 )
            // InternalOseeDsl.g:4517:2: rule__XOseeEnumEntry__Group_3__0__Impl rule__XOseeEnumEntry__Group_3__1
            {
            pushFollow(FOLLOW_7);
            rule__XOseeEnumEntry__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group_3__0"


    // $ANTLR start "rule__XOseeEnumEntry__Group_3__0__Impl"
    // InternalOseeDsl.g:4524:1: rule__XOseeEnumEntry__Group_3__0__Impl : ( 'entryGuid' ) ;
    public final void rule__XOseeEnumEntry__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4528:1: ( ( 'entryGuid' ) )
            // InternalOseeDsl.g:4529:1: ( 'entryGuid' )
            {
            // InternalOseeDsl.g:4529:1: ( 'entryGuid' )
            // InternalOseeDsl.g:4530:1: 'entryGuid'
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0()); 
            match(input,73,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group_3__0__Impl"


    // $ANTLR start "rule__XOseeEnumEntry__Group_3__1"
    // InternalOseeDsl.g:4543:1: rule__XOseeEnumEntry__Group_3__1 : rule__XOseeEnumEntry__Group_3__1__Impl ;
    public final void rule__XOseeEnumEntry__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4547:1: ( rule__XOseeEnumEntry__Group_3__1__Impl )
            // InternalOseeDsl.g:4548:2: rule__XOseeEnumEntry__Group_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__Group_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group_3__1"


    // $ANTLR start "rule__XOseeEnumEntry__Group_3__1__Impl"
    // InternalOseeDsl.g:4554:1: rule__XOseeEnumEntry__Group_3__1__Impl : ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) ) ;
    public final void rule__XOseeEnumEntry__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4558:1: ( ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) ) )
            // InternalOseeDsl.g:4559:1: ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) )
            {
            // InternalOseeDsl.g:4559:1: ( ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 ) )
            // InternalOseeDsl.g:4560:1: ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 )
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidAssignment_3_1()); 
            // InternalOseeDsl.g:4561:1: ( rule__XOseeEnumEntry__EntryGuidAssignment_3_1 )
            // InternalOseeDsl.g:4561:2: rule__XOseeEnumEntry__EntryGuidAssignment_3_1
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__EntryGuidAssignment_3_1();

            state._fsp--;


            }

             after(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidAssignment_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group_3__1__Impl"


    // $ANTLR start "rule__XOseeEnumEntry__Group_4__0"
    // InternalOseeDsl.g:4575:1: rule__XOseeEnumEntry__Group_4__0 : rule__XOseeEnumEntry__Group_4__0__Impl rule__XOseeEnumEntry__Group_4__1 ;
    public final void rule__XOseeEnumEntry__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4579:1: ( rule__XOseeEnumEntry__Group_4__0__Impl rule__XOseeEnumEntry__Group_4__1 )
            // InternalOseeDsl.g:4580:2: rule__XOseeEnumEntry__Group_4__0__Impl rule__XOseeEnumEntry__Group_4__1
            {
            pushFollow(FOLLOW_7);
            rule__XOseeEnumEntry__Group_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__Group_4__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group_4__0"


    // $ANTLR start "rule__XOseeEnumEntry__Group_4__0__Impl"
    // InternalOseeDsl.g:4587:1: rule__XOseeEnumEntry__Group_4__0__Impl : ( 'description' ) ;
    public final void rule__XOseeEnumEntry__Group_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4591:1: ( ( 'description' ) )
            // InternalOseeDsl.g:4592:1: ( 'description' )
            {
            // InternalOseeDsl.g:4592:1: ( 'description' )
            // InternalOseeDsl.g:4593:1: 'description'
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getDescriptionKeyword_4_0()); 
            match(input,67,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumEntryAccess().getDescriptionKeyword_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group_4__0__Impl"


    // $ANTLR start "rule__XOseeEnumEntry__Group_4__1"
    // InternalOseeDsl.g:4606:1: rule__XOseeEnumEntry__Group_4__1 : rule__XOseeEnumEntry__Group_4__1__Impl ;
    public final void rule__XOseeEnumEntry__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4610:1: ( rule__XOseeEnumEntry__Group_4__1__Impl )
            // InternalOseeDsl.g:4611:2: rule__XOseeEnumEntry__Group_4__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__Group_4__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group_4__1"


    // $ANTLR start "rule__XOseeEnumEntry__Group_4__1__Impl"
    // InternalOseeDsl.g:4617:1: rule__XOseeEnumEntry__Group_4__1__Impl : ( ( rule__XOseeEnumEntry__DescriptionAssignment_4_1 ) ) ;
    public final void rule__XOseeEnumEntry__Group_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4621:1: ( ( ( rule__XOseeEnumEntry__DescriptionAssignment_4_1 ) ) )
            // InternalOseeDsl.g:4622:1: ( ( rule__XOseeEnumEntry__DescriptionAssignment_4_1 ) )
            {
            // InternalOseeDsl.g:4622:1: ( ( rule__XOseeEnumEntry__DescriptionAssignment_4_1 ) )
            // InternalOseeDsl.g:4623:1: ( rule__XOseeEnumEntry__DescriptionAssignment_4_1 )
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getDescriptionAssignment_4_1()); 
            // InternalOseeDsl.g:4624:1: ( rule__XOseeEnumEntry__DescriptionAssignment_4_1 )
            // InternalOseeDsl.g:4624:2: rule__XOseeEnumEntry__DescriptionAssignment_4_1
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumEntry__DescriptionAssignment_4_1();

            state._fsp--;


            }

             after(grammarAccess.getXOseeEnumEntryAccess().getDescriptionAssignment_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__Group_4__1__Impl"


    // $ANTLR start "rule__XOseeEnumOverride__Group__0"
    // InternalOseeDsl.g:4638:1: rule__XOseeEnumOverride__Group__0 : rule__XOseeEnumOverride__Group__0__Impl rule__XOseeEnumOverride__Group__1 ;
    public final void rule__XOseeEnumOverride__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4642:1: ( rule__XOseeEnumOverride__Group__0__Impl rule__XOseeEnumOverride__Group__1 )
            // InternalOseeDsl.g:4643:2: rule__XOseeEnumOverride__Group__0__Impl rule__XOseeEnumOverride__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__XOseeEnumOverride__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumOverride__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__0"


    // $ANTLR start "rule__XOseeEnumOverride__Group__0__Impl"
    // InternalOseeDsl.g:4650:1: rule__XOseeEnumOverride__Group__0__Impl : ( 'overrides enum' ) ;
    public final void rule__XOseeEnumOverride__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4654:1: ( ( 'overrides enum' ) )
            // InternalOseeDsl.g:4655:1: ( 'overrides enum' )
            {
            // InternalOseeDsl.g:4655:1: ( 'overrides enum' )
            // InternalOseeDsl.g:4656:1: 'overrides enum'
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0()); 
            match(input,74,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumOverrideAccess().getOverridesEnumKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__0__Impl"


    // $ANTLR start "rule__XOseeEnumOverride__Group__1"
    // InternalOseeDsl.g:4669:1: rule__XOseeEnumOverride__Group__1 : rule__XOseeEnumOverride__Group__1__Impl rule__XOseeEnumOverride__Group__2 ;
    public final void rule__XOseeEnumOverride__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4673:1: ( rule__XOseeEnumOverride__Group__1__Impl rule__XOseeEnumOverride__Group__2 )
            // InternalOseeDsl.g:4674:2: rule__XOseeEnumOverride__Group__1__Impl rule__XOseeEnumOverride__Group__2
            {
            pushFollow(FOLLOW_31);
            rule__XOseeEnumOverride__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumOverride__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__1"


    // $ANTLR start "rule__XOseeEnumOverride__Group__1__Impl"
    // InternalOseeDsl.g:4681:1: rule__XOseeEnumOverride__Group__1__Impl : ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) ) ;
    public final void rule__XOseeEnumOverride__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4685:1: ( ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) ) )
            // InternalOseeDsl.g:4686:1: ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) )
            {
            // InternalOseeDsl.g:4686:1: ( ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 ) )
            // InternalOseeDsl.g:4687:1: ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 )
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeAssignment_1()); 
            // InternalOseeDsl.g:4688:1: ( rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 )
            // InternalOseeDsl.g:4688:2: rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__1__Impl"


    // $ANTLR start "rule__XOseeEnumOverride__Group__2"
    // InternalOseeDsl.g:4698:1: rule__XOseeEnumOverride__Group__2 : rule__XOseeEnumOverride__Group__2__Impl rule__XOseeEnumOverride__Group__3 ;
    public final void rule__XOseeEnumOverride__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4702:1: ( rule__XOseeEnumOverride__Group__2__Impl rule__XOseeEnumOverride__Group__3 )
            // InternalOseeDsl.g:4703:2: rule__XOseeEnumOverride__Group__2__Impl rule__XOseeEnumOverride__Group__3
            {
            pushFollow(FOLLOW_35);
            rule__XOseeEnumOverride__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumOverride__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__2"


    // $ANTLR start "rule__XOseeEnumOverride__Group__2__Impl"
    // InternalOseeDsl.g:4710:1: rule__XOseeEnumOverride__Group__2__Impl : ( '{' ) ;
    public final void rule__XOseeEnumOverride__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4714:1: ( ( '{' ) )
            // InternalOseeDsl.g:4715:1: ( '{' )
            {
            // InternalOseeDsl.g:4715:1: ( '{' )
            // InternalOseeDsl.g:4716:1: '{'
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,53,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumOverrideAccess().getLeftCurlyBracketKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__2__Impl"


    // $ANTLR start "rule__XOseeEnumOverride__Group__3"
    // InternalOseeDsl.g:4729:1: rule__XOseeEnumOverride__Group__3 : rule__XOseeEnumOverride__Group__3__Impl rule__XOseeEnumOverride__Group__4 ;
    public final void rule__XOseeEnumOverride__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4733:1: ( rule__XOseeEnumOverride__Group__3__Impl rule__XOseeEnumOverride__Group__4 )
            // InternalOseeDsl.g:4734:2: rule__XOseeEnumOverride__Group__3__Impl rule__XOseeEnumOverride__Group__4
            {
            pushFollow(FOLLOW_35);
            rule__XOseeEnumOverride__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumOverride__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__3"


    // $ANTLR start "rule__XOseeEnumOverride__Group__3__Impl"
    // InternalOseeDsl.g:4741:1: rule__XOseeEnumOverride__Group__3__Impl : ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? ) ;
    public final void rule__XOseeEnumOverride__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4745:1: ( ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? ) )
            // InternalOseeDsl.g:4746:1: ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? )
            {
            // InternalOseeDsl.g:4746:1: ( ( rule__XOseeEnumOverride__InheritAllAssignment_3 )? )
            // InternalOseeDsl.g:4747:1: ( rule__XOseeEnumOverride__InheritAllAssignment_3 )?
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllAssignment_3()); 
            // InternalOseeDsl.g:4748:1: ( rule__XOseeEnumOverride__InheritAllAssignment_3 )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==98) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // InternalOseeDsl.g:4748:2: rule__XOseeEnumOverride__InheritAllAssignment_3
                    {
                    pushFollow(FOLLOW_2);
                    rule__XOseeEnumOverride__InheritAllAssignment_3();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllAssignment_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__3__Impl"


    // $ANTLR start "rule__XOseeEnumOverride__Group__4"
    // InternalOseeDsl.g:4758:1: rule__XOseeEnumOverride__Group__4 : rule__XOseeEnumOverride__Group__4__Impl rule__XOseeEnumOverride__Group__5 ;
    public final void rule__XOseeEnumOverride__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4762:1: ( rule__XOseeEnumOverride__Group__4__Impl rule__XOseeEnumOverride__Group__5 )
            // InternalOseeDsl.g:4763:2: rule__XOseeEnumOverride__Group__4__Impl rule__XOseeEnumOverride__Group__5
            {
            pushFollow(FOLLOW_35);
            rule__XOseeEnumOverride__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeEnumOverride__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__4"


    // $ANTLR start "rule__XOseeEnumOverride__Group__4__Impl"
    // InternalOseeDsl.g:4770:1: rule__XOseeEnumOverride__Group__4__Impl : ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* ) ;
    public final void rule__XOseeEnumOverride__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4774:1: ( ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* ) )
            // InternalOseeDsl.g:4775:1: ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* )
            {
            // InternalOseeDsl.g:4775:1: ( ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )* )
            // InternalOseeDsl.g:4776:1: ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )*
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsAssignment_4()); 
            // InternalOseeDsl.g:4777:1: ( rule__XOseeEnumOverride__OverrideOptionsAssignment_4 )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( ((LA41_0>=75 && LA41_0<=76)) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // InternalOseeDsl.g:4777:2: rule__XOseeEnumOverride__OverrideOptionsAssignment_4
            	    {
            	    pushFollow(FOLLOW_36);
            	    rule__XOseeEnumOverride__OverrideOptionsAssignment_4();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop41;
                }
            } while (true);

             after(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsAssignment_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__4__Impl"


    // $ANTLR start "rule__XOseeEnumOverride__Group__5"
    // InternalOseeDsl.g:4787:1: rule__XOseeEnumOverride__Group__5 : rule__XOseeEnumOverride__Group__5__Impl ;
    public final void rule__XOseeEnumOverride__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4791:1: ( rule__XOseeEnumOverride__Group__5__Impl )
            // InternalOseeDsl.g:4792:2: rule__XOseeEnumOverride__Group__5__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XOseeEnumOverride__Group__5__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__5"


    // $ANTLR start "rule__XOseeEnumOverride__Group__5__Impl"
    // InternalOseeDsl.g:4798:1: rule__XOseeEnumOverride__Group__5__Impl : ( '}' ) ;
    public final void rule__XOseeEnumOverride__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4802:1: ( ( '}' ) )
            // InternalOseeDsl.g:4803:1: ( '}' )
            {
            // InternalOseeDsl.g:4803:1: ( '}' )
            // InternalOseeDsl.g:4804:1: '}'
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5()); 
            match(input,55,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumOverrideAccess().getRightCurlyBracketKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__Group__5__Impl"


    // $ANTLR start "rule__AddEnum__Group__0"
    // InternalOseeDsl.g:4829:1: rule__AddEnum__Group__0 : rule__AddEnum__Group__0__Impl rule__AddEnum__Group__1 ;
    public final void rule__AddEnum__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4833:1: ( rule__AddEnum__Group__0__Impl rule__AddEnum__Group__1 )
            // InternalOseeDsl.g:4834:2: rule__AddEnum__Group__0__Impl rule__AddEnum__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__AddEnum__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AddEnum__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group__0"


    // $ANTLR start "rule__AddEnum__Group__0__Impl"
    // InternalOseeDsl.g:4841:1: rule__AddEnum__Group__0__Impl : ( 'add' ) ;
    public final void rule__AddEnum__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4845:1: ( ( 'add' ) )
            // InternalOseeDsl.g:4846:1: ( 'add' )
            {
            // InternalOseeDsl.g:4846:1: ( 'add' )
            // InternalOseeDsl.g:4847:1: 'add'
            {
             before(grammarAccess.getAddEnumAccess().getAddKeyword_0()); 
            match(input,75,FOLLOW_2); 
             after(grammarAccess.getAddEnumAccess().getAddKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group__0__Impl"


    // $ANTLR start "rule__AddEnum__Group__1"
    // InternalOseeDsl.g:4860:1: rule__AddEnum__Group__1 : rule__AddEnum__Group__1__Impl rule__AddEnum__Group__2 ;
    public final void rule__AddEnum__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4864:1: ( rule__AddEnum__Group__1__Impl rule__AddEnum__Group__2 )
            // InternalOseeDsl.g:4865:2: rule__AddEnum__Group__1__Impl rule__AddEnum__Group__2
            {
            pushFollow(FOLLOW_34);
            rule__AddEnum__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AddEnum__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group__1"


    // $ANTLR start "rule__AddEnum__Group__1__Impl"
    // InternalOseeDsl.g:4872:1: rule__AddEnum__Group__1__Impl : ( ( rule__AddEnum__EnumEntryAssignment_1 ) ) ;
    public final void rule__AddEnum__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4876:1: ( ( ( rule__AddEnum__EnumEntryAssignment_1 ) ) )
            // InternalOseeDsl.g:4877:1: ( ( rule__AddEnum__EnumEntryAssignment_1 ) )
            {
            // InternalOseeDsl.g:4877:1: ( ( rule__AddEnum__EnumEntryAssignment_1 ) )
            // InternalOseeDsl.g:4878:1: ( rule__AddEnum__EnumEntryAssignment_1 )
            {
             before(grammarAccess.getAddEnumAccess().getEnumEntryAssignment_1()); 
            // InternalOseeDsl.g:4879:1: ( rule__AddEnum__EnumEntryAssignment_1 )
            // InternalOseeDsl.g:4879:2: rule__AddEnum__EnumEntryAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__AddEnum__EnumEntryAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getAddEnumAccess().getEnumEntryAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group__1__Impl"


    // $ANTLR start "rule__AddEnum__Group__2"
    // InternalOseeDsl.g:4889:1: rule__AddEnum__Group__2 : rule__AddEnum__Group__2__Impl rule__AddEnum__Group__3 ;
    public final void rule__AddEnum__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4893:1: ( rule__AddEnum__Group__2__Impl rule__AddEnum__Group__3 )
            // InternalOseeDsl.g:4894:2: rule__AddEnum__Group__2__Impl rule__AddEnum__Group__3
            {
            pushFollow(FOLLOW_34);
            rule__AddEnum__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AddEnum__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group__2"


    // $ANTLR start "rule__AddEnum__Group__2__Impl"
    // InternalOseeDsl.g:4901:1: rule__AddEnum__Group__2__Impl : ( ( rule__AddEnum__OrdinalAssignment_2 )? ) ;
    public final void rule__AddEnum__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4905:1: ( ( ( rule__AddEnum__OrdinalAssignment_2 )? ) )
            // InternalOseeDsl.g:4906:1: ( ( rule__AddEnum__OrdinalAssignment_2 )? )
            {
            // InternalOseeDsl.g:4906:1: ( ( rule__AddEnum__OrdinalAssignment_2 )? )
            // InternalOseeDsl.g:4907:1: ( rule__AddEnum__OrdinalAssignment_2 )?
            {
             before(grammarAccess.getAddEnumAccess().getOrdinalAssignment_2()); 
            // InternalOseeDsl.g:4908:1: ( rule__AddEnum__OrdinalAssignment_2 )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==RULE_WHOLE_NUM_STR) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // InternalOseeDsl.g:4908:2: rule__AddEnum__OrdinalAssignment_2
                    {
                    pushFollow(FOLLOW_2);
                    rule__AddEnum__OrdinalAssignment_2();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAddEnumAccess().getOrdinalAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group__2__Impl"


    // $ANTLR start "rule__AddEnum__Group__3"
    // InternalOseeDsl.g:4918:1: rule__AddEnum__Group__3 : rule__AddEnum__Group__3__Impl rule__AddEnum__Group__4 ;
    public final void rule__AddEnum__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4922:1: ( rule__AddEnum__Group__3__Impl rule__AddEnum__Group__4 )
            // InternalOseeDsl.g:4923:2: rule__AddEnum__Group__3__Impl rule__AddEnum__Group__4
            {
            pushFollow(FOLLOW_34);
            rule__AddEnum__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AddEnum__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group__3"


    // $ANTLR start "rule__AddEnum__Group__3__Impl"
    // InternalOseeDsl.g:4930:1: rule__AddEnum__Group__3__Impl : ( ( rule__AddEnum__Group_3__0 )? ) ;
    public final void rule__AddEnum__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4934:1: ( ( ( rule__AddEnum__Group_3__0 )? ) )
            // InternalOseeDsl.g:4935:1: ( ( rule__AddEnum__Group_3__0 )? )
            {
            // InternalOseeDsl.g:4935:1: ( ( rule__AddEnum__Group_3__0 )? )
            // InternalOseeDsl.g:4936:1: ( rule__AddEnum__Group_3__0 )?
            {
             before(grammarAccess.getAddEnumAccess().getGroup_3()); 
            // InternalOseeDsl.g:4937:1: ( rule__AddEnum__Group_3__0 )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==73) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // InternalOseeDsl.g:4937:2: rule__AddEnum__Group_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__AddEnum__Group_3__0();

                    state._fsp--;


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
    // $ANTLR end "rule__AddEnum__Group__3__Impl"


    // $ANTLR start "rule__AddEnum__Group__4"
    // InternalOseeDsl.g:4947:1: rule__AddEnum__Group__4 : rule__AddEnum__Group__4__Impl ;
    public final void rule__AddEnum__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4951:1: ( rule__AddEnum__Group__4__Impl )
            // InternalOseeDsl.g:4952:2: rule__AddEnum__Group__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AddEnum__Group__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group__4"


    // $ANTLR start "rule__AddEnum__Group__4__Impl"
    // InternalOseeDsl.g:4958:1: rule__AddEnum__Group__4__Impl : ( ( rule__AddEnum__Group_4__0 )? ) ;
    public final void rule__AddEnum__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4962:1: ( ( ( rule__AddEnum__Group_4__0 )? ) )
            // InternalOseeDsl.g:4963:1: ( ( rule__AddEnum__Group_4__0 )? )
            {
            // InternalOseeDsl.g:4963:1: ( ( rule__AddEnum__Group_4__0 )? )
            // InternalOseeDsl.g:4964:1: ( rule__AddEnum__Group_4__0 )?
            {
             before(grammarAccess.getAddEnumAccess().getGroup_4()); 
            // InternalOseeDsl.g:4965:1: ( rule__AddEnum__Group_4__0 )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==67) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // InternalOseeDsl.g:4965:2: rule__AddEnum__Group_4__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__AddEnum__Group_4__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAddEnumAccess().getGroup_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group__4__Impl"


    // $ANTLR start "rule__AddEnum__Group_3__0"
    // InternalOseeDsl.g:4985:1: rule__AddEnum__Group_3__0 : rule__AddEnum__Group_3__0__Impl rule__AddEnum__Group_3__1 ;
    public final void rule__AddEnum__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:4989:1: ( rule__AddEnum__Group_3__0__Impl rule__AddEnum__Group_3__1 )
            // InternalOseeDsl.g:4990:2: rule__AddEnum__Group_3__0__Impl rule__AddEnum__Group_3__1
            {
            pushFollow(FOLLOW_7);
            rule__AddEnum__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AddEnum__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group_3__0"


    // $ANTLR start "rule__AddEnum__Group_3__0__Impl"
    // InternalOseeDsl.g:4997:1: rule__AddEnum__Group_3__0__Impl : ( 'entryGuid' ) ;
    public final void rule__AddEnum__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5001:1: ( ( 'entryGuid' ) )
            // InternalOseeDsl.g:5002:1: ( 'entryGuid' )
            {
            // InternalOseeDsl.g:5002:1: ( 'entryGuid' )
            // InternalOseeDsl.g:5003:1: 'entryGuid'
            {
             before(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0()); 
            match(input,73,FOLLOW_2); 
             after(grammarAccess.getAddEnumAccess().getEntryGuidKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group_3__0__Impl"


    // $ANTLR start "rule__AddEnum__Group_3__1"
    // InternalOseeDsl.g:5016:1: rule__AddEnum__Group_3__1 : rule__AddEnum__Group_3__1__Impl ;
    public final void rule__AddEnum__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5020:1: ( rule__AddEnum__Group_3__1__Impl )
            // InternalOseeDsl.g:5021:2: rule__AddEnum__Group_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AddEnum__Group_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group_3__1"


    // $ANTLR start "rule__AddEnum__Group_3__1__Impl"
    // InternalOseeDsl.g:5027:1: rule__AddEnum__Group_3__1__Impl : ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) ) ;
    public final void rule__AddEnum__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5031:1: ( ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) ) )
            // InternalOseeDsl.g:5032:1: ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) )
            {
            // InternalOseeDsl.g:5032:1: ( ( rule__AddEnum__EntryGuidAssignment_3_1 ) )
            // InternalOseeDsl.g:5033:1: ( rule__AddEnum__EntryGuidAssignment_3_1 )
            {
             before(grammarAccess.getAddEnumAccess().getEntryGuidAssignment_3_1()); 
            // InternalOseeDsl.g:5034:1: ( rule__AddEnum__EntryGuidAssignment_3_1 )
            // InternalOseeDsl.g:5034:2: rule__AddEnum__EntryGuidAssignment_3_1
            {
            pushFollow(FOLLOW_2);
            rule__AddEnum__EntryGuidAssignment_3_1();

            state._fsp--;


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
    // $ANTLR end "rule__AddEnum__Group_3__1__Impl"


    // $ANTLR start "rule__AddEnum__Group_4__0"
    // InternalOseeDsl.g:5048:1: rule__AddEnum__Group_4__0 : rule__AddEnum__Group_4__0__Impl rule__AddEnum__Group_4__1 ;
    public final void rule__AddEnum__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5052:1: ( rule__AddEnum__Group_4__0__Impl rule__AddEnum__Group_4__1 )
            // InternalOseeDsl.g:5053:2: rule__AddEnum__Group_4__0__Impl rule__AddEnum__Group_4__1
            {
            pushFollow(FOLLOW_7);
            rule__AddEnum__Group_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AddEnum__Group_4__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group_4__0"


    // $ANTLR start "rule__AddEnum__Group_4__0__Impl"
    // InternalOseeDsl.g:5060:1: rule__AddEnum__Group_4__0__Impl : ( 'description' ) ;
    public final void rule__AddEnum__Group_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5064:1: ( ( 'description' ) )
            // InternalOseeDsl.g:5065:1: ( 'description' )
            {
            // InternalOseeDsl.g:5065:1: ( 'description' )
            // InternalOseeDsl.g:5066:1: 'description'
            {
             before(grammarAccess.getAddEnumAccess().getDescriptionKeyword_4_0()); 
            match(input,67,FOLLOW_2); 
             after(grammarAccess.getAddEnumAccess().getDescriptionKeyword_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group_4__0__Impl"


    // $ANTLR start "rule__AddEnum__Group_4__1"
    // InternalOseeDsl.g:5079:1: rule__AddEnum__Group_4__1 : rule__AddEnum__Group_4__1__Impl ;
    public final void rule__AddEnum__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5083:1: ( rule__AddEnum__Group_4__1__Impl )
            // InternalOseeDsl.g:5084:2: rule__AddEnum__Group_4__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AddEnum__Group_4__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group_4__1"


    // $ANTLR start "rule__AddEnum__Group_4__1__Impl"
    // InternalOseeDsl.g:5090:1: rule__AddEnum__Group_4__1__Impl : ( ( rule__AddEnum__DescriptionAssignment_4_1 ) ) ;
    public final void rule__AddEnum__Group_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5094:1: ( ( ( rule__AddEnum__DescriptionAssignment_4_1 ) ) )
            // InternalOseeDsl.g:5095:1: ( ( rule__AddEnum__DescriptionAssignment_4_1 ) )
            {
            // InternalOseeDsl.g:5095:1: ( ( rule__AddEnum__DescriptionAssignment_4_1 ) )
            // InternalOseeDsl.g:5096:1: ( rule__AddEnum__DescriptionAssignment_4_1 )
            {
             before(grammarAccess.getAddEnumAccess().getDescriptionAssignment_4_1()); 
            // InternalOseeDsl.g:5097:1: ( rule__AddEnum__DescriptionAssignment_4_1 )
            // InternalOseeDsl.g:5097:2: rule__AddEnum__DescriptionAssignment_4_1
            {
            pushFollow(FOLLOW_2);
            rule__AddEnum__DescriptionAssignment_4_1();

            state._fsp--;


            }

             after(grammarAccess.getAddEnumAccess().getDescriptionAssignment_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__Group_4__1__Impl"


    // $ANTLR start "rule__RemoveEnum__Group__0"
    // InternalOseeDsl.g:5111:1: rule__RemoveEnum__Group__0 : rule__RemoveEnum__Group__0__Impl rule__RemoveEnum__Group__1 ;
    public final void rule__RemoveEnum__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5115:1: ( rule__RemoveEnum__Group__0__Impl rule__RemoveEnum__Group__1 )
            // InternalOseeDsl.g:5116:2: rule__RemoveEnum__Group__0__Impl rule__RemoveEnum__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__RemoveEnum__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RemoveEnum__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveEnum__Group__0"


    // $ANTLR start "rule__RemoveEnum__Group__0__Impl"
    // InternalOseeDsl.g:5123:1: rule__RemoveEnum__Group__0__Impl : ( 'remove' ) ;
    public final void rule__RemoveEnum__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5127:1: ( ( 'remove' ) )
            // InternalOseeDsl.g:5128:1: ( 'remove' )
            {
            // InternalOseeDsl.g:5128:1: ( 'remove' )
            // InternalOseeDsl.g:5129:1: 'remove'
            {
             before(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0()); 
            match(input,76,FOLLOW_2); 
             after(grammarAccess.getRemoveEnumAccess().getRemoveKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveEnum__Group__0__Impl"


    // $ANTLR start "rule__RemoveEnum__Group__1"
    // InternalOseeDsl.g:5142:1: rule__RemoveEnum__Group__1 : rule__RemoveEnum__Group__1__Impl ;
    public final void rule__RemoveEnum__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5146:1: ( rule__RemoveEnum__Group__1__Impl )
            // InternalOseeDsl.g:5147:2: rule__RemoveEnum__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__RemoveEnum__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveEnum__Group__1"


    // $ANTLR start "rule__RemoveEnum__Group__1__Impl"
    // InternalOseeDsl.g:5153:1: rule__RemoveEnum__Group__1__Impl : ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) ) ;
    public final void rule__RemoveEnum__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5157:1: ( ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) ) )
            // InternalOseeDsl.g:5158:1: ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) )
            {
            // InternalOseeDsl.g:5158:1: ( ( rule__RemoveEnum__EnumEntryAssignment_1 ) )
            // InternalOseeDsl.g:5159:1: ( rule__RemoveEnum__EnumEntryAssignment_1 )
            {
             before(grammarAccess.getRemoveEnumAccess().getEnumEntryAssignment_1()); 
            // InternalOseeDsl.g:5160:1: ( rule__RemoveEnum__EnumEntryAssignment_1 )
            // InternalOseeDsl.g:5160:2: rule__RemoveEnum__EnumEntryAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__RemoveEnum__EnumEntryAssignment_1();

            state._fsp--;


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
    // $ANTLR end "rule__RemoveEnum__Group__1__Impl"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__0"
    // InternalOseeDsl.g:5174:1: rule__XOseeArtifactTypeOverride__Group__0 : rule__XOseeArtifactTypeOverride__Group__0__Impl rule__XOseeArtifactTypeOverride__Group__1 ;
    public final void rule__XOseeArtifactTypeOverride__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5178:1: ( rule__XOseeArtifactTypeOverride__Group__0__Impl rule__XOseeArtifactTypeOverride__Group__1 )
            // InternalOseeDsl.g:5179:2: rule__XOseeArtifactTypeOverride__Group__0__Impl rule__XOseeArtifactTypeOverride__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__XOseeArtifactTypeOverride__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeArtifactTypeOverride__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__0"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__0__Impl"
    // InternalOseeDsl.g:5186:1: rule__XOseeArtifactTypeOverride__Group__0__Impl : ( 'overrides artifactType' ) ;
    public final void rule__XOseeArtifactTypeOverride__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5190:1: ( ( 'overrides artifactType' ) )
            // InternalOseeDsl.g:5191:1: ( 'overrides artifactType' )
            {
            // InternalOseeDsl.g:5191:1: ( 'overrides artifactType' )
            // InternalOseeDsl.g:5192:1: 'overrides artifactType'
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridesArtifactTypeKeyword_0()); 
            match(input,77,FOLLOW_2); 
             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridesArtifactTypeKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__0__Impl"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__1"
    // InternalOseeDsl.g:5205:1: rule__XOseeArtifactTypeOverride__Group__1 : rule__XOseeArtifactTypeOverride__Group__1__Impl rule__XOseeArtifactTypeOverride__Group__2 ;
    public final void rule__XOseeArtifactTypeOverride__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5209:1: ( rule__XOseeArtifactTypeOverride__Group__1__Impl rule__XOseeArtifactTypeOverride__Group__2 )
            // InternalOseeDsl.g:5210:2: rule__XOseeArtifactTypeOverride__Group__1__Impl rule__XOseeArtifactTypeOverride__Group__2
            {
            pushFollow(FOLLOW_31);
            rule__XOseeArtifactTypeOverride__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeArtifactTypeOverride__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__1"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__1__Impl"
    // InternalOseeDsl.g:5217:1: rule__XOseeArtifactTypeOverride__Group__1__Impl : ( ( rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1 ) ) ;
    public final void rule__XOseeArtifactTypeOverride__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5221:1: ( ( ( rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1 ) ) )
            // InternalOseeDsl.g:5222:1: ( ( rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1 ) )
            {
            // InternalOseeDsl.g:5222:1: ( ( rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1 ) )
            // InternalOseeDsl.g:5223:1: ( rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1 )
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridenArtifactTypeAssignment_1()); 
            // InternalOseeDsl.g:5224:1: ( rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1 )
            // InternalOseeDsl.g:5224:2: rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridenArtifactTypeAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__1__Impl"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__2"
    // InternalOseeDsl.g:5234:1: rule__XOseeArtifactTypeOverride__Group__2 : rule__XOseeArtifactTypeOverride__Group__2__Impl rule__XOseeArtifactTypeOverride__Group__3 ;
    public final void rule__XOseeArtifactTypeOverride__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5238:1: ( rule__XOseeArtifactTypeOverride__Group__2__Impl rule__XOseeArtifactTypeOverride__Group__3 )
            // InternalOseeDsl.g:5239:2: rule__XOseeArtifactTypeOverride__Group__2__Impl rule__XOseeArtifactTypeOverride__Group__3
            {
            pushFollow(FOLLOW_37);
            rule__XOseeArtifactTypeOverride__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeArtifactTypeOverride__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__2"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__2__Impl"
    // InternalOseeDsl.g:5246:1: rule__XOseeArtifactTypeOverride__Group__2__Impl : ( '{' ) ;
    public final void rule__XOseeArtifactTypeOverride__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5250:1: ( ( '{' ) )
            // InternalOseeDsl.g:5251:1: ( '{' )
            {
            // InternalOseeDsl.g:5251:1: ( '{' )
            // InternalOseeDsl.g:5252:1: '{'
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,53,FOLLOW_2); 
             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getLeftCurlyBracketKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__2__Impl"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__3"
    // InternalOseeDsl.g:5265:1: rule__XOseeArtifactTypeOverride__Group__3 : rule__XOseeArtifactTypeOverride__Group__3__Impl rule__XOseeArtifactTypeOverride__Group__4 ;
    public final void rule__XOseeArtifactTypeOverride__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5269:1: ( rule__XOseeArtifactTypeOverride__Group__3__Impl rule__XOseeArtifactTypeOverride__Group__4 )
            // InternalOseeDsl.g:5270:2: rule__XOseeArtifactTypeOverride__Group__3__Impl rule__XOseeArtifactTypeOverride__Group__4
            {
            pushFollow(FOLLOW_37);
            rule__XOseeArtifactTypeOverride__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeArtifactTypeOverride__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__3"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__3__Impl"
    // InternalOseeDsl.g:5277:1: rule__XOseeArtifactTypeOverride__Group__3__Impl : ( ( rule__XOseeArtifactTypeOverride__InheritAllAssignment_3 )? ) ;
    public final void rule__XOseeArtifactTypeOverride__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5281:1: ( ( ( rule__XOseeArtifactTypeOverride__InheritAllAssignment_3 )? ) )
            // InternalOseeDsl.g:5282:1: ( ( rule__XOseeArtifactTypeOverride__InheritAllAssignment_3 )? )
            {
            // InternalOseeDsl.g:5282:1: ( ( rule__XOseeArtifactTypeOverride__InheritAllAssignment_3 )? )
            // InternalOseeDsl.g:5283:1: ( rule__XOseeArtifactTypeOverride__InheritAllAssignment_3 )?
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getInheritAllAssignment_3()); 
            // InternalOseeDsl.g:5284:1: ( rule__XOseeArtifactTypeOverride__InheritAllAssignment_3 )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==98) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // InternalOseeDsl.g:5284:2: rule__XOseeArtifactTypeOverride__InheritAllAssignment_3
                    {
                    pushFollow(FOLLOW_2);
                    rule__XOseeArtifactTypeOverride__InheritAllAssignment_3();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getInheritAllAssignment_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__3__Impl"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__4"
    // InternalOseeDsl.g:5294:1: rule__XOseeArtifactTypeOverride__Group__4 : rule__XOseeArtifactTypeOverride__Group__4__Impl rule__XOseeArtifactTypeOverride__Group__5 ;
    public final void rule__XOseeArtifactTypeOverride__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5298:1: ( rule__XOseeArtifactTypeOverride__Group__4__Impl rule__XOseeArtifactTypeOverride__Group__5 )
            // InternalOseeDsl.g:5299:2: rule__XOseeArtifactTypeOverride__Group__4__Impl rule__XOseeArtifactTypeOverride__Group__5
            {
            pushFollow(FOLLOW_28);
            rule__XOseeArtifactTypeOverride__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XOseeArtifactTypeOverride__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__4"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__4__Impl"
    // InternalOseeDsl.g:5306:1: rule__XOseeArtifactTypeOverride__Group__4__Impl : ( ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 ) ) ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 )* ) ) ;
    public final void rule__XOseeArtifactTypeOverride__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5310:1: ( ( ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 ) ) ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 )* ) ) )
            // InternalOseeDsl.g:5311:1: ( ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 ) ) ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 )* ) )
            {
            // InternalOseeDsl.g:5311:1: ( ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 ) ) ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 )* ) )
            // InternalOseeDsl.g:5312:1: ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 ) ) ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 )* )
            {
            // InternalOseeDsl.g:5312:1: ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 ) )
            // InternalOseeDsl.g:5313:1: ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 )
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverrideOptionsAssignment_4()); 
            // InternalOseeDsl.g:5314:1: ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 )
            // InternalOseeDsl.g:5314:2: rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4
            {
            pushFollow(FOLLOW_38);
            rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4();

            state._fsp--;


            }

             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverrideOptionsAssignment_4()); 

            }

            // InternalOseeDsl.g:5317:1: ( ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 )* )
            // InternalOseeDsl.g:5318:1: ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 )*
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverrideOptionsAssignment_4()); 
            // InternalOseeDsl.g:5319:1: ( rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( ((LA46_0>=75 && LA46_0<=76)||LA46_0==78) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // InternalOseeDsl.g:5319:2: rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4
            	    {
            	    pushFollow(FOLLOW_38);
            	    rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop46;
                }
            } while (true);

             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverrideOptionsAssignment_4()); 

            }


            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__4__Impl"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__5"
    // InternalOseeDsl.g:5330:1: rule__XOseeArtifactTypeOverride__Group__5 : rule__XOseeArtifactTypeOverride__Group__5__Impl ;
    public final void rule__XOseeArtifactTypeOverride__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5334:1: ( rule__XOseeArtifactTypeOverride__Group__5__Impl )
            // InternalOseeDsl.g:5335:2: rule__XOseeArtifactTypeOverride__Group__5__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XOseeArtifactTypeOverride__Group__5__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__5"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__Group__5__Impl"
    // InternalOseeDsl.g:5341:1: rule__XOseeArtifactTypeOverride__Group__5__Impl : ( '}' ) ;
    public final void rule__XOseeArtifactTypeOverride__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5345:1: ( ( '}' ) )
            // InternalOseeDsl.g:5346:1: ( '}' )
            {
            // InternalOseeDsl.g:5346:1: ( '}' )
            // InternalOseeDsl.g:5347:1: '}'
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getRightCurlyBracketKeyword_5()); 
            match(input,55,FOLLOW_2); 
             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getRightCurlyBracketKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__Group__5__Impl"


    // $ANTLR start "rule__AddAttribute__Group__0"
    // InternalOseeDsl.g:5372:1: rule__AddAttribute__Group__0 : rule__AddAttribute__Group__0__Impl rule__AddAttribute__Group__1 ;
    public final void rule__AddAttribute__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5376:1: ( rule__AddAttribute__Group__0__Impl rule__AddAttribute__Group__1 )
            // InternalOseeDsl.g:5377:2: rule__AddAttribute__Group__0__Impl rule__AddAttribute__Group__1
            {
            pushFollow(FOLLOW_39);
            rule__AddAttribute__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AddAttribute__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddAttribute__Group__0"


    // $ANTLR start "rule__AddAttribute__Group__0__Impl"
    // InternalOseeDsl.g:5384:1: rule__AddAttribute__Group__0__Impl : ( 'add' ) ;
    public final void rule__AddAttribute__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5388:1: ( ( 'add' ) )
            // InternalOseeDsl.g:5389:1: ( 'add' )
            {
            // InternalOseeDsl.g:5389:1: ( 'add' )
            // InternalOseeDsl.g:5390:1: 'add'
            {
             before(grammarAccess.getAddAttributeAccess().getAddKeyword_0()); 
            match(input,75,FOLLOW_2); 
             after(grammarAccess.getAddAttributeAccess().getAddKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddAttribute__Group__0__Impl"


    // $ANTLR start "rule__AddAttribute__Group__1"
    // InternalOseeDsl.g:5403:1: rule__AddAttribute__Group__1 : rule__AddAttribute__Group__1__Impl ;
    public final void rule__AddAttribute__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5407:1: ( rule__AddAttribute__Group__1__Impl )
            // InternalOseeDsl.g:5408:2: rule__AddAttribute__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AddAttribute__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddAttribute__Group__1"


    // $ANTLR start "rule__AddAttribute__Group__1__Impl"
    // InternalOseeDsl.g:5414:1: rule__AddAttribute__Group__1__Impl : ( ( rule__AddAttribute__AttributeAssignment_1 ) ) ;
    public final void rule__AddAttribute__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5418:1: ( ( ( rule__AddAttribute__AttributeAssignment_1 ) ) )
            // InternalOseeDsl.g:5419:1: ( ( rule__AddAttribute__AttributeAssignment_1 ) )
            {
            // InternalOseeDsl.g:5419:1: ( ( rule__AddAttribute__AttributeAssignment_1 ) )
            // InternalOseeDsl.g:5420:1: ( rule__AddAttribute__AttributeAssignment_1 )
            {
             before(grammarAccess.getAddAttributeAccess().getAttributeAssignment_1()); 
            // InternalOseeDsl.g:5421:1: ( rule__AddAttribute__AttributeAssignment_1 )
            // InternalOseeDsl.g:5421:2: rule__AddAttribute__AttributeAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__AddAttribute__AttributeAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getAddAttributeAccess().getAttributeAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddAttribute__Group__1__Impl"


    // $ANTLR start "rule__RemoveAttribute__Group__0"
    // InternalOseeDsl.g:5435:1: rule__RemoveAttribute__Group__0 : rule__RemoveAttribute__Group__0__Impl rule__RemoveAttribute__Group__1 ;
    public final void rule__RemoveAttribute__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5439:1: ( rule__RemoveAttribute__Group__0__Impl rule__RemoveAttribute__Group__1 )
            // InternalOseeDsl.g:5440:2: rule__RemoveAttribute__Group__0__Impl rule__RemoveAttribute__Group__1
            {
            pushFollow(FOLLOW_39);
            rule__RemoveAttribute__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RemoveAttribute__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveAttribute__Group__0"


    // $ANTLR start "rule__RemoveAttribute__Group__0__Impl"
    // InternalOseeDsl.g:5447:1: rule__RemoveAttribute__Group__0__Impl : ( 'remove' ) ;
    public final void rule__RemoveAttribute__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5451:1: ( ( 'remove' ) )
            // InternalOseeDsl.g:5452:1: ( 'remove' )
            {
            // InternalOseeDsl.g:5452:1: ( 'remove' )
            // InternalOseeDsl.g:5453:1: 'remove'
            {
             before(grammarAccess.getRemoveAttributeAccess().getRemoveKeyword_0()); 
            match(input,76,FOLLOW_2); 
             after(grammarAccess.getRemoveAttributeAccess().getRemoveKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveAttribute__Group__0__Impl"


    // $ANTLR start "rule__RemoveAttribute__Group__1"
    // InternalOseeDsl.g:5466:1: rule__RemoveAttribute__Group__1 : rule__RemoveAttribute__Group__1__Impl rule__RemoveAttribute__Group__2 ;
    public final void rule__RemoveAttribute__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5470:1: ( rule__RemoveAttribute__Group__1__Impl rule__RemoveAttribute__Group__2 )
            // InternalOseeDsl.g:5471:2: rule__RemoveAttribute__Group__1__Impl rule__RemoveAttribute__Group__2
            {
            pushFollow(FOLLOW_7);
            rule__RemoveAttribute__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RemoveAttribute__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveAttribute__Group__1"


    // $ANTLR start "rule__RemoveAttribute__Group__1__Impl"
    // InternalOseeDsl.g:5478:1: rule__RemoveAttribute__Group__1__Impl : ( 'attribute' ) ;
    public final void rule__RemoveAttribute__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5482:1: ( ( 'attribute' ) )
            // InternalOseeDsl.g:5483:1: ( 'attribute' )
            {
            // InternalOseeDsl.g:5483:1: ( 'attribute' )
            // InternalOseeDsl.g:5484:1: 'attribute'
            {
             before(grammarAccess.getRemoveAttributeAccess().getAttributeKeyword_1()); 
            match(input,59,FOLLOW_2); 
             after(grammarAccess.getRemoveAttributeAccess().getAttributeKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveAttribute__Group__1__Impl"


    // $ANTLR start "rule__RemoveAttribute__Group__2"
    // InternalOseeDsl.g:5497:1: rule__RemoveAttribute__Group__2 : rule__RemoveAttribute__Group__2__Impl ;
    public final void rule__RemoveAttribute__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5501:1: ( rule__RemoveAttribute__Group__2__Impl )
            // InternalOseeDsl.g:5502:2: rule__RemoveAttribute__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__RemoveAttribute__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveAttribute__Group__2"


    // $ANTLR start "rule__RemoveAttribute__Group__2__Impl"
    // InternalOseeDsl.g:5508:1: rule__RemoveAttribute__Group__2__Impl : ( ( rule__RemoveAttribute__AttributeAssignment_2 ) ) ;
    public final void rule__RemoveAttribute__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5512:1: ( ( ( rule__RemoveAttribute__AttributeAssignment_2 ) ) )
            // InternalOseeDsl.g:5513:1: ( ( rule__RemoveAttribute__AttributeAssignment_2 ) )
            {
            // InternalOseeDsl.g:5513:1: ( ( rule__RemoveAttribute__AttributeAssignment_2 ) )
            // InternalOseeDsl.g:5514:1: ( rule__RemoveAttribute__AttributeAssignment_2 )
            {
             before(grammarAccess.getRemoveAttributeAccess().getAttributeAssignment_2()); 
            // InternalOseeDsl.g:5515:1: ( rule__RemoveAttribute__AttributeAssignment_2 )
            // InternalOseeDsl.g:5515:2: rule__RemoveAttribute__AttributeAssignment_2
            {
            pushFollow(FOLLOW_2);
            rule__RemoveAttribute__AttributeAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getRemoveAttributeAccess().getAttributeAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveAttribute__Group__2__Impl"


    // $ANTLR start "rule__UpdateAttribute__Group__0"
    // InternalOseeDsl.g:5531:1: rule__UpdateAttribute__Group__0 : rule__UpdateAttribute__Group__0__Impl rule__UpdateAttribute__Group__1 ;
    public final void rule__UpdateAttribute__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5535:1: ( rule__UpdateAttribute__Group__0__Impl rule__UpdateAttribute__Group__1 )
            // InternalOseeDsl.g:5536:2: rule__UpdateAttribute__Group__0__Impl rule__UpdateAttribute__Group__1
            {
            pushFollow(FOLLOW_39);
            rule__UpdateAttribute__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__UpdateAttribute__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UpdateAttribute__Group__0"


    // $ANTLR start "rule__UpdateAttribute__Group__0__Impl"
    // InternalOseeDsl.g:5543:1: rule__UpdateAttribute__Group__0__Impl : ( 'update' ) ;
    public final void rule__UpdateAttribute__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5547:1: ( ( 'update' ) )
            // InternalOseeDsl.g:5548:1: ( 'update' )
            {
            // InternalOseeDsl.g:5548:1: ( 'update' )
            // InternalOseeDsl.g:5549:1: 'update'
            {
             before(grammarAccess.getUpdateAttributeAccess().getUpdateKeyword_0()); 
            match(input,78,FOLLOW_2); 
             after(grammarAccess.getUpdateAttributeAccess().getUpdateKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UpdateAttribute__Group__0__Impl"


    // $ANTLR start "rule__UpdateAttribute__Group__1"
    // InternalOseeDsl.g:5562:1: rule__UpdateAttribute__Group__1 : rule__UpdateAttribute__Group__1__Impl ;
    public final void rule__UpdateAttribute__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5566:1: ( rule__UpdateAttribute__Group__1__Impl )
            // InternalOseeDsl.g:5567:2: rule__UpdateAttribute__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__UpdateAttribute__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UpdateAttribute__Group__1"


    // $ANTLR start "rule__UpdateAttribute__Group__1__Impl"
    // InternalOseeDsl.g:5573:1: rule__UpdateAttribute__Group__1__Impl : ( ( rule__UpdateAttribute__AttributeAssignment_1 ) ) ;
    public final void rule__UpdateAttribute__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5577:1: ( ( ( rule__UpdateAttribute__AttributeAssignment_1 ) ) )
            // InternalOseeDsl.g:5578:1: ( ( rule__UpdateAttribute__AttributeAssignment_1 ) )
            {
            // InternalOseeDsl.g:5578:1: ( ( rule__UpdateAttribute__AttributeAssignment_1 ) )
            // InternalOseeDsl.g:5579:1: ( rule__UpdateAttribute__AttributeAssignment_1 )
            {
             before(grammarAccess.getUpdateAttributeAccess().getAttributeAssignment_1()); 
            // InternalOseeDsl.g:5580:1: ( rule__UpdateAttribute__AttributeAssignment_1 )
            // InternalOseeDsl.g:5580:2: rule__UpdateAttribute__AttributeAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__UpdateAttribute__AttributeAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getUpdateAttributeAccess().getAttributeAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UpdateAttribute__Group__1__Impl"


    // $ANTLR start "rule__XRelationType__Group__0"
    // InternalOseeDsl.g:5594:1: rule__XRelationType__Group__0 : rule__XRelationType__Group__0__Impl rule__XRelationType__Group__1 ;
    public final void rule__XRelationType__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5598:1: ( rule__XRelationType__Group__0__Impl rule__XRelationType__Group__1 )
            // InternalOseeDsl.g:5599:2: rule__XRelationType__Group__0__Impl rule__XRelationType__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__XRelationType__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__0"


    // $ANTLR start "rule__XRelationType__Group__0__Impl"
    // InternalOseeDsl.g:5606:1: rule__XRelationType__Group__0__Impl : ( 'relationType' ) ;
    public final void rule__XRelationType__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5610:1: ( ( 'relationType' ) )
            // InternalOseeDsl.g:5611:1: ( 'relationType' )
            {
            // InternalOseeDsl.g:5611:1: ( 'relationType' )
            // InternalOseeDsl.g:5612:1: 'relationType'
            {
             before(grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0()); 
            match(input,79,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getRelationTypeKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__0__Impl"


    // $ANTLR start "rule__XRelationType__Group__1"
    // InternalOseeDsl.g:5625:1: rule__XRelationType__Group__1 : rule__XRelationType__Group__1__Impl rule__XRelationType__Group__2 ;
    public final void rule__XRelationType__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5629:1: ( rule__XRelationType__Group__1__Impl rule__XRelationType__Group__2 )
            // InternalOseeDsl.g:5630:2: rule__XRelationType__Group__1__Impl rule__XRelationType__Group__2
            {
            pushFollow(FOLLOW_31);
            rule__XRelationType__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__1"


    // $ANTLR start "rule__XRelationType__Group__1__Impl"
    // InternalOseeDsl.g:5637:1: rule__XRelationType__Group__1__Impl : ( ( rule__XRelationType__NameAssignment_1 ) ) ;
    public final void rule__XRelationType__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5641:1: ( ( ( rule__XRelationType__NameAssignment_1 ) ) )
            // InternalOseeDsl.g:5642:1: ( ( rule__XRelationType__NameAssignment_1 ) )
            {
            // InternalOseeDsl.g:5642:1: ( ( rule__XRelationType__NameAssignment_1 ) )
            // InternalOseeDsl.g:5643:1: ( rule__XRelationType__NameAssignment_1 )
            {
             before(grammarAccess.getXRelationTypeAccess().getNameAssignment_1()); 
            // InternalOseeDsl.g:5644:1: ( rule__XRelationType__NameAssignment_1 )
            // InternalOseeDsl.g:5644:2: rule__XRelationType__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getXRelationTypeAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__1__Impl"


    // $ANTLR start "rule__XRelationType__Group__2"
    // InternalOseeDsl.g:5654:1: rule__XRelationType__Group__2 : rule__XRelationType__Group__2__Impl rule__XRelationType__Group__3 ;
    public final void rule__XRelationType__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5658:1: ( rule__XRelationType__Group__2__Impl rule__XRelationType__Group__3 )
            // InternalOseeDsl.g:5659:2: rule__XRelationType__Group__2__Impl rule__XRelationType__Group__3
            {
            pushFollow(FOLLOW_13);
            rule__XRelationType__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__2"


    // $ANTLR start "rule__XRelationType__Group__2__Impl"
    // InternalOseeDsl.g:5666:1: rule__XRelationType__Group__2__Impl : ( '{' ) ;
    public final void rule__XRelationType__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5670:1: ( ( '{' ) )
            // InternalOseeDsl.g:5671:1: ( '{' )
            {
            // InternalOseeDsl.g:5671:1: ( '{' )
            // InternalOseeDsl.g:5672:1: '{'
            {
             before(grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,53,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getLeftCurlyBracketKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__2__Impl"


    // $ANTLR start "rule__XRelationType__Group__3"
    // InternalOseeDsl.g:5685:1: rule__XRelationType__Group__3 : rule__XRelationType__Group__3__Impl rule__XRelationType__Group__4 ;
    public final void rule__XRelationType__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5689:1: ( rule__XRelationType__Group__3__Impl rule__XRelationType__Group__4 )
            // InternalOseeDsl.g:5690:2: rule__XRelationType__Group__3__Impl rule__XRelationType__Group__4
            {
            pushFollow(FOLLOW_13);
            rule__XRelationType__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__3"


    // $ANTLR start "rule__XRelationType__Group__3__Impl"
    // InternalOseeDsl.g:5697:1: rule__XRelationType__Group__3__Impl : ( ( rule__XRelationType__Group_3__0 )? ) ;
    public final void rule__XRelationType__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5701:1: ( ( ( rule__XRelationType__Group_3__0 )? ) )
            // InternalOseeDsl.g:5702:1: ( ( rule__XRelationType__Group_3__0 )? )
            {
            // InternalOseeDsl.g:5702:1: ( ( rule__XRelationType__Group_3__0 )? )
            // InternalOseeDsl.g:5703:1: ( rule__XRelationType__Group_3__0 )?
            {
             before(grammarAccess.getXRelationTypeAccess().getGroup_3()); 
            // InternalOseeDsl.g:5704:1: ( rule__XRelationType__Group_3__0 )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==58) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // InternalOseeDsl.g:5704:2: rule__XRelationType__Group_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XRelationType__Group_3__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getXRelationTypeAccess().getGroup_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__3__Impl"


    // $ANTLR start "rule__XRelationType__Group__4"
    // InternalOseeDsl.g:5714:1: rule__XRelationType__Group__4 : rule__XRelationType__Group__4__Impl rule__XRelationType__Group__5 ;
    public final void rule__XRelationType__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5718:1: ( rule__XRelationType__Group__4__Impl rule__XRelationType__Group__5 )
            // InternalOseeDsl.g:5719:2: rule__XRelationType__Group__4__Impl rule__XRelationType__Group__5
            {
            pushFollow(FOLLOW_14);
            rule__XRelationType__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__4"


    // $ANTLR start "rule__XRelationType__Group__4__Impl"
    // InternalOseeDsl.g:5726:1: rule__XRelationType__Group__4__Impl : ( 'id' ) ;
    public final void rule__XRelationType__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5730:1: ( ( 'id' ) )
            // InternalOseeDsl.g:5731:1: ( 'id' )
            {
            // InternalOseeDsl.g:5731:1: ( 'id' )
            // InternalOseeDsl.g:5732:1: 'id'
            {
             before(grammarAccess.getXRelationTypeAccess().getIdKeyword_4()); 
            match(input,54,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getIdKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__4__Impl"


    // $ANTLR start "rule__XRelationType__Group__5"
    // InternalOseeDsl.g:5745:1: rule__XRelationType__Group__5 : rule__XRelationType__Group__5__Impl rule__XRelationType__Group__6 ;
    public final void rule__XRelationType__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5749:1: ( rule__XRelationType__Group__5__Impl rule__XRelationType__Group__6 )
            // InternalOseeDsl.g:5750:2: rule__XRelationType__Group__5__Impl rule__XRelationType__Group__6
            {
            pushFollow(FOLLOW_40);
            rule__XRelationType__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__5"


    // $ANTLR start "rule__XRelationType__Group__5__Impl"
    // InternalOseeDsl.g:5757:1: rule__XRelationType__Group__5__Impl : ( ( rule__XRelationType__IdAssignment_5 ) ) ;
    public final void rule__XRelationType__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5761:1: ( ( ( rule__XRelationType__IdAssignment_5 ) ) )
            // InternalOseeDsl.g:5762:1: ( ( rule__XRelationType__IdAssignment_5 ) )
            {
            // InternalOseeDsl.g:5762:1: ( ( rule__XRelationType__IdAssignment_5 ) )
            // InternalOseeDsl.g:5763:1: ( rule__XRelationType__IdAssignment_5 )
            {
             before(grammarAccess.getXRelationTypeAccess().getIdAssignment_5()); 
            // InternalOseeDsl.g:5764:1: ( rule__XRelationType__IdAssignment_5 )
            // InternalOseeDsl.g:5764:2: rule__XRelationType__IdAssignment_5
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__IdAssignment_5();

            state._fsp--;


            }

             after(grammarAccess.getXRelationTypeAccess().getIdAssignment_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__5__Impl"


    // $ANTLR start "rule__XRelationType__Group__6"
    // InternalOseeDsl.g:5774:1: rule__XRelationType__Group__6 : rule__XRelationType__Group__6__Impl rule__XRelationType__Group__7 ;
    public final void rule__XRelationType__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5778:1: ( rule__XRelationType__Group__6__Impl rule__XRelationType__Group__7 )
            // InternalOseeDsl.g:5779:2: rule__XRelationType__Group__6__Impl rule__XRelationType__Group__7
            {
            pushFollow(FOLLOW_7);
            rule__XRelationType__Group__6__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__7();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__6"


    // $ANTLR start "rule__XRelationType__Group__6__Impl"
    // InternalOseeDsl.g:5786:1: rule__XRelationType__Group__6__Impl : ( 'sideAName' ) ;
    public final void rule__XRelationType__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5790:1: ( ( 'sideAName' ) )
            // InternalOseeDsl.g:5791:1: ( 'sideAName' )
            {
            // InternalOseeDsl.g:5791:1: ( 'sideAName' )
            // InternalOseeDsl.g:5792:1: 'sideAName'
            {
             before(grammarAccess.getXRelationTypeAccess().getSideANameKeyword_6()); 
            match(input,80,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getSideANameKeyword_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__6__Impl"


    // $ANTLR start "rule__XRelationType__Group__7"
    // InternalOseeDsl.g:5805:1: rule__XRelationType__Group__7 : rule__XRelationType__Group__7__Impl rule__XRelationType__Group__8 ;
    public final void rule__XRelationType__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5809:1: ( rule__XRelationType__Group__7__Impl rule__XRelationType__Group__8 )
            // InternalOseeDsl.g:5810:2: rule__XRelationType__Group__7__Impl rule__XRelationType__Group__8
            {
            pushFollow(FOLLOW_41);
            rule__XRelationType__Group__7__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__8();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__7"


    // $ANTLR start "rule__XRelationType__Group__7__Impl"
    // InternalOseeDsl.g:5817:1: rule__XRelationType__Group__7__Impl : ( ( rule__XRelationType__SideANameAssignment_7 ) ) ;
    public final void rule__XRelationType__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5821:1: ( ( ( rule__XRelationType__SideANameAssignment_7 ) ) )
            // InternalOseeDsl.g:5822:1: ( ( rule__XRelationType__SideANameAssignment_7 ) )
            {
            // InternalOseeDsl.g:5822:1: ( ( rule__XRelationType__SideANameAssignment_7 ) )
            // InternalOseeDsl.g:5823:1: ( rule__XRelationType__SideANameAssignment_7 )
            {
             before(grammarAccess.getXRelationTypeAccess().getSideANameAssignment_7()); 
            // InternalOseeDsl.g:5824:1: ( rule__XRelationType__SideANameAssignment_7 )
            // InternalOseeDsl.g:5824:2: rule__XRelationType__SideANameAssignment_7
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__SideANameAssignment_7();

            state._fsp--;


            }

             after(grammarAccess.getXRelationTypeAccess().getSideANameAssignment_7()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__7__Impl"


    // $ANTLR start "rule__XRelationType__Group__8"
    // InternalOseeDsl.g:5834:1: rule__XRelationType__Group__8 : rule__XRelationType__Group__8__Impl rule__XRelationType__Group__9 ;
    public final void rule__XRelationType__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5838:1: ( rule__XRelationType__Group__8__Impl rule__XRelationType__Group__9 )
            // InternalOseeDsl.g:5839:2: rule__XRelationType__Group__8__Impl rule__XRelationType__Group__9
            {
            pushFollow(FOLLOW_7);
            rule__XRelationType__Group__8__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__9();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__8"


    // $ANTLR start "rule__XRelationType__Group__8__Impl"
    // InternalOseeDsl.g:5846:1: rule__XRelationType__Group__8__Impl : ( 'sideAArtifactType' ) ;
    public final void rule__XRelationType__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5850:1: ( ( 'sideAArtifactType' ) )
            // InternalOseeDsl.g:5851:1: ( 'sideAArtifactType' )
            {
            // InternalOseeDsl.g:5851:1: ( 'sideAArtifactType' )
            // InternalOseeDsl.g:5852:1: 'sideAArtifactType'
            {
             before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_8()); 
            match(input,81,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeKeyword_8()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__8__Impl"


    // $ANTLR start "rule__XRelationType__Group__9"
    // InternalOseeDsl.g:5865:1: rule__XRelationType__Group__9 : rule__XRelationType__Group__9__Impl rule__XRelationType__Group__10 ;
    public final void rule__XRelationType__Group__9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5869:1: ( rule__XRelationType__Group__9__Impl rule__XRelationType__Group__10 )
            // InternalOseeDsl.g:5870:2: rule__XRelationType__Group__9__Impl rule__XRelationType__Group__10
            {
            pushFollow(FOLLOW_42);
            rule__XRelationType__Group__9__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__10();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__9"


    // $ANTLR start "rule__XRelationType__Group__9__Impl"
    // InternalOseeDsl.g:5877:1: rule__XRelationType__Group__9__Impl : ( ( rule__XRelationType__SideAArtifactTypeAssignment_9 ) ) ;
    public final void rule__XRelationType__Group__9__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5881:1: ( ( ( rule__XRelationType__SideAArtifactTypeAssignment_9 ) ) )
            // InternalOseeDsl.g:5882:1: ( ( rule__XRelationType__SideAArtifactTypeAssignment_9 ) )
            {
            // InternalOseeDsl.g:5882:1: ( ( rule__XRelationType__SideAArtifactTypeAssignment_9 ) )
            // InternalOseeDsl.g:5883:1: ( rule__XRelationType__SideAArtifactTypeAssignment_9 )
            {
             before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeAssignment_9()); 
            // InternalOseeDsl.g:5884:1: ( rule__XRelationType__SideAArtifactTypeAssignment_9 )
            // InternalOseeDsl.g:5884:2: rule__XRelationType__SideAArtifactTypeAssignment_9
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__SideAArtifactTypeAssignment_9();

            state._fsp--;


            }

             after(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeAssignment_9()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__9__Impl"


    // $ANTLR start "rule__XRelationType__Group__10"
    // InternalOseeDsl.g:5894:1: rule__XRelationType__Group__10 : rule__XRelationType__Group__10__Impl rule__XRelationType__Group__11 ;
    public final void rule__XRelationType__Group__10() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5898:1: ( rule__XRelationType__Group__10__Impl rule__XRelationType__Group__11 )
            // InternalOseeDsl.g:5899:2: rule__XRelationType__Group__10__Impl rule__XRelationType__Group__11
            {
            pushFollow(FOLLOW_7);
            rule__XRelationType__Group__10__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__11();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__10"


    // $ANTLR start "rule__XRelationType__Group__10__Impl"
    // InternalOseeDsl.g:5906:1: rule__XRelationType__Group__10__Impl : ( 'sideBName' ) ;
    public final void rule__XRelationType__Group__10__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5910:1: ( ( 'sideBName' ) )
            // InternalOseeDsl.g:5911:1: ( 'sideBName' )
            {
            // InternalOseeDsl.g:5911:1: ( 'sideBName' )
            // InternalOseeDsl.g:5912:1: 'sideBName'
            {
             before(grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_10()); 
            match(input,82,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getSideBNameKeyword_10()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__10__Impl"


    // $ANTLR start "rule__XRelationType__Group__11"
    // InternalOseeDsl.g:5925:1: rule__XRelationType__Group__11 : rule__XRelationType__Group__11__Impl rule__XRelationType__Group__12 ;
    public final void rule__XRelationType__Group__11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5929:1: ( rule__XRelationType__Group__11__Impl rule__XRelationType__Group__12 )
            // InternalOseeDsl.g:5930:2: rule__XRelationType__Group__11__Impl rule__XRelationType__Group__12
            {
            pushFollow(FOLLOW_43);
            rule__XRelationType__Group__11__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__12();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__11"


    // $ANTLR start "rule__XRelationType__Group__11__Impl"
    // InternalOseeDsl.g:5937:1: rule__XRelationType__Group__11__Impl : ( ( rule__XRelationType__SideBNameAssignment_11 ) ) ;
    public final void rule__XRelationType__Group__11__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5941:1: ( ( ( rule__XRelationType__SideBNameAssignment_11 ) ) )
            // InternalOseeDsl.g:5942:1: ( ( rule__XRelationType__SideBNameAssignment_11 ) )
            {
            // InternalOseeDsl.g:5942:1: ( ( rule__XRelationType__SideBNameAssignment_11 ) )
            // InternalOseeDsl.g:5943:1: ( rule__XRelationType__SideBNameAssignment_11 )
            {
             before(grammarAccess.getXRelationTypeAccess().getSideBNameAssignment_11()); 
            // InternalOseeDsl.g:5944:1: ( rule__XRelationType__SideBNameAssignment_11 )
            // InternalOseeDsl.g:5944:2: rule__XRelationType__SideBNameAssignment_11
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__SideBNameAssignment_11();

            state._fsp--;


            }

             after(grammarAccess.getXRelationTypeAccess().getSideBNameAssignment_11()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__11__Impl"


    // $ANTLR start "rule__XRelationType__Group__12"
    // InternalOseeDsl.g:5954:1: rule__XRelationType__Group__12 : rule__XRelationType__Group__12__Impl rule__XRelationType__Group__13 ;
    public final void rule__XRelationType__Group__12() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5958:1: ( rule__XRelationType__Group__12__Impl rule__XRelationType__Group__13 )
            // InternalOseeDsl.g:5959:2: rule__XRelationType__Group__12__Impl rule__XRelationType__Group__13
            {
            pushFollow(FOLLOW_7);
            rule__XRelationType__Group__12__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__13();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__12"


    // $ANTLR start "rule__XRelationType__Group__12__Impl"
    // InternalOseeDsl.g:5966:1: rule__XRelationType__Group__12__Impl : ( 'sideBArtifactType' ) ;
    public final void rule__XRelationType__Group__12__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5970:1: ( ( 'sideBArtifactType' ) )
            // InternalOseeDsl.g:5971:1: ( 'sideBArtifactType' )
            {
            // InternalOseeDsl.g:5971:1: ( 'sideBArtifactType' )
            // InternalOseeDsl.g:5972:1: 'sideBArtifactType'
            {
             before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_12()); 
            match(input,83,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeKeyword_12()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__12__Impl"


    // $ANTLR start "rule__XRelationType__Group__13"
    // InternalOseeDsl.g:5985:1: rule__XRelationType__Group__13 : rule__XRelationType__Group__13__Impl rule__XRelationType__Group__14 ;
    public final void rule__XRelationType__Group__13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:5989:1: ( rule__XRelationType__Group__13__Impl rule__XRelationType__Group__14 )
            // InternalOseeDsl.g:5990:2: rule__XRelationType__Group__13__Impl rule__XRelationType__Group__14
            {
            pushFollow(FOLLOW_44);
            rule__XRelationType__Group__13__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__14();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__13"


    // $ANTLR start "rule__XRelationType__Group__13__Impl"
    // InternalOseeDsl.g:5997:1: rule__XRelationType__Group__13__Impl : ( ( rule__XRelationType__SideBArtifactTypeAssignment_13 ) ) ;
    public final void rule__XRelationType__Group__13__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6001:1: ( ( ( rule__XRelationType__SideBArtifactTypeAssignment_13 ) ) )
            // InternalOseeDsl.g:6002:1: ( ( rule__XRelationType__SideBArtifactTypeAssignment_13 ) )
            {
            // InternalOseeDsl.g:6002:1: ( ( rule__XRelationType__SideBArtifactTypeAssignment_13 ) )
            // InternalOseeDsl.g:6003:1: ( rule__XRelationType__SideBArtifactTypeAssignment_13 )
            {
             before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeAssignment_13()); 
            // InternalOseeDsl.g:6004:1: ( rule__XRelationType__SideBArtifactTypeAssignment_13 )
            // InternalOseeDsl.g:6004:2: rule__XRelationType__SideBArtifactTypeAssignment_13
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__SideBArtifactTypeAssignment_13();

            state._fsp--;


            }

             after(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeAssignment_13()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__13__Impl"


    // $ANTLR start "rule__XRelationType__Group__14"
    // InternalOseeDsl.g:6014:1: rule__XRelationType__Group__14 : rule__XRelationType__Group__14__Impl rule__XRelationType__Group__15 ;
    public final void rule__XRelationType__Group__14() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6018:1: ( rule__XRelationType__Group__14__Impl rule__XRelationType__Group__15 )
            // InternalOseeDsl.g:6019:2: rule__XRelationType__Group__14__Impl rule__XRelationType__Group__15
            {
            pushFollow(FOLLOW_45);
            rule__XRelationType__Group__14__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__15();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__14"


    // $ANTLR start "rule__XRelationType__Group__14__Impl"
    // InternalOseeDsl.g:6026:1: rule__XRelationType__Group__14__Impl : ( 'defaultOrderType' ) ;
    public final void rule__XRelationType__Group__14__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6030:1: ( ( 'defaultOrderType' ) )
            // InternalOseeDsl.g:6031:1: ( 'defaultOrderType' )
            {
            // InternalOseeDsl.g:6031:1: ( 'defaultOrderType' )
            // InternalOseeDsl.g:6032:1: 'defaultOrderType'
            {
             before(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_14()); 
            match(input,84,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeKeyword_14()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__14__Impl"


    // $ANTLR start "rule__XRelationType__Group__15"
    // InternalOseeDsl.g:6045:1: rule__XRelationType__Group__15 : rule__XRelationType__Group__15__Impl rule__XRelationType__Group__16 ;
    public final void rule__XRelationType__Group__15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6049:1: ( rule__XRelationType__Group__15__Impl rule__XRelationType__Group__16 )
            // InternalOseeDsl.g:6050:2: rule__XRelationType__Group__15__Impl rule__XRelationType__Group__16
            {
            pushFollow(FOLLOW_46);
            rule__XRelationType__Group__15__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__16();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__15"


    // $ANTLR start "rule__XRelationType__Group__15__Impl"
    // InternalOseeDsl.g:6057:1: rule__XRelationType__Group__15__Impl : ( ( rule__XRelationType__DefaultOrderTypeAssignment_15 ) ) ;
    public final void rule__XRelationType__Group__15__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6061:1: ( ( ( rule__XRelationType__DefaultOrderTypeAssignment_15 ) ) )
            // InternalOseeDsl.g:6062:1: ( ( rule__XRelationType__DefaultOrderTypeAssignment_15 ) )
            {
            // InternalOseeDsl.g:6062:1: ( ( rule__XRelationType__DefaultOrderTypeAssignment_15 ) )
            // InternalOseeDsl.g:6063:1: ( rule__XRelationType__DefaultOrderTypeAssignment_15 )
            {
             before(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeAssignment_15()); 
            // InternalOseeDsl.g:6064:1: ( rule__XRelationType__DefaultOrderTypeAssignment_15 )
            // InternalOseeDsl.g:6064:2: rule__XRelationType__DefaultOrderTypeAssignment_15
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__DefaultOrderTypeAssignment_15();

            state._fsp--;


            }

             after(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeAssignment_15()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__15__Impl"


    // $ANTLR start "rule__XRelationType__Group__16"
    // InternalOseeDsl.g:6074:1: rule__XRelationType__Group__16 : rule__XRelationType__Group__16__Impl rule__XRelationType__Group__17 ;
    public final void rule__XRelationType__Group__16() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6078:1: ( rule__XRelationType__Group__16__Impl rule__XRelationType__Group__17 )
            // InternalOseeDsl.g:6079:2: rule__XRelationType__Group__16__Impl rule__XRelationType__Group__17
            {
            pushFollow(FOLLOW_47);
            rule__XRelationType__Group__16__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__17();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__16"


    // $ANTLR start "rule__XRelationType__Group__16__Impl"
    // InternalOseeDsl.g:6086:1: rule__XRelationType__Group__16__Impl : ( 'multiplicity' ) ;
    public final void rule__XRelationType__Group__16__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6090:1: ( ( 'multiplicity' ) )
            // InternalOseeDsl.g:6091:1: ( 'multiplicity' )
            {
            // InternalOseeDsl.g:6091:1: ( 'multiplicity' )
            // InternalOseeDsl.g:6092:1: 'multiplicity'
            {
             before(grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_16()); 
            match(input,85,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getMultiplicityKeyword_16()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__16__Impl"


    // $ANTLR start "rule__XRelationType__Group__17"
    // InternalOseeDsl.g:6105:1: rule__XRelationType__Group__17 : rule__XRelationType__Group__17__Impl rule__XRelationType__Group__18 ;
    public final void rule__XRelationType__Group__17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6109:1: ( rule__XRelationType__Group__17__Impl rule__XRelationType__Group__18 )
            // InternalOseeDsl.g:6110:2: rule__XRelationType__Group__17__Impl rule__XRelationType__Group__18
            {
            pushFollow(FOLLOW_28);
            rule__XRelationType__Group__17__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__18();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__17"


    // $ANTLR start "rule__XRelationType__Group__17__Impl"
    // InternalOseeDsl.g:6117:1: rule__XRelationType__Group__17__Impl : ( ( rule__XRelationType__MultiplicityAssignment_17 ) ) ;
    public final void rule__XRelationType__Group__17__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6121:1: ( ( ( rule__XRelationType__MultiplicityAssignment_17 ) ) )
            // InternalOseeDsl.g:6122:1: ( ( rule__XRelationType__MultiplicityAssignment_17 ) )
            {
            // InternalOseeDsl.g:6122:1: ( ( rule__XRelationType__MultiplicityAssignment_17 ) )
            // InternalOseeDsl.g:6123:1: ( rule__XRelationType__MultiplicityAssignment_17 )
            {
             before(grammarAccess.getXRelationTypeAccess().getMultiplicityAssignment_17()); 
            // InternalOseeDsl.g:6124:1: ( rule__XRelationType__MultiplicityAssignment_17 )
            // InternalOseeDsl.g:6124:2: rule__XRelationType__MultiplicityAssignment_17
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__MultiplicityAssignment_17();

            state._fsp--;


            }

             after(grammarAccess.getXRelationTypeAccess().getMultiplicityAssignment_17()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__17__Impl"


    // $ANTLR start "rule__XRelationType__Group__18"
    // InternalOseeDsl.g:6134:1: rule__XRelationType__Group__18 : rule__XRelationType__Group__18__Impl ;
    public final void rule__XRelationType__Group__18() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6138:1: ( rule__XRelationType__Group__18__Impl )
            // InternalOseeDsl.g:6139:2: rule__XRelationType__Group__18__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__Group__18__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__18"


    // $ANTLR start "rule__XRelationType__Group__18__Impl"
    // InternalOseeDsl.g:6145:1: rule__XRelationType__Group__18__Impl : ( '}' ) ;
    public final void rule__XRelationType__Group__18__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6149:1: ( ( '}' ) )
            // InternalOseeDsl.g:6150:1: ( '}' )
            {
            // InternalOseeDsl.g:6150:1: ( '}' )
            // InternalOseeDsl.g:6151:1: '}'
            {
             before(grammarAccess.getXRelationTypeAccess().getRightCurlyBracketKeyword_18()); 
            match(input,55,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getRightCurlyBracketKeyword_18()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group__18__Impl"


    // $ANTLR start "rule__XRelationType__Group_3__0"
    // InternalOseeDsl.g:6202:1: rule__XRelationType__Group_3__0 : rule__XRelationType__Group_3__0__Impl rule__XRelationType__Group_3__1 ;
    public final void rule__XRelationType__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6206:1: ( rule__XRelationType__Group_3__0__Impl rule__XRelationType__Group_3__1 )
            // InternalOseeDsl.g:6207:2: rule__XRelationType__Group_3__0__Impl rule__XRelationType__Group_3__1
            {
            pushFollow(FOLLOW_7);
            rule__XRelationType__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XRelationType__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group_3__0"


    // $ANTLR start "rule__XRelationType__Group_3__0__Impl"
    // InternalOseeDsl.g:6214:1: rule__XRelationType__Group_3__0__Impl : ( 'guid' ) ;
    public final void rule__XRelationType__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6218:1: ( ( 'guid' ) )
            // InternalOseeDsl.g:6219:1: ( 'guid' )
            {
            // InternalOseeDsl.g:6219:1: ( 'guid' )
            // InternalOseeDsl.g:6220:1: 'guid'
            {
             before(grammarAccess.getXRelationTypeAccess().getGuidKeyword_3_0()); 
            match(input,58,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getGuidKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group_3__0__Impl"


    // $ANTLR start "rule__XRelationType__Group_3__1"
    // InternalOseeDsl.g:6233:1: rule__XRelationType__Group_3__1 : rule__XRelationType__Group_3__1__Impl ;
    public final void rule__XRelationType__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6237:1: ( rule__XRelationType__Group_3__1__Impl )
            // InternalOseeDsl.g:6238:2: rule__XRelationType__Group_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__Group_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group_3__1"


    // $ANTLR start "rule__XRelationType__Group_3__1__Impl"
    // InternalOseeDsl.g:6244:1: rule__XRelationType__Group_3__1__Impl : ( ( rule__XRelationType__TypeGuidAssignment_3_1 ) ) ;
    public final void rule__XRelationType__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6248:1: ( ( ( rule__XRelationType__TypeGuidAssignment_3_1 ) ) )
            // InternalOseeDsl.g:6249:1: ( ( rule__XRelationType__TypeGuidAssignment_3_1 ) )
            {
            // InternalOseeDsl.g:6249:1: ( ( rule__XRelationType__TypeGuidAssignment_3_1 ) )
            // InternalOseeDsl.g:6250:1: ( rule__XRelationType__TypeGuidAssignment_3_1 )
            {
             before(grammarAccess.getXRelationTypeAccess().getTypeGuidAssignment_3_1()); 
            // InternalOseeDsl.g:6251:1: ( rule__XRelationType__TypeGuidAssignment_3_1 )
            // InternalOseeDsl.g:6251:2: rule__XRelationType__TypeGuidAssignment_3_1
            {
            pushFollow(FOLLOW_2);
            rule__XRelationType__TypeGuidAssignment_3_1();

            state._fsp--;


            }

             after(grammarAccess.getXRelationTypeAccess().getTypeGuidAssignment_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__Group_3__1__Impl"


    // $ANTLR start "rule__SimpleCondition__Group__0"
    // InternalOseeDsl.g:6265:1: rule__SimpleCondition__Group__0 : rule__SimpleCondition__Group__0__Impl rule__SimpleCondition__Group__1 ;
    public final void rule__SimpleCondition__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6269:1: ( rule__SimpleCondition__Group__0__Impl rule__SimpleCondition__Group__1 )
            // InternalOseeDsl.g:6270:2: rule__SimpleCondition__Group__0__Impl rule__SimpleCondition__Group__1
            {
            pushFollow(FOLLOW_48);
            rule__SimpleCondition__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__SimpleCondition__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleCondition__Group__0"


    // $ANTLR start "rule__SimpleCondition__Group__0__Impl"
    // InternalOseeDsl.g:6277:1: rule__SimpleCondition__Group__0__Impl : ( ( rule__SimpleCondition__FieldAssignment_0 ) ) ;
    public final void rule__SimpleCondition__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6281:1: ( ( ( rule__SimpleCondition__FieldAssignment_0 ) ) )
            // InternalOseeDsl.g:6282:1: ( ( rule__SimpleCondition__FieldAssignment_0 ) )
            {
            // InternalOseeDsl.g:6282:1: ( ( rule__SimpleCondition__FieldAssignment_0 ) )
            // InternalOseeDsl.g:6283:1: ( rule__SimpleCondition__FieldAssignment_0 )
            {
             before(grammarAccess.getSimpleConditionAccess().getFieldAssignment_0()); 
            // InternalOseeDsl.g:6284:1: ( rule__SimpleCondition__FieldAssignment_0 )
            // InternalOseeDsl.g:6284:2: rule__SimpleCondition__FieldAssignment_0
            {
            pushFollow(FOLLOW_2);
            rule__SimpleCondition__FieldAssignment_0();

            state._fsp--;


            }

             after(grammarAccess.getSimpleConditionAccess().getFieldAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleCondition__Group__0__Impl"


    // $ANTLR start "rule__SimpleCondition__Group__1"
    // InternalOseeDsl.g:6294:1: rule__SimpleCondition__Group__1 : rule__SimpleCondition__Group__1__Impl rule__SimpleCondition__Group__2 ;
    public final void rule__SimpleCondition__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6298:1: ( rule__SimpleCondition__Group__1__Impl rule__SimpleCondition__Group__2 )
            // InternalOseeDsl.g:6299:2: rule__SimpleCondition__Group__1__Impl rule__SimpleCondition__Group__2
            {
            pushFollow(FOLLOW_7);
            rule__SimpleCondition__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__SimpleCondition__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleCondition__Group__1"


    // $ANTLR start "rule__SimpleCondition__Group__1__Impl"
    // InternalOseeDsl.g:6306:1: rule__SimpleCondition__Group__1__Impl : ( ( rule__SimpleCondition__OpAssignment_1 ) ) ;
    public final void rule__SimpleCondition__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6310:1: ( ( ( rule__SimpleCondition__OpAssignment_1 ) ) )
            // InternalOseeDsl.g:6311:1: ( ( rule__SimpleCondition__OpAssignment_1 ) )
            {
            // InternalOseeDsl.g:6311:1: ( ( rule__SimpleCondition__OpAssignment_1 ) )
            // InternalOseeDsl.g:6312:1: ( rule__SimpleCondition__OpAssignment_1 )
            {
             before(grammarAccess.getSimpleConditionAccess().getOpAssignment_1()); 
            // InternalOseeDsl.g:6313:1: ( rule__SimpleCondition__OpAssignment_1 )
            // InternalOseeDsl.g:6313:2: rule__SimpleCondition__OpAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__SimpleCondition__OpAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getSimpleConditionAccess().getOpAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleCondition__Group__1__Impl"


    // $ANTLR start "rule__SimpleCondition__Group__2"
    // InternalOseeDsl.g:6323:1: rule__SimpleCondition__Group__2 : rule__SimpleCondition__Group__2__Impl ;
    public final void rule__SimpleCondition__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6327:1: ( rule__SimpleCondition__Group__2__Impl )
            // InternalOseeDsl.g:6328:2: rule__SimpleCondition__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__SimpleCondition__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleCondition__Group__2"


    // $ANTLR start "rule__SimpleCondition__Group__2__Impl"
    // InternalOseeDsl.g:6334:1: rule__SimpleCondition__Group__2__Impl : ( ( rule__SimpleCondition__ExpressionAssignment_2 ) ) ;
    public final void rule__SimpleCondition__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6338:1: ( ( ( rule__SimpleCondition__ExpressionAssignment_2 ) ) )
            // InternalOseeDsl.g:6339:1: ( ( rule__SimpleCondition__ExpressionAssignment_2 ) )
            {
            // InternalOseeDsl.g:6339:1: ( ( rule__SimpleCondition__ExpressionAssignment_2 ) )
            // InternalOseeDsl.g:6340:1: ( rule__SimpleCondition__ExpressionAssignment_2 )
            {
             before(grammarAccess.getSimpleConditionAccess().getExpressionAssignment_2()); 
            // InternalOseeDsl.g:6341:1: ( rule__SimpleCondition__ExpressionAssignment_2 )
            // InternalOseeDsl.g:6341:2: rule__SimpleCondition__ExpressionAssignment_2
            {
            pushFollow(FOLLOW_2);
            rule__SimpleCondition__ExpressionAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getSimpleConditionAccess().getExpressionAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleCondition__Group__2__Impl"


    // $ANTLR start "rule__CompoundCondition__Group__0"
    // InternalOseeDsl.g:6357:1: rule__CompoundCondition__Group__0 : rule__CompoundCondition__Group__0__Impl rule__CompoundCondition__Group__1 ;
    public final void rule__CompoundCondition__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6361:1: ( rule__CompoundCondition__Group__0__Impl rule__CompoundCondition__Group__1 )
            // InternalOseeDsl.g:6362:2: rule__CompoundCondition__Group__0__Impl rule__CompoundCondition__Group__1
            {
            pushFollow(FOLLOW_49);
            rule__CompoundCondition__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__CompoundCondition__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group__0"


    // $ANTLR start "rule__CompoundCondition__Group__0__Impl"
    // InternalOseeDsl.g:6369:1: rule__CompoundCondition__Group__0__Impl : ( '(' ) ;
    public final void rule__CompoundCondition__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6373:1: ( ( '(' ) )
            // InternalOseeDsl.g:6374:1: ( '(' )
            {
            // InternalOseeDsl.g:6374:1: ( '(' )
            // InternalOseeDsl.g:6375:1: '('
            {
             before(grammarAccess.getCompoundConditionAccess().getLeftParenthesisKeyword_0()); 
            match(input,86,FOLLOW_2); 
             after(grammarAccess.getCompoundConditionAccess().getLeftParenthesisKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group__0__Impl"


    // $ANTLR start "rule__CompoundCondition__Group__1"
    // InternalOseeDsl.g:6388:1: rule__CompoundCondition__Group__1 : rule__CompoundCondition__Group__1__Impl rule__CompoundCondition__Group__2 ;
    public final void rule__CompoundCondition__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6392:1: ( rule__CompoundCondition__Group__1__Impl rule__CompoundCondition__Group__2 )
            // InternalOseeDsl.g:6393:2: rule__CompoundCondition__Group__1__Impl rule__CompoundCondition__Group__2
            {
            pushFollow(FOLLOW_50);
            rule__CompoundCondition__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__CompoundCondition__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group__1"


    // $ANTLR start "rule__CompoundCondition__Group__1__Impl"
    // InternalOseeDsl.g:6400:1: rule__CompoundCondition__Group__1__Impl : ( ( rule__CompoundCondition__ConditionsAssignment_1 ) ) ;
    public final void rule__CompoundCondition__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6404:1: ( ( ( rule__CompoundCondition__ConditionsAssignment_1 ) ) )
            // InternalOseeDsl.g:6405:1: ( ( rule__CompoundCondition__ConditionsAssignment_1 ) )
            {
            // InternalOseeDsl.g:6405:1: ( ( rule__CompoundCondition__ConditionsAssignment_1 ) )
            // InternalOseeDsl.g:6406:1: ( rule__CompoundCondition__ConditionsAssignment_1 )
            {
             before(grammarAccess.getCompoundConditionAccess().getConditionsAssignment_1()); 
            // InternalOseeDsl.g:6407:1: ( rule__CompoundCondition__ConditionsAssignment_1 )
            // InternalOseeDsl.g:6407:2: rule__CompoundCondition__ConditionsAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__CompoundCondition__ConditionsAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getCompoundConditionAccess().getConditionsAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group__1__Impl"


    // $ANTLR start "rule__CompoundCondition__Group__2"
    // InternalOseeDsl.g:6417:1: rule__CompoundCondition__Group__2 : rule__CompoundCondition__Group__2__Impl rule__CompoundCondition__Group__3 ;
    public final void rule__CompoundCondition__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6421:1: ( rule__CompoundCondition__Group__2__Impl rule__CompoundCondition__Group__3 )
            // InternalOseeDsl.g:6422:2: rule__CompoundCondition__Group__2__Impl rule__CompoundCondition__Group__3
            {
            pushFollow(FOLLOW_51);
            rule__CompoundCondition__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__CompoundCondition__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group__2"


    // $ANTLR start "rule__CompoundCondition__Group__2__Impl"
    // InternalOseeDsl.g:6429:1: rule__CompoundCondition__Group__2__Impl : ( ( ( rule__CompoundCondition__Group_2__0 ) ) ( ( rule__CompoundCondition__Group_2__0 )* ) ) ;
    public final void rule__CompoundCondition__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6433:1: ( ( ( ( rule__CompoundCondition__Group_2__0 ) ) ( ( rule__CompoundCondition__Group_2__0 )* ) ) )
            // InternalOseeDsl.g:6434:1: ( ( ( rule__CompoundCondition__Group_2__0 ) ) ( ( rule__CompoundCondition__Group_2__0 )* ) )
            {
            // InternalOseeDsl.g:6434:1: ( ( ( rule__CompoundCondition__Group_2__0 ) ) ( ( rule__CompoundCondition__Group_2__0 )* ) )
            // InternalOseeDsl.g:6435:1: ( ( rule__CompoundCondition__Group_2__0 ) ) ( ( rule__CompoundCondition__Group_2__0 )* )
            {
            // InternalOseeDsl.g:6435:1: ( ( rule__CompoundCondition__Group_2__0 ) )
            // InternalOseeDsl.g:6436:1: ( rule__CompoundCondition__Group_2__0 )
            {
             before(grammarAccess.getCompoundConditionAccess().getGroup_2()); 
            // InternalOseeDsl.g:6437:1: ( rule__CompoundCondition__Group_2__0 )
            // InternalOseeDsl.g:6437:2: rule__CompoundCondition__Group_2__0
            {
            pushFollow(FOLLOW_52);
            rule__CompoundCondition__Group_2__0();

            state._fsp--;


            }

             after(grammarAccess.getCompoundConditionAccess().getGroup_2()); 

            }

            // InternalOseeDsl.g:6440:1: ( ( rule__CompoundCondition__Group_2__0 )* )
            // InternalOseeDsl.g:6441:1: ( rule__CompoundCondition__Group_2__0 )*
            {
             before(grammarAccess.getCompoundConditionAccess().getGroup_2()); 
            // InternalOseeDsl.g:6442:1: ( rule__CompoundCondition__Group_2__0 )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( ((LA48_0>=39 && LA48_0<=40)) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // InternalOseeDsl.g:6442:2: rule__CompoundCondition__Group_2__0
            	    {
            	    pushFollow(FOLLOW_52);
            	    rule__CompoundCondition__Group_2__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);

             after(grammarAccess.getCompoundConditionAccess().getGroup_2()); 

            }


            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group__2__Impl"


    // $ANTLR start "rule__CompoundCondition__Group__3"
    // InternalOseeDsl.g:6453:1: rule__CompoundCondition__Group__3 : rule__CompoundCondition__Group__3__Impl ;
    public final void rule__CompoundCondition__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6457:1: ( rule__CompoundCondition__Group__3__Impl )
            // InternalOseeDsl.g:6458:2: rule__CompoundCondition__Group__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__CompoundCondition__Group__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group__3"


    // $ANTLR start "rule__CompoundCondition__Group__3__Impl"
    // InternalOseeDsl.g:6464:1: rule__CompoundCondition__Group__3__Impl : ( ')' ) ;
    public final void rule__CompoundCondition__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6468:1: ( ( ')' ) )
            // InternalOseeDsl.g:6469:1: ( ')' )
            {
            // InternalOseeDsl.g:6469:1: ( ')' )
            // InternalOseeDsl.g:6470:1: ')'
            {
             before(grammarAccess.getCompoundConditionAccess().getRightParenthesisKeyword_3()); 
            match(input,87,FOLLOW_2); 
             after(grammarAccess.getCompoundConditionAccess().getRightParenthesisKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group__3__Impl"


    // $ANTLR start "rule__CompoundCondition__Group_2__0"
    // InternalOseeDsl.g:6491:1: rule__CompoundCondition__Group_2__0 : rule__CompoundCondition__Group_2__0__Impl rule__CompoundCondition__Group_2__1 ;
    public final void rule__CompoundCondition__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6495:1: ( rule__CompoundCondition__Group_2__0__Impl rule__CompoundCondition__Group_2__1 )
            // InternalOseeDsl.g:6496:2: rule__CompoundCondition__Group_2__0__Impl rule__CompoundCondition__Group_2__1
            {
            pushFollow(FOLLOW_49);
            rule__CompoundCondition__Group_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__CompoundCondition__Group_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group_2__0"


    // $ANTLR start "rule__CompoundCondition__Group_2__0__Impl"
    // InternalOseeDsl.g:6503:1: rule__CompoundCondition__Group_2__0__Impl : ( ( rule__CompoundCondition__OperatorsAssignment_2_0 ) ) ;
    public final void rule__CompoundCondition__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6507:1: ( ( ( rule__CompoundCondition__OperatorsAssignment_2_0 ) ) )
            // InternalOseeDsl.g:6508:1: ( ( rule__CompoundCondition__OperatorsAssignment_2_0 ) )
            {
            // InternalOseeDsl.g:6508:1: ( ( rule__CompoundCondition__OperatorsAssignment_2_0 ) )
            // InternalOseeDsl.g:6509:1: ( rule__CompoundCondition__OperatorsAssignment_2_0 )
            {
             before(grammarAccess.getCompoundConditionAccess().getOperatorsAssignment_2_0()); 
            // InternalOseeDsl.g:6510:1: ( rule__CompoundCondition__OperatorsAssignment_2_0 )
            // InternalOseeDsl.g:6510:2: rule__CompoundCondition__OperatorsAssignment_2_0
            {
            pushFollow(FOLLOW_2);
            rule__CompoundCondition__OperatorsAssignment_2_0();

            state._fsp--;


            }

             after(grammarAccess.getCompoundConditionAccess().getOperatorsAssignment_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group_2__0__Impl"


    // $ANTLR start "rule__CompoundCondition__Group_2__1"
    // InternalOseeDsl.g:6520:1: rule__CompoundCondition__Group_2__1 : rule__CompoundCondition__Group_2__1__Impl ;
    public final void rule__CompoundCondition__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6524:1: ( rule__CompoundCondition__Group_2__1__Impl )
            // InternalOseeDsl.g:6525:2: rule__CompoundCondition__Group_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__CompoundCondition__Group_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group_2__1"


    // $ANTLR start "rule__CompoundCondition__Group_2__1__Impl"
    // InternalOseeDsl.g:6531:1: rule__CompoundCondition__Group_2__1__Impl : ( ( rule__CompoundCondition__ConditionsAssignment_2_1 ) ) ;
    public final void rule__CompoundCondition__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6535:1: ( ( ( rule__CompoundCondition__ConditionsAssignment_2_1 ) ) )
            // InternalOseeDsl.g:6536:1: ( ( rule__CompoundCondition__ConditionsAssignment_2_1 ) )
            {
            // InternalOseeDsl.g:6536:1: ( ( rule__CompoundCondition__ConditionsAssignment_2_1 ) )
            // InternalOseeDsl.g:6537:1: ( rule__CompoundCondition__ConditionsAssignment_2_1 )
            {
             before(grammarAccess.getCompoundConditionAccess().getConditionsAssignment_2_1()); 
            // InternalOseeDsl.g:6538:1: ( rule__CompoundCondition__ConditionsAssignment_2_1 )
            // InternalOseeDsl.g:6538:2: rule__CompoundCondition__ConditionsAssignment_2_1
            {
            pushFollow(FOLLOW_2);
            rule__CompoundCondition__ConditionsAssignment_2_1();

            state._fsp--;


            }

             after(grammarAccess.getCompoundConditionAccess().getConditionsAssignment_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__Group_2__1__Impl"


    // $ANTLR start "rule__XArtifactMatcher__Group__0"
    // InternalOseeDsl.g:6552:1: rule__XArtifactMatcher__Group__0 : rule__XArtifactMatcher__Group__0__Impl rule__XArtifactMatcher__Group__1 ;
    public final void rule__XArtifactMatcher__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6556:1: ( rule__XArtifactMatcher__Group__0__Impl rule__XArtifactMatcher__Group__1 )
            // InternalOseeDsl.g:6557:2: rule__XArtifactMatcher__Group__0__Impl rule__XArtifactMatcher__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__XArtifactMatcher__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__0"


    // $ANTLR start "rule__XArtifactMatcher__Group__0__Impl"
    // InternalOseeDsl.g:6564:1: rule__XArtifactMatcher__Group__0__Impl : ( 'artifactMatcher' ) ;
    public final void rule__XArtifactMatcher__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6568:1: ( ( 'artifactMatcher' ) )
            // InternalOseeDsl.g:6569:1: ( 'artifactMatcher' )
            {
            // InternalOseeDsl.g:6569:1: ( 'artifactMatcher' )
            // InternalOseeDsl.g:6570:1: 'artifactMatcher'
            {
             before(grammarAccess.getXArtifactMatcherAccess().getArtifactMatcherKeyword_0()); 
            match(input,88,FOLLOW_2); 
             after(grammarAccess.getXArtifactMatcherAccess().getArtifactMatcherKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__0__Impl"


    // $ANTLR start "rule__XArtifactMatcher__Group__1"
    // InternalOseeDsl.g:6583:1: rule__XArtifactMatcher__Group__1 : rule__XArtifactMatcher__Group__1__Impl rule__XArtifactMatcher__Group__2 ;
    public final void rule__XArtifactMatcher__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6587:1: ( rule__XArtifactMatcher__Group__1__Impl rule__XArtifactMatcher__Group__2 )
            // InternalOseeDsl.g:6588:2: rule__XArtifactMatcher__Group__1__Impl rule__XArtifactMatcher__Group__2
            {
            pushFollow(FOLLOW_53);
            rule__XArtifactMatcher__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__1"


    // $ANTLR start "rule__XArtifactMatcher__Group__1__Impl"
    // InternalOseeDsl.g:6595:1: rule__XArtifactMatcher__Group__1__Impl : ( ( rule__XArtifactMatcher__NameAssignment_1 ) ) ;
    public final void rule__XArtifactMatcher__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6599:1: ( ( ( rule__XArtifactMatcher__NameAssignment_1 ) ) )
            // InternalOseeDsl.g:6600:1: ( ( rule__XArtifactMatcher__NameAssignment_1 ) )
            {
            // InternalOseeDsl.g:6600:1: ( ( rule__XArtifactMatcher__NameAssignment_1 ) )
            // InternalOseeDsl.g:6601:1: ( rule__XArtifactMatcher__NameAssignment_1 )
            {
             before(grammarAccess.getXArtifactMatcherAccess().getNameAssignment_1()); 
            // InternalOseeDsl.g:6602:1: ( rule__XArtifactMatcher__NameAssignment_1 )
            // InternalOseeDsl.g:6602:2: rule__XArtifactMatcher__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactMatcherAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__1__Impl"


    // $ANTLR start "rule__XArtifactMatcher__Group__2"
    // InternalOseeDsl.g:6612:1: rule__XArtifactMatcher__Group__2 : rule__XArtifactMatcher__Group__2__Impl rule__XArtifactMatcher__Group__3 ;
    public final void rule__XArtifactMatcher__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6616:1: ( rule__XArtifactMatcher__Group__2__Impl rule__XArtifactMatcher__Group__3 )
            // InternalOseeDsl.g:6617:2: rule__XArtifactMatcher__Group__2__Impl rule__XArtifactMatcher__Group__3
            {
            pushFollow(FOLLOW_54);
            rule__XArtifactMatcher__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__2"


    // $ANTLR start "rule__XArtifactMatcher__Group__2__Impl"
    // InternalOseeDsl.g:6624:1: rule__XArtifactMatcher__Group__2__Impl : ( 'where' ) ;
    public final void rule__XArtifactMatcher__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6628:1: ( ( 'where' ) )
            // InternalOseeDsl.g:6629:1: ( 'where' )
            {
            // InternalOseeDsl.g:6629:1: ( 'where' )
            // InternalOseeDsl.g:6630:1: 'where'
            {
             before(grammarAccess.getXArtifactMatcherAccess().getWhereKeyword_2()); 
            match(input,89,FOLLOW_2); 
             after(grammarAccess.getXArtifactMatcherAccess().getWhereKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__2__Impl"


    // $ANTLR start "rule__XArtifactMatcher__Group__3"
    // InternalOseeDsl.g:6643:1: rule__XArtifactMatcher__Group__3 : rule__XArtifactMatcher__Group__3__Impl rule__XArtifactMatcher__Group__4 ;
    public final void rule__XArtifactMatcher__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6647:1: ( rule__XArtifactMatcher__Group__3__Impl rule__XArtifactMatcher__Group__4 )
            // InternalOseeDsl.g:6648:2: rule__XArtifactMatcher__Group__3__Impl rule__XArtifactMatcher__Group__4
            {
            pushFollow(FOLLOW_55);
            rule__XArtifactMatcher__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__3"


    // $ANTLR start "rule__XArtifactMatcher__Group__3__Impl"
    // InternalOseeDsl.g:6655:1: rule__XArtifactMatcher__Group__3__Impl : ( ( rule__XArtifactMatcher__ConditionsAssignment_3 ) ) ;
    public final void rule__XArtifactMatcher__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6659:1: ( ( ( rule__XArtifactMatcher__ConditionsAssignment_3 ) ) )
            // InternalOseeDsl.g:6660:1: ( ( rule__XArtifactMatcher__ConditionsAssignment_3 ) )
            {
            // InternalOseeDsl.g:6660:1: ( ( rule__XArtifactMatcher__ConditionsAssignment_3 ) )
            // InternalOseeDsl.g:6661:1: ( rule__XArtifactMatcher__ConditionsAssignment_3 )
            {
             before(grammarAccess.getXArtifactMatcherAccess().getConditionsAssignment_3()); 
            // InternalOseeDsl.g:6662:1: ( rule__XArtifactMatcher__ConditionsAssignment_3 )
            // InternalOseeDsl.g:6662:2: rule__XArtifactMatcher__ConditionsAssignment_3
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__ConditionsAssignment_3();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactMatcherAccess().getConditionsAssignment_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__3__Impl"


    // $ANTLR start "rule__XArtifactMatcher__Group__4"
    // InternalOseeDsl.g:6672:1: rule__XArtifactMatcher__Group__4 : rule__XArtifactMatcher__Group__4__Impl rule__XArtifactMatcher__Group__5 ;
    public final void rule__XArtifactMatcher__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6676:1: ( rule__XArtifactMatcher__Group__4__Impl rule__XArtifactMatcher__Group__5 )
            // InternalOseeDsl.g:6677:2: rule__XArtifactMatcher__Group__4__Impl rule__XArtifactMatcher__Group__5
            {
            pushFollow(FOLLOW_55);
            rule__XArtifactMatcher__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__4"


    // $ANTLR start "rule__XArtifactMatcher__Group__4__Impl"
    // InternalOseeDsl.g:6684:1: rule__XArtifactMatcher__Group__4__Impl : ( ( rule__XArtifactMatcher__Group_4__0 )* ) ;
    public final void rule__XArtifactMatcher__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6688:1: ( ( ( rule__XArtifactMatcher__Group_4__0 )* ) )
            // InternalOseeDsl.g:6689:1: ( ( rule__XArtifactMatcher__Group_4__0 )* )
            {
            // InternalOseeDsl.g:6689:1: ( ( rule__XArtifactMatcher__Group_4__0 )* )
            // InternalOseeDsl.g:6690:1: ( rule__XArtifactMatcher__Group_4__0 )*
            {
             before(grammarAccess.getXArtifactMatcherAccess().getGroup_4()); 
            // InternalOseeDsl.g:6691:1: ( rule__XArtifactMatcher__Group_4__0 )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( ((LA49_0>=39 && LA49_0<=40)) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // InternalOseeDsl.g:6691:2: rule__XArtifactMatcher__Group_4__0
            	    {
            	    pushFollow(FOLLOW_52);
            	    rule__XArtifactMatcher__Group_4__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop49;
                }
            } while (true);

             after(grammarAccess.getXArtifactMatcherAccess().getGroup_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__4__Impl"


    // $ANTLR start "rule__XArtifactMatcher__Group__5"
    // InternalOseeDsl.g:6701:1: rule__XArtifactMatcher__Group__5 : rule__XArtifactMatcher__Group__5__Impl ;
    public final void rule__XArtifactMatcher__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6705:1: ( rule__XArtifactMatcher__Group__5__Impl )
            // InternalOseeDsl.g:6706:2: rule__XArtifactMatcher__Group__5__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__Group__5__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__5"


    // $ANTLR start "rule__XArtifactMatcher__Group__5__Impl"
    // InternalOseeDsl.g:6712:1: rule__XArtifactMatcher__Group__5__Impl : ( ';' ) ;
    public final void rule__XArtifactMatcher__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6716:1: ( ( ';' ) )
            // InternalOseeDsl.g:6717:1: ( ';' )
            {
            // InternalOseeDsl.g:6717:1: ( ';' )
            // InternalOseeDsl.g:6718:1: ';'
            {
             before(grammarAccess.getXArtifactMatcherAccess().getSemicolonKeyword_5()); 
            match(input,90,FOLLOW_2); 
             after(grammarAccess.getXArtifactMatcherAccess().getSemicolonKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group__5__Impl"


    // $ANTLR start "rule__XArtifactMatcher__Group_4__0"
    // InternalOseeDsl.g:6743:1: rule__XArtifactMatcher__Group_4__0 : rule__XArtifactMatcher__Group_4__0__Impl rule__XArtifactMatcher__Group_4__1 ;
    public final void rule__XArtifactMatcher__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6747:1: ( rule__XArtifactMatcher__Group_4__0__Impl rule__XArtifactMatcher__Group_4__1 )
            // InternalOseeDsl.g:6748:2: rule__XArtifactMatcher__Group_4__0__Impl rule__XArtifactMatcher__Group_4__1
            {
            pushFollow(FOLLOW_54);
            rule__XArtifactMatcher__Group_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__Group_4__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group_4__0"


    // $ANTLR start "rule__XArtifactMatcher__Group_4__0__Impl"
    // InternalOseeDsl.g:6755:1: rule__XArtifactMatcher__Group_4__0__Impl : ( ( rule__XArtifactMatcher__OperatorsAssignment_4_0 ) ) ;
    public final void rule__XArtifactMatcher__Group_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6759:1: ( ( ( rule__XArtifactMatcher__OperatorsAssignment_4_0 ) ) )
            // InternalOseeDsl.g:6760:1: ( ( rule__XArtifactMatcher__OperatorsAssignment_4_0 ) )
            {
            // InternalOseeDsl.g:6760:1: ( ( rule__XArtifactMatcher__OperatorsAssignment_4_0 ) )
            // InternalOseeDsl.g:6761:1: ( rule__XArtifactMatcher__OperatorsAssignment_4_0 )
            {
             before(grammarAccess.getXArtifactMatcherAccess().getOperatorsAssignment_4_0()); 
            // InternalOseeDsl.g:6762:1: ( rule__XArtifactMatcher__OperatorsAssignment_4_0 )
            // InternalOseeDsl.g:6762:2: rule__XArtifactMatcher__OperatorsAssignment_4_0
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__OperatorsAssignment_4_0();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactMatcherAccess().getOperatorsAssignment_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group_4__0__Impl"


    // $ANTLR start "rule__XArtifactMatcher__Group_4__1"
    // InternalOseeDsl.g:6772:1: rule__XArtifactMatcher__Group_4__1 : rule__XArtifactMatcher__Group_4__1__Impl ;
    public final void rule__XArtifactMatcher__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6776:1: ( rule__XArtifactMatcher__Group_4__1__Impl )
            // InternalOseeDsl.g:6777:2: rule__XArtifactMatcher__Group_4__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__Group_4__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group_4__1"


    // $ANTLR start "rule__XArtifactMatcher__Group_4__1__Impl"
    // InternalOseeDsl.g:6783:1: rule__XArtifactMatcher__Group_4__1__Impl : ( ( rule__XArtifactMatcher__ConditionsAssignment_4_1 ) ) ;
    public final void rule__XArtifactMatcher__Group_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6787:1: ( ( ( rule__XArtifactMatcher__ConditionsAssignment_4_1 ) ) )
            // InternalOseeDsl.g:6788:1: ( ( rule__XArtifactMatcher__ConditionsAssignment_4_1 ) )
            {
            // InternalOseeDsl.g:6788:1: ( ( rule__XArtifactMatcher__ConditionsAssignment_4_1 ) )
            // InternalOseeDsl.g:6789:1: ( rule__XArtifactMatcher__ConditionsAssignment_4_1 )
            {
             before(grammarAccess.getXArtifactMatcherAccess().getConditionsAssignment_4_1()); 
            // InternalOseeDsl.g:6790:1: ( rule__XArtifactMatcher__ConditionsAssignment_4_1 )
            // InternalOseeDsl.g:6790:2: rule__XArtifactMatcher__ConditionsAssignment_4_1
            {
            pushFollow(FOLLOW_2);
            rule__XArtifactMatcher__ConditionsAssignment_4_1();

            state._fsp--;


            }

             after(grammarAccess.getXArtifactMatcherAccess().getConditionsAssignment_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__Group_4__1__Impl"


    // $ANTLR start "rule__Role__Group__0"
    // InternalOseeDsl.g:6804:1: rule__Role__Group__0 : rule__Role__Group__0__Impl rule__Role__Group__1 ;
    public final void rule__Role__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6808:1: ( rule__Role__Group__0__Impl rule__Role__Group__1 )
            // InternalOseeDsl.g:6809:2: rule__Role__Group__0__Impl rule__Role__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__Role__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Role__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__0"


    // $ANTLR start "rule__Role__Group__0__Impl"
    // InternalOseeDsl.g:6816:1: rule__Role__Group__0__Impl : ( 'role' ) ;
    public final void rule__Role__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6820:1: ( ( 'role' ) )
            // InternalOseeDsl.g:6821:1: ( 'role' )
            {
            // InternalOseeDsl.g:6821:1: ( 'role' )
            // InternalOseeDsl.g:6822:1: 'role'
            {
             before(grammarAccess.getRoleAccess().getRoleKeyword_0()); 
            match(input,91,FOLLOW_2); 
             after(grammarAccess.getRoleAccess().getRoleKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__0__Impl"


    // $ANTLR start "rule__Role__Group__1"
    // InternalOseeDsl.g:6835:1: rule__Role__Group__1 : rule__Role__Group__1__Impl rule__Role__Group__2 ;
    public final void rule__Role__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6839:1: ( rule__Role__Group__1__Impl rule__Role__Group__2 )
            // InternalOseeDsl.g:6840:2: rule__Role__Group__1__Impl rule__Role__Group__2
            {
            pushFollow(FOLLOW_12);
            rule__Role__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Role__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__1"


    // $ANTLR start "rule__Role__Group__1__Impl"
    // InternalOseeDsl.g:6847:1: rule__Role__Group__1__Impl : ( ( rule__Role__NameAssignment_1 ) ) ;
    public final void rule__Role__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6851:1: ( ( ( rule__Role__NameAssignment_1 ) ) )
            // InternalOseeDsl.g:6852:1: ( ( rule__Role__NameAssignment_1 ) )
            {
            // InternalOseeDsl.g:6852:1: ( ( rule__Role__NameAssignment_1 ) )
            // InternalOseeDsl.g:6853:1: ( rule__Role__NameAssignment_1 )
            {
             before(grammarAccess.getRoleAccess().getNameAssignment_1()); 
            // InternalOseeDsl.g:6854:1: ( rule__Role__NameAssignment_1 )
            // InternalOseeDsl.g:6854:2: rule__Role__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__Role__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getRoleAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__1__Impl"


    // $ANTLR start "rule__Role__Group__2"
    // InternalOseeDsl.g:6864:1: rule__Role__Group__2 : rule__Role__Group__2__Impl rule__Role__Group__3 ;
    public final void rule__Role__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6868:1: ( rule__Role__Group__2__Impl rule__Role__Group__3 )
            // InternalOseeDsl.g:6869:2: rule__Role__Group__2__Impl rule__Role__Group__3
            {
            pushFollow(FOLLOW_12);
            rule__Role__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Role__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__2"


    // $ANTLR start "rule__Role__Group__2__Impl"
    // InternalOseeDsl.g:6876:1: rule__Role__Group__2__Impl : ( ( rule__Role__Group_2__0 )? ) ;
    public final void rule__Role__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6880:1: ( ( ( rule__Role__Group_2__0 )? ) )
            // InternalOseeDsl.g:6881:1: ( ( rule__Role__Group_2__0 )? )
            {
            // InternalOseeDsl.g:6881:1: ( ( rule__Role__Group_2__0 )? )
            // InternalOseeDsl.g:6882:1: ( rule__Role__Group_2__0 )?
            {
             before(grammarAccess.getRoleAccess().getGroup_2()); 
            // InternalOseeDsl.g:6883:1: ( rule__Role__Group_2__0 )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==56) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // InternalOseeDsl.g:6883:2: rule__Role__Group_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__Role__Group_2__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getRoleAccess().getGroup_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__2__Impl"


    // $ANTLR start "rule__Role__Group__3"
    // InternalOseeDsl.g:6893:1: rule__Role__Group__3 : rule__Role__Group__3__Impl rule__Role__Group__4 ;
    public final void rule__Role__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6897:1: ( rule__Role__Group__3__Impl rule__Role__Group__4 )
            // InternalOseeDsl.g:6898:2: rule__Role__Group__3__Impl rule__Role__Group__4
            {
            pushFollow(FOLLOW_56);
            rule__Role__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Role__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__3"


    // $ANTLR start "rule__Role__Group__3__Impl"
    // InternalOseeDsl.g:6905:1: rule__Role__Group__3__Impl : ( '{' ) ;
    public final void rule__Role__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6909:1: ( ( '{' ) )
            // InternalOseeDsl.g:6910:1: ( '{' )
            {
            // InternalOseeDsl.g:6910:1: ( '{' )
            // InternalOseeDsl.g:6911:1: '{'
            {
             before(grammarAccess.getRoleAccess().getLeftCurlyBracketKeyword_3()); 
            match(input,53,FOLLOW_2); 
             after(grammarAccess.getRoleAccess().getLeftCurlyBracketKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__3__Impl"


    // $ANTLR start "rule__Role__Group__4"
    // InternalOseeDsl.g:6924:1: rule__Role__Group__4 : rule__Role__Group__4__Impl rule__Role__Group__5 ;
    public final void rule__Role__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6928:1: ( rule__Role__Group__4__Impl rule__Role__Group__5 )
            // InternalOseeDsl.g:6929:2: rule__Role__Group__4__Impl rule__Role__Group__5
            {
            pushFollow(FOLLOW_28);
            rule__Role__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Role__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__4"


    // $ANTLR start "rule__Role__Group__4__Impl"
    // InternalOseeDsl.g:6936:1: rule__Role__Group__4__Impl : ( ( ( rule__Role__Alternatives_4 ) ) ( ( rule__Role__Alternatives_4 )* ) ) ;
    public final void rule__Role__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6940:1: ( ( ( ( rule__Role__Alternatives_4 ) ) ( ( rule__Role__Alternatives_4 )* ) ) )
            // InternalOseeDsl.g:6941:1: ( ( ( rule__Role__Alternatives_4 ) ) ( ( rule__Role__Alternatives_4 )* ) )
            {
            // InternalOseeDsl.g:6941:1: ( ( ( rule__Role__Alternatives_4 ) ) ( ( rule__Role__Alternatives_4 )* ) )
            // InternalOseeDsl.g:6942:1: ( ( rule__Role__Alternatives_4 ) ) ( ( rule__Role__Alternatives_4 )* )
            {
            // InternalOseeDsl.g:6942:1: ( ( rule__Role__Alternatives_4 ) )
            // InternalOseeDsl.g:6943:1: ( rule__Role__Alternatives_4 )
            {
             before(grammarAccess.getRoleAccess().getAlternatives_4()); 
            // InternalOseeDsl.g:6944:1: ( rule__Role__Alternatives_4 )
            // InternalOseeDsl.g:6944:2: rule__Role__Alternatives_4
            {
            pushFollow(FOLLOW_57);
            rule__Role__Alternatives_4();

            state._fsp--;


            }

             after(grammarAccess.getRoleAccess().getAlternatives_4()); 

            }

            // InternalOseeDsl.g:6947:1: ( ( rule__Role__Alternatives_4 )* )
            // InternalOseeDsl.g:6948:1: ( rule__Role__Alternatives_4 )*
            {
             before(grammarAccess.getRoleAccess().getAlternatives_4()); 
            // InternalOseeDsl.g:6949:1: ( rule__Role__Alternatives_4 )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==58||LA51_0==92) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // InternalOseeDsl.g:6949:2: rule__Role__Alternatives_4
            	    {
            	    pushFollow(FOLLOW_57);
            	    rule__Role__Alternatives_4();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);

             after(grammarAccess.getRoleAccess().getAlternatives_4()); 

            }


            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__4__Impl"


    // $ANTLR start "rule__Role__Group__5"
    // InternalOseeDsl.g:6960:1: rule__Role__Group__5 : rule__Role__Group__5__Impl ;
    public final void rule__Role__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6964:1: ( rule__Role__Group__5__Impl )
            // InternalOseeDsl.g:6965:2: rule__Role__Group__5__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Role__Group__5__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__5"


    // $ANTLR start "rule__Role__Group__5__Impl"
    // InternalOseeDsl.g:6971:1: rule__Role__Group__5__Impl : ( '}' ) ;
    public final void rule__Role__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:6975:1: ( ( '}' ) )
            // InternalOseeDsl.g:6976:1: ( '}' )
            {
            // InternalOseeDsl.g:6976:1: ( '}' )
            // InternalOseeDsl.g:6977:1: '}'
            {
             before(grammarAccess.getRoleAccess().getRightCurlyBracketKeyword_5()); 
            match(input,55,FOLLOW_2); 
             after(grammarAccess.getRoleAccess().getRightCurlyBracketKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group__5__Impl"


    // $ANTLR start "rule__Role__Group_2__0"
    // InternalOseeDsl.g:7002:1: rule__Role__Group_2__0 : rule__Role__Group_2__0__Impl rule__Role__Group_2__1 ;
    public final void rule__Role__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7006:1: ( rule__Role__Group_2__0__Impl rule__Role__Group_2__1 )
            // InternalOseeDsl.g:7007:2: rule__Role__Group_2__0__Impl rule__Role__Group_2__1
            {
            pushFollow(FOLLOW_7);
            rule__Role__Group_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Role__Group_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group_2__0"


    // $ANTLR start "rule__Role__Group_2__0__Impl"
    // InternalOseeDsl.g:7014:1: rule__Role__Group_2__0__Impl : ( 'extends' ) ;
    public final void rule__Role__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7018:1: ( ( 'extends' ) )
            // InternalOseeDsl.g:7019:1: ( 'extends' )
            {
            // InternalOseeDsl.g:7019:1: ( 'extends' )
            // InternalOseeDsl.g:7020:1: 'extends'
            {
             before(grammarAccess.getRoleAccess().getExtendsKeyword_2_0()); 
            match(input,56,FOLLOW_2); 
             after(grammarAccess.getRoleAccess().getExtendsKeyword_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group_2__0__Impl"


    // $ANTLR start "rule__Role__Group_2__1"
    // InternalOseeDsl.g:7033:1: rule__Role__Group_2__1 : rule__Role__Group_2__1__Impl ;
    public final void rule__Role__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7037:1: ( rule__Role__Group_2__1__Impl )
            // InternalOseeDsl.g:7038:2: rule__Role__Group_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Role__Group_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group_2__1"


    // $ANTLR start "rule__Role__Group_2__1__Impl"
    // InternalOseeDsl.g:7044:1: rule__Role__Group_2__1__Impl : ( ( rule__Role__SuperRolesAssignment_2_1 ) ) ;
    public final void rule__Role__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7048:1: ( ( ( rule__Role__SuperRolesAssignment_2_1 ) ) )
            // InternalOseeDsl.g:7049:1: ( ( rule__Role__SuperRolesAssignment_2_1 ) )
            {
            // InternalOseeDsl.g:7049:1: ( ( rule__Role__SuperRolesAssignment_2_1 ) )
            // InternalOseeDsl.g:7050:1: ( rule__Role__SuperRolesAssignment_2_1 )
            {
             before(grammarAccess.getRoleAccess().getSuperRolesAssignment_2_1()); 
            // InternalOseeDsl.g:7051:1: ( rule__Role__SuperRolesAssignment_2_1 )
            // InternalOseeDsl.g:7051:2: rule__Role__SuperRolesAssignment_2_1
            {
            pushFollow(FOLLOW_2);
            rule__Role__SuperRolesAssignment_2_1();

            state._fsp--;


            }

             after(grammarAccess.getRoleAccess().getSuperRolesAssignment_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__Group_2__1__Impl"


    // $ANTLR start "rule__ReferencedContext__Group__0"
    // InternalOseeDsl.g:7065:1: rule__ReferencedContext__Group__0 : rule__ReferencedContext__Group__0__Impl rule__ReferencedContext__Group__1 ;
    public final void rule__ReferencedContext__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7069:1: ( rule__ReferencedContext__Group__0__Impl rule__ReferencedContext__Group__1 )
            // InternalOseeDsl.g:7070:2: rule__ReferencedContext__Group__0__Impl rule__ReferencedContext__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__ReferencedContext__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ReferencedContext__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ReferencedContext__Group__0"


    // $ANTLR start "rule__ReferencedContext__Group__0__Impl"
    // InternalOseeDsl.g:7077:1: rule__ReferencedContext__Group__0__Impl : ( 'accessContext' ) ;
    public final void rule__ReferencedContext__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7081:1: ( ( 'accessContext' ) )
            // InternalOseeDsl.g:7082:1: ( 'accessContext' )
            {
            // InternalOseeDsl.g:7082:1: ( 'accessContext' )
            // InternalOseeDsl.g:7083:1: 'accessContext'
            {
             before(grammarAccess.getReferencedContextAccess().getAccessContextKeyword_0()); 
            match(input,92,FOLLOW_2); 
             after(grammarAccess.getReferencedContextAccess().getAccessContextKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ReferencedContext__Group__0__Impl"


    // $ANTLR start "rule__ReferencedContext__Group__1"
    // InternalOseeDsl.g:7096:1: rule__ReferencedContext__Group__1 : rule__ReferencedContext__Group__1__Impl rule__ReferencedContext__Group__2 ;
    public final void rule__ReferencedContext__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7100:1: ( rule__ReferencedContext__Group__1__Impl rule__ReferencedContext__Group__2 )
            // InternalOseeDsl.g:7101:2: rule__ReferencedContext__Group__1__Impl rule__ReferencedContext__Group__2
            {
            pushFollow(FOLLOW_58);
            rule__ReferencedContext__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ReferencedContext__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ReferencedContext__Group__1"


    // $ANTLR start "rule__ReferencedContext__Group__1__Impl"
    // InternalOseeDsl.g:7108:1: rule__ReferencedContext__Group__1__Impl : ( ( rule__ReferencedContext__AccessContextRefAssignment_1 ) ) ;
    public final void rule__ReferencedContext__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7112:1: ( ( ( rule__ReferencedContext__AccessContextRefAssignment_1 ) ) )
            // InternalOseeDsl.g:7113:1: ( ( rule__ReferencedContext__AccessContextRefAssignment_1 ) )
            {
            // InternalOseeDsl.g:7113:1: ( ( rule__ReferencedContext__AccessContextRefAssignment_1 ) )
            // InternalOseeDsl.g:7114:1: ( rule__ReferencedContext__AccessContextRefAssignment_1 )
            {
             before(grammarAccess.getReferencedContextAccess().getAccessContextRefAssignment_1()); 
            // InternalOseeDsl.g:7115:1: ( rule__ReferencedContext__AccessContextRefAssignment_1 )
            // InternalOseeDsl.g:7115:2: rule__ReferencedContext__AccessContextRefAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__ReferencedContext__AccessContextRefAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getReferencedContextAccess().getAccessContextRefAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ReferencedContext__Group__1__Impl"


    // $ANTLR start "rule__ReferencedContext__Group__2"
    // InternalOseeDsl.g:7125:1: rule__ReferencedContext__Group__2 : rule__ReferencedContext__Group__2__Impl ;
    public final void rule__ReferencedContext__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7129:1: ( rule__ReferencedContext__Group__2__Impl )
            // InternalOseeDsl.g:7130:2: rule__ReferencedContext__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ReferencedContext__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ReferencedContext__Group__2"


    // $ANTLR start "rule__ReferencedContext__Group__2__Impl"
    // InternalOseeDsl.g:7136:1: rule__ReferencedContext__Group__2__Impl : ( ';' ) ;
    public final void rule__ReferencedContext__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7140:1: ( ( ';' ) )
            // InternalOseeDsl.g:7141:1: ( ';' )
            {
            // InternalOseeDsl.g:7141:1: ( ';' )
            // InternalOseeDsl.g:7142:1: ';'
            {
             before(grammarAccess.getReferencedContextAccess().getSemicolonKeyword_2()); 
            match(input,90,FOLLOW_2); 
             after(grammarAccess.getReferencedContextAccess().getSemicolonKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ReferencedContext__Group__2__Impl"


    // $ANTLR start "rule__UsersAndGroups__Group__0"
    // InternalOseeDsl.g:7161:1: rule__UsersAndGroups__Group__0 : rule__UsersAndGroups__Group__0__Impl rule__UsersAndGroups__Group__1 ;
    public final void rule__UsersAndGroups__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7165:1: ( rule__UsersAndGroups__Group__0__Impl rule__UsersAndGroups__Group__1 )
            // InternalOseeDsl.g:7166:2: rule__UsersAndGroups__Group__0__Impl rule__UsersAndGroups__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__UsersAndGroups__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__UsersAndGroups__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UsersAndGroups__Group__0"


    // $ANTLR start "rule__UsersAndGroups__Group__0__Impl"
    // InternalOseeDsl.g:7173:1: rule__UsersAndGroups__Group__0__Impl : ( 'guid' ) ;
    public final void rule__UsersAndGroups__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7177:1: ( ( 'guid' ) )
            // InternalOseeDsl.g:7178:1: ( 'guid' )
            {
            // InternalOseeDsl.g:7178:1: ( 'guid' )
            // InternalOseeDsl.g:7179:1: 'guid'
            {
             before(grammarAccess.getUsersAndGroupsAccess().getGuidKeyword_0()); 
            match(input,58,FOLLOW_2); 
             after(grammarAccess.getUsersAndGroupsAccess().getGuidKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UsersAndGroups__Group__0__Impl"


    // $ANTLR start "rule__UsersAndGroups__Group__1"
    // InternalOseeDsl.g:7192:1: rule__UsersAndGroups__Group__1 : rule__UsersAndGroups__Group__1__Impl rule__UsersAndGroups__Group__2 ;
    public final void rule__UsersAndGroups__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7196:1: ( rule__UsersAndGroups__Group__1__Impl rule__UsersAndGroups__Group__2 )
            // InternalOseeDsl.g:7197:2: rule__UsersAndGroups__Group__1__Impl rule__UsersAndGroups__Group__2
            {
            pushFollow(FOLLOW_58);
            rule__UsersAndGroups__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__UsersAndGroups__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UsersAndGroups__Group__1"


    // $ANTLR start "rule__UsersAndGroups__Group__1__Impl"
    // InternalOseeDsl.g:7204:1: rule__UsersAndGroups__Group__1__Impl : ( ( rule__UsersAndGroups__UserOrGroupGuidAssignment_1 ) ) ;
    public final void rule__UsersAndGroups__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7208:1: ( ( ( rule__UsersAndGroups__UserOrGroupGuidAssignment_1 ) ) )
            // InternalOseeDsl.g:7209:1: ( ( rule__UsersAndGroups__UserOrGroupGuidAssignment_1 ) )
            {
            // InternalOseeDsl.g:7209:1: ( ( rule__UsersAndGroups__UserOrGroupGuidAssignment_1 ) )
            // InternalOseeDsl.g:7210:1: ( rule__UsersAndGroups__UserOrGroupGuidAssignment_1 )
            {
             before(grammarAccess.getUsersAndGroupsAccess().getUserOrGroupGuidAssignment_1()); 
            // InternalOseeDsl.g:7211:1: ( rule__UsersAndGroups__UserOrGroupGuidAssignment_1 )
            // InternalOseeDsl.g:7211:2: rule__UsersAndGroups__UserOrGroupGuidAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__UsersAndGroups__UserOrGroupGuidAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getUsersAndGroupsAccess().getUserOrGroupGuidAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UsersAndGroups__Group__1__Impl"


    // $ANTLR start "rule__UsersAndGroups__Group__2"
    // InternalOseeDsl.g:7221:1: rule__UsersAndGroups__Group__2 : rule__UsersAndGroups__Group__2__Impl ;
    public final void rule__UsersAndGroups__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7225:1: ( rule__UsersAndGroups__Group__2__Impl )
            // InternalOseeDsl.g:7226:2: rule__UsersAndGroups__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__UsersAndGroups__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UsersAndGroups__Group__2"


    // $ANTLR start "rule__UsersAndGroups__Group__2__Impl"
    // InternalOseeDsl.g:7232:1: rule__UsersAndGroups__Group__2__Impl : ( ';' ) ;
    public final void rule__UsersAndGroups__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7236:1: ( ( ';' ) )
            // InternalOseeDsl.g:7237:1: ( ';' )
            {
            // InternalOseeDsl.g:7237:1: ( ';' )
            // InternalOseeDsl.g:7238:1: ';'
            {
             before(grammarAccess.getUsersAndGroupsAccess().getSemicolonKeyword_2()); 
            match(input,90,FOLLOW_2); 
             after(grammarAccess.getUsersAndGroupsAccess().getSemicolonKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UsersAndGroups__Group__2__Impl"


    // $ANTLR start "rule__AccessContext__Group__0"
    // InternalOseeDsl.g:7257:1: rule__AccessContext__Group__0 : rule__AccessContext__Group__0__Impl rule__AccessContext__Group__1 ;
    public final void rule__AccessContext__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7261:1: ( rule__AccessContext__Group__0__Impl rule__AccessContext__Group__1 )
            // InternalOseeDsl.g:7262:2: rule__AccessContext__Group__0__Impl rule__AccessContext__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__AccessContext__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AccessContext__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__0"


    // $ANTLR start "rule__AccessContext__Group__0__Impl"
    // InternalOseeDsl.g:7269:1: rule__AccessContext__Group__0__Impl : ( 'accessContext' ) ;
    public final void rule__AccessContext__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7273:1: ( ( 'accessContext' ) )
            // InternalOseeDsl.g:7274:1: ( 'accessContext' )
            {
            // InternalOseeDsl.g:7274:1: ( 'accessContext' )
            // InternalOseeDsl.g:7275:1: 'accessContext'
            {
             before(grammarAccess.getAccessContextAccess().getAccessContextKeyword_0()); 
            match(input,92,FOLLOW_2); 
             after(grammarAccess.getAccessContextAccess().getAccessContextKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__0__Impl"


    // $ANTLR start "rule__AccessContext__Group__1"
    // InternalOseeDsl.g:7288:1: rule__AccessContext__Group__1 : rule__AccessContext__Group__1__Impl rule__AccessContext__Group__2 ;
    public final void rule__AccessContext__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7292:1: ( rule__AccessContext__Group__1__Impl rule__AccessContext__Group__2 )
            // InternalOseeDsl.g:7293:2: rule__AccessContext__Group__1__Impl rule__AccessContext__Group__2
            {
            pushFollow(FOLLOW_12);
            rule__AccessContext__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AccessContext__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__1"


    // $ANTLR start "rule__AccessContext__Group__1__Impl"
    // InternalOseeDsl.g:7300:1: rule__AccessContext__Group__1__Impl : ( ( rule__AccessContext__NameAssignment_1 ) ) ;
    public final void rule__AccessContext__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7304:1: ( ( ( rule__AccessContext__NameAssignment_1 ) ) )
            // InternalOseeDsl.g:7305:1: ( ( rule__AccessContext__NameAssignment_1 ) )
            {
            // InternalOseeDsl.g:7305:1: ( ( rule__AccessContext__NameAssignment_1 ) )
            // InternalOseeDsl.g:7306:1: ( rule__AccessContext__NameAssignment_1 )
            {
             before(grammarAccess.getAccessContextAccess().getNameAssignment_1()); 
            // InternalOseeDsl.g:7307:1: ( rule__AccessContext__NameAssignment_1 )
            // InternalOseeDsl.g:7307:2: rule__AccessContext__NameAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__AccessContext__NameAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getAccessContextAccess().getNameAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__1__Impl"


    // $ANTLR start "rule__AccessContext__Group__2"
    // InternalOseeDsl.g:7317:1: rule__AccessContext__Group__2 : rule__AccessContext__Group__2__Impl rule__AccessContext__Group__3 ;
    public final void rule__AccessContext__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7321:1: ( rule__AccessContext__Group__2__Impl rule__AccessContext__Group__3 )
            // InternalOseeDsl.g:7322:2: rule__AccessContext__Group__2__Impl rule__AccessContext__Group__3
            {
            pushFollow(FOLLOW_12);
            rule__AccessContext__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AccessContext__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__2"


    // $ANTLR start "rule__AccessContext__Group__2__Impl"
    // InternalOseeDsl.g:7329:1: rule__AccessContext__Group__2__Impl : ( ( rule__AccessContext__Group_2__0 )? ) ;
    public final void rule__AccessContext__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7333:1: ( ( ( rule__AccessContext__Group_2__0 )? ) )
            // InternalOseeDsl.g:7334:1: ( ( rule__AccessContext__Group_2__0 )? )
            {
            // InternalOseeDsl.g:7334:1: ( ( rule__AccessContext__Group_2__0 )? )
            // InternalOseeDsl.g:7335:1: ( rule__AccessContext__Group_2__0 )?
            {
             before(grammarAccess.getAccessContextAccess().getGroup_2()); 
            // InternalOseeDsl.g:7336:1: ( rule__AccessContext__Group_2__0 )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==56) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // InternalOseeDsl.g:7336:2: rule__AccessContext__Group_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__AccessContext__Group_2__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAccessContextAccess().getGroup_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__2__Impl"


    // $ANTLR start "rule__AccessContext__Group__3"
    // InternalOseeDsl.g:7346:1: rule__AccessContext__Group__3 : rule__AccessContext__Group__3__Impl rule__AccessContext__Group__4 ;
    public final void rule__AccessContext__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7350:1: ( rule__AccessContext__Group__3__Impl rule__AccessContext__Group__4 )
            // InternalOseeDsl.g:7351:2: rule__AccessContext__Group__3__Impl rule__AccessContext__Group__4
            {
            pushFollow(FOLLOW_59);
            rule__AccessContext__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AccessContext__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__3"


    // $ANTLR start "rule__AccessContext__Group__3__Impl"
    // InternalOseeDsl.g:7358:1: rule__AccessContext__Group__3__Impl : ( '{' ) ;
    public final void rule__AccessContext__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7362:1: ( ( '{' ) )
            // InternalOseeDsl.g:7363:1: ( '{' )
            {
            // InternalOseeDsl.g:7363:1: ( '{' )
            // InternalOseeDsl.g:7364:1: '{'
            {
             before(grammarAccess.getAccessContextAccess().getLeftCurlyBracketKeyword_3()); 
            match(input,53,FOLLOW_2); 
             after(grammarAccess.getAccessContextAccess().getLeftCurlyBracketKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__3__Impl"


    // $ANTLR start "rule__AccessContext__Group__4"
    // InternalOseeDsl.g:7377:1: rule__AccessContext__Group__4 : rule__AccessContext__Group__4__Impl rule__AccessContext__Group__5 ;
    public final void rule__AccessContext__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7381:1: ( rule__AccessContext__Group__4__Impl rule__AccessContext__Group__5 )
            // InternalOseeDsl.g:7382:2: rule__AccessContext__Group__4__Impl rule__AccessContext__Group__5
            {
            pushFollow(FOLLOW_7);
            rule__AccessContext__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AccessContext__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__4"


    // $ANTLR start "rule__AccessContext__Group__4__Impl"
    // InternalOseeDsl.g:7389:1: rule__AccessContext__Group__4__Impl : ( 'guid' ) ;
    public final void rule__AccessContext__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7393:1: ( ( 'guid' ) )
            // InternalOseeDsl.g:7394:1: ( 'guid' )
            {
            // InternalOseeDsl.g:7394:1: ( 'guid' )
            // InternalOseeDsl.g:7395:1: 'guid'
            {
             before(grammarAccess.getAccessContextAccess().getGuidKeyword_4()); 
            match(input,58,FOLLOW_2); 
             after(grammarAccess.getAccessContextAccess().getGuidKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__4__Impl"


    // $ANTLR start "rule__AccessContext__Group__5"
    // InternalOseeDsl.g:7408:1: rule__AccessContext__Group__5 : rule__AccessContext__Group__5__Impl rule__AccessContext__Group__6 ;
    public final void rule__AccessContext__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7412:1: ( rule__AccessContext__Group__5__Impl rule__AccessContext__Group__6 )
            // InternalOseeDsl.g:7413:2: rule__AccessContext__Group__5__Impl rule__AccessContext__Group__6
            {
            pushFollow(FOLLOW_58);
            rule__AccessContext__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AccessContext__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__5"


    // $ANTLR start "rule__AccessContext__Group__5__Impl"
    // InternalOseeDsl.g:7420:1: rule__AccessContext__Group__5__Impl : ( ( rule__AccessContext__GuidAssignment_5 ) ) ;
    public final void rule__AccessContext__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7424:1: ( ( ( rule__AccessContext__GuidAssignment_5 ) ) )
            // InternalOseeDsl.g:7425:1: ( ( rule__AccessContext__GuidAssignment_5 ) )
            {
            // InternalOseeDsl.g:7425:1: ( ( rule__AccessContext__GuidAssignment_5 ) )
            // InternalOseeDsl.g:7426:1: ( rule__AccessContext__GuidAssignment_5 )
            {
             before(grammarAccess.getAccessContextAccess().getGuidAssignment_5()); 
            // InternalOseeDsl.g:7427:1: ( rule__AccessContext__GuidAssignment_5 )
            // InternalOseeDsl.g:7427:2: rule__AccessContext__GuidAssignment_5
            {
            pushFollow(FOLLOW_2);
            rule__AccessContext__GuidAssignment_5();

            state._fsp--;


            }

             after(grammarAccess.getAccessContextAccess().getGuidAssignment_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__5__Impl"


    // $ANTLR start "rule__AccessContext__Group__6"
    // InternalOseeDsl.g:7437:1: rule__AccessContext__Group__6 : rule__AccessContext__Group__6__Impl rule__AccessContext__Group__7 ;
    public final void rule__AccessContext__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7441:1: ( rule__AccessContext__Group__6__Impl rule__AccessContext__Group__7 )
            // InternalOseeDsl.g:7442:2: rule__AccessContext__Group__6__Impl rule__AccessContext__Group__7
            {
            pushFollow(FOLLOW_60);
            rule__AccessContext__Group__6__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AccessContext__Group__7();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__6"


    // $ANTLR start "rule__AccessContext__Group__6__Impl"
    // InternalOseeDsl.g:7449:1: rule__AccessContext__Group__6__Impl : ( ';' ) ;
    public final void rule__AccessContext__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7453:1: ( ( ';' ) )
            // InternalOseeDsl.g:7454:1: ( ';' )
            {
            // InternalOseeDsl.g:7454:1: ( ';' )
            // InternalOseeDsl.g:7455:1: ';'
            {
             before(grammarAccess.getAccessContextAccess().getSemicolonKeyword_6()); 
            match(input,90,FOLLOW_2); 
             after(grammarAccess.getAccessContextAccess().getSemicolonKeyword_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__6__Impl"


    // $ANTLR start "rule__AccessContext__Group__7"
    // InternalOseeDsl.g:7468:1: rule__AccessContext__Group__7 : rule__AccessContext__Group__7__Impl rule__AccessContext__Group__8 ;
    public final void rule__AccessContext__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7472:1: ( rule__AccessContext__Group__7__Impl rule__AccessContext__Group__8 )
            // InternalOseeDsl.g:7473:2: rule__AccessContext__Group__7__Impl rule__AccessContext__Group__8
            {
            pushFollow(FOLLOW_28);
            rule__AccessContext__Group__7__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AccessContext__Group__8();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__7"


    // $ANTLR start "rule__AccessContext__Group__7__Impl"
    // InternalOseeDsl.g:7480:1: rule__AccessContext__Group__7__Impl : ( ( ( rule__AccessContext__Alternatives_7 ) ) ( ( rule__AccessContext__Alternatives_7 )* ) ) ;
    public final void rule__AccessContext__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7484:1: ( ( ( ( rule__AccessContext__Alternatives_7 ) ) ( ( rule__AccessContext__Alternatives_7 )* ) ) )
            // InternalOseeDsl.g:7485:1: ( ( ( rule__AccessContext__Alternatives_7 ) ) ( ( rule__AccessContext__Alternatives_7 )* ) )
            {
            // InternalOseeDsl.g:7485:1: ( ( ( rule__AccessContext__Alternatives_7 ) ) ( ( rule__AccessContext__Alternatives_7 )* ) )
            // InternalOseeDsl.g:7486:1: ( ( rule__AccessContext__Alternatives_7 ) ) ( ( rule__AccessContext__Alternatives_7 )* )
            {
            // InternalOseeDsl.g:7486:1: ( ( rule__AccessContext__Alternatives_7 ) )
            // InternalOseeDsl.g:7487:1: ( rule__AccessContext__Alternatives_7 )
            {
             before(grammarAccess.getAccessContextAccess().getAlternatives_7()); 
            // InternalOseeDsl.g:7488:1: ( rule__AccessContext__Alternatives_7 )
            // InternalOseeDsl.g:7488:2: rule__AccessContext__Alternatives_7
            {
            pushFollow(FOLLOW_61);
            rule__AccessContext__Alternatives_7();

            state._fsp--;


            }

             after(grammarAccess.getAccessContextAccess().getAlternatives_7()); 

            }

            // InternalOseeDsl.g:7491:1: ( ( rule__AccessContext__Alternatives_7 )* )
            // InternalOseeDsl.g:7492:1: ( rule__AccessContext__Alternatives_7 )*
            {
             before(grammarAccess.getAccessContextAccess().getAlternatives_7()); 
            // InternalOseeDsl.g:7493:1: ( rule__AccessContext__Alternatives_7 )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( ((LA53_0>=45 && LA53_0<=46)||LA53_0==93) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // InternalOseeDsl.g:7493:2: rule__AccessContext__Alternatives_7
            	    {
            	    pushFollow(FOLLOW_61);
            	    rule__AccessContext__Alternatives_7();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop53;
                }
            } while (true);

             after(grammarAccess.getAccessContextAccess().getAlternatives_7()); 

            }


            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__7__Impl"


    // $ANTLR start "rule__AccessContext__Group__8"
    // InternalOseeDsl.g:7504:1: rule__AccessContext__Group__8 : rule__AccessContext__Group__8__Impl ;
    public final void rule__AccessContext__Group__8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7508:1: ( rule__AccessContext__Group__8__Impl )
            // InternalOseeDsl.g:7509:2: rule__AccessContext__Group__8__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AccessContext__Group__8__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__8"


    // $ANTLR start "rule__AccessContext__Group__8__Impl"
    // InternalOseeDsl.g:7515:1: rule__AccessContext__Group__8__Impl : ( '}' ) ;
    public final void rule__AccessContext__Group__8__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7519:1: ( ( '}' ) )
            // InternalOseeDsl.g:7520:1: ( '}' )
            {
            // InternalOseeDsl.g:7520:1: ( '}' )
            // InternalOseeDsl.g:7521:1: '}'
            {
             before(grammarAccess.getAccessContextAccess().getRightCurlyBracketKeyword_8()); 
            match(input,55,FOLLOW_2); 
             after(grammarAccess.getAccessContextAccess().getRightCurlyBracketKeyword_8()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group__8__Impl"


    // $ANTLR start "rule__AccessContext__Group_2__0"
    // InternalOseeDsl.g:7552:1: rule__AccessContext__Group_2__0 : rule__AccessContext__Group_2__0__Impl rule__AccessContext__Group_2__1 ;
    public final void rule__AccessContext__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7556:1: ( rule__AccessContext__Group_2__0__Impl rule__AccessContext__Group_2__1 )
            // InternalOseeDsl.g:7557:2: rule__AccessContext__Group_2__0__Impl rule__AccessContext__Group_2__1
            {
            pushFollow(FOLLOW_7);
            rule__AccessContext__Group_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AccessContext__Group_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group_2__0"


    // $ANTLR start "rule__AccessContext__Group_2__0__Impl"
    // InternalOseeDsl.g:7564:1: rule__AccessContext__Group_2__0__Impl : ( 'extends' ) ;
    public final void rule__AccessContext__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7568:1: ( ( 'extends' ) )
            // InternalOseeDsl.g:7569:1: ( 'extends' )
            {
            // InternalOseeDsl.g:7569:1: ( 'extends' )
            // InternalOseeDsl.g:7570:1: 'extends'
            {
             before(grammarAccess.getAccessContextAccess().getExtendsKeyword_2_0()); 
            match(input,56,FOLLOW_2); 
             after(grammarAccess.getAccessContextAccess().getExtendsKeyword_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group_2__0__Impl"


    // $ANTLR start "rule__AccessContext__Group_2__1"
    // InternalOseeDsl.g:7583:1: rule__AccessContext__Group_2__1 : rule__AccessContext__Group_2__1__Impl ;
    public final void rule__AccessContext__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7587:1: ( rule__AccessContext__Group_2__1__Impl )
            // InternalOseeDsl.g:7588:2: rule__AccessContext__Group_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AccessContext__Group_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group_2__1"


    // $ANTLR start "rule__AccessContext__Group_2__1__Impl"
    // InternalOseeDsl.g:7594:1: rule__AccessContext__Group_2__1__Impl : ( ( rule__AccessContext__SuperAccessContextsAssignment_2_1 ) ) ;
    public final void rule__AccessContext__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7598:1: ( ( ( rule__AccessContext__SuperAccessContextsAssignment_2_1 ) ) )
            // InternalOseeDsl.g:7599:1: ( ( rule__AccessContext__SuperAccessContextsAssignment_2_1 ) )
            {
            // InternalOseeDsl.g:7599:1: ( ( rule__AccessContext__SuperAccessContextsAssignment_2_1 ) )
            // InternalOseeDsl.g:7600:1: ( rule__AccessContext__SuperAccessContextsAssignment_2_1 )
            {
             before(grammarAccess.getAccessContextAccess().getSuperAccessContextsAssignment_2_1()); 
            // InternalOseeDsl.g:7601:1: ( rule__AccessContext__SuperAccessContextsAssignment_2_1 )
            // InternalOseeDsl.g:7601:2: rule__AccessContext__SuperAccessContextsAssignment_2_1
            {
            pushFollow(FOLLOW_2);
            rule__AccessContext__SuperAccessContextsAssignment_2_1();

            state._fsp--;


            }

             after(grammarAccess.getAccessContextAccess().getSuperAccessContextsAssignment_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__Group_2__1__Impl"


    // $ANTLR start "rule__HierarchyRestriction__Group__0"
    // InternalOseeDsl.g:7615:1: rule__HierarchyRestriction__Group__0 : rule__HierarchyRestriction__Group__0__Impl rule__HierarchyRestriction__Group__1 ;
    public final void rule__HierarchyRestriction__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7619:1: ( rule__HierarchyRestriction__Group__0__Impl rule__HierarchyRestriction__Group__1 )
            // InternalOseeDsl.g:7620:2: rule__HierarchyRestriction__Group__0__Impl rule__HierarchyRestriction__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__HierarchyRestriction__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__HierarchyRestriction__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__Group__0"


    // $ANTLR start "rule__HierarchyRestriction__Group__0__Impl"
    // InternalOseeDsl.g:7627:1: rule__HierarchyRestriction__Group__0__Impl : ( 'childrenOf' ) ;
    public final void rule__HierarchyRestriction__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7631:1: ( ( 'childrenOf' ) )
            // InternalOseeDsl.g:7632:1: ( 'childrenOf' )
            {
            // InternalOseeDsl.g:7632:1: ( 'childrenOf' )
            // InternalOseeDsl.g:7633:1: 'childrenOf'
            {
             before(grammarAccess.getHierarchyRestrictionAccess().getChildrenOfKeyword_0()); 
            match(input,93,FOLLOW_2); 
             after(grammarAccess.getHierarchyRestrictionAccess().getChildrenOfKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__Group__0__Impl"


    // $ANTLR start "rule__HierarchyRestriction__Group__1"
    // InternalOseeDsl.g:7646:1: rule__HierarchyRestriction__Group__1 : rule__HierarchyRestriction__Group__1__Impl rule__HierarchyRestriction__Group__2 ;
    public final void rule__HierarchyRestriction__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7650:1: ( rule__HierarchyRestriction__Group__1__Impl rule__HierarchyRestriction__Group__2 )
            // InternalOseeDsl.g:7651:2: rule__HierarchyRestriction__Group__1__Impl rule__HierarchyRestriction__Group__2
            {
            pushFollow(FOLLOW_31);
            rule__HierarchyRestriction__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__HierarchyRestriction__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__Group__1"


    // $ANTLR start "rule__HierarchyRestriction__Group__1__Impl"
    // InternalOseeDsl.g:7658:1: rule__HierarchyRestriction__Group__1__Impl : ( ( rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1 ) ) ;
    public final void rule__HierarchyRestriction__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7662:1: ( ( ( rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1 ) ) )
            // InternalOseeDsl.g:7663:1: ( ( rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1 ) )
            {
            // InternalOseeDsl.g:7663:1: ( ( rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1 ) )
            // InternalOseeDsl.g:7664:1: ( rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1 )
            {
             before(grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefAssignment_1()); 
            // InternalOseeDsl.g:7665:1: ( rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1 )
            // InternalOseeDsl.g:7665:2: rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__Group__1__Impl"


    // $ANTLR start "rule__HierarchyRestriction__Group__2"
    // InternalOseeDsl.g:7675:1: rule__HierarchyRestriction__Group__2 : rule__HierarchyRestriction__Group__2__Impl rule__HierarchyRestriction__Group__3 ;
    public final void rule__HierarchyRestriction__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7679:1: ( rule__HierarchyRestriction__Group__2__Impl rule__HierarchyRestriction__Group__3 )
            // InternalOseeDsl.g:7680:2: rule__HierarchyRestriction__Group__2__Impl rule__HierarchyRestriction__Group__3
            {
            pushFollow(FOLLOW_62);
            rule__HierarchyRestriction__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__HierarchyRestriction__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__Group__2"


    // $ANTLR start "rule__HierarchyRestriction__Group__2__Impl"
    // InternalOseeDsl.g:7687:1: rule__HierarchyRestriction__Group__2__Impl : ( '{' ) ;
    public final void rule__HierarchyRestriction__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7691:1: ( ( '{' ) )
            // InternalOseeDsl.g:7692:1: ( '{' )
            {
            // InternalOseeDsl.g:7692:1: ( '{' )
            // InternalOseeDsl.g:7693:1: '{'
            {
             before(grammarAccess.getHierarchyRestrictionAccess().getLeftCurlyBracketKeyword_2()); 
            match(input,53,FOLLOW_2); 
             after(grammarAccess.getHierarchyRestrictionAccess().getLeftCurlyBracketKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__Group__2__Impl"


    // $ANTLR start "rule__HierarchyRestriction__Group__3"
    // InternalOseeDsl.g:7706:1: rule__HierarchyRestriction__Group__3 : rule__HierarchyRestriction__Group__3__Impl rule__HierarchyRestriction__Group__4 ;
    public final void rule__HierarchyRestriction__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7710:1: ( rule__HierarchyRestriction__Group__3__Impl rule__HierarchyRestriction__Group__4 )
            // InternalOseeDsl.g:7711:2: rule__HierarchyRestriction__Group__3__Impl rule__HierarchyRestriction__Group__4
            {
            pushFollow(FOLLOW_28);
            rule__HierarchyRestriction__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__HierarchyRestriction__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__Group__3"


    // $ANTLR start "rule__HierarchyRestriction__Group__3__Impl"
    // InternalOseeDsl.g:7718:1: rule__HierarchyRestriction__Group__3__Impl : ( ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 ) ) ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 )* ) ) ;
    public final void rule__HierarchyRestriction__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7722:1: ( ( ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 ) ) ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 )* ) ) )
            // InternalOseeDsl.g:7723:1: ( ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 ) ) ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 )* ) )
            {
            // InternalOseeDsl.g:7723:1: ( ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 ) ) ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 )* ) )
            // InternalOseeDsl.g:7724:1: ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 ) ) ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 )* )
            {
            // InternalOseeDsl.g:7724:1: ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 ) )
            // InternalOseeDsl.g:7725:1: ( rule__HierarchyRestriction__AccessRulesAssignment_3 )
            {
             before(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesAssignment_3()); 
            // InternalOseeDsl.g:7726:1: ( rule__HierarchyRestriction__AccessRulesAssignment_3 )
            // InternalOseeDsl.g:7726:2: rule__HierarchyRestriction__AccessRulesAssignment_3
            {
            pushFollow(FOLLOW_63);
            rule__HierarchyRestriction__AccessRulesAssignment_3();

            state._fsp--;


            }

             after(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesAssignment_3()); 

            }

            // InternalOseeDsl.g:7729:1: ( ( rule__HierarchyRestriction__AccessRulesAssignment_3 )* )
            // InternalOseeDsl.g:7730:1: ( rule__HierarchyRestriction__AccessRulesAssignment_3 )*
            {
             before(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesAssignment_3()); 
            // InternalOseeDsl.g:7731:1: ( rule__HierarchyRestriction__AccessRulesAssignment_3 )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( ((LA54_0>=45 && LA54_0<=46)) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // InternalOseeDsl.g:7731:2: rule__HierarchyRestriction__AccessRulesAssignment_3
            	    {
            	    pushFollow(FOLLOW_63);
            	    rule__HierarchyRestriction__AccessRulesAssignment_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop54;
                }
            } while (true);

             after(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesAssignment_3()); 

            }


            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__Group__3__Impl"


    // $ANTLR start "rule__HierarchyRestriction__Group__4"
    // InternalOseeDsl.g:7742:1: rule__HierarchyRestriction__Group__4 : rule__HierarchyRestriction__Group__4__Impl ;
    public final void rule__HierarchyRestriction__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7746:1: ( rule__HierarchyRestriction__Group__4__Impl )
            // InternalOseeDsl.g:7747:2: rule__HierarchyRestriction__Group__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__HierarchyRestriction__Group__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__Group__4"


    // $ANTLR start "rule__HierarchyRestriction__Group__4__Impl"
    // InternalOseeDsl.g:7753:1: rule__HierarchyRestriction__Group__4__Impl : ( '}' ) ;
    public final void rule__HierarchyRestriction__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7757:1: ( ( '}' ) )
            // InternalOseeDsl.g:7758:1: ( '}' )
            {
            // InternalOseeDsl.g:7758:1: ( '}' )
            // InternalOseeDsl.g:7759:1: '}'
            {
             before(grammarAccess.getHierarchyRestrictionAccess().getRightCurlyBracketKeyword_4()); 
            match(input,55,FOLLOW_2); 
             after(grammarAccess.getHierarchyRestrictionAccess().getRightCurlyBracketKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__Group__4__Impl"


    // $ANTLR start "rule__RelationTypeArtifactTypePredicate__Group__0"
    // InternalOseeDsl.g:7782:1: rule__RelationTypeArtifactTypePredicate__Group__0 : rule__RelationTypeArtifactTypePredicate__Group__0__Impl rule__RelationTypeArtifactTypePredicate__Group__1 ;
    public final void rule__RelationTypeArtifactTypePredicate__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7786:1: ( rule__RelationTypeArtifactTypePredicate__Group__0__Impl rule__RelationTypeArtifactTypePredicate__Group__1 )
            // InternalOseeDsl.g:7787:2: rule__RelationTypeArtifactTypePredicate__Group__0__Impl rule__RelationTypeArtifactTypePredicate__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__RelationTypeArtifactTypePredicate__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RelationTypeArtifactTypePredicate__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeArtifactTypePredicate__Group__0"


    // $ANTLR start "rule__RelationTypeArtifactTypePredicate__Group__0__Impl"
    // InternalOseeDsl.g:7794:1: rule__RelationTypeArtifactTypePredicate__Group__0__Impl : ( 'artifactType' ) ;
    public final void rule__RelationTypeArtifactTypePredicate__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7798:1: ( ( 'artifactType' ) )
            // InternalOseeDsl.g:7799:1: ( 'artifactType' )
            {
            // InternalOseeDsl.g:7799:1: ( 'artifactType' )
            // InternalOseeDsl.g:7800:1: 'artifactType'
            {
             before(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeKeyword_0()); 
            match(input,52,FOLLOW_2); 
             after(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeArtifactTypePredicate__Group__0__Impl"


    // $ANTLR start "rule__RelationTypeArtifactTypePredicate__Group__1"
    // InternalOseeDsl.g:7813:1: rule__RelationTypeArtifactTypePredicate__Group__1 : rule__RelationTypeArtifactTypePredicate__Group__1__Impl ;
    public final void rule__RelationTypeArtifactTypePredicate__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7817:1: ( rule__RelationTypeArtifactTypePredicate__Group__1__Impl )
            // InternalOseeDsl.g:7818:2: rule__RelationTypeArtifactTypePredicate__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeArtifactTypePredicate__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeArtifactTypePredicate__Group__1"


    // $ANTLR start "rule__RelationTypeArtifactTypePredicate__Group__1__Impl"
    // InternalOseeDsl.g:7824:1: rule__RelationTypeArtifactTypePredicate__Group__1__Impl : ( ( rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1 ) ) ;
    public final void rule__RelationTypeArtifactTypePredicate__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7828:1: ( ( ( rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1 ) ) )
            // InternalOseeDsl.g:7829:1: ( ( rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1 ) )
            {
            // InternalOseeDsl.g:7829:1: ( ( rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1 ) )
            // InternalOseeDsl.g:7830:1: ( rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1 )
            {
             before(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeRefAssignment_1()); 
            // InternalOseeDsl.g:7831:1: ( rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1 )
            // InternalOseeDsl.g:7831:2: rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeRefAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeArtifactTypePredicate__Group__1__Impl"


    // $ANTLR start "rule__RelationTypeArtifactPredicate__Group__0"
    // InternalOseeDsl.g:7845:1: rule__RelationTypeArtifactPredicate__Group__0 : rule__RelationTypeArtifactPredicate__Group__0__Impl rule__RelationTypeArtifactPredicate__Group__1 ;
    public final void rule__RelationTypeArtifactPredicate__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7849:1: ( rule__RelationTypeArtifactPredicate__Group__0__Impl rule__RelationTypeArtifactPredicate__Group__1 )
            // InternalOseeDsl.g:7850:2: rule__RelationTypeArtifactPredicate__Group__0__Impl rule__RelationTypeArtifactPredicate__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__RelationTypeArtifactPredicate__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RelationTypeArtifactPredicate__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeArtifactPredicate__Group__0"


    // $ANTLR start "rule__RelationTypeArtifactPredicate__Group__0__Impl"
    // InternalOseeDsl.g:7857:1: rule__RelationTypeArtifactPredicate__Group__0__Impl : ( 'artifact' ) ;
    public final void rule__RelationTypeArtifactPredicate__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7861:1: ( ( 'artifact' ) )
            // InternalOseeDsl.g:7862:1: ( 'artifact' )
            {
            // InternalOseeDsl.g:7862:1: ( 'artifact' )
            // InternalOseeDsl.g:7863:1: 'artifact'
            {
             before(grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactKeyword_0()); 
            match(input,94,FOLLOW_2); 
             after(grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeArtifactPredicate__Group__0__Impl"


    // $ANTLR start "rule__RelationTypeArtifactPredicate__Group__1"
    // InternalOseeDsl.g:7876:1: rule__RelationTypeArtifactPredicate__Group__1 : rule__RelationTypeArtifactPredicate__Group__1__Impl ;
    public final void rule__RelationTypeArtifactPredicate__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7880:1: ( rule__RelationTypeArtifactPredicate__Group__1__Impl )
            // InternalOseeDsl.g:7881:2: rule__RelationTypeArtifactPredicate__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeArtifactPredicate__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeArtifactPredicate__Group__1"


    // $ANTLR start "rule__RelationTypeArtifactPredicate__Group__1__Impl"
    // InternalOseeDsl.g:7887:1: rule__RelationTypeArtifactPredicate__Group__1__Impl : ( ( rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1 ) ) ;
    public final void rule__RelationTypeArtifactPredicate__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7891:1: ( ( ( rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1 ) ) )
            // InternalOseeDsl.g:7892:1: ( ( rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1 ) )
            {
            // InternalOseeDsl.g:7892:1: ( ( rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1 ) )
            // InternalOseeDsl.g:7893:1: ( rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1 )
            {
             before(grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactMatcherRefAssignment_1()); 
            // InternalOseeDsl.g:7894:1: ( rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1 )
            // InternalOseeDsl.g:7894:2: rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactMatcherRefAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeArtifactPredicate__Group__1__Impl"


    // $ANTLR start "rule__ArtifactMatchRestriction__Group__0"
    // InternalOseeDsl.g:7908:1: rule__ArtifactMatchRestriction__Group__0 : rule__ArtifactMatchRestriction__Group__0__Impl rule__ArtifactMatchRestriction__Group__1 ;
    public final void rule__ArtifactMatchRestriction__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7912:1: ( rule__ArtifactMatchRestriction__Group__0__Impl rule__ArtifactMatchRestriction__Group__1 )
            // InternalOseeDsl.g:7913:2: rule__ArtifactMatchRestriction__Group__0__Impl rule__ArtifactMatchRestriction__Group__1
            {
            pushFollow(FOLLOW_64);
            rule__ArtifactMatchRestriction__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArtifactMatchRestriction__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__Group__0"


    // $ANTLR start "rule__ArtifactMatchRestriction__Group__0__Impl"
    // InternalOseeDsl.g:7920:1: rule__ArtifactMatchRestriction__Group__0__Impl : ( ( rule__ArtifactMatchRestriction__PermissionAssignment_0 ) ) ;
    public final void rule__ArtifactMatchRestriction__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7924:1: ( ( ( rule__ArtifactMatchRestriction__PermissionAssignment_0 ) ) )
            // InternalOseeDsl.g:7925:1: ( ( rule__ArtifactMatchRestriction__PermissionAssignment_0 ) )
            {
            // InternalOseeDsl.g:7925:1: ( ( rule__ArtifactMatchRestriction__PermissionAssignment_0 ) )
            // InternalOseeDsl.g:7926:1: ( rule__ArtifactMatchRestriction__PermissionAssignment_0 )
            {
             before(grammarAccess.getArtifactMatchRestrictionAccess().getPermissionAssignment_0()); 
            // InternalOseeDsl.g:7927:1: ( rule__ArtifactMatchRestriction__PermissionAssignment_0 )
            // InternalOseeDsl.g:7927:2: rule__ArtifactMatchRestriction__PermissionAssignment_0
            {
            pushFollow(FOLLOW_2);
            rule__ArtifactMatchRestriction__PermissionAssignment_0();

            state._fsp--;


            }

             after(grammarAccess.getArtifactMatchRestrictionAccess().getPermissionAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__Group__0__Impl"


    // $ANTLR start "rule__ArtifactMatchRestriction__Group__1"
    // InternalOseeDsl.g:7937:1: rule__ArtifactMatchRestriction__Group__1 : rule__ArtifactMatchRestriction__Group__1__Impl rule__ArtifactMatchRestriction__Group__2 ;
    public final void rule__ArtifactMatchRestriction__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7941:1: ( rule__ArtifactMatchRestriction__Group__1__Impl rule__ArtifactMatchRestriction__Group__2 )
            // InternalOseeDsl.g:7942:2: rule__ArtifactMatchRestriction__Group__1__Impl rule__ArtifactMatchRestriction__Group__2
            {
            pushFollow(FOLLOW_65);
            rule__ArtifactMatchRestriction__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArtifactMatchRestriction__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__Group__1"


    // $ANTLR start "rule__ArtifactMatchRestriction__Group__1__Impl"
    // InternalOseeDsl.g:7949:1: rule__ArtifactMatchRestriction__Group__1__Impl : ( 'edit' ) ;
    public final void rule__ArtifactMatchRestriction__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7953:1: ( ( 'edit' ) )
            // InternalOseeDsl.g:7954:1: ( 'edit' )
            {
            // InternalOseeDsl.g:7954:1: ( 'edit' )
            // InternalOseeDsl.g:7955:1: 'edit'
            {
             before(grammarAccess.getArtifactMatchRestrictionAccess().getEditKeyword_1()); 
            match(input,95,FOLLOW_2); 
             after(grammarAccess.getArtifactMatchRestrictionAccess().getEditKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__Group__1__Impl"


    // $ANTLR start "rule__ArtifactMatchRestriction__Group__2"
    // InternalOseeDsl.g:7968:1: rule__ArtifactMatchRestriction__Group__2 : rule__ArtifactMatchRestriction__Group__2__Impl rule__ArtifactMatchRestriction__Group__3 ;
    public final void rule__ArtifactMatchRestriction__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7972:1: ( rule__ArtifactMatchRestriction__Group__2__Impl rule__ArtifactMatchRestriction__Group__3 )
            // InternalOseeDsl.g:7973:2: rule__ArtifactMatchRestriction__Group__2__Impl rule__ArtifactMatchRestriction__Group__3
            {
            pushFollow(FOLLOW_7);
            rule__ArtifactMatchRestriction__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArtifactMatchRestriction__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__Group__2"


    // $ANTLR start "rule__ArtifactMatchRestriction__Group__2__Impl"
    // InternalOseeDsl.g:7980:1: rule__ArtifactMatchRestriction__Group__2__Impl : ( 'artifact' ) ;
    public final void rule__ArtifactMatchRestriction__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:7984:1: ( ( 'artifact' ) )
            // InternalOseeDsl.g:7985:1: ( 'artifact' )
            {
            // InternalOseeDsl.g:7985:1: ( 'artifact' )
            // InternalOseeDsl.g:7986:1: 'artifact'
            {
             before(grammarAccess.getArtifactMatchRestrictionAccess().getArtifactKeyword_2()); 
            match(input,94,FOLLOW_2); 
             after(grammarAccess.getArtifactMatchRestrictionAccess().getArtifactKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__Group__2__Impl"


    // $ANTLR start "rule__ArtifactMatchRestriction__Group__3"
    // InternalOseeDsl.g:7999:1: rule__ArtifactMatchRestriction__Group__3 : rule__ArtifactMatchRestriction__Group__3__Impl rule__ArtifactMatchRestriction__Group__4 ;
    public final void rule__ArtifactMatchRestriction__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8003:1: ( rule__ArtifactMatchRestriction__Group__3__Impl rule__ArtifactMatchRestriction__Group__4 )
            // InternalOseeDsl.g:8004:2: rule__ArtifactMatchRestriction__Group__3__Impl rule__ArtifactMatchRestriction__Group__4
            {
            pushFollow(FOLLOW_58);
            rule__ArtifactMatchRestriction__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArtifactMatchRestriction__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__Group__3"


    // $ANTLR start "rule__ArtifactMatchRestriction__Group__3__Impl"
    // InternalOseeDsl.g:8011:1: rule__ArtifactMatchRestriction__Group__3__Impl : ( ( rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3 ) ) ;
    public final void rule__ArtifactMatchRestriction__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8015:1: ( ( ( rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3 ) ) )
            // InternalOseeDsl.g:8016:1: ( ( rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3 ) )
            {
            // InternalOseeDsl.g:8016:1: ( ( rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3 ) )
            // InternalOseeDsl.g:8017:1: ( rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3 )
            {
             before(grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefAssignment_3()); 
            // InternalOseeDsl.g:8018:1: ( rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3 )
            // InternalOseeDsl.g:8018:2: rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3
            {
            pushFollow(FOLLOW_2);
            rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3();

            state._fsp--;


            }

             after(grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefAssignment_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__Group__3__Impl"


    // $ANTLR start "rule__ArtifactMatchRestriction__Group__4"
    // InternalOseeDsl.g:8028:1: rule__ArtifactMatchRestriction__Group__4 : rule__ArtifactMatchRestriction__Group__4__Impl ;
    public final void rule__ArtifactMatchRestriction__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8032:1: ( rule__ArtifactMatchRestriction__Group__4__Impl )
            // InternalOseeDsl.g:8033:2: rule__ArtifactMatchRestriction__Group__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ArtifactMatchRestriction__Group__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__Group__4"


    // $ANTLR start "rule__ArtifactMatchRestriction__Group__4__Impl"
    // InternalOseeDsl.g:8039:1: rule__ArtifactMatchRestriction__Group__4__Impl : ( ';' ) ;
    public final void rule__ArtifactMatchRestriction__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8043:1: ( ( ';' ) )
            // InternalOseeDsl.g:8044:1: ( ';' )
            {
            // InternalOseeDsl.g:8044:1: ( ';' )
            // InternalOseeDsl.g:8045:1: ';'
            {
             before(grammarAccess.getArtifactMatchRestrictionAccess().getSemicolonKeyword_4()); 
            match(input,90,FOLLOW_2); 
             after(grammarAccess.getArtifactMatchRestrictionAccess().getSemicolonKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__Group__4__Impl"


    // $ANTLR start "rule__ArtifactTypeRestriction__Group__0"
    // InternalOseeDsl.g:8068:1: rule__ArtifactTypeRestriction__Group__0 : rule__ArtifactTypeRestriction__Group__0__Impl rule__ArtifactTypeRestriction__Group__1 ;
    public final void rule__ArtifactTypeRestriction__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8072:1: ( rule__ArtifactTypeRestriction__Group__0__Impl rule__ArtifactTypeRestriction__Group__1 )
            // InternalOseeDsl.g:8073:2: rule__ArtifactTypeRestriction__Group__0__Impl rule__ArtifactTypeRestriction__Group__1
            {
            pushFollow(FOLLOW_64);
            rule__ArtifactTypeRestriction__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArtifactTypeRestriction__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__Group__0"


    // $ANTLR start "rule__ArtifactTypeRestriction__Group__0__Impl"
    // InternalOseeDsl.g:8080:1: rule__ArtifactTypeRestriction__Group__0__Impl : ( ( rule__ArtifactTypeRestriction__PermissionAssignment_0 ) ) ;
    public final void rule__ArtifactTypeRestriction__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8084:1: ( ( ( rule__ArtifactTypeRestriction__PermissionAssignment_0 ) ) )
            // InternalOseeDsl.g:8085:1: ( ( rule__ArtifactTypeRestriction__PermissionAssignment_0 ) )
            {
            // InternalOseeDsl.g:8085:1: ( ( rule__ArtifactTypeRestriction__PermissionAssignment_0 ) )
            // InternalOseeDsl.g:8086:1: ( rule__ArtifactTypeRestriction__PermissionAssignment_0 )
            {
             before(grammarAccess.getArtifactTypeRestrictionAccess().getPermissionAssignment_0()); 
            // InternalOseeDsl.g:8087:1: ( rule__ArtifactTypeRestriction__PermissionAssignment_0 )
            // InternalOseeDsl.g:8087:2: rule__ArtifactTypeRestriction__PermissionAssignment_0
            {
            pushFollow(FOLLOW_2);
            rule__ArtifactTypeRestriction__PermissionAssignment_0();

            state._fsp--;


            }

             after(grammarAccess.getArtifactTypeRestrictionAccess().getPermissionAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__Group__0__Impl"


    // $ANTLR start "rule__ArtifactTypeRestriction__Group__1"
    // InternalOseeDsl.g:8097:1: rule__ArtifactTypeRestriction__Group__1 : rule__ArtifactTypeRestriction__Group__1__Impl rule__ArtifactTypeRestriction__Group__2 ;
    public final void rule__ArtifactTypeRestriction__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8101:1: ( rule__ArtifactTypeRestriction__Group__1__Impl rule__ArtifactTypeRestriction__Group__2 )
            // InternalOseeDsl.g:8102:2: rule__ArtifactTypeRestriction__Group__1__Impl rule__ArtifactTypeRestriction__Group__2
            {
            pushFollow(FOLLOW_66);
            rule__ArtifactTypeRestriction__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArtifactTypeRestriction__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__Group__1"


    // $ANTLR start "rule__ArtifactTypeRestriction__Group__1__Impl"
    // InternalOseeDsl.g:8109:1: rule__ArtifactTypeRestriction__Group__1__Impl : ( 'edit' ) ;
    public final void rule__ArtifactTypeRestriction__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8113:1: ( ( 'edit' ) )
            // InternalOseeDsl.g:8114:1: ( 'edit' )
            {
            // InternalOseeDsl.g:8114:1: ( 'edit' )
            // InternalOseeDsl.g:8115:1: 'edit'
            {
             before(grammarAccess.getArtifactTypeRestrictionAccess().getEditKeyword_1()); 
            match(input,95,FOLLOW_2); 
             after(grammarAccess.getArtifactTypeRestrictionAccess().getEditKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__Group__1__Impl"


    // $ANTLR start "rule__ArtifactTypeRestriction__Group__2"
    // InternalOseeDsl.g:8128:1: rule__ArtifactTypeRestriction__Group__2 : rule__ArtifactTypeRestriction__Group__2__Impl rule__ArtifactTypeRestriction__Group__3 ;
    public final void rule__ArtifactTypeRestriction__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8132:1: ( rule__ArtifactTypeRestriction__Group__2__Impl rule__ArtifactTypeRestriction__Group__3 )
            // InternalOseeDsl.g:8133:2: rule__ArtifactTypeRestriction__Group__2__Impl rule__ArtifactTypeRestriction__Group__3
            {
            pushFollow(FOLLOW_7);
            rule__ArtifactTypeRestriction__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArtifactTypeRestriction__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__Group__2"


    // $ANTLR start "rule__ArtifactTypeRestriction__Group__2__Impl"
    // InternalOseeDsl.g:8140:1: rule__ArtifactTypeRestriction__Group__2__Impl : ( 'artifactType' ) ;
    public final void rule__ArtifactTypeRestriction__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8144:1: ( ( 'artifactType' ) )
            // InternalOseeDsl.g:8145:1: ( 'artifactType' )
            {
            // InternalOseeDsl.g:8145:1: ( 'artifactType' )
            // InternalOseeDsl.g:8146:1: 'artifactType'
            {
             before(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeKeyword_2()); 
            match(input,52,FOLLOW_2); 
             after(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__Group__2__Impl"


    // $ANTLR start "rule__ArtifactTypeRestriction__Group__3"
    // InternalOseeDsl.g:8159:1: rule__ArtifactTypeRestriction__Group__3 : rule__ArtifactTypeRestriction__Group__3__Impl rule__ArtifactTypeRestriction__Group__4 ;
    public final void rule__ArtifactTypeRestriction__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8163:1: ( rule__ArtifactTypeRestriction__Group__3__Impl rule__ArtifactTypeRestriction__Group__4 )
            // InternalOseeDsl.g:8164:2: rule__ArtifactTypeRestriction__Group__3__Impl rule__ArtifactTypeRestriction__Group__4
            {
            pushFollow(FOLLOW_58);
            rule__ArtifactTypeRestriction__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ArtifactTypeRestriction__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__Group__3"


    // $ANTLR start "rule__ArtifactTypeRestriction__Group__3__Impl"
    // InternalOseeDsl.g:8171:1: rule__ArtifactTypeRestriction__Group__3__Impl : ( ( rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3 ) ) ;
    public final void rule__ArtifactTypeRestriction__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8175:1: ( ( ( rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3 ) ) )
            // InternalOseeDsl.g:8176:1: ( ( rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3 ) )
            {
            // InternalOseeDsl.g:8176:1: ( ( rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3 ) )
            // InternalOseeDsl.g:8177:1: ( rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3 )
            {
             before(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefAssignment_3()); 
            // InternalOseeDsl.g:8178:1: ( rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3 )
            // InternalOseeDsl.g:8178:2: rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3
            {
            pushFollow(FOLLOW_2);
            rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3();

            state._fsp--;


            }

             after(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefAssignment_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__Group__3__Impl"


    // $ANTLR start "rule__ArtifactTypeRestriction__Group__4"
    // InternalOseeDsl.g:8188:1: rule__ArtifactTypeRestriction__Group__4 : rule__ArtifactTypeRestriction__Group__4__Impl ;
    public final void rule__ArtifactTypeRestriction__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8192:1: ( rule__ArtifactTypeRestriction__Group__4__Impl )
            // InternalOseeDsl.g:8193:2: rule__ArtifactTypeRestriction__Group__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ArtifactTypeRestriction__Group__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__Group__4"


    // $ANTLR start "rule__ArtifactTypeRestriction__Group__4__Impl"
    // InternalOseeDsl.g:8199:1: rule__ArtifactTypeRestriction__Group__4__Impl : ( ';' ) ;
    public final void rule__ArtifactTypeRestriction__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8203:1: ( ( ';' ) )
            // InternalOseeDsl.g:8204:1: ( ';' )
            {
            // InternalOseeDsl.g:8204:1: ( ';' )
            // InternalOseeDsl.g:8205:1: ';'
            {
             before(grammarAccess.getArtifactTypeRestrictionAccess().getSemicolonKeyword_4()); 
            match(input,90,FOLLOW_2); 
             after(grammarAccess.getArtifactTypeRestrictionAccess().getSemicolonKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__Group__4__Impl"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__0"
    // InternalOseeDsl.g:8228:1: rule__AttributeTypeRestriction__Group__0 : rule__AttributeTypeRestriction__Group__0__Impl rule__AttributeTypeRestriction__Group__1 ;
    public final void rule__AttributeTypeRestriction__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8232:1: ( rule__AttributeTypeRestriction__Group__0__Impl rule__AttributeTypeRestriction__Group__1 )
            // InternalOseeDsl.g:8233:2: rule__AttributeTypeRestriction__Group__0__Impl rule__AttributeTypeRestriction__Group__1
            {
            pushFollow(FOLLOW_64);
            rule__AttributeTypeRestriction__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__0"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__0__Impl"
    // InternalOseeDsl.g:8240:1: rule__AttributeTypeRestriction__Group__0__Impl : ( ( rule__AttributeTypeRestriction__PermissionAssignment_0 ) ) ;
    public final void rule__AttributeTypeRestriction__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8244:1: ( ( ( rule__AttributeTypeRestriction__PermissionAssignment_0 ) ) )
            // InternalOseeDsl.g:8245:1: ( ( rule__AttributeTypeRestriction__PermissionAssignment_0 ) )
            {
            // InternalOseeDsl.g:8245:1: ( ( rule__AttributeTypeRestriction__PermissionAssignment_0 ) )
            // InternalOseeDsl.g:8246:1: ( rule__AttributeTypeRestriction__PermissionAssignment_0 )
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getPermissionAssignment_0()); 
            // InternalOseeDsl.g:8247:1: ( rule__AttributeTypeRestriction__PermissionAssignment_0 )
            // InternalOseeDsl.g:8247:2: rule__AttributeTypeRestriction__PermissionAssignment_0
            {
            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__PermissionAssignment_0();

            state._fsp--;


            }

             after(grammarAccess.getAttributeTypeRestrictionAccess().getPermissionAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__0__Impl"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__1"
    // InternalOseeDsl.g:8257:1: rule__AttributeTypeRestriction__Group__1 : rule__AttributeTypeRestriction__Group__1__Impl rule__AttributeTypeRestriction__Group__2 ;
    public final void rule__AttributeTypeRestriction__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8261:1: ( rule__AttributeTypeRestriction__Group__1__Impl rule__AttributeTypeRestriction__Group__2 )
            // InternalOseeDsl.g:8262:2: rule__AttributeTypeRestriction__Group__1__Impl rule__AttributeTypeRestriction__Group__2
            {
            pushFollow(FOLLOW_67);
            rule__AttributeTypeRestriction__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__1"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__1__Impl"
    // InternalOseeDsl.g:8269:1: rule__AttributeTypeRestriction__Group__1__Impl : ( 'edit' ) ;
    public final void rule__AttributeTypeRestriction__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8273:1: ( ( 'edit' ) )
            // InternalOseeDsl.g:8274:1: ( 'edit' )
            {
            // InternalOseeDsl.g:8274:1: ( 'edit' )
            // InternalOseeDsl.g:8275:1: 'edit'
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getEditKeyword_1()); 
            match(input,95,FOLLOW_2); 
             after(grammarAccess.getAttributeTypeRestrictionAccess().getEditKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__1__Impl"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__2"
    // InternalOseeDsl.g:8288:1: rule__AttributeTypeRestriction__Group__2 : rule__AttributeTypeRestriction__Group__2__Impl rule__AttributeTypeRestriction__Group__3 ;
    public final void rule__AttributeTypeRestriction__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8292:1: ( rule__AttributeTypeRestriction__Group__2__Impl rule__AttributeTypeRestriction__Group__3 )
            // InternalOseeDsl.g:8293:2: rule__AttributeTypeRestriction__Group__2__Impl rule__AttributeTypeRestriction__Group__3
            {
            pushFollow(FOLLOW_7);
            rule__AttributeTypeRestriction__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__2"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__2__Impl"
    // InternalOseeDsl.g:8300:1: rule__AttributeTypeRestriction__Group__2__Impl : ( 'attributeType' ) ;
    public final void rule__AttributeTypeRestriction__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8304:1: ( ( 'attributeType' ) )
            // InternalOseeDsl.g:8305:1: ( 'attributeType' )
            {
            // InternalOseeDsl.g:8305:1: ( 'attributeType' )
            // InternalOseeDsl.g:8306:1: 'attributeType'
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeKeyword_2()); 
            match(input,60,FOLLOW_2); 
             after(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__2__Impl"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__3"
    // InternalOseeDsl.g:8319:1: rule__AttributeTypeRestriction__Group__3 : rule__AttributeTypeRestriction__Group__3__Impl rule__AttributeTypeRestriction__Group__4 ;
    public final void rule__AttributeTypeRestriction__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8323:1: ( rule__AttributeTypeRestriction__Group__3__Impl rule__AttributeTypeRestriction__Group__4 )
            // InternalOseeDsl.g:8324:2: rule__AttributeTypeRestriction__Group__3__Impl rule__AttributeTypeRestriction__Group__4
            {
            pushFollow(FOLLOW_68);
            rule__AttributeTypeRestriction__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__3"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__3__Impl"
    // InternalOseeDsl.g:8331:1: rule__AttributeTypeRestriction__Group__3__Impl : ( ( rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3 ) ) ;
    public final void rule__AttributeTypeRestriction__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8335:1: ( ( ( rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3 ) ) )
            // InternalOseeDsl.g:8336:1: ( ( rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3 ) )
            {
            // InternalOseeDsl.g:8336:1: ( ( rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3 ) )
            // InternalOseeDsl.g:8337:1: ( rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3 )
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefAssignment_3()); 
            // InternalOseeDsl.g:8338:1: ( rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3 )
            // InternalOseeDsl.g:8338:2: rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3
            {
            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3();

            state._fsp--;


            }

             after(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefAssignment_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__3__Impl"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__4"
    // InternalOseeDsl.g:8348:1: rule__AttributeTypeRestriction__Group__4 : rule__AttributeTypeRestriction__Group__4__Impl rule__AttributeTypeRestriction__Group__5 ;
    public final void rule__AttributeTypeRestriction__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8352:1: ( rule__AttributeTypeRestriction__Group__4__Impl rule__AttributeTypeRestriction__Group__5 )
            // InternalOseeDsl.g:8353:2: rule__AttributeTypeRestriction__Group__4__Impl rule__AttributeTypeRestriction__Group__5
            {
            pushFollow(FOLLOW_68);
            rule__AttributeTypeRestriction__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__4"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__4__Impl"
    // InternalOseeDsl.g:8360:1: rule__AttributeTypeRestriction__Group__4__Impl : ( ( rule__AttributeTypeRestriction__Group_4__0 )? ) ;
    public final void rule__AttributeTypeRestriction__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8364:1: ( ( ( rule__AttributeTypeRestriction__Group_4__0 )? ) )
            // InternalOseeDsl.g:8365:1: ( ( rule__AttributeTypeRestriction__Group_4__0 )? )
            {
            // InternalOseeDsl.g:8365:1: ( ( rule__AttributeTypeRestriction__Group_4__0 )? )
            // InternalOseeDsl.g:8366:1: ( rule__AttributeTypeRestriction__Group_4__0 )?
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getGroup_4()); 
            // InternalOseeDsl.g:8367:1: ( rule__AttributeTypeRestriction__Group_4__0 )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==96) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // InternalOseeDsl.g:8367:2: rule__AttributeTypeRestriction__Group_4__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__AttributeTypeRestriction__Group_4__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getAttributeTypeRestrictionAccess().getGroup_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__4__Impl"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__5"
    // InternalOseeDsl.g:8377:1: rule__AttributeTypeRestriction__Group__5 : rule__AttributeTypeRestriction__Group__5__Impl ;
    public final void rule__AttributeTypeRestriction__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8381:1: ( rule__AttributeTypeRestriction__Group__5__Impl )
            // InternalOseeDsl.g:8382:2: rule__AttributeTypeRestriction__Group__5__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__Group__5__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__5"


    // $ANTLR start "rule__AttributeTypeRestriction__Group__5__Impl"
    // InternalOseeDsl.g:8388:1: rule__AttributeTypeRestriction__Group__5__Impl : ( ';' ) ;
    public final void rule__AttributeTypeRestriction__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8392:1: ( ( ';' ) )
            // InternalOseeDsl.g:8393:1: ( ';' )
            {
            // InternalOseeDsl.g:8393:1: ( ';' )
            // InternalOseeDsl.g:8394:1: ';'
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getSemicolonKeyword_5()); 
            match(input,90,FOLLOW_2); 
             after(grammarAccess.getAttributeTypeRestrictionAccess().getSemicolonKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group__5__Impl"


    // $ANTLR start "rule__AttributeTypeRestriction__Group_4__0"
    // InternalOseeDsl.g:8419:1: rule__AttributeTypeRestriction__Group_4__0 : rule__AttributeTypeRestriction__Group_4__0__Impl rule__AttributeTypeRestriction__Group_4__1 ;
    public final void rule__AttributeTypeRestriction__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8423:1: ( rule__AttributeTypeRestriction__Group_4__0__Impl rule__AttributeTypeRestriction__Group_4__1 )
            // InternalOseeDsl.g:8424:2: rule__AttributeTypeRestriction__Group_4__0__Impl rule__AttributeTypeRestriction__Group_4__1
            {
            pushFollow(FOLLOW_66);
            rule__AttributeTypeRestriction__Group_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__Group_4__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group_4__0"


    // $ANTLR start "rule__AttributeTypeRestriction__Group_4__0__Impl"
    // InternalOseeDsl.g:8431:1: rule__AttributeTypeRestriction__Group_4__0__Impl : ( 'of' ) ;
    public final void rule__AttributeTypeRestriction__Group_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8435:1: ( ( 'of' ) )
            // InternalOseeDsl.g:8436:1: ( 'of' )
            {
            // InternalOseeDsl.g:8436:1: ( 'of' )
            // InternalOseeDsl.g:8437:1: 'of'
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getOfKeyword_4_0()); 
            match(input,96,FOLLOW_2); 
             after(grammarAccess.getAttributeTypeRestrictionAccess().getOfKeyword_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group_4__0__Impl"


    // $ANTLR start "rule__AttributeTypeRestriction__Group_4__1"
    // InternalOseeDsl.g:8450:1: rule__AttributeTypeRestriction__Group_4__1 : rule__AttributeTypeRestriction__Group_4__1__Impl rule__AttributeTypeRestriction__Group_4__2 ;
    public final void rule__AttributeTypeRestriction__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8454:1: ( rule__AttributeTypeRestriction__Group_4__1__Impl rule__AttributeTypeRestriction__Group_4__2 )
            // InternalOseeDsl.g:8455:2: rule__AttributeTypeRestriction__Group_4__1__Impl rule__AttributeTypeRestriction__Group_4__2
            {
            pushFollow(FOLLOW_7);
            rule__AttributeTypeRestriction__Group_4__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__Group_4__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group_4__1"


    // $ANTLR start "rule__AttributeTypeRestriction__Group_4__1__Impl"
    // InternalOseeDsl.g:8462:1: rule__AttributeTypeRestriction__Group_4__1__Impl : ( 'artifactType' ) ;
    public final void rule__AttributeTypeRestriction__Group_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8466:1: ( ( 'artifactType' ) )
            // InternalOseeDsl.g:8467:1: ( 'artifactType' )
            {
            // InternalOseeDsl.g:8467:1: ( 'artifactType' )
            // InternalOseeDsl.g:8468:1: 'artifactType'
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeKeyword_4_1()); 
            match(input,52,FOLLOW_2); 
             after(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeKeyword_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group_4__1__Impl"


    // $ANTLR start "rule__AttributeTypeRestriction__Group_4__2"
    // InternalOseeDsl.g:8481:1: rule__AttributeTypeRestriction__Group_4__2 : rule__AttributeTypeRestriction__Group_4__2__Impl ;
    public final void rule__AttributeTypeRestriction__Group_4__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8485:1: ( rule__AttributeTypeRestriction__Group_4__2__Impl )
            // InternalOseeDsl.g:8486:2: rule__AttributeTypeRestriction__Group_4__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__Group_4__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group_4__2"


    // $ANTLR start "rule__AttributeTypeRestriction__Group_4__2__Impl"
    // InternalOseeDsl.g:8492:1: rule__AttributeTypeRestriction__Group_4__2__Impl : ( ( rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2 ) ) ;
    public final void rule__AttributeTypeRestriction__Group_4__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8496:1: ( ( ( rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2 ) ) )
            // InternalOseeDsl.g:8497:1: ( ( rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2 ) )
            {
            // InternalOseeDsl.g:8497:1: ( ( rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2 ) )
            // InternalOseeDsl.g:8498:1: ( rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2 )
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefAssignment_4_2()); 
            // InternalOseeDsl.g:8499:1: ( rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2 )
            // InternalOseeDsl.g:8499:2: rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2
            {
            pushFollow(FOLLOW_2);
            rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2();

            state._fsp--;


            }

             after(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefAssignment_4_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__Group_4__2__Impl"


    // $ANTLR start "rule__RelationTypeRestriction__Group__0"
    // InternalOseeDsl.g:8517:1: rule__RelationTypeRestriction__Group__0 : rule__RelationTypeRestriction__Group__0__Impl rule__RelationTypeRestriction__Group__1 ;
    public final void rule__RelationTypeRestriction__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8521:1: ( rule__RelationTypeRestriction__Group__0__Impl rule__RelationTypeRestriction__Group__1 )
            // InternalOseeDsl.g:8522:2: rule__RelationTypeRestriction__Group__0__Impl rule__RelationTypeRestriction__Group__1
            {
            pushFollow(FOLLOW_64);
            rule__RelationTypeRestriction__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__0"


    // $ANTLR start "rule__RelationTypeRestriction__Group__0__Impl"
    // InternalOseeDsl.g:8529:1: rule__RelationTypeRestriction__Group__0__Impl : ( ( rule__RelationTypeRestriction__PermissionAssignment_0 ) ) ;
    public final void rule__RelationTypeRestriction__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8533:1: ( ( ( rule__RelationTypeRestriction__PermissionAssignment_0 ) ) )
            // InternalOseeDsl.g:8534:1: ( ( rule__RelationTypeRestriction__PermissionAssignment_0 ) )
            {
            // InternalOseeDsl.g:8534:1: ( ( rule__RelationTypeRestriction__PermissionAssignment_0 ) )
            // InternalOseeDsl.g:8535:1: ( rule__RelationTypeRestriction__PermissionAssignment_0 )
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getPermissionAssignment_0()); 
            // InternalOseeDsl.g:8536:1: ( rule__RelationTypeRestriction__PermissionAssignment_0 )
            // InternalOseeDsl.g:8536:2: rule__RelationTypeRestriction__PermissionAssignment_0
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__PermissionAssignment_0();

            state._fsp--;


            }

             after(grammarAccess.getRelationTypeRestrictionAccess().getPermissionAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__0__Impl"


    // $ANTLR start "rule__RelationTypeRestriction__Group__1"
    // InternalOseeDsl.g:8546:1: rule__RelationTypeRestriction__Group__1 : rule__RelationTypeRestriction__Group__1__Impl rule__RelationTypeRestriction__Group__2 ;
    public final void rule__RelationTypeRestriction__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8550:1: ( rule__RelationTypeRestriction__Group__1__Impl rule__RelationTypeRestriction__Group__2 )
            // InternalOseeDsl.g:8551:2: rule__RelationTypeRestriction__Group__1__Impl rule__RelationTypeRestriction__Group__2
            {
            pushFollow(FOLLOW_69);
            rule__RelationTypeRestriction__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__1"


    // $ANTLR start "rule__RelationTypeRestriction__Group__1__Impl"
    // InternalOseeDsl.g:8558:1: rule__RelationTypeRestriction__Group__1__Impl : ( 'edit' ) ;
    public final void rule__RelationTypeRestriction__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8562:1: ( ( 'edit' ) )
            // InternalOseeDsl.g:8563:1: ( 'edit' )
            {
            // InternalOseeDsl.g:8563:1: ( 'edit' )
            // InternalOseeDsl.g:8564:1: 'edit'
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getEditKeyword_1()); 
            match(input,95,FOLLOW_2); 
             after(grammarAccess.getRelationTypeRestrictionAccess().getEditKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__1__Impl"


    // $ANTLR start "rule__RelationTypeRestriction__Group__2"
    // InternalOseeDsl.g:8577:1: rule__RelationTypeRestriction__Group__2 : rule__RelationTypeRestriction__Group__2__Impl rule__RelationTypeRestriction__Group__3 ;
    public final void rule__RelationTypeRestriction__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8581:1: ( rule__RelationTypeRestriction__Group__2__Impl rule__RelationTypeRestriction__Group__3 )
            // InternalOseeDsl.g:8582:2: rule__RelationTypeRestriction__Group__2__Impl rule__RelationTypeRestriction__Group__3
            {
            pushFollow(FOLLOW_70);
            rule__RelationTypeRestriction__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__2"


    // $ANTLR start "rule__RelationTypeRestriction__Group__2__Impl"
    // InternalOseeDsl.g:8589:1: rule__RelationTypeRestriction__Group__2__Impl : ( 'relationType' ) ;
    public final void rule__RelationTypeRestriction__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8593:1: ( ( 'relationType' ) )
            // InternalOseeDsl.g:8594:1: ( 'relationType' )
            {
            // InternalOseeDsl.g:8594:1: ( 'relationType' )
            // InternalOseeDsl.g:8595:1: 'relationType'
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeKeyword_2()); 
            match(input,79,FOLLOW_2); 
             after(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeKeyword_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__2__Impl"


    // $ANTLR start "rule__RelationTypeRestriction__Group__3"
    // InternalOseeDsl.g:8608:1: rule__RelationTypeRestriction__Group__3 : rule__RelationTypeRestriction__Group__3__Impl rule__RelationTypeRestriction__Group__4 ;
    public final void rule__RelationTypeRestriction__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8612:1: ( rule__RelationTypeRestriction__Group__3__Impl rule__RelationTypeRestriction__Group__4 )
            // InternalOseeDsl.g:8613:2: rule__RelationTypeRestriction__Group__3__Impl rule__RelationTypeRestriction__Group__4
            {
            pushFollow(FOLLOW_71);
            rule__RelationTypeRestriction__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__3"


    // $ANTLR start "rule__RelationTypeRestriction__Group__3__Impl"
    // InternalOseeDsl.g:8620:1: rule__RelationTypeRestriction__Group__3__Impl : ( ( rule__RelationTypeRestriction__Alternatives_3 ) ) ;
    public final void rule__RelationTypeRestriction__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8624:1: ( ( ( rule__RelationTypeRestriction__Alternatives_3 ) ) )
            // InternalOseeDsl.g:8625:1: ( ( rule__RelationTypeRestriction__Alternatives_3 ) )
            {
            // InternalOseeDsl.g:8625:1: ( ( rule__RelationTypeRestriction__Alternatives_3 ) )
            // InternalOseeDsl.g:8626:1: ( rule__RelationTypeRestriction__Alternatives_3 )
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getAlternatives_3()); 
            // InternalOseeDsl.g:8627:1: ( rule__RelationTypeRestriction__Alternatives_3 )
            // InternalOseeDsl.g:8627:2: rule__RelationTypeRestriction__Alternatives_3
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__Alternatives_3();

            state._fsp--;


            }

             after(grammarAccess.getRelationTypeRestrictionAccess().getAlternatives_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__3__Impl"


    // $ANTLR start "rule__RelationTypeRestriction__Group__4"
    // InternalOseeDsl.g:8637:1: rule__RelationTypeRestriction__Group__4 : rule__RelationTypeRestriction__Group__4__Impl rule__RelationTypeRestriction__Group__5 ;
    public final void rule__RelationTypeRestriction__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8641:1: ( rule__RelationTypeRestriction__Group__4__Impl rule__RelationTypeRestriction__Group__5 )
            // InternalOseeDsl.g:8642:2: rule__RelationTypeRestriction__Group__4__Impl rule__RelationTypeRestriction__Group__5
            {
            pushFollow(FOLLOW_72);
            rule__RelationTypeRestriction__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__4"


    // $ANTLR start "rule__RelationTypeRestriction__Group__4__Impl"
    // InternalOseeDsl.g:8649:1: rule__RelationTypeRestriction__Group__4__Impl : ( ( rule__RelationTypeRestriction__RestrictedToSideAssignment_4 ) ) ;
    public final void rule__RelationTypeRestriction__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8653:1: ( ( ( rule__RelationTypeRestriction__RestrictedToSideAssignment_4 ) ) )
            // InternalOseeDsl.g:8654:1: ( ( rule__RelationTypeRestriction__RestrictedToSideAssignment_4 ) )
            {
            // InternalOseeDsl.g:8654:1: ( ( rule__RelationTypeRestriction__RestrictedToSideAssignment_4 ) )
            // InternalOseeDsl.g:8655:1: ( rule__RelationTypeRestriction__RestrictedToSideAssignment_4 )
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getRestrictedToSideAssignment_4()); 
            // InternalOseeDsl.g:8656:1: ( rule__RelationTypeRestriction__RestrictedToSideAssignment_4 )
            // InternalOseeDsl.g:8656:2: rule__RelationTypeRestriction__RestrictedToSideAssignment_4
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__RestrictedToSideAssignment_4();

            state._fsp--;


            }

             after(grammarAccess.getRelationTypeRestrictionAccess().getRestrictedToSideAssignment_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__4__Impl"


    // $ANTLR start "rule__RelationTypeRestriction__Group__5"
    // InternalOseeDsl.g:8666:1: rule__RelationTypeRestriction__Group__5 : rule__RelationTypeRestriction__Group__5__Impl rule__RelationTypeRestriction__Group__6 ;
    public final void rule__RelationTypeRestriction__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8670:1: ( rule__RelationTypeRestriction__Group__5__Impl rule__RelationTypeRestriction__Group__6 )
            // InternalOseeDsl.g:8671:2: rule__RelationTypeRestriction__Group__5__Impl rule__RelationTypeRestriction__Group__6
            {
            pushFollow(FOLLOW_72);
            rule__RelationTypeRestriction__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__5"


    // $ANTLR start "rule__RelationTypeRestriction__Group__5__Impl"
    // InternalOseeDsl.g:8678:1: rule__RelationTypeRestriction__Group__5__Impl : ( ( rule__RelationTypeRestriction__PredicateAssignment_5 )? ) ;
    public final void rule__RelationTypeRestriction__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8682:1: ( ( ( rule__RelationTypeRestriction__PredicateAssignment_5 )? ) )
            // InternalOseeDsl.g:8683:1: ( ( rule__RelationTypeRestriction__PredicateAssignment_5 )? )
            {
            // InternalOseeDsl.g:8683:1: ( ( rule__RelationTypeRestriction__PredicateAssignment_5 )? )
            // InternalOseeDsl.g:8684:1: ( rule__RelationTypeRestriction__PredicateAssignment_5 )?
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getPredicateAssignment_5()); 
            // InternalOseeDsl.g:8685:1: ( rule__RelationTypeRestriction__PredicateAssignment_5 )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==52||LA56_0==94) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // InternalOseeDsl.g:8685:2: rule__RelationTypeRestriction__PredicateAssignment_5
                    {
                    pushFollow(FOLLOW_2);
                    rule__RelationTypeRestriction__PredicateAssignment_5();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getRelationTypeRestrictionAccess().getPredicateAssignment_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__5__Impl"


    // $ANTLR start "rule__RelationTypeRestriction__Group__6"
    // InternalOseeDsl.g:8695:1: rule__RelationTypeRestriction__Group__6 : rule__RelationTypeRestriction__Group__6__Impl ;
    public final void rule__RelationTypeRestriction__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8699:1: ( rule__RelationTypeRestriction__Group__6__Impl )
            // InternalOseeDsl.g:8700:2: rule__RelationTypeRestriction__Group__6__Impl
            {
            pushFollow(FOLLOW_2);
            rule__RelationTypeRestriction__Group__6__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__6"


    // $ANTLR start "rule__RelationTypeRestriction__Group__6__Impl"
    // InternalOseeDsl.g:8706:1: rule__RelationTypeRestriction__Group__6__Impl : ( ';' ) ;
    public final void rule__RelationTypeRestriction__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8710:1: ( ( ';' ) )
            // InternalOseeDsl.g:8711:1: ( ';' )
            {
            // InternalOseeDsl.g:8711:1: ( ';' )
            // InternalOseeDsl.g:8712:1: ';'
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getSemicolonKeyword_6()); 
            match(input,90,FOLLOW_2); 
             after(grammarAccess.getRelationTypeRestrictionAccess().getSemicolonKeyword_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__Group__6__Impl"


    // $ANTLR start "rule__XAttributeType__UnorderedGroup_14"
    // InternalOseeDsl.g:8740:1: rule__XAttributeType__UnorderedGroup_14 : ( rule__XAttributeType__UnorderedGroup_14__0 )? ;
    public final void rule__XAttributeType__UnorderedGroup_14() throws RecognitionException {

            	int stackSize = keepStackSize();
        		getUnorderedGroupHelper().enter(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            
        try {
            // InternalOseeDsl.g:8745:1: ( ( rule__XAttributeType__UnorderedGroup_14__0 )? )
            // InternalOseeDsl.g:8746:2: ( rule__XAttributeType__UnorderedGroup_14__0 )?
            {
            // InternalOseeDsl.g:8746:2: ( rule__XAttributeType__UnorderedGroup_14__0 )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( LA57_0 == 65 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0) ) {
                alt57=1;
            }
            else if ( LA57_0 == 66 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1) ) {
                alt57=1;
            }
            else if ( LA57_0 == 67 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2) ) {
                alt57=1;
            }
            else if ( LA57_0 == 68 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3) ) {
                alt57=1;
            }
            else if ( LA57_0 == 69 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4) ) {
                alt57=1;
            }
            else if ( LA57_0 == 70 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // InternalOseeDsl.g:8746:2: rule__XAttributeType__UnorderedGroup_14__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__UnorderedGroup_14__0();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	getUnorderedGroupHelper().leave(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__UnorderedGroup_14"


    // $ANTLR start "rule__XAttributeType__UnorderedGroup_14__Impl"
    // InternalOseeDsl.g:8756:1: rule__XAttributeType__UnorderedGroup_14__Impl : ( ({...}? => ( ( ( rule__XAttributeType__Group_14_0__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_1__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_2__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_3__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_4__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_5__0 ) ) ) ) ) ;
    public final void rule__XAttributeType__UnorderedGroup_14__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        		boolean selected = false;
            
        try {
            // InternalOseeDsl.g:8761:1: ( ( ({...}? => ( ( ( rule__XAttributeType__Group_14_0__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_1__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_2__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_3__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_4__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_5__0 ) ) ) ) ) )
            // InternalOseeDsl.g:8762:3: ( ({...}? => ( ( ( rule__XAttributeType__Group_14_0__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_1__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_2__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_3__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_4__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_5__0 ) ) ) ) )
            {
            // InternalOseeDsl.g:8762:3: ( ({...}? => ( ( ( rule__XAttributeType__Group_14_0__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_1__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_2__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_3__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_4__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttributeType__Group_14_5__0 ) ) ) ) )
            int alt58=6;
            int LA58_0 = input.LA(1);

            if ( LA58_0 == 65 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0) ) {
                alt58=1;
            }
            else if ( LA58_0 == 66 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1) ) {
                alt58=2;
            }
            else if ( LA58_0 == 67 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2) ) {
                alt58=3;
            }
            else if ( LA58_0 == 68 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3) ) {
                alt58=4;
            }
            else if ( LA58_0 == 69 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4) ) {
                alt58=5;
            }
            else if ( LA58_0 == 70 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5) ) {
                alt58=6;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 58, 0, input);

                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // InternalOseeDsl.g:8764:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_0__0 ) ) ) )
                    {
                    // InternalOseeDsl.g:8764:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_0__0 ) ) ) )
                    // InternalOseeDsl.g:8765:5: {...}? => ( ( ( rule__XAttributeType__Group_14_0__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0) ) {
                        throw new FailedPredicateException(input, "rule__XAttributeType__UnorderedGroup_14__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0)");
                    }
                    // InternalOseeDsl.g:8765:112: ( ( ( rule__XAttributeType__Group_14_0__0 ) ) )
                    // InternalOseeDsl.g:8766:6: ( ( rule__XAttributeType__Group_14_0__0 ) )
                    {
                     
                    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0);
                    	 				

                    	 				  selected = true;
                    	 				
                    // InternalOseeDsl.g:8772:6: ( ( rule__XAttributeType__Group_14_0__0 ) )
                    // InternalOseeDsl.g:8774:7: ( rule__XAttributeType__Group_14_0__0 )
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getGroup_14_0()); 
                    // InternalOseeDsl.g:8775:7: ( rule__XAttributeType__Group_14_0__0 )
                    // InternalOseeDsl.g:8775:8: rule__XAttributeType__Group_14_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__Group_14_0__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getXAttributeTypeAccess().getGroup_14_0()); 

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalOseeDsl.g:8781:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_1__0 ) ) ) )
                    {
                    // InternalOseeDsl.g:8781:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_1__0 ) ) ) )
                    // InternalOseeDsl.g:8782:5: {...}? => ( ( ( rule__XAttributeType__Group_14_1__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1) ) {
                        throw new FailedPredicateException(input, "rule__XAttributeType__UnorderedGroup_14__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1)");
                    }
                    // InternalOseeDsl.g:8782:112: ( ( ( rule__XAttributeType__Group_14_1__0 ) ) )
                    // InternalOseeDsl.g:8783:6: ( ( rule__XAttributeType__Group_14_1__0 ) )
                    {
                     
                    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1);
                    	 				

                    	 				  selected = true;
                    	 				
                    // InternalOseeDsl.g:8789:6: ( ( rule__XAttributeType__Group_14_1__0 ) )
                    // InternalOseeDsl.g:8791:7: ( rule__XAttributeType__Group_14_1__0 )
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getGroup_14_1()); 
                    // InternalOseeDsl.g:8792:7: ( rule__XAttributeType__Group_14_1__0 )
                    // InternalOseeDsl.g:8792:8: rule__XAttributeType__Group_14_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__Group_14_1__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getXAttributeTypeAccess().getGroup_14_1()); 

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalOseeDsl.g:8798:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_2__0 ) ) ) )
                    {
                    // InternalOseeDsl.g:8798:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_2__0 ) ) ) )
                    // InternalOseeDsl.g:8799:5: {...}? => ( ( ( rule__XAttributeType__Group_14_2__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2) ) {
                        throw new FailedPredicateException(input, "rule__XAttributeType__UnorderedGroup_14__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2)");
                    }
                    // InternalOseeDsl.g:8799:112: ( ( ( rule__XAttributeType__Group_14_2__0 ) ) )
                    // InternalOseeDsl.g:8800:6: ( ( rule__XAttributeType__Group_14_2__0 ) )
                    {
                     
                    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2);
                    	 				

                    	 				  selected = true;
                    	 				
                    // InternalOseeDsl.g:8806:6: ( ( rule__XAttributeType__Group_14_2__0 ) )
                    // InternalOseeDsl.g:8808:7: ( rule__XAttributeType__Group_14_2__0 )
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getGroup_14_2()); 
                    // InternalOseeDsl.g:8809:7: ( rule__XAttributeType__Group_14_2__0 )
                    // InternalOseeDsl.g:8809:8: rule__XAttributeType__Group_14_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__Group_14_2__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getXAttributeTypeAccess().getGroup_14_2()); 

                    }


                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalOseeDsl.g:8815:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_3__0 ) ) ) )
                    {
                    // InternalOseeDsl.g:8815:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_3__0 ) ) ) )
                    // InternalOseeDsl.g:8816:5: {...}? => ( ( ( rule__XAttributeType__Group_14_3__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3) ) {
                        throw new FailedPredicateException(input, "rule__XAttributeType__UnorderedGroup_14__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3)");
                    }
                    // InternalOseeDsl.g:8816:112: ( ( ( rule__XAttributeType__Group_14_3__0 ) ) )
                    // InternalOseeDsl.g:8817:6: ( ( rule__XAttributeType__Group_14_3__0 ) )
                    {
                     
                    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3);
                    	 				

                    	 				  selected = true;
                    	 				
                    // InternalOseeDsl.g:8823:6: ( ( rule__XAttributeType__Group_14_3__0 ) )
                    // InternalOseeDsl.g:8825:7: ( rule__XAttributeType__Group_14_3__0 )
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getGroup_14_3()); 
                    // InternalOseeDsl.g:8826:7: ( rule__XAttributeType__Group_14_3__0 )
                    // InternalOseeDsl.g:8826:8: rule__XAttributeType__Group_14_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__Group_14_3__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getXAttributeTypeAccess().getGroup_14_3()); 

                    }


                    }


                    }


                    }
                    break;
                case 5 :
                    // InternalOseeDsl.g:8832:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_4__0 ) ) ) )
                    {
                    // InternalOseeDsl.g:8832:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_4__0 ) ) ) )
                    // InternalOseeDsl.g:8833:5: {...}? => ( ( ( rule__XAttributeType__Group_14_4__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4) ) {
                        throw new FailedPredicateException(input, "rule__XAttributeType__UnorderedGroup_14__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4)");
                    }
                    // InternalOseeDsl.g:8833:112: ( ( ( rule__XAttributeType__Group_14_4__0 ) ) )
                    // InternalOseeDsl.g:8834:6: ( ( rule__XAttributeType__Group_14_4__0 ) )
                    {
                     
                    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4);
                    	 				

                    	 				  selected = true;
                    	 				
                    // InternalOseeDsl.g:8840:6: ( ( rule__XAttributeType__Group_14_4__0 ) )
                    // InternalOseeDsl.g:8842:7: ( rule__XAttributeType__Group_14_4__0 )
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getGroup_14_4()); 
                    // InternalOseeDsl.g:8843:7: ( rule__XAttributeType__Group_14_4__0 )
                    // InternalOseeDsl.g:8843:8: rule__XAttributeType__Group_14_4__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__Group_14_4__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getXAttributeTypeAccess().getGroup_14_4()); 

                    }


                    }


                    }


                    }
                    break;
                case 6 :
                    // InternalOseeDsl.g:8849:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_5__0 ) ) ) )
                    {
                    // InternalOseeDsl.g:8849:4: ({...}? => ( ( ( rule__XAttributeType__Group_14_5__0 ) ) ) )
                    // InternalOseeDsl.g:8850:5: {...}? => ( ( ( rule__XAttributeType__Group_14_5__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5) ) {
                        throw new FailedPredicateException(input, "rule__XAttributeType__UnorderedGroup_14__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5)");
                    }
                    // InternalOseeDsl.g:8850:112: ( ( ( rule__XAttributeType__Group_14_5__0 ) ) )
                    // InternalOseeDsl.g:8851:6: ( ( rule__XAttributeType__Group_14_5__0 ) )
                    {
                     
                    	 				  getUnorderedGroupHelper().select(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5);
                    	 				

                    	 				  selected = true;
                    	 				
                    // InternalOseeDsl.g:8857:6: ( ( rule__XAttributeType__Group_14_5__0 ) )
                    // InternalOseeDsl.g:8859:7: ( rule__XAttributeType__Group_14_5__0 )
                    {
                     before(grammarAccess.getXAttributeTypeAccess().getGroup_14_5()); 
                    // InternalOseeDsl.g:8860:7: ( rule__XAttributeType__Group_14_5__0 )
                    // InternalOseeDsl.g:8860:8: rule__XAttributeType__Group_14_5__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__Group_14_5__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getXAttributeTypeAccess().getGroup_14_5()); 

                    }


                    }


                    }


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	if (selected)
            		getUnorderedGroupHelper().returnFromSelection(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__UnorderedGroup_14__Impl"


    // $ANTLR start "rule__XAttributeType__UnorderedGroup_14__0"
    // InternalOseeDsl.g:8875:1: rule__XAttributeType__UnorderedGroup_14__0 : rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__1 )? ;
    public final void rule__XAttributeType__UnorderedGroup_14__0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8879:1: ( rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__1 )? )
            // InternalOseeDsl.g:8880:2: rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__1 )?
            {
            pushFollow(FOLLOW_73);
            rule__XAttributeType__UnorderedGroup_14__Impl();

            state._fsp--;

            // InternalOseeDsl.g:8881:2: ( rule__XAttributeType__UnorderedGroup_14__1 )?
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( LA59_0 == 65 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0) ) {
                alt59=1;
            }
            else if ( LA59_0 == 66 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1) ) {
                alt59=1;
            }
            else if ( LA59_0 == 67 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2) ) {
                alt59=1;
            }
            else if ( LA59_0 == 68 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3) ) {
                alt59=1;
            }
            else if ( LA59_0 == 69 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4) ) {
                alt59=1;
            }
            else if ( LA59_0 == 70 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // InternalOseeDsl.g:8881:2: rule__XAttributeType__UnorderedGroup_14__1
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__UnorderedGroup_14__1();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__UnorderedGroup_14__0"


    // $ANTLR start "rule__XAttributeType__UnorderedGroup_14__1"
    // InternalOseeDsl.g:8888:1: rule__XAttributeType__UnorderedGroup_14__1 : rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__2 )? ;
    public final void rule__XAttributeType__UnorderedGroup_14__1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8892:1: ( rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__2 )? )
            // InternalOseeDsl.g:8893:2: rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__2 )?
            {
            pushFollow(FOLLOW_73);
            rule__XAttributeType__UnorderedGroup_14__Impl();

            state._fsp--;

            // InternalOseeDsl.g:8894:2: ( rule__XAttributeType__UnorderedGroup_14__2 )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( LA60_0 == 65 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0) ) {
                alt60=1;
            }
            else if ( LA60_0 == 66 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1) ) {
                alt60=1;
            }
            else if ( LA60_0 == 67 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2) ) {
                alt60=1;
            }
            else if ( LA60_0 == 68 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3) ) {
                alt60=1;
            }
            else if ( LA60_0 == 69 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4) ) {
                alt60=1;
            }
            else if ( LA60_0 == 70 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // InternalOseeDsl.g:8894:2: rule__XAttributeType__UnorderedGroup_14__2
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__UnorderedGroup_14__2();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__UnorderedGroup_14__1"


    // $ANTLR start "rule__XAttributeType__UnorderedGroup_14__2"
    // InternalOseeDsl.g:8901:1: rule__XAttributeType__UnorderedGroup_14__2 : rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__3 )? ;
    public final void rule__XAttributeType__UnorderedGroup_14__2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8905:1: ( rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__3 )? )
            // InternalOseeDsl.g:8906:2: rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__3 )?
            {
            pushFollow(FOLLOW_73);
            rule__XAttributeType__UnorderedGroup_14__Impl();

            state._fsp--;

            // InternalOseeDsl.g:8907:2: ( rule__XAttributeType__UnorderedGroup_14__3 )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( LA61_0 == 65 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0) ) {
                alt61=1;
            }
            else if ( LA61_0 == 66 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1) ) {
                alt61=1;
            }
            else if ( LA61_0 == 67 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2) ) {
                alt61=1;
            }
            else if ( LA61_0 == 68 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3) ) {
                alt61=1;
            }
            else if ( LA61_0 == 69 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4) ) {
                alt61=1;
            }
            else if ( LA61_0 == 70 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5) ) {
                alt61=1;
            }
            switch (alt61) {
                case 1 :
                    // InternalOseeDsl.g:8907:2: rule__XAttributeType__UnorderedGroup_14__3
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__UnorderedGroup_14__3();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__UnorderedGroup_14__2"


    // $ANTLR start "rule__XAttributeType__UnorderedGroup_14__3"
    // InternalOseeDsl.g:8914:1: rule__XAttributeType__UnorderedGroup_14__3 : rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__4 )? ;
    public final void rule__XAttributeType__UnorderedGroup_14__3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8918:1: ( rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__4 )? )
            // InternalOseeDsl.g:8919:2: rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__4 )?
            {
            pushFollow(FOLLOW_73);
            rule__XAttributeType__UnorderedGroup_14__Impl();

            state._fsp--;

            // InternalOseeDsl.g:8920:2: ( rule__XAttributeType__UnorderedGroup_14__4 )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( LA62_0 == 65 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0) ) {
                alt62=1;
            }
            else if ( LA62_0 == 66 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1) ) {
                alt62=1;
            }
            else if ( LA62_0 == 67 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2) ) {
                alt62=1;
            }
            else if ( LA62_0 == 68 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3) ) {
                alt62=1;
            }
            else if ( LA62_0 == 69 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4) ) {
                alt62=1;
            }
            else if ( LA62_0 == 70 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // InternalOseeDsl.g:8920:2: rule__XAttributeType__UnorderedGroup_14__4
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__UnorderedGroup_14__4();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__UnorderedGroup_14__3"


    // $ANTLR start "rule__XAttributeType__UnorderedGroup_14__4"
    // InternalOseeDsl.g:8927:1: rule__XAttributeType__UnorderedGroup_14__4 : rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__5 )? ;
    public final void rule__XAttributeType__UnorderedGroup_14__4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8931:1: ( rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__5 )? )
            // InternalOseeDsl.g:8932:2: rule__XAttributeType__UnorderedGroup_14__Impl ( rule__XAttributeType__UnorderedGroup_14__5 )?
            {
            pushFollow(FOLLOW_73);
            rule__XAttributeType__UnorderedGroup_14__Impl();

            state._fsp--;

            // InternalOseeDsl.g:8933:2: ( rule__XAttributeType__UnorderedGroup_14__5 )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( LA63_0 == 65 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 0) ) {
                alt63=1;
            }
            else if ( LA63_0 == 66 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 1) ) {
                alt63=1;
            }
            else if ( LA63_0 == 67 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 2) ) {
                alt63=1;
            }
            else if ( LA63_0 == 68 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 3) ) {
                alt63=1;
            }
            else if ( LA63_0 == 69 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 4) ) {
                alt63=1;
            }
            else if ( LA63_0 == 70 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), 5) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // InternalOseeDsl.g:8933:2: rule__XAttributeType__UnorderedGroup_14__5
                    {
                    pushFollow(FOLLOW_2);
                    rule__XAttributeType__UnorderedGroup_14__5();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__UnorderedGroup_14__4"


    // $ANTLR start "rule__XAttributeType__UnorderedGroup_14__5"
    // InternalOseeDsl.g:8940:1: rule__XAttributeType__UnorderedGroup_14__5 : rule__XAttributeType__UnorderedGroup_14__Impl ;
    public final void rule__XAttributeType__UnorderedGroup_14__5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8944:1: ( rule__XAttributeType__UnorderedGroup_14__Impl )
            // InternalOseeDsl.g:8945:2: rule__XAttributeType__UnorderedGroup_14__Impl
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__UnorderedGroup_14__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__UnorderedGroup_14__5"


    // $ANTLR start "rule__OseeDsl__ImportsAssignment_0"
    // InternalOseeDsl.g:8964:1: rule__OseeDsl__ImportsAssignment_0 : ( ruleImport ) ;
    public final void rule__OseeDsl__ImportsAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8968:1: ( ( ruleImport ) )
            // InternalOseeDsl.g:8969:1: ( ruleImport )
            {
            // InternalOseeDsl.g:8969:1: ( ruleImport )
            // InternalOseeDsl.g:8970:1: ruleImport
            {
             before(grammarAccess.getOseeDslAccess().getImportsImportParserRuleCall_0_0()); 
            pushFollow(FOLLOW_2);
            ruleImport();

            state._fsp--;

             after(grammarAccess.getOseeDslAccess().getImportsImportParserRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__ImportsAssignment_0"


    // $ANTLR start "rule__OseeDsl__ArtifactTypesAssignment_1_0"
    // InternalOseeDsl.g:8979:1: rule__OseeDsl__ArtifactTypesAssignment_1_0 : ( ruleXArtifactType ) ;
    public final void rule__OseeDsl__ArtifactTypesAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8983:1: ( ( ruleXArtifactType ) )
            // InternalOseeDsl.g:8984:1: ( ruleXArtifactType )
            {
            // InternalOseeDsl.g:8984:1: ( ruleXArtifactType )
            // InternalOseeDsl.g:8985:1: ruleXArtifactType
            {
             before(grammarAccess.getOseeDslAccess().getArtifactTypesXArtifactTypeParserRuleCall_1_0_0()); 
            pushFollow(FOLLOW_2);
            ruleXArtifactType();

            state._fsp--;

             after(grammarAccess.getOseeDslAccess().getArtifactTypesXArtifactTypeParserRuleCall_1_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__ArtifactTypesAssignment_1_0"


    // $ANTLR start "rule__OseeDsl__RelationTypesAssignment_1_1"
    // InternalOseeDsl.g:8994:1: rule__OseeDsl__RelationTypesAssignment_1_1 : ( ruleXRelationType ) ;
    public final void rule__OseeDsl__RelationTypesAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:8998:1: ( ( ruleXRelationType ) )
            // InternalOseeDsl.g:8999:1: ( ruleXRelationType )
            {
            // InternalOseeDsl.g:8999:1: ( ruleXRelationType )
            // InternalOseeDsl.g:9000:1: ruleXRelationType
            {
             before(grammarAccess.getOseeDslAccess().getRelationTypesXRelationTypeParserRuleCall_1_1_0()); 
            pushFollow(FOLLOW_2);
            ruleXRelationType();

            state._fsp--;

             after(grammarAccess.getOseeDslAccess().getRelationTypesXRelationTypeParserRuleCall_1_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__RelationTypesAssignment_1_1"


    // $ANTLR start "rule__OseeDsl__AttributeTypesAssignment_1_2"
    // InternalOseeDsl.g:9009:1: rule__OseeDsl__AttributeTypesAssignment_1_2 : ( ruleXAttributeType ) ;
    public final void rule__OseeDsl__AttributeTypesAssignment_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9013:1: ( ( ruleXAttributeType ) )
            // InternalOseeDsl.g:9014:1: ( ruleXAttributeType )
            {
            // InternalOseeDsl.g:9014:1: ( ruleXAttributeType )
            // InternalOseeDsl.g:9015:1: ruleXAttributeType
            {
             before(grammarAccess.getOseeDslAccess().getAttributeTypesXAttributeTypeParserRuleCall_1_2_0()); 
            pushFollow(FOLLOW_2);
            ruleXAttributeType();

            state._fsp--;

             after(grammarAccess.getOseeDslAccess().getAttributeTypesXAttributeTypeParserRuleCall_1_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__AttributeTypesAssignment_1_2"


    // $ANTLR start "rule__OseeDsl__EnumTypesAssignment_1_3"
    // InternalOseeDsl.g:9024:1: rule__OseeDsl__EnumTypesAssignment_1_3 : ( ruleXOseeEnumType ) ;
    public final void rule__OseeDsl__EnumTypesAssignment_1_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9028:1: ( ( ruleXOseeEnumType ) )
            // InternalOseeDsl.g:9029:1: ( ruleXOseeEnumType )
            {
            // InternalOseeDsl.g:9029:1: ( ruleXOseeEnumType )
            // InternalOseeDsl.g:9030:1: ruleXOseeEnumType
            {
             before(grammarAccess.getOseeDslAccess().getEnumTypesXOseeEnumTypeParserRuleCall_1_3_0()); 
            pushFollow(FOLLOW_2);
            ruleXOseeEnumType();

            state._fsp--;

             after(grammarAccess.getOseeDslAccess().getEnumTypesXOseeEnumTypeParserRuleCall_1_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__EnumTypesAssignment_1_3"


    // $ANTLR start "rule__OseeDsl__EnumOverridesAssignment_1_4"
    // InternalOseeDsl.g:9039:1: rule__OseeDsl__EnumOverridesAssignment_1_4 : ( ruleXOseeEnumOverride ) ;
    public final void rule__OseeDsl__EnumOverridesAssignment_1_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9043:1: ( ( ruleXOseeEnumOverride ) )
            // InternalOseeDsl.g:9044:1: ( ruleXOseeEnumOverride )
            {
            // InternalOseeDsl.g:9044:1: ( ruleXOseeEnumOverride )
            // InternalOseeDsl.g:9045:1: ruleXOseeEnumOverride
            {
             before(grammarAccess.getOseeDslAccess().getEnumOverridesXOseeEnumOverrideParserRuleCall_1_4_0()); 
            pushFollow(FOLLOW_2);
            ruleXOseeEnumOverride();

            state._fsp--;

             after(grammarAccess.getOseeDslAccess().getEnumOverridesXOseeEnumOverrideParserRuleCall_1_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__EnumOverridesAssignment_1_4"


    // $ANTLR start "rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5"
    // InternalOseeDsl.g:9054:1: rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5 : ( ruleXOseeArtifactTypeOverride ) ;
    public final void rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9058:1: ( ( ruleXOseeArtifactTypeOverride ) )
            // InternalOseeDsl.g:9059:1: ( ruleXOseeArtifactTypeOverride )
            {
            // InternalOseeDsl.g:9059:1: ( ruleXOseeArtifactTypeOverride )
            // InternalOseeDsl.g:9060:1: ruleXOseeArtifactTypeOverride
            {
             before(grammarAccess.getOseeDslAccess().getArtifactTypeOverridesXOseeArtifactTypeOverrideParserRuleCall_1_5_0()); 
            pushFollow(FOLLOW_2);
            ruleXOseeArtifactTypeOverride();

            state._fsp--;

             after(grammarAccess.getOseeDslAccess().getArtifactTypeOverridesXOseeArtifactTypeOverrideParserRuleCall_1_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5"


    // $ANTLR start "rule__OseeDsl__ArtifactMatchRefsAssignment_2_0"
    // InternalOseeDsl.g:9069:1: rule__OseeDsl__ArtifactMatchRefsAssignment_2_0 : ( ruleXArtifactMatcher ) ;
    public final void rule__OseeDsl__ArtifactMatchRefsAssignment_2_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9073:1: ( ( ruleXArtifactMatcher ) )
            // InternalOseeDsl.g:9074:1: ( ruleXArtifactMatcher )
            {
            // InternalOseeDsl.g:9074:1: ( ruleXArtifactMatcher )
            // InternalOseeDsl.g:9075:1: ruleXArtifactMatcher
            {
             before(grammarAccess.getOseeDslAccess().getArtifactMatchRefsXArtifactMatcherParserRuleCall_2_0_0()); 
            pushFollow(FOLLOW_2);
            ruleXArtifactMatcher();

            state._fsp--;

             after(grammarAccess.getOseeDslAccess().getArtifactMatchRefsXArtifactMatcherParserRuleCall_2_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__ArtifactMatchRefsAssignment_2_0"


    // $ANTLR start "rule__OseeDsl__AccessDeclarationsAssignment_2_1"
    // InternalOseeDsl.g:9084:1: rule__OseeDsl__AccessDeclarationsAssignment_2_1 : ( ruleAccessContext ) ;
    public final void rule__OseeDsl__AccessDeclarationsAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9088:1: ( ( ruleAccessContext ) )
            // InternalOseeDsl.g:9089:1: ( ruleAccessContext )
            {
            // InternalOseeDsl.g:9089:1: ( ruleAccessContext )
            // InternalOseeDsl.g:9090:1: ruleAccessContext
            {
             before(grammarAccess.getOseeDslAccess().getAccessDeclarationsAccessContextParserRuleCall_2_1_0()); 
            pushFollow(FOLLOW_2);
            ruleAccessContext();

            state._fsp--;

             after(grammarAccess.getOseeDslAccess().getAccessDeclarationsAccessContextParserRuleCall_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__AccessDeclarationsAssignment_2_1"


    // $ANTLR start "rule__OseeDsl__RoleDeclarationsAssignment_2_2"
    // InternalOseeDsl.g:9099:1: rule__OseeDsl__RoleDeclarationsAssignment_2_2 : ( ruleRole ) ;
    public final void rule__OseeDsl__RoleDeclarationsAssignment_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9103:1: ( ( ruleRole ) )
            // InternalOseeDsl.g:9104:1: ( ruleRole )
            {
            // InternalOseeDsl.g:9104:1: ( ruleRole )
            // InternalOseeDsl.g:9105:1: ruleRole
            {
             before(grammarAccess.getOseeDslAccess().getRoleDeclarationsRoleParserRuleCall_2_2_0()); 
            pushFollow(FOLLOW_2);
            ruleRole();

            state._fsp--;

             after(grammarAccess.getOseeDslAccess().getRoleDeclarationsRoleParserRuleCall_2_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__OseeDsl__RoleDeclarationsAssignment_2_2"


    // $ANTLR start "rule__Import__ImportURIAssignment_1"
    // InternalOseeDsl.g:9114:1: rule__Import__ImportURIAssignment_1 : ( RULE_STRING ) ;
    public final void rule__Import__ImportURIAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9118:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9119:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9119:1: ( RULE_STRING )
            // InternalOseeDsl.g:9120:1: RULE_STRING
            {
             before(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
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
    // $ANTLR end "rule__Import__ImportURIAssignment_1"


    // $ANTLR start "rule__XArtifactType__AbstractAssignment_0"
    // InternalOseeDsl.g:9129:1: rule__XArtifactType__AbstractAssignment_0 : ( ( 'abstract' ) ) ;
    public final void rule__XArtifactType__AbstractAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9133:1: ( ( ( 'abstract' ) ) )
            // InternalOseeDsl.g:9134:1: ( ( 'abstract' ) )
            {
            // InternalOseeDsl.g:9134:1: ( ( 'abstract' ) )
            // InternalOseeDsl.g:9135:1: ( 'abstract' )
            {
             before(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            // InternalOseeDsl.g:9136:1: ( 'abstract' )
            // InternalOseeDsl.g:9137:1: 'abstract'
            {
             before(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 
            match(input,97,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 

            }

             after(grammarAccess.getXArtifactTypeAccess().getAbstractAbstractKeyword_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__AbstractAssignment_0"


    // $ANTLR start "rule__XArtifactType__NameAssignment_2"
    // InternalOseeDsl.g:9152:1: rule__XArtifactType__NameAssignment_2 : ( RULE_STRING ) ;
    public final void rule__XArtifactType__NameAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9156:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9157:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9157:1: ( RULE_STRING )
            // InternalOseeDsl.g:9158:1: RULE_STRING
            {
             before(grammarAccess.getXArtifactTypeAccess().getNameSTRINGTerminalRuleCall_2_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getNameSTRINGTerminalRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__NameAssignment_2"


    // $ANTLR start "rule__XArtifactType__SuperArtifactTypesAssignment_3_1"
    // InternalOseeDsl.g:9167:1: rule__XArtifactType__SuperArtifactTypesAssignment_3_1 : ( ( RULE_STRING ) ) ;
    public final void rule__XArtifactType__SuperArtifactTypesAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9171:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9172:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9172:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9173:1: ( RULE_STRING )
            {
             before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_1_0()); 
            // InternalOseeDsl.g:9174:1: ( RULE_STRING )
            // InternalOseeDsl.g:9175:1: RULE_STRING
            {
             before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeSTRINGTerminalRuleCall_3_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeSTRINGTerminalRuleCall_3_1_0_1()); 

            }

             after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__SuperArtifactTypesAssignment_3_1"


    // $ANTLR start "rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1"
    // InternalOseeDsl.g:9186:1: rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1 : ( ( RULE_STRING ) ) ;
    public final void rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9190:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9191:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9191:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9192:1: ( RULE_STRING )
            {
             before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_2_1_0()); 
            // InternalOseeDsl.g:9193:1: ( RULE_STRING )
            // InternalOseeDsl.g:9194:1: RULE_STRING
            {
             before(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeSTRINGTerminalRuleCall_3_2_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeSTRINGTerminalRuleCall_3_2_1_0_1()); 

            }

             after(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesXArtifactTypeCrossReference_3_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1"


    // $ANTLR start "rule__XArtifactType__TypeGuidAssignment_5_1"
    // InternalOseeDsl.g:9205:1: rule__XArtifactType__TypeGuidAssignment_5_1 : ( RULE_STRING ) ;
    public final void rule__XArtifactType__TypeGuidAssignment_5_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9209:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9210:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9210:1: ( RULE_STRING )
            // InternalOseeDsl.g:9211:1: RULE_STRING
            {
             before(grammarAccess.getXArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__TypeGuidAssignment_5_1"


    // $ANTLR start "rule__XArtifactType__IdAssignment_7"
    // InternalOseeDsl.g:9220:1: rule__XArtifactType__IdAssignment_7 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XArtifactType__IdAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9224:1: ( ( RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:9225:1: ( RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:9225:1: ( RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:9226:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getXArtifactTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_7_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_2); 
             after(grammarAccess.getXArtifactTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_7_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__IdAssignment_7"


    // $ANTLR start "rule__XArtifactType__ValidAttributeTypesAssignment_8"
    // InternalOseeDsl.g:9235:1: rule__XArtifactType__ValidAttributeTypesAssignment_8 : ( ruleXAttributeTypeRef ) ;
    public final void rule__XArtifactType__ValidAttributeTypesAssignment_8() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9239:1: ( ( ruleXAttributeTypeRef ) )
            // InternalOseeDsl.g:9240:1: ( ruleXAttributeTypeRef )
            {
            // InternalOseeDsl.g:9240:1: ( ruleXAttributeTypeRef )
            // InternalOseeDsl.g:9241:1: ruleXAttributeTypeRef
            {
             before(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_8_0()); 
            pushFollow(FOLLOW_2);
            ruleXAttributeTypeRef();

            state._fsp--;

             after(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesXAttributeTypeRefParserRuleCall_8_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactType__ValidAttributeTypesAssignment_8"


    // $ANTLR start "rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1"
    // InternalOseeDsl.g:9250:1: rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1 : ( ( RULE_STRING ) ) ;
    public final void rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9254:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9255:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9255:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9256:1: ( RULE_STRING )
            {
             before(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0()); 
            // InternalOseeDsl.g:9257:1: ( RULE_STRING )
            // InternalOseeDsl.g:9258:1: RULE_STRING
            {
             before(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeSTRINGTerminalRuleCall_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeSTRINGTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeXAttributeTypeCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1"


    // $ANTLR start "rule__XAttributeTypeRef__BranchUuidAssignment_2_1"
    // InternalOseeDsl.g:9269:1: rule__XAttributeTypeRef__BranchUuidAssignment_2_1 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XAttributeTypeRef__BranchUuidAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9273:1: ( ( RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:9274:1: ( RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:9274:1: ( RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:9275:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getXAttributeTypeRefAccess().getBranchUuidWHOLE_NUM_STRTerminalRuleCall_2_1_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeRefAccess().getBranchUuidWHOLE_NUM_STRTerminalRuleCall_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeTypeRef__BranchUuidAssignment_2_1"


    // $ANTLR start "rule__XAttributeType__NameAssignment_1"
    // InternalOseeDsl.g:9284:1: rule__XAttributeType__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9288:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9289:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9289:1: ( RULE_STRING )
            // InternalOseeDsl.g:9290:1: RULE_STRING
            {
             before(grammarAccess.getXAttributeTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__NameAssignment_1"


    // $ANTLR start "rule__XAttributeType__BaseAttributeTypeAssignment_2_1"
    // InternalOseeDsl.g:9299:1: rule__XAttributeType__BaseAttributeTypeAssignment_2_1 : ( ruleAttributeBaseType ) ;
    public final void rule__XAttributeType__BaseAttributeTypeAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9303:1: ( ( ruleAttributeBaseType ) )
            // InternalOseeDsl.g:9304:1: ( ruleAttributeBaseType )
            {
            // InternalOseeDsl.g:9304:1: ( ruleAttributeBaseType )
            // InternalOseeDsl.g:9305:1: ruleAttributeBaseType
            {
             before(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 
            pushFollow(FOLLOW_2);
            ruleAttributeBaseType();

            state._fsp--;

             after(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAttributeBaseTypeParserRuleCall_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__BaseAttributeTypeAssignment_2_1"


    // $ANTLR start "rule__XAttributeType__OverrideAssignment_3_1"
    // InternalOseeDsl.g:9314:1: rule__XAttributeType__OverrideAssignment_3_1 : ( ( RULE_STRING ) ) ;
    public final void rule__XAttributeType__OverrideAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9318:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9319:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9319:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9320:1: ( RULE_STRING )
            {
             before(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0()); 
            // InternalOseeDsl.g:9321:1: ( RULE_STRING )
            // InternalOseeDsl.g:9322:1: RULE_STRING
            {
             before(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeSTRINGTerminalRuleCall_3_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeSTRINGTerminalRuleCall_3_1_0_1()); 

            }

             after(grammarAccess.getXAttributeTypeAccess().getOverrideXAttributeTypeCrossReference_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__OverrideAssignment_3_1"


    // $ANTLR start "rule__XAttributeType__TypeGuidAssignment_5_1"
    // InternalOseeDsl.g:9333:1: rule__XAttributeType__TypeGuidAssignment_5_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__TypeGuidAssignment_5_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9337:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9338:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9338:1: ( RULE_STRING )
            // InternalOseeDsl.g:9339:1: RULE_STRING
            {
             before(grammarAccess.getXAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getTypeGuidSTRINGTerminalRuleCall_5_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__TypeGuidAssignment_5_1"


    // $ANTLR start "rule__XAttributeType__IdAssignment_7"
    // InternalOseeDsl.g:9348:1: rule__XAttributeType__IdAssignment_7 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XAttributeType__IdAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9352:1: ( ( RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:9353:1: ( RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:9353:1: ( RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:9354:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getXAttributeTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_7_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_7_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__IdAssignment_7"


    // $ANTLR start "rule__XAttributeType__DataProviderAssignment_9"
    // InternalOseeDsl.g:9363:1: rule__XAttributeType__DataProviderAssignment_9 : ( ( rule__XAttributeType__DataProviderAlternatives_9_0 ) ) ;
    public final void rule__XAttributeType__DataProviderAssignment_9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9367:1: ( ( ( rule__XAttributeType__DataProviderAlternatives_9_0 ) ) )
            // InternalOseeDsl.g:9368:1: ( ( rule__XAttributeType__DataProviderAlternatives_9_0 ) )
            {
            // InternalOseeDsl.g:9368:1: ( ( rule__XAttributeType__DataProviderAlternatives_9_0 ) )
            // InternalOseeDsl.g:9369:1: ( rule__XAttributeType__DataProviderAlternatives_9_0 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getDataProviderAlternatives_9_0()); 
            // InternalOseeDsl.g:9370:1: ( rule__XAttributeType__DataProviderAlternatives_9_0 )
            // InternalOseeDsl.g:9370:2: rule__XAttributeType__DataProviderAlternatives_9_0
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__DataProviderAlternatives_9_0();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getDataProviderAlternatives_9_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__DataProviderAssignment_9"


    // $ANTLR start "rule__XAttributeType__MinAssignment_11"
    // InternalOseeDsl.g:9379:1: rule__XAttributeType__MinAssignment_11 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XAttributeType__MinAssignment_11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9383:1: ( ( RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:9384:1: ( RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:9384:1: ( RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:9385:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getXAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_11_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getMinWHOLE_NUM_STRTerminalRuleCall_11_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__MinAssignment_11"


    // $ANTLR start "rule__XAttributeType__MaxAssignment_13"
    // InternalOseeDsl.g:9394:1: rule__XAttributeType__MaxAssignment_13 : ( ( rule__XAttributeType__MaxAlternatives_13_0 ) ) ;
    public final void rule__XAttributeType__MaxAssignment_13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9398:1: ( ( ( rule__XAttributeType__MaxAlternatives_13_0 ) ) )
            // InternalOseeDsl.g:9399:1: ( ( rule__XAttributeType__MaxAlternatives_13_0 ) )
            {
            // InternalOseeDsl.g:9399:1: ( ( rule__XAttributeType__MaxAlternatives_13_0 ) )
            // InternalOseeDsl.g:9400:1: ( rule__XAttributeType__MaxAlternatives_13_0 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getMaxAlternatives_13_0()); 
            // InternalOseeDsl.g:9401:1: ( rule__XAttributeType__MaxAlternatives_13_0 )
            // InternalOseeDsl.g:9401:2: rule__XAttributeType__MaxAlternatives_13_0
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__MaxAlternatives_13_0();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getMaxAlternatives_13_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__MaxAssignment_13"


    // $ANTLR start "rule__XAttributeType__TaggerIdAssignment_14_0_1"
    // InternalOseeDsl.g:9410:1: rule__XAttributeType__TaggerIdAssignment_14_0_1 : ( ( rule__XAttributeType__TaggerIdAlternatives_14_0_1_0 ) ) ;
    public final void rule__XAttributeType__TaggerIdAssignment_14_0_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9414:1: ( ( ( rule__XAttributeType__TaggerIdAlternatives_14_0_1_0 ) ) )
            // InternalOseeDsl.g:9415:1: ( ( rule__XAttributeType__TaggerIdAlternatives_14_0_1_0 ) )
            {
            // InternalOseeDsl.g:9415:1: ( ( rule__XAttributeType__TaggerIdAlternatives_14_0_1_0 ) )
            // InternalOseeDsl.g:9416:1: ( rule__XAttributeType__TaggerIdAlternatives_14_0_1_0 )
            {
             before(grammarAccess.getXAttributeTypeAccess().getTaggerIdAlternatives_14_0_1_0()); 
            // InternalOseeDsl.g:9417:1: ( rule__XAttributeType__TaggerIdAlternatives_14_0_1_0 )
            // InternalOseeDsl.g:9417:2: rule__XAttributeType__TaggerIdAlternatives_14_0_1_0
            {
            pushFollow(FOLLOW_2);
            rule__XAttributeType__TaggerIdAlternatives_14_0_1_0();

            state._fsp--;


            }

             after(grammarAccess.getXAttributeTypeAccess().getTaggerIdAlternatives_14_0_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__TaggerIdAssignment_14_0_1"


    // $ANTLR start "rule__XAttributeType__EnumTypeAssignment_14_1_1"
    // InternalOseeDsl.g:9426:1: rule__XAttributeType__EnumTypeAssignment_14_1_1 : ( ( RULE_STRING ) ) ;
    public final void rule__XAttributeType__EnumTypeAssignment_14_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9430:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9431:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9431:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9432:1: ( RULE_STRING )
            {
             before(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_14_1_1_0()); 
            // InternalOseeDsl.g:9433:1: ( RULE_STRING )
            // InternalOseeDsl.g:9434:1: RULE_STRING
            {
             before(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeSTRINGTerminalRuleCall_14_1_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeSTRINGTerminalRuleCall_14_1_1_0_1()); 

            }

             after(grammarAccess.getXAttributeTypeAccess().getEnumTypeXOseeEnumTypeCrossReference_14_1_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__EnumTypeAssignment_14_1_1"


    // $ANTLR start "rule__XAttributeType__DescriptionAssignment_14_2_1"
    // InternalOseeDsl.g:9445:1: rule__XAttributeType__DescriptionAssignment_14_2_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__DescriptionAssignment_14_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9449:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9450:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9450:1: ( RULE_STRING )
            // InternalOseeDsl.g:9451:1: RULE_STRING
            {
             before(grammarAccess.getXAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_14_2_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getDescriptionSTRINGTerminalRuleCall_14_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__DescriptionAssignment_14_2_1"


    // $ANTLR start "rule__XAttributeType__DefaultValueAssignment_14_3_1"
    // InternalOseeDsl.g:9460:1: rule__XAttributeType__DefaultValueAssignment_14_3_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__DefaultValueAssignment_14_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9464:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9465:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9465:1: ( RULE_STRING )
            // InternalOseeDsl.g:9466:1: RULE_STRING
            {
             before(grammarAccess.getXAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_14_3_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getDefaultValueSTRINGTerminalRuleCall_14_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__DefaultValueAssignment_14_3_1"


    // $ANTLR start "rule__XAttributeType__FileExtensionAssignment_14_4_1"
    // InternalOseeDsl.g:9475:1: rule__XAttributeType__FileExtensionAssignment_14_4_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__FileExtensionAssignment_14_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9479:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9480:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9480:1: ( RULE_STRING )
            // InternalOseeDsl.g:9481:1: RULE_STRING
            {
             before(grammarAccess.getXAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_14_4_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getFileExtensionSTRINGTerminalRuleCall_14_4_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__FileExtensionAssignment_14_4_1"


    // $ANTLR start "rule__XAttributeType__MediaTypeAssignment_14_5_1"
    // InternalOseeDsl.g:9490:1: rule__XAttributeType__MediaTypeAssignment_14_5_1 : ( RULE_STRING ) ;
    public final void rule__XAttributeType__MediaTypeAssignment_14_5_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9494:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9495:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9495:1: ( RULE_STRING )
            // InternalOseeDsl.g:9496:1: RULE_STRING
            {
             before(grammarAccess.getXAttributeTypeAccess().getMediaTypeSTRINGTerminalRuleCall_14_5_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXAttributeTypeAccess().getMediaTypeSTRINGTerminalRuleCall_14_5_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XAttributeType__MediaTypeAssignment_14_5_1"


    // $ANTLR start "rule__XOseeEnumType__NameAssignment_1"
    // InternalOseeDsl.g:9505:1: rule__XOseeEnumType__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__XOseeEnumType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9509:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9510:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9510:1: ( RULE_STRING )
            // InternalOseeDsl.g:9511:1: RULE_STRING
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__NameAssignment_1"


    // $ANTLR start "rule__XOseeEnumType__TypeGuidAssignment_3_1"
    // InternalOseeDsl.g:9520:1: rule__XOseeEnumType__TypeGuidAssignment_3_1 : ( RULE_STRING ) ;
    public final void rule__XOseeEnumType__TypeGuidAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9524:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9525:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9525:1: ( RULE_STRING )
            // InternalOseeDsl.g:9526:1: RULE_STRING
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__TypeGuidAssignment_3_1"


    // $ANTLR start "rule__XOseeEnumType__IdAssignment_5"
    // InternalOseeDsl.g:9535:1: rule__XOseeEnumType__IdAssignment_5 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XOseeEnumType__IdAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9539:1: ( ( RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:9540:1: ( RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:9540:1: ( RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:9541:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_5_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__IdAssignment_5"


    // $ANTLR start "rule__XOseeEnumType__EnumEntriesAssignment_6"
    // InternalOseeDsl.g:9550:1: rule__XOseeEnumType__EnumEntriesAssignment_6 : ( ruleXOseeEnumEntry ) ;
    public final void rule__XOseeEnumType__EnumEntriesAssignment_6() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9554:1: ( ( ruleXOseeEnumEntry ) )
            // InternalOseeDsl.g:9555:1: ( ruleXOseeEnumEntry )
            {
            // InternalOseeDsl.g:9555:1: ( ruleXOseeEnumEntry )
            // InternalOseeDsl.g:9556:1: ruleXOseeEnumEntry
            {
             before(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_6_0()); 
            pushFollow(FOLLOW_2);
            ruleXOseeEnumEntry();

            state._fsp--;

             after(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesXOseeEnumEntryParserRuleCall_6_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumType__EnumEntriesAssignment_6"


    // $ANTLR start "rule__XOseeEnumEntry__NameAssignment_1"
    // InternalOseeDsl.g:9565:1: rule__XOseeEnumEntry__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__XOseeEnumEntry__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9569:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9570:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9570:1: ( RULE_STRING )
            // InternalOseeDsl.g:9571:1: RULE_STRING
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumEntryAccess().getNameSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__NameAssignment_1"


    // $ANTLR start "rule__XOseeEnumEntry__OrdinalAssignment_2"
    // InternalOseeDsl.g:9580:1: rule__XOseeEnumEntry__OrdinalAssignment_2 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XOseeEnumEntry__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9584:1: ( ( RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:9585:1: ( RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:9585:1: ( RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:9586:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumEntryAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__OrdinalAssignment_2"


    // $ANTLR start "rule__XOseeEnumEntry__EntryGuidAssignment_3_1"
    // InternalOseeDsl.g:9595:1: rule__XOseeEnumEntry__EntryGuidAssignment_3_1 : ( RULE_STRING ) ;
    public final void rule__XOseeEnumEntry__EntryGuidAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9599:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9600:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9600:1: ( RULE_STRING )
            // InternalOseeDsl.g:9601:1: RULE_STRING
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__EntryGuidAssignment_3_1"


    // $ANTLR start "rule__XOseeEnumEntry__DescriptionAssignment_4_1"
    // InternalOseeDsl.g:9610:1: rule__XOseeEnumEntry__DescriptionAssignment_4_1 : ( RULE_STRING ) ;
    public final void rule__XOseeEnumEntry__DescriptionAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9614:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9615:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9615:1: ( RULE_STRING )
            // InternalOseeDsl.g:9616:1: RULE_STRING
            {
             before(grammarAccess.getXOseeEnumEntryAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumEntryAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumEntry__DescriptionAssignment_4_1"


    // $ANTLR start "rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1"
    // InternalOseeDsl.g:9625:1: rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1 : ( ( RULE_STRING ) ) ;
    public final void rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9629:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9630:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9630:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9631:1: ( RULE_STRING )
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0()); 
            // InternalOseeDsl.g:9632:1: ( RULE_STRING )
            // InternalOseeDsl.g:9633:1: RULE_STRING
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeSTRINGTerminalRuleCall_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeSTRINGTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeXOseeEnumTypeCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1"


    // $ANTLR start "rule__XOseeEnumOverride__InheritAllAssignment_3"
    // InternalOseeDsl.g:9644:1: rule__XOseeEnumOverride__InheritAllAssignment_3 : ( ( 'inheritAll' ) ) ;
    public final void rule__XOseeEnumOverride__InheritAllAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9648:1: ( ( ( 'inheritAll' ) ) )
            // InternalOseeDsl.g:9649:1: ( ( 'inheritAll' ) )
            {
            // InternalOseeDsl.g:9649:1: ( ( 'inheritAll' ) )
            // InternalOseeDsl.g:9650:1: ( 'inheritAll' )
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            // InternalOseeDsl.g:9651:1: ( 'inheritAll' )
            // InternalOseeDsl.g:9652:1: 'inheritAll'
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            match(input,98,FOLLOW_2); 
             after(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 

            }

             after(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__InheritAllAssignment_3"


    // $ANTLR start "rule__XOseeEnumOverride__OverrideOptionsAssignment_4"
    // InternalOseeDsl.g:9667:1: rule__XOseeEnumOverride__OverrideOptionsAssignment_4 : ( ruleOverrideOption ) ;
    public final void rule__XOseeEnumOverride__OverrideOptionsAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9671:1: ( ( ruleOverrideOption ) )
            // InternalOseeDsl.g:9672:1: ( ruleOverrideOption )
            {
            // InternalOseeDsl.g:9672:1: ( ruleOverrideOption )
            // InternalOseeDsl.g:9673:1: ruleOverrideOption
            {
             before(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 
            pushFollow(FOLLOW_2);
            ruleOverrideOption();

            state._fsp--;

             after(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsOverrideOptionParserRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeEnumOverride__OverrideOptionsAssignment_4"


    // $ANTLR start "rule__AddEnum__EnumEntryAssignment_1"
    // InternalOseeDsl.g:9682:1: rule__AddEnum__EnumEntryAssignment_1 : ( RULE_STRING ) ;
    public final void rule__AddEnum__EnumEntryAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9686:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9687:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9687:1: ( RULE_STRING )
            // InternalOseeDsl.g:9688:1: RULE_STRING
            {
             before(grammarAccess.getAddEnumAccess().getEnumEntrySTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getAddEnumAccess().getEnumEntrySTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__EnumEntryAssignment_1"


    // $ANTLR start "rule__AddEnum__OrdinalAssignment_2"
    // InternalOseeDsl.g:9697:1: rule__AddEnum__OrdinalAssignment_2 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__AddEnum__OrdinalAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9701:1: ( ( RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:9702:1: ( RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:9702:1: ( RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:9703:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getAddEnumAccess().getOrdinalWHOLE_NUM_STRTerminalRuleCall_2_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_2); 
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
    // $ANTLR end "rule__AddEnum__OrdinalAssignment_2"


    // $ANTLR start "rule__AddEnum__EntryGuidAssignment_3_1"
    // InternalOseeDsl.g:9712:1: rule__AddEnum__EntryGuidAssignment_3_1 : ( RULE_STRING ) ;
    public final void rule__AddEnum__EntryGuidAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9716:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9717:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9717:1: ( RULE_STRING )
            // InternalOseeDsl.g:9718:1: RULE_STRING
            {
             before(grammarAccess.getAddEnumAccess().getEntryGuidSTRINGTerminalRuleCall_3_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
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
    // $ANTLR end "rule__AddEnum__EntryGuidAssignment_3_1"


    // $ANTLR start "rule__AddEnum__DescriptionAssignment_4_1"
    // InternalOseeDsl.g:9727:1: rule__AddEnum__DescriptionAssignment_4_1 : ( RULE_STRING ) ;
    public final void rule__AddEnum__DescriptionAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9731:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9732:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9732:1: ( RULE_STRING )
            // InternalOseeDsl.g:9733:1: RULE_STRING
            {
             before(grammarAccess.getAddEnumAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getAddEnumAccess().getDescriptionSTRINGTerminalRuleCall_4_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddEnum__DescriptionAssignment_4_1"


    // $ANTLR start "rule__RemoveEnum__EnumEntryAssignment_1"
    // InternalOseeDsl.g:9742:1: rule__RemoveEnum__EnumEntryAssignment_1 : ( ( RULE_STRING ) ) ;
    public final void rule__RemoveEnum__EnumEntryAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9746:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9747:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9747:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9748:1: ( RULE_STRING )
            {
             before(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryCrossReference_1_0()); 
            // InternalOseeDsl.g:9749:1: ( RULE_STRING )
            // InternalOseeDsl.g:9750:1: RULE_STRING
            {
             before(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntrySTRINGTerminalRuleCall_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntrySTRINGTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getRemoveEnumAccess().getEnumEntryXOseeEnumEntryCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveEnum__EnumEntryAssignment_1"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1"
    // InternalOseeDsl.g:9761:1: rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1 : ( ( RULE_STRING ) ) ;
    public final void rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9765:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9766:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9766:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9767:1: ( RULE_STRING )
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridenArtifactTypeXArtifactTypeCrossReference_1_0()); 
            // InternalOseeDsl.g:9768:1: ( RULE_STRING )
            // InternalOseeDsl.g:9769:1: RULE_STRING
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridenArtifactTypeXArtifactTypeSTRINGTerminalRuleCall_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridenArtifactTypeXArtifactTypeSTRINGTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridenArtifactTypeXArtifactTypeCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__InheritAllAssignment_3"
    // InternalOseeDsl.g:9780:1: rule__XOseeArtifactTypeOverride__InheritAllAssignment_3 : ( ( 'inheritAll' ) ) ;
    public final void rule__XOseeArtifactTypeOverride__InheritAllAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9784:1: ( ( ( 'inheritAll' ) ) )
            // InternalOseeDsl.g:9785:1: ( ( 'inheritAll' ) )
            {
            // InternalOseeDsl.g:9785:1: ( ( 'inheritAll' ) )
            // InternalOseeDsl.g:9786:1: ( 'inheritAll' )
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            // InternalOseeDsl.g:9787:1: ( 'inheritAll' )
            // InternalOseeDsl.g:9788:1: 'inheritAll'
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 
            match(input,98,FOLLOW_2); 
             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 

            }

             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getInheritAllInheritAllKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__InheritAllAssignment_3"


    // $ANTLR start "rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4"
    // InternalOseeDsl.g:9803:1: rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4 : ( ruleAttributeOverrideOption ) ;
    public final void rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9807:1: ( ( ruleAttributeOverrideOption ) )
            // InternalOseeDsl.g:9808:1: ( ruleAttributeOverrideOption )
            {
            // InternalOseeDsl.g:9808:1: ( ruleAttributeOverrideOption )
            // InternalOseeDsl.g:9809:1: ruleAttributeOverrideOption
            {
             before(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverrideOptionsAttributeOverrideOptionParserRuleCall_4_0()); 
            pushFollow(FOLLOW_2);
            ruleAttributeOverrideOption();

            state._fsp--;

             after(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverrideOptionsAttributeOverrideOptionParserRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4"


    // $ANTLR start "rule__AddAttribute__AttributeAssignment_1"
    // InternalOseeDsl.g:9818:1: rule__AddAttribute__AttributeAssignment_1 : ( ruleXAttributeTypeRef ) ;
    public final void rule__AddAttribute__AttributeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9822:1: ( ( ruleXAttributeTypeRef ) )
            // InternalOseeDsl.g:9823:1: ( ruleXAttributeTypeRef )
            {
            // InternalOseeDsl.g:9823:1: ( ruleXAttributeTypeRef )
            // InternalOseeDsl.g:9824:1: ruleXAttributeTypeRef
            {
             before(grammarAccess.getAddAttributeAccess().getAttributeXAttributeTypeRefParserRuleCall_1_0()); 
            pushFollow(FOLLOW_2);
            ruleXAttributeTypeRef();

            state._fsp--;

             after(grammarAccess.getAddAttributeAccess().getAttributeXAttributeTypeRefParserRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AddAttribute__AttributeAssignment_1"


    // $ANTLR start "rule__RemoveAttribute__AttributeAssignment_2"
    // InternalOseeDsl.g:9833:1: rule__RemoveAttribute__AttributeAssignment_2 : ( ( RULE_STRING ) ) ;
    public final void rule__RemoveAttribute__AttributeAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9837:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9838:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9838:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9839:1: ( RULE_STRING )
            {
             before(grammarAccess.getRemoveAttributeAccess().getAttributeXAttributeTypeCrossReference_2_0()); 
            // InternalOseeDsl.g:9840:1: ( RULE_STRING )
            // InternalOseeDsl.g:9841:1: RULE_STRING
            {
             before(grammarAccess.getRemoveAttributeAccess().getAttributeXAttributeTypeSTRINGTerminalRuleCall_2_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getRemoveAttributeAccess().getAttributeXAttributeTypeSTRINGTerminalRuleCall_2_0_1()); 

            }

             after(grammarAccess.getRemoveAttributeAccess().getAttributeXAttributeTypeCrossReference_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RemoveAttribute__AttributeAssignment_2"


    // $ANTLR start "rule__UpdateAttribute__AttributeAssignment_1"
    // InternalOseeDsl.g:9852:1: rule__UpdateAttribute__AttributeAssignment_1 : ( ruleXAttributeTypeRef ) ;
    public final void rule__UpdateAttribute__AttributeAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9856:1: ( ( ruleXAttributeTypeRef ) )
            // InternalOseeDsl.g:9857:1: ( ruleXAttributeTypeRef )
            {
            // InternalOseeDsl.g:9857:1: ( ruleXAttributeTypeRef )
            // InternalOseeDsl.g:9858:1: ruleXAttributeTypeRef
            {
             before(grammarAccess.getUpdateAttributeAccess().getAttributeXAttributeTypeRefParserRuleCall_1_0()); 
            pushFollow(FOLLOW_2);
            ruleXAttributeTypeRef();

            state._fsp--;

             after(grammarAccess.getUpdateAttributeAccess().getAttributeXAttributeTypeRefParserRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UpdateAttribute__AttributeAssignment_1"


    // $ANTLR start "rule__XRelationType__NameAssignment_1"
    // InternalOseeDsl.g:9867:1: rule__XRelationType__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__XRelationType__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9871:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9872:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9872:1: ( RULE_STRING )
            // InternalOseeDsl.g:9873:1: RULE_STRING
            {
             before(grammarAccess.getXRelationTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getNameSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__NameAssignment_1"


    // $ANTLR start "rule__XRelationType__TypeGuidAssignment_3_1"
    // InternalOseeDsl.g:9882:1: rule__XRelationType__TypeGuidAssignment_3_1 : ( RULE_STRING ) ;
    public final void rule__XRelationType__TypeGuidAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9886:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9887:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9887:1: ( RULE_STRING )
            // InternalOseeDsl.g:9888:1: RULE_STRING
            {
             before(grammarAccess.getXRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getTypeGuidSTRINGTerminalRuleCall_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__TypeGuidAssignment_3_1"


    // $ANTLR start "rule__XRelationType__IdAssignment_5"
    // InternalOseeDsl.g:9897:1: rule__XRelationType__IdAssignment_5 : ( RULE_WHOLE_NUM_STR ) ;
    public final void rule__XRelationType__IdAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9901:1: ( ( RULE_WHOLE_NUM_STR ) )
            // InternalOseeDsl.g:9902:1: ( RULE_WHOLE_NUM_STR )
            {
            // InternalOseeDsl.g:9902:1: ( RULE_WHOLE_NUM_STR )
            // InternalOseeDsl.g:9903:1: RULE_WHOLE_NUM_STR
            {
             before(grammarAccess.getXRelationTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_5_0()); 
            match(input,RULE_WHOLE_NUM_STR,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getIdWHOLE_NUM_STRTerminalRuleCall_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__IdAssignment_5"


    // $ANTLR start "rule__XRelationType__SideANameAssignment_7"
    // InternalOseeDsl.g:9912:1: rule__XRelationType__SideANameAssignment_7 : ( RULE_STRING ) ;
    public final void rule__XRelationType__SideANameAssignment_7() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9916:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9917:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9917:1: ( RULE_STRING )
            // InternalOseeDsl.g:9918:1: RULE_STRING
            {
             before(grammarAccess.getXRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_7_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getSideANameSTRINGTerminalRuleCall_7_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__SideANameAssignment_7"


    // $ANTLR start "rule__XRelationType__SideAArtifactTypeAssignment_9"
    // InternalOseeDsl.g:9927:1: rule__XRelationType__SideAArtifactTypeAssignment_9 : ( ( RULE_STRING ) ) ;
    public final void rule__XRelationType__SideAArtifactTypeAssignment_9() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9931:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9932:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9932:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9933:1: ( RULE_STRING )
            {
             before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_9_0()); 
            // InternalOseeDsl.g:9934:1: ( RULE_STRING )
            // InternalOseeDsl.g:9935:1: RULE_STRING
            {
             before(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeSTRINGTerminalRuleCall_9_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeSTRINGTerminalRuleCall_9_0_1()); 

            }

             after(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeXArtifactTypeCrossReference_9_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__SideAArtifactTypeAssignment_9"


    // $ANTLR start "rule__XRelationType__SideBNameAssignment_11"
    // InternalOseeDsl.g:9946:1: rule__XRelationType__SideBNameAssignment_11 : ( RULE_STRING ) ;
    public final void rule__XRelationType__SideBNameAssignment_11() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9950:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9951:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:9951:1: ( RULE_STRING )
            // InternalOseeDsl.g:9952:1: RULE_STRING
            {
             before(grammarAccess.getXRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_11_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getSideBNameSTRINGTerminalRuleCall_11_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__SideBNameAssignment_11"


    // $ANTLR start "rule__XRelationType__SideBArtifactTypeAssignment_13"
    // InternalOseeDsl.g:9961:1: rule__XRelationType__SideBArtifactTypeAssignment_13 : ( ( RULE_STRING ) ) ;
    public final void rule__XRelationType__SideBArtifactTypeAssignment_13() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9965:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:9966:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:9966:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:9967:1: ( RULE_STRING )
            {
             before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_13_0()); 
            // InternalOseeDsl.g:9968:1: ( RULE_STRING )
            // InternalOseeDsl.g:9969:1: RULE_STRING
            {
             before(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeSTRINGTerminalRuleCall_13_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeSTRINGTerminalRuleCall_13_0_1()); 

            }

             after(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeXArtifactTypeCrossReference_13_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__SideBArtifactTypeAssignment_13"


    // $ANTLR start "rule__XRelationType__DefaultOrderTypeAssignment_15"
    // InternalOseeDsl.g:9980:1: rule__XRelationType__DefaultOrderTypeAssignment_15 : ( ruleRelationOrderType ) ;
    public final void rule__XRelationType__DefaultOrderTypeAssignment_15() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9984:1: ( ( ruleRelationOrderType ) )
            // InternalOseeDsl.g:9985:1: ( ruleRelationOrderType )
            {
            // InternalOseeDsl.g:9985:1: ( ruleRelationOrderType )
            // InternalOseeDsl.g:9986:1: ruleRelationOrderType
            {
             before(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_15_0()); 
            pushFollow(FOLLOW_2);
            ruleRelationOrderType();

            state._fsp--;

             after(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeRelationOrderTypeParserRuleCall_15_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__DefaultOrderTypeAssignment_15"


    // $ANTLR start "rule__XRelationType__MultiplicityAssignment_17"
    // InternalOseeDsl.g:9995:1: rule__XRelationType__MultiplicityAssignment_17 : ( ruleRelationMultiplicityEnum ) ;
    public final void rule__XRelationType__MultiplicityAssignment_17() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:9999:1: ( ( ruleRelationMultiplicityEnum ) )
            // InternalOseeDsl.g:10000:1: ( ruleRelationMultiplicityEnum )
            {
            // InternalOseeDsl.g:10000:1: ( ruleRelationMultiplicityEnum )
            // InternalOseeDsl.g:10001:1: ruleRelationMultiplicityEnum
            {
             before(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_17_0()); 
            pushFollow(FOLLOW_2);
            ruleRelationMultiplicityEnum();

            state._fsp--;

             after(grammarAccess.getXRelationTypeAccess().getMultiplicityRelationMultiplicityEnumEnumRuleCall_17_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XRelationType__MultiplicityAssignment_17"


    // $ANTLR start "rule__SimpleCondition__FieldAssignment_0"
    // InternalOseeDsl.g:10010:1: rule__SimpleCondition__FieldAssignment_0 : ( ruleMatchField ) ;
    public final void rule__SimpleCondition__FieldAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10014:1: ( ( ruleMatchField ) )
            // InternalOseeDsl.g:10015:1: ( ruleMatchField )
            {
            // InternalOseeDsl.g:10015:1: ( ruleMatchField )
            // InternalOseeDsl.g:10016:1: ruleMatchField
            {
             before(grammarAccess.getSimpleConditionAccess().getFieldMatchFieldEnumRuleCall_0_0()); 
            pushFollow(FOLLOW_2);
            ruleMatchField();

            state._fsp--;

             after(grammarAccess.getSimpleConditionAccess().getFieldMatchFieldEnumRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleCondition__FieldAssignment_0"


    // $ANTLR start "rule__SimpleCondition__OpAssignment_1"
    // InternalOseeDsl.g:10025:1: rule__SimpleCondition__OpAssignment_1 : ( ruleCompareOp ) ;
    public final void rule__SimpleCondition__OpAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10029:1: ( ( ruleCompareOp ) )
            // InternalOseeDsl.g:10030:1: ( ruleCompareOp )
            {
            // InternalOseeDsl.g:10030:1: ( ruleCompareOp )
            // InternalOseeDsl.g:10031:1: ruleCompareOp
            {
             before(grammarAccess.getSimpleConditionAccess().getOpCompareOpEnumRuleCall_1_0()); 
            pushFollow(FOLLOW_2);
            ruleCompareOp();

            state._fsp--;

             after(grammarAccess.getSimpleConditionAccess().getOpCompareOpEnumRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleCondition__OpAssignment_1"


    // $ANTLR start "rule__SimpleCondition__ExpressionAssignment_2"
    // InternalOseeDsl.g:10040:1: rule__SimpleCondition__ExpressionAssignment_2 : ( RULE_STRING ) ;
    public final void rule__SimpleCondition__ExpressionAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10044:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10045:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:10045:1: ( RULE_STRING )
            // InternalOseeDsl.g:10046:1: RULE_STRING
            {
             before(grammarAccess.getSimpleConditionAccess().getExpressionSTRINGTerminalRuleCall_2_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getSimpleConditionAccess().getExpressionSTRINGTerminalRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__SimpleCondition__ExpressionAssignment_2"


    // $ANTLR start "rule__CompoundCondition__ConditionsAssignment_1"
    // InternalOseeDsl.g:10055:1: rule__CompoundCondition__ConditionsAssignment_1 : ( ruleSimpleCondition ) ;
    public final void rule__CompoundCondition__ConditionsAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10059:1: ( ( ruleSimpleCondition ) )
            // InternalOseeDsl.g:10060:1: ( ruleSimpleCondition )
            {
            // InternalOseeDsl.g:10060:1: ( ruleSimpleCondition )
            // InternalOseeDsl.g:10061:1: ruleSimpleCondition
            {
             before(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_1_0()); 
            pushFollow(FOLLOW_2);
            ruleSimpleCondition();

            state._fsp--;

             after(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__ConditionsAssignment_1"


    // $ANTLR start "rule__CompoundCondition__OperatorsAssignment_2_0"
    // InternalOseeDsl.g:10070:1: rule__CompoundCondition__OperatorsAssignment_2_0 : ( ruleXLogicOperator ) ;
    public final void rule__CompoundCondition__OperatorsAssignment_2_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10074:1: ( ( ruleXLogicOperator ) )
            // InternalOseeDsl.g:10075:1: ( ruleXLogicOperator )
            {
            // InternalOseeDsl.g:10075:1: ( ruleXLogicOperator )
            // InternalOseeDsl.g:10076:1: ruleXLogicOperator
            {
             before(grammarAccess.getCompoundConditionAccess().getOperatorsXLogicOperatorEnumRuleCall_2_0_0()); 
            pushFollow(FOLLOW_2);
            ruleXLogicOperator();

            state._fsp--;

             after(grammarAccess.getCompoundConditionAccess().getOperatorsXLogicOperatorEnumRuleCall_2_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__OperatorsAssignment_2_0"


    // $ANTLR start "rule__CompoundCondition__ConditionsAssignment_2_1"
    // InternalOseeDsl.g:10085:1: rule__CompoundCondition__ConditionsAssignment_2_1 : ( ruleSimpleCondition ) ;
    public final void rule__CompoundCondition__ConditionsAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10089:1: ( ( ruleSimpleCondition ) )
            // InternalOseeDsl.g:10090:1: ( ruleSimpleCondition )
            {
            // InternalOseeDsl.g:10090:1: ( ruleSimpleCondition )
            // InternalOseeDsl.g:10091:1: ruleSimpleCondition
            {
             before(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_2_1_0()); 
            pushFollow(FOLLOW_2);
            ruleSimpleCondition();

            state._fsp--;

             after(grammarAccess.getCompoundConditionAccess().getConditionsSimpleConditionParserRuleCall_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__CompoundCondition__ConditionsAssignment_2_1"


    // $ANTLR start "rule__XArtifactMatcher__NameAssignment_1"
    // InternalOseeDsl.g:10100:1: rule__XArtifactMatcher__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__XArtifactMatcher__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10104:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10105:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:10105:1: ( RULE_STRING )
            // InternalOseeDsl.g:10106:1: RULE_STRING
            {
             before(grammarAccess.getXArtifactMatcherAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getXArtifactMatcherAccess().getNameSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__NameAssignment_1"


    // $ANTLR start "rule__XArtifactMatcher__ConditionsAssignment_3"
    // InternalOseeDsl.g:10115:1: rule__XArtifactMatcher__ConditionsAssignment_3 : ( ruleCondition ) ;
    public final void rule__XArtifactMatcher__ConditionsAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10119:1: ( ( ruleCondition ) )
            // InternalOseeDsl.g:10120:1: ( ruleCondition )
            {
            // InternalOseeDsl.g:10120:1: ( ruleCondition )
            // InternalOseeDsl.g:10121:1: ruleCondition
            {
             before(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_3_0()); 
            pushFollow(FOLLOW_2);
            ruleCondition();

            state._fsp--;

             after(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__ConditionsAssignment_3"


    // $ANTLR start "rule__XArtifactMatcher__OperatorsAssignment_4_0"
    // InternalOseeDsl.g:10130:1: rule__XArtifactMatcher__OperatorsAssignment_4_0 : ( ruleXLogicOperator ) ;
    public final void rule__XArtifactMatcher__OperatorsAssignment_4_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10134:1: ( ( ruleXLogicOperator ) )
            // InternalOseeDsl.g:10135:1: ( ruleXLogicOperator )
            {
            // InternalOseeDsl.g:10135:1: ( ruleXLogicOperator )
            // InternalOseeDsl.g:10136:1: ruleXLogicOperator
            {
             before(grammarAccess.getXArtifactMatcherAccess().getOperatorsXLogicOperatorEnumRuleCall_4_0_0()); 
            pushFollow(FOLLOW_2);
            ruleXLogicOperator();

            state._fsp--;

             after(grammarAccess.getXArtifactMatcherAccess().getOperatorsXLogicOperatorEnumRuleCall_4_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__OperatorsAssignment_4_0"


    // $ANTLR start "rule__XArtifactMatcher__ConditionsAssignment_4_1"
    // InternalOseeDsl.g:10145:1: rule__XArtifactMatcher__ConditionsAssignment_4_1 : ( ruleCondition ) ;
    public final void rule__XArtifactMatcher__ConditionsAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10149:1: ( ( ruleCondition ) )
            // InternalOseeDsl.g:10150:1: ( ruleCondition )
            {
            // InternalOseeDsl.g:10150:1: ( ruleCondition )
            // InternalOseeDsl.g:10151:1: ruleCondition
            {
             before(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_4_1_0()); 
            pushFollow(FOLLOW_2);
            ruleCondition();

            state._fsp--;

             after(grammarAccess.getXArtifactMatcherAccess().getConditionsConditionParserRuleCall_4_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__XArtifactMatcher__ConditionsAssignment_4_1"


    // $ANTLR start "rule__Role__NameAssignment_1"
    // InternalOseeDsl.g:10160:1: rule__Role__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__Role__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10164:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10165:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:10165:1: ( RULE_STRING )
            // InternalOseeDsl.g:10166:1: RULE_STRING
            {
             before(grammarAccess.getRoleAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getRoleAccess().getNameSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__NameAssignment_1"


    // $ANTLR start "rule__Role__SuperRolesAssignment_2_1"
    // InternalOseeDsl.g:10175:1: rule__Role__SuperRolesAssignment_2_1 : ( ( RULE_STRING ) ) ;
    public final void rule__Role__SuperRolesAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10179:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:10180:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:10180:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10181:1: ( RULE_STRING )
            {
             before(grammarAccess.getRoleAccess().getSuperRolesRoleCrossReference_2_1_0()); 
            // InternalOseeDsl.g:10182:1: ( RULE_STRING )
            // InternalOseeDsl.g:10183:1: RULE_STRING
            {
             before(grammarAccess.getRoleAccess().getSuperRolesRoleSTRINGTerminalRuleCall_2_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getRoleAccess().getSuperRolesRoleSTRINGTerminalRuleCall_2_1_0_1()); 

            }

             after(grammarAccess.getRoleAccess().getSuperRolesRoleCrossReference_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__SuperRolesAssignment_2_1"


    // $ANTLR start "rule__Role__UsersAndGroupsAssignment_4_0"
    // InternalOseeDsl.g:10194:1: rule__Role__UsersAndGroupsAssignment_4_0 : ( ruleUsersAndGroups ) ;
    public final void rule__Role__UsersAndGroupsAssignment_4_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10198:1: ( ( ruleUsersAndGroups ) )
            // InternalOseeDsl.g:10199:1: ( ruleUsersAndGroups )
            {
            // InternalOseeDsl.g:10199:1: ( ruleUsersAndGroups )
            // InternalOseeDsl.g:10200:1: ruleUsersAndGroups
            {
             before(grammarAccess.getRoleAccess().getUsersAndGroupsUsersAndGroupsParserRuleCall_4_0_0()); 
            pushFollow(FOLLOW_2);
            ruleUsersAndGroups();

            state._fsp--;

             after(grammarAccess.getRoleAccess().getUsersAndGroupsUsersAndGroupsParserRuleCall_4_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__UsersAndGroupsAssignment_4_0"


    // $ANTLR start "rule__Role__ReferencedContextsAssignment_4_1"
    // InternalOseeDsl.g:10209:1: rule__Role__ReferencedContextsAssignment_4_1 : ( ruleReferencedContext ) ;
    public final void rule__Role__ReferencedContextsAssignment_4_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10213:1: ( ( ruleReferencedContext ) )
            // InternalOseeDsl.g:10214:1: ( ruleReferencedContext )
            {
            // InternalOseeDsl.g:10214:1: ( ruleReferencedContext )
            // InternalOseeDsl.g:10215:1: ruleReferencedContext
            {
             before(grammarAccess.getRoleAccess().getReferencedContextsReferencedContextParserRuleCall_4_1_0()); 
            pushFollow(FOLLOW_2);
            ruleReferencedContext();

            state._fsp--;

             after(grammarAccess.getRoleAccess().getReferencedContextsReferencedContextParserRuleCall_4_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Role__ReferencedContextsAssignment_4_1"


    // $ANTLR start "rule__ReferencedContext__AccessContextRefAssignment_1"
    // InternalOseeDsl.g:10224:1: rule__ReferencedContext__AccessContextRefAssignment_1 : ( RULE_STRING ) ;
    public final void rule__ReferencedContext__AccessContextRefAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10228:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10229:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:10229:1: ( RULE_STRING )
            // InternalOseeDsl.g:10230:1: RULE_STRING
            {
             before(grammarAccess.getReferencedContextAccess().getAccessContextRefSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getReferencedContextAccess().getAccessContextRefSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ReferencedContext__AccessContextRefAssignment_1"


    // $ANTLR start "rule__UsersAndGroups__UserOrGroupGuidAssignment_1"
    // InternalOseeDsl.g:10239:1: rule__UsersAndGroups__UserOrGroupGuidAssignment_1 : ( RULE_STRING ) ;
    public final void rule__UsersAndGroups__UserOrGroupGuidAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10243:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10244:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:10244:1: ( RULE_STRING )
            // InternalOseeDsl.g:10245:1: RULE_STRING
            {
             before(grammarAccess.getUsersAndGroupsAccess().getUserOrGroupGuidSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getUsersAndGroupsAccess().getUserOrGroupGuidSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__UsersAndGroups__UserOrGroupGuidAssignment_1"


    // $ANTLR start "rule__AccessContext__NameAssignment_1"
    // InternalOseeDsl.g:10254:1: rule__AccessContext__NameAssignment_1 : ( RULE_STRING ) ;
    public final void rule__AccessContext__NameAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10258:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10259:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:10259:1: ( RULE_STRING )
            // InternalOseeDsl.g:10260:1: RULE_STRING
            {
             before(grammarAccess.getAccessContextAccess().getNameSTRINGTerminalRuleCall_1_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getAccessContextAccess().getNameSTRINGTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__NameAssignment_1"


    // $ANTLR start "rule__AccessContext__SuperAccessContextsAssignment_2_1"
    // InternalOseeDsl.g:10269:1: rule__AccessContext__SuperAccessContextsAssignment_2_1 : ( ( RULE_STRING ) ) ;
    public final void rule__AccessContext__SuperAccessContextsAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10273:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:10274:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:10274:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10275:1: ( RULE_STRING )
            {
             before(grammarAccess.getAccessContextAccess().getSuperAccessContextsAccessContextCrossReference_2_1_0()); 
            // InternalOseeDsl.g:10276:1: ( RULE_STRING )
            // InternalOseeDsl.g:10277:1: RULE_STRING
            {
             before(grammarAccess.getAccessContextAccess().getSuperAccessContextsAccessContextSTRINGTerminalRuleCall_2_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getAccessContextAccess().getSuperAccessContextsAccessContextSTRINGTerminalRuleCall_2_1_0_1()); 

            }

             after(grammarAccess.getAccessContextAccess().getSuperAccessContextsAccessContextCrossReference_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__SuperAccessContextsAssignment_2_1"


    // $ANTLR start "rule__AccessContext__GuidAssignment_5"
    // InternalOseeDsl.g:10288:1: rule__AccessContext__GuidAssignment_5 : ( RULE_STRING ) ;
    public final void rule__AccessContext__GuidAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10292:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10293:1: ( RULE_STRING )
            {
            // InternalOseeDsl.g:10293:1: ( RULE_STRING )
            // InternalOseeDsl.g:10294:1: RULE_STRING
            {
             before(grammarAccess.getAccessContextAccess().getGuidSTRINGTerminalRuleCall_5_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getAccessContextAccess().getGuidSTRINGTerminalRuleCall_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__GuidAssignment_5"


    // $ANTLR start "rule__AccessContext__AccessRulesAssignment_7_0"
    // InternalOseeDsl.g:10303:1: rule__AccessContext__AccessRulesAssignment_7_0 : ( ruleObjectRestriction ) ;
    public final void rule__AccessContext__AccessRulesAssignment_7_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10307:1: ( ( ruleObjectRestriction ) )
            // InternalOseeDsl.g:10308:1: ( ruleObjectRestriction )
            {
            // InternalOseeDsl.g:10308:1: ( ruleObjectRestriction )
            // InternalOseeDsl.g:10309:1: ruleObjectRestriction
            {
             before(grammarAccess.getAccessContextAccess().getAccessRulesObjectRestrictionParserRuleCall_7_0_0()); 
            pushFollow(FOLLOW_2);
            ruleObjectRestriction();

            state._fsp--;

             after(grammarAccess.getAccessContextAccess().getAccessRulesObjectRestrictionParserRuleCall_7_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__AccessRulesAssignment_7_0"


    // $ANTLR start "rule__AccessContext__HierarchyRestrictionsAssignment_7_1"
    // InternalOseeDsl.g:10318:1: rule__AccessContext__HierarchyRestrictionsAssignment_7_1 : ( ruleHierarchyRestriction ) ;
    public final void rule__AccessContext__HierarchyRestrictionsAssignment_7_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10322:1: ( ( ruleHierarchyRestriction ) )
            // InternalOseeDsl.g:10323:1: ( ruleHierarchyRestriction )
            {
            // InternalOseeDsl.g:10323:1: ( ruleHierarchyRestriction )
            // InternalOseeDsl.g:10324:1: ruleHierarchyRestriction
            {
             before(grammarAccess.getAccessContextAccess().getHierarchyRestrictionsHierarchyRestrictionParserRuleCall_7_1_0()); 
            pushFollow(FOLLOW_2);
            ruleHierarchyRestriction();

            state._fsp--;

             after(grammarAccess.getAccessContextAccess().getHierarchyRestrictionsHierarchyRestrictionParserRuleCall_7_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AccessContext__HierarchyRestrictionsAssignment_7_1"


    // $ANTLR start "rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1"
    // InternalOseeDsl.g:10333:1: rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1 : ( ( RULE_STRING ) ) ;
    public final void rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10337:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:10338:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:10338:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10339:1: ( RULE_STRING )
            {
             before(grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_1_0()); 
            // InternalOseeDsl.g:10340:1: ( RULE_STRING )
            // InternalOseeDsl.g:10341:1: RULE_STRING
            {
             before(grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefXArtifactMatcherSTRINGTerminalRuleCall_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefXArtifactMatcherSTRINGTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1"


    // $ANTLR start "rule__HierarchyRestriction__AccessRulesAssignment_3"
    // InternalOseeDsl.g:10352:1: rule__HierarchyRestriction__AccessRulesAssignment_3 : ( ruleObjectRestriction ) ;
    public final void rule__HierarchyRestriction__AccessRulesAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10356:1: ( ( ruleObjectRestriction ) )
            // InternalOseeDsl.g:10357:1: ( ruleObjectRestriction )
            {
            // InternalOseeDsl.g:10357:1: ( ruleObjectRestriction )
            // InternalOseeDsl.g:10358:1: ruleObjectRestriction
            {
             before(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesObjectRestrictionParserRuleCall_3_0()); 
            pushFollow(FOLLOW_2);
            ruleObjectRestriction();

            state._fsp--;

             after(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesObjectRestrictionParserRuleCall_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__HierarchyRestriction__AccessRulesAssignment_3"


    // $ANTLR start "rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1"
    // InternalOseeDsl.g:10367:1: rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1 : ( ( RULE_STRING ) ) ;
    public final void rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10371:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:10372:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:10372:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10373:1: ( RULE_STRING )
            {
             before(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeRefXArtifactTypeCrossReference_1_0()); 
            // InternalOseeDsl.g:10374:1: ( RULE_STRING )
            // InternalOseeDsl.g:10375:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeRefXArtifactTypeSTRINGTerminalRuleCall_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeRefXArtifactTypeSTRINGTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeRefXArtifactTypeCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1"


    // $ANTLR start "rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1"
    // InternalOseeDsl.g:10386:1: rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1 : ( ( RULE_STRING ) ) ;
    public final void rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10390:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:10391:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:10391:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10392:1: ( RULE_STRING )
            {
             before(grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_1_0()); 
            // InternalOseeDsl.g:10393:1: ( RULE_STRING )
            // InternalOseeDsl.g:10394:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactMatcherRefXArtifactMatcherSTRINGTerminalRuleCall_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactMatcherRefXArtifactMatcherSTRINGTerminalRuleCall_1_0_1()); 

            }

             after(grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1"


    // $ANTLR start "rule__ArtifactMatchRestriction__PermissionAssignment_0"
    // InternalOseeDsl.g:10405:1: rule__ArtifactMatchRestriction__PermissionAssignment_0 : ( ruleAccessPermissionEnum ) ;
    public final void rule__ArtifactMatchRestriction__PermissionAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10409:1: ( ( ruleAccessPermissionEnum ) )
            // InternalOseeDsl.g:10410:1: ( ruleAccessPermissionEnum )
            {
            // InternalOseeDsl.g:10410:1: ( ruleAccessPermissionEnum )
            // InternalOseeDsl.g:10411:1: ruleAccessPermissionEnum
            {
             before(grammarAccess.getArtifactMatchRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            pushFollow(FOLLOW_2);
            ruleAccessPermissionEnum();

            state._fsp--;

             after(grammarAccess.getArtifactMatchRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__PermissionAssignment_0"


    // $ANTLR start "rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3"
    // InternalOseeDsl.g:10420:1: rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3 : ( ( RULE_STRING ) ) ;
    public final void rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10424:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:10425:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:10425:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10426:1: ( RULE_STRING )
            {
             before(grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_3_0()); 
            // InternalOseeDsl.g:10427:1: ( RULE_STRING )
            // InternalOseeDsl.g:10428:1: RULE_STRING
            {
             before(grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefXArtifactMatcherSTRINGTerminalRuleCall_3_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefXArtifactMatcherSTRINGTerminalRuleCall_3_0_1()); 

            }

             after(grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefXArtifactMatcherCrossReference_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3"


    // $ANTLR start "rule__ArtifactTypeRestriction__PermissionAssignment_0"
    // InternalOseeDsl.g:10439:1: rule__ArtifactTypeRestriction__PermissionAssignment_0 : ( ruleAccessPermissionEnum ) ;
    public final void rule__ArtifactTypeRestriction__PermissionAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10443:1: ( ( ruleAccessPermissionEnum ) )
            // InternalOseeDsl.g:10444:1: ( ruleAccessPermissionEnum )
            {
            // InternalOseeDsl.g:10444:1: ( ruleAccessPermissionEnum )
            // InternalOseeDsl.g:10445:1: ruleAccessPermissionEnum
            {
             before(grammarAccess.getArtifactTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            pushFollow(FOLLOW_2);
            ruleAccessPermissionEnum();

            state._fsp--;

             after(grammarAccess.getArtifactTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__PermissionAssignment_0"


    // $ANTLR start "rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3"
    // InternalOseeDsl.g:10454:1: rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3 : ( ( RULE_STRING ) ) ;
    public final void rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10458:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:10459:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:10459:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10460:1: ( RULE_STRING )
            {
             before(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_3_0()); 
            // InternalOseeDsl.g:10461:1: ( RULE_STRING )
            // InternalOseeDsl.g:10462:1: RULE_STRING
            {
             before(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeSTRINGTerminalRuleCall_3_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeSTRINGTerminalRuleCall_3_0_1()); 

            }

             after(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3"


    // $ANTLR start "rule__AttributeTypeRestriction__PermissionAssignment_0"
    // InternalOseeDsl.g:10473:1: rule__AttributeTypeRestriction__PermissionAssignment_0 : ( ruleAccessPermissionEnum ) ;
    public final void rule__AttributeTypeRestriction__PermissionAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10477:1: ( ( ruleAccessPermissionEnum ) )
            // InternalOseeDsl.g:10478:1: ( ruleAccessPermissionEnum )
            {
            // InternalOseeDsl.g:10478:1: ( ruleAccessPermissionEnum )
            // InternalOseeDsl.g:10479:1: ruleAccessPermissionEnum
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            pushFollow(FOLLOW_2);
            ruleAccessPermissionEnum();

            state._fsp--;

             after(grammarAccess.getAttributeTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__PermissionAssignment_0"


    // $ANTLR start "rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3"
    // InternalOseeDsl.g:10488:1: rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3 : ( ( RULE_STRING ) ) ;
    public final void rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10492:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:10493:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:10493:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10494:1: ( RULE_STRING )
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefXAttributeTypeCrossReference_3_0()); 
            // InternalOseeDsl.g:10495:1: ( RULE_STRING )
            // InternalOseeDsl.g:10496:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefXAttributeTypeSTRINGTerminalRuleCall_3_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefXAttributeTypeSTRINGTerminalRuleCall_3_0_1()); 

            }

             after(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefXAttributeTypeCrossReference_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3"


    // $ANTLR start "rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2"
    // InternalOseeDsl.g:10507:1: rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2 : ( ( RULE_STRING ) ) ;
    public final void rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10511:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:10512:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:10512:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10513:1: ( RULE_STRING )
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_4_2_0()); 
            // InternalOseeDsl.g:10514:1: ( RULE_STRING )
            // InternalOseeDsl.g:10515:1: RULE_STRING
            {
             before(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeSTRINGTerminalRuleCall_4_2_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeSTRINGTerminalRuleCall_4_2_0_1()); 

            }

             after(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefXArtifactTypeCrossReference_4_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2"


    // $ANTLR start "rule__RelationTypeRestriction__PermissionAssignment_0"
    // InternalOseeDsl.g:10530:1: rule__RelationTypeRestriction__PermissionAssignment_0 : ( ruleAccessPermissionEnum ) ;
    public final void rule__RelationTypeRestriction__PermissionAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10534:1: ( ( ruleAccessPermissionEnum ) )
            // InternalOseeDsl.g:10535:1: ( ruleAccessPermissionEnum )
            {
            // InternalOseeDsl.g:10535:1: ( ruleAccessPermissionEnum )
            // InternalOseeDsl.g:10536:1: ruleAccessPermissionEnum
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 
            pushFollow(FOLLOW_2);
            ruleAccessPermissionEnum();

            state._fsp--;

             after(grammarAccess.getRelationTypeRestrictionAccess().getPermissionAccessPermissionEnumEnumRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__PermissionAssignment_0"


    // $ANTLR start "rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0"
    // InternalOseeDsl.g:10545:1: rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0 : ( ruleRelationTypeMatch ) ;
    public final void rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10549:1: ( ( ruleRelationTypeMatch ) )
            // InternalOseeDsl.g:10550:1: ( ruleRelationTypeMatch )
            {
            // InternalOseeDsl.g:10550:1: ( ruleRelationTypeMatch )
            // InternalOseeDsl.g:10551:1: ruleRelationTypeMatch
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeMatchRelationTypeMatchEnumRuleCall_3_0_0()); 
            pushFollow(FOLLOW_2);
            ruleRelationTypeMatch();

            state._fsp--;

             after(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeMatchRelationTypeMatchEnumRuleCall_3_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0"


    // $ANTLR start "rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1"
    // InternalOseeDsl.g:10560:1: rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1 : ( ( RULE_STRING ) ) ;
    public final void rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10564:1: ( ( ( RULE_STRING ) ) )
            // InternalOseeDsl.g:10565:1: ( ( RULE_STRING ) )
            {
            // InternalOseeDsl.g:10565:1: ( ( RULE_STRING ) )
            // InternalOseeDsl.g:10566:1: ( RULE_STRING )
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefXRelationTypeCrossReference_3_1_0()); 
            // InternalOseeDsl.g:10567:1: ( RULE_STRING )
            // InternalOseeDsl.g:10568:1: RULE_STRING
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefXRelationTypeSTRINGTerminalRuleCall_3_1_0_1()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefXRelationTypeSTRINGTerminalRuleCall_3_1_0_1()); 

            }

             after(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefXRelationTypeCrossReference_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1"


    // $ANTLR start "rule__RelationTypeRestriction__RestrictedToSideAssignment_4"
    // InternalOseeDsl.g:10579:1: rule__RelationTypeRestriction__RestrictedToSideAssignment_4 : ( ruleXRelationSideEnum ) ;
    public final void rule__RelationTypeRestriction__RestrictedToSideAssignment_4() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10583:1: ( ( ruleXRelationSideEnum ) )
            // InternalOseeDsl.g:10584:1: ( ruleXRelationSideEnum )
            {
            // InternalOseeDsl.g:10584:1: ( ruleXRelationSideEnum )
            // InternalOseeDsl.g:10585:1: ruleXRelationSideEnum
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getRestrictedToSideXRelationSideEnumEnumRuleCall_4_0()); 
            pushFollow(FOLLOW_2);
            ruleXRelationSideEnum();

            state._fsp--;

             after(grammarAccess.getRelationTypeRestrictionAccess().getRestrictedToSideXRelationSideEnumEnumRuleCall_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__RestrictedToSideAssignment_4"


    // $ANTLR start "rule__RelationTypeRestriction__PredicateAssignment_5"
    // InternalOseeDsl.g:10594:1: rule__RelationTypeRestriction__PredicateAssignment_5 : ( ruleRelationTypePredicate ) ;
    public final void rule__RelationTypeRestriction__PredicateAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
            
        try {
            // InternalOseeDsl.g:10598:1: ( ( ruleRelationTypePredicate ) )
            // InternalOseeDsl.g:10599:1: ( ruleRelationTypePredicate )
            {
            // InternalOseeDsl.g:10599:1: ( ruleRelationTypePredicate )
            // InternalOseeDsl.g:10600:1: ruleRelationTypePredicate
            {
             before(grammarAccess.getRelationTypeRestrictionAccess().getPredicateRelationTypePredicateParserRuleCall_5_0()); 
            pushFollow(FOLLOW_2);
            ruleRelationTypePredicate();

            state._fsp--;

             after(grammarAccess.getRelationTypeRestrictionAccess().getPredicateRelationTypePredicateParserRuleCall_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RelationTypeRestriction__PredicateAssignment_5"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x1010000000000000L,0x000000021900A480L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x1010000000000002L,0x000000020000A480L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000000002L,0x0000000019000000L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0010000000000000L,0x0000000200000000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0120000000000000L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0440000000000000L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0880000000000000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0020000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000000006020L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000000008010L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000000000000L,0x000000000000007EL});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x000000003FFE6020L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000000000016020L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0080000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000208L});
    public static final BitSet FOLLOW_35 = new BitSet(new long[]{0x0080000000000000L,0x0000000400001800L});
    public static final BitSet FOLLOW_36 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001800L});
    public static final BitSet FOLLOW_37 = new BitSet(new long[]{0x0000000000000000L,0x0000000400005800L});
    public static final BitSet FOLLOW_38 = new BitSet(new long[]{0x0000000000000002L,0x0000000400005800L});
    public static final BitSet FOLLOW_39 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_40 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_41 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_42 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_43 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_44 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_45 = new BitSet(new long[]{0x00000001C0000020L});
    public static final BitSet FOLLOW_46 = new BitSet(new long[]{0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_47 = new BitSet(new long[]{0x0000001E00000000L});
    public static final BitSet FOLLOW_48 = new BitSet(new long[]{0x0000006000000000L});
    public static final BitSet FOLLOW_49 = new BitSet(new long[]{0x00001E0000000000L});
    public static final BitSet FOLLOW_50 = new BitSet(new long[]{0x0000018000000000L});
    public static final BitSet FOLLOW_51 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_52 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_53 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_54 = new BitSet(new long[]{0x00001E0000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_55 = new BitSet(new long[]{0x0000018000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_56 = new BitSet(new long[]{0x0400000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_57 = new BitSet(new long[]{0x0400000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_58 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_59 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_60 = new BitSet(new long[]{0x0000600000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_61 = new BitSet(new long[]{0x0000600000000002L,0x0000000020000000L});
    public static final BitSet FOLLOW_62 = new BitSet(new long[]{0x0000600000000000L});
    public static final BitSet FOLLOW_63 = new BitSet(new long[]{0x0000600000000002L});
    public static final BitSet FOLLOW_64 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
    public static final BitSet FOLLOW_65 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_66 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_67 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_68 = new BitSet(new long[]{0x0000000000000000L,0x0000000104000000L});
    public static final BitSet FOLLOW_69 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_70 = new BitSet(new long[]{0x0000000000001040L});
    public static final BitSet FOLLOW_71 = new BitSet(new long[]{0x0003800000000000L});
    public static final BitSet FOLLOW_72 = new BitSet(new long[]{0x0010000000000000L,0x0000000044000000L});
    public static final BitSet FOLLOW_73 = new BitSet(new long[]{0x0000000000000002L,0x000000000000007EL});

}
