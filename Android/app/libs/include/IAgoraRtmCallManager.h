//
//  Agora's RTM SDK
//
//
//  Copyright (c) 2019 Agora.io. All rights reserved.
//
#pragma once

#include "IAgoraRtmService.h"

namespace agora {
  namespace rtm {
    /** @brief <b>RETURNED TO THE CALLER.</b> States of an outgoing call invitation.
    */
    enum LOCAL_INVITATION_STATE {
      /** 0: <b>RETURNED TO THE CALLER.</b> The initial state of a call invitation (idle).
      */
      LOCAL_INVITATION_STATE_IDLE = 0,
      /** 1: <b>RETURNED TO THE CALLER.</b> The call invitation is sent to the callee.
      */
      LOCAL_INVITATION_STATE_SENT_TO_REMOTE = 1,
      /** 2: <b>RETURNED TO THE CALLER.</b> The call invitation is received by the callee.
      */
      LOCAL_INVITATION_STATE_RECEIVED_BY_REMOTE = 2,
      /** 3: <b>RETURNED TO THE CALLER.</b> The call invitation is accepted by the callee.
      */
      LOCAL_INVITATION_STATE_ACCEPTED_BY_REMOTE = 3,
      /** 4: <b>RETURNED TO THE CALLER.</b> The call invitation is declined by the callee.
      */
      LOCAL_INVITATION_STATE_REFUSED_BY_REMOTE = 4,
      /** 5: <b>RETURNED TO THE CALLER.</b> You have canceled the call invitation.
      */
      LOCAL_INVITATION_STATE_CANCELED = 5,
      /** 6: <b>RETURNED TO THE CALLER.</b> The call invitation fails.
      */
      LOCAL_INVITATION_STATE_FAILURE = 6,
    };

    /** @brief <b>RETURNED TO THE CALLEE.</b> States of an incoming call invitation.
    */
    enum REMOTE_INVITATION_STATE {
      /** 0: <b>RETURNED TO THE CALLEE.</b> The initial state of a call invitation (idle).
      */
      REMOTE_INVITATION_STATE_IDLE = 0,
      /** 1: <b>RETURNED TO THE CALLEE.</b> A call invitation from a remote caller is received.
      */
      REMOTE_INVITATION_STATE_INVITATION_RECEIVED = 1,
      /** 2: <b>RETURNED TO THE CALLEE.</b> The message is sent to the caller that the call invitation is accepted.
      */
      REMOTE_INVITATION_STATE_ACCEPT_SENT_TO_LOCAL = 2,
      /** 3: <b>RETURNED TO THE CALLEE.</b> You have declined the call invitation.
      */
      REMOTE_INVITATION_STATE_REFUSED = 3,
      /** 4: <b>RETURNED TO THE CALLEE.</b> You have accepted the call invitation.
      */
      REMOTE_INVITATION_STATE_ACCEPTED = 4,
      /** 5: <b>RETURNED TO THE CALLEE.</b> The call invitation is canceled by the remote caller.
      */
      REMOTE_INVITATION_STATE_CANCELED = 5,
      /** 6: <b>RETURNED TO THE CALLEE.</b> The call invitation fails.
      */
      REMOTE_INVITATION_STATE_FAILURE = 6,
    };

    /** @brief <b>RETURNED TO THE CALLER.</b> Error codes of an outgoing call invitation.
    */
    enum LOCAL_INVITATION_ERR_CODE {
      /** 0: <b>RETURNED TO THE CALLER.</b> The outgoing invitation succeeds.
      */
      LOCAL_INVITATION_ERR_OK = 0,
      /** 1: <b>RETURNED TO THE CALLER.</b> The callee is offline.
        
      The SDK performs the following:
      - Keeps resending the call invitation to the callee, if the callee is offline.
      - Returns this error code, if the callee is still offline 30 seconds since the call invitation is sent.
         */
      LOCAL_INVITATION_ERR_PEER_OFFLINE = 1,
      /** 2: <b>RETURNED TO THE CALLER.</b> The callee is online but has not ACKed to the call invitation 30 seconds since it is sent.
      */
      LOCAL_INVITATION_ERR_PEER_NO_RESPONSE = 2,
      /** 3: <b>RETURNED TO THE CALLER. SAVED FOR FUTURE USE.</b> The call invitation expires 60 seconds since it is sent, if the callee ACKs to the call invitation but neither the caller or callee takes any further action (cancel, accpet, or decline it).
      */
      LOCAL_INVITATION_ERR_INVITATION_EXPIRE = 3,
        /** 4: <b>RETURNED TO THE CALLER.</b> The caller is not logged in. */
      LOCAL_INVITATION_ERR_NOT_LOGGEDIN = 4,
    };
    /** @brief <b>RETURNED TO THE CALLEE.</b> Error codes of an incoming call invitation.
    */
    enum REMOTE_INVITATION_ERR_CODE {
      /** 0: <b>RETURNED TO THE CALLEE.</b> The incoming calll invitation succeeds.
      */
      REMOTE_INVITATION_ERR_OK = 0,
      /** 1: <b>RETURNED TO THE CALLEE.</b> The call invitation received by the callee fails: the callee is not online.
      */
      REMOTE_INVITATION_ERR_PEER_OFFLINE = 1,
      /** 2: <b>RETURNED TO THE CALLEE.</b> The call invitation received by callee fails: the acceptance of the call invitation fails.
      */
      REMOTE_INVITATION_ERR_ACCEPT_FAILURE = 2,
      /** 3: <b>RETURNED TO THE CALLEE.</b> The call invitation expires 60 seconds since it is sent, if the callee ACKs to the call invitation but neither the caller or callee takes any further action (cancel, accpet, or decline it). 
      */
      REMOTE_INVITATION_ERR_INVITATION_EXPIRE = 3,
    };

