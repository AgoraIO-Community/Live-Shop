//
//  Agora's RTM SDK
//
//
//  Copyright (c) 2019 Agora.io. All rights reserved.
//
#pragma once

#if defined(_WIN32)
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#define AGORA_CALL __cdecl
#if defined(AGORARTC_EXPORT)
#define AGORA_API extern "C" __declspec(dllexport)
#else
#define AGORA_API extern "C" __declspec(dllimport)
#endif
#define _AGORA_CPP_API

#elif defined(__APPLE__)
#define AGORA_API __attribute__((visibility("default"))) extern "C"
#define AGORA_CALL
#define _AGORA_CPP_API

#elif defined(__ANDROID__) || defined(__linux__)
#if defined(__ANDROID__) && defined(FEATURE_RTM_STANDALONE_SDK)
#define AGORA_API extern "C"
#define _AGORA_CPP_API
#else
#define AGORA_API extern "C" __attribute__((visibility("default")))
#define _AGORA_CPP_API __attribute__((visibility("default")))
#endif
#define AGORA_CALL

#else
#define AGORA_API extern "C"
#define AGORA_CALL
#define _AGORA_CPP_API
#endif

namespace agora {
  namespace rtm {

    /** Login states.
    */
    enum LOGIN_ERR_CODE {
        /** 0: Login succeeds. No error occurs. */
      LOGIN_ERR_OK = 0,
        /** 1: Login fails. The reason is unknown.*/
      LOGIN_ERR_UNKNOWN = 1,
        /** 2: Login is rejected. the SDK is not initialized or is rejected by the server. */
      LOGIN_ERR_REJECTED = 2, // Occurs when not initialized or server reject
        /** 3: Invalid login argument.*/
      LOGIN_ERR_INVALID_ARGUMENT = 3,
        /** 4: Invalid App ID. */
      LOGIN_ERR_INVALID_APP_ID = 4,
        /** 5: Invalid token. */
      LOGIN_ERR_INVALID_TOKEN = 5,
        /** 6: The token has expired and login is rejected.*/
      LOGIN_ERR_TOKEN_EXPIRED = 6,
        /** 7: Unauthorized login. */
      LOGIN_ERR_NOT_AUTHORIZED = 7,
        /** 8: The user is already logged in or is logging in. */
      LOGIN_ERR_ALREADY_LOGIN = 8,
        /** 9: A login timeout. */
      LOGIN_ERR_TIMEOUT = 9,
        /** 10: Log in or log out too often. */
      LOGIN_ERR_TOO_OFTEN = 10,
    };
    /** Logout states.
    */
    enum LOGOUT_ERR_CODE {
        /** 0: Logout succeeds. No error occurs. */
      LOGOUT_ERR_OK = 0,
        /** 1: Logout fails. Maybe the SDK is not initialized or the user is not logged in. */
      LOGOUT_ERR_REJECTED = 1,
    };

