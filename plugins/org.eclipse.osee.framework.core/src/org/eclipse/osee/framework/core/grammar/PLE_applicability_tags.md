[[_TOC_]]

## PLE Applicability Tags Syntax
- When an applicability tag does not apply, the text it contains is not projected.

### Valid characters of Names
Defined in org.eclipse.osee.framework.core/src/org/eclipse/osee/framework/core/grammar/ApplicabilityGrammar.g
names of features, configurations, configuration groups, and feature values may contain:
- letters
- numbers
- spaces
- limited punctuation _ - . ( )

### Additional Naming constraints
- names must start with a letter
- feature names may not use lowercase letters

### Simple Feature Applicability Tags

> ``Feature[A=Included]`` text_1 ``End Feature``<br>
> Projects `text_1` when feature A is included.

> ``Feature[A]`` text_1 ``End Feature``<br>
> Projects `text_1` when feature A is included.  When the feature name is specified without a value, the default value for the feature is implied.  For features with the values Included/Excluded the default is Included.

> ``Feature[A=Excluded]`` text_1 ``End Feature``<br>
> Projects `text_1` when feature A is excluded.


### Simple Configuration Applicability Tags

> ``Configuration[Config_1]`` text_1 ``End Configuration``<br>
> Projects `text_1` when generating a projection for the configuration Config_1

> ``Configuration Not[Config_1]`` text_1 ``End Configuration``<br>
> Projects `text_1` when generating a projection for any configuration other than Config_1

> ``ConfigurationGroup[CommonConfig]`` text_1 ``End ConfigurationGroup``<br>
> Projects `text_1` when generating a projection for any configuration included in the configuration group CommonConfig

> ``ConfigurationGroup Not[CommonConfig]`` text_1 ``End ConfigurationGroup``<br>
> Projects `text_1` when generating a projection for any configuration except those included in the configuration group CommonConfig

### Tags with else

> ``Feature[A]`` text_1 ``Feature Else`` text_2 ``End Feature``<br>
> Projects `text_1` when feature A is included, otherwise `text_2` is projected

> ``Configuration[Config_1]`` text_1 ``Configuration Else`` text_2 ``End Configuration``<br>
> Projects `text_1` when generating a projection for the configuration Config_1, otherwise `text_2` is projected

> ``ConfigurationGroup[CommonConfig]`` text_1 ``ConfigurationGroup Else`` text_2 ``End ConfigurationGroup``<br>
> Projects `text_1` when generating a projection for any configuration included in the configuration group CommonConfig, otherwise `text_2` is projected

### Tags with compound values (And/Or)

> ``Feature[A=Included | B=Included]`` text_1 ``End Feature``<br>
> Projects `text_1` when feature A is included or feature B is included.

> ``Feature[A=Include & B=Included]`` text_1 ``End Feature``<br>
> Projects `text_1` only when feature A and feature B are included.

> ``Configuration[Config_1 | Config_2]`` text_1 ``End Configuration``<br>
> Projects `text_1` when generating a projection for configuration Config_1 or Config_2

> ``Configuration Not[Config_1 | Config_2]`` text_1 ``End Configuration``<br>
> Projects `text_1` when generating a projection for any configuration other than Config_1 or Config_2

> ``ConfigurationGroup[CommonConfig | ConfigGroupX]`` text_1 ``End ConfigurationGroup``<br>
> Projects `text_1` when generating a projection for any configuration included in the configuration group CommonConfig or ConfigGroupX

> ``ConfigurationGroup Not[CommonConfig | ConfigGroupX]`` text_1 ``End ConfigurationGroup``<br>
> Projects `text_1` when generating a projection for any configuration except those included in the configuration group CommonConfig or ConfigGroupX

#### Not Valid
Using & is not valid for Configurations or ConfigurationGroups because it would always evaluate to not applicable and exclude the text.

>~~``Configuration[Config_1 & Config_2]`` text_1 ``End Configuration``~~

>~~``ConfigurationGroup[CommonConfig & ConfigGroupX]`` text_1 ``End ConfigurationGroup``~~

### Nested Tags

> ``Feature[A]`` text_1 ``Feature[B]`` text_2 ``End Feature`` text_3 ``End Feature``<br>
> Projects `text_1 text_2 text_3` when feature A is included and feature B is included.
> Projects `text_1 text_3` when feature A is included and feature B is excluded.
> Projects nothing when feature A is excluded

> ``Feature[A]`` text_1 ``Feature[B]`` text_2 ``End Feature`` ``Feature Else`` text_3 ``End Feature``<br>
> Projects `text_1 text_2` when feature A is included and feature B is included.
> Projects `text_1` when feature A is included and feature B is excluded.
> Projects `text_3` when feature A is excluded