    /** @brief Error codes of the call invitation methods. */
    enum INVITATION_API_CALL_ERR_CODE {
      /** 0: The method call succeeds.
      */
      INVITATION_API_CALL_ERR_OK = 0,
      /** 1: The method call fails. Invalid argument.
      */
      INVITATION_API_CALL_ERR_INVALID_ARGUMENT = 1,
      /** 2: The method call fails. The call invitation has not started.
      */
      INVITATION_API_CALL_ERR_NOT_STARTED = 2,
      /** 3: The method call fails. The call invitation has ended.
      */
      INVITATION_API_CALL_ERR_ALREADY_END = 3, // accepted, failure, canceled, refused
      /** 4: The method call fails. The call invitation is already accepted.
      */
      INVITATION_API_CALL_ERR_ALREADY_ACCEPT = 4,   // more details
      /** 5: The method call fails. The call invitation is already sent.
      */
      INVITATION_API_CALL_ERR_ALREADY_SENT = 5,
    };

/** The caller's call invitation methods. */
    class ILocalCallInvitation
    {
    protected:
      virtual ~ILocalCallInvitation() {}
    public:
      /** Gets the ID of the callee. */
      virtual const char *getCalleeId() const = 0;
      /** Sets the content of the call invitation.
    
      @param content The content of the call invitation. The number of bytes representing the @p content must not exceed 8 &times; 1024 if encoded in UTF-8.
      */
      virtual void setContent(const char *content) = 0;
      /** Gets the content of the call invitation. */
      virtual const char *getContent() const = 0;
      /** Sets the channel ID.
       
      @param channelId The channel ID to be set.
      */
      virtual void setChannelId(const char *channelId) = 0;
      /** Gets the channel ID. */
      virtual const char *getChannelId() const = 0;
      /** Gets the callee's response to the call invitation. */
      virtual const char *getResponse() const = 0;
      /** Gets the state of the outgoing call invitation. */
      virtual LOCAL_INVITATION_STATE getState() const = 0;
       /** Releases all resources used by the ILocalCallInvitation instance.
      */
      virtual void release() = 0;
    };

/** The callee's call invitation methods. */
    class IRemoteCallInvitation
    {
    protected:
      virtual ~IRemoteCallInvitation() {}
    public:
      /** Gets the ID of the caller. */
      virtual const char *getCallerId() const = 0;
      /** Gets the content of the call invitation. */
      virtual const char *getContent() const = 0;
     /**
     Sets the callee's response to the call invitation.

     @param response The callee's response to the call invitation. The number of bytes representing the @p response must not exceed 8 &times; 1024 if encoded in UTF-8.
     */
      virtual void setResponse(const char *response) = 0;
      /** Gets the callee's response to the call invitation. */
      virtual const char *getResponse() const = 0;
      /** Gets the channel ID. */
      virtual const char *getChannelId() const = 0;
      /** Gets the state of the incoming call invitation. */
      virtual REMOTE_INVITATION_STATE getState() const = 0;
       /** Releases all resources used by the IRemoteCallInvitation instance.
      */
      virtual void release() = 0;
    };