    /** Error codes updating the token. 
    */
    enum RENEW_TOKEN_ERR_CODE {
      /** 0: The method call succeeds.
      */
      RENEW_TOKEN_ERR_OK = 0,
      /** 1: The method call fails.
      */
      RENEW_TOKEN_ERR_FAILURE = 1,
      /** 2: The method call fails. Invalid argument.
      */
      RENEW_TOKEN_ERR_INVALID_ARGUMENT = 2,
      /** 3: The Agora RTM service is not initialized. */
      RENEW_TOKEN_ERR_REJECTED = 3,
      /** 4: The method call frequency has exceeded the limit of two times per second. */
      RENEW_TOKEN_ERR_TOO_OFTEN = 4,
      /** 5: This token has expired. */
      RENEW_TOKEN_ERR_TOKEN_EXPIRED = 5,
      /** 6: This token is invalid. */
      RENEW_TOKEN_ERR_INVALID_TOKEN = 6,
    };
    /** Connection states.
    */
    enum CONNECTION_STATE {
        /** 1: The SDK is logged out of Agora's RTM service and is trying to log in.

      If the connection to Agora's RTM service is lost because, for example, if the network is down or switched, the SDK automatically tries to log in, triggers the \ref agora::rtm::IRtmServiceEventHandler::onConnectionStateChanged "onConnectionStateChanged" callback, and switches to the \ref agora::rtm::CONNECTION_STATE_RECONNECTING "CONNECTION_STATE_RECONNECTING" state.
*/
      CONNECTION_STATE_DISCONNECTED = 1,
        /** 2: The SDK is logging in Agora's RTM service.

       When the application calls the \ref agora::rtm::IRtmService::login "login" method, the SDK starts to log in Agora's RTM service, triggers the \ref agora::rtm::IRtmServiceEventHandler::onConnectionStateChanged "onConnectionStateChanged" callback, and switches to the \ref agora::rtm::CONNECTION_STATE_CONNECTING "CONNECTION_STATE_CONNECTING" state.
       When the SDK successfully logs in Agora's RTM service, it triggers the \ref agora::rtm::IRtmServiceEventHandler::onConnectionStateChanged "onConnectionStateChanged" callback and switches to the \ref agora::rtm::CONNECTION_STATE_CONNECTED "CONNECTION_STATE_CONNECTED" state.

       After the SDK logs in Agora's RTM service and when it finishes initializing the media engine, the SDK triggers the \ref agora::rtm::IRtmServiceEventHandler::onLoginSuccess "onLoginSuccess" callback.
       */
      CONNECTION_STATE_CONNECTING = 2,
        /** 3: The SDK is connected to Agora's RTM system.

       - When the app calls the \ref agora::rtm::IRtmService::login "login" method, the SDK starts to log in Agora's RTM service, triggers the \ref agora::rtm::IRtmServiceEventHandler::onConnectionStateChanged "onConnectionStateChanged" callback, and switches to the \ref agora::rtm::CONNECTION_STATE_CONNECTING "CONNECTION_STATE_CONNECTING" state.
       - When the SDK successfully logs in Agora's RTM service, it triggers the \ref agora::rtm::IRtmServiceEventHandler::onConnectionStateChanged "onConnectionStateChanged" callback and switches to the \ref agora::rtm::CONNECTION_STATE_CONNECTED "CONNECTION_STATE_CONNECTED" state.
       - After the SDK logs in Agora's RTM service and when it finishes initializing the media engine, the SDK triggers the \ref agora::rtm::IChannelEventHandler::onMemberJoined "onMemberJoined" callback.
       */
      CONNECTION_STATE_CONNECTED = 3,
        /** 4: The SDK keeps logging in Agora's RTM service after being logged out because of network issues.

       - If the SDK cannot log in Agora's RTM service within 10 seconds after being logged out, the SDK stays in the \ref agora::rtm::CONNECTION_STATE_RECONNECTING "CONNECTION_STATE_RECONNECTING" state and keeps logging in.
       - If the SDK fails to log in Agora's RTM service 20 minutes after being logged out, the SDK triggers the \ref agora::rtm::IRtmServiceEventHandler::onConnectionStateChanged "onConnectionStateChanged" callback, switches to the \ref agora::rtm::CONNECTION_STATE_ABORTED "CONNECTION_STATE_ABORTED" state, and stops logging in.
       */
      CONNECTION_STATE_RECONNECTING = 4,
        /** 5: The SDK has given up logging in Agora's RTM service, mainly because the SDK is banned by the service or because the SDK has logged in with the same user ID from a different instance or device. To log in again, call the \ref agora::rtm::IRtmService::logout "logout" method before calling the \ref agora::rtm::IRtmService::login "login" method again.
      */
      CONNECTION_STATE_ABORTED = 5,
    };

    /** Reasons for a connection state change.
    */
    enum CONNECTION_CHANGE_REASON {
        /** 1: The SDK is logging in Agora's RTM service.
      */
      CONNECTION_CHANGE_REASON_LOGIN = 1,
        /** 2: The SDK has logged in Agora's RTM service.
      */
      CONNECTION_CHANGE_REASON_LOGIN_SUCCESS = 2,
        /** 3: The SDK fails to log in Agora's RTM service.
      */
      CONNECTION_CHANGE_REASON_LOGIN_FAILURE = 3,
        /** 4: The SDK fails to log in Agora's RTM service for more than six seconds and stops logging in.
      */
      CONNECTION_CHANGE_REASON_LOGIN_TIMEOUT = 4,
        /** 5: The SDK login to Agora's RTM service is interrupted.
      */
      CONNECTION_CHANGE_REASON_INTERRUPTED = 5,
        /** 6: The SDK has logged out of Agora's RTM service.
      */
      CONNECTION_CHANGE_REASON_LOGOUT = 6,
        /** 7: The SDK login to Agora's RTM service is banned by Agora.
      */
      CONNECTION_CHANGE_REASON_BANNED_BY_SERVER = 7,
        /** 8: Another user is logging in the Agora RTM system with the same User ID. */
      CONNECTION_CHANGE_REASON_REMOTE_LOGIN = 8,
    };

