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
import { MimRouteService } from '@osee/messaging/shared/services';

@Injectable({
	providedIn: 'root',
})
export class RouteStateService {
	constructor(private uiService: MimRouteService) {}

	get type() {
		return this.uiService.type;
	}

	get id() {
		return this.uiService.id;
	}

	set branchType(value: string) {
		this.uiService.typeValue = value;
	}

	set branchId(value: string) {
		this.uiService.idValue = value;
	}

	get viewId() {
		return this.uiService.viewId;
	}

	set ViewId(id: string) {
		this.uiService.ViewId = id;
	}

	get isInDiff() {
		return this.uiService.isInDiff;
	}

	set DiffMode(value: boolean) {
		this.uiService.diffMode = value;
	}

	set connectionId(value: string) {
		this.uiService.connectionIdString = value;
	}
	set messageId(value: string) {
		this.uiService.messageIdString = value;
	}
	set subMessageId(value: string) {
		this.uiService.submessageIdString = value;
	}
	set subMessageToStructureBreadCrumbs(value: string) {
		this.uiService.submessageToStructureBreadCrumbsString = value;
	}

	set singleStructureId(value: string) {
		this.uiService.singleStructureIdValue = value;
	}

	get updated() {
		return this.uiService.updated;
	}

	set update(value: boolean) {
		this.uiService.update = value;
	}
}
