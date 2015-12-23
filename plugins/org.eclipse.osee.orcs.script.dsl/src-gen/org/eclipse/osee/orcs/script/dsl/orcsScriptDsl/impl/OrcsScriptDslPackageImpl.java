/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.*;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class OrcsScriptDslPackageImpl extends EPackageImpl implements OrcsScriptDslPackage {
   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass orcsScriptEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass scriptStatementEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass scriptVersionEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osExpressionEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osCollectionLiteralEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osListLiteralEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osQueryStatementEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osQueryEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osCritieriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osCollectClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osLimitClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osCollectExpressionEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osFindClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osObjectQueryEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchQueryStatementEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchQueryEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchQueryByIdEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchQueryAllEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchQueryByPredicateEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchNameCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchNameClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchNameEqualsClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchNamePatternClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchTypeCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchStateCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchArchivedCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchIdCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchOfCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchChildOfClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBranchParentOfClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxQueryStatementEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxQueryEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxQueryByIdEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxQueryAllEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxQueryByPredicateEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxTypeCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxCommentCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxCommentClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxCommentEqualsClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxCommentPatternClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxBranchIdCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxHeadOfBranchIdCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxAuthorIdCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxCommitIdCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxCommitIdClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxCommitIdEqualsClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxCommitIdIsNullClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxIdCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxIdClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxIdEqualsClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxIdOpClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxIdRangeClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxTimestampCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxTimestampClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxTimestampOpClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTxTimestampRangeClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactQueryStatementEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactQueryEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactQueryAllEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactQueryByPredicateEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osItemCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactIdCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactGuidCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactTypeCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactTypeClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactTypeEqualsClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osArtifactTypeInstanceOfClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osAttributeCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osAttributeClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osAttributeExistClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass oseAttributeOpClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osRelationCriteriaEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osRelationClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osRelationExistClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osRelatedToClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osFollowClauseEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osFollowStatementEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osFollowRelationTypeEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osVariableDeclarationEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osVariableEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osAssignmentEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osBooleanLiteralEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osNullLiteralEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osNumberLiteralEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osStringLiteralEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osTemplateLiteralEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osVariableReferenceEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osDotExpressionEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osQueryExpressionEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osCollectObjectExpressionEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osCollectAllFieldsExpressionEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EClass osCollectFieldExpressionEClass = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EEnum osBranchStateEEnum = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EEnum osBranchTypeEEnum = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EEnum osBranchArchiveFilterEEnum = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EEnum osTxTypeEEnum = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EEnum osRelationSideEEnum = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EEnum osOperatorEEnum = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EEnum osNonEqualOperatorEEnum = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EEnum osExistenceOperatorEEnum = null;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private EEnum osQueryOptionEEnum = null;

   /**
    * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
    * EPackage.Registry} by the package package URI value.
    * <p>
    * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
    * performs initialization of the package, or returns the registered package, if one already exists. <!--
    * begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see org.eclipse.emf.ecore.EPackage.Registry
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#eNS_URI
    * @see #init()
    * @generated
    */
   private OrcsScriptDslPackageImpl() {
      super(eNS_URI, OrcsScriptDslFactory.eINSTANCE);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private static boolean isInited = false;

   /**
    * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
    * <p>
    * This method is used to initialize {@link OrcsScriptDslPackage#eINSTANCE} when that field is accessed. Clients
    * should not invoke it directly. Instead, they should simply access that field to obtain the package. <!--
    * begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #eNS_URI
    * @see #createPackageContents()
    * @see #initializePackageContents()
    * @generated
    */
   public static OrcsScriptDslPackage init() {
      if (isInited) {
         return (OrcsScriptDslPackage) EPackage.Registry.INSTANCE.getEPackage(OrcsScriptDslPackage.eNS_URI);
      }

      // Obtain or create and register package
      OrcsScriptDslPackageImpl theOrcsScriptDslPackage = (OrcsScriptDslPackageImpl) (EPackage.Registry.INSTANCE.get(
         eNS_URI) instanceof OrcsScriptDslPackageImpl ? EPackage.Registry.INSTANCE.get(
            eNS_URI) : new OrcsScriptDslPackageImpl());

      isInited = true;

      // Create package meta-data objects
      theOrcsScriptDslPackage.createPackageContents();

      // Initialize created meta-data
      theOrcsScriptDslPackage.initializePackageContents();

      // Mark meta-data to indicate it can't be changed
      theOrcsScriptDslPackage.freeze();

      // Update the registry and return the package
      EPackage.Registry.INSTANCE.put(OrcsScriptDslPackage.eNS_URI, theOrcsScriptDslPackage);
      return theOrcsScriptDslPackage;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOrcsScript() {
      return orcsScriptEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOrcsScript_Version() {
      return (EReference) orcsScriptEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOrcsScript_Statements() {
      return (EReference) orcsScriptEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getScriptStatement() {
      return scriptStatementEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getScriptVersion() {
      return scriptVersionEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getScriptVersion_Name() {
      return (EAttribute) scriptVersionEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getScriptVersion_Version() {
      return (EAttribute) scriptVersionEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsExpression() {
      return osExpressionEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsCollectionLiteral() {
      return osCollectionLiteralEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsListLiteral() {
      return osListLiteralEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsListLiteral_Elements() {
      return (EReference) osListLiteralEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsQueryStatement() {
      return osQueryStatementEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsQueryStatement_Stmt() {
      return (EReference) osQueryStatementEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsQuery() {
      return osQueryEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsQuery_Name() {
      return (EAttribute) osQueryEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsQuery_Collect() {
      return (EReference) osQueryEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsCritieria() {
      return osCritieriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsCritieria_Name() {
      return (EAttribute) osCritieriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsClause() {
      return osClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsClause_Name() {
      return (EAttribute) osClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsCollectClause() {
      return osCollectClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsCollectClause_Name() {
      return (EAttribute) osCollectClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsCollectClause_Expression() {
      return (EReference) osCollectClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsCollectClause_Limit() {
      return (EReference) osCollectClauseEClass.getEStructuralFeatures().get(2);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsLimitClause() {
      return osLimitClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsLimitClause_Name() {
      return (EAttribute) osLimitClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsLimitClause_Limit() {
      return (EReference) osLimitClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsCollectExpression() {
      return osCollectExpressionEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsCollectExpression_Name() {
      return (EAttribute) osCollectExpressionEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsFindClause() {
      return osFindClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsFindClause_Query() {
      return (EReference) osFindClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsObjectQuery() {
      return osObjectQueryEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchQueryStatement() {
      return osBranchQueryStatementEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsBranchQueryStatement_Data() {
      return (EReference) osBranchQueryStatementEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchQuery() {
      return osBranchQueryEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchQueryById() {
      return osBranchQueryByIdEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsBranchQueryById_Name() {
      return (EReference) osBranchQueryByIdEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchQueryAll() {
      return osBranchQueryAllEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsBranchQueryAll_Name() {
      return (EAttribute) osBranchQueryAllEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchQueryByPredicate() {
      return osBranchQueryByPredicateEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsBranchQueryByPredicate_Name() {
      return (EAttribute) osBranchQueryByPredicateEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsBranchQueryByPredicate_Criteria() {
      return (EReference) osBranchQueryByPredicateEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchCriteria() {
      return osBranchCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchNameCriteria() {
      return osBranchNameCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsBranchNameCriteria_Clause() {
      return (EReference) osBranchNameCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchNameClause() {
      return osBranchNameClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsBranchNameClause_Name() {
      return (EAttribute) osBranchNameClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsBranchNameClause_Value() {
      return (EReference) osBranchNameClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchNameEqualsClause() {
      return osBranchNameEqualsClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchNamePatternClause() {
      return osBranchNamePatternClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchTypeCriteria() {
      return osBranchTypeCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsBranchTypeCriteria_Types() {
      return (EAttribute) osBranchTypeCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchStateCriteria() {
      return osBranchStateCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsBranchStateCriteria_States() {
      return (EAttribute) osBranchStateCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchArchivedCriteria() {
      return osBranchArchivedCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsBranchArchivedCriteria_Filter() {
      return (EAttribute) osBranchArchivedCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchIdCriteria() {
      return osBranchIdCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsBranchIdCriteria_Ids() {
      return (EReference) osBranchIdCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchOfCriteria() {
      return osBranchOfCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsBranchOfCriteria_Clause() {
      return (EReference) osBranchOfCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchClause() {
      return osBranchClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsBranchClause_Name() {
      return (EAttribute) osBranchClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsBranchClause_Id() {
      return (EReference) osBranchClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchChildOfClause() {
      return osBranchChildOfClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBranchParentOfClause() {
      return osBranchParentOfClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxQueryStatement() {
      return osTxQueryStatementEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxQueryStatement_Data() {
      return (EReference) osTxQueryStatementEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxQuery() {
      return osTxQueryEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxQueryById() {
      return osTxQueryByIdEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxQueryById_Name() {
      return (EReference) osTxQueryByIdEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxQueryAll() {
      return osTxQueryAllEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTxQueryAll_Name() {
      return (EAttribute) osTxQueryAllEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxQueryByPredicate() {
      return osTxQueryByPredicateEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTxQueryByPredicate_Name() {
      return (EAttribute) osTxQueryByPredicateEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxQueryByPredicate_Criteria() {
      return (EReference) osTxQueryByPredicateEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxCriteria() {
      return osTxCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxTypeCriteria() {
      return osTxTypeCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTxTypeCriteria_Types() {
      return (EAttribute) osTxTypeCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxCommentCriteria() {
      return osTxCommentCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxCommentCriteria_Clause() {
      return (EReference) osTxCommentCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxCommentClause() {
      return osTxCommentClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTxCommentClause_Name() {
      return (EAttribute) osTxCommentClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxCommentClause_Value() {
      return (EReference) osTxCommentClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxCommentEqualsClause() {
      return osTxCommentEqualsClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxCommentPatternClause() {
      return osTxCommentPatternClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxBranchIdCriteria() {
      return osTxBranchIdCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxBranchIdCriteria_Ids() {
      return (EReference) osTxBranchIdCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxHeadOfBranchIdCriteria() {
      return osTxHeadOfBranchIdCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxHeadOfBranchIdCriteria_Id() {
      return (EReference) osTxHeadOfBranchIdCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxAuthorIdCriteria() {
      return osTxAuthorIdCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxAuthorIdCriteria_Ids() {
      return (EReference) osTxAuthorIdCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxCommitIdCriteria() {
      return osTxCommitIdCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxCommitIdCriteria_Clause() {
      return (EReference) osTxCommitIdCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxCommitIdClause() {
      return osTxCommitIdClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTxCommitIdClause_Name() {
      return (EAttribute) osTxCommitIdClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxCommitIdEqualsClause() {
      return osTxCommitIdEqualsClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxCommitIdEqualsClause_Ids() {
      return (EReference) osTxCommitIdEqualsClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxCommitIdIsNullClause() {
      return osTxCommitIdIsNullClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxIdCriteria() {
      return osTxIdCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxIdCriteria_Clause() {
      return (EReference) osTxIdCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxIdClause() {
      return osTxIdClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxIdEqualsClause() {
      return osTxIdEqualsClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTxIdEqualsClause_Name() {
      return (EAttribute) osTxIdEqualsClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxIdEqualsClause_Ids() {
      return (EReference) osTxIdEqualsClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxIdOpClause() {
      return osTxIdOpClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTxIdOpClause_Op() {
      return (EAttribute) osTxIdOpClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxIdOpClause_Id() {
      return (EReference) osTxIdOpClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxIdRangeClause() {
      return osTxIdRangeClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTxIdRangeClause_Name() {
      return (EAttribute) osTxIdRangeClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxIdRangeClause_FromId() {
      return (EReference) osTxIdRangeClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxIdRangeClause_ToId() {
      return (EReference) osTxIdRangeClauseEClass.getEStructuralFeatures().get(2);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxTimestampCriteria() {
      return osTxTimestampCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxTimestampCriteria_Clause() {
      return (EReference) osTxTimestampCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxTimestampClause() {
      return osTxTimestampClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxTimestampOpClause() {
      return osTxTimestampOpClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTxTimestampOpClause_Op() {
      return (EAttribute) osTxTimestampOpClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxTimestampOpClause_Timestamp() {
      return (EReference) osTxTimestampOpClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTxTimestampRangeClause() {
      return osTxTimestampRangeClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTxTimestampRangeClause_Name() {
      return (EAttribute) osTxTimestampRangeClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxTimestampRangeClause_From() {
      return (EReference) osTxTimestampRangeClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsTxTimestampRangeClause_To() {
      return (EReference) osTxTimestampRangeClauseEClass.getEStructuralFeatures().get(2);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactQueryStatement() {
      return osArtifactQueryStatementEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsArtifactQueryStatement_Name() {
      return (EAttribute) osArtifactQueryStatementEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsArtifactQueryStatement_Data() {
      return (EReference) osArtifactQueryStatementEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsArtifactQueryStatement_Collect() {
      return (EReference) osArtifactQueryStatementEClass.getEStructuralFeatures().get(2);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactQuery() {
      return osArtifactQueryEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsArtifactQuery_Name() {
      return (EAttribute) osArtifactQueryEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactQueryAll() {
      return osArtifactQueryAllEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactQueryByPredicate() {
      return osArtifactQueryByPredicateEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsArtifactQueryByPredicate_Criteria() {
      return (EReference) osArtifactQueryByPredicateEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsItemCriteria() {
      return osItemCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactCriteria() {
      return osArtifactCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactIdCriteria() {
      return osArtifactIdCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsArtifactIdCriteria_Ids() {
      return (EReference) osArtifactIdCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactGuidCriteria() {
      return osArtifactGuidCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsArtifactGuidCriteria_Ids() {
      return (EReference) osArtifactGuidCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactTypeCriteria() {
      return osArtifactTypeCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsArtifactTypeCriteria_Clause() {
      return (EReference) osArtifactTypeCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactTypeClause() {
      return osArtifactTypeClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsArtifactTypeClause_Name() {
      return (EAttribute) osArtifactTypeClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsArtifactTypeClause_Types() {
      return (EReference) osArtifactTypeClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactTypeEqualsClause() {
      return osArtifactTypeEqualsClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsArtifactTypeInstanceOfClause() {
      return osArtifactTypeInstanceOfClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsAttributeCriteria() {
      return osAttributeCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsAttributeCriteria_Clause() {
      return (EReference) osAttributeCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsAttributeClause() {
      return osAttributeClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsAttributeClause_Types() {
      return (EReference) osAttributeClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsAttributeClause_Name() {
      return (EAttribute) osAttributeClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsAttributeExistClause() {
      return osAttributeExistClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOseAttributeOpClause() {
      return oseAttributeOpClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOseAttributeOpClause_Options() {
      return (EAttribute) oseAttributeOpClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOseAttributeOpClause_Values() {
      return (EReference) oseAttributeOpClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsRelationCriteria() {
      return osRelationCriteriaEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsRelationCriteria_Clause() {
      return (EReference) osRelationCriteriaEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsRelationClause() {
      return osRelationClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsRelationClause_Name() {
      return (EAttribute) osRelationClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsRelationClause_Type() {
      return (EReference) osRelationClauseEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsRelationClause_Side() {
      return (EAttribute) osRelationClauseEClass.getEStructuralFeatures().get(2);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsRelationExistClause() {
      return osRelationExistClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsRelationExistClause_Op() {
      return (EAttribute) osRelationExistClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsRelatedToClause() {
      return osRelatedToClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsRelatedToClause_Ids() {
      return (EReference) osRelatedToClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsFollowClause() {
      return osFollowClauseEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsFollowClause_Stmt() {
      return (EReference) osFollowClauseEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsFollowStatement() {
      return osFollowStatementEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsFollowRelationType() {
      return osFollowRelationTypeEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsFollowRelationType_Name() {
      return (EAttribute) osFollowRelationTypeEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsFollowRelationType_Type() {
      return (EReference) osFollowRelationTypeEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsFollowRelationType_Side() {
      return (EAttribute) osFollowRelationTypeEClass.getEStructuralFeatures().get(2);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsFollowRelationType_Criteria() {
      return (EReference) osFollowRelationTypeEClass.getEStructuralFeatures().get(3);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsFollowRelationType_Collect() {
      return (EReference) osFollowRelationTypeEClass.getEStructuralFeatures().get(4);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsVariableDeclaration() {
      return osVariableDeclarationEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsVariableDeclaration_Elements() {
      return (EReference) osVariableDeclarationEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsVariable() {
      return osVariableEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsVariable_Name() {
      return (EAttribute) osVariableEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsVariable_Right() {
      return (EReference) osVariableEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsAssignment() {
      return osAssignmentEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsAssignment_Right() {
      return (EReference) osAssignmentEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsBooleanLiteral() {
      return osBooleanLiteralEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsBooleanLiteral_IsTrue() {
      return (EAttribute) osBooleanLiteralEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsNullLiteral() {
      return osNullLiteralEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsNumberLiteral() {
      return osNumberLiteralEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsNumberLiteral_Value() {
      return (EAttribute) osNumberLiteralEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsStringLiteral() {
      return osStringLiteralEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsStringLiteral_Value() {
      return (EAttribute) osStringLiteralEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsTemplateLiteral() {
      return osTemplateLiteralEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsTemplateLiteral_Value() {
      return (EAttribute) osTemplateLiteralEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsVariableReference() {
      return osVariableReferenceEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsVariableReference_Ref() {
      return (EReference) osVariableReferenceEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsDotExpression() {
      return osDotExpressionEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsDotExpression_Ref() {
      return (EReference) osDotExpressionEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsDotExpression_Tail() {
      return (EReference) osDotExpressionEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsQueryExpression() {
      return osQueryExpressionEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EAttribute getOsQueryExpression_Name() {
      return (EAttribute) osQueryExpressionEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsQueryExpression_Query() {
      return (EReference) osQueryExpressionEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsQueryExpression_Clause() {
      return (EReference) osQueryExpressionEClass.getEStructuralFeatures().get(2);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsCollectObjectExpression() {
      return osCollectObjectExpressionEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsCollectObjectExpression_Alias() {
      return (EReference) osCollectObjectExpressionEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsCollectObjectExpression_Expressions() {
      return (EReference) osCollectObjectExpressionEClass.getEStructuralFeatures().get(1);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsCollectAllFieldsExpression() {
      return osCollectAllFieldsExpressionEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EClass getOsCollectFieldExpression() {
      return osCollectFieldExpressionEClass;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EReference getOsCollectFieldExpression_Alias() {
      return (EReference) osCollectFieldExpressionEClass.getEStructuralFeatures().get(0);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EEnum getOsBranchState() {
      return osBranchStateEEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EEnum getOsBranchType() {
      return osBranchTypeEEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EEnum getOsBranchArchiveFilter() {
      return osBranchArchiveFilterEEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EEnum getOsTxType() {
      return osTxTypeEEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EEnum getOsRelationSide() {
      return osRelationSideEEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EEnum getOsOperator() {
      return osOperatorEEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EEnum getOsNonEqualOperator() {
      return osNonEqualOperatorEEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EEnum getOsExistenceOperator() {
      return osExistenceOperatorEEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public EEnum getOsQueryOption() {
      return osQueryOptionEEnum;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public OrcsScriptDslFactory getOrcsScriptDslFactory() {
      return (OrcsScriptDslFactory) getEFactoryInstance();
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private boolean isCreated = false;

   /**
    * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its
    * first. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public void createPackageContents() {
      if (isCreated) {
         return;
      }
      isCreated = true;

      // Create classes and their features
      orcsScriptEClass = createEClass(ORCS_SCRIPT);
      createEReference(orcsScriptEClass, ORCS_SCRIPT__VERSION);
      createEReference(orcsScriptEClass, ORCS_SCRIPT__STATEMENTS);

      scriptStatementEClass = createEClass(SCRIPT_STATEMENT);

      scriptVersionEClass = createEClass(SCRIPT_VERSION);
      createEAttribute(scriptVersionEClass, SCRIPT_VERSION__NAME);
      createEAttribute(scriptVersionEClass, SCRIPT_VERSION__VERSION);

      osExpressionEClass = createEClass(OS_EXPRESSION);

      osCollectionLiteralEClass = createEClass(OS_COLLECTION_LITERAL);

      osListLiteralEClass = createEClass(OS_LIST_LITERAL);
      createEReference(osListLiteralEClass, OS_LIST_LITERAL__ELEMENTS);

      osQueryStatementEClass = createEClass(OS_QUERY_STATEMENT);
      createEReference(osQueryStatementEClass, OS_QUERY_STATEMENT__STMT);

      osQueryEClass = createEClass(OS_QUERY);
      createEAttribute(osQueryEClass, OS_QUERY__NAME);
      createEReference(osQueryEClass, OS_QUERY__COLLECT);

      osCritieriaEClass = createEClass(OS_CRITIERIA);
      createEAttribute(osCritieriaEClass, OS_CRITIERIA__NAME);

      osClauseEClass = createEClass(OS_CLAUSE);
      createEAttribute(osClauseEClass, OS_CLAUSE__NAME);

      osCollectClauseEClass = createEClass(OS_COLLECT_CLAUSE);
      createEAttribute(osCollectClauseEClass, OS_COLLECT_CLAUSE__NAME);
      createEReference(osCollectClauseEClass, OS_COLLECT_CLAUSE__EXPRESSION);
      createEReference(osCollectClauseEClass, OS_COLLECT_CLAUSE__LIMIT);

      osLimitClauseEClass = createEClass(OS_LIMIT_CLAUSE);
      createEAttribute(osLimitClauseEClass, OS_LIMIT_CLAUSE__NAME);
      createEReference(osLimitClauseEClass, OS_LIMIT_CLAUSE__LIMIT);

      osCollectExpressionEClass = createEClass(OS_COLLECT_EXPRESSION);
      createEAttribute(osCollectExpressionEClass, OS_COLLECT_EXPRESSION__NAME);

      osFindClauseEClass = createEClass(OS_FIND_CLAUSE);
      createEReference(osFindClauseEClass, OS_FIND_CLAUSE__QUERY);

      osObjectQueryEClass = createEClass(OS_OBJECT_QUERY);

      osBranchQueryStatementEClass = createEClass(OS_BRANCH_QUERY_STATEMENT);
      createEReference(osBranchQueryStatementEClass, OS_BRANCH_QUERY_STATEMENT__DATA);

      osBranchQueryEClass = createEClass(OS_BRANCH_QUERY);

      osBranchQueryByIdEClass = createEClass(OS_BRANCH_QUERY_BY_ID);
      createEReference(osBranchQueryByIdEClass, OS_BRANCH_QUERY_BY_ID__NAME);

      osBranchQueryAllEClass = createEClass(OS_BRANCH_QUERY_ALL);
      createEAttribute(osBranchQueryAllEClass, OS_BRANCH_QUERY_ALL__NAME);

      osBranchQueryByPredicateEClass = createEClass(OS_BRANCH_QUERY_BY_PREDICATE);
      createEAttribute(osBranchQueryByPredicateEClass, OS_BRANCH_QUERY_BY_PREDICATE__NAME);
      createEReference(osBranchQueryByPredicateEClass, OS_BRANCH_QUERY_BY_PREDICATE__CRITERIA);

      osBranchCriteriaEClass = createEClass(OS_BRANCH_CRITERIA);

      osBranchNameCriteriaEClass = createEClass(OS_BRANCH_NAME_CRITERIA);
      createEReference(osBranchNameCriteriaEClass, OS_BRANCH_NAME_CRITERIA__CLAUSE);

      osBranchNameClauseEClass = createEClass(OS_BRANCH_NAME_CLAUSE);
      createEAttribute(osBranchNameClauseEClass, OS_BRANCH_NAME_CLAUSE__NAME);
      createEReference(osBranchNameClauseEClass, OS_BRANCH_NAME_CLAUSE__VALUE);

      osBranchNameEqualsClauseEClass = createEClass(OS_BRANCH_NAME_EQUALS_CLAUSE);

      osBranchNamePatternClauseEClass = createEClass(OS_BRANCH_NAME_PATTERN_CLAUSE);

      osBranchTypeCriteriaEClass = createEClass(OS_BRANCH_TYPE_CRITERIA);
      createEAttribute(osBranchTypeCriteriaEClass, OS_BRANCH_TYPE_CRITERIA__TYPES);

      osBranchStateCriteriaEClass = createEClass(OS_BRANCH_STATE_CRITERIA);
      createEAttribute(osBranchStateCriteriaEClass, OS_BRANCH_STATE_CRITERIA__STATES);

      osBranchArchivedCriteriaEClass = createEClass(OS_BRANCH_ARCHIVED_CRITERIA);
      createEAttribute(osBranchArchivedCriteriaEClass, OS_BRANCH_ARCHIVED_CRITERIA__FILTER);

      osBranchIdCriteriaEClass = createEClass(OS_BRANCH_ID_CRITERIA);
      createEReference(osBranchIdCriteriaEClass, OS_BRANCH_ID_CRITERIA__IDS);

      osBranchOfCriteriaEClass = createEClass(OS_BRANCH_OF_CRITERIA);
      createEReference(osBranchOfCriteriaEClass, OS_BRANCH_OF_CRITERIA__CLAUSE);

      osBranchClauseEClass = createEClass(OS_BRANCH_CLAUSE);
      createEAttribute(osBranchClauseEClass, OS_BRANCH_CLAUSE__NAME);
      createEReference(osBranchClauseEClass, OS_BRANCH_CLAUSE__ID);

      osBranchChildOfClauseEClass = createEClass(OS_BRANCH_CHILD_OF_CLAUSE);

      osBranchParentOfClauseEClass = createEClass(OS_BRANCH_PARENT_OF_CLAUSE);

      osTxQueryStatementEClass = createEClass(OS_TX_QUERY_STATEMENT);
      createEReference(osTxQueryStatementEClass, OS_TX_QUERY_STATEMENT__DATA);

      osTxQueryEClass = createEClass(OS_TX_QUERY);

      osTxQueryByIdEClass = createEClass(OS_TX_QUERY_BY_ID);
      createEReference(osTxQueryByIdEClass, OS_TX_QUERY_BY_ID__NAME);

      osTxQueryAllEClass = createEClass(OS_TX_QUERY_ALL);
      createEAttribute(osTxQueryAllEClass, OS_TX_QUERY_ALL__NAME);

      osTxQueryByPredicateEClass = createEClass(OS_TX_QUERY_BY_PREDICATE);
      createEAttribute(osTxQueryByPredicateEClass, OS_TX_QUERY_BY_PREDICATE__NAME);
      createEReference(osTxQueryByPredicateEClass, OS_TX_QUERY_BY_PREDICATE__CRITERIA);

      osTxCriteriaEClass = createEClass(OS_TX_CRITERIA);

      osTxTypeCriteriaEClass = createEClass(OS_TX_TYPE_CRITERIA);
      createEAttribute(osTxTypeCriteriaEClass, OS_TX_TYPE_CRITERIA__TYPES);

      osTxCommentCriteriaEClass = createEClass(OS_TX_COMMENT_CRITERIA);
      createEReference(osTxCommentCriteriaEClass, OS_TX_COMMENT_CRITERIA__CLAUSE);

      osTxCommentClauseEClass = createEClass(OS_TX_COMMENT_CLAUSE);
      createEAttribute(osTxCommentClauseEClass, OS_TX_COMMENT_CLAUSE__NAME);
      createEReference(osTxCommentClauseEClass, OS_TX_COMMENT_CLAUSE__VALUE);

      osTxCommentEqualsClauseEClass = createEClass(OS_TX_COMMENT_EQUALS_CLAUSE);

      osTxCommentPatternClauseEClass = createEClass(OS_TX_COMMENT_PATTERN_CLAUSE);

      osTxBranchIdCriteriaEClass = createEClass(OS_TX_BRANCH_ID_CRITERIA);
      createEReference(osTxBranchIdCriteriaEClass, OS_TX_BRANCH_ID_CRITERIA__IDS);

      osTxHeadOfBranchIdCriteriaEClass = createEClass(OS_TX_HEAD_OF_BRANCH_ID_CRITERIA);
      createEReference(osTxHeadOfBranchIdCriteriaEClass, OS_TX_HEAD_OF_BRANCH_ID_CRITERIA__ID);

      osTxAuthorIdCriteriaEClass = createEClass(OS_TX_AUTHOR_ID_CRITERIA);
      createEReference(osTxAuthorIdCriteriaEClass, OS_TX_AUTHOR_ID_CRITERIA__IDS);

      osTxCommitIdCriteriaEClass = createEClass(OS_TX_COMMIT_ID_CRITERIA);
      createEReference(osTxCommitIdCriteriaEClass, OS_TX_COMMIT_ID_CRITERIA__CLAUSE);

      osTxCommitIdClauseEClass = createEClass(OS_TX_COMMIT_ID_CLAUSE);
      createEAttribute(osTxCommitIdClauseEClass, OS_TX_COMMIT_ID_CLAUSE__NAME);

      osTxCommitIdEqualsClauseEClass = createEClass(OS_TX_COMMIT_ID_EQUALS_CLAUSE);
      createEReference(osTxCommitIdEqualsClauseEClass, OS_TX_COMMIT_ID_EQUALS_CLAUSE__IDS);

      osTxCommitIdIsNullClauseEClass = createEClass(OS_TX_COMMIT_ID_IS_NULL_CLAUSE);

      osTxIdCriteriaEClass = createEClass(OS_TX_ID_CRITERIA);
      createEReference(osTxIdCriteriaEClass, OS_TX_ID_CRITERIA__CLAUSE);

      osTxIdClauseEClass = createEClass(OS_TX_ID_CLAUSE);

      osTxIdEqualsClauseEClass = createEClass(OS_TX_ID_EQUALS_CLAUSE);
      createEAttribute(osTxIdEqualsClauseEClass, OS_TX_ID_EQUALS_CLAUSE__NAME);
      createEReference(osTxIdEqualsClauseEClass, OS_TX_ID_EQUALS_CLAUSE__IDS);

      osTxIdOpClauseEClass = createEClass(OS_TX_ID_OP_CLAUSE);
      createEAttribute(osTxIdOpClauseEClass, OS_TX_ID_OP_CLAUSE__OP);
      createEReference(osTxIdOpClauseEClass, OS_TX_ID_OP_CLAUSE__ID);

      osTxIdRangeClauseEClass = createEClass(OS_TX_ID_RANGE_CLAUSE);
      createEAttribute(osTxIdRangeClauseEClass, OS_TX_ID_RANGE_CLAUSE__NAME);
      createEReference(osTxIdRangeClauseEClass, OS_TX_ID_RANGE_CLAUSE__FROM_ID);
      createEReference(osTxIdRangeClauseEClass, OS_TX_ID_RANGE_CLAUSE__TO_ID);

      osTxTimestampCriteriaEClass = createEClass(OS_TX_TIMESTAMP_CRITERIA);
      createEReference(osTxTimestampCriteriaEClass, OS_TX_TIMESTAMP_CRITERIA__CLAUSE);

      osTxTimestampClauseEClass = createEClass(OS_TX_TIMESTAMP_CLAUSE);

      osTxTimestampOpClauseEClass = createEClass(OS_TX_TIMESTAMP_OP_CLAUSE);
      createEAttribute(osTxTimestampOpClauseEClass, OS_TX_TIMESTAMP_OP_CLAUSE__OP);
      createEReference(osTxTimestampOpClauseEClass, OS_TX_TIMESTAMP_OP_CLAUSE__TIMESTAMP);

      osTxTimestampRangeClauseEClass = createEClass(OS_TX_TIMESTAMP_RANGE_CLAUSE);
      createEAttribute(osTxTimestampRangeClauseEClass, OS_TX_TIMESTAMP_RANGE_CLAUSE__NAME);
      createEReference(osTxTimestampRangeClauseEClass, OS_TX_TIMESTAMP_RANGE_CLAUSE__FROM);
      createEReference(osTxTimestampRangeClauseEClass, OS_TX_TIMESTAMP_RANGE_CLAUSE__TO);

      osArtifactQueryStatementEClass = createEClass(OS_ARTIFACT_QUERY_STATEMENT);
      createEAttribute(osArtifactQueryStatementEClass, OS_ARTIFACT_QUERY_STATEMENT__NAME);
      createEReference(osArtifactQueryStatementEClass, OS_ARTIFACT_QUERY_STATEMENT__DATA);
      createEReference(osArtifactQueryStatementEClass, OS_ARTIFACT_QUERY_STATEMENT__COLLECT);

      osArtifactQueryEClass = createEClass(OS_ARTIFACT_QUERY);
      createEAttribute(osArtifactQueryEClass, OS_ARTIFACT_QUERY__NAME);

      osArtifactQueryAllEClass = createEClass(OS_ARTIFACT_QUERY_ALL);

      osArtifactQueryByPredicateEClass = createEClass(OS_ARTIFACT_QUERY_BY_PREDICATE);
      createEReference(osArtifactQueryByPredicateEClass, OS_ARTIFACT_QUERY_BY_PREDICATE__CRITERIA);

      osItemCriteriaEClass = createEClass(OS_ITEM_CRITERIA);

      osArtifactCriteriaEClass = createEClass(OS_ARTIFACT_CRITERIA);

      osArtifactIdCriteriaEClass = createEClass(OS_ARTIFACT_ID_CRITERIA);
      createEReference(osArtifactIdCriteriaEClass, OS_ARTIFACT_ID_CRITERIA__IDS);

      osArtifactGuidCriteriaEClass = createEClass(OS_ARTIFACT_GUID_CRITERIA);
      createEReference(osArtifactGuidCriteriaEClass, OS_ARTIFACT_GUID_CRITERIA__IDS);

      osArtifactTypeCriteriaEClass = createEClass(OS_ARTIFACT_TYPE_CRITERIA);
      createEReference(osArtifactTypeCriteriaEClass, OS_ARTIFACT_TYPE_CRITERIA__CLAUSE);

      osArtifactTypeClauseEClass = createEClass(OS_ARTIFACT_TYPE_CLAUSE);
      createEAttribute(osArtifactTypeClauseEClass, OS_ARTIFACT_TYPE_CLAUSE__NAME);
      createEReference(osArtifactTypeClauseEClass, OS_ARTIFACT_TYPE_CLAUSE__TYPES);

      osArtifactTypeEqualsClauseEClass = createEClass(OS_ARTIFACT_TYPE_EQUALS_CLAUSE);

      osArtifactTypeInstanceOfClauseEClass = createEClass(OS_ARTIFACT_TYPE_INSTANCE_OF_CLAUSE);

      osAttributeCriteriaEClass = createEClass(OS_ATTRIBUTE_CRITERIA);
      createEReference(osAttributeCriteriaEClass, OS_ATTRIBUTE_CRITERIA__CLAUSE);

      osAttributeClauseEClass = createEClass(OS_ATTRIBUTE_CLAUSE);
      createEReference(osAttributeClauseEClass, OS_ATTRIBUTE_CLAUSE__TYPES);
      createEAttribute(osAttributeClauseEClass, OS_ATTRIBUTE_CLAUSE__NAME);

      osAttributeExistClauseEClass = createEClass(OS_ATTRIBUTE_EXIST_CLAUSE);

      oseAttributeOpClauseEClass = createEClass(OSE_ATTRIBUTE_OP_CLAUSE);
      createEAttribute(oseAttributeOpClauseEClass, OSE_ATTRIBUTE_OP_CLAUSE__OPTIONS);
      createEReference(oseAttributeOpClauseEClass, OSE_ATTRIBUTE_OP_CLAUSE__VALUES);

      osRelationCriteriaEClass = createEClass(OS_RELATION_CRITERIA);
      createEReference(osRelationCriteriaEClass, OS_RELATION_CRITERIA__CLAUSE);

      osRelationClauseEClass = createEClass(OS_RELATION_CLAUSE);
      createEAttribute(osRelationClauseEClass, OS_RELATION_CLAUSE__NAME);
      createEReference(osRelationClauseEClass, OS_RELATION_CLAUSE__TYPE);
      createEAttribute(osRelationClauseEClass, OS_RELATION_CLAUSE__SIDE);

      osRelationExistClauseEClass = createEClass(OS_RELATION_EXIST_CLAUSE);
      createEAttribute(osRelationExistClauseEClass, OS_RELATION_EXIST_CLAUSE__OP);

      osRelatedToClauseEClass = createEClass(OS_RELATED_TO_CLAUSE);
      createEReference(osRelatedToClauseEClass, OS_RELATED_TO_CLAUSE__IDS);

      osFollowClauseEClass = createEClass(OS_FOLLOW_CLAUSE);
      createEReference(osFollowClauseEClass, OS_FOLLOW_CLAUSE__STMT);

      osFollowStatementEClass = createEClass(OS_FOLLOW_STATEMENT);

      osFollowRelationTypeEClass = createEClass(OS_FOLLOW_RELATION_TYPE);
      createEAttribute(osFollowRelationTypeEClass, OS_FOLLOW_RELATION_TYPE__NAME);
      createEReference(osFollowRelationTypeEClass, OS_FOLLOW_RELATION_TYPE__TYPE);
      createEAttribute(osFollowRelationTypeEClass, OS_FOLLOW_RELATION_TYPE__SIDE);
      createEReference(osFollowRelationTypeEClass, OS_FOLLOW_RELATION_TYPE__CRITERIA);
      createEReference(osFollowRelationTypeEClass, OS_FOLLOW_RELATION_TYPE__COLLECT);

      osVariableDeclarationEClass = createEClass(OS_VARIABLE_DECLARATION);
      createEReference(osVariableDeclarationEClass, OS_VARIABLE_DECLARATION__ELEMENTS);

      osVariableEClass = createEClass(OS_VARIABLE);
      createEAttribute(osVariableEClass, OS_VARIABLE__NAME);
      createEReference(osVariableEClass, OS_VARIABLE__RIGHT);

      osAssignmentEClass = createEClass(OS_ASSIGNMENT);
      createEReference(osAssignmentEClass, OS_ASSIGNMENT__RIGHT);

      osBooleanLiteralEClass = createEClass(OS_BOOLEAN_LITERAL);
      createEAttribute(osBooleanLiteralEClass, OS_BOOLEAN_LITERAL__IS_TRUE);

      osNullLiteralEClass = createEClass(OS_NULL_LITERAL);

      osNumberLiteralEClass = createEClass(OS_NUMBER_LITERAL);
      createEAttribute(osNumberLiteralEClass, OS_NUMBER_LITERAL__VALUE);

      osStringLiteralEClass = createEClass(OS_STRING_LITERAL);
      createEAttribute(osStringLiteralEClass, OS_STRING_LITERAL__VALUE);

      osTemplateLiteralEClass = createEClass(OS_TEMPLATE_LITERAL);
      createEAttribute(osTemplateLiteralEClass, OS_TEMPLATE_LITERAL__VALUE);

      osVariableReferenceEClass = createEClass(OS_VARIABLE_REFERENCE);
      createEReference(osVariableReferenceEClass, OS_VARIABLE_REFERENCE__REF);

      osDotExpressionEClass = createEClass(OS_DOT_EXPRESSION);
      createEReference(osDotExpressionEClass, OS_DOT_EXPRESSION__REF);
      createEReference(osDotExpressionEClass, OS_DOT_EXPRESSION__TAIL);

      osQueryExpressionEClass = createEClass(OS_QUERY_EXPRESSION);
      createEAttribute(osQueryExpressionEClass, OS_QUERY_EXPRESSION__NAME);
      createEReference(osQueryExpressionEClass, OS_QUERY_EXPRESSION__QUERY);
      createEReference(osQueryExpressionEClass, OS_QUERY_EXPRESSION__CLAUSE);

      osCollectObjectExpressionEClass = createEClass(OS_COLLECT_OBJECT_EXPRESSION);
      createEReference(osCollectObjectExpressionEClass, OS_COLLECT_OBJECT_EXPRESSION__ALIAS);
      createEReference(osCollectObjectExpressionEClass, OS_COLLECT_OBJECT_EXPRESSION__EXPRESSIONS);

      osCollectAllFieldsExpressionEClass = createEClass(OS_COLLECT_ALL_FIELDS_EXPRESSION);

      osCollectFieldExpressionEClass = createEClass(OS_COLLECT_FIELD_EXPRESSION);
      createEReference(osCollectFieldExpressionEClass, OS_COLLECT_FIELD_EXPRESSION__ALIAS);

      // Create enums
      osBranchStateEEnum = createEEnum(OS_BRANCH_STATE);
      osBranchTypeEEnum = createEEnum(OS_BRANCH_TYPE);
      osBranchArchiveFilterEEnum = createEEnum(OS_BRANCH_ARCHIVE_FILTER);
      osTxTypeEEnum = createEEnum(OS_TX_TYPE);
      osRelationSideEEnum = createEEnum(OS_RELATION_SIDE);
      osOperatorEEnum = createEEnum(OS_OPERATOR);
      osNonEqualOperatorEEnum = createEEnum(OS_NON_EQUAL_OPERATOR);
      osExistenceOperatorEEnum = createEEnum(OS_EXISTENCE_OPERATOR);
      osQueryOptionEEnum = createEEnum(OS_QUERY_OPTION);
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private boolean isInitialized = false;

   /**
    * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
    * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   public void initializePackageContents() {
      if (isInitialized) {
         return;
      }
      isInitialized = true;

      // Initialize package
      setName(eNAME);
      setNsPrefix(eNS_PREFIX);
      setNsURI(eNS_URI);

      // Create type parameters

      // Set bounds for type parameters

      // Add supertypes to classes
      osExpressionEClass.getESuperTypes().add(this.getScriptStatement());
      osCollectionLiteralEClass.getESuperTypes().add(this.getOsExpression());
      osListLiteralEClass.getESuperTypes().add(this.getOsCollectionLiteral());
      osQueryStatementEClass.getESuperTypes().add(this.getScriptStatement());
      osFindClauseEClass.getESuperTypes().add(this.getOsClause());
      osBranchQueryStatementEClass.getESuperTypes().add(this.getOsQuery());
      osBranchQueryByIdEClass.getESuperTypes().add(this.getOsBranchQuery());
      osBranchQueryAllEClass.getESuperTypes().add(this.getOsBranchQuery());
      osBranchQueryByPredicateEClass.getESuperTypes().add(this.getOsBranchQuery());
      osBranchCriteriaEClass.getESuperTypes().add(this.getOsCritieria());
      osBranchNameCriteriaEClass.getESuperTypes().add(this.getOsBranchCriteria());
      osBranchNameEqualsClauseEClass.getESuperTypes().add(this.getOsBranchNameClause());
      osBranchNamePatternClauseEClass.getESuperTypes().add(this.getOsBranchNameClause());
      osBranchTypeCriteriaEClass.getESuperTypes().add(this.getOsBranchCriteria());
      osBranchStateCriteriaEClass.getESuperTypes().add(this.getOsBranchCriteria());
      osBranchArchivedCriteriaEClass.getESuperTypes().add(this.getOsBranchCriteria());
      osBranchIdCriteriaEClass.getESuperTypes().add(this.getOsBranchCriteria());
      osBranchOfCriteriaEClass.getESuperTypes().add(this.getOsBranchCriteria());
      osBranchChildOfClauseEClass.getESuperTypes().add(this.getOsBranchClause());
      osBranchParentOfClauseEClass.getESuperTypes().add(this.getOsBranchClause());
      osTxQueryStatementEClass.getESuperTypes().add(this.getOsQuery());
      osTxQueryByIdEClass.getESuperTypes().add(this.getOsTxQuery());
      osTxQueryAllEClass.getESuperTypes().add(this.getOsTxQuery());
      osTxQueryByPredicateEClass.getESuperTypes().add(this.getOsTxQuery());
      osTxCriteriaEClass.getESuperTypes().add(this.getOsCritieria());
      osTxTypeCriteriaEClass.getESuperTypes().add(this.getOsTxCriteria());
      osTxCommentCriteriaEClass.getESuperTypes().add(this.getOsTxCriteria());
      osTxCommentEqualsClauseEClass.getESuperTypes().add(this.getOsTxCommentClause());
      osTxCommentPatternClauseEClass.getESuperTypes().add(this.getOsTxCommentClause());
      osTxBranchIdCriteriaEClass.getESuperTypes().add(this.getOsTxCriteria());
      osTxHeadOfBranchIdCriteriaEClass.getESuperTypes().add(this.getOsTxCriteria());
      osTxAuthorIdCriteriaEClass.getESuperTypes().add(this.getOsTxCriteria());
      osTxCommitIdCriteriaEClass.getESuperTypes().add(this.getOsTxCriteria());
      osTxCommitIdEqualsClauseEClass.getESuperTypes().add(this.getOsTxCommitIdClause());
      osTxCommitIdIsNullClauseEClass.getESuperTypes().add(this.getOsTxCommitIdClause());
      osTxIdCriteriaEClass.getESuperTypes().add(this.getOsTxCriteria());
      osTxIdEqualsClauseEClass.getESuperTypes().add(this.getOsTxIdClause());
      osTxIdOpClauseEClass.getESuperTypes().add(this.getOsTxIdClause());
      osTxIdRangeClauseEClass.getESuperTypes().add(this.getOsTxIdClause());
      osTxTimestampCriteriaEClass.getESuperTypes().add(this.getOsTxCriteria());
      osTxTimestampOpClauseEClass.getESuperTypes().add(this.getOsTxTimestampClause());
      osTxTimestampRangeClauseEClass.getESuperTypes().add(this.getOsTxTimestampClause());
      osArtifactQueryStatementEClass.getESuperTypes().add(this.getOsObjectQuery());
      osArtifactQueryAllEClass.getESuperTypes().add(this.getOsArtifactQuery());
      osArtifactQueryByPredicateEClass.getESuperTypes().add(this.getOsArtifactQuery());
      osItemCriteriaEClass.getESuperTypes().add(this.getOsCritieria());
      osArtifactCriteriaEClass.getESuperTypes().add(this.getOsItemCriteria());
      osArtifactIdCriteriaEClass.getESuperTypes().add(this.getOsArtifactCriteria());
      osArtifactGuidCriteriaEClass.getESuperTypes().add(this.getOsArtifactCriteria());
      osArtifactTypeCriteriaEClass.getESuperTypes().add(this.getOsArtifactCriteria());
      osArtifactTypeEqualsClauseEClass.getESuperTypes().add(this.getOsArtifactTypeClause());
      osArtifactTypeInstanceOfClauseEClass.getESuperTypes().add(this.getOsArtifactTypeClause());
      osAttributeCriteriaEClass.getESuperTypes().add(this.getOsItemCriteria());
      osAttributeExistClauseEClass.getESuperTypes().add(this.getOsAttributeClause());
      oseAttributeOpClauseEClass.getESuperTypes().add(this.getOsAttributeClause());
      osRelationCriteriaEClass.getESuperTypes().add(this.getOsItemCriteria());
      osRelationExistClauseEClass.getESuperTypes().add(this.getOsRelationClause());
      osRelatedToClauseEClass.getESuperTypes().add(this.getOsRelationClause());
      osFollowClauseEClass.getESuperTypes().add(this.getOsClause());
      osFollowRelationTypeEClass.getESuperTypes().add(this.getOsFollowStatement());
      osVariableDeclarationEClass.getESuperTypes().add(this.getOsExpression());
      osVariableEClass.getESuperTypes().add(this.getOsExpression());
      osAssignmentEClass.getESuperTypes().add(this.getOsExpression());
      osBooleanLiteralEClass.getESuperTypes().add(this.getOsExpression());
      osNullLiteralEClass.getESuperTypes().add(this.getOsExpression());
      osNumberLiteralEClass.getESuperTypes().add(this.getOsExpression());
      osStringLiteralEClass.getESuperTypes().add(this.getOsExpression());
      osTemplateLiteralEClass.getESuperTypes().add(this.getOsExpression());
      osVariableReferenceEClass.getESuperTypes().add(this.getOsExpression());
      osDotExpressionEClass.getESuperTypes().add(this.getOsExpression());
      osQueryExpressionEClass.getESuperTypes().add(this.getOsExpression());
      osCollectObjectExpressionEClass.getESuperTypes().add(this.getOsCollectExpression());
      osCollectAllFieldsExpressionEClass.getESuperTypes().add(this.getOsCollectExpression());
      osCollectFieldExpressionEClass.getESuperTypes().add(this.getOsCollectExpression());

      // Initialize classes and features; add operations and parameters
      initEClass(orcsScriptEClass, OrcsScript.class, "OrcsScript", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOrcsScript_Version(), this.getScriptVersion(), null, "version", null, 0, 1, OrcsScript.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOrcsScript_Statements(), this.getScriptStatement(), null, "statements", null, 0, -1,
         OrcsScript.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(scriptStatementEClass, ScriptStatement.class, "ScriptStatement", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(scriptVersionEClass, ScriptVersion.class, "ScriptVersion", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getScriptVersion_Name(), ecorePackage.getEString(), "name", null, 0, 1, ScriptVersion.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEAttribute(getScriptVersion_Version(), ecorePackage.getEString(), "version", null, 0, 1, ScriptVersion.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osExpressionEClass, OsExpression.class, "OsExpression", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osCollectionLiteralEClass, OsCollectionLiteral.class, "OsCollectionLiteral", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osListLiteralEClass, OsListLiteral.class, "OsListLiteral", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsListLiteral_Elements(), this.getOsExpression(), null, "elements", null, 0, -1,
         OsListLiteral.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osQueryStatementEClass, OsQueryStatement.class, "OsQueryStatement", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsQueryStatement_Stmt(), this.getOsExpression(), null, "stmt", null, 0, 1,
         OsQueryStatement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osQueryEClass, OsQuery.class, "OsQuery", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsQuery_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsQuery.class, !IS_TRANSIENT,
         !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsQuery_Collect(), this.getOsCollectClause(), null, "collect", null, 0, 1, OsQuery.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osCritieriaEClass, OsCritieria.class, "OsCritieria", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsCritieria_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsCritieria.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osClauseEClass, OsClause.class, "OsClause", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsClause_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsClause.class, !IS_TRANSIENT,
         !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osCollectClauseEClass, OsCollectClause.class, "OsCollectClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsCollectClause_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsCollectClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsCollectClause_Expression(), this.getOsCollectExpression(), null, "expression", null, 0, 1,
         OsCollectClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsCollectClause_Limit(), this.getOsLimitClause(), null, "limit", null, 0, 1,
         OsCollectClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osLimitClauseEClass, OsLimitClause.class, "OsLimitClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsLimitClause_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsLimitClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsLimitClause_Limit(), this.getOsExpression(), null, "limit", null, 0, 1, OsLimitClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osCollectExpressionEClass, OsCollectExpression.class, "OsCollectExpression", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsCollectExpression_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsCollectExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osFindClauseEClass, OsFindClause.class, "OsFindClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsFindClause_Query(), this.getOsObjectQuery(), null, "query", null, 0, 1, OsFindClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osObjectQueryEClass, OsObjectQuery.class, "OsObjectQuery", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osBranchQueryStatementEClass, OsBranchQueryStatement.class, "OsBranchQueryStatement", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsBranchQueryStatement_Data(), this.getOsBranchQuery(), null, "data", null, 0, 1,
         OsBranchQueryStatement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchQueryEClass, OsBranchQuery.class, "OsBranchQuery", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osBranchQueryByIdEClass, OsBranchQueryById.class, "OsBranchQueryById", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsBranchQueryById_Name(), this.getOsExpression(), null, "name", null, 0, 1,
         OsBranchQueryById.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchQueryAllEClass, OsBranchQueryAll.class, "OsBranchQueryAll", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsBranchQueryAll_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsBranchQueryAll.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchQueryByPredicateEClass, OsBranchQueryByPredicate.class, "OsBranchQueryByPredicate",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsBranchQueryByPredicate_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsBranchQueryByPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsBranchQueryByPredicate_Criteria(), this.getOsBranchCriteria(), null, "criteria", null, 0, -1,
         OsBranchQueryByPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchCriteriaEClass, OsBranchCriteria.class, "OsBranchCriteria", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osBranchNameCriteriaEClass, OsBranchNameCriteria.class, "OsBranchNameCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsBranchNameCriteria_Clause(), this.getOsBranchNameClause(), null, "clause", null, 0, 1,
         OsBranchNameCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchNameClauseEClass, OsBranchNameClause.class, "OsBranchNameClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsBranchNameClause_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsBranchNameClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsBranchNameClause_Value(), this.getOsExpression(), null, "value", null, 0, 1,
         OsBranchNameClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchNameEqualsClauseEClass, OsBranchNameEqualsClause.class, "OsBranchNameEqualsClause",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osBranchNamePatternClauseEClass, OsBranchNamePatternClause.class, "OsBranchNamePatternClause",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osBranchTypeCriteriaEClass, OsBranchTypeCriteria.class, "OsBranchTypeCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsBranchTypeCriteria_Types(), this.getOsBranchType(), "types", null, 0, -1,
         OsBranchTypeCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchStateCriteriaEClass, OsBranchStateCriteria.class, "OsBranchStateCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsBranchStateCriteria_States(), this.getOsBranchState(), "states", null, 0, -1,
         OsBranchStateCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchArchivedCriteriaEClass, OsBranchArchivedCriteria.class, "OsBranchArchivedCriteria",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsBranchArchivedCriteria_Filter(), this.getOsBranchArchiveFilter(), "filter", null, 0, 1,
         OsBranchArchivedCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchIdCriteriaEClass, OsBranchIdCriteria.class, "OsBranchIdCriteria", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsBranchIdCriteria_Ids(), this.getOsExpression(), null, "ids", null, 0, -1,
         OsBranchIdCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchOfCriteriaEClass, OsBranchOfCriteria.class, "OsBranchOfCriteria", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsBranchOfCriteria_Clause(), this.getOsBranchClause(), null, "clause", null, 0, 1,
         OsBranchOfCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchClauseEClass, OsBranchClause.class, "OsBranchClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsBranchClause_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsBranchClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsBranchClause_Id(), this.getOsExpression(), null, "id", null, 0, 1, OsBranchClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osBranchChildOfClauseEClass, OsBranchChildOfClause.class, "OsBranchChildOfClause", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osBranchParentOfClauseEClass, OsBranchParentOfClause.class, "OsBranchParentOfClause", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osTxQueryStatementEClass, OsTxQueryStatement.class, "OsTxQueryStatement", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsTxQueryStatement_Data(), this.getOsTxQuery(), null, "data", null, 0, 1,
         OsTxQueryStatement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxQueryEClass, OsTxQuery.class, "OsTxQuery", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osTxQueryByIdEClass, OsTxQueryById.class, "OsTxQueryById", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsTxQueryById_Name(), this.getOsExpression(), null, "name", null, 0, 1, OsTxQueryById.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osTxQueryAllEClass, OsTxQueryAll.class, "OsTxQueryAll", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTxQueryAll_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsTxQueryAll.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxQueryByPredicateEClass, OsTxQueryByPredicate.class, "OsTxQueryByPredicate", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTxQueryByPredicate_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsTxQueryByPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsTxQueryByPredicate_Criteria(), this.getOsTxCriteria(), null, "criteria", null, 0, -1,
         OsTxQueryByPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxCriteriaEClass, OsTxCriteria.class, "OsTxCriteria", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osTxTypeCriteriaEClass, OsTxTypeCriteria.class, "OsTxTypeCriteria", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTxTypeCriteria_Types(), this.getOsTxType(), "types", null, 0, -1, OsTxTypeCriteria.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxCommentCriteriaEClass, OsTxCommentCriteria.class, "OsTxCommentCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsTxCommentCriteria_Clause(), this.getOsTxCommentClause(), null, "clause", null, 0, 1,
         OsTxCommentCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxCommentClauseEClass, OsTxCommentClause.class, "OsTxCommentClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTxCommentClause_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsTxCommentClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsTxCommentClause_Value(), this.getOsExpression(), null, "value", null, 0, 1,
         OsTxCommentClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxCommentEqualsClauseEClass, OsTxCommentEqualsClause.class, "OsTxCommentEqualsClause", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osTxCommentPatternClauseEClass, OsTxCommentPatternClause.class, "OsTxCommentPatternClause",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osTxBranchIdCriteriaEClass, OsTxBranchIdCriteria.class, "OsTxBranchIdCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsTxBranchIdCriteria_Ids(), this.getOsExpression(), null, "ids", null, 0, -1,
         OsTxBranchIdCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxHeadOfBranchIdCriteriaEClass, OsTxHeadOfBranchIdCriteria.class, "OsTxHeadOfBranchIdCriteria",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsTxHeadOfBranchIdCriteria_Id(), this.getOsExpression(), null, "id", null, 0, 1,
         OsTxHeadOfBranchIdCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
         !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxAuthorIdCriteriaEClass, OsTxAuthorIdCriteria.class, "OsTxAuthorIdCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsTxAuthorIdCriteria_Ids(), this.getOsExpression(), null, "ids", null, 0, -1,
         OsTxAuthorIdCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxCommitIdCriteriaEClass, OsTxCommitIdCriteria.class, "OsTxCommitIdCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsTxCommitIdCriteria_Clause(), this.getOsTxCommitIdClause(), null, "clause", null, 0, 1,
         OsTxCommitIdCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxCommitIdClauseEClass, OsTxCommitIdClause.class, "OsTxCommitIdClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTxCommitIdClause_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsTxCommitIdClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osTxCommitIdEqualsClauseEClass, OsTxCommitIdEqualsClause.class, "OsTxCommitIdEqualsClause",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsTxCommitIdEqualsClause_Ids(), this.getOsExpression(), null, "ids", null, 0, -1,
         OsTxCommitIdEqualsClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxCommitIdIsNullClauseEClass, OsTxCommitIdIsNullClause.class, "OsTxCommitIdIsNullClause",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osTxIdCriteriaEClass, OsTxIdCriteria.class, "OsTxIdCriteria", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsTxIdCriteria_Clause(), this.getOsTxIdClause(), null, "clause", null, 0, 1,
         OsTxIdCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxIdClauseEClass, OsTxIdClause.class, "OsTxIdClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osTxIdEqualsClauseEClass, OsTxIdEqualsClause.class, "OsTxIdEqualsClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTxIdEqualsClause_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsTxIdEqualsClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsTxIdEqualsClause_Ids(), this.getOsExpression(), null, "ids", null, 0, -1,
         OsTxIdEqualsClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxIdOpClauseEClass, OsTxIdOpClause.class, "OsTxIdOpClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTxIdOpClause_Op(), this.getOsNonEqualOperator(), "op", null, 0, 1, OsTxIdOpClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsTxIdOpClause_Id(), this.getOsExpression(), null, "id", null, 0, 1, OsTxIdOpClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osTxIdRangeClauseEClass, OsTxIdRangeClause.class, "OsTxIdRangeClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTxIdRangeClause_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsTxIdRangeClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsTxIdRangeClause_FromId(), this.getOsExpression(), null, "fromId", null, 0, 1,
         OsTxIdRangeClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsTxIdRangeClause_ToId(), this.getOsExpression(), null, "toId", null, 0, 1,
         OsTxIdRangeClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxTimestampCriteriaEClass, OsTxTimestampCriteria.class, "OsTxTimestampCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsTxTimestampCriteria_Clause(), this.getOsTxTimestampClause(), null, "clause", null, 0, 1,
         OsTxTimestampCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxTimestampClauseEClass, OsTxTimestampClause.class, "OsTxTimestampClause", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osTxTimestampOpClauseEClass, OsTxTimestampOpClause.class, "OsTxTimestampOpClause", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTxTimestampOpClause_Op(), this.getOsOperator(), "op", null, 0, 1, OsTxTimestampOpClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsTxTimestampOpClause_Timestamp(), this.getOsExpression(), null, "timestamp", null, 0, 1,
         OsTxTimestampOpClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTxTimestampRangeClauseEClass, OsTxTimestampRangeClause.class, "OsTxTimestampRangeClause",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTxTimestampRangeClause_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsTxTimestampRangeClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsTxTimestampRangeClause_From(), this.getOsExpression(), null, "from", null, 0, 1,
         OsTxTimestampRangeClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsTxTimestampRangeClause_To(), this.getOsExpression(), null, "to", null, 0, 1,
         OsTxTimestampRangeClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osArtifactQueryStatementEClass, OsArtifactQueryStatement.class, "OsArtifactQueryStatement",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsArtifactQueryStatement_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsArtifactQueryStatement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsArtifactQueryStatement_Data(), this.getOsArtifactQuery(), null, "data", null, 0, 1,
         OsArtifactQueryStatement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsArtifactQueryStatement_Collect(), this.getOsCollectClause(), null, "collect", null, 0, 1,
         OsArtifactQueryStatement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osArtifactQueryEClass, OsArtifactQuery.class, "OsArtifactQuery", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsArtifactQuery_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsArtifactQuery.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osArtifactQueryAllEClass, OsArtifactQueryAll.class, "OsArtifactQueryAll", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osArtifactQueryByPredicateEClass, OsArtifactQueryByPredicate.class, "OsArtifactQueryByPredicate",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsArtifactQueryByPredicate_Criteria(), this.getOsItemCriteria(), null, "criteria", null, 0, -1,
         OsArtifactQueryByPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
         !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osItemCriteriaEClass, OsItemCriteria.class, "OsItemCriteria", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osArtifactCriteriaEClass, OsArtifactCriteria.class, "OsArtifactCriteria", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osArtifactIdCriteriaEClass, OsArtifactIdCriteria.class, "OsArtifactIdCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsArtifactIdCriteria_Ids(), this.getOsExpression(), null, "ids", null, 0, -1,
         OsArtifactIdCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osArtifactGuidCriteriaEClass, OsArtifactGuidCriteria.class, "OsArtifactGuidCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsArtifactGuidCriteria_Ids(), this.getOsExpression(), null, "ids", null, 0, -1,
         OsArtifactGuidCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osArtifactTypeCriteriaEClass, OsArtifactTypeCriteria.class, "OsArtifactTypeCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsArtifactTypeCriteria_Clause(), this.getOsArtifactTypeClause(), null, "clause", null, 0, 1,
         OsArtifactTypeCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osArtifactTypeClauseEClass, OsArtifactTypeClause.class, "OsArtifactTypeClause", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsArtifactTypeClause_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsArtifactTypeClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsArtifactTypeClause_Types(), this.getOsExpression(), null, "types", null, 0, -1,
         OsArtifactTypeClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osArtifactTypeEqualsClauseEClass, OsArtifactTypeEqualsClause.class, "OsArtifactTypeEqualsClause",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osArtifactTypeInstanceOfClauseEClass, OsArtifactTypeInstanceOfClause.class,
         "OsArtifactTypeInstanceOfClause", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osAttributeCriteriaEClass, OsAttributeCriteria.class, "OsAttributeCriteria", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsAttributeCriteria_Clause(), this.getOsAttributeClause(), null, "clause", null, 0, 1,
         OsAttributeCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osAttributeClauseEClass, OsAttributeClause.class, "OsAttributeClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsAttributeClause_Types(), this.getOsExpression(), null, "types", null, 0, -1,
         OsAttributeClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEAttribute(getOsAttributeClause_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsAttributeClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osAttributeExistClauseEClass, OsAttributeExistClause.class, "OsAttributeExistClause", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(oseAttributeOpClauseEClass, OseAttributeOpClause.class, "OseAttributeOpClause", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOseAttributeOpClause_Options(), this.getOsQueryOption(), "options", null, 0, -1,
         OseAttributeOpClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOseAttributeOpClause_Values(), this.getOsExpression(), null, "values", null, 0, -1,
         OseAttributeOpClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osRelationCriteriaEClass, OsRelationCriteria.class, "OsRelationCriteria", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsRelationCriteria_Clause(), this.getOsRelationClause(), null, "clause", null, 0, 1,
         OsRelationCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osRelationClauseEClass, OsRelationClause.class, "OsRelationClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsRelationClause_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsRelationClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsRelationClause_Type(), this.getOsExpression(), null, "type", null, 0, 1,
         OsRelationClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEAttribute(getOsRelationClause_Side(), this.getOsRelationSide(), "side", null, 0, 1, OsRelationClause.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osRelationExistClauseEClass, OsRelationExistClause.class, "OsRelationExistClause", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsRelationExistClause_Op(), this.getOsExistenceOperator(), "op", null, 0, 1,
         OsRelationExistClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osRelatedToClauseEClass, OsRelatedToClause.class, "OsRelatedToClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsRelatedToClause_Ids(), this.getOsExpression(), null, "ids", null, 0, -1,
         OsRelatedToClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osFollowClauseEClass, OsFollowClause.class, "OsFollowClause", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsFollowClause_Stmt(), this.getOsFollowStatement(), null, "stmt", null, 0, 1,
         OsFollowClause.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osFollowStatementEClass, OsFollowStatement.class, "OsFollowStatement", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osFollowRelationTypeEClass, OsFollowRelationType.class, "OsFollowRelationType", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsFollowRelationType_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsFollowRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsFollowRelationType_Type(), this.getOsExpression(), null, "type", null, 0, 1,
         OsFollowRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEAttribute(getOsFollowRelationType_Side(), this.getOsRelationSide(), "side", null, 0, 1,
         OsFollowRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsFollowRelationType_Criteria(), this.getOsItemCriteria(), null, "criteria", null, 0, -1,
         OsFollowRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsFollowRelationType_Collect(), this.getOsCollectClause(), null, "collect", null, 0, 1,
         OsFollowRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osVariableDeclarationEClass, OsVariableDeclaration.class, "OsVariableDeclaration", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsVariableDeclaration_Elements(), this.getOsExpression(), null, "elements", null, 0, -1,
         OsVariableDeclaration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osVariableEClass, OsVariable.class, "OsVariable", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsVariable_Name(), ecorePackage.getEString(), "name", null, 0, 1, OsVariable.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsVariable_Right(), this.getOsExpression(), null, "right", null, 0, 1, OsVariable.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osAssignmentEClass, OsAssignment.class, "OsAssignment", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsAssignment_Right(), this.getOsExpression(), null, "right", null, 0, 1, OsAssignment.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osBooleanLiteralEClass, OsBooleanLiteral.class, "OsBooleanLiteral", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsBooleanLiteral_IsTrue(), ecorePackage.getEBoolean(), "isTrue", null, 0, 1,
         OsBooleanLiteral.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osNullLiteralEClass, OsNullLiteral.class, "OsNullLiteral", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);

      initEClass(osNumberLiteralEClass, OsNumberLiteral.class, "OsNumberLiteral", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsNumberLiteral_Value(), ecorePackage.getEString(), "value", null, 0, 1, OsNumberLiteral.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osStringLiteralEClass, OsStringLiteral.class, "OsStringLiteral", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsStringLiteral_Value(), ecorePackage.getEString(), "value", null, 0, 1, OsStringLiteral.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osTemplateLiteralEClass, OsTemplateLiteral.class, "OsTemplateLiteral", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsTemplateLiteral_Value(), ecorePackage.getEString(), "value", null, 0, 1,
         OsTemplateLiteral.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osVariableReferenceEClass, OsVariableReference.class, "OsVariableReference", !IS_ABSTRACT,
         !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsVariableReference_Ref(), this.getOsVariable(), null, "ref", null, 0, 1,
         OsVariableReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osDotExpressionEClass, OsDotExpression.class, "OsDotExpression", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsDotExpression_Ref(), this.getOsExpression(), null, "ref", null, 0, 1, OsDotExpression.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsDotExpression_Tail(), this.getOsExpression(), null, "tail", null, 0, 1, OsDotExpression.class,
         !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);

      initEClass(osQueryExpressionEClass, OsQueryExpression.class, "OsQueryExpression", !IS_ABSTRACT, !IS_INTERFACE,
         IS_GENERATED_INSTANCE_CLASS);
      initEAttribute(getOsQueryExpression_Name(), ecorePackage.getEString(), "name", null, 0, 1,
         OsQueryExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
         !IS_DERIVED, IS_ORDERED);
      initEReference(getOsQueryExpression_Query(), this.getOsQuery(), null, "query", null, 0, 1,
         OsQueryExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsQueryExpression_Clause(), this.getOsClause(), null, "clause", null, 0, -1,
         OsQueryExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osCollectObjectExpressionEClass, OsCollectObjectExpression.class, "OsCollectObjectExpression",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsCollectObjectExpression_Alias(), this.getOsExpression(), null, "alias", null, 0, 1,
         OsCollectObjectExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      initEReference(getOsCollectObjectExpression_Expressions(), this.getOsCollectExpression(), null, "expressions",
         null, 0, -1, OsCollectObjectExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
         !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      initEClass(osCollectAllFieldsExpressionEClass, OsCollectAllFieldsExpression.class, "OsCollectAllFieldsExpression",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

      initEClass(osCollectFieldExpressionEClass, OsCollectFieldExpression.class, "OsCollectFieldExpression",
         !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
      initEReference(getOsCollectFieldExpression_Alias(), this.getOsExpression(), null, "alias", null, 0, 1,
         OsCollectFieldExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
         !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

      // Initialize enums and add enum literals
      initEEnum(osBranchStateEEnum, OsBranchState.class, "OsBranchState");
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.CREATED);
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.MODIFIED);
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.COMMITTED);
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.REBASELINED);
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.DELETED);
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.REBASELINE_IN_PROGRESS);
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.COMMIT_IN_PROGRESS);
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.CREATION_IN_PROGRESS);
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.DELETE_IN_PROGRESS);
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.PURGE_IN_PROGRESS);
      addEEnumLiteral(osBranchStateEEnum, OsBranchState.PURGED);

      initEEnum(osBranchTypeEEnum, OsBranchType.class, "OsBranchType");
      addEEnumLiteral(osBranchTypeEEnum, OsBranchType.WORKING);
      addEEnumLiteral(osBranchTypeEEnum, OsBranchType.BASELINE);
      addEEnumLiteral(osBranchTypeEEnum, OsBranchType.MERGE);
      addEEnumLiteral(osBranchTypeEEnum, OsBranchType.SYSTEM_ROOT);
      addEEnumLiteral(osBranchTypeEEnum, OsBranchType.PORT);

      initEEnum(osBranchArchiveFilterEEnum, OsBranchArchiveFilter.class, "OsBranchArchiveFilter");
      addEEnumLiteral(osBranchArchiveFilterEEnum, OsBranchArchiveFilter.ARCHIVED_EXCLUDED);
      addEEnumLiteral(osBranchArchiveFilterEEnum, OsBranchArchiveFilter.ARCHIVED_INCLUDED);

      initEEnum(osTxTypeEEnum, OsTxType.class, "OsTxType");
      addEEnumLiteral(osTxTypeEEnum, OsTxType.BASELINE);
      addEEnumLiteral(osTxTypeEEnum, OsTxType.NON_BASELINE);

      initEEnum(osRelationSideEEnum, OsRelationSide.class, "OsRelationSide");
      addEEnumLiteral(osRelationSideEEnum, OsRelationSide.SIDE_A);
      addEEnumLiteral(osRelationSideEEnum, OsRelationSide.SIDE_B);

      initEEnum(osOperatorEEnum, OsOperator.class, "OsOperator");
      addEEnumLiteral(osOperatorEEnum, OsOperator.EQUAL);
      addEEnumLiteral(osOperatorEEnum, OsOperator.NOT_EQUAL);
      addEEnumLiteral(osOperatorEEnum, OsOperator.LESS_THAN);
      addEEnumLiteral(osOperatorEEnum, OsOperator.LESS_THAN_EQ);
      addEEnumLiteral(osOperatorEEnum, OsOperator.GREATER_THAN);
      addEEnumLiteral(osOperatorEEnum, OsOperator.GREATER_THAN_EQ);

      initEEnum(osNonEqualOperatorEEnum, OsNonEqualOperator.class, "OsNonEqualOperator");
      addEEnumLiteral(osNonEqualOperatorEEnum, OsNonEqualOperator.NOT_EQUAL);
      addEEnumLiteral(osNonEqualOperatorEEnum, OsNonEqualOperator.LESS_THAN);
      addEEnumLiteral(osNonEqualOperatorEEnum, OsNonEqualOperator.LESS_THAN_EQ);
      addEEnumLiteral(osNonEqualOperatorEEnum, OsNonEqualOperator.GREATER_THAN);
      addEEnumLiteral(osNonEqualOperatorEEnum, OsNonEqualOperator.GREATER_THAN_EQ);

      initEEnum(osExistenceOperatorEEnum, OsExistenceOperator.class, "OsExistenceOperator");
      addEEnumLiteral(osExistenceOperatorEEnum, OsExistenceOperator.EXISTS);
      addEEnumLiteral(osExistenceOperatorEEnum, OsExistenceOperator.NOT_EXISTS);

      initEEnum(osQueryOptionEEnum, OsQueryOption.class, "OsQueryOption");
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.CONTAINS);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.CASE_MATCH);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.CASE_IGNORE);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.EXISTANCE_EXISTS);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.EXISTANCE_NOT_EXISTS);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.TOKEN_COUNT_MATCH);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.TOKEN_COUNT_IGNORE);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.TOKEN_DELIMITER_EXACT);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.TOKEN_DELIMITER_WHITESPACE);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.TOKEN_DELIMITER_ANY);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.TOKEN_MATCH_ORDER_ANY);
      addEEnumLiteral(osQueryOptionEEnum, OsQueryOption.TOKEN_MATCH_ORDER_MATCH);

      // Create resource
      createResource(eNS_URI);
   }

} //OrcsScriptDslPackageImpl
