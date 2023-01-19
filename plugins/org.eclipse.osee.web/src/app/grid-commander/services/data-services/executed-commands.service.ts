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
	combineLatest,
	of,
	tap,
	switchMap,
	take,
	iif,
	takeUntil,
	Subject,
} from 'rxjs';
import { UiService } from '../../../ple-services/ui/ui.service';
import { executedCommand } from '../../types/grid-commander-types/executedCommand';
import {
	Command,
	Parameter,
} from '../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { userHistory } from '../../types/grid-commander-types/userHistory';
import { CheckboxContainerService } from '../command-palette-services/checkbox-container.service';
import { ParameterStringActionService } from '../parameter-services/parameter-string-action.service';
import { ParameterTemplateService } from '../parameter-services/parameter-template.service';
import { CommandGroupOptionsService } from './command-group-options.service';
import { ExecutedCommandsArtifactService } from './executed-commands-artifact.service';
import { UserHistoryService } from './user-history.service';
import { GCBranchIdService } from '../fetch-data-services/gc-branch-id.service';

@Injectable({
	providedIn: 'root',
})
export class ExecutedCommandsService {
	selectedCommand$ = this.commandGroupOptService.selectedCommandObject;
	parameter$ = this.commandGroupOptService.commandsParameter;
	userHistory$ = this.userHistoryService.userHistory$;

	done = new Subject();

	public set doneFx(val: unknown) {
		this.done.next(val);
	}

	constructor(
		private commandGroupOptService: CommandGroupOptionsService,
		private paramTemplateService: ParameterTemplateService,
		private parameterStringActionService: ParameterStringActionService,
		private userHistoryService: UserHistoryService,
		private executedCommandsArtifactService: ExecutedCommandsArtifactService,
		private checkboxContainerService: CheckboxContainerService,
		private branchIdService: GCBranchIdService,
		private uiService: UiService
	) {}

	get updateCommand() {
		return combineLatest([
			this.userHistory$,
			this.parameterStringActionService.executedCommandObject,
			this.parameter$,
		]).pipe(
			take(1),
			switchMap(([userHistory, selectedCommandToUpdate, parameter]) =>
				this.buildParameterizeCommand(parameter).pipe(
					take(1),
					switchMap((executedCommand) =>
						iif(
							() =>
								!this.inExecutedCommandHistory(
									userHistory,
									selectedCommandToUpdate
								),
							//if User's ECH.executedCommands is empty/null or does not include ECA.id
							//create relationship and add the ECA to the ECH
							of(executedCommand).pipe(
								switchMap((executedCommand) =>
									this.executedCommandsArtifactService
										.createCommandAndEstablishHistoryRelationAndAddToHistory(
											this.branchIdService.branchId,
											executedCommand,
											userHistory
										)
										.pipe(
											take(1),
											tap(
												() =>
													(this.uiService.updated =
														true)
											),
											tap(() => this.clearFormOnSubmit())
										)
								)
							),
							//else Modify the ECA -- executedFrequency + 1 and update TimeStamp
							of(selectedCommandToUpdate).pipe(
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
													(this.uiService.updated =
														true)
											),
											tap(() => this.clearFormOnSubmit())
										)
								)
							)
						)
					)
				)
			),
			takeUntil(this.done)
		);
	}

	buildParameterizeCommand(parameter: Parameter) {
		const paramCommand =
			this.paramTemplateService.parameterStringInput.pipe(
				switchMap((userInput) =>
					this.createParameterizedCommandObj(parameter, userInput)
				)
			);
		return combineLatest([this.selectedCommand$, paramCommand]).pipe(
			switchMap(([command, paramCommand]) =>
				this.createNewExecutedCommandObj(command, paramCommand)
			)
		);
	}

	//transform to parameterizedCommand object
	createParameterizedCommandObj(parameter: Parameter, paramValue: string) {
		return of(`{ name: ${parameter.name}, parameterValue: ${paramValue} }`);
	}

	//transform to exCommandObj object to build artifact
	//should pass validation checks prior to new executed command object being created
	createNewExecutedCommandObj(
		command: Command,
		parameterizedCmdObj: string,
		favorite: boolean = false,
		validated: boolean = true
	) {
		return of({
			name: command.name,
			executionFrequency: 1,
			commandTimestamp: new Date().getTime(),
			parameterizedCommand: parameterizedCmdObj,
			favorite: favorite,
			isValidated: validated,
		});
	}

	inExecutedCommandHistory(
		userHistory: userHistory,
		selectedCommand: Partial<executedCommand>
	) {
		const history = userHistory.data;
		const inHistory = history.filter(
			(commandInHistory) => commandInHistory[0] === selectedCommand.id
		);

		if (inHistory.length === 1) {
			return true;
		} else {
			return false;
		}
	}

	clearFormOnSubmit() {
		if (this.checkboxContainerService.clearIsCheckedVal.value == true) {
			this.parameterStringActionService.fromHistory.next(false);
			this.commandGroupOptService.stringToFilterCommandsBy = '';
		}
	}
}
