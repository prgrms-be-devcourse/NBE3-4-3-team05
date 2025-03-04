import React from 'react';
import { Link } from 'react-router-dom';
import './Logo.css'

const Logo = () => {
	return (
    <Link to="/" aria-current="page">
      <h1 className="logo-title"> 5🌏</h1>
    </Link>
  );}


export default Logo;