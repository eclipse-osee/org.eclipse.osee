/*********************************************************************
 * Copyright (c) 2025 Boeing
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
	EventEmitter,
	inject,
	input,
	Output,
} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { filter, map, switchMap, take, tap } from 'rxjs/operators';

import {
	BASEATTRIBUTETYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/attributes/constants';
import {
	getFileExtension,
	getFileNameWithoutExtension,
	readFileAsBase64,
} from '@osee/shared/utils';
import {
	UpdateNativeContentDialogComponent,
	UpdateNativeContentDialogData,
} from '@osee/shared/dialogs';
import { attribute, MAX_FILE_SIZE_BYTES } from '@osee/shared/types';
import { apiURL } from '@osee/environments';

/**
 * Stricter attribute subtypes to guarantee identity and storeType.
 */
export type NameAttribute = attribute & {
	typeId: typeof BASEATTRIBUTETYPEIDENUM.NAME;
	storeType: 'String';
};

export type ExtensionAttribute = attribute & {
	typeId: typeof ATTRIBUTETYPEIDENUM.EXTENSION;
	storeType: 'String';
};

export type NativeContentAttribute = attribute & {
	typeId: typeof ATTRIBUTETYPEIDENUM.NATIVE_CONTENT;
	storeType: 'Input Stream';
};

/**
 * Input containing required attributes and optional extension.
 */
export type NativeEditorAttributes = {
	name: NameAttribute;
	nativeContent: NativeContentAttribute;
	extension?: ExtensionAttribute;
};

@Component({
	selector: 'osee-native-content-editor',
	imports: [MatButton, MatIcon, MatTooltip],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './native-content-editor.component.html',
})
export class NativeContentEditorComponent {
	// Strict input: requires name and nativeContent; extension optional
	nativeEditorAttributes = input.required<NativeEditorAttributes>();

	// Parent controls editability
	editable = input<boolean>(true);

	// Required for URL-based download
	branchId = input<string>('');
	artifactId = input<string>('');

	// Outputs
	@Output() readonly updatedAttributes = new EventEmitter<attribute[]>();

	// DI
	private readonly dialog = inject(MatDialog);
	private readonly http = inject(HttpClient);

	// Derived values for display
	protected readonly fileBaseName = computed<string>(
		() => this.nativeEditorAttributes().name.value ?? ''
	);

	protected readonly fileExtension = computed<string>(() => {
		const ext = this.nativeEditorAttributes().extension;
		return ext ? (ext.value ?? '') : '';
	});

	protected readonly canDownload = computed<boolean>(
		() => !!this.branchId() && !!this.artifactId()
	);

	protected onClickDownload(): void {
		const branch = this.branchId();
		const artifact = this.artifactId();
		if (!branch || !artifact) return;

		const url = `${apiURL}/orcs/branch/${branch}/artifact/${artifact}/attribute/type/${ATTRIBUTETYPEIDENUM.NATIVE_CONTENT}`;

		this.http
			.get(url, { responseType: 'blob' })
			.pipe(
				take(1),
				map((blob) => {
					const base = this.fileBaseName();
					const ext = this.fileExtension();
					const fileName = ext ? `${base}.${ext}` : base || 'download';
					return { blob, fileName };
				}),
				tap(({ blob, fileName }) => {
					const href = URL.createObjectURL(blob);
					const anchor = document.createElement('a');
					anchor.href = href;
					anchor.download = fileName;
					anchor.click();
					URL.revokeObjectURL(href);
				})
			)
			.subscribe();
	}

	// Open dialog, read file, clone attributes, emit only those whose values changed.
	// If extension is absent and the selected file has one, include a new Extension attribute.
	protected onClickOpenUpdate(): void {
		if (!this.editable()) return;

		const base = this.fileBaseName();
		const ext = this.fileExtension();
		const currentFileName = ext ? `${base}.${ext}` : base || 'unnamed';

		const dialogData: UpdateNativeContentDialogData = {
			file: {
				fileName: currentFileName,
			},
			maxFileSizeBytes: MAX_FILE_SIZE_BYTES,
		};

		this.dialog
			.open(UpdateNativeContentDialogComponent, { data: dialogData })
			.afterClosed()
			.pipe(
				take(1),
				filter((file): file is File => !!file),
				switchMap((file) => readFileAsBase64(file)),
				tap(({ file, binaryContent }) => {
					const incomingName = getFileNameWithoutExtension(file.name);
					const incomingExt = getFileExtension(file.name); // '' if none

					// Clone and update required attributes
					const updatedName: NameAttribute = {
						...this.nativeEditorAttributes().name,
						value: incomingName,
					};

					const updatedNative: NativeContentAttribute = {
						...this.nativeEditorAttributes().nativeContent,
						value: binaryContent,
					};

					const changes: attribute[] = [];

					// Only push if actually changed
					if (
						updatedName.value !==
						this.nativeEditorAttributes().name.value
					) {
						changes.push(updatedName);
					}

					if (
						updatedNative.value !==
						this.nativeEditorAttributes().nativeContent.value
					) {
						changes.push(updatedNative);
					}

					// Extension handling:
					// - If extension exists: update/push only if changed.
					// - If missing: if the new file has an extension (non-empty), create a new Extension attribute and push it.
					const existingExt = this.nativeEditorAttributes().extension;
					if (existingExt) {
						const updatedExt: ExtensionAttribute = {
							...existingExt,
							value: incomingExt,
						};
						if (updatedExt.value !== existingExt.value) {
							changes.push(updatedExt);
						}
					} else if (incomingExt) {
						const newExt: ExtensionAttribute = {
							// id is required by `attribute`; since parent save maps only typeId/value,
							// a sentinel like '0' is acceptable. If your transaction layer prefers '-1'
							// for new attributes, adjust accordingly.
							id: '-1',
							name: 'Extension',
							value: incomingExt,
							typeId: ATTRIBUTETYPEIDENUM.EXTENSION,
							storeType: 'String',
							multiplicityId: '3',
						};
						changes.push(newExt);
					}

					if (changes.length > 0) {
						this.updatedAttributes.emit(changes);
					}
				})
			)
			.subscribe();
	}
}
