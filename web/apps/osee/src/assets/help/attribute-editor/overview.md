# Attribute Editor

The attribute editor lets you view and modify all attributes on an artifact. It displays each attribute with an inline editor that auto-saves on focus loss, and provides tools to add new attributes or delete existing ones.

## Editing

All attribute values auto-save when you move focus away from the field. There is no explicit save button for individual attributes.

- **Text fields**: Click to edit, changes save on blur.
- **Enumerations**: Select from the dropdown, saves immediately on selection change.
- **Booleans**: Toggle the switch, saves immediately.

## Toolbar Actions

The toolbar at the top of the artifact editor shows attribute actions when the Attributes section is active.

| Icon | Action |
| :-- | :-- |
| <span class="material-icons">add_circle</span> | Open the Add Attribute dialog to create new attribute instances. |
| <span class="material-icons">delete</span> / <span class="material-icons">delete_sweep</span> | Toggle delete mode on/off. When active, delete icons appear next to each attribute. |

## Adding Attributes

Click <span class="material-icons" style="font-size:14px;vertical-align:middle">add_circle</span> to open the Add Attribute dialog. It shows all attribute types valid for this artifact type that can still have instances added (based on multiplicity rules).

- **Filter**: Type in the search field to narrow the list.
- **Checkboxes**: Select one or more attribute types to add.
- **Quantity**: For types that allow multiple instances (marked "Optional, Unlimited" or "Required, Unlimited"), a number input appears letting you add up to 50 at once.
- **Multiplicity labels**: Each type shows its constraint — Required/Optional and max count.

Click **Add** to create the new attribute instances with default values. They appear immediately in the editor for you to fill in.

## Deleting Attributes

Click <span class="material-icons" style="font-size:14px;vertical-align:middle">delete</span> in the toolbar to enter delete mode. The icon changes to <span class="material-icons" style="font-size:14px;vertical-align:middle">delete_sweep</span> and <span class="material-icons" style="font-size:14px;vertical-align:middle">remove_circle_outline</span> icons appear next to every attribute.

| Icon | Meaning |
| :-- | :-- |
| <span class="material-icons" style="font-size:14px;vertical-align:middle;color:#ff1744">remove_circle_outline</span> | Deletable. Click to immediately remove this attribute instance. |
| <span class="material-icons" style="font-size:14px;vertical-align:middle;opacity:0.38">remove_circle_outline</span> | Cannot be deleted. Hover for the reason (required attribute, at minimum count, or Name/Applicability). |

Click <span class="material-icons" style="font-size:14px;vertical-align:middle">delete_sweep</span> again to exit delete mode.

## Grouped Attributes

When an artifact has multiple instances of the same attribute type, they are grouped together in a bordered section with the type name and instance count as a header (e.g., "Qualification Method (7)"). Each instance can be edited independently.

- **Collapse**: Groups with more than 5 instances show only the first 5 by default. A "Show N more..." link at the bottom expands to reveal all instances.
- **Expand**: Click "Show less" to collapse back to 5.
- **Delete mode**: In delete mode, each visible instance shows its own delete icon. Expand the group first to access instances beyond the initial 5.
