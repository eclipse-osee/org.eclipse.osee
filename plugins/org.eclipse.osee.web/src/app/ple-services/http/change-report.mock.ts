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
import {
  changeInstance,
  ignoreType,
  changeTypeNumber,
  changeTypeEnum,
  ModificationType,
} from 'src/app/types/change-report/change-report';
import { RelationTypeId } from 'src/app/types/constants/RelationTypeId.enum';

export const changeReportMock: changeInstance[] = [
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      typeId: 2834799904,
      notAttributeChange: true,
      notRelationChange: true,
      idIntValue: 111,
      idString: '111',
    },
    artId: '10',
    itemId: '1234',
    itemTypeId: '7890',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '-1',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '-1',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '-1',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '-1',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '-1',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '2',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201289',
    itemId: '10312',
    itemTypeId: '1152921504606847089',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20606',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="ABCkGXcC9BtOVW8ceggA,AAkKSN_MmEVt8yIw4sQA,ABEYW415xCGFe1Wi1NwA,ABIN5ZrPVFEVTDcmr9gA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20618',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="ABCkGXcC9BtOVW8ceggA,AAkKSN_MmEVt8yIw4sQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20636',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="ABCkGXcC9BtOVW8ceggA,AAkKSN_MmEVt8yIw4sQA,ABI4Dl_6DDaQ4SDceEQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20606',
      modType: ModificationType.MODIFIED,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20636',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="ABCkGXcC9BtOVW8ceggA,AAkKSN_MmEVt8yIw4sQA,ABI4Dl_6DDaQ4SDceEQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '111',
      idIntValue: 111,
    },
    artId: '201298',
    itemId: '201298',
    itemTypeId: '126164394421696908',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20569',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20569',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '3298521940448053542',
        name: 'Config = Product C',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20569',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20569',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '3298521940448053542',
        name: 'Config = Product C',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201297',
    itemId: '10324',
    itemTypeId: '2455059983007225763',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20563',
      modType: ModificationType.NEW,
      value: '5',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20620',
      modType: ModificationType.MODIFIED,
      value: '10',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20563',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20620',
      modType: ModificationType.MODIFIED,
      value: '10',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '111',
      idIntValue: 111,
    },
    artId: '201302',
    itemId: '201302',
    itemTypeId: '126164394421696908',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20602',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20602',
      modType: ModificationType.DELETED,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20602',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20602',
      modType: ModificationType.DELETED,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: true,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.RELATION_CHANGE,
      name: changeTypeEnum.RELATION_CHANGE,
      notRelationChange: false,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '333',
      idIntValue: 333,
    },
    artId: '201289',
    itemId: '5141',
    itemTypeId: {
      id: RelationTypeId.INTERFACESUBMESSAGECONTENT,
      name: 'Interface Message SubMessage Content',
      order: 'USER_DEFINED',
      ordered: true,
      multiplicity: 'MANY_TO_MANY',
      idString: '2455059983007225780',
      idIntValue: 225187764,
    },
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20592',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20592',
      modType: ModificationType.DELETED,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20592',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20592',
      modType: ModificationType.DELETED,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '201300',
    deleted: true,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201298',
    itemId: '10326',
    itemTypeId: '2455059983007225769',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20594',
      modType: ModificationType.MODIFIED,
      value: '68',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20619',
      modType: ModificationType.MODIFIED,
      value: '67',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20594',
      modType: ModificationType.MODIFIED,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20619',
      modType: ModificationType.MODIFIED,
      value: '67',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.RELATION_CHANGE,
      name: changeTypeEnum.RELATION_CHANGE,
      notRelationChange: false,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '333',
      idIntValue: 333,
    },
    artId: '201289',
    itemId: '5143',
    itemTypeId: {
      id: RelationTypeId.INTERFACESUBMESSAGECONTENT,
      name: 'Interface Message SubMessage Content',
      order: 'USER_DEFINED',
      ordered: true,
      multiplicity: 'MANY_TO_MANY',
      idString: '2455059983007225780',
      idIntValue: 225187764,
    },
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20607',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20607',
      modType: ModificationType.DELETED,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20607',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20607',
      modType: ModificationType.DELETED,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '201302',
    deleted: true,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201297',
    itemId: '10328',
    itemTypeId: '1152921504606847089',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20597',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="AAkKSN_MmEVt8yIw4sQA,ABIJ5OzfJC_6z3a8cjQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20631',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="AAkKSN_MmEVt8yIw4sQA,ABIJ5OzfJC_6z3a8cjQA,ABI4Dl_6DDaQ4SDceEQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20597',
      modType: ModificationType.MODIFIED,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20631',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="AAkKSN_MmEVt8yIw4sQA,ABIJ5OzfJC_6z3a8cjQA,ABI4Dl_6DDaQ4SDceEQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.RELATION_CHANGE,
      name: changeTypeEnum.RELATION_CHANGE,
      notRelationChange: false,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '333',
      idIntValue: 333,
    },
    artId: '201282',
    itemId: '5144',
    itemTypeId: {
      id: RelationTypeId.INTERFACECONNECTIONCONTENT,
      name: 'Interface Connection Content',
      order: 'LEXICOGRAPHICAL_ASC',
      ordered: true,
      multiplicity: 'MANY_TO_MANY',
      idString: '6039606571486514298',
      idIntValue: 1955695738,
    },
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20616',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20616',
      modType: ModificationType.DELETED,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20616',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20616',
      modType: ModificationType.DELETED,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '201303',
    deleted: true,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.RELATION_CHANGE,
      name: changeTypeEnum.RELATION_CHANGE,
      notRelationChange: false,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '333',
      idIntValue: 333,
    },
    artId: '201282',
    itemId: '5145',
    itemTypeId: {
      id: RelationTypeId.INTERFACECONNECTIONCONTENT,
      name: 'Interface Connection Content',
      order: 'LEXICOGRAPHICAL_ASC',
      ordered: true,
      multiplicity: 'MANY_TO_MANY',
      idString: '6039606571486514298',
      idIntValue: 1955695738,
    },
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20629',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20629',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '201304',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '111',
      idIntValue: 111,
    },
    artId: '201305',
    itemId: '201305',
    itemTypeId: '126164394421696908',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20630',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20630',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.RELATION_CHANGE,
      name: changeTypeEnum.RELATION_CHANGE,
      notRelationChange: false,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '333',
      idIntValue: 333,
    },
    artId: '201297',
    itemId: '5146',
    itemTypeId: {
      id: RelationTypeId.INTERFACESUBMESSAGECONTENT,
      name: 'Interface Message SubMessage Content',
      order: 'USER_DEFINED',
      ordered: true,
      multiplicity: 'MANY_TO_MANY',
      idString: '2455059983007225780',
      idIntValue: 225187764,
    },
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20635',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20635',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '201305',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '111',
      idIntValue: 111,
    },
    artId: '201304',
    itemId: '201304',
    itemTypeId: '2455059983007225775',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20621',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20621',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.RELATION_CHANGE,
      name: changeTypeEnum.RELATION_CHANGE,
      notRelationChange: false,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '333',
      idIntValue: 333,
    },
    artId: '201289',
    itemId: '5147',
    itemTypeId: {
      id: RelationTypeId.INTERFACESUBMESSAGECONTENT,
      name: 'Interface Message SubMessage Content',
      order: 'USER_DEFINED',
      ordered: true,
      multiplicity: 'MANY_TO_MANY',
      idString: '2455059983007225780',
      idIntValue: 225187764,
    },
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20637',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20637',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '201305',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201302',
    itemId: '10339',
    itemTypeId: '1152921504606847088',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20603',
      modType: ModificationType.NEW,
      value: 'test submessage 6',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20603',
      modType: ModificationType.ARTIFACT_DELETED,
      value: 'test submessage 6',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20603',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20603',
      modType: ModificationType.ARTIFACT_DELETED,
      value: 'test submessage 6',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: true,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201302',
    itemId: '10340',
    itemTypeId: '2455059983007225769',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20605',
      modType: ModificationType.NEW,
      value: '762',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20605',
      modType: ModificationType.ARTIFACT_DELETED,
      value: '762',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20605',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20605',
      modType: ModificationType.ARTIFACT_DELETED,
      value: '762',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: true,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201302',
    itemId: '10341',
    itemTypeId: '1152921504606847090',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20604',
      modType: ModificationType.NEW,
      value: 'uiop',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20604',
      modType: ModificationType.ARTIFACT_DELETED,
      value: 'uiop',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20604',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20604',
      modType: ModificationType.ARTIFACT_DELETED,
      value: 'uiop',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: true,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201304',
    itemId: '10350',
    itemTypeId: '2455059983007225754',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20622',
      modType: ModificationType.NEW,
      value: 'true',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20622',
      modType: ModificationType.NEW,
      value: 'true',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201304',
    itemId: '10351',
    itemTypeId: '1152921504606847088',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20623',
      modType: ModificationType.NEW,
      value: 'test message 7',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20623',
      modType: ModificationType.NEW,
      value: 'test message 7',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201304',
    itemId: '10352',
    itemTypeId: '3899709087455064789',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20626',
      modType: ModificationType.NEW,
      value: 'Periodic',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20626',
      modType: ModificationType.NEW,
      value: 'Periodic',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201304',
    itemId: '10353',
    itemTypeId: '2455059983007225770',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20627',
      modType: ModificationType.NEW,
      value: 'Operational',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20627',
      modType: ModificationType.NEW,
      value: 'Operational',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201304',
    itemId: '10354',
    itemTypeId: '2455059983007225768',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20628',
      modType: ModificationType.NEW,
      value: '741',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20628',
      modType: ModificationType.NEW,
      value: '741',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201304',
    itemId: '10355',
    itemTypeId: '1152921504606847090',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20625',
      modType: ModificationType.NEW,
      value: 'dafda',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20625',
      modType: ModificationType.NEW,
      value: 'dafda',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201304',
    itemId: '10356',
    itemTypeId: '2455059983007225763',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20624',
      modType: ModificationType.NEW,
      value: '20',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20624',
      modType: ModificationType.NEW,
      value: '20',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201305',
    itemId: '10357',
    itemTypeId: '1152921504606847088',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20632',
      modType: ModificationType.NEW,
      value: 'test submessage 8',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20632',
      modType: ModificationType.NEW,
      value: 'test submessage 8',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201305',
    itemId: '10358',
    itemTypeId: '2455059983007225769',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20634',
      modType: ModificationType.NEW,
      value: '85',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20634',
      modType: ModificationType.NEW,
      value: '85',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201305',
    itemId: '10359',
    itemTypeId: '1152921504606847090',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20633',
      modType: ModificationType.NEW,
      value: 'dfd',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20633',
      modType: ModificationType.NEW,
      value: 'dfd',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '111',
      idIntValue: 111,
    },
    artId: '201297',
    itemId: '201297',
    itemTypeId: '2455059983007225775',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20597',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="AAkKSN_MmEVt8yIw4sQA,ABIJ5OzfJC_6z3a8cjQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20631',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="AAkKSN_MmEVt8yIw4sQA,ABIJ5OzfJC_6z3a8cjQA,ABI4Dl_6DDaQ4SDceEQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20597',
      modType: ModificationType.MODIFIED,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20631',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="AAkKSN_MmEVt8yIw4sQA,ABIJ5OzfJC_6z3a8cjQA,ABI4Dl_6DDaQ4SDceEQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: true,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: '111',
      idIntValue: 111,
    },
    artId: '201289',
    itemId: '201289',
    itemTypeId: '2455059983007225775',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20606',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="ABCkGXcC9BtOVW8ceggA,AAkKSN_MmEVt8yIw4sQA,ABEYW415xCGFe1Wi1NwA,ABIN5ZrPVFEVTDcmr9gA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20636',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="ABCkGXcC9BtOVW8ceggA,AAkKSN_MmEVt8yIw4sQA,ABI4Dl_6DDaQ4SDceEQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20606',
      modType: ModificationType.MODIFIED,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20636',
      modType: ModificationType.MODIFIED,
      value:
        '<OrderList><Order relType="Interface Message SubMessage Content" side="SIDE_B" orderType="AAT0xogoMjMBhARkBZQA" list="ABCkGXcC9BtOVW8ceggA,AAkKSN_MmEVt8yIw4sQA,ABI4Dl_6DDaQ4SDceEQA"/></OrderList>',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: true,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      typeId: 2834799904,
      notAttributeChange: true,
      idString: '111',
      idIntValue: 111,
    },
    artId: '201279',
    itemId: '201279',
    itemTypeId: '6039606571486514295',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20484',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20687',
      modType: ModificationType.MODIFIED,
      value: 'changed',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20484',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20687',
      modType: ModificationType.MODIFIED,
      value: 'changed',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: true,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      typeId: 2834799904,
      notAttributeChange: false,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201281',
    itemId: '10249',
    itemTypeId: '1152921504606847090',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20494',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '1015',
        branchId: '1014568291390890988',
      },
      gammaId: '20688',
      modType: ModificationType.MODIFIED,
      value: 'changed',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '977',
        branchId: '8',
      },
      gammaId: '20494',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20688',
      modType: ModificationType.MODIFIED,
      value: 'changed',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      typeId: 2834799904,
      notAttributeChange: false,
      idString: '222',
      idIntValue: 222,
    },
    artId: '201279',
    itemId: '10241',
    itemTypeId: '1152921504606847090',
    baselineVersion: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20484',
      modType: ModificationType.NEW,
      value: '',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: '',
      valid: false,
      applicabilityToken: null,
    },
    currentVersion: {
      transactionToken: {
        id: '1014',
        branchId: '1014568291390890988',
      },
      gammaId: '20687',
      modType: ModificationType.MODIFIED,
      value: 'changed',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    destinationVersion: {
      transactionToken: {
        id: '977',
        branchId: '8',
      },
      gammaId: '20484',
      modType: ModificationType.NEW,
      value: null,
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    netChange: {
      transactionToken: {
        id: '-1',
        branchId: '-1',
      },
      gammaId: '20687',
      modType: ModificationType.MODIFIED,
      value: 'changed',
      uri: '',
      valid: true,
      applicabilityToken: {
        id: '1',
        name: 'Base',
      },
    },
    synthetic: false,
    artIdB: '-1',
    deleted: false,
    applicabilityCopy: false,
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201323",
    itemId: "201323",
    itemTypeId: "6",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1052",
        branchId: "3361000790344842462"
      },
      gammaId: "20782",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20782",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201322",
    itemId: "201322",
    itemTypeId: "6",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1050",
        branchId: "3361000790344842462"
      },
      gammaId: "20779",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20779",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201321",
    itemId: "201321",
    itemTypeId: "6",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1047",
        branchId: "3361000790344842462"
      },
      gammaId: "20773",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20773",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201326",
    itemId: "201326",
    itemTypeId: "5849078277209560034",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1073",
        branchId: "3361000790344842462"
      },
      gammaId: "20795",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20795",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201325",
    itemId: "201325",
    itemTypeId: "5849078277209560034",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1057",
        branchId: "3361000790344842462"
      },
      gammaId: "20790",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20790",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201334",
    itemId: "201334",
    itemTypeId: "5849078277209560034",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1095",
        branchId: "3361000790344842462"
      },
      gammaId: "20834",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20834",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201338",
    itemId: "201338",
    itemTypeId: "87",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1109",
        branchId: "3361000790344842462"
      },
      gammaId: "20864",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20864",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201337",
    itemId: "201337",
    itemTypeId: "87",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1108",
        branchId: "3361000790344842462"
      },
      gammaId: "20854",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20854",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201336",
    itemId: "201336",
    itemTypeId: "87",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1107",
        branchId: "3361000790344842462"
      },
      gammaId: "20844",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20844",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201343",
    itemId: "201343",
    itemTypeId: "5849078277209560034",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1133",
        branchId: "3361000790344842462"
      },
      gammaId: "20917",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20917",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201342",
    itemId: "201342",
    itemTypeId: "87",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1132",
        branchId: "3361000790344842462"
      },
      gammaId: "20907",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20907",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "201341",
    itemId: "201341",
    itemTypeId: "87",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1129",
        branchId: "3361000790344842462"
      },
      gammaId: "20896",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20896",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ARTIFACT_CHANGE,
      name: changeTypeEnum.ARTIFACT_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "111",
      idIntValue: 111
    },
    artId: "200048",
    itemId: "200048",
    itemTypeId: "5849078277209560034",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "555",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1146",
        branchId: "3361000790344842462"
      },
      gammaId: "555",
      modType: ModificationType.DELETED,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "555",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "555",
      modType: ModificationType.DELETED,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200052",
    itemId: "10420",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1029",
        branchId: "3361000790344842462"
      },
      gammaId: "20760",
      modType: ModificationType.NEW,
      value: "SPKR_D",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20760",
      modType: ModificationType.NEW,
      value: "SPKR_D",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200047",
    itemId: "10424",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1037",
        branchId: "3361000790344842462"
      },
      gammaId: "20764",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20764",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200046",
    itemId: "10426",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1041",
        branchId: "3361000790344842462"
      },
      gammaId: "20766",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20766",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201321",
    itemId: "10436",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1047",
        branchId: "3361000790344842462"
      },
      gammaId: "20775",
      modType: ModificationType.NEW,
      value: "test",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20775",
      modType: ModificationType.NEW,
      value: "test",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201322",
    itemId: "10439",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1050",
        branchId: "3361000790344842462"
      },
      gammaId: "20780",
      modType: ModificationType.NEW,
      value: "testForEdit",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20780",
      modType: ModificationType.NEW,
      value: "testForEdit",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201323",
    itemId: "10440",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1052",
        branchId: "3361000790344842462"
      },
      gammaId: "20783",
      modType: ModificationType.NEW,
      value: "create",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20783",
      modType: ModificationType.NEW,
      value: "create",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201325",
    itemId: "10443",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1057",
        branchId: "3361000790344842462"
      },
      gammaId: "20792",
      modType: ModificationType.NEW,
      value: "test3",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20792",
      modType: ModificationType.NEW,
      value: "test3",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201325",
    itemId: "10444",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1057",
        branchId: "3361000790344842462"
      },
      gammaId: "20791",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20791",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201326",
    itemId: "10445",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1073",
        branchId: "3361000790344842462"
      },
      gammaId: "20797",
      modType: ModificationType.NEW,
      value: "test9",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20797",
      modType: ModificationType.NEW,
      value: "test9",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201326",
    itemId: "10446",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1073",
        branchId: "3361000790344842462"
      },
      gammaId: "20796",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20796",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201334",
    itemId: "10451",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1095",
        branchId: "3361000790344842462"
      },
      gammaId: "20837",
      modType: ModificationType.NEW,
      value: "test11",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20837",
      modType: ModificationType.NEW,
      value: "test11",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201334",
    itemId: "10452",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1095",
        branchId: "3361000790344842462"
      },
      gammaId: "20835",
      modType: ModificationType.NEW,
      value: "Test",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20835",
      modType: ModificationType.NEW,
      value: "Test",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201334",
    itemId: "10453",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1095",
        branchId: "3361000790344842462"
      },
      gammaId: "20836",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20836",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201336",
    itemId: "10455",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1107",
        branchId: "3361000790344842462"
      },
      gammaId: "20848",
      modType: ModificationType.NEW,
      value: "TESTFEATURE",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20848",
      modType: ModificationType.NEW,
      value: "TESTFEATURE",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201336",
    itemId: "10456",
    itemTypeId: "3641431177461038717",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1107",
        branchId: "3361000790344842462"
      },
      gammaId: "20849",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20849",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201336",
    itemId: "10457",
    itemTypeId: "31669009535111027",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1107",
        branchId: "3361000790344842462"
      },
      gammaId: "20850",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20850",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201336",
    itemId: "10458",
    itemTypeId: "2221435335730390044",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1107",
        branchId: "3361000790344842462"
      },
      gammaId: "20852",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20852",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201336",
    itemId: "10459",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1107",
        branchId: "3361000790344842462"
      },
      gammaId: "20845",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20845",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201336",
    itemId: "10460",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1107",
        branchId: "3361000790344842462"
      },
      gammaId: "20846",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20846",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201336",
    itemId: "10461",
    itemTypeId: "1152921504606847090",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1107",
        branchId: "3361000790344842462"
      },
      gammaId: "20851",
      modType: ModificationType.NEW,
      value: "testing observable changes",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20851",
      modType: ModificationType.NEW,
      value: "testing observable changes",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201336",
    itemId: "10462",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1107",
        branchId: "3361000790344842462"
      },
      gammaId: "20847",
      modType: ModificationType.NEW,
      value: "Test",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20847",
      modType: ModificationType.NEW,
      value: "Test",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201337",
    itemId: "10463",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1108",
        branchId: "3361000790344842462"
      },
      gammaId: "20858",
      modType: ModificationType.NEW,
      value: "FEATURE123",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20858",
      modType: ModificationType.NEW,
      value: "FEATURE123",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201337",
    itemId: "10464",
    itemTypeId: "3641431177461038717",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1108",
        branchId: "3361000790344842462"
      },
      gammaId: "20859",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20859",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201337",
    itemId: "10465",
    itemTypeId: "31669009535111027",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1108",
        branchId: "3361000790344842462"
      },
      gammaId: "20860",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20860",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201337",
    itemId: "10466",
    itemTypeId: "2221435335730390044",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1108",
        branchId: "3361000790344842462"
      },
      gammaId: "20862",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20862",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201337",
    itemId: "10467",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1108",
        branchId: "3361000790344842462"
      },
      gammaId: "20855",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20855",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201337",
    itemId: "10468",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1108",
        branchId: "3361000790344842462"
      },
      gammaId: "20856",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20856",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201337",
    itemId: "10469",
    itemTypeId: "1152921504606847090",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1108",
        branchId: "3361000790344842462"
      },
      gammaId: "20861",
      modType: ModificationType.NEW,
      value: "testing observable changes",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20861",
      modType: ModificationType.NEW,
      value: "testing observable changes",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201337",
    itemId: "10470",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1108",
        branchId: "3361000790344842462"
      },
      gammaId: "20857",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20857",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201338",
    itemId: "10471",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1109",
        branchId: "3361000790344842462"
      },
      gammaId: "20868",
      modType: ModificationType.NEW,
      value: "FEATURE083024823075320573205723094832094832095470480258743",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20868",
      modType: ModificationType.NEW,
      value: "FEATURE083024823075320573205723094832094832095470480258743",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201338",
    itemId: "10472",
    itemTypeId: "3641431177461038717",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1109",
        branchId: "3361000790344842462"
      },
      gammaId: "20869",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20869",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201338",
    itemId: "10473",
    itemTypeId: "31669009535111027",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1109",
        branchId: "3361000790344842462"
      },
      gammaId: "20870",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20870",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201338",
    itemId: "10474",
    itemTypeId: "2221435335730390044",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1109",
        branchId: "3361000790344842462"
      },
      gammaId: "20872",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20872",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201338",
    itemId: "10475",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1109",
        branchId: "3361000790344842462"
      },
      gammaId: "20865",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20865",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201338",
    itemId: "10476",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1109",
        branchId: "3361000790344842462"
      },
      gammaId: "20866",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20866",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201338",
    itemId: "10477",
    itemTypeId: "1152921504606847090",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1109",
        branchId: "3361000790344842462"
      },
      gammaId: "20871",
      modType: ModificationType.NEW,
      value: "testing observable",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20871",
      modType: ModificationType.NEW,
      value: "testing observable",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201338",
    itemId: "10478",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1109",
        branchId: "3361000790344842462"
      },
      gammaId: "20867",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20867",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201341",
    itemId: "10481",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1129",
        branchId: "3361000790344842462"
      },
      gammaId: "20900",
      modType: ModificationType.NEW,
      value: "FEATUREFORTESTINGMERGEDUI",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20900",
      modType: ModificationType.NEW,
      value: "FEATUREFORTESTINGMERGEDUI",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201341",
    itemId: "10482",
    itemTypeId: "3641431177461038717",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1129",
        branchId: "3361000790344842462"
      },
      gammaId: "20901",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20901",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201341",
    itemId: "10483",
    itemTypeId: "31669009535111027",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1129",
        branchId: "3361000790344842462"
      },
      gammaId: "20902",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20902",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201341",
    itemId: "10484",
    itemTypeId: "2221435335730390044",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1129",
        branchId: "3361000790344842462"
      },
      gammaId: "20904",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20904",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201341",
    itemId: "10485",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1129",
        branchId: "3361000790344842462"
      },
      gammaId: "20897",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20897",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201341",
    itemId: "10486",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1129",
        branchId: "3361000790344842462"
      },
      gammaId: "20898",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20898",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201341",
    itemId: "10487",
    itemTypeId: "1152921504606847090",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20903",
      modType: ModificationType.NEW,
      value: "sakldjf;askjflalsd;kjfa",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    currentVersion: {
      transactionToken: {
        id: "1130",
        branchId: "3361000790344842462"
      },
      gammaId: "20906",
      modType: ModificationType.MODIFIED,
      value: "actual description",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20906",
      modType: ModificationType.NEW,
      value: "actual description",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201341",
    itemId: "10488",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1129",
        branchId: "3361000790344842462"
      },
      gammaId: "20899",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20899",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201342",
    itemId: "10489",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1132",
        branchId: "3361000790344842462"
      },
      gammaId: "20911",
      modType: ModificationType.NEW,
      value: "BROKENFEATURE",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20911",
      modType: ModificationType.NEW,
      value: "BROKENFEATURE",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201342",
    itemId: "10490",
    itemTypeId: "3641431177461038717",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1132",
        branchId: "3361000790344842462"
      },
      gammaId: "20912",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20912",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201342",
    itemId: "10491",
    itemTypeId: "31669009535111027",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1132",
        branchId: "3361000790344842462"
      },
      gammaId: "20913",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20913",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201342",
    itemId: "10492",
    itemTypeId: "2221435335730390044",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1132",
        branchId: "3361000790344842462"
      },
      gammaId: "20915",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20915",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201342",
    itemId: "10493",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1132",
        branchId: "3361000790344842462"
      },
      gammaId: "20908",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20908",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201342",
    itemId: "10494",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1132",
        branchId: "3361000790344842462"
      },
      gammaId: "20909",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20909",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201342",
    itemId: "10495",
    itemTypeId: "1152921504606847090",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1132",
        branchId: "3361000790344842462"
      },
      gammaId: "20914",
      modType: ModificationType.NEW,
      value: "yiuyoiyoi",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20914",
      modType: ModificationType.NEW,
      value: "yiuyoiyoi",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201342",
    itemId: "10496",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1132",
        branchId: "3361000790344842462"
      },
      gammaId: "20910",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20910",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201343",
    itemId: "10497",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1133",
        branchId: "3361000790344842462"
      },
      gammaId: "20919",
      modType: ModificationType.NEW,
      value: "newconfig",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20919",
      modType: ModificationType.NEW,
      value: "newconfig",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "201343",
    itemId: "10498",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1133",
        branchId: "3361000790344842462"
      },
      gammaId: "20918",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20918",
      modType: ModificationType.NEW,
      value: "Unspecified",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200046",
    itemId: "10512",
    itemTypeId: "4522673803793808650",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1144",
        branchId: "3361000790344842462"
      },
      gammaId: "20927",
      modType: ModificationType.NEW,
      value: "Test",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20927",
      modType: ModificationType.NEW,
      value: "Test",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200048",
    itemId: "327",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "588",
      modType: ModificationType.NEW,
      value: "Product D",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1146",
        branchId: "3361000790344842462"
      },
      gammaId: "588",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "Product D",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "588",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "588",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "Product D",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200051",
    itemId: "344",
    itemTypeId: "1152921504606847088",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "566",
      modType: ModificationType.NEW,
      value: "JHU_CONTROLLER",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "566",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "JHU_CONTROLLER",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "566",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "566",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "JHU_CONTROLLER",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200051",
    itemId: "345",
    itemTypeId: "3641431177461038717",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "567",
      modType: ModificationType.NEW,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "567",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "567",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "567",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "false",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200051",
    itemId: "346",
    itemTypeId: "31669009535111027",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "568",
      modType: ModificationType.NEW,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "568",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "568",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "568",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "String",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200051",
    itemId: "347",
    itemTypeId: "2221435335730390044",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "570",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "570",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "570",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "570",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200051",
    itemId: "348",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "564",
      modType: ModificationType.NEW,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "564",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "564",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "564",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "Included",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200051",
    itemId: "349",
    itemTypeId: "861995499338466438",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "565",
      modType: ModificationType.NEW,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "565",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "565",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "565",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "Excluded",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200051",
    itemId: "350",
    itemTypeId: "1152921504606847090",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "569",
      modType: ModificationType.NEW,
      value: "A small point of variation",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "569",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "A small point of variation",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "569",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "569",
      modType: ModificationType.ARTIFACT_DELETED,
      value: "A small point of variation",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.ATTRIBUTE_CHANGE,
      name: changeTypeEnum.ATTRIBUTE_CHANGE,
      notRelationChange: true,
      notAttributeChange: false,
      typeId: 2834799904,
      idString: "222",
      idIntValue: 222
    },
    artId: "200052",
    itemId: "358",
    itemTypeId: "1152921504606847090",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "600",
      modType: ModificationType.NEW,
      value: "This feature is multi-select.",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1106",
        branchId: "3361000790344842462"
      },
      gammaId: "20843",
      modType: ModificationType.MODIFIED,
      value: "This feature is multi-select. Testing changes to edit feature observable.",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "600",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "20843",
      modType: ModificationType.MODIFIED,
      value: "This feature is multi-select. Testing changes to edit feature observable.",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "640402747236006441",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1052",
        branchId: "3361000790344842462"
      },
      gammaId: "640402747236006441",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 6795785791754600802",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "640402747236006441",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 6795785791754600802",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2009165141520822680",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1076",
        branchId: "3361000790344842462"
      },
      gammaId: "2009165141520822680",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 6671206472851861370",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2009165141520822680",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 6671206472851861370",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3250048427956729314",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1059",
        branchId: "3361000790344842462"
      },
      gammaId: "3250048427956729314",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 1228402116312747210",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3250048427956729314",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 1228402116312747210",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "5239139789677336838",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5239139789677336838",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    currentVersion: {
      transactionToken: {
        id: "1089",
        branchId: "3361000790344842462"
      },
      gammaId: "5239139789677336838",
      modType: ModificationType.INTRODUCED,
      value: "Tuple2|201326, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5239139789677336838",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2119319497002970312",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2119319497002970312",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    currentVersion: {
      transactionToken: {
        id: "1068",
        branchId: "3361000790344842462"
      },
      gammaId: "2119319497002970312",
      modType: ModificationType.INTRODUCED,
      value: "Tuple2|201322, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2119319497002970312",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3989480733536802897",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3989480733536802897",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1145",
        branchId: "3361000790344842462"
      },
      gammaId: "3989480733536802897",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 2119518475782991281",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "3989480733536802897",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3989480733536802897",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 2119518475782991281",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2377354776535441209",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1140",
        branchId: "3361000790344842462"
      },
      gammaId: "2377354776535441209",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 429400628461874867",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2377354776535441209",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 429400628461874867",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "8731904211914648181",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1051",
        branchId: "3361000790344842462"
      },
      gammaId: "8731904211914648181",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 1228402116312747210",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8731904211914648181",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 1228402116312747210",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "7533194314328506724",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1067",
        branchId: "3361000790344842462"
      },
      gammaId: "7533194314328506724",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "7533194314328506724",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4682810627776601778",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4682810627776601778",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "4682810627776601778",
      modType: ModificationType.DELETED,
      value: "Tuple2|200046, 444739923290685126",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "4682810627776601778",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4682810627776601778",
      modType: ModificationType.DELETED,
      value: "Tuple2|200046, 444739923290685126",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1582232747885842999",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1048",
        branchId: "3361000790344842462"
      },
      gammaId: "1582232747885842999",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 5633677911902083682",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1582232747885842999",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 5633677911902083682",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1154138845113971476",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1096",
        branchId: "3361000790344842462"
      },
      gammaId: "1154138845113971476",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 4580702391533897063",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1154138845113971476",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 4580702391533897063",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3527111634300286079",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3527111634300286079",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1028",
        branchId: "3361000790344842462"
      },
      gammaId: "3527111634300286079",
      modType: ModificationType.DELETED,
      value: "Tuple2|200046, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "3527111634300286079",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3527111634300286079",
      modType: ModificationType.DELETED,
      value: "Tuple2|200046, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4005569140214585845",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1048",
        branchId: "3361000790344842462"
      },
      gammaId: "4005569140214585845",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4005569140214585845",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "876102651561944032",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "876102651561944032",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1027",
        branchId: "3361000790344842462"
      },
      gammaId: "876102651561944032",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 6196988077889164656",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "876102651561944032",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "876102651561944032",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 6196988077889164656",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2612158942630230771",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1048",
        branchId: "3361000790344842462"
      },
      gammaId: "2612158942630230771",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 5969358936393395172",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2612158942630230771",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 5969358936393395172",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "8774446363603083435",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1058",
        branchId: "3361000790344842462"
      },
      gammaId: "8774446363603083435",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 4821764112938308960",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8774446363603083435",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 4821764112938308960",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "6803129857436172776",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1134",
        branchId: "3361000790344842462"
      },
      gammaId: "6803129857436172776",
      modType: ModificationType.NEW,
      value: "Tuple2|201343, 5996720927684664680",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6803129857436172776",
      modType: ModificationType.NEW,
      value: "Tuple2|201343, 5996720927684664680",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "5553115221017927726",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1134",
        branchId: "3361000790344842462"
      },
      gammaId: "5553115221017927726",
      modType: ModificationType.NEW,
      value: "Tuple2|201343, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5553115221017927726",
      modType: ModificationType.NEW,
      value: "Tuple2|201343, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "7667601770738188603",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "7667601770738188603",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1038",
        branchId: "3361000790344842462"
      },
      gammaId: "7667601770738188603",
      modType: ModificationType.DELETED,
      value: "Tuple2|200045, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "7667601770738188603",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "7667601770738188603",
      modType: ModificationType.DELETED,
      value: "Tuple2|200045, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2571490004579944238",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1110",
        branchId: "3361000790344842462"
      },
      gammaId: "2571490004579944238",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 5013684555146319760",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2571490004579944238",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 5013684555146319760",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "6534059499497526404",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6534059499497526404",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "6534059499497526404",
      modType: ModificationType.DELETED,
      value: "Tuple2|200047, 444739923290685126",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "6534059499497526404",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6534059499497526404",
      modType: ModificationType.DELETED,
      value: "Tuple2|200047, 444739923290685126",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2231781680635599713",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2231781680635599713",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1039",
        branchId: "3361000790344842462"
      },
      gammaId: "2231781680635599713",
      modType: ModificationType.DELETED,
      value: "Tuple2|200054, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "16",
        branchId: "8"
      },
      gammaId: "2231781680635599713",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2231781680635599713",
      modType: ModificationType.DELETED,
      value: "Tuple2|200054, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1524079981189144849",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1097",
        branchId: "3361000790344842462"
      },
      gammaId: "1524079981189144849",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1524079981189144849",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4918432693496912164",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1141",
        branchId: "3361000790344842462"
      },
      gammaId: "4918432693496912164",
      modType: ModificationType.NEW,
      value: "Tuple2|201343, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4918432693496912164",
      modType: ModificationType.NEW,
      value: "Tuple2|201343, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "8123709272411109437",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1091",
        branchId: "3361000790344842462"
      },
      gammaId: "8123709272411109437",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 8502426502792154896",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8123709272411109437",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 8502426502792154896",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4986415901986771263",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1119",
        branchId: "3361000790344842462"
      },
      gammaId: "4986415901986771263",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 8840172011837252943",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4986415901986771263",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 8840172011837252943",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "8861928268753409689",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1074",
        branchId: "3361000790344842462"
      },
      gammaId: "8861928268753409689",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 6671206472851861370",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8861928268753409689",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 6671206472851861370",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4506471533916829788",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1113",
        branchId: "3361000790344842462"
      },
      gammaId: "4506471533916829788",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 8840172011837252943",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4506471533916829788",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 8840172011837252943",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1025026122444639371",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1105",
        branchId: "3361000790344842462"
      },
      gammaId: "1025026122444639371",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 4460672960481695616",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1025026122444639371",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 4460672960481695616",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2895915841793885969",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1136",
        branchId: "3361000790344842462"
      },
      gammaId: "2895915841793885969",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 4247547365050953120",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2895915841793885969",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 4247547365050953120",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "6679913727237336607",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1059",
        branchId: "3361000790344842462"
      },
      gammaId: "6679913727237336607",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 4821764112938308960",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6679913727237336607",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 4821764112938308960",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "7157245895631273956",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1099",
        branchId: "3361000790344842462"
      },
      gammaId: "7157245895631273956",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "7157245895631273956",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3747199926162752905",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1051",
        branchId: "3361000790344842462"
      },
      gammaId: "3747199926162752905",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3747199926162752905",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "102178785260011452",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1135",
        branchId: "3361000790344842462"
      },
      gammaId: "102178785260011452",
      modType: ModificationType.NEW,
      value: "Tuple2|201343, 6795785791754600802",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "102178785260011452",
      modType: ModificationType.NEW,
      value: "Tuple2|201343, 6795785791754600802",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "6149831689498675672",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1134",
        branchId: "3361000790344842462"
      },
      gammaId: "6149831689498675672",
      modType: ModificationType.NEW,
      value: "Tuple2|201343, 5996720927684664680",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6149831689498675672",
      modType: ModificationType.NEW,
      value: "Tuple2|201343, 5996720927684664680",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "8540483961706062801",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1047",
        branchId: "3361000790344842462"
      },
      gammaId: "8540483961706062801",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 5633677911902083682",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8540483961706062801",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 5633677911902083682",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "5969186545488511080",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5969186545488511080",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1145",
        branchId: "3361000790344842462"
      },
      gammaId: "5969186545488511080",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "5969186545488511080",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5969186545488511080",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "664962501403163943",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1076",
        branchId: "3361000790344842462"
      },
      gammaId: "664962501403163943",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 6795785791754600802",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "664962501403163943",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 6795785791754600802",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1869262887302263023",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1048",
        branchId: "3361000790344842462"
      },
      gammaId: "1869262887302263023",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1869262887302263023",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1396270177809786685",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1131",
        branchId: "3361000790344842462"
      },
      gammaId: "1396270177809786685",
      modType: ModificationType.NEW,
      value: "Tuple2|201341, 429400628461874867",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1396270177809786685",
      modType: ModificationType.NEW,
      value: "Tuple2|201341, 429400628461874867",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4813027197617455840",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1097",
        branchId: "3361000790344842462"
      },
      gammaId: "4813027197617455840",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 2119518475782991281",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4813027197617455840",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 2119518475782991281",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "8715774983837699292",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1028",
        branchId: "3361000790344842462"
      },
      gammaId: "8715774983837699292",
      modType: ModificationType.NEW,
      value: "Tuple2|200046, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8715774983837699292",
      modType: ModificationType.NEW,
      value: "Tuple2|200046, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "6573937339200309316",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6573937339200309316",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1145",
        branchId: "3361000790344842462"
      },
      gammaId: "6573937339200309316",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 19262790478673727",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "6573937339200309316",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6573937339200309316",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 19262790478673727",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4796073166601693017",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1118",
        branchId: "3361000790344842462"
      },
      gammaId: "4796073166601693017",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 7919212107417715917",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4796073166601693017",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 7919212107417715917",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1966954509095979792",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1058",
        branchId: "3361000790344842462"
      },
      gammaId: "1966954509095979792",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1966954509095979792",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1029342385478094690",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1096",
        branchId: "3361000790344842462"
      },
      gammaId: "1029342385478094690",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 4580702391533897063",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1029342385478094690",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 4580702391533897063",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2656881371675598665",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1153",
        branchId: "3361000790344842462"
      },
      gammaId: "2656881371675598665",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 5250889309976611552",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2656881371675598665",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 5250889309976611552",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "247024875567328087",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1142",
        branchId: "3361000790344842462"
      },
      gammaId: "247024875567328087",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 429400628461874867",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "247024875567328087",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 429400628461874867",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4398931919090050365",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1097",
        branchId: "3361000790344842462"
      },
      gammaId: "4398931919090050365",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4398931919090050365",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "150335987583777334",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1149",
        branchId: "3361000790344842462"
      },
      gammaId: "150335987583777334",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 5634092576675817529",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "150335987583777334",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 5634092576675817529",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "7193773050709129462",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1078",
        branchId: "3361000790344842462"
      },
      gammaId: "7193773050709129462",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 2119518475782991281",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "7193773050709129462",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 2119518475782991281",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2690233398813875826",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2690233398813875826",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    currentVersion: {
      transactionToken: {
        id: "1163",
        branchId: "3361000790344842462"
      },
      gammaId: "2690233398813875826",
      modType: ModificationType.INTRODUCED,
      value: "Tuple2|201322, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2690233398813875826",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "5593913466355110478",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1048",
        branchId: "3361000790344842462"
      },
      gammaId: "5593913466355110478",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5593913466355110478",
      modType: ModificationType.NEW,
      value: "Tuple2|201321, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "895370156264527757",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "895370156264527757",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    currentVersion: {
      transactionToken: {
        id: "1100",
        branchId: "3361000790344842462"
      },
      gammaId: "895370156264527757",
      modType: ModificationType.INTRODUCED,
      value: "Tuple2|201323, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "895370156264527757",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "6193993589067100665",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1096",
        branchId: "3361000790344842462"
      },
      gammaId: "6193993589067100665",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6193993589067100665",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "5301359183826986096",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1137",
        branchId: "3361000790344842462"
      },
      gammaId: "5301359183826986096",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 4247547365050953120",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5301359183826986096",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 4247547365050953120",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3972231424940005442",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3972231424940005442",
      modType: ModificationType.INTRODUCED,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "3972231424940005442",
      modType: ModificationType.DELETED,
      value: "Tuple2|200054, 444739923290685126",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "21",
        branchId: "8"
      },
      gammaId: "3972231424940005442",
      modType: ModificationType.INTRODUCED,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3972231424940005442",
      modType: ModificationType.DELETED,
      value: "Tuple2|200054, 444739923290685126",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "680711251841221102",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1038",
        branchId: "3361000790344842462"
      },
      gammaId: "680711251841221102",
      modType: ModificationType.NEW,
      value: "Tuple2|200045, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "680711251841221102",
      modType: ModificationType.NEW,
      value: "Tuple2|200045, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2096931125198294018",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1030",
        branchId: "3361000790344842462"
      },
      gammaId: "2096931125198294018",
      modType: ModificationType.NEW,
      value: "Tuple2|200052, 8502426502792154896",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2096931125198294018",
      modType: ModificationType.NEW,
      value: "Tuple2|200052, 8502426502792154896",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3620990063697350052",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1053",
        branchId: "3361000790344842462"
      },
      gammaId: "3620990063697350052",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3620990063697350052",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1969399353246064459",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1074",
        branchId: "3361000790344842462"
      },
      gammaId: "1969399353246064459",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 6671206472851861370",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1969399353246064459",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 6671206472851861370",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "8107069710633324694",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8107069710633324694",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1145",
        branchId: "3361000790344842462"
      },
      gammaId: "8107069710633324694",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "8107069710633324694",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8107069710633324694",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4136190757951319373",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1131",
        branchId: "3361000790344842462"
      },
      gammaId: "4136190757951319373",
      modType: ModificationType.NEW,
      value: "Tuple2|201341, 5634092576675817529",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4136190757951319373",
      modType: ModificationType.NEW,
      value: "Tuple2|201341, 5634092576675817529",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4697645547106944475",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4697645547106944475",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    currentVersion: {
      transactionToken: {
        id: "1162",
        branchId: "3361000790344842462"
      },
      gammaId: "4697645547106944475",
      modType: ModificationType.INTRODUCED,
      value: "Tuple2|201325, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4697645547106944475",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2639940493991688849",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1148",
        branchId: "3361000790344842462"
      },
      gammaId: "2639940493991688849",
      modType: ModificationType.NEW,
      value: "Tuple2|200045, 5634092576675817529",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2639940493991688849",
      modType: ModificationType.NEW,
      value: "Tuple2|200045, 5634092576675817529",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2923793996971568896",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2923793996971568896",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    currentVersion: {
      transactionToken: {
        id: "1071",
        branchId: "3361000790344842462"
      },
      gammaId: "2923793996971568896",
      modType: ModificationType.INTRODUCED,
      value: "Tuple2|201322, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2923793996971568896",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "6762352762372321680",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1139",
        branchId: "3361000790344842462"
      },
      gammaId: "6762352762372321680",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 4247547365050953120",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6762352762372321680",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 4247547365050953120",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2371281759076859814",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1098",
        branchId: "3361000790344842462"
      },
      gammaId: "2371281759076859814",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 6795785791754600802",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2371281759076859814",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 6795785791754600802",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "7801173394682320307",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "7801173394682320307",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "7801173394682320307",
      modType: ModificationType.DELETED,
      value: "Tuple2|200051, 6196988077889164656",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "7801173394682320307",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "7801173394682320307",
      modType: ModificationType.DELETED,
      value: "Tuple2|200051, 6196988077889164656",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1397936709101604976",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1098",
        branchId: "3361000790344842462"
      },
      gammaId: "1397936709101604976",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 4580702391533897063",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1397936709101604976",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 4580702391533897063",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1136927258282136651",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1136927258282136651",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "1136927258282136651",
      modType: ModificationType.DELETED,
      value: "Tuple2|200045, 6196988077889164656",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "1136927258282136651",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1136927258282136651",
      modType: ModificationType.DELETED,
      value: "Tuple2|200045, 6196988077889164656",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1452316024383568608",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1111",
        branchId: "3361000790344842462"
      },
      gammaId: "1452316024383568608",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 5658222108662621094",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1452316024383568608",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 5658222108662621094",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4281657910537621140",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4281657910537621140",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1145",
        branchId: "3361000790344842462"
      },
      gammaId: "4281657910537621140",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "4281657910537621140",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4281657910537621140",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "5994493776584250229",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1116",
        branchId: "3361000790344842462"
      },
      gammaId: "5994493776584250229",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 5658222108662621094",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5994493776584250229",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 5658222108662621094",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4041440420897597349",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1143",
        branchId: "3361000790344842462"
      },
      gammaId: "4041440420897597349",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 5765455939650925124",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4041440420897597349",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 5765455939650925124",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "5709284633945210217",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5709284633945210217",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1147",
        branchId: "3361000790344842462"
      },
      gammaId: "5709284633945210217",
      modType: ModificationType.DELETED,
      value: "Tuple2|200051, 444739923290685126",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "5709284633945210217",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5709284633945210217",
      modType: ModificationType.DELETED,
      value: "Tuple2|200051, 444739923290685126",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "6340496292574686523",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1075",
        branchId: "3361000790344842462"
      },
      gammaId: "6340496292574686523",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6340496292574686523",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3349336112006314774",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1039",
        branchId: "3361000790344842462"
      },
      gammaId: "3349336112006314774",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3349336112006314774",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "559309669502221161",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1100",
        branchId: "3361000790344842462"
      },
      gammaId: "559309669502221161",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 8502426502792154896",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "559309669502221161",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 8502426502792154896",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2360913027908108430",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1120",
        branchId: "3361000790344842462"
      },
      gammaId: "2360913027908108430",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 5013684555146319760",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2360913027908108430",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 5013684555146319760",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2242296594466221651",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1090",
        branchId: "3361000790344842462"
      },
      gammaId: "2242296594466221651",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2242296594466221651",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 8626130296978874056",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "2557155030039990046",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1135",
        branchId: "3361000790344842462"
      },
      gammaId: "2557155030039990046",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 5996720927684664680",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "2557155030039990046",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 5996720927684664680",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "1811554881756492088",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1114",
        branchId: "3361000790344842462"
      },
      gammaId: "1811554881756492088",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 5013684555146319760",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "1811554881756492088",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 5013684555146319760",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3560580494921719868",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1115",
        branchId: "3361000790344842462"
      },
      gammaId: "3560580494921719868",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 5658222108662621094",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3560580494921719868",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 5658222108662621094",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "7886164256757105215",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1112",
        branchId: "3361000790344842462"
      },
      gammaId: "7886164256757105215",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 7919212107417715917",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "7886164256757105215",
      modType: ModificationType.NEW,
      value: "Tuple2|200054, 7919212107417715917",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "8717366247386355039",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1075",
        branchId: "3361000790344842462"
      },
      gammaId: "8717366247386355039",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 2119518475782991281",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8717366247386355039",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 2119518475782991281",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "6026489722209336525",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1053",
        branchId: "3361000790344842462"
      },
      gammaId: "6026489722209336525",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 6795785791754600802",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "6026489722209336525",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 6795785791754600802",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "8449464736106354735",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1058",
        branchId: "3361000790344842462"
      },
      gammaId: "8449464736106354735",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 4821764112938308960",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8449464736106354735",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 4821764112938308960",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "9189765534262435201",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "9189765534262435201",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1145",
        branchId: "3361000790344842462"
      },
      gammaId: "9189765534262435201",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 19262790478673727",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "14",
        branchId: "8"
      },
      gammaId: "9189765534262435201",
      modType: ModificationType.NEW,
      value: null,
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "9189765534262435201",
      modType: ModificationType.DELETED,
      value: "Tuple2|200048, 19262790478673727",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: true,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "5043943469834215786",
    itemTypeId: "11",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1050",
        branchId: "3361000790344842462"
      },
      gammaId: "5043943469834215786",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 1228402116312747210",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5043943469834215786",
      modType: ModificationType.NEW,
      value: "Tuple2|201322, 1228402116312747210",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3067877689932442942",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1064",
        branchId: "3361000790344842462"
      },
      gammaId: "3067877689932442942",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3067877689932442942",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 4248215263994192634",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3646007215924182244",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1097",
        branchId: "3361000790344842462"
      },
      gammaId: "3646007215924182244",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3646007215924182244",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 1471859510049246268",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "5939113733428643403",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1074",
        branchId: "3361000790344842462"
      },
      gammaId: "5939113733428643403",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "5939113733428643403",
      modType: ModificationType.NEW,
      value: "Tuple2|201326, 1",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "409982139000377743",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1097",
        branchId: "3361000790344842462"
      },
      gammaId: "409982139000377743",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 8502426502792154896",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "409982139000377743",
      modType: ModificationType.NEW,
      value: "Tuple2|201334, 8502426502792154896",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "4293766023210426122",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1077",
        branchId: "3361000790344842462"
      },
      gammaId: "4293766023210426122",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "4293766023210426122",
      modType: ModificationType.NEW,
      value: "Tuple2|201323, 8829144919157635481",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "8000014369132406911",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1152",
        branchId: "3361000790344842462"
      },
      gammaId: "8000014369132406911",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 5250889309976611552",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "8000014369132406911",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 5250889309976611552",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  },
  {
    ignoreType: ignoreType.NONE,
    changeType: {
      id: changeTypeNumber.TUPLE_CHANGE,
      name: changeTypeEnum.TUPLE_CHANGE,
      notRelationChange: true,
      notAttributeChange: true,
      typeId: 2834799904,
      idString: "444",
      idIntValue: 444
    },
    artId: "-1",
    itemId: "3304216415686106763",
    itemTypeId: "2",
    baselineVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    firstNonCurrentChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    currentVersion: {
      transactionToken: {
        id: "1072",
        branchId: "3361000790344842462"
      },
      gammaId: "3304216415686106763",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 4460672960481695616",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    destinationVersion: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: null,
      modType: ModificationType.NONE,
      value: null,
      uri: "",
      valid: false,
      applicabilityToken: null
    },
    netChange: {
      transactionToken: {
        id: "-1",
        branchId: "-1"
      },
      gammaId: "3304216415686106763",
      modType: ModificationType.NEW,
      value: "Tuple2|201325, 4460672960481695616",
      uri: "",
      valid: true,
      applicabilityToken: {
        id: "1",
        name: "Base"
      }
    },
    synthetic: false,
    artIdB: "-1",
    deleted: false,
    applicabilityCopy: false
  }
];
