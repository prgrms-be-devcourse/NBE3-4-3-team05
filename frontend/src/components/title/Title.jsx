import React from "react";
import "./Title.css";

const Title = ({ type = "", text = "" }) => {
	switch (type) {
		case "error": {
			return <p className="error">{text}</p>;
		}
		case "title": {
			return <h2 className="title">{text}</h2>;
		}
		default:
			return <span>{text}</span>;
	}
};

export default Title;
