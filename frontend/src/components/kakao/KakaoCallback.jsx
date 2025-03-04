import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { UserService } from "src/services/UserService";
import Alert from "../alert/Alert";

const KakaoCallback = () => {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const fetchKakaoLogin = async () => {
      const params = new URLSearchParams(location.search);
      const code = params.get("code");

      if (code) {
        try {
          const response = await UserService.KakaoLogin(code);

          Alert("카카오 로그인 성공!");
          navigate("/", { replace: true });
          window.location.reload();
        } catch (error) {
          Alert("카카오 로그인에 실패했습니다.");
          navigate("/login");
          window.location.reload();
        }
      } else {
        Alert("카카오 로그인에 실패했습니다.");
        navigate("/login");
        window.location.reload();
      }
    };

    fetchKakaoLogin();
  }, [location.search, navigate]);

  return <p>카카오 로그인 처리 중...</p>;
};

export default KakaoCallback;