    /** Callbacks for the call invitation methods. */
    class IRtmCallEventHandler
    {
    public:
      virtual ~IRtmCallEventHandler()
      {
      }
    /** Callback to the caller: occurs when the callee receives the call invitation.

	 This callback notifies the caller that the callee receives the call invitation.
	
	 @param localInvitation An ILocalCallInvitation object.
	 */
      virtual void onLocalInvitationReceivedByPeer(ILocalCallInvitation *localInvitation)
      {
        (ILocalCallInvitation *) localInvitation;
      }
    /** Callback to the caller: occurs when the caller cancels a call invitation.
	 
	 This callback notifies the caller that he/she has canceled a call invitation.
	 
	 @param localInvitation An ILocalCallInvitation object.
	 */
      virtual void onLocalInvitationCanceled(ILocalCallInvitation *localInvitation)
      {
        (ILocalCallInvitation *) localInvitation;
      }
    /** Callback to the caller: occurs when the life cycle of the outgoing call invitation ends in failure.
	 
	 This callback notifies the caller that the life cycle of the outgoing call invitation ends in failure.
	 
     @param localInvitation An ILocalCallInvitation object.
     @param errorCode The error code. See #LOCAL_INVITATION_ERR_CODE.
     */
      virtual void onLocalInvitationFailure(ILocalCallInvitation *localInvitation, LOCAL_INVITATION_ERR_CODE errorCode)
      {
        (ILocalCallInvitation *) localInvitation;
        (LOCAL_INVITATION_ERR_CODE) errorCode;
      }
    /** Callback to the caller: occurs when the callee accepts the call invitation.
	 
	 This callback notifies the caller that the callee accepts the call invitation.
	 
	 @param localInvitation An ILocalCallInvitation object.
  @param response The response from the callee.
	 */
      virtual void onLocalInvitationAccepted(ILocalCallInvitation *localInvitation, const char *response)
      {
        (ILocalCallInvitation *) localInvitation;
        (const char *) response;
      }
/** Callback to the caller: occurs when the callee refuses the call invitation.

This callback notifies the caller that the callee refuses the call invitation.
	 
	 @param localInvitation An ILocalCallInvitation object.
   @param response The response from the callee.
	 */
      virtual void onLocalInvitationRefused(ILocalCallInvitation *localInvitation, const char *response)
      {
        (ILocalCallInvitation *) localInvitation;
        (const char *) response;
      }
   /** Callback for the callee: occurs when the callee refuses a call invitation.
    
     @param remoteInvitation An IRemoteCallInvitation object.
     */
      virtual void onRemoteInvitationRefused(IRemoteCallInvitation *remoteInvitation)
      {
        (IRemoteCallInvitation *) remoteInvitation;
      }
    /** Callback to the callee: occurs when the callee accepts a call invitation.
    
     @param remoteInvitation An IRemoteCallInvitation object.
     */
      virtual void onRemoteInvitationAccepted(IRemoteCallInvitation *remoteInvitation)
      {
        (IRemoteCallInvitation *) remoteInvitation;
      }
   /** Callback to the callee: occurs when the callee receives a call invitation.
	 
	 This callback notifies the callee that the callee receives a call invitation.
	
	 @param remoteInvitation An IRemoteCallInvitation object.
	 */
      virtual void onRemoteInvitationReceived(IRemoteCallInvitation *remoteInvitation)
      {
        (IRemoteCallInvitation *) remoteInvitation;
      }
    /** Callback to the callee: occurs when the life cycle of the incoming call invitation ends in failure.
	
	 This callback notifies the callee that the life cycle of the incoming call invitation ends in failure.
	
     @param remoteInvitation An IRemoteCallInvitation object.
     @param errorCode The error code. See #REMOTE_INVITATION_ERR_CODE.
     */
      virtual void onRemoteInvitationFailure(IRemoteCallInvitation *remoteInvitation, REMOTE_INVITATION_ERR_CODE errorCode)
      {
        (IRemoteCallInvitation *) remoteInvitation;
      }
   /** Callback to the callee: occurs when the caller cancels the call invitation.
	 
	 This callback notifies the callee that the caller cancels the call invitation.
	
	 @param remoteInvitation An IRemoteCallInvitation object.
	 */
      virtual void onRemoteInvitationCanceled(IRemoteCallInvitation *remoteInvitation)
      {
        (IRemoteCallInvitation *) remoteInvitation;
      }
    };
/** RTM call manager methods. */
    class IRtmCallManager
    {
    protected:
      virtual ~IRtmCallManager() {}
    public:
    /** Allows the caller to send a call invitation to the callee. 
    
     @param invitation An ILocalCallInvitation object.
     */
      virtual int sendLocalInvitation(ILocalCallInvitation *invitation)= 0;
    /** Allows the callee to accept a call invitation.
     
     @param invitation An IRemoteCallInvitation object.
     */
      virtual int acceptRemoteInvitation(IRemoteCallInvitation *invitation) = 0;
    /** Allows the callee to decline a call invitation.
    
     @param invitation An IRemoteCallInvitation object.
     */
      virtual int refuseRemoteInvitation(IRemoteCallInvitation *invitation) = 0;
    /** Allows the caller to cancel a call invitation.

     @param invitation An ILocalCallInvitation object.
     */
      virtual int cancelLocalInvitation(ILocalCallInvitation *invitation) = 0;
    /** Creates an ILocalCallInvitation object.
	
	@param calleeId The Callee's User ID.
	@return An ILocalCallInvitation object.
	*/
      virtual ILocalCallInvitation *createLocalCallInvitation(const char *calleeId) = 0;
      // sync_call
      /** Releases all resources used by the IRtmCallManager instance.
      */
      virtual void release() = 0;
    };
  }
}
