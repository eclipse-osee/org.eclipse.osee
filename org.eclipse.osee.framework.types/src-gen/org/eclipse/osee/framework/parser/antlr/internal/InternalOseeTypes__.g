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
T17 : ',' ;
T18 : 'overrides' ;
T19 : '{' ;
T20 : '}' ;
T21 : 'attribute' ;
T22 : 'branchGuid' ;
T23 : 'attributeType' ;
T24 : 'dataProvider' ;
T25 : 'DefaultAttributeDataProvider' ;
T26 : 'UriAttributeDataProvider' ;
T27 : 'MappedAttributeDataProvider' ;
T28 : 'min' ;
T29 : 'max' ;
T30 : 'unlimited' ;
T31 : 'taggerId' ;
T32 : 'DefaultAttributeTaggerProvider' ;
T33 : 'enumType' ;
T34 : 'description' ;
T35 : 'defaultValue' ;
T36 : 'fileExtension' ;
T37 : 'BooleanAttribute' ;
T38 : 'CompressedContentAttribute' ;
T39 : 'DateAttribute' ;
T40 : 'EnumeratedAttribute' ;
T41 : 'FloatingPointAttribute' ;
T42 : 'IntegerAttribute' ;
T43 : 'JavaObjectAttribute' ;
T44 : 'StringAttribute' ;
T45 : 'WordAttribute' ;
T46 : 'oseeEnumType' ;
T47 : 'entry' ;
T48 : 'relationType' ;
T49 : 'sideAName' ;
T50 : 'sideAArtifactType' ;
T51 : 'sideBName' ;
T52 : 'sideBArtifactType' ;
T53 : 'defaultOrderType' ;
T54 : 'multiplicity' ;
T55 : 'Lexicographical_Ascending' ;
T56 : 'Lexicographical_Descending' ;
T57 : 'Unordered' ;
T58 : 'ONE_TO_ONE' ;
T59 : 'ONE_TO_MANY' ;
T60 : 'MANY_TO_ONE' ;
T61 : 'MANY_TO_MANY' ;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1511
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1513
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1515
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1517
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1519
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1521
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1523
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types/src-gen/org/eclipse/osee/framework/parser/antlr/internal/InternalOseeTypes.g" 1525
RULE_ANY_OTHER : .;