    /** States of sending a peer-to-peer message.
    */
    enum PEER_MESSAGE_ERR_CODE {
        /** 0: The receiver receives the peer-to-peer message.
       */
      PEER_MESSAGE_ERR_OK = 0,
        /** 1: The sender fails to send the peer-to-peer message.
      */
      PEER_MESSAGE_ERR_FAILURE = 1,
        /** 2: A timeout in sending the peer-to-peer message. The current timeout is set as 5 seconds.
      */
      PEER_MESSAGE_ERR_SENT_TIMEOUT = 2,
        /** 3: The receiver is offline and does not receive the peer-to-peer message.
       */
      PEER_MESSAGE_ERR_PEER_UNREACHABLE = 3,
        /** 4: The receiver is offline and does not receive the peer-to-peer message.
         * but server has cached the message, when receiver is online, server will deliver the message to it
         */
      PEER_MESSAGE_ERR_CACHED_BY_SERVER = 4,
    };

    /** States of a user joining a channel.
    */
    enum JOIN_CHANNEL_ERR {
        /** 0: The user joins the channel successfully.
      */
      JOIN_CHANNEL_ERR_OK = 0,
        /** 1: The user fails to join the channel.
      */
      JOIN_CHANNEL_ERR_FAILURE = 1,
        /** 2: The user cannot join the channel. The user may already be in the channel.
      */
      JOIN_CHANNEL_ERR_REJECTED = 2, // Usually occurs when the user is already in the channel
        /** 3: The user fails to join the channel. Maybe the argument is invalid.
      */
      JOIN_CHANNEL_ERR_INVALID_ARGUMENT = 3,
        /** 4: A timeout in joining the channel.
      */
      JOIN_CHANNEL_TIMEOUT = 4,
    };

    /** States of a user leaving a channel.
    */
    enum LEAVE_CHANNEL_ERR {
        /** 0: The user leaves the channel successfully.
      */
      LEAVE_CHANNEL_ERR_OK = 0,
        /** 1: The user fails to leave the channel.
      */
      LEAVE_CHANNEL_ERR_FAILURE = 1,
        /** 2: The user cannot leave the channel. The user may not be in the channel.
      */
      LEAVE_CHANNEL_ERR_REJECTED = 2, // Usually occurs when the user is not in the channel
    };

    /** Reasons for a user leaving a channel.
    */
    enum LEAVE_CHANNEL_REASON {
        /** 1: The user has quit the call.
      */
      LEAVE_CHANNEL_REASON_QUIT = 1,
        /** 2: The user is banned by the server.
      */
      LEAVE_CHANNEL_REASON_KICKED = 2,
    };

    /** States of sending a channel message.
    */
    enum CHANNEL_MESSAGE_ERR_CODE {
        /** 0: The sender sends the channel message successfully.
      */
      CHANNEL_MESSAGE_ERR_OK = 0,
        /** 1: The sender fails to send the channel message.
      */
      CHANNEL_MESSAGE_ERR_FAILURE = 1,
        /** 2: A timeout in sending the channel message.
      */
      CHANNEL_MESSAGE_ERR_SENT_TIMEOUT = 2,
    };

    /** States of retrieving the user list of a channel.
    */
    enum GET_MEMBERS_ERR {
        /** 0: Retrieves the user list of a channel.
      */
      GET_MEMBERS_ERR_OK = 0,
        /** 1: Fails to retrieve the user list of a channel.
      */
      GET_MEMBERS_ERR_FAILURE = 1,
        /** 2: Cannot retrieve the user list of a channel.
      */
      GET_MEMBERS_ERR_REJECTED = 2,
        /** 3: A timeout in retreiving the user list of a channel.
      */
      GET_MEMBERS_ERR_TIMEOUT = 3,
    };

/** Error codes querying a peer user's online status. 
 */
    enum QUERY_PEERS_ONLINE_STATUS_ERR {
     /** 0: The method call succeeds.
      */
      QUERY_PEERS_ONLINE_STATUS_ERR_OK = 0,
      /** 1: The method call fails.
      */
      QUERY_PEERS_ONLINE_STATUS_ERR_FAILURE = 1,
      /** 2: The method call fails. Invalid argument.
      */
      QUERY_PEERS_ONLINE_STATUS_ERR_INVALID_ARGUMENT = 2,
      /** 3: The method call fails. The peer user is not logged in the Agora RTM system.
      */
      QUERY_PEERS_ONLINE_STATUS_ERR_REJECTED = 3,
      /** 4: A timeout occurs when querying a peer user's online status. The current timeout is set as 10 seconds. */
      QUERY_PEERS_ONLINE_STATUS_ERR_TIMEOUT = 4,
      /** 5: The method call frequency has exceeded the limit of 10 times every five seconds. */
      QUERY_PEERS_ONLINE_STATUS_ERR_TOO_OFTEN = 5,
    };

