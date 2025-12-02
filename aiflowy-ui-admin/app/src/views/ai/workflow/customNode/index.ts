import docNode from './documentNode';
import downloadNode from './downloadNode';
import makeFileNode from './makeFileNode';
import nodeNames from './nodeNames';
import { PluginNode } from './pluginNode';
import { SaveToDatacenterNode } from './saveToDatacenter';
import { SearchDatacenterNode } from './searchDatacenter';
import sqlNode from './sqlNode';
import { WorkflowNode } from './workflowNode';

export interface CustomNodeOptions {
  handleChosen?: (nodeType: string, updateNodeData: any, value: string) => void;
}
export const getCustomNode = async (options: CustomNodeOptions) => {
  const pluginNode = PluginNode({ onChosen: options.handleChosen });
  const workflowNode = WorkflowNode({ onChosen: options.handleChosen });
  const searchDatacenterNode = await SearchDatacenterNode();
  const saveToDatacenterNode = await SaveToDatacenterNode();
  return {
    ...docNode,
    ...makeFileNode,
    ...downloadNode,
    ...sqlNode,
    [nodeNames.pluginNode]: pluginNode,
    [nodeNames.workflowNode]: workflowNode,
    [nodeNames.searchDatacenterNode]: searchDatacenterNode,
    [nodeNames.saveToDatacenterNode]: saveToDatacenterNode,
  };
};
