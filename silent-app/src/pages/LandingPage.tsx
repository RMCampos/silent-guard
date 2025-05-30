import { Clock, Mail, Shield } from 'lucide-react';
import React from 'react';

type Props = {
  onClickLogin: () => void;
}

const LandingPage: React.FC<Props> = (props) => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
      {/* Header */}
      <header className="px-6 py-4 flex justify-between items-center">
        <div className="flex items-center space-x-2">
          <Shield className="w-8 h-8 text-blue-400" />
          <h1 className="text-2xl font-bold text-white">Silent Guard</h1>
        </div>
        <button 
          onClick={props.onClickLogin}
          className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg transition-colors"
        >
          Login
        </button>
      </header>

      {/* Hero Section */}
      <main className="px-6 py-20">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-5xl font-bold text-white mb-6">
            Your Digital <span className="text-blue-400">Safety Net</span>
          </h2>
          <p className="text-xl text-gray-300 mb-12 max-w-2xl mx-auto">
            Silent Guard is a dead man's switch service that automatically sends important messages 
            when you're unable to check in. Protect your loved ones with crucial information.
          </p>
          
          <div className="grid md:grid-cols-3 gap-8 mt-16">
            <div className="bg-white/10 backdrop-blur-sm rounded-xl p-6 border border-white/20">
              <Clock className="w-12 h-12 text-blue-400 mx-auto mb-4" />
              <h3 className="text-xl font-semibold text-white mb-2">Automatic Monitoring</h3>
              <p className="text-gray-300">Set custom time intervals. If you don't check in, your messages are automatically sent.</p>
            </div>
            
            <div className="bg-white/10 backdrop-blur-sm rounded-xl p-6 border border-white/20">
              <Mail className="w-12 h-12 text-blue-400 mx-auto mb-4" />
              <h3 className="text-xl font-semibold text-white mb-2">Secure Delivery</h3>
              <p className="text-gray-300">Encrypted messages delivered to your specified recipients when needed most.</p>
            </div>
            
            <div className="bg-white/10 backdrop-blur-sm rounded-xl p-6 border border-white/20">
              <Shield className="w-12 h-12 text-blue-400 mx-auto mb-4" />
              <h3 className="text-xl font-semibold text-white mb-2">Complete Privacy</h3>
              <p className="text-gray-300">Your messages are encrypted and only accessible when the conditions are met.</p>
            </div>
          </div>

          <button 
            onClick={props.onClickLogin}
            className="mt-12 bg-blue-600 hover:bg-blue-700 text-white px-8 py-4 rounded-lg text-lg font-semibold transition-colors"
          >
            Get Started Today
          </button>
        </div>
      </main>
    </div>
  );
};

export default LandingPage;
