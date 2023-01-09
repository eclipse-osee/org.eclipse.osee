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
	switchMap,
	take,
	filter,
	share,
	reduce,
	shareReplay,
	map,
	tap,
} from 'rxjs/operators';
import { combineLatest, from, of, OperatorFunction } from 'rxjs';
import {
	changeInstance,
	changeTypeNumber,
	ModificationType,
} from 'src/app/types/change-report/change-report.d';
import { BranchInfoService } from 'src/app/ple-services/http/branch-info.service';
import {
	branchSummary,
	connectionDiffItem,
	DifferenceReport,
	DifferenceReportItem,
	diffItem,
	diffReportSummaryItem,
	diffUrl,
	elementDiffItem,
	enumDiffItem,
	enumSetDiffItem,
	fieldsChanged,
	messageDiffItem,
	nodeDiffItem,
	platformTypeDiffItem,
	structureDiffItem,
	submessageDiffItem,
} from '../../types/DifferenceReport';
import {
	ATTRIBUTETYPEID,
	ATTRIBUTETYPEIDENUM,
} from 'src/app/types/constants/AttributeTypeId.enum';
import { ActionService } from 'src/app/ple-services/http/action.service';
import { DiffReportBranchService } from 'src/app/ple-services/ui/diff/diff-report-branch.service';
import { BranchUIService } from 'src/app/ple-services/ui/branch/branch-ui.service';

@Injectable({
	providedIn: 'root',
})
export class DiffReportService {
	constructor(
		private diffService: DiffReportBranchService,
		private uiService: BranchUIService,
		private branchInfoService: BranchInfoService,
		private actionService: ActionService
	) {}

	private _branchInfo = this.uiService.id.pipe(
		filter((v) => v !== ''),
		take(1),
		switchMap((branchId) =>
			this.branchInfoService.getBranch(branchId).pipe(shareReplay())
		)
	);

	private _parentBranchInfo = this._branchInfo.pipe(
		switchMap((branch) =>
			this.branchInfoService
				.getBranch(branch.parentBranch.id)
				.pipe(share())
		)
	);

	private _branchSummary = combineLatest([
		this._branchInfo,
		this._parentBranchInfo,
	]).pipe(
		switchMap(([branch, parentBranch]) =>
			this.actionService.getAction(branch.associatedArtifact).pipe(
				map((actions) => {
					return {
						pcrNo: actions.length > 0 ? actions[0].AtsId : '',
						description: actions.length > 0 ? actions[0].Name : '',
						compareBranch: parentBranch.name,
					};
				}),
				reduce((acc, curr) => [...acc, curr], [] as branchSummary[])
			)
		)
	);

	private _differenceReport = this.diffService.differenceReport.pipe(
		filter((v) => v !== undefined) as OperatorFunction<
			DifferenceReport | undefined,
			DifferenceReport
		>,
		shareReplay()
	);

	private _nodes = this._differenceReport.pipe(
		switchMap((report) =>
			from(report.nodes).pipe(
				map((node) => {
					let diffItem: DifferenceReportItem =
						report.changeItems[node];
					let item: nodeDiffItem = diffItem.item as nodeDiffItem;
					item.diffInfo = this.getDefaultDiffInfo(diffItem);
					item.color = item.color === '' ? 'Default' : item.color;
					diffItem.changes.every((c) => {
						if (this.isDeleted(c)) {
							item.diffInfo!.deleted = true;
							item.diffInfo!.added = false;
							return false;
						}
						if (item.diffInfo!.added && !this.isAdded(c)) {
							item.diffInfo!.added = false;
						}
						if (
							c.changeType.id !== changeTypeNumber.ARTIFACT_CHANGE
						) {
							if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACENODEADDRESS
							) {
								item.diffInfo!.fieldsChanged['address'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACENODEBGCOLOR
							) {
								item.diffInfo!.fieldsChanged['color'] =
									c.baselineVersion.value || 'Default';
							} else if (
								c.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION
							) {
								item.diffInfo!.fieldsChanged['description'] =
									c.baselineVersion.value || '';
							} else if (
								c.itemTypeId === ATTRIBUTETYPEIDENUM.NAME
							) {
								item.diffInfo!.fieldsChanged['name'] =
									c.baselineVersion.value;
							}
						} else if (
							c.baselineVersion.applicabilityToken &&
							c.baselineVersion.applicabilityToken?.name !==
								c.currentVersion.applicabilityToken?.name
						) {
							item.diffInfo!.fieldsChanged['applicability'] =
								c.baselineVersion.applicabilityToken;
						}
						return true;
					});
					if (
						item.diffInfo.fieldsChanged['applicability'] &&
						diffItem.changes.length === 1
					) {
						item.diffInfo.added = false;
					}
					if (item.diffInfo!.added) {
						item.diffInfo!.fieldsChanged = {} as fieldsChanged;
					}
					return item;
				}),
				filter((item) => !this.isNoChanges(item)),
				reduce((acc, curr) => [...acc, curr], [] as nodeDiffItem[])
			)
		),
		shareReplay(1)
	);

