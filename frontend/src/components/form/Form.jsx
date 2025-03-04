// @ts-nocheck
import React from "react";

const Form = ({ title, element, onSubmit, onChange, disabled }) => {
	return (
		<form className={`${title}-form`} onSubmit={onSubmit}>
			{Array.isArray(element) &&
				element.length > 0 &&
				element.map((ele) => (
					<input
						id={ele.name}
						className="input"
						name={ele.name}
						type={ele.type}
						value={ele.value}
						onChange={(e) => onChange(ele.name, e.target.value)}
						placeholder={ele.placeholder}
						disabled={disabled}
						key={ele.id}
					/>
				))}
			<button
				className="button login-btn"
				type="submit"
				disabled={disabled}
			>
				{disabled ? "loading..." : "onSubmit"}
			</button>
		</form>
	);
};

export default Form;
