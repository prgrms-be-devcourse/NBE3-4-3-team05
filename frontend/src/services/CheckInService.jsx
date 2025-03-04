import axiosInstance from "src/constants/axiosInstance";
import { Project } from "src/constants/project";

// 해당 모임에 대한 투표 정보
const getMyCheckIn = async (scheduleId) => {
    try {
        const token = Project.getJwt();
        if (!token) {
            console.error('로그인 필요');
            return;
        }
        const response = await axiosInstance.get(`${Project.API_URL}/checkin/${scheduleId}/my`);
        if (response.data) {
            return response.data.data;
        } else {
            console.error('No data returned from the server');
        }
    } catch (error) {
        if (error.response) {
            console.error('Error response:', error.response.data);
            console.error('Status code:', error.response.status);
        } else {
            console.error('Request failed:', error.message);
        }
    }
};


// 투표 생성 ( 첫 투표 시 생성 )
const postCheckIn = async (body) => {
    const response = await axiosInstance.post(
        `${Project.API_URL}/checkin`,
        body,
        { withCredentials: true },
    );
    return response;
};

// 투표 수정 ( 첫 투표 이후 사용 )
const putCheckIn = async (body) => {
    const response = await axiosInstance.put(
        `${Project.API_URL}/checkin`,
        body,
        { withCredentials: true },
    );
    return response;
};


const CheckInService = {
    getMyCheckIn,
    postCheckIn,
    putCheckIn,
}

export { CheckInService };