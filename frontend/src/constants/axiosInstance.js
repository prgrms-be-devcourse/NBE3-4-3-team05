import axios from "axios";
import { Project } from "src/constants/project";

const TOKEN = Project.getJwt();

const axiosInstance = axios.create({
	baseURL: Project.API_URL,
	timeout: 1000 * 30,
	headers: {
		"Content-Type": "application/json",
		Authorization: TOKEN ? {TOKEN} : "",
		//Pragma: "no-cache",
		//CacheControl: "no-cache",
		//Expires: "0",
		//usertype: "user",
	},
});

axiosInstance.interceptors.request.use(
	(config) => {
		const token = Project.getJwt();
		if (token) {
			config.headers.Authorization = token;
		}
		return config;
	},
	(error) => {
		return Promise.reject(error);
	}
);


axiosInstance.interceptors.response.use(
	(response) => {
		return response;
	},
	(error) => {
		return Promise.reject(error);
	},
);

export default axiosInstance;