    enum ATTRIBUTE_OPERATION_ERR {
        ATTRIBUTE_OPERATION_ERR_OK = 0,
        ATTRIBUTE_OPERATION_ERR_NOT_READY = 1,
        ATTRIBUTE_OPERATION_ERR_FAILURE = 2,
        ATTRIBUTE_OPERATION_ERR_INVALID_ARGUMENT = 3,
        ATTRIBUTE_OPERATION_ERR_SIZE_OVERFLOW = 4,
        ATTRIBUTE_OPERATION_ERR_TOO_OFTEN = 5,
        ATTRIBUTE_OPERATION_ERR_USER_NOT_FOUND = 6,
        ATTRIBUTE_OPERATION_ERR_TIMEOUT = 7,
    };

    /** Message types.
    */
    enum MESSAGE_TYPE {
        /** 0: The message type is undefined.
      */
      MESSAGE_TYPE_UNDEFINED = 0,
        /** 1: A text message.
      */
      MESSAGE_TYPE_TEXT = 1,
    };
     
     /** Send message option.
     */
     struct SendMessageOptions{
         /** Enable Offline Messaging.
          
          You can now send RTM messages to peers even if they are not login in.
          This feature lets your peers know that you have been trying to reach them. You don't have to wait
          until someone is online before sending them a message.
   
          For now, we only cache latest 200 messages for at most 7 days, if message count limitation is
          reached, you can still send offline message, but the oldest message will be abandoned to keep
          the amount of offline message under 200.
         */
         bool enableOfflineMessaging;
         SendMessageOptions():enableOfflineMessaging(false){}
     };

    struct RtmAttribute
    {
        const char* key;
        const char* value;
    };

    /** Channel and peer-to-peer message methods.
    */
    class IMessage
    {
    protected:
      virtual ~IMessage() {}
    public:
      /** Retrieves the channel or peer-to-peer message ID.
      *
      * @return The message ID, which is created within the message.
      */
      virtual long long getMessageId() const = 0;
      /** Retrieves the channel or peer-to-peer message type.

      @return The message type.
      */
      virtual MESSAGE_TYPE getMessageType() const = 0;
      /** Sets the channel or peer-to-peer text message.
      *
      * @param str The text message to be set.
      */
      virtual void setText(const char *str) = 0;
      /** Retrieves the channel or peer-to-peer text message.
      *
      * @return The received text message.
      */
      virtual const char *getText() const = 0;
      /** Retrieves the time stamp when server receive this message.
      *
      * @return the time stamp of this message.
      */
      virtual long long getServerReceivedTs() const = 0;
      /** Retrieves If the message is an offline message.
      *
      * @return If the message is an offline message.
      */
      virtual bool isOfflineMessage() const = 0;
      /** Releases all resources used by the IMessage instance.
      */
      virtual void release() = 0;
    };

    /** Agora RTM channel member methods.
    */
    class IChannelMember
    {
    protected:
      virtual ~IChannelMember() {}
    public:
      /** Gets the user ID of a user in the channel.
      *
      * @return User ID of a user in the channel.
      */
      virtual const char * getUserId() const = 0;
      /** Gets the channel ID of the user.
      *
      * @return Channel ID of the user.
      */
      virtual const char * getChannelId() const = 0;
      /** Releases all resources used by the IChannelMember instance.
      */
      virtual void release() = 0;
    };

    struct PeerOnlineStatus
    {
      const char* peerId;
      bool isOnline;
    };

    /** Callbacks for the Agora RTM channel methods.
    */
    class IChannelEventHandler
    {
    public:

