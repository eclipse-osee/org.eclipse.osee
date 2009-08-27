lexer grammar InternalOseeTypes;
@header {
package org.eclipse.osee.framework.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

T12 : 'import' ;
T13 : '.' ;
T14 : 'abstract' ;
T15 : 'artifactType' ;
T16 : 'extends' ;
T17 : '{' ;
T18 : '}' ;
T19 : 'attribute' ;
T20 : 'attributeType' ;
T21 : 'dataProvider' ;
T22 : 'DefaultAttributeDataProvider' ;
T23 : 'UriAttributeDataProvider' ;
T24 : 'MappedAttributeDataProvider' ;
T25 : 'min' ;
T26 : 'max' ;
T27 : 'unlimited' ;
T28 : 'taggerId' ;
T29 : 'DefaultAttributeTaggerProvider' ;
T30 : 'enumType' ;
T31 : 'description' ;
T32 : 'defaultValue' ;
T33 : 'fileExtension' ;
T34 : 'BooleanAttribute' ;
T35 : 'CompressedContentAttribute' ;
T36 : 'DateAttribute' ;
T37 : 'EnumeratedAttribute' ;
T38 : 'FloatingPointAttribute' ;
T39 : 'IntegerAttribute' ;
T40 : 'JavaObjectAttribute' ;
T41 : 'StringAttribute' ;
T42 : 'WordAttribute' ;
T43 : 'oseeEnumType' ;
T44 : 'entry' ;
T45 : 'relationType' ;
T46 : 'sideAName' ;
T47 : 'sideAArtifactType' ;
T48 : 'sideBName' ;
T49 : 'sideBArtifactType' ;
T50 : 'defaultOrderType' ;
T51 : 'Lexicographical_Ascending' ;
T52 : 'Lexicographical_Descending' ;
T53 : 'Unordered' ;
T54 : 'multiplicity' ;
T55 : 'ONE_TO_MANY' ;
T56 : 'MANY_TO_MANY' ;
T57 : 'MANY_TO_ONE' ;
T58 : 'ONE_TO_ONE' ;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1247
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1249
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1251
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1253
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1255
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1257
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1259
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1261
RULE_ANY_OTHER : .;


