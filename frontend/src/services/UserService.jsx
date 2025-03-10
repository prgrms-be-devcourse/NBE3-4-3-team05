import axiosInstance from "src/constants/axiosInstance";
import { Project } from "src/constants/project";

// 회원가입
const SignUp = async (body) => {
	const response = await axiosInstance.post(
		`${Project.API_URL}/signup`,
		body,
		{ withCredentials: true },
	);
	return response;
};

// 로그인
const Login = async (body) => {
	const response = await axiosInstance.post(
		`${Project.API_URL}/login`,
		body,
		{ withCredentials: true },
	);
	const accessToken = response.headers["authorization"];
	if (accessToken) {
		Project.setJwt(accessToken);
		return response;
	}
};

// 로그아웃
const Logout = async () => {
	const response = await axiosInstance.post(`${Project.API_URL}/logout`);
	if (response) {
		Project.removeCookie( process.env.REACT_APP_ACCESS_TOKEN, Project.getJwt());
		Project.removeCookie( process.env.REACT_APP_REFRESH_TOKEN, Project.getRefreshJwt());
		return response;
	}
};

// 회원 정보 검색
const getUserInfo = async () => {
	const response = await axiosInstance.get(
		`${Project.API_URL}/users`,
		{ withCredentials: true }
	);
	if (response) {
		console.log("회원정보 조회 성공");
		return response;
	} else {
		return {};
	}
};

// 회원 모임 리스트 조회
const getUserClassInfo = async () => {
	const response = await axiosInstance.get(
		`${Project.API_URL}/users/classes`,
		{ withCredentials: true }
	);
	if (response) {
		console.log("회원 모임 리스트 조회 성공");	
		return response;
	} else {
		return {};
	}
};

// 회원 스케줄 리스트 조회
const getUserScheduleInfo = async () => {
	const response = await axiosInstance.get(
		`${Project.API_URL}/users/schedules`,
		{ withCredentials: true }
	);
	if (response) {
		console.log("회원 스케줄 리스트 조회 성공");	
		return response;
	} else {
		return {};
	}
};

// 회원 정보 수정
const ModifyUserInfo = async (body) => {
	const response = await axiosInstance.patch(
		`${Project.API_URL}/users/profile`,
		body,
		{ withCredentials: true },
	);
	return response;
}

// 카카오 로그인
const KakaoLogin = async (code) => {
  try {
    const response = await axiosInstance.get(
      `${Project.API_URL}/login/kakao`,
      {
        params: { code },
        withCredentials: true,
      }
    );
    
    const accessToken = response.headers["authorization"];
    
    if (accessToken) {
      Project.setJwt(accessToken);
      return response;
    } else {
      throw new Error("Access token not found");
    }
  } catch (error) {
    throw new Error("Kakao login failed");
  }
};


const UserService = {
	SignUp,
	Login,
	Logout,
	getUserInfo,
	getUserClassInfo,
	getUserScheduleInfo,
	ModifyUserInfo,
	KakaoLogin,
};

export { UserService };
