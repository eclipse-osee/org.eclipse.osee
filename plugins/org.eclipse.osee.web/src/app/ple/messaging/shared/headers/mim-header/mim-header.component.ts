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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import {
	MimRouteService,
	SharedConnectionUIService,
	SharedStructureUIService,
} from '@osee/messaging/shared/services';
import { combineLatest, iif, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CurrentBranchInfoService } from '@osee/shared/services';

@Component({
	selector: 'osee-messaging-header',
	templateUrl: './mim-header.component.html',
	styles: [],
	standalone: true,
	imports: [MatButtonModule, RouterLink, NgIf, NgFor, AsyncPipe],
})
export class MimHeaderComponent {
	route = combineLatest([
		this._routeService.type,
		this._routeService.id,
		this._routeService.viewId,
		this._routeService.connectionId,
		this._routeService.messageId,
		this._routeService.submessageId,
		this._routeService.submessageToStructureBreadCrumbs,
		this._routeService.singleStructureId,
	]).pipe(
		switchMap(
			([
				type,
				id,
				viewId,
				connection,
				message,
				submessage,
				submessageToStructureBreadCrumbs,
				singleStructureId,
			]) =>
				iif(
					() => type !== '',
					iif(
						() => id != '',
						this._currentBranchService.currentBranchDetail.pipe(
							switchMap((detail) =>
								iif(
									() =>
										connection !== '0' && connection !== '',
									this._connectionService.connection.pipe(
										switchMap((connectionDetails) =>
											iif(
												() => message !== '',
												iif(
													() => submessage !== '',
													iif(
														() =>
															singleStructureId !==
															'',
														this._structureService.structure.pipe(
															switchMap(
																(structure) =>
																	of([
																		{
																			displayName:
																				type,
																			routerLink:
																				'/ple/messaging/' +
																				'connections' +
																				'/' +
																				type,
																		},
																		{
																			displayName:
																				detail.name,
																			routerLink:
																				'/ple/messaging/' +
																				'connections' +
																				'/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				viewId +
																				'/',
																		},
																		{
																			displayName:
																				connectionDetails.name,
																			routerLink:
																				'/ple/messaging/' +
																				'connections/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				viewId +
																				'/' +
																				connection +
																				'/messages',
																		},
																		{
																			displayName:
																				submessageToStructureBreadCrumbs,
																			routerLink:
																				'/ple/messaging/' +
																				'connections/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				viewId +
																				'/' +
																				connection +
																				'/messages/' +
																				message +
																				'/' +
																				submessage +
																				'/elements',
																		},
																		{
																			displayName:
																				structure.name,
																			routerLink:
																				'/ple/messaging/' +
																				'connections/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				viewId +
																				'/' +
																				connection +
																				'/messages/' +
																				message +
																				'/' +
																				submessage +
																				'/elements/' +
																				singleStructureId,
																		},
																	])
															)
														), //submessage,
														of([
															{
																displayName:
																	type,
																routerLink:
																	'/ple/messaging/' +
																	'connections' +
																	'/' +
																	type,
															},
															{
																displayName:
																	detail.name,
																routerLink:
																	'/ple/messaging/' +
																	'connections' +
																	'/' +
																	type +
																	'/' +
																	id +
																	'/' +
																	viewId +
																	'/',
															},
															{
																displayName:
																	connectionDetails.name,
																routerLink:
																	'/ple/messaging/' +
																	'connections/' +
																	type +
																	'/' +
																	id +
																	'/' +
																	viewId +
																	'/' +
																	connection +
																	'/messages',
															},
															{
																displayName:
																	submessageToStructureBreadCrumbs,
																routerLink:
																	'/ple/messaging/' +
																	'connections/' +
																	type +
																	'/' +
																	id +
																	'/' +
																	viewId +
																	'/' +
																	connection +
																	'/messages/' +
																	message +
																	'/' +
																	submessage +
																	'/elements',
															},
														]) //submessage)
													),
													of() //message, DNE
												),
												of([
													{
														displayName: type,
														routerLink:
															'/ple/messaging/' +
															'connections' +
															'/' +
															type,
													},
													{
														displayName:
															detail.name,
														routerLink:
															'/ple/messaging/' +
															'connections' +
															'/' +
															type +
															'/' +
															id +
															'/' +
															viewId +
															'/',
													},
													{
														displayName:
															connectionDetails.name,
														routerLink:
															'/ple/messaging/' +
															'connections/' +
															type +
															'/' +
															id +
															'/' +
															viewId +
															'/' +
															connection +
															'/messages',
													},
												])
											)
										)
									),
									of([
										{
											displayName: type,
											routerLink:
												'/ple/messaging/' +
												'connections' +
												'/' +
												type,
										},
										{
											displayName: detail.name,
											routerLink: type + '/' + id,
										},
									])
								)
							)
						),
						of([{ displayName: type, routerLink: type }])
					),
					of()
				)
		)
	);

	constructor(
		private _routeService: MimRouteService,
		private _currentBranchService: CurrentBranchInfoService,
		private _connectionService: SharedConnectionUIService,
		private _structureService: SharedStructureUIService
	) {}
}

export default MimHeaderComponent;
