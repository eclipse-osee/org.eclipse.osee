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
import { AsyncPipe } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	computed,
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
	map,
	switchMap,
	tap,
} from 'rxjs';
import { ArtifactExplorerHttpService } from '../../../../../services/artifact-explorer-http.service';
import { ArtifactIconService } from '../../../../../services/artifact-icon.service';
import { createChildArtifactDialogData } from '../../../../../types/artifact-explorer';

@Component({
	selector: 'osee-create-artifact-dialog',
	imports: [
		AsyncPipe,
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

	/** Deep-enough copy so each emitted create request is independent of later edits. */
	private snapshotData(): createChildArtifactDialogData {
		return {
			...this.data,
			attributes: this.data.attributes.map((attr) => ({ ...attr })),
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

	// Attribute fetching to pass into attribute editor - Requires artifact type to be selected

	private readonly _artifactTypeIdSubject = new BehaviorSubject<string>('0');
	protected _attributes = this._artifactTypeIdSubject.asObservable().pipe(
		filter((val) => val != '0'),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((artifactTypeId) =>
			this._artExpHttpService.getArtifactTypeAttributes(artifactTypeId)
		),
		map((attributes) =>
			attributes
				.filter((attribute) => attribute.name?.toLowerCase() !== 'name')
				// Required attributes (multiplicity AT_LEAST_ONE / EXACTLY_ONE) first.
				.sort(
					(a, b) =>
						Number(this.isRequiredMultiplicity(b)) -
						Number(this.isRequiredMultiplicity(a))
				)
		),
		// Seed any server-provided default values into the submission payload so
		// they are saved even if the user never edits the pre-filled field. The
		// attributes editor overwrites these via (updatedAttributes) on any edit.
		tap((attributes) => {
			this.data.attributes = attributes
				.filter((attribute) => `${attribute.value ?? ''}` !== '')
				.map((attribute) => ({
					...attribute,
					value: `${attribute.value}`,
				}));
		})
	);

	// Handle attributes editor form attributes changes

	handleUpdatedAttributes(
		updatedAttributes: attribute<string, ATTRIBUTETYPEID>[]
	) {
		this.data.attributes = updatedAttributes;
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
