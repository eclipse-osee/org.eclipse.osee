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
import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { Observable, takeUntil } from 'rxjs';
import { structure, structureWithChanges } from '../../types/structure';
import { CurrentStructureService } from '../../services/ui/current-structure.service';
import { STRUCTURE_SERVICE_TOKEN } from '../../tokens/injection/structure/token';
import { Inject } from '@angular/core';
export class StructureDataSource extends DataSource<
	structure | structureWithChanges
> {
	constructor(
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structureService: CurrentStructureService
	) {
		super();
	}
	connect(
		collectionViewer: CollectionViewer
	): Observable<readonly (structure | structureWithChanges)[]> {
		return this.structureService.structures.pipe(
			takeUntil(this.structureService.done)
		);
	}

	disconnect(collectionViewer: CollectionViewer): void {}
}
