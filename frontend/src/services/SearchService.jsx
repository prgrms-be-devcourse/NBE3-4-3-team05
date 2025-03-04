import axiosInstance from "../constants/axiosInstance";
import { Project } from "src/constants/project";

// 모임 검색/ 정렬 리스트 조회
const getSearchClasses = async (params = '') => {
    const response = await axiosInstance.get(
        `${Project.API_URL}/search/classes${params}`,
        {
            withCredentials: true,
        }
    )
    return response;
}

const SearchService = {
    getSearchClasses
};

export { SearchService };