      virtual ~IChannelEventHandler()
      {
      }
      /** Occurs when a user joins a channel.

      The local user receives this callback while all remote users receive the \ref agora::rtm::IChannelEventHandler::onMemberJoined "onMemberJoined" callback when the \ref agora::rtm::IChannel::join "join" method call succeeds.
      */
      virtual void onJoinSuccess()
      {
      }
      /** Occurs when a user fails to join a channel.

      The local user receives this callback when the \ref agora::rtm::IChannel::join "join" method call fails. See \ref agora::rtm::JOIN_CHANNEL_ERR "JOIN_CHANNEL_ERR" for the error codes.
      */
      virtual void onJoinFailure(JOIN_CHANNEL_ERR errorCode)
      {
          (JOIN_CHANNEL_ERR) errorCode;
      }
      /** Occurs when a user leaves a channel.

      The SDK triggers this callback when the \ref agora::rtm::IChannel::leave "leave" method call succeeds. See \ref agora::rtm::LEAVE_CHANNEL_ERR "LEAVE_CHANNEL_ERR" for the states.
      */
      virtual void onLeave(LEAVE_CHANNEL_ERR errorCode)
      {
          (LEAVE_CHANNEL_ERR) errorCode;
      }

      /** Occurs when all remote users receive a channel message from a sender.

      All remote users receive this callback while the sender receives the \ref agora::rtm::IChannelEventHandler::onSendMessageResult "onSendMessageResult" callback when the \ref agora::rtm::IChannel::sendMessage "sendMessage" method call succeeds.

      @param userId User ID of the sender.
      @param message The received channel message. See IMessage.

      */
      virtual void onMessageReceived(const char *userId, const IMessage *message)
      {
          (const char *) userId;
          (IMessage *) message;
      }

      /** Occurs when the sent channel message state changes.

      The sender receives this callback while all remote users receive the \ref agora::rtm::IChannelEventHandler::onMessageReceived "onMessageReceived" callback when the \ref agora::rtm::IChannel::sendMessage "sendMessage" method call succeeds.

      @param messageId The ID of the sent channel message.
      @param state The new channel message state. See \ref agora::rtm::CHANNEL_MESSAGE_ERR_CODE "CHANNEL_MESSAGE_ERR_CODE".

      */
      virtual void onSendMessageResult(long long messageId, CHANNEL_MESSAGE_ERR_CODE state)
      {
          (long long) messageId;
          (CHANNEL_MESSAGE_ERR_CODE) state;
      }
      /** Occurs when a user joins a channel.

      All remote users receive this callback while the local user receives the \ref agora::rtm::IChannelEventHandler::onJoinSuccess "onJoinSuccess" callback when the \ref agora::rtm::IChannel::join "join" method call succeeds.

      @param member The user joining the channel. See IChannelMember.
      */
      virtual void onMemberJoined(IChannelMember *member)
      {
          (IChannelMember *) member;
      }
      /** Occurs when a user leaves a channel.

      All remote users receive this callback while the local user receives the the \ref agora::rtm::IChannelEventHandler::onLeave "onLeave" callback when the \ref agora::rtm::IChannel::leave "leave" method call succeeds.

      @param member The user leaving the channel. See IChannelMember.
      */
      virtual void onMemberLeft(IChannelMember *member)
      {
          (IChannelMember *) member;
      }
      /** Occurs when the user list of a channel is retrieved.

      The SDK triggers this callback when the \ref agora::rtm::IChannel::getMembers "getMembers" method call succeeds.

      @param members The user list. See IChannelMember.
      @param userCount The number of users.
      @param errorCode Error code. See \ref agora::rtm::GET_MEMBERS_ERR "GET_MEMBERS_ERR".

      */
      virtual void onGetMembers(IChannelMember **members, int userCount, GET_MEMBERS_ERR errorCode)
      {
          (IChannelMember **) members;
          (int) userCount;
      }
    };

