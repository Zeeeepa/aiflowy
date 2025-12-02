import { getOptions } from '@aiflowy/utils';

import { api } from '#/api/request';

export const SaveToDatacenterNode = async () => {
  const res = await api.get('/api/v1/datacenterTable/list');

  return {
    title: '保存数据',
    group: 'base',
    description: '保存数据到数据中枢',
    icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"><path d="M11 19V9H4V19H11ZM11 7V4C11 3.44772 11.4477 3 12 3H21C21.5523 3 22 3.44772 22 4V20C22 20.5523 21.5523 21 21 21H3C2.44772 21 2 20.5523 2 20V8C2 7.44772 2.44772 7 3 7H11ZM13 5V19H20V5H13ZM5 16H10V18H5V16ZM14 16H19V18H14V16ZM14 13H19V15H14V13ZM14 10H19V12H14V10ZM5 13H10V15H5V13Z"></path></svg>',
    sortNo: 812,
    parametersAddEnable: false,
    outputDefsAddEnable: false,
    parameters: [
      {
        name: 'saveList',
        title: '待保存的数据',
        dataType: 'Array',
        dataTypeDisabled: true,
        required: true,
        parametersAddEnable: false,
        description: '待保存的数据列表',
        deleteDisabled: true,
        nameDisabled: true,
      },
    ],
    outputDefs: [
      {
        name: 'successRows',
        title: '成功插入条数',
        dataType: 'Number',
        dataTypeDisabled: true,
        required: true,
        parametersAddEnable: false,
        description: '成功插入条数',
        deleteDisabled: true,
        nameDisabled: true,
      },
    ],
    forms: [
      {
        type: 'heading',
        label: '数据表',
      },
      {
        type: 'select',
        label: '',
        description: '请选择数据表',
        name: 'tableId',
        defaultValue: '',
        options: getOptions('tableName', 'id', res.data),
      },
    ],
  };
};
