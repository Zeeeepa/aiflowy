import { ref } from 'vue';

import providerList from './providerList.json';

const providerOptions =
  ref<Array<{ icon: string; label: string; options: any; value: string }>>(
    providerList,
  );

/**
 * 根据传入的value，返回对应的icon属性
 * @param targetValue 要匹配的value值
 * @returns 匹配到的icon字符串，未匹配到返回空字符串
 */
export const getIconByValue = (targetValue: string): string => {
  const matchItem = providerOptions.value.find(
    (item) => item.value === targetValue,
  );

  return matchItem?.icon || '';
};

export const isSvgString = (icon: any) => {
  if (typeof icon !== 'string') return false;
  // 简单判断：是否包含 SVG 根标签
  return icon.trim().startsWith('<svg') && icon.trim().endsWith('</svg>');
};
