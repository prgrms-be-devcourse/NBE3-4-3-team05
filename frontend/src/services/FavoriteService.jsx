import axiosInstance from "src/constants/axiosInstance";
import { Project } from "src/constants/project";

// 관심사 리스트 조회
const getFavoriteList = async () => {
	const response = await axiosInstance.get(`${Project.API_URL}/favorites`, {
		withCredentials: true,
	});
	return response;
};

const FavoriteService = {
	getFavoriteList,
}

export { FavoriteService };