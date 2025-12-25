import { $t } from '#/locales';

export type BooleanField =
  | 'supportAudio'
  | 'supportFree'
  | 'supportImage'
  | 'supportImageB64Only'
  | 'supportThinking'
  | 'supportTool'
  | 'supportVideo';

export interface ModelAbilityItem {
  activeType: 'danger' | 'info' | 'primary' | 'success' | 'warning';
  defaultType: 'info';
  field: BooleanField;
  label: string;
  selected: boolean;
  value: string;
}

/**
 * 获取模型能力标签的默认配置
 * @returns ModelAbilityItem[] 模型能力配置数组
 */
export const getDefaultModelAbility = (): ModelAbilityItem[] => [
  {
    label: $t('llm.modelAbility.supportThinking'),
    value: 'thinking',
    defaultType: 'info',
    activeType: 'success',
    selected: false,
    field: 'supportThinking',
  },
  {
    label: $t('llm.modelAbility.supportTool'),
    value: 'tool',
    defaultType: 'info',
    activeType: 'success',
    selected: false,
    field: 'supportTool',
  },
  {
    label: $t('llm.modelAbility.supportVideo'),
    value: 'video',
    defaultType: 'info',
    activeType: 'success',
    selected: false,
    field: 'supportVideo',
  },
  {
    label: $t('llm.modelAbility.supportImage'),
    value: 'image',
    defaultType: 'info',
    activeType: 'success',
    selected: false,
    field: 'supportImage',
  },
  {
    label: $t('llm.modelAbility.supportFree'),
    value: 'free',
    defaultType: 'info',
    activeType: 'success',
    selected: false,
    field: 'supportFree',
  },
  {
    label: $t('llm.modelAbility.supportAudio'),
    value: 'audio',
    defaultType: 'info',
    activeType: 'success',
    selected: false,
    field: 'supportAudio',
  },
  {
    label: $t('llm.modelAbility.supportImageB64Only'),
    value: 'imageB64',
    defaultType: 'info',
    activeType: 'success',
    selected: false,
    field: 'supportImageB64Only',
  },
];

/**
 * 根据字段数组获取对应的标签选中状态
 * @param modelAbility 模型能力数组
 * @param fields 需要获取的字段数组
 * @returns 以字段名为键、选中状态为值的对象
 */
export const getTagsSelectedStatus = (
  modelAbility: ModelAbilityItem[],
  fields: BooleanField[],
): Record<BooleanField, boolean> => {
  const result: Partial<Record<BooleanField, boolean>> = {};

  fields.forEach((field) => {
    const tagItem = modelAbility.find((tag) => tag.field === field);
    result[field] = tagItem?.selected ?? false;
  });

  return result as Record<BooleanField, boolean>;
};

/**
 * 同步标签选中状态与formData中的布尔字段
 * @param modelAbility 模型能力数组
 * @param formData 表单数据对象
 */
export const syncTagSelectedStatus = (
  modelAbility: ModelAbilityItem[],
  formData: Record<BooleanField, boolean>,
): void => {
  modelAbility.forEach((tag) => {
    tag.selected = formData[tag.field] ?? false;
  });
};

/**
 * 处理标签点击事件
 * @param modelAbility 模型能力数组
 * @param item 被点击的标签项
 * @param formData 表单数据对象
 */
export const handleTagClick = (
  // modelAbility: ModelAbilityItem[],
  item: ModelAbilityItem,
  formData: Record<BooleanField, boolean>,
): void => {
  // 切换标签选中状态
  item.selected = !item.selected;

  // 同步更新formData中的布尔字段
  formData[item.field] = item.selected;
};

/**
 * 根据字段获取对应的标签项
 * @param modelAbility 模型能力数组
 * @param field 布尔字段名
 * @returns 标签项 | undefined
 */
export const getTagByField = (
  modelAbility: ModelAbilityItem[],
  field: BooleanField,
): ModelAbilityItem | undefined => {
  return modelAbility.find((tag) => tag.field === field);
};

/**
 * 获取所有支持的BooleanField数组
 */
export const getAllBooleanFields = (): BooleanField[] => [
  'supportThinking',
  'supportTool',
  'supportImage',
  'supportImageB64Only',
  'supportVideo',
  'supportAudio',
  'supportFree',
];
