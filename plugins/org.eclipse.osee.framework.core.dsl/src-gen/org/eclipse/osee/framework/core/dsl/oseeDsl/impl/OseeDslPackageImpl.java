/**
 * <copyright>
 * </copyright>
 *

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
import org.eclipse.osee.framework.core.dsl.oseeDsl.AddEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Import;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeElement;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OverrideOption;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RemoveEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XBranchRef;
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
  private EClass xRelationTypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xArtifactRefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass xBranchRefEClass = null;

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
  private EClass objectRestrictionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass artifactInstanceRestrictionEClass = null;

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
  private EEnum accessPermissionEnumEEnum = null;

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
  public EReference getOseeDsl_BranchRefs()
  {
    return (EReference)oseeDslEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOseeDsl_ArtifactRefs()
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
  public EAttribute getOseeType_TypeGuid()
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
  public EAttribute getXAttributeTypeRef_BranchGuid()
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
  public EAttribute getXOseeEnumEntry_EntryGuid()
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
  public EAttribute getAddEnum_EntryGuid()
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
  public EClass getXArtifactRef()
  {
    return xArtifactRefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXArtifactRef_Name()
  {
    return (EAttribute)xArtifactRefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXArtifactRef_Guid()
  {
    return (EAttribute)xArtifactRefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getXBranchRef()
  {
    return xBranchRefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXBranchRef_Name()
  {
    return (EAttribute)xBranchRefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getXBranchRef_Guid()
  {
    return (EAttribute)xBranchRefEClass.getEStructuralFeatures().get(1);
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
  public EAttribute getAccessContext_Guid()
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
  public EReference getHierarchyRestriction_Artifact()
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
  public EClass getArtifactInstanceRestriction()
  {
    return artifactInstanceRestrictionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getArtifactInstanceRestriction_ArtifactRef()
  {
    return (EReference)artifactInstanceRestrictionEClass.getEStructuralFeatures().get(0);
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
  public EClass getRelationTypeRestriction()
  {
    return relationTypeRestrictionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelationTypeRestriction_RelationTypeRef()
  {
    return (EReference)relationTypeRestrictionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRelationTypeRestriction_RestrictedToSide()
  {
    return (EAttribute)relationTypeRestrictionEClass.getEStructuralFeatures().get(1);
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
  public EEnum getAccessPermissionEnum()
  {
    return accessPermissionEnumEEnum;
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
    createEReference(oseeDslEClass, OSEE_DSL__BRANCH_REFS);
    createEReference(oseeDslEClass, OSEE_DSL__ARTIFACT_REFS);
    createEReference(oseeDslEClass, OSEE_DSL__ACCESS_DECLARATIONS);

    importEClass = createEClass(IMPORT);
    createEAttribute(importEClass, IMPORT__IMPORT_URI);

    oseeElementEClass = createEClass(OSEE_ELEMENT);

    oseeTypeEClass = createEClass(OSEE_TYPE);
    createEAttribute(oseeTypeEClass, OSEE_TYPE__NAME);
    createEAttribute(oseeTypeEClass, OSEE_TYPE__TYPE_GUID);

    xArtifactTypeEClass = createEClass(XARTIFACT_TYPE);
    createEAttribute(xArtifactTypeEClass, XARTIFACT_TYPE__ABSTRACT);
    createEReference(xArtifactTypeEClass, XARTIFACT_TYPE__SUPER_ARTIFACT_TYPES);
    createEReference(xArtifactTypeEClass, XARTIFACT_TYPE__VALID_ATTRIBUTE_TYPES);

    xAttributeTypeRefEClass = createEClass(XATTRIBUTE_TYPE_REF);
    createEReference(xAttributeTypeRefEClass, XATTRIBUTE_TYPE_REF__VALID_ATTRIBUTE_TYPE);
    createEAttribute(xAttributeTypeRefEClass, XATTRIBUTE_TYPE_REF__BRANCH_GUID);

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

    xOseeEnumTypeEClass = createEClass(XOSEE_ENUM_TYPE);
    createEReference(xOseeEnumTypeEClass, XOSEE_ENUM_TYPE__ENUM_ENTRIES);

    xOseeEnumEntryEClass = createEClass(XOSEE_ENUM_ENTRY);
    createEAttribute(xOseeEnumEntryEClass, XOSEE_ENUM_ENTRY__NAME);
    createEAttribute(xOseeEnumEntryEClass, XOSEE_ENUM_ENTRY__ORDINAL);
    createEAttribute(xOseeEnumEntryEClass, XOSEE_ENUM_ENTRY__ENTRY_GUID);

    xOseeEnumOverrideEClass = createEClass(XOSEE_ENUM_OVERRIDE);
    createEReference(xOseeEnumOverrideEClass, XOSEE_ENUM_OVERRIDE__OVERRIDEN_ENUM_TYPE);
    createEAttribute(xOseeEnumOverrideEClass, XOSEE_ENUM_OVERRIDE__INHERIT_ALL);
    createEReference(xOseeEnumOverrideEClass, XOSEE_ENUM_OVERRIDE__OVERRIDE_OPTIONS);

    overrideOptionEClass = createEClass(OVERRIDE_OPTION);

    addEnumEClass = createEClass(ADD_ENUM);
    createEAttribute(addEnumEClass, ADD_ENUM__ENUM_ENTRY);
    createEAttribute(addEnumEClass, ADD_ENUM__ORDINAL);
    createEAttribute(addEnumEClass, ADD_ENUM__ENTRY_GUID);

    removeEnumEClass = createEClass(REMOVE_ENUM);
    createEReference(removeEnumEClass, REMOVE_ENUM__ENUM_ENTRY);

    xRelationTypeEClass = createEClass(XRELATION_TYPE);
    createEAttribute(xRelationTypeEClass, XRELATION_TYPE__SIDE_ANAME);
    createEReference(xRelationTypeEClass, XRELATION_TYPE__SIDE_AARTIFACT_TYPE);
    createEAttribute(xRelationTypeEClass, XRELATION_TYPE__SIDE_BNAME);
    createEReference(xRelationTypeEClass, XRELATION_TYPE__SIDE_BARTIFACT_TYPE);
    createEAttribute(xRelationTypeEClass, XRELATION_TYPE__DEFAULT_ORDER_TYPE);
    createEAttribute(xRelationTypeEClass, XRELATION_TYPE__MULTIPLICITY);

    xArtifactRefEClass = createEClass(XARTIFACT_REF);
    createEAttribute(xArtifactRefEClass, XARTIFACT_REF__NAME);
    createEAttribute(xArtifactRefEClass, XARTIFACT_REF__GUID);

    xBranchRefEClass = createEClass(XBRANCH_REF);
    createEAttribute(xBranchRefEClass, XBRANCH_REF__NAME);
    createEAttribute(xBranchRefEClass, XBRANCH_REF__GUID);

    accessContextEClass = createEClass(ACCESS_CONTEXT);
    createEAttribute(accessContextEClass, ACCESS_CONTEXT__NAME);
    createEReference(accessContextEClass, ACCESS_CONTEXT__SUPER_ACCESS_CONTEXTS);
    createEAttribute(accessContextEClass, ACCESS_CONTEXT__GUID);
    createEReference(accessContextEClass, ACCESS_CONTEXT__ACCESS_RULES);
    createEReference(accessContextEClass, ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS);

    hierarchyRestrictionEClass = createEClass(HIERARCHY_RESTRICTION);
    createEReference(hierarchyRestrictionEClass, HIERARCHY_RESTRICTION__ARTIFACT);
    createEReference(hierarchyRestrictionEClass, HIERARCHY_RESTRICTION__ACCESS_RULES);

    objectRestrictionEClass = createEClass(OBJECT_RESTRICTION);
    createEAttribute(objectRestrictionEClass, OBJECT_RESTRICTION__PERMISSION);

    artifactInstanceRestrictionEClass = createEClass(ARTIFACT_INSTANCE_RESTRICTION);
    createEReference(artifactInstanceRestrictionEClass, ARTIFACT_INSTANCE_RESTRICTION__ARTIFACT_REF);

    artifactTypeRestrictionEClass = createEClass(ARTIFACT_TYPE_RESTRICTION);
    createEReference(artifactTypeRestrictionEClass, ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE_REF);

    attributeTypeRestrictionEClass = createEClass(ATTRIBUTE_TYPE_RESTRICTION);
    createEReference(attributeTypeRestrictionEClass, ATTRIBUTE_TYPE_RESTRICTION__ATTRIBUTE_TYPE_REF);
    createEReference(attributeTypeRestrictionEClass, ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF);

    relationTypeRestrictionEClass = createEClass(RELATION_TYPE_RESTRICTION);
    createEReference(relationTypeRestrictionEClass, RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF);
    createEAttribute(relationTypeRestrictionEClass, RELATION_TYPE_RESTRICTION__RESTRICTED_TO_SIDE);

    // Create enums
    relationMultiplicityEnumEEnum = createEEnum(RELATION_MULTIPLICITY_ENUM);
    accessPermissionEnumEEnum = createEEnum(ACCESS_PERMISSION_ENUM);
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
    xRelationTypeEClass.getESuperTypes().add(this.getOseeType());
    artifactInstanceRestrictionEClass.getESuperTypes().add(this.getObjectRestriction());
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
    initEReference(getOseeDsl_BranchRefs(), this.getXBranchRef(), null, "branchRefs", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_ArtifactRefs(), this.getXArtifactRef(), null, "artifactRefs", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOseeDsl_AccessDeclarations(), this.getAccessContext(), null, "accessDeclarations", null, 0, -1, OseeDsl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(importEClass, Import.class, "Import", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getImport_ImportURI(), ecorePackage.getEString(), "importURI", null, 0, 1, Import.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(oseeElementEClass, OseeElement.class, "OseeElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(oseeTypeEClass, OseeType.class, "OseeType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getOseeType_Name(), ecorePackage.getEString(), "name", null, 0, 1, OseeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getOseeType_TypeGuid(), ecorePackage.getEString(), "typeGuid", null, 0, 1, OseeType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xArtifactTypeEClass, XArtifactType.class, "XArtifactType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXArtifactType_Abstract(), ecorePackage.getEBoolean(), "abstract", null, 0, 1, XArtifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXArtifactType_SuperArtifactTypes(), this.getXArtifactType(), null, "superArtifactTypes", null, 0, -1, XArtifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXArtifactType_ValidAttributeTypes(), this.getXAttributeTypeRef(), null, "validAttributeTypes", null, 0, -1, XArtifactType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xAttributeTypeRefEClass, XAttributeTypeRef.class, "XAttributeTypeRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getXAttributeTypeRef_ValidAttributeType(), this.getXAttributeType(), null, "validAttributeType", null, 0, 1, XAttributeTypeRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXAttributeTypeRef_BranchGuid(), ecorePackage.getEString(), "branchGuid", null, 0, 1, XAttributeTypeRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

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

    initEClass(xOseeEnumTypeEClass, XOseeEnumType.class, "XOseeEnumType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getXOseeEnumType_EnumEntries(), this.getXOseeEnumEntry(), null, "enumEntries", null, 0, -1, XOseeEnumType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xOseeEnumEntryEClass, XOseeEnumEntry.class, "XOseeEnumEntry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXOseeEnumEntry_Name(), ecorePackage.getEString(), "name", null, 0, 1, XOseeEnumEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXOseeEnumEntry_Ordinal(), ecorePackage.getEString(), "ordinal", null, 0, 1, XOseeEnumEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXOseeEnumEntry_EntryGuid(), ecorePackage.getEString(), "entryGuid", null, 0, 1, XOseeEnumEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xOseeEnumOverrideEClass, XOseeEnumOverride.class, "XOseeEnumOverride", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getXOseeEnumOverride_OverridenEnumType(), this.getXOseeEnumType(), null, "overridenEnumType", null, 0, 1, XOseeEnumOverride.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXOseeEnumOverride_InheritAll(), ecorePackage.getEBoolean(), "inheritAll", null, 0, 1, XOseeEnumOverride.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXOseeEnumOverride_OverrideOptions(), this.getOverrideOption(), null, "overrideOptions", null, 0, -1, XOseeEnumOverride.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(overrideOptionEClass, OverrideOption.class, "OverrideOption", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(addEnumEClass, AddEnum.class, "AddEnum", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAddEnum_EnumEntry(), ecorePackage.getEString(), "enumEntry", null, 0, 1, AddEnum.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAddEnum_Ordinal(), ecorePackage.getEString(), "ordinal", null, 0, 1, AddEnum.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAddEnum_EntryGuid(), ecorePackage.getEString(), "entryGuid", null, 0, 1, AddEnum.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(removeEnumEClass, RemoveEnum.class, "RemoveEnum", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRemoveEnum_EnumEntry(), this.getXOseeEnumEntry(), null, "enumEntry", null, 0, 1, RemoveEnum.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xRelationTypeEClass, XRelationType.class, "XRelationType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXRelationType_SideAName(), ecorePackage.getEString(), "sideAName", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXRelationType_SideAArtifactType(), this.getXArtifactType(), null, "sideAArtifactType", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXRelationType_SideBName(), ecorePackage.getEString(), "sideBName", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getXRelationType_SideBArtifactType(), this.getXArtifactType(), null, "sideBArtifactType", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXRelationType_DefaultOrderType(), ecorePackage.getEString(), "defaultOrderType", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXRelationType_Multiplicity(), this.getRelationMultiplicityEnum(), "multiplicity", null, 0, 1, XRelationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xArtifactRefEClass, XArtifactRef.class, "XArtifactRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXArtifactRef_Name(), ecorePackage.getEString(), "name", null, 0, 1, XArtifactRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXArtifactRef_Guid(), ecorePackage.getEString(), "guid", null, 0, 1, XArtifactRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(xBranchRefEClass, XBranchRef.class, "XBranchRef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getXBranchRef_Name(), ecorePackage.getEString(), "name", null, 0, 1, XBranchRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getXBranchRef_Guid(), ecorePackage.getEString(), "guid", null, 0, 1, XBranchRef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(accessContextEClass, AccessContext.class, "AccessContext", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAccessContext_Name(), ecorePackage.getEString(), "name", null, 0, 1, AccessContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAccessContext_SuperAccessContexts(), this.getAccessContext(), null, "superAccessContexts", null, 0, -1, AccessContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAccessContext_Guid(), ecorePackage.getEString(), "guid", null, 0, 1, AccessContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAccessContext_AccessRules(), this.getObjectRestriction(), null, "accessRules", null, 0, -1, AccessContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAccessContext_HierarchyRestrictions(), this.getHierarchyRestriction(), null, "hierarchyRestrictions", null, 0, -1, AccessContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(hierarchyRestrictionEClass, HierarchyRestriction.class, "HierarchyRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getHierarchyRestriction_Artifact(), this.getXArtifactRef(), null, "artifact", null, 0, 1, HierarchyRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getHierarchyRestriction_AccessRules(), this.getObjectRestriction(), null, "accessRules", null, 0, -1, HierarchyRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(objectRestrictionEClass, ObjectRestriction.class, "ObjectRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getObjectRestriction_Permission(), this.getAccessPermissionEnum(), "permission", null, 0, 1, ObjectRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(artifactInstanceRestrictionEClass, ArtifactInstanceRestriction.class, "ArtifactInstanceRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getArtifactInstanceRestriction_ArtifactRef(), this.getXArtifactRef(), null, "artifactRef", null, 0, 1, ArtifactInstanceRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(artifactTypeRestrictionEClass, ArtifactTypeRestriction.class, "ArtifactTypeRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getArtifactTypeRestriction_ArtifactTypeRef(), this.getXArtifactType(), null, "artifactTypeRef", null, 0, 1, ArtifactTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeTypeRestrictionEClass, AttributeTypeRestriction.class, "AttributeTypeRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAttributeTypeRestriction_AttributeTypeRef(), this.getXAttributeType(), null, "attributeTypeRef", null, 0, 1, AttributeTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttributeTypeRestriction_ArtifactTypeRef(), this.getXArtifactType(), null, "artifactTypeRef", null, 0, 1, AttributeTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(relationTypeRestrictionEClass, RelationTypeRestriction.class, "RelationTypeRestriction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRelationTypeRestriction_RelationTypeRef(), this.getXRelationType(), null, "relationTypeRef", null, 0, 1, RelationTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRelationTypeRestriction_RestrictedToSide(), this.getXRelationSideEnum(), "restrictedToSide", null, 0, 1, RelationTypeRestriction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.class, "RelationMultiplicityEnum");
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.ONE_TO_ONE);
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.ONE_TO_MANY);
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.MANY_TO_ONE);
    addEEnumLiteral(relationMultiplicityEnumEEnum, RelationMultiplicityEnum.MANY_TO_MANY);

    initEEnum(accessPermissionEnumEEnum, AccessPermissionEnum.class, "AccessPermissionEnum");
    addEEnumLiteral(accessPermissionEnumEEnum, AccessPermissionEnum.ALLOW);
    addEEnumLiteral(accessPermissionEnumEEnum, AccessPermissionEnum.DENY);

    initEEnum(xRelationSideEnumEEnum, XRelationSideEnum.class, "XRelationSideEnum");
    addEEnumLiteral(xRelationSideEnumEEnum, XRelationSideEnum.SIDE_A);
    addEEnumLiteral(xRelationSideEnumEEnum, XRelationSideEnum.SIDE_B);
    addEEnumLiteral(xRelationSideEnumEEnum, XRelationSideEnum.BOTH);

    // Create resource
    createResource(eNS_URI);
  }

} //OseeDslPackageImpl
