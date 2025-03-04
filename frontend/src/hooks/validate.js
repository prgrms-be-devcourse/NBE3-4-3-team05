// @ts-nocheck
import Alert from "src/components/alert/Alert";

const Validate = (body) => {
	const requiredFields = Object.keys(body);

	for (let field of requiredFields) {
		if (!body[field]) {
			Alert(`${field}을/를 입력해 주세요.`);
			return false;
		}
	}
	return true;
};

export default Validate;
