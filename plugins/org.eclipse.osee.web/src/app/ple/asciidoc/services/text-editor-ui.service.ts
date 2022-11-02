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
import { UiService } from 'src/app/ple-services/ui/ui.service';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class TextEditorUiService {
	private _artifactId: BehaviorSubject<string> = new BehaviorSubject<string>(
		'0'
	);
	private _userInputUpdate$ = new BehaviorSubject<string>('');
	private _userInput = this._userInputUpdate$.asObservable();
	// beh subject also observable, but has unnecessary extra functionality
	// add new functionality and grab what you need from UI service
	private _id = this._artifactId.asObservable();

	constructor(private uiServe: UiService) {}

	get artifactId() {
		return this._id;
	}

	set artifactIdValue(value: string) {
		this._artifactId.next(value);
	}

	get type() {
		return this.uiServe.type;
	}

	set idValue(id: string | number) {
		this.uiServe.idValue = id;
	}

	get id() {
		return this.uiServe.id;
	}

	get update() {
		return this.uiServe.update;
	}

	set updated(value: boolean) {
		this.uiServe.updated = value;
	}

	set typeValue(branchType: string) {
		this.uiServe.typeValue = branchType;
	}

	get userInput() {
		return this._userInput;
	}

	set userInputUpdate(value: string) {
		this._userInputUpdate$.next(value);
	}
}
