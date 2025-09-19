import React from 'react';
import {env} from "../env.ts";

const Footer: React.FC = () => {
  return (
    <footer className="px-6 py-4 border-t border-slate-700 mt-auto bg-slate-800">
      <div className="max-w-4xl mx-auto text-center">
        <p className="text-slate-400 text-sm">
          Silent Guard {env.VITE_BUILD}
        </p>
      </div>
    </footer>
  );
}

export default Footer;
