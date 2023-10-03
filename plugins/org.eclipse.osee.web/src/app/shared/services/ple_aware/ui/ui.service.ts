/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { Injectable } from '@angular/core';
import { ViewsUiService } from './views/views-ui.service';
import { BranchUIService } from './branch/branch-ui.service';
import { DiffModeService } from './diff/diff-mode.service';
import { ErrorService } from './error/error.service';
import { UpdateService } from './update/update.service';
import { HttpLoadingService } from '../../network/http-loading.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
	providedIn: 'root',
})
export class UiService {
	constructor(
		private branchService: BranchUIService,
		private viewService: ViewsUiService,
		private updateService: UpdateService,
		private diffModeService: DiffModeService,
		private loadingService: HttpLoadingService,
		private errorService: ErrorService
	) {}

	get id() {
		return this.branchService.id;
	}

	/**
	 * @deprecated will be replacing id with idAsObservable's functionality
	 */
	get idAsObservable() {
		return this.branchService.idAsObservable;
	}

	get type() {
		return this.branchService.type;
	}

	set idValue(id: string | number) {
		this.branchService.idValue = id;
	}

	set typeValue(branchType: 'working' | 'baseline' | '') {
		this.branchService.typeValue = branchType;
	}

	get update() {
		return this.updateService.update;
	}

	set updated(value: boolean) {
		this.updateService.updated = value;
	}

	get isInDiff() {
		return this.diffModeService.isInDiff;
	}

	set diffMode(value: boolean) {
		this.diffModeService.DiffMode = value;
	}

	get isLoading() {
		return this.loadingService.isLoading;
	}

	set loading(value: boolean) {
		this.loadingService.loading = value;
	}

	get viewId() {
		return this.viewService.viewId;
	}

	set viewIdValue(id: string) {
		this.viewService.ViewId = id;
	}

	public get errorText() {
		return this.errorService.errorText;
	}

	public get errorDetails() {
		return this.errorService.errorDetails;
	}

	public set ErrorText(errorText: string) {
		this.errorService.setError(errorText, '');
	}

	public set httpError(error: HttpErrorResponse) {
		this.errorService.setHttpError(error);
	}
}