    /** Agora RTM channel methods.
    */
    class IChannel
    {
    protected:
      virtual ~IChannel() {}
    public:
//            virtual void setEventHandler(IChannelEventHandler *eventHandler) = 0;
      /** Allows a user to join a channel.

      - If this method call succeeds:
          - The local user receives the \ref agora::rtm::IChannelEventHandler::onJoinSuccess "onJoinSuccess" callback.
          - All remote users receive the \ref agora::rtm::IChannelEventHandler::onMemberJoined "onMemberJoined" callback.
      - If this method call fails, the local user receives the \ref agora::rtm::IChannelEventHandler::onJoinFailure "onJoinFailure" callback. See \ref agora::rtm::JOIN_CHANNEL_ERR "JOIN_CHANNEL_ERR"
      for the error codes.
      */
      virtual int join() = 0;
      /** Allows a user to leave a channel.

      - If this method call succeeds:
          - The local user receives the \ref agora::rtm::IChannelEventHandler::onLeave "onLeave" callback with the LEAVE_CHANNEL_ERR_OK state.
          - All remote users receive the \ref agora::rtm::IChannelEventHandler::onMemberLeft "onMemberLeft" callback.
      - If this method call fails, the local user receives the \ref agora::rtm::IChannelEventHandler::onLeave "onLeave" callback with an error code. See \ref agora::rtm::LEAVE_CHANNEL_ERR "LEAVE_CHANNEL_ERR" for the error codes.

      */
      virtual int leave() = 0;
      /** Allows a local user to send a message in a channel.

      If this method call succeeds:
      - The local user receives the \ref agora::rtm::IChannelEventHandler::onSendMessageResult "onSendMessageResult" callback.
      - All remote users receive the \ref agora::rtm::IChannelEventHandler::onMessageReceived	"onMessageReceived" callback.

      @param message The message to be sent. See IMessage.
      */
      virtual int sendMessage(const IMessage *message) = 0;
      /** Gets the channel ID.
      */
      virtual const char *getId() const = 0;
      /** Retrieves the user list of a channel.

      If this method call succeeds, the SDK triggers the \ref agora::rtm::IChannelEventHandler::onGetMembers "onGetMembers" callback.
      */
      virtual int getMembers() = 0;

      // sync_call
      /** Releases all resources used by the IChannel instance.
      */
      virtual void release() = 0;
    };

    class IRtmServiceEventHandler
    {
    public:
      virtual ~IRtmServiceEventHandler()
      {
      }

      /** Occurs when a user logs in Agora's RTM service.

      The local user receives this callback when the \ref agora::rtm::IRtmService::login "login" method call succeeds.
      */
      virtual void onLoginSuccess() {}
      /** Occurs when a user fails to log in Agora's RTM service.

      The local user receives this callback when the \ref agora::rtm::IRtmService::login "login" method call fails. See \ref agora::rtm::LOGIN_ERR_CODE "LOGIN_ERR_CODE" for the error codes.
      */
      virtual void onLoginFailure(LOGIN_ERR_CODE errorCode)
      {
          (LOGIN_ERR_CODE) errorCode;
      }
      /** Returns the result of calling the renewToken method.
       
       @param token Your current token.
       @param errorCode The error code. See RENEW_TOKEN_ERR_CODE.
       */
      virtual void onRenewTokenResult(const char* token, RENEW_TOKEN_ERR_CODE errorCode)
      {
          (const char*) token;
          (RENEW_TOKEN_ERR_CODE) errorCode;
      }
      /** Occurs when the token has expired. */
      virtual void onTokenExpired()
      {
      }

      /** Occurs when a user logs out of Agora's RTM service.

      The local user receives this callback when the SDK calls the \ref agora::rtm::IRtmService::logout "logout" method. See \ref agora::rtm::LOGOUT_ERR_CODE "LOGOUT_ERR_CODE" for the states.

      */
      virtual void onLogout(LOGOUT_ERR_CODE errorCode)
      {
          (LOGOUT_ERR_CODE) errorCode;
      }
      /** Occurs when a connection state changes between the SDK and Agora's RTM service.

       @param state The new connection state. See \ref agora::rtm::CONNECTION_STATE "CONNECTION_STATE".
       @param reason The reason for the connection state change. See \ref agora::rtm::CONNECTION_CHANGE_REASON "CONNECTION_CHANGE_REASON".
       */
      virtual void onConnectionStateChanged(CONNECTION_STATE state, CONNECTION_CHANGE_REASON reason)
      {
      }
      /** Occurs when the sent peer-to-peer message state changes.

      The sender receives this callback while the receiver receives the \ref agora::rtm::IRtmServiceEventHandler::onMessageReceivedFromPeer "onMessageReceivedFromPeer" callback when the \ref agora::rtm::IRtmService::sendMessageToPeer "sendMessageToPeer" method call succeeds.

      @param messageId The ID of the sent message.
      @param errorCode The new peer-to-peer message state. See \ref agora::rtm::PEER_MESSAGE_ERR_CODE "PEER_MESSAGE_ERR_CODE".

      */
      virtual void onSendMessageResult(long long messageId, PEER_MESSAGE_ERR_CODE errorCode)
      {
          (long long) messageId;
          (PEER_MESSAGE_ERR_CODE) errorCode;
      }
      /** Occurs when the receiver receives the peer-to-peer message from the sender.

      The receiver receives this callback while the sender receives the \ref agora::rtm::IRtmServiceEventHandler::onSendMessageResult "onSendMessageResult" callback when the \ref agora::rtm::IRtmService::sendMessageToPeer "sendMessageToPeer" method call succeeds.

      @param peerId User ID of the sender.
      @param message The message sent by the sender. See IMessage.

      */
      virtual void onMessageReceivedFromPeer(const char *peerId, const IMessage *message)
      {
          (char *) peerId;
          (IMessage *) message;
      }

