import { useAccessStore } from '@aiflowy/stores';

export function hasPermission(codes: string[]) {
  const accessStore = useAccessStore();
  const userCodesSet = new Set(accessStore.accessCodes);

  const intersection = codes.filter((item) => userCodesSet.has(item));
  return intersection.length > 0;
}
