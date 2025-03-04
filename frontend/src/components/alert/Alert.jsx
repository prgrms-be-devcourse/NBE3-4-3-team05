// @ts-nocheck
import "./Alert.css";

const Alert = async (header, cancelButtonText, confirmButtonText, _select) => {
	let select = _select;

	const _alertCreate = async (
		header,
		cancelButtonText,
		confirmButtonText
	) => {
		let button = `<button id="customBtnSelect"><p>${confirmButtonText || "확인"}</p></button>`;

		if (cancelButtonText && confirmButtonText) {
			button = `
                <button id="customBtnSelect"><p>${confirmButtonText}</p></button>
                <button id="customBtnClose"><p>${cancelButtonText}</p></button>`;
		}

		let ele = document.querySelector("body");
		var _alert = document.createElement("div");
		_alert.className = "alertBg show-alert";
		_alert.id = "alertBg";
		_alert.innerHTML = `<div class="custom-alert">
                          <div class="alert-text">
                            <p>${header}</p>
                          </div>
                          <div class="alert-footer">
                            ${button}
                          </div>
                        </div>`;
		ele.appendChild(_alert);

		if (cancelButtonText && document.getElementById("customBtnClose")) {
			document.getElementById("customBtnClose").onclick = () => {
				selectAlert(false);
				closeAlert();
			};
		}

		document.getElementById("customBtnSelect").onclick = () => {
			selectAlert(true);
			closeAlert();
		};

		document.getElementById("alertBg").onclick = (e) =>
			currentTargetClick(e);
	};

	const currentTargetClick = ({ target }) => {
		const alert = document.querySelector(".custom-alert");

		if (alert) {
			if (!alert.contains(target)) {
				selectAlert(false);
				closeAlert();
			}
		}
	};

	const closeAlert = () => {
		const child = document.getElementById("alertBg");
		if (child) child.parentNode.removeChild(child);
	};

	const selectAlert = (_res) => {
		if (select) select(_res);
		if (_res === false) {
			closeAlert();
		}
	};

	if (!document.getElementById("alertBg")) {
		_alertCreate(header, cancelButtonText, confirmButtonText);
	}
};

export default Alert;
