/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage
 * @generated
 */
public class OrcsScriptDslSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static OrcsScriptDslPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrcsScriptDslSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = OrcsScriptDslPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @parameter ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case OrcsScriptDslPackage.ORCS_SCRIPT:
      {
        OrcsScript orcsScript = (OrcsScript)theEObject;
        T result = caseOrcsScript(orcsScript);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.SCRIPT_STATEMENT:
      {
        ScriptStatement scriptStatement = (ScriptStatement)theEObject;
        T result = caseScriptStatement(scriptStatement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.SCRIPT_VERSION:
      {
        ScriptVersion scriptVersion = (ScriptVersion)theEObject;
        T result = caseScriptVersion(scriptVersion);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_EXPRESSION:
      {
        OsExpression osExpression = (OsExpression)theEObject;
        T result = caseOsExpression(osExpression);
        if (result == null) result = caseScriptStatement(osExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_COLLECTION_LITERAL:
      {
        OsCollectionLiteral osCollectionLiteral = (OsCollectionLiteral)theEObject;
        T result = caseOsCollectionLiteral(osCollectionLiteral);
        if (result == null) result = caseOsExpression(osCollectionLiteral);
        if (result == null) result = caseScriptStatement(osCollectionLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_LIST_LITERAL:
      {
        OsListLiteral osListLiteral = (OsListLiteral)theEObject;
        T result = caseOsListLiteral(osListLiteral);
        if (result == null) result = caseOsCollectionLiteral(osListLiteral);
        if (result == null) result = caseOsExpression(osListLiteral);
        if (result == null) result = caseScriptStatement(osListLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_QUERY_STATEMENT:
      {
        OsQueryStatement osQueryStatement = (OsQueryStatement)theEObject;
        T result = caseOsQueryStatement(osQueryStatement);
        if (result == null) result = caseScriptStatement(osQueryStatement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_QUERY:
      {
        OsQuery osQuery = (OsQuery)theEObject;
        T result = caseOsQuery(osQuery);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_CRITIERIA:
      {
        OsCritieria osCritieria = (OsCritieria)theEObject;
        T result = caseOsCritieria(osCritieria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_CLAUSE:
      {
        OsClause osClause = (OsClause)theEObject;
        T result = caseOsClause(osClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_COLLECT_CLAUSE:
      {
        OsCollectClause osCollectClause = (OsCollectClause)theEObject;
        T result = caseOsCollectClause(osCollectClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_LIMIT_CLAUSE:
      {
        OsLimitClause osLimitClause = (OsLimitClause)theEObject;
        T result = caseOsLimitClause(osLimitClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_COLLECT_EXPRESSION:
      {
        OsCollectExpression osCollectExpression = (OsCollectExpression)theEObject;
        T result = caseOsCollectExpression(osCollectExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_FIND_CLAUSE:
      {
        OsFindClause osFindClause = (OsFindClause)theEObject;
        T result = caseOsFindClause(osFindClause);
        if (result == null) result = caseOsClause(osFindClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_OBJECT_QUERY:
      {
        OsObjectQuery osObjectQuery = (OsObjectQuery)theEObject;
        T result = caseOsObjectQuery(osObjectQuery);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_QUERY_STATEMENT:
      {
        OsBranchQueryStatement osBranchQueryStatement = (OsBranchQueryStatement)theEObject;
        T result = caseOsBranchQueryStatement(osBranchQueryStatement);
        if (result == null) result = caseOsQuery(osBranchQueryStatement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_QUERY:
      {
        OsBranchQuery osBranchQuery = (OsBranchQuery)theEObject;
        T result = caseOsBranchQuery(osBranchQuery);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_QUERY_BY_ID:
      {
        OsBranchQueryById osBranchQueryById = (OsBranchQueryById)theEObject;
        T result = caseOsBranchQueryById(osBranchQueryById);
        if (result == null) result = caseOsBranchQuery(osBranchQueryById);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_QUERY_ALL:
      {
        OsBranchQueryAll osBranchQueryAll = (OsBranchQueryAll)theEObject;
        T result = caseOsBranchQueryAll(osBranchQueryAll);
        if (result == null) result = caseOsBranchQuery(osBranchQueryAll);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_QUERY_BY_PREDICATE:
      {
        OsBranchQueryByPredicate osBranchQueryByPredicate = (OsBranchQueryByPredicate)theEObject;
        T result = caseOsBranchQueryByPredicate(osBranchQueryByPredicate);
        if (result == null) result = caseOsBranchQuery(osBranchQueryByPredicate);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_CRITERIA:
      {
        OsBranchCriteria osBranchCriteria = (OsBranchCriteria)theEObject;
        T result = caseOsBranchCriteria(osBranchCriteria);
        if (result == null) result = caseOsCritieria(osBranchCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_NAME_CRITERIA:
      {
        OsBranchNameCriteria osBranchNameCriteria = (OsBranchNameCriteria)theEObject;
        T result = caseOsBranchNameCriteria(osBranchNameCriteria);
        if (result == null) result = caseOsBranchCriteria(osBranchNameCriteria);
        if (result == null) result = caseOsCritieria(osBranchNameCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_NAME_CLAUSE:
      {
        OsBranchNameClause osBranchNameClause = (OsBranchNameClause)theEObject;
        T result = caseOsBranchNameClause(osBranchNameClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_NAME_EQUALS_CLAUSE:
      {
        OsBranchNameEqualsClause osBranchNameEqualsClause = (OsBranchNameEqualsClause)theEObject;
        T result = caseOsBranchNameEqualsClause(osBranchNameEqualsClause);
        if (result == null) result = caseOsBranchNameClause(osBranchNameEqualsClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_NAME_PATTERN_CLAUSE:
      {
        OsBranchNamePatternClause osBranchNamePatternClause = (OsBranchNamePatternClause)theEObject;
        T result = caseOsBranchNamePatternClause(osBranchNamePatternClause);
        if (result == null) result = caseOsBranchNameClause(osBranchNamePatternClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_TYPE_CRITERIA:
      {
        OsBranchTypeCriteria osBranchTypeCriteria = (OsBranchTypeCriteria)theEObject;
        T result = caseOsBranchTypeCriteria(osBranchTypeCriteria);
        if (result == null) result = caseOsBranchCriteria(osBranchTypeCriteria);
        if (result == null) result = caseOsCritieria(osBranchTypeCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_STATE_CRITERIA:
      {
        OsBranchStateCriteria osBranchStateCriteria = (OsBranchStateCriteria)theEObject;
        T result = caseOsBranchStateCriteria(osBranchStateCriteria);
        if (result == null) result = caseOsBranchCriteria(osBranchStateCriteria);
        if (result == null) result = caseOsCritieria(osBranchStateCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_ARCHIVED_CRITERIA:
      {
        OsBranchArchivedCriteria osBranchArchivedCriteria = (OsBranchArchivedCriteria)theEObject;
        T result = caseOsBranchArchivedCriteria(osBranchArchivedCriteria);
        if (result == null) result = caseOsBranchCriteria(osBranchArchivedCriteria);
        if (result == null) result = caseOsCritieria(osBranchArchivedCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_ID_CRITERIA:
      {
        OsBranchIdCriteria osBranchIdCriteria = (OsBranchIdCriteria)theEObject;
        T result = caseOsBranchIdCriteria(osBranchIdCriteria);
        if (result == null) result = caseOsBranchCriteria(osBranchIdCriteria);
        if (result == null) result = caseOsCritieria(osBranchIdCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_OF_CRITERIA:
      {
        OsBranchOfCriteria osBranchOfCriteria = (OsBranchOfCriteria)theEObject;
        T result = caseOsBranchOfCriteria(osBranchOfCriteria);
        if (result == null) result = caseOsBranchCriteria(osBranchOfCriteria);
        if (result == null) result = caseOsCritieria(osBranchOfCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_CLAUSE:
      {
        OsBranchClause osBranchClause = (OsBranchClause)theEObject;
        T result = caseOsBranchClause(osBranchClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_CHILD_OF_CLAUSE:
      {
        OsBranchChildOfClause osBranchChildOfClause = (OsBranchChildOfClause)theEObject;
        T result = caseOsBranchChildOfClause(osBranchChildOfClause);
        if (result == null) result = caseOsBranchClause(osBranchChildOfClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BRANCH_PARENT_OF_CLAUSE:
      {
        OsBranchParentOfClause osBranchParentOfClause = (OsBranchParentOfClause)theEObject;
        T result = caseOsBranchParentOfClause(osBranchParentOfClause);
        if (result == null) result = caseOsBranchClause(osBranchParentOfClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_QUERY_STATEMENT:
      {
        OsTxQueryStatement osTxQueryStatement = (OsTxQueryStatement)theEObject;
        T result = caseOsTxQueryStatement(osTxQueryStatement);
        if (result == null) result = caseOsQuery(osTxQueryStatement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_QUERY:
      {
        OsTxQuery osTxQuery = (OsTxQuery)theEObject;
        T result = caseOsTxQuery(osTxQuery);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_QUERY_BY_ID:
      {
        OsTxQueryById osTxQueryById = (OsTxQueryById)theEObject;
        T result = caseOsTxQueryById(osTxQueryById);
        if (result == null) result = caseOsTxQuery(osTxQueryById);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_QUERY_ALL:
      {
        OsTxQueryAll osTxQueryAll = (OsTxQueryAll)theEObject;
        T result = caseOsTxQueryAll(osTxQueryAll);
        if (result == null) result = caseOsTxQuery(osTxQueryAll);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_QUERY_BY_PREDICATE:
      {
        OsTxQueryByPredicate osTxQueryByPredicate = (OsTxQueryByPredicate)theEObject;
        T result = caseOsTxQueryByPredicate(osTxQueryByPredicate);
        if (result == null) result = caseOsTxQuery(osTxQueryByPredicate);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_CRITERIA:
      {
        OsTxCriteria osTxCriteria = (OsTxCriteria)theEObject;
        T result = caseOsTxCriteria(osTxCriteria);
        if (result == null) result = caseOsCritieria(osTxCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_TYPE_CRITERIA:
      {
        OsTxTypeCriteria osTxTypeCriteria = (OsTxTypeCriteria)theEObject;
        T result = caseOsTxTypeCriteria(osTxTypeCriteria);
        if (result == null) result = caseOsTxCriteria(osTxTypeCriteria);
        if (result == null) result = caseOsCritieria(osTxTypeCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_COMMENT_CRITERIA:
      {
        OsTxCommentCriteria osTxCommentCriteria = (OsTxCommentCriteria)theEObject;
        T result = caseOsTxCommentCriteria(osTxCommentCriteria);
        if (result == null) result = caseOsTxCriteria(osTxCommentCriteria);
        if (result == null) result = caseOsCritieria(osTxCommentCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_COMMENT_CLAUSE:
      {
        OsTxCommentClause osTxCommentClause = (OsTxCommentClause)theEObject;
        T result = caseOsTxCommentClause(osTxCommentClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_COMMENT_EQUALS_CLAUSE:
      {
        OsTxCommentEqualsClause osTxCommentEqualsClause = (OsTxCommentEqualsClause)theEObject;
        T result = caseOsTxCommentEqualsClause(osTxCommentEqualsClause);
        if (result == null) result = caseOsTxCommentClause(osTxCommentEqualsClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_COMMENT_PATTERN_CLAUSE:
      {
        OsTxCommentPatternClause osTxCommentPatternClause = (OsTxCommentPatternClause)theEObject;
        T result = caseOsTxCommentPatternClause(osTxCommentPatternClause);
        if (result == null) result = caseOsTxCommentClause(osTxCommentPatternClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_BRANCH_ID_CRITERIA:
      {
        OsTxBranchIdCriteria osTxBranchIdCriteria = (OsTxBranchIdCriteria)theEObject;
        T result = caseOsTxBranchIdCriteria(osTxBranchIdCriteria);
        if (result == null) result = caseOsTxCriteria(osTxBranchIdCriteria);
        if (result == null) result = caseOsCritieria(osTxBranchIdCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_HEAD_OF_BRANCH_ID_CRITERIA:
      {
        OsTxHeadOfBranchIdCriteria osTxHeadOfBranchIdCriteria = (OsTxHeadOfBranchIdCriteria)theEObject;
        T result = caseOsTxHeadOfBranchIdCriteria(osTxHeadOfBranchIdCriteria);
        if (result == null) result = caseOsTxCriteria(osTxHeadOfBranchIdCriteria);
        if (result == null) result = caseOsCritieria(osTxHeadOfBranchIdCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_AUTHOR_ID_CRITERIA:
      {
        OsTxAuthorIdCriteria osTxAuthorIdCriteria = (OsTxAuthorIdCriteria)theEObject;
        T result = caseOsTxAuthorIdCriteria(osTxAuthorIdCriteria);
        if (result == null) result = caseOsTxCriteria(osTxAuthorIdCriteria);
        if (result == null) result = caseOsCritieria(osTxAuthorIdCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_COMMIT_ID_CRITERIA:
      {
        OsTxCommitIdCriteria osTxCommitIdCriteria = (OsTxCommitIdCriteria)theEObject;
        T result = caseOsTxCommitIdCriteria(osTxCommitIdCriteria);
        if (result == null) result = caseOsTxCriteria(osTxCommitIdCriteria);
        if (result == null) result = caseOsCritieria(osTxCommitIdCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_COMMIT_ID_CLAUSE:
      {
        OsTxCommitIdClause osTxCommitIdClause = (OsTxCommitIdClause)theEObject;
        T result = caseOsTxCommitIdClause(osTxCommitIdClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_COMMIT_ID_EQUALS_CLAUSE:
      {
        OsTxCommitIdEqualsClause osTxCommitIdEqualsClause = (OsTxCommitIdEqualsClause)theEObject;
        T result = caseOsTxCommitIdEqualsClause(osTxCommitIdEqualsClause);
        if (result == null) result = caseOsTxCommitIdClause(osTxCommitIdEqualsClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_COMMIT_ID_IS_NULL_CLAUSE:
      {
        OsTxCommitIdIsNullClause osTxCommitIdIsNullClause = (OsTxCommitIdIsNullClause)theEObject;
        T result = caseOsTxCommitIdIsNullClause(osTxCommitIdIsNullClause);
        if (result == null) result = caseOsTxCommitIdClause(osTxCommitIdIsNullClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_ID_CRITERIA:
      {
        OsTxIdCriteria osTxIdCriteria = (OsTxIdCriteria)theEObject;
        T result = caseOsTxIdCriteria(osTxIdCriteria);
        if (result == null) result = caseOsTxCriteria(osTxIdCriteria);
        if (result == null) result = caseOsCritieria(osTxIdCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_ID_CLAUSE:
      {
        OsTxIdClause osTxIdClause = (OsTxIdClause)theEObject;
        T result = caseOsTxIdClause(osTxIdClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_ID_EQUALS_CLAUSE:
      {
        OsTxIdEqualsClause osTxIdEqualsClause = (OsTxIdEqualsClause)theEObject;
        T result = caseOsTxIdEqualsClause(osTxIdEqualsClause);
        if (result == null) result = caseOsTxIdClause(osTxIdEqualsClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_ID_OP_CLAUSE:
      {
        OsTxIdOpClause osTxIdOpClause = (OsTxIdOpClause)theEObject;
        T result = caseOsTxIdOpClause(osTxIdOpClause);
        if (result == null) result = caseOsTxIdClause(osTxIdOpClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_ID_RANGE_CLAUSE:
      {
        OsTxIdRangeClause osTxIdRangeClause = (OsTxIdRangeClause)theEObject;
        T result = caseOsTxIdRangeClause(osTxIdRangeClause);
        if (result == null) result = caseOsTxIdClause(osTxIdRangeClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_TIMESTAMP_CRITERIA:
      {
        OsTxTimestampCriteria osTxTimestampCriteria = (OsTxTimestampCriteria)theEObject;
        T result = caseOsTxTimestampCriteria(osTxTimestampCriteria);
        if (result == null) result = caseOsTxCriteria(osTxTimestampCriteria);
        if (result == null) result = caseOsCritieria(osTxTimestampCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_TIMESTAMP_CLAUSE:
      {
        OsTxTimestampClause osTxTimestampClause = (OsTxTimestampClause)theEObject;
        T result = caseOsTxTimestampClause(osTxTimestampClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_TIMESTAMP_OP_CLAUSE:
      {
        OsTxTimestampOpClause osTxTimestampOpClause = (OsTxTimestampOpClause)theEObject;
        T result = caseOsTxTimestampOpClause(osTxTimestampOpClause);
        if (result == null) result = caseOsTxTimestampClause(osTxTimestampOpClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TX_TIMESTAMP_RANGE_CLAUSE:
      {
        OsTxTimestampRangeClause osTxTimestampRangeClause = (OsTxTimestampRangeClause)theEObject;
        T result = caseOsTxTimestampRangeClause(osTxTimestampRangeClause);
        if (result == null) result = caseOsTxTimestampClause(osTxTimestampRangeClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_STATEMENT:
      {
        OsArtifactQueryStatement osArtifactQueryStatement = (OsArtifactQueryStatement)theEObject;
        T result = caseOsArtifactQueryStatement(osArtifactQueryStatement);
        if (result == null) result = caseOsObjectQuery(osArtifactQueryStatement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY:
      {
        OsArtifactQuery osArtifactQuery = (OsArtifactQuery)theEObject;
        T result = caseOsArtifactQuery(osArtifactQuery);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_ALL:
      {
        OsArtifactQueryAll osArtifactQueryAll = (OsArtifactQueryAll)theEObject;
        T result = caseOsArtifactQueryAll(osArtifactQueryAll);
        if (result == null) result = caseOsArtifactQuery(osArtifactQueryAll);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_QUERY_BY_PREDICATE:
      {
        OsArtifactQueryByPredicate osArtifactQueryByPredicate = (OsArtifactQueryByPredicate)theEObject;
        T result = caseOsArtifactQueryByPredicate(osArtifactQueryByPredicate);
        if (result == null) result = caseOsArtifactQuery(osArtifactQueryByPredicate);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ITEM_CRITERIA:
      {
        OsItemCriteria osItemCriteria = (OsItemCriteria)theEObject;
        T result = caseOsItemCriteria(osItemCriteria);
        if (result == null) result = caseOsCritieria(osItemCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_CRITERIA:
      {
        OsArtifactCriteria osArtifactCriteria = (OsArtifactCriteria)theEObject;
        T result = caseOsArtifactCriteria(osArtifactCriteria);
        if (result == null) result = caseOsItemCriteria(osArtifactCriteria);
        if (result == null) result = caseOsCritieria(osArtifactCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_ID_CRITERIA:
      {
        OsArtifactIdCriteria osArtifactIdCriteria = (OsArtifactIdCriteria)theEObject;
        T result = caseOsArtifactIdCriteria(osArtifactIdCriteria);
        if (result == null) result = caseOsArtifactCriteria(osArtifactIdCriteria);
        if (result == null) result = caseOsItemCriteria(osArtifactIdCriteria);
        if (result == null) result = caseOsCritieria(osArtifactIdCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_GUID_CRITERIA:
      {
        OsArtifactGuidCriteria osArtifactGuidCriteria = (OsArtifactGuidCriteria)theEObject;
        T result = caseOsArtifactGuidCriteria(osArtifactGuidCriteria);
        if (result == null) result = caseOsArtifactCriteria(osArtifactGuidCriteria);
        if (result == null) result = caseOsItemCriteria(osArtifactGuidCriteria);
        if (result == null) result = caseOsCritieria(osArtifactGuidCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_CRITERIA:
      {
        OsArtifactTypeCriteria osArtifactTypeCriteria = (OsArtifactTypeCriteria)theEObject;
        T result = caseOsArtifactTypeCriteria(osArtifactTypeCriteria);
        if (result == null) result = caseOsArtifactCriteria(osArtifactTypeCriteria);
        if (result == null) result = caseOsItemCriteria(osArtifactTypeCriteria);
        if (result == null) result = caseOsCritieria(osArtifactTypeCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_CLAUSE:
      {
        OsArtifactTypeClause osArtifactTypeClause = (OsArtifactTypeClause)theEObject;
        T result = caseOsArtifactTypeClause(osArtifactTypeClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_EQUALS_CLAUSE:
      {
        OsArtifactTypeEqualsClause osArtifactTypeEqualsClause = (OsArtifactTypeEqualsClause)theEObject;
        T result = caseOsArtifactTypeEqualsClause(osArtifactTypeEqualsClause);
        if (result == null) result = caseOsArtifactTypeClause(osArtifactTypeEqualsClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ARTIFACT_TYPE_INSTANCE_OF_CLAUSE:
      {
        OsArtifactTypeInstanceOfClause osArtifactTypeInstanceOfClause = (OsArtifactTypeInstanceOfClause)theEObject;
        T result = caseOsArtifactTypeInstanceOfClause(osArtifactTypeInstanceOfClause);
        if (result == null) result = caseOsArtifactTypeClause(osArtifactTypeInstanceOfClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ATTRIBUTE_CRITERIA:
      {
        OsAttributeCriteria osAttributeCriteria = (OsAttributeCriteria)theEObject;
        T result = caseOsAttributeCriteria(osAttributeCriteria);
        if (result == null) result = caseOsItemCriteria(osAttributeCriteria);
        if (result == null) result = caseOsCritieria(osAttributeCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ATTRIBUTE_CLAUSE:
      {
        OsAttributeClause osAttributeClause = (OsAttributeClause)theEObject;
        T result = caseOsAttributeClause(osAttributeClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ATTRIBUTE_EXIST_CLAUSE:
      {
        OsAttributeExistClause osAttributeExistClause = (OsAttributeExistClause)theEObject;
        T result = caseOsAttributeExistClause(osAttributeExistClause);
        if (result == null) result = caseOsAttributeClause(osAttributeExistClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OSE_ATTRIBUTE_OP_CLAUSE:
      {
        OseAttributeOpClause oseAttributeOpClause = (OseAttributeOpClause)theEObject;
        T result = caseOseAttributeOpClause(oseAttributeOpClause);
        if (result == null) result = caseOsAttributeClause(oseAttributeOpClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_RELATION_CRITERIA:
      {
        OsRelationCriteria osRelationCriteria = (OsRelationCriteria)theEObject;
        T result = caseOsRelationCriteria(osRelationCriteria);
        if (result == null) result = caseOsItemCriteria(osRelationCriteria);
        if (result == null) result = caseOsCritieria(osRelationCriteria);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_RELATION_CLAUSE:
      {
        OsRelationClause osRelationClause = (OsRelationClause)theEObject;
        T result = caseOsRelationClause(osRelationClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_RELATION_EXIST_CLAUSE:
      {
        OsRelationExistClause osRelationExistClause = (OsRelationExistClause)theEObject;
        T result = caseOsRelationExistClause(osRelationExistClause);
        if (result == null) result = caseOsRelationClause(osRelationExistClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_RELATED_TO_CLAUSE:
      {
        OsRelatedToClause osRelatedToClause = (OsRelatedToClause)theEObject;
        T result = caseOsRelatedToClause(osRelatedToClause);
        if (result == null) result = caseOsRelationClause(osRelatedToClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_FOLLOW_CLAUSE:
      {
        OsFollowClause osFollowClause = (OsFollowClause)theEObject;
        T result = caseOsFollowClause(osFollowClause);
        if (result == null) result = caseOsClause(osFollowClause);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_FOLLOW_STATEMENT:
      {
        OsFollowStatement osFollowStatement = (OsFollowStatement)theEObject;
        T result = caseOsFollowStatement(osFollowStatement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_FOLLOW_RELATION_TYPE:
      {
        OsFollowRelationType osFollowRelationType = (OsFollowRelationType)theEObject;
        T result = caseOsFollowRelationType(osFollowRelationType);
        if (result == null) result = caseOsFollowStatement(osFollowRelationType);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_VARIABLE_DECLARATION:
      {
        OsVariableDeclaration osVariableDeclaration = (OsVariableDeclaration)theEObject;
        T result = caseOsVariableDeclaration(osVariableDeclaration);
        if (result == null) result = caseOsExpression(osVariableDeclaration);
        if (result == null) result = caseScriptStatement(osVariableDeclaration);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_VARIABLE:
      {
        OsVariable osVariable = (OsVariable)theEObject;
        T result = caseOsVariable(osVariable);
        if (result == null) result = caseOsExpression(osVariable);
        if (result == null) result = caseScriptStatement(osVariable);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_ASSIGNMENT:
      {
        OsAssignment osAssignment = (OsAssignment)theEObject;
        T result = caseOsAssignment(osAssignment);
        if (result == null) result = caseOsExpression(osAssignment);
        if (result == null) result = caseScriptStatement(osAssignment);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_BOOLEAN_LITERAL:
      {
        OsBooleanLiteral osBooleanLiteral = (OsBooleanLiteral)theEObject;
        T result = caseOsBooleanLiteral(osBooleanLiteral);
        if (result == null) result = caseOsExpression(osBooleanLiteral);
        if (result == null) result = caseScriptStatement(osBooleanLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_NULL_LITERAL:
      {
        OsNullLiteral osNullLiteral = (OsNullLiteral)theEObject;
        T result = caseOsNullLiteral(osNullLiteral);
        if (result == null) result = caseOsExpression(osNullLiteral);
        if (result == null) result = caseScriptStatement(osNullLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_NUMBER_LITERAL:
      {
        OsNumberLiteral osNumberLiteral = (OsNumberLiteral)theEObject;
        T result = caseOsNumberLiteral(osNumberLiteral);
        if (result == null) result = caseOsExpression(osNumberLiteral);
        if (result == null) result = caseScriptStatement(osNumberLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_STRING_LITERAL:
      {
        OsStringLiteral osStringLiteral = (OsStringLiteral)theEObject;
        T result = caseOsStringLiteral(osStringLiteral);
        if (result == null) result = caseOsExpression(osStringLiteral);
        if (result == null) result = caseScriptStatement(osStringLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_TEMPLATE_LITERAL:
      {
        OsTemplateLiteral osTemplateLiteral = (OsTemplateLiteral)theEObject;
        T result = caseOsTemplateLiteral(osTemplateLiteral);
        if (result == null) result = caseOsExpression(osTemplateLiteral);
        if (result == null) result = caseScriptStatement(osTemplateLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_VARIABLE_REFERENCE:
      {
        OsVariableReference osVariableReference = (OsVariableReference)theEObject;
        T result = caseOsVariableReference(osVariableReference);
        if (result == null) result = caseOsExpression(osVariableReference);
        if (result == null) result = caseScriptStatement(osVariableReference);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_DOT_EXPRESSION:
      {
        OsDotExpression osDotExpression = (OsDotExpression)theEObject;
        T result = caseOsDotExpression(osDotExpression);
        if (result == null) result = caseOsExpression(osDotExpression);
        if (result == null) result = caseScriptStatement(osDotExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_QUERY_EXPRESSION:
      {
        OsQueryExpression osQueryExpression = (OsQueryExpression)theEObject;
        T result = caseOsQueryExpression(osQueryExpression);
        if (result == null) result = caseOsExpression(osQueryExpression);
        if (result == null) result = caseScriptStatement(osQueryExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_COLLECT_OBJECT_EXPRESSION:
      {
        OsCollectObjectExpression osCollectObjectExpression = (OsCollectObjectExpression)theEObject;
        T result = caseOsCollectObjectExpression(osCollectObjectExpression);
        if (result == null) result = caseOsCollectExpression(osCollectObjectExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_COLLECT_ALL_FIELDS_EXPRESSION:
      {
        OsCollectAllFieldsExpression osCollectAllFieldsExpression = (OsCollectAllFieldsExpression)theEObject;
        T result = caseOsCollectAllFieldsExpression(osCollectAllFieldsExpression);
        if (result == null) result = caseOsCollectExpression(osCollectAllFieldsExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case OrcsScriptDslPackage.OS_COLLECT_FIELD_EXPRESSION:
      {
        OsCollectFieldExpression osCollectFieldExpression = (OsCollectFieldExpression)theEObject;
        T result = caseOsCollectFieldExpression(osCollectFieldExpression);
        if (result == null) result = caseOsCollectExpression(osCollectFieldExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Orcs Script</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Orcs Script</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOrcsScript(OrcsScript object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Script Statement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Script Statement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseScriptStatement(ScriptStatement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Script Version</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Script Version</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseScriptVersion(ScriptVersion object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsExpression(OsExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Collection Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Collection Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsCollectionLiteral(OsCollectionLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os List Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os List Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsListLiteral(OsListLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Query Statement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Query Statement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsQueryStatement(OsQueryStatement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Query</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Query</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsQuery(OsQuery object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Critieria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Critieria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsCritieria(OsCritieria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsClause(OsClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Collect Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Collect Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsCollectClause(OsCollectClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Limit Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Limit Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsLimitClause(OsLimitClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Collect Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Collect Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsCollectExpression(OsCollectExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Find Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Find Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsFindClause(OsFindClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Object Query</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Object Query</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsObjectQuery(OsObjectQuery object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Query Statement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Query Statement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchQueryStatement(OsBranchQueryStatement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Query</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Query</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchQuery(OsBranchQuery object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Query By Id</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Query By Id</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchQueryById(OsBranchQueryById object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Query All</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Query All</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchQueryAll(OsBranchQueryAll object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Query By Predicate</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Query By Predicate</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchQueryByPredicate(OsBranchQueryByPredicate object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchCriteria(OsBranchCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Name Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Name Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchNameCriteria(OsBranchNameCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Name Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Name Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchNameClause(OsBranchNameClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Name Equals Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Name Equals Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchNameEqualsClause(OsBranchNameEqualsClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Name Pattern Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Name Pattern Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchNamePatternClause(OsBranchNamePatternClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Type Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Type Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchTypeCriteria(OsBranchTypeCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch State Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch State Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchStateCriteria(OsBranchStateCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Archived Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Archived Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchArchivedCriteria(OsBranchArchivedCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Id Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Id Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchIdCriteria(OsBranchIdCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Of Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Of Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchOfCriteria(OsBranchOfCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchClause(OsBranchClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Child Of Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Child Of Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchChildOfClause(OsBranchChildOfClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Branch Parent Of Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Branch Parent Of Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBranchParentOfClause(OsBranchParentOfClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Query Statement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Query Statement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxQueryStatement(OsTxQueryStatement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Query</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Query</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxQuery(OsTxQuery object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Query By Id</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Query By Id</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxQueryById(OsTxQueryById object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Query All</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Query All</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxQueryAll(OsTxQueryAll object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Query By Predicate</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Query By Predicate</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxQueryByPredicate(OsTxQueryByPredicate object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxCriteria(OsTxCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Type Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Type Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxTypeCriteria(OsTxTypeCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Comment Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Comment Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxCommentCriteria(OsTxCommentCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Comment Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Comment Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxCommentClause(OsTxCommentClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Comment Equals Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Comment Equals Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxCommentEqualsClause(OsTxCommentEqualsClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Comment Pattern Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Comment Pattern Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxCommentPatternClause(OsTxCommentPatternClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Branch Id Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Branch Id Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxBranchIdCriteria(OsTxBranchIdCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Head Of Branch Id Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Head Of Branch Id Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxHeadOfBranchIdCriteria(OsTxHeadOfBranchIdCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Author Id Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Author Id Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxAuthorIdCriteria(OsTxAuthorIdCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Commit Id Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Commit Id Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxCommitIdCriteria(OsTxCommitIdCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Commit Id Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Commit Id Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxCommitIdClause(OsTxCommitIdClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Commit Id Equals Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Commit Id Equals Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxCommitIdEqualsClause(OsTxCommitIdEqualsClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Commit Id Is Null Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Commit Id Is Null Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxCommitIdIsNullClause(OsTxCommitIdIsNullClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Id Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Id Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxIdCriteria(OsTxIdCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Id Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Id Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxIdClause(OsTxIdClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Id Equals Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Id Equals Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxIdEqualsClause(OsTxIdEqualsClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Id Op Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Id Op Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxIdOpClause(OsTxIdOpClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Id Range Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Id Range Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxIdRangeClause(OsTxIdRangeClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Timestamp Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Timestamp Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxTimestampCriteria(OsTxTimestampCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Timestamp Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Timestamp Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxTimestampClause(OsTxTimestampClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Timestamp Op Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Timestamp Op Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxTimestampOpClause(OsTxTimestampOpClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Tx Timestamp Range Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Tx Timestamp Range Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTxTimestampRangeClause(OsTxTimestampRangeClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Query Statement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Query Statement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactQueryStatement(OsArtifactQueryStatement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Query</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Query</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactQuery(OsArtifactQuery object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Query All</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Query All</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactQueryAll(OsArtifactQueryAll object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Query By Predicate</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Query By Predicate</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactQueryByPredicate(OsArtifactQueryByPredicate object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Item Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Item Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsItemCriteria(OsItemCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactCriteria(OsArtifactCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Id Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Id Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactIdCriteria(OsArtifactIdCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Guid Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Guid Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactGuidCriteria(OsArtifactGuidCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Type Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Type Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactTypeCriteria(OsArtifactTypeCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Type Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Type Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactTypeClause(OsArtifactTypeClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Type Equals Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Type Equals Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactTypeEqualsClause(OsArtifactTypeEqualsClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Artifact Type Instance Of Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Artifact Type Instance Of Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsArtifactTypeInstanceOfClause(OsArtifactTypeInstanceOfClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Attribute Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Attribute Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsAttributeCriteria(OsAttributeCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Attribute Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Attribute Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsAttributeClause(OsAttributeClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Attribute Exist Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Attribute Exist Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsAttributeExistClause(OsAttributeExistClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ose Attribute Op Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ose Attribute Op Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOseAttributeOpClause(OseAttributeOpClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Relation Criteria</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Relation Criteria</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsRelationCriteria(OsRelationCriteria object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Relation Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Relation Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsRelationClause(OsRelationClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Relation Exist Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Relation Exist Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsRelationExistClause(OsRelationExistClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Related To Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Related To Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsRelatedToClause(OsRelatedToClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Follow Clause</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Follow Clause</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsFollowClause(OsFollowClause object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Follow Statement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Follow Statement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsFollowStatement(OsFollowStatement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Follow Relation Type</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Follow Relation Type</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsFollowRelationType(OsFollowRelationType object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Variable Declaration</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Variable Declaration</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsVariableDeclaration(OsVariableDeclaration object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Variable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Variable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsVariable(OsVariable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Assignment</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Assignment</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsAssignment(OsAssignment object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Boolean Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Boolean Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsBooleanLiteral(OsBooleanLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Null Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Null Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsNullLiteral(OsNullLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Number Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Number Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsNumberLiteral(OsNumberLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os String Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os String Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsStringLiteral(OsStringLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Template Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Template Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsTemplateLiteral(OsTemplateLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Variable Reference</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Variable Reference</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsVariableReference(OsVariableReference object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Dot Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Dot Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsDotExpression(OsDotExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Query Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Query Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsQueryExpression(OsQueryExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Collect Object Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Collect Object Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsCollectObjectExpression(OsCollectObjectExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Collect All Fields Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Collect All Fields Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsCollectAllFieldsExpression(OsCollectAllFieldsExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Os Collect Field Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Os Collect Field Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOsCollectFieldExpression(OsCollectFieldExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} //OrcsScriptDslSwitch
