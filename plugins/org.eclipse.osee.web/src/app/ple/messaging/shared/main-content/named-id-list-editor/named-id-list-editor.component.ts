/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, inject, Input, Output } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { NamedId } from '@osee/shared/types';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EditViewFreeTextFieldDialogComponent } from '@osee/messaging/shared/dialogs/free-text';
import { filter, skip, Subject, switchMap, take, tap } from 'rxjs';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { EditViewFreeTextDialog } from '@osee/messaging/shared/types';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';

@Component({
	selector: 'osee-named-id-list-editor',
	standalone: true,
	imports: [
		NgFor,
		NgIf,
		FormsModule,
		MatListModule,
		MatDividerModule,
		MatDialogModule,
		MatPaginatorModule,
		MatInputModule,
		MatFormFieldModule,
		MatIconModule,
	],
	templateUrl: './named-id-list-editor.component.html',
	styles: [],
})
export class NamedIdListEditorComponent {
	@Input() name: string = '';
	@Input() dataToDisplay: NamedId[] = [];
	@Input() count: number = 0;

	@Input() pageSize: number = 10;

	@Input() pageIndex: number = 1;
	@Input() allowedToEdit: boolean = false;

	@Output() pageEvent = new Subject<PageEvent>();

	filter: string = '';
	_filterChange = new Subject<string>();
	@Output() filterChange = this._filterChange;

	@Output() namedIdEdit = new Subject<NamedId>();

	@Output() createNew = new Subject<string>();
	_dialog = inject(MatDialog);

	openNameDialog(namedIdToModify: NamedId) {
		this._dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: namedIdToModify.name,
					type: 'Name',
					return: namedIdToModify.name,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((v) => v !== 'ok' && v !== 'cancel' && v !== undefined),
				tap((v) => {
					this.namedIdEdit.next({
						id: namedIdToModify.id,
						name: v.return,
					});
				})
			)
			.subscribe();
	}

	updateFilter(f: string) {
		this._filterChange.next(f);
	}

	addNamedId() {
		const returnVal = '';
		this._dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: '',
					type: 'Name',
					return: returnVal,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((v) => v !== 'ok' && v !== 'cancel' && v !== undefined),
				tap((v) => {
					this.createNew.next(v.return);
				})
			)
			.subscribe();
	}
}
