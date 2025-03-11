import axiosInstance from "src/constants/axiosInstance";
import { Project } from "src/constants/project";

const getLocationInfo = async (filterType, bottomLeft, topRight) => {
    try {
        const response = await axiosInstance
            .get(`${Project.API_URL}/kakaomap?filterType=${filterType}&bottomLeftLat=${bottomLeft.lat}&bottomLeftLng=${bottomLeft.lng}&topRightLat=${topRight.lat}&topRightLng=${topRight.lng}`,
                {
                    withCredentials: true,
                }
            );
        return response;
    } catch (error) {
        console.error("KakaoMap API 호출 실패:", error);
        // 실패 시 빈 데이터 반환하여 앱이 계속 작동하도록 함
        return { data: { data: [] } };
    }
};


const KakaoMapService = {
    getLocationInfo,
}

export { KakaoMapService };