lexer grammar InternalOseeTypes;
@header {
package org.eclipse.osee.framework.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.Lexer;
}

T12 : 'DefaultAttributeDataProvider' ;
T13 : 'UriAttributeDataProvider' ;
T14 : 'unlimited' ;
T15 : 'DefaultAttributeTaggerProvider' ;
T16 : 'BooleanAttribute' ;
T17 : 'CompressedContentAttribute' ;
T18 : 'DateAttribute' ;
T19 : 'EnumeratedAttribute' ;
T20 : 'FloatingPointAttribute' ;
T21 : 'IntegerAttribute' ;
T22 : 'JavaObjectAttribute' ;
T23 : 'StringAttribute' ;
T24 : 'WordAttribute' ;
T25 : 'Lexicographical_Ascending' ;
T26 : 'Lexicographical_Descending' ;
T27 : 'Unordered' ;
T28 : 'ONE_TO_ONE' ;
T29 : 'ONE_TO_MANY' ;
T30 : 'MANY_TO_ONE' ;
T31 : 'MANY_TO_MANY' ;
T32 : 'import' ;
T33 : '.' ;
T34 : 'artifactType' ;
T35 : '{' ;
T36 : 'guid' ;
T37 : '}' ;
T38 : 'extends' ;
T39 : ',' ;
T40 : 'attribute' ;
T41 : 'branchGuid' ;
T42 : 'attributeType' ;
T43 : 'dataProvider' ;
T44 : 'min' ;
T45 : 'max' ;
T46 : 'overrides' ;
T47 : 'taggerId' ;
T48 : 'enumType' ;
T49 : 'description' ;
T50 : 'defaultValue' ;
T51 : 'fileExtension' ;
T52 : 'oseeEnumType' ;
T53 : 'entry' ;
T54 : 'entryGuid' ;
T55 : 'overrides enum' ;
T56 : 'add' ;
T57 : 'remove' ;
T58 : 'relationType' ;
T59 : 'sideAName' ;
T60 : 'sideAArtifactType' ;
T61 : 'sideBName' ;
T62 : 'sideBArtifactType' ;
T63 : 'defaultOrderType' ;
T64 : 'multiplicity' ;
T65 : 'abstract' ;
T66 : 'inheritAll' ;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3828
RULE_WHOLE_NUM_STR : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3830
RULE_ID : '^'? ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3832
RULE_INT : ('0'..'9')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3834
RULE_STRING : ('"' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'"')))* '"'|'\'' ('\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')|~(('\\'|'\'')))* '\'');

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3836
RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3838
RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3840
RULE_WS : (' '|'\t'|'\r'|'\n')+;

// $ANTLR src "../org.eclipse.osee.framework.types.ui/src-gen/org/eclipse/osee/framework/contentassist/antlr/internal/InternalOseeTypes.g" 3842
RULE_ANY_OTHER : .;


