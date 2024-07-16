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
import { AsyncPipe } from '@angular/common';
import { Component, WritableSignal, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { transactionResult } from '@osee/shared/types/change-report';
import { BehaviorSubject, map, switchMap, tap } from 'rxjs';
import { CiDashboardImportService } from '../../services/ci-dashboard-import.service';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';

@Component({
	selector: 'osee-ci-dashboard-import',
	standalone: true,
	imports: [AsyncPipe, CiDashboardControlsComponent, MatButton, MatIcon],
	templateUrl: './ci-dashboard-import.component.html',
})
export default class CiDashboardImportComponent {
	constructor(
		private uiService: CiDashboardUiService,
		private importService: CiDashboardImportService
	) {}

	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);
	ciSetIdValid = toSignal(
		this.uiService.ciSetId.pipe(
			map((id) => id !== undefined && id !== '' && id !== '-1')
		),
		{ initialValue: false }
	);

	ciSet = this.uiService.ciSetId.pipe(
		tap((_) => this.txResult.next(undefined))
	);

	selectedFile: WritableSignal<File | undefined> = signal(undefined);

	importResult = toObservable(this.selectedFile).pipe(
		switchMap((file) => this.importService.importFile(file)),
		tap((res) => {
			this.importService.StartImport = false;
			this.txResult.next(res);
		})
	);

	txResult = new BehaviorSubject<transactionResult | undefined>(undefined);

	selectFile(event: Event) {
		const target = event.target as HTMLInputElement;
		if (target.files && target.files.length > 0) {
			const file: File = target.files[0];
			this.selectedFile.set(file);
		}
	}

	startImport() {
		if (this.selectedFile === undefined) {
			return;
		}
		this.importService.StartImport = true;
	}
}
