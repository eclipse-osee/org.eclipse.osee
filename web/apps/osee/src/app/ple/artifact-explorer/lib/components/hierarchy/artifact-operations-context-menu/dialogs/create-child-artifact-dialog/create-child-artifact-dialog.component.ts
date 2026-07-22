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
	inject,
	signal,
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
	MatDialogClose,
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
import { ArtifactUiService } from '@osee/shared/services';
import { NamedId } from '@osee/shared/types';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import {
	BehaviorSubject,
	debounceTime,
	distinctUntilChanged,
	filter,
	map,
	switchMap,
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
		MatDialogClose,
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
	private artifactIconService = inject(ArtifactIconService);

	onCancel() {
		this.dialogRef.close();
	}

	// Artifact type single select

	private _typeAhead = new BehaviorSubject<string>('');
	private _artExpHttpService = inject(ArtifactExplorerHttpService);
	private _artUiService = inject(ArtifactUiService);
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

	private _artifactTypeIdSubject = new BehaviorSubject<string>('0');
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
				.sort((a, b) =>
					a.multiplicity?.id === '2' || a.multiplicity?.id === '4'
						? -1
						: b.multiplicity?.id === '2' ||
							  b.multiplicity?.id === '4'
							? 1
							: 0
				)
		)
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

	// Handle form status change

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}
}