      virtual void onQueryPeersOnlineStatusResult(long long requestId, const PeerOnlineStatus* peersStatus , int peerCount, QUERY_PEERS_ONLINE_STATUS_ERR errorCode)
      {
          (long long) requestId; 
          (const PeerOnlineStatus*) peersStatus;
          (int) peerCount;
          (QUERY_PEERS_ONLINE_STATUS_ERR) errorCode;
      }

      virtual void onSetLocalUserAttributesResult(long long requestId, ATTRIBUTE_OPERATION_ERR errorCode)
      {
          (long long) requestId;
          (ATTRIBUTE_OPERATION_ERR) errorCode;
      }

      virtual void onAddOrUpdateLocalUserAttributesResult(long long requestId, ATTRIBUTE_OPERATION_ERR errorCode)
      {
         (long long) requestId;
         (ATTRIBUTE_OPERATION_ERR) errorCode;
      }

      virtual void onDeleteLocalUserAttributesResult(long long requestId, ATTRIBUTE_OPERATION_ERR errorCode)
      {
         (long long) requestId;
         (ATTRIBUTE_OPERATION_ERR) errorCode;
      }

      virtual void onClearLocalUserAttributesResult(long long requestId, ATTRIBUTE_OPERATION_ERR errorCode) 
      {
          (long long) requestId;
          (ATTRIBUTE_OPERATION_ERR) errorCode;
      }

      virtual void onGetUserAttributesResult(long long requestId, const char* userId, const RtmAttribute* attributes, int numberOfAttributes, ATTRIBUTE_OPERATION_ERR errorCode)
      {
          (long long) requestId;
          (const RtmAttribute*) attributes;
          (int) numberOfAttributes;
          (ATTRIBUTE_OPERATION_ERR) errorCode;
      }
    };


    class IRtmCallManager;
    class IRtmCallEventHandler;

    class IRtmService
    {
    protected:
      virtual ~IRtmService() {}
    public:
      /** Creates an IRtmService instance.

      The Agora RTM SDK supports multiple RtmClient instances.

      All methods in the RtmClient class are executed asynchronously.

      @param appId The App ID issued by Agora to you. Apply for a new App ID from Agora if it is missing from your kit.
      @param eventHandler An IRtmServiceEventHandler object that invokes callbacks to be passed to the application on Agora RTM SDK runtime events.
      */
      virtual int initialize(const char *appId, IRtmServiceEventHandler *eventHandler) = 0;

      virtual void addEventHandler(IRtmServiceEventHandler *eventHandler) = 0;
      virtual void removeEventHandler(IRtmServiceEventHandler *eventHandler) = 0;

      /** Releases all resources used by the IRtmService instance.
      */
      virtual void release(bool sync = false) = 0;

      /** Allows a user to log in Agora's RTM system.

      - If this method call succeeds, the local user receives the \ref agora::rtm::IRtmServiceEventHandler::onLoginSuccess "onLoginSuccess" callback.
      - If this method call fails, the local user receives the \ref agora::rtm::IRtmServiceEventHandler::onLoginFailure "onLoginFailure" callback. See \ref agora::rtm::LOGIN_ERR_CODE "LOGIN_ERR_CODE" for the error codes.

      @param token Token used to log in Agora's RTM system and used when dynamic authentication is enabled. Set @p token as @p nil in the integration and test stages.
      @param userId User ID of the user logging in Agora's RTM system. The string must not exceed 64 bytes in length or start with a space.
      Supported characters:

      - The 26 lowercase English letters: a to z
      - The 26 uppercase English letters: A to Z
      - The 10 numbers: 0 to 9
      - Space
      - "!", "#", "$", "%", "&", "(", ")", "+", "-", ":", ";", "<", "=", ".", ">", "?", "@", "[", "]", "^", "_", " {", "}", "|", "~", ","

      @note Do not set @p userId as @p null.
      */
      virtual int login(const char *token, const char *userId) = 0;

