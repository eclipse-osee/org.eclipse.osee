/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage
 * @generated
 */
public interface OseeDslFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  OseeDslFactory eINSTANCE = org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Osee Dsl</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Osee Dsl</em>'.
   * @generated
   */
  OseeDsl createOseeDsl();

  /**
   * Returns a new object of class '<em>Import</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Import</em>'.
   * @generated
   */
  Import createImport();

  /**
   * Returns a new object of class '<em>Osee Element</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Osee Element</em>'.
   * @generated
   */
  OseeElement createOseeElement();

  /**
   * Returns a new object of class '<em>Osee Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Osee Type</em>'.
   * @generated
   */
  OseeType createOseeType();

  /**
   * Returns a new object of class '<em>XArtifact Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>XArtifact Type</em>'.
   * @generated
   */
  XArtifactType createXArtifactType();

  /**
   * Returns a new object of class '<em>XAttribute Type Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>XAttribute Type Ref</em>'.
   * @generated
   */
  XAttributeTypeRef createXAttributeTypeRef();

  /**
   * Returns a new object of class '<em>XAttribute Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>XAttribute Type</em>'.
   * @generated
   */
  XAttributeType createXAttributeType();

  /**
   * Returns a new object of class '<em>XOsee Enum Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>XOsee Enum Type</em>'.
   * @generated
   */
  XOseeEnumType createXOseeEnumType();

  /**
   * Returns a new object of class '<em>XOsee Enum Entry</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>XOsee Enum Entry</em>'.
   * @generated
   */
  XOseeEnumEntry createXOseeEnumEntry();

  /**
   * Returns a new object of class '<em>XOsee Enum Override</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>XOsee Enum Override</em>'.
   * @generated
   */
  XOseeEnumOverride createXOseeEnumOverride();

  /**
   * Returns a new object of class '<em>Override Option</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Override Option</em>'.
   * @generated
   */
  OverrideOption createOverrideOption();

  /**
   * Returns a new object of class '<em>Add Enum</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Add Enum</em>'.
   * @generated
   */
  AddEnum createAddEnum();

  /**
   * Returns a new object of class '<em>Remove Enum</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Remove Enum</em>'.
   * @generated
   */
  RemoveEnum createRemoveEnum();

  /**
   * Returns a new object of class '<em>XOsee Artifact Type Override</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>XOsee Artifact Type Override</em>'.
   * @generated
   */
  XOseeArtifactTypeOverride createXOseeArtifactTypeOverride();

  /**
   * Returns a new object of class '<em>Attribute Override Option</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Override Option</em>'.
   * @generated
   */
  AttributeOverrideOption createAttributeOverrideOption();

  /**
   * Returns a new object of class '<em>Add Attribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Add Attribute</em>'.
   * @generated
   */
  AddAttribute createAddAttribute();

  /**
   * Returns a new object of class '<em>Remove Attribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Remove Attribute</em>'.
   * @generated
   */
  RemoveAttribute createRemoveAttribute();

  /**
   * Returns a new object of class '<em>Update Attribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Update Attribute</em>'.
   * @generated
   */
  UpdateAttribute createUpdateAttribute();

  /**
   * Returns a new object of class '<em>XRelation Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>XRelation Type</em>'.
   * @generated
   */
  XRelationType createXRelationType();

  /**
   * Returns a new object of class '<em>Condition</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Condition</em>'.
   * @generated
   */
  Condition createCondition();

  /**
   * Returns a new object of class '<em>Simple Condition</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Simple Condition</em>'.
   * @generated
   */
  SimpleCondition createSimpleCondition();

  /**
   * Returns a new object of class '<em>Compound Condition</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Compound Condition</em>'.
   * @generated
   */
  CompoundCondition createCompoundCondition();

  /**
   * Returns a new object of class '<em>XArtifact Matcher</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>XArtifact Matcher</em>'.
   * @generated
   */
  XArtifactMatcher createXArtifactMatcher();

  /**
   * Returns a new object of class '<em>Role</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Role</em>'.
   * @generated
   */
  Role createRole();

  /**
   * Returns a new object of class '<em>Referenced Context</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Referenced Context</em>'.
   * @generated
   */
  ReferencedContext createReferencedContext();

  /**
   * Returns a new object of class '<em>Users And Groups</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Users And Groups</em>'.
   * @generated
   */
  UsersAndGroups createUsersAndGroups();

  /**
   * Returns a new object of class '<em>Access Context</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Access Context</em>'.
   * @generated
   */
  AccessContext createAccessContext();

  /**
   * Returns a new object of class '<em>Hierarchy Restriction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Hierarchy Restriction</em>'.
   * @generated
   */
  HierarchyRestriction createHierarchyRestriction();

  /**
   * Returns a new object of class '<em>Relation Type Artifact Type Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Relation Type Artifact Type Predicate</em>'.
   * @generated
   */
  RelationTypeArtifactTypePredicate createRelationTypeArtifactTypePredicate();

  /**
   * Returns a new object of class '<em>Relation Type Artifact Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Relation Type Artifact Predicate</em>'.
   * @generated
   */
  RelationTypeArtifactPredicate createRelationTypeArtifactPredicate();

  /**
   * Returns a new object of class '<em>Relation Type Predicate</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Relation Type Predicate</em>'.
   * @generated
   */
  RelationTypePredicate createRelationTypePredicate();

  /**
   * Returns a new object of class '<em>Object Restriction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Object Restriction</em>'.
   * @generated
   */
  ObjectRestriction createObjectRestriction();

  /**
   * Returns a new object of class '<em>Artifact Match Restriction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Artifact Match Restriction</em>'.
   * @generated
   */
  ArtifactMatchRestriction createArtifactMatchRestriction();

  /**
   * Returns a new object of class '<em>Artifact Type Restriction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Artifact Type Restriction</em>'.
   * @generated
   */
  ArtifactTypeRestriction createArtifactTypeRestriction();

  /**
   * Returns a new object of class '<em>Attribute Type Restriction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Type Restriction</em>'.
   * @generated
   */
  AttributeTypeRestriction createAttributeTypeRestriction();

  /**
   * Returns a new object of class '<em>Legacy Relation Type Restriction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Legacy Relation Type Restriction</em>'.
   * @generated
   */
  LegacyRelationTypeRestriction createLegacyRelationTypeRestriction();

  /**
   * Returns a new object of class '<em>Relation Type Restriction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Relation Type Restriction</em>'.
   * @generated
   */
  RelationTypeRestriction createRelationTypeRestriction();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  OseeDslPackage getOseeDslPackage();

} //OseeDslFactory
