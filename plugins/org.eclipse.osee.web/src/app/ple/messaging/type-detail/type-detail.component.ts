/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { AsyncPipe, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TypesUIService } from '@osee/messaging/shared/services';
import { PlatformTypeCardComponent } from '@osee/messaging/shared/main-content';
import { TypeDetailService } from '@osee/messaging/type-detail';
import { filter, map, switchMap } from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-type-detail',
	templateUrl: './type-detail.component.html',
	styles: [],
	standalone: true,
	imports: [NgIf, AsyncPipe, PlatformTypeCardComponent],
})
export class TypeDetailComponent implements OnInit {
	type = this._typeDetail.typeId.pipe(
		filter((typeId) => typeId !== ''),
		switchMap((typeId) => this._typeService.getType(typeId))
	);
	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private _typeDetail: TypeDetailService,
		private _typeService: TypesUIService
	) {}

	ngOnInit(): void {
		this.route.paramMap
			.pipe(
				map((params) => {
					this._typeDetail.idValue = params.get('branchId') || '';
					this._typeDetail.typeValue =
						(params.get('branchType') as
							| 'working'
							| 'baseline'
							| '') || '';
					this._typeDetail.type = params.get('typeId') || '';
				})
			)
			.subscribe();
	}
}
export default TypeDetailComponent;
