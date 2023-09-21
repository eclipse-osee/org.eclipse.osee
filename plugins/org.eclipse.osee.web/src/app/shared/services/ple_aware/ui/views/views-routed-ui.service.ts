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
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ViewsUiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class ViewsRoutedUiService {
	constructor(
		private viewsService: ViewsUiService,
		private router: Router
	) {}

	get viewId() {
		return this.viewsService.viewId;
	}

	set ViewId(id: string) {
		const formattedViewId = this.viewId.getValue().replace('-', '%2D');
		const formattedId = id.replace('-', '%2D');
		this.viewsService.ViewId = id;
		this.router.navigateByUrl(
			this.router.url.replace(formattedViewId, formattedId)
		);
	}
}
