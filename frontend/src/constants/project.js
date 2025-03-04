import { Cookies } from "react-cookie";

const cookies = new Cookies();

const DOMAIN = process.env.REACT_APP_DEFAULT_URL;
const API_URL = process.env.REACT_APP_API_URL;
const PROJECT_ID = process.env.REACT_APP_PROJECT_ID;
const TOKEN = process.env.REACT_APP_ACCESS_TOKEN;
const REFRESH_TOKEN = process.env.REACT_APP_REFRESH_TOKEN;
const USER_SESSION = `ID_${PROJECT_ID}_SES`;

const getJwt = () => cookies.get(TOKEN);
const getRefreshJwt = () => cookies.get(REFRESH_TOKEN);

const setJwt = (accessToken) => {
	if (!accessToken) {
		console.error('Invalid access token');
		return;
	}
	cookies.set(TOKEN, accessToken, {
		path: "/",
		secure: true,
		sameSite: "Strict",
	});
};

const setUserId = (user = "") => {
	if (!user) {
		console.error('Invalid user ID');
		return;
	}
	cookies.set(USER_SESSION, user, {
		path: "/",
		domain: DOMAIN,
		secure: true,
		sameSite: "Strict",
	});
};

const getUserId = () => cookies.get(USER_SESSION) || "";

const loginCheck = () => {
	const jwt = getJwt();
	const userId = getUserId();
	return !!(jwt && userId);
};

const removeCookie = (name, options = {}) => {
	if (!name) {
		console.error('Cookie name is required');
		return;
	}
	cookies.remove(name, options);
};

const cookieRemove = () => {
	removeCookie(TOKEN, { path: "/", domain: DOMAIN });
	removeCookie(USER_SESSION, { path: "/", domain: DOMAIN });
};

const removeStorage = () => {
	try {
		localStorage.clear();
		sessionStorage.clear();
	} catch (error) {
		console.error('Error clearing localStorage/sessionStorage:', error);
	}
};

const userLogout = () => {
	removeStorage();
	cookieRemove();
	window.location.reload();
};

const Project = {
	setJwt,
	getJwt,
	getRefreshJwt,
	getUserId,
	setUserId,
	loginCheck,
	removeCookie,
	userLogout,
	removeStorage,
	cookieRemove,
	DOMAIN,
	API_URL,
	PROJECT_ID,
	USER_SESSION,
	REFRESH_TOKEN,
};

export { Project };
