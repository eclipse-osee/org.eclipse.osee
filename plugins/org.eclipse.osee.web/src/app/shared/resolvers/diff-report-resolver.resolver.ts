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
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { first } from 'rxjs/operators';
import { DiffUIService } from 'src/app/ple-services/httpui/diff-uiservice.service';
import { changeInstance } from '@osee/shared/types/change-report';

const resolvedSubject = new ReplaySubject<changeInstance[] | undefined>(1);
let requested = false;
export const diffReportResolverFn: ResolveFn<changeInstance[] | undefined> = (
	route: ActivatedRouteSnapshot
) => {
	const diffService = inject(DiffUIService);
	let currentRoute = route;
	while (
		!currentRoute.paramMap.has('branchId') &&
		!currentRoute.paramMap.has('branchType') &&
		currentRoute.parent !== null
	) {
		currentRoute = currentRoute.parent;
	}
	if (diffService.id !== currentRoute.paramMap.get('branchId')) {
		requested = false;
		diffService.branchId = currentRoute.paramMap.get('branchId') || '';
	}
	if (diffService.type !== currentRoute.paramMap.get('branchType')) {
		requested = false;
		diffService.branchType = currentRoute.paramMap.get('branchType') || '';
	}
	diffService.DiffMode = route.url.some((e) => e.path === 'diff');
	if (!requested) {
		requested = true;
		diffService.diff.subscribe((value) => {
			resolvedSubject.next(value);
		});
	}
	return resolvedSubject.pipe(first());
};
