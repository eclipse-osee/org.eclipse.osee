/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { filter, switchMap, take, tap } from 'rxjs/operators';

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
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { MAX_FILE_SIZE_BYTES } from '@osee/shared/types';
import { NativeContentDownloadService } from './native-content-download.service';

/**
 * Stricter attribute subtypes to guarantee identity and storeType.
 */
export type NameAttribute = attribute<string, ATTRIBUTETYPEID> & {
	typeId: typeof BASEATTRIBUTETYPEIDENUM.NAME;
	storeType: 'String';
};

export type ExtensionAttribute = attribute<string, ATTRIBUTETYPEID> & {
	typeId: typeof ATTRIBUTETYPEIDENUM.EXTENSION;
	storeType: 'String';
};

export type NativeContentAttribute = attribute<string, ATTRIBUTETYPEID> & {
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

	// Whether there are unsaved native content changes (controlled by parent)
	hasUnsavedChanges = input<boolean>(false);

	// Pending display values (controlled by parent)
	pendingName = input<string | null>(null);
	pendingExtension = input<string | null>(null);

	// Outputs
	@Output() readonly updatedAttributes =
		new EventEmitter<attribute<string, ATTRIBUTETYPEID>[]>();

	// DI
	private readonly dialog = inject(MatDialog);
	private readonly downloadService = inject(NativeContentDownloadService);

	// Derived values for display — prefer pending values over input values
	protected readonly fileBaseName = computed<string>(() => {
		const pending = this.pendingName();
		if (pending !== null) return pending;
		return this.nativeEditorAttributes().name.value ?? '';
	});

	protected readonly fileExtension = computed<string>(() => {
		const pending = this.pendingExtension();
		if (pending !== null) return pending;
		const ext = this.nativeEditorAttributes().extension;
		return ext ? (ext.value ?? '') : '';
	});

	protected readonly canDownload = computed<boolean>(
		() =>
			!!this.branchId() &&
			!!this.artifactId() &&
			!this.hasUnsavedChanges()
	);

	protected onClickDownload(): void {
		const branch = this.branchId();
		const artifact = this.artifactId();
		if (!branch || !artifact) return;

		const base = this.fileBaseName();
		const ext = this.fileExtension();
		const fileName = ext ? `${base}.${ext}` : base || 'download';

		this.downloadService
			.downloadNativeContent(branch, artifact, fileName)
			.pipe(take(1))
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

					const changes: attribute<string, ATTRIBUTETYPEID>[] = [];

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
							gammaId: '-1',
							storeType: 'String',
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
