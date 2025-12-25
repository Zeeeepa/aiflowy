import type { BooleanField, ModelAbilityItem } from './model-ability';

import type { llmType } from '#/api';

/**
 * 将 llm 数据转换为标签选中状态
 * @param llm LLM数据对象
 * @param modelAbility 模型能力数组
 * @returns 更新后的模型能力数组
 */
export const mapLlmToModelAbility = (
  llm: llmType,
  modelAbility: ModelAbilityItem[],
): ModelAbilityItem[] => {
  return modelAbility.map((tag) => ({
    ...tag,
    selected: Boolean(llm[tag.field as keyof llmType]),
  }));
};

/**
 * 从标签选中状态生成 features 对象
 * @param modelAbility 模型能力数组
 * @returns 包含所有字段的features对象
 */
export const generateFeaturesFromModelAbility = (
  modelAbility: ModelAbilityItem[],
): Record<BooleanField, boolean> => {
  const features: Partial<Record<BooleanField, boolean>> = {};

  modelAbility.forEach((tag) => {
    features[tag.field] = tag.selected;
  });

  return features as Record<BooleanField, boolean>;
};

/**
 * 过滤显示选中的标签
 * @param modelAbility 模型能力数组
 * @returns 选中的标签数组
 */
export const getSelectedModelAbility = (
  modelAbility: ModelAbilityItem[],
): ModelAbilityItem[] => {
  return modelAbility.filter((tag) => tag.selected);
};

/**
 * 重置所有标签为未选中状态
 * @param modelAbility 模型能力数组
 */
export const resetModelAbility = (modelAbility: ModelAbilityItem[]): void => {
  modelAbility.forEach((tag) => {
    tag.selected = false;
  });
};

/**
 * 根据标签选中状态更新表单数据
 * @param modelAbility 模型能力数组
 * @param formData 表单数据对象
 */
export const updateFormDataFromModelAbility = (
  modelAbility: ModelAbilityItem[],
  formData: Record<BooleanField, boolean>,
): void => {
  modelAbility.forEach((tag) => {
    formData[tag.field] = tag.selected;
  });
};
