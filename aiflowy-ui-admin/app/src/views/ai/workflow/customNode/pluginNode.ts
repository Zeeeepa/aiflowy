import nodeNames from './nodeNames';

export interface PluginNodeOptions {
  onChosen?: (nodeType: string, updateNodeData: any, value: string) => void;
}

export const PluginNode = (options: PluginNodeOptions = {}) => ({
  title: '插件',
  group: 'base',
  description: '选择定义好的插件',
  icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"><path d="M13 18V20H19V22H13C11.8954 22 11 21.1046 11 20V18H8C5.79086 18 4 16.2091 4 14V7C4 6.44772 4.44772 6 5 6H8V2H10V6H14V2H16V6H19C19.5523 6 20 6.44772 20 7V14C20 16.2091 18.2091 18 16 18H13ZM8 16H16C17.1046 16 18 15.1046 18 14V11H6V14C6 15.1046 6.89543 16 8 16ZM18 8H6V9H18V8ZM12 14.5C11.4477 14.5 11 14.0523 11 13.5C11 12.9477 11.4477 12.5 12 12.5C12.5523 12.5 13 12.9477 13 13.5C13 14.0523 12.5523 14.5 12 14.5Z"></path></svg>',
  sortNo: 810,
  parametersAddEnable: false,
  outputDefsAddEnable: false,
  forms: [
    {
      type: 'chosen',
      label: '插件选择',
      chosen: {
        labelDataKey: 'pluginName',
        valueDataKey: 'pluginId',
        onChosen: (updateNodeData: any, value: any) => {
          options.onChosen?.(nodeNames.pluginNode, updateNodeData, value);
        },
      },
    },
  ],
});
