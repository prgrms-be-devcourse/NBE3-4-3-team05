import axiosInstance from "src/constants/axiosInstance";
import { Project } from "src/constants/project";

const getLocationInfo = async (filterType, bottomLeft, topRight) => {
    const response = await axiosInstance
    .get(`${Project.API_URL}/kakaomap?filterType=${filterType}&bottomLeftLat=${bottomLeft.lat}&bottomLeftLng=${bottomLeft.lng}&topRightLat=${topRight.lat}&topRightLng=${topRight.lng}`, 
      {
        withCredentials: true,
      }
    );
    return response;
  };


const KakaoMapService = {
    getLocationInfo,
}

export { KakaoMapService };