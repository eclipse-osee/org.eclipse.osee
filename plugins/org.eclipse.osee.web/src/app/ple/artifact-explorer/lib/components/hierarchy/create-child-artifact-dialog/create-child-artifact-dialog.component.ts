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
	Component,
	Inject,
	Optional,
	Output,
	ViewChild,
	inject,
} from '@angular/core';
import { AsyncPipe, CommonModule } from '@angular/common';
import {
	MAT_DIALOG_DATA,
	MatDialogModule,
	MatDialogRef,
} from '@angular/material/dialog';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {
	createChildArtifactDialogData,
	artifactTypeIcon,
} from '../../../types/artifact-explorer.data';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { MatListModule } from '@angular/material/list';
import {
	BehaviorSubject,
	ReplaySubject,
	debounceTime,
	distinctUntilChanged,
	filter,
	map,
	switchMap,
} from 'rxjs';
import { NamedId, attribute } from '@osee/shared/types';
import { MatOptionModule } from '@angular/material/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import {
	AttributesEditorComponent,
	MatOptionLoadingComponent,
} from '@osee/shared/components';
import { MatIconModule } from '@angular/material/icon';
import { FormDirective } from '@osee/shared/directives';
import { ArtifactUiService } from '@osee/shared/services';
import { ArtifactIconService } from '../../../services/artifact-icon.service';

function controlContainerFactory(controlContainer?: ControlContainer) {
	return controlContainer;
}

@Component({
	selector: 'osee-create-artifact-dialog',
	standalone: true,
	imports: [
		CommonModule,
		MatDialogModule,
		MatFormFieldModule,
		MatInputModule,
		FormsModule,
		MatButtonModule,
		MatListModule,
		MatOptionModule,
		MatAutocompleteModule,
		MatOptionLoadingComponent,
		MatIconModule,
		AsyncPipe,
		AttributesEditorComponent,
		FormDirective,
	],
	templateUrl: './create-child-artifact-dialog.component.html',
	viewProviders: [
		{
			provide: ControlContainer,
			useFactory: controlContainerFactory,
			deps: [[new Optional(), NgForm]],
		},
	],
})
export class CreateChildArtifactDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<CreateChildArtifactDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: createChildArtifactDialogData,
		private artifactIconService: ArtifactIconService
	) {}

	onCancel() {
		this.dialogRef.close();
	}

	// Artifact type single select

	private _typeAhead = new BehaviorSubject<string>('');
	private _openAutoComplete = new ReplaySubject<void>();
	private _isOpen = new BehaviorSubject<boolean>(false);
	private _artExpHttpService = inject(ArtifactExplorerHttpService);
	private _artUiService = inject(ArtifactUiService);

	protected _artifactTypes = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				distinctUntilChanged(),
				debounceTime(500),
				switchMap((filter) =>
					this._artUiService.getArtifactTypes(filter)
				)
			)
		)
	);

	get filter() {
		return this._typeAhead;
	}
	updateTypeAhead(value: string | NamedId) {
		this.data.artifactTypeId = '0';
		if (typeof value === 'string') {
			this._typeAhead.next(value);
		} else {
			this._typeAhead.next(value.name);
		}
	}
	autoCompleteOpened() {
		this._openAutoComplete.next();
		this._isOpen.next(true);
	}
	close() {
		this._isOpen.next(false);
	}
	updateValue(value: NamedId): void {
		this.data.artifactTypeId = value.id;
		this._artifactTypeIdSubject.next(value.id);
	}
	get isOpen() {
		return this._isOpen;
	}
	clear() {
		this.updateTypeAhead('');
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
				.filter((attribute) => attribute.name.toLowerCase() !== 'name')
				.sort((a, b) =>
					a.multiplicityId === '2' || a.multiplicityId === '4'
						? -1
						: b.multiplicityId === '2' || b.multiplicityId === '4'
						  ? 1
						  : 0
				)
		)
	);

	// Handle attributes editor form attributes changes

	handleUpdatedAttributes(updatedAttributes: attribute[]) {
		this.data.attributes = updatedAttributes;
	}

	// Make sure required data is filled out

	get isDataComplete(): string {
		return `${
			!!this.data.name &&
			!!this.data.artifactTypeId &&
			this.data.attributes.length > 0 &&
			this.data.attributes.every((attribute) =>
				['2', '4'].includes(attribute.multiplicityId)
					? attribute.value !== ''
					: true
			)
		}`;
	}

	// Handle form status change

	@ViewChild('createChildArtifactForm') _createChildArtifactForm!: NgForm;

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}
}
