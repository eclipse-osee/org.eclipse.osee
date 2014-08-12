/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class OrcsScriptDslFactoryImpl extends EFactoryImpl implements OrcsScriptDslFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OrcsScriptDslFactory init()
  {
    try
    {
      OrcsScriptDslFactory theOrcsScriptDslFactory = (OrcsScriptDslFactory)EPackage.Registry.INSTANCE.getEFactory(OrcsScriptDslPackage.eNS_URI);
      if (theOrcsScriptDslFactory != null)
      {
        return theOrcsScriptDslFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new OrcsScriptDslFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrcsScriptDslFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case OrcsScriptDslPackage.ORCS_SCRIPT: return createOrcsScript();
      case OrcsScriptDslPackage.SCRIPT_STATEMENT: return createScriptStatement();
      case OrcsScriptDslPackage.SCRIPT_VERSION: return createScriptVersion();
      case OrcsScriptDslPackage.OS_EXPRESSION: return createOsExpression();
      case OrcsScriptDslPackage.OS_COLLECTION_LITERAL: return createOsCollectionLiteral();
      case OrcsScriptDslPackage.OS_LIST_LITERAL: return createOsListLiteral();
      case OrcsScriptDslPackage.OS_QUERY_STATEMENT: return createOsQueryStatement();
      case OrcsScriptDslPackage.OS_QUERY: return createOsQuery();
      case OrcsScriptDslPackage.OS_CRITIERIA: return createOsCritieria();
      case OrcsScriptDslPackage.OS_CLAUSE: return createOsClause();
      case OrcsScriptDslPackage.OS_COLLECT_CLAUSE: return createOsCollectClause();
      case OrcsScriptDslPackage.OS_LIMIT_CLAUSE: return createOsLimitClause();
      case OrcsScriptDslPackage.OS_COLLECT_EXPRESSION: return createOsCollectExpression();
      case OrcsScriptDslPackage.OS_FIND_CLAUSE: return createOsFindClause();
      case OrcsScriptDslPackage.OS_OBJECT_QUERY: return createOsObjectQuery();
      case OrcsScriptDslPackage.OS_BRANCH_QUERY_STATEMENT: return createOsBranchQueryStatement();
      case OrcsScriptDslPackage.OS_BRANCH_QUERY: return createOsBranchQuery();
      case OrcsScriptDslPackage.OS_BRANCH_QUERY_BY_ID: return createOsBranchQueryById();
      case OrcsScriptDslPackage.OS_BRANCH_QUERY_ALL: return createOsBranchQueryAll();
      case OrcsScriptDslPackage.OS_BRANCH_QUERY_BY_PREDICATE: return createOsBranchQueryByPredicate();
      case OrcsScriptDslPackage.OS_BRANCH_CRITERIA: return createOsBranchCriteria();
      case OrcsScriptDslPackage.OS_BRANCH_NAME_CRITERIA: return createOsBranchNameCriteria();
      case OrcsScriptDslPackage.OS_BRANCH_NAME_CLAUSE: return createOsBranchNameClause();
      case OrcsScriptDslPackage.OS_BRANCH_NAME_EQUALS_CLAUSE: return createOsBranchNameEqualsClause();
      case OrcsScriptDslPackage.OS_BRANCH_NAME_PATTERN_CLAUSE: return createOsBranchNamePatternClause();
      case OrcsScriptDslPackage.OS_BRANCH_TYPE_CRITERIA: return createOsBranchTypeCriteria();
      case OrcsScriptDslPackage.OS_BRANCH_STATE_CRITERIA: return createOsBranchStateCriteria();
      case OrcsScriptDslPackage.OS_BRANCH_ARCHIVED_CRITERIA: return createOsBranchArchivedCriteria();
      case OrcsScriptDslPackage.OS_BRANCH_ID_CRITERIA: return createOsBranchIdCriteria();
      case OrcsScriptDslPackage.OS_BRANCH_OF_CRITERIA: return createOsBranchOfCriteria();
      case OrcsScriptDslPackage.OS_BRANCH_CLAUSE: return createOsBranchClause();
      case OrcsScriptDslPackage.OS_BRANCH_CHILD_OF_CLAUSE: return createOsBranchChildOfClause();
      case OrcsScriptDslPackage.OS_BRANCH_PARENT_OF_CLAUSE: return createOsBranchParentOfClause();
      case OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT: return createOsTxQueryStatement();
      case OrcsScriptDslPackage.OS_TX_QUERY: return createOsTxQuery();
      case OrcsScriptDslPackage.OS_TX_QUERY_BY_ID: return createOsTxQueryById();
      case OrcsScriptDslPackage.OS_TX_QUERY_ALL: return createOsTxQueryAll();
      case OrcsScriptDslPackage.OS_TX_QUERY_BY_PREDICATE: return createOsTxQueryByPredicate();
      case OrcsScriptDslPackage.OS_TX_CRITERIA: return createOsTxCriteria();
      case OrcsScriptDslPackage.OS_TX_TYPE_CRITERIA: return createOsTxTypeCriteria();
      case OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA: return createOsTxCommentCriteria();
      case OrcsScriptDslPackage.OS_TX_COMMENT_CLAUSE: return createOsTxCommentClause();
      case OrcsScriptDslPackage.OS_TX_COMMENT_EQUALS_CLAUSE: return createOsTxCommentEqualsClause();
      case OrcsScriptDslPackage.OS_TX_COMMENT_PATTERN_CLAUSE: return createOsTxCommentPatternClause();
      case OrcsScriptDslPackage.OS_TX_BRANCH_ID_CRITERIA: return createOsTxBranchIdCriteria();
      case OrcsScriptDslPackage.OS_TX_HEAD_OF_BRANCH_ID_CRITERIA: return createOsTxHeadOfBranchIdCriteria();
      case OrcsScriptDslPackage.OS_TX_AUTHOR_ID_CRITERIA: return createOsTxAuthorIdCriteria();
      case OrcsScriptDslPackage.OS_TX_COMMIT_ID_CRITERIA: return createOsTxCommitIdCriteria();
      case OrcsScriptDslPackage.OS_TX_COMMIT_ID_CLAUSE: return createOsTxCommitIdClause();
      case OrcsScriptDslPackage.OS_TX_COMMIT_ID_EQUALS_CLAUSE: return createOsTxCommitIdEqualsClause();
      case OrcsScriptDslPackage.OS_TX_COMMIT_ID_IS_NULL_CLAUSE: return createOsTxCommitIdIsNullClause();
      case OrcsScriptDslPackage.OS_TX_ID_CRITERIA: return createOsTxIdCriteria();
      case OrcsScriptDslPackage.OS_TX_ID_CLAUSE: return createOsTxIdClause();
      case OrcsScriptDslPackage.OS_TX_ID_EQUALS_CLAUSE: return createOsTxIdEqualsClause();
      case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE: return createOsTxIdOpClause();
      case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE: return createOsTxIdRangeClause();
      case OrcsScriptDslPackage.OS_TX_TIMESTAMP_CRITERIA: return createOsTxTimestampCriteria();
      case OrcsScriptDslPackage.OS_TX_TIMESTAMP_CLAUSE: return createOsTxTimestampClause();
      case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE: return createOsTxTimestampOpClause();
      case OrcsScriptDslPackage.OS_TX_TIMESTAMP_RANGE_CLAUSE: return createOsTxTimestampRangeClause();
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT: return createOsArtifactQueryStatement();
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY: return createOsArtifactQuery();
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_ALL: return createOsArtifactQueryAll();
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_BY_PREDICATE: return createOsArtifactQueryByPredicate();
      case OrcsScriptDslPackage.OS_ITEM_CRITERIA: return createOsItemCriteria();
      case OrcsScriptDslPackage.OS_ARTIFACT_CRITERIA: return createOsArtifactCriteria();
      case OrcsScriptDslPackage.OS_ARTIFACT_ID_CRITERIA: return createOsArtifactIdCriteria();
      case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_CRITERIA: return createOsArtifactTypeCriteria();
      case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_CLAUSE: return createOsArtifactTypeClause();
      case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_EQUALS_CLAUSE: return createOsArtifactTypeEqualsClause();
      case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_INSTANCE_OF_CLAUSE: return createOsArtifactTypeInstanceOfClause();
      case OrcsScriptDslPackage.OS_ATTRIBUTE_CRITERIA: return createOsAttributeCriteria();
      case OrcsScriptDslPackage.OS_ATTRIBUTE_CLAUSE: return createOsAttributeClause();
      case OrcsScriptDslPackage.OS_ATTRIBUTE_EXIST_CLAUSE: return createOsAttributeExistClause();
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE: return createOseAttributeOpClause();
      case OrcsScriptDslPackage.OS_RELATION_CRITERIA: return createOsRelationCriteria();
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE: return createOsRelationClause();
      case OrcsScriptDslPackage.OS_RELATION_EXIST_CLAUSE: return createOsRelationExistClause();
      case OrcsScriptDslPackage.OS_RELATED_TO_CLAUSE: return createOsRelatedToClause();
      case OrcsScriptDslPackage.OS_FOLLOW_CLAUSE: return createOsFollowClause();
      case OrcsScriptDslPackage.OS_FOLLOW_STATEMENT: return createOsFollowStatement();
      case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE: return createOsFollowRelationType();
      case OrcsScriptDslPackage.OS_VARIABLE_DECLARATION: return createOsVariableDeclaration();
      case OrcsScriptDslPackage.OS_VARIABLE: return createOsVariable();
      case OrcsScriptDslPackage.OS_ASSIGNMENT: return createOsAssignment();
      case OrcsScriptDslPackage.OS_BOOLEAN_LITERAL: return createOsBooleanLiteral();
      case OrcsScriptDslPackage.OS_NULL_LITERAL: return createOsNullLiteral();
      case OrcsScriptDslPackage.OS_NUMBER_LITERAL: return createOsNumberLiteral();
      case OrcsScriptDslPackage.OS_STRING_LITERAL: return createOsStringLiteral();
      case OrcsScriptDslPackage.OS_TEMPLATE_LITERAL: return createOsTemplateLiteral();
      case OrcsScriptDslPackage.OS_VARIABLE_REFERENCE: return createOsVariableReference();
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION: return createOsDotExpression();
      case OrcsScriptDslPackage.OS_QUERY_EXPRESSION: return createOsQueryExpression();
      case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION: return createOsCollectObjectExpression();
      case OrcsScriptDslPackage.OS_COLLECT_ALL_FIELDS_EXPRESSION: return createOsCollectAllFieldsExpression();
      case OrcsScriptDslPackage.OS_COLLECT_FIELD_EXPRESSION: return createOsCollectFieldExpression();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case OrcsScriptDslPackage.OS_BRANCH_STATE:
        return createOsBranchStateFromString(eDataType, initialValue);
      case OrcsScriptDslPackage.OS_BRANCH_TYPE:
        return createOsBranchTypeFromString(eDataType, initialValue);
      case OrcsScriptDslPackage.OS_BRANCH_ARCHIVE_FILTER:
        return createOsBranchArchiveFilterFromString(eDataType, initialValue);
      case OrcsScriptDslPackage.OS_TX_TYPE:
        return createOsTxTypeFromString(eDataType, initialValue);
      case OrcsScriptDslPackage.OS_RELATION_SIDE:
        return createOsRelationSideFromString(eDataType, initialValue);
      case OrcsScriptDslPackage.OS_OPERATOR:
        return createOsOperatorFromString(eDataType, initialValue);
      case OrcsScriptDslPackage.OS_NON_EQUAL_OPERATOR:
        return createOsNonEqualOperatorFromString(eDataType, initialValue);
      case OrcsScriptDslPackage.OS_EXISTENCE_OPERATOR:
        return createOsExistenceOperatorFromString(eDataType, initialValue);
      case OrcsScriptDslPackage.OS_QUERY_OPTION:
        return createOsQueryOptionFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case OrcsScriptDslPackage.OS_BRANCH_STATE:
        return convertOsBranchStateToString(eDataType, instanceValue);
      case OrcsScriptDslPackage.OS_BRANCH_TYPE:
        return convertOsBranchTypeToString(eDataType, instanceValue);
      case OrcsScriptDslPackage.OS_BRANCH_ARCHIVE_FILTER:
        return convertOsBranchArchiveFilterToString(eDataType, instanceValue);
      case OrcsScriptDslPackage.OS_TX_TYPE:
        return convertOsTxTypeToString(eDataType, instanceValue);
      case OrcsScriptDslPackage.OS_RELATION_SIDE:
        return convertOsRelationSideToString(eDataType, instanceValue);
      case OrcsScriptDslPackage.OS_OPERATOR:
        return convertOsOperatorToString(eDataType, instanceValue);
      case OrcsScriptDslPackage.OS_NON_EQUAL_OPERATOR:
        return convertOsNonEqualOperatorToString(eDataType, instanceValue);
      case OrcsScriptDslPackage.OS_EXISTENCE_OPERATOR:
        return convertOsExistenceOperatorToString(eDataType, instanceValue);
      case OrcsScriptDslPackage.OS_QUERY_OPTION:
        return convertOsQueryOptionToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrcsScript createOrcsScript()
  {
    OrcsScriptImpl orcsScript = new OrcsScriptImpl();
    return orcsScript;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ScriptStatement createScriptStatement()
  {
    ScriptStatementImpl scriptStatement = new ScriptStatementImpl();
    return scriptStatement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ScriptVersion createScriptVersion()
  {
    ScriptVersionImpl scriptVersion = new ScriptVersionImpl();
    return scriptVersion;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsExpression createOsExpression()
  {
    OsExpressionImpl osExpression = new OsExpressionImpl();
    return osExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsCollectionLiteral createOsCollectionLiteral()
  {
    OsCollectionLiteralImpl osCollectionLiteral = new OsCollectionLiteralImpl();
    return osCollectionLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsListLiteral createOsListLiteral()
  {
    OsListLiteralImpl osListLiteral = new OsListLiteralImpl();
    return osListLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsQueryStatement createOsQueryStatement()
  {
    OsQueryStatementImpl osQueryStatement = new OsQueryStatementImpl();
    return osQueryStatement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsQuery createOsQuery()
  {
    OsQueryImpl osQuery = new OsQueryImpl();
    return osQuery;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsCritieria createOsCritieria()
  {
    OsCritieriaImpl osCritieria = new OsCritieriaImpl();
    return osCritieria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsClause createOsClause()
  {
    OsClauseImpl osClause = new OsClauseImpl();
    return osClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsCollectClause createOsCollectClause()
  {
    OsCollectClauseImpl osCollectClause = new OsCollectClauseImpl();
    return osCollectClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsLimitClause createOsLimitClause()
  {
    OsLimitClauseImpl osLimitClause = new OsLimitClauseImpl();
    return osLimitClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsCollectExpression createOsCollectExpression()
  {
    OsCollectExpressionImpl osCollectExpression = new OsCollectExpressionImpl();
    return osCollectExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsFindClause createOsFindClause()
  {
    OsFindClauseImpl osFindClause = new OsFindClauseImpl();
    return osFindClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsObjectQuery createOsObjectQuery()
  {
    OsObjectQueryImpl osObjectQuery = new OsObjectQueryImpl();
    return osObjectQuery;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchQueryStatement createOsBranchQueryStatement()
  {
    OsBranchQueryStatementImpl osBranchQueryStatement = new OsBranchQueryStatementImpl();
    return osBranchQueryStatement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchQuery createOsBranchQuery()
  {
    OsBranchQueryImpl osBranchQuery = new OsBranchQueryImpl();
    return osBranchQuery;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchQueryById createOsBranchQueryById()
  {
    OsBranchQueryByIdImpl osBranchQueryById = new OsBranchQueryByIdImpl();
    return osBranchQueryById;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchQueryAll createOsBranchQueryAll()
  {
    OsBranchQueryAllImpl osBranchQueryAll = new OsBranchQueryAllImpl();
    return osBranchQueryAll;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchQueryByPredicate createOsBranchQueryByPredicate()
  {
    OsBranchQueryByPredicateImpl osBranchQueryByPredicate = new OsBranchQueryByPredicateImpl();
    return osBranchQueryByPredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchCriteria createOsBranchCriteria()
  {
    OsBranchCriteriaImpl osBranchCriteria = new OsBranchCriteriaImpl();
    return osBranchCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchNameCriteria createOsBranchNameCriteria()
  {
    OsBranchNameCriteriaImpl osBranchNameCriteria = new OsBranchNameCriteriaImpl();
    return osBranchNameCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchNameClause createOsBranchNameClause()
  {
    OsBranchNameClauseImpl osBranchNameClause = new OsBranchNameClauseImpl();
    return osBranchNameClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchNameEqualsClause createOsBranchNameEqualsClause()
  {
    OsBranchNameEqualsClauseImpl osBranchNameEqualsClause = new OsBranchNameEqualsClauseImpl();
    return osBranchNameEqualsClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchNamePatternClause createOsBranchNamePatternClause()
  {
    OsBranchNamePatternClauseImpl osBranchNamePatternClause = new OsBranchNamePatternClauseImpl();
    return osBranchNamePatternClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchTypeCriteria createOsBranchTypeCriteria()
  {
    OsBranchTypeCriteriaImpl osBranchTypeCriteria = new OsBranchTypeCriteriaImpl();
    return osBranchTypeCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchStateCriteria createOsBranchStateCriteria()
  {
    OsBranchStateCriteriaImpl osBranchStateCriteria = new OsBranchStateCriteriaImpl();
    return osBranchStateCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchArchivedCriteria createOsBranchArchivedCriteria()
  {
    OsBranchArchivedCriteriaImpl osBranchArchivedCriteria = new OsBranchArchivedCriteriaImpl();
    return osBranchArchivedCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchIdCriteria createOsBranchIdCriteria()
  {
    OsBranchIdCriteriaImpl osBranchIdCriteria = new OsBranchIdCriteriaImpl();
    return osBranchIdCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchOfCriteria createOsBranchOfCriteria()
  {
    OsBranchOfCriteriaImpl osBranchOfCriteria = new OsBranchOfCriteriaImpl();
    return osBranchOfCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchClause createOsBranchClause()
  {
    OsBranchClauseImpl osBranchClause = new OsBranchClauseImpl();
    return osBranchClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchChildOfClause createOsBranchChildOfClause()
  {
    OsBranchChildOfClauseImpl osBranchChildOfClause = new OsBranchChildOfClauseImpl();
    return osBranchChildOfClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchParentOfClause createOsBranchParentOfClause()
  {
    OsBranchParentOfClauseImpl osBranchParentOfClause = new OsBranchParentOfClauseImpl();
    return osBranchParentOfClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxQueryStatement createOsTxQueryStatement()
  {
    OsTxQueryStatementImpl osTxQueryStatement = new OsTxQueryStatementImpl();
    return osTxQueryStatement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxQuery createOsTxQuery()
  {
    OsTxQueryImpl osTxQuery = new OsTxQueryImpl();
    return osTxQuery;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxQueryById createOsTxQueryById()
  {
    OsTxQueryByIdImpl osTxQueryById = new OsTxQueryByIdImpl();
    return osTxQueryById;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxQueryAll createOsTxQueryAll()
  {
    OsTxQueryAllImpl osTxQueryAll = new OsTxQueryAllImpl();
    return osTxQueryAll;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxQueryByPredicate createOsTxQueryByPredicate()
  {
    OsTxQueryByPredicateImpl osTxQueryByPredicate = new OsTxQueryByPredicateImpl();
    return osTxQueryByPredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxCriteria createOsTxCriteria()
  {
    OsTxCriteriaImpl osTxCriteria = new OsTxCriteriaImpl();
    return osTxCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxTypeCriteria createOsTxTypeCriteria()
  {
    OsTxTypeCriteriaImpl osTxTypeCriteria = new OsTxTypeCriteriaImpl();
    return osTxTypeCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxCommentCriteria createOsTxCommentCriteria()
  {
    OsTxCommentCriteriaImpl osTxCommentCriteria = new OsTxCommentCriteriaImpl();
    return osTxCommentCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxCommentClause createOsTxCommentClause()
  {
    OsTxCommentClauseImpl osTxCommentClause = new OsTxCommentClauseImpl();
    return osTxCommentClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxCommentEqualsClause createOsTxCommentEqualsClause()
  {
    OsTxCommentEqualsClauseImpl osTxCommentEqualsClause = new OsTxCommentEqualsClauseImpl();
    return osTxCommentEqualsClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxCommentPatternClause createOsTxCommentPatternClause()
  {
    OsTxCommentPatternClauseImpl osTxCommentPatternClause = new OsTxCommentPatternClauseImpl();
    return osTxCommentPatternClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxBranchIdCriteria createOsTxBranchIdCriteria()
  {
    OsTxBranchIdCriteriaImpl osTxBranchIdCriteria = new OsTxBranchIdCriteriaImpl();
    return osTxBranchIdCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxHeadOfBranchIdCriteria createOsTxHeadOfBranchIdCriteria()
  {
    OsTxHeadOfBranchIdCriteriaImpl osTxHeadOfBranchIdCriteria = new OsTxHeadOfBranchIdCriteriaImpl();
    return osTxHeadOfBranchIdCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxAuthorIdCriteria createOsTxAuthorIdCriteria()
  {
    OsTxAuthorIdCriteriaImpl osTxAuthorIdCriteria = new OsTxAuthorIdCriteriaImpl();
    return osTxAuthorIdCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxCommitIdCriteria createOsTxCommitIdCriteria()
  {
    OsTxCommitIdCriteriaImpl osTxCommitIdCriteria = new OsTxCommitIdCriteriaImpl();
    return osTxCommitIdCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxCommitIdClause createOsTxCommitIdClause()
  {
    OsTxCommitIdClauseImpl osTxCommitIdClause = new OsTxCommitIdClauseImpl();
    return osTxCommitIdClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxCommitIdEqualsClause createOsTxCommitIdEqualsClause()
  {
    OsTxCommitIdEqualsClauseImpl osTxCommitIdEqualsClause = new OsTxCommitIdEqualsClauseImpl();
    return osTxCommitIdEqualsClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxCommitIdIsNullClause createOsTxCommitIdIsNullClause()
  {
    OsTxCommitIdIsNullClauseImpl osTxCommitIdIsNullClause = new OsTxCommitIdIsNullClauseImpl();
    return osTxCommitIdIsNullClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxIdCriteria createOsTxIdCriteria()
  {
    OsTxIdCriteriaImpl osTxIdCriteria = new OsTxIdCriteriaImpl();
    return osTxIdCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxIdClause createOsTxIdClause()
  {
    OsTxIdClauseImpl osTxIdClause = new OsTxIdClauseImpl();
    return osTxIdClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxIdEqualsClause createOsTxIdEqualsClause()
  {
    OsTxIdEqualsClauseImpl osTxIdEqualsClause = new OsTxIdEqualsClauseImpl();
    return osTxIdEqualsClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxIdOpClause createOsTxIdOpClause()
  {
    OsTxIdOpClauseImpl osTxIdOpClause = new OsTxIdOpClauseImpl();
    return osTxIdOpClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxIdRangeClause createOsTxIdRangeClause()
  {
    OsTxIdRangeClauseImpl osTxIdRangeClause = new OsTxIdRangeClauseImpl();
    return osTxIdRangeClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxTimestampCriteria createOsTxTimestampCriteria()
  {
    OsTxTimestampCriteriaImpl osTxTimestampCriteria = new OsTxTimestampCriteriaImpl();
    return osTxTimestampCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxTimestampClause createOsTxTimestampClause()
  {
    OsTxTimestampClauseImpl osTxTimestampClause = new OsTxTimestampClauseImpl();
    return osTxTimestampClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxTimestampOpClause createOsTxTimestampOpClause()
  {
    OsTxTimestampOpClauseImpl osTxTimestampOpClause = new OsTxTimestampOpClauseImpl();
    return osTxTimestampOpClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxTimestampRangeClause createOsTxTimestampRangeClause()
  {
    OsTxTimestampRangeClauseImpl osTxTimestampRangeClause = new OsTxTimestampRangeClauseImpl();
    return osTxTimestampRangeClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactQueryStatement createOsArtifactQueryStatement()
  {
    OsArtifactQueryStatementImpl osArtifactQueryStatement = new OsArtifactQueryStatementImpl();
    return osArtifactQueryStatement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactQuery createOsArtifactQuery()
  {
    OsArtifactQueryImpl osArtifactQuery = new OsArtifactQueryImpl();
    return osArtifactQuery;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactQueryAll createOsArtifactQueryAll()
  {
    OsArtifactQueryAllImpl osArtifactQueryAll = new OsArtifactQueryAllImpl();
    return osArtifactQueryAll;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactQueryByPredicate createOsArtifactQueryByPredicate()
  {
    OsArtifactQueryByPredicateImpl osArtifactQueryByPredicate = new OsArtifactQueryByPredicateImpl();
    return osArtifactQueryByPredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsItemCriteria createOsItemCriteria()
  {
    OsItemCriteriaImpl osItemCriteria = new OsItemCriteriaImpl();
    return osItemCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactCriteria createOsArtifactCriteria()
  {
    OsArtifactCriteriaImpl osArtifactCriteria = new OsArtifactCriteriaImpl();
    return osArtifactCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactIdCriteria createOsArtifactIdCriteria()
  {
    OsArtifactIdCriteriaImpl osArtifactIdCriteria = new OsArtifactIdCriteriaImpl();
    return osArtifactIdCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactTypeCriteria createOsArtifactTypeCriteria()
  {
    OsArtifactTypeCriteriaImpl osArtifactTypeCriteria = new OsArtifactTypeCriteriaImpl();
    return osArtifactTypeCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactTypeClause createOsArtifactTypeClause()
  {
    OsArtifactTypeClauseImpl osArtifactTypeClause = new OsArtifactTypeClauseImpl();
    return osArtifactTypeClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactTypeEqualsClause createOsArtifactTypeEqualsClause()
  {
    OsArtifactTypeEqualsClauseImpl osArtifactTypeEqualsClause = new OsArtifactTypeEqualsClauseImpl();
    return osArtifactTypeEqualsClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsArtifactTypeInstanceOfClause createOsArtifactTypeInstanceOfClause()
  {
    OsArtifactTypeInstanceOfClauseImpl osArtifactTypeInstanceOfClause = new OsArtifactTypeInstanceOfClauseImpl();
    return osArtifactTypeInstanceOfClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsAttributeCriteria createOsAttributeCriteria()
  {
    OsAttributeCriteriaImpl osAttributeCriteria = new OsAttributeCriteriaImpl();
    return osAttributeCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsAttributeClause createOsAttributeClause()
  {
    OsAttributeClauseImpl osAttributeClause = new OsAttributeClauseImpl();
    return osAttributeClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsAttributeExistClause createOsAttributeExistClause()
  {
    OsAttributeExistClauseImpl osAttributeExistClause = new OsAttributeExistClauseImpl();
    return osAttributeExistClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseAttributeOpClause createOseAttributeOpClause()
  {
    OseAttributeOpClauseImpl oseAttributeOpClause = new OseAttributeOpClauseImpl();
    return oseAttributeOpClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsRelationCriteria createOsRelationCriteria()
  {
    OsRelationCriteriaImpl osRelationCriteria = new OsRelationCriteriaImpl();
    return osRelationCriteria;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsRelationClause createOsRelationClause()
  {
    OsRelationClauseImpl osRelationClause = new OsRelationClauseImpl();
    return osRelationClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsRelationExistClause createOsRelationExistClause()
  {
    OsRelationExistClauseImpl osRelationExistClause = new OsRelationExistClauseImpl();
    return osRelationExistClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsRelatedToClause createOsRelatedToClause()
  {
    OsRelatedToClauseImpl osRelatedToClause = new OsRelatedToClauseImpl();
    return osRelatedToClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsFollowClause createOsFollowClause()
  {
    OsFollowClauseImpl osFollowClause = new OsFollowClauseImpl();
    return osFollowClause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsFollowStatement createOsFollowStatement()
  {
    OsFollowStatementImpl osFollowStatement = new OsFollowStatementImpl();
    return osFollowStatement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsFollowRelationType createOsFollowRelationType()
  {
    OsFollowRelationTypeImpl osFollowRelationType = new OsFollowRelationTypeImpl();
    return osFollowRelationType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsVariableDeclaration createOsVariableDeclaration()
  {
    OsVariableDeclarationImpl osVariableDeclaration = new OsVariableDeclarationImpl();
    return osVariableDeclaration;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsVariable createOsVariable()
  {
    OsVariableImpl osVariable = new OsVariableImpl();
    return osVariable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsAssignment createOsAssignment()
  {
    OsAssignmentImpl osAssignment = new OsAssignmentImpl();
    return osAssignment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBooleanLiteral createOsBooleanLiteral()
  {
    OsBooleanLiteralImpl osBooleanLiteral = new OsBooleanLiteralImpl();
    return osBooleanLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsNullLiteral createOsNullLiteral()
  {
    OsNullLiteralImpl osNullLiteral = new OsNullLiteralImpl();
    return osNullLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsNumberLiteral createOsNumberLiteral()
  {
    OsNumberLiteralImpl osNumberLiteral = new OsNumberLiteralImpl();
    return osNumberLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsStringLiteral createOsStringLiteral()
  {
    OsStringLiteralImpl osStringLiteral = new OsStringLiteralImpl();
    return osStringLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTemplateLiteral createOsTemplateLiteral()
  {
    OsTemplateLiteralImpl osTemplateLiteral = new OsTemplateLiteralImpl();
    return osTemplateLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsVariableReference createOsVariableReference()
  {
    OsVariableReferenceImpl osVariableReference = new OsVariableReferenceImpl();
    return osVariableReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsDotExpression createOsDotExpression()
  {
    OsDotExpressionImpl osDotExpression = new OsDotExpressionImpl();
    return osDotExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsQueryExpression createOsQueryExpression()
  {
    OsQueryExpressionImpl osQueryExpression = new OsQueryExpressionImpl();
    return osQueryExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsCollectObjectExpression createOsCollectObjectExpression()
  {
    OsCollectObjectExpressionImpl osCollectObjectExpression = new OsCollectObjectExpressionImpl();
    return osCollectObjectExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsCollectAllFieldsExpression createOsCollectAllFieldsExpression()
  {
    OsCollectAllFieldsExpressionImpl osCollectAllFieldsExpression = new OsCollectAllFieldsExpressionImpl();
    return osCollectAllFieldsExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsCollectFieldExpression createOsCollectFieldExpression()
  {
    OsCollectFieldExpressionImpl osCollectFieldExpression = new OsCollectFieldExpressionImpl();
    return osCollectFieldExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchState createOsBranchStateFromString(EDataType eDataType, String initialValue)
  {
    OsBranchState result = OsBranchState.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOsBranchStateToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchType createOsBranchTypeFromString(EDataType eDataType, String initialValue)
  {
    OsBranchType result = OsBranchType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOsBranchTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsBranchArchiveFilter createOsBranchArchiveFilterFromString(EDataType eDataType, String initialValue)
  {
    OsBranchArchiveFilter result = OsBranchArchiveFilter.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOsBranchArchiveFilterToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsTxType createOsTxTypeFromString(EDataType eDataType, String initialValue)
  {
    OsTxType result = OsTxType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOsTxTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsRelationSide createOsRelationSideFromString(EDataType eDataType, String initialValue)
  {
    OsRelationSide result = OsRelationSide.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOsRelationSideToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsOperator createOsOperatorFromString(EDataType eDataType, String initialValue)
  {
    OsOperator result = OsOperator.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOsOperatorToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsNonEqualOperator createOsNonEqualOperatorFromString(EDataType eDataType, String initialValue)
  {
    OsNonEqualOperator result = OsNonEqualOperator.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOsNonEqualOperatorToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsExistenceOperator createOsExistenceOperatorFromString(EDataType eDataType, String initialValue)
  {
    OsExistenceOperator result = OsExistenceOperator.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOsExistenceOperatorToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OsQueryOption createOsQueryOptionFromString(EDataType eDataType, String initialValue)
  {
    OsQueryOption result = OsQueryOption.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertOsQueryOptionToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrcsScriptDslPackage getOrcsScriptDslPackage()
  {
    return (OrcsScriptDslPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static OrcsScriptDslPackage getPackage()
  {
    return OrcsScriptDslPackage.eINSTANCE;
  }

} //OrcsScriptDslFactoryImpl
