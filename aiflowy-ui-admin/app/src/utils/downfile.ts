import { $t } from '@aiflowy/locales';

import { ElMessage } from 'element-plus';

/**
 * 通用文件下载方法（基于blob流，强制触发下载，避免预览）
 * @param url 文件下载地址（支持跨域/同源）
 * @param defaultFileName 默认文件名（响应头无文件名时使用）
 */
export const downloadFile = async (url: string, defaultFileName: string) => {
  // 校验参数
  if (!url) {
    return;
  }

  try {
    // 1. 发起fetch请求获取文件流（支持跨域，需后端配置CORS）
    const response = await fetch(url, {
      method: 'GET',
      credentials: 'include', // 携带cookie（如需鉴权时添加）
    });

    // 2. 校验响应状态
    if (!response.ok) {
      throw new Error(`请求失败，状态码：${response.status}`);
    }

    // 3. 提取文件名（优先从响应头获取，兼容中文解码）
    const fileName = defaultFileName || '下载文件';
    // 4. 将响应转为blob对象
    const blob = await response.blob();

    // 5. 创建blob URL（关键：强制触发下载，避免预览）
    const downloadUrl = window.URL.createObjectURL(blob);

    // 6. 创建临时a标签触发下载
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = fileName; // 强制下载（blob URL不受跨域限制）
    link.style.display = 'none';
    document.body.append(link);
    link.click();

    // 7. 清理资源（避免内存泄漏）
    link.remove();
    window.URL.revokeObjectURL(downloadUrl);

    // 8. 下载成功提示
    ElMessage.success($t('message.downloadSuccess'));
  } catch (error) {
    console.error('文件下载失败：', error);
    ElMessage.error($t('message.downloadFail'));
  }
};

/**
 * 适配S3文件下载的快捷方法（复用核心downloadFile）
 * @param filePath
 * @param fileName
 */
export const fetchFileDownload = async (filePath: string, fileName: string) => {
  if (!filePath || !filePath.startsWith('http')) {
    ElMessage.warning('无效的文件下载地址');
    return;
  }

  // 调用核心下载方法
  await downloadFile(filePath, fileName);
};
