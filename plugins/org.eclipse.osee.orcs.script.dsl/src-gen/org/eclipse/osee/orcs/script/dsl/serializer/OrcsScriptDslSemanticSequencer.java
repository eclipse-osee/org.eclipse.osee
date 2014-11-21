package org.eclipse.osee.orcs.script.dsl.serializer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactGuidCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryAll;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryByPredicate;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeEqualsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeInstanceOfClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAssignment;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeExistClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBooleanLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchivedCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchChildOfClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameEqualsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNamePatternClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchOfCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchParentOfClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryAll;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryById;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryByPredicate;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchStateCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchTypeCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectAllFieldsExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFindClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsListLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNullLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNumberLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelatedToClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationExistClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTemplateLiteral;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxAuthorIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxBranchIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentEqualsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentPatternClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdEqualsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdIsNullClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxHeadOfBranchIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdEqualsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryAll;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryById;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryByPredicate;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTypeCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableDeclaration;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion;
import org.eclipse.osee.orcs.script.dsl.services.OrcsScriptDslGrammarAccess;
import org.eclipse.xtext.serializer.acceptor.ISemanticSequenceAcceptor;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.diagnostic.ISemanticSequencerDiagnosticProvider;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor;
import org.eclipse.xtext.serializer.sequencer.AbstractDelegatingSemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.GenericSequencer;
import org.eclipse.xtext.serializer.sequencer.ISemanticNodeProvider.INodesForEObjectProvider;
import org.eclipse.xtext.serializer.sequencer.ISemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;

@SuppressWarnings("all")
public class OrcsScriptDslSemanticSequencer extends AbstractDelegatingSemanticSequencer {

	@Inject
	private OrcsScriptDslGrammarAccess grammarAccess;
	
