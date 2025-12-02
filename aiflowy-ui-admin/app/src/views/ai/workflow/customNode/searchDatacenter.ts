import { getOptions } from '@aiflowy/utils';

import { api } from '#/api/request';

export const SearchDatacenterNode = async () => {
  const res = await api.get('/api/v1/datacenterTable/list');

  return {
    title: '查询数据',
    group: 'base',
    description: '查询数据中枢的数据',
    icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"><path d="M11 2C15.968 2 20 6.032 20 11C20 15.968 15.968 20 11 20C6.032 20 2 15.968 2 11C2 6.032 6.032 2 11 2ZM11 18C14.8675 18 18 14.8675 18 11C18 7.1325 14.8675 4 11 4C7.1325 4 4 7.1325 4 11C4 14.8675 7.1325 18 11 18ZM19.4853 18.0711L22.3137 20.8995L20.8995 22.3137L18.0711 19.4853L19.4853 18.0711Z"></path></svg>',
    sortNo: 813,
    parametersAddEnable: true,
    outputDefsAddEnable: false,
    parameters: [],
    outputDefs: [
      {
        name: 'rows',
        title: '查询结果',
        dataType: 'Array',
        dataTypeDisabled: true,
        required: true,
        parametersAddEnable: false,
        description: '查询结果',
        deleteDisabled: true,
        nameDisabled: false,
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
      {
        type: 'heading',
        label: '过滤条件',
      },
      {
        type: 'textarea',
        label: "如：name='张三' and age=21 or field = {{流程变量}}",
        description: '',
        name: 'where',
        defaultValue: '',
      },
      {
        type: 'heading',
        label: '限制条数',
      },
      {
        type: 'input',
        label: '',
        description: '',
        name: 'limit',
        defaultValue: '10',
      },
    ],
  };
};
