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
import { BehaviorSubject, Observable } from 'rxjs';
import { artifactHierarchyOptions } from '../types/artifact-explorer.data';

@Injectable({
	providedIn: 'root',
})
export class ArtifactHierarchyOptionsService {
	private optionsSubject: BehaviorSubject<artifactHierarchyOptions> =
		new BehaviorSubject<artifactHierarchyOptions>({ showRelations: false });
	public options$: Observable<artifactHierarchyOptions> =
		this.optionsSubject.asObservable();

	updateOptions(newOptions: artifactHierarchyOptions) {
		this.optionsSubject.next(newOptions);
	}
}