	private _connections = this._differenceReport.pipe(
		switchMap((report) =>
			from(report.connections).pipe(
				map((connection) => {
					let diffItem: DifferenceReportItem =
						report.changeItems[connection];
					let item: connectionDiffItem =
						diffItem.item as connectionDiffItem;
					item.diffInfo = this.getDefaultDiffInfo(diffItem);
					diffItem.changes.every((c) => {
						if (this.isDeleted(c)) {
							item.diffInfo!.deleted = true;
							item.diffInfo!.added = false;
							return false;
						}
						if (item.diffInfo!.added && !this.isAdded(c)) {
							item.diffInfo!.added = false;
						}
						if (
							c.changeType.id != changeTypeNumber.ARTIFACT_CHANGE
						) {
							if (c.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
								item.diffInfo!.fieldsChanged['name'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION
							) {
								item.diffInfo!.fieldsChanged['description'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACETRANSPORTTYPE
							) {
								item.diffInfo!.fieldsChanged['transportType'] =
									c.baselineVersion.value;
							}
						} else if (this.isApplicabilityChange(c)) {
							item.diffInfo!.fieldsChanged['applicability'] =
								c.baselineVersion.applicabilityToken;
						}
						return true;
					});
					if (
						item.diffInfo.fieldsChanged['applicability'] &&
						diffItem.changes.length === 1
					) {
						item.diffInfo.added = false;
					}
					if (item.diffInfo!.added || item.diffInfo!.deleted) {
						item.diffInfo!.fieldsChanged = {} as fieldsChanged;
					}
					return item;
				}),
				filter((item) => !this.isNoChanges(item)),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as connectionDiffItem[]
				)
			)
		),
		shareReplay(1)
	);

	private _messages = combineLatest([
		this._differenceReport,
		this._branchInfo,
	]).pipe(
		switchMap(([report, branchInfo]) =>
			from(report.messages).pipe(
				map((message) => {
					let diffItem: DifferenceReportItem =
						report.changeItems[message];
					let item: messageDiffItem =
						diffItem.item as messageDiffItem;
					item.diffInfo = this.getDefaultDiffInfo(diffItem);
					diffItem.changes.every((c) => {
						if (this.isDeleted(c)) {
							item.diffInfo!.deleted = true;
							item.diffInfo!.added = false;
							return false;
						}
						if (item.diffInfo!.added && !this.isAdded(c)) {
							item.diffInfo!.added = false;
						}
						if (
							c.changeType.id != changeTypeNumber.ARTIFACT_CHANGE
						) {
							if (c.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
								item.diffInfo!.fieldsChanged['name'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION
							) {
								item.diffInfo!.fieldsChanged['description'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEMESSAGENUMBER
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceMessageNumber'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEPERIODICITY
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceMessagePeriodicity'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERATE
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceMessageRate'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEMESSAGEWRITEACCESS
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceMessageWriteAccess'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEMESSAGETYPE
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceMessageType'
								] = c.baselineVersion.value;
							}
						} else if (this.isApplicabilityChange(c)) {
							item.diffInfo!.fieldsChanged['applicability'] =
								c.baselineVersion.applicabilityToken;
						}
						return true;
					});
					if (
						item.diffInfo.fieldsChanged['applicability'] &&
						diffItem.changes.length === 1
					) {
						item.diffInfo.added = false;
					}
					if (item.diffInfo!.added || item.diffInfo!.deleted) {
						item.diffInfo!.fieldsChanged = {} as fieldsChanged;
					}
					return item;
				}),
				filter((item) => !this.isNoChanges(item)),
				reduce((acc, curr) => [...acc, curr], [] as messageDiffItem[])
			)
		),
		shareReplay(1)
	);

	private _subMessages = combineLatest([
		this._differenceReport,
		this._branchInfo,
	]).pipe(
		switchMap(([report, branchInfo]) =>
			from(report.subMessages).pipe(
				map((submessage) => {
					let diffItem: DifferenceReportItem =
						report.changeItems[submessage];
					let item: submessageDiffItem =
						diffItem.item as submessageDiffItem;
					item.diffInfo = this.getDefaultDiffInfo(diffItem);
					diffItem.changes.every((c) => {
						if (this.isDeleted(c)) {
							item.diffInfo!.deleted = true;
							item.diffInfo!.added = false;
							return false;
						}
						if (item.diffInfo!.added && !this.isAdded(c)) {
							item.diffInfo!.added = false;
						}
						if (
							c.changeType.id != changeTypeNumber.ARTIFACT_CHANGE
						) {
							if (c.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
								item.diffInfo!.fieldsChanged['name'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION
							) {
								item.diffInfo!.fieldsChanged['description'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACESUBMESSAGENUMBER
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceSubMessageNumber'
								] = c.baselineVersion.value;
							}
						} else if (this.isApplicabilityChange(c)) {
							item.diffInfo!.fieldsChanged['applicability'] =
								c.baselineVersion.applicabilityToken;
						}
						return true;
					});
					if (
						item.diffInfo.fieldsChanged['applicability'] &&
						diffItem.changes.length === 1
					) {
						item.diffInfo.added = false;
					}
					if (item.diffInfo!.added || item.diffInfo!.deleted) {
						item.diffInfo!.fieldsChanged = {} as fieldsChanged;
					}
					return item;
				}),
				filter((item) => !this.isNoChanges(item)),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as submessageDiffItem[]
				)
			)
		),
		shareReplay(1)
	);

	private _enumSets = this._differenceReport.pipe(
		switchMap((report) =>
			from(report.enumSets).pipe(
				map((enumSet) => {
					let diffItem: DifferenceReportItem =
						report.changeItems[enumSet];
					let item: enumSetDiffItem =
						diffItem.item as enumSetDiffItem;
					item.diffInfo = this.getDefaultDiffInfo(diffItem);
					diffItem.changes.every((c) => {
						if (this.isDeleted(c)) {
							item.diffInfo!.deleted = true;
							item.diffInfo!.added = false;
							return false;
						}
						if (item.diffInfo!.added && !this.isAdded(c)) {
							item.diffInfo!.added = false;
						}
						if (
							c.changeType.id != changeTypeNumber.ARTIFACT_CHANGE
						) {
							if (
								c.itemTypeId === ATTRIBUTETYPEIDENUM.NAME &&
								item.enumerations.some((e) => e.id === c.artId)
							) {
								item.diffInfo!.fieldsChanged['name'] =
									c.baselineVersion.value;
							}
						} else if (this.isApplicabilityChange(c)) {
							item.diffInfo!.fieldsChanged['applicability'] =
								c.baselineVersion.applicabilityToken;
						}
						return true;
					});
					if (
						item.diffInfo.fieldsChanged['applicability'] &&
						diffItem.changes.length === 1
					) {
						item.diffInfo.added = false;
					}
					if (item.diffInfo!.added || item.diffInfo!.deleted) {
						item.diffInfo!.fieldsChanged = {} as fieldsChanged;
					}
					return item;
				}),
				filter((item) => !this.isNoChanges(item)),
				reduce((acc, curr) => [...acc, curr], [] as enumSetDiffItem[])
			)
		),
		shareReplay(1)
	);

	private _elements = combineLatest([
		this._differenceReport,
		this._enumSets,
	]).pipe(
		switchMap(([report, enumSets]) =>
			from(report.elements).pipe(
				map((element) => {
					let diffItem: DifferenceReportItem =
						report.changeItems[element];
					let item: elementDiffItem =
						diffItem.item as elementDiffItem;
					item.diffInfo = this.getDefaultDiffInfo(diffItem);
					diffItem.changes.every((c) => {
						if (this.isDeleted(c)) {
							item.diffInfo!.deleted = true;
							item.diffInfo!.added = false;
							return false;
						}
						if (item.diffInfo!.added && !this.isAdded(c)) {
							item.diffInfo!.added = false;
						}
						if (
							c.changeType.id === changeTypeNumber.RELATION_CHANGE
						) {
							let pTypeDiff: DifferenceReportItem =
								report.changeItems[c.artIdB];
							if (pTypeDiff) {
								let pTypeDiffItem: platformTypeDiffItem =
									pTypeDiff.item as platformTypeDiffItem;
								if (
									item.logicalType !==
									pTypeDiffItem.interfaceLogicalType
								) {
									item.diffInfo!.fieldsChanged[
										'logicalType'
									] = pTypeDiffItem.interfaceLogicalType;
								}
								if (
									item.elementSizeInBits + '' !==
									pTypeDiffItem.interfacePlatformTypeBitSize
								) {
									item.diffInfo!.fieldsChanged[
										'elementSizeInBits'
									] =
										pTypeDiffItem.interfacePlatformTypeBitSize;
								}
								if (
									item.interfaceDefaultValue !==
									pTypeDiffItem.interfaceDefaultValue
								) {
									item.diffInfo!.fieldsChanged[
										'interfaceDefaultValue'
									] = pTypeDiffItem.interfaceDefaultValue;
								}
								if (
									item.interfacePlatformTypeMaxval !==
									pTypeDiffItem.interfacePlatformTypeMaxval
								) {
									item.diffInfo!.fieldsChanged[
										'interfacePlatformTypeMaxval'
									] =
										pTypeDiffItem.interfacePlatformTypeMaxval;
								}
								if (
									item.interfacePlatformTypeMinval !==
									pTypeDiffItem.interfacePlatformTypeMinval
								) {
									item.diffInfo!.fieldsChanged[
										'interfacePlatformTypeMinval'
									] =
										pTypeDiffItem.interfacePlatformTypeMinval;
								}
								if (
									item.units !==
									pTypeDiffItem.interfacePlatformTypeUnits
								) {
									item.diffInfo!.fieldsChanged['units'] =
										pTypeDiffItem.interfacePlatformTypeUnits;
								}
							}
						} else if (
							c.changeType.id !== changeTypeNumber.ARTIFACT_CHANGE
						) {
							if (
								c.itemTypeId === ATTRIBUTETYPEIDENUM.NAME &&
								c.artId === element
							) {
								item.diffInfo!.fieldsChanged['name'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION
							) {
								item.diffInfo!.fieldsChanged['description'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEELEMENTALTERABLE
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceElementAlterable'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS
							) {
								item.diffInfo!.fieldsChanged['units'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE
							) {
								item.diffInfo!.fieldsChanged[
									'elementSizeInBits'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL
							) {
								item.diffInfo!.fieldsChanged[
									'interfacePlatformTypeMinval'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL
							) {
								item.diffInfo!.fieldsChanged[
									'interfacePlatformTypeMaxval'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceDefaultValue'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId === ATTRIBUTETYPEIDENUM.NOTES
							) {
								item.diffInfo!.fieldsChanged['notes'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEELEMENTSTART
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceElementIndexStart'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEELEMENTEND
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceElementIndexEnd'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEENUMLITERAL
							) {
								item.diffInfo!.fieldsChanged['enumLiteral'] =
									c.baselineVersion.value;
							}
						} else if (this.isApplicabilityChange(c)) {
							item.diffInfo!.fieldsChanged['applicability'] =
								c.baselineVersion.applicabilityToken;
						}
						return true;
					});

					if (item.logicalType === 'enumeration') {
						enumSets.forEach((enumSet) => {
							let enumDiffItem: DifferenceReportItem =
								report.changeItems[enumSet.id];
							if (
								enumDiffItem.parents.includes(
									'' + item.platformTypeId
								)
							) {
								item.enumeration = enumSet.diffInfo?.deleted
									? ''
									: this.getEnumerationString(enumSet);
								if (enumDiffItem.changes.length > 0) {
									item.diffInfo!.fieldsChanged[
										'enumeration'
									] = enumSet.diffInfo?.added
										? ''
										: this.getBaselineEnumerationString(
												enumDiffItem,
												enumSet
										  );
								}
							}
						});
					}

					if (
						item.diffInfo.fieldsChanged['applicability'] &&
						diffItem.changes.length === 1
					) {
						item.diffInfo.added = false;
					}
					if (item.diffInfo!.added || item.diffInfo!.deleted) {
						item.diffInfo!.fieldsChanged = {} as fieldsChanged;
					}
					return item;
				}),
				filter((item) => !this.isNoChanges(item)),
				reduce((acc, curr) => [...acc, curr], [] as elementDiffItem[])
			)
		),
		shareReplay(1)
	);

	private _structures = this._differenceReport.pipe(
		switchMap((report) =>
			from(report.structures).pipe(
				map((structure) => {
					let diffItem: DifferenceReportItem =
						report.changeItems[structure];
					let item: structureDiffItem =
						diffItem.item as structureDiffItem;
					item.diffInfo = this.getDefaultDiffInfo(diffItem);
					item.elementChanges = [];
					diffItem.changes.every((c) => {
						if (this.isDeleted(c)) {
							item.diffInfo!.deleted = true;
							item.diffInfo!.added = false;
							return false;
						}
						if (item.diffInfo!.added && !this.isAdded(c)) {
							item.diffInfo!.added = false;
						}
						if (
							c.changeType.id === changeTypeNumber.RELATION_CHANGE
						) {
							let elementDiff: DifferenceReportItem =
								report.changeItems[c.artIdB];
							if (elementDiff) {
								let elementCopy: elementDiffItem = JSON.parse(
									JSON.stringify(elementDiff.item)
								);
								elementCopy.diffInfo =
									this.getDefaultDiffInfo(elementDiff);
								elementCopy.diffInfo.added = !c.deleted;
								elementCopy.diffInfo.deleted = c.deleted;
								item.elementChanges?.push(elementCopy);
							}
						} else if (
							c.changeType.id != changeTypeNumber.ARTIFACT_CHANGE
						) {
							if (c.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
								item.diffInfo!.fieldsChanged['name'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION
							) {
								item.diffInfo!.fieldsChanged['description'] =
									c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEMINSIMULTANEITY
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceMinSimultaneity'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACEMAXSIMULTANEITY
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceMaxSimultaneity'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACETASKFILETYPE
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceTaskFileType'
								] = c.baselineVersion.value;
							} else if (
								c.itemTypeId ===
								ATTRIBUTETYPEIDENUM.INTERFACESTRUCTURECATEGORY
							) {
								item.diffInfo!.fieldsChanged[
									'interfaceStructureCategory'
								] = c.baselineVersion.value;
							}
						}
						return true;
					});
					if (item.diffInfo!.added || item.diffInfo!.deleted) {
						item.diffInfo!.fieldsChanged = {} as fieldsChanged;
					}
					return item;
				}),
				filter(
					(item) =>
						!this.isNoChanges(item) ||
						item.elementChanges!.length > 0
				),
				reduce((acc, curr) => [...acc, curr], [] as structureDiffItem[])
			)
		),
		shareReplay(1)
	);

	private structsWithElements = [] as structureDiffItem[];

	// This should be cleaned up to use rxjs operators rather than doing everything in the tap
	private _structuresWithElements = combineLatest([
		this._differenceReport,
		this._structures,
		this._elements,
	]).pipe(
		switchMap(([report, structures, elements]) =>
			of(elements).pipe(
				tap((_) => {
					// Initialize the structs list to a copy of the structures array in order to not modify the original structures
					this.structsWithElements = [
						...JSON.parse(JSON.stringify(structures)),
					];
					elements.forEach((e) => {
						let elementDiff = report.changeItems[e.id];
						elementDiff.parents.forEach((parent) => {
							let structs = this.structsWithElements.filter(
								(s) => s.id === parent
							);
							let struct: structureDiffItem;
							if (structs.length === 0) {
								struct = report.changeItems[parent]
									?.item as structureDiffItem;
								struct.elementChanges = [];
								struct.diffInfo = {
									added: false,
									deleted: false,
									fieldsChanged: {} as fieldsChanged,
									url: {},
								};
								this.structsWithElements.push(struct);
							} else {
								struct = structs[0];
							}
							if (
								struct.elementChanges?.filter(
									(ec) => ec.id === e.id
								).length == 0
							) {
								struct.elementChanges?.push(e);
							}
						});
					});
				}),
				map((_) => this.structsWithElements)
			)
		),
		shareReplay(1)
	);

	// The report summary only shows differences in
	private _diffReportSummary = this._structuresWithElements.pipe(
		switchMap((structures) =>
			from(structures).pipe(
				map((structure) => {
					let summaryItem: diffReportSummaryItem = {
						id: structure.id,
						changeType: 'Structure',
						action: structure.diffInfo?.added
							? 'Added'
							: structure.diffInfo?.deleted
							? 'Deleted'
							: 'Edited',
						name: structure.name,
						details: [],
					};
					if (
						Object.keys(structure.diffInfo!.fieldsChanged).length >
						0
					) {
						summaryItem.details.push('Attribute changes');
					}
					if (structure.elementChanges!.length > 0) {
						summaryItem.details.push('Element changes');
					}
					return summaryItem;
				}),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as diffReportSummaryItem[]
				)
			)
		)
	);

	private isDeleted(change: changeInstance) {
		return (
			change.changeType.id === changeTypeNumber.ARTIFACT_CHANGE &&
			change.currentVersion.modType === ModificationType.DELETED
		);
	}

	private isAdded(change: changeInstance) {
		return (
			change.netChange.modType === ModificationType.NEW ||
			change.netChange.modType === ModificationType.INTRODUCED
		);
	}

	private isApplicabilityChange(change: changeInstance) {
		return (
			change.baselineVersion.applicabilityToken &&
			change.baselineVersion.applicabilityToken?.name !==
				change.currentVersion.applicabilityToken?.name
		);
	}

	private getDefaultDiffInfo(item: DifferenceReportItem) {
		// Default added to true if there are changes, false if there are no changes.
		return {
			added:
				item.changes.filter(
					(c) => c.changeType.id !== changeTypeNumber.RELATION_CHANGE
				).length > 0,
			deleted: false,
			fieldsChanged: {} as fieldsChanged,
			url: { label: '', url: '' } as diffUrl,
		};
	}

	private getEnumerationString(enumSet: enumSetDiffItem) {
		// TODO - Planning on using the enum set description field to get the enum string instead.
		let enumString = '';
		enumSet.enumerations.forEach((e) => {
			enumString +=
				e.ordinal + '=' + e.name + ' [' + e.applicability.name + ']; ';
		});
		return enumString;
	}

	private getBaselineEnumerationString(
		enumDiffItem: DifferenceReportItem,
		enumSet: enumSetDiffItem
	) {
		// TODO - Planning on using the enum set description field to get the enum string instead.
		let enums: enumDiffItem[] = enumSet.enumerations;
		enumDiffItem.changes.map((c) => {
			if (
				c.changeType.id === changeTypeNumber.ARTIFACT_CHANGE &&
				c.currentVersion.modType === ModificationType.DELETED
			) {
				enums = enums.filter((e) => e.id !== c.artId);
			}
		});
		let enumString = '';
		enums.forEach((e) => {
			enumString +=
				e.ordinal + '=' + e.name + ' [' + e.applicability.name + ']; ';
		});
		return enumString;
	}

	/**
	 * Determines if an item should display in the diff table. true if the item
	 * has no fields changed and is not added or deleted, or if the item has an invalid id.
	 * @param item
	 * @returns
	 */
	private isNoChanges(item: diffItem) {
		return (
			item.id === '-1' ||
			(!item.diffInfo?.deleted &&
				!item.diffInfo?.added &&
				item.diffInfo?.fieldsChanged &&
				Object.keys(item.diffInfo?.fieldsChanged).length === 0 &&
				Object.getPrototypeOf(item.diffInfo?.fieldsChanged) ===
					Object.prototype)
		);
	}

	// GETTERS
	get branchInfo() {
		return this._branchInfo;
	}

	get parentBranchInfo() {
		return this._parentBranchInfo;
	}

	get branchSummary() {
		return this._branchSummary;
	}

	get diffReportSummary() {
		return this._diffReportSummary;
	}

	get diffReport() {
		return this._differenceReport;
	}

	get nodes() {
		return this._nodes;
	}

	get connections() {
		return this._connections;
	}

	get messages() {
		return this._messages;
	}

	get submessages() {
		return this._subMessages;
	}

	get structures() {
		return this._structures;
	}

	get structuresWithElements() {
		return this._structuresWithElements;
	}

	get elements() {
		return this._elements;
	}
}