      /** Allows a user to log out of Agora's RTM system.

      The local user receives the \ref agora::rtm::IRtmServiceEventHandler::onLogout "onLogout" callback. See \ref agora::rtm::LOGOUT_ERR_CODE "LOGIN_ERR_CODE" for the states.
      */
      virtual int logout() = 0;
      /** Renews the token
       
       @param token Your new token.
       */
      virtual int renewToken(const char* token) = 0;

      /** Allows a local user (sender) to send a peer-to-peer message to a specific user (receiver).

      If this method call succeeds:
      - The sender receives the \ref agora::rtm::IRtmServiceEventHandler::onSendMessageResult "onSendMessageResult" callback.
      - The receiver receives the \ref agora::rtm::IRtmServiceEventHandler::onMessageReceivedFromPeer "onMessageReceivedFromPeer" callback.

       @param peerId User ID of the receiver.
       @param message The message to be sent. For information about creating a message, see IMessage.
       */
      virtual int sendMessageToPeer(const char *peerId, const IMessage *message) = 0;

      /** Allows a local user (sender) to send a peer-to-peer message to a specific user (receiver).
        
      If this method call succeeds:
      - The sender receives the \ref agora::rtm::IRtmServiceEventHandler::onSendMessageResult "onSendMessageResult" callback.
      - The receiver receives the \ref agora::rtm::IRtmServiceEventHandler::onMessageReceivedFromPeer "onMessageReceivedFromPeer" callback.

       @param peerId User ID of the receiver.
       @param message The message to be sent. For information about creating a message, see IMessage.
       @param options options when send message to peer. See \ref agora::rtm::SendMessageOptions "SendMessageOptions".
       */
      virtual int sendMessageToPeer(const char *peerId, const IMessage *message, const SendMessageOptions& options) = 0;

      /** Creates an Agora RTM channel.

      - If the method call succeeds, the SDK returns an IChannel instance.
      - If this method call fails, the SDK returns @p null. The @p channelId may be invalid, or an existing channel with the same @p channelId exists, or the number of channels is over the limit.

      @note You can create multiple channels in an IRtmService instance.

      @param channelId The unique channel name for the Agora RTM session in the string format smaller than 64 bytes and cannot be empty or set as @p null.
      Supported characters:
      - The 26 lowercase English letters: a to z
      - The 26 uppercase English letters: A to Z
      - The 10 numbers: 0 to 9
      - Space
      - "!", "#", "$", "%", "&", "(", ")", "+", "-", ":", ";", "<", "=", ".", ">", "?", "@", "[", "]", "^", "_", " {", "}", "|", "~", ","

      @param eventHandler See IChannelEventHandler.
      */
      virtual IChannel * createChannel(const char *channelId, IChannelEventHandler *eventHandler) = 0;

      virtual IRtmCallManager* getRtmCallManager(IRtmCallEventHandler*eventHandler) = 0;
      /** Creates an IMessage instance.
      *
      * @note The IMessage instance can be either for a channel or peer-to-peer message.
      *
      * @return An IMessage instance.
      */
      virtual IMessage *createMessage() = 0;
      // todo: move to private
      virtual int setParameters(const char* parameters) = 0;
//            virtual int getParameters(const char* key, any_document_t& result) = 0;
        
        /** Queries the online status of the specified users
         
         @param peerIds[] A list of the specified user IDs.
         @param peerCount Length of the list.
         @param requestId The requester's user ID.
         */
      virtual int queryPeersOnlineStatus(const char* peerIds[], int peerCount, long long& requestId) = 0;

      virtual int setLocalUserAttributes(const RtmAttribute* attributes, int numberOfAttributes, long long &requestId) = 0;

      virtual int addOrUpdateLocalUserAttributes(const RtmAttribute* attributes, int numberOfAttributes, long long &requestId) = 0;

      virtual int deleteLocalUserAttributesByKeys(const char* attributeKeys[], int numberOfKeys, long long &requestId) = 0;

      virtual int clearLocalUserAttributes(long long &requestId) = 0;

      virtual int getUserAttributes(const char* userId, long long &requestId) = 0;

      virtual int getUserAttributesByKeys(const char* userId, const char* attributeKeys[], int numberOfKeys, long long &requestId) = 0;

    };
  }
}
