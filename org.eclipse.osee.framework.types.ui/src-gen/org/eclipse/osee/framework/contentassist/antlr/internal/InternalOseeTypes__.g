lexer grammar InternalOseeTypes;
@header {
package org.eclipse.osee.framework.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.Lexer;
}

T12 : 'DefaultAttributeDataProvider' ;
T13 : 'UriAttributeDataProvider' ;
T14 : 'MappedAttributeDataProvider' ;
T15 : 'unlimited' ;
T16 : 'DefaultAttributeTaggerProvider' ;
T17 : 'BooleanAttribute' ;
T18 : 'CompressedContentAttribute' ;
T19 : 'DateAttribute' ;
T20 : 'EnumeratedAttribute' ;
T21 : 'FloatingPointAttribute' ;
T22 : 'IntegerAttribute' ;
T23 : 'JavaObjectAttribute' ;
T24 : 'StringAttribute' ;
T25 : 'WordAttribute' ;
T26 : 'Lexicographical_Ascending' ;
T27 : 'Lexicographical_Descending' ;
T28 : 'Unordered' ;
T29 : 'ONE_TO_ONE' ;
T30 : 'ONE_TO_MANY' ;
T31 : 'MANY_TO_ONE' ;
T32 : 'MANY_TO_MANY' ;
T33 : 'import' ;
T34 : '.' ;
T35 : 'artifactType' ;
T36 : '{' ;
T37 : '}' ;
T38 : 'extends' ;
T39 : ',' ;
T40 : 'overrides' ;
T41 : 'attribute' ;
T42 : 'branchGuid' ;
T43 : 'attributeType' ;
T44 : 'dataProvider' ;
T45 : 'min' ;
T46 : 'max' ;
T47 : 'taggerId' ;
T48 : 'enumType' ;
T49 : 'description' ;
T50 : 'defaultValue' ;
T51 : 'fileExtension' ;
T52 : 'oseeEnumType' ;
T53 : 'entry' ;
T54 : 'relationType' ;
T55 : 'sideAName' ;
T56 : 'sideAArtifactType' ;
T57 : 'sideBName' ;
T58 : 'sideBArtifactType' ;
T59 : 'defaultOrderType' ;
T60 : 'multiplicity' ;
T61 : 'abstract' ;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3244
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3246
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3248
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3250
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3252
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3254
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3256
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3258
RULE_ANY_OTHER : .;