Note: Ensure each tag has a matching end tag.  Tags may be embedded inside of else tags.

Note: If you use nested tags inside of tables the same rules apply, the tags must be over a whole row, several rows, or within 1 cell.


### Tags inside tables
**Valid:** Tags around an entire row(s) are valid. Tags inside 1 cell are valid. Nothing else is valid.

**Not Valid:** Tags that go across multiple cells but not the entire row are not valid.

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---|
| ``Feature[A]`` 1a | 1b | 1c | 1d  | 1e ``End Feature`` |
| ``Feature[A]`` 2a | 2b | 2c | 2d  | 2e  |
| 3a | 3b | 3c | 3d  | 3e ``End Feature`` |
| ``Feature[A]`` 4a ``End Feature`` <br> ``Feature[A]`` 5a ``End Feature`` | 2b | 4c | 4d | ``Feature[A]`` 4e ``End Feature`` <br> ``Feature[A]`` 5e ``End Feature`` |
| ~~``Feature[A]`` 6a~~ | ~~6b~~ | ~~6c``End Feature``~~ | ~~6d~~  | ~~6e~~ |



## Previewing Change Reports/Diffs
It is important to initially preview change reports with no view selected and then each individual view that will be impacted based on the new changes.
- View Not Selected:
    - All applicability tags will be showing
    - All text inside tags will be showing

- View Is Selected:
    - All applicability tags will be removed
        - Note that if the working or baseline branch is missing a start or end applicability tag, it will not be parsed out correctly and the tag may appear in the change report.
    - Only text that is applicable will display in change report based on selected view.

- Examples:
>    - Baseline Branch text:
>
>        text_1
>
>    - Working Branch text:
>
>        ``Feature[A=Included]`` text_1 ``End Feature``
>
>    Note that the a new feature tag was put around the text.

>    - Diff with no view selected:
>
>        ``Feature[A=Included]`` text_1 ``End Feature``
>
>    - Diff with view where A is included:
>
>        text_1
>
>    Note that this shows no change because the text on preview has not changed comparing the working branch to the baseline branch when feature A is included for the selected view.

>    - Diff with view where A is excluded:
>
>        ~~text_1~~
>
>    Note that this shows the text has been deleted. Before this change was added, if a user were to view the baseline branch with the selected view where A is excluded, the text would appear in the preview/publish. Since the working branch has stated this text will only appear in a preview/publish where A in included, it will show it as deleted from this view where A is excluded.

## Other Important Notes
- If applicability tags are on their own line, the line will be removed upon projection. However, if there is leading or trailing white space, the line will note be removed. For example:
>     Feature[A]¶ - would result in the whole line being removed
>     text_1¶
>     End Feature¶ - would result in the whole line being remove

>      Feature[A]¶ - would not result in the whole line being removed because of leading space characters
>     text_1¶
>     End Feature¶ - would result in the whole line being remove

>     Feature[A]¶ - would result in the whole line being remove
>     text_1¶
>     End Feature ¶ - would not result in the whole line being removed because of trailing space characters

- ‘Feature’, ‘End Feature’, ‘Feature Else’, ‘Configuration’, ‘End Configuration’, and ‘Configuration Else’ are all case sensitive.
    - Using ‘feature’, ‘end feature’, ‘feature else’, ‘configuration’, ‘end configuration’, and ’configuration else’ are all invalid.
- The features/configurations anmes inside the brackets are case insensitive.
- The spacing does not matter between features when using ‘&’ or ‘|’.  A space or no space before or after the symbols is supported.
- White space inside of the start and end tags will remain. Do not try to line up the feature tags. For example:
```
The list:
   Feature[FEATURE_ABC]  F1, End Feature
   Feature[FEATURE_1]    F2, End Feature
   Feature[FEATURE_DE]   F3, End Feature
   Feature[FEATURE_LONG] F4, End Feature
                         Text
```
Becomes:
```
The list:
     F1,
       F2,
      F3,
    F4,
                         Text
```

## Debug Hints/Tips
- Check spelling and correct capitalization on all applicability tags
- Verify each start tag has a matching end tag (NOTE: when embedding tags, each start tag still requires a matching end tag) and that the start and end tag types match (e.g., “``End Feature``” for a starting “``Feature[A]``” and “``End ConfigurationGroup``” for a starting “``ConfigurationGroup[CommonConfig]``”.
- Verify all brackets ‘[‘ ‘]’ are correct
- Verify the feature(s) inside the brackets are spelled correctly and are valid.
    - **NOTE:** To determine if feature is valid, check the Feature Definition artifact on your branch which is located under the ‘Product Line’ folder)