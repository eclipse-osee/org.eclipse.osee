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
import { Injectable, inject } from '@angular/core';
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
	private branchService = inject(BranchUIService);
	private viewService = inject(ViewsUiService);
	private updateService = inject(UpdateService);
	private diffModeService = inject(DiffModeService);
	private loadingService = inject(HttpLoadingService);
	private errorService = inject(ErrorService);

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

	get category() {
		return this.branchService.category;
	}

	set idValue(id: string | number) {
		this.branchService.idValue = id;
	}

	set typeValue(branchType: 'working' | 'baseline' | '') {
		this.branchService.typeValue = branchType;
	}

	set categoryValue(branchCategory: string) {
		this.branchService.categoryValue = branchCategory;
	}

	get update() {
		return this.updateService.update;
	}

	set updated(value: boolean) {
		this.updateService.updated = value;
	}

	get updateArtifact() {
		return this.updateService.updateArtifact;
	}

	set updatedArtifact(value: string) {
		this.updateService.updatedArtifact = value;
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

	set ViewId(value: string) {
		this.viewService.ViewId = value;
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
