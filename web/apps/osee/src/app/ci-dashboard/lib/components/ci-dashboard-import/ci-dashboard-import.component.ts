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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	effect,
	inject,
	signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { take, tap } from 'rxjs';
import { CiDashboardImportService } from '../../services/ci-dashboard-import.service';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import {
	TmoImportResult,
	tmoImportResultSentinel,
} from '../../types/tmo-import';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';

@Component({
	selector: 'osee-ci-dashboard-import',
	imports: [CiDashboardControlsComponent, MatButton, MatIcon],
	templateUrl: './ci-dashboard-import.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class CiDashboardImportComponent {
	private uiService = inject(CiDashboardUiService);
	private importService = inject(CiDashboardImportService);

	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);
	ciSetId = toSignal(this.uiService.ciSetId);
	ciSetIdValid = computed(() => {
		const set = this.ciSetId();
		return set !== undefined && set !== '' && set !== '-1';
	});

	private _ciSetEffect = effect(
		() => {
			this.ciSetId();
			this.importResult.set(tmoImportResultSentinel);
		},
		{ allowSignalWrites: true }
	);

	selectedFile = signal<File | undefined>(undefined);
	private importResult = signal<TmoImportResult>(tmoImportResultSentinel);
	txResult = computed(() => this.importResult()?.txResult);

	selectFile(event: Event) {
		const target = event.target as HTMLInputElement;
		if (target.files && target.files.length > 0) {
			const file: File = target.files[0];
			this.selectedFile.set(file);
		}
	}

	startImport() {
		if (this.selectedFile() === undefined) {
			return;
		}
		this.importService
			.importFile(this.selectedFile())
			.pipe(
				take(1),
				tap((res) => this.importResult.set(res))
			)
			.subscribe();
	}
}
