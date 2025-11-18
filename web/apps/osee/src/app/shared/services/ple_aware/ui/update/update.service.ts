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
import { Injectable, signal, WritableSignal } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Injectable({
	providedIn: 'root',
})
export class UpdateService {
	private _updateRequired: Subject<boolean> = new Subject<boolean>();
	private _updateArtifact = new Subject<string>();

	private _updateOccurred = this._updateRequired.pipe(debounceTime(100));
	private _updateCount: WritableSignal<number> = signal(0);

	get update() {
		return this._updateOccurred;
	}

	get updateCount() {
		return this._updateCount.asReadonly();
	}

	set updated(value: boolean) {
		this._updateRequired.next(value);
		this._updateCount.update((count) => count + 1);
	}

	get updateArtifact() {
		return this._updateArtifact;
	}

	set updatedArtifact(value: string) {
		this._updateArtifact.next(value);
	}
}
