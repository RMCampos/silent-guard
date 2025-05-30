import { useState, useEffect } from 'react';
import { Shield, Clock, Mail, User, LogOut, Eye, EyeOff, Plus, Edit, Trash2, Save, X } from 'lucide-react';
import LoginModal from './components/LoginModal';

type Message = {
  id: number;
  title: string;
  content: string;
  days: number;
  recipient: string;
  active: boolean
}

type ResetForm = {
  email: string;
}

type AccountForm = {
  name: string;
  email: string;
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

const emptyMessage: Message = { id: 0, title: '', content: '', days: 30, recipient: '', active: true };
const emptyResetForm: ResetForm = { email: '' };
const emptyAccountForm: AccountForm = { name: 'John Doe', email: 'john@example.com', currentPassword: '', newPassword: '', confirmPassword: '' };

const messagesMock: Message[] = [
  { id: 1, title: 'Emergency Contact', content: 'Important information for my family...', days: 30, recipient: 'family@example.com', active: true },
  { id: 2, title: 'Business Handover', content: 'Access credentials and procedures...', days: 7, recipient: 'partner@company.com', active: false }
];

const App = () => {
  const [currentPage, setCurrentPage] = useState<string>('landing');
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [showLoginModal, setShowLoginModal] = useState<boolean>(false);
  const [showAccountModal, setShowAccountModal] = useState<boolean>(false);
  const [loginMode, setLoginMode] = useState<string>('login');
  const [messages, setMessages] = useState<Message[]>(messagesMock);
  const [editingMessage, setEditingMessage] = useState<Message | null>(null);
  const [newMessage, setNewMessage] = useState<Message>(emptyMessage);
  const [resetForm, setResetForm] = useState(emptyResetForm);
  const [accountForm, setAccountForm] = useState<AccountForm>(emptyAccountForm);

  useEffect(() => {
    console.log('isAuthenticated', isAuthenticated);
    const auth = localStorage.getItem('silentGuardAuth');
    if (auth) {
      setIsAuthenticated(true);
      setCurrentPage('dashboard');
    }
  }, [isAuthenticated]);

  const handleLogin = () => {
    // e.preventDefault();

    // Simulate login
    setIsAuthenticated(true);
    setCurrentPage('dashboard');
    setShowLoginModal(false);
    setLoginForm({ email: '', password: '' });
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setCurrentPage('landing');
  };

  const handleResetPassword = () => {
    alert('Password reset link sent to ' + resetForm.email);
    setResetForm({ email: '' });
    setLoginMode('login');
  };

  const handleSaveMessage = () => {
    if (editingMessage) {
      setMessages(messages.map(msg => msg.id === editingMessage.id ? editingMessage : msg));
      setEditingMessage(null);
    } else {
      const newMsg = { ...newMessage, id: Date.now() };
      setMessages([...messages, newMsg]);
      // setNewMessage({ title: '', content: '', days: 30, recipient: '', active: true });
    }
  };

  const handleDeleteMessage = (id: number) => {
    if (confirm('Are you sure you want to delete this message?')) {
      setMessages(messages.filter(msg => msg.id !== id));
    }
  };

  const toggleMessageStatus = (id: number) => {
    setMessages(messages.map(msg => 
      msg.id === id ? { ...msg, active: !msg.active } : msg
    ));
  };

  const LandingPage = () => (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
      {/* Header */}
      <header className="px-6 py-4 flex justify-between items-center">
        <div className="flex items-center space-x-2">
          <Shield className="w-8 h-8 text-blue-400" />
          <h1 className="text-2xl font-bold text-white">Silent Guard</h1>
        </div>
        <button 
          onClick={() => setShowLoginModal(true)}
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
            onClick={() => setShowLoginModal(true)}
            className="mt-12 bg-blue-600 hover:bg-blue-700 text-white px-8 py-4 rounded-lg text-lg font-semibold transition-colors"
          >
            Get Started Today
          </button>
        </div>
      </main>
    </div>
  );

  const Dashboard = () => (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b px-6 py-4">
        <div className="flex justify-between items-center">
          <div className="flex items-center space-x-2">
            <Shield className="w-8 h-8 text-blue-600" />
            <h1 className="text-2xl font-bold text-gray-900">Silent Guard</h1>
          </div>
          <div className="flex items-center space-x-4">
            <button 
              onClick={() => setShowAccountModal(true)}
              className="flex items-center space-x-2 text-gray-700 hover:text-gray-900"
            >
              <User className="w-5 h-5" />
              <span>Account</span>
            </button>
            <button 
              onClick={handleLogout}
              className="flex items-center space-x-2 text-gray-700 hover:text-gray-900"
            >
              <LogOut className="w-5 h-5" />
              <span>Logout</span>
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-6xl mx-auto px-6 py-8">
        <div className="mb-8">
          <h2 className="text-3xl font-bold text-gray-900 mb-2">Your Messages</h2>
          <p className="text-gray-600">Manage your dead man's switch messages and monitoring settings.</p>
        </div>

        {/* New Message Form */}
        <div className="bg-white rounded-xl shadow-sm border p-6 mb-8">
          <h3 className="text-xl font-semibold text-gray-900 mb-4">Create New Message</h3>
          <div className="grid md:grid-cols-2 gap-4">
            <input
              type="text"
              placeholder="Message title"
              value={newMessage.title}
              onChange={(e) => setNewMessage({...newMessage, title: e.target.value})}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <input
              type="email"
              placeholder="Recipient email"
              value={newMessage.recipient}
              onChange={(e) => setNewMessage({...newMessage, recipient: e.target.value})}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <div className="mt-4">
            <textarea
              placeholder="Message content"
              rows={4}
              value={newMessage.content}
              onChange={(e) => setNewMessage({...newMessage, content: e.target.value})}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <div className="mt-4 flex items-center space-x-4">
            <div className="flex items-center space-x-2">
              <label className="text-sm font-medium text-gray-700">Days until trigger:</label>
              <input
                type="number"
                min="1"
                max="365"
                value={newMessage.days}
                onChange={(e) => setNewMessage({...newMessage, days: parseInt(e.target.value)})}
                className="w-20 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <button
              onClick={handleSaveMessage}
              disabled={!newMessage.title || !newMessage.content || !newMessage.recipient}
              className="flex items-center space-x-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white px-4 py-2 rounded-lg transition-colors"
            >
              <Plus className="w-4 h-4" />
              <span>Add Message</span>
            </button>
          </div>
        </div>

        {/* Messages List */}
        <div className="grid gap-6">
          {messages.map((message) => (
            <div key={message.id} className="bg-white rounded-xl shadow-sm border p-6">
              {editingMessage?.id === message.id ? (
                <div className="space-y-4">
                  <input
                    type="text"
                    value={editingMessage.title}
                    onChange={(e) => setEditingMessage({...editingMessage, title: e.target.value})}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <input
                    type="email"
                    value={editingMessage.recipient}
                    onChange={(e) => setEditingMessage({...editingMessage, recipient: e.target.value})}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <textarea
                    rows={4}
                    value={editingMessage.content}
                    onChange={(e) => setEditingMessage({...editingMessage, content: e.target.value})}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-2">
                      <label className="text-sm font-medium text-gray-700">Days:</label>
                      <input
                        type="number"
                        min="1"
                        max="365"
                        value={editingMessage.days}
                        onChange={(e) => setEditingMessage({...editingMessage, days: parseInt(e.target.value)})}
                        className="w-20 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      />
                    </div>
                    <div className="flex space-x-2">
                      <button
                        onClick={handleSaveMessage}
                        className="flex items-center space-x-1 bg-green-600 hover:bg-green-700 text-white px-3 py-2 rounded-lg transition-colors"
                      >
                        <Save className="w-4 h-4" />
                        <span>Save</span>
                      </button>
                      <button
                        onClick={() => setEditingMessage(null)}
                        className="flex items-center space-x-1 bg-gray-600 hover:bg-gray-700 text-white px-3 py-2 rounded-lg transition-colors"
                      >
                        <X className="w-4 h-4" />
                        <span>Cancel</span>
                      </button>
                    </div>
                  </div>
                </div>
              ) : (
                <div>
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <h3 className="text-xl font-semibold text-gray-900">{message.title}</h3>
                      <p className="text-sm text-gray-600">To: {message.recipient}</p>
                    </div>
                    <div className="flex items-center space-x-2">
                      <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                        message.active 
                          ? 'bg-green-100 text-green-800' 
                          : 'bg-gray-100 text-gray-800'
                      }`}>
                        {message.active ? 'Active' : 'Inactive'}
                      </span>
                    </div>
                  </div>
                  <p className="text-gray-700 mb-4">{message.content}</p>
                  <div className="flex justify-between items-center">
                    <span className="text-sm text-gray-600">
                      Trigger in {message.days} days
                    </span>
                    <div className="flex space-x-2">
                      <button
                        onClick={() => toggleMessageStatus(message.id)}
                        className={`px-3 py-1 rounded-lg text-sm transition-colors ${
                          message.active
                            ? 'bg-red-100 text-red-700 hover:bg-red-200'
                            : 'bg-green-100 text-green-700 hover:bg-green-200'
                        }`}
                      >
                        {message.active ? 'Deactivate' : 'Activate'}
                      </button>
                      <button
                        onClick={() => setEditingMessage(message)}
                        className="flex items-center space-x-1 bg-blue-100 text-blue-700 hover:bg-blue-200 px-3 py-1 rounded-lg transition-colors"
                      >
                        <Edit className="w-4 h-4" />
                        <span>Edit</span>
                      </button>
                      <button
                        onClick={() => handleDeleteMessage(message.id)}
                        className="flex items-center space-x-1 bg-red-100 text-red-700 hover:bg-red-200 px-3 py-1 rounded-lg transition-colors"
                      >
                        <Trash2 className="w-4 h-4" />
                        <span>Delete</span>
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      </main>
    </div>
  );

  // Account Modal
  const AccountModal = () => (
    showAccountModal && (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
        <div className="bg-white rounded-xl max-w-md w-full p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold text-gray-900">Account Settings</h2>
            <button 
              onClick={() => setShowAccountModal(false)}
              className="text-gray-400 hover:text-gray-600"
            >
              <X className="w-6 h-6" />
            </button>
          </div>

          <form className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
              <input
                type="text"
                value={accountForm.name}
                onChange={(e) => setAccountForm({...accountForm, name: e.target.value})}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input
                type="email"
                value={accountForm.email}
                onChange={(e) => setAccountForm({...accountForm, email: e.target.value})}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Current Password</label>
              <input
                type="password"
                value={accountForm.currentPassword}
                onChange={(e) => setAccountForm({...accountForm, currentPassword: e.target.value})}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">New Password</label>
              <input
                type="password"
                value={accountForm.newPassword}
                onChange={(e) => setAccountForm({...accountForm, newPassword: e.target.value})}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Confirm New Password</label>
              <input
                type="password"
                value={accountForm.confirmPassword}
                onChange={(e) => setAccountForm({...accountForm, confirmPassword: e.target.value})}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <button
              type="submit"
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-semibold transition-colors"
              onClick={(e) => {
                e.preventDefault();
                alert('Account updated successfully!');
                setShowAccountModal(false);
              }}
            >
              Update Account
            </button>
          </form>
        </div>
      </div>
    )
  );

  return (
    <div>
      {currentPage === 'landing' ? <LandingPage /> : <Dashboard />}
      <LoginModal
        show={showLoginModal}
        loginMode={loginMode}
        onClose={() => setShowLoginModal(false)}
        onSubmitLogin={handleLogin}
      />
      <AccountModal />
    </div>
  );
  
};

export default App;
