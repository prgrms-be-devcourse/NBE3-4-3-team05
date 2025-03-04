import React from 'react';
import Logo from '../logo/Logo';
import './Header.css'
import { Project } from 'src/constants/project';
import { useNavigate } from 'react-router-dom';

import { UserService } from "src/services/UserService";

const Header = () => {
  const isLogin= Project.getJwt();
  const navigate = useNavigate();

  const handleLoginOrLogout = async () => {
    if (!isLogin) return navigate('/login');
    try {
        const res = await UserService.Logout();
        navigate('/');
        setTimeout(() => {
          window.location.reload();
        }, 100);
      } catch (error) {
        console.error("로그아웃 실패", error);
      }
  }

  const handleSignupOrMypage = () => {
    navigate(isLogin?'/mypage':"/join");
  }

	return (
    <header id="header">
      <Logo />
      <nav className='nav-wrap'>
        <button onClick={handleLoginOrLogout}>{!isLogin ? "로그인" : "로그아웃"}</button>
        <button onClick={handleSignupOrMypage}>{!isLogin ? "회원가입" : "마이페이지"}</button>
      </nav>
    </header>
  );
};

export default Header;