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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';

type DialogData = {
	readonly content: unknown;
};

@Component({
	selector: 'osee-publish-launcher-result-dialog',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatButton,
	],
	template: `
		<h2 mat-dialog-title>Publish Results</h2>
		<mat-dialog-content class="tw-min-h-96 tw-min-w-96">
			<pre class="tw-whitespace-pre-wrap tw-text-sm">{{
				formatContent(data.content)
			}}</pre>
		</mat-dialog-content>
		<mat-dialog-actions align="end">
			<button
				mat-button
				mat-dialog-close>
				Close
			</button>
		</mat-dialog-actions>
	`,
})
export class PublishLauncherResultDialogComponent {
	private readonly dialogRef = inject(
		MatDialogRef<PublishLauncherResultDialogComponent>
	);
	readonly data = inject<DialogData>(MAT_DIALOG_DATA);

	formatContent(content: unknown): string {
		if (typeof content === 'string') {
			return content;
		}
		return JSON.stringify(content, null, 2);
	}

	closeDialog(): void {
		this.dialogRef.close();
	}
}
