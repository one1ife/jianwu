/**
 * 设备检测工具
 * 用于判断当前设备类型
 */

/**
 * 判断是否为移动设备
 * @returns {boolean} 是否为移动设备
 */
export function isMobile() {
  const userAgent = navigator.userAgent.toLowerCase();
  return /android|webos|iphone|ipad|ipod|blackberry|windows phone/.test(userAgent);
}

/**
 * 判断是否为iOS设备
 * @returns {boolean} 是否为iOS设备
 */
export function isIOS() {
  const userAgent = navigator.userAgent.toLowerCase();
  return /iphone|ipad|ipod/.test(userAgent);
}

/**
 * 判断是否为Android设备
 * @returns {boolean} 是否为Android设备
 */
export function isAndroid() {
  const userAgent = navigator.userAgent.toLowerCase();
  return /android/.test(userAgent);
}

/**
 * 根据屏幕宽度判断是否为移动设备视图
 * @returns {boolean} 是否为移动设备视图
 */
export function isMobileView() {
  return window.innerWidth < 768;
} 