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
import { Injectable } from '@angular/core';
import { affectedArtifact } from '@osee/messaging/shared/types';
import { iif, of } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { AffectedArtifactService } from '../http/affected-artifact.service';

@Injectable({
	providedIn: 'root',
})
export class BranchedAffectedArtifactService {
	constructor(
		private service: AffectedArtifactService,
		private ui: UiService
	) {}

	getEnumSetsByEnums(enumId: string | number) {
		return this.ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			switchMap((id) =>
				iif(
					() => id !== '',
					this.service.getEnumSetsByEnums(id, enumId),
					of<affectedArtifact[]>([])
				)
			)
		);
	}

	getPlatformTypesByEnumSet(enumSetId: string | number) {
		return this.ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			switchMap((id) =>
				iif(
					() => id !== '',
					this.service.getPlatformTypesByEnumSet(id, enumSetId),
					of<affectedArtifact[]>([])
				)
			)
		);
	}

	getElementsByType(typeId: string | number) {
		return this.ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			switchMap((id) =>
				iif(
					() => id !== '',
					this.service.getElementsByType(id, typeId),
					of<affectedArtifact[]>([])
				)
			)
		);
	}

	getStructuresByElement(elementId: string | number) {
		return this.ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			switchMap((id) =>
				iif(
					() => id !== '',
					this.service.getStructuresByElement(id, elementId),
					of<affectedArtifact[]>([])
				)
			)
		);
	}

	getSubMessagesByStructure(structureId: string | number) {
		return this.ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			switchMap((id) =>
				iif(
					() => id !== '',
					this.service.getSubMessagesByStructure(id, structureId),
					of<affectedArtifact[]>([])
				)
			)
		);
	}

	getMessagesBySubMessage(subMessageId: string | number) {
		return this.ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			switchMap((id) =>
				iif(
					() => id !== '',
					this.service.getMessagesBySubMessage(id, subMessageId),
					of<affectedArtifact[]>([])
				)
			)
		);
	}
}
