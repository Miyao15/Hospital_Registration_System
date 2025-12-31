// 定位工具
const AMAP_KEY = '4715be851a025f4db49e9d9acb754ee4';

/**
 * 获取当前位置
 */
export const getCurrentLocation = async () => {
  const result = await getLocationByIP();
  if (result && result.city) {
    return result;
  }
  throw new Error('定位失败');
};

/**
 * IP定位 - 多服务备用
 */
export const getLocationByIP = async () => {
  // 1. 先尝试高德
  try {
    const amapResult = await tryAmap();
    if (amapResult && amapResult.city) {
      return amapResult;
    }
  } catch (e) {
    console.warn('高德定位失败:', e.message);
  }
  
  // 2. 备用：ip-api.com
  try {
    const ipApiResult = await tryIpApi();
    if (ipApiResult && ipApiResult.city) {
      return ipApiResult;
    }
  } catch (e) {
    console.warn('ip-api定位失败:', e.message);
  }
  
  return null;
};

// 高德IP定位
const tryAmap = async () => {
  const url = `https://restapi.amap.com/v3/ip?key=${AMAP_KEY}`;
  const response = await fetch(url);
  const data = await response.json();
  
  console.log('高德IP定位返回:', data);
  
  if (data.status === '1') {
    let city = data.city;
    let province = data.province;
    
    // 处理空数组情况
    if (Array.isArray(city) && city.length === 0) city = '';
    if (Array.isArray(province) && province.length === 0) province = '';
    if (Array.isArray(city)) city = city[0] || '';
    if (Array.isArray(province)) province = province[0] || '';
    
    if (city || province) {
      return {
        province: province || '',
        city: city || province || '',
        district: '',
        address: city || province || '',
        location: null
      };
    }
  }
  return null;
};

// ip-api.com (免费，支持中文)
const tryIpApi = async () => {
  const response = await fetch('http://ip-api.com/json/?lang=zh-CN');
  const data = await response.json();
  
  console.log('ip-api定位返回:', data);
  
  if (data.status === 'success' && data.city) {
    return {
      province: data.regionName || '',
      city: data.city || '',
      district: '',
      address: data.city || data.regionName || '',
      location: { lng: data.lon, lat: data.lat }
    };
  }
  return null;
};

export default {
  getCurrentLocation,
  getLocationByIP
};
