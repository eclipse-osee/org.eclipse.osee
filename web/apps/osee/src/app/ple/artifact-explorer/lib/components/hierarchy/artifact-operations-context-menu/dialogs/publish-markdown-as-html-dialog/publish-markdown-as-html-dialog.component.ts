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
import { Component, inject, viewChild } from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
} from '@angular/material/dialog';
import { artifactTypeIcon } from '@osee/artifact-with-relations/types';
import { ArtifactExplorerHttpService } from '../../../../../services/artifact-explorer-http.service';
import { ArtifactIconService } from '../../../../../services/artifact-icon.service';
import {
	key,
	publishingTemplateKey,
	publishMarkdownDialogData,
} from '../../../../../types/artifact-explorer';
import { ArtifactDialogTitleComponent } from '../../../../shared/artifact-dialog-title/artifact-dialog-title.component';
import { FormsModule, NgForm } from '@angular/forms';
import { NamedId } from '@osee/shared/types';
import {
	BehaviorSubject,
	ReplaySubject,
	debounceTime,
	distinctUntilChanged,
	map,
	switchMap,
} from 'rxjs';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';

@Component({
	selector: 'osee-markdown-publish-dialog',
	standalone: true,
	imports: [
		AsyncPipe,
		ArtifactDialogTitleComponent,
		FormsModule,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatAutocomplete,
		MatIcon,
		MatInput,
		MatAutocompleteTrigger,
		MatOption,
		MatDialogClose,
		MatButton,
		MatDialogActions,
	],
	templateUrl: './publish-markdown-as-html-dialog.component.html',
})
export class PublishMarkdownAsHtmlDialogComponent {
	dialogRef =
		inject<MatDialogRef<PublishMarkdownAsHtmlDialogComponent>>(
			MatDialogRef
		);
	data = inject<publishMarkdownDialogData>(MAT_DIALOG_DATA);
	private artifactIconService = inject(ArtifactIconService);

	onCancel() {
		this.dialogRef.close();
	}

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}

	protected _publishMarkdownForm = viewChild.required('publishMarkdownForm', {
		read: NgForm,
	});

	// Template selector

	private _typeAhead = new BehaviorSubject<string>('');
	private _openAutoComplete = new ReplaySubject<void>();
	private _isOpen = new BehaviorSubject<boolean>(false);

	private _artExpHttpService = inject(ArtifactExplorerHttpService);

	protected _templates = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				distinctUntilChanged(),
				debounceTime(500),
				switchMap((filterBySafeName) =>
					this._artExpHttpService
						.getPublishingTemplateKeyGroups(filterBySafeName)
						.pipe(
							map(
								(groups) =>
									groups.publishingTemplateKeyGroupList
							)
						)
				)
			)
		)
	);

	get filterBySafeName() {
		return this._typeAhead;
	}
	updateTypeAhead(value: string | NamedId) {
		this.data.templateId = '0';
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
	updateValue(value: publishingTemplateKey): void {
		this.data.templateId = value.identifier.key;
	}
	get isOpen() {
		return this._isOpen;
	}
	clear() {
		this.updateTypeAhead('');
	}
	displayFn(val: key) {
		return val?.key;
	}
}
