/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	effect,
	ElementRef,
	inject,
	signal,
	viewChild,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialog,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { artifactTypeIcon } from '@osee/artifact-with-relations/types';
import { AttributesEditorComponent } from '@osee/shared/components';
import { FormDirective } from '@osee/shared/directives';
import { ImmediateErrorStateMatcher } from '@osee/shared/matchers';
import { ArtifactUiService } from '@osee/shared/services';
import { NamedId } from '@osee/shared/types';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID, MULTIPLICITY_ID } from '@osee/attributes/constants';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import {
	BehaviorSubject,
	Subject,
	debounceTime,
	distinctUntilChanged,
	filter,
	switchMap,
	take,
} from 'rxjs';
import { ArtifactExplorerHttpService } from '../../../../../services/artifact-explorer-http.service';
import { ArtifactIconService } from '../../../../../services/artifact-icon.service';
import { createChildArtifactDialogData } from '../../../../../types/artifact-explorer';
import {
	AddAttributeDialogComponent,
	addAttributeDialogData,
	addAttributeDialogResult,
} from '../../../../editor/attributes-editor-panel/add-attribute-dialog/add-attribute-dialog.component';

@Component({
	selector: 'osee-create-artifact-dialog',
	imports: [
		FormsModule,
		AttributesEditorComponent,
		FormDirective,
		MatDialogTitle,
		MatIcon,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatSuffix,
		MatIconButton,
		MatOption,
		MatDialogActions,
		MatButton,
		MatTooltip,
	],
	templateUrl: './create-child-artifact-dialog.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class CreateChildArtifactDialogComponent {
	dialogRef =
		inject<MatDialogRef<CreateChildArtifactDialogComponent>>(MatDialogRef);
	data = inject<createChildArtifactDialogData>(MAT_DIALOG_DATA);
	private readonly artifactIconService = inject(ArtifactIconService);

	/**
	 * Emits a create request each time the user submits. The opener subscribes
	 * to perform the transaction; `keepOpen` tells it whether the dialog is
	 * staying open for another entry (Create & add another) or closing (Create).
	 */
	readonly create = new Subject<{
		data: createChildArtifactDialogData;
		keepOpen: boolean;
	}>();

	/** Highlights required fields as invalid immediately, before they're touched. */
	protected readonly errorMatcher = new ImmediateErrorStateMatcher();

	private readonly nameInput =
		viewChild<ElementRef<HTMLInputElement>>('nameInput');

	onCancel() {
		this.dialogRef.close();
	}

	/**
	 * Creates the current artifact and keeps the dialog open, clearing only the
	 * name (type and attribute values carry over) so the user can add another of
	 * the same type without re-entering shared values.
	 */
	createAndAddAnother() {
		this.create.next({ data: this.snapshotData(), keepOpen: true });
		this.data.name = '';
		this.nameInput()?.nativeElement.focus();
	}

	/** Creates the current artifact and closes the dialog. */
	createAndClose() {
		this.create.next({ data: this.snapshotData(), keepOpen: false });
		this.dialogRef.close();
	}

	/**
	 * Builds the submission payload from the currently visible attributes. The
	 * editor mutates each attribute's `value` in place, so this reflects the
	 * latest edits. Every visible attribute is included — required ones and any
	 * the user added — even when left at its (possibly empty) default, so an
	 * added-but-untouched attribute is still created with its default value.
	 * Deep-copied so each emitted create request is independent of later edits.
	 */
	private snapshotData(): createChildArtifactDialogData {
		const attributes = this.visibleAttributes().map((attr) => ({
			...attr,
			value: `${attr.value ?? ''}`,
		}));
		return {
			...this.data,
			attributes,
		};
	}

	// Artifact type single select

	private readonly _typeAhead = new BehaviorSubject<string>('');
	private readonly _artExpHttpService = inject(ArtifactExplorerHttpService);
	private readonly _artUiService = inject(ArtifactUiService);
	protected readonly inputFocused = signal(false);

	/** Debounced filter signal that drives the httpResource. */
	private readonly debouncedFilter = toSignal(
		this._typeAhead.pipe(distinctUntilChanged(), debounceTime(500)),
		{ initialValue: '' }
	);

	/** Resource that fetches concrete artifact types based on the debounced filter. */
	private readonly _typesResource =
		this._artUiService.getArtifactTypesResource(this.debouncedFilter, true);

	protected readonly artifactTypes = computed(
		() => this._typesResource.value() ?? []
	);
	protected readonly isLoadingTypes = this._typesResource.isLoading;

	displayArtifactType(value: NamedId | string): string {
		if (typeof value === 'string') {
			return value;
		}
		return value?.name ?? '';
	}

	updateTypeAhead(value: string) {
		this.data.artifactTypeId = '0';
		this._typeAhead.next(value);
	}
	autoCompleteOpened() {
		this.inputFocused.set(true);
	}
	updateValue(value: NamedId): void {
		this.data.artifactTypeId = value.id;
		this._artifactTypeIdSubject.next(value.id);
	}
	clear(input: HTMLInputElement) {
		input.value = '';
		this._typeAhead.next('');
		this.data.artifactTypeId = '0';
		// Refocus so the user can immediately type a new search
		input.focus();
	}

	// Attributes: fetch the artifact type's valid attributes, seed the editor
	// with the REQUIRED ones (optional ones are added on demand via the Add
	// Attribute dialog). Requires an artifact type to be selected.

	private readonly _dialog = inject(MatDialog);
	private readonly _artifactTypeIdSubject = new BehaviorSubject<string>('0');

	/** All valid attribute type tokens for the selected artifact type (defaults live in each token's `value`). */
	protected readonly allAttributeTypes = signal<
		attribute<string, ATTRIBUTETYPEID>[]
	>([]);

	/** Attributes currently shown in the editor (required + any the user added). */
	protected readonly visibleAttributes = signal<
		attribute<string, ATTRIBUTETYPEID>[]
	>([]);

	/** Attribute types the user can still add (optional, or repeatable). */
	protected readonly hasAddableAttributes = computed(
		() => this.buildAddableTypes().length > 0
	);

	private readonly _attributesLoad = this._artifactTypeIdSubject
		.asObservable()
		.pipe(
			filter((val) => val != '0'),
			debounceTime(500),
			distinctUntilChanged(),
			switchMap((artifactTypeId) =>
				this._artExpHttpService.getArtifactTypeAttributes(
					artifactTypeId
				)
			)
		);

	private readonly _attributeTypes = toSignal(this._attributesLoad, {
		initialValue: [] as attribute<string, ATTRIBUTETYPEID>[],
	});

	constructor() {
		// When the artifact type's attributes load, seed the editor with the
		// required attributes (with their server-provided default values).
		effect(() => {
			const types = this._attributeTypes();
			this.allAttributeTypes.set(types);
			const required = types
				.filter((attr) => attr.name?.toLowerCase() !== 'name')
				.filter((attr) => this.isRequiredMultiplicity(attr))
				.map((attr) => this.toSeededAttribute(attr));
			this.visibleAttributes.set(required);
		});
	}

	/** Opens the Add Attribute dialog and appends the chosen optional attributes. */
	protected openAddAttributeDialog() {
		const dialogData: addAttributeDialogData = {
			allAttributeTypes: this.allAttributeTypes(),
			existingAttributes: this.visibleAttributes(),
		};
		this._dialog
			.open(AddAttributeDialogComponent, {
				data: dialogData,
				width: '480px',
				restoreFocus: false,
			})
			.afterClosed()
			.pipe(take(1))
			.subscribe((result: addAttributeDialogResult | undefined) => {
				if (result && result.selectedTypes.length > 0) {
					const added = result.selectedTypes.map((attr) =>
						this.toSeededAttribute(attr)
					);
					this.visibleAttributes.update((attrs) => [
						...attrs,
						...added,
					]);
				}
			});
	}

	/** Removes an optional attribute the user added. */
	protected removeAttribute(attr: attribute<string, ATTRIBUTETYPEID>) {
		this.visibleAttributes.update((attrs) =>
			attrs.filter((a) => a !== attr)
		);
	}

	/** Copies an attribute type token into an editable attribute with its default value stringified. */
	private toSeededAttribute(
		attr: attribute<string, ATTRIBUTETYPEID>
	): attribute<string, ATTRIBUTETYPEID> {
		return { ...attr, value: `${attr.value ?? ''}` };
	}

	/** The addable types not already required/shown (mirrors the add dialog's own filter). */
	private buildAddableTypes(): attribute<string, ATTRIBUTETYPEID>[] {
		const existing = this.visibleAttributes();
		return this.allAttributeTypes().filter((type) => {
			if (type.name?.toLowerCase() === 'name') {
				return false;
			}
			const count = existing.filter(
				(e) => e.typeId === type.typeId
			).length;
			const id = type.multiplicity?.id;
			// EXACTLY_ONE / ZERO_OR_ONE: max one instance.
			if (
				id === MULTIPLICITY_ID.EXACTLY_ONE ||
				id === MULTIPLICITY_ID.ZERO_OR_ONE
			) {
				return count < 1;
			}
			// ANY / AT_LEAST_ONE: unlimited.
			return true;
		});
	}

	// Make sure required data is filled out

	get isArtifactTypeValid(): boolean {
		return !!this.data.artifactTypeId && this.data.artifactTypeId !== '0';
	}

	/** True when the attribute's multiplicity is EXACTLY_ONE or AT_LEAST_ONE. */
	private isRequiredMultiplicity(
		attr: attribute<string, ATTRIBUTETYPEID>
	): boolean {
		return (
			attr.multiplicity?.id === MULTIPLICITY_ID.EXACTLY_ONE ||
			attr.multiplicity?.id === MULTIPLICITY_ID.AT_LEAST_ONE
		);
	}

	/** Tooltip explaining why the create buttons are disabled; empty when enabled. */
	protected disabledReason(formInvalid: boolean | null): string {
		if (!this.isArtifactTypeValid) {
			return 'Select a valid artifact type from the list.';
		}
		if (formInvalid) {
			return 'Required fields not filled out.';
		}
		return '';
	}

	// Handle form status change

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}
}
