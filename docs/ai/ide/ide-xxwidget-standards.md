---
summary: "Coding standards for XWidget/XX widget subclasses in the OSEE XX widget architecture: naming, OSGi registration, constructors, required VAL_ value-type option, which lifecycle methods to override vs not, validation, ATS widgets, composite widgets, and WidgetId token files"
tags: [ide, xwidget, xxwidget, standards, osgi, widgets]
fileMatch: "**/widgets/xx/**,**/*XX*Widget*,**/WidgetId*.java"
---

# XXWidget Standards

Standards for all XX widget subclasses in the OSEE XX widget architecture.

## Naming Conventions

1. Concrete widgets extending `XXAbstractBaseXWidget` must start with `XX` (e.g., `XXBranchSelWidget`).
2. Concrete widgets extending `XWidget` directly must start with `X` but NOT `XX`.
3. Abstract widget base classes must start with `XAbstract` or `XXAbstract`.
4. Class name must match the `WidgetId` name exactly (e.g., class `XXBranchSelWidget` → `WidgetId.XXBranchSelWidget`).
5. Widget names containing `Dam` are retired — use `Art` instead.
6. Widgets implementing `ArtifactWidget` or `AttributeWidget` must include `ArtWidget`, `Viewer`, or `Persist` in the name.

## Registration & OSGi

7. Every concrete widget must have `@Component(service = XWidget.class, immediate = true)`.
8. Abstract widgets must NOT have `@Component`.
9. Every concrete widget must declare `public static final WidgetId ID = WidgetId.<ClassName>;`.
10. The `WidgetId` field name in `WidgetId.java` or `WidgetIdAts.java` must match the string value passed to the constructor.

## Constructors

11. Every concrete widget must have a default no-arg constructor for OSGi instantiation.
12. Only ONE constructor should call `super(...)` — others must chain via `this(...)`.
13. Constructors must pass `ID` (not `WidgetId.XXSomething`) to super/this.
14. Abstract widgets must have at least one constructor accepting `(WidgetId widgetId, ...)` and passing it to `super(widgetId, ...)`.

## Value Type Options (Required)

15. Every widget must declare exactly ONE of these `XOption` values:
    - `VAL_MULTI_SEL` — multi-select from selectable list
    - `VAL_SINGLE_SEL` — single-select from selectable list
    - `VAL_MULTI_TEXT` — multi-line text entry
    - `VAL_SINGLE_TEXT` — single-line text entry
    - `VAL_NO_SEL` — widget does not produce a value (labels, buttons)
    - `VAL_NOT_APPLICABLE` — composite/container widget

16. The value type determines which dialog `handleSelection()` opens. Do not override `handleSelection()` unless the widget has a custom selection mechanism (e.g., branch picker dialog).

## Lifecycle Methods — Do NOT Override

17. Do NOT override `validate()` — override `getValidator()` instead to return a custom `IValueValidator`.
18. Do NOT override `getSelected()` — use `setSelected()` or `initializeSelected()` to populate values.
19. Do NOT call `refresh()` from `setArtifact()` — it is called automatically after `setArtifact`.
20. Do NOT call `setSelected()` from `setArtifact()` — use `initializeSelected()` instead.
21. In `initializeSelected()`, always call `setSelected(value, false)` (with `false`) to prevent triggering refresh during initialization.

## Lifecycle Methods — Safe to Override

22. `initializeSelected()` — populate "selected" from artifact attributes or other sources.
23. `getSelectable()` — return available options for selection dialogs.
24. `getDisplayValue()` — convert selected items to a display string.
25. `getValidator()` — return `IValueValidator` for additional validation; return `XXNoOpValidator.instance` if no extra validation needed.
26. `getSentinel()` — return the empty/default value for the widget's type.
27. `handleSelection()` — only if the widget needs a completely custom selection mechanism.
28. `createLabelWidget()` — only to add custom behavior after calling `super.createLabelWidget()`.

## Validation

29. Widgets call `validateValuesXOption()` before `handleSelection()` to ensure exactly one `VAL_*` option is set.
30. The `XWidgetReportOperations.openWidgetReport()` method validates all registered widgets at runtime.
31. Do NOT use `XWidgetUtility.setStatus` directly — the validation framework handles this.

## ATS Widgets

32. ATS widgets should have an `xxWid` field of type `XXWorkItemData` for accessing the work item context.
33. ATS widgets use `WidgetIdAts` instead of `WidgetId` for their ID constant.

## Composite Widgets

34. Composite widgets wrapping multiple children must:
    - Override `getChildrenXWidgets()` to return child widgets.
    - Delegate `validate()`, `refresh()`, `dispose()`, `setEditable()`, `setArtifact()`, `setNotificationsAllowed()` to children.
    - Register `XModifiedListener` on children and call `notifyXModifiedListeners()` on itself when children change.
    - Use `notifyRightClickListeners()` for right-click events that should propagate differently than normal modifications.

## Prohibited Patterns

35. No `System.err.print` or `System.out.print` — use `OseeLog`.
36. No text blocks (`"""`) — project targets Java 11.
37. No `var` — use concrete types.
38. No `/** {@inheritDoc} */` comments.
39. No `TBD` markers in production code.

## WidgetId Token Files

40. `WidgetId.java` (framework) and `WidgetIdAts.java` (ATS) contain auto-generated token declarations between `// START` and `// END` comments.
41. Do not manually edit these sections — they are regenerated by `RunXWidgetCodeReview`.
42. Each entry must follow: `public static final WidgetId[Ats] ClassName = new WidgetId[Ats]("ClassName");`
