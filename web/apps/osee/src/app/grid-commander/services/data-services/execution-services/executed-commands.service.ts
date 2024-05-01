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
import {
	BehaviorSubject,
	combineLatest,
	debounceTime,
	iif,
	of,
	Subject,
	switchMap,
	take,
	takeUntil,
	tap,
} from 'rxjs';
import { UiService } from '@osee/shared/services';
import { executedCommand } from '../../../types/grid-commander-types/executedCommand';
import { GCBranchIdService } from '../../fetch-data-services/branch/gc-branch-id.service';
import { CommandGroupOptionsService } from '../commands/command-group-options.service';
import { UserHistoryService } from '../history/user-history.service';
import { CommandFromUserHistoryService } from '../selected-command-data/command-from-history/command-from-user-history.service';
import { ParameterDataService } from '../selected-command-data/parameter-data/parameter-data.service';
import { SelectedCommandDataService } from '../selected-command-data/selected-command-data.service';
import { ExecutedCommandsArtifactService } from './executed-commands-artifact.service';

@Injectable({
	providedIn: 'root',
})
export class ExecutedCommandsService {
	isFromHistory = this.commandFromUserHistoryService.fromHistoryAsObservable;
	executedCommandDetails$ =
		this.commandFromUserHistoryService.executedCommandDetails;
	parameter$ = this.parameterDataService.parameter$;

	userHistory$ = this.userHistoryService.userHistory$;

	commandHasBeenSelected$ =
		this.selectedCommandDataService.selectedCommandNotEmpty;

	private _modifiedExecutedCommandDetails = new BehaviorSubject<
		Partial<executedCommand>
	>({});

	done = new Subject();

	public set doneFx(val: unknown) {
		this.done.next(val);
	}

	constructor(
		private commandGroupOptService: CommandGroupOptionsService,
		private selectedCommandDataService: SelectedCommandDataService,
		private parameterDataService: ParameterDataService,
		private commandFromUserHistoryService: CommandFromUserHistoryService,
		private userHistoryService: UserHistoryService,
		private executedCommandsArtifactService: ExecutedCommandsArtifactService,
		private branchIdService: GCBranchIdService,
		private uiService: UiService
	) {}

	get updateCommand() {
		return combineLatest([
			this.userHistory$,
			this.isFromHistory,
			this.executedCommandDetails$,
			this.parameter$,
			this.commandHasBeenSelected$,
		]).pipe(
			take(1),
			debounceTime(1000),
			switchMap(
				([userHistory, fromHistory, executedCmdDetails, parameter]) =>
					iif(
						() => fromHistory,
						//else Modify the artifact -- executedFrequency + 1 and update TimeStamp
						of(executedCmdDetails).pipe(
							switchMap((commandObj) =>
								this.executedCommandsArtifactService
									.modifyExistingCommandArtifact(
										this.branchIdService.branchId,
										commandObj
									)
									.pipe(
										take(1),
										tap(
											() =>
												(this.uiService.updated = true)
										),
										tap(() => this.clearFormOnSubmit())
									)
							)
						),

						//if not from history create relationship and add the artifact to the history
						of(parameter).pipe(
							switchMap((parameter) =>
								this.commandFromUserHistoryService.buildParameterizeCommand(
									parameter
								)
							),
							switchMap((parameterizedObj) =>
								this.executedCommandsArtifactService
									.createCommandAndEstablishHistoryRelationAndAddToHistory(
										this.branchIdService.branchId,
										parameterizedObj,
										userHistory
									)
									.pipe(
										take(1),
										tap(
											() =>
												(this.uiService.updated = true)
										),
										tap(() => this.clearFormOnSubmit())
									)
							)
						)
					)
			),
			takeUntil(this.done)
		);
	}

	clearFormOnSubmit() {
		this.commandFromUserHistoryService.fromHistory = false;
		this.commandGroupOptService.stringToFilterCommandsBy = '';
	}

	public get modifiedExecutedCommandDetails() {
		return this._modifiedExecutedCommandDetails.value;
	}

	public set modifiedExecutedCommandDetails(value: Partial<executedCommand>) {
		this._modifiedExecutedCommandDetails.next(value);
	}
}
