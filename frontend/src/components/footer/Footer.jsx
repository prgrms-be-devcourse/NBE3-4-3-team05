import React from "react";
import "./Footer.css";
import { Element } from "src/constants/element";

export default function Footer() {
  return (
    <footer id="footer" aria-label="Footer nav, and copyright information">
      <h4 className="footer-info">More Details about Team 5's project !</h4>
      <hr />
      <ul className="footer-icon-wrap">
        {Element.FOOTER_ICONS.map(({ href, label, IconComponent, id }) => (
          <li key={id}>
            <a
              href={href}
              target="_blank"
              rel="noopener noreferrer"
              aria-label={label}
            >
              {IconComponent}
            </a>
          </li>
        ))}
      </ul>
      <p className="copyright">
        &copy; {new Date().getFullYear()}, Team 5 All Rights Reserved.
      </p>
    </footer>
  );
}
