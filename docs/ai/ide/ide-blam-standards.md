---
summary: "Coding standards for all AbstractBlam subclasses in OSEE: class structure, member ordering, @Component annotation, the XWidgetBuilder pattern (getXWidgetItems, convenience methods, WidgetId constants), static label constants and XWidgetLabels, ATS vs framework builders, static AttributeType imports, naming/style rules, testing (AbstractBlamTest), and description requirements."
tags: [ide, blam, abstractblam, standards, xwidgetbuilder, testing]
fileMatch: "*Blam*.java"
---

# BLAM Standards

Standards for all AbstractBlam subclasses in the OSEE codebase.

## Class Structure

1. Class name must end with `Blam`.
2. Annotate with `@Component(service = AbstractBlam.class, immediate = true)`.
3. Declare `private static final String BLAM_NAME = "...";` for the display name.
4. Declare `private static final String BLAM_DESC = "...";` if a description constant is needed.
5. Default constructor must call `super(BLAM_NAME, OseeImage)`.
6. Do **not** override `getName()` or `getImage()/getImageDescriptor()` — both are set via the constructor.

## Member Ordering

Organize class members in this order:

1. `BLAM_NAME`
2. `BLAM_DESC`
3. Label constants (widget labels used in `getXWidgetItems` and `runOperation`)
4. Other static fields
5. Instance fields
6. Constructor(s)
7. `getXWidgetItems()`
8. `widgetCreated()`
9. `runOperation()` / `createOperation()`
10. Helper methods
11. `getCategories()`
12. `getDescriptionUsage()`

## @Component Annotation

- Every concrete Blam must have `@Component(service = AbstractBlam.class, immediate = true)`.
- Abstract base Blam classes must NOT have `@Component`.

## Widget Builder

- Use `getXWidgetItems()` with the `XWidgetBuilder` pattern — never `getXWidgetsXml()`.
- ATS blams: call `createWidgetBuilder()` (provides `wb`/`wba`, an `XWidgetBuilderAts`). Framework blams: use `new XWidgetBuilder()`.
- ATS-specific widgets use `WidgetIdAts.XXxxx`; framework widgets use `WidgetId.XXxxx`. Never raw string widget type names.
- Do **not** call `.endWidget()` — it is unnecessary.
- `XXCheckBoxWidget` defaults to `LABEL_AFTER` and horizontal label. Do NOT add `.andLabelAfter()`, `.andHorizLabel()`, or `.andDefault(false)`.
- **Wrapped Parameter Values** - Use `XOption.WRAP_VALUES` on BLAM widgets whose selected values can be long. The
  renderer applies it to inherited read-only `Text` controls; specialized controls and text editors keep their own
  presentation.

```java
@Override
public List<XWidgetData> getXWidgetItems() {
   createWidgetBuilder();  // ATS blams (provides wb and wba)
   // or: XWidgetBuilder wb = new XWidgetBuilder();  // framework blams
   wb.andTextWidget(TITLE).andRequired();
   wb.andCheckBoxWidget("Persist");
   return wb.getXWidgetDatas();
}
```

### Use convenience methods, not generic `andWidget()`

- `andCheckBoxWidget("label")` instead of `andWidget("label", WidgetId.XXCheckBoxWidget)`
- `andTextWidget("label")` instead of `andWidget("label", WidgetId.XXTextWidget)`
- `andListDropWidget("label")` instead of `andWidget("label", WidgetId.XXListDropWidget)`
- `andStringsSelWidget("label")` instead of `andWidget("label", WidgetId.XXStringsWidget)`
- `andBranchSelWidget("label")` instead of `andWidget("label", WidgetId.XXBranchListWidget)`
- `andButtonPushWidget("label")` instead of `andWidget("label", WidgetId.XXButtonPushWidget)`
- `andDateWidget("label")` instead of `andWidget("label", WidgetId.XXDateSelWidget)`
- `andLabelWidget("label")` instead of `andWidget("label", WidgetId.XXLabelWidget)`
- `andArtifactTypeWidget()` instead of `andWidget("Artifact Type", WidgetId.XXArtifactTypeWidget)`
- `andAttributeTypeWidget()` instead of `andWidget("Attribute Type", WidgetId.XXAttributeTypeWidget)`

## Naming & Style

- Widget labels referenced in both `getXWidgetItems` and `runOperation`/`widgetCreated` must be `static final String` constants (extracted to the top of the class), so the same constant is used in the `XWidgetBuilder` call, `widgetCreated()` checks, and `variableMap` reads.

```java
private static final String SOURCE_WORKFLOWS = "Source Workflows (drop here)";

// In getXWidgetItems:
wb.andListDropWidget(SOURCE_WORKFLOWS);
// In runOperation:
List<Artifact> sources = variableMap.getArtifacts(SOURCE_WORKFLOWS);
```

- Common labels shared across multiple BLAMs must use `XWidgetLabels` constants from `org.eclipse.osee.framework.core.widget.XWidgetLabels` — do not redeclare them locally (e.g., `wb.andTextWidget(XWidgetLabels.SOURCE_BRANCH)`).
- Blam-specific labels (used in only one Blam) are `private static final String` constants in that Blam.
- Static fields must be `final` and `UPPER_CASE`.
- No blank lines between field declarations.
- Use concrete types — no `var`.
- No `/** {@inheritDoc} */` comments.
- Replace anonymous inner classes with lambdas where possible.
- No text blocks (`"""`) — project targets Java 11.

### AttributeType references — use static imports

```java
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;

// Then use directly:
wb.andTextWidget(Description).andHeight(80).andRequired();
```

## Description

- Every Blam must override `getDescriptionUsage()` with a meaningful description — not the default.

## Testing

- Every non-abstract Blam must have a corresponding `<BlamName>Test` class in the same package.
- The test class must extend `AbstractBlamTest` and pass the Blam instance to the super constructor:

```java
public class PortPairsBlamTest extends AbstractBlamTest {
   public PortPairsBlamTest() {
      super(new PortPairsBlam());
   }
}
```

- `AbstractBlamTest` automatically tests:
  - Editor opens successfully
  - Title and description are non-null/non-empty
  - All widgets render without error
  - Dummy data can be set on each widget
  - Data round-trips through the `VariableMap`
- Override `setDummyData(XWidget)` in the test subclass if a widget requires specific test data (e.g., artifact selections, custom branch).
- Test classes are discovered at runtime via reflection: `<BlamClassName>Test` in the same package, using the Blam's classloader.