	public void createSequence(EObject context, EObject semanticObject) {
		if(semanticObject.eClass().getEPackage() == OrcsScriptDslPackage.eINSTANCE) switch(semanticObject.eClass().getClassifierID()) {
			case OrcsScriptDslPackage.ORCS_SCRIPT:
				if(context == grammarAccess.getOrcsScriptRule()) {
					sequence_OrcsScript(context, (OrcsScript) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ARTIFACT_GUID_CRITERIA:
				if(context == grammarAccess.getOsArtifactCriteriaRule() ||
				   context == grammarAccess.getOsArtifactGuidCriteriaRule() ||
				   context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsItemCriteriaRule()) {
					sequence_OsArtifactGuidCriteria(context, (OsArtifactGuidCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ARTIFACT_ID_CRITERIA:
				if(context == grammarAccess.getOsArtifactCriteriaRule() ||
				   context == grammarAccess.getOsArtifactIdCriteriaRule() ||
				   context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsItemCriteriaRule()) {
					sequence_OsArtifactIdCriteria(context, (OsArtifactIdCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_ALL:
				if(context == grammarAccess.getOsArtifactQueryRule() ||
				   context == grammarAccess.getOsArtifactQueryAllRule()) {
					sequence_OsArtifactQueryAll(context, (OsArtifactQueryAll) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_BY_PREDICATE:
				if(context == grammarAccess.getOsArtifactQueryRule() ||
				   context == grammarAccess.getOsArtifactQueryByPredicateRule()) {
					sequence_OsArtifactQueryByPredicate(context, (OsArtifactQueryByPredicate) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT:
				if(context == grammarAccess.getOsArtifactQueryStatementRule() ||
				   context == grammarAccess.getOsObjectQueryRule()) {
					sequence_OsArtifactQueryStatement(context, (OsArtifactQueryStatement) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_CRITERIA:
				if(context == grammarAccess.getOsArtifactCriteriaRule() ||
				   context == grammarAccess.getOsArtifactTypeCriteriaRule() ||
				   context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsItemCriteriaRule()) {
					sequence_OsArtifactTypeCriteria(context, (OsArtifactTypeCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_EQUALS_CLAUSE:
				if(context == grammarAccess.getOsArtifactTypeClauseRule() ||
				   context == grammarAccess.getOsArtifactTypeEqualsClauseRule()) {
					sequence_OsArtifactTypeEqualsClause(context, (OsArtifactTypeEqualsClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_INSTANCE_OF_CLAUSE:
				if(context == grammarAccess.getOsArtifactTypeClauseRule() ||
				   context == grammarAccess.getOsArtifactTypeInstanceOfClauseRule()) {
					sequence_OsArtifactTypeInstanceOfClause(context, (OsArtifactTypeInstanceOfClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ASSIGNMENT:
				if(context == grammarAccess.getOsAssignmentRule() ||
				   context == grammarAccess.getOsExpressionRule()) {
					sequence_OsAssignment(context, (OsAssignment) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ATTRIBUTE_CRITERIA:
				if(context == grammarAccess.getOsAttributeCriteriaRule() ||
				   context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsItemCriteriaRule()) {
					sequence_OsAttributeCriteria(context, (OsAttributeCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_ATTRIBUTE_EXIST_CLAUSE:
				if(context == grammarAccess.getOsAttributeClauseRule() ||
				   context == grammarAccess.getOsAttributeExistClauseRule()) {
					sequence_OsAttributeExistClause(context, (OsAttributeExistClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BOOLEAN_LITERAL:
				if(context == grammarAccess.getOsAssignmentValueRule() ||
				   context == grammarAccess.getOsBooleanLiteralRule() ||
				   context == grammarAccess.getOsLiteralRule()) {
					sequence_OsBooleanLiteral(context, (OsBooleanLiteral) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_ARCHIVED_CRITERIA:
				if(context == grammarAccess.getOsBranchArchivedCriteriaRule() ||
				   context == grammarAccess.getOsBranchCriteriaRule() ||
				   context == grammarAccess.getOsCritieriaRule()) {
					sequence_OsBranchArchivedCriteria(context, (OsBranchArchivedCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_CHILD_OF_CLAUSE:
				if(context == grammarAccess.getOsBranchChildOfClauseRule() ||
				   context == grammarAccess.getOsBranchClauseRule()) {
					sequence_OsBranchChildOfClause(context, (OsBranchChildOfClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_ID_CRITERIA:
				if(context == grammarAccess.getOsBranchCriteriaRule() ||
				   context == grammarAccess.getOsBranchIdCriteriaRule() ||
				   context == grammarAccess.getOsCritieriaRule()) {
					sequence_OsBranchIdCriteria(context, (OsBranchIdCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_NAME_CRITERIA:
				if(context == grammarAccess.getOsBranchCriteriaRule() ||
				   context == grammarAccess.getOsBranchNameCriteriaRule() ||
				   context == grammarAccess.getOsCritieriaRule()) {
					sequence_OsBranchNameCriteria(context, (OsBranchNameCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_NAME_EQUALS_CLAUSE:
				if(context == grammarAccess.getOsBranchNameClauseRule() ||
				   context == grammarAccess.getOsBranchNameEqualsClauseRule()) {
					sequence_OsBranchNameEqualsClause(context, (OsBranchNameEqualsClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_NAME_PATTERN_CLAUSE:
				if(context == grammarAccess.getOsBranchNameClauseRule() ||
				   context == grammarAccess.getOsBranchNamePatternClauseRule()) {
					sequence_OsBranchNamePatternClause(context, (OsBranchNamePatternClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_OF_CRITERIA:
				if(context == grammarAccess.getOsBranchCriteriaRule() ||
				   context == grammarAccess.getOsBranchOfCriteriaRule() ||
				   context == grammarAccess.getOsCritieriaRule()) {
					sequence_OsBranchOfCriteria(context, (OsBranchOfCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_PARENT_OF_CLAUSE:
				if(context == grammarAccess.getOsBranchClauseRule() ||
				   context == grammarAccess.getOsBranchParentOfClauseRule()) {
					sequence_OsBranchParentOfClause(context, (OsBranchParentOfClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_QUERY_ALL:
				if(context == grammarAccess.getOsBranchQueryRule() ||
				   context == grammarAccess.getOsBranchQueryAllRule()) {
					sequence_OsBranchQueryAll(context, (OsBranchQueryAll) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_QUERY_BY_ID:
				if(context == grammarAccess.getOsBranchQueryRule() ||
				   context == grammarAccess.getOsBranchQueryByIdRule()) {
					sequence_OsBranchQueryById(context, (OsBranchQueryById) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_QUERY_BY_PREDICATE:
				if(context == grammarAccess.getOsBranchQueryRule() ||
				   context == grammarAccess.getOsBranchQueryByPredicateRule()) {
					sequence_OsBranchQueryByPredicate(context, (OsBranchQueryByPredicate) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_QUERY_STATEMENT:
				if(context == grammarAccess.getOsBranchQueryStatementRule() ||
				   context == grammarAccess.getOsQueryRule()) {
					sequence_OsBranchQueryStatement(context, (OsBranchQueryStatement) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_STATE_CRITERIA:
				if(context == grammarAccess.getOsBranchCriteriaRule() ||
				   context == grammarAccess.getOsBranchStateCriteriaRule() ||
				   context == grammarAccess.getOsCritieriaRule()) {
					sequence_OsBranchStateCriteria(context, (OsBranchStateCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_BRANCH_TYPE_CRITERIA:
				if(context == grammarAccess.getOsBranchCriteriaRule() ||
				   context == grammarAccess.getOsBranchTypeCriteriaRule() ||
				   context == grammarAccess.getOsCritieriaRule()) {
					sequence_OsBranchTypeCriteria(context, (OsBranchTypeCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_COLLECT_ALL_FIELDS_EXPRESSION:
				if(context == grammarAccess.getOsCollectAllFieldsExpressionRule() ||
				   context == grammarAccess.getOsCollectExpressionRule()) {
					sequence_OsCollectAllFieldsExpression(context, (OsCollectAllFieldsExpression) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_COLLECT_CLAUSE:
				if(context == grammarAccess.getOsCollectClauseRule()) {
					sequence_OsCollectClause(context, (OsCollectClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_COLLECT_FIELD_EXPRESSION:
				if(context == grammarAccess.getOsCollectExpressionRule() ||
				   context == grammarAccess.getOsCollectFieldExpressionRule() ||
				   context == grammarAccess.getOsCollectItemExpressionRule()) {
					sequence_OsCollectFieldExpression(context, (OsCollectFieldExpression) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION:
				if(context == grammarAccess.getOsCollectExpressionRule() ||
				   context == grammarAccess.getOsCollectItemExpressionRule() ||
				   context == grammarAccess.getOsCollectObjectExpressionRule()) {
					sequence_OsCollectObjectExpression(context, (OsCollectObjectExpression) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_DOT_EXPRESSION:
				if(context == grammarAccess.getOsAliasRule() ||
				   context == grammarAccess.getOsArtifactGuidRule() ||
				   context == grammarAccess.getOsArtifactIdRule() ||
				   context == grammarAccess.getOsAttributeValueRule() ||
				   context == grammarAccess.getOsBranchIdRule() ||
				   context == grammarAccess.getOsBranchIdOrNameRule() ||
				   context == grammarAccess.getOsBranchNameRule() ||
				   context == grammarAccess.getOsBranchPatternRule() ||
				   context == grammarAccess.getOsDotExpressionRule() ||
				   context == grammarAccess.getOsDotExpressionAccess().getOsDotExpressionRefAction_1_0() ||
				   context == grammarAccess.getOsLimitRule() ||
				   context == grammarAccess.getOsMetaTypeIdRule() ||
				   context == grammarAccess.getOsTxCommentRule() ||
				   context == grammarAccess.getOsTxCommentPatternRule() ||
				   context == grammarAccess.getOsTxIdRule() ||
				   context == grammarAccess.getOsTxTimestampRule()) {
					sequence_OsDotExpression(context, (OsDotExpression) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_FIND_CLAUSE:
				if(context == grammarAccess.getOsClauseRule() ||
				   context == grammarAccess.getOsFindClauseRule()) {
					sequence_OsFindClause(context, (OsFindClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_FOLLOW_CLAUSE:
				if(context == grammarAccess.getOsClauseRule() ||
				   context == grammarAccess.getOsFollowClauseRule()) {
					sequence_OsFollowClause(context, (OsFollowClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE:
				if(context == grammarAccess.getOsFollowRelationTypeRule() ||
				   context == grammarAccess.getOsFollowStatementRule()) {
					sequence_OsFollowRelationType(context, (OsFollowRelationType) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_LIMIT_CLAUSE:
				if(context == grammarAccess.getOsLimitClauseRule()) {
					sequence_OsLimitClause(context, (OsLimitClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_LIST_LITERAL:
				if(context == grammarAccess.getOsAssignmentValueRule() ||
				   context == grammarAccess.getOsCollectionLiteralRule() ||
				   context == grammarAccess.getOsListLiteralRule()) {
					sequence_OsListLiteral(context, (OsListLiteral) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_NULL_LITERAL:
				if(context == grammarAccess.getOsAssignmentValueRule() ||
				   context == grammarAccess.getOsLiteralRule() ||
				   context == grammarAccess.getOsNullLiteralRule()) {
					sequence_OsNullLiteral(context, (OsNullLiteral) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_NUMBER_LITERAL:
				if(context == grammarAccess.getOsArtifactIdRule() ||
				   context == grammarAccess.getOsAssignmentValueRule() ||
				   context == grammarAccess.getOsBranchIdRule() ||
				   context == grammarAccess.getOsBranchIdOrNameRule() ||
				   context == grammarAccess.getOsLimitRule() ||
				   context == grammarAccess.getOsLiteralRule() ||
				   context == grammarAccess.getOsMetaTypeIdRule() ||
				   context == grammarAccess.getOsNumberLiteralRule() ||
				   context == grammarAccess.getOsTxIdRule()) {
					sequence_OsNumberLiteral(context, (OsNumberLiteral) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_QUERY_EXPRESSION:
				if(context == grammarAccess.getOsAssignmentValueRule() ||
				   context == grammarAccess.getOsQueryExpressionRule()) {
					sequence_OsQueryExpression(context, (OsQueryExpression) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_QUERY_STATEMENT:
				if(context == grammarAccess.getOsQueryStatementRule() ||
				   context == grammarAccess.getScriptStatementRule()) {
					sequence_OsQueryStatement(context, (OsQueryStatement) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_RELATED_TO_CLAUSE:
				if(context == grammarAccess.getOsRelatedToClauseRule() ||
				   context == grammarAccess.getOsRelationClauseRule()) {
					sequence_OsRelatedToClause(context, (OsRelatedToClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_RELATION_CRITERIA:
				if(context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsItemCriteriaRule() ||
				   context == grammarAccess.getOsRelationCriteriaRule()) {
					sequence_OsRelationCriteria(context, (OsRelationCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_RELATION_EXIST_CLAUSE:
				if(context == grammarAccess.getOsRelationClauseRule() ||
				   context == grammarAccess.getOsRelationExistClauseRule()) {
					sequence_OsRelationExistClause(context, (OsRelationExistClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_STRING_LITERAL:
				if(context == grammarAccess.getOsAliasRule() ||
				   context == grammarAccess.getOsArtifactGuidRule() ||
				   context == grammarAccess.getOsAssignmentValueRule() ||
				   context == grammarAccess.getOsAttributeValueRule() ||
				   context == grammarAccess.getOsBranchIdOrNameRule() ||
				   context == grammarAccess.getOsBranchNameRule() ||
				   context == grammarAccess.getOsBranchPatternRule() ||
				   context == grammarAccess.getOsLiteralRule() ||
				   context == grammarAccess.getOsMetaTypeIdRule() ||
				   context == grammarAccess.getOsStringLiteralRule() ||
				   context == grammarAccess.getOsTxCommentRule() ||
				   context == grammarAccess.getOsTxCommentPatternRule() ||
				   context == grammarAccess.getOsTxTimestampRule()) {
					sequence_OsStringLiteral(context, (OsStringLiteral) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TEMPLATE_LITERAL:
				if(context == grammarAccess.getOsAliasRule() ||
				   context == grammarAccess.getOsArtifactGuidRule() ||
				   context == grammarAccess.getOsArtifactIdRule() ||
				   context == grammarAccess.getOsAssignmentValueRule() ||
				   context == grammarAccess.getOsAttributeValueRule() ||
				   context == grammarAccess.getOsBranchIdRule() ||
				   context == grammarAccess.getOsBranchIdOrNameRule() ||
				   context == grammarAccess.getOsBranchNameRule() ||
				   context == grammarAccess.getOsBranchPatternRule() ||
				   context == grammarAccess.getOsLimitRule() ||
				   context == grammarAccess.getOsLiteralRule() ||
				   context == grammarAccess.getOsMetaTypeIdRule() ||
				   context == grammarAccess.getOsTemplateLiteralRule() ||
				   context == grammarAccess.getOsTxCommentRule() ||
				   context == grammarAccess.getOsTxCommentPatternRule() ||
				   context == grammarAccess.getOsTxIdRule() ||
				   context == grammarAccess.getOsTxTimestampRule()) {
					sequence_OsTemplateLiteral(context, (OsTemplateLiteral) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_AUTHOR_ID_CRITERIA:
				if(context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsTxAuthorIdCriteriaRule() ||
				   context == grammarAccess.getOsTxCriteriaRule()) {
					sequence_OsTxAuthorIdCriteria(context, (OsTxAuthorIdCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_BRANCH_ID_CRITERIA:
				if(context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsTxBranchIdCriteriaRule() ||
				   context == grammarAccess.getOsTxCriteriaRule()) {
					sequence_OsTxBranchIdCriteria(context, (OsTxBranchIdCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA:
				if(context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsTxCommentCriteriaRule() ||
				   context == grammarAccess.getOsTxCriteriaRule()) {
					sequence_OsTxCommentCriteria(context, (OsTxCommentCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_COMMENT_EQUALS_CLAUSE:
				if(context == grammarAccess.getOsTxCommentClauseRule() ||
				   context == grammarAccess.getOsTxCommentEqualsClauseRule()) {
					sequence_OsTxCommentEqualsClause(context, (OsTxCommentEqualsClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_COMMENT_PATTERN_CLAUSE:
				if(context == grammarAccess.getOsTxCommentClauseRule() ||
				   context == grammarAccess.getOsTxCommentPatternClauseRule()) {
					sequence_OsTxCommentPatternClause(context, (OsTxCommentPatternClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_COMMIT_ID_CRITERIA:
				if(context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsTxCommitIdCriteriaRule() ||
				   context == grammarAccess.getOsTxCriteriaRule()) {
					sequence_OsTxCommitIdCriteria(context, (OsTxCommitIdCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_COMMIT_ID_EQUALS_CLAUSE:
				if(context == grammarAccess.getOsTxCommitIdClauseRule() ||
				   context == grammarAccess.getOsTxCommitIdEqualsClauseRule()) {
					sequence_OsTxCommitIdEqualsClause(context, (OsTxCommitIdEqualsClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_COMMIT_ID_IS_NULL_CLAUSE:
				if(context == grammarAccess.getOsTxCommitIdClauseRule() ||
				   context == grammarAccess.getOsTxCommitIdIsNullClauseRule()) {
					sequence_OsTxCommitIdIsNullClause(context, (OsTxCommitIdIsNullClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_HEAD_OF_BRANCH_ID_CRITERIA:
				if(context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsTxCriteriaRule() ||
				   context == grammarAccess.getOsTxHeadOfBranchIdCriteriaRule()) {
					sequence_OsTxHeadOfBranchIdCriteria(context, (OsTxHeadOfBranchIdCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_ID_CRITERIA:
				if(context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsTxCriteriaRule() ||
				   context == grammarAccess.getOsTxIdCriteriaRule()) {
					sequence_OsTxIdCriteria(context, (OsTxIdCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_ID_EQUALS_CLAUSE:
				if(context == grammarAccess.getOsTxIdClauseRule() ||
				   context == grammarAccess.getOsTxIdEqualsClauseRule()) {
					sequence_OsTxIdEqualsClause(context, (OsTxIdEqualsClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE:
				if(context == grammarAccess.getOsTxIdClauseRule() ||
				   context == grammarAccess.getOsTxIdOpClauseRule()) {
					sequence_OsTxIdOpClause(context, (OsTxIdOpClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE:
				if(context == grammarAccess.getOsTxIdClauseRule() ||
				   context == grammarAccess.getOsTxIdRangeClauseRule()) {
					sequence_OsTxIdRangeClause(context, (OsTxIdRangeClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_QUERY_ALL:
				if(context == grammarAccess.getOsTxQueryRule() ||
				   context == grammarAccess.getOsTxQueryAllRule()) {
					sequence_OsTxQueryAll(context, (OsTxQueryAll) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_QUERY_BY_ID:
				if(context == grammarAccess.getOsTxQueryRule() ||
				   context == grammarAccess.getOsTxQueryByIdRule()) {
					sequence_OsTxQueryById(context, (OsTxQueryById) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_QUERY_BY_PREDICATE:
				if(context == grammarAccess.getOsTxQueryRule() ||
				   context == grammarAccess.getOsTxQueryByPredicateRule()) {
					sequence_OsTxQueryByPredicate(context, (OsTxQueryByPredicate) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT:
				if(context == grammarAccess.getOsQueryRule() ||
				   context == grammarAccess.getOsTxQueryStatementRule()) {
					sequence_OsTxQueryStatement(context, (OsTxQueryStatement) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_TIMESTAMP_CRITERIA:
				if(context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsTxCriteriaRule() ||
				   context == grammarAccess.getOsTxTimestampCriteriaRule()) {
					sequence_OsTxTimestampCriteria(context, (OsTxTimestampCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE:
				if(context == grammarAccess.getOsTxTimestampClauseRule() ||
				   context == grammarAccess.getOsTxTimestampOpClauseRule()) {
					sequence_OsTxTimestampOpClause(context, (OsTxTimestampOpClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_TIMESTAMP_RANGE_CLAUSE:
				if(context == grammarAccess.getOsTxTimestampClauseRule() ||
				   context == grammarAccess.getOsTxTimestampRangeClauseRule()) {
					sequence_OsTxTimestampRangeClause(context, (OsTxTimestampRangeClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_TX_TYPE_CRITERIA:
				if(context == grammarAccess.getOsCritieriaRule() ||
				   context == grammarAccess.getOsTxCriteriaRule() ||
				   context == grammarAccess.getOsTxTypeCriteriaRule()) {
					sequence_OsTxTypeCriteria(context, (OsTxTypeCriteria) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_VARIABLE:
				if(context == grammarAccess.getOsVariableRule()) {
					sequence_OsVariable(context, (OsVariable) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_VARIABLE_DECLARATION:
				if(context == grammarAccess.getOsVariableDeclarationRule() ||
				   context == grammarAccess.getScriptStatementRule()) {
					sequence_OsVariableDeclaration(context, (OsVariableDeclaration) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OS_VARIABLE_REFERENCE:
				if(context == grammarAccess.getOsAliasRule() ||
				   context == grammarAccess.getOsArtifactGuidRule() ||
				   context == grammarAccess.getOsArtifactIdRule() ||
				   context == grammarAccess.getOsAssignmentValueRule() ||
				   context == grammarAccess.getOsAttributeValueRule() ||
				   context == grammarAccess.getOsBranchIdRule() ||
				   context == grammarAccess.getOsBranchIdOrNameRule() ||
				   context == grammarAccess.getOsBranchNameRule() ||
				   context == grammarAccess.getOsBranchPatternRule() ||
				   context == grammarAccess.getOsDotExpressionRule() ||
				   context == grammarAccess.getOsDotExpressionAccess().getOsDotExpressionRefAction_1_0() ||
				   context == grammarAccess.getOsLimitRule() ||
				   context == grammarAccess.getOsMetaTypeIdRule() ||
				   context == grammarAccess.getOsTxCommentRule() ||
				   context == grammarAccess.getOsTxCommentPatternRule() ||
				   context == grammarAccess.getOsTxIdRule() ||
				   context == grammarAccess.getOsTxTimestampRule() ||
				   context == grammarAccess.getOsVariableReferenceRule()) {
					sequence_OsVariableReference(context, (OsVariableReference) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE:
				if(context == grammarAccess.getOsAttributeClauseRule() ||
				   context == grammarAccess.getOseAttributeOpClauseRule()) {
					sequence_OseAttributeOpClause(context, (OseAttributeOpClause) semanticObject); 
					return; 
				}
				else break;
			case OrcsScriptDslPackage.SCRIPT_VERSION:
				if(context == grammarAccess.getScriptVersionRule()) {
					sequence_ScriptVersion(context, (ScriptVersion) semanticObject); 
					return; 
				}
				else break;
			}
		if (errorAcceptor != null) errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Constraint:
	 *     (version=ScriptVersion? statements+=ScriptStatement*)
	 */
	protected void sequence_OrcsScript(EObject context, OrcsScript semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='art-guid' (ids+=OsArtifactGuid | (ids+=OsArtifactGuid ids+=OsArtifactGuid*)))
	 */
	protected void sequence_OsArtifactGuidCriteria(EObject context, OsArtifactGuidCriteria semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='art-id' (ids+=OsArtifactId | (ids+=OsArtifactId ids+=OsArtifactId*)))
	 */
	protected void sequence_OsArtifactIdCriteria(EObject context, OsArtifactIdCriteria semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     name='*'
	 */
	protected void sequence_OsArtifactQueryAll(EObject context, OsArtifactQueryAll semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_ARTIFACT_QUERY__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_ARTIFACT_QUERY__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsArtifactQueryAllAccess().getNameAsteriskKeyword_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='where' criteria+=OsItemCriteria criteria+=OsItemCriteria*)
	 */
	protected void sequence_OsArtifactQueryByPredicate(EObject context, OsArtifactQueryByPredicate semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='artifacts' data=OsArtifactQuery collect=OsCollectClause?)
	 */
	protected void sequence_OsArtifactQueryStatement(EObject context, OsArtifactQueryStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='art-type' clause=OsArtifactTypeClause)
	 */
	protected void sequence_OsArtifactTypeCriteria(EObject context, OsArtifactTypeCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_ARTIFACT_TYPE_CRITERIA__CLAUSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_ARTIFACT_TYPE_CRITERIA__CLAUSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsArtifactTypeCriteriaAccess().getNameArtTypeKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsArtifactTypeCriteriaAccess().getClauseOsArtifactTypeClauseParserRuleCall_1_0(), semanticObject.getClause());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='=' (types+=OsMetaTypeId | (types+=OsMetaTypeId types+=OsMetaTypeId*)))
	 */
	protected void sequence_OsArtifactTypeEqualsClause(EObject context, OsArtifactTypeEqualsClause semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='instance-of' (types+=OsMetaTypeId | (types+=OsMetaTypeId types+=OsMetaTypeId*)))
	 */
	protected void sequence_OsArtifactTypeInstanceOfClause(EObject context, OsArtifactTypeInstanceOfClause semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     right=OsAssignmentValue
	 */
	protected void sequence_OsAssignment(EObject context, OsAssignment semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_ASSIGNMENT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_ASSIGNMENT__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsAssignmentAccess().getRightOsAssignmentValueParserRuleCall_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='attribute' clause=OsAttributeClause)
	 */
	protected void sequence_OsAttributeCriteria(EObject context, OsAttributeCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_ATTRIBUTE_CRITERIA__CLAUSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_ATTRIBUTE_CRITERIA__CLAUSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsAttributeCriteriaAccess().getNameAttributeKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsAttributeCriteriaAccess().getClauseOsAttributeClauseParserRuleCall_1_0(), semanticObject.getClause());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ((types+=OsMetaTypeId | (types+=OsMetaTypeId types+=OsMetaTypeId*)) name='exists')
	 */
	protected void sequence_OsAttributeExistClause(EObject context, OsAttributeExistClause semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (isTrue?='true'?)
	 */
	protected void sequence_OsBooleanLiteral(EObject context, OsBooleanLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='archived' filter=OsBranchArchiveFilter)
	 */
	protected void sequence_OsBranchArchivedCriteria(EObject context, OsBranchArchivedCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_ARCHIVED_CRITERIA__FILTER) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_ARCHIVED_CRITERIA__FILTER));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsBranchArchivedCriteriaAccess().getNameArchivedKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsBranchArchivedCriteriaAccess().getFilterOsBranchArchiveFilterEnumRuleCall_2_0(), semanticObject.getFilter());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='child-of' id=OsBranchId)
	 */
	protected void sequence_OsBranchChildOfClause(EObject context, OsBranchChildOfClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_CLAUSE__ID) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_CLAUSE__ID));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsBranchChildOfClauseAccess().getNameChildOfKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsBranchChildOfClauseAccess().getIdOsBranchIdParserRuleCall_1_0(), semanticObject.getId());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='branch-id' (ids+=OsBranchId | (ids+=OsBranchId ids+=OsBranchId*)))
	 */
	protected void sequence_OsBranchIdCriteria(EObject context, OsBranchIdCriteria semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='name' clause=OsBranchNameClause)
	 */
	protected void sequence_OsBranchNameCriteria(EObject context, OsBranchNameCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_NAME_CRITERIA__CLAUSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_NAME_CRITERIA__CLAUSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsBranchNameCriteriaAccess().getNameNameKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsBranchNameCriteriaAccess().getClauseOsBranchNameClauseParserRuleCall_1_0(), semanticObject.getClause());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='=' value=OsBranchName)
	 */
	protected void sequence_OsBranchNameEqualsClause(EObject context, OsBranchNameEqualsClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_NAME_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_NAME_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_NAME_CLAUSE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_NAME_CLAUSE__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsBranchNameEqualsClauseAccess().getNameEqualsSignKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsBranchNameEqualsClauseAccess().getValueOsBranchNameParserRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='matches' value=OsBranchPattern)
	 */
	protected void sequence_OsBranchNamePatternClause(EObject context, OsBranchNamePatternClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_NAME_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_NAME_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_NAME_CLAUSE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_NAME_CLAUSE__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsBranchNamePatternClauseAccess().getNameMatchesKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsBranchNamePatternClauseAccess().getValueOsBranchPatternParserRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='branch' clause=OsBranchClause)
	 */
	protected void sequence_OsBranchOfCriteria(EObject context, OsBranchOfCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_OF_CRITERIA__CLAUSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_OF_CRITERIA__CLAUSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsBranchOfCriteriaAccess().getNameBranchKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsBranchOfCriteriaAccess().getClauseOsBranchClauseParserRuleCall_2_0(), semanticObject.getClause());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='parent-of' id=OsBranchId)
	 */
	protected void sequence_OsBranchParentOfClause(EObject context, OsBranchParentOfClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_CLAUSE__ID) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_CLAUSE__ID));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsBranchParentOfClauseAccess().getNameParentOfKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsBranchParentOfClauseAccess().getIdOsBranchIdParserRuleCall_1_0(), semanticObject.getId());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     name='*'
	 */
	protected void sequence_OsBranchQueryAll(EObject context, OsBranchQueryAll semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_QUERY_ALL__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_QUERY_ALL__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsBranchQueryAllAccess().getNameAsteriskKeyword_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     name=OsBranchIdOrName
	 */
	protected void sequence_OsBranchQueryById(EObject context, OsBranchQueryById semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_QUERY_BY_ID__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_BRANCH_QUERY_BY_ID__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsBranchQueryByIdAccess().getNameOsBranchIdOrNameParserRuleCall_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='where' criteria+=OsBranchCriteria criteria+=OsBranchCriteria*)
	 */
	protected void sequence_OsBranchQueryByPredicate(EObject context, OsBranchQueryByPredicate semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='branch' data=OsBranchQuery collect=OsCollectClause?)
	 */
	protected void sequence_OsBranchQueryStatement(EObject context, OsBranchQueryStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='state' (states+=OsBranchState | (states+=OsBranchState states+=OsBranchState*)))
	 */
	protected void sequence_OsBranchStateCriteria(EObject context, OsBranchStateCriteria semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='type' (types+=OsBranchType | (types+=OsBranchType types+=OsBranchType*)))
	 */
	protected void sequence_OsBranchTypeCriteria(EObject context, OsBranchTypeCriteria semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     name='*'
	 */
	protected void sequence_OsCollectAllFieldsExpression(EObject context, OsCollectAllFieldsExpression semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_COLLECT_EXPRESSION__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_COLLECT_EXPRESSION__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsCollectAllFieldsExpressionAccess().getNameAsteriskKeyword_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='collect' expression=OsCollectObjectExpression limit=OsLimitClause?)
	 */
	protected void sequence_OsCollectClause(EObject context, OsCollectClause semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=OsFieldId alias=OsAlias?)
	 */
	protected void sequence_OsCollectFieldExpression(EObject context, OsCollectFieldExpression semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (
	 *         name=OsCollectTypeId 
	 *         alias=OsAlias? 
	 *         (expressions+=OsCollectAllFieldsExpression | (expressions+=OsCollectItemExpression expressions+=OsCollectItemExpression*))
	 *     )
	 */
	protected void sequence_OsCollectObjectExpression(EObject context, OsCollectObjectExpression semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (ref=OsDotExpression_OsDotExpression_1_0 tail=[OsExpression|ID])
	 */
	protected void sequence_OsDotExpression(EObject context, OsDotExpression semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_DOT_EXPRESSION__REF) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_DOT_EXPRESSION__REF));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_DOT_EXPRESSION__TAIL) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_DOT_EXPRESSION__TAIL));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsDotExpressionAccess().getOsDotExpressionRefAction_1_0(), semanticObject.getRef());
		feeder.accept(grammarAccess.getOsDotExpressionAccess().getTailOsExpressionIDTerminalRuleCall_1_2_0_1(), semanticObject.getTail());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='find' query=OsObjectQuery)
	 */
	protected void sequence_OsFindClause(EObject context, OsFindClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_FIND_CLAUSE__QUERY) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_FIND_CLAUSE__QUERY));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsFindClauseAccess().getNameFindKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsFindClauseAccess().getQueryOsObjectQueryParserRuleCall_1_0(), semanticObject.getQuery());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='follow' stmt=OsFollowStatement)
	 */
	protected void sequence_OsFollowClause(EObject context, OsFollowClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_FOLLOW_CLAUSE__STMT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_FOLLOW_CLAUSE__STMT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsFollowClauseAccess().getNameFollowKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsFollowClauseAccess().getStmtOsFollowStatementParserRuleCall_1_0(), semanticObject.getStmt());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='relation' type=OsMetaTypeId side=OsRelationSide (criteria+=OsItemCriteria criteria+=OsItemCriteria*)? collect=OsCollectClause?)
	 */
	protected void sequence_OsFollowRelationType(EObject context, OsFollowRelationType semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='limit' limit=OsLimit)
	 */
	protected void sequence_OsLimitClause(EObject context, OsLimitClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_LIMIT_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_LIMIT_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_LIMIT_CLAUSE__LIMIT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_LIMIT_CLAUSE__LIMIT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsLimitClauseAccess().getNameLimitKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsLimitClauseAccess().getLimitOsLimitParserRuleCall_1_0(), semanticObject.getLimit());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ((elements+=OsExpression elements+=OsExpression*)?)
	 */
	protected void sequence_OsListLiteral(EObject context, OsListLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     {OsNullLiteral}
	 */
	protected void sequence_OsNullLiteral(EObject context, OsNullLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     value=Number
	 */
	protected void sequence_OsNumberLiteral(EObject context, OsNumberLiteral semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_NUMBER_LITERAL__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_NUMBER_LITERAL__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsNumberLiteralAccess().getValueNumberParserRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='start' query=OsQuery clause+=OsClause*)
	 */
	protected void sequence_OsQueryExpression(EObject context, OsQueryExpression semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     stmt=OsQueryExpression
	 */
	protected void sequence_OsQueryStatement(EObject context, OsQueryStatement semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_QUERY_STATEMENT__STMT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_QUERY_STATEMENT__STMT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsQueryStatementAccess().getStmtOsQueryExpressionParserRuleCall_0_0(), semanticObject.getStmt());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='type' type=OsMetaTypeId side=OsRelationSide (ids+=OsArtifactId | (ids+=OsArtifactId ids+=OsArtifactId*)))
	 */
	protected void sequence_OsRelatedToClause(EObject context, OsRelatedToClause semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='relation' clause=OsRelationClause)
	 */
	protected void sequence_OsRelationCriteria(EObject context, OsRelationCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_RELATION_CRITERIA__CLAUSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_RELATION_CRITERIA__CLAUSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsRelationCriteriaAccess().getNameRelationKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsRelationCriteriaAccess().getClauseOsRelationClauseParserRuleCall_1_0(), semanticObject.getClause());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='type' type=OsMetaTypeId op=OsExistenceOperator side=OsRelationSide?)
	 */
	protected void sequence_OsRelationExistClause(EObject context, OsRelationExistClause semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     value=STRING
	 */
	protected void sequence_OsStringLiteral(EObject context, OsStringLiteral semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_STRING_LITERAL__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_STRING_LITERAL__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsStringLiteralAccess().getValueSTRINGTerminalRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     value=OsTemplateId
	 */
	protected void sequence_OsTemplateLiteral(EObject context, OsTemplateLiteral semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TEMPLATE_LITERAL__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TEMPLATE_LITERAL__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTemplateLiteralAccess().getValueOsTemplateIdParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='author-id' (ids+=OsArtifactId | (ids+=OsArtifactId ids+=OsArtifactId*)))
	 */
	protected void sequence_OsTxAuthorIdCriteria(EObject context, OsTxAuthorIdCriteria semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='branch-id' (ids+=OsBranchId | (ids+=OsBranchId ids+=OsBranchId*)))
	 */
	protected void sequence_OsTxBranchIdCriteria(EObject context, OsTxBranchIdCriteria semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='comment' clause=OsTxCommentClause)
	 */
	protected void sequence_OsTxCommentCriteria(EObject context, OsTxCommentCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CRITERIA__CLAUSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CRITERIA__CLAUSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxCommentCriteriaAccess().getNameCommentKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsTxCommentCriteriaAccess().getClauseOsTxCommentClauseParserRuleCall_1_0(), semanticObject.getClause());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='=' value=OsTxComment)
	 */
	protected void sequence_OsTxCommentEqualsClause(EObject context, OsTxCommentEqualsClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CLAUSE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CLAUSE__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxCommentEqualsClauseAccess().getNameEqualsSignKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsTxCommentEqualsClauseAccess().getValueOsTxCommentParserRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='matches' value=OsTxCommentPattern)
	 */
	protected void sequence_OsTxCommentPatternClause(EObject context, OsTxCommentPatternClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CLAUSE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMENT_CLAUSE__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxCommentPatternClauseAccess().getNameMatchesKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsTxCommentPatternClauseAccess().getValueOsTxCommentPatternParserRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='commit-id' clause=OsTxCommitIdClause)
	 */
	protected void sequence_OsTxCommitIdCriteria(EObject context, OsTxCommitIdCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMIT_ID_CRITERIA__CLAUSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMIT_ID_CRITERIA__CLAUSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxCommitIdCriteriaAccess().getNameCommitIdKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsTxCommitIdCriteriaAccess().getClauseOsTxCommitIdClauseParserRuleCall_1_0(), semanticObject.getClause());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='=' (ids+=OsArtifactId | (ids+=OsArtifactId ids+=OsArtifactId*)))
	 */
	protected void sequence_OsTxCommitIdEqualsClause(EObject context, OsTxCommitIdEqualsClause semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     name='is'
	 */
	protected void sequence_OsTxCommitIdIsNullClause(EObject context, OsTxCommitIdIsNullClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMIT_ID_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_COMMIT_ID_CLAUSE__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxCommitIdIsNullClauseAccess().getNameIsKeyword_0_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='is-head' id=OsBranchId)
	 */
	protected void sequence_OsTxHeadOfBranchIdCriteria(EObject context, OsTxHeadOfBranchIdCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_HEAD_OF_BRANCH_ID_CRITERIA__ID) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_HEAD_OF_BRANCH_ID_CRITERIA__ID));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxHeadOfBranchIdCriteriaAccess().getNameIsHeadKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsTxHeadOfBranchIdCriteriaAccess().getIdOsBranchIdParserRuleCall_3_0(), semanticObject.getId());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='tx-id' clause=OsTxIdClause)
	 */
	protected void sequence_OsTxIdCriteria(EObject context, OsTxIdCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_CRITERIA__CLAUSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_CRITERIA__CLAUSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxIdCriteriaAccess().getNameTxIdKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsTxIdCriteriaAccess().getClauseOsTxIdClauseParserRuleCall_1_0(), semanticObject.getClause());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='=' (ids+=OsTxId | (ids+=OsTxId ids+=OsTxId*)))
	 */
	protected void sequence_OsTxIdEqualsClause(EObject context, OsTxIdEqualsClause semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (op=OsNonEqualOperator id=OsTxId)
	 */
	protected void sequence_OsTxIdOpClause(EObject context, OsTxIdOpClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_OP_CLAUSE__OP) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_OP_CLAUSE__OP));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_OP_CLAUSE__ID) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_OP_CLAUSE__ID));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxIdOpClauseAccess().getOpOsNonEqualOperatorEnumRuleCall_0_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getOsTxIdOpClauseAccess().getIdOsTxIdParserRuleCall_1_0(), semanticObject.getId());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='in' fromId=OsTxId toId=OsTxId)
	 */
	protected void sequence_OsTxIdRangeClause(EObject context, OsTxIdRangeClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_RANGE_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_RANGE_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_RANGE_CLAUSE__FROM_ID) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_RANGE_CLAUSE__FROM_ID));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_RANGE_CLAUSE__TO_ID) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_ID_RANGE_CLAUSE__TO_ID));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxIdRangeClauseAccess().getNameInKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsTxIdRangeClauseAccess().getFromIdOsTxIdParserRuleCall_2_0(), semanticObject.getFromId());
		feeder.accept(grammarAccess.getOsTxIdRangeClauseAccess().getToIdOsTxIdParserRuleCall_4_0(), semanticObject.getToId());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     name='*'
	 */
	protected void sequence_OsTxQueryAll(EObject context, OsTxQueryAll semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_QUERY_ALL__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_QUERY_ALL__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxQueryAllAccess().getNameAsteriskKeyword_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     name=OsTxId
	 */
	protected void sequence_OsTxQueryById(EObject context, OsTxQueryById semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_QUERY_BY_ID__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_QUERY_BY_ID__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxQueryByIdAccess().getNameOsTxIdParserRuleCall_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='where' criteria+=OsTxCriteria criteria+=OsTxCriteria*)
	 */
	protected void sequence_OsTxQueryByPredicate(EObject context, OsTxQueryByPredicate semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='tx' data=OsTxQuery collect=OsCollectClause?)
	 */
	protected void sequence_OsTxQueryStatement(EObject context, OsTxQueryStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='date' clause=OsTxTimestampClause)
	 */
	protected void sequence_OsTxTimestampCriteria(EObject context, OsTxTimestampCriteria semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_CRITIERIA__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_CRITERIA__CLAUSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_CRITERIA__CLAUSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxTimestampCriteriaAccess().getNameDateKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsTxTimestampCriteriaAccess().getClauseOsTxTimestampClauseParserRuleCall_1_0(), semanticObject.getClause());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (op=OsOperator timestamp=OsTxTimestamp)
	 */
	protected void sequence_OsTxTimestampOpClause(EObject context, OsTxTimestampOpClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_OP_CLAUSE__OP) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_OP_CLAUSE__OP));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxTimestampOpClauseAccess().getOpOsOperatorEnumRuleCall_0_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getOsTxTimestampOpClauseAccess().getTimestampOsTxTimestampParserRuleCall_1_0(), semanticObject.getTimestamp());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='in' from=OsTxTimestamp to=OsTxTimestamp)
	 */
	protected void sequence_OsTxTimestampRangeClause(EObject context, OsTxTimestampRangeClause semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_RANGE_CLAUSE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_RANGE_CLAUSE__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_RANGE_CLAUSE__FROM) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_RANGE_CLAUSE__FROM));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_RANGE_CLAUSE__TO) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_TX_TIMESTAMP_RANGE_CLAUSE__TO));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsTxTimestampRangeClauseAccess().getNameInKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getOsTxTimestampRangeClauseAccess().getFromOsTxTimestampParserRuleCall_2_0(), semanticObject.getFrom());
		feeder.accept(grammarAccess.getOsTxTimestampRangeClauseAccess().getToOsTxTimestampParserRuleCall_4_0(), semanticObject.getTo());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name='type' (types+=OsTxType | (types+=OsTxType types+=OsTxType*)))
	 */
	protected void sequence_OsTxTypeCriteria(EObject context, OsTxTypeCriteria semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (elements+=OsVariable elements+=OsVariable*)
	 */
	protected void sequence_OsVariableDeclaration(EObject context, OsVariableDeclaration semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ref=[OsVariable|ID]
	 */
	protected void sequence_OsVariableReference(EObject context, OsVariableReference semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.OS_VARIABLE_REFERENCE__REF) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.OS_VARIABLE_REFERENCE__REF));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOsVariableReferenceAccess().getRefOsVariableIDTerminalRuleCall_1_0_1(), semanticObject.getRef());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID right=OsExpression?)
	 */
	protected void sequence_OsVariable(EObject context, OsVariable semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (
	 *         name='=' 
	 *         (types+=OsMetaTypeId | (types+=OsMetaTypeId types+=OsMetaTypeId*)) 
	 *         (options+=OsQueryOption | (options+=OsQueryOption options+=OsQueryOption*))? 
	 *         (values+=OsAttributeValue | (values+=OsAttributeValue values+=OsAttributeValue*))
	 *     )
	 */
	protected void sequence_OseAttributeOpClause(EObject context, OseAttributeOpClause semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name='script-version' version=SEMANTIC_VERSION)
	 */
	protected void sequence_ScriptVersion(EObject context, ScriptVersion semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.SCRIPT_VERSION__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.SCRIPT_VERSION__NAME));
			if(transientValues.isValueTransient(semanticObject, OrcsScriptDslPackage.Literals.SCRIPT_VERSION__VERSION) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, OrcsScriptDslPackage.Literals.SCRIPT_VERSION__VERSION));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getScriptVersionAccess().getNameScriptVersionKeyword_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getScriptVersionAccess().getVersionSEMANTIC_VERSIONTerminalRuleCall_1_0(), semanticObject.getVersion());
		feeder.finish();
	}
}
