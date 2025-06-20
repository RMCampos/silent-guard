import { Edit, LogOut, Plus, Save, Shield, Trash2, User, X } from 'lucide-react';
import React, { Fragment, useCallback, useEffect, useState } from 'react';
import type { Message } from '../types/Message';
import { useAuth0 } from '@auth0/auth0-react';
import AccountModal from '../components/AccountModal';
import { createMessage, deleteMessage, getMessages, signInOrSignUpUser, updateMessage } from '../services/apiService';
import { useToken } from '../context/TokenContext';

type Props = {
  setPageChanged: () => void;
};

const emptyMessage: Message = { id: 0, title: '', content: '', daysToTrigger: 30, recipients: '', active: true };

const DashboardPage: React.FC<Props> = (props) => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [editingMessage, setEditingMessage] = useState<Message | null>(null);
  const [newMessage, setNewMessage] = useState<Message>(emptyMessage);
  const [showAccountModal, setShowAccountModal] = useState<boolean>(false);
  const [userValidated, setUserValidated] = useState<boolean>(false);
  const { user, isAuthenticated, logout } = useAuth0();
  const { accessToken } = useToken();

  const handleSaveMessage = async () => {
    if (editingMessage) {
      try {
        await updateMessage(accessToken, editingMessage);
        setEditingMessage(null);
        fetchAllMessages();
      }
      catch (e: unknown) {
        const error = e as Error;
        console.error(error);
        // TODO: handle error nicely
      }
    } else {
      try {
        await createMessage(accessToken, newMessage);
        fetchAllMessages();
      }
      catch (e: unknown) {
        const error = e as Error;
        console.error(error);
        // TODO: handle error nicely
      }
    }
  };

  const handleDeleteMessage = async (id: number) => {
    if (confirm('Are you sure you want to delete this message?')) {
      const messageToDelete = messages.filter(msg => msg.id === id);

      if (messageToDelete.length > 0) {
        try {
          await deleteMessage(accessToken, messageToDelete[0].id);
          setEditingMessage(null);
          fetchAllMessages();
        }
        catch (e: unknown) {
          const error = e as Error;
          console.error(error);
          // TODO: handle error nicely
        }
      }
    }
  };

  const fetchAllMessages = async () => {
    if (userValidated) {
      try {
        const messagesFetched: Message[] = await getMessages(accessToken);
        setMessages(messagesFetched);
      } catch (e: unknown) {
        const error = e as Error;
        console.error(error);
        // TODO: handle error nicely
      }
    }
  };

  const toggleMessageStatus = async (id: number) => {
    const messageToActivate = messages.filter(msg => msg.id === id);

    if (messageToActivate.length > 0) {
      try {
        messageToActivate[0].active = !messageToActivate[0];
        await updateMessage(accessToken, messageToActivate[0]);
        setEditingMessage(null);
        fetchAllMessages();
      }
      catch (e: unknown) {
        const error = e as Error;
        console.error(error);
        // TODO: handle error nicely
      }
    }
  };

  const validateUser = useCallback(async () => {
    try {
      await signInOrSignUpUser(accessToken)
      setUserValidated(true);
      
    }
    catch (e: unknown) {
      const err = e as Error;
      if (err.message === 'Invalid user!') {
        props.setPageChanged();
      }
      else {
        console.error(err);
      }
    }
  }, [props, accessToken]);

  useEffect(() => {
    if (!isAuthenticated) {
      props.setPageChanged();
    }

    if (!userValidated) { 
      validateUser();  
    }

    fetchAllMessages();
  }, [isAuthenticated, props, accessToken, userValidated, validateUser]);

  return (
    <Fragment>
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
                onClick={() => logout({})}
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
                value={newMessage.recipients}
                onChange={(e) => setNewMessage({...newMessage, recipients: e.target.value})}
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
                <label className="text-sm font-medium text-gray-700">Trigger every (days):</label>
                <input
                  type="number"
                  min="1"
                  max="365"
                  value={newMessage.daysToTrigger}
                  onChange={(e) => setNewMessage({...newMessage, daysToTrigger: parseInt(e.target.value)})}
                  className="w-20 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
              <button
                onClick={handleSaveMessage}
                disabled={!newMessage.title || !newMessage.content || !newMessage.recipients}
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
                      value={editingMessage.recipients}
                      onChange={(e) => setEditingMessage({...editingMessage, recipients: e.target.value})}
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
                          value={editingMessage.daysToTrigger}
                          onChange={(e) => setEditingMessage({...editingMessage, daysToTrigger: parseInt(e.target.value)})}
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
                        <p className="text-sm text-gray-600">To: {message.recipients}</p>
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
                        Trigger every {message.daysToTrigger} day(s)
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
      <AccountModal
        show={showAccountModal}
        onClose={() => {
          setShowAccountModal(false);
        }}
        onSubmitAccount={() => {}}
        user={user}
      />
    </Fragment>
  );
}

export default DashboardPage;
