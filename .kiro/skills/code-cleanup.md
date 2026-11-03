---
name: code-cleanup
description: "Standard OSEE Java code-cleanup rules to apply when performing a cleanup pass on Java files: replace var with concrete types, remove {@inheritDoc}, minimize formatter on/off, make static fields final, prefer lambdas, fix typos, simplify comments, remove redundant Javadoc and self-qualifying references and EOF comments, UPPER_CASE constants, member ordering, prefer String.format, use static imports, and no blank lines between field declarations."
---

# Code Cleanup Rules

Standard code cleanup rules for the OSEE codebase. Apply these when performing code cleanup on Java files.

## 1. Replace `var` with Concrete Types

Do not use Java `var` (local variable type inference). Always declare variables with their explicit concrete class or interface type.

**Before:**
```java
var name = variableMap.getString("key");
var template = getTemplate(safeName);
var list = variableMap.getArtifactTypes("key");
```

**After:**
```java
String name = variableMap.getString("key");
PublishingTemplate template = getTemplate(safeName);
List<ArtifactTypeToken> list = variableMap.getArtifactTypes("key");
```

## 2. Remove `{@inheritDoc}` Comments

Remove all `/** {@inheritDoc} */` Javadoc comments. The `@Override` annotation is sufficient to indicate an overridden method.

**Before:**
```java
/**
 * {@inheritDoc}
 */
@Override
public String getName() {
```

**After:**
```java
@Override
public String getName() {
```

## 3. Minimize Formatter On/Off Comments

Remove `//@formatter:off` and `//@formatter:on` comments unless they protect genuinely complex formatting that a formatter would destroy (e.g., carefully aligned lookup tables, DSL-like builder chains where alignment is critical to readability). If the code reads fine without suppression, remove the comments.

## 4. Make Static Fields `final`

Static fields that are effectively constants should be declared `private static final`.

**Before:**
```java
private static String ARTIFACTS = "Artifacts";
```

**After:**
```java
private static final String ARTIFACTS = "Artifacts";
```

## 5. Replace Anonymous Classes with Lambdas

Where a single-method anonymous class is used (e.g., `Runnable`, functional interfaces), replace it with a lambda expression.

**Before:**
```java
Display.getDefault().syncExec(new Runnable() {
   @Override
   public void run() {
      // ...
   }
});
```

**After:**
```java
Display.getDefault().syncExec(() -> {
   // ...
});
```

## 6. Fix Typos

Correct spelling errors in comments, string literals, and error messages.

## 7. Simplify Block Comments to Line Comments

Single-line comments should use `//` instead of `/* */` block style. Remove blank lines between the comment and the field/method it documents.

**Before:**
```java
   /*
    * Templates
    */

   String name = getValue();
```

**After:**
```java
   // Templates
   String name = getValue();
```

Multi-line Javadoc (`/** */`) is still appropriate for method documentation with `@param`, `@return`, `@throws` tags when they add information beyond what the method signature already communicates. Remove blank lines between the Javadoc and the method signature.

## 8. Remove Redundant Javadoc

Remove Javadoc where the comments merely restate the method signature. If `@param`, `@return`, and `@throws` tags only describe what is already obvious from the method name, parameter names, and return type, delete the entire doc block.

**Before:**
```java
/**
 * Makes the server REST API call to obtain the Publishing Template with the provided safeName.
 *
 * @param safeName the safe name of the Publishing Template to get.
 * @return the {@link PublishingTemplate}.
 * @throws OseeCoreException when unable to obtain the Publishing Template.
 */
private PublishingTemplate getTemplate(String safeName) {
```

**After:**
```java
private PublishingTemplate getTemplate(String safeName) {
```

Keep Javadoc only when it adds context not evident from the code — non-obvious preconditions, side effects, threading concerns, or domain-specific behavior.

## 9. Remove Self-Qualifying Class References

Do not qualify references to static fields or methods with the class name when the reference is within the same class. Just use the field/method name directly.

**Before:**
```java
public class MyBlam {
   private static final String MY_FIELD = "value";

   public void doWork() {
      String x = MyBlam.MY_FIELD;
   }
}
```

**After:**
```java
public class MyBlam {
   private static final String MY_FIELD = "value";

   public void doWork() {
      String x = MY_FIELD;
   }
}
```

Note: `ClassName.this` references in inner classes are legitimate and should NOT be removed.

## 10. Remove EOF Comments

Remove all `/* EOF */` comments at the end of files. They add no value.

## 11. Use UPPER_CASE for Static Final String Constants

All `private static final String` field names must use UPPER_CASE with underscores between words, not camelCase.

**Before:**
```java
private static final String blamName = "My BLAM";
private static final String variableBranchIdentifier = "Branch Identifier";
```

**After:**
```java
private static final String BLAM_NAME = "My BLAM";
private static final String VARIABLE_BRANCH_IDENTIFIER = "Branch Identifier";
```

## 12. Class Member Ordering

Organize class members in this order:

1. Static fields (constants first)
2. Instance fields: private, then protected, then public
3. Constructors
4. Primary operation methods (`runOperation`, `createOperation`, `doWork`, `main`, etc.)
5. Supporting/helper methods

## 13. Prefer String.format Over Concatenation and StringBuilder

Use `String.format` instead of string concatenation (`+`) or `StringBuilder` when building strings with mixed literals and variables. Reserve `StringBuilder` for loops or genuinely complex/performance-critical assembly.

When a method already accepts format arguments (e.g., `logf` on `AbstractBlam`, `xResultData.logf`), use the format variant directly instead of wrapping with `String.format`.

Exception: Do not convert string concatenation to `String.format` inside `getXWidgetsXml()` methods. These will be migrated to `getXWidgetItems()` builder notation, and concatenation is easier to parse for that conversion.

**Before:**
```java
log("artifact [" + fileName + "] does not exist");
log(String.format("artifact [%s] does not exist", fileName));
```

**After:**
```java
logf("artifact [%s] does not exist", fileName);
```

## 14. Use Static Imports for Readability

Use static imports when they improve readability or keep code on a single short line. Good candidates include enum constants, assertion methods, and frequently referenced static fields.

**Before:**
```java
builder.andIsOfType(CoreArtifactTypes.CodeUnit);
artifact.setSoleAttributeValue(CoreAttributeTypes.DataRightsClassification, value);
```

**After:**
```java
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.CodeUnit;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.DataRightsClassification;

builder.andIsOfType(CodeUnit);
artifact.setSoleAttributeValue(DataRightsClassification, value);
```

## 15. No Blank Lines Between Field Declarations

Do not put blank lines between field declarations of the same access level. Keep a single blank line only between the static constants block and the instance fields block.

**Before:**
```java
private XCombo branchViewWidget;

private XBranchSelectWidget branchWidget;

private XCombo childWidget;
```

**After:**
```java
private XCombo branchViewWidget;
private XBranchSelectWidget branchWidget;
private XCombo childWidget;
```
