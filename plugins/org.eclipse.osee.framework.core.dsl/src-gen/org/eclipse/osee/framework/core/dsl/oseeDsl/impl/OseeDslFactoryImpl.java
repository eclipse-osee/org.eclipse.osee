/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.osee.framework.core.dsl.oseeDsl.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class OseeDslFactoryImpl extends EFactoryImpl implements OseeDslFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static OseeDslFactory init()
  {
    try
    {
      OseeDslFactory theOseeDslFactory = (OseeDslFactory)EPackage.Registry.INSTANCE.getEFactory(OseeDslPackage.eNS_URI);
      if (theOseeDslFactory != null)
      {
        return theOseeDslFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new OseeDslFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeDslFactoryImpl()
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
      case OseeDslPackage.OSEE_DSL: return createOseeDsl();
      case OseeDslPackage.IMPORT: return createImport();
      case OseeDslPackage.OSEE_ELEMENT: return createOseeElement();
      case OseeDslPackage.OSEE_TYPE: return createOseeType();
      case OseeDslPackage.XARTIFACT_TYPE: return createXArtifactType();
      case OseeDslPackage.XATTRIBUTE_TYPE_REF: return createXAttributeTypeRef();
      case OseeDslPackage.XATTRIBUTE_TYPE: return createXAttributeType();
      case OseeDslPackage.XOSEE_ENUM_TYPE: return createXOseeEnumType();
      case OseeDslPackage.XOSEE_ENUM_ENTRY: return createXOseeEnumEntry();
      case OseeDslPackage.XOSEE_ENUM_OVERRIDE: return createXOseeEnumOverride();
      case OseeDslPackage.OVERRIDE_OPTION: return createOverrideOption();
      case OseeDslPackage.ADD_ENUM: return createAddEnum();
      case OseeDslPackage.REMOVE_ENUM: return createRemoveEnum();
      case OseeDslPackage.XOSEE_ARTIFACT_TYPE_OVERRIDE: return createXOseeArtifactTypeOverride();
      case OseeDslPackage.ATTRIBUTE_OVERRIDE_OPTION: return createAttributeOverrideOption();
      case OseeDslPackage.ADD_ATTRIBUTE: return createAddAttribute();
      case OseeDslPackage.REMOVE_ATTRIBUTE: return createRemoveAttribute();
      case OseeDslPackage.UPDATE_ATTRIBUTE: return createUpdateAttribute();
      case OseeDslPackage.XRELATION_TYPE: return createXRelationType();
      case OseeDslPackage.CONDITION: return createCondition();
      case OseeDslPackage.SIMPLE_CONDITION: return createSimpleCondition();
      case OseeDslPackage.COMPOUND_CONDITION: return createCompoundCondition();
      case OseeDslPackage.XARTIFACT_MATCHER: return createXArtifactMatcher();
      case OseeDslPackage.ROLE: return createRole();
      case OseeDslPackage.REFERENCED_CONTEXT: return createReferencedContext();
      case OseeDslPackage.USERS_AND_GROUPS: return createUsersAndGroups();
      case OseeDslPackage.ACCESS_CONTEXT: return createAccessContext();
      case OseeDslPackage.HIERARCHY_RESTRICTION: return createHierarchyRestriction();
      case OseeDslPackage.RELATION_TYPE_ARTIFACT_TYPE_PREDICATE: return createRelationTypeArtifactTypePredicate();
      case OseeDslPackage.RELATION_TYPE_ARTIFACT_PREDICATE: return createRelationTypeArtifactPredicate();
      case OseeDslPackage.RELATION_TYPE_PREDICATE: return createRelationTypePredicate();
      case OseeDslPackage.OBJECT_RESTRICTION: return createObjectRestriction();
      case OseeDslPackage.ARTIFACT_MATCH_RESTRICTION: return createArtifactMatchRestriction();
      case OseeDslPackage.ARTIFACT_TYPE_RESTRICTION: return createArtifactTypeRestriction();
      case OseeDslPackage.ATTRIBUTE_TYPE_RESTRICTION: return createAttributeTypeRestriction();
      case OseeDslPackage.LEGACY_RELATION_TYPE_RESTRICTION: return createLegacyRelationTypeRestriction();
      case OseeDslPackage.RELATION_TYPE_RESTRICTION: return createRelationTypeRestriction();
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
      case OseeDslPackage.RELATION_MULTIPLICITY_ENUM:
        return createRelationMultiplicityEnumFromString(eDataType, initialValue);
      case OseeDslPackage.COMPARE_OP:
        return createCompareOpFromString(eDataType, initialValue);
      case OseeDslPackage.XLOGIC_OPERATOR:
        return createXLogicOperatorFromString(eDataType, initialValue);
      case OseeDslPackage.MATCH_FIELD:
        return createMatchFieldFromString(eDataType, initialValue);
      case OseeDslPackage.ACCESS_PERMISSION_ENUM:
        return createAccessPermissionEnumFromString(eDataType, initialValue);
      case OseeDslPackage.RELATION_TYPE_MATCH:
        return createRelationTypeMatchFromString(eDataType, initialValue);
      case OseeDslPackage.XRELATION_SIDE_ENUM:
        return createXRelationSideEnumFromString(eDataType, initialValue);
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
      case OseeDslPackage.RELATION_MULTIPLICITY_ENUM:
        return convertRelationMultiplicityEnumToString(eDataType, instanceValue);
      case OseeDslPackage.COMPARE_OP:
        return convertCompareOpToString(eDataType, instanceValue);
      case OseeDslPackage.XLOGIC_OPERATOR:
        return convertXLogicOperatorToString(eDataType, instanceValue);
      case OseeDslPackage.MATCH_FIELD:
        return convertMatchFieldToString(eDataType, instanceValue);
      case OseeDslPackage.ACCESS_PERMISSION_ENUM:
        return convertAccessPermissionEnumToString(eDataType, instanceValue);
      case OseeDslPackage.RELATION_TYPE_MATCH:
        return convertRelationTypeMatchToString(eDataType, instanceValue);
      case OseeDslPackage.XRELATION_SIDE_ENUM:
        return convertXRelationSideEnumToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeDsl createOseeDsl()
  {
    OseeDslImpl oseeDsl = new OseeDslImpl();
    return oseeDsl;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Import createImport()
  {
    ImportImpl import_ = new ImportImpl();
    return import_;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeElement createOseeElement()
  {
    OseeElementImpl oseeElement = new OseeElementImpl();
    return oseeElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeType createOseeType()
  {
    OseeTypeImpl oseeType = new OseeTypeImpl();
    return oseeType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactType createXArtifactType()
  {
    XArtifactTypeImpl xArtifactType = new XArtifactTypeImpl();
    return xArtifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XAttributeTypeRef createXAttributeTypeRef()
  {
    XAttributeTypeRefImpl xAttributeTypeRef = new XAttributeTypeRefImpl();
    return xAttributeTypeRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XAttributeType createXAttributeType()
  {
    XAttributeTypeImpl xAttributeType = new XAttributeTypeImpl();
    return xAttributeType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XOseeEnumType createXOseeEnumType()
  {
    XOseeEnumTypeImpl xOseeEnumType = new XOseeEnumTypeImpl();
    return xOseeEnumType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XOseeEnumEntry createXOseeEnumEntry()
  {
    XOseeEnumEntryImpl xOseeEnumEntry = new XOseeEnumEntryImpl();
    return xOseeEnumEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XOseeEnumOverride createXOseeEnumOverride()
  {
    XOseeEnumOverrideImpl xOseeEnumOverride = new XOseeEnumOverrideImpl();
    return xOseeEnumOverride;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OverrideOption createOverrideOption()
  {
    OverrideOptionImpl overrideOption = new OverrideOptionImpl();
    return overrideOption;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AddEnum createAddEnum()
  {
    AddEnumImpl addEnum = new AddEnumImpl();
    return addEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RemoveEnum createRemoveEnum()
  {
    RemoveEnumImpl removeEnum = new RemoveEnumImpl();
    return removeEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XOseeArtifactTypeOverride createXOseeArtifactTypeOverride()
  {
    XOseeArtifactTypeOverrideImpl xOseeArtifactTypeOverride = new XOseeArtifactTypeOverrideImpl();
    return xOseeArtifactTypeOverride;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeOverrideOption createAttributeOverrideOption()
  {
    AttributeOverrideOptionImpl attributeOverrideOption = new AttributeOverrideOptionImpl();
    return attributeOverrideOption;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AddAttribute createAddAttribute()
  {
    AddAttributeImpl addAttribute = new AddAttributeImpl();
    return addAttribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RemoveAttribute createRemoveAttribute()
  {
    RemoveAttributeImpl removeAttribute = new RemoveAttributeImpl();
    return removeAttribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UpdateAttribute createUpdateAttribute()
  {
    UpdateAttributeImpl updateAttribute = new UpdateAttributeImpl();
    return updateAttribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XRelationType createXRelationType()
  {
    XRelationTypeImpl xRelationType = new XRelationTypeImpl();
    return xRelationType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Condition createCondition()
  {
    ConditionImpl condition = new ConditionImpl();
    return condition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SimpleCondition createSimpleCondition()
  {
    SimpleConditionImpl simpleCondition = new SimpleConditionImpl();
    return simpleCondition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CompoundCondition createCompoundCondition()
  {
    CompoundConditionImpl compoundCondition = new CompoundConditionImpl();
    return compoundCondition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactMatcher createXArtifactMatcher()
  {
    XArtifactMatcherImpl xArtifactMatcher = new XArtifactMatcherImpl();
    return xArtifactMatcher;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Role createRole()
  {
    RoleImpl role = new RoleImpl();
    return role;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReferencedContext createReferencedContext()
  {
    ReferencedContextImpl referencedContext = new ReferencedContextImpl();
    return referencedContext;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UsersAndGroups createUsersAndGroups()
  {
    UsersAndGroupsImpl usersAndGroups = new UsersAndGroupsImpl();
    return usersAndGroups;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AccessContext createAccessContext()
  {
    AccessContextImpl accessContext = new AccessContextImpl();
    return accessContext;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public HierarchyRestriction createHierarchyRestriction()
  {
    HierarchyRestrictionImpl hierarchyRestriction = new HierarchyRestrictionImpl();
    return hierarchyRestriction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelationTypeArtifactTypePredicate createRelationTypeArtifactTypePredicate()
  {
    RelationTypeArtifactTypePredicateImpl relationTypeArtifactTypePredicate = new RelationTypeArtifactTypePredicateImpl();
    return relationTypeArtifactTypePredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelationTypeArtifactPredicate createRelationTypeArtifactPredicate()
  {
    RelationTypeArtifactPredicateImpl relationTypeArtifactPredicate = new RelationTypeArtifactPredicateImpl();
    return relationTypeArtifactPredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelationTypePredicate createRelationTypePredicate()
  {
    RelationTypePredicateImpl relationTypePredicate = new RelationTypePredicateImpl();
    return relationTypePredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ObjectRestriction createObjectRestriction()
  {
    ObjectRestrictionImpl objectRestriction = new ObjectRestrictionImpl();
    return objectRestriction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ArtifactMatchRestriction createArtifactMatchRestriction()
  {
    ArtifactMatchRestrictionImpl artifactMatchRestriction = new ArtifactMatchRestrictionImpl();
    return artifactMatchRestriction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ArtifactTypeRestriction createArtifactTypeRestriction()
  {
    ArtifactTypeRestrictionImpl artifactTypeRestriction = new ArtifactTypeRestrictionImpl();
    return artifactTypeRestriction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AttributeTypeRestriction createAttributeTypeRestriction()
  {
    AttributeTypeRestrictionImpl attributeTypeRestriction = new AttributeTypeRestrictionImpl();
    return attributeTypeRestriction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LegacyRelationTypeRestriction createLegacyRelationTypeRestriction()
  {
    LegacyRelationTypeRestrictionImpl legacyRelationTypeRestriction = new LegacyRelationTypeRestrictionImpl();
    return legacyRelationTypeRestriction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelationTypeRestriction createRelationTypeRestriction()
  {
    RelationTypeRestrictionImpl relationTypeRestriction = new RelationTypeRestrictionImpl();
    return relationTypeRestriction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelationMultiplicityEnum createRelationMultiplicityEnumFromString(EDataType eDataType, String initialValue)
  {
    RelationMultiplicityEnum result = RelationMultiplicityEnum.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertRelationMultiplicityEnumToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CompareOp createCompareOpFromString(EDataType eDataType, String initialValue)
  {
    CompareOp result = CompareOp.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertCompareOpToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XLogicOperator createXLogicOperatorFromString(EDataType eDataType, String initialValue)
  {
    XLogicOperator result = XLogicOperator.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertXLogicOperatorToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MatchField createMatchFieldFromString(EDataType eDataType, String initialValue)
  {
    MatchField result = MatchField.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertMatchFieldToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AccessPermissionEnum createAccessPermissionEnumFromString(EDataType eDataType, String initialValue)
  {
    AccessPermissionEnum result = AccessPermissionEnum.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertAccessPermissionEnumToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelationTypeMatch createRelationTypeMatchFromString(EDataType eDataType, String initialValue)
  {
    RelationTypeMatch result = RelationTypeMatch.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertRelationTypeMatchToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XRelationSideEnum createXRelationSideEnumFromString(EDataType eDataType, String initialValue)
  {
    XRelationSideEnum result = XRelationSideEnum.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertXRelationSideEnumToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeDslPackage getOseeDslPackage()
  {
    return (OseeDslPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static OseeDslPackage getPackage()
  {
    return OseeDslPackage.eINSTANCE;
  }

} //OseeDslFactoryImpl
