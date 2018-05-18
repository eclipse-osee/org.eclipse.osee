/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AddAttribute;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeOverrideOption;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp;
import org.eclipse.osee.framework.core.dsl.oseeDsl.CompoundCondition;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Condition;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Import;
import org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeElement;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactPredicate;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeMatch;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypePredicate;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveAttribute;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Role;
import org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition;
import org.eclipse.osee.framework.core.dsl.oseeDsl.UpdateAttribute;
import org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XLogicOperator;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumEntry;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class OseeDslPackageImpl extends EPackageImpl implements OseeDslPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass oseeDslEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass importEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass oseeElementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass oseeTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xArtifactTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xAttributeTypeRefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xAttributeTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xOseeEnumTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xOseeEnumEntryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xOseeEnumOverrideEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass overrideOptionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass addEnumEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass removeEnumEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xOseeArtifactTypeOverrideEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeOverrideOptionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass addAttributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass removeAttributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass updateAttributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xRelationTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conditionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass simpleConditionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass compoundConditionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xArtifactMatcherEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass roleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass referencedContextEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass usersAndGroupsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass accessContextEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass hierarchyRestrictionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass relationTypeArtifactTypePredicateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass relationTypeArtifactPredicateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass relationTypePredicateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass objectRestrictionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass artifactMatchRestrictionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass artifactTypeRestrictionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeTypeRestrictionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass legacyRelationTypeRestrictionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass relationTypeRestrictionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum relationMultiplicityEnumEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum compareOpEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum xLogicOperatorEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum matchFieldEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum accessPermissionEnumEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum relationTypeMatchEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum xRelationSideEnumEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private OseeDslPackageImpl()
  {
    super(eNS_URI, OseeDslFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link OseeDslPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static OseeDslPackage init()
  {
    if (isInited) return (OseeDslPackage)EPackage.Registry.INSTANCE.getEPackage(OseeDslPackage.eNS_URI);

    // Obtain or create and register package
    OseeDslPackageImpl theOseeDslPackage = (OseeDslPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof OseeDslPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new OseeDslPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theOseeDslPackage.createPackageContents();

    // Initialize created meta-data
    theOseeDslPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theOseeDslPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(OseeDslPackage.eNS_URI, theOseeDslPackage);
    return theOseeDslPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOseeDsl()
  {
    return oseeDslEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_Imports()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_ArtifactTypes()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_RelationTypes()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_AttributeTypes()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_EnumTypes()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_EnumOverrides()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_ArtifactTypeOverrides()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_ArtifactMatchRefs()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_AccessDeclarations()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_RoleDeclarations()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getImport()
  {
    return importEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getImport_ImportURI()
  {
    return (EAttribute)importEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOseeElement()
  {
    return oseeElementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOseeType()
  {
    return oseeTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getOseeType_Name()
  {
    return (EAttribute)oseeTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getOseeType_Id()
  {
    return (EAttribute)oseeTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXArtifactType()
  {
    return xArtifactTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXArtifactType_Abstract()
  {
    return (EAttribute)xArtifactTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXArtifactType_SuperArtifactTypes()
  {
    return (EReference)xArtifactTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXArtifactType_ValidAttributeTypes()
  {
    return (EReference)xArtifactTypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXAttributeTypeRef()
  {
    return xAttributeTypeRefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXAttributeTypeRef_ValidAttributeType()
  {
    return (EReference)xAttributeTypeRefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXAttributeTypeRef_BranchUuid()
  {
    return (EAttribute)xAttributeTypeRefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXAttributeType()
  {
    return xAttributeTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXAttributeType_BaseAttributeType()
  {
    return (EAttribute)xAttributeTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXAttributeType_Override()
  {
    return (EReference)xAttributeTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXAttributeType_DataProvider()
  {
    return (EAttribute)xAttributeTypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXAttributeType_Min()
  {
    return (EAttribute)xAttributeTypeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXAttributeType_Max()
  {
    return (EAttribute)xAttributeTypeEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXAttributeType_TaggerId()
  {
    return (EAttribute)xAttributeTypeEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXAttributeType_EnumType()
  {
    return (EReference)xAttributeTypeEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXAttributeType_Description()
  {
    return (EAttribute)xAttributeTypeEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXAttributeType_DefaultValue()
  {
    return (EAttribute)xAttributeTypeEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXAttributeType_FileExtension()
  {
    return (EAttribute)xAttributeTypeEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXAttributeType_MediaType()
  {
    return (EAttribute)xAttributeTypeEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXOseeEnumType()
  {
    return xOseeEnumTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXOseeEnumType_EnumEntries()
  {
    return (EReference)xOseeEnumTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXOseeEnumEntry()
  {
    return xOseeEnumEntryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXOseeEnumEntry_Name()
  {
    return (EAttribute)xOseeEnumEntryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXOseeEnumEntry_Ordinal()
  {
    return (EAttribute)xOseeEnumEntryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXOseeEnumEntry_Description()
  {
    return (EAttribute)xOseeEnumEntryEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXOseeEnumOverride()
  {
    return xOseeEnumOverrideEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXOseeEnumOverride_OverridenEnumType()
  {
    return (EReference)xOseeEnumOverrideEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXOseeEnumOverride_InheritAll()
  {
    return (EAttribute)xOseeEnumOverrideEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXOseeEnumOverride_OverrideOptions()
  {
    return (EReference)xOseeEnumOverrideEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOverrideOption()
  {
    return overrideOptionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAddEnum()
  {
    return addEnumEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAddEnum_EnumEntry()
  {
    return (EAttribute)addEnumEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAddEnum_Ordinal()
  {
    return (EAttribute)addEnumEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAddEnum_Description()
  {
    return (EAttribute)addEnumEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRemoveEnum()
  {
    return removeEnumEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRemoveEnum_EnumEntry()
  {
    return (EReference)removeEnumEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXOseeArtifactTypeOverride()
  {
    return xOseeArtifactTypeOverrideEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXOseeArtifactTypeOverride_OverridenArtifactType()
  {
    return (EReference)xOseeArtifactTypeOverrideEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXOseeArtifactTypeOverride_InheritAll()
  {
    return (EAttribute)xOseeArtifactTypeOverrideEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXOseeArtifactTypeOverride_OverrideOptions()
  {
    return (EReference)xOseeArtifactTypeOverrideEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeOverrideOption()
  {
    return attributeOverrideOptionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAddAttribute()
  {
    return addAttributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAddAttribute_Attribute()
  {
    return (EReference)addAttributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRemoveAttribute()
  {
    return removeAttributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRemoveAttribute_Attribute()
  {
    return (EReference)removeAttributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUpdateAttribute()
  {
    return updateAttributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getUpdateAttribute_Attribute()
  {
    return (EReference)updateAttributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXRelationType()
  {
    return xRelationTypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXRelationType_SideAName()
  {
    return (EAttribute)xRelationTypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXRelationType_SideAArtifactType()
  {
    return (EReference)xRelationTypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXRelationType_SideBName()
  {
    return (EAttribute)xRelationTypeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXRelationType_SideBArtifactType()
  {
    return (EReference)xRelationTypeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXRelationType_DefaultOrderType()
  {
    return (EAttribute)xRelationTypeEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXRelationType_Multiplicity()
  {
    return (EAttribute)xRelationTypeEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCondition()
  {
    return conditionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSimpleCondition()
  {
    return simpleConditionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSimpleCondition_Field()
  {
    return (EAttribute)simpleConditionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSimpleCondition_Op()
  {
    return (EAttribute)simpleConditionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSimpleCondition_Expression()
  {
    return (EAttribute)simpleConditionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCompoundCondition()
  {
    return compoundConditionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCompoundCondition_Conditions()
  {
    return (EReference)compoundConditionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCompoundCondition_Operators()
  {
    return (EAttribute)compoundConditionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXArtifactMatcher()
  {
    return xArtifactMatcherEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXArtifactMatcher_Name()
  {
    return (EAttribute)xArtifactMatcherEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getXArtifactMatcher_Conditions()
  {
    return (EReference)xArtifactMatcherEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXArtifactMatcher_Operators()
  {
    return (EAttribute)xArtifactMatcherEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRole()
  {
    return roleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRole_Name()
  {
    return (EAttribute)roleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRole_SuperRoles()
  {
    return (EReference)roleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRole_UsersAndGroups()
  {
    return (EReference)roleEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRole_ReferencedContexts()
  {
    return (EReference)roleEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getReferencedContext()
  {
    return referencedContextEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getReferencedContext_AccessContextRef()
  {
    return (EAttribute)referencedContextEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUsersAndGroups()
  {
    return usersAndGroupsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getUsersAndGroups_UserOrGroupId()
  {
    return (EAttribute)usersAndGroupsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAccessContext()
  {
    return accessContextEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAccessContext_Name()
  {
    return (EAttribute)accessContextEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAccessContext_SuperAccessContexts()
  {
    return (EReference)accessContextEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAccessContext_Id()
  {
    return (EAttribute)accessContextEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAccessContext_AccessRules()
  {
    return (EReference)accessContextEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAccessContext_HierarchyRestrictions()
  {
    return (EReference)accessContextEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getHierarchyRestriction()
  {
    return hierarchyRestrictionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getHierarchyRestriction_ArtifactMatcherRef()
  {
    return (EReference)hierarchyRestrictionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getHierarchyRestriction_AccessRules()
  {
    return (EReference)hierarchyRestrictionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRelationTypeArtifactTypePredicate()
  {
    return relationTypeArtifactTypePredicateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelationTypeArtifactTypePredicate_ArtifactTypeRef()
  {
    return (EReference)relationTypeArtifactTypePredicateEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRelationTypeArtifactPredicate()
  {
    return relationTypeArtifactPredicateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelationTypeArtifactPredicate_ArtifactMatcherRef()
  {
    return (EReference)relationTypeArtifactPredicateEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRelationTypePredicate()
  {
    return relationTypePredicateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getObjectRestriction()
  {
    return objectRestrictionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getObjectRestriction_Permission()
  {
    return (EAttribute)objectRestrictionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getArtifactMatchRestriction()
  {
    return artifactMatchRestrictionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getArtifactMatchRestriction_ArtifactMatcherRef()
  {
    return (EReference)artifactMatchRestrictionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getArtifactTypeRestriction()
  {
    return artifactTypeRestrictionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getArtifactTypeRestriction_ArtifactTypeRef()
  {
    return (EReference)artifactTypeRestrictionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeTypeRestriction()
  {
    return attributeTypeRestrictionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeTypeRestriction_AttributeTypeRef()
  {
    return (EReference)attributeTypeRestrictionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeTypeRestriction_ArtifactTypeRef()
  {
    return (EReference)attributeTypeRestrictionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLegacyRelationTypeRestriction()
  {
    return legacyRelationTypeRestrictionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLegacyRelationTypeRestriction_Permission()
  {
    return (EAttribute)legacyRelationTypeRestrictionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLegacyRelationTypeRestriction_RelationTypeRef()
  {
    return (EReference)legacyRelationTypeRestrictionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLegacyRelationTypeRestriction_RestrictedToSide()
  {
    return (EAttribute)legacyRelationTypeRestrictionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLegacyRelationTypeRestriction_ArtifactMatcherRef()
  {
    return (EReference)legacyRelationTypeRestrictionEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRelationTypeRestriction()
  {
    return relationTypeRestrictionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRelationTypeRestriction_RelationTypeMatch()
  {
    return (EAttribute)relationTypeRestrictionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelationTypeRestriction_RelationTypeRef()
  {
    return (EReference)relationTypeRestrictionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRelationTypeRestriction_RestrictedToSide()
  {
    return (EAttribute)relationTypeRestrictionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelationTypeRestriction_Predicate()
  {
    return (EReference)relationTypeRestrictionEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getRelationMultiplicityEnum()
  {
    return relationMultiplicityEnumEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getCompareOp()
  {
    return compareOpEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getXLogicOperator()
  {
    return xLogicOperatorEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getMatchField()
  {
    return matchFieldEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getAccessPermissionEnum()
  {
    return accessPermissionEnumEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getRelationTypeMatch()
  {
    return relationTypeMatchEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getXRelationSideEnum()
  {
    return xRelationSideEnumEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeDslFactory getOseeDslFactory()
  {
    return (OseeDslFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    oseeDslEClass = createEClass(OSEE_DSL);
    createEReference(oseeDslEClass, OSEE_DSL__IMPORTS);
    createEReference(oseeDslEClass, OSEE_DSL__ARTIFACT_TYPES);
    createEReference(oseeDslEClass, OSEE_DSL__RELATION_TYPES);
    createEReference(oseeDslEClass, OSEE_DSL__ATTRIBUTE_TYPES);
    createEReference(oseeDslEClass, OSEE_DSL__ENUM_TYPES);
    createEReference(oseeDslEClass, OSEE_DSL__ENUM_OVERRIDES);
    createEReference(oseeDslEClass, OSEE_DSL__ARTIFACT_TYPE_OVERRIDES);
    createEReference(oseeDslEClass, OSEE_DSL__ARTIFACT_MATCH_REFS);
    createEReference(oseeDslEClass, OSEE_DSL__ACCESS_DECLARATIONS);
    createEReference(oseeDslEClass, OSEE_DSL__ROLE_DECLARATIONS);

    importEClass = createEClass(IMPORT);
    createEAttribute(importEClass, IMPORT__IMPORT_URI);

    oseeElementEClass = createEClass(OSEE_ELEMENT);

    oseeTypeEClass = createEClass(OSEE_TYPE);
    createEAttribute(oseeTypeEClass, OSEE_TYPE__NAME);
    createEAttribute(oseeTypeEClass, OSEE_TYPE__ID);

    xArtifactTypeEClass = createEClass(XARTIFACT_TYPE);
    createEAttribute(xArtifactTypeEClass, XARTIFACT_TYPE__ABSTRACT);
    createEReference(xArtifactTypeEClass, XARTIFACT_TYPE__SUPER_ARTIFACT_TYPES);
    createEReference(xArtifactTypeEClass, XARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES);

    xAttributeTypeRefEClass = createEClass(XATTRIBUTE_TYPE_REF);
    createEReference(xAttributeTypeRefEClass, XATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE);
    createEAttribute(xAttributeTypeRefEClass, XATTRIBUTE_TYPE_REF__BRANCH_UUID);

    xAttributeTypeEClass = createEClass(XATTRIBUTE_TYPE);
    createEAttribute(xAttributeTypeEClass, XATTRIBUTE_TYPE__BASE_ATTRIBUTE_TYPE);
    createEReference(xAttributeTypeEClass, XATTRIBUTE_TYPE__OVERRIDE);
    createEAttribute(xAttributeTypeEClass, XATTRIBUTE_TYPE__DATA_PROVIDER);
    createEAttribute(xAttributeTypeEClass, XATTRIBUTE_TYPE__MIN);
    createEAttribute(xAttributeTypeEClass, XATTRIBUTE_TYPE__MAX);
    createEAttribute(xAttributeTypeEClass, XATTRIBUTE_TYPE__TAGGER_ID);
    createEReference(xAttributeTypeEClass, XATTRIBUTE_TYPE__ENUM_TYPE);
    createEAttribute(xAttributeTypeEClass, XATTRIBUTE_TYPE__DESCRIPTION);
    createEAttribute(xAttributeTypeEClass, XATTRIBUTE_TYPE__DEFAULT_VALUE);
    createEAttribute(xAttributeTypeEClass, XATTRIBUTE_TYPE__FILE_EXTENSION);
    createEAttribute(xAttributeTypeEClass, XATTRIBUTE_TYPE__MEDIA_TYPE);

    xOseeEnumTypeEClass = createEClass(XOSEE_ENUM_TYPE);
    createEReference(xOseeEnumTypeEClass, XOSEE_ENUM_TYPE__ENUM_ENTRIES);

    xOseeEnumEntryEClass = createEClass(XOSEE_ENUM_ENTRY);
    createEAttribute(xOseeEnumEntryEClass, XOSEE_ENUM_ENTRY__NAME);
    createEAttribute(xOseeEnumEntryEClass, XOSEE_ENUM_ENTRY__ORDINAL);
    createEAttribute(xOseeEnumEntryEClass, XOSEE_ENUM_ENTRY__DESCRIPTION);

    xOseeEnumOverrideEClass = createEClass(XOSEE_ENUM_OVERRIDE);
    createEReference(xOseeEnumOverrideEClass, XOSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE);
    createEAttribute(xOseeEnumOverrideEClass, XOSEE_ENUM_OVERRIDE__INHERIT_ALL);
    createEReference(xOseeEnumOverrideEClass, XOSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS);

    overrideOptionEClass = createEClass(OVERRIDE_OPTION);

    addEnumEClass = createEClass(ADD_ENUM);
    createEAttribute(addEnumEClass, ADD_ENUM__ENUM_ENTRY);
    createEAttribute(addEnumEClass, ADD_ENUM__ORDINAL);
    createEAttribute(addEnumEClass, ADD_ENUM__DESCRIPTION);

    removeEnumEClass = createEClass(REMOVE_ENUM);
    createEReference(removeEnumEClass, REMOVE_ENUM__ENUM_ENTRY);

    xOseeArtifactTypeOverrideEClass = createEClass(XOSEE_ARTIFACT_TYPE_OVERRIDE);
    createEReference(xOseeArtifactTypeOverrideEClass, XOSEE_ARTIFACT_TYPE_OVERRIDE__OVERRIDEN_ARTIFACT_TYPE);
    createEAttribute(xOseeArtifactTypeOverrideEClass, XOSEE_ARTIFACT_TYPE_OVERRIDE__INHERIT_ALL);
    createEReference(xOseeArtifactTypeOverrideEClass, XOSEE_ARTIFACT_TYPE_OVERRIDE__OVERRIDE_OPTIONS);

    attributeOverrideOptionEClass = createEClass(ATTRIBUTE_OVERRIDE_OPTION);

    addAttributeEClass = createEClass(ADD_ATTRIBUTE);
    createEReference(addAttributeEClass, ADD_ATTRIBUTE__ATTRIBUTE);

    removeAttributeEClass = createEClass(REMOVE_ATTRIBUTE);
    createEReference(removeAttributeEClass, REMOVE_ATTRIBUTE__ATTRIBUTE);

    updateAttributeEClass = createEClass(UPDATE_ATTRIBUTE);
    createEReference(updateAttributeEClass, UPDATE_ATTRIBUTE__ATTRIBUTE);

    xRelationTypeEClass = createEClass(XRELATION_TYPE);
    createEAttribute(xRelationTypeEClass, XRELATION_TYPE__SIDE_ANAME);
    createEReference(xRelationTypeEClass, XRELATION_TYPE__SIDE_AARTIFACT_TYPE);
    createEAttribute(xRelationTypeEClass, XRELATION_TYPE__SIDE_BNAME);
    createEReference(xRelationTypeEClass, XRELATION_TYPE__SIDE_BARTIFACT_TYPE);
    createEAttribute(xRelationTypeEClass, XRELATION_TYPE__DEFAULT_ORDER_TYPE);
    createEAttribute(xRelationTypeEClass, XRELATION_TYPE__MULTIPLICITY);

    conditionEClass = createEClass(CONDITION);

    simpleConditionEClass = createEClass(SIMPLE_CONDITION);
    createEAttribute(simpleConditionEClass, SIMPLE_CONDITION__FIELD);
    createEAttribute(simpleConditionEClass, SIMPLE_CONDITION__OP);
    createEAttribute(simpleConditionEClass, SIMPLE_CONDITION__EXPRESSION);

    compoundConditionEClass = createEClass(COMPOUND_CONDITION);
    createEReference(compoundConditionEClass, COMPOUND_CONDITION__CONDITIONS);
    createEAttribute(compoundConditionEClass, COMPOUND_CONDITION__OPERATORS);

    xArtifactMatcherEClass = createEClass(XARTIFACT_MATCHER);
    createEAttribute(xArtifactMatcherEClass, XARTIFACT_MATCHER__NAME);
    createEReference(xArtifactMatcherEClass, XARTIFACT_MATCHER__CONDITIONS);
    createEAttribute(xArtifactMatcherEClass, XARTIFACT_MATCHER__OPERATORS);

    roleEClass = createEClass(ROLE);
    createEAttribute(roleEClass, ROLE__NAME);
    createEReference(roleEClass, ROLE__SUPER_ROLES);
    createEReference(roleEClass, ROLE__USERS_AND_GROUPS);
    createEReference(roleEClass, ROLE__REFERENCED_CONTEXTS);

    referencedContextEClass = createEClass(REFERENCED_CONTEXT);
    createEAttribute(referencedContextEClass, REFERENCED_CONTEXT__ACCESS_CONTEXT_REF);

    usersAndGroupsEClass = createEClass(USERS_AND_GROUPS);
    createEAttribute(usersAndGroupsEClass, USERS_AND_GROUPS__USER_OR_GROUP_ID);

    accessContextEClass = createEClass(ACCESS_CONTEXT);
    createEAttribute(accessContextEClass, ACCESS_CONTEXT__NAME);
    createEReference(accessContextEClass, ACCESS_CONTEXT__SUPER_ACCESS_CONTEXTS);
    createEAttribute(accessContextEClass, ACCESS_CONTEXT__ID);
    createEReference(accessContextEClass, ACCESS_CONTEXT__ACCESS_RULES);
    createEReference(accessContextEClass, ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS);

    hierarchyRestrictionEClass = createEClass(HIERARCHY_RESTRICTION);
    createEReference(hierarchyRestrictionEClass, HIERARCHY_RESTRICTION__ARTIFACT_MATCHER_REF);
    createEReference(hierarchyRestrictionEClass, HIERARCHY_RESTRICTION__ACCESS_RULES);

    relationTypeArtifactTypePredicateEClass = createEClass(RELATION_TYPE_ARTIFACT_TYPE_PREDICATE);
    createEReference(relationTypeArtifactTypePredicateEClass, RELATION_TYPE_ARTIFACT_TYPE_PREDICATE__ARTIFACT_TYPE_REF);

    relationTypeArtifactPredicateEClass = createEClass(RELATION_TYPE_ARTIFACT_PREDICATE);
    createEReference(relationTypeArtifactPredicateEClass, RELATION_TYPE_ARTIFACT_PREDICATE__ARTIFACT_MATCHER_REF);

    relationTypePredicateEClass = createEClass(RELATION_TYPE_PREDICATE);

    objectRestrictionEClass = createEClass(OBJECT_RESTRICTION);
    createEAttribute(objectRestrictionEClass, OBJECT_RESTRICTION__PERMISSION);

    artifactMatchRestrictionEClass = createEClass(ARTIFACT_MATCH_RESTRICTION);
    createEReference(artifactMatchRestrictionEClass, ARTIFACT_MATCH_RESTRICTION__ARTIFACT_MATCHER_REF);

    artifactTypeRestrictionEClass = createEClass(ARTIFACT_TYPE_RESTRICTION);
    createEReference(artifactTypeRestrictionEClass, ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE_REF);

    attributeTypeRestrictionEClass = createEClass(ATTRIBUTE_TYPE_RESTRICTION);
    createEReference(attributeTypeRestrictionEClass, ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE_REF);
    createEReference(attributeTypeRestrictionEClass, ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF);

    legacyRelationTypeRestrictionEClass = createEClass(LEGACY_RELATION_TYPE_RESTRICTION);
    createEAttribute(legacyRelationTypeRestrictionEClass, LEGACY_RELATION_TYPE_RESTRICTION__PERMISSION);
    createEReference(legacyRelationTypeRestrictionEClass, LEGACY_RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF);
    createEAttribute(legacyRelationTypeRestrictionEClass, LEGACY_RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE);
    createEReference(legacyRelationTypeRestrictionEClass, LEGACY_RELATION_TYPE_RESTRICTION__ARTIFACT_MATCHER_REF);

    relationTypeRestrictionEClass = createEClass(RELATION_TYPE_RESTRICTION);
    createEAttribute(relationTypeRestrictionEClass, RELATION_TYPE_RESTRICTION__RELATION_TYPE_MATCH);
    createEReference(relationTypeRestrictionEClass, RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF);
    createEAttribute(relationTypeRestrictionEClass, RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE);
    createEReference(relationTypeRestrictionEClass, RELATION_TYPE_RESTRICTION__PREDICATE);

    // Create enums
    relationMultiplicityEnumEEnum = createEEnum(RELATION_MULTIPLICITY_ENUM);
    compareOpEEnum = createEEnum(COMPARE_OP);
    xLogicOperatorEEnum = createEEnum(XLOGIC_OPERATOR);
    matchFieldEEnum = createEEnum(MATCH_FIELD);
    accessPermissionEnumEEnum = createEEnum(ACCESS_PERMISSION_ENUM);
    relationTypeMatchEEnum = createEEnum(RELATION_TYPE_MATCH);
    xRelationSideEnumEEnum = createEEnum(XRELATION_SIDE_ENUM);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    oseeTypeEClass.getESuperTypes().add(this.getOseeElement());
    xArtifactTypeEClass.getESuperTypes().add(this.getOseeType());
    xAttributeTypeEClass.getESuperTypes().add(this.getOseeType());
    xOseeEnumTypeEClass.getESuperTypes().add(this.getOseeType());
    xOseeEnumOverrideEClass.getESuperTypes().add(this.getOseeElement());
    addEnumEClass.getESuperTypes().add(this.getOverrideOption());
    removeEnumEClass.getESuperTypes().add(this.getOverrideOption());
    addAttributeEClass.getESuperTypes().add(this.getAttributeOverrideOption());
    removeAttributeEClass.getESuperTypes().add(this.getAttributeOverrideOption());
    updateAttributeEClass.getESuperTypes().add(this.getAttributeOverrideOption());
    xRelationTypeEClass.getESuperTypes().add(this.getOseeType());
    simpleConditionEClass.getESuperTypes().add(this.getCondition());
    compoundConditionEClass.getESuperTypes().add(this.getCondition());
    relationTypeArtifactTypePredicateEClass.getESuperTypes().add(this.getRelationTypePredicate());
    relationTypeArtifactPredicateEClass.getESuperTypes().add(this.getRelationTypePredicate());
    artifactMatchRestrictionEClass.getESuperTypes().add(this.getObjectRestriction());
    artifactTypeRestrictionEClass.getESuperTypes().add(this.getObjectRestriction());
    attributeTypeRestrictionEClass.getESuperTypes().add(this.getObjectRestriction());
    relationTypeRestrictionEClass.getESuperTypes().add(this.getObjectRestriction());

    // Initialize classes and features; add operations and parameters
    initEClass(oseeDslEClass, OseeDsl.class, "OseeDsl", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOseeDsl_Imports(), this.getImport(), null, "imports", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_ArtifactTypes(), this.getXArtifactType(), null, "artifactTypes", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_RelationTypes(), this.getXRelationType(), null, "relationTypes", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_AttributeTypes(), this.getXAttributeType(), null, "attributeTypes", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_EnumTypes(), this.getXOseeEnumType(), null, "enumTypes", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_EnumOverrides(), this.getXOseeEnumOverride(), null, "enumOverrides", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_ArtifactTypeOverrides(), this.getXOseeArtifactTypeOverride(), null, "artifactTypeOverrides", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_ArtifactMatchRefs(), this.getXArtifactMatcher(), null, "artifactMatchRefs", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_AccessDeclarations(), this.getAccessContext(), null, "accessDeclarations", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_RoleDeclarations(), this.getRole(), null, "roleDeclarations", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(importEClass, Import.class, "Import", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getImport_ImportURI(), ecorePackage.getEString(), "importURI", null, 0, 1, Import.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(oseeElementEClass, OseeElement.class, "OseeElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(oseeTypeEClass, OseeType.class, "OseeType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getOseeType_Name(), ecorePackage.getEString(), "name", null, 0, 1, OseeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOseeType_Id(), ecorePackage.getEString(), "id", null, 0, 1, OseeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xArtifactTypeEClass, XArtifactType.class, "XArtifactType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXArtifactType_Abstract(), ecorePackage.getEBoolean(), "abstract", null, 0, 1, XArtifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXArtifactType_SuperArtifactTypes(), this.getXArtifactType(), null, "superArtifactTypes", null, 0, -1, XArtifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXArtifactType_ValidAttributeTypes(), this.getXAttributeTypeRef(), null, "validAttributeTypes", null, 0, -1, XArtifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xAttributeTypeRefEClass, XAttributeTypeRef.class, "XAttributeTypeRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getXAttributeTypeRef_ValidAttributeType(), this.getXAttributeType(), null, "validAttributeType", null, 0, 1, XAttributeTypeRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXAttributeTypeRef_BranchUuid(), ecorePackage.getEString(), "branchUuid", null, 0, 1, XAttributeTypeRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xAttributeTypeEClass, XAttributeType.class, "XAttributeType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXAttributeType_BaseAttributeType(), ecorePackage.getEString(), "baseAttributeType", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXAttributeType_Override(), this.getXAttributeType(), null, "override", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXAttributeType_DataProvider(), ecorePackage.getEString(), "dataProvider", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXAttributeType_Min(), ecorePackage.getEString(), "min", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXAttributeType_Max(), ecorePackage.getEString(), "max", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXAttributeType_TaggerId(), ecorePackage.getEString(), "taggerId", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXAttributeType_EnumType(), this.getXOseeEnumType(), null, "enumType", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXAttributeType_Description(), ecorePackage.getEString(), "description", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXAttributeType_DefaultValue(), ecorePackage.getEString(), "defaultValue", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXAttributeType_FileExtension(), ecorePackage.getEString(), "fileExtension", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXAttributeType_MediaType(), ecorePackage.getEString(), "mediaType", null, 0, 1, XAttributeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xOseeEnumTypeEClass, XOseeEnumType.class, "XOseeEnumType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getXOseeEnumType_EnumEntries(), this.getXOseeEnumEntry(), null, "enumEntries", null, 0, -1, XOseeEnumType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xOseeEnumEntryEClass, XOseeEnumEntry.class, "XOseeEnumEntry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXOseeEnumEntry_Name(), ecorePackage.getEString(), "name", null, 0, 1, XOseeEnumEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXOseeEnumEntry_Ordinal(), ecorePackage.getEString(), "ordinal", null, 0, 1, XOseeEnumEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXOseeEnumEntry_Description(), ecorePackage.getEString(), "description", null, 0, 1, XOseeEnumEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xOseeEnumOverrideEClass, XOseeEnumOverride.class, "XOseeEnumOverride", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getXOseeEnumOverride_OverridenEnumType(), this.getXOseeEnumType(), null, "overridenEnumType", null, 0, 1, XOseeEnumOverride.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXOseeEnumOverride_InheritAll(), ecorePackage.getEBoolean(), "inheritAll", null, 0, 1, XOseeEnumOverride.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXOseeEnumOverride_OverrideOptions(), this.getOverrideOption(), null, "overrideOptions", null, 0, -1, XOseeEnumOverride.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(overrideOptionEClass, OverrideOption.class, "OverrideOption", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(addEnumEClass, AddEnum.class, "AddEnum", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAddEnum_EnumEntry(), ecorePackage.getEString(), "enumEntry", null, 0, 1, AddEnum.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAddEnum_Ordinal(), ecorePackage.getEString(), "ordinal", null, 0, 1, AddEnum.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAddEnum_Description(), ecorePackage.getEString(), "description", null, 0, 1, AddEnum.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(removeEnumEClass, RemoveEnum.class, "RemoveEnum", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRemoveEnum_EnumEntry(), this.getXOseeEnumEntry(), null, "enumEntry", null, 0, 1, RemoveEnum.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xOseeArtifactTypeOverrideEClass, XOseeArtifactTypeOverride.class, "XOseeArtifactTypeOverride", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getXOseeArtifactTypeOverride_OverridenArtifactType(), this.getXArtifactType(), null, "overridenArtifactType", null, 0, 1, XOseeArtifactTypeOverride.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXOseeArtifactTypeOverride_InheritAll(), ecorePackage.getEBoolean(), "inheritAll", null, 0, 1, XOseeArtifactTypeOverride.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXOseeArtifactTypeOverride_OverrideOptions(), this.getAttributeOverrideOption(), null, "overrideOptions", null, 0, -1, XOseeArtifactTypeOverride.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeOverrideOptionEClass, AttributeOverrideOption.class, "AttributeOverrideOption", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(addAttributeEClass, AddAttribute.class, "AddAttribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAddAttribute_Attribute(), this.getXAttributeTypeRef(), null, "attribute", null, 0, 1, AddAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(removeAttributeEClass, RemoveAttribute.class, "RemoveAttribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRemoveAttribute_Attribute(), this.getXAttributeType(), null, "attribute", null, 0, 1, RemoveAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(updateAttributeEClass, UpdateAttribute.class, "UpdateAttribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getUpdateAttribute_Attribute(), this.getXAttributeTypeRef(), null, "attribute", null, 0, 1, UpdateAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xRelationTypeEClass, XRelationType.class, "XRelationType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXRelationType_SideAName(), ecorePackage.getEString(), "sideAName", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXRelationType_SideAArtifactType(), this.getXArtifactType(), null, "sideAArtifactType", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXRelationType_SideBName(), ecorePackage.getEString(), "sideBName", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXRelationType_SideBArtifactType(), this.getXArtifactType(), null, "sideBArtifactType", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXRelationType_DefaultOrderType(), ecorePackage.getEString(), "defaultOrderType", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXRelationType_Multiplicity(), this.getRelationMultiplicityEnum(), "multiplicity", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conditionEClass, Condition.class, "Condition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(simpleConditionEClass, SimpleCondition.class, "SimpleCondition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSimpleCondition_Field(), this.getMatchField(), "field", null, 0, 1, SimpleCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSimpleCondition_Op(), this.getCompareOp(), "op", null, 0, 1, SimpleCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSimpleCondition_Expression(), ecorePackage.getEString(), "expression", null, 0, 1, SimpleCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(compoundConditionEClass, CompoundCondition.class, "CompoundCondition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCompoundCondition_Conditions(), this.getSimpleCondition(), null, "conditions", null, 0, -1, CompoundCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCompoundCondition_Operators(), this.getXLogicOperator(), "operators", null, 0, -1, CompoundCondition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xArtifactMatcherEClass, XArtifactMatcher.class, "XArtifactMatcher", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXArtifactMatcher_Name(), ecorePackage.getEString(), "name", null, 0, 1, XArtifactMatcher.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXArtifactMatcher_Conditions(), this.getCondition(), null, "conditions", null, 0, -1, XArtifactMatcher.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXArtifactMatcher_Operators(), this.getXLogicOperator(), "operators", null, 0, -1, XArtifactMatcher.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(roleEClass, Role.class, "Role", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getRole_Name(), ecorePackage.getEString(), "name", null, 0, 1, Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRole_SuperRoles(), this.getRole(), null, "superRoles", null, 0, -1, Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRole_UsersAndGroups(), this.getUsersAndGroups(), null, "usersAndGroups", null, 0, -1, Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRole_ReferencedContexts(), this.getReferencedContext(), null, "referencedContexts", null, 0, -1, Role.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(referencedContextEClass, ReferencedContext.class, "ReferencedContext", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getReferencedContext_AccessContextRef(), ecorePackage.getEString(), "accessContextRef", null, 0, 1, ReferencedContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(usersAndGroupsEClass, UsersAndGroups.class, "UsersAndGroups", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUsersAndGroups_UserOrGroupId(), ecorePackage.getEString(), "userOrGroupId", null, 0, 1, UsersAndGroups.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(accessContextEClass, AccessContext.class, "AccessContext", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAccessContext_Name(), ecorePackage.getEString(), "name", null, 0, 1, AccessContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAccessContext_SuperAccessContexts(), this.getAccessContext(), null, "superAccessContexts", null, 0, -1, AccessContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAccessContext_Id(), ecorePackage.getEString(), "id", null, 0, 1, AccessContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAccessContext_AccessRules(), this.getObjectRestriction(), null, "accessRules", null, 0, -1, AccessContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAccessContext_HierarchyRestrictions(), this.getHierarchyRestriction(), null, "hierarchyRestrictions", null, 0, -1, AccessContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(hierarchyRestrictionEClass, HierarchyRestriction.class, "HierarchyRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getHierarchyRestriction_ArtifactMatcherRef(), this.getXArtifactMatcher(), null, "artifactMatcherRef", null, 0, 1, HierarchyRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getHierarchyRestriction_AccessRules(), this.getObjectRestriction(), null, "accessRules", null, 0, -1, HierarchyRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(relationTypeArtifactTypePredicateEClass, RelationTypeArtifactTypePredicate.class, "RelationTypeArtifactTypePredicate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRelationTypeArtifactTypePredicate_ArtifactTypeRef(), this.getXArtifactType(), null, "artifactTypeRef", null, 0, 1, RelationTypeArtifactTypePredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(relationTypeArtifactPredicateEClass, RelationTypeArtifactPredicate.class, "RelationTypeArtifactPredicate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRelationTypeArtifactPredicate_ArtifactMatcherRef(), this.getXArtifactMatcher(), null, "artifactMatcherRef", null, 0, 1, RelationTypeArtifactPredicate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(relationTypePredicateEClass, RelationTypePredicate.class, "RelationTypePredicate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(objectRestrictionEClass, ObjectRestriction.class, "ObjectRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getObjectRestriction_Permission(), this.getAccessPermissionEnum(), "permission", null, 0, 1, ObjectRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(artifactMatchRestrictionEClass, ArtifactMatchRestriction.class, "ArtifactMatchRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getArtifactMatchRestriction_ArtifactMatcherRef(), this.getXArtifactMatcher(), null, "artifactMatcherRef", null, 0, 1, ArtifactMatchRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(artifactTypeRestrictionEClass, ArtifactTypeRestriction.class, "ArtifactTypeRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getArtifactTypeRestriction_ArtifactTypeRef(), this.getXArtifactType(), null, "artifactTypeRef", null, 0, 1, ArtifactTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeTypeRestrictionEClass, AttributeTypeRestriction.class, "AttributeTypeRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAttributeTypeRestriction_AttributeTypeRef(), this.getXAttributeType(), null, "attributeTypeRef", null, 0, 1, AttributeTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttributeTypeRestriction_ArtifactTypeRef(), this.getXArtifactType(), null, "artifactTypeRef", null, 0, 1, AttributeTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(legacyRelationTypeRestrictionEClass, LegacyRelationTypeRestriction.class, "LegacyRelationTypeRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getLegacyRelationTypeRestriction_Permission(), this.getAccessPermissionEnum(), "permission", null, 0, 1, LegacyRelationTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getLegacyRelationTypeRestriction_RelationTypeRef(), this.getXRelationType(), null, "relationTypeRef", null, 0, 1, LegacyRelationTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getLegacyRelationTypeRestriction_RestrictedToSide(), this.getXRelationSideEnum(), "restrictedToSide", null, 0, 1, LegacyRelationTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getLegacyRelationTypeRestriction_ArtifactMatcherRef(), this.getXArtifactMatcher(), null, "artifactMatcherRef", null, 0, 1, LegacyRelationTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(relationTypeRestrictionEClass, RelationTypeRestriction.class, "RelationTypeRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getRelationTypeRestriction_RelationTypeMatch(), ecorePackage.getEBoolean(), "relationTypeMatch", null, 0, 1, RelationTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRelationTypeRestriction_RelationTypeRef(), this.getXRelationType(), null, "relationTypeRef", null, 0, 1, RelationTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRelationTypeRestriction_RestrictedToSide(), this.getXRelationSideEnum(), "restrictedToSide", null, 0, 1, RelationTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRelationTypeRestriction_Predicate(), this.getRelationTypePredicate(), null, "predicate", null, 0, 1, RelationTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.class, "RelationMultiplicityEnum");
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.ONE_TO_ONE);
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.ONE_TO_MANY);
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.MANY_TO_ONE);
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.MANY_TO_MANY);

    initEEnum(compareOpEEnum, CompareOp.class, "CompareOp");
    addEEnumLiteral(compareOpEEnum, CompareOp.EQ);
    addEEnumLiteral(compareOpEEnum, CompareOp.LIKE);

    initEEnum(xLogicOperatorEEnum, XLogicOperator.class, "XLogicOperator");
    addEEnumLiteral(xLogicOperatorEEnum, XLogicOperator.AND);
    addEEnumLiteral(xLogicOperatorEEnum, XLogicOperator.OR);

    initEEnum(matchFieldEEnum, MatchField.class, "MatchField");
    addEEnumLiteral(matchFieldEEnum, MatchField.ARTIFACT_NAME);
    addEEnumLiteral(matchFieldEEnum, MatchField.ARTIFACT_ID);
    addEEnumLiteral(matchFieldEEnum, MatchField.BRANCH_NAME);
    addEEnumLiteral(matchFieldEEnum, MatchField.BRANCH_UUID);

    initEEnum(accessPermissionEnumEEnum, AccessPermissionEnum.class, "AccessPermissionEnum");
    addEEnumLiteral(accessPermissionEnumEEnum, AccessPermissionEnum.ALLOW);
    addEEnumLiteral(accessPermissionEnumEEnum, AccessPermissionEnum.DENY);

    initEEnum(relationTypeMatchEEnum, RelationTypeMatch.class, "RelationTypeMatch");
    addEEnumLiteral(relationTypeMatchEEnum, RelationTypeMatch.ALL);

    initEEnum(xRelationSideEnumEEnum, XRelationSideEnum.class, "XRelationSideEnum");
    addEEnumLiteral(xRelationSideEnumEEnum, XRelationSideEnum.SIDE_A);
    addEEnumLiteral(xRelationSideEnumEEnum, XRelationSideEnum.SIDE_B);
    addEEnumLiteral(xRelationSideEnumEEnum, XRelationSideEnum.BOTH);

    // Create resource
    createResource(eNS_URI);
  }

} //OseeDslPackageImpl
