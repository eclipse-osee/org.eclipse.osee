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
import { Injectable, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ViewsUiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class ViewsRoutedUiService {
	private viewsService = inject(ViewsUiService);
	private router = inject(Router);
	private route = inject(ActivatedRoute);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
		this.route.queryParamMap?.subscribe((queryParams) => {
			const viewId = queryParams.get('view');
			if (viewId !== null) {
				this.viewsService.ViewId = viewId;
			}
		});
	}

	get viewId() {
		return this.viewsService.viewId;
	}

	set ViewId(id: string) {
		this.viewsService.ViewId = id;
		const tree = this.router.parseUrl(this.router.url);
		const queryParams = tree.queryParams;
		if (!id || id === '' || id === '-1') {
			delete queryParams['view'];
		} else {
			queryParams['view'] = id;
		}
		this.router.navigate([], { queryParams: queryParams });
	}
}
