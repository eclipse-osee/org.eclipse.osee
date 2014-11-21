/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage
 * @generated
 */
public class OrcsScriptDslAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static OrcsScriptDslPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrcsScriptDslAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = OrcsScriptDslPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OrcsScriptDslSwitch<Adapter> modelSwitch =
    new OrcsScriptDslSwitch<Adapter>()
    {
      @Override
      public Adapter caseOrcsScript(OrcsScript object)
      {
        return createOrcsScriptAdapter();
      }
      @Override
      public Adapter caseScriptStatement(ScriptStatement object)
      {
        return createScriptStatementAdapter();
      }
      @Override
      public Adapter caseScriptVersion(ScriptVersion object)
      {
        return createScriptVersionAdapter();
      }
      @Override
      public Adapter caseOsExpression(OsExpression object)
      {
        return createOsExpressionAdapter();
      }
      @Override
      public Adapter caseOsCollectionLiteral(OsCollectionLiteral object)
      {
        return createOsCollectionLiteralAdapter();
      }
      @Override
      public Adapter caseOsListLiteral(OsListLiteral object)
      {
        return createOsListLiteralAdapter();
      }
      @Override
      public Adapter caseOsQueryStatement(OsQueryStatement object)
      {
        return createOsQueryStatementAdapter();
      }
      @Override
      public Adapter caseOsQuery(OsQuery object)
      {
        return createOsQueryAdapter();
      }
      @Override
      public Adapter caseOsCritieria(OsCritieria object)
      {
        return createOsCritieriaAdapter();
      }
      @Override
      public Adapter caseOsClause(OsClause object)
      {
        return createOsClauseAdapter();
      }
      @Override
      public Adapter caseOsCollectClause(OsCollectClause object)
      {
        return createOsCollectClauseAdapter();
      }
      @Override
      public Adapter caseOsLimitClause(OsLimitClause object)
      {
        return createOsLimitClauseAdapter();
      }
      @Override
      public Adapter caseOsCollectExpression(OsCollectExpression object)
      {
        return createOsCollectExpressionAdapter();
      }
      @Override
      public Adapter caseOsFindClause(OsFindClause object)
      {
        return createOsFindClauseAdapter();
      }
      @Override
      public Adapter caseOsObjectQuery(OsObjectQuery object)
      {
        return createOsObjectQueryAdapter();
      }
      @Override
      public Adapter caseOsBranchQueryStatement(OsBranchQueryStatement object)
      {
        return createOsBranchQueryStatementAdapter();
      }
      @Override
      public Adapter caseOsBranchQuery(OsBranchQuery object)
      {
        return createOsBranchQueryAdapter();
      }
      @Override
      public Adapter caseOsBranchQueryById(OsBranchQueryById object)
      {
        return createOsBranchQueryByIdAdapter();
      }
      @Override
      public Adapter caseOsBranchQueryAll(OsBranchQueryAll object)
      {
        return createOsBranchQueryAllAdapter();
      }
      @Override
      public Adapter caseOsBranchQueryByPredicate(OsBranchQueryByPredicate object)
      {
        return createOsBranchQueryByPredicateAdapter();
      }
      @Override
      public Adapter caseOsBranchCriteria(OsBranchCriteria object)
      {
        return createOsBranchCriteriaAdapter();
      }
      @Override
      public Adapter caseOsBranchNameCriteria(OsBranchNameCriteria object)
      {
        return createOsBranchNameCriteriaAdapter();
      }
      @Override
      public Adapter caseOsBranchNameClause(OsBranchNameClause object)
      {
        return createOsBranchNameClauseAdapter();
      }
      @Override
      public Adapter caseOsBranchNameEqualsClause(OsBranchNameEqualsClause object)
      {
        return createOsBranchNameEqualsClauseAdapter();
      }
      @Override
      public Adapter caseOsBranchNamePatternClause(OsBranchNamePatternClause object)
      {
        return createOsBranchNamePatternClauseAdapter();
      }
      @Override
      public Adapter caseOsBranchTypeCriteria(OsBranchTypeCriteria object)
      {
        return createOsBranchTypeCriteriaAdapter();
      }
      @Override
      public Adapter caseOsBranchStateCriteria(OsBranchStateCriteria object)
      {
        return createOsBranchStateCriteriaAdapter();
      }
      @Override
      public Adapter caseOsBranchArchivedCriteria(OsBranchArchivedCriteria object)
      {
        return createOsBranchArchivedCriteriaAdapter();
      }
      @Override
      public Adapter caseOsBranchIdCriteria(OsBranchIdCriteria object)
      {
        return createOsBranchIdCriteriaAdapter();
      }
      @Override
      public Adapter caseOsBranchOfCriteria(OsBranchOfCriteria object)
      {
        return createOsBranchOfCriteriaAdapter();
      }
      @Override
      public Adapter caseOsBranchClause(OsBranchClause object)
      {
        return createOsBranchClauseAdapter();
      }
      @Override
      public Adapter caseOsBranchChildOfClause(OsBranchChildOfClause object)
      {
        return createOsBranchChildOfClauseAdapter();
      }
      @Override
      public Adapter caseOsBranchParentOfClause(OsBranchParentOfClause object)
      {
        return createOsBranchParentOfClauseAdapter();
      }
      @Override
      public Adapter caseOsTxQueryStatement(OsTxQueryStatement object)
      {
        return createOsTxQueryStatementAdapter();
      }
      @Override
      public Adapter caseOsTxQuery(OsTxQuery object)
      {
        return createOsTxQueryAdapter();
      }
      @Override
      public Adapter caseOsTxQueryById(OsTxQueryById object)
      {
        return createOsTxQueryByIdAdapter();
      }
      @Override
      public Adapter caseOsTxQueryAll(OsTxQueryAll object)
      {
        return createOsTxQueryAllAdapter();
      }
      @Override
      public Adapter caseOsTxQueryByPredicate(OsTxQueryByPredicate object)
      {
        return createOsTxQueryByPredicateAdapter();
      }
      @Override
      public Adapter caseOsTxCriteria(OsTxCriteria object)
      {
        return createOsTxCriteriaAdapter();
      }
      @Override
      public Adapter caseOsTxTypeCriteria(OsTxTypeCriteria object)
      {
        return createOsTxTypeCriteriaAdapter();
      }
      @Override
      public Adapter caseOsTxCommentCriteria(OsTxCommentCriteria object)
      {
        return createOsTxCommentCriteriaAdapter();
      }
      @Override
      public Adapter caseOsTxCommentClause(OsTxCommentClause object)
      {
        return createOsTxCommentClauseAdapter();
      }
      @Override
      public Adapter caseOsTxCommentEqualsClause(OsTxCommentEqualsClause object)
      {
        return createOsTxCommentEqualsClauseAdapter();
      }
      @Override
      public Adapter caseOsTxCommentPatternClause(OsTxCommentPatternClause object)
      {
        return createOsTxCommentPatternClauseAdapter();
      }
      @Override
      public Adapter caseOsTxBranchIdCriteria(OsTxBranchIdCriteria object)
      {
        return createOsTxBranchIdCriteriaAdapter();
      }
      @Override
      public Adapter caseOsTxHeadOfBranchIdCriteria(OsTxHeadOfBranchIdCriteria object)
      {
        return createOsTxHeadOfBranchIdCriteriaAdapter();
      }
      @Override
      public Adapter caseOsTxAuthorIdCriteria(OsTxAuthorIdCriteria object)
      {
        return createOsTxAuthorIdCriteriaAdapter();
      }
      @Override
      public Adapter caseOsTxCommitIdCriteria(OsTxCommitIdCriteria object)
      {
        return createOsTxCommitIdCriteriaAdapter();
      }
      @Override
      public Adapter caseOsTxCommitIdClause(OsTxCommitIdClause object)
      {
        return createOsTxCommitIdClauseAdapter();
      }
      @Override
      public Adapter caseOsTxCommitIdEqualsClause(OsTxCommitIdEqualsClause object)
      {
        return createOsTxCommitIdEqualsClauseAdapter();
      }
      @Override
      public Adapter caseOsTxCommitIdIsNullClause(OsTxCommitIdIsNullClause object)
      {
        return createOsTxCommitIdIsNullClauseAdapter();
      }
      @Override
      public Adapter caseOsTxIdCriteria(OsTxIdCriteria object)
      {
        return createOsTxIdCriteriaAdapter();
      }
      @Override
      public Adapter caseOsTxIdClause(OsTxIdClause object)
      {
        return createOsTxIdClauseAdapter();
      }
      @Override
      public Adapter caseOsTxIdEqualsClause(OsTxIdEqualsClause object)
      {
        return createOsTxIdEqualsClauseAdapter();
      }
      @Override
      public Adapter caseOsTxIdOpClause(OsTxIdOpClause object)
      {
        return createOsTxIdOpClauseAdapter();
      }
      @Override
      public Adapter caseOsTxIdRangeClause(OsTxIdRangeClause object)
      {
        return createOsTxIdRangeClauseAdapter();
      }
      @Override
      public Adapter caseOsTxTimestampCriteria(OsTxTimestampCriteria object)
      {
        return createOsTxTimestampCriteriaAdapter();
      }
      @Override
      public Adapter caseOsTxTimestampClause(OsTxTimestampClause object)
      {
        return createOsTxTimestampClauseAdapter();
      }
      @Override
      public Adapter caseOsTxTimestampOpClause(OsTxTimestampOpClause object)
      {
        return createOsTxTimestampOpClauseAdapter();
      }
      @Override
      public Adapter caseOsTxTimestampRangeClause(OsTxTimestampRangeClause object)
      {
        return createOsTxTimestampRangeClauseAdapter();
      }
      @Override
      public Adapter caseOsArtifactQueryStatement(OsArtifactQueryStatement object)
      {
        return createOsArtifactQueryStatementAdapter();
      }
      @Override
      public Adapter caseOsArtifactQuery(OsArtifactQuery object)
      {
        return createOsArtifactQueryAdapter();
      }
      @Override
      public Adapter caseOsArtifactQueryAll(OsArtifactQueryAll object)
      {
        return createOsArtifactQueryAllAdapter();
      }
      @Override
      public Adapter caseOsArtifactQueryByPredicate(OsArtifactQueryByPredicate object)
      {
        return createOsArtifactQueryByPredicateAdapter();
      }
      @Override
      public Adapter caseOsItemCriteria(OsItemCriteria object)
      {
        return createOsItemCriteriaAdapter();
      }
      @Override
      public Adapter caseOsArtifactCriteria(OsArtifactCriteria object)
      {
        return createOsArtifactCriteriaAdapter();
      }
      @Override
      public Adapter caseOsArtifactIdCriteria(OsArtifactIdCriteria object)
      {
        return createOsArtifactIdCriteriaAdapter();
      }
      @Override
      public Adapter caseOsArtifactGuidCriteria(OsArtifactGuidCriteria object)
      {
        return createOsArtifactGuidCriteriaAdapter();
      }
      @Override
      public Adapter caseOsArtifactTypeCriteria(OsArtifactTypeCriteria object)
      {
        return createOsArtifactTypeCriteriaAdapter();
      }
      @Override
      public Adapter caseOsArtifactTypeClause(OsArtifactTypeClause object)
      {
        return createOsArtifactTypeClauseAdapter();
      }
      @Override
      public Adapter caseOsArtifactTypeEqualsClause(OsArtifactTypeEqualsClause object)
      {
        return createOsArtifactTypeEqualsClauseAdapter();
      }
      @Override
      public Adapter caseOsArtifactTypeInstanceOfClause(OsArtifactTypeInstanceOfClause object)
      {
        return createOsArtifactTypeInstanceOfClauseAdapter();
      }
      @Override
      public Adapter caseOsAttributeCriteria(OsAttributeCriteria object)
      {
        return createOsAttributeCriteriaAdapter();
      }
      @Override
      public Adapter caseOsAttributeClause(OsAttributeClause object)
      {
        return createOsAttributeClauseAdapter();
      }
      @Override
      public Adapter caseOsAttributeExistClause(OsAttributeExistClause object)
      {
        return createOsAttributeExistClauseAdapter();
      }
      @Override
      public Adapter caseOseAttributeOpClause(OseAttributeOpClause object)
      {
        return createOseAttributeOpClauseAdapter();
      }
      @Override
      public Adapter caseOsRelationCriteria(OsRelationCriteria object)
      {
        return createOsRelationCriteriaAdapter();
      }
      @Override
      public Adapter caseOsRelationClause(OsRelationClause object)
      {
        return createOsRelationClauseAdapter();
      }
      @Override
      public Adapter caseOsRelationExistClause(OsRelationExistClause object)
      {
        return createOsRelationExistClauseAdapter();
      }
      @Override
      public Adapter caseOsRelatedToClause(OsRelatedToClause object)
      {
        return createOsRelatedToClauseAdapter();
      }
      @Override
      public Adapter caseOsFollowClause(OsFollowClause object)
      {
        return createOsFollowClauseAdapter();
      }
      @Override
      public Adapter caseOsFollowStatement(OsFollowStatement object)
      {
        return createOsFollowStatementAdapter();
      }
      @Override
      public Adapter caseOsFollowRelationType(OsFollowRelationType object)
      {
        return createOsFollowRelationTypeAdapter();
      }
      @Override
      public Adapter caseOsVariableDeclaration(OsVariableDeclaration object)
      {
        return createOsVariableDeclarationAdapter();
      }
      @Override
      public Adapter caseOsVariable(OsVariable object)
      {
        return createOsVariableAdapter();
      }
      @Override
      public Adapter caseOsAssignment(OsAssignment object)
      {
        return createOsAssignmentAdapter();
      }
      @Override
      public Adapter caseOsBooleanLiteral(OsBooleanLiteral object)
      {
        return createOsBooleanLiteralAdapter();
      }
      @Override
      public Adapter caseOsNullLiteral(OsNullLiteral object)
      {
        return createOsNullLiteralAdapter();
      }
      @Override
      public Adapter caseOsNumberLiteral(OsNumberLiteral object)
      {
        return createOsNumberLiteralAdapter();
      }
      @Override
      public Adapter caseOsStringLiteral(OsStringLiteral object)
      {
        return createOsStringLiteralAdapter();
      }
      @Override
      public Adapter caseOsTemplateLiteral(OsTemplateLiteral object)
      {
        return createOsTemplateLiteralAdapter();
      }
      @Override
      public Adapter caseOsVariableReference(OsVariableReference object)
      {
        return createOsVariableReferenceAdapter();
      }
      @Override
      public Adapter caseOsDotExpression(OsDotExpression object)
      {
        return createOsDotExpressionAdapter();
      }
      @Override
      public Adapter caseOsQueryExpression(OsQueryExpression object)
      {
        return createOsQueryExpressionAdapter();
      }
      @Override
      public Adapter caseOsCollectObjectExpression(OsCollectObjectExpression object)
      {
        return createOsCollectObjectExpressionAdapter();
      }
      @Override
      public Adapter caseOsCollectAllFieldsExpression(OsCollectAllFieldsExpression object)
      {
        return createOsCollectAllFieldsExpressionAdapter();
      }
      @Override
      public Adapter caseOsCollectFieldExpression(OsCollectFieldExpression object)
      {
        return createOsCollectFieldExpressionAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript <em>Orcs Script</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript
   * @generated
   */
  public Adapter createOrcsScriptAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptStatement <em>Script Statement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptStatement
   * @generated
   */
  public Adapter createScriptStatementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion <em>Script Version</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion
   * @generated
   */
  public Adapter createScriptVersionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression <em>Os Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression
   * @generated
   */
  public Adapter createOsExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectionLiteral <em>Os Collection Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectionLiteral
   * @generated
   */
  public Adapter createOsCollectionLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsListLiteral <em>Os List Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsListLiteral
   * @generated
   */
  public Adapter createOsListLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryStatement <em>Os Query Statement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryStatement
   * @generated
   */
  public Adapter createOsQueryStatementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery <em>Os Query</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery
   * @generated
   */
  public Adapter createOsQueryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCritieria <em>Os Critieria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCritieria
   * @generated
   */
  public Adapter createOsCritieriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsClause <em>Os Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsClause
   * @generated
   */
  public Adapter createOsClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause <em>Os Collect Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause
   * @generated
   */
  public Adapter createOsCollectClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause <em>Os Limit Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause
   * @generated
   */
  public Adapter createOsLimitClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression <em>Os Collect Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression
   * @generated
   */
  public Adapter createOsCollectExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFindClause <em>Os Find Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFindClause
   * @generated
   */
  public Adapter createOsFindClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsObjectQuery <em>Os Object Query</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsObjectQuery
   * @generated
   */
  public Adapter createOsObjectQueryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryStatement <em>Os Branch Query Statement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryStatement
   * @generated
   */
  public Adapter createOsBranchQueryStatementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQuery <em>Os Branch Query</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQuery
   * @generated
   */
  public Adapter createOsBranchQueryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryById <em>Os Branch Query By Id</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryById
   * @generated
   */
  public Adapter createOsBranchQueryByIdAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryAll <em>Os Branch Query All</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryAll
   * @generated
   */
  public Adapter createOsBranchQueryAllAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryByPredicate <em>Os Branch Query By Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryByPredicate
   * @generated
   */
  public Adapter createOsBranchQueryByPredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchCriteria <em>Os Branch Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchCriteria
   * @generated
   */
  public Adapter createOsBranchCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameCriteria <em>Os Branch Name Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameCriteria
   * @generated
   */
  public Adapter createOsBranchNameCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameClause <em>Os Branch Name Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameClause
   * @generated
   */
  public Adapter createOsBranchNameClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameEqualsClause <em>Os Branch Name Equals Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameEqualsClause
   * @generated
   */
  public Adapter createOsBranchNameEqualsClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNamePatternClause <em>Os Branch Name Pattern Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNamePatternClause
   * @generated
   */
  public Adapter createOsBranchNamePatternClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchTypeCriteria <em>Os Branch Type Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchTypeCriteria
   * @generated
   */
  public Adapter createOsBranchTypeCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchStateCriteria <em>Os Branch State Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchStateCriteria
   * @generated
   */
  public Adapter createOsBranchStateCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchivedCriteria <em>Os Branch Archived Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchivedCriteria
   * @generated
   */
  public Adapter createOsBranchArchivedCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchIdCriteria <em>Os Branch Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchIdCriteria
   * @generated
   */
  public Adapter createOsBranchIdCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchOfCriteria <em>Os Branch Of Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchOfCriteria
   * @generated
   */
  public Adapter createOsBranchOfCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchClause <em>Os Branch Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchClause
   * @generated
   */
  public Adapter createOsBranchClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchChildOfClause <em>Os Branch Child Of Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchChildOfClause
   * @generated
   */
  public Adapter createOsBranchChildOfClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchParentOfClause <em>Os Branch Parent Of Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchParentOfClause
   * @generated
   */
  public Adapter createOsBranchParentOfClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement <em>Os Tx Query Statement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement
   * @generated
   */
  public Adapter createOsTxQueryStatementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQuery <em>Os Tx Query</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQuery
   * @generated
   */
  public Adapter createOsTxQueryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryById <em>Os Tx Query By Id</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryById
   * @generated
   */
  public Adapter createOsTxQueryByIdAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryAll <em>Os Tx Query All</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryAll
   * @generated
   */
  public Adapter createOsTxQueryAllAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryByPredicate <em>Os Tx Query By Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryByPredicate
   * @generated
   */
  public Adapter createOsTxQueryByPredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCriteria <em>Os Tx Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCriteria
   * @generated
   */
  public Adapter createOsTxCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTypeCriteria <em>Os Tx Type Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTypeCriteria
   * @generated
   */
  public Adapter createOsTxTypeCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentCriteria <em>Os Tx Comment Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentCriteria
   * @generated
   */
  public Adapter createOsTxCommentCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentClause <em>Os Tx Comment Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentClause
   * @generated
   */
  public Adapter createOsTxCommentClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentEqualsClause <em>Os Tx Comment Equals Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentEqualsClause
   * @generated
   */
  public Adapter createOsTxCommentEqualsClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentPatternClause <em>Os Tx Comment Pattern Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentPatternClause
   * @generated
   */
  public Adapter createOsTxCommentPatternClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxBranchIdCriteria <em>Os Tx Branch Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxBranchIdCriteria
   * @generated
   */
  public Adapter createOsTxBranchIdCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxHeadOfBranchIdCriteria <em>Os Tx Head Of Branch Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxHeadOfBranchIdCriteria
   * @generated
   */
  public Adapter createOsTxHeadOfBranchIdCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxAuthorIdCriteria <em>Os Tx Author Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxAuthorIdCriteria
   * @generated
   */
  public Adapter createOsTxAuthorIdCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdCriteria <em>Os Tx Commit Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdCriteria
   * @generated
   */
  public Adapter createOsTxCommitIdCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdClause <em>Os Tx Commit Id Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdClause
   * @generated
   */
  public Adapter createOsTxCommitIdClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdEqualsClause <em>Os Tx Commit Id Equals Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdEqualsClause
   * @generated
   */
  public Adapter createOsTxCommitIdEqualsClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdIsNullClause <em>Os Tx Commit Id Is Null Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdIsNullClause
   * @generated
   */
  public Adapter createOsTxCommitIdIsNullClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdCriteria <em>Os Tx Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdCriteria
   * @generated
   */
  public Adapter createOsTxIdCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdClause <em>Os Tx Id Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdClause
   * @generated
   */
  public Adapter createOsTxIdClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdEqualsClause <em>Os Tx Id Equals Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdEqualsClause
   * @generated
   */
  public Adapter createOsTxIdEqualsClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause <em>Os Tx Id Op Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause
   * @generated
   */
  public Adapter createOsTxIdOpClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause <em>Os Tx Id Range Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause
   * @generated
   */
  public Adapter createOsTxIdRangeClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampCriteria <em>Os Tx Timestamp Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampCriteria
   * @generated
   */
  public Adapter createOsTxTimestampCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampClause <em>Os Tx Timestamp Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampClause
   * @generated
   */
  public Adapter createOsTxTimestampClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause <em>Os Tx Timestamp Op Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause
   * @generated
   */
  public Adapter createOsTxTimestampOpClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause <em>Os Tx Timestamp Range Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause
   * @generated
   */
  public Adapter createOsTxTimestampRangeClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement <em>Os Artifact Query Statement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryStatement
   * @generated
   */
  public Adapter createOsArtifactQueryStatementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQuery <em>Os Artifact Query</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQuery
   * @generated
   */
  public Adapter createOsArtifactQueryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryAll <em>Os Artifact Query All</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryAll
   * @generated
   */
  public Adapter createOsArtifactQueryAllAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryByPredicate <em>Os Artifact Query By Predicate</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryByPredicate
   * @generated
   */
  public Adapter createOsArtifactQueryByPredicateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsItemCriteria <em>Os Item Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsItemCriteria
   * @generated
   */
  public Adapter createOsItemCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactCriteria <em>Os Artifact Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactCriteria
   * @generated
   */
  public Adapter createOsArtifactCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactIdCriteria <em>Os Artifact Id Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactIdCriteria
   * @generated
   */
  public Adapter createOsArtifactIdCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactGuidCriteria <em>Os Artifact Guid Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactGuidCriteria
   * @generated
   */
  public Adapter createOsArtifactGuidCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeCriteria <em>Os Artifact Type Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeCriteria
   * @generated
   */
  public Adapter createOsArtifactTypeCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeClause <em>Os Artifact Type Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeClause
   * @generated
   */
  public Adapter createOsArtifactTypeClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeEqualsClause <em>Os Artifact Type Equals Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeEqualsClause
   * @generated
   */
  public Adapter createOsArtifactTypeEqualsClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeInstanceOfClause <em>Os Artifact Type Instance Of Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeInstanceOfClause
   * @generated
   */
  public Adapter createOsArtifactTypeInstanceOfClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeCriteria <em>Os Attribute Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeCriteria
   * @generated
   */
  public Adapter createOsAttributeCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeClause <em>Os Attribute Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeClause
   * @generated
   */
  public Adapter createOsAttributeClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeExistClause <em>Os Attribute Exist Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeExistClause
   * @generated
   */
  public Adapter createOsAttributeExistClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause <em>Ose Attribute Op Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause
   * @generated
   */
  public Adapter createOseAttributeOpClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationCriteria <em>Os Relation Criteria</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationCriteria
   * @generated
   */
  public Adapter createOsRelationCriteriaAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause <em>Os Relation Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationClause
   * @generated
   */
  public Adapter createOsRelationClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationExistClause <em>Os Relation Exist Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationExistClause
   * @generated
   */
  public Adapter createOsRelationExistClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelatedToClause <em>Os Related To Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelatedToClause
   * @generated
   */
  public Adapter createOsRelatedToClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowClause <em>Os Follow Clause</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowClause
   * @generated
   */
  public Adapter createOsFollowClauseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowStatement <em>Os Follow Statement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowStatement
   * @generated
   */
  public Adapter createOsFollowStatementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType <em>Os Follow Relation Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType
   * @generated
   */
  public Adapter createOsFollowRelationTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableDeclaration <em>Os Variable Declaration</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableDeclaration
   * @generated
   */
  public Adapter createOsVariableDeclarationAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable <em>Os Variable</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariable
   * @generated
   */
  public Adapter createOsVariableAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAssignment <em>Os Assignment</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAssignment
   * @generated
   */
  public Adapter createOsAssignmentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBooleanLiteral <em>Os Boolean Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBooleanLiteral
   * @generated
   */
  public Adapter createOsBooleanLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNullLiteral <em>Os Null Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNullLiteral
   * @generated
   */
  public Adapter createOsNullLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNumberLiteral <em>Os Number Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNumberLiteral
   * @generated
   */
  public Adapter createOsNumberLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral <em>Os String Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral
   * @generated
   */
  public Adapter createOsStringLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTemplateLiteral <em>Os Template Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTemplateLiteral
   * @generated
   */
  public Adapter createOsTemplateLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference <em>Os Variable Reference</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsVariableReference
   * @generated
   */
  public Adapter createOsVariableReferenceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression <em>Os Dot Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsDotExpression
   * @generated
   */
  public Adapter createOsDotExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression <em>Os Query Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryExpression
   * @generated
   */
  public Adapter createOsQueryExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression <em>Os Collect Object Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression
   * @generated
   */
  public Adapter createOsCollectObjectExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectAllFieldsExpression <em>Os Collect All Fields Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectAllFieldsExpression
   * @generated
   */
  public Adapter createOsCollectAllFieldsExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression <em>Os Collect Field Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression
   * @generated
   */
  public Adapter createOsCollectFieldExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //OrcsScriptDslAdapterFactory
